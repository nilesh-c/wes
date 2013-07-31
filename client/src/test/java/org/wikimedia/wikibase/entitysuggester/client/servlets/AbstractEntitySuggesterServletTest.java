/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wikimedia.wikibase.entitysuggester.client.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import net.myrrix.client.translating.TranslatingRecommender;
import org.apache.mahout.cf.taste.common.TasteException;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.wikimedia.wikibase.entitysuggester.client.recommender.WebClientRecommender;

/**
 *
 * @author Nilesh Chakraborty
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractEntitySuggesterServletTest {

    @Mock
    TranslatingRecommender translatingRecommender;
    @Mock
    AbstractEntitySuggesterServlet servlet;
    @Mock
    TranslatedRecommendedItem translatedRecommendedItem1;
    @Mock
    TranslatedRecommendedItem translatedRecommendedItem2;

    @Before
    public void setUp() {
        doReturn("P67").when(translatedRecommendedItem1).getItemID(); // Suggested wikibaseProperty
        doReturn(0.256543f).when(translatedRecommendedItem1).getValue(); // Relative score for this suggestion
        doReturn("P127").when(translatedRecommendedItem2).getItemID();
        doReturn(0.345637f).when(translatedRecommendedItem2).getValue();
        doReturn(new MockServletConfig()).when(servlet).getServletConfig();
    }

    /**
     * Test of initializeClientRecommender method, of class
     * AbstractEntitySuggesterServlet.
     */
    @Test
    public void testInitializeClientRecommender() throws TasteException, IOException, ServletException {
        // Setting something as a stub to test later if initialized properly
        List<TranslatedRecommendedItem> list = new ArrayList<TranslatedRecommendedItem>();
        list.add(translatedRecommendedItem1);
        // The WebClientRecommender inside the servlet should suggest translatedRecommendedItem1, ie. wikibaseProperty P67 always
        doReturn(list).when(translatingRecommender).recommend(anyString(), anyInt());

        ArrayList<String> propertyIDList = new ArrayList<String>();
        propertyIDList.add("P67");
        propertyIDList.add("P192");
        translatingRecommender.addItemIDs(propertyIDList);
        WebClientRecommender webClientRecommender = new WebClientRecommender(translatingRecommender);

        servlet.getServletConfig().getServletContext().setAttribute("recommender", webClientRecommender);;
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

        WebClientRecommender fromServlet = servlet.getClientRecommender();
        List<TranslatedRecommendedItem> suggestions = webClientRecommender.recommend("Q87", 10);
        assertEquals("P67", suggestions.get(0).getItemID());
    }

    /**
     * Test of output method of class AbstractEntitySuggesterServlet.
     */
    @Test
    public void testOutput() throws IOException {
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();
        List<TranslatedRecommendedItem> list = new ArrayList<TranslatedRecommendedItem>();
        list.add(translatedRecommendedItem1);
        list.add(translatedRecommendedItem2);
        servlet.output(mockHttpServletRequest, mockHttpServletResponse, list);
        String responseOutput = mockHttpServletResponse.getContentAsString();
        assertEquals("[[\"P67\",0.256543],[\"P127\",0.345637]]", responseOutput);
    }
}
