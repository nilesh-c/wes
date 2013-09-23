package org.wikimedia.wikibase.entitysuggester.client.recommenders.impl;

import com.google.common.base.Predicate;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import org.apache.mahout.cf.taste.common.TasteException;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.AbstractRecommender;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.util.TranslatedRecommendedItemDatasetParser;

/**
 * Recommender that consists of a List of TranslatedRecommendedItems;
 * recommendations are basically the top N elements of the list, given N.
 *
 * @author nilesh
 */
public class SortedListRecommender extends AbstractRecommender {

    protected List<TranslatedRecommendedItem> list = new ArrayList<TranslatedRecommendedItem>();

    /**
     * Reads the dataReader into the list of TranslatedRecommendedItems.
     *
     * @param dataReader
     * @throws IOException
     * @throws TasteException
     */
    @Override
    public void train(Reader dataReader) throws IOException, TasteException {
        Predicate<String> readUntil = new Predicate<String>() {

            @Override
            public boolean apply(String t) {
                return t == null;
            }
        };

        TranslatedRecommendedItemDatasetParser parser = new TranslatedRecommendedItemDatasetParser(dataReader, readUntil);

        parser.fetchNewKeyListPair();
        list.addAll(parser.getNewList());
    }

    /**
     * @param input
     * @param howMany
     * @return List with the top howMany elements from the list instance
     * variable. Ignore input in here, it'll most probably be empty.
     * @throws TasteException
     */
    @Override
    public List<TranslatedRecommendedItem> recommend(List<String> input, int howMany) throws TasteException {
        if (howMany > list.size()) {
            howMany = list.size();
        }
        return list.subList(0, howMany);
    }
}
