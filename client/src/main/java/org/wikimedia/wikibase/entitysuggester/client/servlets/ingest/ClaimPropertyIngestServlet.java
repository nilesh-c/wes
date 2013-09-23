package org.wikimedia.wikibase.entitysuggester.client.servlets.ingest;

import javax.servlet.annotation.WebServlet;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;

/**
 * Claim property dataset trainer Servlet
 * 
 * @author Nilesh Chakraborty
 */
@WebServlet(
        name = "ClaimPropertyIngestServlet",
        displayName = "Claim Property Dataset Trainer",
        urlPatterns = "/ingest/claimprops/*",
        loadOnStartup = 1
)
public class ClaimPropertyIngestServlet extends AbstractIngestServlet {

    /**
     * @return EntityType - the type of entity suggester this Servlet trains.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.CLAIM_PROPERTY;
    }
}
