package org.apache.http.impl.conn;

import java.util.Date;
import java.io.IOException;
import org.apache.http.HttpClientConnection;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.PoolEntry;

@Contract(threading = ThreadingBehavior.SAFE)
class CPoolEntry extends PoolEntry<HttpRoute, ManagedHttpClientConnection>
{
    private final Log log;
    private volatile boolean routeComplete;
    
    public CPoolEntry(final Log log, final String id, final HttpRoute route, final ManagedHttpClientConnection conn, final long timeToLive, final TimeUnit timeUnit) {
        super(id, (Object)route, (Object)conn, timeToLive, timeUnit);
        this.log = log;
    }
    
    public void markRouteComplete() {
        this.routeComplete = true;
    }
    
    public boolean isRouteComplete() {
        return this.routeComplete;
    }
    
    public void closeConnection() throws IOException {
        final HttpClientConnection conn = (HttpClientConnection)this.getConnection();
        conn.close();
    }
    
    public void shutdownConnection() throws IOException {
        final HttpClientConnection conn = (HttpClientConnection)this.getConnection();
        conn.shutdown();
    }
    
    public boolean isExpired(final long now) {
        final boolean expired = super.isExpired(now);
        if (expired && this.log.isDebugEnabled()) {
            this.log.debug((Object)("Connection " + this + " expired @ " + new Date(this.getExpiry())));
        }
        return expired;
    }
    
    public boolean isClosed() {
        final HttpClientConnection conn = (HttpClientConnection)this.getConnection();
        return !conn.isOpen();
    }
    
    public void close() {
        try {
            this.closeConnection();
        }
        catch (final IOException ex) {
            this.log.debug((Object)"I/O error closing connection", (Throwable)ex);
        }
    }
}
