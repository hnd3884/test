package com.adventnet.db.migration.handler;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;

public abstract class AbstractDBMigrationHandler implements DBMigrationHandler
{
    private static final Logger LOGGER;
    private String handlerName;
    
    public AbstractDBMigrationHandler() {
        this.handlerName = null;
    }
    
    @Override
    public final void setHandlerName(final String name) {
        this.handlerName = name;
    }
    
    @Override
    public final String getHandlerName() {
        return this.handlerName;
    }
    
    @Override
    public boolean processTable(final String tableName) {
        return true;
    }
    
    @Override
    public void preInvokeForCreateTable(final String tableName) throws Exception {
    }
    
    @Override
    public void preInvokeForFetchdata(final SelectQuery sQuery) throws Exception {
    }
    
    @Override
    public List<String> getSelectColumns(final SelectQuery sQuery) throws Exception {
        final List<String> colNames = new ArrayList<String>();
        final List<Column> cols = sQuery.getSelectColumns();
        for (int i = 0; i < cols.size(); ++i) {
            colNames.add(cols.get(i).getColumnName());
        }
        return colNames;
    }
    
    @Override
    public Row preInvokeForInsert(final Row row) throws Exception {
        return row;
    }
    
    @Override
    public void postInvokeForCreateTable(final String tableName) throws Exception {
    }
    
    @Override
    public Operation handleException(final AlterTableQuery alterQuery, final SQLException sqle, final Connection dstConnection) throws Exception {
        return Operation.STOP_MIGRATION;
    }
    
    protected int cleanupAbandonedChilds(final ForeignKeyDefinition fkDef) throws MetaDataException, QueryConstructionException, SQLException {
        try (final Connection con = DBMigrationUtil.getDestConnection()) {
            return PersistenceUtil.removeOrphanRows(DBMigrationUtil.getDestDBAdapter(), con, fkDef);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(AbstractDBMigrationHandler.class.getName());
    }
}
