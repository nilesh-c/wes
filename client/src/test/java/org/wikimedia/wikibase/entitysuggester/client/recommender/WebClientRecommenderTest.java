/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wikimedia.wikibase.entitysuggester.client.recommender;

import java.util.ArrayList;
import java.util.List;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import org.wikimedia.wikibase.entitysuggester.client.recommender.WebClientRecommender;
import net.myrrix.client.translating.TranslatingRecommender;
import org.apache.mahout.cf.taste.common.TasteException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author Nilesh Chakraborty
 */
@RunWith(MockitoJUnitRunner.class)
public class WebClientRecommenderTest {

    @Mock
    TranslatingRecommender translatingRecommender;
    @Mock
    TranslatedRecommendedItem translatedRecommendedItem;

    @Before
    public void setUp() throws TasteException {
        doReturn("P67").when(translatedRecommendedItem).getItemID(); // Suggested wikibaseProperty
        doReturn(0.256543f).when(translatedRecommendedItem).getValue(); // Relative score for this suggestion

        List< TranslatedRecommendedItem> list = new ArrayList<TranslatedRecommendedItem>();
        list.add(translatedRecommendedItem);

        // return no suggedstions (empty list) or any wikibaseItem ID that's not in the dataset
        doReturn(new ArrayList<TranslatedRecommendedItem>()).when(translatingRecommender).recommend(anyString(), anyInt());

        // suggest only P67 for the wikibaseItem Q87, as an example
        doReturn(list).when(translatingRecommender).recommend(eq("Q87"), anyInt());

        // suggest only P67 for any anonymous suggestion
        doReturn(list).when(translatingRecommender).recommendToAnonymous(any(String[].class), anyInt());
    }

    @Test
    public void testRecommend() throws TasteException {
        ArrayList<String> propertyIDList = new ArrayList<String>();
        propertyIDList.add("P67");
        propertyIDList.add("P192");
        translatingRecommender.addItemIDs(propertyIDList);
        AbstractClientRecommender webClientRecommender = new WebClientRecommender(translatingRecommender);

        // Test with a wikibaseItem that is in the Myrrix model
        List<TranslatedRecommendedItem> suggestions = webClientRecommender.recommend("Q87", 10);
        assertEquals("P67", suggestions.get(0).getItemID());
        assertEquals(0.256543f, suggestions.get(0).getValue(), 0.001);

        // Test with a wikibaseItem that doesn't exist in the Myrrix model
        suggestions = webClientRecommender.recommend("Q6835", 10);
        assertEquals(0, suggestions.size());
    }

    @Test
    public void testRecommendAnonymous() throws TasteException {
        ArrayList<String> propertyIDList = new ArrayList<String>();
        propertyIDList.add("P67");
        propertyIDList.add("P192");
        translatingRecommender.addItemIDs(propertyIDList);
        AbstractClientRecommender webClientRecommender = new WebClientRecommender(translatingRecommender);

        List<TranslatedRecommendedItem> suggestions = webClientRecommender.recommendAnonymous(new String[]{"P67", "P78"}, 2);
        assertEquals("P67", suggestions.get(0).getItemID());
    }

    @After
    public void tearDown() {
    }
}
