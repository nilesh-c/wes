/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wikimedia.wikibase.entitysuggester.client.recommenders.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import org.apache.mahout.cf.taste.common.TasteException;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.Recommender;

/**
 *
 * @author nilesh
 */
public class SortedListRecommenderTest {

    Recommender recommender = new SortedListRecommender();

    @Test
    public void testTrainRecommend() throws IOException, TasteException {
        String input = "Q67,2783\nQ2,23";
        recommender.train(new StringReader(input));
        
        float total = 2783 + 23; //see values in input String
        float normalized1 = 2783 / total;
        float normalized2 = 23 / total;
        
        //Recommendation will contain normalized values sorted in descending order.
        //Test with howMany = 20
        String requiredResult = "[[\"Q67\"," + normalized1 + "],[\"Q2\"," + normalized2 + "]]";
        assertTrue(requiredResult.equals(recommender.recommendAsJSON(new ArrayList<String>(), 20)));

        //Test with howMany = 1
        requiredResult = "[[\"Q67\"," + normalized1 + "]]";
        assertTrue(requiredResult.equals(recommender.recommendAsJSON(new ArrayList<String>(), 1)));
    }
}
