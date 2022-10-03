package com.zoho.cp;

import java.util.concurrent.Executor;
import java.sql.SQLClientInfoException;
import java.sql.Savepoint;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.SQLWarning;
import java.util.Map;
import java.sql.DatabaseMetaData;
import java.util.Properties;
import java.sql.Struct;
import javax.transaction.SystemException;
import com.zoho.mickey.ExceptionUtils;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Array;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Statement;
import java.util.LinkedList;
import java.sql.Connection;

public class LogicalConnection implements Connection
{
    private boolean closed;
    private Connection physicalConnection;
    private ConnectionDetail detail;
    private LinkedList<Statement> statementList;
    private static final boolean ALLOW_NONENLISTED_CONNECTIONS;
    private static final Logger LOGGER;
    private static final Logger METHOD_ACCESS_LOGGER;
    
    public LogicalConnection(final ConnectionDetail detail) {
        this.statementList = new LinkedList<Statement>();
        (this.detail = detail).addChild(this);
        this.physicalConnection = detail.physicalConnection;
        LogicalConnection.LOGGER.log(Level.FINE, "DEBUG :: Logical Connection :: {0}, Physical Connection :: {1} :: Constructor", new Object[] { this, this.physicalConnection });
    }
    
    public LogicalConnection() {
        this.statementList = new LinkedList<Statement>();
    }
    
    protected ConnectionDetail getConnectionDetail() {
        return this.detail;
    }
    
    public boolean isDetailValid() {
        return this.detail == null || this.detail.isValid();
    }
    
    void setClosed() {
        this.closed = true;
        this.closeAllCreatedStatements();
    }
    
    private void closeAllCreatedStatements() {
        Statement statement = null;
        do {
            try {
                statement = this.statementList.poll();
            }
            finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (final Exception exc) {
                    LogicalConnection.LOGGER.log(Level.SEVERE, "Exception during closing statements enlisted in logical connection", exc);
                }
            }
        } while (statement != null);
    }
    
    @Override
    public void close() throws SQLException {
        if (this.closed) {
            return;
        }
        Label_0122: {
            try {
                final Txn txn = (Txn)TxMgr.getInstance().getTransaction();
                if (TxMgr.getInstance().getStatus() != 6) {
                    if (txn == null || txn.isEnlisted(this.detail)) {
                        this.setClosed();
                        break Label_0122;
                    }
                }
                try {
                    this.detail.physicalConnection.setAutoCommit(true);
                }
                catch (final Exception e) {
                    LogicalConnection.LOGGER.log(Level.INFO, "Unable to set autocommit as true", e);
                }
                this.detail.closeAndRemoveChildren();
                this.detail.returnToPool();
                LogicalConnection.LOGGER.log(Level.FINE, "Logical connection closed and return to the pool");
                return;
            }
            catch (final Exception exc) {
                throw new SQLException("Exception occurred during closing logical connection", exc);
            }
        }
        LogicalConnection.LOGGER.log(Level.FINE, "DEBUG :: LogicalConnection :: {0} :: close", this);
    }
    
    private void checkClosed() throws SQLException {
        if (this.closed) {
            throw new SQLException("Logical connection is already closed " + this);
        }
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        this.checkClosed();
        try {
            this.physicalConnection.clearWarnings();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void commit() throws SQLException {
        this.checkClosed();
        try {
            this.physicalConnection.commit();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Array createArrayOf(final String arg0, final Object[] arg1) throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.createArrayOf(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.createBlob();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Clob createClob() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.createClob();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.createNClob();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.createSQLXML();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Statement createStatement() throws SQLException {
        this.checkClosed();
        try {
            final Txn txn = (Txn)TxMgr.getInstance().getTransaction();
            if (txn != null && !txn.isEnlisted(this.getConnectionDetail())) {
                if (!LogicalConnection.ALLOW_NONENLISTED_CONNECTIONS) {
                    throw new SQLException("Given connection is not enlisted in the transaction");
                }
                LogicalConnection.METHOD_ACCESS_LOGGER.log(Level.WARNING, ExceptionUtils.getStackTraceForCurrentThread());
            }
            final Statement statement = new WrappedStatement(this.physicalConnection.createStatement(), this);
            this.statementList.add(statement);
            return statement;
        }
        catch (final SystemException e) {
            final SQLException sqle = new SQLException(e.getMessage(), (Throwable)e);
            this.handleException(sqle);
            throw sqle;
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Statement createStatement(final int arg0, final int arg1, final int arg2) throws SQLException {
        this.checkClosed();
        try {
            final Statement statement = new WrappedStatement(this.physicalConnection.createStatement(arg0, arg1, arg2), this);
            this.statementList.add(statement);
            return statement;
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Statement createStatement(final int arg0, final int arg1) throws SQLException {
        this.checkClosed();
        try {
            final Statement statement = new WrappedStatement(this.physicalConnection.createStatement(arg0, arg1), this);
            this.statementList.add(statement);
            return statement;
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Struct createStruct(final String arg0, final Object[] arg1) throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.createStruct(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.getAutoCommit();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String getCatalog() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.getCatalog();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.getClientInfo();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String getClientInfo(final String arg0) throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.getClientInfo(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getHoldability() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.getHoldability();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.getMetaData();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getTransactionIsolation() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.getTransactionIsolation();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.getTypeMap();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.getWarnings();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this.closed;
    }
    
    @Override
    public boolean isReadOnly() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.isReadOnly();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isValid(final int arg0) throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.isValid(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> arg0) throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.isWrapperFor(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String nativeSQL(final String arg0) throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.nativeSQL(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public CallableStatement prepareCall(final String arg0, final int arg1, final int arg2, final int arg3) throws SQLException {
        this.checkClosed();
        try {
            final CallableStatement callStatment = new WrappedCallableStatement(this.physicalConnection.prepareCall(arg0, arg1, arg2, arg3), this);
            this.statementList.add(callStatment);
            return callStatment;
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public CallableStatement prepareCall(final String arg0, final int arg1, final int arg2) throws SQLException {
        this.checkClosed();
        try {
            final CallableStatement callStatment = new WrappedCallableStatement(this.physicalConnection.prepareCall(arg0, arg1, arg2), this);
            this.statementList.add(callStatment);
            return callStatment;
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public CallableStatement prepareCall(final String arg0) throws SQLException {
        this.checkClosed();
        try {
            final CallableStatement callStatment = new WrappedCallableStatement(this.physicalConnection.prepareCall(arg0), this);
            this.statementList.add(callStatment);
            return callStatment;
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String arg0, final int arg1, final int arg2, final int arg3) throws SQLException {
        this.checkClosed();
        try {
            final PreparedStatement prepStatment = new WrappedPreparedStatement(this.physicalConnection.prepareStatement(arg0, arg1, arg2, arg3), this);
            this.statementList.add(prepStatment);
            return prepStatment;
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String arg0, final int arg1, final int arg2) throws SQLException {
        this.checkClosed();
        try {
            final PreparedStatement prepStatement = new WrappedPreparedStatement(this.physicalConnection.prepareStatement(arg0, arg1, arg2), this);
            this.statementList.add(prepStatement);
            return prepStatement;
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String arg0, final int arg1) throws SQLException {
        this.checkClosed();
        try {
            final PreparedStatement prepStatement = new WrappedPreparedStatement(this.physicalConnection.prepareStatement(arg0, arg1), this);
            this.statementList.add(prepStatement);
            return prepStatement;
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String arg0, final int[] arg1) throws SQLException {
        this.checkClosed();
        try {
            final PreparedStatement prepStatement = new WrappedPreparedStatement(this.physicalConnection.prepareStatement(arg0, arg1), this);
            this.statementList.add(prepStatement);
            return prepStatement;
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String arg0, final String[] arg1) throws SQLException {
        this.checkClosed();
        try {
            final PreparedStatement prepStatement = new WrappedPreparedStatement(this.physicalConnection.prepareStatement(arg0, arg1), this);
            this.statementList.add(prepStatement);
            return prepStatement;
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public PreparedStatement prepareStatement(final String arg0) throws SQLException {
        this.checkClosed();
        try {
            final PreparedStatement prepStatement = new WrappedPreparedStatement(this.physicalConnection.prepareStatement(arg0), this);
            this.statementList.add(prepStatement);
            return prepStatement;
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void releaseSavepoint(final Savepoint arg0) throws SQLException {
        this.checkClosed();
        try {
            this.physicalConnection.releaseSavepoint(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void rollback() throws SQLException {
        this.checkClosed();
        try {
            this.physicalConnection.rollback();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void rollback(final Savepoint arg0) throws SQLException {
        this.checkClosed();
        try {
            this.physicalConnection.rollback(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setAutoCommit(final boolean arg0) throws SQLException {
        this.checkClosed();
        try {
            this.physicalConnection.setAutoCommit(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setCatalog(final String arg0) throws SQLException {
        this.checkClosed();
        try {
            this.physicalConnection.setCatalog(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setClientInfo(final Properties arg0) throws SQLClientInfoException {
        this.physicalConnection.setClientInfo(arg0);
    }
    
    @Override
    public void setClientInfo(final String arg0, final String arg1) throws SQLClientInfoException {
        this.physicalConnection.setClientInfo(arg0, arg1);
    }
    
    @Override
    public void setHoldability(final int arg0) throws SQLException {
        this.checkClosed();
        try {
            this.physicalConnection.setHoldability(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setReadOnly(final boolean arg0) throws SQLException {
        this.checkClosed();
        try {
            this.physicalConnection.setReadOnly(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Savepoint setSavepoint() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.setSavepoint();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Savepoint setSavepoint(final String arg0) throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.setSavepoint(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setTransactionIsolation(final int arg0) throws SQLException {
        throw new SQLException("set Txn Isolation method is not supported");
    }
    
    @Override
    public void setTypeMap(final Map<String, Class<?>> arg0) throws SQLException {
        this.checkClosed();
        try {
            this.physicalConnection.setTypeMap(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public <T> T unwrap(final Class<T> arg0) throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.unwrap(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    void handleException(final SQLException exc) throws SQLException {
        this.detail.handleException(exc);
    }
    
    public MeteredSocket getMeteredSocket() {
        return this.detail.getMeteredSocket();
    }
    
    @Override
    public void abort(final Executor arg0) throws SQLException {
        this.checkClosed();
        try {
            this.physicalConnection.abort(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.getNetworkTimeout();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String getSchema() throws SQLException {
        this.checkClosed();
        try {
            return this.physicalConnection.getSchema();
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNetworkTimeout(final Executor arg0, final int arg1) throws SQLException {
        this.checkClosed();
        try {
            this.physicalConnection.setNetworkTimeout(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setSchema(final String arg0) throws SQLException {
        this.checkClosed();
        try {
            this.physicalConnection.setSchema(arg0);
        }
        catch (final SQLException exc) {
            this.handleException(exc);
            throw exc;
        }
    }
    
    public Connection getPhysicalConnection() {
        return this.physicalConnection;
    }
    
    static {
        ALLOW_NONENLISTED_CONNECTIONS = Boolean.parseBoolean(System.getProperty("allow.nonenlisted.conn", "true"));
        LOGGER = Logger.getLogger(LogicalConnection.class.getName());
        METHOD_ACCESS_LOGGER = Logger.getLogger("DBCPMethodAccess");
    }
}
