package org.wikimedia.wikibase.entitysuggester.client.servlets.ingest;

import javax.servlet.annotation.WebServlet;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;

/**
 * Qualifier property dataset trainer Servlet
 * 
 * @author Nilesh Chakraborty
 */
@WebServlet(
        name = "QualifierIngestServlet",
        displayName = "Qualifier Dataset Trainer",
        urlPatterns = "/ingest/qualifiers/*",
        loadOnStartup = 1
)
public class QualifierIngestServlet extends AbstractIngestServlet {

    /**
     * @return EntityType - the type of entity suggester this Servlet trains.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.QUALIFIER;
    }
}
