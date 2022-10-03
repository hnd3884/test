package com.adventnet.db.adapter;

import com.adventnet.ds.query.BulkLoad;
import java.util.List;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.logging.Level;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.ds.query.AlterTableQuery;
import java.sql.Connection;
import com.adventnet.ds.query.UpdateQuery;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

public class DefaultDCAdapter implements DCAdapter
{
    private static final Logger LOGGER;
    private DBAdapter dbAdapter;
    
    public DefaultDCAdapter() {
        this.dbAdapter = null;
    }
    
    @Override
    public void setValue(final PreparedStatement ps, int columnIndex, final Map<String, Integer> sqlTypes, final Map<String, Object> dyValues) throws SQLException {
        final Set<String> s = dyValues.keySet();
        for (final String columnName : s) {
            this.dbAdapter.setValue(ps, columnIndex++, sqlTypes.get(columnName), dyValues.get(columnName));
        }
    }
    
    @Override
    public UpdateQuery getModifiedUpdateQuery(final UpdateQuery query) {
        return query;
    }
    
    @Override
    public void addDynamicColumn(final Connection connection, final AlterTableQuery alterTableQuery) throws SQLException {
        final AlterTableQuery modifiedAtq = new AlterTableQueryImpl(alterTableQuery.getTableName());
        try {
            final ColumnDefinition colDef = (ColumnDefinition)alterTableQuery.getAlterOperations().get(0).getAlterObject();
            modifiedAtq.addColumn(colDef);
        }
        catch (final QueryConstructionException e) {
            throw new SQLException(e.getMessage(), e);
        }
        DefaultDCAdapter.LOGGER.log(Level.FINE, "Formed AlterQuery Object :: {0}", modifiedAtq);
        this.dbAdapter.alterTable(connection, modifiedAtq);
    }
    
    @Override
    public void deleteDynamicColumn(final Connection connection, final AlterTableQuery alterTableQuery) throws SQLException {
        final AlterTableQuery modifiedAtq = new AlterTableQueryImpl(alterTableQuery.getTableName());
        try {
            modifiedAtq.removeColumn((String)alterTableQuery.getAlterOperations().get(0).getAlterObject());
        }
        catch (final QueryConstructionException e) {
            throw new SQLException(e.getMessage(), e);
        }
        DefaultDCAdapter.LOGGER.log(Level.FINE, "Formed AlterQuery Object :: {0}", modifiedAtq);
        this.dbAdapter.alterTable(connection, modifiedAtq);
    }
    
    @Override
    public void validateVersion(final Connection conn) {
    }
    
    @Override
    public void modifyDynamicColumn(final Connection conn, final AlterTableQuery alterTableQuery) throws SQLException {
        final AlterTableQuery modifiedAtq = new AlterTableQueryImpl(alterTableQuery.getTableName());
        try {
            final ColumnDefinition cd = (ColumnDefinition)alterTableQuery.getAlterOperations().get(0).getAlterObject();
            modifiedAtq.modifyColumn(cd.getColumnName(), cd);
        }
        catch (final QueryConstructionException e) {
            throw new SQLException(e.getMessage(), e);
        }
        DefaultDCAdapter.LOGGER.log(Level.FINE, "Formed AlterQuery Object :: {0}", modifiedAtq);
        this.dbAdapter.alterTable(conn, modifiedAtq);
    }
    
    @Override
    public void renameDynamicColumn(final Connection conn, final AlterTableQuery alterTableQuery) throws SQLException {
        final AlterTableQuery modifiedAtq = new AlterTableQueryImpl(alterTableQuery.getTableName());
        try {
            final String[] names = (String[])alterTableQuery.getAlterOperations().get(0).getAlterObject();
            modifiedAtq.renameColumn(names[0], names[1]);
        }
        catch (final QueryConstructionException e) {
            throw new SQLException(e.getMessage(), e);
        }
        DefaultDCAdapter.LOGGER.log(Level.FINE, "Formed AlterQuery Object :: {0}", modifiedAtq);
        this.dbAdapter.alterTable(conn, modifiedAtq);
    }
    
    @Override
    public String getDataType(final String tableName, final String columnName) throws MetaDataException {
        return MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(columnName).getDataType();
    }
    
    @Override
    public void deleteAllDynamicColumns(final Connection connection, final String tableName) throws SQLException {
        try {
            final List<String> dynamicColumns = MetaDataUtil.getTableDefinitionByName(tableName).getDynamicColumnNames();
            if (dynamicColumns != null) {
                final AlterTableQuery atq = new AlterTableQueryImpl(tableName);
                for (final String columnName : dynamicColumns) {
                    atq.removeColumn(columnName, true);
                }
                DefaultDCAdapter.LOGGER.log(Level.INFO, this.dbAdapter.getSQLGenerator().getSQLForAlterTable(atq));
                this.dbAdapter.alterTable(connection, atq);
            }
        }
        catch (final QueryConstructionException qae) {
            throw new SQLException(qae.getMessage(), qae);
        }
        catch (final MetaDataException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }
    
    @Override
    public void preAlterTable(final Connection connection, final AlterTableQuery alterTableQuery) throws SQLException {
    }
    
    @Override
    public void initDBAdapter(final DBAdapter dbAdapter) {
        if (this.dbAdapter == null) {
            this.dbAdapter = dbAdapter;
        }
        else {
            DefaultDCAdapter.LOGGER.log(Level.WARNING, "DBAdapter has already been initialized. Existing value :: {0}", this.dbAdapter);
        }
    }
    
    @Override
    public Object getModifiedObjectForDynamicColumn(final Object oldValue, final String newColumnName, final Object newValue) {
        return newValue;
    }
    
    @Override
    public void loadDynamicColumnDetails(final BulkInsertObject bio, final BulkLoad bulk, final List<String> dynamicColumns, final int[] recomputedIndex, int index) throws MetaDataException {
        for (final String columnName : dynamicColumns) {
            bio.addColName(columnName);
            bio.addColTypeName(MetaDataUtil.getTableDefinitionByName(bulk.getTableName()).getColumnDefinitionByName(columnName).getDataType());
            bio.addColType(MetaDataUtil.getTableDefinitionByName(bulk.getTableName()).getColumnDefinitionByName(columnName).getSQLType());
            recomputedIndex[bulk.getColumnNames().indexOf(columnName)] = index++;
        }
    }
    
    static {
        LOGGER = Logger.getLogger(DefaultDCAdapter.class.getName());
    }
}
