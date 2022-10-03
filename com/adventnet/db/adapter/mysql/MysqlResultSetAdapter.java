package com.adventnet.db.adapter.mysql;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import com.adventnet.db.adapter.TypeTransformer;
import java.sql.ResultSet;
import com.adventnet.db.adapter.ResultSetAdapter;

public class MysqlResultSetAdapter extends ResultSetAdapter
{
    public MysqlResultSetAdapter(final ResultSet rs) throws SQLException {
        this(rs, (TypeTransformer)null);
    }
    
    public MysqlResultSetAdapter(final ResultSet rs, final TypeTransformer transformer) throws SQLException {
        super(rs, new MysqlResultSetMetaDataAdapter(rs.getMetaData(), transformer));
        this.dbType = "mysql";
    }
}
