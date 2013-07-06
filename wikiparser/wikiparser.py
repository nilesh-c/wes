#!/usr/bin/python

from lxml import etree
from StringIO import StringIO
import json
import sys

count = 0
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
        title = page['title'][1:]
        if page['ns'] == '0':
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
                            sys.stdout.write(toyield1.encode("utf-8", 'ignore'))
                            sys.stdout.write(toyield2.encode("utf-8", 'ignore'))
                        except KeyError:
                            toyield1 = toyield2 = None
    except KeyError:
        pass

if __name__ == '__main__':
    main()
