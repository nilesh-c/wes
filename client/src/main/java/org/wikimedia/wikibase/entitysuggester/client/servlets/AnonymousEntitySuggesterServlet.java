package org.wikimedia.wikibase.entitysuggester.client.servlets;

import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import org.apache.mahout.cf.taste.common.TasteException;

/**
 * This is part of the actual REST API - client servlet to suggest
 * wikibaseProperties for anonymous wikibaseItems.
 *
 * @author Nilesh Chakraborty
 */
public class AnonymousEntitySuggesterServlet extends AbstractEntitySuggesterServlet {

    /**
     * Handle a HTTP GET request to suggest wikibaseProperties for anonymous
     * wikibaseItems (that don't exist in the dataset)
     *
     * The properties are given as input in the following format:
     * http://host:port/entitysuggester/suggest/P192,P25,P62 where
     * "/entitysuggester/suggest/" is defined in web.xml.
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        super.doGet(request, response);

        String pathInfo = request.getPathInfo();
        String[] pathComponents = Iterables.toArray(SLASH.split(pathInfo), String.class);

        if (pathComponents.length == 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            List<TranslatedRecommendedItem> recommended = getClientRecommender().recommendAnonymous(pathComponents, getHowMany(request));
            output(request, response, recommended);
        } catch (TasteException te) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, te.toString());
        }
    }
}
