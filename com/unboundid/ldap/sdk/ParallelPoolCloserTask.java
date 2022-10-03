package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import java.util.logging.Level;

final class ParallelPoolCloserTask implements Runnable
{
    private final boolean unbind;
    private final LDAPConnection connection;
    
    ParallelPoolCloserTask(final LDAPConnection connection, final boolean unbind) {
        this.connection = connection;
        this.unbind = unbind;
    }
    
    @Override
    public void run() {
        final AbstractConnectionPool pool = this.connection.getConnectionPool();
        if (pool != null) {
            final LDAPConnectionPoolStatistics stats = pool.getConnectionPoolStatistics();
            if (stats != null) {
                stats.incrementNumConnectionsClosedUnneeded();
                Debug.debugConnectionPool(Level.INFO, pool, this.connection, "Closing a pooled connection because the pool is closing", null);
            }
        }
        this.connection.setDisconnectInfo(DisconnectType.POOL_CLOSED, null, null);
        if (this.unbind) {
            this.connection.terminate(null);
        }
        else {
            this.connection.setClosed();
        }
    }
}
