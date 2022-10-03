package com.adventnet.db.adapter.postgres;

import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.ds.query.Function;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.Column;
import java.util.Map;
import com.adventnet.db.adapter.SQLGenerator;
import java.util.logging.Logger;
import com.adventnet.db.adapter.DCSQLGenerator;

public class PostgresJSONDCSQLGenerator implements DCSQLGenerator
{
    private static final Logger LOGGER;
    private SQLGenerator sqlGenerator;
    
    public PostgresJSONDCSQLGenerator() {
        this.sqlGenerator = null;
    }
    
    @Override
    public void getSQLForInsert(final String tableName, final Map<Column, Object> values, final StringBuilder valueBuffer, final StringBuilder buffer) throws QueryConstructionException {
        if (values == null || values.isEmpty()) {
            buffer.append("DYJSONCOL");
            valueBuffer.append("null");
        }
        else if (values.containsValue(QueryConstants.PREPARED_STMT_CONST)) {
            final String columnName = ((Column)values.entrySet().iterator().next().getKey()).getDefinition().getPhysicalColumn();
            buffer.append(columnName);
            valueBuffer.append("?");
        }
        else {
            final String columnName = ((Column)values.entrySet().iterator().next().getKey()).getDefinition().getPhysicalColumn();
            buffer.append(columnName);
            final Map<String, Object> keyVsValue = new HashMap<String, Object>();
            final Set<Column> keys = values.keySet();
            for (final Column dyCol : keys) {
                if (values.get(dyCol) != null) {
                    keyVsValue.put(dyCol.getColumnName(), values.get(dyCol));
                }
            }
            final JSONObject dcValue = new JSONObject((Map)keyVsValue);
            valueBuffer.append("'");
            valueBuffer.append(dcValue.toString());
            valueBuffer.append("'");
        }
    }
    
    @Override
    public String getDCSpecificColumnName(final String columnName) {
        return "DYJSONCOL->>'" + columnName + "'";
    }
    
    @Override
    public String getDCSpecificColumnName(final String tableName, final String columnName) {
        try {
            if (MetaDataUtil.getTableDefinitionByName(tableName).getDynamicColumnNames() != null && MetaDataUtil.getTableDefinitionByName(tableName).getDynamicColumnNames().contains(columnName)) {
                return this.getDCSpecificColumnName(columnName);
            }
            return this.sqlGenerator.getDBSpecificColumnName(columnName);
        }
        catch (final MetaDataException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public String getSQLForCast(final String columnString, final Column column) {
        try {
            return "(" + columnString + ")::" + this.sqlGenerator.getDBDataType(column.getDefinition());
        }
        catch (final QueryConstructionException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    @Override
    public String getDCDataType(final String columnName) {
        return "DCJSON";
    }
    
    @Override
    public int getDCSQLType(final String columnName) {
        return 1111;
    }
    
    @Override
    public Map getModifiedValueForUpdate(final String tableName, final Map<Column, Object> dyValues) throws MetaDataException {
        if (dyValues != null && !dyValues.isEmpty()) {
            String physicalColumn = null;
            final Map<String, Object> keyVsValue = new HashMap<String, Object>();
            final Set<Column> keys = dyValues.keySet();
            for (final Column dyCol : keys) {
                if (physicalColumn == null) {
                    physicalColumn = MetaDataUtil.getTableDefinitionByName(tableName).getColumnDefinitionByName(dyCol.getColumnName()).getPhysicalColumn();
                }
                if (dyValues.get(dyCol) != QueryConstants.PREPARED_STMT_CONST) {
                    keyVsValue.put(dyCol.getColumnName(), dyValues.get(dyCol));
                }
            }
            final JSONObject dcValue = new JSONObject((Map)keyVsValue);
            final Column func = Column.createFunction("JSONB_UPDATE", new Function.ReservedParameter(tableName + "." + physicalColumn), keyVsValue.isEmpty() ? new Function.ReservedParameter("?") : dcValue.toString());
            func.setType(2000);
            final Map values = new HashMap();
            values.put(Column.getColumn(tableName, physicalColumn), func);
            return values;
        }
        return null;
    }
    
    @Override
    public boolean isUniqueKeySupported() {
        return false;
    }
    
    @Override
    public void initSQLGenerator(final SQLGenerator sqlGenerator) {
        if (this.sqlGenerator == null) {
            this.sqlGenerator = sqlGenerator;
        }
        else {
            PostgresJSONDCSQLGenerator.LOGGER.log(Level.WARNING, "SQLGenerator has already been initialized. Existing value :: {0}", this.sqlGenerator);
        }
    }
    
    @Override
    public String getSQLForArchiveTableCheckConstraint(final String invisibleTable, final List<ColumnDefinition> colDefs) {
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(PostgresJSONDCSQLGenerator.class.getName());
    }
}
