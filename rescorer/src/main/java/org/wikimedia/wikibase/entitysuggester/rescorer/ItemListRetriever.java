package org.wikimedia.wikibase.entitysuggester.rescorer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import net.myrrix.common.random.MemoryIDMigrator;
import org.apache.mahout.common.iterator.FileLineIterable;

/**
 * Used with EntityRescorerProvider to initialize an IDMigrator instance and use
 * it to translate wikibaseProperty or wikibaseValue IDs.
 *
 * This may be removed or refactored heavily shortly if I remove wikibaseValue
 * suggestion from this part of the suggester.
 *
 * @author Nilesh Chakraborty
 */
public class ItemListRetriever {

    private MemoryIDMigrator idTranslator;

    ItemListRetriever(URI itemlistFileName) throws IOException {
        idTranslator = new MemoryIDMigrator();
        File f = new File(itemlistFileName);
        idTranslator.initialize(new FileLineIterable(f));
        System.out.println("Finished id translator init");
    }

    String getStringIDFor(long longID) {
        return idTranslator.toStringID(longID);
    }
}
