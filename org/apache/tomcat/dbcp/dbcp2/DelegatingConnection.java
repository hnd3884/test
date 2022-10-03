package org.apache.tomcat.dbcp.dbcp2;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.Properties;
import java.sql.SQLClientInfoException;
import java.sql.Struct;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Array;
import java.sql.Savepoint;
import java.util.Iterator;
import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.SQLWarning;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.ClientInfoStatus;
import java.util.Map;
import java.sql.Connection;

public class DelegatingConnection<C extends Connection> extends AbandonedTrace implements Connection
{
    private static final Map<String, ClientInfoStatus> EMPTY_FAILED_PROPERTIES;
    private volatile C connection;
    private volatile boolean closed;
    private boolean cacheState;
    private Boolean autoCommitCached;
    private Boolean readOnlyCached;
    private Integer defaultQueryTimeoutSeconds;
    
    public DelegatingConnection(final C c) {
        this.cacheState = true;
        this.connection = c;
    }
    
    @Override
    public synchronized String toString() {
        String str = null;
        final Connection conn = this.getInnermostDelegateInternal();
        if (conn != null) {
            try {
                if (conn.isClosed()) {
                    str = "connection is closed";
                }
                else {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(this.hashCode());
                    final DatabaseMetaData meta = conn.getMetaData();
                    if (meta != null) {
                        sb.append(", URL=");
                        sb.append(meta.getURL());
                        sb.append(", ");
                        sb.append(meta.getDriverName());
                        str = sb.toString();
                    }
                }
            }
            catch (final SQLException ex) {}
        }
        return (str != null) ? str : super.toString();
    }
    
    public C getDelegate() {
        return this.getDelegateInternal();
    }
    
    protected final C getDelegateInternal() {
        return this.connection;
    }
    
    public boolean innermostDelegateEquals(final Connection c) {
        final Connection innerCon = this.getInnermostDelegateInternal();
        if (innerCon == null) {
            return c == null;
        }
        return innerCon.equals(c);
    }
    
    public Connection getInnermostDelegate() {
        return this.getInnermostDelegateInternal();
    }
    
    public final Connection getInnermostDelegateInternal() {
        Connection conn = this.connection;
        while (conn instanceof DelegatingConnection) {
            conn = ((DelegatingConnection)conn).getDelegateInternal();
            if (this == conn) {
                return null;
            }
        }
        return conn;
    }
    
    public void setDelegate(final C connection) {
        this.connection = connection;
    }
    
    @Override
    public void close() throws SQLException {
        if (!this.closed) {
            this.closeInternal();
        }
    }
    
    protected boolean isClosedInternal() {
        return this.closed;
    }
    
    protected void setClosedInternal(final boolean closed) {
        this.closed = closed;
    }
    
    protected final void closeInternal() throws SQLException {
        try {
            this.passivate();
        }
        finally {
            if (this.connection != null) {
                boolean connectionIsClosed;
                try {
                    connectionIsClosed = this.connection.isClosed();
                }
                catch (final SQLException e) {
                    connectionIsClosed = false;
                }
                try {
                    if (!connectionIsClosed) {
                        this.connection.close();
                    }
                }
                finally {
                    this.closed = true;
                }
            }
            else {
                this.closed = true;
            }
        }
    }
    
    protected void handleException(final SQLException e) throws SQLException {
        throw e;
    }
    
    protected <T extends Throwable> T handleExceptionNoThrow(final T e) {
        return e;
    }
    
    private void initializeStatement(final DelegatingStatement ds) throws SQLException {
        if (this.defaultQueryTimeoutSeconds != null && this.defaultQueryTimeoutSeconds != ds.getQueryTimeout()) {
            ds.setQueryTimeout(this.defaultQueryTimeoutSeconds);
        }
    }
    
    @Override
    public Statement createStatement() throws SQLException {
        this.checkOpen();
        try {
            final DelegatingStatement ds = new DelegatingStatement(this, this.connection.createStatement());
            this.initializeStatement(ds);
            return ds;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        this.checkOpen();
        try {
            final DelegatingStatement ds = new DelegatingStatement(this, this.connection.createStatement(resultSetType, resultSetConcurrency));
            this.initializeStatement(ds);
            return ds;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        this.checkOpen();
        try {
            final DelegatingPreparedStatement dps = new DelegatingPreparedStatement(this, this.connection.prepareStatement(sql));
            this.initializeStatement(dps);
            return dps;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        this.checkOpen();
        try {
            final DelegatingPreparedStatement dps = new DelegatingPreparedStatement(this, this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency));
            this.initializeStatement(dps);
            return dps;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        this.checkOpen();
        try {
            final DelegatingCallableStatement dcs = new DelegatingCallableStatement(this, this.connection.prepareCall(sql));
            this.initializeStatement(dcs);
            return dcs;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        this.checkOpen();
        try {
            final DelegatingCallableStatement dcs = new DelegatingCallableStatement(this, this.connection.prepareCall(sql, resultSetType, resultSetConcurrency));
            this.initializeStatement(dcs);
            return dcs;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        this.checkOpen();
        try {
            this.connection.clearWarnings();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void commit() throws SQLException {
        this.checkOpen();
        try {
            this.connection.commit();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    public boolean getCacheState() {
        return this.cacheState;
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        this.checkOpen();
        if (this.cacheState && this.autoCommitCached != null) {
            return this.autoCommitCached;
        }
        try {
            this.autoCommitCached = this.connection.getAutoCommit();
            return this.autoCommitCached;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public String getCatalog() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.getCatalog();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingDatabaseMetaData(this, this.connection.getMetaData());
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public int getTransactionIsolation() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.getTransactionIsolation();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return -1;
        }
    }
    
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.getTypeMap();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.getWarnings();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public boolean isReadOnly() throws SQLException {
        this.checkOpen();
        if (this.cacheState && this.readOnlyCached != null) {
            return this.readOnlyCached;
        }
        try {
            this.readOnlyCached = this.connection.isReadOnly();
            return this.readOnlyCached;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public String nativeSQL(final String sql) throws SQLException {
        this.checkOpen();
        try {
            return this.connection.nativeSQL(sql);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public void rollback() throws SQLException {
        this.checkOpen();
        try {
            this.connection.rollback();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    public Integer getDefaultQueryTimeout() {
        return this.defaultQueryTimeoutSeconds;
    }
    
    public void setDefaultQueryTimeout(final Integer defaultQueryTimeoutSeconds) {
        this.defaultQueryTimeoutSeconds = defaultQueryTimeoutSeconds;
    }
    
    public void setCacheState(final boolean cacheState) {
        this.cacheState = cacheState;
    }
    
    public void clearCachedState() {
        this.autoCommitCached = null;
        this.readOnlyCached = null;
        if (this.connection instanceof DelegatingConnection) {
            ((DelegatingConnection)this.connection).clearCachedState();
        }
    }
    
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        this.checkOpen();
        try {
            this.connection.setAutoCommit(autoCommit);
            if (this.cacheState) {
                this.autoCommitCached = this.connection.getAutoCommit();
            }
        }
        catch (final SQLException e) {
            this.autoCommitCached = null;
            this.handleException(e);
        }
    }
    
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        this.checkOpen();
        try {
            this.connection.setCatalog(catalog);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        this.checkOpen();
        try {
            this.connection.setReadOnly(readOnly);
            if (this.cacheState) {
                this.readOnlyCached = this.connection.isReadOnly();
            }
        }
        catch (final SQLException e) {
            this.readOnlyCached = null;
            this.handleException(e);
        }
    }
    
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        this.checkOpen();
        try {
            this.connection.setTransactionIsolation(level);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        this.checkOpen();
        try {
            this.connection.setTypeMap(map);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this.closed || this.connection == null || this.connection.isClosed();
    }
    
    protected void checkOpen() throws SQLException {
        if (!this.closed) {
            return;
        }
        if (null != this.connection) {
            String label = "";
            try {
                label = this.connection.toString();
            }
            catch (final Exception ex) {}
            throw new SQLException("Connection " + label + " is closed.");
        }
        throw new SQLException("Connection is null.");
    }
    
    protected void activate() {
        this.closed = false;
        this.setLastUsed();
        if (this.connection instanceof DelegatingConnection) {
            ((DelegatingConnection)this.connection).activate();
        }
    }
    
    protected void passivate() throws SQLException {
        final List<AbandonedTrace> traces = this.getTrace();
        if (traces != null && !traces.isEmpty()) {
            final List<Exception> thrownList = new ArrayList<Exception>();
            for (final Object trace : traces) {
                if (trace instanceof Statement) {
                    try {
                        ((Statement)trace).close();
                    }
                    catch (final Exception e) {
                        thrownList.add(e);
                    }
                }
                else {
                    if (!(trace instanceof ResultSet)) {
                        continue;
                    }
                    try {
                        ((ResultSet)trace).close();
                    }
                    catch (final Exception e) {
                        thrownList.add(e);
                    }
                }
            }
            this.clearTrace();
            if (!thrownList.isEmpty()) {
                throw new SQLExceptionList(thrownList);
            }
        }
        this.setLastUsed(0L);
    }
    
    @Override
    public int getHoldability() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.getHoldability();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public void setHoldability(final int holdability) throws SQLException {
        this.checkOpen();
        try {
            this.connection.setHoldability(holdability);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public Savepoint setSavepoint() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.setSavepoint();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        this.checkOpen();
        try {
            return this.connection.setSavepoint(name);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        this.checkOpen();
        try {
            this.connection.rollback(savepoint);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        this.checkOpen();
        try {
            this.connection.releaseSavepoint(savepoint);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        this.checkOpen();
        try {
            final DelegatingStatement ds = new DelegatingStatement(this, this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
            this.initializeStatement(ds);
            return ds;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        this.checkOpen();
        try {
            final DelegatingPreparedStatement dps = new DelegatingPreparedStatement(this, this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
            this.initializeStatement(dps);
            return dps;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        this.checkOpen();
        try {
            final DelegatingCallableStatement dcs = new DelegatingCallableStatement(this, this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
            this.initializeStatement(dcs);
            return dcs;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        this.checkOpen();
        try {
            final DelegatingPreparedStatement dps = new DelegatingPreparedStatement(this, this.connection.prepareStatement(sql, autoGeneratedKeys));
            this.initializeStatement(dps);
            return dps;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        this.checkOpen();
        try {
            final DelegatingPreparedStatement dps = new DelegatingPreparedStatement(this, this.connection.prepareStatement(sql, columnIndexes));
            this.initializeStatement(dps);
            return dps;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        this.checkOpen();
        try {
            final DelegatingPreparedStatement dps = new DelegatingPreparedStatement(this, this.connection.prepareStatement(sql, columnNames));
            this.initializeStatement(dps);
            return dps;
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(this.getClass()) || iface.isAssignableFrom(this.connection.getClass()) || this.connection.isWrapperFor(iface);
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return iface.cast(this);
        }
        if (iface.isAssignableFrom(this.connection.getClass())) {
            return iface.cast(this.connection);
        }
        return this.connection.unwrap(iface);
    }
    
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        this.checkOpen();
        try {
            return this.connection.createArrayOf(typeName, elements);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.createBlob();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Clob createClob() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.createClob();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.createNClob();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.createSQLXML();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        this.checkOpen();
        try {
            return this.connection.createStruct(typeName, attributes);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public boolean isValid(final int timeoutSeconds) throws SQLException {
        if (this.isClosed()) {
            return false;
        }
        try {
            return this.connection.isValid(timeoutSeconds);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        try {
            this.checkOpen();
            this.connection.setClientInfo(name, value);
        }
        catch (final SQLClientInfoException e) {
            throw e;
        }
        catch (final SQLException e2) {
            throw new SQLClientInfoException("Connection is closed.", DelegatingConnection.EMPTY_FAILED_PROPERTIES, e2);
        }
    }
    
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        try {
            this.checkOpen();
            this.connection.setClientInfo(properties);
        }
        catch (final SQLClientInfoException e) {
            throw e;
        }
        catch (final SQLException e2) {
            throw new SQLClientInfoException("Connection is closed.", DelegatingConnection.EMPTY_FAILED_PROPERTIES, e2);
        }
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        this.checkOpen();
        try {
            return this.connection.getClientInfo();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public String getClientInfo(final String name) throws SQLException {
        this.checkOpen();
        try {
            return this.connection.getClientInfo(name);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public void setSchema(final String schema) throws SQLException {
        this.checkOpen();
        try {
            Jdbc41Bridge.setSchema(this.connection, schema);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public String getSchema() throws SQLException {
        this.checkOpen();
        try {
            return Jdbc41Bridge.getSchema(this.connection);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
        try {
            Jdbc41Bridge.abort(this.connection, executor);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        this.checkOpen();
        try {
            Jdbc41Bridge.setNetworkTimeout(this.connection, executor, milliseconds);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        this.checkOpen();
        try {
            return Jdbc41Bridge.getNetworkTimeout(this.connection);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    static {
        EMPTY_FAILED_PROPERTIES = Collections.emptyMap();
    }
}
