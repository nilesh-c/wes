package org.wikimedia.wikibase.entitysuggester.client.servlets.suggest;

import javax.servlet.annotation.WebServlet;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;

/**
 * Value suggester Servlet
 * 
 * @author Nilesh Chakraborty
 */
@WebServlet(
        name = "ValueSuggesterServlet",
        displayName = "Value Suggester",
        urlPatterns = "/suggest/values/*"
)
public class ValueSuggesterServlet extends AbstractSuggesterServlet {

    /**
     * @return EntityType - the type of entity suggester this Servlet trains.
     */
    @Override
    protected EntityType getEntityType() {
        return EntityType.VALUE;
    }
}
