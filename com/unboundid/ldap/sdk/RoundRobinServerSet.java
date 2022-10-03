package com.unboundid.ldap.sdk;

import java.util.Iterator;
import java.util.List;
import com.unboundid.util.ObjectPair;
import java.util.ArrayList;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import javax.net.SocketFactory;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class RoundRobinServerSet extends ServerSet
{
    static final String PROPERTY_DEFAULT_BLACKLIST_CHECK_INTERVAL_MILLIS;
    private final AtomicLong nextSlot;
    private final BindRequest bindRequest;
    private final int[] ports;
    private final LDAPConnectionOptions connectionOptions;
    private final PostConnectProcessor postConnectProcessor;
    private final ServerSetBlacklistManager blacklistManager;
    private final SocketFactory socketFactory;
    private final String[] addresses;
    
    public RoundRobinServerSet(final String[] addresses, final int[] ports) {
        this(addresses, ports, null, null);
    }
    
    public RoundRobinServerSet(final String[] addresses, final int[] ports, final LDAPConnectionOptions connectionOptions) {
        this(addresses, ports, null, connectionOptions);
    }
    
    public RoundRobinServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory) {
        this(addresses, ports, socketFactory, null);
    }
    
    public RoundRobinServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions) {
        this(addresses, ports, socketFactory, connectionOptions, null, null);
    }
    
    public RoundRobinServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions, final BindRequest bindRequest, final PostConnectProcessor postConnectProcessor) {
        this(addresses, ports, socketFactory, connectionOptions, bindRequest, postConnectProcessor, getDefaultBlacklistCheckIntervalMillis());
    }
    
    public RoundRobinServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions, final BindRequest bindRequest, final PostConnectProcessor postConnectProcessor, final long blacklistCheckIntervalMillis) {
        Validator.ensureNotNull(addresses, ports);
        Validator.ensureTrue(addresses.length > 0, "RoundRobinServerSet.addresses must not be empty.");
        Validator.ensureTrue(addresses.length == ports.length, "RoundRobinServerSet addresses and ports arrays must be the same size.");
        this.addresses = addresses;
        this.ports = ports;
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
        this.nextSlot = new AtomicLong(0L);
        if (blacklistCheckIntervalMillis > 0L) {
            this.blacklistManager = new ServerSetBlacklistManager(this, socketFactory, connectionOptions, bindRequest, postConnectProcessor, blacklistCheckIntervalMillis);
        }
        else {
            this.blacklistManager = null;
        }
    }
    
    private static long getDefaultBlacklistCheckIntervalMillis() {
        final String propertyValue = StaticUtils.getSystemProperty(RoundRobinServerSet.PROPERTY_DEFAULT_BLACKLIST_CHECK_INTERVAL_MILLIS);
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
        return this.addresses;
    }
    
    public int[] getPorts() {
        return this.ports;
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
        final int initialSlotNumber = (int)(this.nextSlot.getAndIncrement() % this.addresses.length);
        LDAPException lastException = null;
        List<ObjectPair<String, Integer>> blacklistedServers = null;
        for (int i = 0; i < this.addresses.length; ++i) {
            final int slotNumber = (initialSlotNumber + i) % this.addresses.length;
            final String address = this.addresses[slotNumber];
            final int port = this.ports[slotNumber];
            if (this.blacklistManager != null && this.blacklistManager.isBlacklisted(address, port)) {
                if (blacklistedServers == null) {
                    blacklistedServers = new ArrayList<ObjectPair<String, Integer>>(this.addresses.length);
                }
                blacklistedServers.add(new ObjectPair<String, Integer>(address, port));
            }
            else {
                try {
                    final LDAPConnection c = new LDAPConnection(this.socketFactory, this.connectionOptions, this.addresses[slotNumber], this.ports[slotNumber]);
                    ServerSet.doBindPostConnectAndHealthCheckProcessing(c, this.bindRequest, this.postConnectProcessor, healthCheck);
                    this.associateConnectionWithThisServerSet(c);
                    return c;
                }
                catch (final LDAPException e) {
                    Debug.debugException(e);
                    lastException = e;
                    if (this.blacklistManager != null) {
                        this.blacklistManager.addToBlacklist(address, port, healthCheck);
                    }
                }
            }
        }
        if (blacklistedServers != null) {
            for (final ObjectPair<String, Integer> hostPort : blacklistedServers) {
                try {
                    final LDAPConnection c2 = new LDAPConnection(this.socketFactory, this.connectionOptions, hostPort.getFirst(), hostPort.getSecond());
                    ServerSet.doBindPostConnectAndHealthCheckProcessing(c2, this.bindRequest, this.postConnectProcessor, healthCheck);
                    this.associateConnectionWithThisServerSet(c2);
                    this.blacklistManager.removeFromBlacklist(hostPort);
                    return c2;
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
    
    ServerSetBlacklistManager getBlacklistManager() {
        return this.blacklistManager;
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("RoundRobinServerSet(servers={");
        for (int i = 0; i < this.addresses.length; ++i) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(this.addresses[i]);
            buffer.append(':');
            buffer.append(this.ports[i]);
        }
        buffer.append("}, includesAuthentication=");
        buffer.append(this.bindRequest != null);
        buffer.append(", includesPostConnectProcessing=");
        buffer.append(this.postConnectProcessor != null);
        buffer.append(')');
    }
    
    static {
        PROPERTY_DEFAULT_BLACKLIST_CHECK_INTERVAL_MILLIS = RoundRobinServerSet.class.getName() + ".defaultBlacklistCheckIntervalMillis";
    }
}
