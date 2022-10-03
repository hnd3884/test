package com.zoho.mickey.cp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import com.zoho.metrics.Metrics;
import java.sql.SQLException;
import com.zoho.mickey.ExceptionUtils;
import java.util.logging.Level;
import com.zoho.cp.ConnectionPool;
import java.util.logging.Logger;
import com.zoho.cp.ConnectionDetail;
import com.zoho.cp.ConnectionCreator;
import java.sql.Connection;
import java.util.Map;

public class ConnectionInfoFactory
{
    private static Map<Connection, ConnectionCreator.ConnectionInfo> physicalConnectionsVsConnectionInfo;
    private static Map<Connection, ConnectionDetail> physicalConnectionVsConnectionDetail;
    private static final Logger GENERAL_LOGGER;
    private static final Logger CONNECTION_DUMP_LOGGER;
    private static final Logger DUPLICATE_CONNECTION_LOGGER;
    private static final Logger CONNECTION_MISSING_LOGGER;
    private static final Logger CONNECTION_DETAIL_LOGGER;
    private static final Logger CONNECTION_INFO_LOGGER;
    private static final boolean TRACKING;
    public static final boolean LOG_CONNECTION_INFO;
    public static final boolean LOG_CONNECTION_DETAIL;
    private static ConnectionPool connectionPool;
    
    public static void setConnectionPool(final ConnectionPool connectionPool) {
        if (connectionPool == null) {
            throw new NullPointerException("Given ConnectionPool object is null");
        }
        if (!connectionPool.isActive()) {
            throw new IllegalStateException("Given ConnectionPool is inactive");
        }
        if (ConnectionInfoFactory.connectionPool == null) {
            ConnectionInfoFactory.connectionPool = connectionPool;
        }
        else if (!ConnectionInfoFactory.connectionPool.isActive()) {
            ConnectionInfoFactory.GENERAL_LOGGER.log(Level.WARNING, "Replacing the connection pool object as old one is invalid. Invoking thread :: {0}", ExceptionUtils.getStackTraceForCurrentThread());
            ConnectionInfoFactory.connectionPool = connectionPool;
        }
        else {
            ConnectionInfoFactory.GENERAL_LOGGER.log(Level.WARNING, "Someone is trying to modify ConnectionPool object. Invoking thread :: {0}", ExceptionUtils.getStackTraceForCurrentThread());
        }
    }
    
    public static boolean isTracking() {
        return ConnectionInfoFactory.TRACKING;
    }
    
    public static void addConnectionInfo(final Connection connection, final ConnectionCreator.ConnectionInfo connectionInfo) {
        if (ConnectionInfoFactory.TRACKING) {
            if (!ConnectionInfoFactory.connectionPool.isActive()) {
                final RuntimeException exception = new RuntimeException("ConnectionPool is inactive. But there are incoming connections.");
                ConnectionInfoFactory.GENERAL_LOGGER.log(Level.SEVERE, "ConnectionPool is inactive", exception);
                throw exception;
            }
            if (ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.get(connection) != null) {
                ConnectionInfoFactory.DUPLICATE_CONNECTION_LOGGER.log(Level.SEVERE, "Old PhysicalConnection :: {0}, ConnectionInfo :: {1}", new Object[] { connection, ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.get(connection) });
                ConnectionInfoFactory.DUPLICATE_CONNECTION_LOGGER.log(Level.SEVERE, "New PhysicalConnection :: {0}, ConnectionInfo :: {1}", new Object[] { connection, connectionInfo });
                ConnectionInfoFactory.DUPLICATE_CONNECTION_LOGGER.log(Level.WARNING, ExceptionUtils.getStackTraceForCurrentThread());
                ConnectionInfoFactory.DUPLICATE_CONNECTION_LOGGER.log(Level.SEVERE, "--------------------------------------------------------------");
            }
            if (ConnectionInfoFactory.LOG_CONNECTION_INFO) {
                ConnectionInfoFactory.CONNECTION_INFO_LOGGER.log(Level.FINER, "ConnectionInfoFactory.addConnectionInfo :: {0}", connectionInfo);
            }
            ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.put(connection, connectionInfo);
        }
    }
    
    public static ConnectionCreator.ConnectionInfo removeConnectionInfo(final Connection physicalConnection) {
        if (ConnectionInfoFactory.TRACKING) {
            if (ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.get(physicalConnection) == null) {
                ConnectionInfoFactory.CONNECTION_MISSING_LOGGER.log(Level.SEVERE, "PhysicalConnection :: {0} in ConnectionMap", physicalConnection);
                ConnectionInfoFactory.CONNECTION_MISSING_LOGGER.log(Level.WARNING, ExceptionUtils.getStackTraceForCurrentThread());
            }
            final ConnectionCreator.ConnectionInfo connectionInfo = ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.remove(physicalConnection);
            if (ConnectionInfoFactory.LOG_CONNECTION_INFO) {
                ConnectionInfoFactory.CONNECTION_INFO_LOGGER.log(Level.FINER, "ConnectionInfoFactory.removeConnectionInfo :: {0}", connectionInfo);
            }
            return connectionInfo;
        }
        return null;
    }
    
    public static void dumpInUseConnections() {
        final Metrics<ConnectionPool.ConnectionPoolStats> connectionPoolStatsMetrics = ConnectionInfoFactory.connectionPool.getStats();
        ConnectionInfoFactory.CONNECTION_DUMP_LOGGER.log(Level.INFO, "Connection Pool Stats :: {0}", connectionPoolStatsMetrics);
        if (!ConnectionInfoFactory.TRACKING) {
            return;
        }
        final int permits = ConnectionInfoFactory.connectionPool.getFreeConns().availablePermits();
        ConnectionInfoFactory.CONNECTION_DUMP_LOGGER.log(Level.INFO, "Semaphore Permits :: {0}", permits);
        ConnectionInfoFactory.CONNECTION_DUMP_LOGGER.log(Level.INFO, "In use ConnectionInfo count :: {0}", ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.size());
        ConnectionInfoFactory.CONNECTION_DUMP_LOGGER.log(Level.INFO, "In use ConnectionDetail count :: {0}", ConnectionInfoFactory.physicalConnectionVsConnectionDetail.size());
        final int usedConnections = connectionPoolStatsMetrics.get(ConnectionPool.ConnectionPoolStats.used_connections);
        final int freeConnections = connectionPoolStatsMetrics.get(ConnectionPool.ConnectionPoolStats.free_connections);
        if (freeConnections != permits) {
            ConnectionInfoFactory.CONNECTION_DUMP_LOGGER.log(Level.WARNING, "Free Connections [{0}] mismatches with Permits [{1}].", new Object[] { freeConnections, permits });
        }
        if (usedConnections != ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.size()) {
            ConnectionInfoFactory.CONNECTION_DUMP_LOGGER.log(Level.WARNING, "In use connections traces [{0}] mismatches with ConnectionPoolStats [{1}].", new Object[] { ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.size(), usedConnections });
        }
        if (usedConnections != ConnectionInfoFactory.physicalConnectionVsConnectionDetail.size()) {
            ConnectionInfoFactory.CONNECTION_DUMP_LOGGER.log(Level.WARNING, "In use connections stack [{0}] mismatches with ConnectionPoolStats [{1}].", new Object[] { ConnectionInfoFactory.physicalConnectionVsConnectionDetail.size(), usedConnections });
        }
        if (ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.size() != ConnectionInfoFactory.physicalConnectionVsConnectionDetail.size()) {
            ConnectionInfoFactory.CONNECTION_DUMP_LOGGER.log(Level.WARNING, "In use connections count in traces [{0}] mismatches with stack [{1}].", new Object[] { ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.size(), ConnectionInfoFactory.physicalConnectionVsConnectionDetail.size() });
        }
        if (ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.size() > 0) {
            final Set<?> s = ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.keySet();
            for (final Connection c : s) {
                final ConnectionCreator.ConnectionInfo connInfo = ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.get(c);
                try {
                    connInfo.setClosedState(c.isClosed() ? ClosedState.CLOSED : ClosedState.OPEN);
                }
                catch (final SQLException sqle) {
                    connInfo.setClosedState(ClosedState.EXCEPTION_OCCURRED);
                }
            }
            ConnectionInfoFactory.CONNECTION_DUMP_LOGGER.warning("In use ConnectionInfo :: " + ConnectionInfoFactory.physicalConnectionsVsConnectionInfo);
        }
        if (ConnectionInfoFactory.physicalConnectionVsConnectionDetail.size() > 0) {
            ConnectionInfoFactory.CONNECTION_DUMP_LOGGER.warning("In use ConnectionDetails :: " + ConnectionInfoFactory.physicalConnectionVsConnectionDetail);
        }
        ConnectionInfoFactory.CONNECTION_DUMP_LOGGER.warning("All connections in Queue :: " + ConnectionInfoFactory.connectionPool.getFreeConns());
        ConnectionInfoFactory.CONNECTION_DUMP_LOGGER.warning("All connections in ConnectionPool :: " + ConnectionInfoFactory.connectionPool.getAllConnections());
    }
    
    public static int getInUseConnectionCount(final long timeInSeconds) {
        final Metrics<ConnectionPool.ConnectionPoolStats> connectionPoolStatsMetrics = ConnectionInfoFactory.connectionPool.getStats();
        if (!ConnectionInfoFactory.TRACKING) {
            return connectionPoolStatsMetrics.get(ConnectionPool.ConnectionPoolStats.used_connections);
        }
        int count = 0;
        final long timeInMilliSeconds = timeInSeconds * 1000L;
        final long currentTime = System.currentTimeMillis();
        final Set<?> s = ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.keySet();
        for (final Connection c : s) {
            final ConnectionCreator.ConnectionInfo connInfo = ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.get(c);
            try {
                connInfo.setClosedState(c.isClosed() ? ClosedState.CLOSED : ClosedState.OPEN);
            }
            catch (final SQLException sqle) {
                connInfo.setClosedState(ClosedState.EXCEPTION_OCCURRED);
            }
            final long creationTime = connInfo.getDateTime();
            if (creationTime <= currentTime - timeInMilliSeconds) {
                ++count;
            }
        }
        return count;
    }
    
    public static Map<String, ConnectionCreator.ConnectionInfo> getInUseConnectionInfo(final long timeInSeconds) {
        if (!ConnectionInfoFactory.TRACKING) {
            return Collections.emptyMap();
        }
        final long timeInMilliSeconds = timeInSeconds * 1000L;
        final long currentTime = System.currentTimeMillis();
        final Map<String, ConnectionCreator.ConnectionInfo> localPhysicalTraces = new HashMap<String, ConnectionCreator.ConnectionInfo>();
        final Set<?> s = ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.keySet();
        for (final Connection c : s) {
            final ConnectionCreator.ConnectionInfo connInfo = ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.get(c);
            try {
                connInfo.setClosedState(c.isClosed() ? ClosedState.CLOSED : ClosedState.OPEN);
            }
            catch (final SQLException sqle) {
                connInfo.setClosedState(ClosedState.EXCEPTION_OCCURRED);
            }
            final long creationTime = connInfo.getDateTime();
            if (creationTime <= currentTime - timeInMilliSeconds) {
                localPhysicalTraces.put(c.toString(), ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.get(c));
            }
        }
        return localPhysicalTraces;
    }
    
    public static ConnectionCreator.ConnectionInfo getConnectionInfo(final Connection conn) {
        return ConnectionInfoFactory.physicalConnectionsVsConnectionInfo.get(conn);
    }
    
    public static void addConnectionDetail(final Connection connection, final ConnectionDetail connectionDetail) {
        if (ConnectionInfoFactory.TRACKING) {
            if (!ConnectionInfoFactory.connectionPool.isActive()) {
                final RuntimeException exception = new RuntimeException("ConnectionPool is inactive. But there are incoming connections.");
                ConnectionInfoFactory.GENERAL_LOGGER.log(Level.SEVERE, "ConnectionPool is inactive", exception);
                throw exception;
            }
            if (ConnectionInfoFactory.physicalConnectionVsConnectionDetail.get(connection) != null) {
                ConnectionInfoFactory.DUPLICATE_CONNECTION_LOGGER.log(Level.SEVERE, "Old PhysicalConnection :: {0}, ConnectionDetail :: {1}", new Object[] { connection, ConnectionInfoFactory.physicalConnectionVsConnectionDetail.get(connection) });
                ConnectionInfoFactory.DUPLICATE_CONNECTION_LOGGER.log(Level.SEVERE, "New PhysicalConnection :: {0}, ConnectionDetail :: {1}", new Object[] { connection, connectionDetail });
                ConnectionInfoFactory.DUPLICATE_CONNECTION_LOGGER.log(Level.WARNING, ExceptionUtils.getStackTraceForCurrentThread());
                ConnectionInfoFactory.DUPLICATE_CONNECTION_LOGGER.log(Level.SEVERE, "--------------------------------------------------------------");
            }
            if (ConnectionInfoFactory.LOG_CONNECTION_DETAIL) {
                ConnectionInfoFactory.CONNECTION_DETAIL_LOGGER.log(Level.FINER, "ConnectionInfoFactory.addConnectionDetail :: {0}", connectionDetail);
            }
            ConnectionInfoFactory.physicalConnectionVsConnectionDetail.put(connection, connectionDetail);
        }
    }
    
    public static ConnectionDetail removeConnectionDetail(final Connection physicalConnection) {
        if (ConnectionInfoFactory.TRACKING) {
            if (ConnectionInfoFactory.physicalConnectionVsConnectionDetail.get(physicalConnection) == null) {
                ConnectionInfoFactory.CONNECTION_MISSING_LOGGER.log(Level.SEVERE, "PhysicalConnection :: {0} in ConnectionStack", physicalConnection);
                ConnectionInfoFactory.CONNECTION_MISSING_LOGGER.log(Level.WARNING, ExceptionUtils.getStackTraceForCurrentThread());
            }
            final ConnectionDetail connectionDetail = ConnectionInfoFactory.physicalConnectionVsConnectionDetail.remove(physicalConnection);
            if (ConnectionInfoFactory.LOG_CONNECTION_DETAIL) {
                ConnectionInfoFactory.CONNECTION_DETAIL_LOGGER.log(Level.FINER, "ConnectionInfoFactory.removeConnectionDetail :: {0}", connectionDetail);
            }
            return connectionDetail;
        }
        return null;
    }
    
    static {
        ConnectionInfoFactory.physicalConnectionsVsConnectionInfo = new ConcurrentHashMap<Connection, ConnectionCreator.ConnectionInfo>();
        ConnectionInfoFactory.physicalConnectionVsConnectionDetail = new ConcurrentHashMap<Connection, ConnectionDetail>();
        GENERAL_LOGGER = Logger.getLogger(ConnectionInfoFactory.class.getName());
        CONNECTION_DUMP_LOGGER = Logger.getLogger("ConnectionDump");
        DUPLICATE_CONNECTION_LOGGER = Logger.getLogger("DuplicateConnection");
        CONNECTION_MISSING_LOGGER = Logger.getLogger("MissingConnection");
        CONNECTION_DETAIL_LOGGER = Logger.getLogger("ConnectionDetailLogger");
        CONNECTION_INFO_LOGGER = Logger.getLogger("ConnectionInfoLogger");
        TRACKING = Boolean.parseBoolean(System.getProperty("connection.track", "false"));
        LOG_CONNECTION_INFO = Boolean.parseBoolean(System.getProperty("connection.track.conn.info", "false"));
        LOG_CONNECTION_DETAIL = Boolean.parseBoolean(System.getProperty("connection.track.conn.detail", "false"));
        ConnectionInfoFactory.connectionPool = null;
    }
}
