package com.adventnet.db.adapter.mssql;

import java.sql.SQLException;
import com.adventnet.db.adapter.TypeTransformer;
import java.sql.ResultSetMetaData;
import com.adventnet.db.adapter.ResultSetMetaDataAdapter;

public class MssqlResultSetMetaDataAdapter extends ResultSetMetaDataAdapter
{
    public MssqlResultSetMetaDataAdapter(final ResultSetMetaData rsmd, final TypeTransformer transformer) {
        super(rsmd, transformer);
    }
    
    @Override
    public int getColumnType(final int column) throws SQLException {
        int type;
        final int orginalType = type = this.rsmd.getColumnType(column);
        if (orginalType == 2005) {
            type = -1;
        }
        if (orginalType == -3) {
            type = -4;
        }
        else if (orginalType == -151) {
            type = 93;
        }
        type = this.transformer.getColumnType(orginalType, type, column, this);
        return type;
    }
}
