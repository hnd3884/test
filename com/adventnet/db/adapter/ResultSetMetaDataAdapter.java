package com.adventnet.db.adapter;

import java.sql.SQLException;
import java.sql.ResultSetMetaData;

public class ResultSetMetaDataAdapter implements ResultSetMetaData
{
    protected final ResultSetMetaData rsmd;
    protected final TypeTransformer transformer;
    
    public ResultSetMetaDataAdapter(final ResultSetMetaData rsmd, final TypeTransformer transformer) {
        this.rsmd = rsmd;
        this.transformer = ((transformer == null) ? TypeTransformer.DEFAULT : transformer);
    }
    
    @Override
    public int getColumnCount() throws SQLException {
        return this.rsmd.getColumnCount();
    }
    
    @Override
    public boolean isAutoIncrement(final int column) throws SQLException {
        return this.rsmd.isAutoIncrement(column);
    }
    
    @Override
    public boolean isCaseSensitive(final int column) throws SQLException {
        return this.rsmd.isCaseSensitive(column);
    }
    
    @Override
    public boolean isSearchable(final int column) throws SQLException {
        return this.rsmd.isSearchable(column);
    }
    
    @Override
    public boolean isCurrency(final int column) throws SQLException {
        return this.rsmd.isCurrency(column);
    }
    
    @Override
    public int isNullable(final int column) throws SQLException {
        return this.rsmd.isNullable(column);
    }
    
    @Override
    public boolean isSigned(final int column) throws SQLException {
        return this.rsmd.isSigned(column);
    }
    
    @Override
    public int getColumnDisplaySize(final int column) throws SQLException {
        return this.rsmd.getColumnDisplaySize(column);
    }
    
    @Override
    public String getColumnLabel(final int column) throws SQLException {
        return this.rsmd.getColumnLabel(column);
    }
    
    @Override
    public String getColumnName(final int column) throws SQLException {
        return this.rsmd.getColumnName(column);
    }
    
    @Override
    public String getSchemaName(final int column) throws SQLException {
        return this.rsmd.getSchemaName(column);
    }
    
    @Override
    public int getPrecision(final int column) throws SQLException {
        return this.rsmd.getPrecision(column);
    }
    
    @Override
    public int getScale(final int column) throws SQLException {
        return this.rsmd.getScale(column);
    }
    
    @Override
    public String getTableName(final int column) throws SQLException {
        return this.rsmd.getTableName(column);
    }
    
    @Override
    public String getCatalogName(final int column) throws SQLException {
        return this.rsmd.getCatalogName(column);
    }
    
    @Override
    public int getColumnType(final int column) throws SQLException {
        final int type = this.rsmd.getColumnType(column);
        return this.transformer.getColumnType(type, type, column, this);
    }
    
    @Override
    public String getColumnTypeName(final int column) throws SQLException {
        return this.rsmd.getColumnTypeName(column);
    }
    
    @Override
    public boolean isReadOnly(final int column) throws SQLException {
        return this.rsmd.isReadOnly(column);
    }
    
    @Override
    public boolean isWritable(final int column) throws SQLException {
        return this.rsmd.isWritable(column);
    }
    
    @Override
    public boolean isDefinitelyWritable(final int column) throws SQLException {
        return this.rsmd.isDefinitelyWritable(column);
    }
    
    @Override
    public String getColumnClassName(final int column) throws SQLException {
        return this.rsmd.getColumnClassName(column);
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return this.rsmd.unwrap(iface);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return this.rsmd.isWrapperFor(iface);
    }
}
