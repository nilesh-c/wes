package org.wikimedia.wikibase.entitysuggester.client.recommender;

import java.io.File;
import java.io.Reader;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import net.myrrix.client.MyrrixClientConfiguration;
import net.myrrix.client.translating.TranslatedRecommendedItem;
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
     * @param idListFile - The path to the file with the list of
     * wikibaseProperties This may be removed in the near future, if I remove
     * wikibaseValue suggestion from this part of the suggester engine.
     * @param myrrixClientConfiguration
     * @throws TasteException
     */
    public WebClientRecommender(String idListFile, MyrrixClientConfiguration myrrixClientConfiguration) throws TasteException {
        super(myrrixClientConfiguration);
        clientRecommender.addItemIDs(new File(idListFile));
    }

    /**
     * Same as above, except that it takes a URI instead of a String for
     * specifying the idListFile path.
     *
     * @param idListFile
     * @param myrrixClientConfiguration
     * @throws TasteException
     */
    public WebClientRecommender(URI idListFile, MyrrixClientConfiguration myrrixClientConfiguration) throws TasteException {
        super(myrrixClientConfiguration);
        clientRecommender.addItemIDs(new File(idListFile));
    }

    /**
     * Used to fetch wikibaseProperty suggestions for already existing
     * wikibaseItems.
     *
     * @param recommendTo
     * @param recommendType
     * @param howMany
     * @return
     * @throws TasteException
     */
    @Override
    public List<TranslatedRecommendedItem> recommend(String recommendTo, String recommendType, int howMany) throws TasteException {
        List<TranslatedRecommendedItem> recommendations = clientRecommender.recommend(recommendTo, howMany, false, new String[]{recommendType});
        return recommendations;
    }

    /**
     * Used to fetch wikibaseProperty suggestions for already anonymous (perhaps
     * being created) wikibaseItems.
     *
     * @param recommendType
     * @param howMany
     * @param list
     * @return
     * @throws TasteException
     */
    @Override
    public List<TranslatedRecommendedItem> recommendAnonymous(String recommendType, int howMany, String[] list) throws TasteException {
        float[] values = new float[list.length];
        Arrays.fill(values, 30);
        List<TranslatedRecommendedItem> recommendations = clientRecommender.recommendToAnonymous(list, values, howMany, new String[]{recommendType}, "testID");
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
