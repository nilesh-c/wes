package org.wikimedia.wikibase.entitysuggester.client.recommenders.util;

import java.io.IOException;
import net.myrrix.client.ClientRecommender;
import net.myrrix.client.MyrrixClientConfiguration;
import net.myrrix.client.translating.TranslatingClientRecommender;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.Recommender;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.impl.MultiMapRecommender;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.impl.PropertyRecommender;

/**
 * Factory class with static method for creating recommenders for different
 * entity types.
 *
 * @author Nilesh Chakraborty
 */
public class RecommenderFactory {

    private RecommenderFactory() {
    }

    /**
     * @param entityType
     * @param host
     * @param port
     * @return Recommender created according to entityType
     * @throws IOException
     */
    public static Recommender create(EntityType entityType, String host, int port) throws IOException {
        Recommender recommender = null;
        switch (entityType) {
            case CLAIM_PROPERTY:
                recommender = createPropertyRecommender(host, port, "myrrix-claimprops");
                break;
            case REF_PROPERTY:
                recommender = createPropertyRecommender(host, port, "myrrix-refprops");
                break;
            case QUALIFIER:
            case VALUE:
                recommender = new MultiMapRecommender();
        }
        return recommender;
    }

    /**
     * @param host
     * @param port
     * @param contextPath
     * @return PropertyRecommender by configuring a Myrrix client with the given
     * host, port and contextPath.
     * @throws IOException
     */
    public static PropertyRecommender createPropertyRecommender(String host, int port, String contextPath) throws IOException {
        MyrrixClientConfiguration config = new MyrrixClientConfiguration();
        config.setHost(host);
        config.setPort(port);
        config.setContextPath(contextPath);
        return new PropertyRecommender(new TranslatingClientRecommender(new ClientRecommender(config)));
    }
}
