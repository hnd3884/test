package org.apache.tomcat.dbcp.dbcp2.datasources;

import org.apache.juli.logging.LogFactory;
import java.sql.Connection;
import javax.sql.ConnectionPoolDataSource;
import org.apache.tomcat.dbcp.pool2.SwallowedExceptionListener;
import org.apache.tomcat.dbcp.dbcp2.SwallowedExceptionLogger;
import org.apache.tomcat.dbcp.pool2.PooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.impl.GenericObjectPool;
import java.io.IOException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import java.io.ObjectInputStream;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;
import javax.naming.Reference;
import java.util.NoSuchElementException;
import javax.naming.NamingException;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.pool2.ObjectPool;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.apache.juli.logging.Log;

public class PerUserPoolDataSource extends InstanceKeyDataSource
{
    private static final long serialVersionUID = 7872747993848065028L;
    private static final Log log;
    private Map<String, Boolean> perUserBlockWhenExhausted;
    private Map<String, String> perUserEvictionPolicyClassName;
    private Map<String, Boolean> perUserLifo;
    private Map<String, Integer> perUserMaxIdle;
    private Map<String, Integer> perUserMaxTotal;
    private Map<String, Long> perUserMaxWaitMillis;
    private Map<String, Long> perUserMinEvictableIdleTimeMillis;
    private Map<String, Integer> perUserMinIdle;
    private Map<String, Integer> perUserNumTestsPerEvictionRun;
    private Map<String, Long> perUserSoftMinEvictableIdleTimeMillis;
    private Map<String, Boolean> perUserTestOnCreate;
    private Map<String, Boolean> perUserTestOnBorrow;
    private Map<String, Boolean> perUserTestOnReturn;
    private Map<String, Boolean> perUserTestWhileIdle;
    private Map<String, Long> perUserTimeBetweenEvictionRunsMillis;
    private Map<String, Boolean> perUserDefaultAutoCommit;
    private Map<String, Integer> perUserDefaultTransactionIsolation;
    private Map<String, Boolean> perUserDefaultReadOnly;
    private transient Map<PoolKey, PooledConnectionManager> managers;
    
    public PerUserPoolDataSource() {
        this.managers = new HashMap<PoolKey, PooledConnectionManager>();
    }
    
    public void clear() {
        for (final PooledConnectionManager manager : this.managers.values()) {
            try {
                this.getCPDSConnectionFactoryPool(manager).clear();
            }
            catch (final Exception ex) {}
        }
        InstanceKeyDataSourceFactory.removeInstance(this.getInstanceKey());
    }
    
    @Override
    public void close() {
        for (final PooledConnectionManager manager : this.managers.values()) {
            try {
                this.getCPDSConnectionFactoryPool(manager).close();
            }
            catch (final Exception ex) {}
        }
        InstanceKeyDataSourceFactory.removeInstance(this.getInstanceKey());
    }
    
    private HashMap<String, Boolean> createMap() {
        return new HashMap<String, Boolean>();
    }
    
    @Override
    protected PooledConnectionManager getConnectionManager(final UserPassKey upKey) {
        return this.managers.get(this.getPoolKey(upKey.getUserName()));
    }
    
    private ObjectPool<PooledConnectionAndInfo> getCPDSConnectionFactoryPool(final PooledConnectionManager manager) {
        return ((CPDSConnectionFactory)manager).getPool();
    }
    
    public int getNumActive() {
        return this.getNumActive(null);
    }
    
    public int getNumActive(final String userName) {
        final ObjectPool<PooledConnectionAndInfo> pool = this.getPool(this.getPoolKey(userName));
        return (pool == null) ? 0 : pool.getNumActive();
    }
    
    public int getNumIdle() {
        return this.getNumIdle(null);
    }
    
    public int getNumIdle(final String userName) {
        final ObjectPool<PooledConnectionAndInfo> pool = this.getPool(this.getPoolKey(userName));
        return (pool == null) ? 0 : pool.getNumIdle();
    }
    
    public boolean getPerUserBlockWhenExhausted(final String userName) {
        Boolean value = null;
        if (this.perUserBlockWhenExhausted != null) {
            value = this.perUserBlockWhenExhausted.get(userName);
        }
        if (value == null) {
            return this.getDefaultBlockWhenExhausted();
        }
        return value;
    }
    
    public Boolean getPerUserDefaultAutoCommit(final String userName) {
        Boolean value = null;
        if (this.perUserDefaultAutoCommit != null) {
            value = this.perUserDefaultAutoCommit.get(userName);
        }
        return value;
    }
    
    public Boolean getPerUserDefaultReadOnly(final String userName) {
        Boolean value = null;
        if (this.perUserDefaultReadOnly != null) {
            value = this.perUserDefaultReadOnly.get(userName);
        }
        return value;
    }
    
    public Integer getPerUserDefaultTransactionIsolation(final String userName) {
        Integer value = null;
        if (this.perUserDefaultTransactionIsolation != null) {
            value = this.perUserDefaultTransactionIsolation.get(userName);
        }
        return value;
    }
    
    public String getPerUserEvictionPolicyClassName(final String userName) {
        String value = null;
        if (this.perUserEvictionPolicyClassName != null) {
            value = this.perUserEvictionPolicyClassName.get(userName);
        }
        if (value == null) {
            return this.getDefaultEvictionPolicyClassName();
        }
        return value;
    }
    
    public boolean getPerUserLifo(final String userName) {
        Boolean value = null;
        if (this.perUserLifo != null) {
            value = this.perUserLifo.get(userName);
        }
        if (value == null) {
            return this.getDefaultLifo();
        }
        return value;
    }
    
    public int getPerUserMaxIdle(final String userName) {
        Integer value = null;
        if (this.perUserMaxIdle != null) {
            value = this.perUserMaxIdle.get(userName);
        }
        if (value == null) {
            return this.getDefaultMaxIdle();
        }
        return value;
    }
    
    public int getPerUserMaxTotal(final String userName) {
        Integer value = null;
        if (this.perUserMaxTotal != null) {
            value = this.perUserMaxTotal.get(userName);
        }
        if (value == null) {
            return this.getDefaultMaxTotal();
        }
        return value;
    }
    
    public long getPerUserMaxWaitMillis(final String userName) {
        Long value = null;
        if (this.perUserMaxWaitMillis != null) {
            value = this.perUserMaxWaitMillis.get(userName);
        }
        if (value == null) {
            return this.getDefaultMaxWaitMillis();
        }
        return value;
    }
    
    public long getPerUserMinEvictableIdleTimeMillis(final String userName) {
        Long value = null;
        if (this.perUserMinEvictableIdleTimeMillis != null) {
            value = this.perUserMinEvictableIdleTimeMillis.get(userName);
        }
        if (value == null) {
            return this.getDefaultMinEvictableIdleTimeMillis();
        }
        return value;
    }
    
    public int getPerUserMinIdle(final String userName) {
        Integer value = null;
        if (this.perUserMinIdle != null) {
            value = this.perUserMinIdle.get(userName);
        }
        if (value == null) {
            return this.getDefaultMinIdle();
        }
        return value;
    }
    
    public int getPerUserNumTestsPerEvictionRun(final String userName) {
        Integer value = null;
        if (this.perUserNumTestsPerEvictionRun != null) {
            value = this.perUserNumTestsPerEvictionRun.get(userName);
        }
        if (value == null) {
            return this.getDefaultNumTestsPerEvictionRun();
        }
        return value;
    }
    
    public long getPerUserSoftMinEvictableIdleTimeMillis(final String userName) {
        Long value = null;
        if (this.perUserSoftMinEvictableIdleTimeMillis != null) {
            value = this.perUserSoftMinEvictableIdleTimeMillis.get(userName);
        }
        if (value == null) {
            return this.getDefaultSoftMinEvictableIdleTimeMillis();
        }
        return value;
    }
    
    public boolean getPerUserTestOnBorrow(final String userName) {
        Boolean value = null;
        if (this.perUserTestOnBorrow != null) {
            value = this.perUserTestOnBorrow.get(userName);
        }
        if (value == null) {
            return this.getDefaultTestOnBorrow();
        }
        return value;
    }
    
    public boolean getPerUserTestOnCreate(final String userName) {
        Boolean value = null;
        if (this.perUserTestOnCreate != null) {
            value = this.perUserTestOnCreate.get(userName);
        }
        if (value == null) {
            return this.getDefaultTestOnCreate();
        }
        return value;
    }
    
    public boolean getPerUserTestOnReturn(final String userName) {
        Boolean value = null;
        if (this.perUserTestOnReturn != null) {
            value = this.perUserTestOnReturn.get(userName);
        }
        if (value == null) {
            return this.getDefaultTestOnReturn();
        }
        return value;
    }
    
    public boolean getPerUserTestWhileIdle(final String userName) {
        Boolean value = null;
        if (this.perUserTestWhileIdle != null) {
            value = this.perUserTestWhileIdle.get(userName);
        }
        if (value == null) {
            return this.getDefaultTestWhileIdle();
        }
        return value;
    }
    
    public long getPerUserTimeBetweenEvictionRunsMillis(final String userName) {
        Long value = null;
        if (this.perUserTimeBetweenEvictionRunsMillis != null) {
            value = this.perUserTimeBetweenEvictionRunsMillis.get(userName);
        }
        if (value == null) {
            return this.getDefaultTimeBetweenEvictionRunsMillis();
        }
        return value;
    }
    
    private ObjectPool<PooledConnectionAndInfo> getPool(final PoolKey poolKey) {
        final CPDSConnectionFactory mgr = this.managers.get(poolKey);
        return (mgr == null) ? null : mgr.getPool();
    }
    
    @Override
    protected PooledConnectionAndInfo getPooledConnectionAndInfo(final String userName, final String password) throws SQLException {
        final PoolKey key = this.getPoolKey(userName);
        PooledConnectionManager manager;
        ObjectPool<PooledConnectionAndInfo> pool;
        synchronized (this) {
            manager = this.managers.get(key);
            if (manager == null) {
                try {
                    this.registerPool(userName, password);
                    manager = this.managers.get(key);
                }
                catch (final NamingException e) {
                    throw new SQLException("RegisterPool failed", e);
                }
            }
            pool = this.getCPDSConnectionFactoryPool(manager);
        }
        PooledConnectionAndInfo info = null;
        try {
            info = pool.borrowObject();
        }
        catch (final NoSuchElementException ex) {
            throw new SQLException("Could not retrieve connection info from pool", ex);
        }
        catch (final Exception e2) {
            try {
                this.testCPDS(userName, password);
            }
            catch (final Exception ex2) {
                throw new SQLException("Could not retrieve connection info from pool", ex2);
            }
            manager.closePool(userName);
            synchronized (this) {
                this.managers.remove(key);
            }
            try {
                this.registerPool(userName, password);
                pool = this.getPool(key);
            }
            catch (final NamingException ne) {
                throw new SQLException("RegisterPool failed", ne);
            }
            try {
                info = pool.borrowObject();
            }
            catch (final Exception ex2) {
                throw new SQLException("Could not retrieve connection info from pool", ex2);
            }
        }
        return info;
    }
    
    private PoolKey getPoolKey(final String userName) {
        return new PoolKey(this.getDataSourceName(), userName);
    }
    
    @Override
    public Reference getReference() throws NamingException {
        final Reference ref = new Reference(this.getClass().getName(), PerUserPoolDataSourceFactory.class.getName(), null);
        ref.add(new StringRefAddr("instanceKey", this.getInstanceKey()));
        return ref;
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        try {
            in.defaultReadObject();
            final PerUserPoolDataSource oldDS = (PerUserPoolDataSource)new PerUserPoolDataSourceFactory().getObjectInstance(this.getReference(), null, null, null);
            this.managers = oldDS.managers;
        }
        catch (final NamingException e) {
            throw new IOException("NamingException: " + e);
        }
    }
    
    private synchronized void registerPool(final String userName, final String password) throws NamingException, SQLException {
        final ConnectionPoolDataSource cpds = this.testCPDS(userName, password);
        final CPDSConnectionFactory factory = new CPDSConnectionFactory(cpds, this.getValidationQuery(), this.getValidationQueryTimeout(), this.isRollbackAfterValidation(), userName, password);
        factory.setMaxConnLifetimeMillis(this.getMaxConnLifetimeMillis());
        final GenericObjectPool<PooledConnectionAndInfo> pool = new GenericObjectPool<PooledConnectionAndInfo>(factory);
        factory.setPool(pool);
        pool.setBlockWhenExhausted(this.getPerUserBlockWhenExhausted(userName));
        pool.setEvictionPolicyClassName(this.getPerUserEvictionPolicyClassName(userName));
        pool.setLifo(this.getPerUserLifo(userName));
        pool.setMaxIdle(this.getPerUserMaxIdle(userName));
        pool.setMaxTotal(this.getPerUserMaxTotal(userName));
        pool.setMaxWaitMillis(this.getPerUserMaxWaitMillis(userName));
        pool.setMinEvictableIdleTimeMillis(this.getPerUserMinEvictableIdleTimeMillis(userName));
        pool.setMinIdle(this.getPerUserMinIdle(userName));
        pool.setNumTestsPerEvictionRun(this.getPerUserNumTestsPerEvictionRun(userName));
        pool.setSoftMinEvictableIdleTimeMillis(this.getPerUserSoftMinEvictableIdleTimeMillis(userName));
        pool.setTestOnCreate(this.getPerUserTestOnCreate(userName));
        pool.setTestOnBorrow(this.getPerUserTestOnBorrow(userName));
        pool.setTestOnReturn(this.getPerUserTestOnReturn(userName));
        pool.setTestWhileIdle(this.getPerUserTestWhileIdle(userName));
        pool.setTimeBetweenEvictionRunsMillis(this.getPerUserTimeBetweenEvictionRunsMillis(userName));
        pool.setSwallowedExceptionListener(new SwallowedExceptionLogger(PerUserPoolDataSource.log));
        final Object old = this.managers.put(this.getPoolKey(userName), factory);
        if (old != null) {
            throw new IllegalStateException("Pool already contains an entry for this user/password: " + userName);
        }
    }
    
    void setPerUserBlockWhenExhausted(final Map<String, Boolean> userDefaultBlockWhenExhausted) {
        this.assertInitializationAllowed();
        if (this.perUserBlockWhenExhausted == null) {
            this.perUserBlockWhenExhausted = this.createMap();
        }
        else {
            this.perUserBlockWhenExhausted.clear();
        }
        this.perUserBlockWhenExhausted.putAll(userDefaultBlockWhenExhausted);
    }
    
    public void setPerUserBlockWhenExhausted(final String userName, final Boolean value) {
        this.assertInitializationAllowed();
        if (this.perUserBlockWhenExhausted == null) {
            this.perUserBlockWhenExhausted = this.createMap();
        }
        this.perUserBlockWhenExhausted.put(userName, value);
    }
    
    void setPerUserDefaultAutoCommit(final Map<String, Boolean> userDefaultAutoCommit) {
        this.assertInitializationAllowed();
        if (this.perUserDefaultAutoCommit == null) {
            this.perUserDefaultAutoCommit = this.createMap();
        }
        else {
            this.perUserDefaultAutoCommit.clear();
        }
        this.perUserDefaultAutoCommit.putAll(userDefaultAutoCommit);
    }
    
    public void setPerUserDefaultAutoCommit(final String userName, final Boolean value) {
        this.assertInitializationAllowed();
        if (this.perUserDefaultAutoCommit == null) {
            this.perUserDefaultAutoCommit = this.createMap();
        }
        this.perUserDefaultAutoCommit.put(userName, value);
    }
    
    void setPerUserDefaultReadOnly(final Map<String, Boolean> userDefaultReadOnly) {
        this.assertInitializationAllowed();
        if (this.perUserDefaultReadOnly == null) {
            this.perUserDefaultReadOnly = this.createMap();
        }
        else {
            this.perUserDefaultReadOnly.clear();
        }
        this.perUserDefaultReadOnly.putAll(userDefaultReadOnly);
    }
    
    public void setPerUserDefaultReadOnly(final String userName, final Boolean value) {
        this.assertInitializationAllowed();
        if (this.perUserDefaultReadOnly == null) {
            this.perUserDefaultReadOnly = this.createMap();
        }
        this.perUserDefaultReadOnly.put(userName, value);
    }
    
    void setPerUserDefaultTransactionIsolation(final Map<String, Integer> userDefaultTransactionIsolation) {
        this.assertInitializationAllowed();
        if (this.perUserDefaultTransactionIsolation == null) {
            this.perUserDefaultTransactionIsolation = new HashMap<String, Integer>();
        }
        else {
            this.perUserDefaultTransactionIsolation.clear();
        }
        this.perUserDefaultTransactionIsolation.putAll(userDefaultTransactionIsolation);
    }
    
    public void setPerUserDefaultTransactionIsolation(final String userName, final Integer value) {
        this.assertInitializationAllowed();
        if (this.perUserDefaultTransactionIsolation == null) {
            this.perUserDefaultTransactionIsolation = new HashMap<String, Integer>();
        }
        this.perUserDefaultTransactionIsolation.put(userName, value);
    }
    
    void setPerUserEvictionPolicyClassName(final Map<String, String> userDefaultEvictionPolicyClassName) {
        this.assertInitializationAllowed();
        if (this.perUserEvictionPolicyClassName == null) {
            this.perUserEvictionPolicyClassName = new HashMap<String, String>();
        }
        else {
            this.perUserEvictionPolicyClassName.clear();
        }
        this.perUserEvictionPolicyClassName.putAll(userDefaultEvictionPolicyClassName);
    }
    
    public void setPerUserEvictionPolicyClassName(final String userName, final String value) {
        this.assertInitializationAllowed();
        if (this.perUserEvictionPolicyClassName == null) {
            this.perUserEvictionPolicyClassName = new HashMap<String, String>();
        }
        this.perUserEvictionPolicyClassName.put(userName, value);
    }
    
    void setPerUserLifo(final Map<String, Boolean> userDefaultLifo) {
        this.assertInitializationAllowed();
        if (this.perUserLifo == null) {
            this.perUserLifo = this.createMap();
        }
        else {
            this.perUserLifo.clear();
        }
        this.perUserLifo.putAll(userDefaultLifo);
    }
    
    public void setPerUserLifo(final String userName, final Boolean value) {
        this.assertInitializationAllowed();
        if (this.perUserLifo == null) {
            this.perUserLifo = this.createMap();
        }
        this.perUserLifo.put(userName, value);
    }
    
    void setPerUserMaxIdle(final Map<String, Integer> userDefaultMaxIdle) {
        this.assertInitializationAllowed();
        if (this.perUserMaxIdle == null) {
            this.perUserMaxIdle = new HashMap<String, Integer>();
        }
        else {
            this.perUserMaxIdle.clear();
        }
        this.perUserMaxIdle.putAll(userDefaultMaxIdle);
    }
    
    public void setPerUserMaxIdle(final String userName, final Integer value) {
        this.assertInitializationAllowed();
        if (this.perUserMaxIdle == null) {
            this.perUserMaxIdle = new HashMap<String, Integer>();
        }
        this.perUserMaxIdle.put(userName, value);
    }
    
    void setPerUserMaxTotal(final Map<String, Integer> userDefaultMaxTotal) {
        this.assertInitializationAllowed();
        if (this.perUserMaxTotal == null) {
            this.perUserMaxTotal = new HashMap<String, Integer>();
        }
        else {
            this.perUserMaxTotal.clear();
        }
        this.perUserMaxTotal.putAll(userDefaultMaxTotal);
    }
    
    public void setPerUserMaxTotal(final String userName, final Integer value) {
        this.assertInitializationAllowed();
        if (this.perUserMaxTotal == null) {
            this.perUserMaxTotal = new HashMap<String, Integer>();
        }
        this.perUserMaxTotal.put(userName, value);
    }
    
    void setPerUserMaxWaitMillis(final Map<String, Long> userDefaultMaxWaitMillis) {
        this.assertInitializationAllowed();
        if (this.perUserMaxWaitMillis == null) {
            this.perUserMaxWaitMillis = new HashMap<String, Long>();
        }
        else {
            this.perUserMaxWaitMillis.clear();
        }
        this.perUserMaxWaitMillis.putAll(userDefaultMaxWaitMillis);
    }
    
    public void setPerUserMaxWaitMillis(final String userName, final Long value) {
        this.assertInitializationAllowed();
        if (this.perUserMaxWaitMillis == null) {
            this.perUserMaxWaitMillis = new HashMap<String, Long>();
        }
        this.perUserMaxWaitMillis.put(userName, value);
    }
    
    void setPerUserMinEvictableIdleTimeMillis(final Map<String, Long> userDefaultMinEvictableIdleTimeMillis) {
        this.assertInitializationAllowed();
        if (this.perUserMinEvictableIdleTimeMillis == null) {
            this.perUserMinEvictableIdleTimeMillis = new HashMap<String, Long>();
        }
        else {
            this.perUserMinEvictableIdleTimeMillis.clear();
        }
        this.perUserMinEvictableIdleTimeMillis.putAll(userDefaultMinEvictableIdleTimeMillis);
    }
    
    public void setPerUserMinEvictableIdleTimeMillis(final String userName, final Long value) {
        this.assertInitializationAllowed();
        if (this.perUserMinEvictableIdleTimeMillis == null) {
            this.perUserMinEvictableIdleTimeMillis = new HashMap<String, Long>();
        }
        this.perUserMinEvictableIdleTimeMillis.put(userName, value);
    }
    
    void setPerUserMinIdle(final Map<String, Integer> userDefaultMinIdle) {
        this.assertInitializationAllowed();
        if (this.perUserMinIdle == null) {
            this.perUserMinIdle = new HashMap<String, Integer>();
        }
        else {
            this.perUserMinIdle.clear();
        }
        this.perUserMinIdle.putAll(userDefaultMinIdle);
    }
    
    public void setPerUserMinIdle(final String userName, final Integer value) {
        this.assertInitializationAllowed();
        if (this.perUserMinIdle == null) {
            this.perUserMinIdle = new HashMap<String, Integer>();
        }
        this.perUserMinIdle.put(userName, value);
    }
    
    void setPerUserNumTestsPerEvictionRun(final Map<String, Integer> userDefaultNumTestsPerEvictionRun) {
        this.assertInitializationAllowed();
        if (this.perUserNumTestsPerEvictionRun == null) {
            this.perUserNumTestsPerEvictionRun = new HashMap<String, Integer>();
        }
        else {
            this.perUserNumTestsPerEvictionRun.clear();
        }
        this.perUserNumTestsPerEvictionRun.putAll(userDefaultNumTestsPerEvictionRun);
    }
    
    public void setPerUserNumTestsPerEvictionRun(final String userName, final Integer value) {
        this.assertInitializationAllowed();
        if (this.perUserNumTestsPerEvictionRun == null) {
            this.perUserNumTestsPerEvictionRun = new HashMap<String, Integer>();
        }
        this.perUserNumTestsPerEvictionRun.put(userName, value);
    }
    
    void setPerUserSoftMinEvictableIdleTimeMillis(final Map<String, Long> userDefaultSoftMinEvictableIdleTimeMillis) {
        this.assertInitializationAllowed();
        if (this.perUserSoftMinEvictableIdleTimeMillis == null) {
            this.perUserSoftMinEvictableIdleTimeMillis = new HashMap<String, Long>();
        }
        else {
            this.perUserSoftMinEvictableIdleTimeMillis.clear();
        }
        this.perUserSoftMinEvictableIdleTimeMillis.putAll(userDefaultSoftMinEvictableIdleTimeMillis);
    }
    
    public void setPerUserSoftMinEvictableIdleTimeMillis(final String userName, final Long value) {
        this.assertInitializationAllowed();
        if (this.perUserSoftMinEvictableIdleTimeMillis == null) {
            this.perUserSoftMinEvictableIdleTimeMillis = new HashMap<String, Long>();
        }
        this.perUserSoftMinEvictableIdleTimeMillis.put(userName, value);
    }
    
    void setPerUserTestOnBorrow(final Map<String, Boolean> userDefaultTestOnBorrow) {
        this.assertInitializationAllowed();
        if (this.perUserTestOnBorrow == null) {
            this.perUserTestOnBorrow = this.createMap();
        }
        else {
            this.perUserTestOnBorrow.clear();
        }
        this.perUserTestOnBorrow.putAll(userDefaultTestOnBorrow);
    }
    
    public void setPerUserTestOnBorrow(final String userName, final Boolean value) {
        this.assertInitializationAllowed();
        if (this.perUserTestOnBorrow == null) {
            this.perUserTestOnBorrow = this.createMap();
        }
        this.perUserTestOnBorrow.put(userName, value);
    }
    
    void setPerUserTestOnCreate(final Map<String, Boolean> userDefaultTestOnCreate) {
        this.assertInitializationAllowed();
        if (this.perUserTestOnCreate == null) {
            this.perUserTestOnCreate = this.createMap();
        }
        else {
            this.perUserTestOnCreate.clear();
        }
        this.perUserTestOnCreate.putAll(userDefaultTestOnCreate);
    }
    
    public void setPerUserTestOnCreate(final String userName, final Boolean value) {
        this.assertInitializationAllowed();
        if (this.perUserTestOnCreate == null) {
            this.perUserTestOnCreate = this.createMap();
        }
        this.perUserTestOnCreate.put(userName, value);
    }
    
    void setPerUserTestOnReturn(final Map<String, Boolean> userDefaultTestOnReturn) {
        this.assertInitializationAllowed();
        if (this.perUserTestOnReturn == null) {
            this.perUserTestOnReturn = this.createMap();
        }
        else {
            this.perUserTestOnReturn.clear();
        }
        this.perUserTestOnReturn.putAll(userDefaultTestOnReturn);
    }
    
    public void setPerUserTestOnReturn(final String userName, final Boolean value) {
        this.assertInitializationAllowed();
        if (this.perUserTestOnReturn == null) {
            this.perUserTestOnReturn = this.createMap();
        }
        this.perUserTestOnReturn.put(userName, value);
    }
    
    void setPerUserTestWhileIdle(final Map<String, Boolean> userDefaultTestWhileIdle) {
        this.assertInitializationAllowed();
        if (this.perUserTestWhileIdle == null) {
            this.perUserTestWhileIdle = this.createMap();
        }
        else {
            this.perUserTestWhileIdle.clear();
        }
        this.perUserTestWhileIdle.putAll(userDefaultTestWhileIdle);
    }
    
    public void setPerUserTestWhileIdle(final String userName, final Boolean value) {
        this.assertInitializationAllowed();
        if (this.perUserTestWhileIdle == null) {
            this.perUserTestWhileIdle = this.createMap();
        }
        this.perUserTestWhileIdle.put(userName, value);
    }
    
    void setPerUserTimeBetweenEvictionRunsMillis(final Map<String, Long> userDefaultTimeBetweenEvictionRunsMillis) {
        this.assertInitializationAllowed();
        if (this.perUserTimeBetweenEvictionRunsMillis == null) {
            this.perUserTimeBetweenEvictionRunsMillis = new HashMap<String, Long>();
        }
        else {
            this.perUserTimeBetweenEvictionRunsMillis.clear();
        }
        this.perUserTimeBetweenEvictionRunsMillis.putAll(userDefaultTimeBetweenEvictionRunsMillis);
    }
    
    public void setPerUserTimeBetweenEvictionRunsMillis(final String userName, final Long value) {
        this.assertInitializationAllowed();
        if (this.perUserTimeBetweenEvictionRunsMillis == null) {
            this.perUserTimeBetweenEvictionRunsMillis = new HashMap<String, Long>();
        }
        this.perUserTimeBetweenEvictionRunsMillis.put(userName, value);
    }
    
    @Override
    protected void setupDefaults(final Connection con, final String userName) throws SQLException {
        Boolean defaultAutoCommit = this.isDefaultAutoCommit();
        if (userName != null) {
            final Boolean userMax = this.getPerUserDefaultAutoCommit(userName);
            if (userMax != null) {
                defaultAutoCommit = userMax;
            }
        }
        Boolean defaultReadOnly = this.isDefaultReadOnly();
        if (userName != null) {
            final Boolean userMax2 = this.getPerUserDefaultReadOnly(userName);
            if (userMax2 != null) {
                defaultReadOnly = userMax2;
            }
        }
        int defaultTransactionIsolation = this.getDefaultTransactionIsolation();
        if (userName != null) {
            final Integer userMax3 = this.getPerUserDefaultTransactionIsolation(userName);
            if (userMax3 != null) {
                defaultTransactionIsolation = userMax3;
            }
        }
        if (defaultAutoCommit != null && con.getAutoCommit() != defaultAutoCommit) {
            con.setAutoCommit(defaultAutoCommit);
        }
        if (defaultTransactionIsolation != -1) {
            con.setTransactionIsolation(defaultTransactionIsolation);
        }
        if (defaultReadOnly != null && con.isReadOnly() != defaultReadOnly) {
            con.setReadOnly(defaultReadOnly);
        }
    }
    
    static {
        log = LogFactory.getLog((Class)PerUserPoolDataSource.class);
    }
}
