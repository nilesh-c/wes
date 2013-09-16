#!/usr/bin/python

"""This is the mapper module for running the wikiparser Hadoop scripts. See main() for usage info.
The org.wikimedia.wikibase.entitysuggester.wikiparser.WikiPageInputFormat class in wikiparser-0.1.jar
is an InputFormat class that is used by Hadoop to split the input XML data dump into chunks of <page>...</page>.
In simple terms, if there are N mappers and M chunks, Hadoop divides them into the N mappers.

This module contains an abstract class with a public run() method. It reads the chunks of <page>...</page> from
stdin and calls _getClaimsAndTitle on a single chunk, which in turn extracts the claims from a page (if it's a wikibase Item)
and calls _generateKeyValues on the list of claims. _generateKeyValues is implemented in the different subclasses to yield
different kinds of CSV rows for the different datasets.
"""

from lxml import etree
from itertools import izip
from StringIO import StringIO
import json
import sys
import traceback

def main():
    """The following are 6 types of sample commands. Note that the -input and -output parameters in the Hadoop command
    are paths on HDFS and not on the local filesystem. Replace /path/to with the actual paths of the files.
    wikiparser-0.1.jar, mapper.py and reducer.py have to available in a location where Hadoop has enough permissions to access.

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

    mapperClass = {
        'global-ip-pairs': GlobalIPPairGeneratorMapper,
        'ref-cp-pairs': RefClaimPropPairGeneratorMapper,
        'ref-props': CountRefPropsMapper,
        'global-props': CountGlobalPropsMapper,
        'qual-props': CountQualiferPropsMapper,
        'prop-values': CountPropertyValuesMapper
    }

    try:
        mapper = mapperClass[sys.argv[1]]()
    except (KeyError, IndexError):
        print "Unrecognized/missing mapper arguments."
        print "Please enter any of global-ip-pairs, ref-cp-pairs, ref-props, global-props, qual-props, prop-values."
        return

    mapper.run()

class AbstractWikiParserMapper:
    """Abstract class that should be inherited by all mapper classes"""

    def run(self):
        """Main driver method that iterates through the pages and sends them to the generateKeyValues method."""
        while True:
            page = ''
            i = sys.stdin.readline()
            if not i: break
            if '<page>' in i:
                i = sys.stdin.readline()
                while '</page>' not in i:
                    page += i
                    i = sys.stdin.readline()
                page = '<page>' + page + '</page>'
                (claims, title) = self._getClaimsAndTitle(page)
                if claims != None: self._generateKeyValues(claims, title)
                sys.stdout.flush()

    def _getClaimsAndTitle(self, page):
        """Parses a page and its JSON text to return the list of claims.        """
        tree = etree.parse(StringIO(page))
        page = {child.tag:child.text for child in tree.iter()}
        if page['ns'] == '0':
            title = page['title'][1:]
            text = json.loads(page['text'])
            if 'claims' in text:
                return text['claims'], title
            else:
                return None, None

    def _generateKeyValues(self, claims, title):
        """Iterates through a list of claims, performs some operations and writes (key, value) pairs
        to stdout to be fed into the reducer script."""
        raise NotImplementedError("Please implement this method in a subclass.")

    def _pairwise(self, iterable):
        a = iter(iterable)
        return izip(a, a)

class GlobalIPPairGeneratorMapper(AbstractWikiParserMapper):
    """The mapper class for generating the item-property pairs and item-(property-value) pairs using Hadoop."""

    def _generateKeyValues(self, claims, title):
        """Iterates through an item's claims, extracts the wikibaseProperties and sends the item ID, property ID
        and a relative score of 1 to stdout, separated by commas."""
        ipPair = ipPairOld = None
        for claim in claims:
            statement = {i:j for i, j in self._pairwise(claim['m'])}
            if statement == None: continue
            try:
                ipPair = "Q%s,P%s,1" % (str(title), str(statement['value'])) # ,1 is for relative score. It's same for all, ie. 1.
                # This code may be added in the future to generate CSV rows with values. Please make it in a different class:
                ## if 'wikibase-entityid' not in statement: continue
                ## value = str(statement['wikibase-entityid']['numeric-id']).encode("utf-8", 'ignore').strip()
                ## ipvPair = "Q%s,P%s----Q%s,1" % (str(title), str(statement['value']), value) # for itemID,propertyID---valueID rows
                ## pvPair = "P%s,Q%s,1" % (str(statement['value']), value) # for propertyID,valueID rows
                # After that, write ipvPair or pvPair to stdout, whichever is needed.
                
                if ipPairOld != ipPair: #This check is to prevent printing duplicate itemID,propertyID pairs.
                    ipPairOld = ipPair
                    sys.stdout.write(ipPair.encode("utf-8", 'ignore').strip() + "\n")
            except KeyError:
                pass

class RefClaimPropPairGeneratorMapper(AbstractWikiParserMapper):
    """The mapper class for generating the claim guid-property pairs for source refs using Hadoop."""

    def _generateKeyValues(self, claims, title):
        """Iterates through an item's claims, extracts the claim GUIDs and their source ref wikibaseProperties
        and sends them to stdout with claim GUID as key and prefixed property ID as value."""
        ipPair = ipPairOld = None
        for claim in claims:
            statement = {i:j for i, j in self._pairwise(claim['m'])}
            if statement == None: continue
            try:
                guid = claim['g']
                for ref in claim['refs']:
                    reference = {i:j for i, j in self._pairwise(ref[0])}
                    sys.stdout.write("%s,P%s,1\n" % (guid, str(reference['value'])))
            except KeyError:
                pass

class CountRefPropsMapper(AbstractWikiParserMapper):
    """The mapper class to count frequencies of properties used in source refs from the wikidata dump using Hadoop."""

    def _generateKeyValues(self, claims, title):
        """Iterates through an item's claims to extract the properties from the source references and sends them
        to stdout with the property id as the key and "1" as the value. This is similar to MapReduce wordcount."""
        for claim in claims:
            if 'refs' not in claim: continue
            for ref in claim['refs']:
                source = {i:j for i, j in self._pairwise(ref[0])}
                if source == None: continue
                try:
                    prop = str(source['value'])
                    sys.stdout.write("P%s\t1\n" % prop)
                except KeyError:
                    pass

class CountGlobalPropsMapper(AbstractWikiParserMapper):
    """The mapper class to count global property frequencies from the wikidata dump using Hadoop."""

    def _generateKeyValues(self, claims, title):
        """Iterates through an item's claims to extract the properties from the statements and sends them to stdout
        with the property id as the key and "1" as the value. This is similar to MapReduce wordcount."""
        for claim in claims:
            statement = {i:j for i, j in self._pairwise(claim['m'])}
            if statement == None: continue
            try:
                prop = str(statement['value'])
                sys.stdout.write("P%s\t1\n" % prop)
            except KeyError:
                pass

class CountQualiferPropsMapper(AbstractWikiParserMapper):
    """The mapper class to count global qualifier property frequencies from the wikidata dump using Hadoop."""

    def _generateKeyValues(self, claims, title):
        """Iterates through an item's claims to extract the properties from the statements and the properties of its
        qualifiers if any exist, and sends them to stdout with the statement's property id as the key and the qualifier
        property id as the value. This is similar to MapReduce wordcount."""
        for claim in claims:
            statement = {i:j for i, j in self._pairwise(claim['m'])}
            if statement == None: continue
            try:
                prop = str(statement['value'])
                for q in claim['q']:
                    qualifier = {i:j for i, j in self._pairwise(q)}
                    sys.stdout.write("P%s\tP%s\n" % (prop, str(qualifier['value'])))
            except KeyError:
                pass

class CountPropertyValuesMapper(AbstractWikiParserMapper):
    """The mapper class to count global qualifier property frequencies from the wikidata dump using Hadoop."""

    def _generateKeyValues(self, claims, title):
        """Iterates through an item's claims to extract the wikibaseProperties and wikibaseValues from the statements
        and sends them to stdout with the wikibaseProperty id as the key and the wikibaseValue as the value.
        This is similar to MapReduce wordcount."""
        for claim in claims:
            statement = {i:j for i, j in self._pairwise(claim['m'])}
            if statement == None: continue
            try:
                prop = str(statement['value'])
                if 'wikibase-entityid' not in statement: continue
                value = str(statement['wikibase-entityid']['numeric-id']).encode("utf-8", 'ignore').strip()
                sys.stdout.write("P%s\tQ%s\n" % (prop, value))
            except KeyError:
                pass

if __name__ == '__main__':
    main()
