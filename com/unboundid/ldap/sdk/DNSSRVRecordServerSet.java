package com.unboundid.ldap.sdk;

import com.unboundid.util.Debug;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.net.SocketFactory;
import java.util.Hashtable;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DNSSRVRecordServerSet extends ServerSet
{
    private static final String DEFAULT_RECORD_NAME = "_ldap._tcp";
    private static final long DEFAULT_TTL_MILLIS = 3600000L;
    private static final String DEFAULT_DNS_PROVIDER_URL = "dns:";
    private final BindRequest bindRequest;
    private final Hashtable<String, String> jndiProperties;
    private final LDAPConnectionOptions connectionOptions;
    private final long ttlMillis;
    private final PostConnectProcessor postConnectProcessor;
    private final SocketFactory socketFactory;
    private volatile SRVRecordSet recordSet;
    private final String recordName;
    private final String providerURL;
    
    public DNSSRVRecordServerSet(final String recordName) {
        this(recordName, null, 3600000L, null, null);
    }
    
    public DNSSRVRecordServerSet(final String recordName, final String providerURL, final long ttlMillis, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions) {
        this(recordName, providerURL, null, ttlMillis, socketFactory, connectionOptions);
    }
    
    public DNSSRVRecordServerSet(final String recordName, final String providerURL, final Properties jndiProperties, final long ttlMillis, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions) {
        this(recordName, providerURL, jndiProperties, ttlMillis, socketFactory, connectionOptions, null, null);
    }
    
    public DNSSRVRecordServerSet(final String recordName, final String providerURL, final Properties jndiProperties, final long ttlMillis, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions, final BindRequest bindRequest, final PostConnectProcessor postConnectProcessor) {
        this.socketFactory = socketFactory;
        this.connectionOptions = connectionOptions;
        this.bindRequest = bindRequest;
        this.postConnectProcessor = postConnectProcessor;
        this.recordSet = null;
        if (recordName == null) {
            this.recordName = "_ldap._tcp";
        }
        else {
            this.recordName = recordName;
        }
        if (providerURL == null) {
            this.providerURL = "dns:";
        }
        else {
            this.providerURL = providerURL;
        }
        this.jndiProperties = new Hashtable<String, String>(10);
        if (jndiProperties != null) {
            for (final Map.Entry<Object, Object> e : jndiProperties.entrySet()) {
                this.jndiProperties.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
            }
        }
        if (!this.jndiProperties.containsKey("java.naming.factory.initial")) {
            this.jndiProperties.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        }
        if (!this.jndiProperties.containsKey("java.naming.provider.url")) {
            this.jndiProperties.put("java.naming.provider.url", this.providerURL);
        }
        if (ttlMillis <= 0L) {
            this.ttlMillis = 3600000L;
        }
        else {
            this.ttlMillis = ttlMillis;
        }
    }
    
    public String getRecordName() {
        return this.recordName;
    }
    
    public String getProviderURL() {
        return this.providerURL;
    }
    
    public Map<String, String> getJNDIProperties() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)this.jndiProperties);
    }
    
    public long getTTLMillis() {
        return this.ttlMillis;
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
        Label_0053: {
            if (this.recordSet != null) {
                if (!this.recordSet.isExpired()) {
                    break Label_0053;
                }
            }
            try {
                this.recordSet = SRVRecordSet.getRecordSet(this.recordName, this.jndiProperties, this.ttlMillis);
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                if (this.recordSet == null) {
                    throw le;
                }
            }
        }
        LDAPException firstException = null;
        for (final SRVRecord r : this.recordSet.getOrderedRecords()) {
            try {
                final LDAPConnection connection = new LDAPConnection(this.socketFactory, this.connectionOptions, r.getAddress(), r.getPort());
                ServerSet.doBindPostConnectAndHealthCheckProcessing(connection, this.bindRequest, this.postConnectProcessor, healthCheck);
                this.associateConnectionWithThisServerSet(connection);
                return connection;
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                if (firstException != null) {
                    continue;
                }
                firstException = le2;
                continue;
            }
            break;
        }
        throw firstException;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("DNSSRVRecordServerSet(recordName='");
        buffer.append(this.recordName);
        buffer.append("', providerURL='");
        buffer.append(this.providerURL);
        buffer.append("', ttlMillis=");
        buffer.append(this.ttlMillis);
        if (this.socketFactory != null) {
            buffer.append(", socketFactoryClass='");
            buffer.append(this.socketFactory.getClass().getName());
            buffer.append('\'');
        }
        if (this.connectionOptions != null) {
            buffer.append(", connectionOptions");
            this.connectionOptions.toString(buffer);
        }
        buffer.append(", includesAuthentication=");
        buffer.append(this.bindRequest != null);
        buffer.append(", includesPostConnectProcessing=");
        buffer.append(this.postConnectProcessor != null);
        buffer.append(')');
    }
}
