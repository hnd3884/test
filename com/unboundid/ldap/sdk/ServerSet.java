package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Extensible;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_THREADSAFE)
public abstract class ServerSet
{
    protected ServerSet() {
    }
    
    public boolean includesAuthentication() {
        return false;
    }
    
    public boolean includesPostConnectProcessing() {
        return false;
    }
    
    public abstract LDAPConnection getConnection() throws LDAPException;
    
    public LDAPConnection getConnection(final LDAPConnectionPoolHealthCheck healthCheck) throws LDAPException {
        final LDAPConnection c = this.getConnection();
        if (healthCheck != null) {
            try {
                healthCheck.ensureNewConnectionValid(c);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                c.close();
                throw le;
            }
        }
        return c;
    }
    
    protected static void doBindPostConnectAndHealthCheckProcessing(final LDAPConnection connection, final BindRequest bindRequest, final PostConnectProcessor postConnectProcessor, final LDAPConnectionPoolHealthCheck healthCheck) throws LDAPException {
        try {
            if (postConnectProcessor != null) {
                postConnectProcessor.processPreAuthenticatedConnection(connection);
            }
            if (bindRequest != null) {
                LDAPException bindException = null;
                BindResult bindResult;
                try {
                    bindResult = connection.bind(bindRequest.duplicate());
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    bindException = le;
                    bindResult = new BindResult(le);
                }
                if (healthCheck != null) {
                    healthCheck.ensureConnectionValidAfterAuthentication(connection, bindResult);
                }
                if (bindException != null) {
                    throw bindException;
                }
            }
            if (postConnectProcessor != null) {
                postConnectProcessor.processPostAuthenticatedConnection(connection);
            }
            if (healthCheck != null) {
                healthCheck.ensureNewConnectionValid(connection);
            }
        }
        catch (final LDAPException le2) {
            Debug.debugException(le2);
            connection.closeWithoutUnbind();
            throw le2;
        }
    }
    
    protected final void associateConnectionWithThisServerSet(final LDAPConnection connection) {
        if (connection != null) {
            connection.setServerSet(this);
        }
    }
    
    protected void handleConnectionClosed(final LDAPConnection connection, final String host, final int port, final DisconnectType disconnectType, final String message, final Throwable cause) {
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("ServerSet(className=");
        buffer.append(this.getClass().getName());
        buffer.append(')');
    }
}
