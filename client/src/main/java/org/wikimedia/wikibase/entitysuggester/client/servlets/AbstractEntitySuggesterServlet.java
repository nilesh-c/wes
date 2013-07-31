package org.wikimedia.wikibase.entitysuggester.client.servlets;

import com.google.common.base.Splitter;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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
        List suggestedProperties = new ArrayList();
        for (TranslatedRecommendedItem item : items) {
            suggestedProperties.add(new Object[]{item.getItemID(), item.getValue()});
        }
        String json = new Gson().toJson(suggestedProperties);
        writer.write(json);
        writer.flush();
    }
}
