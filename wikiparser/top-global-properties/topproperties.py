#!/usr/bin/python

""" The mapper script to count global property frequencies from the wikidata dump using Hadoop.

Sample command to run this on Hadoop:
bin/hadoop jar contrib/streaming/hadoop*streaming*jar -libjars /tmp/entity-suggester-client.jar \
  -inputformat org.wikimedia.wikibase.entitysuggester.wikiparser.WikiPageInputFormat \
  -input /input/dump.xml -output /output \
  -file  /tmp/topproperties.py -mapper /tmp/topproperties.py \
  -file /tmp/topproperties_r.py -reducer '/tmp/topproperties_r.py'
bin/hadoop dfs -cat /output/part-* | sort -t, -k1,1nr > sortedoutput

Please see topproperties_r.py too
"""
from lxml import etree
from itertools import izip
from StringIO import StringIO
import json
import sys

page = ''

def main():
    """Iterates through the input and sends each page to parsePage"""
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
            parsePage(page)

def pairwise(iterable):
    a = iter(iterable)
    return izip(a, a)

def parsePage(page):
    """Parses a page and its JSON text to extract the properties from the statements and sends them to stdout
    with the property id as the key and "1" as the value. This is similar to MapReduce wordcount.
    """
    tree = etree.parse(StringIO(page))
    page = {child.tag:child.text for child in tree.iter()}
    try:
        if page['ns'] != '0': return
        title = page['title'][1:]
        text = json.loads(page['text'])
        statement = None
        if 'claims' not in text: return
        for a in text['claims']:
            statement = {i:j for i, j in pairwise(a['m'])}
            if statement != None:
                try:
                    prop = str(statement['value']).encode("utf-8", 'ignore').strip()
                    sys.stdout.write(prop + "\t" + "1" + "\n")
                except KeyError:
                    pass
    except (KeyError, ValueError, TypeError) as e:
        sys.stderr.write("Error occurred for page : " + str(title) + ", ns = " + str(page['ns']) + "\n")
        sys.stderr.write(traceback.format_exc() + "\n")

if __name__ == '__main__':
    main()
