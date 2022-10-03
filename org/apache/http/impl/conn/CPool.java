package org.apache.http.impl.conn;

import org.apache.http.pool.PoolEntry;
import org.apache.http.pool.PoolEntryCallback;
import org.apache.commons.logging.LogFactory;
import org.apache.http.pool.ConnFactory;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.AbstractConnPool;

@Contract(threading = ThreadingBehavior.SAFE)
class CPool extends AbstractConnPool<HttpRoute, ManagedHttpClientConnection, CPoolEntry>
{
    private static final AtomicLong COUNTER;
    private final Log log;
    private final long timeToLive;
    private final TimeUnit timeUnit;
    
    public CPool(final ConnFactory<HttpRoute, ManagedHttpClientConnection> connFactory, final int defaultMaxPerRoute, final int maxTotal, final long timeToLive, final TimeUnit timeUnit) {
        super((ConnFactory)connFactory, defaultMaxPerRoute, maxTotal);
        this.log = LogFactory.getLog((Class)CPool.class);
        this.timeToLive = timeToLive;
        this.timeUnit = timeUnit;
    }
    
    protected CPoolEntry createEntry(final HttpRoute route, final ManagedHttpClientConnection conn) {
        final String id = Long.toString(CPool.COUNTER.getAndIncrement());
        return new CPoolEntry(this.log, id, route, conn, this.timeToLive, this.timeUnit);
    }
    
    protected boolean validate(final CPoolEntry entry) {
        return !((ManagedHttpClientConnection)entry.getConnection()).isStale();
    }
    
    protected void enumAvailable(final PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> callback) {
        super.enumAvailable((PoolEntryCallback)callback);
    }
    
    protected void enumLeased(final PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> callback) {
        super.enumLeased((PoolEntryCallback)callback);
    }
    
    static {
        COUNTER = new AtomicLong();
    }
}
