package com.unboundid.ldap.sdk;

import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Serializable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.MOSTLY_THREADSAFE)
public final class LDAPConnectionPoolStatistics implements Serializable
{
    private static final long serialVersionUID = 1493039391352814874L;
    private final AtomicLong numConnectionsClosedDefunct;
    private final AtomicLong numConnectionsClosedExpired;
    private final AtomicLong numConnectionsClosedUnneeded;
    private final AtomicLong numFailedCheckouts;
    private final AtomicLong numFailedConnectionAttempts;
    private final AtomicLong numReleasedValid;
    private final AtomicLong numSuccessfulCheckouts;
    private final AtomicLong numSuccessfulCheckoutsAfterWait;
    private final AtomicLong numSuccessfulCheckoutsNewConnection;
    private final AtomicLong numSuccessfulCheckoutsWithoutWait;
    private final AtomicLong numSuccessfulConnectionAttempts;
    private final AbstractConnectionPool pool;
    
    public LDAPConnectionPoolStatistics(final AbstractConnectionPool pool) {
        this.pool = pool;
        this.numSuccessfulConnectionAttempts = new AtomicLong(0L);
        this.numFailedConnectionAttempts = new AtomicLong(0L);
        this.numConnectionsClosedDefunct = new AtomicLong(0L);
        this.numConnectionsClosedExpired = new AtomicLong(0L);
        this.numConnectionsClosedUnneeded = new AtomicLong(0L);
        this.numSuccessfulCheckouts = new AtomicLong(0L);
        this.numSuccessfulCheckoutsAfterWait = new AtomicLong(0L);
        this.numSuccessfulCheckoutsNewConnection = new AtomicLong(0L);
        this.numSuccessfulCheckoutsWithoutWait = new AtomicLong(0L);
        this.numFailedCheckouts = new AtomicLong(0L);
        this.numReleasedValid = new AtomicLong(0L);
    }
    
    public void reset() {
        this.numSuccessfulConnectionAttempts.set(0L);
        this.numFailedConnectionAttempts.set(0L);
        this.numConnectionsClosedDefunct.set(0L);
        this.numConnectionsClosedExpired.set(0L);
        this.numConnectionsClosedUnneeded.set(0L);
        this.numSuccessfulCheckouts.set(0L);
        this.numSuccessfulCheckoutsAfterWait.set(0L);
        this.numSuccessfulCheckoutsNewConnection.set(0L);
        this.numSuccessfulCheckoutsWithoutWait.set(0L);
        this.numFailedCheckouts.set(0L);
        this.numReleasedValid.set(0L);
    }
    
    public long getNumSuccessfulConnectionAttempts() {
        return this.numSuccessfulConnectionAttempts.get();
    }
    
    void incrementNumSuccessfulConnectionAttempts() {
        this.numSuccessfulConnectionAttempts.incrementAndGet();
    }
    
    public long getNumFailedConnectionAttempts() {
        return this.numFailedConnectionAttempts.get();
    }
    
    void incrementNumFailedConnectionAttempts() {
        this.numFailedConnectionAttempts.incrementAndGet();
    }
    
    public long getNumConnectionsClosedDefunct() {
        return this.numConnectionsClosedDefunct.get();
    }
    
    void incrementNumConnectionsClosedDefunct() {
        this.numConnectionsClosedDefunct.incrementAndGet();
    }
    
    public long getNumConnectionsClosedExpired() {
        return this.numConnectionsClosedExpired.get();
    }
    
    void incrementNumConnectionsClosedExpired() {
        this.numConnectionsClosedExpired.incrementAndGet();
    }
    
    public long getNumConnectionsClosedUnneeded() {
        return this.numConnectionsClosedUnneeded.get();
    }
    
    void incrementNumConnectionsClosedUnneeded() {
        this.numConnectionsClosedUnneeded.incrementAndGet();
    }
    
    public long getNumSuccessfulCheckouts() {
        return this.numSuccessfulCheckouts.get();
    }
    
    public long getNumSuccessfulCheckoutsWithoutWaiting() {
        return this.numSuccessfulCheckoutsWithoutWait.get();
    }
    
    public long getNumSuccessfulCheckoutsAfterWaiting() {
        return this.numSuccessfulCheckoutsAfterWait.get();
    }
    
    public long getNumSuccessfulCheckoutsNewConnection() {
        return this.numSuccessfulCheckoutsNewConnection.get();
    }
    
    void incrementNumSuccessfulCheckoutsWithoutWaiting() {
        this.numSuccessfulCheckouts.incrementAndGet();
        this.numSuccessfulCheckoutsWithoutWait.incrementAndGet();
    }
    
    void incrementNumSuccessfulCheckoutsAfterWaiting() {
        this.numSuccessfulCheckouts.incrementAndGet();
        this.numSuccessfulCheckoutsAfterWait.incrementAndGet();
    }
    
    void incrementNumSuccessfulCheckoutsNewConnection() {
        this.numSuccessfulCheckouts.incrementAndGet();
        this.numSuccessfulCheckoutsNewConnection.incrementAndGet();
    }
    
    public long getNumFailedCheckouts() {
        return this.numFailedCheckouts.get();
    }
    
    void incrementNumFailedCheckouts() {
        this.numFailedCheckouts.incrementAndGet();
    }
    
    public long getNumReleasedValid() {
        return this.numReleasedValid.get();
    }
    
    void incrementNumReleasedValid() {
        this.numReleasedValid.incrementAndGet();
    }
    
    public int getNumAvailableConnections() {
        return this.pool.getCurrentAvailableConnections();
    }
    
    public int getMaximumAvailableConnections() {
        return this.pool.getMaximumAvailableConnections();
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        final long availableConns = this.pool.getCurrentAvailableConnections();
        final long maxConns = this.pool.getMaximumAvailableConnections();
        final long successfulConns = this.numSuccessfulConnectionAttempts.get();
        final long failedConns = this.numFailedConnectionAttempts.get();
        final long connsClosedDefunct = this.numConnectionsClosedDefunct.get();
        final long connsClosedExpired = this.numConnectionsClosedExpired.get();
        final long connsClosedUnneeded = this.numConnectionsClosedUnneeded.get();
        final long successfulCheckouts = this.numSuccessfulCheckouts.get();
        final long failedCheckouts = this.numFailedCheckouts.get();
        final long releasedValid = this.numReleasedValid.get();
        buffer.append("LDAPConnectionPoolStatistics(numAvailableConnections=");
        buffer.append(availableConns);
        buffer.append(", maxAvailableConnections=");
        buffer.append(maxConns);
        buffer.append(", numSuccessfulConnectionAttempts=");
        buffer.append(successfulConns);
        buffer.append(", numFailedConnectionAttempts=");
        buffer.append(failedConns);
        buffer.append(", numConnectionsClosedDefunct=");
        buffer.append(connsClosedDefunct);
        buffer.append(", numConnectionsClosedExpired=");
        buffer.append(connsClosedExpired);
        buffer.append(", numConnectionsClosedUnneeded=");
        buffer.append(connsClosedUnneeded);
        buffer.append(", numSuccessfulCheckouts=");
        buffer.append(successfulCheckouts);
        buffer.append(", numFailedCheckouts=");
        buffer.append(failedCheckouts);
        buffer.append(", numReleasedValid=");
        buffer.append(releasedValid);
        buffer.append(')');
    }
}
