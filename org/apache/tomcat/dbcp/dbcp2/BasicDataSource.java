package org.apache.tomcat.dbcp.dbcp2;

import java.util.Hashtable;
import java.sql.DriverManager;
import org.apache.juli.logging.LogFactory;
import javax.management.MalformedObjectNameException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Iterator;
import java.util.ArrayList;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import javax.management.ObjectName;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.util.Collections;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.sql.Connection;
import java.util.Collection;
import org.apache.tomcat.dbcp.pool2.PooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.ObjectPool;
import org.apache.tomcat.dbcp.pool2.SwallowedExceptionListener;
import org.apache.tomcat.dbcp.pool2.impl.GenericObjectPoolConfig;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.apache.tomcat.dbcp.pool2.impl.BaseObjectPoolConfig;
import org.apache.tomcat.dbcp.pool2.impl.AbandonedConfig;
import java.io.PrintWriter;
import java.util.Properties;
import org.apache.tomcat.dbcp.pool2.impl.GenericObjectPool;
import java.util.Set;
import java.util.List;
import java.sql.Driver;
import org.apache.juli.logging.Log;
import javax.management.MBeanRegistration;
import javax.sql.DataSource;

public class BasicDataSource implements DataSource, BasicDataSourceMXBean, MBeanRegistration, AutoCloseable
{
    private static final Log log;
    private volatile Boolean defaultAutoCommit;
    private transient Boolean defaultReadOnly;
    private volatile int defaultTransactionIsolation;
    private Integer defaultQueryTimeoutSeconds;
    private volatile String defaultCatalog;
    private volatile String defaultSchema;
    private boolean cacheState;
    private Driver driver;
    private String driverClassName;
    private ClassLoader driverClassLoader;
    private boolean lifo;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    private int initialSize;
    private long maxWaitMillis;
    private boolean poolPreparedStatements;
    private boolean clearStatementPoolOnReturn;
    private int maxOpenPreparedStatements;
    private boolean testOnCreate;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private long timeBetweenEvictionRunsMillis;
    private int numTestsPerEvictionRun;
    private long minEvictableIdleTimeMillis;
    private long softMinEvictableIdleTimeMillis;
    private String evictionPolicyClassName;
    private boolean testWhileIdle;
    private volatile String password;
    private String url;
    private String userName;
    private volatile String validationQuery;
    private volatile int validationQueryTimeoutSeconds;
    private String connectionFactoryClassName;
    private volatile List<String> connectionInitSqls;
    private boolean accessToUnderlyingConnectionAllowed;
    private long maxConnLifetimeMillis;
    private boolean logExpiredConnections;
    private String jmxName;
    private boolean autoCommitOnReturn;
    private boolean rollbackOnReturn;
    private volatile Set<String> disconnectionSqlCodes;
    private boolean fastFailValidation;
    private volatile GenericObjectPool<PoolableConnection> connectionPool;
    private Properties connectionProperties;
    private volatile DataSource dataSource;
    private volatile PrintWriter logWriter;
    private AbandonedConfig abandonedConfig;
    private boolean closed;
    private ObjectNameWrapper registeredJmxObjectName;
    
    public BasicDataSource() {
        this.defaultTransactionIsolation = -1;
        this.cacheState = true;
        this.lifo = true;
        this.maxTotal = 8;
        this.maxIdle = 8;
        this.minIdle = 0;
        this.maxWaitMillis = -1L;
        this.maxOpenPreparedStatements = -1;
        this.testOnBorrow = true;
        this.timeBetweenEvictionRunsMillis = -1L;
        this.numTestsPerEvictionRun = 3;
        this.minEvictableIdleTimeMillis = 1800000L;
        this.softMinEvictableIdleTimeMillis = -1L;
        this.evictionPolicyClassName = BaseObjectPoolConfig.DEFAULT_EVICTION_POLICY_CLASS_NAME;
        this.validationQueryTimeoutSeconds = -1;
        this.maxConnLifetimeMillis = -1L;
        this.logExpiredConnections = true;
        this.autoCommitOnReturn = true;
        this.rollbackOnReturn = true;
        this.connectionProperties = new Properties();
        this.logWriter = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
    }
    
    protected static void validateConnectionFactory(final PoolableConnectionFactory connectionFactory) throws Exception {
        PoolableConnection conn = null;
        PooledObject<PoolableConnection> p = null;
        try {
            p = connectionFactory.makeObject();
            conn = p.getObject();
            connectionFactory.activateObject(p);
            connectionFactory.validateConnection(conn);
            connectionFactory.passivateObject(p);
        }
        finally {
            if (p != null) {
                connectionFactory.destroyObject(p);
            }
        }
    }
    
    public void addConnectionProperty(final String name, final String value) {
        ((Hashtable<String, String>)this.connectionProperties).put(name, value);
    }
    
    @Override
    public synchronized void close() throws SQLException {
        if (this.registeredJmxObjectName != null) {
            this.registeredJmxObjectName.unregisterMBean();
            this.registeredJmxObjectName = null;
        }
        this.closed = true;
        final GenericObjectPool<?> oldPool = this.connectionPool;
        this.connectionPool = null;
        this.dataSource = null;
        try {
            if (oldPool != null) {
                oldPool.close();
            }
        }
        catch (final RuntimeException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new SQLException(Utils.getMessage("pool.close.fail"), e2);
        }
    }
    
    private void closeConnectionPool() {
        final GenericObjectPool<?> oldPool = this.connectionPool;
        this.connectionPool = null;
        try {
            if (oldPool != null) {
                oldPool.close();
            }
        }
        catch (final Exception ex) {}
    }
    
    protected ConnectionFactory createConnectionFactory() throws SQLException {
        return ConnectionFactoryFactory.createConnectionFactory(this, DriverFactory.createDriver(this));
    }
    
    protected void createConnectionPool(final PoolableConnectionFactory factory) {
        final GenericObjectPoolConfig<PoolableConnection> config = new GenericObjectPoolConfig<PoolableConnection>();
        this.updateJmxName(config);
        config.setJmxEnabled(this.registeredJmxObjectName != null);
        final GenericObjectPool<PoolableConnection> gop = this.createObjectPool(factory, config, this.abandonedConfig);
        gop.setMaxTotal(this.maxTotal);
        gop.setMaxIdle(this.maxIdle);
        gop.setMinIdle(this.minIdle);
        gop.setMaxWaitMillis(this.maxWaitMillis);
        gop.setTestOnCreate(this.testOnCreate);
        gop.setTestOnBorrow(this.testOnBorrow);
        gop.setTestOnReturn(this.testOnReturn);
        gop.setNumTestsPerEvictionRun(this.numTestsPerEvictionRun);
        gop.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis);
        gop.setSoftMinEvictableIdleTimeMillis(this.softMinEvictableIdleTimeMillis);
        gop.setTestWhileIdle(this.testWhileIdle);
        gop.setLifo(this.lifo);
        gop.setSwallowedExceptionListener(new SwallowedExceptionLogger(BasicDataSource.log, this.logExpiredConnections));
        gop.setEvictionPolicyClassName(this.evictionPolicyClassName);
        factory.setPool(gop);
        this.connectionPool = gop;
    }
    
    protected DataSource createDataSource() throws SQLException {
        if (this.closed) {
            throw new SQLException("Data source is closed");
        }
        if (this.dataSource != null) {
            return this.dataSource;
        }
        synchronized (this) {
            if (this.dataSource != null) {
                return this.dataSource;
            }
            this.jmxRegister();
            final ConnectionFactory driverConnectionFactory = this.createConnectionFactory();
            boolean success = false;
            PoolableConnectionFactory poolableConnectionFactory;
            try {
                poolableConnectionFactory = this.createPoolableConnectionFactory(driverConnectionFactory);
                poolableConnectionFactory.setPoolStatements(this.poolPreparedStatements);
                poolableConnectionFactory.setMaxOpenPreparedStatements(this.maxOpenPreparedStatements);
                success = true;
            }
            catch (final SQLException | RuntimeException se) {
                throw se;
            }
            catch (final Exception ex) {
                throw new SQLException("Error creating connection factory", ex);
            }
            if (success) {
                this.createConnectionPool(poolableConnectionFactory);
            }
            success = false;
            DataSource newDataSource;
            try {
                newDataSource = this.createDataSourceInstance();
                newDataSource.setLogWriter(this.logWriter);
                success = true;
            }
            catch (final SQLException | RuntimeException se2) {
                throw se2;
            }
            catch (final Exception ex2) {
                throw new SQLException("Error creating datasource", ex2);
            }
            finally {
                if (!success) {
                    this.closeConnectionPool();
                }
            }
            try {
                for (int i = 0; i < this.initialSize; ++i) {
                    this.connectionPool.addObject();
                }
            }
            catch (final Exception e) {
                this.closeConnectionPool();
                throw new SQLException("Error preloading the connection pool", e);
            }
            this.startPoolMaintenance();
            return this.dataSource = newDataSource;
        }
    }
    
    protected DataSource createDataSourceInstance() throws SQLException {
        final PoolingDataSource<PoolableConnection> pds = new PoolingDataSource<PoolableConnection>(this.connectionPool);
        pds.setAccessToUnderlyingConnectionAllowed(this.isAccessToUnderlyingConnectionAllowed());
        return pds;
    }
    
    protected GenericObjectPool<PoolableConnection> createObjectPool(final PoolableConnectionFactory factory, final GenericObjectPoolConfig<PoolableConnection> poolConfig, final AbandonedConfig abandonedConfig) {
        GenericObjectPool<PoolableConnection> gop;
        if (abandonedConfig != null && (abandonedConfig.getRemoveAbandonedOnBorrow() || abandonedConfig.getRemoveAbandonedOnMaintenance())) {
            gop = new GenericObjectPool<PoolableConnection>(factory, poolConfig, abandonedConfig);
        }
        else {
            gop = new GenericObjectPool<PoolableConnection>(factory, poolConfig);
        }
        return gop;
    }
    
    protected PoolableConnectionFactory createPoolableConnectionFactory(final ConnectionFactory driverConnectionFactory) throws SQLException {
        PoolableConnectionFactory connectionFactory = null;
        try {
            connectionFactory = new PoolableConnectionFactory(driverConnectionFactory, ObjectNameWrapper.unwrap(this.registeredJmxObjectName));
            connectionFactory.setValidationQuery(this.validationQuery);
            connectionFactory.setValidationQueryTimeout(this.validationQueryTimeoutSeconds);
            connectionFactory.setConnectionInitSql(this.connectionInitSqls);
            connectionFactory.setDefaultReadOnly(this.defaultReadOnly);
            connectionFactory.setDefaultAutoCommit(this.defaultAutoCommit);
            connectionFactory.setDefaultTransactionIsolation(this.defaultTransactionIsolation);
            connectionFactory.setDefaultCatalog(this.defaultCatalog);
            connectionFactory.setDefaultSchema(this.defaultSchema);
            connectionFactory.setCacheState(this.cacheState);
            connectionFactory.setPoolStatements(this.poolPreparedStatements);
            connectionFactory.setClearStatementPoolOnReturn(this.clearStatementPoolOnReturn);
            connectionFactory.setMaxOpenPreparedStatements(this.maxOpenPreparedStatements);
            connectionFactory.setMaxConnLifetimeMillis(this.maxConnLifetimeMillis);
            connectionFactory.setRollbackOnReturn(this.getRollbackOnReturn());
            connectionFactory.setAutoCommitOnReturn(this.getAutoCommitOnReturn());
            connectionFactory.setDefaultQueryTimeout(this.getDefaultQueryTimeout());
            connectionFactory.setFastFailValidation(this.fastFailValidation);
            connectionFactory.setDisconnectionSqlCodes(this.disconnectionSqlCodes);
            validateConnectionFactory(connectionFactory);
        }
        catch (final RuntimeException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new SQLException("Cannot create PoolableConnectionFactory (" + e2.getMessage() + ")", e2);
        }
        return connectionFactory;
    }
    
    public void evict() throws Exception {
        if (this.connectionPool != null) {
            this.connectionPool.evict();
        }
    }
    
    public PrintWriter getAbandonedLogWriter() {
        return (this.abandonedConfig == null) ? null : this.abandonedConfig.getLogWriter();
    }
    
    @Override
    public boolean getAbandonedUsageTracking() {
        return this.abandonedConfig != null && this.abandonedConfig.getUseUsageTracking();
    }
    
    public boolean getAutoCommitOnReturn() {
        return this.autoCommitOnReturn;
    }
    
    @Override
    public boolean getCacheState() {
        return this.cacheState;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        if (Utils.IS_SECURITY_ENABLED) {
            final PrivilegedExceptionAction<Connection> action = new PaGetConnection();
            try {
                return AccessController.doPrivileged(action);
            }
            catch (final PrivilegedActionException e) {
                final Throwable cause = e.getCause();
                if (cause instanceof SQLException) {
                    throw (SQLException)cause;
                }
                throw new SQLException(e);
            }
        }
        return this.createDataSource().getConnection();
    }
    
    @Override
    public Connection getConnection(final String user, final String pass) throws SQLException {
        throw new UnsupportedOperationException("Not supported by BasicDataSource");
    }
    
    public String getConnectionFactoryClassName() {
        return this.connectionFactoryClassName;
    }
    
    public List<String> getConnectionInitSqls() {
        final List<String> result = this.connectionInitSqls;
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }
    
    @Override
    public String[] getConnectionInitSqlsAsArray() {
        return this.getConnectionInitSqls().toArray(Utils.EMPTY_STRING_ARRAY);
    }
    
    protected GenericObjectPool<PoolableConnection> getConnectionPool() {
        return this.connectionPool;
    }
    
    Properties getConnectionProperties() {
        return this.connectionProperties;
    }
    
    @Override
    public Boolean getDefaultAutoCommit() {
        return this.defaultAutoCommit;
    }
    
    @Override
    public String getDefaultCatalog() {
        return this.defaultCatalog;
    }
    
    public Integer getDefaultQueryTimeout() {
        return this.defaultQueryTimeoutSeconds;
    }
    
    @Override
    public Boolean getDefaultReadOnly() {
        return this.defaultReadOnly;
    }
    
    @Override
    public String getDefaultSchema() {
        return this.defaultSchema;
    }
    
    @Override
    public int getDefaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }
    
    public Set<String> getDisconnectionSqlCodes() {
        final Set<String> result = this.disconnectionSqlCodes;
        if (result == null) {
            return Collections.emptySet();
        }
        return result;
    }
    
    @Override
    public String[] getDisconnectionSqlCodesAsArray() {
        return this.getDisconnectionSqlCodes().toArray(Utils.EMPTY_STRING_ARRAY);
    }
    
    public synchronized Driver getDriver() {
        return this.driver;
    }
    
    public synchronized ClassLoader getDriverClassLoader() {
        return this.driverClassLoader;
    }
    
    @Override
    public synchronized String getDriverClassName() {
        return this.driverClassName;
    }
    
    @Deprecated
    public boolean getEnableAutoCommitOnReturn() {
        return this.autoCommitOnReturn;
    }
    
    public synchronized String getEvictionPolicyClassName() {
        return this.evictionPolicyClassName;
    }
    
    @Override
    public boolean getFastFailValidation() {
        return this.fastFailValidation;
    }
    
    @Override
    public synchronized int getInitialSize() {
        return this.initialSize;
    }
    
    public String getJmxName() {
        return this.jmxName;
    }
    
    @Override
    public synchronized boolean getLifo() {
        return this.lifo;
    }
    
    @Override
    public boolean getLogAbandoned() {
        return this.abandonedConfig != null && this.abandonedConfig.getLogAbandoned();
    }
    
    @Override
    public boolean getLogExpiredConnections() {
        return this.logExpiredConnections;
    }
    
    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported by BasicDataSource");
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.createDataSource().getLogWriter();
    }
    
    @Override
    public long getMaxConnLifetimeMillis() {
        return this.maxConnLifetimeMillis;
    }
    
    @Override
    public synchronized int getMaxIdle() {
        return this.maxIdle;
    }
    
    @Override
    public synchronized int getMaxOpenPreparedStatements() {
        return this.maxOpenPreparedStatements;
    }
    
    @Override
    public synchronized int getMaxTotal() {
        return this.maxTotal;
    }
    
    @Override
    public synchronized long getMaxWaitMillis() {
        return this.maxWaitMillis;
    }
    
    @Override
    public synchronized long getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }
    
    @Override
    public synchronized int getMinIdle() {
        return this.minIdle;
    }
    
    @Override
    public int getNumActive() {
        final GenericObjectPool<PoolableConnection> pool = this.connectionPool;
        return (pool == null) ? 0 : pool.getNumActive();
    }
    
    @Override
    public int getNumIdle() {
        final GenericObjectPool<PoolableConnection> pool = this.connectionPool;
        return (pool == null) ? 0 : pool.getNumIdle();
    }
    
    @Override
    public synchronized int getNumTestsPerEvictionRun() {
        return this.numTestsPerEvictionRun;
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
    
    @Override
    public String getPassword() {
        return this.password;
    }
    
    protected ObjectName getRegisteredJmxName() {
        return ObjectNameWrapper.unwrap(this.registeredJmxObjectName);
    }
    
    @Override
    public boolean getRemoveAbandonedOnBorrow() {
        return this.abandonedConfig != null && this.abandonedConfig.getRemoveAbandonedOnBorrow();
    }
    
    @Override
    public boolean getRemoveAbandonedOnMaintenance() {
        return this.abandonedConfig != null && this.abandonedConfig.getRemoveAbandonedOnMaintenance();
    }
    
    @Override
    public int getRemoveAbandonedTimeout() {
        return (this.abandonedConfig == null) ? 300 : this.abandonedConfig.getRemoveAbandonedTimeout();
    }
    
    public boolean getRollbackOnReturn() {
        return this.rollbackOnReturn;
    }
    
    @Override
    public synchronized long getSoftMinEvictableIdleTimeMillis() {
        return this.softMinEvictableIdleTimeMillis;
    }
    
    @Override
    public synchronized boolean getTestOnBorrow() {
        return this.testOnBorrow;
    }
    
    @Override
    public synchronized boolean getTestOnCreate() {
        return this.testOnCreate;
    }
    
    public synchronized boolean getTestOnReturn() {
        return this.testOnReturn;
    }
    
    @Override
    public synchronized boolean getTestWhileIdle() {
        return this.testWhileIdle;
    }
    
    @Override
    public synchronized long getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }
    
    @Override
    public synchronized String getUrl() {
        return this.url;
    }
    
    @Override
    public String getUsername() {
        return this.userName;
    }
    
    @Override
    public String getValidationQuery() {
        return this.validationQuery;
    }
    
    @Override
    public int getValidationQueryTimeout() {
        return this.validationQueryTimeoutSeconds;
    }
    
    public void invalidateConnection(final Connection connection) throws IllegalStateException {
        if (connection == null) {
            return;
        }
        if (this.connectionPool == null) {
            throw new IllegalStateException("Cannot invalidate connection: ConnectionPool is null.");
        }
        PoolableConnection poolableConnection;
        try {
            poolableConnection = connection.unwrap(PoolableConnection.class);
            if (poolableConnection == null) {
                throw new IllegalStateException("Cannot invalidate connection: Connection is not a poolable connection.");
            }
        }
        catch (final SQLException e) {
            throw new IllegalStateException("Cannot invalidate connection: Unwrapping poolable connection failed.", e);
        }
        try {
            this.connectionPool.invalidateObject(poolableConnection);
        }
        catch (final Exception e2) {
            throw new IllegalStateException("Invalidating connection threw unexpected exception", e2);
        }
    }
    
    @Override
    public synchronized boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }
    
    @Override
    public boolean isClearStatementPoolOnReturn() {
        return this.clearStatementPoolOnReturn;
    }
    
    @Override
    public synchronized boolean isClosed() {
        return this.closed;
    }
    
    private boolean isEmpty(final String value) {
        return value == null || value.trim().isEmpty();
    }
    
    @Override
    public synchronized boolean isPoolPreparedStatements() {
        return this.poolPreparedStatements;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return iface != null && iface.isInstance(this);
    }
    
    private void jmxRegister() {
        if (this.registeredJmxObjectName != null) {
            return;
        }
        final String requestedName = this.getJmxName();
        if (requestedName == null) {
            return;
        }
        this.registeredJmxObjectName = this.registerJmxObjectName(requestedName, null);
        try {
            final StandardMBean standardMBean = new StandardMBean((T)this, (Class<T>)DataSourceMXBean.class);
            this.registeredJmxObjectName.registerMBean(standardMBean);
        }
        catch (final NotCompliantMBeanException e) {
            BasicDataSource.log.warn((Object)("The requested JMX name [" + requestedName + "] was not valid and will be ignored."));
        }
    }
    
    protected void log(final String message) {
        if (this.logWriter != null) {
            this.logWriter.println(message);
        }
    }
    
    protected void log(final String message, final Throwable throwable) {
        if (this.logWriter != null) {
            this.logWriter.println(message);
            throwable.printStackTrace(this.logWriter);
        }
    }
    
    @Override
    public void postDeregister() {
    }
    
    @Override
    public void postRegister(final Boolean registrationDone) {
    }
    
    @Override
    public void preDeregister() throws Exception {
    }
    
    @Override
    public ObjectName preRegister(final MBeanServer server, final ObjectName objectName) {
        this.registeredJmxObjectName = this.registerJmxObjectName(this.getJmxName(), objectName);
        return ObjectNameWrapper.unwrap(this.registeredJmxObjectName);
    }
    
    public void removeConnectionProperty(final String name) {
        this.connectionProperties.remove(name);
    }
    
    @Override
    public synchronized void restart() throws SQLException {
        this.close();
        this.start();
    }
    
    public void setAbandonedLogWriter(final PrintWriter logWriter) {
        if (this.abandonedConfig == null) {
            this.abandonedConfig = new AbandonedConfig();
        }
        this.abandonedConfig.setLogWriter(logWriter);
        final GenericObjectPool<?> gop = this.connectionPool;
        if (gop != null) {
            gop.setAbandonedConfig(this.abandonedConfig);
        }
    }
    
    public void setAbandonedUsageTracking(final boolean usageTracking) {
        if (this.abandonedConfig == null) {
            this.abandonedConfig = new AbandonedConfig();
        }
        this.abandonedConfig.setUseUsageTracking(usageTracking);
        final GenericObjectPool<?> gop = this.connectionPool;
        if (gop != null) {
            gop.setAbandonedConfig(this.abandonedConfig);
        }
    }
    
    public synchronized void setAccessToUnderlyingConnectionAllowed(final boolean allow) {
        this.accessToUnderlyingConnectionAllowed = allow;
    }
    
    public void setAutoCommitOnReturn(final boolean autoCommitOnReturn) {
        this.autoCommitOnReturn = autoCommitOnReturn;
    }
    
    public void setCacheState(final boolean cacheState) {
        this.cacheState = cacheState;
    }
    
    public void setClearStatementPoolOnReturn(final boolean clearStatementPoolOnReturn) {
        this.clearStatementPoolOnReturn = clearStatementPoolOnReturn;
    }
    
    public void setConnectionFactoryClassName(final String connectionFactoryClassName) {
        this.connectionFactoryClassName = (this.isEmpty(connectionFactoryClassName) ? null : connectionFactoryClassName);
    }
    
    public void setConnectionInitSqls(final Collection<String> connectionInitSqls) {
        if (connectionInitSqls != null && !connectionInitSqls.isEmpty()) {
            ArrayList<String> newVal = null;
            for (final String s : connectionInitSqls) {
                if (!this.isEmpty(s)) {
                    if (newVal == null) {
                        newVal = new ArrayList<String>();
                    }
                    newVal.add(s);
                }
            }
            this.connectionInitSqls = newVal;
        }
        else {
            this.connectionInitSqls = null;
        }
    }
    
    public void setConnectionProperties(final String connectionProperties) {
        Objects.requireNonNull(connectionProperties, "connectionProperties is null");
        final String[] entries = connectionProperties.split(";");
        final Properties properties = new Properties();
        for (final String entry : entries) {
            if (!entry.isEmpty()) {
                final int index = entry.indexOf(61);
                if (index > 0) {
                    final String name = entry.substring(0, index);
                    final String value = entry.substring(index + 1);
                    properties.setProperty(name, value);
                }
                else {
                    properties.setProperty(entry, "");
                }
            }
        }
        this.connectionProperties = properties;
    }
    
    public void setDefaultAutoCommit(final Boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }
    
    public void setDefaultCatalog(final String defaultCatalog) {
        this.defaultCatalog = (this.isEmpty(defaultCatalog) ? null : defaultCatalog);
    }
    
    public void setDefaultQueryTimeout(final Integer defaultQueryTimeoutSeconds) {
        this.defaultQueryTimeoutSeconds = defaultQueryTimeoutSeconds;
    }
    
    public void setDefaultReadOnly(final Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }
    
    public void setDefaultSchema(final String defaultSchema) {
        this.defaultSchema = (this.isEmpty(defaultSchema) ? null : defaultSchema);
    }
    
    public void setDefaultTransactionIsolation(final int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }
    
    public void setDisconnectionSqlCodes(final Collection<String> disconnectionSqlCodes) {
        if (disconnectionSqlCodes != null && !disconnectionSqlCodes.isEmpty()) {
            HashSet<String> newVal = null;
            for (final String s : disconnectionSqlCodes) {
                if (!this.isEmpty(s)) {
                    if (newVal == null) {
                        newVal = new HashSet<String>();
                    }
                    newVal.add(s);
                }
            }
            this.disconnectionSqlCodes = newVal;
        }
        else {
            this.disconnectionSqlCodes = null;
        }
    }
    
    public synchronized void setDriver(final Driver driver) {
        this.driver = driver;
    }
    
    public synchronized void setDriverClassLoader(final ClassLoader driverClassLoader) {
        this.driverClassLoader = driverClassLoader;
    }
    
    public synchronized void setDriverClassName(final String driverClassName) {
        this.driverClassName = (this.isEmpty(driverClassName) ? null : driverClassName);
    }
    
    @Deprecated
    public void setEnableAutoCommitOnReturn(final boolean autoCommitOnReturn) {
        this.autoCommitOnReturn = autoCommitOnReturn;
    }
    
    public synchronized void setEvictionPolicyClassName(final String evictionPolicyClassName) {
        if (this.connectionPool != null) {
            this.connectionPool.setEvictionPolicyClassName(evictionPolicyClassName);
        }
        this.evictionPolicyClassName = evictionPolicyClassName;
    }
    
    public void setFastFailValidation(final boolean fastFailValidation) {
        this.fastFailValidation = fastFailValidation;
    }
    
    public synchronized void setInitialSize(final int initialSize) {
        this.initialSize = initialSize;
    }
    
    public void setJmxName(final String jmxName) {
        this.jmxName = jmxName;
    }
    
    public synchronized void setLifo(final boolean lifo) {
        this.lifo = lifo;
        if (this.connectionPool != null) {
            this.connectionPool.setLifo(lifo);
        }
    }
    
    public void setLogAbandoned(final boolean logAbandoned) {
        if (this.abandonedConfig == null) {
            this.abandonedConfig = new AbandonedConfig();
        }
        this.abandonedConfig.setLogAbandoned(logAbandoned);
        final GenericObjectPool<?> gop = this.connectionPool;
        if (gop != null) {
            gop.setAbandonedConfig(this.abandonedConfig);
        }
    }
    
    public void setLogExpiredConnections(final boolean logExpiredConnections) {
        this.logExpiredConnections = logExpiredConnections;
    }
    
    @Override
    public void setLoginTimeout(final int loginTimeout) throws SQLException {
        throw new UnsupportedOperationException("Not supported by BasicDataSource");
    }
    
    @Override
    public void setLogWriter(final PrintWriter logWriter) throws SQLException {
        this.createDataSource().setLogWriter(logWriter);
        this.logWriter = logWriter;
    }
    
    public void setMaxConnLifetimeMillis(final long maxConnLifetimeMillis) {
        this.maxConnLifetimeMillis = maxConnLifetimeMillis;
    }
    
    public synchronized void setMaxIdle(final int maxIdle) {
        this.maxIdle = maxIdle;
        if (this.connectionPool != null) {
            this.connectionPool.setMaxIdle(maxIdle);
        }
    }
    
    public synchronized void setMaxOpenPreparedStatements(final int maxOpenStatements) {
        this.maxOpenPreparedStatements = maxOpenStatements;
    }
    
    public synchronized void setMaxTotal(final int maxTotal) {
        this.maxTotal = maxTotal;
        if (this.connectionPool != null) {
            this.connectionPool.setMaxTotal(maxTotal);
        }
    }
    
    public synchronized void setMaxWaitMillis(final long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
        if (this.connectionPool != null) {
            this.connectionPool.setMaxWaitMillis(maxWaitMillis);
        }
    }
    
    public synchronized void setMinEvictableIdleTimeMillis(final long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        if (this.connectionPool != null) {
            this.connectionPool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        }
    }
    
    public synchronized void setMinIdle(final int minIdle) {
        this.minIdle = minIdle;
        if (this.connectionPool != null) {
            this.connectionPool.setMinIdle(minIdle);
        }
    }
    
    public synchronized void setNumTestsPerEvictionRun(final int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
        if (this.connectionPool != null) {
            this.connectionPool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        }
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public synchronized void setPoolPreparedStatements(final boolean poolingStatements) {
        this.poolPreparedStatements = poolingStatements;
    }
    
    public void setRemoveAbandonedOnBorrow(final boolean removeAbandonedOnBorrow) {
        if (this.abandonedConfig == null) {
            this.abandonedConfig = new AbandonedConfig();
        }
        this.abandonedConfig.setRemoveAbandonedOnBorrow(removeAbandonedOnBorrow);
        final GenericObjectPool<?> gop = this.connectionPool;
        if (gop != null) {
            gop.setAbandonedConfig(this.abandonedConfig);
        }
    }
    
    public void setRemoveAbandonedOnMaintenance(final boolean removeAbandonedOnMaintenance) {
        if (this.abandonedConfig == null) {
            this.abandonedConfig = new AbandonedConfig();
        }
        this.abandonedConfig.setRemoveAbandonedOnMaintenance(removeAbandonedOnMaintenance);
        final GenericObjectPool<?> gop = this.connectionPool;
        if (gop != null) {
            gop.setAbandonedConfig(this.abandonedConfig);
        }
    }
    
    public void setRemoveAbandonedTimeout(final int removeAbandonedTimeout) {
        if (this.abandonedConfig == null) {
            this.abandonedConfig = new AbandonedConfig();
        }
        this.abandonedConfig.setRemoveAbandonedTimeout(removeAbandonedTimeout);
        final GenericObjectPool<?> gop = this.connectionPool;
        if (gop != null) {
            gop.setAbandonedConfig(this.abandonedConfig);
        }
    }
    
    public void setRollbackOnReturn(final boolean rollbackOnReturn) {
        this.rollbackOnReturn = rollbackOnReturn;
    }
    
    public synchronized void setSoftMinEvictableIdleTimeMillis(final long softMinEvictableIdleTimeMillis) {
        this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
        if (this.connectionPool != null) {
            this.connectionPool.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
        }
    }
    
    public synchronized void setTestOnBorrow(final boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
        if (this.connectionPool != null) {
            this.connectionPool.setTestOnBorrow(testOnBorrow);
        }
    }
    
    public synchronized void setTestOnCreate(final boolean testOnCreate) {
        this.testOnCreate = testOnCreate;
        if (this.connectionPool != null) {
            this.connectionPool.setTestOnCreate(testOnCreate);
        }
    }
    
    public synchronized void setTestOnReturn(final boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
        if (this.connectionPool != null) {
            this.connectionPool.setTestOnReturn(testOnReturn);
        }
    }
    
    public synchronized void setTestWhileIdle(final boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
        if (this.connectionPool != null) {
            this.connectionPool.setTestWhileIdle(testWhileIdle);
        }
    }
    
    public synchronized void setTimeBetweenEvictionRunsMillis(final long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        if (this.connectionPool != null) {
            this.connectionPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        }
    }
    
    public synchronized void setUrl(final String url) {
        this.url = url;
    }
    
    public void setUsername(final String userName) {
        this.userName = userName;
    }
    
    public void setValidationQuery(final String validationQuery) {
        this.validationQuery = (this.isEmpty(validationQuery) ? null : validationQuery);
    }
    
    public void setValidationQueryTimeout(final int validationQueryTimeoutSeconds) {
        this.validationQueryTimeoutSeconds = validationQueryTimeoutSeconds;
    }
    
    @Override
    public synchronized void start() throws SQLException {
        this.closed = false;
        this.createDataSource();
    }
    
    protected void startPoolMaintenance() {
        if (this.connectionPool != null && this.timeBetweenEvictionRunsMillis > 0L) {
            this.connectionPool.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis);
        }
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (this.isWrapperFor(iface)) {
            return iface.cast(this);
        }
        throw new SQLException(this + " is not a wrapper for " + iface);
    }
    
    private void updateJmxName(final GenericObjectPoolConfig<?> config) {
        if (this.registeredJmxObjectName == null) {
            return;
        }
        final StringBuilder base = new StringBuilder(this.registeredJmxObjectName.toString());
        base.append(",connectionpool=");
        config.setJmxNameBase(base.toString());
        config.setJmxNamePrefix("connections");
    }
    
    private ObjectNameWrapper registerJmxObjectName(final String requestedName, final ObjectName objectName) {
        ObjectNameWrapper objectNameWrapper = null;
        if (requestedName != null) {
            try {
                objectNameWrapper = ObjectNameWrapper.wrap(requestedName);
            }
            catch (final MalformedObjectNameException e) {
                BasicDataSource.log.warn((Object)("The requested JMX name '" + requestedName + "' was not valid and will be ignored."));
            }
        }
        if (objectNameWrapper == null) {
            objectNameWrapper = ObjectNameWrapper.wrap(objectName);
        }
        return objectNameWrapper;
    }
    
    static {
        log = LogFactory.getLog((Class)BasicDataSource.class);
        DriverManager.getDrivers();
        try {
            if (Utils.IS_SECURITY_ENABLED) {
                final ClassLoader loader = BasicDataSource.class.getClassLoader();
                final String dbcpPackageName = BasicDataSource.class.getPackage().getName();
                loader.loadClass(dbcpPackageName + ".DelegatingCallableStatement");
                loader.loadClass(dbcpPackageName + ".DelegatingDatabaseMetaData");
                loader.loadClass(dbcpPackageName + ".DelegatingPreparedStatement");
                loader.loadClass(dbcpPackageName + ".DelegatingResultSet");
                loader.loadClass(dbcpPackageName + ".PoolableCallableStatement");
                loader.loadClass(dbcpPackageName + ".PoolablePreparedStatement");
                loader.loadClass(dbcpPackageName + ".PoolingConnection$StatementType");
                loader.loadClass(dbcpPackageName + ".PStmtKey");
                final String poolPackageName = PooledObject.class.getPackage().getName();
                loader.loadClass(poolPackageName + ".impl.LinkedBlockingDeque$Node");
                loader.loadClass(poolPackageName + ".impl.GenericKeyedObjectPool$ObjectDeque");
            }
        }
        catch (final ClassNotFoundException cnfe) {
            throw new IllegalStateException("Unable to pre-load classes", cnfe);
        }
    }
    
    private class PaGetConnection implements PrivilegedExceptionAction<Connection>
    {
        @Override
        public Connection run() throws SQLException {
            return BasicDataSource.this.createDataSource().getConnection();
        }
    }
}
