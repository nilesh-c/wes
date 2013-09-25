package org.wikimedia.wikibase.entitysuggester.client.recommenders.util;

import com.google.common.base.Predicate;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import net.myrrix.common.io.IOUtils;

/**
 * Used to iterate through a dataset and get the keys and corresponding lists
 * after sorting and normalizing them.
 * 
 * @author Nilesh Chakraborty
 */
public class TranslatedRecommendedItemDatasetParser {

    private Predicate<String> keyFound;
    private BufferedReader reader;
    private ArrayList<TranslatedRecommendedItem> list = null;
    private String key = null;
    private String temp = null;
    private boolean hasNext;

    /**
     * The data passed through reader should have the following format:
     * ####SOMEKEY
     * blah1,34
     * blah3,454
     * blahother,2
     * ####SOMEOTHERKEY
     * blah5,2
     * blahagain,54
     * blah2,2
     * 
     * The object passed through keyFound has to implement the Predicate.apply
     * method which is used to check whether the next key has been found or the
     * end has been reached. For example, the constraint for the above example is
     * if the current line is equal to null or contains '####'.
     * 
     * @param reader
     * @param keyFound
     * @throws IOException
     */
    public TranslatedRecommendedItemDatasetParser(Reader reader, Predicate<String> keyFound) throws IOException {
        this.keyFound = keyFound;
        this.reader = IOUtils.buffer(reader);
        temp = this.reader.readLine();
        hasNext = temp != null;
    }

    /**
     * @return list The newly generated ArrayList.
     */
    public ArrayList<TranslatedRecommendedItem> getNewList() {
        return list;
    }

    /**
     * @return key The newly generated String key (that satisfies the Predicate.apply method).
     */
    public String getNewKey() {
        return key;
    }

    /**
     * @return hasNext boolean to check whether the end of the dataset has been reached.
     */
    public boolean hasNext() {
        return hasNext;
    }

    /**
     * Parses the dataset and sets key to the new key that satisfies the predicate
     * and sets list to the new list of TranslatedRecommendedItems generated
     * from the dataset.
     * 
     * Each row is split by comma; the left part is the entry ID and the right part
     * is the relative score, that is generally the frequency/count of occurrences.
     * 
     * @throws IOException
     */
    public void fetchNewKeyListPair() throws IOException {
        list = new ArrayList<TranslatedRecommendedItem>();
        float countSum = 0, count = 0;

        if (keyFound.apply(temp) == true) {
            key = temp.trim();
            temp = reader.readLine().trim();
        }

        while (keyFound.apply(temp) == false) {
            String strings[] = temp.trim().split(",");
            String item = strings[0].trim();
            count = Integer.parseInt(strings[1]);
            countSum += count;
            list.add(new TranslatedRecommendedItemImpl(item, count));
            temp = reader.readLine();
        }

        sortAndNormalizeList(list, countSum);
        
        hasNext = temp != null;
    }

    /**
     * Normalizes the list's relative scores or values by dividing each by
     * countSum, and sorts it in descending order of that value.
     * 
     * This is done so that recommending from a list now becomes a matter of fetching
     * the top N items from the list.
     * 
     * @param list
     * @param countSum
     */
    protected void sortAndNormalizeList(ArrayList<TranslatedRecommendedItem> list, float countSum) {
        for (Iterator<TranslatedRecommendedItem> it = list.iterator(); it.hasNext();) {
            TranslatedRecommendedItemImpl item = (TranslatedRecommendedItemImpl) it.next();
            item.setValue(item.getValue() / countSum); // Normalize all values by the sum of counts
        }

        Collections.sort(list, new Comparator<TranslatedRecommendedItem>() {

            @Override
            public int compare(TranslatedRecommendedItem o1, TranslatedRecommendedItem o2) {
                return Float.compare(o2.getValue(), o1.getValue());
            }
        });
    }
}
