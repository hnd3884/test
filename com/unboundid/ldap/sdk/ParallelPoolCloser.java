package com.unboundid.ldap.sdk;

import java.util.Iterator;
import com.unboundid.util.Debug;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.Collection;

final class ParallelPoolCloser
{
    private final boolean unbind;
    private final Collection<LDAPConnection> connections;
    private final int numThreads;
    
    ParallelPoolCloser(final Collection<LDAPConnection> connections, final boolean unbind, final int numThreads) {
        this.connections = connections;
        this.unbind = unbind;
        this.numThreads = numThreads;
    }
    
    void closeConnections() {
        final int numConnections = this.connections.size();
        final ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(numConnections);
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(this.numThreads, this.numThreads, 0L, TimeUnit.MILLISECONDS, queue);
        final ArrayList<Future<?>> results = new ArrayList<Future<?>>(numConnections);
        for (final LDAPConnection conn : this.connections) {
            results.add(executor.submit(new ParallelPoolCloserTask(conn, this.unbind)));
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
    }
}
