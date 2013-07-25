package org.wikimedia.wikibase.entitysuggester.client.servlets;

import com.google.common.base.Charsets;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.myrrix.client.ClientRecommender;
import net.myrrix.client.MyrrixClientConfiguration;
import net.myrrix.client.translating.TranslatingClientRecommender;
import org.apache.mahout.cf.taste.common.TasteException;
import org.wikimedia.wikibase.entitysuggester.client.recommender.WebClientRecommender;

/**
 * This is part of the actual REST API - client servlet to feed/train the Myrrix
 * engine with a csv file.
 *
 * @see WebClientRecommender.ingest()
 *
 * @author Nilesh Chakraborty
 */
public class DataIngestServlet extends AbstractEntitySuggesterServlet {

    /**
     * Handle a HTTP POST request where the training file contents are sent in
     * the POST body.
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        super.doPost(request, response);
        Reader reader;
        String contentEncoding = request.getHeader("Content-Encoding");
        if (contentEncoding == null) {
            reader = request.getReader();
        } else if ("gzip".equals(contentEncoding)) {
            String charEncodingName = request.getCharacterEncoding();
            Charset charEncoding = charEncodingName == null ? Charsets.UTF_8 : Charset.forName(charEncodingName);
            reader = new InputStreamReader(new GZIPInputStream(request.getInputStream()), charEncoding);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported Content-Encoding");
            return;
        }

        BufferedReader br = new BufferedReader(reader);
        ArrayList<String> myrrixItemList = new ArrayList<String>();
        String temp;
        StringBuilder dataBuilder = new StringBuilder();
        while ((temp = br.readLine()) != null) {
            if (temp.contains(",")) {
                dataBuilder.append(temp).append("\n");
                String[] chunks = temp.split(",");
                String property = "";
                for (int i = 1; i < chunks.length - 1; i++) { // Extract only the middle portion (the wikibaseProperty)
                    property += chunks[i];
                }
                myrrixItemList.add(property); // Creating the list of wikibaseProperties
            }
        }

        try {
            MyrrixClientConfiguration config = new MyrrixClientConfiguration();
            config.setHost(request.getServerName());
            config.setPort(request.getServerPort());
            if (getServletConfig().getServletContext().getAttribute("recommender") == null) {
                WebClientRecommender webClientRecommender = new WebClientRecommender(new TranslatingClientRecommender(new ClientRecommender(config)));
                getServletConfig().getServletContext().setAttribute("recommender", webClientRecommender);
            }
            getClientRecommender().addPropertyIDs(myrrixItemList);
            getClientRecommender().ingest(new StringReader(dataBuilder.toString()));
        } catch (IllegalArgumentException iae) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, iae.toString());
        } catch (NoSuchElementException nsee) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, nsee.toString());
        } catch (TasteException te) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, te.toString());
            getServletContext().log("Unexpected error in " + getClass().getSimpleName(), te);
        }
    }
}
