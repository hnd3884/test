package com.me.devicemanagement.framework.server.util;

import com.me.devicemanagement.framework.webclient.admin.DBQueryExecutorAPI;
import java.util.function.Function;
import java.util.Map;
import java.util.HashMap;
import com.adventnet.persistence.DataAccess;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.sql.SQLException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.Hashtable;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.WritableDataObject;
import java.util.Properties;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.List;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class DBUtil extends DBUtilWrapper
{
    protected static Logger logger;
    
    public static DataObject setColumnValue(final DataObject configDOFromDB, final String tableName, final String columnName, final Object value) throws DataAccessException {
        final Iterator it = configDOFromDB.getRows(tableName);
        if (it != null) {
            while (it.hasNext()) {
                final Row row = it.next();
                row.set(columnName, value);
                configDOFromDB.updateRow(row);
            }
        }
        return configDOFromDB;
    }
    
    public static DataObject insertRowsInDO(final DataObject configDOFromDB, final DataObject configDO, final List excludeTableList, final List excludeColumnList) throws DataAccessException {
        final List tableList = configDO.getTableNames();
        DBUtil.logger.log(Level.INFO, "tableList in configDO of insertRowsInDO :" + tableList);
        for (int i = 0; i < tableList.size(); ++i) {
            final String tableName = tableList.get(i);
            if (!excludeTableList.contains(tableName)) {
                final Iterator iterator = configDO.getRows(tableName);
                while (iterator.hasNext()) {
                    final Row row = iterator.next();
                    configDOFromDB.addRow(row);
                    DBUtil.logger.log(Level.INFO, "row has been added in configDOFromDB");
                }
            }
        }
        return configDOFromDB;
    }
    
    public static Row getRowForTable(final List rowList, final String tableName) {
        Row resultRow = null;
        for (int rowSize = rowList.size(), s = 0; s < rowSize; ++s) {
            final Row row = rowList.get(s);
            if (row.getTableName().equalsIgnoreCase(tableName)) {
                resultRow = row;
                break;
            }
        }
        return resultRow;
    }
    
    public static List RemoveRowFromList(final List rowList, final String tableNameToRemoveRow) {
        for (int rowSize = rowList.size(), s = 0; s < rowSize; ++s) {
            final Row row = rowList.get(s);
            if (row.getTableName().equalsIgnoreCase(tableNameToRemoveRow)) {
                rowList.remove(s);
                break;
            }
        }
        return rowList;
    }
    
    public static List getListfromDataObject(final DataObject dataObject, final String tableName) throws DataAccessException {
        final Iterator it = dataObject.getRows(tableName);
        final List workGroupList = new ArrayList();
        while (it.hasNext()) {
            final Row row = it.next();
            final List columnsList = row.getColumns();
            final Properties props = new Properties();
            for (int i = 0; i < columnsList.size(); ++i) {
                final String columnName = columnsList.get(i);
                final Object value = row.get(columnName);
                if (value != null) {
                    props.setProperty(columnName, value.toString());
                }
            }
            workGroupList.add(props);
        }
        return workGroupList;
    }
    
    public static DataObject mergeDOs(final List dataObjs) throws Exception {
        final DataObject fullDO = SyMUtil.getPersistence().constructDataObject();
        for (int doSize = dataObjs.size(), s = 0; s < doSize; ++s) {
            final DataObject tempDO = dataObjs.get(s);
            fullDO.merge(tempDO);
        }
        ((WritableDataObject)fullDO).clearOperations();
        return fullDO;
    }
    
    public static int getRecordCount(final String tableName, final String columnName, final Criteria criteria) throws Exception {
        int recordCount = 0;
        final SelectQuery query = new DBUtil().constructSelectQuery(tableName, criteria);
        recordCount = getRecordCount(query, tableName, columnName);
        return recordCount;
    }
    
    public static int getRecordCount(final SelectQuery selectQuery, final String tableName, final String columnName) throws Exception {
        return new DBUtil().getRecordCount(selectQuery, tableName, columnName, false);
    }
    
    public static DataObject getDataObjectFromDB(final String tableName, final String columnName, final Object columnValue) throws Exception {
        final Column col = Column.getColumn(tableName, columnName);
        final Criteria criteria = new Criteria(col, columnValue, 0, false);
        final DataObject resDO = SyMUtil.getPersistence().get(tableName, criteria);
        if (resDO.isEmpty()) {
            return null;
        }
        return resDO;
    }
    
    public static DataObject getDataObjectFromDB(final String tableName, final String[] columnNames) throws Exception {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        for (final String columnName : columnNames) {
            selectQuery.addSelectColumn(Column.getColumn(tableName, columnName));
        }
        final DataObject resDO = SyMUtil.getPersistence().get(selectQuery);
        return resDO;
    }
    
    public static Row getRowFromDB(final String tableName, final String columnName, final Object columnValue) throws Exception {
        final DataObject resDO = getDataObjectFromDB(tableName, columnName, columnValue);
        if (resDO != null) {
            return resDO.getRow(tableName);
        }
        return null;
    }
    
    public static Iterator getRowsFromDB(final String tableName, final String columnName, final Object columnValue) throws Exception {
        final DataObject resDO = getDataObjectFromDB(tableName, columnName, columnValue);
        if (resDO != null) {
            return resDO.getRows(tableName);
        }
        return null;
    }
    
    public static Object getValueFromDB(final String tableName, final String criteriaColumnName, final Object criteriaColumnValue, final String returnColumnName) throws Exception {
        return new DBUtil().getValueFromDB(tableName, criteriaColumnName, criteriaColumnValue, returnColumnName, false);
    }
    
    public static Object getFirstValueFromDBWithOutCriteria(final String tableName, final String returnColumnName) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        query.addSelectColumn(new Column(tableName, "*"));
        final DataObject resDO = SyMUtil.getPersistence().get(query);
        if (resDO.isEmpty()) {
            return null;
        }
        return resDO.getFirstValue(tableName, returnColumnName);
    }
    
    public static Hashtable getMaxOfValues(final String tableName, final String[] columnNames, final Criteria criteria) throws Exception {
        final String sourceMethod = "getMaxOfValues";
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        for (int s = 0; s < columnNames.length; ++s) {
            Column selCol = new Column(tableName, columnNames[s]);
            selCol = selCol.maximum();
            selCol.setColumnAlias("MAX_" + columnNames[s]);
            query.addSelectColumn(selCol);
        }
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        final Hashtable returnHash = new Hashtable();
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                for (int s2 = 0; s2 < columnNames.length; ++s2) {
                    final Object colValue = ds.getValue("MAX_" + columnNames[s2]);
                    if (colValue != null) {
                        returnHash.put(columnNames[s2], colValue);
                    }
                }
            }
            ds.close();
        }
        catch (final Exception ex) {
            throw ex;
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return returnHash;
    }
    
    public static Object getMaxOfValue(final String tableName, final String columnName, final Criteria criteria) throws Exception {
        final String sourceMethod = "getMaxOfValue";
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        Column selCol = new Column(tableName, columnName);
        selCol = selCol.maximum();
        query.addSelectColumn(selCol);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        Object resultVal = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                resultVal = ds.getValue(1);
            }
            ds.close();
        }
        catch (final Exception ex) {
            throw ex;
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return resultVal;
    }
    
    public static Object getMinOfValue(final String tableName, final String columnName, final Criteria criteria) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        Column selCol = new Column(tableName, columnName);
        selCol = selCol.minimum();
        query.addSelectColumn(selCol);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        Object resultVal = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                resultVal = ds.getValue(1);
            }
            ds.close();
        }
        catch (final Exception ex) {
            throw ex;
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex2) {}
        }
        return resultVal;
    }
    
    public static Row constructRowFromProps(final String tableName, final Properties rowInfo) {
        final Row row = new Row(tableName);
        final List columnList = row.getColumns();
        for (int colSize = columnList.size(), i = 0; i < colSize; ++i) {
            final String columnName = columnList.get(i);
            final Object columnValue = ((Hashtable<K, Object>)rowInfo).get(columnName);
            if (columnValue != null) {
                row.set(columnName, columnValue);
            }
        }
        return row;
    }
    
    public static String convertMinutesToDays(final Long mins) {
        String resultStr = "";
        final Long oneDayInMins = new Long(1440L);
        final Long oneHourInMins = new Long(60L);
        final Long days = mins / oneDayInMins;
        final Long remainderHrs = mins % oneDayInMins;
        final Long hrs = remainderHrs / oneHourInMins;
        final Long remainderMins = remainderHrs % oneHourInMins;
        if (days > 0L) {
            final String unit = (days > 1L) ? "days" : "day";
            resultStr = resultStr + days + " " + unit;
        }
        if (hrs > 0L) {
            if (resultStr.trim().length() > 0) {
                resultStr += ", ";
            }
            final String unit = (hrs > 1L) ? "hours" : "hour";
            resultStr = resultStr + hrs + " " + unit;
        }
        if (remainderMins > 0L) {
            if (resultStr.trim().length() > 0) {
                resultStr += ", ";
            }
            final String unit = (remainderMins > 1L) ? "minutes" : "minute";
            resultStr = resultStr + remainderMins + " " + unit;
        }
        return resultStr;
    }
    
    public static int getRecordCount(final SelectQuery selectQuery) throws Exception {
        return new DBUtil().getRecordCount(selectQuery, false);
    }
    
    public static String getActiveDBName() {
        final String currentDB = PersistenceInitializer.getConfigurationValue("DBName");
        return currentDB;
    }
    
    public static String getConditionPrefix() {
        final String currentDB = getActiveDBName();
        String condPrefix = "";
        if (currentDB.equalsIgnoreCase("mssql")) {
            condPrefix = "N";
        }
        return condPrefix;
    }
    
    public static int getRecordActualCount(final String tableName, final String columnName, final Criteria criteria) throws Exception {
        int recordCount = 0;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        recordCount = getRecordActualCount(query, tableName, columnName);
        return recordCount;
    }
    
    public static int getRecordActualCount(final SelectQuery selectQuery, final String tableName, final String columnName) throws Exception {
        int recordCount = 0;
        Column selCol = new Column(tableName, columnName);
        selCol = selCol.count();
        selectQuery.addSelectColumn(selCol);
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)selectQuery, conn);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                if (value != null) {
                    recordCount = (int)value;
                }
            }
            ds.close();
        }
        catch (final QueryConstructionException ex) {
            throw ex;
        }
        catch (final SQLException ex2) {
            throw ex2;
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {}
        }
        return recordCount;
    }
    
    public static List getDistinctColumnValue(final String tableName, final String columnName, final Criteria criteira) throws Exception {
        final List distinctValue = new ArrayList();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        if (criteira != null) {
            query.setCriteria(criteira);
        }
        Column selCol = new Column(tableName, columnName);
        selCol = selCol.distinct();
        query.addSelectColumn(selCol);
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                final Object value = ds.getValue(1);
                if (value != null) {
                    distinctValue.add(value.toString());
                }
            }
            ds.close();
        }
        catch (final QueryConstructionException ex) {
            throw ex;
        }
        catch (final SQLException ex2) {
            throw ex2;
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final SQLException ex3) {
                throw new SyMException(1001, "Exception occured while getting distinct value in DBUtil", ex3);
            }
        }
        return distinctValue;
    }
    
    public static List copyRow(final Row sourceRow, final List descRows, final List excludeColumnList) throws DataAccessException {
        final List columnsList = sourceRow.getColumns();
        for (int i = 0; i < columnsList.size(); ++i) {
            final String columnName = columnsList.get(i);
            if (!excludeColumnList.contains(columnName)) {
                final Object value = sourceRow.get(columnName);
                for (int j = 0; j < descRows.size(); ++j) {
                    final Row targetRow = descRows.remove(j);
                    targetRow.set(columnName, value);
                    descRows.add(j, targetRow);
                }
            }
        }
        return descRows;
    }
    
    public static Row copyRow(final Row sourceRow, final Row descRow, final List excludeColumnList) throws DataAccessException {
        List descRows = new ArrayList();
        descRows.add(descRow);
        descRows = copyRow(sourceRow, descRows, excludeColumnList);
        return descRows.get(0);
    }
    
    public static Object[] getColumnValues(final Iterator itr, final String columnName) {
        final List temp = getColumnValuesAsList(itr, columnName);
        return temp.toArray();
    }
    
    public static List getColumnValuesAsList(final Iterator itr, final String columnName) {
        final ArrayList list = new ArrayList();
        while (itr.hasNext()) {
            final Row row = itr.next();
            final Object key = row.get(columnName);
            list.add(key);
        }
        return list;
    }
    
    public static Object getSumOfValue(final String tableName, final String columnName, final Criteria criteria) throws Exception {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        Column selCol = new Column(tableName, columnName);
        selCol = selCol.summation();
        query.addSelectColumn(selCol);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        Object resultVal = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                resultVal = ds.getValue(1);
            }
            ds.close();
        }
        catch (final Exception ex) {
            throw ex;
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex2) {
                throw ex2;
            }
        }
        return resultVal;
    }
    
    public static int getDOSize(final DataObject dobj, final String tableName) {
        return getDOSize(dobj, tableName, null);
    }
    
    public static int getDOSize(final DataObject dobj, final String tableName, final Criteria criteria) {
        int size = 0;
        try {
            size = ((criteria != null) ? getIteratorSize(dobj.getRows(tableName, criteria)) : (dobj.isEmpty() ? 0 : dobj.size(tableName)));
        }
        catch (final Exception ex) {
            DBUtil.logger.log(Level.WARNING, "Exception while in getDOSize()  ", ex);
        }
        return size;
    }
    
    public static Integer getIteratorSize(final Iterator itr) {
        Integer size = 0;
        while (itr.hasNext()) {
            itr.next();
            ++size;
        }
        return size;
    }
    
    public static Long getUVHValue(final String uvhPattern) throws DataAccessException {
        final Criteria criteria = new Criteria(Column.getColumn("UVHValues", "PATTERN"), (Object)uvhPattern, 0);
        DataObject dobj = null;
        try {
            dobj = DataAccess.get("UVHValues", criteria);
        }
        catch (final Exception re) {
            throw new DataAccessException("Exception occured while fetching dataobject", (Throwable)re);
        }
        final Row accRow = dobj.getFirstRow("UVHValues");
        return (Long)accRow.get("GENVALUES");
    }
    
    public static Long getParentAutoGenID(final String parentTableName, final String parentOldPKColumn, final String parentAutoGenColumn, final Object criteriaColumnValue) {
        Long autoGenID = null;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(parentTableName));
            selectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            selectQuery.setCriteria(new Criteria(Column.getColumn(parentTableName, parentOldPKColumn), criteriaColumnValue, 0));
            final DataObject dataObject = DataAccess.get(selectQuery);
            autoGenID = (Long)dataObject.getFirstRow(parentTableName).get(parentAutoGenColumn);
            return autoGenID;
        }
        catch (final DataAccessException e) {
            DBUtil.logger.log(Level.WARNING, "Exception while getting AUTO_GEN_ID for table : " + parentTableName + ", parent auto generation id : " + parentAutoGenColumn, (Throwable)e);
            e.printStackTrace();
            return null;
        }
    }
    
    public static HashMap executeCountQuery(final SelectQuery query) {
        final HashMap graphData = new HashMap();
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                final Object key = ds.getValue(1);
                final Object value = ds.getValue(2);
                if (key != null && value != null) {
                    graphData.put(key, value);
                }
            }
            ds.close();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex2) {
                ex2.printStackTrace();
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {
                ex3.printStackTrace();
            }
        }
        return graphData;
    }
    
    public static List<Map<String, Object>> getTableAsListOfMap(final DataObject dataObject, final String tableName) {
        try {
            final Iterator<Row> rowIterator = dataObject.getRows(tableName);
            final List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>(dataObject.size(tableName));
            rowIterator.forEachRemaining(row -> {
                final List<String> columnList = row.getColumns();
                final int initialSize = (int)(columnList.size() / 0.75f) + 1;
                final Map<String, Object> rowMap = new HashMap<String, Object>(initialSize);
                columnList.forEach(columnName -> map.computeIfAbsent(columnName, row2::get));
                list.add(rowMap);
                return;
            });
            return recordList;
        }
        catch (final Exception ex) {
            DBUtil.logger.log(Level.SEVERE, "Exception in getting table as list of maps", ex);
            return new ArrayList<Map<String, Object>>(0);
        }
    }
    
    public static List getTableAsListOfMaps(final DataObject dataObject, final String tableName) {
        try {
            final Iterator rowIterator = dataObject.getRows(tableName);
            final List recordList = new ArrayList();
            while (rowIterator.hasNext()) {
                final Row row = rowIterator.next();
                final HashMap rowMap = new HashMap();
                final List columnList = row.getColumns();
                for (int j = 0; j < columnList.size(); ++j) {
                    final String columnName = columnList.get(j);
                    final Object obj = row.get(columnName);
                    if (row.get(columnName) != null) {
                        rowMap.put(columnName, obj);
                    }
                }
                recordList.add(rowMap);
            }
            return recordList;
        }
        catch (final DataAccessException ex) {
            DBUtil.logger.log(Level.SEVERE, "Exception in getting table as list of maps", (Throwable)ex);
        }
        catch (final Exception ex2) {
            DBUtil.logger.log(Level.SEVERE, "Exception in getting table as list of maps", ex2);
        }
        return null;
    }
    
    public static String getInsertIntoSelectQuery(final String tableName, final String selectSQL) {
        final StringBuffer sql = new StringBuffer("");
        if (tableName != null && !tableName.equals("") && selectSQL != null && !selectSQL.equals("")) {
            sql.append("Insert into ");
            sql.append(tableName);
            sql.append(" ");
            sql.append(selectSQL);
        }
        return sql.toString();
    }
    
    public static String getInsertIntoSelectQuery(final String tableName, final String selectSQL, final String[] columnList) {
        final StringBuilder table = new StringBuilder();
        if (columnList != null && columnList.length > 0) {
            final StringBuilder columns = new StringBuilder();
            for (int i = 0; i < columnList.length; ++i) {
                columns.append(columnList[i]);
                if (i != columnList.length - 1) {
                    columns.append(",");
                }
            }
            table.append(tableName);
            table.append(" (");
            table.append(columns.toString());
            table.append(")");
        }
        return getInsertIntoSelectQuery(table.toString(), selectSQL);
    }
    
    public static int getRecordCount(final Connection conn, final String query) throws Exception {
        int recordCount = 0;
        DataSet dataSet = null;
        try {
            dataSet = RelationalAPI.getInstance().executeQuery(query, conn);
            while (dataSet.next()) {
                final Object value = dataSet.getValue("count");
                if (value != null) {
                    recordCount = Integer.valueOf(value.toString());
                }
            }
        }
        catch (final Exception e) {
            DBUtil.logger.log(Level.WARNING, "Caught Exception " + e);
            throw e;
        }
        finally {
            try {
                if (dataSet != null) {
                    dataSet.close();
                }
            }
            catch (final Exception ex) {}
        }
        return recordCount;
    }
    
    public static int getRecordCount(final String query) throws Exception {
        Connection conn = null;
        try {
            conn = getConnection("READ_ONLY");
            return getRecordCount(conn, query);
        }
        catch (final Exception e) {
            DBUtil.logger.log(Level.WARNING, "Caught Exception " + e);
            throw e;
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex) {}
        }
    }
    
    public static Connection getConnection(final String connectionType) throws Exception {
        return ((DBQueryExecutorAPI)Class.forName(ProductClassLoader.getSingleImplProductClass("DM_DB_QUERY_EXECUTOR_IMPL_CLASS")).newInstance()).getConnection(connectionType);
    }
    
    public static HashMap<String, String> getDisplayNameForColumns(final String tableName, final List columnList) {
        final HashMap<String, String> displayNameHashMap = new HashMap<String, String>();
        try {
            final Criteria columnCrit = new Criteria(Column.getColumn("ColumnDetails", "COLUMN_NAME"), (Object)columnList.toArray(), 8, false);
            final Criteria tableCrit = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), (Object)tableName, 0, false);
            final DataObject columnDetailsDO = SyMUtil.getColumnDetailsForColumn(columnCrit.and(tableCrit));
            if (columnDetailsDO != null && !columnDetailsDO.isEmpty()) {
                final Iterator columnDetailsIterator = columnDetailsDO.getRows("ColumnDetails");
                while (columnDetailsIterator.hasNext()) {
                    final Row columnDetailsRow = columnDetailsIterator.next();
                    final String columnName = (String)columnDetailsRow.get("COLUMN_NAME");
                    final String displayName = (String)columnDetailsRow.get("DISPLAY_NAME");
                    displayNameHashMap.put(displayName, columnName);
                }
            }
        }
        catch (final Exception e) {
            DBUtil.logger.log(Level.SEVERE, "Exception while getting DisplayNamesForColumns", e);
        }
        return displayNameHashMap;
    }
    
    public static String getColumnNameForDisplayColumn(final String tableName, final String displayName) {
        final Criteria columnCriteria = new Criteria(Column.getColumn("ColumnDetails", "DISPLAY_NAME"), (Object)displayName, 0, false);
        final Criteria tableCriteria = new Criteria(Column.getColumn("TableDetails", "TABLE_NAME"), (Object)tableName, 0, false);
        final DataObject columnDetailsDO = SyMUtil.getColumnDetailsForColumn(tableCriteria.and(columnCriteria));
        try {
            if (columnDetailsDO != null && !columnDetailsDO.isEmpty()) {
                final Row row = columnDetailsDO.getFirstRow("ColumnDetails");
                return (String)row.get("COLUMN_NAME");
            }
        }
        catch (final Exception ex) {
            DBUtil.logger.log(Level.SEVERE, "Exception while getting ColumnNameForDisplayColumns", ex);
        }
        return null;
    }
    
    static {
        DBUtil.logger = Logger.getLogger(DBUtil.class.getName());
    }
}
