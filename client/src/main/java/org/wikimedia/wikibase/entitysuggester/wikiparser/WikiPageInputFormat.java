package org.wikimedia.wikibase.entitysuggester.wikiparser;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.wikimedia.wikibase.entitysuggester.wikiparser.XMLInputFormat.XMLRecordReader;

/**
 * InputFormat implementation to be used with Hadoop for parsing the wikidata
 * XML dumps,
 *
 * @author Nilesh Chakraborty
 */
public class WikiPageInputFormat extends FileInputFormat<LongWritable, Text> {

    /**
     * Everything between the start and end tags is treated as a single chunk
     * and passed to the mapper.
     */
    public static final String START_TAG = "<page>";
    public static final String END_TAG = "</page>";

    /**
     *
     * @param split
     * @param conf
     * @param reporter
     * @return
     * @throws IOException
     */
    @Override
    public RecordReader<LongWritable, Text> getRecordReader(InputSplit split,
            JobConf conf, Reporter reporter) throws IOException {
        conf.set(XMLInputFormat.START_TAG_KEY, START_TAG);
        conf.set(XMLInputFormat.END_TAG_KEY, END_TAG);
        return new XMLRecordReader((FileSplit) split, conf);
    }
}