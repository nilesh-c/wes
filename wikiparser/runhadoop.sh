#!/bin/bash 
    d=`dirname "$0"`

    #You may need to change the following two variables to suit your installation.
    hadoop=/usr/share/hadoop
    hadoop_executable=hadoop

    if [ -z "$3" ]; then
        echo "$0 <mode> <xmldump> <output> [<local-output>]"
        echo "<mode> can be any of global-ip-pairs, ref-cp-pairs, ref-props, global-props, qual-props, prop-values."
        echo "<xmldump> is the location of the wikidata data dump file on HDFS."
        echo "<output> is the directory to write output to on HDFS."
        echo "<local-output> is optional - if it is specified, the output from HDFS will be copied to this file on the local file system."
        exit 1
    fi

    if [ $1 == "global-ip-pairs" -o $1 == "ref-cp-pairs" ]; then
        $hadoop_executable jar $hadoop/contrib/streaming/hadoop*streaming*.jar \
            -libjars "$d/target/wikiparser-0.1.jar" \
            -inputformat org.wikimedia.wikibase.entitysuggester.wikiparser.WikiPageInputFormat \
            -input "$2" -output "$3" \
            -file "$d/mapper.py" -mapper "$d/mapper.py $1" \
            -reducer '/bin/cat'
    else
        $hadoop_executable jar $hadoop/contrib/streaming/hadoop*streaming*.jar \
            -libjars "$d/target/wikiparser-0.1.jar" \
            -inputformat org.wikimedia.wikibase.entitysuggester.wikiparser.WikiPageInputFormat \
            -input "$2" -output "$3" \
            -file "$d/mapper.py" -mapper "$d/mapper.py $1" \
            -file "$d/reducer.py" -reducer "$d/reducer.py $1"
    fi

    if [ -n "$4" ]; then
        $hadoop_executable dfs -cat $3/part-* > $4
    fi
