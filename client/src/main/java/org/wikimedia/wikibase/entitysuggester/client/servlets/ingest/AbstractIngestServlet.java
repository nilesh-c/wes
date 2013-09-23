package org.wikimedia.wikibase.entitysuggester.client.servlets.ingest;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.mahout.cf.taste.common.TasteException;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.Recommender;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.util.RecommenderFactory;
import org.wikimedia.wikibase.entitysuggester.client.servlets.AbstractEntitySuggesterServlet;

/**
 * Abstract Servlet class that all Ingester Servlets should extend.
 *
 * @author Nilesh Chakraborty
 */
public abstract class AbstractIngestServlet extends AbstractEntitySuggesterServlet {

    /**
     * Checks if a Recommender has been set for this entity type and creates one
     * if needed; then it calls trainRecommender with the request reader.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Recommender recommender = getRecommender();
        if (recommender == null) {
            //Changed request.getServerName() to "127.0.0.1" because of NAT problems on test server.
            recommender = RecommenderFactory.create(getEntityType(), "127.0.0.1", request.getServerPort());
            setRecommender(recommender);
        }

        try {
            trainRecommender(getRequestReader(request, response));
        } catch (TasteException ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.toString());
            getServletContext().log("Unexpected error in " + getClass().getSimpleName(), ex);
        }
    }

    /**
     * Returns the request reader after performing some checks.
     *
     * @param request
     * @param response
     * @return Reader that reads the request body after checking the
     * Content-Encoding and wrapping it as necessary.
     * @throws IOException
     */
    protected Reader getRequestReader(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            return null;
        }
        return reader;
    }

    /**
     * Trains the Recommender for this Servlet using the data read from
     * dataReader.
     *
     * @param dataReader
     * @throws IOException
     * @throws TasteException
     */
    protected void trainRecommender(Reader dataReader) throws IOException, TasteException {
        getRecommender().train(dataReader);
    }
}
