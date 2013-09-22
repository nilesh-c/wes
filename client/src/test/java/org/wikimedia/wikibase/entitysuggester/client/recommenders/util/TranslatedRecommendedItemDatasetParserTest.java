/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wikimedia.wikibase.entitysuggester.client.recommenders.util;

import com.google.common.base.Predicate;
import java.io.IOException;
import java.io.StringReader;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author nilesh
 */
public class TranslatedRecommendedItemDatasetParserTest {

    TranslatedRecommendedItemDatasetParser parser;
    String testInput = "####SOMEKEY\n"
                     + "blah1,34\n"
                     + "blah3,454\n"
                     + "blahother,2\n"
                     + "####SOMEOTHERKEY\n"
                     + "blah5,2\n"
                     + "blahagain,54\n";

    @Test
    public void testParser() throws IOException {
        parser.fetchNewKeyListPair();
        assertTrue("####SOMEKEY".equals(parser.getNewKey()));

        // TODO: This is just a basic test that counts the number of items in the list.
        // Perhaps add a more detailed test that compares the different elements with
        // the elements from testInput String.
        System.out.println(parser.getNewList().size());
        assertTrue(parser.getNewList().size() == 3);

        parser.fetchNewKeyListPair();
        assertTrue("####SOMEOTHERKEY".equals(parser.getNewKey()));
        assertTrue(parser.getNewList().size() == 2); // Check testInput. Second list has two entries.

        assertFalse(parser.hasNext());
    }

    @Before
    public void setUp() throws IOException {
        Predicate<String> readUntil = new Predicate<String>() {

            @Override
            public boolean apply(String t) {
                return t == null || t.contains("####");
            }
        };
        parser = new TranslatedRecommendedItemDatasetParser(new StringReader(testInput), readUntil);
    }
}
