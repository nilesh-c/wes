/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wikimedia.wikibase.entitysuggester.client.recommenders.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.List;
import net.myrrix.client.translating.TranslatingRecommender;
import org.apache.mahout.cf.taste.common.TasteException;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.isA;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 *
 * @author nilesh
 */
@RunWith(MockitoJUnitRunner.class)
public class MyrrixWebClientRecommenderTest {

    MyrrixWebClientRecommender myrrixWebClientRecommender;
    @Mock
    TranslatingRecommender translatingRecommender;
    String testInput = "Q432,P32,1\nQ107,P11,1";

    @Test
    public void testTrain() throws TasteException, IOException {
        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Reader reader = (Reader) args[0];
                CharBuffer cb = CharBuffer.allocate(100);
                reader.read(cb);
                cb.flip();
                String temp = cb.toString();
                assertTrue(temp.trim().equals(testInput));
                reader.close();
                return null;
            }
        }).when(translatingRecommender).ingest(isA(Reader.class));

        doAnswer(new Answer() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                List<String> list = (List<String>) args[0];
                assertTrue(list.get(0).equals("P32"));
                assertTrue(list.get(1).equals("P11"));
                return null;
            }
        }).when(translatingRecommender).addItemIDs(isA(Iterable.class));
        myrrixWebClientRecommender.train(new StringReader(testInput));
    }

    @Before
    public void setUp() throws TasteException {
        myrrixWebClientRecommender = new MyrrixWebClientRecommender(translatingRecommender);
    }

    @After
    public void tearDown() {
    }
}
