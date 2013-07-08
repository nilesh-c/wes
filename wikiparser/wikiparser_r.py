#!/usr/bin/python

import sys

def main():
    for i in sys.stdin:
        (key, value) = i.split("\t")
        sys.stdout.write(value + "\n")
        
if __name__ == '__main__':
    main()
