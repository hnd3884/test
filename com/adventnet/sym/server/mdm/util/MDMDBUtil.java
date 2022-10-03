package com.adventnet.sym.server.mdm.util;

import java.util.HashSet;
import com.me.idps.core.util.IdpsUtil;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.util.DBUtil;

public class MDMDBUtil extends DBUtil
{
    public static final String DISTINCT_COUNT_COLUMN = "DISTINCT_COUNT";
    public static final String ALL = "*";
    public static Logger logger;
    
    public static JSONObject rowToJSON(final Row row) {
        return rowToJSON(row, new String[0]);
    }
    
    public static JSONObject rowToJSON(final Row row, final String[] omittedColumns) {
        final JSONObject jsonObject = new JSONObject();
        final List<String> omittedColumnList = Arrays.asList(omittedColumns);
        final List<String> columns = row.getColumns();
        for (final String column : columns) {
            final Object value = row.get(column);
            if (value != null && !omittedColumnList.contains(column)) {
                jsonObject.put(column, value);
            }
        }
        return (jsonObject.length() == 0) ? null : jsonObject;
    }
    
    public static JSONObject getDistinctCount(final SelectQuery selectQuery, final String baseTable, final String countColumn) throws Exception {
        final Column groupByColumn = Column.getColumn(baseTable, countColumn);
        final List groupByList = new ArrayList();
        groupByList.add(groupByColumn);
        final GroupByClause groupByClause = new GroupByClause(groupByList);
        selectQuery.setGroupByClause(groupByClause);
        selectQuery.addSelectColumn(groupByColumn);
        final DerivedTable derivedTable = new DerivedTable("CountTable", (Query)selectQuery);
        final SelectQuery countSelect = (SelectQuery)new SelectQueryImpl((Table)derivedTable);
        final Column countCol = new Column("CountTable", countColumn).count();
        countCol.setColumnAlias("DISTINCT_COUNT");
        countSelect.addSelectColumn(countCol);
        final JSONArray jsonArray = MDMUtil.executeSelectQuery(countSelect);
        return (jsonArray.size() == 0) ? new JSONObject() : new JSONObject(jsonArray.get(0).toString());
    }
    
    public static JSONObject getDistinctCount(final String baseTable, final String countColumn) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table(baseTable));
        return getDistinctCount(selectQuery, baseTable, countColumn);
    }
    
    private static Map<String, Object> arrayToMap(final Object[][] array) {
        final Map<String, Object> rowMap = new HashMap<String, Object>();
        for (int i = 0; i < array.length; ++i) {
            rowMap.put((String)array[i][0], array[i][1]);
        }
        return rowMap;
    }
    
    private static void addItemsToRow(final Row row, final Object[][] rowMapArray) throws Exception {
        final Map<String, Object> rowMap = arrayToMap(rowMapArray);
        final List<String> columns = row.getColumns();
        for (final String column : columns) {
            if (rowMap.containsKey(column)) {
                final Object value = rowMap.get(column);
                if (value == null) {
                    continue;
                }
                row.set(column, rowMap.get(column));
            }
        }
    }
    
    public static Row createRow(final String tableName, final Object[][] rowMapArray) throws Exception {
        final Row row = new Row(tableName);
        addItemsToRow(row, rowMapArray);
        return row;
    }
    
    public static void createRowAndPersist(final String tableName, final Object[][] rowMapArray) throws Exception {
        try {
            MDMDBUtil.logger.log(Level.FINE, "Going to add row to table {0}", new Object[] { tableName });
            final Row row = createRow(tableName, rowMapArray);
            final DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
            dataObject.addRow(row);
            MDMUtil.getPersistence().add(dataObject);
            MDMDBUtil.logger.log(Level.INFO, "Add operation to table {0} success", new Object[] { tableName });
        }
        catch (final Exception e) {
            MDMDBUtil.logger.log(Level.SEVERE, e, () -> "Add operation to table " + s + " failed");
            throw e;
        }
    }
    
    public static Row updateRow(final DataObject dataObject, final String tableName, final Object[][] rowMapArray) throws Exception {
        Row row = null;
        try {
            MDMDBUtil.logger.log(Level.FINE, "Going to perform add/Update operation on table {0}", new Object[] { tableName });
            row = new Row(tableName);
            final Map<String, Object> rowMap = arrayToMap(rowMapArray);
            Boolean isModify = dataObject.containsTable(tableName);
            final Iterator<String> pkColumns = row.getPKColumns().iterator();
            Criteria criteria = null;
            row = null;
            if (isModify) {
                while (pkColumns.hasNext()) {
                    final String column = pkColumns.next();
                    if (!rowMap.containsKey(column)) {
                        isModify = Boolean.FALSE;
                        break;
                    }
                    final Object pkValue = rowMap.get(column);
                    if (pkValue == null) {
                        isModify = Boolean.FALSE;
                        break;
                    }
                    if (criteria == null) {
                        criteria = new Criteria(Column.getColumn(tableName, column), pkValue, 0);
                    }
                    else {
                        criteria = criteria.and(new Criteria(Column.getColumn(tableName, column), pkValue, 0));
                    }
                }
            }
            if (criteria != null && isModify) {
                row = dataObject.getRow(tableName, criteria);
                isModify = (row != null);
            }
            row = (isModify ? row : new Row(tableName));
            addItemsToRow(row, rowMapArray);
            if (isModify) {
                dataObject.updateRow(row);
            }
            else {
                dataObject.addRow(row);
            }
        }
        catch (final Exception e) {
            MDMDBUtil.logger.log(Level.SEVERE, e, () -> "Failed to perform add/Update operation in " + s);
            throw e;
        }
        return row;
    }
    
    public static Row updateFirstRow(final DataObject dataObject, final String tableName, final Object[][] rowMapArray) throws Exception {
        final Boolean isModify = dataObject.containsTable(tableName);
        final Row row = isModify ? dataObject.getFirstRow(tableName) : new Row(tableName);
        addItemsToRow(row, rowMapArray);
        if (isModify) {
            dataObject.updateRow(row);
        }
        else {
            dataObject.addRow(row);
        }
        return row;
    }
    
    public static Row updateRow(Row row, final String tableName, final Object[][] rowMapArray) throws Exception {
        if (row == null) {
            row = createRow(tableName, rowMapArray);
        }
        else {
            addItemsToRow(row, rowMapArray);
        }
        return row;
    }
    
    public static void updateRowAndPersist(final DataObject dataObject, final String tableName, final Object[][] rowMapArray) throws Exception {
        final Boolean isModify = !dataObject.isEmpty();
        updateRow(dataObject, tableName, rowMapArray);
        if (isModify) {
            MDMUtil.getPersistence().update(dataObject);
        }
        else {
            MDMUtil.getPersistence().add(dataObject);
        }
    }
    
    public static void addOrUpdateAndPersist(final String tableName, final Object[][] rowMapArray) throws Exception {
        final DataObject dataObject = getDataObjectUsingPK(tableName, rowMapArray);
        updateRowAndPersist(dataObject, tableName, rowMapArray);
    }
    
    private static Criteria getCriteriaUsingPK(final String tableName, final Object[][] rowMapArray) throws Exception {
        Criteria criteria = null;
        final Map<String, Object> rowMap = arrayToMap(rowMapArray);
        final Row row = new Row(tableName);
        final List columns = row.getPKColumns();
        for (final String column : columns) {
            if (rowMap.containsKey(column)) {
                final Object value = rowMap.get(column);
                final Criteria nextCriteria = new Criteria(Column.getColumn(tableName, column), value, 0);
                criteria = ((criteria == null) ? nextCriteria : criteria.and(nextCriteria));
            }
        }
        return criteria;
    }
    
    private static Criteria getCriteria(final String tableName, final Object[][] rowMapArray) throws Exception {
        Criteria criteria = null;
        final Map<String, Object> rowMap = arrayToMap(rowMapArray);
        final Row row = new Row(tableName);
        final List columns = row.getColumns();
        for (final String column : columns) {
            if (rowMap.containsKey(column)) {
                final Object value = rowMap.get(column);
                Criteria nextCriteria;
                if (value != null && value.getClass().isArray()) {
                    nextCriteria = new Criteria(Column.getColumn(tableName, column), value, 8);
                }
                else {
                    nextCriteria = new Criteria(Column.getColumn(tableName, column), value, 0);
                }
                criteria = ((criteria == null) ? nextCriteria : criteria.and(nextCriteria));
            }
        }
        return criteria;
    }
    
    public static Row getFirstRow(final DataObject dataObject, final String tableName, final Object[][] rowMapArray) throws Exception {
        return dataObject.containsTable(tableName) ? dataObject.getRow(tableName, getCriteria(tableName, rowMapArray)) : null;
    }
    
    public static Iterator<Row> getRows(final DataObject dataObject, final String tableName, final Object[][] rowMapArray) throws Exception {
        return dataObject.getRows(tableName, getCriteria(tableName, rowMapArray));
    }
    
    public static Row getFirstRow(final String tableName, final Object[][] rowMapArray) throws Exception {
        final DataObject dataObject = getDataObject(tableName, rowMapArray);
        return dataObject.containsTable(tableName) ? dataObject.getFirstRow(tableName) : null;
    }
    
    public static DataObject getDataObject(final String tableName, final Object[][] rowMapArray) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        query.addSelectColumn(Column.getColumn(tableName, "*"));
        query.setCriteria(getCriteria(tableName, rowMapArray));
        return MDMUtil.getPersistence().get(query);
    }
    
    public static DataObject getDataObjectUsingPK(final String tableName, final Object[][] rowMapArray) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        query.addSelectColumn(Column.getColumn(tableName, "*"));
        query.setCriteria(getCriteriaUsingPK(tableName, rowMapArray));
        return MDMUtil.getPersistence().get(query);
    }
    
    public static Iterator<Row> getRows(final String tableName, final Object[][] rowMapArray) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        query.addSelectColumn(Column.getColumn(tableName, "*"));
        query.setCriteria(getCriteria(tableName, rowMapArray));
        final DataObject dataObject = MDMUtil.getPersistence().get(query);
        if (!dataObject.containsTable(tableName)) {
            return null;
        }
        return MDMUtil.getPersistence().get(query).getRows(tableName);
    }
    
    public static int getCount(final SelectQuery selectQuery, final String tableNameAlias, final String columnNameAlias) throws Exception {
        final SelectQuery countQuery = (SelectQuery)selectQuery.clone();
        final ArrayList<Column> columnList = (ArrayList<Column>)countQuery.getSelectColumns();
        for (final Column column : columnList) {
            countQuery.removeSelectColumn(column);
        }
        final ArrayList<SortColumn> sortColumnList = (ArrayList<SortColumn>)countQuery.getSortColumns();
        for (final SortColumn sortColumn : sortColumnList) {
            countQuery.removeSortColumn(sortColumn);
        }
        Column countColumn = Column.getColumn(tableNameAlias, columnNameAlias);
        countColumn = countColumn.distinct();
        countColumn = countColumn.count();
        countQuery.addSelectColumn(countColumn);
        return DBUtil.getRecordCount(countQuery);
    }
    
    public static void deleteRows(final DataObject dataObject, final String tableName, final Object[][] rowMapArray) throws Exception {
        dataObject.deleteRows(tableName, getCriteria(tableName, rowMapArray));
    }
    
    public static void deleteRows(final String tableName, final Object[][] rowMapArray) throws Exception {
        final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl(tableName);
        deleteQuery.setCriteria(getCriteria(tableName, rowMapArray));
        MDMUtil.getPersistence().delete(deleteQuery);
    }
    
    public static Row cloneRow(final Row row, final String[] notTobeClonedColumns) {
        List<String> notTobeCloned = Arrays.asList(notTobeClonedColumns);
        final String tableName = row.getTableName();
        if (notTobeCloned == null) {
            notTobeCloned = new ArrayList<String>();
        }
        final List columns = row.getColumns();
        final Row clonedRow = new Row(tableName);
        for (final String column : columns) {
            if (!notTobeCloned.contains(column)) {
                clonedRow.set(column, row.get(column));
            }
        }
        return clonedRow;
    }
    
    public static org.json.JSONArray getRowsAsJSONArray(final DataObject dataObject, final String tableName) throws Exception {
        final Iterator<Row> iterator = dataObject.getRows(tableName);
        final org.json.JSONArray array = new org.json.JSONArray();
        while (iterator.hasNext()) {
            array.put((Object)rowToJSON(iterator.next()));
        }
        return array;
    }
    
    public static Criteria andCriteria(final Criteria baseCriteria, final Criteria newCriteria) {
        return IdpsUtil.andCriteria(baseCriteria, newCriteria);
    }
    
    public static Criteria orCriteria(Criteria baseCriteria, final Criteria newCriteria) {
        baseCriteria = ((baseCriteria == null) ? newCriteria : baseCriteria.or(newCriteria));
        return baseCriteria;
    }
    
    public static HashSet getColumnValuesAsSet(final Iterator itr, final String columnName) {
        final HashSet set = new HashSet();
        while (itr.hasNext()) {
            final Row row = itr.next();
            final Object key = row.get(columnName);
            set.add(key);
        }
        return set;
    }
    
    static {
        MDMDBUtil.logger = Logger.getLogger("MDMConfigLogger");
    }
}
