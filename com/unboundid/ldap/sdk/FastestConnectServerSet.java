package com.unboundid.ldap.sdk;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ArrayBlockingQueue;
import com.unboundid.util.Validator;
import javax.net.SocketFactory;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class FastestConnectServerSet extends ServerSet
{
    private final BindRequest bindRequest;
    private final int[] ports;
    private final LDAPConnectionOptions connectionOptions;
    private final PostConnectProcessor postConnectProcessor;
    private final SocketFactory socketFactory;
    private final String[] addresses;
    
    public FastestConnectServerSet(final String[] addresses, final int[] ports) {
        this(addresses, ports, null, null);
    }
    
    public FastestConnectServerSet(final String[] addresses, final int[] ports, final LDAPConnectionOptions connectionOptions) {
        this(addresses, ports, null, connectionOptions);
    }
    
    public FastestConnectServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory) {
        this(addresses, ports, socketFactory, null);
    }
    
    public FastestConnectServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions) {
        this(addresses, ports, socketFactory, connectionOptions, null, null);
    }
    
    public FastestConnectServerSet(final String[] addresses, final int[] ports, final SocketFactory socketFactory, final LDAPConnectionOptions connectionOptions, final BindRequest bindRequest, final PostConnectProcessor postConnectProcessor) {
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
        if (!this.connectionOptions.allowConcurrentSocketFactoryUse()) {
            throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_FASTEST_CONNECT_SET_OPTIONS_NOT_PARALLEL.get());
        }
        final ArrayBlockingQueue<Object> resultQueue = new ArrayBlockingQueue<Object>(this.addresses.length, false);
        final AtomicBoolean connectionSelected = new AtomicBoolean(false);
        final FastestConnectThread[] connectThreads = new FastestConnectThread[this.addresses.length];
        for (int i = 0; i < connectThreads.length; ++i) {
            connectThreads[i] = new FastestConnectThread(this.addresses[i], this.ports[i], this.socketFactory, this.connectionOptions, this.bindRequest, this.postConnectProcessor, healthCheck, resultQueue, connectionSelected);
        }
        for (final FastestConnectThread t : connectThreads) {
            t.start();
        }
        try {
            final long connectTimeout = this.connectionOptions.getConnectTimeoutMillis();
            long effectiveConnectTimeout;
            if (connectTimeout > 0L && connectTimeout < 2147483647L) {
                effectiveConnectTimeout = connectTimeout;
            }
            else {
                effectiveConnectTimeout = 2147483647L;
            }
            int connectFailures = 0;
            final long stopWaitingTime = System.currentTimeMillis() + effectiveConnectTimeout;
            while (true) {
                final long waitTime = stopWaitingTime - System.currentTimeMillis();
                Object o;
                if (waitTime > 0L) {
                    o = resultQueue.poll(waitTime, TimeUnit.MILLISECONDS);
                }
                else {
                    o = resultQueue.poll();
                }
                if (o == null) {
                    throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_FASTEST_CONNECT_SET_CONNECT_TIMEOUT.get(effectiveConnectTimeout));
                }
                if (o instanceof LDAPConnection) {
                    final LDAPConnection conn = (LDAPConnection)o;
                    this.associateConnectionWithThisServerSet(conn);
                    return conn;
                }
                if (++connectFailures >= this.addresses.length) {
                    throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_FASTEST_CONNECT_SET_ALL_FAILED.get());
                }
            }
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            throw le;
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_FASTEST_CONNECT_SET_CONNECT_EXCEPTION.get(StaticUtils.getExceptionMessage(e)), e);
        }
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("FastestConnectServerSet(servers={");
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
}
