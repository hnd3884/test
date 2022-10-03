package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import com.unboundid.util.DebugType;
import java.util.logging.Level;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class PruneUnneededConnectionsLDAPConnectionPoolHealthCheck extends LDAPConnectionPoolHealthCheck
{
    private final AtomicReference<Long> earliestTimeWithMoreThanMinAvailableConnections;
    private final int minAvailableConnections;
    private final long minDurationMillisExceedingMinAvailableConnections;
    
    public PruneUnneededConnectionsLDAPConnectionPoolHealthCheck(final int minAvailableConnections, final long minDurationMillisExceedingMinAvailableConnections) {
        this.minAvailableConnections = Math.max(0, minAvailableConnections);
        this.minDurationMillisExceedingMinAvailableConnections = Math.max(0L, minDurationMillisExceedingMinAvailableConnections);
        this.earliestTimeWithMoreThanMinAvailableConnections = new AtomicReference<Long>();
    }
    
    public int getMinAvailableConnections() {
        return this.minAvailableConnections;
    }
    
    public long getMinDurationMillisExceedingMinAvailableConnections() {
        return this.minDurationMillisExceedingMinAvailableConnections;
    }
    
    @Override
    public void performPoolMaintenance(final AbstractConnectionPool pool) {
        if (!(pool instanceof LDAPConnectionPool)) {
            Debug.debug(Level.WARNING, DebugType.CONNECT, "Only " + LDAPConnectionPool.class.getName() + " instances may be used in conjunction with the " + "PruneUnneededConnectionsLDAPConnectionPoolHealthCheck.  " + "The provided pool had an incompatible type of " + pool.getClass().getName() + '.');
            this.earliestTimeWithMoreThanMinAvailableConnections.set(null);
            return;
        }
        final int availableConnections = pool.getCurrentAvailableConnections();
        if (availableConnections <= this.minAvailableConnections) {
            this.earliestTimeWithMoreThanMinAvailableConnections.set(null);
            return;
        }
        final Long earliestTime = this.earliestTimeWithMoreThanMinAvailableConnections.get();
        if (earliestTime == null) {
            if (this.minDurationMillisExceedingMinAvailableConnections <= 0L) {
                ((LDAPConnectionPool)pool).shrinkPool(this.minAvailableConnections);
            }
            else {
                this.earliestTimeWithMoreThanMinAvailableConnections.set(System.currentTimeMillis());
            }
        }
        else {
            final long millisWithMoreThanMinAvailableConnections = System.currentTimeMillis() - earliestTime;
            if (millisWithMoreThanMinAvailableConnections >= this.minDurationMillisExceedingMinAvailableConnections) {
                ((LDAPConnectionPool)pool).shrinkPool(this.minAvailableConnections);
                this.earliestTimeWithMoreThanMinAvailableConnections.set(null);
            }
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("PruneUnneededConnectionsLDAPConnectionPoolHealthCheck(minAvailableConnections=");
        buffer.append(this.minAvailableConnections);
        buffer.append(", minDurationMillisExceedingMinAvailableConnections=");
        buffer.append(this.minDurationMillisExceedingMinAvailableConnections);
        buffer.append(')');
    }
}
