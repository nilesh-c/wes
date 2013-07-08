#!/usr/bin/python

from lxml import etree
from StringIO import StringIO
import json
import sys

page = ''

def main():
    while True:
        page = ''
        i = sys.stdin.readline()
        if not i: break
        if '<page>' in i:
            i = sys.stdin.readline()
            while '</page>' not in i:
                page += i#.strip()
                i = sys.stdin.readline()
            page = '<page>' + page + '</page>'
            parsePage(page)

def pairwise(iterable):
    from itertools import izip
    a = iter(iterable)
    return izip(a, a)

def parsePage(page):
    tree = etree.parse(StringIO(page))
    page = {child.tag:child.text for child in tree.iter()}
    try:
        if page['ns'] == '0':
            title = page['title'][1:]
            text = json.loads(page['text'])
            statement = None
            if 'claims' in text:
                for a in text['claims']:
                    statement = {i:j for i, j in pairwise(a['m'])}
                    if statement != None:
                        try:
                            toyield1 = str(statement['value'])
                            value = str(statement['wikibase-entityid']['numeric-id']) if 'wikibase-entityid' in statement else statement['string']
                            toyield2 = str(statement['value']) + "----" + value
                            sys.stdout.write("||\t" + str(title) + "," + toyield1.encode("utf-8", 'ignore') + "\n")
                            sys.stdout.write("||\t" + str(title) + "," + toyield2.encode("utf-8", 'ignore') + "\n")
                            sys.stdout.write("$$\t" + toyield1.encode("utf-8", 'ignore') + "\n")
                            sys.stdout.write("$$\t" + toyield2.encode("utf-8", 'ignore') + "\n")
                        except KeyError:
                            pass
    except (KeyError, ValueError, TypeError) as e:
        sys.stderr.write("Error occurred for page : " + str(title) + ", ns = " + str(page['ns']) + "\n")
        sys.stderr.write(traceback.format_exc() + "\n")

if __name__ == '__main__':
    main()
