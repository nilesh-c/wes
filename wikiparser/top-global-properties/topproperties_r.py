#!/usr/bin/python

""" The reducer script to count global property frequencies from the wikidata dump using Hadoop.

If the reducer output is set to "/output", run: bin/hadoop dfs -cat /output/part-* | sort -t, -k1,1nr > sortedoutput
to get the properties sorted in descending order of their frequencies.
"""

import sys

def main():
    """Counts the number of occurrences for each property and outputs the property id as key
    and the occurrence frequency as the value
    """
    current_word = None
    current_count = 0
    key = None

    for i in sys.stdin:
        (key, value) = i.split("\t")
        try:
            count = int(value.strip())
        except ValueError:
            continue

        if current_word == key:
            current_count += count

        else:
            if current_word:
                sys.stdout.write("%s\t%s\n" % (current_word, str(current_count)))
            current_count = count
            current_word = key

    if current_word == key:
        sys.stdout.write("%s\t%s\n" % (current_word, str(current_count)))

if __name__ == '__main__':
    main()
