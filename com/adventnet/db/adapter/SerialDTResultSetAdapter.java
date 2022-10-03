package com.adventnet.db.adapter;

import java.sql.SQLException;

public class SerialDTResultSetAdapter implements DTResultSetAdapter
{
    @Override
    public Object getValue(final ResultSetAdapter rs, final int columnIndex, final String dataType) throws SQLException {
        if (rs.getString(columnIndex) == null) {
            return null;
        }
        return Long.valueOf(rs.getString(columnIndex));
    }
    
    @Override
    public Object getValue(final ResultSetAdapter rs, final String columnAlias, final String dataType) throws SQLException {
        if (rs.getString(columnAlias) == null) {
            return null;
        }
        return Long.valueOf(rs.getString(columnAlias));
    }
}
