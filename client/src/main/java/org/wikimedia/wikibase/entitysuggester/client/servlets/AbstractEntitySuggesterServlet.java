package org.wikimedia.wikibase.entitysuggester.client.servlets;

import com.google.common.base.Splitter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.myrrix.client.ClientRecommender;
import net.myrrix.client.MyrrixClientConfiguration;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import net.myrrix.client.translating.TranslatingClientRecommender;
import net.myrrix.web.servlets.AbstractMyrrixServlet;
import org.apache.mahout.cf.taste.common.TasteException;
import org.wikimedia.wikibase.entitysuggester.client.recommender.WebClientRecommender;

/**
 * Base class for all client servlets
 *
 * @author Nilesh Chakraborty
 */
public abstract class AbstractEntitySuggesterServlet extends AbstractMyrrixServlet {

    static final Splitter SLASH = Splitter.on('/').omitEmptyStrings();
    private WebClientRecommender recommender = null;

    /**
     * Initializes the WebClientRecommender if it's null and returns it
     *
     * @return the WebClientRecommender instance
     */
    protected final WebClientRecommender getClientRecommender() {
        if (recommender == null) {
            synchronized (this) {
                if (recommender == null) {
                    recommender = (WebClientRecommender) this.getServletConfig().getServletContext().getAttribute("recommender");
                }
            }
        }
        return recommender;
    }

    /**
     * Output JSON-formatted results.
     *
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
        writer.flush();
    }
}
