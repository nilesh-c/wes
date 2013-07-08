#!/usr/bin/python

from lxml import etree
from StringIO import StringIO
import json
import sys
import MySQLdb as mdb
import traceback

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
                    page += i
                    i = sys.stdin.readline()
                page = '<page>' + page + '</page>'
                parsePage(con, cur, page)
                count += 1
                if(count % 10000 == 0):
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
        if page['ns'] == '0':
            title = page['title'][1:]
            text = json.loads(page['text'])
            if 'en' not in text['label']: return
            label = text['label']['en'].encode("utf-8", 'ignore')
            cur.execute("""INSERT INTO label VALUES (%s, %s, %s)""", (int(title), 'en', label))
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
        elif page['ns'] == '120':
            title = page['title'][10:]
            text = json.loads(page['text'])
            label = text['label']['en'].encode("utf-8", 'ignore')
            cur.execute("""INSERT INTO plabel VALUES (%s, %s, %s)""", (int(title), 'en', label))
    except (KeyError, ValueError, TypeError) as e:
        sys.stderr.write("Error occurred for page : " + str(title) + ", ns = " + str(page['ns']) + "\n")
        sys.stderr.write(traceback.format_exc() + "\n")
    except mdb.Error, e:
        print "Error %d: %s" % (e.args[0],e.args[1])
        sys.exit(1)

if __name__ == '__main__':
    main()
