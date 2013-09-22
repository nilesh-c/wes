package org.wikimedia.wikibase.entitysuggester.client.recommenders.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import org.apache.mahout.cf.taste.common.TasteException;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.AbstractRecommender;
import org.wikimedia.wikibase.entitysuggester.client.recommenders.util.TranslatedRecommendedItemDatasetParser;

/**
 * This recommender basically consists of a Map of Lists and provides
 * recommendations by pulling the top N entries from a list, given a specific
 * key.
 *
 * The data passed through train should have the following format:
 * ####SOMEKEY
 * blah1,34
 * blah3,454
 * blahother,2
 * ####SOMEOTHERKEY
 * blah5,2
 * blahagain,54
 * blah2,2
 *
 * Where the "blah"s are the IDs of entries in the list of
 * TranslatedRecommendedItems and the numeric right-hand-side part is the count
 * or relative score.
 *
 * Example use case for value suggestions: A dataset with properties as keys,
 * and the items that have appeared as values corresponding to those properties
 * and the entry IDs with the frequency of occurrence (count) as relative
 * scores..
 *
 * @see TranslatedRecommendedItemDatasetParser
 *
 * @author Nilesh Chakraborty
 */
public class MultiMapRecommender extends AbstractRecommender {

    private ListMultimap<String, TranslatedRecommendedItem> multimap = ArrayListMultimap.create();

    /**
     * @see Recommender.train
     *
     * @param dataReader
     * @throws IOException
     */
    @Override
    public void train(Reader dataReader) throws IOException {

        //Pass this predicate to the parser to split the data into chunks of
        //String key - List list pairs, where the keys begin with '####'.
        Predicate<String> readUntil = new Predicate<String>() {

            @Override
            public boolean apply(String t) {
                return t == null || t.contains("####");
            }
        };

        TranslatedRecommendedItemDatasetParser parser = new TranslatedRecommendedItemDatasetParser(dataReader, readUntil);

        while (parser.hasNext()) {
            parser.fetchNewKeyListPair();
            multimap.putAll(parser.getNewKey().substring(4), parser.getNewList());
        }
    }

    /**
     * @see Recommender.recommend
     *
     * @param input
     * @param howMany
     * @return List of TranslatedRecommendItems
     */
    @Override
    public List<TranslatedRecommendedItem> recommend(List<String> input, int howMany) throws TasteException {
        if (input.isEmpty()) {
            throw new TasteException("No input given.");
        }
        List<TranslatedRecommendedItem> list = multimap.get(input.get(0));
        if (howMany > list.size()) {
            howMany = list.size();
        }
        return list.subList(0, howMany);
    }
}
