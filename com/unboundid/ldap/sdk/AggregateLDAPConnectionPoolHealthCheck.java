package com.unboundid.ldap.sdk;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import com.unboundid.util.StaticUtils;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AggregateLDAPConnectionPoolHealthCheck extends LDAPConnectionPoolHealthCheck
{
    private final List<LDAPConnectionPoolHealthCheck> healthChecks;
    
    public AggregateLDAPConnectionPoolHealthCheck(final LDAPConnectionPoolHealthCheck... healthChecks) {
        this(StaticUtils.toList(healthChecks));
    }
    
    public AggregateLDAPConnectionPoolHealthCheck(final Collection<? extends LDAPConnectionPoolHealthCheck> healthChecks) {
        if (healthChecks == null) {
            this.healthChecks = Collections.emptyList();
        }
        else {
            this.healthChecks = Collections.unmodifiableList((List<? extends LDAPConnectionPoolHealthCheck>)new ArrayList<LDAPConnectionPoolHealthCheck>(healthChecks));
        }
    }
    
    @Override
    public void ensureNewConnectionValid(final LDAPConnection connection) throws LDAPException {
        for (final LDAPConnectionPoolHealthCheck hc : this.healthChecks) {
            hc.ensureNewConnectionValid(connection);
        }
    }
    
    @Override
    public void ensureConnectionValidAfterAuthentication(final LDAPConnection connection, final BindResult bindResult) throws LDAPException {
        for (final LDAPConnectionPoolHealthCheck hc : this.healthChecks) {
            hc.ensureConnectionValidAfterAuthentication(connection, bindResult);
        }
    }
    
    @Override
    public void ensureConnectionValidForCheckout(final LDAPConnection connection) throws LDAPException {
        for (final LDAPConnectionPoolHealthCheck hc : this.healthChecks) {
            hc.ensureConnectionValidForCheckout(connection);
        }
    }
    
    @Override
    public void ensureConnectionValidForRelease(final LDAPConnection connection) throws LDAPException {
        for (final LDAPConnectionPoolHealthCheck hc : this.healthChecks) {
            hc.ensureConnectionValidForRelease(connection);
        }
    }
    
    @Override
    public void ensureConnectionValidForContinuedUse(final LDAPConnection connection) throws LDAPException {
        for (final LDAPConnectionPoolHealthCheck hc : this.healthChecks) {
            hc.ensureConnectionValidForContinuedUse(connection);
        }
    }
    
    @Override
    public void performPoolMaintenance(final AbstractConnectionPool pool) {
        for (final LDAPConnectionPoolHealthCheck hc : this.healthChecks) {
            hc.performPoolMaintenance(pool);
        }
    }
    
    @Override
    public void ensureConnectionValidAfterException(final LDAPConnection connection, final LDAPException exception) throws LDAPException {
        for (final LDAPConnectionPoolHealthCheck hc : this.healthChecks) {
            hc.ensureConnectionValidAfterException(connection, exception);
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("AggregateLDAPConnectionPoolHealthCheck(healthChecks={");
        final Iterator<LDAPConnectionPoolHealthCheck> iterator = this.healthChecks.iterator();
        while (iterator.hasNext()) {
            iterator.next().toString(buffer);
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("})");
    }
}
