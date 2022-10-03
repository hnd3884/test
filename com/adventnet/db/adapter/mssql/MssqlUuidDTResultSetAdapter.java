package com.adventnet.db.adapter.mssql;

import java.sql.SQLException;
import com.adventnet.db.adapter.ResultSetAdapter;
import com.adventnet.db.adapter.DTResultSetAdapter;

public class MssqlUuidDTResultSetAdapter implements DTResultSetAdapter
{
    @Override
    public Object getValue(final ResultSetAdapter rs, final int columnIndex, final String dataType) throws SQLException {
        if (rs.getString(columnIndex) == null) {
            return null;
        }
        return rs.getString(columnIndex);
    }
    
    @Override
    public Object getValue(final ResultSetAdapter rs, final String columnAlias, final String dataType) throws SQLException {
        if (rs.getString(columnAlias) == null) {
            return null;
        }
        return rs.getString(columnAlias);
    }
}
