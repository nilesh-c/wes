/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysuggester.client.idmigrator;

import org.apache.mahout.cf.taste.model.UpdatableIDMigrator;
import net.myrrix.common.collection.FastByIDMap;

/**
 * Taken from Mahout and modified...docs coming later
 *
 * @author nilesh
 */
public final class MemoryIDMigrator extends AbstractIDMigrator implements UpdatableIDMigrator {

    private final FastByIDMap<String> longToString;

    public MemoryIDMigrator() {
        this.longToString = new FastByIDMap<String>(100);
    }

    @Override
    public void storeMapping(long longID, String stringID) {
        synchronized (longToString) {
            longToString.put(longID, stringID);
        }
    }

    @Override
    public String toStringID(long longID) {
        synchronized (longToString) {
            return longToString.get(longID);
        }
    }

    @Override
    public void initialize(Iterable<String> stringIDs) {
        for (String stringID : stringIDs) {
            storeMapping(toLongID(stringID), stringID);
        }
    }
}