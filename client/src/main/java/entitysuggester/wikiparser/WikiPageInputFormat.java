package entitysuggester.wikiparser;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

import entitysuggester.wikiparser.XMLInputFormat;
import entitysuggester.wikiparser.XMLInputFormat.XMLRecordReader;

public class WikiPageInputFormat extends FileInputFormat<LongWritable, Text> {

  public static final String START_TAG = "<page>";
  public static final String END_TAG = "</page>";

  @Override
  public RecordReader<LongWritable, Text> getRecordReader(InputSplit split,
      JobConf conf, Reporter reporter) throws IOException {
    conf.set(XMLInputFormat.START_TAG_KEY, START_TAG);
    conf.set(XMLInputFormat.END_TAG_KEY, END_TAG);
    return new XMLRecordReader((FileSplit) split, conf);
  }
}