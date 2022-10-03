package org.apache.tomcat.dbcp.dbcp2;

import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.Ref;
import java.util.Map;
import java.sql.NClob;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Clob;
import java.io.Reader;
import java.sql.Blob;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;

public class DelegatingCallableStatement extends DelegatingPreparedStatement implements CallableStatement
{
    public DelegatingCallableStatement(final DelegatingConnection<?> connection, final CallableStatement statement) {
        super(connection, statement);
    }
    
    @Override
    public Array getArray(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getArray(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Array getArray(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getArray(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public BigDecimal getBigDecimal(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBigDecimal(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Deprecated
    @Override
    public BigDecimal getBigDecimal(final int parameterIndex, final int scale) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBigDecimal(parameterIndex, scale);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public BigDecimal getBigDecimal(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBigDecimal(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Blob getBlob(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBlob(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Blob getBlob(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBlob(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public boolean getBoolean(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBoolean(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public boolean getBoolean(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBoolean(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public byte getByte(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getByte(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public byte getByte(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getByte(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public byte[] getBytes(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBytes(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public byte[] getBytes(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBytes(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Reader getCharacterStream(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getCharacterStream(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Reader getCharacterStream(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getCharacterStream(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Clob getClob(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getClob(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Clob getClob(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getClob(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Date getDate(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getDate(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Date getDate(final int parameterIndex, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getDate(parameterIndex, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Date getDate(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getDate(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Date getDate(final String parameterName, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getDate(parameterName, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    private CallableStatement getDelegateCallableStatement() {
        return (CallableStatement)this.getDelegate();
    }
    
    @Override
    public double getDouble(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getDouble(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0.0;
        }
    }
    
    @Override
    public double getDouble(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getDouble(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0.0;
        }
    }
    
    @Override
    public float getFloat(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getFloat(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0.0f;
        }
    }
    
    @Override
    public float getFloat(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getFloat(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0.0f;
        }
    }
    
    @Override
    public int getInt(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getInt(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public int getInt(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getInt(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public long getLong(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getLong(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0L;
        }
    }
    
    @Override
    public long getLong(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getLong(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0L;
        }
    }
    
    @Override
    public Reader getNCharacterStream(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getNCharacterStream(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Reader getNCharacterStream(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getNCharacterStream(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public NClob getNClob(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getNClob(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public NClob getNClob(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getNClob(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public String getNString(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getNString(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public String getNString(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getNString(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Object getObject(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getObject(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public <T> T getObject(final int parameterIndex, final Class<T> type) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getObject(parameterIndex, type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Object getObject(final int i, final Map<String, Class<?>> map) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getObject(i, map);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Object getObject(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getObject(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public <T> T getObject(final String parameterName, final Class<T> type) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getObject(parameterName, type);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Object getObject(final String parameterName, final Map<String, Class<?>> map) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getObject(parameterName, map);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Ref getRef(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getRef(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Ref getRef(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getRef(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public RowId getRowId(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getRowId(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public RowId getRowId(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getRowId(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public short getShort(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getShort(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public short getShort(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getShort(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    @Override
    public SQLXML getSQLXML(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getSQLXML(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public SQLXML getSQLXML(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getSQLXML(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public String getString(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getString(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public String getString(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getString(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Time getTime(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTime(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Time getTime(final int parameterIndex, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTime(parameterIndex, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Time getTime(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTime(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Time getTime(final String parameterName, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTime(parameterName, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTimestamp(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final int parameterIndex, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTimestamp(parameterIndex, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTimestamp(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public Timestamp getTimestamp(final String parameterName, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTimestamp(parameterName, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public URL getURL(final int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getURL(parameterIndex);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public URL getURL(final String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getURL(parameterName);
        }
        catch (final SQLException e) {
            this.handleException(e);
            return null;
        }
    }
    
    @Override
    public void registerOutParameter(final int parameterIndex, final int sqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterIndex, sqlType);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void registerOutParameter(final int parameterIndex, final int sqlType, final int scale) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterIndex, sqlType, scale);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void registerOutParameter(final int paramIndex, final int sqlType, final String typeName) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(paramIndex, sqlType, typeName);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final int sqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterName, sqlType);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final int sqlType, final int scale) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterName, sqlType, scale);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void registerOutParameter(final String parameterName, final int sqlType, final String typeName) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterName, sqlType, typeName);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setAsciiStream(final String parameterName, final InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setAsciiStream(parameterName, inputStream);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setAsciiStream(final String parameterName, final InputStream x, final int length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setAsciiStream(parameterName, x, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setAsciiStream(final String parameterName, final InputStream inputStream, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setAsciiStream(parameterName, inputStream, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBigDecimal(final String parameterName, final BigDecimal x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBigDecimal(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBinaryStream(final String parameterName, final InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBinaryStream(parameterName, inputStream);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBinaryStream(final String parameterName, final InputStream x, final int length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBinaryStream(parameterName, x, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBinaryStream(final String parameterName, final InputStream inputStream, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBinaryStream(parameterName, inputStream, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBlob(final String parameterName, final Blob blob) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBlob(parameterName, blob);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBlob(final String parameterName, final InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBlob(parameterName, inputStream);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBlob(final String parameterName, final InputStream inputStream, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBlob(parameterName, inputStream, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBoolean(final String parameterName, final boolean x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBoolean(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setByte(final String parameterName, final byte x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setByte(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBytes(final String parameterName, final byte[] x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBytes(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setCharacterStream(final String parameterName, final Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setCharacterStream(parameterName, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setCharacterStream(final String parameterName, final Reader reader, final int length) throws SQLException {
        this.checkOpen();
        this.getDelegateCallableStatement().setCharacterStream(parameterName, reader, length);
    }
    
    @Override
    public void setCharacterStream(final String parameterName, final Reader reader, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setCharacterStream(parameterName, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setClob(final String parameterName, final Clob clob) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setClob(parameterName, clob);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setClob(final String parameterName, final Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setClob(parameterName, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setClob(final String parameterName, final Reader reader, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setClob(parameterName, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setDate(final String parameterName, final Date x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setDate(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setDate(final String parameterName, final Date x, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setDate(parameterName, x, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setDouble(final String parameterName, final double x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setDouble(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setFloat(final String parameterName, final float x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setFloat(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setInt(final String parameterName, final int x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setInt(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setLong(final String parameterName, final long x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setLong(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNCharacterStream(final String parameterName, final Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNCharacterStream(parameterName, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNCharacterStream(final String parameterName, final Reader reader, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNCharacterStream(parameterName, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNClob(final String parameterName, final NClob value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNClob(parameterName, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNClob(final String parameterName, final Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNClob(parameterName, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNClob(final String parameterName, final Reader reader, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNClob(parameterName, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNString(final String parameterName, final String value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNString(parameterName, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNull(final String parameterName, final int sqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNull(parameterName, sqlType);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNull(final String parameterName, final int sqlType, final String typeName) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNull(parameterName, sqlType, typeName);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setObject(final String parameterName, final Object x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setObject(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setObject(final String parameterName, final Object x, final int targetSqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setObject(parameterName, x, targetSqlType);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setObject(final String parameterName, final Object x, final int targetSqlType, final int scale) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setObject(parameterName, x, targetSqlType, scale);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setRowId(final String parameterName, final RowId value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setRowId(parameterName, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setShort(final String parameterName, final short x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setShort(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setSQLXML(final String parameterName, final SQLXML value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setSQLXML(parameterName, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setString(final String parameterName, final String x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setString(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTime(final String parameterName, final Time x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setTime(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTime(final String parameterName, final Time x, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setTime(parameterName, x, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTimestamp(final String parameterName, final Timestamp x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setTimestamp(parameterName, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTimestamp(final String parameterName, final Timestamp x, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setTimestamp(parameterName, x, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setURL(final String parameterName, final URL val) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setURL(parameterName, val);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().wasNull();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
}
