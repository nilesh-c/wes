package org.wikimedia.wikibase.entitysuggester.client.servlets.ingest;

import javax.servlet.annotation.WebServlet;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;

/**
 * Source reference property dataset trainer Servlet
 * 
 * @author Nilesh Chakraborty
 */
@WebServlet(
        name = "RefPropertyIngestServlet",
        displayName = "Sourceref Property Dataset Trainer",
        urlPatterns = "/ingest/refprops/*",
        loadOnStartup = 1
)
public class RefPropertyIngestServlet extends AbstractIngestServlet {

    /**
     * @return EntityType - the type of entity suggester this Servlet trains.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.REF_PROPERTY;
    }
}
