#!/usr/bin/python

from lxml import etree
from StringIO import StringIO
import json
import sys
import MySQLdb as mdb

count = 0
page = ''

def main():
    con = None
    cur = None
    try:
        con = mdb.connect('localhost', 'root', 'password', 'wikidatawiki');
        cur = con.cursor()
        cur.execute("SET FOREIGN_KEY_CHECKS = 0")
        cur.execute("SET UNIQUE_CHECKS = 0")
        cur.execute("SET AUTOCOMMIT = 0")
        count = 0
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
                parsePage(con, cur, page)
                count += 1
                if(count % 1000 == 0)
                    con.commit()
    finally:
        if cur:
            cur.execute("SET UNIQUE_CHECKS = 1")
            cur.execute("SET FOREIGN_KEY_CHECKS = 1")
            con.commit()
        if con:
            con.close()

def pairwise(iterable):
    from itertools import izip
    a = iter(iterable)
    return izip(a, a)

def parsePage(con, cur, page):
    tree = etree.parse(StringIO(page))
    page = {child.tag:child.text for child in tree.iter()}
    try:
        title = page['title'][1:]
        if page['ns'] == '0':
            text = json.loads(page['text'])
            cur.execute("""INSERT INTO label VALUES (%s, %s, %s)""", (int(title), 'en', text['label']['en'].encode("utf-8", 'ignore')))
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
        elif page['ns'] == '120':
            text = json.loads(page['text'])
            cur.execute("""INSERT INTO plabel VALUES (%s, %s, %s)""", (int(title), 'en', text['label']['en'].encode("utf-8", 'ignore')))
    except KeyError:
        pass
    except mdb.Error, e:
        print "Error %d: %s" % (e.args[0],e.args[1])
        sys.exit(1)

if __name__ == '__main__':
    main()
