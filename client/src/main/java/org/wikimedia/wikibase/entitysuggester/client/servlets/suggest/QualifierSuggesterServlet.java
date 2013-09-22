package org.wikimedia.wikibase.entitysuggester.client.servlets.suggest;

import javax.servlet.annotation.WebServlet;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;


/**
 * Qualifier property suggester Servlet
 * 
 * @author Nilesh Chakraborty
 */
@WebServlet(
        name = "QualifierSuggesterServlet",
        displayName = "Qualifier Suggester",
        urlPatterns = "/suggest/qualifiers/*"
)
public class QualifierSuggesterServlet extends AbstractSuggesterServlet {

    /**
     * @return EntityType - the type of entity suggester this Servlet trains.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.QUALIFIER;
    }
}
