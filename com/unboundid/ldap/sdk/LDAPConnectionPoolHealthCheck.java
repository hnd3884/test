package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public class LDAPConnectionPoolHealthCheck
{
    public void ensureNewConnectionValid(final LDAPConnection connection) throws LDAPException {
    }
    
    public void ensureConnectionValidAfterAuthentication(final LDAPConnection connection, final BindResult bindResult) throws LDAPException {
    }
    
    public void ensureConnectionValidForCheckout(final LDAPConnection connection) throws LDAPException {
    }
    
    public void ensureConnectionValidForRelease(final LDAPConnection connection) throws LDAPException {
    }
    
    public void ensureConnectionValidForContinuedUse(final LDAPConnection connection) throws LDAPException {
    }
    
    public void performPoolMaintenance(final AbstractConnectionPool pool) {
    }
    
    public void ensureConnectionValidAfterException(final LDAPConnection connection, final LDAPException exception) throws LDAPException {
        if (!ResultCode.isConnectionUsable(exception.getResultCode())) {
            throw new LDAPException(ResultCode.SERVER_DOWN, LDAPMessages.ERR_POOL_HEALTH_CHECK_CONN_INVALID_AFTER_EXCEPTION.get(StaticUtils.getExceptionMessage(exception)), exception);
        }
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("LDAPConnectionPoolHealthCheck()");
    }
}
