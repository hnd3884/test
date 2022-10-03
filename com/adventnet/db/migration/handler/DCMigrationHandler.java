package com.adventnet.db.migration.handler;

import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import com.adventnet.db.adapter.DBAdapter;

public interface DCMigrationHandler
{
    void preInvokeForDynamicColumns(final DBAdapter p0, final Connection p1, final List<Long> p2) throws SQLException;
    
    void postInvokeForDynamicColumns(final DBAdapter p0, final Connection p1, final List<Long> p2) throws SQLException;
}
