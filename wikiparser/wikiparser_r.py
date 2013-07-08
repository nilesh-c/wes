#!/usr/bin/python

import sys

def main():
    listout = open(sys.argv[1], "w")
    for i in sys.stdin:
        (key, value) = i.split("\t")
        if key == "@@":
            sys.stdout.write(value)
        else:
            listout.write(value)
    listout.close()
        
if __name__ == '__main__':
    main()
