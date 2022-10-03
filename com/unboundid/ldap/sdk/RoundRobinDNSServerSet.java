package com.unboundid.ldap.sdk;

import java.net.UnknownHostException;
import java.util.StringTokenizer;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadLocalRandom;
import java.util.Collection;
import java.util.Arrays;
import java.util.List;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import javax.naming.directory.InitialDirContext;
import com.unboundid.util.Debug;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import com.unboundid.util.Validator;
import java.util.Properties;
import javax.net.SocketFactory;
import java.util.Hashtable;
import java.net.InetAddress;
import com.unboundid.util.ObjectPair;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RoundRobinDNSServerSet extends ServerSet
{
    static final String PROPERTY_DEFAULT_ADDRESSES;
    private final AddressSelectionMode selectionMode;
    private final AtomicLong roundRobinCounter;
    private final AtomicReference<ObjectPair<InetAddress[], Long>> resolvedAddressesWithTimeout;
    private final BindRequest bindRequest;
    private final Hashtable<String, String> jndiProperties;
    private final int port;
    private final LDAPConnectionOptions connectionOptions;
    private final long cacheTimeoutMillis;
    private final PostConnectProcessor postConnectProcessor;
    private final SocketFactory socketFactory;
    private final String hostname;
    private final String providerURL;
    private final String[] dnsRecordTypes;
    
    public RoundRobinDNSServerSet(final String hostname, final int port, final AddressSelectionMode selectionMode, final long cacheTimeoutMillis, final String providerURL, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions) {
        this(hostname, port, selectionMode, cacheTimeoutMillis, providerURL, null, null, socketFactory, connectionOptions);
    }
    
    public RoundRobinDNSServerSet(final String hostname, final int port, final AddressSelectionMode selectionMode, final long cacheTimeoutMillis, final String providerURL, final Properties jndiProperties, final String[] dnsRecordTypes, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions) {
        this(hostname, port, selectionMode, cacheTimeoutMillis, providerURL, jndiProperties, dnsRecordTypes, socketFactory, connectionOptions, null, null);
    }
    
    public RoundRobinDNSServerSet(final String hostname, final int port, final AddressSelectionMode selectionMode, final long cacheTimeoutMillis, final String providerURL, final Properties jndiProperties, final String[] dnsRecordTypes, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions, final BindRequest bindRequest, final PostConnectProcessor postConnectProcessor) {
        Validator.ensureNotNull(hostname);
        Validator.ensureTrue(port >= 1 && port <= 65535);
        Validator.ensureNotNull(selectionMode);
        this.hostname = hostname;
        this.port = port;
        this.selectionMode = selectionMode;
        this.providerURL = providerURL;
        this.bindRequest = bindRequest;
        this.postConnectProcessor = postConnectProcessor;
        if (jndiProperties == null) {
            if (providerURL == null) {
                this.jndiProperties = null;
            }
            else {
                (this.jndiProperties = new Hashtable<String, String>(2)).put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
                this.jndiProperties.put("java.naming.provider.url", providerURL);
            }
        }
        else {
            this.jndiProperties = new Hashtable<String, String>(jndiProperties.size() + 2);
            for (final Map.Entry<Object, Object> e : jndiProperties.entrySet()) {
                this.jndiProperties.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
            }
            if (!this.jndiProperties.containsKey("java.naming.factory.initial")) {
                this.jndiProperties.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            }
            if (!this.jndiProperties.containsKey("java.naming.provider.url") && providerURL != null) {
                this.jndiProperties.put("java.naming.provider.url", providerURL);
            }
        }
        if (dnsRecordTypes == null) {
            this.dnsRecordTypes = new String[] { "A" };
        }
        else {
            this.dnsRecordTypes = dnsRecordTypes;
        }
        if (cacheTimeoutMillis > 0L) {
            this.cacheTimeoutMillis = cacheTimeoutMillis;
        }
        else {
            this.cacheTimeoutMillis = 0L;
        }
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
        this.roundRobinCounter = new AtomicLong(0L);
        this.resolvedAddressesWithTimeout = new AtomicReference<ObjectPair<InetAddress[], Long>>();
    }
    
    public String getHostname() {
        return this.hostname;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public AddressSelectionMode getAddressSelectionMode() {
        return this.selectionMode;
    }
    
    public long getCacheTimeoutMillis() {
        return this.cacheTimeoutMillis;
    }
    
    public String getProviderURL() {
        return this.providerURL;
    }
    
    public Map<String, String> getJNDIProperties() {
        if (this.jndiProperties == null) {
            return null;
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)this.jndiProperties);
    }
    
    public String[] getDNSRecordTypes() {
        return this.dnsRecordTypes;
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
    public synchronized LDAPConnection getConnection(final LDAPConnectionPoolHealthCheck healthCheck) throws LDAPException {
        LDAPException firstException = null;
        final LDAPConnection conn = new LDAPConnection(this.socketFactory, this.connectionOptions);
        for (final InetAddress a : this.orderAddresses(this.resolveHostname())) {
            boolean close = true;
            try {
                conn.connect(this.hostname, a, this.port, this.connectionOptions.getConnectTimeoutMillis());
                ServerSet.doBindPostConnectAndHealthCheckProcessing(conn, this.bindRequest, this.postConnectProcessor, healthCheck);
                close = false;
                this.associateConnectionWithThisServerSet(conn);
                return conn;
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                if (firstException != null) {
                    continue;
                }
                firstException = le;
            }
            finally {
                if (close) {
                    conn.close();
                }
            }
        }
        throw firstException;
    }
    
    InetAddress[] resolveHostname() throws LDAPException {
        final ObjectPair<InetAddress[], Long> pair = this.resolvedAddressesWithTimeout.get();
        if (pair != null && pair.getSecond() >= System.currentTimeMillis()) {
            return pair.getFirst();
        }
        InetAddress[] addresses = null;
        try {
            if (this.jndiProperties == null) {
                addresses = this.connectionOptions.getNameResolver().getAllByName(this.hostname);
            }
            else {
                final InitialDirContext context = new InitialDirContext(this.jndiProperties);
                Attributes attributes;
                try {
                    attributes = context.getAttributes(this.hostname, this.dnsRecordTypes);
                }
                finally {
                    context.close();
                }
                if (attributes != null) {
                    final ArrayList<InetAddress> addressList = new ArrayList<InetAddress>(10);
                    for (final String recordType : this.dnsRecordTypes) {
                        final Attribute a = attributes.get(recordType);
                        if (a != null) {
                            final NamingEnumeration<?> values = a.getAll();
                            while (values.hasMore()) {
                                final Object value = values.next();
                                addressList.add(this.getInetAddressForIP(String.valueOf(value)));
                            }
                        }
                    }
                    if (!addressList.isEmpty()) {
                        addresses = new InetAddress[addressList.size()];
                        addressList.toArray(addresses);
                    }
                }
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
            addresses = this.getDefaultAddresses();
        }
        if (addresses != null && addresses.length > 0) {
            long timeoutTime;
            if (this.cacheTimeoutMillis > 0L) {
                timeoutTime = System.currentTimeMillis() + this.cacheTimeoutMillis;
            }
            else {
                timeoutTime = System.currentTimeMillis() - 1L;
            }
            this.resolvedAddressesWithTimeout.set(new ObjectPair<InetAddress[], Long>(addresses, timeoutTime));
            return addresses;
        }
        if (pair != null) {
            return pair.getFirst();
        }
        throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_ROUND_ROBIN_DNS_SERVER_SET_CANNOT_RESOLVE.get(this.hostname));
    }
    
    List<InetAddress> orderAddresses(final InetAddress[] addresses) {
        final ArrayList<InetAddress> l = new ArrayList<InetAddress>(addresses.length);
        switch (this.selectionMode) {
            case RANDOM: {
                l.addAll(Arrays.asList(addresses));
                Collections.shuffle(l, ThreadLocalRandom.get());
                break;
            }
            case ROUND_ROBIN: {
                int i;
                int index;
                for (index = (i = (int)(this.roundRobinCounter.getAndIncrement() % addresses.length)); i < addresses.length; ++i) {
                    l.add(addresses[i]);
                }
                for (i = 0; i < index; ++i) {
                    l.add(addresses[i]);
                }
                break;
            }
            default: {
                l.addAll(Arrays.asList(addresses));
                break;
            }
        }
        return l;
    }
    
    InetAddress[] getDefaultAddresses() {
        final String defaultAddrsStr = StaticUtils.getSystemProperty(RoundRobinDNSServerSet.PROPERTY_DEFAULT_ADDRESSES);
        if (defaultAddrsStr == null) {
            return null;
        }
        final StringTokenizer tokenizer = new StringTokenizer(defaultAddrsStr, " ,");
        final InetAddress[] addresses = new InetAddress[tokenizer.countTokens()];
        for (int i = 0; i < addresses.length; ++i) {
            try {
                addresses[i] = this.getInetAddressForIP(tokenizer.nextToken());
            }
            catch (final Exception e) {
                Debug.debugException(e);
                return null;
            }
        }
        return addresses;
    }
    
    private InetAddress getInetAddressForIP(final String ipAddress) throws UnknownHostException {
        final InetAddress byName = this.connectionOptions.getNameResolver().getByName(String.valueOf(ipAddress));
        return InetAddress.getByAddress(this.hostname, byName.getAddress());
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("RoundRobinDNSServerSet(hostname='");
        buffer.append(this.hostname);
        buffer.append("', port=");
        buffer.append(this.port);
        buffer.append(", addressSelectionMode=");
        buffer.append(this.selectionMode.name());
        buffer.append(", cacheTimeoutMillis=");
        buffer.append(this.cacheTimeoutMillis);
        if (this.providerURL != null) {
            buffer.append(", providerURL='");
            buffer.append(this.providerURL);
            buffer.append('\'');
        }
        buffer.append(", includesAuthentication=");
        buffer.append(this.bindRequest != null);
        buffer.append(", includesPostConnectProcessing=");
        buffer.append(this.postConnectProcessor != null);
        buffer.append(')');
    }
    
    static {
        PROPERTY_DEFAULT_ADDRESSES = RoundRobinDNSServerSet.class.getName() + ".defaultAddresses";
    }
    
    public enum AddressSelectionMode
    {
        FAILOVER, 
        RANDOM, 
        ROUND_ROBIN;
        
        public static AddressSelectionMode forName(final String name) {
            final String lowerCase = StaticUtils.toLowerCase(name);
            switch (lowerCase) {
                case "failover": {
                    return AddressSelectionMode.FAILOVER;
                }
                case "random": {
                    return AddressSelectionMode.RANDOM;
                }
                case "roundrobin":
                case "round-robin":
                case "round_robin": {
                    return AddressSelectionMode.ROUND_ROBIN;
                }
                default: {
                    return null;
                }
            }
        }
    }
}
