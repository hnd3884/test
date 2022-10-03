package org.apache.tomcat.dbcp.dbcp2.datasources;

import javax.sql.ConnectionEvent;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import org.apache.tomcat.dbcp.pool2.DestroyMode;
import org.apache.tomcat.dbcp.pool2.impl.DefaultPooledObject;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import javax.sql.PooledConnection;
import java.util.Set;
import org.apache.tomcat.dbcp.pool2.ObjectPool;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.ConnectionEventListener;
import org.apache.tomcat.dbcp.pool2.PooledObjectFactory;

class CPDSConnectionFactory implements PooledObjectFactory<PooledConnectionAndInfo>, ConnectionEventListener, PooledConnectionManager
{
    private static final String NO_KEY_MESSAGE = "close() was called on a Connection, but I have no record of the underlying PooledConnection.";
    private final ConnectionPoolDataSource cpds;
    private final String validationQuery;
    private final int validationQueryTimeoutSeconds;
    private final boolean rollbackAfterValidation;
    private ObjectPool<PooledConnectionAndInfo> pool;
    private final String userName;
    private char[] userPassword;
    private long maxConnLifetimeMillis;
    private final Set<PooledConnection> validatingSet;
    private final Map<PooledConnection, PooledConnectionAndInfo> pcMap;
    
    public CPDSConnectionFactory(final ConnectionPoolDataSource cpds, final String validationQuery, final int validationQueryTimeoutSeconds, final boolean rollbackAfterValidation, final String userName, final char[] userPassword) {
        this.maxConnLifetimeMillis = -1L;
        this.validatingSet = Collections.newSetFromMap(new ConcurrentHashMap<PooledConnection, Boolean>());
        this.pcMap = new ConcurrentHashMap<PooledConnection, PooledConnectionAndInfo>();
        this.cpds = cpds;
        this.validationQuery = validationQuery;
        this.validationQueryTimeoutSeconds = validationQueryTimeoutSeconds;
        this.userName = userName;
        this.userPassword = userPassword;
        this.rollbackAfterValidation = rollbackAfterValidation;
    }
    
    public CPDSConnectionFactory(final ConnectionPoolDataSource cpds, final String validationQuery, final int validationQueryTimeoutSeconds, final boolean rollbackAfterValidation, final String userName, final String userPassword) {
        this(cpds, validationQuery, validationQueryTimeoutSeconds, rollbackAfterValidation, userName, Utils.toCharArray(userPassword));
    }
    
    char[] getPasswordCharArray() {
        return this.userPassword;
    }
    
    public ObjectPool<PooledConnectionAndInfo> getPool() {
        return this.pool;
    }
    
    public void setPool(final ObjectPool<PooledConnectionAndInfo> pool) {
        this.pool = pool;
    }
    
    @Override
    public synchronized PooledObject<PooledConnectionAndInfo> makeObject() {
        PooledConnectionAndInfo pci;
        try {
            PooledConnection pc = null;
            if (this.userName == null) {
                pc = this.cpds.getPooledConnection();
            }
            else {
                pc = this.cpds.getPooledConnection(this.userName, Utils.toString(this.userPassword));
            }
            if (pc == null) {
                throw new IllegalStateException("Connection pool data source returned null from getPooledConnection");
            }
            pc.addConnectionEventListener(this);
            pci = new PooledConnectionAndInfo(pc, this.userName, this.userPassword);
            this.pcMap.put(pc, pci);
        }
        catch (final SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return new DefaultPooledObject<PooledConnectionAndInfo>(pci);
    }
    
    @Override
    public void destroyObject(final PooledObject<PooledConnectionAndInfo> p) throws Exception {
        this.doDestroyObject(p.getObject());
    }
    
    @Override
    public void destroyObject(final PooledObject<PooledConnectionAndInfo> p, final DestroyMode mode) throws Exception {
        this.destroyObject(p);
    }
    
    private void doDestroyObject(final PooledConnectionAndInfo pci) throws Exception {
        final PooledConnection pc = pci.getPooledConnection();
        pc.removeConnectionEventListener(this);
        this.pcMap.remove(pc);
        pc.close();
    }
    
    @Override
    public boolean validateObject(final PooledObject<PooledConnectionAndInfo> p) {
        try {
            this.validateLifetime(p);
        }
        catch (final Exception e) {
            return false;
        }
        boolean valid = false;
        final PooledConnection pconn = p.getObject().getPooledConnection();
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
    public void passivateObject(final PooledObject<PooledConnectionAndInfo> p) throws Exception {
        this.validateLifetime(p);
    }
    
    @Override
    public void activateObject(final PooledObject<PooledConnectionAndInfo> p) throws Exception {
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
                this.pool.returnObject(pci);
            }
            catch (final Exception e) {
                System.err.println("CLOSING DOWN CONNECTION AS IT COULD NOT BE RETURNED TO THE POOL");
                pc.removeConnectionEventListener(this);
                try {
                    this.doDestroyObject(pci);
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
        final PooledConnectionAndInfo pci = this.pcMap.get(pc);
        if (pci == null) {
            throw new IllegalStateException("close() was called on a Connection, but I have no record of the underlying PooledConnection.");
        }
        try {
            this.pool.invalidateObject(pci);
        }
        catch (final Exception e) {
            System.err.println("EXCEPTION WHILE DESTROYING OBJECT " + pci);
            e.printStackTrace();
        }
    }
    
    @Override
    public void invalidate(final PooledConnection pc) throws SQLException {
        final PooledConnectionAndInfo pci = this.pcMap.get(pc);
        if (pci == null) {
            throw new IllegalStateException("close() was called on a Connection, but I have no record of the underlying PooledConnection.");
        }
        try {
            this.pool.invalidateObject(pci);
            this.pool.close();
        }
        catch (final Exception ex) {
            throw new SQLException("Error invalidating connection", ex);
        }
    }
    
    public synchronized void setPassword(final char[] userPassword) {
        this.userPassword = Utils.clone(userPassword);
    }
    
    @Override
    public synchronized void setPassword(final String userPassword) {
        this.userPassword = Utils.toCharArray(userPassword);
    }
    
    public void setMaxConnLifetimeMillis(final long maxConnLifetimeMillis) {
        this.maxConnLifetimeMillis = maxConnLifetimeMillis;
    }
    
    @Override
    public void closePool(final String userName) throws SQLException {
        synchronized (this) {
            if (userName == null || !userName.equals(this.userName)) {
                return;
            }
        }
        try {
            this.pool.close();
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
    
    @Override
    public synchronized String toString() {
        final StringBuilder builder = new StringBuilder(super.toString());
        builder.append("[cpds=");
        builder.append(this.cpds);
        builder.append(", validationQuery=");
        builder.append(this.validationQuery);
        builder.append(", validationQueryTimeoutSeconds=");
        builder.append(this.validationQueryTimeoutSeconds);
        builder.append(", rollbackAfterValidation=");
        builder.append(this.rollbackAfterValidation);
        builder.append(", pool=");
        builder.append(this.pool);
        builder.append(", maxConnLifetimeMillis=");
        builder.append(this.maxConnLifetimeMillis);
        builder.append(", validatingSet=");
        builder.append(this.validatingSet);
        builder.append(", pcMap=");
        builder.append(this.pcMap);
        builder.append(']');
        return builder.toString();
    }
}
