package com.me.idps.core.util;

import java.util.HashMap;
import org.json.JSONException;
import java.util.Set;
import java.util.Properties;
import java.util.Iterator;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.CaseExpression;
import java.util.ArrayList;
import com.adventnet.ds.query.GroupByClause;
import java.sql.SQLException;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DataSet;
import java.util.logging.Level;
import com.adventnet.db.api.RelationalAPI;
import java.util.List;
import java.sql.Connection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.idps.core.sync.product.DirectoryProductOpsHandler;
import com.me.idps.core.sync.events.IdpEventConstants;
import com.me.idps.core.sync.product.DirProdImplRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.JsonElement;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.GsonBuilder;
import com.me.devicemanagement.framework.server.util.SyMUtil;

public class IdpsUtil extends SyMUtil
{
    private static IdpsUtil idpsUtil;
    
    public static IdpsUtil getInstance() {
        if (IdpsUtil.idpsUtil == null) {
            IdpsUtil.idpsUtil = new IdpsUtil();
        }
        return IdpsUtil.idpsUtil;
    }
    
    public static String getPrettyJSON(final String uglyJGSONStr) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final JsonParser jp = new JsonParser();
        final JsonElement je = jp.parse(uglyJGSONStr);
        return gson.toJson(je);
    }
    
    public static String getPrettyJSON(final JSONObject uglyJGSON) {
        return getPrettyJSON(uglyJGSON.toString());
    }
    
    public static String getPrettyJSON(final JSONArray uglyJGSONAr) {
        return getPrettyJSON(uglyJGSONAr.toString());
    }
    
    public static String getPrettyJSON(final org.json.simple.JSONObject uglyJGSON) {
        return getPrettyJSON(uglyJGSON.toJSONString());
    }
    
    public static String getPrettyJSON(final org.json.simple.JSONArray uglyJGSONAr) {
        return getPrettyJSON(uglyJGSONAr.toJSONString());
    }
    
    public static boolean isFeatureAvailable(final String featureKey) throws Exception {
        final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
        dirProdImplRequest.eventType = IdpEventConstants.FEATURE_PARAMS;
        dirProdImplRequest.args = new Object[] { "CHECK_FEATURE_AVAILABILITY", featureKey };
        return (boolean)DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
    }
    
    public static void updateFeatureAvailability(final String featureKey, final boolean value) throws Exception {
        final DirProdImplRequest dirProdImplRequest = new DirProdImplRequest();
        dirProdImplRequest.eventType = IdpEventConstants.FEATURE_PARAMS;
        dirProdImplRequest.args = new Object[] { "UPDATE_FEATURE_AVAILIBILITY", featureKey, value };
        DirectoryProductOpsHandler.getInstance().invokeProductImpl(dirProdImplRequest);
    }
    
    public Long getSchedulerCustomizedTask(final Long customerId, final int operationTaskType) throws Exception {
        Long taskId = -1L;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomerInfo"));
        selectQuery.addJoin(new Join("CustomerInfo", "TaskToCustomerRel", new String[] { "CUSTOMER_ID" }, new String[] { "CUSTOMER_ID" }, 2));
        selectQuery.addJoin(new Join("TaskToCustomerRel", "TaskDetails", new String[] { "TASK_ID" }, new String[] { "TASK_ID" }, 2));
        final Criteria taskCri = new Criteria(new Column("TaskDetails", "TYPE"), (Object)operationTaskType, 0);
        final Criteria custCri = new Criteria(new Column("CustomerInfo", "CUSTOMER_ID"), (Object)customerId, 0);
        selectQuery.addSelectColumn(Column.getColumn("TaskToCustomerRel", "CUSTOMER_ID"));
        selectQuery.addSelectColumn(Column.getColumn("TaskToCustomerRel", "TASK_ID"));
        selectQuery.setCriteria(custCri.and(taskCri));
        final DataObject dataObject = getPersistenceLite().get(selectQuery);
        if (dataObject != null && !dataObject.isEmpty()) {
            taskId = (Long)dataObject.getFirstValue("TaskToCustomerRel", "TASK_ID");
        }
        return taskId;
    }
    
    public boolean isValidEmail(final String email) {
        if (!isStringEmpty(email)) {
            final Pattern pattern = Pattern.compile("^([\\w-\\$\\+\\'\\!\\#\\%\\*\\/\\=\\?\\^\\_\\.\\{\\|\\}\\~]+(?:\\.[\\w-\\$\\+]+)*)@((?:[\\w-]+\\.)*\\w[\\w-]{0,66})\\.([a-z]{2,12}(?:\\.[a-z]{2})?)$");
            final Matcher matcher = pattern.matcher(email.toLowerCase());
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }
    
    public Integer getIntVal(final Object val) {
        final String strVal = String.valueOf(val);
        if (!isStringEmpty(strVal)) {
            return Integer.valueOf(strVal);
        }
        return null;
    }
    
    public static org.json.simple.JSONArray executeSelectQuery(final Connection connection, final String selectQuery, final List columns) throws Exception {
        DataSet dataSet = null;
        org.json.simple.JSONArray dsJSArray = new org.json.simple.JSONArray();
        try {
            dataSet = RelationalAPI.getInstance().executeQuery(selectQuery, connection);
            dsJSArray = convertDataSetToJSONArray(dataSet, columns);
        }
        catch (final Exception ex) {
            IdpsUtil.logger.log(Level.SEVERE, "exception exectuging {0}", new Object[] { String.valueOf(selectQuery) });
            IdpsUtil.logger.log(Level.SEVERE, null, ex);
            throw ex;
        }
        finally {
            if (dataSet != null) {
                try {
                    dataSet.close();
                }
                catch (final Exception ex2) {
                    IdpsUtil.logger.log(Level.SEVERE, null, ex2);
                }
            }
        }
        return dsJSArray;
    }
    
    public static org.json.simple.JSONArray executeSelectQuery(final Connection connection, final SelectQuery selectQuery) throws Exception {
        final RelationalAPI relationAPI = RelationalAPI.getInstance();
        return executeSelectQuery(connection, relationAPI.getSelectSQL((Query)selectQuery), selectQuery.getSelectColumns());
    }
    
    public static org.json.simple.JSONArray executeSelectQuery(final SelectQuery selectQuery) {
        DataSet dataSet = null;
        Connection connection = null;
        org.json.simple.JSONArray dsJSArray = new org.json.simple.JSONArray();
        try {
            connection = RelationalAPI.getInstance().getConnection();
            dataSet = RelationalAPI.getInstance().executeQuery((Query)selectQuery, connection);
            dsJSArray = convertDataSetToJSONArray(dataSet, selectQuery.getSelectColumns());
        }
        catch (final Exception ex) {
            IdpsUtil.logger.log(Level.SEVERE, "Exception in executeSelectQuery", ex);
        }
        finally {
            CustomGroupUtil.getInstance().closeConnection(connection, dataSet);
        }
        return dsJSArray;
    }
    
    protected static org.json.simple.JSONArray convertDataSetToJSONArray(final DataSet dataSet, final List columns) throws SQLException {
        final org.json.simple.JSONArray dsJSArray = new org.json.simple.JSONArray();
        while (dataSet.next()) {
            final org.json.simple.JSONObject jsObject = new org.json.simple.JSONObject();
            for (int i = 0; i < columns.size(); ++i) {
                final Column column = columns.get(i);
                try {
                    if (dataSet.getValue(column.getColumnAlias()) != null) {
                        jsObject.put((Object)column.getColumnAlias(), dataSet.getValue(column.getColumnAlias()));
                    }
                }
                catch (final Exception ex) {
                    try {
                        jsObject.put((Object)column.getColumnAlias(), dataSet.getValue(column.getColumnAlias().toLowerCase()));
                    }
                    catch (final Exception ex2) {
                        try {
                            jsObject.put((Object)column.getColumnAlias(), dataSet.getValue(column.getColumnAlias().toUpperCase()));
                        }
                        catch (final Exception ex3) {
                            IdpsUtil.logger.log(Level.FINE, "could not get value from dataset for : {0}", new Object[] { column });
                        }
                    }
                }
            }
            dsJSArray.add((Object)jsObject);
        }
        return dsJSArray;
    }
    
    public GroupByClause getCustomerGroupClause() {
        final ArrayList groupByColumnsList = new ArrayList();
        groupByColumnsList.add(new Column("CustomerInfo", "CUSTOMER_ID"));
        final GroupByClause groupByColumn = new GroupByClause((List)groupByColumnsList);
        return groupByColumn;
    }
    
    public Column getDistinctCountCaseExpressionColumn(final CaseExpression expression, final int datatype, final String columnAlias) {
        final Column selectDistinctColumn = (Column)Column.createFunction("DISTINCT", new Object[] { expression });
        final Column selectColumn = (Column)Column.createFunction("COUNT", new Object[] { selectDistinctColumn });
        selectColumn.setType(datatype);
        selectColumn.setColumnAlias(columnAlias);
        return selectColumn;
    }
    
    public Column getDistinctColumn(final Column column, final int datatype, final String columnAlias) {
        final Column selectDistinctColumn = (Column)Column.createFunction("DISTINCT", new Object[] { column });
        selectDistinctColumn.setType(datatype);
        selectDistinctColumn.setColumnAlias(columnAlias);
        return selectDistinctColumn;
    }
    
    public Column getMinCaseExpressionColumn(final CaseExpression expression, final int datatype, final String columnAlias) {
        final Column selectColumn = (Column)Column.createFunction("MIN", new Object[] { expression });
        selectColumn.setType(datatype);
        selectColumn.setColumnAlias(columnAlias);
        return selectColumn;
    }
    
    public Column getCountCaseExpressionColumn(final CaseExpression expression, final int datatype, final String columnAlias) {
        final Column selectColumn = (Column)Column.createFunction("COUNT", new Object[] { expression });
        selectColumn.setType(datatype);
        selectColumn.setColumnAlias(columnAlias);
        return selectColumn;
    }
    
    public Column getCountCaseExpressionColumn(final CaseExpression expression) {
        return this.getCountCaseExpressionColumn(expression, 4, expression.getColumnAlias());
    }
    
    public static Column getCountOfColumn(final Column column, final String colAlias) {
        final Column countCol = (Column)Column.createFunction("COUNT", new Object[] { column });
        countCol.setType(4);
        countCol.setColumnAlias(colAlias);
        return countCol;
    }
    
    public static Column getCountOfColumn(final Column column) {
        String colAlias = column.getColumnAlias();
        if (SyMUtil.isStringEmpty(colAlias)) {
            colAlias = column.getColumnName() + "count";
        }
        return getCountOfColumn(column, colAlias);
    }
    
    public static Column getCountOfColumn(final String tableName, final String columnName, final String columnAlias) {
        return getCountOfColumn(new Column(tableName, columnName), columnAlias);
    }
    
    public static Column getMaxOfColumn(final String tableName, final String columnName, final String alias, final int maxDataType) {
        return getMaxOfColumn(new Column(tableName, columnName), alias, maxDataType);
    }
    
    public static Column getMaxOfColumn(final Column column, final String alias, final int maxDataType) {
        final Column maxCol = (Column)Column.createFunction("MAX", new Object[] { column });
        maxCol.setType(maxDataType);
        maxCol.setColumnAlias(alias);
        return maxCol;
    }
    
    public static Column getMinOfColumn(final String tableName, final String columnName, final String alias, final int maxDataType) {
        final Column minCol = (Column)Column.createFunction("MIN", new Object[] { new Column(tableName, columnName) });
        minCol.setType(maxDataType);
        minCol.setColumnAlias(alias);
        return minCol;
    }
    
    public Column getAvgCaseExpressionColumn(final CaseExpression expression, final int datatype, final String columnAlias) {
        final Column selectColumn = (Column)Column.createFunction("AVG", new Object[] { expression });
        selectColumn.setType(datatype);
        selectColumn.setColumnAlias(columnAlias);
        return selectColumn;
    }
    
    public Column getMaxCaseExpressionColumn(final CaseExpression expression, final int datatype, final String columnAlias) {
        final Column selectColumn = (Column)Column.createFunction("MAX", new Object[] { expression });
        selectColumn.setType(datatype);
        selectColumn.setColumnAlias(columnAlias);
        return selectColumn;
    }
    
    public Column getSDCaseExpressionColumn(final CaseExpression expression, final int datatype, final String columnAlias) {
        final String activedb = DBUtil.getActiveDBName();
        String sdFunctionName = "stddev_pop";
        if (activedb != null && activedb.equalsIgnoreCase("mssql")) {
            sdFunctionName = "STDEV";
        }
        final Column selectColumn = (Column)Column.createFunction(sdFunctionName, new Object[] { expression });
        selectColumn.setType(datatype);
        selectColumn.setColumnAlias(columnAlias);
        return selectColumn;
    }
    
    public Column getDistinctIntegerCountCaseExpressionColumn(final Column distCountCol) {
        final Column selectDistinctColumn = (Column)Column.createFunction("DISTINCT", new Object[] { distCountCol });
        final Column selectColumn = (Column)Column.createFunction("COUNT", new Object[] { selectDistinctColumn });
        selectColumn.setType(4);
        selectColumn.setColumnAlias(distCountCol.getColumnAlias());
        return selectColumn;
    }
    
    public Column getDistinctStringArrayCaseExpressionColumn(final CaseExpression expression) {
        final Column selectDistinctColumn = (Column)Column.createFunction("DISTINCT", new Object[] { expression });
        final Column selectArrayColumn = (Column)Column.createFunction("STRING_AGG", new Object[] { selectDistinctColumn, ";" });
        selectArrayColumn.setType(12);
        selectArrayColumn.setColumnAlias(expression.getColumnAlias());
        return selectArrayColumn;
    }
    
    public static Long getAdminUserId() {
        try {
            try {
                Object aaaUserID = DBUtil.getValueFromDB("AaaUser", "DESCRIPTION", (Object)"Product Admin", "USER_ID");
                if (aaaUserID != null) {
                    return (Long)aaaUserID;
                }
                aaaUserID = DBUtil.getValueFromDB("AaaUser", "DESCRIPTION", (Object)"Super Admin", "USER_ID");
                if (aaaUserID != null) {
                    return (Long)aaaUserID;
                }
            }
            catch (final Exception ex) {
                IdpsUtil.logger.log(Level.FINEST, "no super admin user found");
            }
            final ArrayList<Hashtable> usersList = DMUserHandler.getUserListForRole("Administrator");
            for (final Hashtable userInfo : usersList) {
                if (!userInfo.get("NAME").equalsIgnoreCase("dummy")) {
                    return userInfo.get("USER_ID");
                }
            }
        }
        catch (final Exception e) {
            IdpsUtil.logger.log(Level.SEVERE, "Exception in fetching admin user", e);
        }
        return null;
    }
    
    public static JSONObject convertPropertiesToJSONObject(final Properties props) throws JSONException {
        final JSONObject jsonObject = new JSONObject();
        final Set keys = props.keySet();
        final Iterator<String> itr = keys.iterator();
        while (itr != null && itr.hasNext()) {
            final String key = itr.next();
            final Object value = ((Hashtable<K, Object>)props).get(key);
            jsonObject.put(key, value);
        }
        return jsonObject;
    }
    
    public static HashMap convertJsonToHashMap(final JSONObject jsonObject) throws JSONException {
        final HashMap prop = new HashMap();
        final Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            final String key = iterator.next().toString();
            final Object value = jsonObject.get(key);
            prop.put(key, value);
        }
        return prop;
    }
    
    public static Criteria andCriteria(Criteria baseCriteria, final Criteria newCriteria) {
        baseCriteria = ((baseCriteria == null) ? newCriteria : baseCriteria.and(newCriteria));
        return baseCriteria;
    }
    
    static {
        IdpsUtil.idpsUtil = null;
    }
}
