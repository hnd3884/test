package org.apache.tomcat.dbcp.dbcp2.cpdsadapter;

import javax.naming.NamingException;
import javax.naming.StringRefAddr;
import org.apache.tomcat.dbcp.pool2.KeyedObjectPool;
import org.apache.tomcat.dbcp.pool2.KeyedPooledObjectFactory;
import org.apache.tomcat.dbcp.dbcp2.PStmtKey;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPool;
import org.apache.tomcat.dbcp.dbcp2.DelegatingPreparedStatement;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPoolConfig;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.PooledConnection;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.naming.Reference;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import java.util.Properties;
import java.io.PrintWriter;
import javax.naming.spi.ObjectFactory;
import java.io.Serializable;
import javax.naming.Referenceable;
import javax.sql.ConnectionPoolDataSource;

public class DriverAdapterCPDS implements ConnectionPoolDataSource, Referenceable, Serializable, ObjectFactory
{
    private static final long serialVersionUID = -4820523787212147844L;
    private static final String GET_CONNECTION_CALLED = "A PooledConnection was already requested from this source, further initialization is not allowed.";
    private String description;
    private String url;
    private String userName;
    private char[] userPassword;
    private String driver;
    private int loginTimeout;
    private transient PrintWriter logWriter;
    private boolean poolPreparedStatements;
    private int maxIdle;
    private long timeBetweenEvictionRunsMillis;
    private int numTestsPerEvictionRun;
    private int minEvictableIdleTimeMillis;
    private int maxPreparedStatements;
    private volatile boolean getConnectionCalled;
    private Properties connectionProperties;
    private boolean accessToUnderlyingConnectionAllowed;
    
    public DriverAdapterCPDS() {
        this.maxIdle = 10;
        this.timeBetweenEvictionRunsMillis = -1L;
        this.numTestsPerEvictionRun = -1;
        this.minEvictableIdleTimeMillis = -1;
        this.maxPreparedStatements = -1;
    }
    
    private void assertInitializationAllowed() throws IllegalStateException {
        if (this.getConnectionCalled) {
            throw new IllegalStateException("A PooledConnection was already requested from this source, further initialization is not allowed.");
        }
    }
    
    private boolean getBooleanContentString(final RefAddr ra) {
        return Boolean.parseBoolean(this.getStringContent(ra));
    }
    
    public Properties getConnectionProperties() {
        return this.connectionProperties;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getDriver() {
        return this.driver;
    }
    
    private int getIntegerStringContent(final RefAddr ra) {
        return Integer.parseInt(this.getStringContent(ra));
    }
    
    @Override
    public int getLoginTimeout() {
        return this.loginTimeout;
    }
    
    @Override
    public PrintWriter getLogWriter() {
        return this.logWriter;
    }
    
    public int getMaxIdle() {
        return this.maxIdle;
    }
    
    public int getMaxPreparedStatements() {
        return this.maxPreparedStatements;
    }
    
    public int getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }
    
    public int getNumTestsPerEvictionRun() {
        return this.numTestsPerEvictionRun;
    }
    
    @Override
    public Object getObjectInstance(final Object refObj, final Name name, final Context context, final Hashtable<?, ?> env) throws Exception {
        DriverAdapterCPDS cpds = null;
        if (refObj instanceof Reference) {
            final Reference ref = (Reference)refObj;
            if (ref.getClassName().equals(this.getClass().getName())) {
                RefAddr ra = ref.get("description");
                if (this.isNotEmpty(ra)) {
                    this.setDescription(this.getStringContent(ra));
                }
                ra = ref.get("driver");
                if (this.isNotEmpty(ra)) {
                    this.setDriver(this.getStringContent(ra));
                }
                ra = ref.get("url");
                if (this.isNotEmpty(ra)) {
                    this.setUrl(this.getStringContent(ra));
                }
                ra = ref.get("user");
                if (this.isNotEmpty(ra)) {
                    this.setUser(this.getStringContent(ra));
                }
                ra = ref.get("password");
                if (this.isNotEmpty(ra)) {
                    this.setPassword(this.getStringContent(ra));
                }
                ra = ref.get("poolPreparedStatements");
                if (this.isNotEmpty(ra)) {
                    this.setPoolPreparedStatements(this.getBooleanContentString(ra));
                }
                ra = ref.get("maxIdle");
                if (this.isNotEmpty(ra)) {
                    this.setMaxIdle(this.getIntegerStringContent(ra));
                }
                ra = ref.get("timeBetweenEvictionRunsMillis");
                if (this.isNotEmpty(ra)) {
                    this.setTimeBetweenEvictionRunsMillis(this.getIntegerStringContent(ra));
                }
                ra = ref.get("numTestsPerEvictionRun");
                if (this.isNotEmpty(ra)) {
                    this.setNumTestsPerEvictionRun(this.getIntegerStringContent(ra));
                }
                ra = ref.get("minEvictableIdleTimeMillis");
                if (this.isNotEmpty(ra)) {
                    this.setMinEvictableIdleTimeMillis(this.getIntegerStringContent(ra));
                }
                ra = ref.get("maxPreparedStatements");
                if (this.isNotEmpty(ra)) {
                    this.setMaxPreparedStatements(this.getIntegerStringContent(ra));
                }
                ra = ref.get("accessToUnderlyingConnectionAllowed");
                if (this.isNotEmpty(ra)) {
                    this.setAccessToUnderlyingConnectionAllowed(this.getBooleanContentString(ra));
                }
                cpds = this;
            }
        }
        return cpds;
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
    
    public String getPassword() {
        return Utils.toString(this.userPassword);
    }
    
    public char[] getPasswordCharArray() {
        return (char[])((this.userPassword == null) ? null : ((char[])this.userPassword.clone()));
    }
    
    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        return this.getPooledConnection(this.getUser(), this.getPassword());
    }
    
    @Override
    public PooledConnection getPooledConnection(final String pooledUserName, final String pooledUserPassword) throws SQLException {
        this.getConnectionCalled = true;
        PooledConnectionImpl pooledConnection = null;
        try {
            if (this.connectionProperties != null) {
                this.update(this.connectionProperties, "user", pooledUserName);
                this.update(this.connectionProperties, "password", pooledUserPassword);
                pooledConnection = new PooledConnectionImpl(DriverManager.getConnection(this.getUrl(), this.connectionProperties));
            }
            else {
                pooledConnection = new PooledConnectionImpl(DriverManager.getConnection(this.getUrl(), pooledUserName, pooledUserPassword));
            }
            pooledConnection.setAccessToUnderlyingConnectionAllowed(this.isAccessToUnderlyingConnectionAllowed());
        }
        catch (final ClassCircularityError e) {
            if (this.connectionProperties != null) {
                pooledConnection = new PooledConnectionImpl(DriverManager.getConnection(this.getUrl(), this.connectionProperties));
            }
            else {
                pooledConnection = new PooledConnectionImpl(DriverManager.getConnection(this.getUrl(), pooledUserName, pooledUserPassword));
            }
            pooledConnection.setAccessToUnderlyingConnectionAllowed(this.isAccessToUnderlyingConnectionAllowed());
        }
        KeyedObjectPool<PStmtKey, DelegatingPreparedStatement> stmtPool = null;
        if (this.isPoolPreparedStatements()) {
            final GenericKeyedObjectPoolConfig<DelegatingPreparedStatement> config = new GenericKeyedObjectPoolConfig<DelegatingPreparedStatement>();
            config.setMaxTotalPerKey(Integer.MAX_VALUE);
            config.setBlockWhenExhausted(false);
            config.setMaxWaitMillis(0L);
            config.setMaxIdlePerKey(this.getMaxIdle());
            if (this.getMaxPreparedStatements() <= 0) {
                config.setTimeBetweenEvictionRunsMillis(this.getTimeBetweenEvictionRunsMillis());
                config.setNumTestsPerEvictionRun(this.getNumTestsPerEvictionRun());
                config.setMinEvictableIdleTimeMillis(this.getMinEvictableIdleTimeMillis());
            }
            else {
                config.setMaxTotal(this.getMaxPreparedStatements());
                config.setTimeBetweenEvictionRunsMillis(-1L);
                config.setNumTestsPerEvictionRun(0);
                config.setMinEvictableIdleTimeMillis(0L);
            }
            stmtPool = new GenericKeyedObjectPool<PStmtKey, DelegatingPreparedStatement>(pooledConnection, config);
            pooledConnection.setStatementPool(stmtPool);
        }
        return pooledConnection;
    }
    
    @Override
    public Reference getReference() throws NamingException {
        final String factory = this.getClass().getName();
        final Reference ref = new Reference(this.getClass().getName(), factory, null);
        ref.add(new StringRefAddr("description", this.getDescription()));
        ref.add(new StringRefAddr("driver", this.getDriver()));
        ref.add(new StringRefAddr("loginTimeout", String.valueOf(this.getLoginTimeout())));
        ref.add(new StringRefAddr("password", this.getPassword()));
        ref.add(new StringRefAddr("user", this.getUser()));
        ref.add(new StringRefAddr("url", this.getUrl()));
        ref.add(new StringRefAddr("poolPreparedStatements", String.valueOf(this.isPoolPreparedStatements())));
        ref.add(new StringRefAddr("maxIdle", String.valueOf(this.getMaxIdle())));
        ref.add(new StringRefAddr("timeBetweenEvictionRunsMillis", String.valueOf(this.getTimeBetweenEvictionRunsMillis())));
        ref.add(new StringRefAddr("numTestsPerEvictionRun", String.valueOf(this.getNumTestsPerEvictionRun())));
        ref.add(new StringRefAddr("minEvictableIdleTimeMillis", String.valueOf(this.getMinEvictableIdleTimeMillis())));
        ref.add(new StringRefAddr("maxPreparedStatements", String.valueOf(this.getMaxPreparedStatements())));
        return ref;
    }
    
    private String getStringContent(final RefAddr ra) {
        return ra.getContent().toString();
    }
    
    public long getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public String getUser() {
        return this.userName;
    }
    
    public synchronized boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }
    
    private boolean isNotEmpty(final RefAddr ra) {
        return ra != null && ra.getContent() != null;
    }
    
    public boolean isPoolPreparedStatements() {
        return this.poolPreparedStatements;
    }
    
    public synchronized void setAccessToUnderlyingConnectionAllowed(final boolean allow) {
        this.accessToUnderlyingConnectionAllowed = allow;
    }
    
    public void setConnectionProperties(final Properties props) {
        this.assertInitializationAllowed();
        this.connectionProperties = props;
        if (this.connectionProperties != null) {
            if (this.connectionProperties.containsKey("user")) {
                this.setUser(this.connectionProperties.getProperty("user"));
            }
            if (this.connectionProperties.containsKey("password")) {
                this.setPassword(this.connectionProperties.getProperty("password"));
            }
        }
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public void setDriver(final String driver) throws ClassNotFoundException {
        this.assertInitializationAllowed();
        Class.forName(this.driver = driver);
    }
    
    @Override
    public void setLoginTimeout(final int seconds) {
        this.loginTimeout = seconds;
    }
    
    @Override
    public void setLogWriter(final PrintWriter logWriter) {
        this.logWriter = logWriter;
    }
    
    public void setMaxIdle(final int maxIdle) {
        this.assertInitializationAllowed();
        this.maxIdle = maxIdle;
    }
    
    public void setMaxPreparedStatements(final int maxPreparedStatements) {
        this.maxPreparedStatements = maxPreparedStatements;
    }
    
    public void setMinEvictableIdleTimeMillis(final int minEvictableIdleTimeMillis) {
        this.assertInitializationAllowed();
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }
    
    public void setNumTestsPerEvictionRun(final int numTestsPerEvictionRun) {
        this.assertInitializationAllowed();
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }
    
    public void setPassword(final char[] userPassword) {
        this.assertInitializationAllowed();
        this.userPassword = Utils.clone(userPassword);
        this.update(this.connectionProperties, "password", Utils.toString(this.userPassword));
    }
    
    public void setPassword(final String userPassword) {
        this.assertInitializationAllowed();
        this.userPassword = Utils.toCharArray(userPassword);
        this.update(this.connectionProperties, "password", userPassword);
    }
    
    public void setPoolPreparedStatements(final boolean poolPreparedStatements) {
        this.assertInitializationAllowed();
        this.poolPreparedStatements = poolPreparedStatements;
    }
    
    public void setTimeBetweenEvictionRunsMillis(final long timeBetweenEvictionRunsMillis) {
        this.assertInitializationAllowed();
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }
    
    public void setUrl(final String url) {
        this.assertInitializationAllowed();
        this.url = url;
    }
    
    public void setUser(final String userName) {
        this.assertInitializationAllowed();
        this.userName = userName;
        this.update(this.connectionProperties, "user", userName);
    }
    
    @Override
    public synchronized String toString() {
        final StringBuilder builder = new StringBuilder(super.toString());
        builder.append("[description=");
        builder.append(this.description);
        builder.append(", url=");
        builder.append(this.url);
        builder.append(", driver=");
        builder.append(this.driver);
        builder.append(", loginTimeout=");
        builder.append(this.loginTimeout);
        builder.append(", poolPreparedStatements=");
        builder.append(this.poolPreparedStatements);
        builder.append(", maxIdle=");
        builder.append(this.maxIdle);
        builder.append(", timeBetweenEvictionRunsMillis=");
        builder.append(this.timeBetweenEvictionRunsMillis);
        builder.append(", numTestsPerEvictionRun=");
        builder.append(this.numTestsPerEvictionRun);
        builder.append(", minEvictableIdleTimeMillis=");
        builder.append(this.minEvictableIdleTimeMillis);
        builder.append(", maxPreparedStatements=");
        builder.append(this.maxPreparedStatements);
        builder.append(", getConnectionCalled=");
        builder.append(this.getConnectionCalled);
        builder.append(", connectionProperties=");
        builder.append(Utils.cloneWithoutCredentials(this.connectionProperties));
        builder.append(", accessToUnderlyingConnectionAllowed=");
        builder.append(this.accessToUnderlyingConnectionAllowed);
        builder.append("]");
        return builder.toString();
    }
    
    private void update(final Properties properties, final String key, final String value) {
        if (properties != null && key != null) {
            if (value == null) {
                properties.remove(key);
            }
            else {
                properties.setProperty(key, value);
            }
        }
    }
    
    static {
        DriverManager.getDrivers();
    }
}
