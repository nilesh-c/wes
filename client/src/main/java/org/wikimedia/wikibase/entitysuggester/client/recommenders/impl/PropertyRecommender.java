package org.wikimedia.wikibase.entitysuggester.client.recommenders.impl;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import net.myrrix.client.translating.TranslatingRecommender;
import org.apache.mahout.cf.taste.common.TasteException;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.EmptyTrainable;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.Recommender;

/**
 * Recommends properties using a Myrrix instance; if the input is empty, it uses
 * a SortedListRecommender to provide suggestions.
 *
 * @see MyrrixWebClientRecommender
 * @see SortedListRecommender
 *
 * @author Nilesh Chakraborty
 */
public class PropertyRecommender extends MyrrixWebClientRecommender implements EmptyTrainable {

    protected Recommender emptyRecommender = new SortedListRecommender();

    /**
     * @param translatingRecommender
     */
    public PropertyRecommender(TranslatingRecommender translatingRecommender) {
        super(translatingRecommender);
    }

    /**
     * @see EmptyTrainable.trainEmpty
     *
     * @param dataReader
     * @throws IOException
     * @throws TasteException
     */
    @Override
    public void trainEmpty(Reader dataReader) throws IOException, TasteException {
        emptyRecommender.train(dataReader);
    }

    /**
     * Provide property recommendations based on the property IDs in input using
     * collaborative filtering.
     *
     * The property IDs should be prefixed if the dataset fed into the Myrrix
     * instance uses prefixed IDs (this is what currently happens in the Python
     * scripts).
     *
     * @see SortedListRecommender.recommend
     * @see MyrrixWebClientRecommender.recommend
     *
     * @param input
     * @param howMany
     * @return
     * @throws TasteException
     */
    @Override
    public List<TranslatedRecommendedItem> recommend(List<String> input, int howMany) throws TasteException {
        if (input.isEmpty()) {
            return emptyRecommender.recommend(input, howMany);
        } else {
            return super.recommend(input, howMany);
        }
    }
}
