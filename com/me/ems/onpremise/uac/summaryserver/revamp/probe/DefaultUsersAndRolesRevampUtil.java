package com.me.ems.onpremise.uac.summaryserver.revamp.probe;

import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.ds.query.Join;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.ForeignKeyColumnDefinition;
import com.adventnet.db.persistence.metadata.ForeignKeyDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.List;
import java.util.Map;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class DefaultUsersAndRolesRevampUtil
{
    private static final Logger REVAMP_LOGGER;
    
    private DefaultUsersAndRolesRevampUtil() {
    }
    
    public static void updateTable(final String tableName, final String conditionColumn, final Object conditionValue, final String replaceColumn, final Object newValue) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        query.addSelectColumn(new Column(tableName, "*"));
        query.setCriteria(new Criteria(Column.getColumn(tableName, conditionColumn), conditionValue, 0));
        final DataObject dataObject = DataAccess.get(query);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows(tableName);
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                row.set(replaceColumn, newValue);
                dataObject.updateRow(row);
            }
            if (!dataObject.isEmpty()) {
                DataAccess.update(dataObject);
            }
        }
    }
    
    public static void updateChildTables(final Map<String, List<String>> childTables, final Map<Long, Long> newIds) throws Exception {
        DefaultUsersAndRolesRevampUtil.REVAMP_LOGGER.log(Level.INFO, "TABLE_NAME - COLUMN_NAMES");
        for (final Map.Entry<String, List<String>> childTable : childTables.entrySet()) {
            final String childTableName = childTable.getKey();
            final List<String> childTableColumns = childTable.getValue();
            final String logData = childTableName + "-" + childTableColumns.toString();
            DefaultUsersAndRolesRevampUtil.REVAMP_LOGGER.log(Level.INFO, logData);
            if (childTableColumns.size() > 1) {
                updateMultipleColumns(childTableName, childTableColumns, newIds);
                for (final String columnName : childTableColumns) {
                    final Map<String, List<String>> secondChildTables = getChildTableColumns(childTableName, columnName, null);
                    if (!secondChildTables.isEmpty()) {
                        updateChildTables(secondChildTables, newIds);
                    }
                }
            }
            else {
                updateColumn(childTableName, childTableColumns.toArray()[0].toString(), newIds);
                final Map<String, List<String>> secondChildTables2 = getChildTableColumns(childTableName, childTableColumns.toArray()[0].toString(), null);
                if (secondChildTables2.isEmpty()) {
                    continue;
                }
                updateChildTables(secondChildTables2, newIds);
            }
        }
        DefaultUsersAndRolesRevampUtil.REVAMP_LOGGER.log(Level.INFO, "\n");
    }
    
    public static void updateColumn(final String childTableName, final String childTableColumn, final Map<Long, Long> newIds) throws DataAccessException {
        final List<Row> rowList = new ArrayList<Row>();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(childTableName));
        query.addSelectColumn(new Column(childTableName, "*"));
        final Criteria criteria = new Criteria(Column.getColumn(childTableName, childTableColumn), (Object)newIds.keySet().toArray(), 8);
        query.setCriteria(criteria);
        final DataObject oldDataObject = DataAccess.get(query);
        final Iterator<Row> iterator = oldDataObject.getRows(childTableName);
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            final Long currentId = (Long)row.get(childTableColumn);
            if (newIds.containsKey(currentId)) {
                row.set(childTableColumn, (Object)newIds.get(currentId));
                rowList.add(row);
            }
        }
        for (final Row row2 : rowList) {
            oldDataObject.updateRow(row2);
        }
        DataAccess.update(oldDataObject);
    }
    
    public static void updateMultipleColumns(final String childTableName, final List<String> columnNames, final Map<Long, Long> newIds) throws DataAccessException {
        final List<Row> rowList = new ArrayList<Row>();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(childTableName));
        query.addSelectColumn(new Column(childTableName, "*"));
        Criteria criteria = null;
        for (final String columnName : columnNames) {
            if (criteria == null) {
                criteria = new Criteria(Column.getColumn(childTableName, columnName), (Object)newIds.keySet().toArray(), 8);
            }
            else {
                criteria = criteria.and(new Criteria(Column.getColumn(childTableName, columnName), (Object)newIds.keySet().toArray(), 8));
            }
        }
        query.setCriteria(criteria);
        final DataObject oldDataObject = DataAccess.get(query);
        final Iterator<Row> iterator = oldDataObject.getRows(childTableName);
        while (iterator.hasNext()) {
            final Row row = iterator.next();
            for (final String columnName2 : columnNames) {
                final Long currentId = (Long)row.get(columnName2);
                row.set(columnName2, (Object)newIds.get(currentId));
            }
            rowList.add(row);
        }
        for (final Row row2 : rowList) {
            oldDataObject.updateRow(row2);
        }
        DataAccess.update(oldDataObject);
    }
    
    public static Map<String, List<String>> getChildTableColumns(final String tableName, final String columnName, final List<String> excludeTableList) throws MetaDataException, DataAccessException {
        final Map<String, List<String>> childTableData = new HashMap<String, List<String>>();
        final List<ForeignKeyDefinition> foreignKeyDefinitions = MetaDataUtil.getReferringForeignKeyDefinitions(tableName, columnName);
        for (final ForeignKeyDefinition foreignKeyDefinition : foreignKeyDefinitions) {
            final String childTableName = foreignKeyDefinition.getSlaveTableName();
            if (getRowCount(childTableName) > 0 && (excludeTableList == null || !excludeTableList.contains(childTableName))) {
                for (final Object fkColumnDefinition : foreignKeyDefinition.getForeignKeyColumns()) {
                    final ForeignKeyColumnDefinition foreignKeyColumnDefinition = (ForeignKeyColumnDefinition)fkColumnDefinition;
                    final String childTableColumnName = foreignKeyColumnDefinition.getLocalColumnDefinition().getColumnName();
                    List<String> referredChildColumns;
                    if (childTableData.containsKey(childTableName)) {
                        referredChildColumns = childTableData.get(childTableName);
                    }
                    else {
                        referredChildColumns = new ArrayList<String>();
                    }
                    referredChildColumns.add(childTableColumnName);
                    childTableData.put(childTableName, referredChildColumns);
                }
            }
        }
        getIndirectChildTableColumns(childTableData, tableName, columnName, excludeTableList);
        if (childTableData.size() > 0) {
            final String tableData = tableName + ":" + columnName + "-DEPENDENCY COUNT=" + childTableData.size() + "\n\n";
            DefaultUsersAndRolesRevampUtil.REVAMP_LOGGER.log(Level.INFO, tableData);
        }
        return childTableData;
    }
    
    public static Map<String, List<String>> getIndirectChildTableColumns(final Map<String, List<String>> childTableData, final String parentTableName, final String parentColumnName, final List<String> excludeTableList) throws DataAccessException {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ParentDependencyInfo"));
        selectQuery.addJoin(new Join("ParentDependencyInfo", "ChildDependencyInfo", new String[] { "ID" }, new String[] { "PARENT_ID" }, 1));
        final Criteria tableNameCriteria = new Criteria(Column.getColumn("ParentDependencyInfo", "TABLE_NAME"), (Object)parentTableName, 0);
        final Criteria columnNameCriteria = new Criteria(Column.getColumn("ParentDependencyInfo", "COLUMN_NAME"), (Object)parentColumnName, 0);
        selectQuery.setCriteria(tableNameCriteria.and(columnNameCriteria));
        selectQuery.addSelectColumn(new Column((String)null, "*"));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("ChildDependencyInfo");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String childTableName = (String)row.get("CHILD_TABLE_NAME");
                if (getRowCount(childTableName) > 0 && (excludeTableList == null || !excludeTableList.contains(childTableName))) {
                    final String childTableColumnName = (String)row.get("CHILD_COLUMN_NAME");
                    List<String> referredChildColumns;
                    if (childTableData.containsKey(childTableName)) {
                        referredChildColumns = childTableData.get(childTableName);
                    }
                    else {
                        referredChildColumns = new ArrayList<String>();
                    }
                    referredChildColumns.add(childTableColumnName);
                    childTableData.put(childTableName, referredChildColumns);
                }
            }
        }
        return childTableData;
    }
    
    public static int getRowCount(final String childTableName) throws DataAccessException {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(childTableName));
        query.addSelectColumn(new Column(childTableName, "*"));
        final DataObject dataObject = DataAccess.get(query);
        return dataObject.size(childTableName);
    }
    
    public static Map<String, Map<Object, Object>> getUserIdMap(final Properties userProperties) throws DataAccessException {
        final Map<String, Map<Object, Object>> patternMap = new HashMap<String, Map<Object, Object>>();
        final Map<Object, Object> userStatus = new HashMap<Object, Object>();
        final String[] tableNames = { "AaaUser", "AaaLogin" };
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UVHValues"));
        selectQuery.addSelectColumn(new Column("UVHValues", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("UVHValues", "TABLE_NAME"), (Object)tableNames, 8));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("UVHValues");
            while (iterator.hasNext()) {
                final Map<Object, Object> idMap = new HashMap<Object, Object>();
                final Row row = iterator.next();
                final String pattern = (row.get("PATTERN") + "").replace(":", "-");
                final Object psPatternId = row.get("GENVALUES");
                if (userProperties.containsKey(pattern)) {
                    final Object ssPatternId = ((Hashtable<K, Object>)userProperties).get(pattern);
                    idMap.put("oldId", psPatternId);
                    idMap.put("newId", ssPatternId);
                    patternMap.put(pattern, idMap);
                    if (!row.get("TABLE_NAME").equals("AaaUser")) {
                        continue;
                    }
                    userStatus.put(ssPatternId, ((Hashtable<K, Object>)userProperties).get(ssPatternId));
                }
            }
        }
        patternMap.put("userStatus", userStatus);
        return patternMap;
    }
    
    public static Map<Long, Long> getRoleIdMap(final Properties roleProperties) throws DataAccessException {
        final Map<Long, Long> roleIdMap = new HashMap<Long, Long>();
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("UVHValues"));
        selectQuery.addSelectColumn(new Column("UVHValues", "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("UVHValues", "TABLE_NAME"), (Object)"UMRole", 0));
        final DataObject dataObject = DataAccess.get(selectQuery);
        if (dataObject != null && !dataObject.isEmpty()) {
            final Iterator<Row> iterator = dataObject.getRows("UVHValues");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String pattern = (row.get("PATTERN") + "").replace(":", "-");
                final Long psId = (Long)row.get("GENVALUES");
                if (roleProperties.containsKey(pattern)) {
                    final Long ssId = new Long(((Hashtable<K, String>)roleProperties).get(pattern));
                    roleIdMap.put(psId, ssId);
                }
            }
        }
        return roleIdMap;
    }
    
    static {
        REVAMP_LOGGER = Logger.getLogger("ProbeServerRevampLogger");
    }
}
