package org.wikimedia.wikibase.entitysuggester.client.servlets.ingest;

import java.io.IOException;
import java.io.Reader;
import javax.servlet.annotation.WebServlet;
import org.apache.mahout.cf.taste.common.TasteException;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.impl.PropertyRecommender;

/**
 * Empty source reference property dataset trainer Servlet
 * 
 * @author Nilesh Chakraborty
 */
@WebServlet(
        name = "EmptyRefPropertyIngestServlet",
        displayName = "Empty Sourceref Property Dataset Trainer",
        urlPatterns = "/ingest/empty-refprops/*",
        loadOnStartup = 1
)
public class EmptyRefPropertyIngestServlet extends AbstractIngestServlet {

    /**
     * @return EntityType - the type of entity suggester this Servlet trains.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.REF_PROPERTY;
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
