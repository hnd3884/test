package com.adventnet.db.adapter;

import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.RowId;
import java.net.URL;
import java.util.Calendar;
import java.sql.Array;
import java.sql.Ref;
import java.util.Map;
import java.sql.Statement;
import java.io.Reader;
import java.sql.SQLWarning;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Blob;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;

public class ResultSetAdapter implements ResultSet
{
    protected String dbType;
    private boolean closed;
    protected ResultSet rs;
    protected ResultSetMetaData rsmd;
    protected boolean rangeHandled;
    
    public ResultSetAdapter(final ResultSet rs) throws SQLException {
        this(rs, rs.getMetaData());
    }
    
    public ResultSetAdapter(final ResultSet rs, final TypeTransformer transformer) throws SQLException {
        this(rs, new ResultSetMetaDataAdapter(rs.getMetaData(), transformer));
    }
    
    public ResultSetAdapter(final ResultSet rs, final ResultSetMetaData rsmd) {
        this.dbType = null;
        this.closed = false;
        this.rs = null;
        this.rsmd = null;
        this.rs = rs;
        this.rsmd = rsmd;
    }
    
    public ResultSet getResultSet() {
        return this.rs;
    }
    
    public int getColumnCount() throws SQLException {
        return this.rsmd.getColumnCount();
    }
    
    public String getColumnName(final int columnIndex) throws SQLException {
        return this.rsmd.getColumnName(columnIndex);
    }
    
    public String getColumnLabel(final int columnIndex) throws SQLException {
        return this.rsmd.getColumnLabel(columnIndex);
    }
    
    public String getTableName(final int columnIndex) throws SQLException {
        return this.rsmd.getTableName(columnIndex);
    }
    
    public int getColumnType(final int columnIndex) throws SQLException {
        return this.rsmd.getColumnType(columnIndex);
    }
    
    @Override
    public int findColumn(final String columnName) throws SQLException {
        final int index = this.rs.findColumn(columnName);
        if (index < 1) {
            throw new SQLException("Column not found in the ResultSet:" + columnName);
        }
        return index;
    }
    
    @Override
    public boolean next() throws SQLException {
        return this.rs.next();
    }
    
    @Override
    public int getRow() throws SQLException {
        return this.rs.getRow();
    }
    
    @Override
    public boolean relative(final int rows) throws SQLException {
        if (rows < 0) {
            throw new SQLException("Only forward movement of cursor is allowed");
        }
        return this.rs.relative(rows);
    }
    
    @Override
    public void close() throws SQLException {
        if (this.closed) {
            return;
        }
        this.rs.close();
        this.closed = true;
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        return this.rs.wasNull();
    }
    
    @Deprecated
    public String getString(final int columnIndex, final int type) throws SQLException {
        return this.rs.getString(columnIndex);
    }
    
    @Deprecated
    public long getLong(final int columnIndex, final int type) throws SQLException {
        return this.rs.getLong(columnIndex);
    }
    
    @Deprecated
    public float getFloat(final int columnIndex, final int type) throws SQLException {
        return this.rs.getFloat(columnIndex);
    }
    
    @Deprecated
    public double getDouble(final int columnIndex, final int type) throws SQLException {
        return this.rs.getDouble(columnIndex);
    }
    
    @Deprecated
    public InputStream getBlob(final int columnIndex, final int type) throws SQLException {
        final Blob blob = this.rs.getBlob(columnIndex);
        if (blob != null) {
            return blob.getBinaryStream();
        }
        return null;
    }
    
    @Deprecated
    public Clob getClob(final int columnIndex, final int type) throws SQLException {
        return this.rs.getClob(columnIndex);
    }
    
    @Deprecated
    public int getInt(final int columnIndex, final int type) throws SQLException {
        return this.rs.getInt(columnIndex);
    }
    
    @Deprecated
    public BigDecimal getDecimal(final String columnAlias, final int type) throws SQLException {
        return this.rs.getBigDecimal(columnAlias);
    }
    
    @Deprecated
    public BigDecimal getDecimal(final int columnIndex, final int type) throws SQLException {
        return this.rs.getBigDecimal(columnIndex);
    }
    
    @Deprecated
    public String getString(final String columnName, final int type) throws SQLException {
        return this.rs.getString(columnName);
    }
    
    @Deprecated
    public InputStream getBlob(final String columnName, final int type) throws SQLException {
        final Blob blob = this.rs.getBlob(columnName);
        if (blob != null) {
            return blob.getBinaryStream();
        }
        return null;
    }
    
    @Deprecated
    public Clob getClob(final String columnName, final int type) throws SQLException {
        return this.rs.getClob(columnName);
    }
    
    @Deprecated
    public double getDouble(final String columnName, final int type) throws SQLException {
        return this.rs.getDouble(columnName);
    }
    
    @Deprecated
    public float getFloat(final String columnName, final int type) throws SQLException {
        return this.rs.getFloat(columnName);
    }
    
    @Deprecated
    public boolean getBoolean(final String columnName, final int type) throws SQLException {
        return this.rs.getBoolean(columnName);
    }
    
    @Deprecated
    public boolean getBoolean(final int columnIndex, final int type) throws SQLException {
        return this.rs.getBoolean(columnIndex);
    }
    
    @Deprecated
    public int getInt(final String columnName, final int type) throws SQLException {
        return this.rs.getInt(columnName);
    }
    
    @Deprecated
    public long getLong(final String columnName, final int type) throws SQLException {
        return this.rs.getLong(columnName);
    }
    
    @Deprecated
    public Date getDate(final String columnName, final int type) throws SQLException {
        return this.rs.getDate(columnName);
    }
    
    @Deprecated
    public Date getDate(final int columnIndex, final int type) throws SQLException {
        return this.rs.getDate(columnIndex);
    }
    
    @Deprecated
    public Time getTime(final String columnName, final int type) throws SQLException {
        return this.rs.getTime(columnName);
    }
    
    @Deprecated
    public Time getTime(final int columnIndex, final int type) throws SQLException {
        return this.rs.getTime(columnIndex);
    }
    
    @Deprecated
    public Timestamp getTimestamp(final String columnName, final int type) throws SQLException {
        return this.rs.getTimestamp(columnName);
    }
    
    @Deprecated
    public Timestamp getTimestamp(final int columnIndex, final int type) throws SQLException {
        return this.rs.getTimestamp(columnIndex);
    }
    
    public boolean isRangeHandled() {
        return this.rangeHandled;
    }
    
    public void setRangeHandled(final boolean newRangeHandled) {
        this.rangeHandled = newRangeHandled;
    }
    
    @Deprecated
    public Object getObject(final int columnIndex, final int type) throws SQLException {
        return this.rs.getObject(columnIndex);
    }
    
    @Deprecated
    public Object getObject(final String columnName, final int type) throws SQLException {
        return this.rs.getObject(columnName);
    }
    
    public boolean isNumber(final int columnIndex) throws SQLException {
        return this.getType(columnIndex) == -5;
    }
    
    public boolean isReal(final int columnIndex) throws SQLException {
        return this.getType(columnIndex) == 3;
    }
    
    public boolean isChar(final int columnIndex) throws SQLException {
        return this.getType(columnIndex) == 1;
    }
    
    public boolean isDate(final int columnIndex) throws SQLException {
        return this.getType(columnIndex) == 91;
    }
    
    public boolean isTime(final int columnIndex) throws SQLException {
        return this.getType(columnIndex) == 92;
    }
    
    public boolean isTimestamp(final int columnIndex) throws SQLException {
        return this.getType(columnIndex) == 93;
    }
    
    public boolean isBlob(final int columnIndex) throws SQLException {
        return this.getType(columnIndex) == 2004;
    }
    
    public boolean isBoolean(final int columnIndex) throws SQLException {
        return this.getType(columnIndex) == 16;
    }
    
    public String getClassType(final int columnIndex) throws SQLException {
        final int type = this.getType(columnIndex);
        switch (type) {
            case -5: {
                return Long.class.getName();
            }
            case 1: {
                return String.class.getName();
            }
            case 91: {
                return Timestamp.class.getName();
            }
            case 16: {
                return Boolean.class.getName();
            }
            case 2004: {
                return InputStream.class.getName();
            }
            default: {
                return Object.class.getName();
            }
        }
    }
    
    private int getType(final int columnIndex) throws SQLException {
        final int type = this.getColumnType(columnIndex);
        switch (type) {
            case -6:
            case -5:
            case 4: {
                return -5;
            }
            case 3:
            case 6:
            case 8: {
                return 3;
            }
            case -3:
            case -2:
            case -1:
            case 1:
            case 12:
            case 2005: {
                return 1;
            }
            case 91:
            case 92:
            case 93:
            case 2004: {
                return type;
            }
            case -7:
            case 16: {
                return 16;
            }
            default: {
                return 1111;
            }
        }
    }
    
    @Override
    public int getFetchSize() throws SQLException {
        return this.rs.getStatement().getFetchSize();
    }
    
    @Override
    public int getType() throws SQLException {
        return this.rs.getType();
    }
    
    public DTResultSetAdapter getDTResultSetAdapter(final String dataType) {
        if (DataTypeManager.isDataTypeSupported(dataType)) {
            return DataTypeManager.getDataTypeDefinition(dataType).getDTResultSetAdapter(this.getDBType());
        }
        return null;
    }
    
    public String getDBType() {
        return this.dbType;
    }
    
    @Override
    public String getString(final int columnIndex) throws SQLException {
        return this.rs.getString(columnIndex);
    }
    
    @Override
    public boolean getBoolean(final int columnIndex) throws SQLException {
        return this.rs.getBoolean(columnIndex);
    }
    
    @Override
    public byte getByte(final int columnIndex) throws SQLException {
        return this.rs.getByte(columnIndex);
    }
    
    @Override
    public short getShort(final int columnIndex) throws SQLException {
        return this.rs.getShort(columnIndex);
    }
    
    @Override
    public int getInt(final int columnIndex) throws SQLException {
        return this.rs.getInt(columnIndex);
    }
    
    @Override
    public long getLong(final int columnIndex) throws SQLException {
        return this.rs.getLong(columnIndex);
    }
    
    @Override
    public float getFloat(final int columnIndex) throws SQLException {
        return this.rs.getFloat(columnIndex);
    }
    
    @Override
    public double getDouble(final int columnIndex) throws SQLException {
        return this.rs.getDouble(columnIndex);
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
        return this.rs.getBigDecimal(columnIndex, scale);
    }
    
    @Override
    public byte[] getBytes(final int columnIndex) throws SQLException {
        return this.rs.getBytes(columnIndex);
    }
    
    @Override
    public Date getDate(final int columnIndex) throws SQLException {
        return this.rs.getDate(columnIndex);
    }
    
    @Override
    public Time getTime(final int columnIndex) throws SQLException {
        return this.rs.getTime(columnIndex);
    }
    
    @Override
    public Timestamp getTimestamp(final int columnIndex) throws SQLException {
        return this.rs.getTimestamp(columnIndex);
    }
    
    @Override
    public InputStream getAsciiStream(final int columnIndex) throws SQLException {
        return this.rs.getAsciiStream(columnIndex);
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        return this.rs.getUnicodeStream(columnIndex);
    }
    
    @Override
    public InputStream getBinaryStream(final int columnIndex) throws SQLException {
        return this.rs.getBinaryStream(columnIndex);
    }
    
    @Override
    public String getString(final String columnLabel) throws SQLException {
        return this.rs.getString(columnLabel);
    }
    
    @Override
    public boolean getBoolean(final String columnLabel) throws SQLException {
        return this.rs.getBoolean(columnLabel);
    }
    
    @Override
    public byte getByte(final String columnLabel) throws SQLException {
        return this.rs.getByte(columnLabel);
    }
    
    @Override
    public short getShort(final String columnLabel) throws SQLException {
        return this.rs.getShort(columnLabel);
    }
    
    @Override
    public int getInt(final String columnLabel) throws SQLException {
        return this.rs.getInt(columnLabel);
    }
    
    @Override
    public long getLong(final String columnLabel) throws SQLException {
        return this.rs.getLong(columnLabel);
    }
    
    @Override
    public float getFloat(final String columnLabel) throws SQLException {
        return this.rs.getFloat(columnLabel);
    }
    
    @Override
    public double getDouble(final String columnLabel) throws SQLException {
        return this.rs.getDouble(columnLabel);
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final String columnLabel, final int scale) throws SQLException {
        return this.rs.getBigDecimal(columnLabel, scale);
    }
    
    @Override
    public byte[] getBytes(final String columnLabel) throws SQLException {
        return this.rs.getBytes(columnLabel);
    }
    
    @Override
    public Date getDate(final String columnLabel) throws SQLException {
        return this.rs.getDate(columnLabel);
    }
    
    @Override
    public Time getTime(final String columnLabel) throws SQLException {
        return this.rs.getTime(columnLabel);
    }
    
    @Override
    public Timestamp getTimestamp(final String columnLabel) throws SQLException {
        return this.rs.getTimestamp(columnLabel);
    }
    
    @Override
    public InputStream getAsciiStream(final String columnLabel) throws SQLException {
        return this.rs.getAsciiStream(columnLabel);
    }
    
    @Deprecated
    @Override
    public InputStream getUnicodeStream(final String columnLabel) throws SQLException {
        return this.rs.getUnicodeStream(columnLabel);
    }
    
    @Override
    public InputStream getBinaryStream(final String columnLabel) throws SQLException {
        return this.rs.getBinaryStream(columnLabel);
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.rs.getWarnings();
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        this.rs.clearWarnings();
    }
    
    @Override
    public String getCursorName() throws SQLException {
        return this.rs.getCursorName();
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.rsmd;
    }
    
    @Override
    public Object getObject(final int columnIndex) throws SQLException {
        return this.rs.getObject(columnIndex);
    }
    
    @Override
    public Object getObject(final String columnLabel) throws SQLException {
        return this.rs.getObject(columnLabel);
    }
    
    @Override
    public Reader getCharacterStream(final int columnIndex) throws SQLException {
        return this.rs.getCharacterStream(columnIndex);
    }
    
    @Override
    public Reader getCharacterStream(final String columnLabel) throws SQLException {
        return this.rs.getCharacterStream(columnLabel);
    }
    
    @Override
    public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        return this.rs.getBigDecimal(columnIndex);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String columnLabel) throws SQLException {
        return this.rs.getBigDecimal(columnLabel);
    }
    
    @Override
    public boolean isBeforeFirst() throws SQLException {
        return this.rs.isBeforeFirst();
    }
    
    @Override
    public boolean isAfterLast() throws SQLException {
        return this.rs.isAfterLast();
    }
    
    @Override
    public boolean isFirst() throws SQLException {
        return this.rs.isFirst();
    }
    
    @Override
    public boolean isLast() throws SQLException {
        return this.rs.isLast();
    }
    
    @Override
    public void beforeFirst() throws SQLException {
        this.rs.beforeFirst();
    }
    
    @Override
    public void afterLast() throws SQLException {
        this.rs.afterLast();
    }
    
    @Override
    public boolean first() throws SQLException {
        return this.rs.first();
    }
    
    @Override
    public boolean last() throws SQLException {
        return this.rs.last();
    }
    
    @Override
    public boolean absolute(final int row) throws SQLException {
        return this.rs.absolute(row);
    }
    
    @Override
    public boolean previous() throws SQLException {
        return this.rs.previous();
    }
    
    @Override
    public void setFetchDirection(final int direction) throws SQLException {
        this.rs.setFetchDirection(direction);
    }
    
    @Override
    public int getFetchDirection() throws SQLException {
        return this.rs.getFetchDirection();
    }
    
    @Override
    public void setFetchSize(final int rows) throws SQLException {
        this.rs.setFetchSize(rows);
    }
    
    @Override
    public int getConcurrency() throws SQLException {
        return this.rs.getConcurrency();
    }
    
    @Override
    public boolean rowUpdated() throws SQLException {
        return this.rs.rowUpdated();
    }
    
    @Override
    public boolean rowInserted() throws SQLException {
        return this.rs.rowInserted();
    }
    
    @Override
    public boolean rowDeleted() throws SQLException {
        return this.rs.rowDeleted();
    }
    
    @Override
    public void updateNull(final int columnIndex) throws SQLException {
        this.rs.updateNull(columnIndex);
    }
    
    @Override
    public void updateBoolean(final int columnIndex, final boolean x) throws SQLException {
        this.rs.updateBoolean(columnIndex, x);
    }
    
    @Override
    public void updateByte(final int columnIndex, final byte x) throws SQLException {
        this.rs.updateByte(columnIndex, x);
    }
    
    @Override
    public void updateShort(final int columnIndex, final short x) throws SQLException {
        this.rs.updateShort(columnIndex, x);
    }
    
    @Override
    public void updateInt(final int columnIndex, final int x) throws SQLException {
        this.rs.updateInt(columnIndex, x);
    }
    
    @Override
    public void updateLong(final int columnIndex, final long x) throws SQLException {
        this.rs.updateLong(columnIndex, x);
    }
    
    @Override
    public void updateFloat(final int columnIndex, final float x) throws SQLException {
        this.rs.updateFloat(columnIndex, x);
    }
    
    @Override
    public void updateDouble(final int columnIndex, final double x) throws SQLException {
        this.rs.updateDouble(columnIndex, x);
    }
    
    @Override
    public void updateBigDecimal(final int columnIndex, final BigDecimal x) throws SQLException {
        this.rs.updateBigDecimal(columnIndex, x);
    }
    
    @Override
    public void updateString(final int columnIndex, final String x) throws SQLException {
        this.rs.updateString(columnIndex, x);
    }
    
    @Override
    public void updateBytes(final int columnIndex, final byte[] x) throws SQLException {
        this.rs.updateBytes(columnIndex, x);
    }
    
    @Override
    public void updateDate(final int columnIndex, final Date x) throws SQLException {
        this.rs.updateDate(columnIndex, x);
    }
    
    @Override
    public void updateTime(final int columnIndex, final Time x) throws SQLException {
        this.rs.updateTime(columnIndex, x);
    }
    
    @Override
    public void updateTimestamp(final int columnIndex, final Timestamp x) throws SQLException {
        this.rs.updateTimestamp(columnIndex, x);
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        this.rs.updateAsciiStream(columnIndex, x, length);
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
        this.rs.updateBinaryStream(columnIndex, x, length);
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final int length) throws SQLException {
        this.rs.updateCharacterStream(columnIndex, x, length);
    }
    
    @Override
    public void updateObject(final int columnIndex, final Object x, final int scaleOrLength) throws SQLException {
        this.rs.updateObject(columnIndex, x, scaleOrLength);
    }
    
    @Override
    public void updateObject(final int columnIndex, final Object x) throws SQLException {
        this.rs.updateObject(columnIndex, x);
    }
    
    @Override
    public void updateNull(final String columnLabel) throws SQLException {
        this.rs.updateNull(columnLabel);
    }
    
    @Override
    public void updateBoolean(final String columnLabel, final boolean x) throws SQLException {
        this.rs.updateBoolean(columnLabel, x);
    }
    
    @Override
    public void updateByte(final String columnLabel, final byte x) throws SQLException {
        this.rs.updateByte(columnLabel, x);
    }
    
    @Override
    public void updateShort(final String columnLabel, final short x) throws SQLException {
        this.rs.updateShort(columnLabel, x);
    }
    
    @Override
    public void updateInt(final String columnLabel, final int x) throws SQLException {
        this.rs.updateInt(columnLabel, x);
    }
    
    @Override
    public void updateLong(final String columnLabel, final long x) throws SQLException {
        this.rs.updateLong(columnLabel, x);
    }
    
    @Override
    public void updateFloat(final String columnLabel, final float x) throws SQLException {
        this.rs.updateFloat(columnLabel, x);
    }
    
    @Override
    public void updateDouble(final String columnLabel, final double x) throws SQLException {
        this.rs.updateDouble(columnLabel, x);
    }
    
    @Override
    public void updateBigDecimal(final String columnLabel, final BigDecimal x) throws SQLException {
        this.rs.updateBigDecimal(columnLabel, x);
    }
    
    @Override
    public void updateString(final String columnLabel, final String x) throws SQLException {
        this.rs.updateString(columnLabel, x);
    }
    
    @Override
    public void updateBytes(final String columnLabel, final byte[] x) throws SQLException {
        this.rs.updateBytes(columnLabel, x);
    }
    
    @Override
    public void updateDate(final String columnLabel, final Date x) throws SQLException {
        this.rs.updateDate(columnLabel, x);
    }
    
    @Override
    public void updateTime(final String columnLabel, final Time x) throws SQLException {
        this.rs.updateTime(columnLabel, x);
    }
    
    @Override
    public void updateTimestamp(final String columnLabel, final Timestamp x) throws SQLException {
        this.rs.updateTimestamp(columnLabel, x);
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
        this.rs.updateAsciiStream(columnLabel, x, length);
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x, final int length) throws SQLException {
        this.rs.updateBinaryStream(columnLabel, x, length);
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader, final int length) throws SQLException {
        this.rs.updateCharacterStream(columnLabel, reader, length);
    }
    
    @Override
    public void updateObject(final String columnLabel, final Object x, final int scaleOrLength) throws SQLException {
        this.rs.updateObject(columnLabel, x, scaleOrLength);
    }
    
    @Override
    public void updateObject(final String columnLabel, final Object x) throws SQLException {
        this.rs.updateObject(columnLabel, x);
    }
    
    @Override
    public void insertRow() throws SQLException {
        this.rs.insertRow();
    }
    
    @Override
    public void updateRow() throws SQLException {
        this.rs.updateRow();
    }
    
    @Override
    public void deleteRow() throws SQLException {
        this.rs.deleteRow();
    }
    
    @Override
    public void refreshRow() throws SQLException {
        this.rs.refreshRow();
    }
    
    @Override
    public void cancelRowUpdates() throws SQLException {
        this.rs.cancelRowUpdates();
    }
    
    @Override
    public void moveToInsertRow() throws SQLException {
        this.rs.moveToInsertRow();
    }
    
    @Override
    public void moveToCurrentRow() throws SQLException {
        this.rs.moveToCurrentRow();
    }
    
    @Override
    public Statement getStatement() throws SQLException {
        return this.rs.getStatement();
    }
    
    @Override
    public Object getObject(final int columnIndex, final Map<String, Class<?>> map) throws SQLException {
        return this.rs.getObject(columnIndex, map);
    }
    
    @Override
    public Ref getRef(final int columnIndex) throws SQLException {
        return this.rs.getRef(columnIndex);
    }
    
    @Override
    public Blob getBlob(final int columnIndex) throws SQLException {
        return this.rs.getBlob(columnIndex);
    }
    
    @Override
    public Clob getClob(final int columnIndex) throws SQLException {
        return this.rs.getClob(columnIndex);
    }
    
    @Override
    public Array getArray(final int columnIndex) throws SQLException {
        return this.rs.getArray(columnIndex);
    }
    
    @Override
    public Object getObject(final String columnLabel, final Map<String, Class<?>> map) throws SQLException {
        return this.rs.getObject(columnLabel, map);
    }
    
    @Override
    public Ref getRef(final String columnLabel) throws SQLException {
        return this.rs.getRef(columnLabel);
    }
    
    @Override
    public Blob getBlob(final String columnLabel) throws SQLException {
        return this.rs.getBlob(columnLabel);
    }
    
    @Override
    public Clob getClob(final String columnLabel) throws SQLException {
        return this.rs.getClob(columnLabel);
    }
    
    @Override
    public Array getArray(final String columnLabel) throws SQLException {
        return this.rs.getArray(columnLabel);
    }
    
    @Override
    public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
        return this.rs.getDate(columnIndex, cal);
    }
    
    @Override
    public Date getDate(final String columnLabel, final Calendar cal) throws SQLException {
        return this.rs.getDate(columnLabel, cal);
    }
    
    @Override
    public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
        return this.rs.getTime(columnIndex, cal);
    }
    
    @Override
    public Time getTime(final String columnLabel, final Calendar cal) throws SQLException {
        return this.rs.getTime(columnLabel, cal);
    }
    
    @Override
    public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
        return this.rs.getTimestamp(columnIndex, cal);
    }
    
    @Override
    public Timestamp getTimestamp(final String columnLabel, final Calendar cal) throws SQLException {
        return this.rs.getTimestamp(columnLabel, cal);
    }
    
    @Override
    public URL getURL(final int columnIndex) throws SQLException {
        return this.rs.getURL(columnIndex);
    }
    
    @Override
    public URL getURL(final String columnLabel) throws SQLException {
        return this.rs.getURL(columnLabel);
    }
    
    @Override
    public void updateRef(final int columnIndex, final Ref x) throws SQLException {
        this.rs.updateRef(columnIndex, x);
    }
    
    @Override
    public void updateRef(final String columnLabel, final Ref x) throws SQLException {
        this.rs.updateRef(columnLabel, x);
    }
    
    @Override
    public void updateBlob(final int columnIndex, final Blob x) throws SQLException {
        this.rs.updateBlob(columnIndex, x);
    }
    
    @Override
    public void updateBlob(final String columnLabel, final Blob x) throws SQLException {
        this.rs.updateBlob(columnLabel, x);
    }
    
    @Override
    public void updateClob(final int columnIndex, final Clob x) throws SQLException {
        this.rs.updateClob(columnIndex, x);
    }
    
    @Override
    public void updateClob(final String columnLabel, final Clob x) throws SQLException {
        this.rs.updateClob(columnLabel, x);
    }
    
    @Override
    public void updateArray(final int columnIndex, final Array x) throws SQLException {
        this.rs.updateArray(columnIndex, x);
    }
    
    @Override
    public void updateArray(final String columnLabel, final Array x) throws SQLException {
        this.rs.updateArray(columnLabel, x);
    }
    
    @Override
    public RowId getRowId(final int columnIndex) throws SQLException {
        return this.rs.getRowId(columnIndex);
    }
    
    @Override
    public RowId getRowId(final String columnLabel) throws SQLException {
        return this.rs.getRowId(columnLabel);
    }
    
    @Override
    public void updateRowId(final int columnIndex, final RowId x) throws SQLException {
        this.rs.updateRowId(columnIndex, x);
    }
    
    @Override
    public void updateRowId(final String columnLabel, final RowId x) throws SQLException {
        this.rs.updateRowId(columnLabel, x);
    }
    
    @Override
    public int getHoldability() throws SQLException {
        return this.rs.getHoldability();
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return this.rs.isClosed();
    }
    
    @Override
    public void updateNString(final int columnIndex, final String nString) throws SQLException {
        this.rs.updateNString(columnIndex, nString);
    }
    
    @Override
    public void updateNString(final String columnLabel, final String nString) throws SQLException {
        this.rs.updateNString(columnLabel, nString);
    }
    
    @Override
    public void updateNClob(final int columnIndex, final NClob nClob) throws SQLException {
        this.rs.updateNClob(columnIndex, nClob);
    }
    
    @Override
    public void updateNClob(final String columnLabel, final NClob nClob) throws SQLException {
        this.rs.updateNClob(columnLabel, nClob);
    }
    
    @Override
    public NClob getNClob(final int columnIndex) throws SQLException {
        return this.rs.getNClob(columnIndex);
    }
    
    @Override
    public NClob getNClob(final String columnLabel) throws SQLException {
        return this.rs.getNClob(columnLabel);
    }
    
    @Override
    public SQLXML getSQLXML(final int columnIndex) throws SQLException {
        return this.rs.getSQLXML(columnIndex);
    }
    
    @Override
    public SQLXML getSQLXML(final String columnLabel) throws SQLException {
        return this.rs.getSQLXML(columnLabel);
    }
    
    @Override
    public void updateSQLXML(final int columnIndex, final SQLXML xmlObject) throws SQLException {
        this.rs.updateSQLXML(columnIndex, xmlObject);
    }
    
    @Override
    public void updateSQLXML(final String columnLabel, final SQLXML xmlObject) throws SQLException {
        this.rs.updateSQLXML(columnLabel, xmlObject);
    }
    
    @Override
    public String getNString(final int columnIndex) throws SQLException {
        return this.rs.getNString(columnIndex);
    }
    
    @Override
    public String getNString(final String columnLabel) throws SQLException {
        return this.rs.getNString(columnLabel);
    }
    
    @Override
    public Reader getNCharacterStream(final int columnIndex) throws SQLException {
        return this.rs.getNCharacterStream(columnIndex);
    }
    
    @Override
    public Reader getNCharacterStream(final String columnLabel) throws SQLException {
        return this.rs.getNCharacterStream(columnLabel);
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        this.rs.updateNCharacterStream(columnIndex, x, length);
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        this.rs.updateNCharacterStream(columnLabel, reader, length);
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        this.rs.updateAsciiStream(columnIndex, x, length);
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x, final long length) throws SQLException {
        this.rs.updateBinaryStream(columnIndex, x, length);
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x, final long length) throws SQLException {
        this.rs.updateCharacterStream(columnIndex, x, length);
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        this.rs.updateAsciiStream(columnLabel, x, length);
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x, final long length) throws SQLException {
        this.rs.updateBinaryStream(columnLabel, x, length);
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader, final long length) throws SQLException {
        this.rs.updateCharacterStream(columnLabel, reader, length);
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream, final long length) throws SQLException {
        this.rs.updateBlob(columnIndex, inputStream, length);
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream, final long length) throws SQLException {
        this.rs.updateBlob(columnLabel, inputStream, length);
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        this.rs.updateClob(columnIndex, reader, length);
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        this.rs.updateClob(columnLabel, reader, length);
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader, final long length) throws SQLException {
        this.rs.updateNClob(columnIndex, reader, length);
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader, final long length) throws SQLException {
        this.rs.updateNClob(columnLabel, reader, length);
    }
    
    @Override
    public void updateNCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        this.rs.updateNCharacterStream(columnIndex, x);
    }
    
    @Override
    public void updateNCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        this.rs.updateNCharacterStream(columnLabel, reader);
    }
    
    @Override
    public void updateAsciiStream(final int columnIndex, final InputStream x) throws SQLException {
        this.rs.updateAsciiStream(columnIndex, x);
    }
    
    @Override
    public void updateBinaryStream(final int columnIndex, final InputStream x) throws SQLException {
        this.rs.updateBinaryStream(columnIndex, x);
    }
    
    @Override
    public void updateCharacterStream(final int columnIndex, final Reader x) throws SQLException {
        this.rs.updateCharacterStream(columnIndex, x);
    }
    
    @Override
    public void updateAsciiStream(final String columnLabel, final InputStream x) throws SQLException {
        this.rs.updateAsciiStream(columnLabel, x);
    }
    
    @Override
    public void updateBinaryStream(final String columnLabel, final InputStream x) throws SQLException {
        this.rs.updateBinaryStream(columnLabel, x);
    }
    
    @Override
    public void updateCharacterStream(final String columnLabel, final Reader reader) throws SQLException {
        this.rs.updateCharacterStream(columnLabel, reader);
    }
    
    @Override
    public void updateBlob(final int columnIndex, final InputStream inputStream) throws SQLException {
        this.rs.updateBlob(columnIndex, inputStream);
    }
    
    @Override
    public void updateBlob(final String columnLabel, final InputStream inputStream) throws SQLException {
        this.rs.updateBlob(columnLabel, inputStream);
    }
    
    @Override
    public void updateClob(final int columnIndex, final Reader reader) throws SQLException {
        this.rs.updateClob(columnIndex, reader);
    }
    
    @Override
    public void updateClob(final String columnLabel, final Reader reader) throws SQLException {
        this.rs.updateClob(columnLabel, reader);
    }
    
    @Override
    public void updateNClob(final int columnIndex, final Reader reader) throws SQLException {
        this.rs.updateNClob(columnIndex, reader);
    }
    
    @Override
    public void updateNClob(final String columnLabel, final Reader reader) throws SQLException {
        this.rs.updateNClob(columnLabel, reader);
    }
    
    @Override
    public <T> T getObject(final int columnIndex, final Class<T> type) throws SQLException {
        return this.rs.getObject(columnIndex, type);
    }
    
    @Override
    public <T> T getObject(final String columnLabel, final Class<T> type) throws SQLException {
        return this.rs.getObject(columnLabel, type);
    }
    
    @Override
    public void updateObject(final int columnIndex, final Object x, final SQLType targetSqlType, final int scaleOrLength) throws SQLException {
        this.rs.updateObject(columnIndex, x, targetSqlType, scaleOrLength);
    }
    
    @Override
    public void updateObject(final String columnLabel, final Object x, final SQLType targetSqlType, final int scaleOrLength) throws SQLException {
        this.rs.updateObject(columnLabel, x, targetSqlType, scaleOrLength);
    }
    
    @Override
    public void updateObject(final int columnIndex, final Object x, final SQLType targetSqlType) throws SQLException {
        this.rs.updateObject(columnIndex, x, targetSqlType);
    }
    
    @Override
    public void updateObject(final String columnLabel, final Object x, final SQLType targetSqlType) throws SQLException {
        this.rs.updateObject(columnLabel, x, targetSqlType);
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return this.rs.unwrap(iface);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return this.rs.isWrapperFor(iface);
    }
    
    public InputStream getBlobAsInputStream(final int columnIndex) throws SQLException {
        final Blob blob = this.rs.getBlob(columnIndex);
        if (blob != null) {
            return blob.getBinaryStream();
        }
        return null;
    }
    
    public InputStream getBlobAsInputStream(final String columnName) throws SQLException {
        final Blob blob = this.rs.getBlob(columnName);
        if (blob != null) {
            return blob.getBinaryStream();
        }
        return null;
    }
}
