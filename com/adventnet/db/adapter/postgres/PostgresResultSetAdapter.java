package com.adventnet.db.adapter.postgres;

import java.io.Reader;
import java.sql.Blob;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import com.adventnet.db.adapter.TypeTransformer;
import java.sql.ResultSet;
import com.adventnet.db.adapter.ResultSetAdapter;

public class PostgresResultSetAdapter extends ResultSetAdapter
{
    public PostgresResultSetAdapter(final ResultSet rs) throws SQLException {
        this(rs, (TypeTransformer)null);
    }
    
    public PostgresResultSetAdapter(final ResultSet rs, final TypeTransformer transformer) throws SQLException {
        super(rs, new PostgresResultSetMetaDataAdapter(rs.getMetaData(), transformer));
        this.dbType = "postgres";
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
    
    @Deprecated
    @Override
    public InputStream getBlob(final String columnName, final int type) throws SQLException {
        final Blob blob = this.rs.getBlob(columnName);
        if (blob != null) {
            return blob.getBinaryStream();
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
    public Reader getNCharacterStream(final int columnIndex) throws SQLException {
        return this.getCharacterStream(columnIndex);
    }
    
    @Override
    public Reader getNCharacterStream(final String columnLabel) throws SQLException {
        return this.getCharacterStream(columnLabel);
    }
}
