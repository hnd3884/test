package com.unboundid.ldap.sdk;

import com.unboundid.util.Validator;
import javax.net.SocketFactory;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SingleServerSet extends ServerSet
{
    private final BindRequest bindRequest;
    private final int port;
    private final LDAPConnectionOptions connectionOptions;
    private final PostConnectProcessor postConnectProcessor;
    private final SocketFactory socketFactory;
    private final String address;
    
    public SingleServerSet(final String address, final int port) {
        this(address, port, null, null);
    }
    
    public SingleServerSet(final String address, final int port, final LDAPConnectionOptions connectionOptions) {
        this(address, port, null, connectionOptions);
    }
    
    public SingleServerSet(final String address, final int port, final SocketFactory socketFactory) {
        this(address, port, socketFactory, null);
    }
    
    public SingleServerSet(final String address, final int port, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions) {
        this(address, port, socketFactory, connectionOptions, null, null);
    }
    
    public SingleServerSet(final String address, final int port, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions, final BindRequest bindRequest, final PostConnectProcessor postConnectProcessor) {
        Validator.ensureNotNull(address);
        Validator.ensureTrue(port > 0 && port < 65536, "SingleServerSet.port must be between 1 and 65535.");
        this.address = address;
        this.port = port;
        this.bindRequest = bindRequest;
        this.postConnectProcessor = postConnectProcessor;
        if (socketFactory == null) {
            this.socketFactory = SocketFactory.getDefault();
        }
        else {
            this.socketFactory = socketFactory;
        }
        if (connectionOptions == null) {
            this.connectionOptions = new LDAPConnectionOptions();
        }
        else {
            this.connectionOptions = connectionOptions;
        }
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public SocketFactory getSocketFactory() {
        return this.socketFactory;
    }
    
    public LDAPConnectionOptions getConnectionOptions() {
        return this.connectionOptions;
    }
    
    @Override
    public boolean includesAuthentication() {
        return this.bindRequest != null;
    }
    
    @Override
    public boolean includesPostConnectProcessing() {
        return this.postConnectProcessor != null;
    }
    
    @Override
    public LDAPConnection getConnection() throws LDAPException {
        return this.getConnection(null);
    }
    
    @Override
    public LDAPConnection getConnection(final LDAPConnectionPoolHealthCheck healthCheck) throws LDAPException {
        final LDAPConnection connection = new LDAPConnection(this.socketFactory, this.connectionOptions, this.address, this.port);
        ServerSet.doBindPostConnectAndHealthCheckProcessing(connection, this.bindRequest, this.postConnectProcessor, healthCheck);
        this.associateConnectionWithThisServerSet(connection);
        return connection;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("SingleServerSet(server=");
        buffer.append(this.address);
        buffer.append(':');
        buffer.append(this.port);
        buffer.append(", includesAuthentication=");
        buffer.append(this.bindRequest != null);
        buffer.append(", includesPostConnectProcessing=");
        buffer.append(this.postConnectProcessor != null);
        buffer.append(')');
    }
}
