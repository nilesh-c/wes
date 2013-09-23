package org.wikimedia.wikibase.entitysuggester.client.recommenders;

import java.io.IOException;
import java.io.Reader;
import org.apache.mahout.cf.taste.common.TasteException;

/**
 * Interface for Recommenders that may provide recommendations for empty input
 * and therefore need to be trained for it.
 *
 * @author nilesh
 */
public interface EmptyTrainable {

    /**
     * Train the recommender for providing recommendations for empty inputs.
     *
     * @param dataReader
     * @throws IOException
     * @throws TasteException
     */
    public abstract void trainEmpty(Reader dataReader) throws IOException, TasteException;
}
