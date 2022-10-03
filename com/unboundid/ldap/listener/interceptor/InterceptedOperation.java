package com.unboundid.ldap.listener.interceptor;

import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.protocol.IntermediateResponseProtocolOp;
import com.unboundid.ldap.sdk.IntermediateResponse;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.listener.LDAPListenerClientConnection;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
abstract class InterceptedOperation implements InMemoryInterceptedRequest, InMemoryInterceptedResult
{
    private final int messageID;
    private final LDAPListenerClientConnection clientConnection;
    private final Map<String, Object> propertyMap;
    
    InterceptedOperation(final LDAPListenerClientConnection clientConnection, final int messageID) {
        this.clientConnection = clientConnection;
        this.messageID = messageID;
        this.propertyMap = new HashMap<String, Object>(StaticUtils.computeMapCapacity(10));
    }
    
    InterceptedOperation(final InterceptedOperation operation) {
        this.clientConnection = operation.clientConnection;
        this.messageID = operation.messageID;
        this.propertyMap = operation.propertyMap;
    }
    
    LDAPListenerClientConnection getClientConnection() {
        return this.clientConnection;
    }
    
    @Override
    public final long getConnectionID() {
        if (this.clientConnection == null) {
            return -1L;
        }
        return this.clientConnection.getConnectionID();
    }
    
    @Override
    public String getConnectedAddress() {
        if (this.clientConnection == null) {
            return null;
        }
        final Socket s = this.clientConnection.getSocket();
        if (s == null) {
            return null;
        }
        final InetAddress localAddress = s.getLocalAddress();
        if (localAddress == null) {
            return null;
        }
        return localAddress.getHostAddress();
    }
    
    @Override
    public int getConnectedPort() {
        if (this.clientConnection == null) {
            return -1;
        }
        final Socket s = this.clientConnection.getSocket();
        if (s == null) {
            return -1;
        }
        return s.getLocalPort();
    }
    
    @Override
    public final int getMessageID() {
        return this.messageID;
    }
    
    @Override
    public final void sendIntermediateResponse(final IntermediateResponse intermediateResponse) throws LDAPException {
        this.clientConnection.sendIntermediateResponse(this.messageID, new IntermediateResponseProtocolOp(intermediateResponse), intermediateResponse.getControls());
    }
    
    @Override
    public final void sendUnsolicitedNotification(final ExtendedResult unsolicitedNotification) throws LDAPException {
        this.clientConnection.sendUnsolicitedNotification(unsolicitedNotification);
    }
    
    @Override
    public final Object getProperty(final String name) {
        return this.propertyMap.get(name);
    }
    
    @Override
    public final Object setProperty(final String name, final Object value) {
        if (value == null) {
            return this.propertyMap.remove(name);
        }
        return this.propertyMap.put(name, value);
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public abstract void toString(final StringBuilder p0);
    
    protected final void appendCommonToString(final StringBuilder buffer) {
        buffer.append("connectionID=");
        buffer.append(this.getConnectionID());
        buffer.append(", connectedAddress='");
        buffer.append(this.getConnectedAddress());
        buffer.append("', connectedPort=");
        buffer.append(this.getConnectedPort());
        buffer.append(", messageID=");
        buffer.append(this.messageID);
    }
}
