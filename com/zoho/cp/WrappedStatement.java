package com.zoho.cp;

import java.sql.SQLWarning;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class WrappedStatement implements Statement
{
    private Statement statement;
    private LogicalConnection logConn;
    
    public WrappedStatement(final Statement statement, final LogicalConnection conn) {
        this.statement = statement;
        this.logConn = conn;
    }
    
    @Override
    public void addBatch(final String arg0) throws SQLException {
        try {
            this.statement.addBatch(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void cancel() throws SQLException {
        try {
            this.statement.cancel();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void clearBatch() throws SQLException {
        try {
            this.statement.clearBatch();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        try {
            this.statement.clearWarnings();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void close() throws SQLException {
        try {
            this.statement.close();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute(final String arg0, final int arg1) throws SQLException {
        try {
            return this.statement.execute(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute(final String arg0, final int[] arg1) throws SQLException {
        try {
            return this.statement.execute(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute(final String arg0, final String[] arg1) throws SQLException {
        try {
            return this.statement.execute(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute(final String arg0) throws SQLException {
        try {
            return this.statement.execute(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        try {
            return this.statement.executeBatch();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSet executeQuery(final String arg0) throws SQLException {
        try {
            return new WrappedResultSet(this.statement.executeQuery(arg0), this.logConn);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate(final String arg0, final int arg1) throws SQLException {
        try {
            return this.statement.executeUpdate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate(final String arg0, final int[] arg1) throws SQLException {
        try {
            return this.statement.executeUpdate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate(final String arg0, final String[] arg1) throws SQLException {
        try {
            return this.statement.executeUpdate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate(final String arg0) throws SQLException {
        try {
            return this.statement.executeUpdate(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        try {
            return this.statement.getConnection();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        try {
            return this.statement.getFetchDirection();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        try {
            return this.statement.getFetchSize();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        try {
            return new WrappedResultSet(this.statement.getGeneratedKeys(), this.logConn);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getMaxFieldSize() throws SQLException {
        try {
            return this.statement.getMaxFieldSize();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getMaxRows() throws SQLException {
        try {
            return this.statement.getMaxRows();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean getMoreResults() throws SQLException {
        try {
            return this.statement.getMoreResults();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean getMoreResults(final int arg0) throws SQLException {
        try {
            return this.statement.getMoreResults(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getQueryTimeout() throws SQLException {
        try {
            return this.statement.getQueryTimeout();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSet getResultSet() throws SQLException {
        try {
            final ResultSet rs = this.statement.getResultSet();
            if (rs == null) {
                return null;
            }
            return new WrappedResultSet(rs, this.logConn);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getResultSetConcurrency() throws SQLException {
        try {
            return this.statement.getResultSetConcurrency();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        try {
            return this.statement.getResultSetHoldability();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getResultSetType() throws SQLException {
        try {
            return this.statement.getResultSetType();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getUpdateCount() throws SQLException {
        try {
            return this.statement.getUpdateCount();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        try {
            return this.statement.getWarnings();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        try {
            return this.statement.isClosed();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        try {
            return this.statement.isPoolable();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> arg0) throws SQLException {
        try {
            return this.statement.isWrapperFor(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setCursorName(final String arg0) throws SQLException {
        try {
            this.statement.setCursorName(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setEscapeProcessing(final boolean arg0) throws SQLException {
        try {
            this.statement.setEscapeProcessing(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setFetchDirection(final int arg0) throws SQLException {
        try {
            this.statement.setFetchDirection(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setFetchSize(final int arg0) throws SQLException {
        try {
            this.statement.setFetchSize(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setMaxFieldSize(final int arg0) throws SQLException {
        try {
            this.statement.setMaxFieldSize(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setMaxRows(final int arg0) throws SQLException {
        try {
            this.statement.setMaxRows(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setPoolable(final boolean arg0) throws SQLException {
        try {
            this.statement.setPoolable(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setQueryTimeout(final int arg0) throws SQLException {
        try {
            this.statement.setQueryTimeout(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public <T> T unwrap(final Class<T> arg0) throws SQLException {
        try {
            return this.statement.unwrap(arg0);
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void closeOnCompletion() throws SQLException {
        try {
            this.statement.closeOnCompletion();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        try {
            return this.statement.isCloseOnCompletion();
        }
        catch (final SQLException exc) {
            this.logConn.handleException(exc);
            throw exc;
        }
    }
}
