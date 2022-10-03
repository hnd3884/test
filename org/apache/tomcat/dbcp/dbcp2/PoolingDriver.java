package org.apache.tomcat.dbcp.dbcp2;

import java.sql.DriverManager;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.sql.SQLException;
import java.sql.Connection;
import org.apache.tomcat.dbcp.pool2.ObjectPool;
import java.util.HashMap;
import java.sql.DriverPropertyInfo;
import java.sql.Driver;

public class PoolingDriver implements Driver
{
    private static final DriverPropertyInfo[] EMPTY_DRIVER_PROPERTY_INFO_ARRAY;
    protected static final HashMap<String, ObjectPool<? extends Connection>> pools;
    private final boolean accessToUnderlyingConnectionAllowed;
    public static final String URL_PREFIX = "jdbc:apache:commons:dbcp:";
    protected static final int URL_PREFIX_LEN;
    protected static final int MAJOR_VERSION = 1;
    protected static final int MINOR_VERSION = 0;
    
    public PoolingDriver() {
        this(true);
    }
    
    protected PoolingDriver(final boolean accessToUnderlyingConnectionAllowed) {
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
    }
    
    protected boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }
    
    public synchronized ObjectPool<? extends Connection> getConnectionPool(final String name) throws SQLException {
        final ObjectPool<? extends Connection> pool = PoolingDriver.pools.get(name);
        if (null == pool) {
            throw new SQLException("Pool not registered: " + name);
        }
        return pool;
    }
    
    public synchronized void registerPool(final String name, final ObjectPool<? extends Connection> pool) {
        PoolingDriver.pools.put(name, pool);
    }
    
    public synchronized void closePool(final String name) throws SQLException {
        final ObjectPool<? extends Connection> pool = PoolingDriver.pools.get(name);
        if (pool != null) {
            PoolingDriver.pools.remove(name);
            try {
                pool.close();
            }
            catch (final Exception e) {
                throw new SQLException("Error closing pool " + name, e);
            }
        }
    }
    
    public synchronized String[] getPoolNames() {
        return PoolingDriver.pools.keySet().toArray(Utils.EMPTY_STRING_ARRAY);
    }
    
    @Override
    public boolean acceptsURL(final String url) throws SQLException {
        return url != null && url.startsWith("jdbc:apache:commons:dbcp:");
    }
    
    @Override
    public Connection connect(final String url, final Properties info) throws SQLException {
        if (this.acceptsURL(url)) {
            final ObjectPool<? extends Connection> pool = this.getConnectionPool(url.substring(PoolingDriver.URL_PREFIX_LEN));
            try {
                final Connection conn = (Connection)pool.borrowObject();
                if (conn == null) {
                    return null;
                }
                return new PoolGuardConnectionWrapper(pool, conn);
            }
            catch (final NoSuchElementException e) {
                throw new SQLException("Cannot get a connection, pool error: " + e.getMessage(), e);
            }
            catch (final SQLException | RuntimeException e2) {
                throw e2;
            }
            catch (final Exception e2) {
                throw new SQLException("Cannot get a connection, general error: " + e2.getMessage(), e2);
            }
        }
        return null;
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public void invalidateConnection(final Connection conn) throws SQLException {
        if (!(conn instanceof PoolGuardConnectionWrapper)) {
            throw new SQLException("Invalid connection class");
        }
        final PoolGuardConnectionWrapper pgconn = (PoolGuardConnectionWrapper)conn;
        final ObjectPool<Connection> pool = (ObjectPool<Connection>)pgconn.pool;
        try {
            pool.invalidateObject(pgconn.getDelegateInternal());
        }
        catch (final Exception ex) {}
    }
    
    @Override
    public int getMajorVersion() {
        return 1;
    }
    
    @Override
    public int getMinorVersion() {
        return 0;
    }
    
    @Override
    public boolean jdbcCompliant() {
        return true;
    }
    
    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String url, final Properties info) {
        return PoolingDriver.EMPTY_DRIVER_PROPERTY_INFO_ARRAY;
    }
    
    static {
        EMPTY_DRIVER_PROPERTY_INFO_ARRAY = new DriverPropertyInfo[0];
        try {
            DriverManager.registerDriver(new PoolingDriver());
        }
        catch (final Exception ex) {}
        pools = new HashMap<String, ObjectPool<? extends Connection>>();
        URL_PREFIX_LEN = "jdbc:apache:commons:dbcp:".length();
    }
    
    private class PoolGuardConnectionWrapper extends DelegatingConnection<Connection>
    {
        private final ObjectPool<? extends Connection> pool;
        
        PoolGuardConnectionWrapper(final ObjectPool<? extends Connection> pool, final Connection delegate) {
            super(delegate);
            this.pool = pool;
        }
        
        @Override
        public Connection getDelegate() {
            if (PoolingDriver.this.isAccessToUnderlyingConnectionAllowed()) {
                return super.getDelegate();
            }
            return null;
        }
        
        @Override
        public Connection getInnermostDelegate() {
            if (PoolingDriver.this.isAccessToUnderlyingConnectionAllowed()) {
                return super.getInnermostDelegate();
            }
            return null;
        }
    }
}
