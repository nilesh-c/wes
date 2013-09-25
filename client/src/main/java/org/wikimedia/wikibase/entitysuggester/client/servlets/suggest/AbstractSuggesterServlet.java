package org.wikimedia.wikibase.entitysuggester.client.servlets.suggest;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.mahout.cf.taste.common.TasteException;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.Recommender;
import org.wikimedia.wikibase.entitysuggester.client.servlets.AbstractEntitySuggesterServlet;

/**
 * Abstract Servlet class that all Suggester Servlets should extend.
 *
 * @author Nilesh Chakraborty
 */
public abstract class AbstractSuggesterServlet extends AbstractEntitySuggesterServlet {

    /**
     * Splitter used to split the entity IDs in the input URL into individual
     * IDs. Examples are provided in webapp/index.jsp.
     */
    protected static final Splitter INPUT_SEPARATOR = Splitter.on(",").omitEmptyStrings().trimResults();

    /**
     * Throws an exception if the Recommender for this Servlet's entity type has
     * not been set.
     *
     * The Recommender has to be set from an Ingest Servlet so that it can be
     * used to provide suggestions.
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        Recommender recommender = getRecommender();
        if (recommender == null) {
            throw new ServletException("Please initialize the recommendation engine by using the ingest servlet before making any requests to this suggester servlet");
        }
    }

    /**
     * Handles a GET request to the Servlet by fetching suggestions from the
     * Recommender as JSON and writing it to the response.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        List<String> inputList;
        if (pathInfo == null) {
            inputList = new ArrayList<String>();
        } else {
            inputList = INPUT_SEPARATOR.splitToList(pathInfo.substring(1));
        }
        int howMany = getHowMany(request);

        try {
            String jsonSuggestions = getRecommender().recommendAsJSON(inputList, howMany);
            response.getWriter().write(jsonSuggestions);
        } catch (TasteException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.toString());
        }
    }

    /**
     * @param request
     * @return howMany URL parameter.
     */
    protected static int getHowMany(ServletRequest request) {
        int howMany;
        try {
            howMany = Integer.parseInt(request.getParameter("howMany"));
        } catch (NumberFormatException nfe) {
            howMany = 0;
        }
        Preconditions.checkArgument(howMany > 0, "URL parameter howMany is mandatory and must be positive");
        return howMany;
    }
}
