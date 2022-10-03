package com.zoho.cp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.concurrent.Executor;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import com.zoho.metrics.Metrics;
import java.util.Map;
import java.sql.SQLException;
import com.zoho.mickey.ExceptionUtils;
import java.util.logging.Level;
import com.zoho.mickey.cp.ConnectionInfoFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionPool
{
    private ConcurrentHashMap<ConnectionDetail, String> allConns;
    private ObjectStack<ConnectionDetail> freeConns;
    private int maxConnections;
    private int blockingTimeout;
    ConnectionCreator creator;
    private boolean active;
    private int maxIdleTime;
    private static final Logger LOGGER;
    static ThreadPoolExecutor executors;
    
    public ConnectionPool(final int maxConnections, final ConnectionCreator creator, final int blockingTimeout, final int maxIdleTime) throws Exception {
        this.allConns = new ConcurrentHashMap<ConnectionDetail, String>();
        this.active = true;
        this.creator = creator;
        this.maxConnections = maxConnections;
        this.blockingTimeout = blockingTimeout;
        this.maxIdleTime = maxIdleTime;
        this.freeConns = new ObjectStack<ConnectionDetail>(maxConnections, Boolean.getBoolean(ConnectionPool.class.getName() + ".fair"));
        ConnectionInfoFactory.setConnectionPool(this);
    }
    
    public ConnectionDetail getConnectionDetail() throws Exception {
        if (!this.active) {
            throw new Exception("This connection pool is not active.. Couldn't give connection");
        }
        ConnectionDetail detail;
        while (true) {
            detail = this._getConnectionDetail(true);
            if (detail.isValid()) {
                break;
            }
            this.removeConnectionDetail(detail);
        }
        return detail;
    }
    
    private ConnectionDetail _getConnectionDetail(final boolean canCreateNew) throws Exception {
        ConnectionDetail detail = null;
        ConnectionCreator.ConnectionInfo connectionInfo = null;
        try {
            detail = this.freeConns.acquirePermitAndPollLastElement(this.blockingTimeout);
            if (detail == null && canCreateNew) {
                connectionInfo = this.creator.createConnectionInfo();
                final Long serverId = this.creator.fetchServerId(connectionInfo.conn);
                detail = new ConnectionDetail(connectionInfo.conn, serverId, this, connectionInfo.socket);
                detail.setAbortHandler(this.creator.getAbortHandler());
                this.addConnection(detail);
            }
            else if (detail != null) {
                connectionInfo = new ConnectionCreator.ConnectionInfo();
                connectionInfo.setPhysicalConnection(detail.physicalConnection);
            }
            if (detail != null) {
                detail.hasReturned.set(false);
            }
            ConnectionInfoFactory.addConnectionInfo(detail.physicalConnection, connectionInfo);
            ConnectionInfoFactory.addConnectionDetail(detail.physicalConnection, detail);
            ConnectionPool.LOGGER.log(Level.FINE, "DEBUG :: ConnectionDetail :: {0} :: _getConnectionDetail", detail);
            detail.stackTrace = ExceptionUtils.getStackTraceForCurrentThread();
            return detail;
        }
        catch (final NoPermitsAvailableException exc) {
            throw new Exception("No ManagedConnections available within configured blocking timeout: " + this.blockingTimeout + " [ms]", exc);
        }
        catch (final SQLException sqle) {
            if (detail != null) {
                ConnectionInfoFactory.removeConnectionDetail(detail.physicalConnection);
                ConnectionInfoFactory.removeConnectionInfo(detail.physicalConnection);
            }
            this.freeConns.releasePermit();
            if (detail != null) {
                this.handleException(sqle, detail);
            }
            throw new Exception("Exception during getConnection from pool", sqle);
        }
        catch (final InterruptedException exc2) {
            throw new Exception("InterruptedException during getConnection from pool", exc2);
        }
    }
    
    void returnConnectionDetail(final ConnectionDetail detail) {
        if (detail.hasReturned.compareAndSet(false, true)) {
            if (!this.active) {
                this.removeConnectionDetail(detail);
                return;
            }
            detail.updateUsageCounter(IdleConnectionRemover.getThreadTurnCounter());
            detail.stackTrace = "";
            ConnectionInfoFactory.removeConnectionInfo(detail.physicalConnection);
            ConnectionInfoFactory.removeConnectionDetail(detail.physicalConnection);
            this.freeConns.addElementAtLastAndReleasePermit(detail);
        }
    }
    
    private void addConnection(final ConnectionDetail detail) {
        this.allConns.put(detail, "");
    }
    
    void removeConnectionDetail(final ConnectionDetail detail) {
        if (detail.hasReturned.compareAndSet(false, true)) {
            final String value = this.allConns.remove(detail);
            if (value == null) {
                return;
            }
            ConnectionInfoFactory.removeConnectionInfo(detail.physicalConnection);
            ConnectionInfoFactory.removeConnectionDetail(detail.physicalConnection);
            this.freeConns.releasePermit();
            ConnectionPool.LOGGER.log(Level.FINE, "DEBUG :: ConnectionDetail :: {0} :: removeConnectionDetail", detail);
            this.log(Level.INFO, "Connection detail removed from the pool due to error" + detail, new Object[0]);
        }
    }
    
    public void setMaxConnections(final int newMaxConnections) throws Exception {
        if (newMaxConnections >= this.maxConnections) {
            this.freeConns.increaseCapacity(newMaxConnections - this.maxConnections);
        }
        else {
            this.reduceConnectionsInPool(newMaxConnections);
        }
        this.log(Level.SEVERE, "MaxConnections in the connection pool is changed from {0} to {1}", this.maxConnections, newMaxConnections);
        this.maxConnections = newMaxConnections;
    }
    
    private void reduceConnectionsInPool(final int newMaxConnections) throws Exception {
        for (int connectionsToBeReduced = this.maxConnections - newMaxConnections; connectionsToBeReduced > 0; --connectionsToBeReduced) {
            final ConnectionDetail detail = this._getConnectionDetail(false);
            if (detail != null) {
                this.allConns.remove(detail);
                detail.safeDestroy();
            }
        }
    }
    
    public Map<ConnectionDetail, String> getAllConnections() {
        return this.allConns;
    }
    
    public Metrics<ConnectionPoolStats> getStats() {
        final Metrics<ConnectionPoolStats> metrics = new Metrics<ConnectionPoolStats>(ConnectionPoolStats.class);
        metrics.set(ConnectionPoolStats.max_connections, this.maxConnections);
        for (final ConnectionDetail conn : this.allConns.keySet()) {
            if (!conn.hasReturned.get()) {
                metrics.inc(ConnectionPoolStats.used_connections);
            }
            else {
                metrics.inc(ConnectionPoolStats.unused_connections);
            }
        }
        metrics.set(ConnectionPoolStats.free_connections, metrics.get(ConnectionPoolStats.max_connections) - metrics.get(ConnectionPoolStats.used_connections));
        return metrics;
    }
    
    public void removeTimedOutConnection() {
        try {
            while (true) {
                final ConnectionDetail detail = this.freeConns.acquirePermitAndPollFirstElement();
                if (detail == null) {
                    this.freeConns.releasePermit();
                    this.log(Level.FINE, "No idle connection found.semaphore permit released.Will retry after some time", new Object[0]);
                    return;
                }
                ConnectionInfoFactory.addConnectionDetail(detail.physicalConnection, detail);
                final boolean canBeRemoved = detail.getUsageCounter() + TimeUnit.SECONDS.toMinutes(this.maxIdleTime) / IdleConnectionRemover.connectionRemoverThreadinterval <= IdleConnectionRemover.getThreadTurnCounter();
                if (!canBeRemoved) {
                    detail.stackTrace = "";
                    ConnectionInfoFactory.removeConnectionDetail(detail.physicalConnection);
                    this.freeConns.addElementAtFirstAndReleasePermit(detail);
                    return;
                }
                detail.safeDestroy();
                this.allConns.remove(detail);
                ConnectionInfoFactory.removeConnectionDetail(detail.physicalConnection);
                this.freeConns.releasePermit();
                ConnectionPool.LOGGER.log(Level.FINE, "DEBUG :: ConnectionDetail :: {0} :: removeConnectionDetail", detail);
                this.log(Level.INFO, "Idle Connection Removed from the pool: " + detail + " Idle for more than " + (IdleConnectionRemover.getThreadTurnCounter() - detail.getUsageCounter()) * IdleConnectionRemover.connectionRemoverThreadinterval + "[mins]", new Object[0]);
            }
        }
        catch (final NoPermitsAvailableException exc) {
            this.log(Level.INFO, "All the Connections in pool seems busy.So no need to remove idle connection.Will retry after some time", new Object[0]);
        }
    }
    
    public void flush() {
        final List<ConnectionDetail> detailList = this.freeConns.removeAndReturnAllElements();
        for (final ConnectionDetail detail : detailList) {
            detail.safeDestroy();
            this.allConns.remove(detail);
        }
        this.log(Level.INFO, "All Connections in the pool is flushed due to connection error", new Object[0]);
    }
    
    public void abortAllConnections() {
        this.abortAllConnections(false);
    }
    
    public void abortAllConnections(final boolean forceAbort) {
        if (!this.creator.isMysql()) {
            return;
        }
        int noOfConnectionsAborted = 0;
        Long newServerId = 0L;
        try {
            newServerId = (forceAbort ? 0L : this.creator.createNewConnAndFetchServerId());
            this.log(Level.INFO, "Server Id got from the new cluster is" + newServerId, new Object[0]);
            final Enumeration<ConnectionDetail> connList = this.allConns.keys();
            while (connList.hasMoreElements()) {
                try {
                    final ConnectionDetail detail = connList.nextElement();
                    if (!forceAbort && detail.getServerId().equals(newServerId)) {
                        continue;
                    }
                    this.log(Level.INFO, "Server Id got from existing connection is" + detail.getServerId() + ". Stale Connection found.So Aborting..", new Object[0]);
                    final Connection conn = detail.physicalConnection;
                    conn.abort(ConnectionPool.executors);
                    detail.setIsValid(false);
                    ++noOfConnectionsAborted;
                    detail.returnToPool();
                }
                catch (final SQLException exc) {
                    this.log(Level.INFO, "Exception occurred while aborting connections in the pool", exc);
                }
            }
            ConnectionPool.LOGGER.info("Stale connections for the clusterIp:" + this.creator.getDBIp() + " were aborted due to cluster Failure.No of connections aborted : " + noOfConnectionsAborted);
        }
        catch (final Exception exc2) {
            ConnectionPool.LOGGER.log(Level.INFO, "Exception while aborting connections", exc2);
        }
        this.log(Level.INFO, "no of connections  aborted : " + noOfConnectionsAborted, new Object[0]);
    }
    
    public void handleException(final SQLException exc, final ConnectionDetail detail) throws SQLException {
        this.creator.handleException(exc, detail);
    }
    
    public ObjectStack<ConnectionDetail> getFreeConns() {
        return this.freeConns;
    }
    
    private void log(final Level level, final String message, final Object... params) {
        ConnectionPool.LOGGER.log(level, "ClusterIP:" + this.creator.getDBIp() + " " + message, params);
    }
    
    public void setActive(final boolean isActive) {
        this.active = isActive;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    static {
        LOGGER = Logger.getLogger(ConnectionPool.class.getName());
        (ConnectionPool.executors = new ThreadPoolExecutor(5, 5, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(1000))).allowCoreThreadTimeOut(true);
    }
    
    public enum ConnectionPoolStats
    {
        used_connections, 
        free_connections, 
        max_connections, 
        unused_connections;
    }
}
