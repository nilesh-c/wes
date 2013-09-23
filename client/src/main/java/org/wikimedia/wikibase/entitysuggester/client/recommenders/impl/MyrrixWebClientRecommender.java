package org.wikimedia.wikibase.entitysuggester.client.recommenders.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import net.myrrix.client.translating.TranslatingRecommender;
import org.apache.mahout.cf.taste.common.TasteException;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.AbstractRecommender;

/**
 * A Recommender class used with a Myrrix instance to provide recommendations.
 * It delegates train and recommend to a TranslatingRecommender instance.
 *
 * @author Nilesh Chakraborty
 */
public class MyrrixWebClientRecommender extends AbstractRecommender {

    protected TranslatingRecommender translatingRecommender;

    /**
     * Initialize with a TranslatingRecommender.
     *
     * @param translatingRecommender
     */
    public MyrrixWebClientRecommender(TranslatingRecommender translatingRecommender) {
        this.translatingRecommender = translatingRecommender;
    }

    /**
     * Reads the dataReader into a StringBuilder, creates a list of Myrrix
     * "Items" and uses TranslatingRecommender.ingest and
     * TranslatingRecommender.addItemIDs to train the Myrrix instance.
     *
     * @see Recommender.train
     *
     * @param dataReader
     * @throws IOException
     * @throws TasteException
     */
    @Override
    public void train(Reader dataReader) throws IOException, TasteException {
        BufferedReader br = new BufferedReader(dataReader);
        ArrayList<String> myrrixItemList = new ArrayList<String>();
        String temp;
        StringBuilder dataBuilder = new StringBuilder();
        while ((temp = br.readLine()) != null) {
            if (temp.contains(",")) {
                // Assume dataReader cannot be reset. Cache the data as it is read, to be fed into Myrrix translatingRecommender later.
                dataBuilder.append(temp).append("\n");
                String[] chunks = temp.split(",");
                String property = "";
                for (int i = 1; i < chunks.length - 1; i++) { // Extract only the middle portion (the Myrrix "Item", not the "User")
                    property += chunks[i];
                }
                myrrixItemList.add(property); // Creating the list of Myrrix "Items"
            }
        }
        translatingRecommender.ingest(new StringReader(dataBuilder.toString()));
        translatingRecommender.addItemIDs(myrrixItemList);
    }

    /**
     * Provide recommendations based on the Myrrix Items in input using
     * collaborative filtering.
     *
     * @see Recommender.recommend
     *
     * @param input
     * @param howMany
     * @return
     * @throws TasteException
     */
    @Override
    public List<TranslatedRecommendedItem> recommend(List<String> input, int howMany) throws TasteException {
        String[] inputArray = input.toArray(new String[1]);
        return translatingRecommender.recommendToAnonymous(inputArray, howMany);
    }
}
