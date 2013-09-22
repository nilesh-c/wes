/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wikimedia.wikibase.entitysuggester.client.servlets;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockServletConfig;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.Recommender;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.impl.MultiMapRecommender;

/**
 *
 * @author nilesh
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractEntitySuggesterServletTest {

    @Mock
    AbstractEntitySuggesterServlet servlet;
    Recommender recommender;

    @Test
    public void testSetGetRecommender() {
        servlet.setRecommender(recommender);
        assertEquals(servlet.getRecommender(), recommender);
    }

    @Before
    public void setUp() {
        Recommender recommender = new MultiMapRecommender();
        doReturn(new MockServletConfig()).when(servlet).getServletConfig();
        doReturn(EntityType.VALUE).when(servlet).getEntityType();
    }
}
