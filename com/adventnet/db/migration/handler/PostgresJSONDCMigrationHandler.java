package com.adventnet.db.migration.handler;

import java.sql.Statement;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.sql.SQLException;
import java.util.List;
import java.sql.Connection;
import com.adventnet.db.adapter.DBAdapter;

public class PostgresJSONDCMigrationHandler implements DCMigrationHandler
{
    @Override
    public void preInvokeForDynamicColumns(final DBAdapter destDBAdapter, final Connection connection, final List<Long> tableIds) throws SQLException {
    }
    
    @Override
    public void postInvokeForDynamicColumns(final DBAdapter destDBAdapter, final Connection connection, final List<Long> tableIds) throws SQLException {
        try {
            final UpdateQuery uq = new UpdateQueryImpl("ColumnDetails");
            uq.setUpdateColumn("PHYSICAL_COLUMN", "DYJSONCOL");
            final Criteria c1 = new Criteria(Column.getColumn("ColumnDetails", "TABLE_ID"), tableIds.toArray(), 8);
            final Criteria c2 = new Criteria(Column.getColumn("ColumnDetails", "IS_DYNAMIC"), true, 0);
            uq.setCriteria(c1.and(c2));
            final String updateSQL = destDBAdapter.getSQLGenerator().getSQLForUpdate(uq.getTableList(), uq.getUpdateColumns(), uq.getCriteria(), uq.getJoins());
            try (final Statement s = connection.createStatement()) {
                destDBAdapter.execute(s, updateSQL);
            }
        }
        catch (final QueryConstructionException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }
}
