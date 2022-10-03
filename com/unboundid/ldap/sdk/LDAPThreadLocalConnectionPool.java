package com.unboundid.ldap.sdk;

import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.logging.Level;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import java.util.Collections;
import java.util.EnumSet;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.ObjectPair;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPThreadLocalConnectionPool extends AbstractConnectionPool
{
    private static final long DEFAULT_HEALTH_CHECK_INTERVAL = 60000L;
    private final AtomicReference<Set<OperationType>> retryOperationTypes;
    private volatile boolean closed;
    private volatile BindRequest bindRequest;
    private final ConcurrentHashMap<Thread, LDAPConnection> connections;
    private LDAPConnectionPoolHealthCheck healthCheck;
    private final LDAPConnectionPoolHealthCheckThread healthCheckThread;
    private final LDAPConnectionPoolStatistics poolStatistics;
    private volatile long healthCheckInterval;
    private volatile long lastExpiredDisconnectTime;
    private volatile long maxConnectionAge;
    private volatile long minDisconnectInterval;
    private volatile ObjectPair<Long, Schema> pooledSchema;
    private final PostConnectProcessor postConnectProcessor;
    private volatile ServerSet serverSet;
    private String connectionPoolName;
    
    public LDAPThreadLocalConnectionPool(final LDAPConnection connection) throws LDAPException {
        this(connection, null);
    }
    
    public LDAPThreadLocalConnectionPool(final LDAPConnection connection, final PostConnectProcessor postConnectProcessor) throws LDAPException {
        Validator.ensureNotNull(connection);
        this.postConnectProcessor = null;
        this.healthCheck = new LDAPConnectionPoolHealthCheck();
        this.healthCheckInterval = 60000L;
        this.poolStatistics = new LDAPConnectionPoolStatistics(this);
        this.connectionPoolName = null;
        this.retryOperationTypes = new AtomicReference<Set<OperationType>>(Collections.unmodifiableSet((Set<? extends OperationType>)EnumSet.noneOf(OperationType.class)));
        if (!connection.isConnected()) {
            throw new LDAPException(ResultCode.PARAM_ERROR, LDAPMessages.ERR_POOL_CONN_NOT_ESTABLISHED.get());
        }
        this.bindRequest = connection.getLastBindRequest();
        this.serverSet = new SingleServerSet(connection.getConnectedAddress(), connection.getConnectedPort(), connection.getLastUsedSocketFactory(), connection.getConnectionOptions(), null, postConnectProcessor);
        (this.connections = new ConcurrentHashMap<Thread, LDAPConnection>(StaticUtils.computeMapCapacity(20))).put(Thread.currentThread(), connection);
        this.lastExpiredDisconnectTime = 0L;
        this.maxConnectionAge = 0L;
        this.closed = false;
        this.minDisconnectInterval = 0L;
        (this.healthCheckThread = new LDAPConnectionPoolHealthCheckThread(this)).start();
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
    }
    
    public LDAPThreadLocalConnectionPool(final ServerSet serverSet, final BindRequest bindRequest) {
        this(serverSet, bindRequest, null);
    }
    
    public LDAPThreadLocalConnectionPool(final ServerSet serverSet, final BindRequest bindRequest, final PostConnectProcessor postConnectProcessor) {
        Validator.ensureNotNull(serverSet);
        this.serverSet = serverSet;
        this.bindRequest = bindRequest;
        this.postConnectProcessor = postConnectProcessor;
        if (serverSet.includesAuthentication()) {
            Validator.ensureTrue(bindRequest != null, "LDAPThreadLocalConnectionPool.bindRequest must not be null if serverSet.includesAuthentication returns true");
        }
        if (serverSet.includesPostConnectProcessing()) {
            Validator.ensureTrue(postConnectProcessor == null, "LDAPThreadLocalConnectionPool.postConnectProcessor must be null if serverSet.includesPostConnectProcessing returns true.");
        }
        this.healthCheck = new LDAPConnectionPoolHealthCheck();
        this.healthCheckInterval = 60000L;
        this.poolStatistics = new LDAPConnectionPoolStatistics(this);
        this.connectionPoolName = null;
        this.retryOperationTypes = new AtomicReference<Set<OperationType>>(Collections.unmodifiableSet((Set<? extends OperationType>)EnumSet.noneOf(OperationType.class)));
        this.connections = new ConcurrentHashMap<Thread, LDAPConnection>(StaticUtils.computeMapCapacity(20));
        this.lastExpiredDisconnectTime = 0L;
        this.maxConnectionAge = 0L;
        this.minDisconnectInterval = 0L;
        this.closed = false;
        (this.healthCheckThread = new LDAPConnectionPoolHealthCheckThread(this)).start();
    }
    
    private LDAPConnection createConnection() throws LDAPException {
        LDAPConnection c;
        try {
            c = this.serverSet.getConnection(this.healthCheck);
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
                this.healthCheck.ensureConnectionValidAfterAuthentication(c, bindResult);
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
        Label_0625: {
            if (opts.usePooledSchema()) {
                final long currentTime = System.currentTimeMillis();
                if (this.pooledSchema != null) {
                    if (currentTime <= this.pooledSchema.getFirst()) {
                        c.setCachedSchema(this.pooledSchema.getSecond());
                        break Label_0625;
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
            if (numThreads > 1) {
                final ArrayList<LDAPConnection> connList = new ArrayList<LDAPConnection>(this.connections.size());
                final Iterator<LDAPConnection> iterator = this.connections.values().iterator();
                while (iterator.hasNext()) {
                    connList.add(iterator.next());
                    iterator.remove();
                }
                if (!connList.isEmpty()) {
                    final ParallelPoolCloser closer = new ParallelPoolCloser(connList, unbind, numThreads);
                    closer.closeConnections();
                }
            }
            else {
                final Iterator<Map.Entry<Thread, LDAPConnection>> iterator2 = this.connections.entrySet().iterator();
                while (iterator2.hasNext()) {
                    final LDAPConnection conn = (LDAPConnection)iterator2.next().getValue();
                    iterator2.remove();
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
        final Thread t = Thread.currentThread();
        LDAPConnection conn = this.connections.get(t);
        if (this.closed) {
            if (conn != null) {
                conn.terminate(null);
                this.connections.remove(t);
            }
            this.poolStatistics.incrementNumFailedCheckouts();
            Debug.debugConnectionPool(Level.SEVERE, this, null, "Failed to get a connection to a closed connection pool", null);
            throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_POOL_CLOSED.get());
        }
        boolean created = false;
        if (conn == null || !conn.isConnected()) {
            conn = this.createConnection();
            this.connections.put(t, conn);
            created = true;
        }
        try {
            this.healthCheck.ensureConnectionValidForCheckout(conn);
            if (created) {
                this.poolStatistics.incrementNumSuccessfulCheckoutsNewConnection();
                Debug.debugConnectionPool(Level.INFO, this, conn, "Checked out a newly created pooled connection", null);
            }
            else {
                this.poolStatistics.incrementNumSuccessfulCheckoutsWithoutWaiting();
                Debug.debugConnectionPool(Level.INFO, this, conn, "Checked out an existing pooled connection", null);
            }
            return conn;
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            conn.setClosed();
            this.connections.remove(t);
            if (created) {
                this.poolStatistics.incrementNumFailedCheckouts();
                Debug.debugConnectionPool(Level.SEVERE, this, conn, "Failed to check out a connection because a newly created connection failed the checkout health check", le);
                throw le;
            }
            try {
                conn = this.createConnection();
                this.healthCheck.ensureConnectionValidForCheckout(conn);
                this.connections.put(t, conn);
                this.poolStatistics.incrementNumSuccessfulCheckoutsNewConnection();
                Debug.debugConnectionPool(Level.INFO, this, conn, "Checked out a newly created pooled connection", null);
                return conn;
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                this.poolStatistics.incrementNumFailedCheckouts();
                if (conn == null) {
                    Debug.debugConnectionPool(Level.SEVERE, this, conn, "Unable to check out a connection because an error occurred while establishing the connection", le);
                }
                else {
                    Debug.debugConnectionPool(Level.SEVERE, this, conn, "Unable to check out a newly created connection because it failed the checkout health check", le);
                    conn.setClosed();
                }
                throw le;
            }
        }
    }
    
    @Override
    public void releaseConnection(final LDAPConnection connection) {
        if (connection == null) {
            return;
        }
        connection.setConnectionPoolName(this.connectionPoolName);
        if (this.connectionIsExpired(connection)) {
            try {
                final LDAPConnection newConnection = this.createConnection();
                this.connections.put(Thread.currentThread(), newConnection);
                connection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_EXPIRED, null, null);
                connection.terminate(null);
                this.poolStatistics.incrementNumConnectionsClosedExpired();
                Debug.debugConnectionPool(Level.WARNING, this, connection, "Closing a released connection because it is expired", null);
                this.lastExpiredDisconnectTime = System.currentTimeMillis();
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
            }
        }
        try {
            this.healthCheck.ensureConnectionValidForRelease(connection);
        }
        catch (final LDAPException le) {
            this.releaseDefunctConnection(connection);
            return;
        }
        this.poolStatistics.incrementNumReleasedValid();
        Debug.debugConnectionPool(Level.INFO, this, connection, "Released a connection back to the pool", null);
        if (this.closed) {
            this.close();
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
                    connection.terminate(null);
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
    
    private void handleDefunctConnection(final LDAPConnection connection) {
        final Thread t = Thread.currentThread();
        connection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT, null, null);
        connection.setClosed();
        this.connections.remove(t);
        if (this.closed) {
            return;
        }
        try {
            final LDAPConnection conn = this.createConnection();
            this.connections.put(t, conn);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
        }
    }
    
    @Override
    public LDAPConnection replaceDefunctConnection(final LDAPConnection connection) throws LDAPException {
        this.poolStatistics.incrementNumConnectionsClosedDefunct();
        Debug.debugConnectionPool(Level.WARNING, this, connection, "Releasing a defunct connection that is to be replaced", null);
        connection.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_DEFUNCT, null, null);
        connection.setClosed();
        this.connections.remove(Thread.currentThread(), connection);
        if (this.closed) {
            throw new LDAPException(ResultCode.CONNECT_ERROR, LDAPMessages.ERR_POOL_CLOSED.get());
        }
        final LDAPConnection newConnection = this.createConnection();
        this.connections.put(Thread.currentThread(), newConnection);
        return newConnection;
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
        if (this.maxConnectionAge <= 0L) {
            return false;
        }
        final long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastExpiredDisconnectTime < this.minDisconnectInterval) {
            return false;
        }
        final long connectionAge = currentTime - connection.getConnectTime();
        return connectionAge > this.maxConnectionAge;
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
    
    @Override
    protected void doHealthCheck() {
        final Iterator<Map.Entry<Thread, LDAPConnection>> iterator = this.connections.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Thread, LDAPConnection> e = iterator.next();
            final Thread t = e.getKey();
            final LDAPConnection c = e.getValue();
            if (!t.isAlive()) {
                c.setDisconnectInfo(DisconnectType.POOLED_CONNECTION_UNNEEDED, null, null);
                c.terminate(null);
                iterator.remove();
            }
        }
    }
    
    @Override
    public int getCurrentAvailableConnections() {
        return -1;
    }
    
    @Override
    public int getMaximumAvailableConnections() {
        return -1;
    }
    
    @Override
    public LDAPConnectionPoolStatistics getConnectionPoolStatistics() {
        return this.poolStatistics;
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }
    
    @Override
    public void toString(final StringBuilder buffer) {
        buffer.append("LDAPThreadLocalConnectionPool(");
        final String name = this.connectionPoolName;
        if (name != null) {
            buffer.append("name='");
            buffer.append(name);
            buffer.append("', ");
        }
        buffer.append("serverSet=");
        this.serverSet.toString(buffer);
        buffer.append(')');
    }
}
