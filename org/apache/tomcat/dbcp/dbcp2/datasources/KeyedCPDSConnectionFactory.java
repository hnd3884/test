package org.apache.tomcat.dbcp.dbcp2.datasources;

import javax.sql.ConnectionEvent;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.pool2.DestroyMode;
import org.apache.tomcat.dbcp.pool2.impl.DefaultPooledObject;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import javax.sql.PooledConnection;
import java.util.Set;
import org.apache.tomcat.dbcp.pool2.KeyedObjectPool;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.ConnectionEventListener;
import org.apache.tomcat.dbcp.pool2.KeyedPooledObjectFactory;

class KeyedCPDSConnectionFactory implements KeyedPooledObjectFactory<UserPassKey, PooledConnectionAndInfo>, ConnectionEventListener, PooledConnectionManager
{
    private static final String NO_KEY_MESSAGE = "close() was called on a Connection, but I have no record of the underlying PooledConnection.";
    private final ConnectionPoolDataSource cpds;
    private final String validationQuery;
    private final int validationQueryTimeoutSeconds;
    private final boolean rollbackAfterValidation;
    private KeyedObjectPool<UserPassKey, PooledConnectionAndInfo> pool;
    private long maxConnLifetimeMillis;
    private final Set<PooledConnection> validatingSet;
    private final Map<PooledConnection, PooledConnectionAndInfo> pcMap;
    
    public KeyedCPDSConnectionFactory(final ConnectionPoolDataSource cpds, final String validationQuery, final int validationQueryTimeoutSeconds, final boolean rollbackAfterValidation) {
        this.maxConnLifetimeMillis = -1L;
        this.validatingSet = Collections.newSetFromMap(new ConcurrentHashMap<PooledConnection, Boolean>());
        this.pcMap = new ConcurrentHashMap<PooledConnection, PooledConnectionAndInfo>();
        this.cpds = cpds;
        this.validationQuery = validationQuery;
        this.validationQueryTimeoutSeconds = validationQueryTimeoutSeconds;
        this.rollbackAfterValidation = rollbackAfterValidation;
    }
    
    public void setPool(final KeyedObjectPool<UserPassKey, PooledConnectionAndInfo> pool) {
        this.pool = pool;
    }
    
    public KeyedObjectPool<UserPassKey, PooledConnectionAndInfo> getPool() {
        return this.pool;
    }
    
    @Override
    public synchronized PooledObject<PooledConnectionAndInfo> makeObject(final UserPassKey upkey) throws Exception {
        PooledConnection pooledConnection = null;
        final String userName = upkey.getUserName();
        final String password = upkey.getPassword();
        if (userName == null) {
            pooledConnection = this.cpds.getPooledConnection();
        }
        else {
            pooledConnection = this.cpds.getPooledConnection(userName, password);
        }
        if (pooledConnection == null) {
            throw new IllegalStateException("Connection pool data source returned null from getPooledConnection");
        }
        pooledConnection.addConnectionEventListener(this);
        final PooledConnectionAndInfo pci = new PooledConnectionAndInfo(pooledConnection, userName, upkey.getPasswordCharArray());
        this.pcMap.put(pooledConnection, pci);
        return new DefaultPooledObject<PooledConnectionAndInfo>(pci);
    }
    
    @Override
    public void destroyObject(final UserPassKey key, final PooledObject<PooledConnectionAndInfo> p) throws Exception {
        final PooledConnection pooledConnection = p.getObject().getPooledConnection();
        pooledConnection.removeConnectionEventListener(this);
        this.pcMap.remove(pooledConnection);
        pooledConnection.close();
    }
    
    @Override
    public void destroyObject(final UserPassKey key, final PooledObject<PooledConnectionAndInfo> p, final DestroyMode mode) throws Exception {
        this.destroyObject(key, p);
    }
    
    @Override
    public boolean validateObject(final UserPassKey key, final PooledObject<PooledConnectionAndInfo> pooledObject) {
        try {
            this.validateLifetime(pooledObject);
        }
        catch (final Exception e) {
            return false;
        }
        boolean valid = false;
        final PooledConnection pconn = pooledObject.getObject().getPooledConnection();
        Connection conn = null;
        this.validatingSet.add(pconn);
        if (null == this.validationQuery) {
            int timeoutSeconds = this.validationQueryTimeoutSeconds;
            if (timeoutSeconds < 0) {
                timeoutSeconds = 0;
            }
            try {
                conn = pconn.getConnection();
                valid = conn.isValid(timeoutSeconds);
            }
            catch (final SQLException e2) {
                valid = false;
            }
            finally {
                Utils.closeQuietly(conn);
                this.validatingSet.remove(pconn);
            }
        }
        else {
            Statement stmt = null;
            ResultSet rset = null;
            this.validatingSet.add(pconn);
            try {
                conn = pconn.getConnection();
                stmt = conn.createStatement();
                rset = stmt.executeQuery(this.validationQuery);
                valid = rset.next();
                if (this.rollbackAfterValidation) {
                    conn.rollback();
                }
            }
            catch (final Exception e3) {
                valid = false;
            }
            finally {
                Utils.closeQuietly(rset);
                Utils.closeQuietly(stmt);
                Utils.closeQuietly(conn);
                this.validatingSet.remove(pconn);
            }
        }
        return valid;
    }
    
    @Override
    public void passivateObject(final UserPassKey key, final PooledObject<PooledConnectionAndInfo> p) throws Exception {
        this.validateLifetime(p);
    }
    
    @Override
    public void activateObject(final UserPassKey key, final PooledObject<PooledConnectionAndInfo> p) throws Exception {
        this.validateLifetime(p);
    }
    
    @Override
    public void connectionClosed(final ConnectionEvent event) {
        final PooledConnection pc = (PooledConnection)event.getSource();
        if (!this.validatingSet.contains(pc)) {
            final PooledConnectionAndInfo pci = this.pcMap.get(pc);
            if (pci == null) {
                throw new IllegalStateException("close() was called on a Connection, but I have no record of the underlying PooledConnection.");
            }
            try {
                this.pool.returnObject(pci.getUserPassKey(), pci);
            }
            catch (final Exception e) {
                System.err.println("CLOSING DOWN CONNECTION AS IT COULD NOT BE RETURNED TO THE POOL");
                pc.removeConnectionEventListener(this);
                try {
                    this.pool.invalidateObject(pci.getUserPassKey(), pci);
                }
                catch (final Exception e2) {
                    System.err.println("EXCEPTION WHILE DESTROYING OBJECT " + pci);
                    e2.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public void connectionErrorOccurred(final ConnectionEvent event) {
        final PooledConnection pc = (PooledConnection)event.getSource();
        if (null != event.getSQLException()) {
            System.err.println("CLOSING DOWN CONNECTION DUE TO INTERNAL ERROR (" + event.getSQLException() + ")");
        }
        pc.removeConnectionEventListener(this);
        final PooledConnectionAndInfo info = this.pcMap.get(pc);
        if (info == null) {
            throw new IllegalStateException("close() was called on a Connection, but I have no record of the underlying PooledConnection.");
        }
        try {
            this.pool.invalidateObject(info.getUserPassKey(), info);
        }
        catch (final Exception e) {
            System.err.println("EXCEPTION WHILE DESTROYING OBJECT " + info);
            e.printStackTrace();
        }
    }
    
    @Override
    public void invalidate(final PooledConnection pc) throws SQLException {
        final PooledConnectionAndInfo info = this.pcMap.get(pc);
        if (info == null) {
            throw new IllegalStateException("close() was called on a Connection, but I have no record of the underlying PooledConnection.");
        }
        final UserPassKey key = info.getUserPassKey();
        try {
            this.pool.invalidateObject(key, info);
            this.pool.clear(key);
        }
        catch (final Exception ex) {
            throw new SQLException("Error invalidating connection", ex);
        }
    }
    
    @Override
    public void setPassword(final String password) {
    }
    
    public void setMaxConnLifetimeMillis(final long maxConnLifetimeMillis) {
        this.maxConnLifetimeMillis = maxConnLifetimeMillis;
    }
    
    @Override
    public void closePool(final String userName) throws SQLException {
        try {
            this.pool.clear(new UserPassKey(userName));
        }
        catch (final Exception ex) {
            throw new SQLException("Error closing connection pool", ex);
        }
    }
    
    private void validateLifetime(final PooledObject<PooledConnectionAndInfo> p) throws Exception {
        if (this.maxConnLifetimeMillis > 0L) {
            final long lifetimeMillis = System.currentTimeMillis() - p.getCreateTime();
            if (lifetimeMillis > this.maxConnLifetimeMillis) {
                throw new Exception(Utils.getMessage("connectionFactory.lifetimeExceeded", lifetimeMillis, this.maxConnLifetimeMillis));
            }
        }
    }
}
