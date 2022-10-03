package com.zoho.cp;

import java.sql.SQLWarning;
import java.net.URL;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Statement;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.Ref;
import java.util.Map;
import java.sql.NClob;
import java.sql.ResultSetMetaData;
import java.sql.Date;
import java.util.Calendar;
import java.sql.Clob;
import java.io.Reader;
import java.sql.Blob;
import java.math.BigDecimal;
import java.io.InputStream;
import java.sql.Array;
import java.sql.SQLException;
import java.sql.ResultSet;

public class WrappedResultSet implements ResultSet
{
    private ResultSet rs;
    private LogicalConnection logCon;
    
    public WrappedResultSet(final ResultSet rs, final LogicalConnection connection) {
        this.rs = rs;
        this.logCon = connection;
    }
    
    @Override
    public boolean absolute(final int arg0) throws SQLException {
        try {
            return this.rs.absolute(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void afterLast() throws SQLException {
        try {
            this.rs.afterLast();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        try {
            this.rs.beforeFirst();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void cancelRowUpdates() throws SQLException {
        try {
            this.rs.cancelRowUpdates();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        try {
            this.rs.clearWarnings();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void close() throws SQLException {
        try {
            this.rs.close();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void deleteRow() throws SQLException {
        try {
            this.rs.deleteRow();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int findColumn(final String arg0) throws SQLException {
        try {
            return this.rs.findColumn(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean first() throws SQLException {
        try {
            return this.rs.first();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Array getArray(final int arg0) throws SQLException {
        try {
            return this.rs.getArray(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Array getArray(final String arg0) throws SQLException {
        try {
            return this.rs.getArray(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public InputStream getAsciiStream(final int arg0) throws SQLException {
        try {
            return this.rs.getAsciiStream(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public InputStream getAsciiStream(final String arg0) throws SQLException {
        try {
            return this.rs.getAsciiStream(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public BigDecimal getBigDecimal(final int arg0, final int arg1) throws SQLException {
        try {
            return this.rs.getBigDecimal(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public BigDecimal getBigDecimal(final int arg0) throws SQLException {
        try {
            return this.rs.getBigDecimal(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public BigDecimal getBigDecimal(final String arg0, final int arg1) throws SQLException {
        try {
            return this.rs.getBigDecimal(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public BigDecimal getBigDecimal(final String arg0) throws SQLException {
        try {
            return this.rs.getBigDecimal(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public InputStream getBinaryStream(final int arg0) throws SQLException {
        try {
            return this.rs.getBinaryStream(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public InputStream getBinaryStream(final String arg0) throws SQLException {
        try {
            return this.rs.getBinaryStream(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Blob getBlob(final int arg0) throws SQLException {
        try {
            return this.rs.getBlob(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Blob getBlob(final String arg0) throws SQLException {
        try {
            return this.rs.getBlob(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean getBoolean(final int arg0) throws SQLException {
        try {
            return this.rs.getBoolean(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean getBoolean(final String arg0) throws SQLException {
        try {
            return this.rs.getBoolean(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public byte getByte(final int arg0) throws SQLException {
        try {
            return this.rs.getByte(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public byte getByte(final String arg0) throws SQLException {
        try {
            return this.rs.getByte(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public byte[] getBytes(final int arg0) throws SQLException {
        try {
            return this.rs.getBytes(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public byte[] getBytes(final String arg0) throws SQLException {
        try {
            return this.rs.getBytes(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Reader getCharacterStream(final int arg0) throws SQLException {
        try {
            return this.rs.getCharacterStream(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Reader getCharacterStream(final String arg0) throws SQLException {
        try {
            return this.rs.getCharacterStream(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Clob getClob(final int arg0) throws SQLException {
        try {
            return this.rs.getClob(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Clob getClob(final String arg0) throws SQLException {
        try {
            return this.rs.getClob(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getConcurrency() throws SQLException {
        try {
            return this.rs.getConcurrency();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String getCursorName() throws SQLException {
        try {
            return this.rs.getCursorName();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Date getDate(final int arg0, final Calendar arg1) throws SQLException {
        try {
            return this.rs.getDate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Date getDate(final int arg0) throws SQLException {
        try {
            return this.rs.getDate(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Date getDate(final String arg0, final Calendar arg1) throws SQLException {
        try {
            return this.rs.getDate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Date getDate(final String arg0) throws SQLException {
        try {
            return this.rs.getDate(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public double getDouble(final int arg0) throws SQLException {
        try {
            return this.rs.getDouble(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public double getDouble(final String arg0) throws SQLException {
        try {
            return this.rs.getDouble(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        try {
            return this.rs.getFetchDirection();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        try {
            return this.rs.getFetchSize();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public float getFloat(final int arg0) throws SQLException {
        try {
            return this.rs.getFloat(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public float getFloat(final String arg0) throws SQLException {
        try {
            return this.rs.getFloat(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getHoldability() throws SQLException {
        try {
            return this.rs.getHoldability();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getInt(final int arg0) throws SQLException {
        try {
            return this.rs.getInt(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getInt(final String arg0) throws SQLException {
        try {
            return this.rs.getInt(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public long getLong(final int arg0) throws SQLException {
        try {
            return this.rs.getLong(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public long getLong(final String arg0) throws SQLException {
        try {
            return this.rs.getLong(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return this.rs.getMetaData();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Reader getNCharacterStream(final int arg0) throws SQLException {
        try {
            return this.rs.getNCharacterStream(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Reader getNCharacterStream(final String arg0) throws SQLException {
        try {
            return this.rs.getNCharacterStream(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public NClob getNClob(final int arg0) throws SQLException {
        try {
            return this.rs.getNClob(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public NClob getNClob(final String arg0) throws SQLException {
        try {
            return this.rs.getNClob(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String getNString(final int arg0) throws SQLException {
        try {
            return this.rs.getNString(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String getNString(final String arg0) throws SQLException {
        try {
            return this.rs.getNString(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Object getObject(final int arg0, final Map<String, Class<?>> arg1) throws SQLException {
        try {
            return this.rs.getObject(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Object getObject(final int arg0) throws SQLException {
        try {
            return this.rs.getObject(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Object getObject(final String arg0, final Map<String, Class<?>> arg1) throws SQLException {
        try {
            return this.rs.getObject(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Object getObject(final String arg0) throws SQLException {
        try {
            return this.rs.getObject(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Ref getRef(final int arg0) throws SQLException {
        try {
            return this.rs.getRef(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Ref getRef(final String arg0) throws SQLException {
        try {
            return this.rs.getRef(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getRow() throws SQLException {
        try {
            return this.rs.getRow();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public RowId getRowId(final int arg0) throws SQLException {
        try {
            return this.rs.getRowId(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public RowId getRowId(final String arg0) throws SQLException {
        try {
            return this.rs.getRowId(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public SQLXML getSQLXML(final int arg0) throws SQLException {
        try {
            return this.rs.getSQLXML(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public SQLXML getSQLXML(final String arg0) throws SQLException {
        try {
            return this.rs.getSQLXML(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public short getShort(final int arg0) throws SQLException {
        try {
            return this.rs.getShort(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public short getShort(final String arg0) throws SQLException {
        try {
            return this.rs.getShort(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Statement getStatement() throws SQLException {
        try {
            return this.rs.getStatement();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String getString(final int arg0) throws SQLException {
        try {
            return this.rs.getString(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public String getString(final String arg0) throws SQLException {
        try {
            return this.rs.getString(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Time getTime(final int arg0, final Calendar arg1) throws SQLException {
        try {
            return this.rs.getTime(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Time getTime(final int arg0) throws SQLException {
        try {
            return this.rs.getTime(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Time getTime(final String arg0, final Calendar arg1) throws SQLException {
        try {
            return this.rs.getTime(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Time getTime(final String arg0) throws SQLException {
        try {
            return this.rs.getTime(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final int arg0, final Calendar arg1) throws SQLException {
        try {
            return this.rs.getTimestamp(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final int arg0) throws SQLException {
        try {
            return this.rs.getTimestamp(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final String arg0, final Calendar arg1) throws SQLException {
        try {
            return this.rs.getTimestamp(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final String arg0) throws SQLException {
        try {
            return this.rs.getTimestamp(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public int getType() throws SQLException {
        try {
            return this.rs.getType();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public URL getURL(final int arg0) throws SQLException {
        try {
            return this.rs.getURL(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public URL getURL(final String arg0) throws SQLException {
        try {
            return this.rs.getURL(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public InputStream getUnicodeStream(final int arg0) throws SQLException {
        try {
            return this.rs.getUnicodeStream(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public InputStream getUnicodeStream(final String arg0) throws SQLException {
        try {
            return this.rs.getUnicodeStream(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        try {
            return this.rs.getWarnings();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void insertRow() throws SQLException {
        try {
            this.rs.insertRow();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isAfterLast() throws SQLException {
        try {
            return this.rs.isAfterLast();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isBeforeFirst() throws SQLException {
        try {
            return this.rs.isBeforeFirst();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        try {
            return this.rs.isClosed();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isFirst() throws SQLException {
        try {
            return this.rs.isFirst();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isLast() throws SQLException {
        try {
            return this.rs.isLast();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> arg0) throws SQLException {
        try {
            return this.rs.isWrapperFor(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean last() throws SQLException {
        try {
            return this.rs.last();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void moveToCurrentRow() throws SQLException {
        try {
            this.rs.moveToCurrentRow();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void moveToInsertRow() throws SQLException {
        try {
            this.rs.moveToInsertRow();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean next() throws SQLException {
        try {
            return this.rs.next();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean previous() throws SQLException {
        try {
            return this.rs.previous();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void refreshRow() throws SQLException {
        try {
            this.rs.refreshRow();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean relative(final int arg0) throws SQLException {
        try {
            return this.rs.relative(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean rowDeleted() throws SQLException {
        try {
            return this.rs.rowDeleted();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean rowInserted() throws SQLException {
        try {
            return this.rs.rowInserted();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean rowUpdated() throws SQLException {
        try {
            return this.rs.rowUpdated();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setFetchDirection(final int arg0) throws SQLException {
        try {
            this.rs.setFetchDirection(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void setFetchSize(final int arg0) throws SQLException {
        try {
            this.rs.setFetchSize(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public <T> T unwrap(final Class<T> arg0) throws SQLException {
        try {
            return this.rs.unwrap(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateArray(final int arg0, final Array arg1) throws SQLException {
        try {
            this.rs.updateArray(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateArray(final String arg0, final Array arg1) throws SQLException {
        try {
            this.rs.updateArray(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateAsciiStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
        try {
            this.rs.updateAsciiStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateAsciiStream(final int arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateAsciiStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateAsciiStream(final int arg0, final InputStream arg1) throws SQLException {
        try {
            this.rs.updateAsciiStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateAsciiStream(final String arg0, final InputStream arg1, final int arg2) throws SQLException {
        try {
            this.rs.updateAsciiStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateAsciiStream(final String arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateAsciiStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateAsciiStream(final String arg0, final InputStream arg1) throws SQLException {
        try {
            this.rs.updateAsciiStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBigDecimal(final int arg0, final BigDecimal arg1) throws SQLException {
        try {
            this.rs.updateBigDecimal(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBigDecimal(final String arg0, final BigDecimal arg1) throws SQLException {
        try {
            this.rs.updateBigDecimal(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBinaryStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
        try {
            this.rs.updateBinaryStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBinaryStream(final int arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateBinaryStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBinaryStream(final int arg0, final InputStream arg1) throws SQLException {
        try {
            this.rs.updateBinaryStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBinaryStream(final String arg0, final InputStream arg1, final int arg2) throws SQLException {
        try {
            this.rs.updateBinaryStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBinaryStream(final String arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateBinaryStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBinaryStream(final String arg0, final InputStream arg1) throws SQLException {
        try {
            this.rs.updateBinaryStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBlob(final int arg0, final Blob arg1) throws SQLException {
        try {
            this.rs.updateBlob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBlob(final int arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateBlob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBlob(final int arg0, final InputStream arg1) throws SQLException {
        try {
            this.rs.updateBlob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBlob(final String arg0, final Blob arg1) throws SQLException {
        try {
            this.rs.updateBlob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBlob(final String arg0, final InputStream arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateBlob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBlob(final String arg0, final InputStream arg1) throws SQLException {
        try {
            this.rs.updateBlob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBoolean(final int arg0, final boolean arg1) throws SQLException {
        try {
            this.rs.updateBoolean(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBoolean(final String arg0, final boolean arg1) throws SQLException {
        try {
            this.rs.updateBoolean(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateByte(final int arg0, final byte arg1) throws SQLException {
        try {
            this.rs.updateByte(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateByte(final String arg0, final byte arg1) throws SQLException {
        try {
            this.rs.updateByte(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBytes(final int arg0, final byte[] arg1) throws SQLException {
        try {
            this.rs.updateBytes(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateBytes(final String arg0, final byte[] arg1) throws SQLException {
        try {
            this.rs.updateBytes(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateCharacterStream(final int arg0, final Reader arg1, final int arg2) throws SQLException {
        try {
            this.rs.updateCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateCharacterStream(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateCharacterStream(final int arg0, final Reader arg1) throws SQLException {
        try {
            this.rs.updateCharacterStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateCharacterStream(final String arg0, final Reader arg1, final int arg2) throws SQLException {
        try {
            this.rs.updateCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateCharacterStream(final String arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateCharacterStream(final String arg0, final Reader arg1) throws SQLException {
        try {
            this.rs.updateCharacterStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateClob(final int arg0, final Clob arg1) throws SQLException {
        try {
            this.rs.updateClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateClob(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateClob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateClob(final int arg0, final Reader arg1) throws SQLException {
        try {
            this.rs.updateClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateClob(final String arg0, final Clob arg1) throws SQLException {
        this.rs.updateClob(arg0, arg1);
    }
    
    @Override
    public void updateClob(final String arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateClob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateClob(final String arg0, final Reader arg1) throws SQLException {
        try {
            this.rs.updateClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateDate(final int arg0, final Date arg1) throws SQLException {
        try {
            this.rs.updateDate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateDate(final String arg0, final Date arg1) throws SQLException {
        try {
            this.rs.updateDate(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateDouble(final int arg0, final double arg1) throws SQLException {
        try {
            this.rs.updateDouble(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateDouble(final String arg0, final double arg1) throws SQLException {
        try {
            this.rs.updateDouble(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateFloat(final int arg0, final float arg1) throws SQLException {
        try {
            this.rs.updateFloat(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateFloat(final String arg0, final float arg1) throws SQLException {
        try {
            this.rs.updateFloat(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateInt(final int arg0, final int arg1) throws SQLException {
        try {
            this.rs.updateInt(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateInt(final String arg0, final int arg1) throws SQLException {
        try {
            this.rs.updateInt(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateLong(final int arg0, final long arg1) throws SQLException {
        try {
            this.rs.updateLong(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateLong(final String arg0, final long arg1) throws SQLException {
        try {
            this.rs.updateLong(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNCharacterStream(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateNCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNCharacterStream(final int arg0, final Reader arg1) throws SQLException {
        try {
            this.rs.updateNCharacterStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNCharacterStream(final String arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateNCharacterStream(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNCharacterStream(final String arg0, final Reader arg1) throws SQLException {
        try {
            this.rs.updateNCharacterStream(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNClob(final int arg0, final NClob arg1) throws SQLException {
        try {
            this.rs.updateNClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNClob(final int arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateNClob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNClob(final int arg0, final Reader arg1) throws SQLException {
        try {
            this.rs.updateNClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNClob(final String arg0, final NClob arg1) throws SQLException {
        try {
            this.rs.updateNClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNClob(final String arg0, final Reader arg1, final long arg2) throws SQLException {
        try {
            this.rs.updateNClob(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNClob(final String arg0, final Reader arg1) throws SQLException {
        try {
            this.rs.updateNClob(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNString(final int arg0, final String arg1) throws SQLException {
        try {
            this.rs.updateNString(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNString(final String arg0, final String arg1) throws SQLException {
        try {
            this.rs.updateNString(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNull(final int arg0) throws SQLException {
        try {
            this.rs.updateNull(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateNull(final String arg0) throws SQLException {
        try {
            this.rs.updateNull(arg0);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateObject(final int arg0, final Object arg1, final int arg2) throws SQLException {
        try {
            this.rs.updateObject(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateObject(final int arg0, final Object arg1) throws SQLException {
        try {
            this.rs.updateObject(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateObject(final String arg0, final Object arg1, final int arg2) throws SQLException {
        try {
            this.rs.updateObject(arg0, arg1, arg2);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateObject(final String arg0, final Object arg1) throws SQLException {
        try {
            this.rs.updateObject(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateRef(final int arg0, final Ref arg1) throws SQLException {
        try {
            this.rs.updateRef(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateRef(final String arg0, final Ref arg1) throws SQLException {
        try {
            this.rs.updateRef(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateRow() throws SQLException {
        try {
            this.rs.updateRow();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateRowId(final int arg0, final RowId arg1) throws SQLException {
        try {
            this.rs.updateRowId(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateRowId(final String arg0, final RowId arg1) throws SQLException {
        try {
            this.rs.updateRowId(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateSQLXML(final int arg0, final SQLXML arg1) throws SQLException {
        try {
            this.rs.updateSQLXML(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateSQLXML(final String arg0, final SQLXML arg1) throws SQLException {
        try {
            this.rs.updateSQLXML(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateShort(final int arg0, final short arg1) throws SQLException {
        try {
            this.rs.updateShort(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateShort(final String arg0, final short arg1) throws SQLException {
        try {
            this.rs.updateShort(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateString(final int arg0, final String arg1) throws SQLException {
        try {
            this.rs.updateString(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateString(final String arg0, final String arg1) throws SQLException {
        try {
            this.rs.updateString(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateTime(final int arg0, final Time arg1) throws SQLException {
        try {
            this.rs.updateTime(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateTime(final String arg0, final Time arg1) throws SQLException {
        try {
            this.rs.updateTime(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateTimestamp(final int arg0, final Timestamp arg1) throws SQLException {
        try {
            this.rs.updateTimestamp(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public void updateTimestamp(final String arg0, final Timestamp arg1) throws SQLException {
        try {
            this.rs.updateTimestamp(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        try {
            return this.rs.wasNull();
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public <T> T getObject(final int arg0, final Class<T> arg1) throws SQLException {
        try {
            return this.rs.getObject(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
    
    @Override
    public <T> T getObject(final String arg0, final Class<T> arg1) throws SQLException {
        try {
            return this.rs.getObject(arg0, arg1);
        }
        catch (final SQLException exc) {
            this.logCon.handleException(exc);
            throw exc;
        }
    }
}
