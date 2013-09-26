#!/bin/bash

d=`dirname "$0"`

if [ -z "$4" ]; then
    echo "$0 <xmldump> <temp> <host> <port>"
    echo "<xmldump> is the location of the wikidata data dump file on HDFS."
    echo "<temp> is the path to a temporary directory on HDFS that does not exist."
    echo "<host> is the Java REST API host"
    echo "<port> is the Java REST API port"
    echo "Example: ./train.sh /input/dump.xml /tmp localhost 8080"
    exit 1
fi
   
types="prop-values,values qual-props,qualifiers global-ip-pairs,claimprops ref-cp-pairs,refprops global-props,empty-claimprops ref-props,empty-refprops"

for i in $types
do
    split=(${i//,/ })
    mode=${split[0]}
    servlet=${split[1]}
    echo ./runhadoop.sh $mode $1 $2/$file $d/train$servlet.csv
    echo curl -X POST --data-binary @train$servlet.csv http://$3:$4/entitysuggester/ingest/$servlet
done
