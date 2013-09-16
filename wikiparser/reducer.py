#!/usr/bin/python

"""This is the reducer module for the wikiparser Hadoop scripts. See main() for usage info."""

import sys

def main():
    """ Here are 6 types of sample commands:
    1. For generating the item-property pairs using Hadoop:
    (Dataset used to suggest global claim properties)
    bin/hadoop jar contrib/streaming/hadoop*streaming*jar -libjars /path/to/wikiparser-0.1.jar \
      -inputformat org.wikimedia.wikibase.entitysuggester.wikiparser.WikiPageInputFormat \
      -input /input/dump.xml -output /output/global-ip-pairs \
      -file  /path/to/mapper.py -mapper '/path/to/mapper.py global-ip-pairs' \
      -reducer '/bin/cat'

    2. For generating the claim guid-property pairs for source refs using Hadoop:
    (Dataset used to suggest source ref properties)
    bin/hadoop jar contrib/streaming/hadoop*streaming*jar -libjars /path/to/wikiparser-0.1.jar \
      -inputformat org.wikimedia.wikibase.entitysuggester.wikiparser.WikiPageInputFormat \
      -input /input/dump.xml -output /output/ref-cp-pairs \
      -file  /path/to/mapper.py -mapper '/path/to/mapper.py ref-cp-pairs' \
      -reducer '/bin/cat'

    3. For counting frequencies of properties used in source refs:
    (Dataset used to suggest source ref properties for empty source refs)
    bin/hadoop jar contrib/streaming/hadoop*streaming*jar -libjars /path/to/wikiparser-0.1.jar \
      -inputformat org.wikimedia.wikibase.entitysuggester.wikiparser.WikiPageInputFormat \
      -input /input/dump.xml -output /output/ref-props \
      -file  /path/to/mapper.py -mapper '/path/to/mapper.py ref-props' \
      -file /path/to/reducer.py -reducer '/path/to/reducer.py ref-props'

    4. For counting global property (used in all claims) frequencies:
    (Dataset used to suggest global claim properties for empty items)
    bin/hadoop jar contrib/streaming/hadoop*streaming*jar -libjars /path/to/wikiparser-0.1.jar \
      -inputformat org.wikimedia.wikibase.entitysuggester.wikiparser.WikiPageInputFormat \
      -input /input/dump.xml -output /output/global-props \
      -file  /path/to/mapper.py -mapper '/path/to/mapper.py global-props' \
      -file /path/to/reducer.py -reducer '/path/to/reducer.py global-props'
      
    5. For counting frequency of properties used in qualifiers, to find the top N popular qualifier properties for each property:
    (Dataset used to suggest qualifiers, given a property)
    bin/hadoop jar contrib/streaming/hadoop*streaming*jar -libjars /path/to/wikiparser-0.1.jar \
      -inputformat org.wikimedia.wikibase.entitysuggester.wikiparser.WikiPageInputFormat \
      -input /input/dump.xml -output /output/qual-props \
      -file  /path/to/mapper.py -mapper '/path/to/mapper.py qual-props' \
      -file /path/to/reducer.py -reducer '/path/to/reducer.py qual-props'
      
    6. For counting frequency of values used with properties, to find the top N popular values for each property:
    (Dataset used to suggest values, given a property)
    bin/hadoop jar contrib/streaming/hadoop*streaming*jar -libjars /path/to/wikiparser-0.1.jar \
      -inputformat org.wikimedia.wikibase.entitysuggester.wikiparser.WikiPageInputFormat \
      -input /input/dump.xml -output /output/prop-values \
      -file  /path/to/mapper.py -mapper '/path/to/mapper.py prop-values' \
      -file /path/to/reducer.py -reducer '/path/to/reducer.py prop-values'
    """

    reducerClass = {
        'ref-props': CountPropsReducer,
        'global-props': CountPropsReducer,
        'qual-props': CountQualiferPropsReducer,
        'prop-values': CountQualiferPropsReducer
    }

    try:
        reducer = reducerClass[sys.argv[1]]()
    except (KeyError, IndexError):
        print "Unrecognized/missing reducer arguments."
        print "Please enter any of ref-props, global-props, qual-props, prop-values."
        return

    reducer.run()

class BaseWikiParserReducer:
    """Base/abstract class that should be inherited by all reducer classes"""

    def run(self):
        """Main driver method that iterates through the key-value pairs obtained from the mapper and writes out the final results."""
        raise NotImplementedError("Please implement this method in a subclass.")

class CountPropsReducer(BaseWikiParserReducer):
    """The reducer class to count frequencies of properties used in source refs from the wikidata dump, and global property frequencies using Hadoop."""

    def run(self):
        """Counts the number of occurrences for each property and outputs the property id as key
        and the occurrence frequency as the value to stdout.
        """
        oldKey = None
        currentCount = 0
        key = None

        for i in sys.stdin:
            (key, value) = i.split("\t")
            try:
                count = int(value.strip())
            except ValueError:
                continue

            if oldKey == key:
                currentCount += count

            else:
                if oldKey:
                    sys.stdout.write("%s,%d\n" % (oldKey, currentCount))
                currentCount = count
                oldKey = key

        if oldKey == key:
            sys.stdout.write("%s,%d\n" % (oldKey, currentCount))

class CountQualiferPropsReducer(BaseWikiParserReducer):
    """The reducer class to count global qualifier property frequencies from the wikidata dump using Hadoop.
    This is also used for counting the occurrences of values for each property."""

    def run(self):
        """Counts the number of occurrences for each property and outputs the property id as key
        and the occurrence frequency as the value to stdout.
        """
        oldKey = None
        key = None
        qualifierCounts = {}

        for i in sys.stdin:
            (key, value) = i.split("\t")
            value = value.strip()

            if oldKey == key:
                if value in qualifierCounts:
                    qualifierCounts[value] += 1
                else:
                    qualifierCounts[value] = 1
            else:
                if oldKey:
                    sys.stdout.write("####%s\n" % oldKey) # The property ID, followed by the list of qualifer prop counts
                    for q in qualifierCounts:
                        sys.stdout.write("%s,%d\n" % (q, qualifierCounts[q]))
                oldKey = key
                qualifierCounts = {}
                qualifierCounts[value] = 1

if __name__ == '__main__':
    main()
