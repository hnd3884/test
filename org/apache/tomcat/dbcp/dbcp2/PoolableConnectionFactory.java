package org.apache.tomcat.dbcp.dbcp2;

import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.dbcp.pool2.KeyedObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.DefaultPooledObject;
import org.apache.tomcat.dbcp.pool2.KeyedPooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.GenericKeyedObjectPoolConfig;
import java.util.Iterator;
import java.sql.Statement;
import java.util.Objects;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.pool2.DestroyMode;
import java.sql.Connection;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.tomcat.dbcp.pool2.ObjectPool;
import java.util.Collection;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.tomcat.dbcp.pool2.PooledObjectFactory;

public class PoolableConnectionFactory implements PooledObjectFactory<PoolableConnection>
{
    private static final Log log;
    static final int UNKNOWN_TRANSACTION_ISOLATION = -1;
    private final ConnectionFactory connectionFactory;
    private final ObjectName dataSourceJmxObjectName;
    private volatile String validationQuery;
    private volatile int validationQueryTimeoutSeconds;
    private Collection<String> connectionInitSqls;
    private Collection<String> disconnectionSqlCodes;
    private boolean fastFailValidation;
    private volatile ObjectPool<PoolableConnection> pool;
    private Boolean defaultReadOnly;
    private Boolean defaultAutoCommit;
    private boolean autoCommitOnReturn;
    private boolean rollbackOnReturn;
    private int defaultTransactionIsolation;
    private String defaultCatalog;
    private String defaultSchema;
    private boolean cacheState;
    private boolean poolStatements;
    private boolean clearStatementPoolOnReturn;
    private int maxOpenPreparedStatements;
    private long maxConnLifetimeMillis;
    private final AtomicLong connectionIndex;
    private Integer defaultQueryTimeoutSeconds;
    
    public PoolableConnectionFactory(final ConnectionFactory connFactory, final ObjectName dataSourceJmxObjectName) {
        this.validationQueryTimeoutSeconds = -1;
        this.fastFailValidation = true;
        this.autoCommitOnReturn = true;
        this.rollbackOnReturn = true;
        this.defaultTransactionIsolation = -1;
        this.maxOpenPreparedStatements = 8;
        this.maxConnLifetimeMillis = -1L;
        this.connectionIndex = new AtomicLong(0L);
        this.connectionFactory = connFactory;
        this.dataSourceJmxObjectName = dataSourceJmxObjectName;
    }
    
    @Override
    public void activateObject(final PooledObject<PoolableConnection> p) throws Exception {
        this.validateLifetime(p);
        final PoolableConnection conn = p.getObject();
        conn.activate();
        if (this.defaultAutoCommit != null && conn.getAutoCommit() != this.defaultAutoCommit) {
            conn.setAutoCommit(this.defaultAutoCommit);
        }
        if (this.defaultTransactionIsolation != -1 && conn.getTransactionIsolation() != this.defaultTransactionIsolation) {
            conn.setTransactionIsolation(this.defaultTransactionIsolation);
        }
        if (this.defaultReadOnly != null && conn.isReadOnly() != this.defaultReadOnly) {
            conn.setReadOnly(this.defaultReadOnly);
        }
        if (this.defaultCatalog != null && !this.defaultCatalog.equals(conn.getCatalog())) {
            conn.setCatalog(this.defaultCatalog);
        }
        if (this.defaultSchema != null && !this.defaultSchema.equals(Jdbc41Bridge.getSchema(conn))) {
            Jdbc41Bridge.setSchema(conn, this.defaultSchema);
        }
        conn.setDefaultQueryTimeout(this.defaultQueryTimeoutSeconds);
    }
    
    @Override
    public void destroyObject(final PooledObject<PoolableConnection> p) throws Exception {
        p.getObject().reallyClose();
    }
    
    @Override
    public void destroyObject(final PooledObject<PoolableConnection> p, final DestroyMode mode) throws Exception {
        this.destroyObject(p);
    }
    
    public boolean getCacheState() {
        return this.cacheState;
    }
    
    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }
    
    protected AtomicLong getConnectionIndex() {
        return this.connectionIndex;
    }
    
    public Collection<String> getConnectionInitSqls() {
        return this.connectionInitSqls;
    }
    
    public ObjectName getDataSourceJmxName() {
        return this.dataSourceJmxObjectName;
    }
    
    public ObjectName getDataSourceJmxObjectName() {
        return this.dataSourceJmxObjectName;
    }
    
    public Boolean getDefaultAutoCommit() {
        return this.defaultAutoCommit;
    }
    
    public String getDefaultCatalog() {
        return this.defaultCatalog;
    }
    
    public Integer getDefaultQueryTimeout() {
        return this.defaultQueryTimeoutSeconds;
    }
    
    public Integer getDefaultQueryTimeoutSeconds() {
        return this.defaultQueryTimeoutSeconds;
    }
    
    public Boolean getDefaultReadOnly() {
        return this.defaultReadOnly;
    }
    
    public String getDefaultSchema() {
        return this.defaultSchema;
    }
    
    public int getDefaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }
    
    public Collection<String> getDisconnectionSqlCodes() {
        return this.disconnectionSqlCodes;
    }
    
    public long getMaxConnLifetimeMillis() {
        return this.maxConnLifetimeMillis;
    }
    
    protected int getMaxOpenPreparedStatements() {
        return this.maxOpenPreparedStatements;
    }
    
    public synchronized ObjectPool<PoolableConnection> getPool() {
        return this.pool;
    }
    
    public boolean getPoolStatements() {
        return this.poolStatements;
    }
    
    public String getValidationQuery() {
        return this.validationQuery;
    }
    
    public int getValidationQueryTimeoutSeconds() {
        return this.validationQueryTimeoutSeconds;
    }
    
    protected void initializeConnection(final Connection conn) throws SQLException {
        final Collection<String> sqls = this.connectionInitSqls;
        if (conn.isClosed()) {
            throw new SQLException("initializeConnection: connection closed");
        }
        if (null != sqls) {
            try (final Statement stmt = conn.createStatement()) {
                for (final String sql : sqls) {
                    Objects.requireNonNull(sql, "null connectionInitSqls element");
                    stmt.execute(sql);
                }
            }
        }
    }
    
    public boolean isAutoCommitOnReturn() {
        return this.autoCommitOnReturn;
    }
    
    @Deprecated
    public boolean isEnableAutoCommitOnReturn() {
        return this.autoCommitOnReturn;
    }
    
    public boolean isFastFailValidation() {
        return this.fastFailValidation;
    }
    
    public boolean isRollbackOnReturn() {
        return this.rollbackOnReturn;
    }
    
    @Override
    public PooledObject<PoolableConnection> makeObject() throws Exception {
        Connection conn = this.connectionFactory.createConnection();
        if (conn == null) {
            throw new IllegalStateException("Connection factory returned null from createConnection");
        }
        try {
            this.initializeConnection(conn);
        }
        catch (final SQLException sqle) {
            try {
                conn.close();
            }
            catch (final SQLException ex) {}
            throw sqle;
        }
        final long connIndex = this.connectionIndex.getAndIncrement();
        if (this.poolStatements) {
            conn = new PoolingConnection(conn);
            final GenericKeyedObjectPoolConfig<DelegatingPreparedStatement> config = new GenericKeyedObjectPoolConfig<DelegatingPreparedStatement>();
            config.setMaxTotalPerKey(-1);
            config.setBlockWhenExhausted(false);
            config.setMaxWaitMillis(0L);
            config.setMaxIdlePerKey(1);
            config.setMaxTotal(this.maxOpenPreparedStatements);
            if (this.dataSourceJmxObjectName != null) {
                final StringBuilder base = new StringBuilder(this.dataSourceJmxObjectName.toString());
                base.append(",connectionpool=connections,connection=");
                base.append(connIndex);
                config.setJmxNameBase(base.toString());
                config.setJmxNamePrefix(",statementpool=statements");
            }
            else {
                config.setJmxEnabled(false);
            }
            final PoolingConnection poolingConn = (PoolingConnection)conn;
            final KeyedObjectPool<PStmtKey, DelegatingPreparedStatement> stmtPool = new GenericKeyedObjectPool<PStmtKey, DelegatingPreparedStatement>(poolingConn, config);
            poolingConn.setStatementPool(stmtPool);
            poolingConn.setClearStatementPoolOnReturn(this.clearStatementPoolOnReturn);
            poolingConn.setCacheState(this.cacheState);
        }
        ObjectName connJmxName;
        if (this.dataSourceJmxObjectName == null) {
            connJmxName = null;
        }
        else {
            connJmxName = new ObjectName(this.dataSourceJmxObjectName.toString() + ",connectionpool=connections,connection=" + connIndex);
        }
        final PoolableConnection pc = new PoolableConnection(conn, this.pool, connJmxName, this.disconnectionSqlCodes, this.fastFailValidation);
        pc.setCacheState(this.cacheState);
        return new DefaultPooledObject<PoolableConnection>(pc);
    }
    
    @Override
    public void passivateObject(final PooledObject<PoolableConnection> p) throws Exception {
        this.validateLifetime(p);
        final PoolableConnection conn = p.getObject();
        Boolean connAutoCommit = null;
        if (this.rollbackOnReturn) {
            connAutoCommit = conn.getAutoCommit();
            if (!connAutoCommit && !conn.isReadOnly()) {
                conn.rollback();
            }
        }
        conn.clearWarnings();
        if (this.autoCommitOnReturn) {
            if (connAutoCommit == null) {
                connAutoCommit = conn.getAutoCommit();
            }
            if (!connAutoCommit) {
                conn.setAutoCommit(true);
            }
        }
        conn.passivate();
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
    
    public void setConnectionInitSql(final Collection<String> connectionInitSqls) {
        this.connectionInitSqls = connectionInitSqls;
    }
    
    public void setDefaultAutoCommit(final Boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }
    
    public void setDefaultCatalog(final String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }
    
    public void setDefaultQueryTimeout(final Integer defaultQueryTimeoutSeconds) {
        this.defaultQueryTimeoutSeconds = defaultQueryTimeoutSeconds;
    }
    
    public void setDefaultReadOnly(final Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }
    
    public void setDefaultSchema(final String defaultSchema) {
        this.defaultSchema = defaultSchema;
    }
    
    public void setDefaultTransactionIsolation(final int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }
    
    public void setDisconnectionSqlCodes(final Collection<String> disconnectionSqlCodes) {
        this.disconnectionSqlCodes = disconnectionSqlCodes;
    }
    
    @Deprecated
    public void setEnableAutoCommitOnReturn(final boolean autoCommitOnReturn) {
        this.autoCommitOnReturn = autoCommitOnReturn;
    }
    
    public void setFastFailValidation(final boolean fastFailValidation) {
        this.fastFailValidation = fastFailValidation;
    }
    
    public void setMaxConnLifetimeMillis(final long maxConnLifetimeMillis) {
        this.maxConnLifetimeMillis = maxConnLifetimeMillis;
    }
    
    public void setMaxOpenPreparedStatements(final int maxOpenPreparedStatements) {
        this.maxOpenPreparedStatements = maxOpenPreparedStatements;
    }
    
    @Deprecated
    public void setMaxOpenPrepatedStatements(final int maxOpenPreparedStatements) {
        this.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
    }
    
    public synchronized void setPool(final ObjectPool<PoolableConnection> pool) {
        if (null != this.pool && pool != this.pool) {
            try {
                this.pool.close();
            }
            catch (final Exception ex) {}
        }
        this.pool = pool;
    }
    
    public void setPoolStatements(final boolean poolStatements) {
        this.poolStatements = poolStatements;
    }
    
    public void setRollbackOnReturn(final boolean rollbackOnReturn) {
        this.rollbackOnReturn = rollbackOnReturn;
    }
    
    public void setValidationQuery(final String validationQuery) {
        this.validationQuery = validationQuery;
    }
    
    public void setValidationQueryTimeout(final int validationQueryTimeoutSeconds) {
        this.validationQueryTimeoutSeconds = validationQueryTimeoutSeconds;
    }
    
    public void validateConnection(final PoolableConnection conn) throws SQLException {
        if (conn.isClosed()) {
            throw new SQLException("validateConnection: connection closed");
        }
        conn.validate(this.validationQuery, this.validationQueryTimeoutSeconds);
    }
    
    private void validateLifetime(final PooledObject<PoolableConnection> p) throws Exception {
        if (this.maxConnLifetimeMillis > 0L) {
            final long lifetimeMillis = System.currentTimeMillis() - p.getCreateTime();
            if (lifetimeMillis > this.maxConnLifetimeMillis) {
                throw new LifetimeExceededException(Utils.getMessage("connectionFactory.lifetimeExceeded", lifetimeMillis, this.maxConnLifetimeMillis));
            }
        }
    }
    
    @Override
    public boolean validateObject(final PooledObject<PoolableConnection> p) {
        try {
            this.validateLifetime(p);
            this.validateConnection(p.getObject());
            return true;
        }
        catch (final Exception e) {
            if (PoolableConnectionFactory.log.isDebugEnabled()) {
                PoolableConnectionFactory.log.debug((Object)Utils.getMessage("poolableConnectionFactory.validateObject.fail"), (Throwable)e);
            }
            return false;
        }
    }
    
    static {
        log = LogFactory.getLog((Class)PoolableConnectionFactory.class);
    }
}
