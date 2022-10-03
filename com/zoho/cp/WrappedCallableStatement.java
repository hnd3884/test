package com.zoho.cp;

import java.io.InputStream;
import java.sql.SQLWarning;
import java.net.URL;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.Ref;
import java.sql.ParameterMetaData;
import java.util.Map;
import java.sql.NClob;
import java.sql.ResultSetMetaData;
import java.sql.Date;
import java.util.Calendar;
import java.sql.Connection;
import java.sql.Clob;
import java.io.Reader;
import java.sql.Blob;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;

public class WrappedCallableStatement implements CallableStatement
{
    private CallableStatement callableStatement;
    private LogicalConnection logicalConnection;
    
    WrappedCallableStatement(final CallableStatement callableStatement, final LogicalConnection logicalConnection) {
        this.callableStatement = callableStatement;
        this.logicalConnection = logicalConnection;
    }
    
    @Override
    public void addBatch() throws SQLException {
        try {
            this.callableStatement.addBatch();
        }
        catch (final Exception exc) {
            this.logicalConnection.close();
        }
    }
    
    @Override
    public void addBatch(final String arg0) throws SQLException {
        try {
            this.callableStatement.addBatch(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void cancel() throws SQLException {
        try {
            this.callableStatement.cancel();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void clearBatch() throws SQLException {
        try {
            this.callableStatement.clearBatch();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void clearParameters() throws SQLException {
        try {
            this.callableStatement.clearParameters();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        try {
            this.callableStatement.clearWarnings();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void close() throws SQLException {
        try {
            this.callableStatement.close();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute() throws SQLException {
        try {
            return this.callableStatement.execute();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute(final String arg0, final int arg1) throws SQLException {
        try {
            return this.callableStatement.execute(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute(final String arg0, final int[] arg1) throws SQLException {
        try {
            return this.callableStatement.execute(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute(final String arg0, final String[] arg1) throws SQLException {
        try {
            return this.callableStatement.execute(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean execute(final String arg0) throws SQLException {
        try {
            return this.callableStatement.execute(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int[] executeBatch() throws SQLException {
        try {
            return this.callableStatement.executeBatch();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSet executeQuery() throws SQLException {
        try {
            return this.callableStatement.executeQuery();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSet executeQuery(final String arg0) throws SQLException {
        try {
            return this.callableStatement.executeQuery(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate() throws SQLException {
        try {
            return this.callableStatement.executeUpdate();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate(final String arg0, final int arg1) throws SQLException {
        try {
            return this.callableStatement.executeUpdate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate(final String arg0, final int[] arg1) throws SQLException {
        try {
            return this.callableStatement.executeUpdate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate(final String arg0, final String[] arg1) throws SQLException {
        try {
            return this.callableStatement.executeUpdate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int executeUpdate(final String arg0) throws SQLException {
        try {
            return this.callableStatement.executeUpdate(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Array getArray(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getArray(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Array getArray(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getArray(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public BigDecimal getBigDecimal(final int arg0, final int arg1) throws SQLException {
        try {
            return this.callableStatement.getBigDecimal(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public BigDecimal getBigDecimal(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getBigDecimal(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public BigDecimal getBigDecimal(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getBigDecimal(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Blob getBlob(final int arg0) throws SQLException {
        return this.callableStatement.getBlob(arg0);
    }
    
    @Override
    public Blob getBlob(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getBlob(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean getBoolean(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getBoolean(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean getBoolean(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getBoolean(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public byte getByte(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getByte(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public byte getByte(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getByte(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public byte[] getBytes(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getBytes(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public byte[] getBytes(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getBytes(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Reader getCharacterStream(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getCharacterStream(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Reader getCharacterStream(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getCharacterStream(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Clob getClob(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getClob(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Clob getClob(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getClob(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        try {
            return this.callableStatement.getConnection();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Date getDate(final int arg0, final Calendar arg1) throws SQLException {
        try {
            return this.callableStatement.getDate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Date getDate(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getDate(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Date getDate(final String arg0, final Calendar arg1) throws SQLException {
        try {
            return this.callableStatement.getDate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Date getDate(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getDate(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public double getDouble(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getDouble(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public double getDouble(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getDouble(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        try {
            return this.callableStatement.getFetchDirection();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        try {
            return this.callableStatement.getFetchSize();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public float getFloat(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getFloat(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public float getFloat(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getFloat(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        try {
            return this.callableStatement.getGeneratedKeys();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getInt(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getInt(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getInt(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getInt(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public long getLong(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getLong(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public long getLong(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getLong(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getMaxFieldSize() throws SQLException {
        try {
            return this.callableStatement.getMaxFieldSize();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getMaxRows() throws SQLException {
        try {
            return this.callableStatement.getMaxRows();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return this.callableStatement.getMetaData();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean getMoreResults() throws SQLException {
        try {
            return this.callableStatement.getMoreResults();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean getMoreResults(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getMoreResults(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Reader getNCharacterStream(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getNCharacterStream(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Reader getNCharacterStream(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getNCharacterStream(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public NClob getNClob(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getNClob(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public NClob getNClob(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getNClob(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String getNString(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getNString(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String getNString(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getNString(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Object getObject(final int arg0, final Map<String, Class<?>> arg1) throws SQLException {
        try {
            return this.callableStatement.getObject(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Object getObject(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getObject(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Object getObject(final String arg0, final Map<String, Class<?>> arg1) throws SQLException {
        try {
            return this.callableStatement.getObject(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Object getObject(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getObject(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        try {
            return this.callableStatement.getParameterMetaData();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getQueryTimeout() throws SQLException {
        try {
            return this.callableStatement.getQueryTimeout();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Ref getRef(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getRef(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Ref getRef(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getRef(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSet getResultSet() throws SQLException {
        try {
            return this.callableStatement.getResultSet();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getResultSetConcurrency() throws SQLException {
        try {
            return this.callableStatement.getResultSetConcurrency();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getResultSetHoldability() throws SQLException {
        try {
            return this.callableStatement.getResultSetHoldability();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getResultSetType() throws SQLException {
        try {
            return this.callableStatement.getResultSetType();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public RowId getRowId(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getRowId(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public RowId getRowId(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getRowId(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public SQLXML getSQLXML(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getSQLXML(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public SQLXML getSQLXML(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getSQLXML(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public short getShort(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getShort(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public short getShort(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getShort(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String getString(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getString(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String getString(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getString(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Time getTime(final int arg0, final Calendar arg1) throws SQLException {
        try {
            return this.callableStatement.getTime(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Time getTime(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getTime(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Time getTime(final String arg0, final Calendar arg1) throws SQLException {
        try {
            return this.callableStatement.getTime(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Time getTime(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getTime(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final int arg0, final Calendar arg1) throws SQLException {
        try {
            return this.callableStatement.getTimestamp(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getTimestamp(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final String arg0, final Calendar arg1) throws SQLException {
        try {
            return this.callableStatement.getTimestamp(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getTimestamp(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public URL getURL(final int arg0) throws SQLException {
        try {
            return this.callableStatement.getURL(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public URL getURL(final String arg0) throws SQLException {
        try {
            return this.callableStatement.getURL(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getUpdateCount() throws SQLException {
        try {
            return this.callableStatement.getUpdateCount();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        try {
            return this.callableStatement.getWarnings();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        try {
            return this.callableStatement.isClosed();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isPoolable() throws SQLException {
        try {
            return this.callableStatement.isPoolable();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> arg0) throws SQLException {
        try {
            return this.callableStatement.isWrapperFor(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void registerOutParameter(final int arg0, final int arg1, final int arg2) throws SQLException {
        try {
            this.callableStatement.registerOutParameter(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void registerOutParameter(final int arg0, final int arg1, final String arg2) throws SQLException {
        try {
            this.callableStatement.registerOutParameter(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void registerOutParameter(final int arg0, final int arg1) throws SQLException {
        try {
            this.callableStatement.registerOutParameter(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void registerOutParameter(final String arg0, final int arg1, final int arg2) throws SQLException {
        try {
            this.callableStatement.registerOutParameter(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void registerOutParameter(final String arg0, final int arg1, final String arg2) throws SQLException {
        try {
            this.callableStatement.registerOutParameter(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void registerOutParameter(final String arg0, final int arg1) throws SQLException {
        try {
            this.callableStatement.registerOutParameter(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setArray(final int arg0, final Array arg1) throws SQLException {
        try {
            this.callableStatement.setArray(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setAsciiStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
        try {
            this.callableStatement.setAsciiStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setAsciiStream(final int arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.callableStatement.setAsciiStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setAsciiStream(final int arg0, final InputStream arg1) throws SQLException {
        try {
            this.callableStatement.setAsciiStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setAsciiStream(final String arg0, final InputStream arg1, final int arg2) throws SQLException {
        try {
            this.callableStatement.setAsciiStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setAsciiStream(final String arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.callableStatement.setAsciiStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setAsciiStream(final String arg0, final InputStream arg1) throws SQLException {
        try {
            this.callableStatement.setAsciiStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBigDecimal(final int arg0, final BigDecimal arg1) throws SQLException {
        try {
            this.callableStatement.setBigDecimal(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBigDecimal(final String arg0, final BigDecimal arg1) throws SQLException {
        try {
            this.callableStatement.setBigDecimal(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBinaryStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
        try {
            this.callableStatement.setBinaryStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBinaryStream(final int arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.callableStatement.setBinaryStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBinaryStream(final int arg0, final InputStream arg1) throws SQLException {
        try {
            this.callableStatement.setBinaryStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBinaryStream(final String arg0, final InputStream arg1, final int arg2) throws SQLException {
        try {
            this.callableStatement.setBinaryStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBinaryStream(final String arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.callableStatement.setBinaryStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBinaryStream(final String arg0, final InputStream arg1) throws SQLException {
        try {
            this.callableStatement.setBinaryStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBlob(final int arg0, final Blob arg1) throws SQLException {
        try {
            this.callableStatement.setBlob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBlob(final int arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.callableStatement.setBlob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBlob(final int arg0, final InputStream arg1) throws SQLException {
        try {
            this.callableStatement.setBlob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBlob(final String arg0, final Blob arg1) throws SQLException {
        try {
            this.callableStatement.setBlob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBlob(final String arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.callableStatement.setBlob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBlob(final String arg0, final InputStream arg1) throws SQLException {
        try {
            this.callableStatement.setBlob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBoolean(final int arg0, final boolean arg1) throws SQLException {
        try {
            this.callableStatement.setBoolean(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBoolean(final String arg0, final boolean arg1) throws SQLException {
        try {
            this.callableStatement.setBoolean(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setByte(final int arg0, final byte arg1) throws SQLException {
        try {
            this.callableStatement.setByte(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setByte(final String arg0, final byte arg1) throws SQLException {
        try {
            this.callableStatement.setByte(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBytes(final int arg0, final byte[] arg1) throws SQLException {
        try {
            this.callableStatement.setBytes(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setBytes(final String arg0, final byte[] arg1) throws SQLException {
        try {
            this.callableStatement.setBytes(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setCharacterStream(final int arg0, final Reader arg1, final int arg2) throws SQLException {
        try {
            this.callableStatement.setCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setCharacterStream(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.callableStatement.setCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setCharacterStream(final int arg0, final Reader arg1) throws SQLException {
        try {
            this.callableStatement.setCharacterStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setCharacterStream(final String arg0, final Reader arg1, final int arg2) throws SQLException {
        try {
            this.callableStatement.setCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setCharacterStream(final String arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.callableStatement.setCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setCharacterStream(final String arg0, final Reader arg1) throws SQLException {
        try {
            this.callableStatement.setCharacterStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setClob(final int arg0, final Clob arg1) throws SQLException {
        try {
            this.callableStatement.setClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setClob(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.callableStatement.setClob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setClob(final int arg0, final Reader arg1) throws SQLException {
        try {
            this.callableStatement.setClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setClob(final String arg0, final Clob arg1) throws SQLException {
        try {
            this.callableStatement.setClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setClob(final String arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.callableStatement.setClob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setClob(final String arg0, final Reader arg1) throws SQLException {
        try {
            this.callableStatement.setClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setCursorName(final String arg0) throws SQLException {
        try {
            this.callableStatement.setCursorName(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setDate(final int arg0, final Date arg1, final Calendar arg2) throws SQLException {
        try {
            this.callableStatement.setDate(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setDate(final int arg0, final Date arg1) throws SQLException {
        try {
            this.callableStatement.setDate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setDate(final String arg0, final Date arg1, final Calendar arg2) throws SQLException {
        try {
            this.callableStatement.setDate(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setDate(final String arg0, final Date arg1) throws SQLException {
        try {
            this.callableStatement.setDate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setDouble(final int arg0, final double arg1) throws SQLException {
        try {
            this.callableStatement.setDouble(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setDouble(final String arg0, final double arg1) throws SQLException {
        try {
            this.callableStatement.setDouble(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setEscapeProcessing(final boolean arg0) throws SQLException {
        try {
            this.callableStatement.setEscapeProcessing(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setFetchDirection(final int arg0) throws SQLException {
        try {
            this.callableStatement.setFetchDirection(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setFetchSize(final int arg0) throws SQLException {
        try {
            this.callableStatement.setFetchSize(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setFloat(final int arg0, final float arg1) throws SQLException {
        try {
            this.callableStatement.setFloat(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setFloat(final String arg0, final float arg1) throws SQLException {
        try {
            this.callableStatement.setFloat(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setInt(final int arg0, final int arg1) throws SQLException {
        try {
            this.callableStatement.setInt(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setInt(final String arg0, final int arg1) throws SQLException {
        try {
            this.callableStatement.setInt(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setLong(final int arg0, final long arg1) throws SQLException {
        try {
            this.callableStatement.setLong(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setLong(final String arg0, final long arg1) throws SQLException {
        try {
            this.callableStatement.setLong(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setMaxFieldSize(final int arg0) throws SQLException {
        try {
            this.callableStatement.setMaxFieldSize(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setMaxRows(final int arg0) throws SQLException {
        try {
            this.callableStatement.setMaxRows(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNCharacterStream(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.callableStatement.setNCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNCharacterStream(final int arg0, final Reader arg1) throws SQLException {
        try {
            this.callableStatement.setNCharacterStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNCharacterStream(final String arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.callableStatement.setNCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNCharacterStream(final String arg0, final Reader arg1) throws SQLException {
        try {
            this.callableStatement.setNCharacterStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNClob(final int arg0, final NClob arg1) throws SQLException {
        try {
            this.callableStatement.setNClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNClob(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.callableStatement.setNClob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNClob(final int arg0, final Reader arg1) throws SQLException {
        try {
            this.callableStatement.setNClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNClob(final String arg0, final NClob arg1) throws SQLException {
        try {
            this.callableStatement.setNClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNClob(final String arg0, final Reader arg1, final long arg2) throws SQLException {
        this.callableStatement.setNClob(arg0, arg1, arg2);
    }
    
    @Override
    public void setNClob(final String arg0, final Reader arg1) throws SQLException {
        try {
            this.callableStatement.setNClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNString(final int arg0, final String arg1) throws SQLException {
        try {
            this.callableStatement.setNString(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNString(final String arg0, final String arg1) throws SQLException {
        try {
            this.callableStatement.setNString(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNull(final int arg0, final int arg1, final String arg2) throws SQLException {
        try {
            this.callableStatement.setNull(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNull(final int arg0, final int arg1) throws SQLException {
        try {
            this.callableStatement.setNull(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNull(final String arg0, final int arg1, final String arg2) throws SQLException {
        try {
            this.callableStatement.setNull(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setNull(final String arg0, final int arg1) throws SQLException {
        try {
            this.callableStatement.setNull(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setObject(final int arg0, final Object arg1, final int arg2, final int arg3) throws SQLException {
        try {
            this.callableStatement.setObject(arg0, arg1, arg2, arg3);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setObject(final int arg0, final Object arg1, final int arg2) throws SQLException {
        try {
            this.callableStatement.setObject(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setObject(final int arg0, final Object arg1) throws SQLException {
        try {
            this.callableStatement.setObject(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setObject(final String arg0, final Object arg1, final int arg2, final int arg3) throws SQLException {
        try {
            this.callableStatement.setObject(arg0, arg1, arg2, arg3);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setObject(final String arg0, final Object arg1, final int arg2) throws SQLException {
        try {
            this.callableStatement.setObject(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setObject(final String arg0, final Object arg1) throws SQLException {
        try {
            this.callableStatement.setObject(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setPoolable(final boolean arg0) throws SQLException {
        try {
            this.callableStatement.setPoolable(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setQueryTimeout(final int arg0) throws SQLException {
        try {
            this.callableStatement.setQueryTimeout(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setRef(final int arg0, final Ref arg1) throws SQLException {
        try {
            this.callableStatement.setRef(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setRowId(final int arg0, final RowId arg1) throws SQLException {
        try {
            this.callableStatement.setRowId(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setRowId(final String arg0, final RowId arg1) throws SQLException {
        try {
            this.callableStatement.setRowId(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setSQLXML(final int arg0, final SQLXML arg1) throws SQLException {
        try {
            this.callableStatement.setSQLXML(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setSQLXML(final String arg0, final SQLXML arg1) throws SQLException {
        try {
            this.callableStatement.setSQLXML(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setShort(final int arg0, final short arg1) throws SQLException {
        try {
            this.callableStatement.setShort(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setShort(final String arg0, final short arg1) throws SQLException {
        try {
            this.callableStatement.setShort(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setString(final int arg0, final String arg1) throws SQLException {
        try {
            this.callableStatement.setString(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setString(final String arg0, final String arg1) throws SQLException {
        try {
            this.callableStatement.setString(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setTime(final int arg0, final Time arg1, final Calendar arg2) throws SQLException {
        try {
            this.callableStatement.setTime(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setTime(final int arg0, final Time arg1) throws SQLException {
        try {
            this.callableStatement.setTime(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setTime(final String arg0, final Time arg1, final Calendar arg2) throws SQLException {
        try {
            this.callableStatement.setTime(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setTime(final String arg0, final Time arg1) throws SQLException {
        try {
            this.callableStatement.setTime(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setTimestamp(final int arg0, final Timestamp arg1, final Calendar arg2) throws SQLException {
        try {
            this.callableStatement.setTimestamp(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setTimestamp(final int arg0, final Timestamp arg1) throws SQLException {
        try {
            this.callableStatement.setTimestamp(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setTimestamp(final String arg0, final Timestamp arg1, final Calendar arg2) throws SQLException {
        try {
            this.callableStatement.setTimestamp(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setTimestamp(final String arg0, final Timestamp arg1) throws SQLException {
        try {
            this.callableStatement.setTimestamp(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setURL(final int arg0, final URL arg1) throws SQLException {
        try {
            this.callableStatement.setURL(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setURL(final String arg0, final URL arg1) throws SQLException {
        try {
            this.callableStatement.setURL(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setUnicodeStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
        try {
            this.callableStatement.setUnicodeStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public <T> T unwrap(final Class<T> arg0) throws SQLException {
        try {
            return this.callableStatement.unwrap(arg0);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        try {
            return this.callableStatement.wasNull();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void closeOnCompletion() throws SQLException {
        try {
            this.callableStatement.closeOnCompletion();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        try {
            return this.callableStatement.isCloseOnCompletion();
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public <T> T getObject(final int arg0, final Class<T> arg1) throws SQLException {
        try {
            return this.callableStatement.getObject(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public <T> T getObject(final String arg0, final Class<T> arg1) throws SQLException {
        try {
            return this.callableStatement.getObject(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logicalConnection.handleException(exc);
            throw exc;
        }
    }
}
