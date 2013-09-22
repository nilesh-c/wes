package org.wikimedia.wikibase.entitysuggester.client.servlets.ingest;

import javax.servlet.annotation.WebServlet;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;

/**
 * Value dataset trainer Servlet
 * 
 * @author Nilesh Chakraborty
 */
@WebServlet(
        name = "ValueIngestServlet",
        displayName = "Value Dataset Trainer",
        urlPatterns = "/ingest/values/*",
        loadOnStartup = 1
)
public class ValueIngestServlet extends AbstractIngestServlet {

    /**
     * @return EntityType - the type of entity suggester this Servlet trains.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.VALUE;
    }
}