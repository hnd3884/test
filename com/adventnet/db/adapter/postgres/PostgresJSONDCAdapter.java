package com.adventnet.db.adapter.postgres;

import org.json.JSONException;
import com.adventnet.ds.query.BulkLoad;
import com.adventnet.db.adapter.BulkInsertObject;
import java.sql.Statement;
import java.util.List;
import java.sql.DatabaseMetaData;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.AlterTableQueryImpl;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import com.zoho.mickey.api.DataTypeUtil;
import com.adventnet.ds.query.AlterOperation;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.ds.query.AlterTableQuery;
import java.sql.Connection;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Function;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.Column;
import java.util.HashMap;
import com.adventnet.ds.query.UpdateQuery;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONObject;
import java.util.Map;
import java.sql.PreparedStatement;
import com.adventnet.db.adapter.DBAdapter;
import java.util.logging.Logger;
import com.adventnet.db.adapter.DCAdapter;

public class PostgresJSONDCAdapter implements DCAdapter
{
    private static final Logger LOGGER;
    private static String physicalColumnName;
    private DBAdapter dbAdapter;
    
    public PostgresJSONDCAdapter() {
        this.dbAdapter = null;
    }
    
    @Override
    public void setValue(final PreparedStatement ps, final int columnIndex, final Map<String, Integer> sqlTypes, final Map<String, Object> value) throws SQLException {
        if (value != null) {
            final Map<String, Object> valueMap = value;
            if (ps.toString().toLowerCase().startsWith("insert")) {
                final Set<String> keys = valueMap.keySet();
                final Iterator<String> i = keys.iterator();
                while (i.hasNext()) {
                    final String columnName = i.next();
                    if (valueMap.get(columnName) == null) {
                        i.remove();
                    }
                }
            }
            final JSONObject dcValue = new JSONObject((Map)valueMap);
            ps.setObject(columnIndex, dcValue.toString(), 1111);
        }
        else {
            ps.setNull(columnIndex, 1111);
        }
    }
    
    @Override
    public UpdateQuery getModifiedUpdateQuery(final UpdateQuery query) throws MetaDataException {
        final Map updateColumns = query.getUpdateColumns();
        Iterator<?> colIterator = updateColumns.keySet().iterator();
        final Map<Column, Object> dynamicColumns = new HashMap<Column, Object>();
        final Map<Column, Object> staticColumns = new HashMap<Column, Object>();
        while (colIterator.hasNext()) {
            final Column updateCol = (Column)colIterator.next();
            if (MetaDataUtil.getTableDefinitionByName(query.getTableName()).getColumnDefinitionByName(updateCol.getColumnName()).isDynamic()) {
                dynamicColumns.put(updateCol, updateColumns.get(updateCol));
            }
            else {
                staticColumns.put(updateCol, updateColumns.get(updateCol));
            }
        }
        UpdateQuery uq = null;
        if (!dynamicColumns.isEmpty()) {
            uq = new UpdateQueryImpl(query.getTableName());
            if (!dynamicColumns.isEmpty()) {
                colIterator = staticColumns.keySet().iterator();
                while (colIterator.hasNext()) {
                    final Column col = (Column)colIterator.next();
                    uq.setUpdateColumn(col.getColumnName(), staticColumns.get(col));
                }
                colIterator = dynamicColumns.keySet().iterator();
                final Column col = (Column)colIterator.next();
                final String physicalColumn = MetaDataUtil.getTableDefinitionByName(query.getTableName()).getColumnDefinitionByName(col.getColumnName()).getPhysicalColumn();
                final Column func = Column.createFunction("JSONB_UPDATE", new Function.ReservedParameter(uq.getTableName() + "." + physicalColumn), new Function.ReservedParameter("?"));
                func.setType(2000);
                uq.setUpdateColumn(MetaDataUtil.getTableDefinitionByName(query.getTableName()).getColumnDefinitionByName(col.getColumnName()).getPhysicalColumn(), func);
            }
            else {
                colIterator = staticColumns.entrySet().iterator();
                while (colIterator.hasNext()) {
                    final Column col = (Column)colIterator.next();
                    uq.setUpdateColumn(col.getColumnName(), staticColumns.get(col));
                }
            }
            uq.setCriteria(query.getCriteria());
            for (final Join join : query.getJoins()) {
                uq.addJoin(join);
            }
        }
        else {
            uq = query;
        }
        return uq;
    }
    
    @Override
    public void addDynamicColumn(final Connection connection, final AlterTableQuery alterTableQuery) throws SQLException {
        try {
            final ColumnDefinition colDef = (ColumnDefinition)alterTableQuery.getAlterOperations().get(0).getAlterObject();
            String dataType = colDef.getDataType();
            if (DataTypeUtil.isEDT(dataType)) {
                dataType = DataTypeManager.getDataTypeDefinition(dataType).getBaseType();
            }
            else if (DataTypeUtil.isUDT(dataType) && colDef.isEncryptedColumn()) {
                final Column c = new Column(alterTableQuery.getTableName(), null);
                c.setDataType(dataType);
                final String encryptedString = DataTypeManager.getDataTypeDefinition(dataType).getDTSQLGenerator(this.dbAdapter.getDBType()).getDTSpecificDecryptionString(c, "?");
                if (!encryptedString.equals("?")) {
                    throw new IllegalArgumentException(colDef.getDataType() + " is not allowed for jsondc");
                }
                if (colDef.getDefaultValue() != null) {
                    throw new IllegalArgumentException(colDef.getDataType() + " is not allowed for jsondc with default value");
                }
            }
            if (dataType.equals("SCHAR") || dataType.equals("BLOB") || dataType.equals("SBLOB")) {
                throw new IllegalArgumentException(colDef.getDataType() + " is not allowed for jsondc");
            }
            TableDefinition td = alterTableQuery.getTableDefinition();
            if (td == null) {
                td = MetaDataUtil.getTableDefinitionByName(alterTableQuery.getTableName());
            }
            if (td.getDynamicColumnNames() == null) {
                final ColumnDefinition clonedColDef = (ColumnDefinition)colDef.clone();
                final AlterTableQueryImpl modifiedAtq = new AlterTableQueryImpl(alterTableQuery.getTableName());
                modifiedAtq.addColumn(clonedColDef);
                clonedColDef.setColumnName(PostgresJSONDCAdapter.physicalColumnName);
                clonedColDef.setNullable(true);
                clonedColDef.setDefaultValue(null);
                clonedColDef.setDataType("DCJSON");
                clonedColDef.setTableName(alterTableQuery.getTableName());
                this.dbAdapter.alterTable(connection, modifiedAtq);
            }
            colDef.setPhysicalColumn(PostgresJSONDCAdapter.physicalColumnName);
        }
        catch (final CloneNotSupportedException e) {
            throw new SQLException(e.getMessage(), e);
        }
        catch (final QueryConstructionException e2) {
            throw new SQLException(e2.getMessage(), e2);
        }
        catch (final MetaDataException mde) {
            throw new SQLException(mde.getMessage(), mde);
        }
    }
    
    @Override
    public void deleteDynamicColumn(final Connection connection, final AlterTableQuery alterTableQuery) throws SQLException {
        try {
            TableDefinition td = alterTableQuery.getTableDefinition();
            if (td == null) {
                td = MetaDataUtil.getTableDefinitionByName(alterTableQuery.getTableName());
            }
            if (td.getDynamicColumnNames().size() == 1) {
                final AlterTableQueryImpl modifiedAtq = new AlterTableQueryImpl(alterTableQuery.getTableName());
                modifiedAtq.removeColumn(PostgresJSONDCAdapter.physicalColumnName);
                this.dbAdapter.alterTable(connection, modifiedAtq);
            }
        }
        catch (final QueryConstructionException e) {
            throw new SQLException(e.getMessage(), e);
        }
        catch (final MetaDataException mde) {
            throw new SQLException(mde.getMessage(), mde);
        }
    }
    
    @Override
    public void validateVersion(final Connection conn) {
        try {
            final DatabaseMetaData dbm = conn.getMetaData();
            final String version = dbm.getDatabaseProductVersion();
            PostgresJSONDCAdapter.LOGGER.log(Level.INFO, "Postgres version : {0}", version);
            final Float dbVer = new Float(dbm.getDatabaseMajorVersion() + "." + dbm.getDatabaseMinorVersion());
            if (dbVer < new Float("9.5")) {
                throw new UnsupportedOperationException("This version of Postgres :: " + version + " is not supported for dynamic columns of type jsondc");
            }
            PostgresJSONDCAdapter.LOGGER.log(Level.INFO, "This version of Postgres :: {0} is supported for dynamic columns of type jsondc", version);
        }
        catch (final SQLException ex) {
            PostgresJSONDCAdapter.LOGGER.log(Level.INFO, "Unable to validate version for dynamic columns of type jsondc");
            ex.printStackTrace();
        }
    }
    
    @Override
    public void modifyDynamicColumn(final Connection conn, final AlterTableQuery alterTableQuery) throws SQLException {
    }
    
    @Override
    public void renameDynamicColumn(final Connection conn, final AlterTableQuery alterTableQuery) throws SQLException {
        throw new UnsupportedOperationException("Renaming JSON type dynamic column is not supported yet.");
    }
    
    @Override
    public String getDataType(final String tableName, final String columnName) {
        return "DCJSON";
    }
    
    @Override
    public void deleteAllDynamicColumns(final Connection connection, final String tableName) throws SQLException {
        try {
            final List<String> dynamicColumns = MetaDataUtil.getTableDefinitionByName(tableName).getDynamicColumnNames();
            if (dynamicColumns != null) {
                final AlterTableQueryImpl modifiedAtq = new AlterTableQueryImpl(tableName);
                modifiedAtq.removeColumn(PostgresJSONDCAdapter.physicalColumnName);
                PostgresJSONDCAdapter.LOGGER.log(Level.INFO, this.dbAdapter.getSQLGenerator().getSQLForAlterTable(modifiedAtq));
                this.dbAdapter.alterTable(connection, modifiedAtq);
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
        try {
            for (final AlterOperation ao : alterTableQuery.getAlterOperations()) {
                if (ao.getOperationType() == 20) {
                    final String columnName = (String)ao.getAlterObject();
                    final ColumnDefinition cd = MetaDataUtil.getTableDefinitionByName(ao.getTableName()).getColumnDefinitionByName(columnName);
                    if (cd.getPhysicalColumn() == null) {
                        continue;
                    }
                    final UpdateQuery uq = new UpdateQueryImpl(cd.getTableName());
                    uq.setUpdateColumn(cd.getColumnName(), null);
                    try (final Statement s = connection.createStatement()) {
                        s.execute(this.dbAdapter.getSQLGenerator().getSQLForUpdate(uq.getTableList(), uq.getUpdateColumns(), uq.getCriteria(), uq.getJoins()));
                    }
                }
            }
        }
        catch (final MetaDataException | QueryConstructionException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }
    
    @Override
    public void initDBAdapter(final DBAdapter dbAdapter) {
        if (this.dbAdapter == null) {
            this.dbAdapter = dbAdapter;
        }
        else {
            PostgresJSONDCAdapter.LOGGER.log(Level.WARNING, "DBAdapter has already been initialized. Existing value :: {0}", this.dbAdapter);
        }
    }
    
    @Override
    public void loadDynamicColumnDetails(final BulkInsertObject bio, final BulkLoad bulk, final List<String> dynamicColumns, final int[] recomputedIndex, final int startingIndex) throws MetaDataException {
        bio.addColName(PostgresJSONDCAdapter.physicalColumnName);
        bio.addColTypeName("DCJSON");
        bio.addColType(1111);
        for (final String dynamicColumn : dynamicColumns) {
            recomputedIndex[bulk.getColumnNames().indexOf(dynamicColumn)] = startingIndex;
        }
    }
    
    @Override
    public Object getModifiedObjectForDynamicColumn(final Object oldValue, final String newColumnName, final Object newValue) throws JSONException {
        if (oldValue == null) {
            final JSONObject dcValue = new JSONObject();
            dcValue.put(newColumnName, newValue);
            return dcValue;
        }
        if (oldValue instanceof JSONObject) {
            final JSONObject dcValue = (JSONObject)oldValue;
            dcValue.put(newColumnName, newValue);
            return dcValue;
        }
        if (oldValue instanceof Map) {
            final JSONObject dcValue = new JSONObject((Map)oldValue);
            dcValue.put(newColumnName, newValue);
            return dcValue;
        }
        throw new IllegalArgumentException("Unknown old value format");
    }
    
    static {
        LOGGER = Logger.getLogger(PostgresJSONDCAdapter.class.getName());
        PostgresJSONDCAdapter.physicalColumnName = "DYJSONCOL";
    }
}
