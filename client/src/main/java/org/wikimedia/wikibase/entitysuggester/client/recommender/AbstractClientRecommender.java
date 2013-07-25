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
import net.myrrix.client.translating.TranslatingRecommender;
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

    protected TranslatingRecommender clientRecommender;

    public AbstractClientRecommender(TranslatingRecommender clientRecommender) {
        this.clientRecommender = clientRecommender;
    }

    public abstract List<TranslatedRecommendedItem> recommend(String recommendTo, int howMany) throws TasteException;

    public abstract List<TranslatedRecommendedItem> recommendAnonymous(String[] list, int howMany) throws TasteException;

    public abstract void ingest(Reader csvReader) throws TasteException;
}
