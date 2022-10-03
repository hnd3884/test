package com.adventnet.sym.server.dcapi;

import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import java.util.ArrayList;
import java.util.List;
import com.me.devicemanagement.framework.webclient.api.util.ApiSQLViewController;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.webclient.api.util.ApiViewController;
import com.adventnet.ds.query.util.QueryUtil;
import com.me.devicemanagement.framework.server.ddextension.DDExtnParserTask;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.webclient.api.mapper.RequestMapper;
import java.sql.Connection;
import org.json.JSONObject;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.api.util.APIUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import com.me.devicemanagement.framework.webclient.api.util.APIRequest;
import java.util.logging.Logger;
import java.util.HashMap;

public class ApiDataCollector
{
    private HashMap tables;
    private Logger logger;
    private static final String[] QUERY_ID;
    private static final String[] QUERYID_TABLEALIAS;
    private static final String[] QUERYID_CRITERIA;
    
    public ApiDataCollector() {
        this.logger = Logger.getLogger("DCAPILogger");
    }
    
    public HashMap getTables() {
        return this.tables;
    }
    
    public void setTables(final HashMap tables) {
        this.tables = tables;
    }
    
    public JSONArray getJSONView(final APIRequest apiRequest) throws JSONException {
        Connection connection = null;
        try {
            final RequestMapper.Entity.Request request = apiRequest.getRequest();
            final RequestMapper.Entity.Request.ViewConfiguration viewConfiguration = request.getViewConfiguration();
            final Criteria criteria = new Criteria(new Column("CustomViewConfiguration", "CVNAME"), (Object)viewConfiguration.getCvName(), 0);
            final DataObject dataObject = DataAccess.get("CustomViewConfiguration", criteria);
            if (!dataObject.isEmpty()) {
                final Row row = dataObject.getRow("CustomViewConfiguration");
                final Object cvid = row.get("CVID");
                final Long qID = (Long)row.get("QUERYID");
                final Criteria cri = new Criteria(new Column("SelectTable", "QUERYID"), (Object)qID, 0);
                final DataObject dobj = DataAccess.get("SelectTable", cri);
                connection = RelationalAPI.getInstance().getConnection();
                DataSet dataSet = null;
                if (dobj.isEmpty()) {
                    final String queryString = this.getSelectQueryFromNativeSqlString(cvid, viewConfiguration, apiRequest);
                    this.logger.info(queryString);
                    if (queryString != null) {
                        dataSet = RelationalAPI.getInstance().executeQuery(queryString, connection);
                        final JSONArray jsonArray = this.constructJSONFromDSForNativeQuery(dataSet, apiRequest);
                        return jsonArray;
                    }
                }
                else {
                    final SelectQuery selectQuery = this.getSelectQuery(cvid, viewConfiguration, apiRequest);
                    if (selectQuery != null) {
                        dataSet = RelationalAPI.getInstance().executeQuery((Query)selectQuery, connection);
                        final JSONArray jsonArray = APIUtil.getInstance().constructJSONFromDS(dataSet);
                        return jsonArray;
                    }
                }
            }
        }
        catch (final DataAccessException dae) {
            this.logger.log(Level.SEVERE, "DataAccessException while getting json view for the given view configuration", (Throwable)dae);
        }
        catch (final SQLException sqe) {
            this.logger.log(Level.SEVERE, "SQLException while getting json view for the given view configuration", sqe);
        }
        catch (final QueryConstructionException qce) {
            this.logger.log(Level.SEVERE, "QueryConstructionException while getting json view for the given view configuration", (Throwable)qce);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting json view for the given view configuration", ex);
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception ex2) {
                this.logger.log(Level.SEVERE, "Exception while getting json view for the given view configuration", ex2);
            }
        }
        final JSONObject obj = new JSONObject();
        obj.put("error", (Object)"Error Occurred");
        obj.put("error_description", (Object)"Unknown Error Occurred! Verify the request data provided!");
        final JSONArray jarr = new JSONArray();
        jarr.put((Object)obj);
        return jarr;
    }
    
    private void loadColumnDefnsFromCache() {
        Object value = ApiFactoryProvider.getCacheAccessAPI().getCache(DDExtnParserTask.cacheName);
        if (null == value) {
            new DDExtnParserTask().initiateDDExtnLoading();
            try {
                Thread.sleep(5000L);
            }
            catch (final InterruptedException e) {
                this.logger.log(Level.SEVERE, "Exception while making the thread sleep", e);
            }
        }
        synchronized (DDExtnParserTask.DATA_DICTIONARY_EXTN_LOCK) {
            value = ApiFactoryProvider.getCacheAccessAPI().getCache(DDExtnParserTask.cacheName);
            this.setTables((HashMap)value);
        }
    }
    
    public SelectQuery getSelectQuery(final Object customViewId, final RequestMapper.Entity.Request.ViewConfiguration viewConfiguration, final APIRequest apiRequest) throws Exception {
        this.loadColumnDefnsFromCache();
        final Row customViewRow = new Row("CustomViewConfiguration");
        customViewRow.set(1, customViewId);
        final DataObject customViewDO = DataAccess.get("CustomViewConfiguration", customViewRow);
        final long queryID = (long)customViewDO.getFirstValue("CustomViewConfiguration", 3);
        final SelectQuery dumsq = this.getSelectQueryForSelectQueryRetrieval(queryID);
        final DataObject dataObj = DataAccess.get(dumsq);
        final SelectQuery[] sq = QueryUtil.getSelectQueryFromDO(dataObj);
        SelectQuery selectQuery = sq[0];
        final ApiViewController viewController = (ApiViewController)Class.forName(viewConfiguration.getViewController()).newInstance();
        selectQuery = viewController.updateSelectQuery(selectQuery, apiRequest);
        selectQuery = viewController.setCriteria(selectQuery, apiRequest);
        selectQuery = this.setRBCACriteria(selectQuery, apiRequest);
        selectQuery = this.setSortColumn(selectQuery, apiRequest);
        selectQuery = this.setDisplayName(selectQuery);
        selectQuery = this.setSearchTags(apiRequest, selectQuery);
        this.setTotalRecords((SelectQuery)selectQuery.clone(), apiRequest);
        selectQuery = this.setPageLength(selectQuery, apiRequest);
        this.setTables(null);
        this.logger.info(RelationalAPI.getInstance().getSelectSQL((Query)selectQuery));
        return selectQuery;
    }
    
    public SelectQuery setRBCACriteria(final SelectQuery selectQuery, final APIRequest request) {
        final Object techID = request.getParameterList().get("loginid");
        if (techID != null) {
            final boolean managedResourceTableCheck = selectQuery.getTableList().contains(new Table("ManagedComputer"));
            final boolean invcomputerTableCheck = selectQuery.getTableList().contains(new Table("InvComputer"));
            final Long loginID = Long.parseLong(String.valueOf(techID));
            final boolean isAdmin = DMUserHandler.isUserInRole(loginID, "All_Managed_Computer");
            if (!isAdmin) {
                Criteria cgCriteria = null;
                if (managedResourceTableCheck) {
                    selectQuery.addJoin(new Join("ManagedComputer", "UserResourceMapping", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                    cgCriteria = this.getUserMappingCriteria(loginID);
                }
                else if (invcomputerTableCheck) {
                    selectQuery.addJoin(new Join("InvComputer", "UserResourceMapping", new String[] { "COMPUTER_ID" }, new String[] { "RESOURCE_ID" }, 2));
                    cgCriteria = this.getUserMappingCriteria(loginID);
                }
                if (cgCriteria != null) {
                    cgCriteria = this.getJoinedCriteria(cgCriteria, selectQuery.getCriteria());
                    selectQuery.setCriteria(cgCriteria);
                }
            }
        }
        return selectQuery;
    }
    
    private Criteria getUserMappingCriteria(final Long loginID) {
        try {
            final Criteria userCriteria = new Criteria(Column.getColumn("UserResourceMapping", "LOGIN_ID"), (Object)loginID, 0);
            return userCriteria;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getSelectQueryFromNativeSqlString(final Object customViewId, final RequestMapper.Entity.Request.ViewConfiguration viewConfiguration, final APIRequest apiRequest) {
        Connection conn = null;
        final DataSet ds = null;
        this.loadColumnDefnsFromCache();
        try {
            final Row customViewRow = new Row("CustomViewConfiguration");
            customViewRow.set(1, customViewId);
            final DataObject customViewDO = DataAccess.get("CustomViewConfiguration", customViewRow);
            final long queryID = (long)customViewDO.getFirstValue("CustomViewConfiguration", 3);
            String queryString = "";
            final String sql = "SELECT * from ACSQLSTRING WHERE QUERYID='" + queryID + "'";
            conn = RelationalAPI.getInstance().getConnection();
            final DataSet dataSet = RelationalAPI.getInstance().executeQuery(sql, conn);
            if (dataSet != null) {
                dataSet.next();
                queryString = dataSet.getAsString("sql");
            }
            String sortString = "";
            if (!queryString.equals("")) {
                final ApiSQLViewController sqlViewController = (ApiSQLViewController)Class.forName(viewConfiguration.getViewController()).newInstance();
                queryString = sqlViewController.setVariableValues(apiRequest, queryString);
                sortString = sqlViewController.getSortString(apiRequest);
                final StringBuffer sb = new StringBuffer(queryString);
                sb.append(sortString);
                queryString = sb.toString();
                return queryString;
            }
        }
        catch (final SQLException ex) {
            this.logger.log(Level.SEVERE, "SQLException while getting native sql query", ex);
        }
        catch (final DataAccessException ex2) {
            this.logger.log(Level.SEVERE, "DataAccessException while getting native sql query", (Throwable)ex2);
        }
        catch (final QueryConstructionException ex3) {
            this.logger.log(Level.SEVERE, "QueryConstructionException while getting native sql query", (Throwable)ex3);
        }
        catch (final ClassNotFoundException ex4) {
            this.logger.log(Level.SEVERE, "ClassNotFoundException while getting native sql query", ex4);
        }
        catch (final InstantiationException ex5) {
            this.logger.log(Level.SEVERE, "InstantiationException while getting native sql query", ex5);
        }
        catch (final IllegalAccessException ex6) {
            this.logger.log(Level.SEVERE, "IllegalAccessException while getting native sql query", ex6);
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception while closing connection and dataset ", e);
            }
        }
        return null;
    }
    
    private SelectQuery setSearchTags(final APIRequest apiRequest, final SelectQuery selectQuery) {
        final HashMap parameterList = apiRequest.getParameterList();
        if (parameterList.containsKey("searchtype") && parameterList.containsKey("searchcolumn") && parameterList.containsKey("searchvalue")) {
            final Column selectColumn = this.getColumnForCri(selectQuery.getSelectColumns(), parameterList.get("searchcolumn").toString());
            if (selectColumn != null) {
                Criteria criteria = new Criteria(selectColumn, (Object)parameterList.get("searchvalue").toString(), 12, (boolean)Boolean.FALSE);
                criteria = this.getJoinedCriteria(selectQuery.getCriteria(), criteria);
                selectQuery.setCriteria(criteria);
            }
        }
        return selectQuery;
    }
    
    private Column getColumnForCri(final List selectColumns, final String searchcolumn) {
        Column returnCol = null;
        String tableName = null;
        String columnName = searchcolumn;
        if (searchcolumn.contains(".")) {
            tableName = searchcolumn.substring(0, searchcolumn.indexOf("."));
            columnName = searchcolumn.substring(searchcolumn.indexOf(".") + 1);
        }
        for (int columnIndex = 0; columnIndex < selectColumns.size(); ++columnIndex) {
            final Column column = selectColumns.get(columnIndex);
            if (columnName.equalsIgnoreCase(column.getColumnAlias())) {
                if (tableName != null && tableName.equalsIgnoreCase(column.getTableAlias())) {
                    return column;
                }
                returnCol = column;
            }
        }
        return returnCol;
    }
    
    private Criteria getJoinedCriteria(final Criteria firstCri, final Criteria secondCri) {
        if (firstCri != null && secondCri != null) {
            return firstCri.and(secondCri);
        }
        if (firstCri != null) {
            return firstCri;
        }
        if (secondCri != null) {
            return secondCri;
        }
        return null;
    }
    
    private SelectQuery setDisplayName(final SelectQuery selectQuery) {
        final List selectColumns = selectQuery.getSelectColumns();
        final List newSelectCols = new ArrayList();
        final List newSelectColsDisName = new ArrayList();
        for (int i = 0; i < selectColumns.size(); ++i) {
            final Column column = selectColumns.get(i);
            selectQuery.removeSelectColumn(column);
            final String tableName = column.getTableAlias();
            final String columnName = column.getColumnName();
            String displayName = this.getDisplayNameFromCache(tableName, columnName, column.getColumnAlias());
            if (newSelectColsDisName.contains(displayName)) {
                displayName = column.getColumnAlias();
            }
            newSelectColsDisName.add(displayName);
            final Column column2 = new Column(tableName, columnName, displayName);
            newSelectCols.add(column2);
        }
        selectQuery.addSelectColumns(newSelectCols);
        return selectQuery;
    }
    
    private String getDisplayNameFromCache(final String tableName, final String columnName, String columnAlias) {
        try {
            if (null != this.getTables()) {
                final Object value = this.getTables().get(tableName);
                if (value != null) {
                    final Object columns = ((HashMap)value).get(columnName);
                    if (columns != null && ((HashMap)columns).get("display-name") != null) {
                        columnAlias = ((HashMap)columns).get("display-name").toString();
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while getting display name from cache {0}", e);
            return columnAlias;
        }
        return columnAlias;
    }
    
    private JSONArray constructJSONFromDSForNativeQuery(final DataSet dataSet, final APIRequest apiRequest) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        final HashMap<String, String> dispNameCache = new HashMap<String, String>();
        boolean firstRow = Boolean.TRUE;
        final HashMap parameterList = apiRequest.getParameterList();
        String searchKey = null;
        String searchValue = null;
        if (parameterList.containsKey("searchtype") && parameterList.containsKey("searchcolumn") && parameterList.containsKey("searchvalue")) {
            searchKey = parameterList.get("searchcolumn");
            searchValue = parameterList.get("searchvalue");
        }
        try {
            if (dataSet != null) {
                final int columnCount = dataSet.getColumnCount();
                while (dataSet.next()) {
                    int searchFlag = -1;
                    final JSONObject jsonObject = new JSONObject();
                    for (int i = 1; i <= columnCount; ++i) {
                        final String columnName = dataSet.getColumnName(i);
                        final Object columnValue = dataSet.getValue(i);
                        String displayName = columnName;
                        if (columnName.contains(".")) {
                            if (firstRow) {
                                final String tableName = columnName.substring(0, columnName.indexOf("."));
                                final String colName = columnName.substring(columnName.indexOf(".") + 1);
                                displayName = this.getDisplayNameFromCache(tableName, colName, colName);
                                dispNameCache.put(columnName, displayName);
                            }
                            else {
                                displayName = dispNameCache.get(columnName);
                            }
                        }
                        if (searchKey != null && searchValue != null) {
                            final String colVal = (columnValue == null) ? "" : columnValue.toString();
                            String searchCol = searchKey;
                            if (searchKey.contains(".")) {
                                searchCol = searchKey.substring(searchKey.indexOf(".") + 1);
                            }
                            if (displayName.equalsIgnoreCase(searchCol) && colVal.contains(searchValue)) {
                                searchFlag = 1;
                            }
                        }
                        else if (searchKey == null || searchKey.equals("") || searchValue == null || searchValue.equals("")) {
                            searchFlag = 1;
                        }
                        if (!jsonObject.has(displayName.toLowerCase())) {
                            jsonObject.put(displayName.toLowerCase(), (columnValue == null) ? "--" : columnValue);
                        }
                    }
                    firstRow = Boolean.FALSE;
                    if (searchFlag > 0) {
                        jsonArray.put((Object)jsonObject);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while constructing JSON object from DataSet {0}", e);
        }
        apiRequest.setTotalRecords((long)jsonArray.length());
        jsonArray = this.setPageLength(apiRequest, jsonArray);
        return jsonArray;
    }
    
    private void setTotalRecords(final SelectQuery selectQuery, final APIRequest apiRequest) throws QueryConstructionException {
        final String countQuery = "Select count(1) from (" + this.getSelectSQL(selectQuery) + ") alias";
        Connection connection = null;
        try {
            connection = RelationalAPI.getInstance().getConnection();
            final DataSet dataSet = RelationalAPI.getInstance().executeQuery(countQuery, connection);
            if (dataSet != null) {
                dataSet.next();
                final Object count = dataSet.getValue(1);
                apiRequest.setTotalRecords(Long.parseLong(count.toString()));
            }
        }
        catch (final SQLException e) {
            this.logger.log(Level.SEVERE, "SQLException while calculating total records for the given query {0}", e);
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception while closing connection on setting total records {0}", e2);
            }
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e3) {
                this.logger.log(Level.SEVERE, "Exception while closing connection on setting total records {0}", e3);
            }
        }
    }
    
    private String getSelectSQL(final SelectQuery selectQuery) {
        try {
            selectQuery.setRange((Range)null);
            for (int size = selectQuery.getSortColumns().size(), i = 0; i < size; ++i) {
                selectQuery.removeSortColumn(0);
            }
            return RelationalAPI.getInstance().getSelectSQL((Query)selectQuery);
        }
        catch (final QueryConstructionException e) {
            this.logger.log(Level.SEVERE, "Exception while closing connection on setting total records {0}", (Throwable)e);
            return null;
        }
    }
    
    private SelectQuery setSortColumn(final SelectQuery selectQuery, final APIRequest apiRequest) {
        final List selectColumns = selectQuery.getSelectColumns();
        if (selectColumns != null && selectColumns.size() > 0) {
            boolean isAsc = Boolean.TRUE;
            final RequestMapper.Entity.Request.ViewConfiguration viewConfiguration = apiRequest.getRequest().getViewConfiguration();
            final String sortColString = viewConfiguration.getSortCol();
            Column sortCol = null;
            if (sortColString != null && !sortColString.trim().equals("")) {
                final String tableName = sortColString.substring(0, sortColString.indexOf("."));
                final String colName = sortColString.substring(sortColString.indexOf(".") + 1);
                final String sortOrder = viewConfiguration.getSortOrder();
                if (sortOrder != null && sortOrder.equalsIgnoreCase("DESC")) {
                    isAsc = Boolean.FALSE;
                }
                sortCol = new Column(tableName, colName);
            }
            else {
                sortCol = selectColumns.get(0);
                if (apiRequest.getSortorder().equalsIgnoreCase("desc")) {
                    isAsc = Boolean.FALSE;
                }
            }
            if (sortCol != null) {
                final SortColumn sortColumn = new SortColumn(sortCol, isAsc);
                selectQuery.addSortColumn(sortColumn);
            }
        }
        return selectQuery;
    }
    
    private SelectQuery setPageLength(final SelectQuery selectQuery, final APIRequest apiRequest) {
        final int pageLength = apiRequest.getPageLimit();
        final int pageIndex = apiRequest.getPageIndex();
        final int startIndex = pageLength * pageIndex - (pageLength - 1);
        selectQuery.setRange(new Range(startIndex, pageLength));
        return selectQuery;
    }
    
    private JSONArray setPageLength(final APIRequest apiRequest, final JSONArray jsonArray) throws JSONException {
        final int pageLength = apiRequest.getPageLimit();
        final int pageIndex = apiRequest.getPageIndex();
        final int startIndex = pageLength * pageIndex - (pageLength - 1);
        final JSONArray tempArray = new JSONArray();
        if (jsonArray.length() > 0) {
            for (int i = startIndex - 1; i < jsonArray.length() && i < startIndex + pageLength - 1; ++i) {
                tempArray.put((Object)jsonArray.getJSONObject(i));
            }
        }
        return tempArray;
    }
    
    private SelectQuery getSelectQueryForSelectQueryRetrieval(final long queryID) {
        final SelectQuery sqForQueryID = (SelectQuery)new SelectQueryImpl(new Table("SelectQuery"));
        sqForQueryID.addSelectColumn(new Column((String)null, "*"));
        sqForQueryID.addJoin(new Join("SelectQuery", "SelectTable", ApiDataCollector.QUERY_ID, ApiDataCollector.QUERY_ID, 2));
        sqForQueryID.addJoin(new Join("SelectTable", "SelectColumn", ApiDataCollector.QUERYID_TABLEALIAS, ApiDataCollector.QUERYID_TABLEALIAS, 1));
        sqForQueryID.addJoin(new Join("SelectTable", "JoinTable", ApiDataCollector.QUERYID_TABLEALIAS, ApiDataCollector.QUERYID_TABLEALIAS, 1));
        sqForQueryID.addJoin(new Join("JoinTable", "JoinColumns", ApiDataCollector.QUERYID_TABLEALIAS, ApiDataCollector.QUERYID_TABLEALIAS, 1));
        sqForQueryID.addJoin(new Join("SelectQuery", "Criteria", ApiDataCollector.QUERY_ID, ApiDataCollector.QUERY_ID, 1));
        sqForQueryID.addJoin(new Join("Criteria", "RelationalCriteria", ApiDataCollector.QUERYID_CRITERIA, ApiDataCollector.QUERYID_CRITERIA, 1));
        sqForQueryID.addJoin(new Join("SelectTable", "SortColumn", ApiDataCollector.QUERYID_TABLEALIAS, ApiDataCollector.QUERYID_TABLEALIAS, 1));
        sqForQueryID.addJoin(new Join("SelectQuery", "JoinCriteria", ApiDataCollector.QUERY_ID, ApiDataCollector.QUERY_ID, 1));
        sqForQueryID.addJoin(new Join("JoinCriteria", "JoinRelCriteria", ApiDataCollector.QUERYID_CRITERIA, ApiDataCollector.QUERYID_CRITERIA, 1));
        final Criteria sqCriteria = new Criteria(new Column("SelectQuery", "QUERYID"), (Object)new Long(queryID), 0);
        sqForQueryID.setCriteria(sqCriteria);
        sqForQueryID.addSortColumn(new SortColumn(new Column("RelationalCriteria", "RELATIONALCRITERIAID"), true));
        return sqForQueryID;
    }
    
    static {
        QUERY_ID = new String[] { "QUERYID" };
        QUERYID_TABLEALIAS = new String[] { "QUERYID", "TABLEALIAS" };
        QUERYID_CRITERIA = new String[] { "QUERYID", "CRITERIAID" };
    }
}
