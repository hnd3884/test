package org.apache.tomcat.dbcp.dbcp2;

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
import java.sql.ParameterMetaData;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class DelegatingPreparedStatement extends DelegatingStatement implements PreparedStatement
{
    public DelegatingPreparedStatement(final DelegatingConnection<?> connection, final PreparedStatement statement) {
        super(connection, statement);
    }
    
    @Override
    public void addBatch() throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().addBatch();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void clearParameters() throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().clearParameters();
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public boolean execute() throws SQLException {
        this.checkOpen();
        if (this.getConnectionInternal() != null) {
            this.getConnectionInternal().setLastUsed();
        }
        try {
            return this.getDelegatePreparedStatement().execute();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return false;
        }
    }
    
    @Override
    public ResultSet executeQuery() throws SQLException {
        this.checkOpen();
        if (this.getConnectionInternal() != null) {
            this.getConnectionInternal().setLastUsed();
        }
        try {
            return DelegatingResultSet.wrapResultSet(this, this.getDelegatePreparedStatement().executeQuery());
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public int executeUpdate() throws SQLException {
        this.checkOpen();
        if (this.getConnectionInternal() != null) {
            this.getConnectionInternal().setLastUsed();
        }
        try {
            return this.getDelegatePreparedStatement().executeUpdate();
        }
        catch (final SQLException e) {
            this.handleException(e);
            return 0;
        }
    }
    
    private PreparedStatement getDelegatePreparedStatement() {
        return (PreparedStatement)this.getDelegate();
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegatePreparedStatement().getMetaData();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegatePreparedStatement().getParameterMetaData();
        }
        catch (final SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }
    
    @Override
    public void setArray(final int i, final Array x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setArray(i, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setAsciiStream(parameterIndex, inputStream);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setAsciiStream(parameterIndex, x, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setAsciiStream(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setAsciiStream(parameterIndex, inputStream, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBigDecimal(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBinaryStream(parameterIndex, inputStream);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBinaryStream(parameterIndex, x, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBinaryStream(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBinaryStream(parameterIndex, inputStream, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBlob(final int i, final Blob x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBlob(i, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBlob(parameterIndex, inputStream);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBlob(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBlob(parameterIndex, inputStream, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBoolean(final int parameterIndex, final boolean x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBoolean(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setByte(final int parameterIndex, final byte x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setByte(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setBytes(final int parameterIndex, final byte[] x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBytes(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setCharacterStream(parameterIndex, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final int length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setCharacterStream(parameterIndex, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setCharacterStream(parameterIndex, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setClob(final int i, final Clob x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setClob(i, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setClob(final int parameterIndex, final Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setClob(parameterIndex, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setClob(parameterIndex, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setDate(final int parameterIndex, final Date x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setDate(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setDate(final int parameterIndex, final Date x, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setDate(parameterIndex, x, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setDouble(final int parameterIndex, final double x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setDouble(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setFloat(final int parameterIndex, final float x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setFloat(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setInt(final int parameterIndex, final int x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setInt(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setLong(final int parameterIndex, final long x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setLong(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNCharacterStream(parameterIndex, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNCharacterStream(final int parameterIndex, final Reader value, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNCharacterStream(parameterIndex, value, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNClob(final int parameterIndex, final NClob value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNClob(parameterIndex, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNClob(final int parameterIndex, final Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNClob(parameterIndex, reader);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNClob(parameterIndex, reader, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNString(final int parameterIndex, final String value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNString(parameterIndex, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNull(final int parameterIndex, final int sqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNull(parameterIndex, sqlType);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setNull(final int paramIndex, final int sqlType, final String typeName) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNull(paramIndex, sqlType, typeName);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setObject(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setObject(parameterIndex, x, targetSqlType);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setObject(final int parameterIndex, final Object x, final int targetSqlType, final int scale) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setObject(parameterIndex, x, targetSqlType, scale);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setRef(final int i, final Ref x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setRef(i, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setRowId(final int parameterIndex, final RowId value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setRowId(parameterIndex, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setShort(final int parameterIndex, final short x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setShort(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setSQLXML(final int parameterIndex, final SQLXML value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setSQLXML(parameterIndex, value);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setString(final int parameterIndex, final String x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setString(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTime(final int parameterIndex, final Time x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setTime(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTime(final int parameterIndex, final Time x, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setTime(parameterIndex, x, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setTimestamp(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setTimestamp(final int parameterIndex, final Timestamp x, final Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setTimestamp(parameterIndex, x, cal);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Deprecated
    @Override
    public void setUnicodeStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setUnicodeStream(parameterIndex, x, length);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public void setURL(final int parameterIndex, final URL x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setURL(parameterIndex, x);
        }
        catch (final SQLException e) {
            this.handleException(e);
        }
    }
    
    @Override
    public synchronized String toString() {
        final Statement statement = this.getDelegate();
        return (statement == null) ? "NULL" : statement.toString();
    }
}
