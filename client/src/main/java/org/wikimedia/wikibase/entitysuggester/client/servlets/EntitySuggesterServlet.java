/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * wikibaseProperties for already existing (TODO) or anonymous wikibaseItems
 * @author Nilesh Chakraborty
 */
public class EntitySuggesterServlet extends AbstractEntitySuggesterServlet {

    /**
     * Handle a HTTP GET request to suggest wikibaseProperties for anonymous
     * wikibaseItems (that don't exist in the dataset)
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
            List<TranslatedRecommendedItem> recommended = getClientRecommender().recommendAnonymous(request.getParameter("type"), getHowMany(request), pathComponents);
            output(request, response, recommended);
        } catch (TasteException te) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, te.toString());
        }
    }

    /**
     * Output JSON-formatted results.
     * @param request
     * @param response
     * @param items
     * @throws IOException
     */
    protected final void output(HttpServletRequest request,
            ServletResponse response,
            List<TranslatedRecommendedItem> items) throws IOException {

        PrintWriter writer = response.getWriter();
        // Always print JSON
        writer.write('[');
        boolean first = true;
        for (TranslatedRecommendedItem item : items) {
            if (first) {
                first = false;
            } else {
                writer.write(',');
            }
            writer.write("[\"");
            writer.write(item.getItemID());
            writer.write("\",");
            writer.write(Float.toString(item.getValue()));
            writer.write(']');
        }
        writer.write(']');
    }
}
