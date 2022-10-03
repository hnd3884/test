package com.unboundid.ldap.sdk;

import com.unboundid.ldap.protocol.LDAPResponse;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import com.unboundid.util.StaticUtils;
import java.util.logging.Level;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import com.unboundid.util.Debug;
import java.util.Collections;
import java.util.EnumSet;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.ObjectPair;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPConnectionPool extends AbstractConnectionPool
{
    private static final long DEFAULT_HEALTH_CHECK_INTERVAL = 60000L;
    static final String ATTACHMENT_NAME_MAX_CONNECTION_AGE;
    private final AtomicInteger failedReplaceCount;
    private final AtomicReference<Set<OperationType>> retryOperationTypes;
    private volatile boolean closed;
    private boolean createIfNecessary;
    private volatile boolean checkConnectionAgeOnRelease;
    private volatile boolean trySynchronousReadDuringHealthCheck;
    private volatile BindRequest bindRequest;
    private final int numConnections;
    private volatile int minConnectionGoal;
    private LDAPConnectionPoolHealthCheck healthCheck;
    private final LDAPConnectionPoolHealthCheckThread healthCheckThread;
    private final LDAPConnectionPoolStatistics poolStatistics;
    private final LinkedBlockingQueue<LDAPConnection> availableConnections;
    private volatile long healthCheckInterval;
    private volatile long lastExpiredDisconnectTime;
    private volatile long maxConnectionAge;
    private volatile Long maxDefunctReplacementConnectionAge;
    private long maxWaitTime;
    private volatile long minDisconnectInterval;
    private volatile ObjectPair<Long, Schema> pooledSchema;
    private final PostConnectProcessor postConnectProcessor;
    private volatile ServerSet serverSet;
    private String connectionPoolName;
    
    public LDAPConnectionPool(final LDAPConnection connection, final int numConnections) throws LDAPException {
        this(connection, 1, numConnections, null);
    }
    
    public LDAPConnectionPool(final LDAPConnection connection, final int initialConnections, final int maxConnections) throws LDAPException {
        this(connection, initialConnections, maxConnections, null);
    }
    
    public LDAPConnectionPool(final LDAPConnection connection, final int initialConnections, final int maxConnections, final PostConnectProcessor postConnectProcessor) throws LDAPException {
        this(connection, initialConnections, maxConnections, postConnectProcessor, true);
    }
    
    public LDAPConnectionPool(final LDAPConnection connection, final int initialConnections, final int maxConnections, final PostConnectProcessor postConnectProcessor, final boolean throwOnConnectFailure) throws LDAPException {
        this(connection, initialConnections, maxConnections, 1, postConnectProcessor, throwOnConnectFailure);
    }
    
    public LDAPConnectionPool(final LDAPConnection connection, final int initialConnections, final int maxConnections, final int initialConnectThreads, final PostConnectProcessor postConnectProcessor, final boolean throwOnConnectFailure) throws LDAPException {
        this(connection, initialConnections, maxConnections, initialConnectThreads, postConnectProcessor, throwOnConnectFailure, null);
    }
    
    public LDAPConnectionPool(final LDAPConnection connection, final int initialConnections, final int maxConnections, final int initialConnectThreads, final PostConnectProcessor postConnectProcessor, final boolean throwOnConnectFailure, final LDAPConnectionPoolHealthCheck healthCheck) throws LDAPException {
        Validator.ensureNotNull(connection);
        Validator.ensureTrue(initialConnections >= 1, "LDAPConnectionPool.initialConnections must be at least 1.");
        Validator.ensureTrue(maxConnections >= initialConnections, "LDAPConnectionPool.initialConnections must not be greater than maxConnections.");
        this.postConnectProcessor = null;
        this.trySynchronousReadDuringHealthCheck = true;
        this.healthCheckInterval = 60000L;
        this.poolStatistics = new LDAPConnectionPoolStatistics(this);
        this.pooledSchema = null;
        this.connectionPoolName = null;
        this.retryOperationTypes = new AtomicReference<Set<OperationType>>(Collections.unmodifiableSet((Set<? extends OperationType>)EnumSet.noneOf(OperationType.class)));
        this.numConnections = maxConnections;
        this.minConnectionGoal = 0;
        this.availableConnections = new LinkedBlockingQueue<LDAPConnection>(this.numConnections);
        if (!connection.isConnected()) {
            throw new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_POOL_CONN_NOT_ESTABLISHED.get());
        }
        if (healthCheck == null) {
            this.healthCheck = new LDAPConnectionPoolHealthCheck();
        }
        else {
            this.healthCheck = healthCheck;
        }
        this.bindRequest = connection.getLastBindRequest();
        this.serverSet = new SingleServerSet(connection.getConnectedAddress(), connection.getConnectedPort(), connection.getLastUsedSocketFactory(), connection.getConnectionOptions(), null, postConnectProcessor);
        final LDAPConnectionOptions opts = connection.getConnectionOptions();
        if (opts.usePooledSchema()) {
            try {
                final Schema schema = connection.getSchema();
                if (schema != null) {
                    connection.setCachedSchema(schema);
                    final long currentTime = System.currentTimeMillis();
                    final long timeout = opts.getPooledSchemaTimeoutMillis();
                    if (timeout <= 0L || timeout + currentTime <= 0L) {
                        this.pooledSchema = new ObjectPair<Long, Schema>(Long.MAX_VALUE, schema);
                    }
                    else {
                        this.pooledSchema = new ObjectPair<Long, Schema>(timeout + currentTime, schema);
                    }
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        List<LDAPConnection> connList;
        if (initialConnectThreads > 1) {
            connList = Collections.synchronizedList(new ArrayList<LDAPConnection>(initialConnections));
            final ParallelPoolConnector connector = new ParallelPoolConnector(this, connList, initialConnections, initialConnectThreads, throwOnConnectFailure);
            connector.establishConnections();
        }
        else {
            connList = new ArrayList<LDAPConnection>(initialConnections);
            connection.setConnectionName(null);
            connection.setConnectionPool(this);
            connList.add(connection);
            for (int i = 1; i < initialConnections; ++i) {
                try {
                    connList.add(this.createConnection());
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    if (throwOnConnectFailure) {
                        for (final LDAPConnection c : connList) {
                            try {
                                c.setDisconnectInfo(DisconnectType.POOL_CREATION_FAILURE, null, le);
                                c.setClosed();
                            }
                            catch (final Exception e2) {
                                Debug.debugException(e2);
                            }
                        }
                        throw le;
                    }
                }
            }
        }
        this.availableConnections.addAll((Collection<?>)connList);
        this.failedReplaceCount = new AtomicInteger(maxConnections - this.availableConnections.size());
        this.createIfNecessary = true;
        this.checkConnectionAgeOnRelease = false;
        this.maxConnectionAge = 0L;
        this.maxDefunctReplacementConnectionAge = null;
        this.minDisconnectInterval = 0L;
        this.lastExpiredDisconnectTime = 0L;
        this.maxWaitTime = 0L;
        this.closed = false;
        (this.healthCheckThread = new LDAPConnectionPoolHealthCheckThread(this)).start();
    }
    
    public LDAPConnectionPool(final ServerSet serverSet, final BindRequest bindRequest, final int numConnections) throws LDAPException {
        this(serverSet, bindRequest, 1, numConnections, null);
    }
    
    public LDAPConnectionPool(final ServerSet serverSet, final BindRequest bindRequest, final int initialConnections, final int maxConnections) throws LDAPException {
        this(serverSet, bindRequest, initialConnections, maxConnections, null);
    }
    
    public LDAPConnectionPool(final ServerSet serverSet, final BindRequest bindRequest, final int initialConnections, final int maxConnections, final PostConnectProcessor postConnectProcessor) throws LDAPException {
        this(serverSet, bindRequest, initialConnections, maxConnections, postConnectProcessor, true);
    }
    
    public LDAPConnectionPool(final ServerSet serverSet, final BindRequest bindRequest, final int initialConnections, final int maxConnections, final PostConnectProcessor postConnectProcessor, final boolean throwOnConnectFailure) throws LDAPException {
        this(serverSet, bindRequest, initialConnections, maxConnections, 1, postConnectProcessor, throwOnConnectFailure);
    }
    
    public LDAPConnectionPool(final ServerSet serverSet, final BindRequest bindRequest, final int initialConnections, final int maxConnections, final int initialConnectThreads, final PostConnectProcessor postConnectProcessor, final boolean throwOnConnectFailure) throws LDAPException {
        this(serverSet, bindRequest, initialConnections, maxConnections, initialConnectThreads, postConnectProcessor, throwOnConnectFailure, null);
    }
    
    public LDAPConnectionPool(final ServerSet serverSet, final BindRequest bindRequest, final int initialConnections, final int maxConnections, final int initialConnectThreads, final PostConnectProcessor postConnectProcessor, final boolean throwOnConnectFailure, final LDAPConnectionPoolHealthCheck healthCheck) throws LDAPException {
        Validator.ensureNotNull(serverSet);
        Validator.ensureTrue(initialConnections >= 0, "LDAPConnectionPool.initialConnections must be greater than or equal to 0.");
        Validator.ensureTrue(maxConnections > 0, "LDAPConnectionPool.maxConnections must be greater than 0.");
        Validator.ensureTrue(maxConnections >= initialConnections, "LDAPConnectionPool.initialConnections must not be greater than maxConnections.");
        this.serverSet = serverSet;
        this.bindRequest = bindRequest;
        this.postConnectProcessor = postConnectProcessor;
        if (serverSet.includesAuthentication()) {
            Validator.ensureTrue(bindRequest != null, "LDAPConnectionPool.bindRequest must not be null if serverSet.includesAuthentication returns true");
        }
        if (serverSet.includesPostConnectProcessing()) {
            Validator.ensureTrue(postConnectProcessor == null, "LDAPConnectionPool.postConnectProcessor must be null if serverSet.includesPostConnectProcessing returns true.");
        }
        this.trySynchronousReadDuringHealthCheck = false;
        this.healthCheckInterval = 60000L;
        this.poolStatistics = new LDAPConnectionPoolStatistics(this);
        this.pooledSchema = null;
        this.connectionPoolName = null;
        this.retryOperationTypes = new AtomicReference<Set<OperationType>>(Collections.unmodifiableSet((Set<? extends OperationType>)EnumSet.noneOf(OperationType.class)));
        this.minConnectionGoal = 0;
        this.numConnections = maxConnections;
        this.availableConnections = new LinkedBlockingQueue<LDAPConnection>(this.numConnections);
        if (healthCheck == null) {
            this.healthCheck = new LDAPConnectionPoolHealthCheck();
        }
        else {
            this.healthCheck = healthCheck;
        }
        List<LDAPConnection> connList;
        if (initialConnectThreads > 1) {
            connList = Collections.synchronizedList(new ArrayList<LDAPConnection>(initialConnections));
            final ParallelPoolConnector connector = new ParallelPoolConnector(this, connList, initialConnections, initialConnectThreads, throwOnConnectFailure);
            connector.establishConnections();
        }
        else {
            connList = new ArrayList<LDAPConnection>(initialConnections);
            for (int i = 0; i < initialConnections; ++i) {
                try {
                    connList.add(this.createConnection());
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    if (throwOnConnectFailure) {
                        for (final LDAPConnection c : connList) {
                            try {
                                c.setDisconnectInfo(DisconnectType.POOL_CREATION_FAILURE, null, le);
                                c.setClosed();
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                            }
                        }
                        throw le;
                    }
                }
            }
        }
        this.availableConnections.addAll((Collection<?>)connList);
        this.failedReplaceCount = new AtomicInteger(maxConnections - this.availableConnections.size());
        this.createIfNecessary = true;
        this.checkConnectionAgeOnRelease = false;
        this.maxConnectionAge = 0L;
        this.maxDefunctReplacementConnectionAge = null;
        this.minDisconnectInterval = 0L;
        this.lastExpiredDisconnectTime = 0L;
        this.maxWaitTime = 0L;
        this.closed = false;
        (this.healthCheckThread = new LDAPConnectionPoolHealthCheckThread(this)).start();
    }
    
    LDAPConnection createConnection() throws LDAPException {
        return this.createConnection(this.healthCheck);
    }
    
    private LDAPConnection createConnection(final LDAPConnectionPoolHealthCheck healthCheck) throws LDAPException {
        LDAPConnection c;
        try {
            c = this.serverSet.getConnection(healthCheck);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.poolStatistics.incrementNumFailedConnectionAttempts();
            Debug.debugConnectionPool(Level.SEVERE, this, null, "Unable to create a new pooled connection", le);
            throw le;
        }
        c.setConnectionPool(this);
        LDAPConnectionOptions opts = c.getConnectionOptions();
        if (opts.autoReconnect()) {
            opts = opts.duplicate();
            opts.setAutoReconnect(false);
            c.setConnectionOptions(opts);
        }
        if (this.postConnectProcessor != null) {
            try {
                this.postConnectProcessor.processPreAuthenticatedConnection(c);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                try {
                    this.poolStatistics.incrementNumFailedConnectionAttempts();
                    Debug.debugConnectionPool(Level.SEVERE, this, c, "Exception in pre-authentication post-connect processing", e);
                    c.setDisconnectInfo(DisconnectType.POOL_CREATION_FAILURE, null, e);
                    c.setClosed();
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                }
                if (e instanceof LDAPException) {
                    throw (LDAPException)e;
                }
                throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_POOL_POST_CONNECT_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
            }
        }
        if (this.bindRequest != null && !this.serverSet.includesAuthentication()) {
            BindResult bindResult;
            try {
                bindResult = c.bind(this.bindRequest.duplicate());
            }
            catch (final LDAPBindException lbe) {
                Debug.debugException(lbe);
                bindResult = lbe.getBindResult();
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                bindResult = new BindResult(le2);
            }
            try {
                if (healthCheck != null) {
                    healthCheck.ensureConnectionValidAfterAuthentication(c, bindResult);
                }
                if (bindResult.getResultCode() != ResultCode.SUCCESS) {
                    throw new LDAPBindException(bindResult);
                }
            }
            catch (final LDAPException le2) {
                Debug.debugException(le2);
                try {
                    this.poolStatistics.incrementNumFailedConnectionAttempts();
                    if (bindResult.getResultCode() != ResultCode.SUCCESS) {
                        Debug.debugConnectionPool(Level.SEVERE, this, c, "Failed to authenticate a new pooled connection", le2);
                    }
                    else {
                        Debug.debugConnectionPool(Level.SEVERE, this, c, "A new pooled connection failed its post-authentication health check", le2);
                    }
                    c.setDisconnectInfo(DisconnectType.BIND_FAILED, null, le2);
                    c.setClosed();
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                }
                throw le2;
            }
        }
        if (this.postConnectProcessor != null) {
            try {
                this.postConnectProcessor.processPostAuthenticatedConnection(c);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                try {
                    this.poolStatistics.incrementNumFailedConnectionAttempts();
                    Debug.debugConnectionPool(Level.SEVERE, this, c, "Exception in post-authentication post-connect processing", e);
                    c.setDisconnectInfo(DisconnectType.POOL_CREATION_FAILURE, null, e);
                    c.setClosed();
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                }
                if (e instanceof LDAPException) {
                    throw (LDAPException)e;
                }
                throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_POOL_POST_CONNECT_ERROR.get(StaticUtils.getExceptionMessage(e)), e);
            }
        }
        Label_0650: {
            if (opts.usePooledSchema()) {
                final long currentTime = System.currentTimeMillis();
                if (this.pooledSchema != null) {
                    if (currentTime <= this.pooledSchema.getFirst()) {
                        c.setCachedSchema(this.pooledSchema.getSecond());
                        break Label_0650;
                    }
                }
                try {
                    final Schema schema = c.getSchema();
                    if (schema != null) {
                        c.setCachedSchema(schema);
                        final long timeout = opts.getPooledSchemaTimeoutMillis();
                        if (timeout <= 0L || currentTime + timeout <= 0L) {
                            this.pooledSchema = new ObjectPair<Long, Schema>(Long.MAX_VALUE, schema);
                        }
                        else {
                            this.pooledSchema = new ObjectPair<Long, Schema>(currentTime + timeout, schema);
                        }
                    }
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                    if (this.pooledSchema != null) {
                        c.setCachedSchema(this.pooledSchema.getSecond());
                    }
                }
            }
        }
        c.setConnectionPoolName(this.connectionPoolName);
        this.poolStatistics.incrementNumSuccessfulConnectionAttempts();
        Debug.debugConnectionPool(Level.INFO, this, c, "Successfully created a new pooled connection", null);
        return c;
    }
    
    @Override
    public void close() {
        this.close(true, 1);
    }
    
    @Override
    public void close(final boolean unbind, final int numThreads) {
        try {
            final boolean healthCheckThreadAlreadySignaled = this.closed;
            this.closed = true;
            this.healthCheckThread.stopRunning(!healthCheckThreadAlreadySignaled);
            if (numThreads <= 1) {
                while (true) {
                    final LDAPConnection conn = this.availableConnections.poll();
                    if (conn == null) {
                        break;
                    }
                    this.poolStatistics.incrementNumConnectionsClosedUnneeded();
                    Debug.debugConnectionPool(Level.INFO, this, conn, "Closed a connection as part of closing the connection pool", null);
                    conn.setDisconnectInfo(DisconnectType.POOL_CLOSED, null, null);
                    if (unbind) {
                        conn.terminate(null);
                    }
                    else {
                        conn.setClosed();
                    }
                }
                return;
            }
            final ArrayList<LDAPConnection> connList = new ArrayList<LDAPConnection>(this.availableConnections.size());
            this.availableConnections.drainTo(connList);
            if (!connList.isEmpty()) {
                final ParallelPoolCloser closer = new ParallelPoolCloser(connList, unbind, numThreads);
                closer.closeConnections();
            }
        }
        finally {
            Debug.debugConnectionPool(Level.INFO, this, null, "Closed the connection pool", null);
        }
    }
    
    @Override
    public boolean isClosed() {
        return this.closed;
    }
    
    public BindResult bindAndRevertAuthentication(final String bindDN, final String password, final Control... controls) throws LDAPException {
        return this.bindAndRevertAuthentication(new SimpleBindRequest(bindDN, password, controls));
    }
    
    public BindResult bindAndRevertAuthentication(final BindRequest bindRequest) throws LDAPException {
        LDAPConnection conn = this.getConnection();
        try {
            final BindResult result = conn.bind(bindRequest);
            this.releaseAndReAuthenticateConnection(conn);
            return result;
        }
        catch (final Throwable t) {
            Debug.debugException(t);
            if (!(t instanceof LDAPException)) {
                this.releaseDefunctConnection(conn);
                StaticUtils.rethrowIfError(t);
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_POOL_OP_EXCEPTION.get(StaticUtils.getExceptionMessage(t)), t);
            }
            LDAPException le = (LDAPException)t;
            boolean shouldThrow;
            try {
                this.healthCheck.ensureConnectionValidAfterException(conn, le);
                this.releaseAndReAuthenticateConnection(conn);
                shouldThrow = true;
            }
            catch (final Exception e) {
                Debug.debugException(e);
                if (!this.getOperationTypesToRetryDueToInvalidConnections().contains(OperationType.BIND)) {
                    this.releaseDefunctConnection(conn);
                    shouldThrow = true;
                }
                else {
                    shouldThrow = false;
                }
            }
            if (shouldThrow) {
                throw le;
            }
            conn = this.replaceDefunctConnection(conn);
            try {
                final BindResult result = conn.bind(bindRequest);
                this.releaseAndReAuthenticateConnection(conn);
                return result;
            }
            catch (final Throwable t) {
                Debug.debugException(t);
                if (t instanceof LDAPException) {
                    le = (LDAPException)t;
                    try {
                        this.healthCheck.ensureConnectionValidAfterException(conn, le);
                        this.releaseAndReAuthenticateConnection(conn);
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                        this.releaseDefunctConnection(conn);
                    }
                    throw le;
                }
                this.releaseDefunctConnection(conn);
                StaticUtils.rethrowIfError(t);
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_POOL_OP_EXCEPTION.get(StaticUtils.getExceptionMessage(t)), t);
            }
        }
    }
    
    @Override
    public LDAPConnection getConnection() throws LDAPException {
        if (this.closed) {
            this.poolStatistics.incrementNumFailedCheckouts();
            Debug.debugConnectionPool(Level.SEVERE, this, null, "Failed to get a connection to a closed connection pool", null);
            throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_POOL_CLOSED.get());
        }
        LDAPConnection conn = this.availableConnections.poll();
        if (conn != null) {
            Exception connException = null;
            if (conn.isConnected()) {
                try {
                    this.healthCheck.ensureConnectionValidForCheckout(conn);
                    this.poolStatistics.incrementNumSuccessfulCheckoutsWithoutWaiting();
                    Debug.debugConnectionPool(Level.INFO, this, conn, "Checked out an immediately available pooled connection", null);
                    return conn;
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    connException = le;
                }
            }
            this.poolStatistics.incrementNumConnectionsClosedDefunct();
            Debug.debugConnectionPool(Level.WARNING, this, conn, "Closing a defunct connection encountered during checkout", connException);
            this.handleDefunctConnection(conn);
            for (int i = 0; i < this.numConnections; ++i) {
                conn = this.availableConnections.poll();
                if (conn == null) {
                    break;
                }
                if (conn.isConnected()) {
                    try {
                        this.healthCheck.ensureConnectionValidForCheckout(conn);
                        this.poolStatistics.incrementNumSuccessfulCheckoutsWithoutWaiting();
                        Debug.debugConnectionPool(Level.INFO, this, conn, "Checked out an immediately available pooled connection", null);
                        return conn;
                    }
                    catch (final LDAPException le2) {
                        Debug.debugException(le2);
                        this.poolStatistics.incrementNumConnectionsClosedDefunct();
                        Debug.debugConnectionPool(Level.WARNING, this, conn, "Closing a defunct connection encountered during checkout", le2);
                        this.handleDefunctConnection(conn);
                        continue;
                    }
                }
                this.poolStatistics.incrementNumConnectionsClosedDefunct();
                Debug.debugConnectionPool(Level.WARNING, this, conn, "Closing a defunct connection encountered during checkout", null);
                this.handleDefunctConnection(conn);
            }
        }
        if (this.failedReplaceCount.get() > 0) {
            final int newReplaceCount = this.failedReplaceCount.getAndDecrement();
            if (newReplaceCount > 0) {
                try {
                    conn = this.createConnection();
                    this.poolStatistics.incrementNumSuccessfulCheckoutsNewConnection();
                    Debug.debugConnectionPool(Level.INFO, this, conn, "Checked out a newly created connection", null);
                    return conn;
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    this.failedReplaceCount.incrementAndGet();
                    this.poolStatistics.incrementNumFailedCheckouts();
                    Debug.debugConnectionPool(Level.SEVERE, this, conn, "Unable to create a new connection for checkout", le);
                    throw le;
                }
            }
            this.failedReplaceCount.incrementAndGet();
        }
        if (this.maxWaitTime > 0L) {
            try {
                final long startWaitTime = System.currentTimeMillis();
                conn = this.availableConnections.poll(this.maxWaitTime, TimeUnit.MILLISECONDS);
                final long elapsedWaitTime = System.currentTimeMillis() - startWaitTime;
                if (conn != null) {
                    try {
                        this.healthCheck.ensureConnectionValidForCheckout(conn);
                        this.poolStatistics.incrementNumSuccessfulCheckoutsAfterWaiting();
                        Debug.debugConnectionPool(Level.INFO, this, conn, "Checked out an existing connection after waiting " + elapsedWaitTime + "ms for it to become available", null);
                        return conn;
                    }
                    catch (final LDAPException le3) {
                        Debug.debugException(le3);
                        this.poolStatistics.incrementNumConnectionsClosedDefunct();
                        Debug.debugConnectionPool(Level.WARNING, this, conn, "Got a connection for checkout after waiting " + elapsedWaitTime + "ms for it to become available, but " + "the connection failed the checkout health check", le3);
                        this.handleDefunctConnection(conn);
                    }
                }
            }
            catch (final InterruptedException ie) {
                Debug.debugException(ie);
                Thread.currentThread().interrupt();
                throw new LDAPException(ResultCode.LOCAL_ERROR, LDAPMessages.ERR_POOL_CHECKOUT_INTERRUPTED.get(), ie);
            }
        }
        if (this.createIfNecessary) {
            try {
                conn = this.createConnection();
                this.poolStatistics.incrementNumSuccessfulCheckoutsNewConnection();
                Debug.debugConnectionPool(Level.INFO, this, conn, "Checked out a newly created connection", null);
                return conn;
            }
            catch (final LDAPException le4) {
                Debug.debugException(le4);
                this.poolStatistics.incrementNumFailedCheckouts();
                Debug.debugConnectionPool(Level.SEVERE, this, null, "Unable to create a new connection for checkout", le4);
                throw le4;
            }
        }
        this.poolStatistics.incrementNumFailedCheckouts();
        Debug.debugConnectionPool(Level.SEVERE, this, null, "Unable to check out a connection because none are available", null);
        throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_POOL_NO_CONNECTIONS.get());
    }
    
    public LDAPConnection getConnection(final String host, final int port) {
        if (this.closed) {
            this.poolStatistics.incrementNumFailedCheckouts();
            Debug.debugConnectionPool(Level.WARNING, this, null, "Failed to get a connection to a closed connection pool", null);
            return null;
        }
        final HashSet<LDAPConnection> examinedConnections = new HashSet<LDAPConnection>(StaticUtils.computeMapCapacity(this.numConnections));
        while (true) {
            final LDAPConnection conn = this.availableConnections.poll();
            if (conn == null) {
                this.poolStatistics.incrementNumFailedCheckouts();
                Debug.debugConnectionPool(Level.SEVERE, this, null, "Failed to get an existing connection to " + host + ':' + port + " because no connections are immediately available", null);
                return null;
            }
            if (examinedConnections.contains(conn)) {
                if (!this.availableConnections.offer(conn)) {
                    this.discardConnection(conn);
                }
                this.poolStatistics.incrementNumFailedCheckouts();
                Debug.debugConnectionPool(Level.WARNING, this, null, "Failed to get an existing connection to " + host + ':' + port + " because none of the available connections are " + "established to that server", null);
                return null;
            }
            if (conn.getConnectedAddress().equals(host) && port == conn.getConnectedPort()) {
                try {
                    this.healthCheck.ensureConnectionValidForCheckout(conn);
                    this.poolStatistics.incrementNumSuccessfulCheckoutsWithoutWaiting();
                    Debug.debugConnectionPool(Level.INFO, this, conn, "Successfully checked out an existing connection to requested server " + host + ':' + port, null);
                    return conn;
                }
                catch (final LDAPException le) {
                    Debug.debugException(le);
                    this.poolStatistics.incrementNumConnectionsClosedDefunct();
                    Debug.debugConnectionPool(Level.WARNING, this, conn, "Closing an existing connection to requested server " + host + ':' + port + " because it failed the checkout health " + "check", le);
                    this.handleDefunctConnection(conn);
                    continue;
                }
            }
            if (this.availableConnections.offer(conn)) {
                examinedConnections.add(conn);
            }
            else {
                this.discardConnection(conn);
            }
        }
    }
    
    @Override
    public void releaseConnection(final LDAPConnection connection) {
        if (connection == null) {
            return;
        }
        connection.setConnectionPoolName(this.connectionPoolName);
        if (this.checkConnectionAgeOnRelease && this.connectionIsExpired(connection)) {
            try {
                final LDAPConnection newConnection = this.createConnection();
                if (this.availableConnections.offer(newConnection)) {
                    connection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_EXPIRED, null, null);
                    connection.terminate(null);
                    this.poolStatistics.incrementNumConnectionsClosedExpired();
                    Debug.debugConnectionPool(Level.WARNING, this, connection, "Closing a released connection because it is expired", null);
                    this.lastExpiredDisconnectTime = System.currentTimeMillis();
                }
                else {
                    newConnection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_UNNEEDED, null, null);
                    newConnection.terminate(null);
                    this.poolStatistics.incrementNumConnectionsClosedUnneeded();
                    Debug.debugConnectionPool(Level.WARNING, this, connection, "Closing a released connection because the pool is already full", null);
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
            }
            return;
        }
        try {
            this.healthCheck.ensureConnectionValidForRelease(connection);
        }
        catch (final LDAPException le) {
            this.releaseDefunctConnection(connection);
            return;
        }
        if (this.availableConnections.offer(connection)) {
            this.poolStatistics.incrementNumReleasedValid();
            Debug.debugConnectionPool(Level.INFO, this, connection, "Released a connection back to the pool", null);
            if (this.closed) {
                this.close();
            }
            return;
        }
        connection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_UNNEEDED, null, null);
        this.poolStatistics.incrementNumConnectionsClosedUnneeded();
        Debug.debugConnectionPool(Level.WARNING, this, connection, "Closing a released connection because the pool is already full", null);
        connection.terminate(null);
    }
    
    public void discardConnection(final LDAPConnection connection) {
        if (connection == null) {
            return;
        }
        connection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_UNNEEDED, null, null);
        connection.terminate(null);
        this.poolStatistics.incrementNumConnectionsClosedUnneeded();
        Debug.debugConnectionPool(Level.INFO, this, connection, "Discareded a connection that is no longer needed", null);
        if (this.availableConnections.remainingCapacity() > 0) {
            final int newReplaceCount = this.failedReplaceCount.incrementAndGet();
            if (newReplaceCount > this.numConnections) {
                this.failedReplaceCount.set(this.numConnections);
            }
        }
    }
    
    public void releaseAndReAuthenticateConnection(final LDAPConnection connection) {
        if (connection == null) {
            return;
        }
        try {
            BindResult bindResult;
            try {
                if (this.bindRequest == null) {
                    bindResult = connection.bind("", "");
                }
                else {
                    bindResult = connection.bind(this.bindRequest.duplicate());
                }
            }
            catch (final LDAPBindException lbe) {
                Debug.debugException(lbe);
                bindResult = lbe.getBindResult();
            }
            try {
                this.healthCheck.ensureConnectionValidAfterAuthentication(connection, bindResult);
                if (bindResult.getResultCode() != ResultCode.SUCCESS) {
                    throw new LDAPBindException(bindResult);
                }
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                try {
                    connection.setDisconnectInfo(DisconnectType.BIND_FAILED, null, le);
                    connection.setClosed();
                    this.releaseDefunctConnection(connection);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
                throw le;
            }
            this.releaseConnection(connection);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            this.releaseDefunctConnection(connection);
        }
    }
    
    @Override
    public void releaseDefunctConnection(final LDAPConnection connection) {
        if (connection == null) {
            return;
        }
        connection.setConnectionPoolName(this.connectionPoolName);
        this.poolStatistics.incrementNumConnectionsClosedDefunct();
        Debug.debugConnectionPool(Level.WARNING, this, connection, "Releasing a defunct connection", null);
        this.handleDefunctConnection(connection);
    }
    
    private LDAPConnection handleDefunctConnection(final LDAPConnection connection) {
        connection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT, null, null);
        connection.setClosed();
        if (this.closed) {
            return null;
        }
        if (this.createIfNecessary && this.availableConnections.remainingCapacity() <= 0) {
            return null;
        }
        try {
            final LDAPConnection conn = this.createConnection();
            if (this.maxDefunctReplacementConnectionAge != null && conn.getAttachment(LDAPConnectionPool.ATTACHMENT_NAME_MAX_CONNECTION_AGE) == null) {
                conn.setAttachment(LDAPConnectionPool.ATTACHMENT_NAME_MAX_CONNECTION_AGE, this.maxDefunctReplacementConnectionAge);
            }
            if (!this.availableConnections.offer(conn)) {
                conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_UNNEEDED, null, null);
                conn.terminate(null);
                return null;
            }
            return conn;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            final int newReplaceCount = this.failedReplaceCount.incrementAndGet();
            if (newReplaceCount > this.numConnections) {
                this.failedReplaceCount.set(this.numConnections);
            }
            return null;
        }
    }
    
    @Override
    public LDAPConnection replaceDefunctConnection(final LDAPConnection connection) throws LDAPException {
        this.poolStatistics.incrementNumConnectionsClosedDefunct();
        Debug.debugConnectionPool(Level.WARNING, this, connection, "Releasing a defunct connection that is to be replaced", null);
        connection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT, null, null);
        connection.setClosed();
        if (this.closed) {
            throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_POOL_CLOSED.get());
        }
        try {
            return this.createConnection();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.failedReplaceCount.incrementAndGet();
            throw le;
        }
    }
    
    @Override
    public Set<OperationType> getOperationTypesToRetryDueToInvalidConnections() {
        return this.retryOperationTypes.get();
    }
    
    @Override
    public void setRetryFailedOperationsDueToInvalidConnections(final Set<OperationType> operationTypes) {
        if (operationTypes == null || operationTypes.isEmpty()) {
            this.retryOperationTypes.set(Collections.unmodifiableSet((Set<? extends OperationType>)EnumSet.noneOf(OperationType.class)));
        }
        else {
            final EnumSet<OperationType> s = EnumSet.noneOf(OperationType.class);
            s.addAll((Collection<?>)operationTypes);
            this.retryOperationTypes.set(Collections.unmodifiableSet((Set<? extends OperationType>)s));
        }
    }
    
    private boolean connectionIsExpired(final LDAPConnection connection) {
        final Object maxAgeObj = connection.getAttachment(LDAPConnectionPool.ATTACHMENT_NAME_MAX_CONNECTION_AGE);
        long maxAge;
        if (maxAgeObj != null && maxAgeObj instanceof Long) {
            maxAge = (long)maxAgeObj;
        }
        else {
            maxAge = this.maxConnectionAge;
        }
        if (maxAge <= 0L) {
            return false;
        }
        final long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastExpiredDisconnectTime < this.minDisconnectInterval) {
            return false;
        }
        final long connectionAge = currentTime - connection.getConnectTime();
        return connectionAge > maxAge;
    }
    
    public void setBindRequest(final BindRequest bindRequest) {
        this.bindRequest = bindRequest;
    }
    
    public void setServerSet(final ServerSet serverSet) {
        Validator.ensureNotNull(serverSet);
        this.serverSet = serverSet;
    }
    
    @Override
    public String getConnectionPoolName() {
        return this.connectionPoolName;
    }
    
    @Override
    public void setConnectionPoolName(final String connectionPoolName) {
        this.connectionPoolName = connectionPoolName;
        for (final LDAPConnection c : this.availableConnections) {
            c.setConnectionPoolName(connectionPoolName);
        }
    }
    
    public boolean getCreateIfNecessary() {
        return this.createIfNecessary;
    }
    
    public void setCreateIfNecessary(final boolean createIfNecessary) {
        this.createIfNecessary = createIfNecessary;
    }
    
    public long getMaxWaitTimeMillis() {
        return this.maxWaitTime;
    }
    
    public void setMaxWaitTimeMillis(final long maxWaitTime) {
        if (maxWaitTime > 0L) {
            this.maxWaitTime = maxWaitTime;
        }
        else {
            this.maxWaitTime = 0L;
        }
    }
    
    public long getMaxConnectionAgeMillis() {
        return this.maxConnectionAge;
    }
    
    public void setMaxConnectionAgeMillis(final long maxConnectionAge) {
        if (maxConnectionAge > 0L) {
            this.maxConnectionAge = maxConnectionAge;
        }
        else {
            this.maxConnectionAge = 0L;
        }
    }
    
    public Long getMaxDefunctReplacementConnectionAgeMillis() {
        return this.maxDefunctReplacementConnectionAge;
    }
    
    public void setMaxDefunctReplacementConnectionAgeMillis(final Long maxDefunctReplacementConnectionAge) {
        if (maxDefunctReplacementConnectionAge == null) {
            this.maxDefunctReplacementConnectionAge = null;
        }
        else if (maxDefunctReplacementConnectionAge > 0L) {
            this.maxDefunctReplacementConnectionAge = maxDefunctReplacementConnectionAge;
        }
        else {
            this.maxDefunctReplacementConnectionAge = 0L;
        }
    }
    
    public boolean checkConnectionAgeOnRelease() {
        return this.checkConnectionAgeOnRelease;
    }
    
    public void setCheckConnectionAgeOnRelease(final boolean checkConnectionAgeOnRelease) {
        this.checkConnectionAgeOnRelease = checkConnectionAgeOnRelease;
    }
    
    public long getMinDisconnectIntervalMillis() {
        return this.minDisconnectInterval;
    }
    
    public void setMinDisconnectIntervalMillis(final long minDisconnectInterval) {
        if (minDisconnectInterval > 0L) {
            this.minDisconnectInterval = minDisconnectInterval;
        }
        else {
            this.minDisconnectInterval = 0L;
        }
    }
    
    @Override
    public LDAPConnectionPoolHealthCheck getHealthCheck() {
        return this.healthCheck;
    }
    
    public void setHealthCheck(final LDAPConnectionPoolHealthCheck healthCheck) {
        Validator.ensureNotNull(healthCheck);
        this.healthCheck = healthCheck;
    }
    
    @Override
    public long getHealthCheckIntervalMillis() {
        return this.healthCheckInterval;
    }
    
    @Override
    public void setHealthCheckIntervalMillis(final long healthCheckInterval) {
        Validator.ensureTrue(healthCheckInterval > 0L, "LDAPConnectionPool.healthCheckInterval must be greater than 0.");
        this.healthCheckInterval = healthCheckInterval;
        this.healthCheckThread.wakeUp();
    }
    
    public boolean trySynchronousReadDuringHealthCheck() {
        return this.trySynchronousReadDuringHealthCheck;
    }
    
    public void setTrySynchronousReadDuringHealthCheck(final boolean trySynchronousReadDuringHealthCheck) {
        this.trySynchronousReadDuringHealthCheck = trySynchronousReadDuringHealthCheck;
    }
    
    @Override
    protected void doHealthCheck() {
        this.invokeHealthCheck(null, true);
    }
    
    public LDAPConnectionPoolHealthCheckResult invokeHealthCheck(final LDAPConnectionPoolHealthCheck healthCheck, final boolean checkForExpiration) {
        return this.invokeHealthCheck(healthCheck, checkForExpiration, checkForExpiration);
    }
    
    public LDAPConnectionPoolHealthCheckResult invokeHealthCheck(final LDAPConnectionPoolHealthCheck healthCheck, final boolean checkForExpiration, final boolean checkMinConnectionGoal) {
        LDAPConnectionPoolHealthCheck hc;
        if (healthCheck == null) {
            hc = this.healthCheck;
        }
        else {
            hc = healthCheck;
        }
        final HashSet<LDAPConnection> examinedConnections = new HashSet<LDAPConnection>(StaticUtils.computeMapCapacity(this.numConnections));
        int numExamined = 0;
        int numDefunct = 0;
        int numExpired = 0;
        int i = 0;
        while (i < this.numConnections) {
            LDAPConnection conn = this.availableConnections.poll();
            if (conn == null) {
                break;
            }
            if (examinedConnections.contains(conn)) {
                if (!this.availableConnections.offer(conn)) {
                    conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_UNNEEDED, null, null);
                    this.poolStatistics.incrementNumConnectionsClosedUnneeded();
                    Debug.debugConnectionPool(Level.INFO, this, conn, "Closing a connection that had just been health checked because the pool is now full", null);
                    conn.terminate(null);
                    break;
                }
                break;
            }
            else {
                ++numExamined;
                Label_1534: {
                    if (!conn.isConnected()) {
                        ++numDefunct;
                        this.poolStatistics.incrementNumConnectionsClosedDefunct();
                        Debug.debugConnectionPool(Level.WARNING, this, conn, "Closing a connection that was identified as not established during health check processing", null);
                        conn = this.handleDefunctConnection(conn);
                        if (conn != null) {
                            examinedConnections.add(conn);
                        }
                    }
                    else {
                        if (checkForExpiration && this.connectionIsExpired(conn)) {
                            ++numExpired;
                            try {
                                final LDAPConnection newConnection = this.createConnection();
                                if (this.availableConnections.offer(newConnection)) {
                                    examinedConnections.add(newConnection);
                                    conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_EXPIRED, null, null);
                                    conn.terminate(null);
                                    this.poolStatistics.incrementNumConnectionsClosedExpired();
                                    Debug.debugConnectionPool(Level.INFO, this, conn, "Closing a connection that was identified as expired during health check processing", null);
                                    this.lastExpiredDisconnectTime = System.currentTimeMillis();
                                    break Label_1534;
                                }
                                newConnection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_UNNEEDED, null, null);
                                newConnection.terminate(null);
                                this.poolStatistics.incrementNumConnectionsClosedUnneeded();
                                Debug.debugConnectionPool(Level.INFO, this, newConnection, "Closing a newly created connection created to replace an expired connection because the pool is already full", null);
                            }
                            catch (final LDAPException le) {
                                Debug.debugException(le);
                            }
                        }
                        if (this.trySynchronousReadDuringHealthCheck && conn.synchronousMode()) {
                            int previousTimeout = Integer.MIN_VALUE;
                            Socket s = null;
                            try {
                                s = conn.getConnectionInternals(true).getSocket();
                                previousTimeout = s.getSoTimeout();
                                InternalSDKHelper.setSoTimeout(conn, 1);
                                final LDAPResponse response = conn.readResponse(0);
                                if (response instanceof ConnectionClosedResponse) {
                                    ++numDefunct;
                                    conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT, LDAPMessages.ERR_POOL_HEALTH_CHECK_CONN_CLOSED.get(), null);
                                    this.poolStatistics.incrementNumConnectionsClosedDefunct();
                                    Debug.debugConnectionPool(Level.WARNING, this, conn, "Closing existing connection discovered to be disconnected during health check processing", null);
                                    conn = this.handleDefunctConnection(conn);
                                    if (conn != null) {
                                        examinedConnections.add(conn);
                                    }
                                }
                                if (response instanceof ExtendedResult) {
                                    final UnsolicitedNotificationHandler h = conn.getConnectionOptions().getUnsolicitedNotificationHandler();
                                    if (h != null) {
                                        h.handleUnsolicitedNotification(conn, (ExtendedResult)response);
                                    }
                                }
                                else if (response instanceof LDAPResult) {
                                    final LDAPResult r = (LDAPResult)response;
                                    if (r.getResultCode() == ResultCode.SERVER_DOWN) {
                                        ++numDefunct;
                                        conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT, LDAPMessages.ERR_POOL_HEALTH_CHECK_CONN_CLOSED.get(), null);
                                        this.poolStatistics.incrementNumConnectionsClosedDefunct();
                                        Debug.debugConnectionPool(Level.WARNING, this, conn, "Closing existing connection discovered to be invalid with result " + r + " during health check " + "processing", null);
                                        conn = this.handleDefunctConnection(conn);
                                        if (conn != null) {
                                            examinedConnections.add(conn);
                                        }
                                    }
                                }
                            }
                            catch (final LDAPException le2) {
                                if (le2.getResultCode() == ResultCode.TIMEOUT) {
                                    Debug.debugException(Level.FINEST, le2);
                                }
                                else {
                                    Debug.debugException(le2);
                                    ++numDefunct;
                                    conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT, LDAPMessages.ERR_POOL_HEALTH_CHECK_READ_FAILURE.get(StaticUtils.getExceptionMessage(le2)), le2);
                                    this.poolStatistics.incrementNumConnectionsClosedDefunct();
                                    Debug.debugConnectionPool(Level.WARNING, this, conn, "Closing existing connection discovered to be invalid during health check processing", le2);
                                    conn = this.handleDefunctConnection(conn);
                                    if (conn != null) {
                                        examinedConnections.add(conn);
                                    }
                                }
                            }
                            catch (final Exception e) {
                                Debug.debugException(e);
                                ++numDefunct;
                                conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT, LDAPMessages.ERR_POOL_HEALTH_CHECK_READ_FAILURE.get(StaticUtils.getExceptionMessage(e)), e);
                                this.poolStatistics.incrementNumConnectionsClosedDefunct();
                                Debug.debugConnectionPool(Level.SEVERE, this, conn, "Closing existing connection discovered to be invalid with an unexpected exception type during health check processing", e);
                                conn = this.handleDefunctConnection(conn);
                                if (conn != null) {
                                    examinedConnections.add(conn);
                                }
                            }
                            finally {
                                if (previousTimeout != Integer.MIN_VALUE) {
                                    try {
                                        if (s != null) {
                                            InternalSDKHelper.setSoTimeout(conn, previousTimeout);
                                        }
                                    }
                                    catch (final Exception e2) {
                                        Debug.debugException(e2);
                                        ++numDefunct;
                                        conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT, null, e2);
                                        this.poolStatistics.incrementNumConnectionsClosedDefunct();
                                        Debug.debugConnectionPool(Level.SEVERE, this, conn, "Closing existing connection during health check processing because an error occurred while attempting to set the SO_TIMEOUT", e2);
                                        conn = this.handleDefunctConnection(conn);
                                        if (conn != null) {
                                            examinedConnections.add(conn);
                                        }
                                    }
                                }
                            }
                        }
                        try {
                            hc.ensureConnectionValidForContinuedUse(conn);
                            if (this.availableConnections.offer(conn)) {
                                examinedConnections.add(conn);
                            }
                            else {
                                conn.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_UNNEEDED, null, null);
                                this.poolStatistics.incrementNumConnectionsClosedUnneeded();
                                Debug.debugConnectionPool(Level.INFO, this, conn, "Closing existing connection that passed health check processing because the pool is already full", null);
                                conn.terminate(null);
                            }
                        }
                        catch (final Exception e3) {
                            Debug.debugException(e3);
                            ++numDefunct;
                            this.poolStatistics.incrementNumConnectionsClosedDefunct();
                            Debug.debugConnectionPool(Level.WARNING, this, conn, "Closing existing connection that failed health check processing", e3);
                            conn = this.handleDefunctConnection(conn);
                            if (conn != null) {
                                examinedConnections.add(conn);
                            }
                        }
                    }
                }
                ++i;
            }
        }
        if (checkMinConnectionGoal) {
            try {
                for (int neededConnections = this.minConnectionGoal - this.availableConnections.size(), j = 0; j < neededConnections; ++j) {
                    final LDAPConnection conn2 = this.createConnection(hc);
                    if (!this.availableConnections.offer(conn2)) {
                        conn2.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_UNNEEDED, null, null);
                        this.poolStatistics.incrementNumConnectionsClosedUnneeded();
                        Debug.debugConnectionPool(Level.INFO, this, conn2, "Closing a new connection that was created during health check processing in achieve the minimum connection goal, but the pool had already become full after the connection was created", null);
                        conn2.terminate(null);
                        break;
                    }
                }
            }
            catch (final Exception e4) {
                Debug.debugException(e4);
            }
        }
        return new LDAPConnectionPoolHealthCheckResult(numExamined, numExpired, numDefunct);
    }
    
    @Override
    public int getCurrentAvailableConnections() {
        return this.availableConnections.size();
    }
    
    @Override
    public int getMaximumAvailableConnections() {
        return this.numConnections;
    }
    
    public int getMinimumAvailableConnectionGoal() {
        return this.minConnectionGoal;
    }
    
    public void setMinimumAvailableConnectionGoal(final int goal) {
        if (goal > this.numConnections) {
            this.minConnectionGoal = this.numConnections;
        }
        else if (goal > 0) {
            this.minConnectionGoal = goal;
        }
        else {
            this.minConnectionGoal = 0;
        }
    }
    
    @Override
    public LDAPConnectionPoolStatistics getConnectionPoolStatistics() {
        return this.poolStatistics;
    }
    
    public void shrinkPool(final int connectionsToRetain) {
        while (this.availableConnections.size() > connectionsToRetain) {
            LDAPConnection conn;
            try {
                conn = this.getConnection();
            }
            catch (final LDAPException le) {
                return;
            }
            if (this.availableConnections.size() < connectionsToRetain) {
                this.releaseConnection(conn);
                return;
            }
            this.discardConnection(conn);
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("LDAPConnectionPool(");
        final String name = this.connectionPoolName;
        if (name != null) {
            buffer.append("name='");
            buffer.append(name);
            buffer.append("', ");
        }
        buffer.append("serverSet=");
        this.serverSet.toString(buffer);
        buffer.append(", maxConnections=");
        buffer.append(this.numConnections);
        buffer.append(')');
    }
    
    static {
        ATTACHMENT_NAME_MAX_CONNECTION_AGE = LDAPConnectionPool.class.getName() + ".maxConnectionAge";
    }
}
