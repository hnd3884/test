package org.apache.http.impl.conn;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.apache.http.config.ConnectionConfig;
import java.util.Set;
import org.apache.http.pool.PoolEntryCallback;
import java.io.IOException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.pool.PoolEntry;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.CancellationException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import java.util.concurrent.ExecutionException;
import org.apache.http.HttpClientConnection;
import java.util.concurrent.Future;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.util.Asserts;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.config.SocketConfig;
import org.apache.http.HttpHost;
import org.apache.http.pool.PoolStats;
import org.apache.http.util.Args;
import org.apache.http.pool.ConnFactory;
import org.apache.commons.logging.LogFactory;
import org.apache.http.config.Lookup;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.HttpConnectionFactory;
import java.util.concurrent.TimeUnit;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.config.Registry;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.http.conn.HttpClientConnectionOperator;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import java.io.Closeable;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.ConnPoolControl;
import org.apache.http.conn.HttpClientConnectionManager;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class PoolingHttpClientConnectionManager implements HttpClientConnectionManager, ConnPoolControl<HttpRoute>, Closeable
{
    private final Log log;
    private final ConfigData configData;
    private final CPool pool;
    private final HttpClientConnectionOperator connectionOperator;
    private final AtomicBoolean isShutDown;
    
    private static Registry<ConnectionSocketFactory> getDefaultRegistry() {
        return (Registry<ConnectionSocketFactory>)RegistryBuilder.create().register("http", (Object)PlainConnectionSocketFactory.getSocketFactory()).register("https", (Object)SSLConnectionSocketFactory.getSocketFactory()).build();
    }
    
    public PoolingHttpClientConnectionManager() {
        this(getDefaultRegistry());
    }
    
    public PoolingHttpClientConnectionManager(final long timeToLive, final TimeUnit timeUnit) {
        this(getDefaultRegistry(), null, null, null, timeToLive, timeUnit);
    }
    
    public PoolingHttpClientConnectionManager(final Registry<ConnectionSocketFactory> socketFactoryRegistry) {
        this(socketFactoryRegistry, null, null);
    }
    
    public PoolingHttpClientConnectionManager(final Registry<ConnectionSocketFactory> socketFactoryRegistry, final DnsResolver dnsResolver) {
        this(socketFactoryRegistry, null, dnsResolver);
    }
    
    public PoolingHttpClientConnectionManager(final Registry<ConnectionSocketFactory> socketFactoryRegistry, final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory) {
        this(socketFactoryRegistry, connFactory, null);
    }
    
    public PoolingHttpClientConnectionManager(final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory) {
        this(getDefaultRegistry(), connFactory, null);
    }
    
    public PoolingHttpClientConnectionManager(final Registry<ConnectionSocketFactory> socketFactoryRegistry, final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory, final DnsResolver dnsResolver) {
        this(socketFactoryRegistry, connFactory, null, dnsResolver, -1L, TimeUnit.MILLISECONDS);
    }
    
    public PoolingHttpClientConnectionManager(final Registry<ConnectionSocketFactory> socketFactoryRegistry, final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory, final SchemePortResolver schemePortResolver, final DnsResolver dnsResolver, final long timeToLive, final TimeUnit timeUnit) {
        this(new DefaultHttpClientConnectionOperator((Lookup<ConnectionSocketFactory>)socketFactoryRegistry, schemePortResolver, dnsResolver), connFactory, timeToLive, timeUnit);
    }
    
    public PoolingHttpClientConnectionManager(final HttpClientConnectionOperator httpClientConnectionOperator, final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory, final long timeToLive, final TimeUnit timeUnit) {
        this.log = LogFactory.getLog((Class)this.getClass());
        this.configData = new ConfigData();
        (this.pool = new CPool((ConnFactory<HttpRoute, ManagedHttpClientConnection>)new InternalConnectionFactory(this.configData, connFactory), 2, 20, timeToLive, timeUnit)).setValidateAfterInactivity(2000);
        this.connectionOperator = (HttpClientConnectionOperator)Args.notNull((Object)httpClientConnectionOperator, "HttpClientConnectionOperator");
        this.isShutDown = new AtomicBoolean(false);
    }
    
    PoolingHttpClientConnectionManager(final CPool pool, final Lookup<ConnectionSocketFactory> socketFactoryRegistry, final SchemePortResolver schemePortResolver, final DnsResolver dnsResolver) {
        this.log = LogFactory.getLog((Class)this.getClass());
        this.configData = new ConfigData();
        this.pool = pool;
        this.connectionOperator = new DefaultHttpClientConnectionOperator(socketFactoryRegistry, schemePortResolver, dnsResolver);
        this.isShutDown = new AtomicBoolean(false);
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            this.shutdown();
        }
        finally {
            super.finalize();
        }
    }
    
    public void close() {
        this.shutdown();
    }
    
    private String format(final HttpRoute route, final Object state) {
        final StringBuilder buf = new StringBuilder();
        buf.append("[route: ").append(route).append("]");
        if (state != null) {
            buf.append("[state: ").append(state).append("]");
        }
        return buf.toString();
    }
    
    private String formatStats(final HttpRoute route) {
        final StringBuilder buf = new StringBuilder();
        final PoolStats totals = this.pool.getTotalStats();
        final PoolStats stats = this.pool.getStats((Object)route);
        buf.append("[total available: ").append(totals.getAvailable()).append("; ");
        buf.append("route allocated: ").append(stats.getLeased() + stats.getAvailable());
        buf.append(" of ").append(stats.getMax()).append("; ");
        buf.append("total allocated: ").append(totals.getLeased() + totals.getAvailable());
        buf.append(" of ").append(totals.getMax()).append("]");
        return buf.toString();
    }
    
    private String format(final CPoolEntry entry) {
        final StringBuilder buf = new StringBuilder();
        buf.append("[id: ").append(entry.getId()).append("]");
        buf.append("[route: ").append(entry.getRoute()).append("]");
        final Object state = entry.getState();
        if (state != null) {
            buf.append("[state: ").append(state).append("]");
        }
        return buf.toString();
    }
    
    private SocketConfig resolveSocketConfig(final HttpHost host) {
        SocketConfig socketConfig = this.configData.getSocketConfig(host);
        if (socketConfig == null) {
            socketConfig = this.configData.getDefaultSocketConfig();
        }
        if (socketConfig == null) {
            socketConfig = SocketConfig.DEFAULT;
        }
        return socketConfig;
    }
    
    @Override
    public ConnectionRequest requestConnection(final HttpRoute route, final Object state) {
        Args.notNull((Object)route, "HTTP route");
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Connection request: " + this.format(route, state) + this.formatStats(route)));
        }
        Asserts.check(!this.isShutDown.get(), "Connection pool shut down");
        final Future<CPoolEntry> future = this.pool.lease((Object)route, state, (FutureCallback)null);
        return new ConnectionRequest() {
            public boolean cancel() {
                return future.cancel(true);
            }
            
            @Override
            public HttpClientConnection get(final long timeout, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException {
                final HttpClientConnection conn = PoolingHttpClientConnectionManager.this.leaseConnection(future, timeout, timeUnit);
                if (conn.isOpen()) {
                    HttpHost host;
                    if (route.getProxyHost() != null) {
                        host = route.getProxyHost();
                    }
                    else {
                        host = route.getTargetHost();
                    }
                    final SocketConfig socketConfig = PoolingHttpClientConnectionManager.this.resolveSocketConfig(host);
                    conn.setSocketTimeout(socketConfig.getSoTimeout());
                }
                return conn;
            }
        };
    }
    
    protected HttpClientConnection leaseConnection(final Future<CPoolEntry> future, final long timeout, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException {
        try {
            final CPoolEntry entry = future.get(timeout, timeUnit);
            if (entry == null || future.isCancelled()) {
                throw new ExecutionException(new CancellationException("Operation cancelled"));
            }
            Asserts.check(entry.getConnection() != null, "Pool entry with no connection");
            if (this.log.isDebugEnabled()) {
                this.log.debug((Object)("Connection leased: " + this.format(entry) + this.formatStats((HttpRoute)entry.getRoute())));
            }
            return CPoolProxy.newProxy(entry);
        }
        catch (final TimeoutException ex) {
            throw new ConnectionPoolTimeoutException("Timeout waiting for connection from pool");
        }
    }
    
    @Override
    public void releaseConnection(final HttpClientConnection managedConn, final Object state, final long keepalive, final TimeUnit timeUnit) {
        Args.notNull((Object)managedConn, "Managed connection");
        synchronized (managedConn) {
            final CPoolEntry entry = CPoolProxy.detach(managedConn);
            if (entry == null) {
                return;
            }
            final ManagedHttpClientConnection conn = (ManagedHttpClientConnection)entry.getConnection();
            try {
                if (conn.isOpen()) {
                    final TimeUnit effectiveUnit = (timeUnit != null) ? timeUnit : TimeUnit.MILLISECONDS;
                    entry.setState(state);
                    entry.updateExpiry(keepalive, effectiveUnit);
                    if (this.log.isDebugEnabled()) {
                        String s;
                        if (keepalive > 0L) {
                            s = "for " + effectiveUnit.toMillis(keepalive) / 1000.0 + " seconds";
                        }
                        else {
                            s = "indefinitely";
                        }
                        this.log.debug((Object)("Connection " + this.format(entry) + " can be kept alive " + s));
                    }
                    conn.setSocketTimeout(0);
                }
            }
            finally {
                this.pool.release((PoolEntry)entry, conn.isOpen() && entry.isRouteComplete());
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Connection released: " + this.format(entry) + this.formatStats((HttpRoute)entry.getRoute())));
                }
            }
        }
    }
    
    @Override
    public void connect(final HttpClientConnection managedConn, final HttpRoute route, final int connectTimeout, final HttpContext context) throws IOException {
        Args.notNull((Object)managedConn, "Managed Connection");
        Args.notNull((Object)route, "HTTP route");
        final ManagedHttpClientConnection conn;
        synchronized (managedConn) {
            final CPoolEntry entry = CPoolProxy.getPoolEntry(managedConn);
            conn = (ManagedHttpClientConnection)entry.getConnection();
        }
        HttpHost host;
        if (route.getProxyHost() != null) {
            host = route.getProxyHost();
        }
        else {
            host = route.getTargetHost();
        }
        this.connectionOperator.connect(conn, host, route.getLocalSocketAddress(), connectTimeout, this.resolveSocketConfig(host), context);
    }
    
    @Override
    public void upgrade(final HttpClientConnection managedConn, final HttpRoute route, final HttpContext context) throws IOException {
        Args.notNull((Object)managedConn, "Managed Connection");
        Args.notNull((Object)route, "HTTP route");
        final ManagedHttpClientConnection conn;
        synchronized (managedConn) {
            final CPoolEntry entry = CPoolProxy.getPoolEntry(managedConn);
            conn = (ManagedHttpClientConnection)entry.getConnection();
        }
        this.connectionOperator.upgrade(conn, route.getTargetHost(), context);
    }
    
    @Override
    public void routeComplete(final HttpClientConnection managedConn, final HttpRoute route, final HttpContext context) throws IOException {
        Args.notNull((Object)managedConn, "Managed Connection");
        Args.notNull((Object)route, "HTTP route");
        synchronized (managedConn) {
            final CPoolEntry entry = CPoolProxy.getPoolEntry(managedConn);
            entry.markRouteComplete();
        }
    }
    
    @Override
    public void shutdown() {
        if (this.isShutDown.compareAndSet(false, true)) {
            this.log.debug((Object)"Connection manager is shutting down");
            try {
                this.pool.enumLeased((PoolEntryCallback<HttpRoute, ManagedHttpClientConnection>)new PoolEntryCallback<HttpRoute, ManagedHttpClientConnection>() {
                    public void process(final PoolEntry<HttpRoute, ManagedHttpClientConnection> entry) {
                        final ManagedHttpClientConnection connection = (ManagedHttpClientConnection)entry.getConnection();
                        if (connection != null) {
                            try {
                                connection.shutdown();
                            }
                            catch (final IOException iox) {
                                if (PoolingHttpClientConnectionManager.this.log.isDebugEnabled()) {
                                    PoolingHttpClientConnectionManager.this.log.debug((Object)"I/O exception shutting down connection", (Throwable)iox);
                                }
                            }
                        }
                    }
                });
                this.pool.shutdown();
            }
            catch (final IOException ex) {
                this.log.debug((Object)"I/O exception shutting down connection manager", (Throwable)ex);
            }
            this.log.debug((Object)"Connection manager shut down");
        }
    }
    
    @Override
    public void closeIdleConnections(final long idleTimeout, final TimeUnit timeUnit) {
        if (this.log.isDebugEnabled()) {
            this.log.debug((Object)("Closing connections idle longer than " + idleTimeout + " " + timeUnit));
        }
        this.pool.closeIdle(idleTimeout, timeUnit);
    }
    
    @Override
    public void closeExpiredConnections() {
        this.log.debug((Object)"Closing expired connections");
        this.pool.closeExpired();
    }
    
    protected void enumAvailable(final PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> callback) {
        this.pool.enumAvailable(callback);
    }
    
    protected void enumLeased(final PoolEntryCallback<HttpRoute, ManagedHttpClientConnection> callback) {
        this.pool.enumLeased(callback);
    }
    
    public int getMaxTotal() {
        return this.pool.getMaxTotal();
    }
    
    public void setMaxTotal(final int max) {
        this.pool.setMaxTotal(max);
    }
    
    public int getDefaultMaxPerRoute() {
        return this.pool.getDefaultMaxPerRoute();
    }
    
    public void setDefaultMaxPerRoute(final int max) {
        this.pool.setDefaultMaxPerRoute(max);
    }
    
    public int getMaxPerRoute(final HttpRoute route) {
        return this.pool.getMaxPerRoute((Object)route);
    }
    
    public void setMaxPerRoute(final HttpRoute route, final int max) {
        this.pool.setMaxPerRoute((Object)route, max);
    }
    
    public PoolStats getTotalStats() {
        return this.pool.getTotalStats();
    }
    
    public PoolStats getStats(final HttpRoute route) {
        return this.pool.getStats((Object)route);
    }
    
    public Set<HttpRoute> getRoutes() {
        return this.pool.getRoutes();
    }
    
    public SocketConfig getDefaultSocketConfig() {
        return this.configData.getDefaultSocketConfig();
    }
    
    public void setDefaultSocketConfig(final SocketConfig defaultSocketConfig) {
        this.configData.setDefaultSocketConfig(defaultSocketConfig);
    }
    
    public ConnectionConfig getDefaultConnectionConfig() {
        return this.configData.getDefaultConnectionConfig();
    }
    
    public void setDefaultConnectionConfig(final ConnectionConfig defaultConnectionConfig) {
        this.configData.setDefaultConnectionConfig(defaultConnectionConfig);
    }
    
    public SocketConfig getSocketConfig(final HttpHost host) {
        return this.configData.getSocketConfig(host);
    }
    
    public void setSocketConfig(final HttpHost host, final SocketConfig socketConfig) {
        this.configData.setSocketConfig(host, socketConfig);
    }
    
    public ConnectionConfig getConnectionConfig(final HttpHost host) {
        return this.configData.getConnectionConfig(host);
    }
    
    public void setConnectionConfig(final HttpHost host, final ConnectionConfig connectionConfig) {
        this.configData.setConnectionConfig(host, connectionConfig);
    }
    
    public int getValidateAfterInactivity() {
        return this.pool.getValidateAfterInactivity();
    }
    
    public void setValidateAfterInactivity(final int ms) {
        this.pool.setValidateAfterInactivity(ms);
    }
    
    static class ConfigData
    {
        private final Map<HttpHost, SocketConfig> socketConfigMap;
        private final Map<HttpHost, ConnectionConfig> connectionConfigMap;
        private volatile SocketConfig defaultSocketConfig;
        private volatile ConnectionConfig defaultConnectionConfig;
        
        ConfigData() {
            this.socketConfigMap = new ConcurrentHashMap<HttpHost, SocketConfig>();
            this.connectionConfigMap = new ConcurrentHashMap<HttpHost, ConnectionConfig>();
        }
        
        public SocketConfig getDefaultSocketConfig() {
            return this.defaultSocketConfig;
        }
        
        public void setDefaultSocketConfig(final SocketConfig defaultSocketConfig) {
            this.defaultSocketConfig = defaultSocketConfig;
        }
        
        public ConnectionConfig getDefaultConnectionConfig() {
            return this.defaultConnectionConfig;
        }
        
        public void setDefaultConnectionConfig(final ConnectionConfig defaultConnectionConfig) {
            this.defaultConnectionConfig = defaultConnectionConfig;
        }
        
        public SocketConfig getSocketConfig(final HttpHost host) {
            return this.socketConfigMap.get(host);
        }
        
        public void setSocketConfig(final HttpHost host, final SocketConfig socketConfig) {
            this.socketConfigMap.put(host, socketConfig);
        }
        
        public ConnectionConfig getConnectionConfig(final HttpHost host) {
            return this.connectionConfigMap.get(host);
        }
        
        public void setConnectionConfig(final HttpHost host, final ConnectionConfig connectionConfig) {
            this.connectionConfigMap.put(host, connectionConfig);
        }
    }
    
    static class InternalConnectionFactory implements ConnFactory<HttpRoute, ManagedHttpClientConnection>
    {
        private final ConfigData configData;
        private final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory;
        
        InternalConnectionFactory(final ConfigData configData, final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory) {
            this.configData = ((configData != null) ? configData : new ConfigData());
            this.connFactory = ((connFactory != null) ? connFactory : ManagedHttpClientConnectionFactory.INSTANCE);
        }
        
        public ManagedHttpClientConnection create(final HttpRoute route) throws IOException {
            ConnectionConfig config = null;
            if (route.getProxyHost() != null) {
                config = this.configData.getConnectionConfig(route.getProxyHost());
            }
            if (config == null) {
                config = this.configData.getConnectionConfig(route.getTargetHost());
            }
            if (config == null) {
                config = this.configData.getDefaultConnectionConfig();
            }
            if (config == null) {
                config = ConnectionConfig.DEFAULT;
            }
            return this.connFactory.create(route, config);
        }
    }
}
