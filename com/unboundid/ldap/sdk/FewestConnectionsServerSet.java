package com.unboundid.ldap.sdk;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Iterator;
import com.unboundid.util.Debug;
import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import javax.net.SocketFactory;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ObjectPair;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class FewestConnectionsServerSet extends ServerSet
{
    static final String PROPERTY_DEFAULT_BLACKLIST_CHECK_INTERVAL_MILLIS;
    private final BindRequest bindRequest;
    private final LDAPConnectionOptions connectionOptions;
    private final Map<ObjectPair<String, Integer>, AtomicLong> connectionCountsByServer;
    private final PostConnectProcessor postConnectProcessor;
    private final ServerSetBlacklistManager blacklistManager;
    private final SocketFactory socketFactory;
    
    public FewestConnectionsServerSet(final String[] addresses, final int[] ports) {
        this(addresses, ports, null, null);
    }
    
    public FewestConnectionsServerSet(final String[] addresses, final int[] ports, final LDAPConnectionOptions connectionOptions) {
        this(addresses, ports, null, connectionOptions);
    }
    
    public FewestConnectionsServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory) {
        this(addresses, ports, socketFactory, null);
    }
    
    public FewestConnectionsServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions) {
        this(addresses, ports, socketFactory, connectionOptions, null, null);
    }
    
    public FewestConnectionsServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions, final BindRequest bindRequest, final PostConnectProcessor postConnectProcessor) {
        this(addresses, ports, socketFactory, connectionOptions, bindRequest, postConnectProcessor, getDefaultBlacklistCheckIntervalMillis());
    }
    
    public FewestConnectionsServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions, final BindRequest bindRequest, final PostConnectProcessor postConnectProcessor, final long blacklistCheckIntervalMillis) {
        Validator.ensureNotNull(addresses, ports);
        Validator.ensureTrue(addresses.length > 0, "FewestConnectionsServerSet.addresses must not be empty.");
        Validator.ensureTrue(addresses.length == ports.length, "FewestConnectionsServerSet addresses and ports arrays must be the same size.");
        final LinkedHashMap<ObjectPair<String, Integer>, AtomicLong> m = new LinkedHashMap<ObjectPair<String, Integer>, AtomicLong>(StaticUtils.computeMapCapacity(ports.length));
        for (int i = 0; i < addresses.length; ++i) {
            m.put(new ObjectPair<String, Integer>(addresses[i], ports[i]), new AtomicLong(0L));
        }
        this.connectionCountsByServer = Collections.unmodifiableMap((Map<? extends ObjectPair<String, Integer>, ? extends AtomicLong>)m);
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
        if (blacklistCheckIntervalMillis > 0L) {
            this.blacklistManager = new ServerSetBlacklistManager(this, socketFactory, connectionOptions, bindRequest, postConnectProcessor, blacklistCheckIntervalMillis);
        }
        else {
            this.blacklistManager = null;
        }
    }
    
    private static long getDefaultBlacklistCheckIntervalMillis() {
        final String propertyValue = StaticUtils.getSystemProperty(FewestConnectionsServerSet.PROPERTY_DEFAULT_BLACKLIST_CHECK_INTERVAL_MILLIS);
        if (propertyValue != null) {
            try {
                return Long.parseLong(propertyValue);
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        return 30000L;
    }
    
    public String[] getAddresses() {
        int i = 0;
        final String[] addresses = new String[this.connectionCountsByServer.size()];
        for (final ObjectPair<String, Integer> hostPort : this.connectionCountsByServer.keySet()) {
            addresses[i++] = hostPort.getFirst();
        }
        return addresses;
    }
    
    public int[] getPorts() {
        int i = 0;
        final int[] ports = new int[this.connectionCountsByServer.size()];
        for (final ObjectPair<String, Integer> hostPort : this.connectionCountsByServer.keySet()) {
            ports[i++] = hostPort.getSecond();
        }
        return ports;
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
        final TreeMap<Long, List<ObjectPair<String, Integer>>> serversByCount = new TreeMap<Long, List<ObjectPair<String, Integer>>>();
        for (final Map.Entry<ObjectPair<String, Integer>, AtomicLong> e : this.connectionCountsByServer.entrySet()) {
            final ObjectPair<String, Integer> hostPort = e.getKey();
            final long count = e.getValue().get();
            List<ObjectPair<String, Integer>> l = serversByCount.get(count);
            if (l == null) {
                l = new ArrayList<ObjectPair<String, Integer>>(this.connectionCountsByServer.size());
                serversByCount.put(count, l);
            }
            l.add(hostPort);
        }
        LDAPException lastException = null;
        List<ObjectPair<String, Integer>> blacklistedServers = null;
        for (final List<ObjectPair<String, Integer>> i : serversByCount.values()) {
            if (i.size() > 1) {
                Collections.shuffle(i);
            }
            for (final ObjectPair<String, Integer> hostPort2 : i) {
                if (this.blacklistManager == null || !this.blacklistManager.isBlacklisted(hostPort2)) {
                    try {
                        final LDAPConnection conn = new LDAPConnection(this.socketFactory, this.connectionOptions, hostPort2.getFirst(), hostPort2.getSecond());
                        ServerSet.doBindPostConnectAndHealthCheckProcessing(conn, this.bindRequest, this.postConnectProcessor, healthCheck);
                        this.connectionCountsByServer.get(hostPort2).incrementAndGet();
                        this.associateConnectionWithThisServerSet(conn);
                        return conn;
                    }
                    catch (final LDAPException le) {
                        Debug.debugException(le);
                        lastException = le;
                        if (this.blacklistManager == null) {
                            continue;
                        }
                        this.blacklistManager.addToBlacklist(hostPort2, healthCheck);
                        continue;
                    }
                    break;
                }
                if (blacklistedServers == null) {
                    blacklistedServers = new ArrayList<ObjectPair<String, Integer>>(this.connectionCountsByServer.size());
                }
                blacklistedServers.add(hostPort2);
            }
        }
        if (blacklistedServers != null) {
            for (final ObjectPair<String, Integer> hostPort3 : blacklistedServers) {
                try {
                    final LDAPConnection c = new LDAPConnection(this.socketFactory, this.connectionOptions, hostPort3.getFirst(), hostPort3.getSecond());
                    ServerSet.doBindPostConnectAndHealthCheckProcessing(c, this.bindRequest, this.postConnectProcessor, healthCheck);
                    this.associateConnectionWithThisServerSet(c);
                    this.blacklistManager.removeFromBlacklist(hostPort3);
                    return c;
                }
                catch (final LDAPException e2) {
                    Debug.debugException(e2);
                    lastException = e2;
                    continue;
                }
                break;
            }
        }
        throw lastException;
    }
    
    @Override
    protected void handleConnectionClosed(final LDAPConnection connection, final String host, final int port, final DisconnectType disconnectType, final String message, final Throwable cause) {
        final ObjectPair<String, Integer> hostPort = new ObjectPair<String, Integer>(host, port);
        final AtomicLong counter = this.connectionCountsByServer.get(hostPort);
        if (counter != null) {
            final long remainingCount = counter.decrementAndGet();
            if (remainingCount < 0L) {
                counter.compareAndSet(remainingCount, 0L);
            }
        }
    }
    
    ServerSetBlacklistManager getBlacklistManager() {
        return this.blacklistManager;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("FewestConnectionsServerSet(servers={");
        final Iterator<Map.Entry<ObjectPair<String, Integer>, AtomicLong>> cbsIterator = this.connectionCountsByServer.entrySet().iterator();
        while (cbsIterator.hasNext()) {
            final Map.Entry<ObjectPair<String, Integer>, AtomicLong> e = cbsIterator.next();
            final ObjectPair<String, Integer> hostPort = e.getKey();
            final long count = e.getValue().get();
            buffer.append('\'');
            buffer.append(hostPort.getFirst());
            buffer.append(':');
            buffer.append(hostPort.getSecond());
            buffer.append("':");
            buffer.append(count);
            if (cbsIterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append("}, includesAuthentication=");
        buffer.append(this.bindRequest != null);
        buffer.append(", includesPostConnectProcessing=");
        buffer.append(this.postConnectProcessor != null);
        buffer.append(')');
    }
    
    static {
        PROPERTY_DEFAULT_BLACKLIST_CHECK_INTERVAL_MILLIS = FewestConnectionsServerSet.class.getName() + ".defaultBlacklistCheckIntervalMillis";
    }
}
