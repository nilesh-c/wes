package org.wikimedia.wikibase.entitysuggester.client.recommenders.util;

import net.myrrix.client.translating.TranslatedRecommendedItem;

/**
 * A basic implementation of TranslatedRecommendedItem.
 *
 * @author Nilesh Chakraborty
 */
public class TranslatedRecommendedItemImpl implements TranslatedRecommendedItem {

    private String itemID;
    private float value;

    /**
     * @param itemID
     * @param value
     */
    public TranslatedRecommendedItemImpl(String itemID, float value) {
        this.itemID = itemID;
        this.value = value;
    }

    /**
     * @param itemID
     */
    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    /**
     * @param value
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * @return String itemID
     */
    @Override
    public String getItemID() {
        return itemID;
    }

    /**
     * @return float value
     */
    @Override
    public float getValue() {
        return value;
    }
}
