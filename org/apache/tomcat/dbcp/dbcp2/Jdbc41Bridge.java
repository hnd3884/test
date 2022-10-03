package org.apache.tomcat.dbcp.dbcp2;

import java.util.logging.Logger;
import javax.sql.CommonDataSource;
import java.sql.Statement;
import java.sql.SQLFeatureNotSupportedException;
import java.net.URL;
import java.sql.SQLXML;
import java.sql.RowId;
import java.sql.Ref;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Array;
import java.io.Reader;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.sql.Connection;

public class Jdbc41Bridge
{
    public static void abort(final Connection connection, final Executor executor) throws SQLException {
        try {
            connection.abort(executor);
        }
        catch (final AbstractMethodError e) {
            connection.close();
        }
    }
    
    public static boolean generatedKeyAlwaysReturned(final DatabaseMetaData databaseMetaData) throws SQLException {
        try {
            return databaseMetaData.generatedKeyAlwaysReturned();
        }
        catch (final AbstractMethodError e) {
            return false;
        }
    }
    
    public static int getNetworkTimeout(final Connection connection) throws SQLException {
        try {
            return connection.getNetworkTimeout();
        }
        catch (final AbstractMethodError e) {
            return 0;
        }
    }
    
    public static <T> T getObject(final ResultSet resultSet, final int columnIndex, final Class<T> type) throws SQLException {
        try {
            return resultSet.getObject(columnIndex, type);
        }
        catch (final AbstractMethodError e) {
            if (type == String.class) {
                return (T)resultSet.getString(columnIndex);
            }
            if (type == Integer.class) {
                return (T)Integer.valueOf(resultSet.getInt(columnIndex));
            }
            if (type == Long.class) {
                return (T)Long.valueOf(resultSet.getLong(columnIndex));
            }
            if (type == Double.class) {
                return (T)Double.valueOf(resultSet.getDouble(columnIndex));
            }
            if (type == Float.class) {
                return (T)Float.valueOf(resultSet.getFloat(columnIndex));
            }
            if (type == Short.class) {
                return (T)Short.valueOf(resultSet.getShort(columnIndex));
            }
            if (type == BigDecimal.class) {
                return (T)resultSet.getBigDecimal(columnIndex);
            }
            if (type == Byte.class) {
                return (T)Byte.valueOf(resultSet.getByte(columnIndex));
            }
            if (type == Date.class) {
                return (T)resultSet.getDate(columnIndex);
            }
            if (type == Time.class) {
                return (T)resultSet.getTime(columnIndex);
            }
            if (type == Timestamp.class) {
                return (T)resultSet.getTimestamp(columnIndex);
            }
            if (type == InputStream.class) {
                return (T)resultSet.getBinaryStream(columnIndex);
            }
            if (type == Reader.class) {
                return (T)resultSet.getCharacterStream(columnIndex);
            }
            if (type == Object.class) {
                return (T)resultSet.getObject(columnIndex);
            }
            if (type == Boolean.class) {
                return (T)Boolean.valueOf(resultSet.getBoolean(columnIndex));
            }
            if (type == Array.class) {
                return (T)resultSet.getArray(columnIndex);
            }
            if (type == Blob.class) {
                return (T)resultSet.getBlob(columnIndex);
            }
            if (type == Clob.class) {
                return (T)resultSet.getClob(columnIndex);
            }
            if (type == Ref.class) {
                return (T)resultSet.getRef(columnIndex);
            }
            if (type == RowId.class) {
                return (T)resultSet.getRowId(columnIndex);
            }
            if (type == SQLXML.class) {
                return (T)resultSet.getSQLXML(columnIndex);
            }
            if (type == URL.class) {
                return (T)resultSet.getURL(columnIndex);
            }
            throw new SQLFeatureNotSupportedException(String.format("resultSet=%s, columnIndex=%,d, type=%s", resultSet, columnIndex, type));
        }
    }
    
    public static <T> T getObject(final ResultSet resultSet, final String columnLabel, final Class<T> type) throws SQLException {
        try {
            return resultSet.getObject(columnLabel, type);
        }
        catch (final AbstractMethodError e) {
            if (type == Integer.class) {
                return (T)Integer.valueOf(resultSet.getInt(columnLabel));
            }
            if (type == Long.class) {
                return (T)Long.valueOf(resultSet.getLong(columnLabel));
            }
            if (type == Double.class) {
                return (T)Double.valueOf(resultSet.getDouble(columnLabel));
            }
            if (type == Float.class) {
                return (T)Float.valueOf(resultSet.getFloat(columnLabel));
            }
            if (type == Short.class) {
                return (T)Short.valueOf(resultSet.getShort(columnLabel));
            }
            if (type == BigDecimal.class) {
                return (T)resultSet.getBigDecimal(columnLabel);
            }
            if (type == Byte.class) {
                return (T)Byte.valueOf(resultSet.getByte(columnLabel));
            }
            if (type == Date.class) {
                return (T)resultSet.getDate(columnLabel);
            }
            if (type == Time.class) {
                return (T)resultSet.getTime(columnLabel);
            }
            if (type == Timestamp.class) {
                return (T)resultSet.getTimestamp(columnLabel);
            }
            if (type == InputStream.class) {
                return (T)resultSet.getBinaryStream(columnLabel);
            }
            if (type == Reader.class) {
                return (T)resultSet.getCharacterStream(columnLabel);
            }
            if (type == Object.class) {
                return (T)resultSet.getObject(columnLabel);
            }
            if (type == Boolean.class) {
                return (T)Boolean.valueOf(resultSet.getBoolean(columnLabel));
            }
            if (type == Array.class) {
                return (T)resultSet.getArray(columnLabel);
            }
            if (type == Blob.class) {
                return (T)resultSet.getBlob(columnLabel);
            }
            if (type == Clob.class) {
                return (T)resultSet.getClob(columnLabel);
            }
            if (type == Ref.class) {
                return (T)resultSet.getRef(columnLabel);
            }
            if (type == RowId.class) {
                return (T)resultSet.getRowId(columnLabel);
            }
            if (type == SQLXML.class) {
                return (T)resultSet.getSQLXML(columnLabel);
            }
            if (type == URL.class) {
                return (T)resultSet.getURL(columnLabel);
            }
            throw new SQLFeatureNotSupportedException(String.format("resultSet=%s, columnLabel=%s, type=%s", resultSet, columnLabel, type));
        }
    }
    
    public static ResultSet getPseudoColumns(final DatabaseMetaData databaseMetaData, final String catalog, final String schemaPattern, final String tableNamePattern, final String columnNamePattern) throws SQLException {
        try {
            return databaseMetaData.getPseudoColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
        }
        catch (final AbstractMethodError e) {
            return null;
        }
    }
    
    public static String getSchema(final Connection connection) throws SQLException {
        try {
            return connection.getSchema();
        }
        catch (final AbstractMethodError e) {
            return null;
        }
    }
    
    public static void setNetworkTimeout(final Connection connection, final Executor executor, final int milliseconds) throws SQLException {
        try {
            connection.setNetworkTimeout(executor, milliseconds);
        }
        catch (final AbstractMethodError abstractMethodError) {}
    }
    
    public static void setSchema(final Connection connection, final String schema) throws SQLException {
        try {
            connection.setSchema(schema);
        }
        catch (final AbstractMethodError abstractMethodError) {}
    }
    
    public static void closeOnCompletion(final Statement statement) throws SQLException {
        try {
            statement.closeOnCompletion();
        }
        catch (final AbstractMethodError e) {
            if (statement.isClosed()) {
                throw new SQLException("Statement closed");
            }
        }
    }
    
    public static boolean isCloseOnCompletion(final Statement statement) throws SQLException {
        try {
            return statement.isCloseOnCompletion();
        }
        catch (final AbstractMethodError e) {
            if (statement.isClosed()) {
                throw new SQLException("Statement closed");
            }
            return false;
        }
    }
    
    public static Logger getParentLogger(final CommonDataSource commonDataSource) throws SQLFeatureNotSupportedException {
        try {
            return commonDataSource.getParentLogger();
        }
        catch (final AbstractMethodError e) {
            throw new SQLFeatureNotSupportedException("javax.sql.CommonDataSource#getParentLogger()");
        }
    }
}
