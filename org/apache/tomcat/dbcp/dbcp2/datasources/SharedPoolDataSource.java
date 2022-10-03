package org.apache.tomcat.dbcp.dbcp2.datasources;

import java.io.IOException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import java.io.ObjectInputStream;
import java.sql.Connection;
import javax.sql.ConnectionPoolDataSource;
import org.apache.tomcat.dbcp.pool2.KeyedPooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPoolConfig;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;
import javax.naming.Reference;
import javax.naming.NamingException;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.pool2.KeyedObjectPool;

public class SharedPoolDataSource extends InstanceKeyDataSource
{
    private static final long serialVersionUID = -1458539734480586454L;
    private int maxTotal;
    private transient KeyedObjectPool<UserPassKey, PooledConnectionAndInfo> pool;
    private transient KeyedCPDSConnectionFactory factory;
    
    public SharedPoolDataSource() {
        this.maxTotal = -1;
    }
    
    @Override
    public void close() throws Exception {
        if (this.pool != null) {
            this.pool.close();
        }
        InstanceKeyDataSourceFactory.removeInstance(this.getInstanceKey());
    }
    
    public int getMaxTotal() {
        return this.maxTotal;
    }
    
    public void setMaxTotal(final int maxTotal) {
        this.assertInitializationAllowed();
        this.maxTotal = maxTotal;
    }
    
    public int getNumActive() {
        return (this.pool == null) ? 0 : this.pool.getNumActive();
    }
    
    public int getNumIdle() {
        return (this.pool == null) ? 0 : this.pool.getNumIdle();
    }
    
    @Override
    protected PooledConnectionAndInfo getPooledConnectionAndInfo(final String userName, final String userPassword) throws SQLException {
        synchronized (this) {
            if (this.pool == null) {
                try {
                    this.registerPool(userName, userPassword);
                }
                catch (final NamingException e) {
                    throw new SQLException("RegisterPool failed", e);
                }
            }
        }
        PooledConnectionAndInfo info = null;
        final UserPassKey key = new UserPassKey(userName, userPassword);
        try {
            info = this.pool.borrowObject(key);
        }
        catch (final Exception e2) {
            throw new SQLException("Could not retrieve connection info from pool", e2);
        }
        return info;
    }
    
    @Override
    protected PooledConnectionManager getConnectionManager(final UserPassKey upkey) {
        return this.factory;
    }
    
    @Override
    public Reference getReference() throws NamingException {
        final Reference ref = new Reference(this.getClass().getName(), SharedPoolDataSourceFactory.class.getName(), null);
        ref.add(new StringRefAddr("instanceKey", this.getInstanceKey()));
        return ref;
    }
    
    private void registerPool(final String userName, final String password) throws NamingException, SQLException {
        final ConnectionPoolDataSource cpds = this.testCPDS(userName, password);
        (this.factory = new KeyedCPDSConnectionFactory(cpds, this.getValidationQuery(), this.getValidationQueryTimeout(), this.isRollbackAfterValidation())).setMaxConnLifetimeMillis(this.getMaxConnLifetimeMillis());
        final GenericKeyedObjectPoolConfig<PooledConnectionAndInfo> config = new GenericKeyedObjectPoolConfig<PooledConnectionAndInfo>();
        config.setBlockWhenExhausted(this.getDefaultBlockWhenExhausted());
        config.setEvictionPolicyClassName(this.getDefaultEvictionPolicyClassName());
        config.setLifo(this.getDefaultLifo());
        config.setMaxIdlePerKey(this.getDefaultMaxIdle());
        config.setMaxTotal(this.getMaxTotal());
        config.setMaxTotalPerKey(this.getDefaultMaxTotal());
        config.setMaxWaitMillis(this.getDefaultMaxWaitMillis());
        config.setMinEvictableIdleTimeMillis(this.getDefaultMinEvictableIdleTimeMillis());
        config.setMinIdlePerKey(this.getDefaultMinIdle());
        config.setNumTestsPerEvictionRun(this.getDefaultNumTestsPerEvictionRun());
        config.setSoftMinEvictableIdleTimeMillis(this.getDefaultSoftMinEvictableIdleTimeMillis());
        config.setTestOnCreate(this.getDefaultTestOnCreate());
        config.setTestOnBorrow(this.getDefaultTestOnBorrow());
        config.setTestOnReturn(this.getDefaultTestOnReturn());
        config.setTestWhileIdle(this.getDefaultTestWhileIdle());
        config.setTimeBetweenEvictionRunsMillis(this.getDefaultTimeBetweenEvictionRunsMillis());
        final KeyedObjectPool<UserPassKey, PooledConnectionAndInfo> tmpPool = new GenericKeyedObjectPool<UserPassKey, PooledConnectionAndInfo>(this.factory, config);
        this.factory.setPool(tmpPool);
        this.pool = tmpPool;
    }
    
    @Override
    protected void setupDefaults(final Connection connection, final String userName) throws SQLException {
        final Boolean defaultAutoCommit = this.isDefaultAutoCommit();
        if (defaultAutoCommit != null && connection.getAutoCommit() != defaultAutoCommit) {
            connection.setAutoCommit(defaultAutoCommit);
        }
        final int defaultTransactionIsolation = this.getDefaultTransactionIsolation();
        if (defaultTransactionIsolation != -1) {
            connection.setTransactionIsolation(defaultTransactionIsolation);
        }
        final Boolean defaultReadOnly = this.isDefaultReadOnly();
        if (defaultReadOnly != null && connection.isReadOnly() != defaultReadOnly) {
            connection.setReadOnly(defaultReadOnly);
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            in.defaultReadObject();
            final SharedPoolDataSource oldDS = (SharedPoolDataSource)new SharedPoolDataSourceFactory().getObjectInstance(this.getReference(), null, null, null);
            this.pool = oldDS.pool;
        }
        catch (final NamingException e) {
            throw new IOException("NamingException: " + e);
        }
    }
    
    @Override
    protected void toStringFields(final StringBuilder builder) {
        super.toStringFields(builder);
        builder.append(", maxTotal=");
        builder.append(this.maxTotal);
    }
}
