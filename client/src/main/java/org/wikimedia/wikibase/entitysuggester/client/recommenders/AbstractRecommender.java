package org.wikimedia.wikibase.entitysuggester.client.recommenders;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import org.apache.mahout.cf.taste.common.TasteException;

/**
 * Abstract Recommender class that accepts a list of strings as input and
 * returns recommendations as TranslatedRecommendedItem.
 *
 * @author Nilesh Chakraborty
 */
public abstract class AbstractRecommender implements Recommender<TranslatedRecommendedItem, String> {

    /**
     * @see Recommender.recommenAsJSON
     *
     * @param input
     * @param howMany
     * @return JSON String.
     * @throws TasteException
     */
    @Override
    public final String recommendAsJSON(List<String> input, int howMany) throws TasteException {
        List<TranslatedRecommendedItem> list = recommend(input, howMany);
        List recommendations = new ArrayList();
        for (TranslatedRecommendedItem item : list) {
            recommendations.add(new Object[]{item.getItemID(), item.getValue()});
        }

        return new Gson().toJson(recommendations);
    }
}
