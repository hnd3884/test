package org.apache.tomcat.dbcp.dbcp2;

import java.sql.SQLWarning;
import java.net.URL;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.Ref;
import java.util.Map;
import java.sql.NClob;
import java.sql.ResultSetMetaData;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Clob;
import java.io.Reader;
import java.sql.Blob;
import java.math.BigDecimal;
import java.io.InputStream;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public final class DelegatingResultSet extends AbandonedTrace implements ResultSet
{
    private final ResultSet resultSet;
    private Statement statement;
    private Connection connection;
    
    public static ResultSet wrapResultSet(final Connection connection, final ResultSet resultSet) {
        if (null == resultSet) {
            return null;
        }
        return new DelegatingResultSet(connection, resultSet);
    }
    
    public static ResultSet wrapResultSet(final Statement statement, final ResultSet resultSet) {
        if (null == resultSet) {
            return null;
        }
        return new DelegatingResultSet(statement, resultSet);
    }
    
    private DelegatingResultSet(final Connection connection, final ResultSet resultSet) {
        super((AbandonedTrace)connection);
        this.connection = connection;
        this.resultSet = resultSet;
    }
    
    private DelegatingResultSet(final Statement statement, final ResultSet resultSet) {
        super((AbandonedTrace)statement);
        this.statement = statement;
        this.resultSet = resultSet;
    }
    
    @Override
    public boolean absolute(final int row) throws SQLException {
        try {
            return this.resultSet.absolute(row);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public void afterLast() throws SQLException {
        try {
            this.resultSet.afterLast();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        try {
            this.resultSet.beforeFirst();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void cancelRowUpdates() throws SQLException {
        try {
            this.resultSet.cancelRowUpdates();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        try {
            this.resultSet.clearWarnings();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void close() throws SQLException {
        try {
            if (this.statement != null) {
                this.removeThisTrace(this.statement);
                this.statement = null;
            }
            if (this.connection != null) {
                this.removeThisTrace(this.connection);
                this.connection = null;
            }
            this.resultSet.close();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void deleteRow() throws SQLException {
        try {
            this.resultSet.deleteRow();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public int findColumn(final String columnName) throws SQLException {
        try {
            return this.resultSet.findColumn(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public boolean first() throws SQLException {
        try {
            return this.resultSet.first();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public Array getArray(final int i) throws SQLException {
        try {
            return this.resultSet.getArray(i);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Array getArray(final String colName) throws SQLException {
        try {
            return this.resultSet.getArray(colName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public InputStream getAsciiStream(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getAsciiStream(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public InputStream getAsciiStream(final String columnName) throws SQLException {
        try {
            return this.resultSet.getAsciiStream(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getBigDecimal(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
        try {
            return this.resultSet.getBigDecimal(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public BigDecimal getBigDecimal(final String columnName) throws SQLException {
        try {
            return this.resultSet.getBigDecimal(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final String columnName, final int scale) throws SQLException {
        try {
            return this.resultSet.getBigDecimal(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public InputStream getBinaryStream(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getBinaryStream(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public InputStream getBinaryStream(final String columnName) throws SQLException {
        try {
            return this.resultSet.getBinaryStream(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Blob getBlob(final int i) throws SQLException {
        try {
            return this.resultSet.getBlob(i);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Blob getBlob(final String colName) throws SQLException {
        try {
            return this.resultSet.getBlob(colName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public boolean getBoolean(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getBoolean(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean getBoolean(final String columnName) throws SQLException {
        try {
            return this.resultSet.getBoolean(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public byte getByte(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getByte(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public byte getByte(final String columnName) throws SQLException {
        try {
            return this.resultSet.getByte(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public byte[] getBytes(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getBytes(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public byte[] getBytes(final String columnName) throws SQLException {
        try {
            return this.resultSet.getBytes(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Reader getCharacterStream(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getCharacterStream(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Reader getCharacterStream(final String columnName) throws SQLException {
        try {
            return this.resultSet.getCharacterStream(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Clob getClob(final int i) throws SQLException {
        try {
            return this.resultSet.getClob(i);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Clob getClob(final String colName) throws SQLException {
        try {
            return this.resultSet.getClob(colName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public int getConcurrency() throws SQLException {
        try {
            return this.resultSet.getConcurrency();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public String getCursorName() throws SQLException {
        try {
            return this.resultSet.getCursorName();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Date getDate(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getDate(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
        try {
            return this.resultSet.getDate(columnIndex, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Date getDate(final String columnName) throws SQLException {
        try {
            return this.resultSet.getDate(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Date getDate(final String columnName, final Calendar cal) throws SQLException {
        try {
            return this.resultSet.getDate(columnName, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    public ResultSet getDelegate() {
        return this.resultSet;
    }
    
    @Override
    public double getDouble(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getDouble(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0.0;
        }
    }
    
    @Override
    public double getDouble(final String columnName) throws SQLException {
        try {
            return this.resultSet.getDouble(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0.0;
        }
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        try {
            return this.resultSet.getFetchDirection();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        try {
            return this.resultSet.getFetchSize();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public float getFloat(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getFloat(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0.0f;
        }
    }
    
    @Override
    public float getFloat(final String columnName) throws SQLException {
        try {
            return this.resultSet.getFloat(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0.0f;
        }
    }
    
    @Override
    public int getHoldability() throws SQLException {
        try {
            return this.resultSet.getHoldability();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    public ResultSet getInnermostDelegate() {
        ResultSet r = this.resultSet;
        while (r instanceof DelegatingResultSet) {
            r = ((DelegatingResultSet)r).getDelegate();
            if (this == r) {
                return null;
            }
        }
        return r;
    }
    
    @Override
    public int getInt(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getInt(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getInt(final String columnName) throws SQLException {
        try {
            return this.resultSet.getInt(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public long getLong(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getLong(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0L;
        }
    }
    
    @Override
    public long getLong(final String columnName) throws SQLException {
        try {
            return this.resultSet.getLong(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0L;
        }
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return this.resultSet.getMetaData();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Reader getNCharacterStream(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getNCharacterStream(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Reader getNCharacterStream(final String columnLabel) throws SQLException {
        try {
            return this.resultSet.getNCharacterStream(columnLabel);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public NClob getNClob(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getNClob(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public NClob getNClob(final String columnLabel) throws SQLException {
        try {
            return this.resultSet.getNClob(columnLabel);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public String getNString(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getNString(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public String getNString(final String columnLabel) throws SQLException {
        try {
            return this.resultSet.getNString(columnLabel);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Object getObject(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getObject(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
        try {
            return Jdbc41Bridge.getObject(this.resultSet, columnIndex, type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Object getObject(final int i, final Map<String, Class<?>> map) throws SQLException {
        try {
            return this.resultSet.getObject(i, map);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Object getObject(final String columnName) throws SQLException {
        try {
            return this.resultSet.getObject(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
        try {
            return Jdbc41Bridge.getObject(this.resultSet, columnLabel, type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Object getObject(final String colName, final Map<String, Class<?>> map) throws SQLException {
        try {
            return this.resultSet.getObject(colName, map);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Ref getRef(final int i) throws SQLException {
        try {
            return this.resultSet.getRef(i);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Ref getRef(final String colName) throws SQLException {
        try {
            return this.resultSet.getRef(colName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public int getRow() throws SQLException {
        try {
            return this.resultSet.getRow();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public RowId getRowId(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getRowId(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public RowId getRowId(final String columnLabel) throws SQLException {
        try {
            return this.resultSet.getRowId(columnLabel);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public short getShort(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getShort(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public short getShort(final String columnName) throws SQLException {
        try {
            return this.resultSet.getShort(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public SQLXML getSQLXML(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getSQLXML(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public SQLXML getSQLXML(final String columnLabel) throws SQLException {
        try {
            return this.resultSet.getSQLXML(columnLabel);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Statement getStatement() throws SQLException {
        return this.statement;
    }
    
    @Override
    public String getString(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getString(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public String getString(final String columnName) throws SQLException {
        try {
            return this.resultSet.getString(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Time getTime(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getTime(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
        try {
            return this.resultSet.getTime(columnIndex, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Time getTime(final String columnName) throws SQLException {
        try {
            return this.resultSet.getTime(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Time getTime(final String columnName, final Calendar cal) throws SQLException {
        try {
            return this.resultSet.getTime(columnName, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getTimestamp(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
        try {
            return this.resultSet.getTimestamp(columnIndex, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final String columnName) throws SQLException {
        try {
            return this.resultSet.getTimestamp(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final String columnName, final Calendar cal) throws SQLException {
        try {
            return this.resultSet.getTimestamp(columnName, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public int getType() throws SQLException {
        try {
            return this.resultSet.getType();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getUnicodeStream(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final String columnName) throws SQLException {
        try {
            return this.resultSet.getUnicodeStream(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public URL getURL(final int columnIndex) throws SQLException {
        try {
            return this.resultSet.getURL(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public URL getURL(final String columnName) throws SQLException {
        try {
            return this.resultSet.getURL(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        try {
            return this.resultSet.getWarnings();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    protected void handleException(final SQLException e) throws SQLException {
        if (this.statement instanceof DelegatingStatement) {
            ((DelegatingStatement)this.statement).handleException(e);
        }
        else {
            if (!(this.connection instanceof DelegatingConnection)) {
                throw e;
            }
            ((DelegatingConnection)this.connection).handleException(e);
        }
    }
    
    @Override
    public void insertRow() throws SQLException {
        try {
            this.resultSet.insertRow();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public boolean isAfterLast() throws SQLException {
        try {
            return this.resultSet.isAfterLast();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean isBeforeFirst() throws SQLException {
        try {
            return this.resultSet.isBeforeFirst();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        try {
            return this.resultSet.isClosed();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean isFirst() throws SQLException {
        try {
            return this.resultSet.isFirst();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean isLast() throws SQLException {
        try {
            return this.resultSet.isLast();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(this.getClass()) || iface.isAssignableFrom(this.resultSet.getClass()) || this.resultSet.isWrapperFor(iface);
    }
    
    @Override
    public boolean last() throws SQLException {
        try {
            return this.resultSet.last();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public void moveToCurrentRow() throws SQLException {
        try {
            this.resultSet.moveToCurrentRow();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void moveToInsertRow() throws SQLException {
        try {
            this.resultSet.moveToInsertRow();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public boolean next() throws SQLException {
        try {
            return this.resultSet.next();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean previous() throws SQLException {
        try {
            return this.resultSet.previous();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public void refreshRow() throws SQLException {
        try {
            this.resultSet.refreshRow();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public boolean relative(final int rows) throws SQLException {
        try {
            return this.resultSet.relative(rows);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean rowDeleted() throws SQLException {
        try {
            return this.resultSet.rowDeleted();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean rowInserted() throws SQLException {
        try {
            return this.resultSet.rowInserted();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean rowUpdated() throws SQLException {
        try {
            return this.resultSet.rowUpdated();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public void setFetchDirection(final int direction) throws SQLException {
        try {
            this.resultSet.setFetchDirection(direction);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setFetchSize(final int rows) throws SQLException {
        try {
            this.resultSet.setFetchSize(rows);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public synchronized String toString() {
        return super.toString() + "[resultSet=" + this.resultSet + ", statement=" + this.statement + ", connection=" + this.connection + "]";
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return iface.cast(this);
        }
        if (iface.isAssignableFrom(this.resultSet.getClass())) {
            return iface.cast(this.resultSet);
        }
        return this.resultSet.unwrap(iface);
    }
    
    @Override
    public void updateArray(final int columnIndex, final Array x) throws SQLException {
        try {
            this.resultSet.updateArray(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateArray(final String columnName, final Array x) throws SQLException {
        try {
            this.resultSet.updateArray(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream inputStream) throws SQLException {
        try {
            this.resultSet.updateAsciiStream(columnIndex, inputStream);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        try {
            this.resultSet.updateAsciiStream(columnIndex, x, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
        try {
            this.resultSet.updateAsciiStream(columnIndex, inputStream, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream inputStream) throws SQLException {
        try {
            this.resultSet.updateAsciiStream(columnLabel, inputStream);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateAsciiStream(final String columnName, final InputStream x, final int length) throws SQLException {
        try {
            this.resultSet.updateAsciiStream(columnName, x, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
        try {
            this.resultSet.updateAsciiStream(columnLabel, inputStream, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBigDecimal(final int columnIndex, final BigDecimal x) throws SQLException {
        try {
            this.resultSet.updateBigDecimal(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBigDecimal(final String columnName, final BigDecimal x) throws SQLException {
        try {
            this.resultSet.updateBigDecimal(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream inputStream) throws SQLException {
        try {
            this.resultSet.updateBinaryStream(columnIndex, inputStream);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        try {
            this.resultSet.updateBinaryStream(columnIndex, x, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
        try {
            this.resultSet.updateBinaryStream(columnIndex, inputStream, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream inputStream) throws SQLException {
        try {
            this.resultSet.updateBinaryStream(columnLabel, inputStream);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBinaryStream(final String columnName, final InputStream x, final int length) throws SQLException {
        try {
            this.resultSet.updateBinaryStream(columnName, x, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
        try {
            this.resultSet.updateBinaryStream(columnLabel, inputStream, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBlob(final int columnIndex, final Blob x) throws SQLException {
        try {
            this.resultSet.updateBlob(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
        try {
            this.resultSet.updateBlob(columnIndex, inputStream);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
        try {
            this.resultSet.updateBlob(columnIndex, inputStream, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBlob(final String columnName, final Blob x) throws SQLException {
        try {
            this.resultSet.updateBlob(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
        try {
            this.resultSet.updateBlob(columnLabel, inputStream);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
        try {
            this.resultSet.updateBlob(columnLabel, inputStream, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBoolean(final int columnIndex, final boolean x) throws SQLException {
        try {
            this.resultSet.updateBoolean(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBoolean(final String columnName, final boolean x) throws SQLException {
        try {
            this.resultSet.updateBoolean(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateByte(final int columnIndex, final byte x) throws SQLException {
        try {
            this.resultSet.updateByte(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateByte(final String columnName, final byte x) throws SQLException {
        try {
            this.resultSet.updateByte(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBytes(final int columnIndex, final byte[] x) throws SQLException {
        try {
            this.resultSet.updateBytes(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateBytes(final String columnName, final byte[] x) throws SQLException {
        try {
            this.resultSet.updateBytes(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader reader) throws SQLException {
        try {
            this.resultSet.updateCharacterStream(columnIndex, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final int length) throws SQLException {
        try {
            this.resultSet.updateCharacterStream(columnIndex, x, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader reader, final long length) throws SQLException {
        try {
            this.resultSet.updateCharacterStream(columnIndex, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        try {
            this.resultSet.updateCharacterStream(columnLabel, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateCharacterStream(final String columnName, final Reader reader, final int length) throws SQLException {
        try {
            this.resultSet.updateCharacterStream(columnName, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        try {
            this.resultSet.updateCharacterStream(columnLabel, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateClob(final int columnIndex, final Clob x) throws SQLException {
        try {
            this.resultSet.updateClob(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader) throws SQLException {
        try {
            this.resultSet.updateClob(columnIndex, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        try {
            this.resultSet.updateClob(columnIndex, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateClob(final String columnName, final Clob x) throws SQLException {
        try {
            this.resultSet.updateClob(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader) throws SQLException {
        try {
            this.resultSet.updateClob(columnLabel, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        try {
            this.resultSet.updateClob(columnLabel, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateDate(final int columnIndex, final Date x) throws SQLException {
        try {
            this.resultSet.updateDate(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateDate(final String columnName, final Date x) throws SQLException {
        try {
            this.resultSet.updateDate(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateDouble(final int columnIndex, final double x) throws SQLException {
        try {
            this.resultSet.updateDouble(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateDouble(final String columnName, final double x) throws SQLException {
        try {
            this.resultSet.updateDouble(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateFloat(final int columnIndex, final float x) throws SQLException {
        try {
            this.resultSet.updateFloat(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateFloat(final String columnName, final float x) throws SQLException {
        try {
            this.resultSet.updateFloat(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateInt(final int columnIndex, final int x) throws SQLException {
        try {
            this.resultSet.updateInt(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateInt(final String columnName, final int x) throws SQLException {
        try {
            this.resultSet.updateInt(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateLong(final int columnIndex, final long x) throws SQLException {
        try {
            this.resultSet.updateLong(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateLong(final String columnName, final long x) throws SQLException {
        try {
            this.resultSet.updateLong(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader reader) throws SQLException {
        try {
            this.resultSet.updateNCharacterStream(columnIndex, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader reader, final long length) throws SQLException {
        try {
            this.resultSet.updateNCharacterStream(columnIndex, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        try {
            this.resultSet.updateNCharacterStream(columnLabel, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        try {
            this.resultSet.updateNCharacterStream(columnLabel, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNClob(final int columnIndex, final NClob value) throws SQLException {
        try {
            this.resultSet.updateNClob(columnIndex, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader) throws SQLException {
        try {
            this.resultSet.updateNClob(columnIndex, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        try {
            this.resultSet.updateNClob(columnIndex, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNClob(final String columnLabel, final NClob value) throws SQLException {
        try {
            this.resultSet.updateNClob(columnLabel, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
        try {
            this.resultSet.updateNClob(columnLabel, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        try {
            this.resultSet.updateNClob(columnLabel, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNString(final int columnIndex, final String value) throws SQLException {
        try {
            this.resultSet.updateNString(columnIndex, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNString(final String columnLabel, final String value) throws SQLException {
        try {
            this.resultSet.updateNString(columnLabel, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNull(final int columnIndex) throws SQLException {
        try {
            this.resultSet.updateNull(columnIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateNull(final String columnName) throws SQLException {
        try {
            this.resultSet.updateNull(columnName);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateObject(final int columnIndex, final Object x) throws SQLException {
        try {
            this.resultSet.updateObject(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateObject(final int columnIndex, final Object x, final int scale) throws SQLException {
        try {
            this.resultSet.updateObject(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateObject(final String columnName, final Object x) throws SQLException {
        try {
            this.resultSet.updateObject(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateObject(final String columnName, final Object x, final int scale) throws SQLException {
        try {
            this.resultSet.updateObject(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateRef(final int columnIndex, final Ref x) throws SQLException {
        try {
            this.resultSet.updateRef(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateRef(final String columnName, final Ref x) throws SQLException {
        try {
            this.resultSet.updateRef(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateRow() throws SQLException {
        try {
            this.resultSet.updateRow();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateRowId(final int columnIndex, final RowId value) throws SQLException {
        try {
            this.resultSet.updateRowId(columnIndex, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateRowId(final String columnLabel, final RowId value) throws SQLException {
        try {
            this.resultSet.updateRowId(columnLabel, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateShort(final int columnIndex, final short x) throws SQLException {
        try {
            this.resultSet.updateShort(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateShort(final String columnName, final short x) throws SQLException {
        try {
            this.resultSet.updateShort(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateSQLXML(final int columnIndex, final SQLXML value) throws SQLException {
        try {
            this.resultSet.updateSQLXML(columnIndex, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateSQLXML(final String columnLabel, final SQLXML value) throws SQLException {
        try {
            this.resultSet.updateSQLXML(columnLabel, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateString(final int columnIndex, final String x) throws SQLException {
        try {
            this.resultSet.updateString(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateString(final String columnName, final String x) throws SQLException {
        try {
            this.resultSet.updateString(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateTime(final int columnIndex, final Time x) throws SQLException {
        try {
            this.resultSet.updateTime(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateTime(final String columnName, final Time x) throws SQLException {
        try {
            this.resultSet.updateTime(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateTimestamp(final int columnIndex, final Timestamp x) throws SQLException {
        try {
            this.resultSet.updateTimestamp(columnIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void updateTimestamp(final String columnName, final Timestamp x) throws SQLException {
        try {
            this.resultSet.updateTimestamp(columnName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        try {
            return this.resultSet.wasNull();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
}
