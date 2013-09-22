package org.wikimedia.wikibase.entitysuggester.client.servlets.suggest;

import javax.servlet.annotation.WebServlet;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;


/**
 * Source Reference property suggester Servlet
 * 
 * @author Nilesh Chakraborty
 */
@WebServlet(
        name = "RefPropertySuggesterServlet",
        displayName = "Source Reference Property Suggester",
        urlPatterns = "/suggest/refprops/*"
)
public class RefPropertySuggesterServlet extends AbstractSuggesterServlet {

    /**
     * @return EntityType - the type of entity suggester this Servlet trains.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.REF_PROPERTY;
    }
}
