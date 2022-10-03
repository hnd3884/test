package com.adventnet.db.adapter.mysql;

import java.sql.SQLException;
import com.adventnet.db.adapter.ResultSetAdapter;
import com.adventnet.db.adapter.DTResultSetAdapter;

public class MysqlUuidDTResultSetAdapter implements DTResultSetAdapter
{
    @Override
    public Object getValue(final ResultSetAdapter rs, final int columnIndex, final String dataType) throws SQLException {
        final String s = rs.getString(columnIndex);
        if (s == null) {
            return null;
        }
        return s;
    }
    
    @Override
    public Object getValue(final ResultSetAdapter rs, final String columnAlias, final String dataType) throws SQLException {
        final String s = rs.getString(columnAlias);
        if (s == null) {
            return null;
        }
        return s;
    }
}
