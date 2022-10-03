package org.apache.tomcat.dbcp.dbcp2;

import org.apache.juli.logging.LogFactory;
import java.util.NoSuchElementException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.pool2.impl.GenericObjectPool;
import java.util.Objects;
import org.apache.tomcat.dbcp.pool2.ObjectPool;
import java.io.PrintWriter;
import org.apache.juli.logging.Log;
import javax.sql.DataSource;
import java.sql.Connection;

public class PoolingDataSource<C extends Connection> implements DataSource, AutoCloseable
{
    private static final Log log;
    private boolean accessToUnderlyingConnectionAllowed;
    private PrintWriter logWriter;
    private final ObjectPool<C> pool;
    
    public PoolingDataSource(final ObjectPool<C> pool) {
        Objects.requireNonNull(pool, "Pool must not be null.");
        this.pool = pool;
        if (this.pool instanceof GenericObjectPool) {
            final PoolableConnectionFactory pcf = (PoolableConnectionFactory)((GenericObjectPool)this.pool).getFactory();
            Objects.requireNonNull(pcf, "PoolableConnectionFactory must not be null.");
            if (pcf.getPool() != this.pool) {
                PoolingDataSource.log.warn((Object)Utils.getMessage("poolingDataSource.factoryConfig"));
                final ObjectPool<PoolableConnection> p = (ObjectPool<PoolableConnection>)this.pool;
                pcf.setPool(p);
            }
        }
    }
    
    @Override
    public void close() throws RuntimeException, SQLException {
        try {
            this.pool.close();
        }
        catch (final RuntimeException rte) {
            throw new RuntimeException(Utils.getMessage("pool.close.fail"), rte);
        }
        catch (final Exception e) {
            throw new SQLException(Utils.getMessage("pool.close.fail"), e);
        }
    }
    
    public boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }
    
    public void setAccessToUnderlyingConnectionAllowed(final boolean allow) {
        this.accessToUnderlyingConnectionAllowed = allow;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return iface != null && iface.isInstance(this);
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (this.isWrapperFor(iface)) {
            return iface.cast(this);
        }
        throw new SQLException(this + " is not a wrapper for " + iface);
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        try {
            final C conn = this.pool.borrowObject();
            if (conn == null) {
                return null;
            }
            return new PoolGuardConnectionWrapper<Object>(conn);
        }
        catch (final NoSuchElementException e) {
            throw new SQLException("Cannot get a connection, pool error " + e.getMessage(), e);
        }
        catch (final SQLException | RuntimeException e2) {
            throw e2;
        }
        catch (final InterruptedException e3) {
            Thread.currentThread().interrupt();
            throw new SQLException("Cannot get a connection, general error", e3);
        }
        catch (final Exception e2) {
            throw new SQLException("Cannot get a connection, general error", e2);
        }
    }
    
    @Override
    public Connection getConnection(final String uname, final String passwd) throws SQLException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public PrintWriter getLogWriter() {
        return this.logWriter;
    }
    
    @Override
    public int getLoginTimeout() {
        throw new UnsupportedOperationException("Login timeout is not supported.");
    }
    
    @Override
    public void setLoginTimeout(final int seconds) {
        throw new UnsupportedOperationException("Login timeout is not supported.");
    }
    
    @Override
    public void setLogWriter(final PrintWriter out) {
        this.logWriter = out;
    }
    
    protected ObjectPool<C> getPool() {
        return this.pool;
    }
    
    static {
        log = LogFactory.getLog((Class)PoolingDataSource.class);
    }
    
    private class PoolGuardConnectionWrapper<D extends Connection> extends DelegatingConnection<D>
    {
        PoolGuardConnectionWrapper(final D delegate) {
            super(delegate);
        }
        
        @Override
        public D getDelegate() {
            return (D)(PoolingDataSource.this.isAccessToUnderlyingConnectionAllowed() ? super.getDelegate() : null);
        }
        
        @Override
        public Connection getInnermostDelegate() {
            return PoolingDataSource.this.isAccessToUnderlyingConnectionAllowed() ? super.getInnermostDelegate() : null;
        }
        
        @Override
        public void close() throws SQLException {
            if (this.getDelegateInternal() != null) {
                super.close();
                super.setDelegate(null);
            }
        }
        
        @Override
        public boolean isClosed() throws SQLException {
            return this.getDelegateInternal() == null || super.isClosed();
        }
    }
}
