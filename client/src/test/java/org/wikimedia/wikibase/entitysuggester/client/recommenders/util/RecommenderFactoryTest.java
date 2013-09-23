/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wikimedia.wikibase.entitysuggester.client.recommenders.util;

import java.io.IOException;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.Recommender;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.impl.MultiMapRecommender;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.impl.PropertyRecommender;

/**
 *
 * @author nilesh
 */
public class RecommenderFactoryTest {

    @Test
    public void testCreate() throws IOException {
        Recommender recommender;

        recommender = RecommenderFactory.create(EntityType.QUALIFIER, "localhost", 8080);
        assertTrue(recommender instanceof MultiMapRecommender);

        recommender = RecommenderFactory.create(EntityType.VALUE, "localhost", 8080);
        assertTrue(recommender instanceof MultiMapRecommender);

        recommender = RecommenderFactory.create(EntityType.CLAIM_PROPERTY, "localhost", 8080);
        assertTrue(recommender instanceof PropertyRecommender);

        recommender = RecommenderFactory.create(EntityType.REF_PROPERTY, "localhost", 8080);
        assertTrue(recommender instanceof PropertyRecommender);
    }
}
