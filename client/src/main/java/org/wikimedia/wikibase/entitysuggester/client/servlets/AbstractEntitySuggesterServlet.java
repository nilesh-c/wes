package org.wikimedia.wikibase.entitysuggester.client.servlets;

import javax.servlet.http.HttpServlet;
import org.wikimedia.wikibase.entitysuggester.client.EntityType;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.Recommender;

/**
 * Abstract Servlet class that all Suggester and Ingester Servlets should
 * extend.
 *
 * @author Nilesh Chakraborty
 */
public abstract class AbstractEntitySuggesterServlet extends HttpServlet {

    /**
     * @return EntityType, the kind of entity that this Servlet suggests or
     * ingests (gets trained by).
     */
    protected abstract EntityType getEntityType();

    /**
     * @return Recommender for the EntityType that this servlet (subclass)
     * suggests or trains.
     */
    protected Recommender getRecommender() {
        return (Recommender) this.getServletConfig().getServletContext().getAttribute(getEntityType().name());
    }

    /**
     * Sets a Recommender in the ServletContext.
     *
     * @param recommender Recommender for the EntityType that this servlet
     * (subclass) suggests or trains.
     */
    protected void setRecommender(Recommender recommender) {
        this.getServletConfig().getServletContext().setAttribute(getEntityType().name(), recommender);
    }
}
