package org.wikimedia.wikibase.entitysuggester.client.servlets.suggest;

import javax.servlet.annotation.WebServlet;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;

/**
 * Claim property suggester Servlet
 * 
 * @author Nilesh Chakraborty
 */
@WebServlet(
        name = "ClaimPropertySuggesterServlet",
        displayName = "Claim Property Suggester",
        urlPatterns = "/suggest/claimprops/*"
)
public class ClaimPropertySuggesterServlet extends AbstractSuggesterServlet {

    /**
     * @return EntityType - the type of entity suggester this Servlet trains.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.CLAIM_PROPERTY;
    }
}
