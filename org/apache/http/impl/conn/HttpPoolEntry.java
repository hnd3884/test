package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.commons.logging.Log;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.PoolEntry;

@Deprecated
class HttpPoolEntry extends PoolEntry<HttpRoute, OperatedClientConnection>
{
    private final Log log;
    private final RouteTracker tracker;
    
    public HttpPoolEntry(final Log log, final String id, final HttpRoute route, final OperatedClientConnection conn, final long timeToLive, final TimeUnit timeUnit) {
        super(id, (Object)route, (Object)conn, timeToLive, timeUnit);
        this.log = log;
        this.tracker = new RouteTracker(route);
    }
    
    public boolean isExpired(final long now) {
        final boolean expired = super.isExpired(now);
        if (expired && this.log.isDebugEnabled()) {
            this.log.debug((Object)("Connection " + this + " expired @ " + new Date(this.getExpiry())));
        }
        return expired;
    }
    
    RouteTracker getTracker() {
        return this.tracker;
    }
    
    HttpRoute getPlannedRoute() {
        return (HttpRoute)this.getRoute();
    }
    
    HttpRoute getEffectiveRoute() {
        return this.tracker.toRoute();
    }
    
    public boolean isClosed() {
        final OperatedClientConnection conn = (OperatedClientConnection)this.getConnection();
        return !conn.isOpen();
    }
    
    public void close() {
        final OperatedClientConnection conn = (OperatedClientConnection)this.getConnection();
        try {
            conn.close();
        }
        catch (final IOException ex) {
            this.log.debug((Object)"I/O error closing connection", (Throwable)ex);
        }
    }
}
