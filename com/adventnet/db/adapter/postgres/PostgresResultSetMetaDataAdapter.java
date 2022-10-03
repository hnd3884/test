package com.adventnet.db.adapter.postgres;

import java.sql.SQLException;
import com.adventnet.db.adapter.TypeTransformer;
import java.sql.ResultSetMetaData;
import com.adventnet.db.adapter.ResultSetMetaDataAdapter;

public class PostgresResultSetMetaDataAdapter extends ResultSetMetaDataAdapter
{
    public PostgresResultSetMetaDataAdapter(final ResultSetMetaData rsmd, final TypeTransformer transformer) {
        super(rsmd, transformer);
    }
    
    @Override
    public int getColumnType(final int column) throws SQLException {
        int type;
        final int orginalIndex = type = this.rsmd.getColumnType(column);
        if (type == 1111) {
            final String typeName = this.rsmd.getColumnTypeName(column);
            if ("citext".equalsIgnoreCase(typeName)) {
                type = 12;
            }
        }
        type = this.transformer.getColumnType(orginalIndex, type, column, this);
        return type;
    }
}
