package org.wikimedia.wikibase.entitysuggester.client.servlets.ingest;

import java.io.IOException;
import java.io.Reader;
import javax.servlet.annotation.WebServlet;
import org.apache.mahout.cf.taste.common.TasteException;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.impl.PropertyRecommender;

/**
 * Empty claim property dataset trainer Servlet
 * 
 * @author Nilesh Chakraborty
 */
@WebServlet(
        name = "EmptyClaimPropertyIngestServlet",
        displayName = "Empty Claim Property Dataset Trainer",
        urlPatterns = "/ingest/empty-claimprops/*",
        loadOnStartup = 1
)
public class EmptyClaimPropertyIngestServlet extends AbstractIngestServlet {

    /**
     * @return EntityType - the type of entity suggester this Servlet trains.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.CLAIM_PROPERTY;
    }

    /**
     * Overrides AbstractIngestServlet.trainRecommender to train the PropertyRecommender for empty
     * inputs.
     * 
     * @see AbstractIngestServlet.trainRecommender
     * 
     * @param dataReader
     * @throws IOException
     * @throws TasteException
     */
    @Override
    protected void trainRecommender(Reader dataReader) throws IOException, TasteException {
        ((PropertyRecommender) getRecommender()).trainEmpty(dataReader);
    }
}
