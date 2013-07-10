/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysuggester.client.idmigrator;

/**
 *
 * @author nilesh
 */
public final class OneWayIDMigrator extends AbstractIDMigrator {

    @Override
    public String toStringID(long longID) {
        throw new UnsupportedOperationException();
    }
}
