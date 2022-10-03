package com.microsoft.sqlserver.jdbc;

import java.util.UUID;
import java.sql.SQLClientInfoException;
import java.util.Properties;
import java.sql.Struct;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Array;
import java.sql.Savepoint;
import java.util.Map;
import java.sql.SQLWarning;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.security.Permission;
import java.sql.SQLPermission;
import java.text.MessageFormat;
import java.util.concurrent.Executor;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.Serializable;

class SQLServerConnectionPoolProxy implements ISQLServerConnection, Serializable
{
    private static final long serialVersionUID = 5752599482349578127L;
    private SQLServerConnection wrappedConnection;
    private boolean bIsOpen;
    private static final AtomicInteger baseConnectionID;
    private final String traceID;
    private static final String callAbortPerm = "callAbort";
    
    private static int nextConnectionID() {
        return SQLServerConnectionPoolProxy.baseConnectionID.incrementAndGet();
    }
    
    @Override
    public String toString() {
        return this.traceID;
    }
    
    SQLServerConnectionPoolProxy(final SQLServerConnection con) {
        this.traceID = " ProxyConnectionID:" + nextConnectionID();
        (this.wrappedConnection = con).setAssociatedProxy(this);
        this.bIsOpen = true;
    }
    
    SQLServerConnection getWrappedConnection() {
        return this.wrappedConnection;
    }
    
    void checkClosed() throws SQLServerException {
        if (!this.bIsOpen) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_connectionIsClosed"), null, false);
        }
    }
    
    @Override
    public Statement createStatement() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.createStatement();
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareCall(sql);
    }
    
    @Override
    public String nativeSQL(final String sql) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.nativeSQL(sql);
    }
    
    @Override
    public void setAutoCommit(final boolean newAutoCommitMode) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.setAutoCommit(newAutoCommitMode);
    }
    
    @Override
    public boolean getAutoCommit() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getAutoCommit();
    }
    
    @Override
    public void commit() throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.commit();
    }
    
    @Override
    public void rollback() throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.rollback();
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
        if (!this.bIsOpen || null == this.wrappedConnection) {
            return;
        }
        if (null == executor) {
            final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidArgument"));
            final Object[] msgArgs = { "executor" };
            SQLServerException.makeFromDriverError(null, null, form.format(msgArgs), null, false);
        }
        final SecurityManager secMgr = System.getSecurityManager();
        if (secMgr != null) {
            try {
                final SQLPermission perm = new SQLPermission("callAbort");
                secMgr.checkPermission(perm);
            }
            catch (final SecurityException ex) {
                final MessageFormat form2 = new MessageFormat(SQLServerException.getErrString("R_permissionDenied"));
                final Object[] msgArgs2 = { "callAbort" };
                throw new SQLServerException(form2.format(msgArgs2), null, 0, ex);
            }
        }
        this.bIsOpen = false;
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (SQLServerConnectionPoolProxy.this.wrappedConnection.getConnectionLogger().isLoggable(Level.FINER)) {
                    SQLServerConnectionPoolProxy.this.wrappedConnection.getConnectionLogger().finer(this.toString() + " Connection proxy aborted ");
                }
                try {
                    SQLServerConnectionPoolProxy.this.wrappedConnection.poolCloseEventNotify();
                    SQLServerConnectionPoolProxy.this.wrappedConnection = null;
                }
                catch (final SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    
    @Override
    public void close() throws SQLServerException {
        if (this.bIsOpen && null != this.wrappedConnection) {
            if (this.wrappedConnection.getConnectionLogger().isLoggable(Level.FINER)) {
                this.wrappedConnection.getConnectionLogger().finer(this.toString() + " Connection proxy closed ");
            }
            this.wrappedConnection.poolCloseEventNotify();
            this.wrappedConnection = null;
        }
        this.bIsOpen = false;
    }
    
    void internalClose() {
        this.bIsOpen = false;
        this.wrappedConnection = null;
    }
    
    @Override
    public boolean isClosed() throws SQLServerException {
        return !this.bIsOpen;
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getMetaData();
    }
    
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.setReadOnly(readOnly);
    }
    
    @Override
    public boolean isReadOnly() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.isReadOnly();
    }
    
    @Override
    public void setCatalog(final String catalog) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.setCatalog(catalog);
    }
    
    @Override
    public String getCatalog() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getCatalog();
    }
    
    @Override
    public void setTransactionIsolation(final int level) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.setTransactionIsolation(level);
    }
    
    @Override
    public int getTransactionIsolation() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getTransactionIsolation();
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getWarnings();
    }
    
    @Override
    public void clearWarnings() throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.clearWarnings();
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createStatement(resultSetType, resultSetConcurrency);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sSql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sSql, resultSetType, resultSetConcurrency);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }
    
    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        this.checkClosed();
        this.wrappedConnection.setTypeMap(map);
    }
    
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getTypeMap();
    }
    
    @Override
    public Statement createStatement(final int nType, final int nConcur, final int nHold) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.createStatement(nType, nConcur, nHold);
    }
    
    @Override
    public Statement createStatement(final int nType, final int nConcur, final int nHold, final SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.createStatement(nType, nConcur, nHold, stmtColEncSetting);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int nType, final int nConcur, final int nHold) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, nType, nConcur, nHold);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int nType, final int nConcur, final int nHold, final SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, nType, nConcur, nHold, stmtColEncSetting);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int nType, final int nConcur, final int nHold) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareCall(sql, nType, nConcur, nHold);
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int nType, final int nConcur, final int nHold, final SQLServerStatementColumnEncryptionSetting stmtColEncSetiing) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareCall(sql, nType, nConcur, nHold, stmtColEncSetiing);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int flag) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, flag);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int flag, final SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, flag, stmtColEncSetting);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, columnIndexes);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes, final SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, columnIndexes, stmtColEncSetting);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, columnNames);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames, final SQLServerStatementColumnEncryptionSetting stmtColEncSetting) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.prepareStatement(sql, columnNames, stmtColEncSetting);
    }
    
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        this.checkClosed();
        this.wrappedConnection.releaseSavepoint(savepoint);
    }
    
    @Override
    public Savepoint setSavepoint(final String sName) throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.setSavepoint(sName);
    }
    
    @Override
    public Savepoint setSavepoint() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.setSavepoint();
    }
    
    @Override
    public void rollback(final Savepoint s) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.rollback(s);
    }
    
    @Override
    public int getHoldability() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getHoldability();
    }
    
    @Override
    public void setHoldability(final int nNewHold) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.setHoldability(nNewHold);
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.getNetworkTimeout();
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int timeout) throws SQLException {
        this.checkClosed();
        this.wrappedConnection.setNetworkTimeout(executor, timeout);
    }
    
    @Override
    public String getSchema() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.getSchema();
    }
    
    @Override
    public void setSchema(final String schema) throws SQLException {
        this.checkClosed();
        this.wrappedConnection.setSchema(schema);
    }
    
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createArrayOf(typeName, elements);
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createBlob();
    }
    
    @Override
    public Clob createClob() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createClob();
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createNClob();
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createSQLXML();
    }
    
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.createStruct(typeName, attributes);
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.getClientInfo();
    }
    
    @Override
    public String getClientInfo(final String name) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.getClientInfo(name);
    }
    
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        this.wrappedConnection.setClientInfo(properties);
    }
    
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        this.wrappedConnection.setClientInfo(name, value);
    }
    
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        this.checkClosed();
        return this.wrappedConnection.isValid(timeout);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        this.wrappedConnection.getConnectionLogger().entering(this.toString(), "isWrapperFor", iface);
        final boolean f = iface.isInstance(this);
        this.wrappedConnection.getConnectionLogger().exiting(this.toString(), "isWrapperFor", f);
        return f;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        this.wrappedConnection.getConnectionLogger().entering(this.toString(), "unwrap", iface);
        T t;
        try {
            t = iface.cast(this);
        }
        catch (final ClassCastException e) {
            final SQLServerException newe = new SQLServerException(e.getMessage(), e);
            throw newe;
        }
        this.wrappedConnection.getConnectionLogger().exiting(this.toString(), "unwrap", t);
        return t;
    }
    
    @Override
    public UUID getClientConnectionId() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getClientConnectionId();
    }
    
    @Override
    public void setSendTimeAsDatetime(final boolean sendTimeAsDateTimeValue) throws SQLServerException {
        this.checkClosed();
        this.wrappedConnection.setSendTimeAsDatetime(sendTimeAsDateTimeValue);
    }
    
    @Override
    public boolean getSendTimeAsDatetime() throws SQLServerException {
        this.checkClosed();
        return this.wrappedConnection.getSendTimeAsDatetime();
    }
    
    @Override
    public int getDiscardedServerPreparedStatementCount() {
        return this.wrappedConnection.getDiscardedServerPreparedStatementCount();
    }
    
    @Override
    public void closeUnreferencedPreparedStatementHandles() {
        this.wrappedConnection.closeUnreferencedPreparedStatementHandles();
    }
    
    @Override
    public boolean getEnablePrepareOnFirstPreparedStatementCall() {
        return this.wrappedConnection.getEnablePrepareOnFirstPreparedStatementCall();
    }
    
    @Override
    public void setEnablePrepareOnFirstPreparedStatementCall(final boolean value) {
        this.wrappedConnection.setEnablePrepareOnFirstPreparedStatementCall(value);
    }
    
    @Override
    public int getServerPreparedStatementDiscardThreshold() {
        return this.wrappedConnection.getServerPreparedStatementDiscardThreshold();
    }
    
    @Override
    public void setServerPreparedStatementDiscardThreshold(final int value) {
        this.wrappedConnection.setServerPreparedStatementDiscardThreshold(value);
    }
    
    @Override
    public void setStatementPoolingCacheSize(final int value) {
        this.wrappedConnection.setStatementPoolingCacheSize(value);
    }
    
    @Override
    public int getStatementPoolingCacheSize() {
        return this.wrappedConnection.getStatementPoolingCacheSize();
    }
    
    @Override
    public boolean isStatementPoolingEnabled() {
        return this.wrappedConnection.isStatementPoolingEnabled();
    }
    
    @Override
    public int getStatementHandleCacheEntryCount() {
        return this.wrappedConnection.getStatementHandleCacheEntryCount();
    }
    
    @Override
    public void setDisableStatementPooling(final boolean value) {
        this.wrappedConnection.setDisableStatementPooling(value);
    }
    
    @Override
    public boolean getDisableStatementPooling() {
        return this.wrappedConnection.getDisableStatementPooling();
    }
    
    @Override
    public void setUseFmtOnly(final boolean useFmtOnly) {
        this.wrappedConnection.setUseFmtOnly(useFmtOnly);
    }
    
    @Override
    public boolean getUseFmtOnly() {
        return this.wrappedConnection.getUseFmtOnly();
    }
    
    static {
        baseConnectionID = new AtomicInteger(0);
    }
}
