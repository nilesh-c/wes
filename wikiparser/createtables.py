#!/usr/bin/python

"""Creates the DB and tables for wikiparser_db.py to insert into."""
import sys
import MySQLdb as mdb

def main():
    con = mdb.connect('localhost', 'root', 'password', 'wikidatawiki');
    with con:
        cur = con.cursor()
        try:
            cur.execute("DROP TABLE IF EXISTS plabel")
        except MySQLdb.Warning:
            pass
        cur.execute("CREATE TABLE plabel("
                    "pl_id INT UNSIGNED NOT NULL, "
                    "pl_lang VARCHAR(32) NOT NULL, "
                    "pl_text VARCHAR(256) NOT NULL) "
                    "ENGINE=InnoDB "
                    "CHARSET binary")
        try:
            cur.execute("DROP TABLE IF EXISTS label")
        except MySQLdb.Warning:
            pass
        cur.execute("CREATE TABLE label("
                    "l_id INT UNSIGNED NOT NULL, "
                    "l_lang VARCHAR(32) NOT NULL, "
                    "l_text VARCHAR(256) NOT NULL) "
                    "ENGINE=InnoDB "
                    "CHARSET binary")
        con.commit()

if __name__ == '__main__':
	main()
