package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

final class ParallelPoolConnectorTask implements Runnable
{
    private final AtomicReference<LDAPException> firstException;
    private final boolean throwOnConnectFailure;
    private final LDAPConnectionPool pool;
    private final List<LDAPConnection> connList;
    
    ParallelPoolConnectorTask(final LDAPConnectionPool pool, final List<LDAPConnection> connList, final AtomicReference<LDAPException> firstException, final boolean throwOnConnectFailure) {
        this.pool = pool;
        this.connList = connList;
        this.firstException = firstException;
        this.throwOnConnectFailure = throwOnConnectFailure;
    }
    
    @Override
    public void run() {
        try {
            if (this.throwOnConnectFailure && this.firstException.get() != null) {
                return;
            }
            this.connList.add(this.pool.createConnection());
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            if (this.throwOnConnectFailure) {
                this.firstException.compareAndSet(null, le);
            }
        }
    }
}
