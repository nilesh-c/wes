package org.wikimedia.wikibase.entitysuggester.client.recommenders;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;

/**
 * Generic Recommender interface for suggesting entitites.
 *
 * @param <T> The type of List to be returned by recommend.
 * @param <V> The type of List to be given as input.
 * @author nilesh
 */
public interface Recommender<T, V> {

    /**
     * Train the recommender.
     *
     * @param dataReader
     * @throws IOException
     * @throws TasteException
     */
    public abstract void train(Reader dataReader) throws IOException, TasteException;

    /**
     * Return recommendations.
     *
     * @param input
     * @param howMany
     * @return List of recommendations.
     * @throws TasteException
     */
    public abstract List<T> recommend(List<V> input, int howMany) throws TasteException;

    /**
     * Return the recommended entities in JSON format for convenience.
     *
     * @param input
     * @param howMany
     * @return JSON String.
     * @throws TasteException
     */
    public abstract String recommendAsJSON(List<V> input, int howMany) throws TasteException;
}
