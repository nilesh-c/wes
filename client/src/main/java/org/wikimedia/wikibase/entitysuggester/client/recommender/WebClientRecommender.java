package org.wikimedia.wikibase.entitysuggester.client.recommender;

import java.io.File;
import java.io.Reader;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import net.myrrix.client.translating.TranslatingRecommender;
import org.apache.mahout.cf.taste.common.TasteException;

/**
 * The actual ClientRecommender class used inside the client servlets.
 *
 * @author Nilesh Chakraborty
 */
public class WebClientRecommender extends AbstractClientRecommender {

    /**
     * Create a WebClientRecommender.
     *
     * @param translatingRecommender
     * @throws TasteException
     */
    public WebClientRecommender(TranslatingRecommender translatingRecommender) throws TasteException {
        super(translatingRecommender);
    }

    /**
     * Create a WebClientRecommender.
     *
     * @param myrrixItemIdList - It is the list of all wikibaseProperties (as
     * Strings) in the data pushed through DataIngestServlet. This is a list
     * kept in memory used by the TranslatingClientRecommender to be able to
     * translate between the internal hashed numeric representation of the Pxxxx
     * wikibaseProperty Strings.
     */
    public void addPropertyIDs(List<String> myrrixItemIdList) {
        clientRecommender.addItemIDs(myrrixItemIdList);
    }

    /**
     * Used to fetch wikibaseProperty suggestions for already existing
     * wikibaseItems.
     *
     * @param recommendTo - a wikibaseItem as a String. All wikibaseItems and
     * wikibaseProperties as given with their prefixes
     * @param howMany
     * @return List of TranslatedRecommendedItems, the suggested
     * wikibaseProperties
     * @throws TasteException
     */
    @Override
    public List<TranslatedRecommendedItem> recommend(String recommendTo, int howMany) throws TasteException {
        List<TranslatedRecommendedItem> recommendations = clientRecommender.recommend(recommendTo, howMany);
        return recommendations;
    }

    /**
     * Used to fetch wikibaseProperty suggestions for anonymous (perhaps being
     * created) wikibaseItems.
     *
     * @param howMany
     * @param forProperties - An array of wikibaseProperties that are used in
     * the anonymous wikibaseItem
     * @return List of TranslatedRecommendedItems, the suggested
     * wikibaseProperties
     * @throws TasteException
     */
    @Override
    public List<TranslatedRecommendedItem> recommendAnonymous(String[] forProperties, int howMany) throws TasteException {
        List<TranslatedRecommendedItem> recommendations = clientRecommender.recommendToAnonymous(forProperties, howMany);
        return recommendations;
    }

    /**
     * Used to feed/train Myrrix with a csv file containing rows of
     * wikibaseItem, wikibaseProperty, weight
     *
     * @param csvReader
     * @throws TasteException
     */
    @Override
    public void ingest(Reader csvReader) throws TasteException {
        clientRecommender.ingest(csvReader);
    }
}
