/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wikimedia.wikibase.entitysuggester.client.recommender;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.myrrix.client.ClientRecommender;
import net.myrrix.client.MyrrixClientConfiguration;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import net.myrrix.client.translating.TranslatingClientRecommender;
import org.apache.mahout.cf.taste.common.TasteException;

/**
 * Extend this class to delegate calls for suggesting wikibaseProperties for
 * already existing and new wikibaseItems to a Myrrix
 * TranslatingClientRecommender object.
 *
 * ClientRecommenders are basically Java wrappers used to query the Myrrix
 * servlets using TranslatingClientRecommenders to handle String IDs to numeric
 * hashes to be used with Myrrix.
 *
 * @author Nilesh Chakraborty
 */
abstract class AbstractClientRecommender {

    protected TranslatingClientRecommender clientRecommender;

    public AbstractClientRecommender(MyrrixClientConfiguration myrrixClientConfiguration) {
        try {
            clientRecommender = new TranslatingClientRecommender(new ClientRecommender(myrrixClientConfiguration));
        } catch (IOException ex) {
            Logger.getLogger(CLIClientRecommender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<TranslatedRecommendedItem> recommend(String recommendTo, String recommendType, int howMany) throws TasteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TranslatedRecommendedItem> recommendAnonymous(String recommendType, int howMany, String[] list) throws TasteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TranslatedRecommendedItem> recommend(String idListFile, String recommendTo, String recommendType, int howMany) throws TasteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<TranslatedRecommendedItem> recommendAnonymous(String idListFile, String recommendType, int howMany, String[] list) throws TasteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void ingest(String csvFile) throws TasteException {
    }

    public void ingest(Reader csvReader) throws TasteException {
    }
}
