package com.adventnet.db.adapter.mysql;

import com.adventnet.db.adapter.TypeTransformer;
import java.sql.ResultSetMetaData;
import com.adventnet.db.adapter.ResultSetMetaDataAdapter;

public class MysqlResultSetMetaDataAdapter extends ResultSetMetaDataAdapter
{
    public MysqlResultSetMetaDataAdapter(final ResultSetMetaData rsmd, final TypeTransformer transformer) {
        super(rsmd, transformer);
    }
}
