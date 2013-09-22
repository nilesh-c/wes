/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wikimedia.wikibase.entitysuggester.client.recommenders.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.Recommender;

/**
 *
 * @author nilesh
 */
@RunWith(MockitoJUnitRunner.class)
public class MultiMapRecommenderTest {

    Recommender recommender = new MultiMapRecommender();

    @Test
    public void testTrainRecommend() throws IOException, TasteException {
        String input = "####P432\nQ32,432\nQ67,2783\nQ2,23\n####P107\nQ11,45\nQ32,222";
        recommender.train(new StringReader(input));
        
        List<String> list = new ArrayList<String>();
        list.add("P107");
        
        float total = 45 + 222; //see values in input String
        float normalized1 = 222 / total;
        float normalized2 = 45 / total;
        
        //For P107, recommendation will contain normalized values under P107, ie. Q11 and Q32 sorted in descending order.
        //Test with howMany = 40
        String requiredResult = "[[\"Q32\"," + normalized1 + "],[\"Q11\"," + normalized2 + "]]";
        assertTrue(requiredResult.equals(recommender.recommendAsJSON(list, 40)));

        //Test with howMany = 1
        requiredResult = "[[\"Q32\"," + normalized1 + "]]";
        assertTrue(requiredResult.equals(recommender.recommendAsJSON(list, 1)));
    }
}
