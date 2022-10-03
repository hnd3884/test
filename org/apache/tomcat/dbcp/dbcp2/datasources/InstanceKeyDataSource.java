package org.apache.tomcat.dbcp.dbcp2.datasources;

import javax.sql.PooledConnection;
import javax.naming.Context;
import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.Connection;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.pool2.impl.BaseObjectPoolConfig;
import java.io.PrintWriter;
import java.util.Properties;
import javax.sql.ConnectionPoolDataSource;
import java.io.Serializable;
import javax.naming.Referenceable;
import javax.sql.DataSource;

public abstract class InstanceKeyDataSource implements DataSource, Referenceable, Serializable, AutoCloseable
{
    private static final long serialVersionUID = -6819270431752240878L;
    private static final String GET_CONNECTION_CALLED = "A Connection was already requested from this source, further initialization is not allowed.";
    private static final String BAD_TRANSACTION_ISOLATION = "The requested TransactionIsolation level is invalid.";
    protected static final int UNKNOWN_TRANSACTIONISOLATION = -1;
    private volatile boolean getConnectionCalled;
    private ConnectionPoolDataSource dataSource;
    private String dataSourceName;
    private String description;
    private Properties jndiEnvironment;
    private int loginTimeout;
    private PrintWriter logWriter;
    private String instanceKey;
    private boolean defaultBlockWhenExhausted;
    private String defaultEvictionPolicyClassName;
    private boolean defaultLifo;
    private int defaultMaxIdle;
    private int defaultMaxTotal;
    private long defaultMaxWaitMillis;
    private long defaultMinEvictableIdleTimeMillis;
    private int defaultMinIdle;
    private int defaultNumTestsPerEvictionRun;
    private long defaultSoftMinEvictableIdleTimeMillis;
    private boolean defaultTestOnCreate;
    private boolean defaultTestOnBorrow;
    private boolean defaultTestOnReturn;
    private boolean defaultTestWhileIdle;
    private long defaultTimeBetweenEvictionRunsMillis;
    private String validationQuery;
    private int validationQueryTimeoutSeconds;
    private boolean rollbackAfterValidation;
    private long maxConnLifetimeMillis;
    private Boolean defaultAutoCommit;
    private int defaultTransactionIsolation;
    private Boolean defaultReadOnly;
    
    public InstanceKeyDataSource() {
        this.defaultBlockWhenExhausted = true;
        this.defaultEvictionPolicyClassName = BaseObjectPoolConfig.DEFAULT_EVICTION_POLICY_CLASS_NAME;
        this.defaultLifo = true;
        this.defaultMaxIdle = 8;
        this.defaultMaxTotal = -1;
        this.defaultMaxWaitMillis = -1L;
        this.defaultMinEvictableIdleTimeMillis = 1800000L;
        this.defaultMinIdle = 0;
        this.defaultNumTestsPerEvictionRun = 3;
        this.defaultSoftMinEvictableIdleTimeMillis = -1L;
        this.defaultTestOnCreate = false;
        this.defaultTestOnBorrow = false;
        this.defaultTestOnReturn = false;
        this.defaultTestWhileIdle = false;
        this.defaultTimeBetweenEvictionRunsMillis = -1L;
        this.validationQueryTimeoutSeconds = -1;
        this.maxConnLifetimeMillis = -1L;
        this.defaultTransactionIsolation = -1;
    }
    
    protected void assertInitializationAllowed() throws IllegalStateException {
        if (this.getConnectionCalled) {
            throw new IllegalStateException("A Connection was already requested from this source, further initialization is not allowed.");
        }
    }
    
    @Override
    public abstract void close() throws Exception;
    
    protected abstract PooledConnectionManager getConnectionManager(final UserPassKey p0);
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (this.isWrapperFor(iface)) {
            return (T)this;
        }
        throw new SQLException(this + " is not a wrapper for " + iface);
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public boolean getDefaultBlockWhenExhausted() {
        return this.defaultBlockWhenExhausted;
    }
    
    public void setDefaultBlockWhenExhausted(final boolean blockWhenExhausted) {
        this.assertInitializationAllowed();
        this.defaultBlockWhenExhausted = blockWhenExhausted;
    }
    
    public String getDefaultEvictionPolicyClassName() {
        return this.defaultEvictionPolicyClassName;
    }
    
    public void setDefaultEvictionPolicyClassName(final String evictionPolicyClassName) {
        this.assertInitializationAllowed();
        this.defaultEvictionPolicyClassName = evictionPolicyClassName;
    }
    
    public boolean getDefaultLifo() {
        return this.defaultLifo;
    }
    
    public void setDefaultLifo(final boolean lifo) {
        this.assertInitializationAllowed();
        this.defaultLifo = lifo;
    }
    
    public int getDefaultMaxIdle() {
        return this.defaultMaxIdle;
    }
    
    public void setDefaultMaxIdle(final int maxIdle) {
        this.assertInitializationAllowed();
        this.defaultMaxIdle = maxIdle;
    }
    
    public int getDefaultMaxTotal() {
        return this.defaultMaxTotal;
    }
    
    public void setDefaultMaxTotal(final int maxTotal) {
        this.assertInitializationAllowed();
        this.defaultMaxTotal = maxTotal;
    }
    
    public long getDefaultMaxWaitMillis() {
        return this.defaultMaxWaitMillis;
    }
    
    public void setDefaultMaxWaitMillis(final long maxWaitMillis) {
        this.assertInitializationAllowed();
        this.defaultMaxWaitMillis = maxWaitMillis;
    }
    
    public long getDefaultMinEvictableIdleTimeMillis() {
        return this.defaultMinEvictableIdleTimeMillis;
    }
    
    public void setDefaultMinEvictableIdleTimeMillis(final long minEvictableIdleTimeMillis) {
        this.assertInitializationAllowed();
        this.defaultMinEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }
    
    public int getDefaultMinIdle() {
        return this.defaultMinIdle;
    }
    
    public void setDefaultMinIdle(final int minIdle) {
        this.assertInitializationAllowed();
        this.defaultMinIdle = minIdle;
    }
    
    public int getDefaultNumTestsPerEvictionRun() {
        return this.defaultNumTestsPerEvictionRun;
    }
    
    public void setDefaultNumTestsPerEvictionRun(final int numTestsPerEvictionRun) {
        this.assertInitializationAllowed();
        this.defaultNumTestsPerEvictionRun = numTestsPerEvictionRun;
    }
    
    public long getDefaultSoftMinEvictableIdleTimeMillis() {
        return this.defaultSoftMinEvictableIdleTimeMillis;
    }
    
    public void setDefaultSoftMinEvictableIdleTimeMillis(final long softMinEvictableIdleTimeMillis) {
        this.assertInitializationAllowed();
        this.defaultSoftMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
    }
    
    public boolean getDefaultTestOnCreate() {
        return this.defaultTestOnCreate;
    }
    
    public void setDefaultTestOnCreate(final boolean testOnCreate) {
        this.assertInitializationAllowed();
        this.defaultTestOnCreate = testOnCreate;
    }
    
    public boolean getDefaultTestOnBorrow() {
        return this.defaultTestOnBorrow;
    }
    
    public void setDefaultTestOnBorrow(final boolean testOnBorrow) {
        this.assertInitializationAllowed();
        this.defaultTestOnBorrow = testOnBorrow;
    }
    
    public boolean getDefaultTestOnReturn() {
        return this.defaultTestOnReturn;
    }
    
    public void setDefaultTestOnReturn(final boolean testOnReturn) {
        this.assertInitializationAllowed();
        this.defaultTestOnReturn = testOnReturn;
    }
    
    public boolean getDefaultTestWhileIdle() {
        return this.defaultTestWhileIdle;
    }
    
    public void setDefaultTestWhileIdle(final boolean testWhileIdle) {
        this.assertInitializationAllowed();
        this.defaultTestWhileIdle = testWhileIdle;
    }
    
    public long getDefaultTimeBetweenEvictionRunsMillis() {
        return this.defaultTimeBetweenEvictionRunsMillis;
    }
    
    public void setDefaultTimeBetweenEvictionRunsMillis(final long timeBetweenEvictionRunsMillis) {
        this.assertInitializationAllowed();
        this.defaultTimeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }
    
    public ConnectionPoolDataSource getConnectionPoolDataSource() {
        return this.dataSource;
    }
    
    public void setConnectionPoolDataSource(final ConnectionPoolDataSource dataSource) {
        this.assertInitializationAllowed();
        if (this.dataSourceName != null) {
            throw new IllegalStateException("Cannot set the DataSource, if JNDI is used.");
        }
        if (this.dataSource != null) {
            throw new IllegalStateException("The CPDS has already been set. It cannot be altered.");
        }
        this.dataSource = dataSource;
        this.instanceKey = InstanceKeyDataSourceFactory.registerNewInstance(this);
    }
    
    public String getDataSourceName() {
        return this.dataSourceName;
    }
    
    public void setDataSourceName(final String dataSourceName) {
        this.assertInitializationAllowed();
        if (this.dataSource != null) {
            throw new IllegalStateException("Cannot set the JNDI name for the DataSource, if already set using setConnectionPoolDataSource.");
        }
        if (this.dataSourceName != null) {
            throw new IllegalStateException("The DataSourceName has already been set. It cannot be altered.");
        }
        this.dataSourceName = dataSourceName;
        this.instanceKey = InstanceKeyDataSourceFactory.registerNewInstance(this);
    }
    
    public Boolean isDefaultAutoCommit() {
        return this.defaultAutoCommit;
    }
    
    public void setDefaultAutoCommit(final Boolean defaultAutoCommit) {
        this.assertInitializationAllowed();
        this.defaultAutoCommit = defaultAutoCommit;
    }
    
    public Boolean isDefaultReadOnly() {
        return this.defaultReadOnly;
    }
    
    public void setDefaultReadOnly(final Boolean defaultReadOnly) {
        this.assertInitializationAllowed();
        this.defaultReadOnly = defaultReadOnly;
    }
    
    public int getDefaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }
    
    public void setDefaultTransactionIsolation(final int defaultTransactionIsolation) {
        this.assertInitializationAllowed();
        switch (defaultTransactionIsolation) {
            case 0:
            case 1:
            case 2:
            case 4:
            case 8: {
                this.defaultTransactionIsolation = defaultTransactionIsolation;
                return;
            }
            default: {
                throw new IllegalArgumentException("The requested TransactionIsolation level is invalid.");
            }
        }
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    protected String getInstanceKey() {
        return this.instanceKey;
    }
    
    public String getJndiEnvironment(final String key) {
        String value = null;
        if (this.jndiEnvironment != null) {
            value = this.jndiEnvironment.getProperty(key);
        }
        return value;
    }
    
    public void setJndiEnvironment(final String key, final String value) {
        if (this.jndiEnvironment == null) {
            this.jndiEnvironment = new Properties();
        }
        this.jndiEnvironment.setProperty(key, value);
    }
    
    void setJndiEnvironment(final Properties properties) {
        if (this.jndiEnvironment == null) {
            this.jndiEnvironment = new Properties();
        }
        else {
            this.jndiEnvironment.clear();
        }
        this.jndiEnvironment.putAll(properties);
    }
    
    @Override
    public int getLoginTimeout() {
        return this.loginTimeout;
    }
    
    @Override
    public void setLoginTimeout(final int loginTimeout) {
        this.loginTimeout = loginTimeout;
    }
    
    @Override
    public PrintWriter getLogWriter() {
        if (this.logWriter == null) {
            this.logWriter = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        }
        return this.logWriter;
    }
    
    @Override
    public void setLogWriter(final PrintWriter logWriter) {
        this.logWriter = logWriter;
    }
    
    public String getValidationQuery() {
        return this.validationQuery;
    }
    
    public void setValidationQuery(final String validationQuery) {
        this.assertInitializationAllowed();
        this.validationQuery = validationQuery;
    }
    
    public int getValidationQueryTimeout() {
        return this.validationQueryTimeoutSeconds;
    }
    
    public void setValidationQueryTimeout(final int validationQueryTimeoutSeconds) {
        this.validationQueryTimeoutSeconds = validationQueryTimeoutSeconds;
    }
    
    public boolean isRollbackAfterValidation() {
        return this.rollbackAfterValidation;
    }
    
    public void setRollbackAfterValidation(final boolean rollbackAfterValidation) {
        this.assertInitializationAllowed();
        this.rollbackAfterValidation = rollbackAfterValidation;
    }
    
    public long getMaxConnLifetimeMillis() {
        return this.maxConnLifetimeMillis;
    }
    
    public void setMaxConnLifetimeMillis(final long maxConnLifetimeMillis) {
        this.maxConnLifetimeMillis = maxConnLifetimeMillis;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return this.getConnection(null, null);
    }
    
    @Override
    public Connection getConnection(final String userName, final String userPassword) throws SQLException {
        if (this.instanceKey == null) {
            throw new SQLException("Must set the ConnectionPoolDataSource through setDataSourceName or setConnectionPoolDataSource before calling getConnection.");
        }
        this.getConnectionCalled = true;
        PooledConnectionAndInfo info = null;
        try {
            info = this.getPooledConnectionAndInfo(userName, userPassword);
        }
        catch (final RuntimeException | SQLException e) {
            this.closeDueToException(info);
            throw e;
        }
        catch (final Exception e) {
            this.closeDueToException(info);
            throw new SQLException("Cannot borrow connection from pool", e);
        }
        Label_0280: {
            if (null == userPassword) {
                if (null == info.getPassword()) {
                    break Label_0280;
                }
            }
            else if (userPassword.equals(info.getPassword())) {
                break Label_0280;
            }
            try {
                this.testCPDS(userName, userPassword);
            }
            catch (final SQLException ex) {
                this.closeDueToException(info);
                throw new SQLException("Given password did not match password used to create the PooledConnection.", ex);
            }
            catch (final NamingException ne) {
                throw new SQLException("NamingException encountered connecting to database", ne);
            }
            final UserPassKey upkey = info.getUserPassKey();
            final PooledConnectionManager manager = this.getConnectionManager(upkey);
            manager.invalidate(info.getPooledConnection());
            manager.setPassword(upkey.getPassword());
            info = null;
            for (int i = 0; i < 10; ++i) {
                try {
                    info = this.getPooledConnectionAndInfo(userName, userPassword);
                }
                catch (final RuntimeException | SQLException e2) {
                    this.closeDueToException(info);
                    throw e2;
                }
                catch (final Exception e2) {
                    this.closeDueToException(info);
                    throw new SQLException("Cannot borrow connection from pool", e2);
                }
                if (info != null && userPassword != null && userPassword.equals(info.getPassword())) {
                    break;
                }
                if (info != null) {
                    manager.invalidate(info.getPooledConnection());
                }
                info = null;
            }
            if (info == null) {
                throw new SQLException("Cannot borrow connection from pool - password change failure.");
            }
        }
        final Connection con = info.getPooledConnection().getConnection();
        try {
            this.setupDefaults(con, userName);
            con.clearWarnings();
            return con;
        }
        catch (final SQLException ex2) {
            try {
                con.close();
            }
            catch (final Exception exc) {
                this.getLogWriter().println("ignoring exception during close: " + exc);
            }
            throw ex2;
        }
    }
    
    protected abstract PooledConnectionAndInfo getPooledConnectionAndInfo(final String p0, final String p1) throws SQLException;
    
    protected abstract void setupDefaults(final Connection p0, final String p1) throws SQLException;
    
    private void closeDueToException(final PooledConnectionAndInfo info) {
        if (info != null) {
            try {
                info.getPooledConnection().getConnection().close();
            }
            catch (final Exception e) {
                this.getLogWriter().println("[ERROR] Could not return connection to pool during exception handling. " + e.getMessage());
            }
        }
    }
    
    protected ConnectionPoolDataSource testCPDS(final String userName, final String userPassword) throws NamingException, SQLException {
        ConnectionPoolDataSource cpds = this.dataSource;
        if (cpds == null) {
            Context ctx = null;
            if (this.jndiEnvironment == null) {
                ctx = new InitialContext();
            }
            else {
                ctx = new InitialContext(this.jndiEnvironment);
            }
            final Object ds = ctx.lookup(this.dataSourceName);
            if (!(ds instanceof ConnectionPoolDataSource)) {
                throw new SQLException("Illegal configuration: DataSource " + this.dataSourceName + " (" + ds.getClass().getName() + ")" + " doesn't implement javax.sql.ConnectionPoolDataSource");
            }
            cpds = (ConnectionPoolDataSource)ds;
        }
        PooledConnection conn = null;
        try {
            if (userName != null) {
                conn = cpds.getPooledConnection(userName, userPassword);
            }
            else {
                conn = cpds.getPooledConnection();
            }
            if (conn == null) {
                throw new SQLException("Cannot connect using the supplied userName/password");
            }
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (final SQLException ex) {}
            }
        }
        return cpds;
    }
    
    @Override
    public synchronized String toString() {
        final StringBuilder builder = new StringBuilder(super.toString());
        builder.append('[');
        this.toStringFields(builder);
        builder.append(']');
        return builder.toString();
    }
    
    protected void toStringFields(final StringBuilder builder) {
        builder.append("getConnectionCalled=");
        builder.append(this.getConnectionCalled);
        builder.append(", dataSource=");
        builder.append(this.dataSource);
        builder.append(", dataSourceName=");
        builder.append(this.dataSourceName);
        builder.append(", description=");
        builder.append(this.description);
        builder.append(", jndiEnvironment=");
        builder.append(this.jndiEnvironment);
        builder.append(", loginTimeout=");
        builder.append(this.loginTimeout);
        builder.append(", logWriter=");
        builder.append(this.logWriter);
        builder.append(", instanceKey=");
        builder.append(this.instanceKey);
        builder.append(", defaultBlockWhenExhausted=");
        builder.append(this.defaultBlockWhenExhausted);
        builder.append(", defaultEvictionPolicyClassName=");
        builder.append(this.defaultEvictionPolicyClassName);
        builder.append(", defaultLifo=");
        builder.append(this.defaultLifo);
        builder.append(", defaultMaxIdle=");
        builder.append(this.defaultMaxIdle);
        builder.append(", defaultMaxTotal=");
        builder.append(this.defaultMaxTotal);
        builder.append(", defaultMaxWaitMillis=");
        builder.append(this.defaultMaxWaitMillis);
        builder.append(", defaultMinEvictableIdleTimeMillis=");
        builder.append(this.defaultMinEvictableIdleTimeMillis);
        builder.append(", defaultMinIdle=");
        builder.append(this.defaultMinIdle);
        builder.append(", defaultNumTestsPerEvictionRun=");
        builder.append(this.defaultNumTestsPerEvictionRun);
        builder.append(", defaultSoftMinEvictableIdleTimeMillis=");
        builder.append(this.defaultSoftMinEvictableIdleTimeMillis);
        builder.append(", defaultTestOnCreate=");
        builder.append(this.defaultTestOnCreate);
        builder.append(", defaultTestOnBorrow=");
        builder.append(this.defaultTestOnBorrow);
        builder.append(", defaultTestOnReturn=");
        builder.append(this.defaultTestOnReturn);
        builder.append(", defaultTestWhileIdle=");
        builder.append(this.defaultTestWhileIdle);
        builder.append(", defaultTimeBetweenEvictionRunsMillis=");
        builder.append(this.defaultTimeBetweenEvictionRunsMillis);
        builder.append(", validationQuery=");
        builder.append(this.validationQuery);
        builder.append(", validationQueryTimeoutSeconds=");
        builder.append(this.validationQueryTimeoutSeconds);
        builder.append(", rollbackAfterValidation=");
        builder.append(this.rollbackAfterValidation);
        builder.append(", maxConnLifetimeMillis=");
        builder.append(this.maxConnLifetimeMillis);
        builder.append(", defaultAutoCommit=");
        builder.append(this.defaultAutoCommit);
        builder.append(", defaultTransactionIsolation=");
        builder.append(this.defaultTransactionIsolation);
        builder.append(", defaultReadOnly=");
        builder.append(this.defaultReadOnly);
    }
}
