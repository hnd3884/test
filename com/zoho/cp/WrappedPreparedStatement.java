package com.zoho.cp;

import java.net.URL;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.Ref;
import java.sql.NClob;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Clob;
import java.io.Reader;
import java.sql.Blob;
import java.math.BigDecimal;
import java.io.InputStream;
import java.sql.Array;
import java.sql.SQLWarning;
import java.sql.ParameterMetaData;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class WrappedPreparedStatement implements PreparedStatement
{
    private PreparedStatement preparedStatement;
    private LogicalConnection logicalConnection;
    
    public WrappedPreparedStatement(final PreparedStatement preparedStatement, final LogicalConnection connection) {
        this.preparedStatement = preparedStatement;
        this.logicalConnection = connection;
    }
    
    @Override
    public void addBatch() throws SQLException {
        try {
            this.preparedStatement.addBatch();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void addBatch(final String arg0) throws SQLException {
        try {
            this.preparedStatement.addBatch(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void cancel() throws SQLException {
        try {
            this.preparedStatement.cancel();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void clearBatch() throws SQLException {
        try {
            this.preparedStatement.clearBatch();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void clearParameters() throws SQLException {
        try {
            this.preparedStatement.clearParameters();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        try {
            this.preparedStatement.clearWarnings();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void close() throws SQLException {
        try {
            this.preparedStatement.close();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute() throws SQLException {
        try {
            return this.preparedStatement.execute();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute(final String arg0, final int arg1) throws SQLException {
        try {
            return this.preparedStatement.execute(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute(final String arg0, final int[] arg1) throws SQLException {
        try {
            return this.preparedStatement.execute(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute(final String arg0, final String[] arg1) throws SQLException {
        try {
            return this.preparedStatement.execute(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute(final String arg0) throws SQLException {
        try {
            return this.preparedStatement.execute(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        try {
            return this.preparedStatement.executeBatch();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSet executeQuery() throws SQLException {
        try {
            return new WrappedResultSet(this.preparedStatement.executeQuery(), this.logicalConnection);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSet executeQuery(final String arg0) throws SQLException {
        try {
            return new WrappedResultSet(this.preparedStatement.executeQuery(arg0), this.logicalConnection);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate() throws SQLException {
        try {
            return this.preparedStatement.executeUpdate();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate(final String arg0, final int arg1) throws SQLException {
        try {
            return this.preparedStatement.executeUpdate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate(final String arg0, final int[] arg1) throws SQLException {
        try {
            return this.preparedStatement.executeUpdate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate(final String arg0, final String[] arg1) throws SQLException {
        try {
            return this.preparedStatement.executeUpdate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate(final String arg0) throws SQLException {
        try {
            return this.preparedStatement.executeUpdate(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        try {
            return this.preparedStatement.getConnection();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        try {
            return this.preparedStatement.getFetchDirection();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        try {
            return this.preparedStatement.getFetchSize();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        try {
            return new WrappedResultSet(this.preparedStatement.getGeneratedKeys(), this.logicalConnection);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getMaxFieldSize() throws SQLException {
        try {
            return this.preparedStatement.getMaxFieldSize();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getMaxRows() throws SQLException {
        try {
            return this.preparedStatement.getMaxRows();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return this.preparedStatement.getMetaData();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean getMoreResults() throws SQLException {
        try {
            return this.preparedStatement.getMoreResults();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean getMoreResults(final int arg0) throws SQLException {
        try {
            return this.preparedStatement.getMoreResults(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        try {
            return this.preparedStatement.getParameterMetaData();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getQueryTimeout() throws SQLException {
        try {
            return this.preparedStatement.getQueryTimeout();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSet getResultSet() throws SQLException {
        try {
            return new WrappedResultSet(this.preparedStatement.getResultSet(), this.logicalConnection);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getResultSetConcurrency() throws SQLException {
        try {
            return this.preparedStatement.getResultSetConcurrency();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        try {
            return this.preparedStatement.getResultSetHoldability();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getResultSetType() throws SQLException {
        try {
            return this.preparedStatement.getResultSetType();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getUpdateCount() throws SQLException {
        try {
            return this.preparedStatement.getUpdateCount();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        try {
            return this.preparedStatement.getWarnings();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        try {
            return this.preparedStatement.isClosed();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        try {
            return this.preparedStatement.isPoolable();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> arg0) throws SQLException {
        try {
            return this.preparedStatement.isWrapperFor(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setArray(final int arg0, final Array arg1) throws SQLException {
        try {
            this.preparedStatement.setArray(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setAsciiStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
        try {
            this.preparedStatement.setAsciiStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setAsciiStream(final int arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.preparedStatement.setAsciiStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setAsciiStream(final int arg0, final InputStream arg1) throws SQLException {
        try {
            this.preparedStatement.setAsciiStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBigDecimal(final int arg0, final BigDecimal arg1) throws SQLException {
        try {
            this.preparedStatement.setBigDecimal(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBinaryStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
        try {
            this.preparedStatement.setBinaryStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBinaryStream(final int arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.preparedStatement.setBinaryStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBinaryStream(final int arg0, final InputStream arg1) throws SQLException {
        try {
            this.preparedStatement.setBinaryStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBlob(final int arg0, final Blob arg1) throws SQLException {
        try {
            this.preparedStatement.setBlob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBlob(final int arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.preparedStatement.setBlob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBlob(final int arg0, final InputStream arg1) throws SQLException {
        try {
            this.preparedStatement.setBlob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBoolean(final int arg0, final boolean arg1) throws SQLException {
        try {
            this.preparedStatement.setBoolean(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setByte(final int arg0, final byte arg1) throws SQLException {
        try {
            this.preparedStatement.setByte(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBytes(final int arg0, final byte[] arg1) throws SQLException {
        try {
            this.preparedStatement.setBytes(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setCharacterStream(final int arg0, final Reader arg1, final int arg2) throws SQLException {
        try {
            this.preparedStatement.setCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setCharacterStream(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.preparedStatement.setCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setCharacterStream(final int arg0, final Reader arg1) throws SQLException {
        try {
            this.preparedStatement.setCharacterStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setClob(final int arg0, final Clob arg1) throws SQLException {
        try {
            this.preparedStatement.setClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setClob(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.preparedStatement.setClob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setClob(final int arg0, final Reader arg1) throws SQLException {
        try {
            this.preparedStatement.setClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setCursorName(final String arg0) throws SQLException {
        try {
            this.preparedStatement.setCursorName(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setDate(final int arg0, final Date arg1, final Calendar arg2) throws SQLException {
        try {
            this.preparedStatement.setDate(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setDate(final int arg0, final Date arg1) throws SQLException {
        try {
            this.preparedStatement.setDate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setDouble(final int arg0, final double arg1) throws SQLException {
        try {
            this.preparedStatement.setDouble(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setEscapeProcessing(final boolean arg0) throws SQLException {
        try {
            this.preparedStatement.setEscapeProcessing(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setFetchDirection(final int arg0) throws SQLException {
        try {
            this.preparedStatement.setFetchDirection(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setFetchSize(final int arg0) throws SQLException {
        try {
            this.preparedStatement.setFetchSize(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setFloat(final int arg0, final float arg1) throws SQLException {
        try {
            this.preparedStatement.setFloat(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setInt(final int arg0, final int arg1) throws SQLException {
        try {
            this.preparedStatement.setInt(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setLong(final int arg0, final long arg1) throws SQLException {
        try {
            this.preparedStatement.setLong(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setMaxFieldSize(final int arg0) throws SQLException {
        try {
            this.preparedStatement.setMaxFieldSize(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setMaxRows(final int arg0) throws SQLException {
        try {
            this.preparedStatement.setMaxRows(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNCharacterStream(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.preparedStatement.setNCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNCharacterStream(final int arg0, final Reader arg1) throws SQLException {
        try {
            this.preparedStatement.setNCharacterStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNClob(final int arg0, final NClob arg1) throws SQLException {
        try {
            this.preparedStatement.setNClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNClob(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.preparedStatement.setNClob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNClob(final int arg0, final Reader arg1) throws SQLException {
        try {
            this.preparedStatement.setNClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNString(final int arg0, final String arg1) throws SQLException {
        try {
            this.preparedStatement.setNString(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNull(final int arg0, final int arg1, final String arg2) throws SQLException {
        try {
            this.preparedStatement.setNull(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNull(final int arg0, final int arg1) throws SQLException {
        try {
            this.preparedStatement.setNull(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setObject(final int arg0, final Object arg1, final int arg2, final int arg3) throws SQLException {
        try {
            this.preparedStatement.setObject(arg0, arg1, arg2, arg3);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setObject(final int arg0, final Object arg1, final int arg2) throws SQLException {
        try {
            this.preparedStatement.setObject(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setObject(final int arg0, final Object arg1) throws SQLException {
        try {
            this.preparedStatement.setObject(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setPoolable(final boolean arg0) throws SQLException {
        try {
            this.preparedStatement.setPoolable(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setQueryTimeout(final int arg0) throws SQLException {
        try {
            this.preparedStatement.setQueryTimeout(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setRef(final int arg0, final Ref arg1) throws SQLException {
        try {
            this.preparedStatement.setRef(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setRowId(final int arg0, final RowId arg1) throws SQLException {
        try {
            this.preparedStatement.setRowId(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setSQLXML(final int arg0, final SQLXML arg1) throws SQLException {
        try {
            this.preparedStatement.setSQLXML(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setShort(final int arg0, final short arg1) throws SQLException {
        try {
            this.preparedStatement.setShort(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setString(final int arg0, final String arg1) throws SQLException {
        try {
            this.preparedStatement.setString(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setTime(final int arg0, final Time arg1, final Calendar arg2) throws SQLException {
        try {
            this.preparedStatement.setTime(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setTime(final int arg0, final Time arg1) throws SQLException {
        try {
            this.preparedStatement.setTime(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setTimestamp(final int arg0, final Timestamp arg1, final Calendar arg2) throws SQLException {
        try {
            this.preparedStatement.setTimestamp(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setTimestamp(final int arg0, final Timestamp arg1) throws SQLException {
        try {
            this.preparedStatement.setTimestamp(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setURL(final int arg0, final URL arg1) throws SQLException {
        try {
            this.preparedStatement.setURL(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setUnicodeStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
        try {
            this.preparedStatement.setUnicodeStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public <T> T unwrap(final Class<T> arg0) throws SQLException {
        try {
            return this.preparedStatement.unwrap(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void closeOnCompletion() throws SQLException {
        try {
            this.preparedStatement.closeOnCompletion();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        try {
            return this.preparedStatement.isCloseOnCompletion();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String toString() {
        return this.preparedStatement.toString();
    }
}
