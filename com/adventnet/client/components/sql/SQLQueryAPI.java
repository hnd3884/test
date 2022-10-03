package com.adventnet.client.components.sql;

import java.util.Arrays;
import java.sql.Statement;
import java.sql.ResultSet;
import java.math.BigDecimal;
import java.util.logging.Level;
import com.adventnet.client.components.table.web.ExportTableViewModel;
import com.adventnet.client.components.table.web.ExportTableModel;
import java.util.Map;
import com.zoho.mickey.api.SQLStringAPI;
import com.adventnet.db.util.SelectQueryStringUtil;
import java.util.HashMap;
import com.adventnet.client.ClientException;
import com.adventnet.client.ClientErrorCodes;
import com.adventnet.client.components.table.TableViewState;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Range;
import com.adventnet.client.components.table.web.TableDatasetModel;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.client.components.table.web.SqlViewController;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.adventnet.client.cache.StaticCache;
import com.adventnet.client.tpl.TemplateAPI;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SQLQueryAPI
{
    private static final Logger out;
    private static ArrayList tableList;
    private static ArrayList counttableList;
    private static ArrayList sumtableList;
    
    public static String getSQLString(final Object cvId, final TemplateAPI.VariableHandler varHandler, final Object handlerContext) throws Exception {
        final Object[] compiledInfo = getCompiledInfo(cvId, handlerContext);
        return TemplateAPI.getFilledString(compiledInfo, varHandler, handlerContext);
    }
    
    private static Object[] getCountSQLCompiledInfo(final Object cvId, final Object handlerContext) throws Exception {
        String key = "ACCOUNTSQLSTRING:" + cvId;
        final String filterName = getFilterName(handlerContext);
        key = key + ":" + filterName;
        Object[] compiledInfo = (Object[])StaticCache.getFromCache((Object)key);
        if (compiledInfo != null) {
            return compiledInfo;
        }
        final DataObject dao = getCountACSQLDO(cvId);
        final Row r = dao.getRow("ACCountSQLString");
        if (r == null) {
            return null;
        }
        final String sql = (String)r.get("SQL");
        compiledInfo = new Object[3];
        TemplateAPI.fillCompiledInfo(sql, compiledInfo, TemplateAPI.simplePattern);
        compiledInfo[2] = r;
        StaticCache.addToCache((Object)key, (Object)compiledInfo, (List)SQLQueryAPI.counttableList);
        return compiledInfo;
    }
    
    private static Object[] getSumSQLCompiledInfo(final Object cvId, final Object handlerContext) throws Exception {
        String key = "ACSUMSQLSTRING:" + cvId;
        final String filterName = getFilterName(handlerContext);
        key = key + ":" + filterName;
        Object[] compiledInfo = (Object[])StaticCache.getFromCache((Object)key);
        if (compiledInfo != null) {
            return compiledInfo;
        }
        final DataObject dao = getSumACSQLDO(cvId);
        final Row r = dao.getRow("ACSumSQLString");
        if (r == null) {
            return null;
        }
        final String sql = (String)r.get("SQL");
        compiledInfo = new Object[3];
        TemplateAPI.fillCompiledInfo(sql, compiledInfo, TemplateAPI.simplePattern);
        compiledInfo[2] = r;
        StaticCache.addToCache((Object)key, (Object)compiledInfo, (List)SQLQueryAPI.counttableList);
        return compiledInfo;
    }
    
    private static String getFilterName(final Object handlerContext) {
        String filterName = "";
        if (!(handlerContext instanceof ViewContext)) {
            return filterName;
        }
        final ViewContext viewContext = (ViewContext)handlerContext;
        filterName = (String)viewContext.getStateOrURLStateParameter("SELFILTER");
        if (filterName == null) {
            filterName = "";
        }
        return filterName;
    }
    
    private static String getCriteriaAsString(final Object handlerContext) throws Exception {
        if (handlerContext == null || !(handlerContext instanceof ViewContext)) {
            return null;
        }
        final ViewContext viewCtx = (ViewContext)handlerContext;
        final Criteria obj = (Criteria)viewCtx.getTransientState("FILTER");
        if (obj == null) {
            return null;
        }
        final String criteriaAsStr = RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().formWhereClause(obj);
        return criteriaAsStr;
    }
    
    private static String mergeSQLAndCriteria(String sql, String criteria) {
        if (criteria == null) {
            return sql;
        }
        final String sqlQuery = sql.toUpperCase();
        final int groupByIndex = sqlQuery.indexOf("GROUP BY");
        final int orderByIndex = sqlQuery.indexOf("ORDER BY");
        if (groupByIndex != -1) {
            final String groupBySql = sql.substring(groupByIndex);
            sql = sql.substring(0, groupByIndex - 1);
            criteria += groupBySql;
        }
        else if (orderByIndex != -1) {
            final String orderBySql = sql.substring(orderByIndex);
            sql = sql.substring(0, orderByIndex - 1);
            criteria += orderBySql;
        }
        sql = sql + " AND" + criteria;
        return sql;
    }
    
    private static Object[] getCompiledInfo(final Object cvId, final Object handlerContext) throws Exception {
        Object[] compiledInfo = null;
        final String sqlKey = ((ViewContext)handlerContext).getModel().getViewName() + "_SQL";
        final DataObject dao = getACSQLDO(cvId);
        final Row r = dao.getFirstRow("ACSQLString");
        String sql = (String)r.get(2);
        final String sqlWithDynamicColumns = sql.contains("${DYNAMIC_COLUMN}") ? sql.replace("${DYNAMIC_COLUMN}", SqlViewController.getDynamicColumns((ViewContext)handlerContext)) : sql;
        StaticCache.addToCache((Object)sqlKey, (Object)sqlWithDynamicColumns, (List)SQLQueryAPI.tableList);
        final String criteriaAsStr = getCriteriaAsString(handlerContext);
        sql = mergeSQLAndCriteria(sqlWithDynamicColumns, criteriaAsStr);
        compiledInfo = new Object[3];
        TemplateAPI.fillCompiledInfo(sql, compiledInfo, TemplateAPI.simplePattern);
        compiledInfo[2] = r;
        return compiledInfo;
    }
    
    public static DataObject getACSQLDO(final Object customViewId) throws Exception {
        final String key = "ACSQLDO:" + customViewId;
        DataObject acSqlDO = (DataObject)StaticCache.getFromCache((Object)key);
        if (acSqlDO != null) {
            return acSqlDO;
        }
        final Row r = new Row("CustomViewConfiguration");
        if (customViewId instanceof String) {
            r.set("CVNAME", customViewId);
        }
        else if (customViewId instanceof Long) {
            r.set("CVID", customViewId);
        }
        if (SQLQueryAPI.tableList == null) {
            final ArrayList<String> tempAr = new ArrayList<String>();
            synchronized (tempAr) {
                tempAr.add("SelectQuery");
                tempAr.add("ACSQLString");
                tempAr.add("CustomViewConfiguration");
            }
            SQLQueryAPI.tableList = tempAr;
        }
        acSqlDO = LookUpUtil.getPersistence().get((List)SQLQueryAPI.tableList, r);
        StaticCache.addToCache((Object)key, (Object)acSqlDO, (List)SQLQueryAPI.tableList);
        return acSqlDO;
    }
    
    public static DataObject getCountACSQLDO(final Object customViewId) throws Exception {
        final String key = "ACCountSQLDO:" + customViewId;
        DataObject acCountSqlDO = (DataObject)StaticCache.getFromCache((Object)key);
        if (acCountSqlDO != null) {
            return acCountSqlDO;
        }
        final Row r = new Row("CustomViewConfiguration");
        r.set("CVID", customViewId);
        if (SQLQueryAPI.counttableList == null) {
            final ArrayList<String> tempAr = new ArrayList<String>();
            synchronized (tempAr) {
                tempAr.add("SelectQuery");
                tempAr.add("ACSQLString");
                tempAr.add("ACCountSQLString");
                tempAr.add("CustomViewConfiguration");
            }
            SQLQueryAPI.counttableList = tempAr;
        }
        acCountSqlDO = LookUpUtil.getPersistence().get((List)SQLQueryAPI.counttableList, r);
        StaticCache.addToCache((Object)key, (Object)acCountSqlDO, (List)SQLQueryAPI.counttableList);
        return acCountSqlDO;
    }
    
    public static DataObject getSumACSQLDO(final Object cvId) throws Exception {
        final String key = "ACSumSQLDO:" + cvId;
        DataObject acSumSqlDO = (DataObject)StaticCache.getFromCache((Object)key);
        if (acSumSqlDO != null) {
            return acSumSqlDO;
        }
        final Row r = new Row("CustomViewConfiguration");
        r.set("CVID", cvId);
        if (SQLQueryAPI.sumtableList == null) {
            final ArrayList<String> tempAr = new ArrayList<String>();
            synchronized (tempAr) {
                tempAr.add("SelectQuery");
                tempAr.add("ACSQLString");
                tempAr.add("ACSumSQLString");
                tempAr.add("CustomViewConfiguration");
            }
            SQLQueryAPI.sumtableList = tempAr;
        }
        acSumSqlDO = LookUpUtil.getPersistence().get((List)SQLQueryAPI.sumtableList, r);
        StaticCache.addToCache((Object)key, (Object)acSumSqlDO, (List)SQLQueryAPI.sumtableList);
        return acSumSqlDO;
    }
    
    public static String getSumSQL(final Object cvId, final String constructedSQL, final Object handlerContext) throws Exception {
        final ViewContext vc = (ViewContext)handlerContext;
        final String[] sumcols = vc.getModel().getFeatureValue("SUMCOLS").split(",");
        String sumsql = "";
        for (int i = 0; i < sumcols.length; ++i) {
            if (i == 0) {
                sumsql = "select sum(" + sumcols[i] + ")";
            }
            else {
                sumsql = sumsql + ", sum(" + sumcols[i] + ")";
            }
        }
        return sumsql + " from (" + constructedSQL + ") sel";
    }
    
    public static String getCountSQL(final Object cvId, final String constructedSQL, final Object handlerContext) throws Exception {
        final Object[] compiledInfo = getCompiledInfo(cvId, handlerContext);
        final Row r = (Row)compiledInfo[2];
        return RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().formCountSQL(constructedSQL, (boolean)r.get("GROUPBYUSED"));
    }
    
    public static String getCountSQL(final Object cvId, final String constructedSQL, final TemplateAPI.VariableHandler varHandler, final Object handlerContext) throws Exception {
        Object[] compiledInfo = null;
        try {
            compiledInfo = getCountSQLCompiledInfo(cvId, handlerContext);
        }
        catch (final DataAccessException exp) {
            exp.printStackTrace();
        }
        if (compiledInfo == null) {
            return getCountSQL(cvId, constructedSQL, handlerContext);
        }
        return TemplateAPI.getFilledString(compiledInfo, varHandler, handlerContext);
    }
    
    public static String getSumSQL(final Object cvId, final String constructedSQL, final TemplateAPI.VariableHandler varHandler, final Object handlerContext) throws Exception {
        Object[] compiledInfo = null;
        try {
            compiledInfo = getSumSQLCompiledInfo(cvId, handlerContext);
        }
        catch (final DataAccessException exp) {
            exp.printStackTrace();
        }
        if (compiledInfo == null) {
            return getSumSQL(cvId, constructedSQL, handlerContext);
        }
        return TemplateAPI.getFilledString(compiledInfo, varHandler, handlerContext);
    }
    
    public static TableDatasetModel getAsTableModel(final String sqlQuery, final String countSql) throws Exception {
        return getAsTableModel(sqlQuery, false, countSql, null, true, null, null);
    }
    
    public static TableDatasetModel getAsTableModel(final String sqlQuery, final boolean isUnionQuery, final String countSql) throws Exception {
        return getAsTableModel(sqlQuery, isUnionQuery, countSql, null, true, null, null);
    }
    
    public static TableDatasetModel getAsTableModel(final String sqlQuery, final String countSql, final ViewContext viewContext) throws Exception {
        return getAsTableModel(sqlQuery, false, countSql, viewContext, true, null, null);
    }
    
    public static TableDatasetModel getAsTableModel(final String sqlQuery, final String countSql, final ViewContext viewCtx, final boolean isCount) throws Exception {
        return getAsTableModel(sqlQuery, false, countSql, viewCtx, isCount, null, null);
    }
    
    private static long getCount(final String countSql, final Connection con) throws SQLException, QueryConstructionException {
        DataSet ds = null;
        long totalCount = -1L;
        if (countSql != null) {
            try {
                ds = RelationalAPI.getInstance().executeQuery(countSql, con);
                if (ds.next()) {
                    totalCount = (long)ds.getValue(1, -5);
                }
            }
            catch (final Exception e) {
                SQLQueryAPI.out.fine("Exception occuured while executing count query : " + countSql);
                e.printStackTrace();
                throw e;
            }
            finally {
                if (ds != null) {
                    try {
                        ds.close();
                    }
                    catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return totalCount;
    }
    
    private static Range getAvailableRangeForNormalCount(final Range range, final long totalCount, final boolean isNoCount) {
        final int pageLen = range.getNumberOfObjects();
        int startIndex = range.getStartIndex();
        if (totalCount == 0L) {
            startIndex = 0;
        }
        if (startIndex > totalCount) {
            final int viewLength = Math.max(isNoCount ? (pageLen - 1) : pageLen, 10);
            int pageNum = (int)totalCount / viewLength;
            if ((int)totalCount % viewLength == 0) {
                --pageNum;
            }
            startIndex = pageNum * viewLength + 1;
        }
        return new Range(startIndex, pageLen);
    }
    
    public static TableDatasetModel getAsTableModel(final String sqlQuery, final boolean isUnionQuery, final String countSql, final ViewContext viewCtx, boolean isCount, Range range, final String sumsql) throws Exception {
        boolean fetchCountOnly = false;
        final boolean isNoCount = !isCount;
        boolean fetchPrevPage = false;
        String no_of_attempt_to_fetchPrevPage = null;
        if (viewCtx != null) {
            fetchCountOnly = Boolean.TRUE.equals(viewCtx.getTransientState("fetchCountOnly"));
            fetchPrevPage = (viewCtx.isCSRComponent() ? ((TableViewState)viewCtx.getViewState()).isPrevPageToBeFetched() : Boolean.parseBoolean(viewCtx.getRequest().getParameter("fetchPrevPage")));
            if (sqlQuery == null && !fetchCountOnly) {
                throw new ClientException(ClientErrorCodes.SQL_QUERY_NULL);
            }
            if (countSql == null && (!isNoCount || fetchCountOnly || fetchPrevPage)) {
                throw new ClientException(ClientErrorCodes.SQL_QUERY_NULL_FETCHCOUNT);
            }
            no_of_attempt_to_fetchPrevPage = viewCtx.getModel().getFeatureValue("no_of_attempt_to_fetchPrevPage");
        }
        final TableDatasetModel tdm = new TableDatasetModel();
        final RelationalAPI relationalAPI = RelationalAPI.getInstance();
        long totalCount = -1L;
        try (final Connection con = RelationalAPI.getInstance().getConnection()) {
            final Map templateValues = new HashMap();
            RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().fillUserDataRange(templateValues);
            String updatedSqlWithRange = null;
            String templateReplacedSQL = null;
            if (sqlQuery != null) {
                templateReplacedSQL = SelectQueryStringUtil.replaceAllTemplatesForSQL(sqlQuery, templateValues);
            }
            final int no_of_times_to_fetch_prevPage = (no_of_attempt_to_fetchPrevPage != null) ? Integer.parseInt(no_of_attempt_to_fetchPrevPage) : 2;
            int iteration_count = 0;
            while (iteration_count <= no_of_times_to_fetch_prevPage) {
                if (isCount && countSql != null) {
                    totalCount = getCount(SelectQueryStringUtil.replaceAllTemplatesForSQL(countSql, templateValues), con);
                    tdm.setTotalRecordsCount(totalCount);
                }
                if (fetchCountOnly) {
                    return tdm;
                }
                final HashMap totalSumMap = tdm.getTotalSumMap();
                updateTotalSumMap(totalSumMap, templateValues, con, viewCtx, sumsql);
                int startIndex = 0;
                int viewLength = 0;
                if (range != null) {
                    if (totalCount != -1L) {
                        range = getAvailableRangeForNormalCount(range, totalCount, isNoCount);
                    }
                    startIndex = range.getStartIndex();
                    viewLength = range.getNumberOfObjects();
                }
                if (isUnionQuery) {
                    updatedSqlWithRange = SQLStringAPI.getInstance().getSQLForUnionWithRange(templateReplacedSQL, range);
                }
                else {
                    updatedSqlWithRange = SQLStringAPI.getInstance().getSQLForSelectWithRange(templateReplacedSQL, range);
                }
                tdm.setStartIndex(startIndex);
                tdm.setPageLength(viewLength);
                try (final DataSet ds = relationalAPI.executeQuery(updatedSqlWithRange, con)) {
                    tdm.updateModel(viewCtx, ds);
                }
                catch (final Exception e) {
                    SQLQueryAPI.out.fine("Execption occurred while executing the query : " + updatedSqlWithRange);
                    e.printStackTrace();
                    throw e;
                }
                if (!isNoCount || !fetchPrevPage || tdm.getFetchedRecordsCount() != 0L || startIndex <= 1) {
                    break;
                }
                startIndex = Math.max(startIndex - viewLength + 1, 1);
                range = new Range(startIndex, viewLength);
                if (++iteration_count != no_of_times_to_fetch_prevPage) {
                    continue;
                }
                isCount = true;
            }
        }
        catch (final Exception e2) {
            e2.printStackTrace();
            throw e2;
        }
        if (sumsql != null) {
            setViewSumMapInModel(tdm, viewCtx);
        }
        return tdm;
    }
    
    public static ExportTableModel getExportTableModel(String sqlQuery, final ViewContext viewCtx, final String sumsql) throws Exception {
        final ExportTableViewModel tableModel = new ExportTableViewModel();
        final Map templateValues = new HashMap();
        try {
            RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().fillUserDataRange(templateValues);
            sqlQuery = SelectQueryStringUtil.replaceAllTemplatesForSQL(sqlQuery, templateValues);
            final HashMap totalSumMap = tableModel.getTotalSumMap();
            updateTotalSumMap(totalSumMap, templateValues, (Connection)viewCtx.getTransientState("CONNECTION"), viewCtx, sumsql);
            final DataSet ds = RelationalAPI.getInstance().executeReadOnlyQuery(sqlQuery, (Connection)viewCtx.getTransientState("CONNECTION"));
            tableModel.updateModel(ds);
        }
        catch (final Exception ex) {
            SQLQueryAPI.out.log(Level.SEVERE, " Exception occurred while constructing export table model" + ex.getMessage());
            ex.printStackTrace();
            throw new Exception("Exception occurred while constructing export table model ", ex);
        }
        return tableModel;
    }
    
    private static void updateTotalSumMap(final HashMap totalSumMap, final Map templateValues, final Connection con, final ViewContext viewCtx, final String sumsql) throws Exception {
        ResultSet rs = null;
        Statement st = null;
        try {
            if (sumsql != null) {
                final String sql = SelectQueryStringUtil.replaceAllTemplatesForSQL(sumsql, templateValues);
                st = con.createStatement();
                rs = st.executeQuery(sql);
                BigDecimal sum = BigDecimal.ZERO;
                if (rs.next()) {
                    final String[] sumcols = viewCtx.getModel().getFeatureValue("SUMCOLS").toString().split(",");
                    for (int noOfSumCols = sumcols.length, i = 1; i <= noOfSumCols; ++i) {
                        sum = rs.getBigDecimal(i);
                        totalSumMap.put(sumcols[i - 1], sum);
                    }
                }
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (st != null) {
                try {
                    st.close();
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final Exception ex2) {
                    ex2.printStackTrace();
                }
            }
            if (st != null) {
                try {
                    st.close();
                }
                catch (final Exception ex2) {
                    ex2.printStackTrace();
                }
            }
        }
    }
    
    private static void setViewSumMapInModel(final TableDatasetModel tdm, final ViewContext vc) throws Exception {
        final HashMap map = tdm.getViewSumMap();
        if (vc.getModel().getFeatureValue("VIEWSUMCOLS") != null) {
            final String[] viewSumCols = vc.getModel().getFeatureValue("VIEWSUMCOLS").toString().split(",");
            final int rowcount = tdm.getRowCount();
            final int colcount = tdm.getColumnCount();
            if (rowcount <= 0 || colcount < viewSumCols.length) {
                SQLQueryAPI.out.log(Level.FINER, "row count is {0}, column count is {1} viewsumcols is {2} hence returning", new Object[] { rowcount, colcount, Arrays.asList(viewSumCols) });
                return;
            }
            for (int i = 0; i < viewSumCols.length; ++i) {
                final String colAlias = viewSumCols[i];
                final int colIndex = tdm.getColumnIndex(colAlias);
                if (colIndex == -1) {
                    throw new Exception("unknown column " + colAlias + " given");
                }
                BigDecimal sum = BigDecimal.ZERO;
                for (int j = 0; j < rowcount; ++j) {
                    final Object val = tdm.getValueAt(j, colIndex);
                    if (val != null && !val.equals("")) {
                        if (val instanceof BigDecimal) {
                            sum.add((BigDecimal)val);
                        }
                        else {
                            final double el = Double.parseDouble(val.toString());
                            final BigDecimal bd = new BigDecimal(el);
                            sum = sum.add(bd);
                        }
                    }
                }
                map.put(colAlias, sum);
            }
        }
    }
    
    static {
        out = Logger.getLogger(SQLQueryAPI.class.getName());
    }
}
