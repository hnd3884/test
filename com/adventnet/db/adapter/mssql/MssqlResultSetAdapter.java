package com.adventnet.db.adapter.mssql;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import com.adventnet.persistence.PersistenceInitializer;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import com.adventnet.db.adapter.TypeTransformer;
import java.sql.ResultSet;
import com.adventnet.db.adapter.ResultSetAdapter;

public class MssqlResultSetAdapter extends ResultSetAdapter
{
    public MssqlResultSetAdapter(final ResultSet rs) throws SQLException {
        this(rs, (TypeTransformer)null);
    }
    
    public MssqlResultSetAdapter(final ResultSet rs, final TypeTransformer transformer) throws SQLException {
        super(rs, new MssqlResultSetMetaDataAdapter(rs.getMetaData(), transformer));
        this.dbType = "mssql";
        this.rangeHandled = !Boolean.valueOf(PersistenceInitializer.getConfigurationValue("use_top_for_range"));
    }
    
    @Override
    public boolean relative(final int rows) throws SQLException {
        if (rows < 0) {
            throw new SQLException("Only forward movement of cursor is supported");
        }
        boolean result = true;
        for (int i = 0; i < rows; ++i) {
            if (!this.getResultSet().next()) {
                result = false;
                break;
            }
        }
        return result;
    }
    
    @Deprecated
    @Override
    public float getFloat(final int columnIndex, final int type) throws SQLException {
        final Object value = this.rs.getObject(columnIndex);
        if (value == null) {
            return 0.0f;
        }
        return new Float(value.toString());
    }
    
    @Deprecated
    @Override
    public float getFloat(final String columnName, final int type) throws SQLException {
        final Object value = this.rs.getObject(columnName);
        if (value == null) {
            return 0.0f;
        }
        return new Float(value.toString());
    }
    
    @Deprecated
    @Override
    public int getInt(final int columnIndex, final int type) throws SQLException {
        final Object value = this.rs.getObject(columnIndex);
        if (value == null) {
            return 0;
        }
        return new Integer(value.toString());
    }
    
    @Deprecated
    @Override
    public int getInt(final String columnName, final int type) throws SQLException {
        final Object value = this.rs.getObject(columnName);
        if (value == null) {
            return 0;
        }
        return new Integer(value.toString());
    }
    
    @Deprecated
    @Override
    public InputStream getBlob(final int columnIndex, final int type) throws SQLException {
        final byte[] temp = this.rs.getBytes(columnIndex);
        if (temp != null) {
            return new ByteArrayInputStream(temp);
        }
        return null;
    }
    
    @Override
    public InputStream getBlobAsInputStream(final int columnIndex) throws SQLException {
        final byte[] temp = this.rs.getBytes(columnIndex);
        if (temp != null) {
            return new ByteArrayInputStream(temp);
        }
        return null;
    }
    
    @Override
    public InputStream getBlobAsInputStream(final String columnName) throws SQLException {
        final byte[] temp = this.rs.getBytes(columnName);
        if (temp != null) {
            return new ByteArrayInputStream(temp);
        }
        return null;
    }
    
    @Override
    @Deprecated
    public float getFloat(final int columnIndex) throws SQLException {
        final Object value = this.rs.getObject(columnIndex);
        if (value == null) {
            return 0.0f;
        }
        return Float.parseFloat(value.toString());
    }
    
    @Override
    @Deprecated
    public float getFloat(final String columnName) throws SQLException {
        final Object value = this.rs.getObject(columnName);
        if (value == null) {
            return 0.0f;
        }
        return Float.parseFloat(value.toString());
    }
    
    @Override
    @Deprecated
    public int getInt(final int columnIndex) throws SQLException {
        final Object value = this.rs.getObject(columnIndex);
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value.toString());
    }
    
    @Override
    @Deprecated
    public int getInt(final String columnName) throws SQLException {
        final Object value = this.rs.getObject(columnName);
        if (value == null) {
            return 0;
        }
        return Integer.parseInt(value.toString());
    }
}
