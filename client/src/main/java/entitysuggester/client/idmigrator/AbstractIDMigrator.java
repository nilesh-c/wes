package entitysuggester.client.idmigrator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import com.google.common.base.Charsets;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.model.IDMigrator;

/**
 * Taken from Mahout and modified...docs coming later
 * @author nilesh
 */
public abstract class AbstractIDMigrator implements IDMigrator {

    private final MessageDigest md5Digest;

    protected AbstractIDMigrator() {
        try {
            md5Digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            // Can't happen
            throw new IllegalStateException(nsae);
        }
    }

    protected final long hash(String value) {
        byte[] md5hash;
        synchronized (md5Digest) {
            md5hash = md5Digest.digest(value.getBytes(Charsets.UTF_8));
            md5Digest.reset();
        }
        long hash = 0L;
        for (int i = 0; i < 8; i++) {
            hash = hash << 8 | md5hash[i] & 0x00000000000000FFL;
        }
        return hash < 0 ? -hash : hash;
    }

    @Override
    public long toLongID(String stringID) {
        return hash(stringID);
    }

    @Override
    public void refresh(Collection<Refreshable> alreadyRefreshed) {
    }
}
