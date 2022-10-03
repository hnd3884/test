package com.unboundid.ldap.sdk;

import java.util.Iterator;
import com.unboundid.util.Debug;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.List;

final class ParallelPoolConnector
{
    private final boolean throwOnConnectFailure;
    private final int numConnections;
    private final int numThreads;
    private final LDAPConnectionPool pool;
    private final List<LDAPConnection> connList;
    
    ParallelPoolConnector(final LDAPConnectionPool pool, final List<LDAPConnection> connList, final int numConnections, final int numThreads, final boolean throwOnConnectFailure) {
        this.pool = pool;
        this.connList = connList;
        this.numConnections = numConnections;
        this.numThreads = numThreads;
        this.throwOnConnectFailure = throwOnConnectFailure;
    }
    
    void establishConnections() throws LDAPException {
        final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(this.numConnections);
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(this.numThreads, this.numThreads, 0L, TimeUnit.MILLISECONDS, queue);
        final AtomicReference<LDAPException> firstException = new AtomicReference<LDAPException>();
        final ArrayList<Future<?>> results = new ArrayList<Future<?>>(this.numConnections);
        for (int i = 0; i < this.numConnections; ++i) {
            results.add(executor.submit(new ParallelPoolConnectorTask(this.pool, this.connList, firstException, this.throwOnConnectFailure)));
        }
        for (final Future<?> f : results) {
            try {
                f.get();
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        executor.shutdown();
        if (this.throwOnConnectFailure) {
            final LDAPException le = firstException.get();
            if (le != null) {
                for (final LDAPConnection c : this.connList) {
                    c.terminate(null);
                }
                this.connList.clear();
                throw le;
            }
        }
    }
}
