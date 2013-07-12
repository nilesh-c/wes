/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wikimedia.wikibase.entitysuggester.client.servlets;

import com.google.common.base.Splitter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.myrrix.client.MyrrixClientConfiguration;
import net.myrrix.web.servlets.AbstractMyrrixServlet;
import org.apache.mahout.cf.taste.common.TasteException;
import org.wikimedia.wikibase.entitysuggester.client.recommender.WebClientRecommender;

/**
 *
 * @author nilesh
 */
public abstract class AbstractEntitySuggesterServlet extends AbstractMyrrixServlet {

    static final Splitter SLASH = Splitter.on('/').omitEmptyStrings();
    private WebClientRecommender recommender = null;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (recommender == null) {
            initializeClientRecommender(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (recommender == null) {
            initializeClientRecommender(request, response);
        }
    }

    protected final WebClientRecommender getClientRecommender() {
        return recommender;
    }

    protected final URL getPropFilePath(String name) throws NamingException {
        Context initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        String fileName = (String) envCtx.lookup(name);
        URL filePath = getClass().getClassLoader().getResource(fileName);
        return filePath;
    }

    private void initializeClientRecommender(HttpServletRequest request, HttpServletResponse response) throws IOException {
        synchronized (this) {
            if (recommender == null) {
                try {
                    MyrrixClientConfiguration config = new MyrrixClientConfiguration();
                    config.setHost(request.getServerName());
                    config.setPort(request.getServerPort());
                    recommender = new WebClientRecommender(getPropFilePath("proplist").toURI(), config);
                } catch (URISyntaxException use) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, use.toString());
                } catch (NamingException ne) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, ne.toString());
                } catch (TasteException te) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, te.toString());
                }
            }
        }
    }
}
