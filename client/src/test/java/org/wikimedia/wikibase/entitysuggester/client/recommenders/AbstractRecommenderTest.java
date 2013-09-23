/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wikimedia.wikibase.entitysuggester.client.recommenders;

import java.util.ArrayList;
import java.util.List;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import org.apache.mahout.cf.taste.common.TasteException;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.runners.MockitoJUnitRunner;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.util.TranslatedRecommendedItemImpl;

/**
 *
 * @author nilesh
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractRecommenderTest {

    @Mock
    AbstractRecommender abstractRecommender;

    @Test
    public void testRecommendAsJSON() throws TasteException {
        assertTrue("[[\"P43\",0.004543],[\"P25\",0.43]]".equals(abstractRecommender.recommendAsJSON(new ArrayList<String>(), 5)));
    }

    @Before
    public void setUp() throws TasteException {
        List<TranslatedRecommendedItem> list = new ArrayList<TranslatedRecommendedItem>();
        list.add(new TranslatedRecommendedItemImpl("P43", 0.004543f));
        list.add(new TranslatedRecommendedItemImpl("P25", 0.43f));
        doReturn(list).when(abstractRecommender).recommend(anyList(), anyInt());
    }
}
