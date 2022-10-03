package com.me.devicemanagement.framework.webclient.reportcriteria;

import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterConstants;
import java.util.Collection;
import java.util.Arrays;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilterCriteria;
import com.me.devicemanagement.framework.common.api.v1.model.DCViewFilter;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.DataAccess;
import java.io.IOException;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterUtil;
import com.me.devicemanagement.framework.server.customreport.CRConstantValues;
import java.util.Date;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.persistence.DataAccessException;
import java.util.LinkedHashMap;
import com.me.devicemanagement.framework.server.reportcriteria.CriteriaColumnValueUtil;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.json.JSONArray;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import java.util.Iterator;
import java.util.Set;
import java.util.HashMap;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ReportCriteriaUtil
{
    protected static ReportCriteriaUtil reportCriteriaUtilBase;
    protected static Logger logger;
    protected static String defaultClass;
    protected static String crDefaultClass;
    private ArrayList customCriteriaReportList;
    
    private ReportCriteriaUtil() {
        this.customCriteriaReportList = new ArrayList();
    }
    
    public void addCustomScheduleReportToList(final String scheduleId) {
        this.customCriteriaReportList.add(scheduleId);
    }
    
    public boolean isCustomScheduleReport(final String scheduleId) {
        if (this.customCriteriaReportList.contains(scheduleId)) {
            this.customCriteriaReportList.remove(scheduleId);
            return true;
        }
        return false;
    }
    
    public static ReportCriteriaUtil getInstance() {
        if (ReportCriteriaUtil.reportCriteriaUtilBase == null) {
            ReportCriteriaUtil.reportCriteriaUtilBase = new ReportCriteriaUtil();
        }
        return ReportCriteriaUtil.reportCriteriaUtilBase;
    }
    
    public List getColumnValues(final Long columnID, final Long viewID, final Map filterMap, final Long loginID) throws Exception {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getColumnValues");
        try {
            String className;
            List searchValues;
            if ((className = this.hasSpecialHandlerClass(columnID)) != null) {
                final CriteriaColumnValue columnValueFetch = (CriteriaColumnValue)Class.forName(className).newInstance();
                searchValues = columnValueFetch.getColumnBrowseValues(columnID, viewID, filterMap, loginID);
            }
            else {
                final List columnIDs = new ArrayList();
                columnIDs.add(columnID);
                final SelectQuery crColumnQuery = this.getCRColumnQuery(columnIDs);
                final Criteria moduleCriteria = new Criteria(Column.getColumn("CRModule", "MODULE_NAME"), (Object)"Inventory", 0, false);
                Criteria criteria = crColumnQuery.getCriteria();
                criteria = ((criteria != null) ? criteria.and(moduleCriteria) : null);
                crColumnQuery.setCriteria(criteria);
                final DataObject columnDO = SyMUtil.getPersistence().get(crColumnQuery);
                if (!columnDO.isEmpty()) {
                    className = ReportCriteriaUtil.crDefaultClass;
                }
                else {
                    className = ReportCriteriaUtil.defaultClass;
                }
                final CriteriaColumnValue columnValueFetch = (CriteriaColumnValue)Class.forName(className).newInstance();
                searchValues = columnValueFetch.getColumnBrowseValues(columnID, viewID, filterMap, loginID);
            }
            return searchValues;
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Exception in fetching browse values", ex);
            throw ex;
        }
    }
    
    public SelectQuery getCRColumnQuery(final List columnIDs) {
        final SelectQuery colQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRModule"));
        final Join subModuleJoin = new Join(Table.getTable("CRModule"), Table.getTable("CRSubModule"), new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2);
        final Join crcolumnJoin = new Join(Table.getTable("CRSubModule"), Table.getTable("CRColumns"), new String[] { "SUB_MODULE_ID" }, new String[] { "SUB_MODULE_ID" }, 2);
        final Criteria columnCrit = new Criteria(Column.getColumn("CRColumns", "COLUMN_ID"), (Object)columnIDs.toArray(), 8);
        colQuery.addSelectColumn(new Column((String)null, "*"));
        colQuery.addJoin(subModuleJoin);
        colQuery.addJoin(crcolumnJoin);
        colQuery.setCriteria(columnCrit);
        return colQuery;
    }
    
    @Deprecated
    public String hasSpecialHandlerClass(final Long columnId) {
        String className = null;
        final ArrayList columnIDs = new ArrayList();
        columnIDs.add(columnId);
        final HashMap handlerClasses = this.getSpecialHandlerClass(columnIDs);
        final Set<String> keys = handlerClasses.keySet();
        for (final String columnName : keys) {
            className = handlerClasses.get(columnName);
        }
        return className;
    }
    
    public HashMap getSpecialHandlerClass(final ArrayList columnIDs) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.hasSpecialHandlerClass");
        final HashMap handlerClasses = new HashMap();
        try {
            if (!columnIDs.isEmpty()) {
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ColumnValueFetchImpl"));
                final Join crColumnJoin = new Join("ColumnValueFetchImpl", "CRColumns", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
                final Column idColumn = Column.getColumn("ColumnValueFetchImpl", "COLUMN_ID");
                final Criteria columnIDsCrit = new Criteria(idColumn, (Object)columnIDs.toArray(), 8);
                query.addSelectColumn(Column.getColumn((String)null, "*"));
                query.addJoin(crColumnJoin);
                query.setCriteria(columnIDsCrit);
                ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil.hasSpecialHandlerClass: {0} ", RelationalAPI.getInstance().getSelectSQL((Query)query));
                final DataObject dataObject = SyMUtil.getPersistence().get(query);
                if (!dataObject.isEmpty()) {
                    final Iterator iterator = dataObject.getRows("ColumnValueFetchImpl");
                    while (iterator.hasNext()) {
                        final Row row = iterator.next();
                        final Long columnID = (Long)row.get("COLUMN_ID");
                        final String className = (String)row.get("IMPL_CLASS_NAME");
                        final Criteria columnIDCrit = new Criteria(idColumn, (Object)columnID, 0);
                        final String columnName = (String)dataObject.getValue("CRColumns", "COLUMN_NAME_ALIAS", columnIDCrit);
                        handlerClasses.put(columnName, className);
                    }
                }
            }
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Exception in fetching special handler class name from db for browse value fetch", ex);
        }
        return handlerClasses;
    }
    
    public boolean hasCustomScheduleCriteria(final String scheduleId) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.hasCustomScheduleCriteria");
        try {
            if (scheduleId == null || scheduleId.equals("")) {
                return false;
            }
            final Column scheduleIdColumn = Column.getColumn("SRToCriteriaRel", "SCHEDULE_REP_ID");
            final Criteria cri = new Criteria(scheduleIdColumn, (Object)Long.valueOf(scheduleId), 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("SRToCriteriaRel"));
            query.addSelectColumn(new Column((String)null, "*"));
            query.setCriteria(cri);
            ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil.hasCustomScheduleCriteria: {0} ", RelationalAPI.getInstance().getSelectSQL((Query)query));
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                return true;
            }
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Exception in fetching custom schedule criteria details", ex);
        }
        return false;
    }
    
    public String getReportName(final String reportId) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getReportName");
        String viewNameValue = "Report";
        try {
            final int reportIdVal = Integer.parseInt(reportId);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewParams"));
            final Column viewNameCol = new Column("ViewParams", "TITLE");
            final Column idColumn = new Column("ViewParams", "VIEW_ID");
            final Criteria viewIdCriteria = new Criteria(new Column("ViewParams", "VIEW_ID"), (Object)reportIdVal, 0);
            query.addSelectColumn(viewNameCol);
            query.addSelectColumn(idColumn);
            query.setCriteria(viewIdCriteria);
            final DataObject viewName = SyMUtil.getPersistence().get(query);
            if (!viewName.isEmpty()) {
                final String viewNameKey = (String)viewName.getFirstRow("ViewParams").get("TITLE");
                viewNameValue = I18N.getMsg(viewNameKey, new Object[0]);
            }
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Exception in getting report name", ex);
        }
        return viewNameValue;
    }
    
    private ArrayList getColumnCategoriesList(final String viewId) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getColumnCategoriesList");
        final RelationalAPI relApi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet categories = null;
        final ArrayList categoriesList = new ArrayList();
        try {
            conn = relApi.getConnection();
            final Column viewCol = Column.getColumn("ViewToCRColumnsRel", "VIEW_ID");
            final Column categoryCol = Column.getColumn("CRColumns", "COLUMN_CATEGORY");
            final Criteria cri = new Criteria(viewCol, (Object)viewId, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewToCRColumnsRel"));
            final Join join = new Join("ViewToCRColumnsRel", "CRColumns", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
            query.addJoin(join);
            query.addSelectColumn(categoryCol);
            query.setDistinct(true);
            query.setCriteria(cri);
            categories = relApi.executeQuery((Query)query, conn);
            while (categories.next()) {
                categoriesList.add(categories.getValue(1));
            }
            ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil.getColumnCategoriesList: search values query string: {0}", RelationalAPI.getInstance().getSelectSQL((Query)query));
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Exception in fetching column category list", ex);
            try {
                if (categories != null) {
                    categories.close();
                }
            }
            catch (final SQLException ex2) {
                ReportCriteriaUtil.logger.log(Level.SEVERE, "Exsception while closing column categories data object", ex2);
            }
            try {
                conn.close();
            }
            catch (final SQLException ex2) {
                ReportCriteriaUtil.logger.log(Level.SEVERE, "Exception in closing DB connection", ex2);
            }
        }
        finally {
            try {
                if (categories != null) {
                    categories.close();
                }
            }
            catch (final SQLException ex3) {
                ReportCriteriaUtil.logger.log(Level.SEVERE, "Exsception while closing column categories data object", ex3);
            }
            try {
                conn.close();
            }
            catch (final SQLException ex3) {
                ReportCriteriaUtil.logger.log(Level.SEVERE, "Exception in closing DB connection", ex3);
            }
        }
        return categoriesList;
    }
    
    public JSONArray buildColumnListJson(final String viewId) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.buildColumnListJson");
        final JSONArray criteriaCols = new JSONArray();
        try {
            ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil.buildColumnListJson: Going to generate criteria for report id: {0}", viewId);
            final Column viewCol = Column.getColumn("ViewToCRColumnsRel", "VIEW_ID");
            Criteria cri = new Criteria(viewCol, (Object)viewId, 0);
            final Join subModuleJoin = new Join("CRColumns", "CRSubModule", new String[] { "SUB_MODULE_ID" }, new String[] { "SUB_MODULE_ID" }, 2);
            final Join moduleJoin = new Join("CRSubModule", "CRModule", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2);
            final Criteria moduleCriteria = new Criteria(new Column("CRModule", "MODULE_NAME"), (Object)"ScheduleReportCriteria", 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewToCRColumnsRel"));
            final Join join = new Join("ViewToCRColumnsRel", "CRColumns", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
            cri = cri.and(moduleCriteria);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.addJoin(join);
            query.addJoin(subModuleJoin);
            query.addJoin(moduleJoin);
            query.setCriteria(cri);
            ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil.buildColumnListJson: Query to fetch criteria columns: {0} ", RelationalAPI.getInstance().getSelectSQL((Query)query));
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            final ArrayList categories = this.getColumnCategoriesList(viewId);
            if (!dataObject.isEmpty()) {
                for (int i = 0; i < categories.size(); ++i) {
                    final JSONObject categoryDetails = new JSONObject();
                    final JSONArray columnsList = new JSONArray();
                    final Object value = categories.get(i);
                    categoryDetails.put("category", (Object)I18N.getMsg((String)value, new Object[0]));
                    final Criteria categoryCriteria = new Criteria(new Column("CRColumns", "COLUMN_CATEGORY"), value, 0);
                    final Iterator iter = dataObject.getRows("CRColumns", categoryCriteria);
                    while (iter.hasNext()) {
                        final JSONObject columnDetails = new JSONObject();
                        final Row tableRows = iter.next();
                        final String colname = (String)tableRows.get("DISPLAY_NAME");
                        final String dataType = (String)tableRows.get("DATA_TYPE");
                        final String colId = String.valueOf(tableRows.get("COLUMN_ID"));
                        final String searchEnabled = String.valueOf(tableRows.get("SEARCH_ENABLED"));
                        final String displayName = I18N.getMsg(colname, new Object[0]);
                        columnDetails.put("displayString", (Object)displayName);
                        columnDetails.put("dataType", (Object)dataType);
                        columnDetails.put("columnId", (Object)colId);
                        columnDetails.put("searchEnabled", (Object)searchEnabled);
                        columnsList.put((Object)columnDetails);
                    }
                    categoryDetails.put("columns", (Object)columnsList);
                    criteriaCols.put((Object)categoryDetails);
                }
                ReportCriteriaUtil.logger.log(Level.FINE, "Criteria columns for " + viewId + " is " + criteriaCols.toString());
                return criteriaCols;
            }
            return null;
        }
        catch (final Exception e) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "Schedule Report :Exception in fetching criteria status", e);
            return null;
        }
    }
    
    public JSONArray buildCriteriaJson(final Long taskId) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.buildCriteriaJson");
        HashMap<Object, Object> reportIdList = new HashMap<Object, Object>();
        final JSONArray allCriteria = new JSONArray();
        try {
            final Column taskIdCol = new Column("ScheduleRepToReportRel", "TASK_ID");
            final Criteria criteria = new Criteria(taskIdCol, (Object)taskId, 0);
            reportIdList = this.getColumnValues("ScheduleRepToReportRel", "SCHEDULE_REP_ID", "REPORT_ID", criteria);
            ReportCriteriaUtil.logger.log(Level.INFO, "ReportCriteriaUtil.buildCriteriaJson: list of reports: {0}", reportIdList);
            for (final Object nextReport : reportIdList.keySet()) {
                final JSONObject reportCritObj = new JSONObject();
                final DataObject dataObject = this.getCriteriaDetailsFromDb(nextReport);
                if (!dataObject.isEmpty()) {
                    final Iterator iter = dataObject.getRows("CriteriaColumnDetails");
                    final JSONArray reportCritList = new JSONArray();
                    while (iter.hasNext()) {
                        final Row tableRows = iter.next();
                        final JSONObject reportCriteria = new JSONObject();
                        final Column colId = new Column("CRColumns", "COLUMN_ID");
                        final Criteria colIdCriteria = new Criteria(colId, tableRows.get("COLUMN_ID"), 0);
                        final String dataType = (String)dataObject.getRow("CRColumns", colIdCriteria).get("DATA_TYPE");
                        final Object colIdValue = tableRows.get("COLUMN_ID");
                        reportCriteria.put("COLUMN_ID", colIdValue);
                        reportCriteria.put("COMPARATOR", tableRows.get("COMPARATOR"));
                        reportCriteria.put("SEARCH_VALUE", tableRows.get("SEARCH_VALUE"));
                        reportCriteria.put("SEARCH_VALUE_2", tableRows.get("SEARCH_VALUE_2"));
                        if (tableRows.get("LOGICAL_OPERATOR") != null) {
                            reportCriteria.put("LOGICAL_OPERATOR", tableRows.get("LOGICAL_OPERATOR"));
                        }
                        reportCriteria.put("CRITERIA_ORDER", tableRows.get("CRITERIA_ORDER"));
                        if (dataType.equalsIgnoreCase("I18N")) {
                            final String[] searchValues = tableRows.get("SEARCH_VALUE").toString().split(Pattern.quote("$@$"));
                            final StringBuilder resultSearchString = new StringBuilder();
                            for (int i = 0; i < searchValues.length; ++i) {
                                resultSearchString.append(I18N.getMsg(searchValues[i], new Object[0]));
                                if (i < searchValues.length - 1) {
                                    resultSearchString.append(",");
                                }
                            }
                            reportCriteria.put("DISPLAY_VALUE", (Object)String.valueOf(resultSearchString));
                        }
                        else if (dataType.equalsIgnoreCase("EQUALONLY")) {
                            SelectQuery searchValueQuery = null;
                            final String[] searchValues2 = tableRows.get("SEARCH_VALUE").toString().split(Pattern.quote("$@$"));
                            final StringBuilder resultSearchString2 = new StringBuilder();
                            final int viewId = reportIdList.get(nextReport).intValue();
                            if ((searchValueQuery = CriteriaColumnValueUtil.getInstance().customBrowseValuesFetchQuery((Long)colIdValue, new Long(viewId), null, false, null)) == null) {
                                final HashMap<Object, Object> transformValue = CriteriaColumnValueUtil.getInstance().getTranformValueList((Long)colIdValue, null);
                                final LinkedHashMap<String, String> browseValuesString = new LinkedHashMap<String, String>();
                                for (final Map.Entry<Object, Object> entry : transformValue.entrySet()) {
                                    try {
                                        browseValuesString.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                                    }
                                    catch (final ClassCastException cce) {
                                        ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while covnerting transformation values to string", cce);
                                    }
                                }
                                for (int j = 0; j < searchValues2.length; ++j) {
                                    resultSearchString2.append(browseValuesString.get(searchValues2[j]));
                                    if (j < searchValues2.length - 1) {
                                        resultSearchString2.append(",");
                                    }
                                }
                                reportCriteria.put("DISPLAY_VALUE", (Object)String.valueOf(resultSearchString2));
                            }
                            else {
                                final LinkedHashMap<Object, Object> browseValues = new LinkedHashMap<Object, Object>();
                                browseValues.putAll(CriteriaColumnValueUtil.getInstance().getBrowseValuesFromDB(searchValueQuery));
                                final LinkedHashMap<String, String> browseValuesString = new LinkedHashMap<String, String>();
                                for (final Map.Entry<Object, Object> entry : browseValues.entrySet()) {
                                    try {
                                        browseValuesString.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                                    }
                                    catch (final ClassCastException cce) {
                                        ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while covnerting transformation values to string", cce);
                                    }
                                }
                                for (int j = 0; j < searchValues2.length; ++j) {
                                    resultSearchString2.append(browseValuesString.get(searchValues2[j]));
                                    if (j < searchValues2.length - 1) {
                                        resultSearchString2.append(", ");
                                    }
                                }
                                reportCriteria.put("DISPLAY_VALUE", (Object)String.valueOf(resultSearchString2));
                            }
                        }
                        else if (dataType.equalsIgnoreCase("DATE")) {
                            final String searchValue = tableRows.get("SEARCH_VALUE").toString();
                            reportCriteria.put("DISPLAY_VALUE", (Object)searchValue);
                        }
                        else if (dataType.equalsIgnoreCase("BOOLEAN")) {
                            final Boolean searchValues3 = Boolean.parseBoolean(tableRows.get("SEARCH_VALUE").toString());
                            if (searchValues3) {
                                reportCriteria.put("DISPLAY_VALUE", (Object)"True");
                            }
                            else if (!searchValues3) {
                                reportCriteria.put("DISPLAY_VALUE", (Object)"False");
                            }
                        }
                        reportCritList.put((Object)reportCriteria);
                    }
                    reportCritObj.put("VIEW_ID", (Object)String.valueOf(reportIdList.get(nextReport).toString()));
                    reportCritObj.put("VALUES", (Object)reportCritList);
                    allCriteria.put((Object)reportCritObj);
                }
            }
            ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil.buildCriteriaJson: Criteria JSON is {0}", allCriteria);
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria: Exception while generating criteria JSON", ex);
        }
        return allCriteria;
    }
    
    public HashMap<Object, Object> getColumnValues(final String table, final String keyColumn, final String valueColumn, final Criteria criteria) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getColumnValues");
        final HashMap<Object, Object> reportIdList = new HashMap<Object, Object>();
        try {
            final Table tableName = new Table(table);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(tableName);
            final Column keyColumnName = new Column(table, keyColumn);
            final Column valueColumnName = new Column(table, valueColumn);
            query.addSelectColumn(keyColumnName);
            query.addSelectColumn(valueColumnName);
            query.setCriteria(criteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Iterator iter = dataObject.getRows(table);
                while (iter.hasNext()) {
                    final Row tableRows = iter.next();
                    final Object keyColVal = tableRows.get(keyColumn);
                    final Object valueColVal = tableRows.get(valueColumn);
                    reportIdList.put(keyColVal, valueColVal);
                }
            }
        }
        catch (final DataAccessException ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria: Exception while fetching criteria column details", (Throwable)ex);
        }
        return reportIdList;
    }
    
    public DataObject getCriteriaDetailsFromDb(final Object scheduleID) {
        return this.getCriteriaDetailsFromDb(scheduleID, null, null, null);
    }
    
    public DataObject getCriteriaDetailsFromDb(final Object scheduleID, final String viewId, final Integer columnFor) {
        return this.getCriteriaDetailsFromDb(scheduleID, viewId, columnFor, null);
    }
    
    public DataObject getCriteriaDetailsFromDb(final Object scheduleID, final String viewId, final Integer columnFor, final String placeHolderName) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getCriteriaDetailsFromDb");
        DataObject dataObject = null;
        try {
            final Column schRepIdCol = Column.getColumn("SRToCriteriaRel", "SCHEDULE_REP_ID");
            final Column orderCol = Column.getColumn("CriteriaColumnDetails", "CRITERIA_ORDER");
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CriteriaColumnDetails"));
            final Join join = new Join("CriteriaColumnDetails", "SRToCriteriaRel", new String[] { "CRITERIA_COLUMN_ID" }, new String[] { "CRITERIA_COLUMN_ID" }, 2);
            final Join join2 = new Join("CriteriaColumnDetails", "CRColumns", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
            Criteria srIdCriteria = new Criteria(schRepIdCol, scheduleID, 0);
            final SortColumn sc = new SortColumn(orderCol, true);
            query.addJoin(join);
            query.addJoin(join2);
            if (viewId != null) {
                final Join viewJoin = new Join("CRColumns", "ViewToCRColumnsRel", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
                Criteria viewCrit = new Criteria(Column.getColumn("ViewToCRColumnsRel", "VIEW_ID"), (Object)viewId, 0);
                query.addJoin(viewJoin);
                final Column colTypeCol = Column.getColumn("ViewToCRColumnsRel", "COLUMN_TYPE");
                if (columnFor == 1) {
                    viewCrit = viewCrit.and(new Criteria(colTypeCol, (Object)new Integer[] { 1, 3 }, 8));
                }
                else if (columnFor == 2) {
                    viewCrit = viewCrit.and(new Criteria(colTypeCol, (Object)new Integer[] { 2, 3 }, 8));
                }
                if (placeHolderName != null) {
                    final Join placeHolderJoin = new Join("ViewToCRColumnsRel", "PlaceHolderToCRColumnRel", new String[] { "VIEW_ID", "COLUMN_ID" }, new String[] { "VIEW_ID", "COLUMN_ID" }, 2);
                    query.addJoin(placeHolderJoin);
                    final Criteria placeHolderCrit = new Criteria(Column.getColumn("PlaceHolderToCRColumnRel", "PLACE_HOLDER"), (Object)placeHolderName, 0);
                    viewCrit = viewCrit.and(placeHolderCrit);
                }
                srIdCriteria = srIdCriteria.and(viewCrit);
            }
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.setCriteria(srIdCriteria);
            query.addSortColumn(sc);
            dataObject = SyMUtil.getPersistence().get(query);
            ReportCriteriaUtil.logger.log(Level.FINE, "Report Criteria: Dataobject for report criteria is {0}", dataObject);
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria: Exception while fetching dataobject for schedule report criteria", ex);
        }
        return dataObject;
    }
    
    public String getScheduleReportCriteriaString(final String scheduleId) {
        return this.getScheduleReportCriteriaString(scheduleId, null);
    }
    
    public String getScheduleReportCriteriaString(final String scheduleId, final String viewId) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getScheduleReportCriteriaString: Adding criteria string for Reports");
        final StringBuilder criteriaString = new StringBuilder();
        try {
            final DataObject dataObject = this.getCriteriaDetailsFromDb(scheduleId, viewId, 1);
            final Iterator iter = dataObject.getRows("CriteriaColumnDetails");
            int dispOrder = new Integer(1);
            while (iter.hasNext()) {
                final Row tableRows = iter.next();
                final String columnID = String.valueOf(tableRows.get("COLUMN_ID"));
                final Column idCol = Column.getColumn("CRColumns", "COLUMN_ID");
                final Criteria columnCriteria = new Criteria(idCol, (Object)columnID, 0);
                final Row value = dataObject.getRow("CRColumns", columnCriteria);
                final String tableName = String.valueOf(value.get("TABLE_NAME_ALIAS"));
                final String columnName = String.valueOf(value.get("COLUMN_NAME_ALIAS"));
                final String dataType = String.valueOf(value.get("DATA_TYPE"));
                final String comparator = String.valueOf(tableRows.get("COMPARATOR"));
                final String searchValue = String.valueOf(tableRows.get("SEARCH_VALUE"));
                final StringBuilder criteria = new StringBuilder();
                criteria.append(" ");
                criteria.append("(");
                criteria.append(this.buildCriteria(tableName, columnName, dataType, comparator, searchValue));
                criteria.append(")");
                if (dispOrder == 1) {
                    criteriaString.append(criteria.toString());
                }
                else if (dispOrder > 1) {
                    final int logicalOperand = (int)tableRows.get("LOGICAL_OPERATOR");
                    if (logicalOperand == 1) {
                        criteriaString.append(" ");
                        criteriaString.append("AND");
                    }
                    else if (logicalOperand == 2) {
                        criteriaString.append(" ");
                        criteriaString.append("OR");
                    }
                    criteriaString.append((CharSequence)criteria);
                }
                ++dispOrder;
            }
            ReportCriteriaUtil.logger.log(Level.INFO, "Criteria set by user for schedule report {0} is {1}", new String[] { scheduleId, criteriaString.toString() });
            return criteriaString.toString();
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria: Exception while generating criteia string for schedule report: " + scheduleId, ex);
            return "";
        }
    }
    
    public String buildCriteria(final String tableName, final String columnName, final String dataType, final String comparator, final String searchValue) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.buildCriteria");
        final StringBuilder criteria = new StringBuilder();
        if (dataType.equalsIgnoreCase("DATE")) {
            return this.buildDateCriteria(tableName, columnName, comparator, searchValue, dataType);
        }
        final String comparatorString = this.getOperatorToken(comparator, dataType);
        Boolean isNullhandlingRequired = Boolean.FALSE;
        String newSearchString = "";
        if (comparatorString.equalsIgnoreCase("in") || comparatorString.equalsIgnoreCase("not in")) {
            final Object[] searchValues = searchValue.split(Pattern.quote("$@$"));
            newSearchString = this.getNoNullSearchString(searchValues);
            int noNullSearchLength = 0;
            if (!newSearchString.equalsIgnoreCase("")) {
                final Object[] noNullSearchValues = newSearchString.split(Pattern.quote("$@$"));
                noNullSearchLength = noNullSearchValues.length;
            }
            if (searchValues.length != noNullSearchLength) {
                isNullhandlingRequired = Boolean.TRUE;
            }
        }
        if (!isNullhandlingRequired) {
            if (!tableName.equalsIgnoreCase("")) {
                criteria.append(tableName).append(".");
            }
            criteria.append(columnName);
            criteria.append(" ");
            criteria.append(comparatorString);
            criteria.append(" ");
            ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil.getScheduleReportCriteriaString: Search operator:{0} and search value:{1}", new String[] { comparatorString, searchValue });
            criteria.append(getSearchString(searchValue, comparator, dataType));
        }
        else {
            if (!tableName.equalsIgnoreCase("")) {
                criteria.append(tableName).append(".");
            }
            criteria.append(columnName);
            criteria.append(" ");
            if (comparatorString.equalsIgnoreCase("in")) {
                criteria.append("is");
            }
            else {
                criteria.append("is not");
            }
            criteria.append(" ");
            ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil.getScheduleReportCriteriaString: Search operator:{0} and search value:{1}", new String[] { comparatorString, searchValue });
            criteria.append("null");
            if (!newSearchString.equalsIgnoreCase("")) {
                criteria.append(" ");
                if (comparatorString.equalsIgnoreCase("in")) {
                    criteria.append("OR");
                }
                else {
                    criteria.append("AND");
                }
                criteria.append(" ");
                criteria.append(tableName).append(".");
                criteria.append(columnName);
                criteria.append(" ");
                criteria.append(comparatorString);
                criteria.append(" ");
                ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil.getScheduleReportCriteriaString: Search operator:{0} and search value:{1}", new String[] { comparatorString, searchValue });
                criteria.append(getSearchString(newSearchString, comparator, dataType));
            }
        }
        return criteria.toString();
    }
    
    public String buildDateCriteria(final String tableName, final String columnName, final String criteriaType, final String searchValue, final String dataType) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.buildDateCriteria");
        final StringBuilder dateCriteria = new StringBuilder();
        if (this.isPredefinedDateRange(searchValue)) {
            try {
                final Hashtable<String, Long> dateRange = DateTimeUtil.determine_From_To_Times(searchValue);
                final Long fromDate = dateRange.get("date1");
                final Long toDate = dateRange.get("date2");
                dateCriteria.append(this.dateRangeString(fromDate, toDate, tableName, columnName));
            }
            catch (final Exception ex) {
                ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria: Exception while appending predefined date range criteria string for schedule report query", ex);
            }
        }
        else {
            dateCriteria.append(this.customDateCriteriaString(tableName, columnName, criteriaType, searchValue, dataType));
        }
        ReportCriteriaUtil.logger.log(Level.FINE, "Date Range Criteria: {0}", dateCriteria.toString());
        return dateCriteria.toString();
    }
    
    private String customDateCriteriaString(final String tableName, final String columnName, String comparator, final String searchValue, final String dataType) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.customDateCriteriaString");
        comparator = comparator.trim();
        final StringBuilder dateCriteria = new StringBuilder();
        Date date = new Date();
        if (!comparator.equalsIgnoreCase("Last n Days") || !comparator.equalsIgnoreCase("Before n Days") || !comparator.equalsIgnoreCase("Next N Days")) {
            new DateTimeUtil();
            date = DateTimeUtil.getDateFromString(searchValue.trim(), "yyyy-MM-dd");
        }
        if (comparator.equalsIgnoreCase("is")) {
            final Long fromDate = date.getTime();
            new DateTimeUtil();
            final String nextDayString = DateTimeUtil.increment_Decrement_Dates(searchValue, 1L, "yyyy-MM-dd");
            new DateTimeUtil();
            final Date nextDay = DateTimeUtil.getDateFromString(nextDayString.trim(), "yyyy-MM-dd");
            final Long toDate = nextDay.getTime() - 1L;
            dateCriteria.append(this.dateRangeString(fromDate, toDate, tableName, columnName));
        }
        else if (comparator.equalsIgnoreCase("Last n Days")) {
            final int noOfDays = Integer.valueOf(searchValue);
            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, true);
            final Long fromDate2 = dateRange.get("fromDate");
            final Long toDate = dateRange.get("toDate");
            dateCriteria.append(this.dateRangeString(fromDate2, toDate, tableName, columnName));
        }
        else if (comparator.equalsIgnoreCase("Before n Days")) {
            final int noOfDays = Integer.valueOf(searchValue);
            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, true);
            final Long fromDate2 = dateRange.get("fromDate");
            if (!tableName.equalsIgnoreCase("")) {
                dateCriteria.append(tableName).append(".");
            }
            dateCriteria.append(columnName);
            dateCriteria.append(" ");
            dateCriteria.append("less than");
            dateCriteria.append(" ");
            dateCriteria.append(fromDate2);
        }
        else if (comparator.equalsIgnoreCase("Next N Days")) {
            final int noOfDays = Integer.valueOf(searchValue);
            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, false);
            final Long fromDate2 = dateRange.get("fromDate");
            final Long toDate = dateRange.get("toDate");
            dateCriteria.append(this.dateRangeString(fromDate2, toDate, tableName, columnName));
        }
        else {
            if (!tableName.equalsIgnoreCase("")) {
                dateCriteria.append(tableName).append(".");
            }
            dateCriteria.append(columnName);
            dateCriteria.append(" ");
            dateCriteria.append(this.getOperatorToken(comparator, dataType));
            dateCriteria.append(" ");
            if (comparator.equalsIgnoreCase("Before")) {
                dateCriteria.append(date.getTime());
            }
            else if (comparator.equalsIgnoreCase("After")) {
                new DateTimeUtil();
                final String nextDayString2 = DateTimeUtil.increment_Decrement_Dates(searchValue, 1L, "yyyy-MM-dd");
                new DateTimeUtil();
                final Date nextDay2 = DateTimeUtil.getDateFromString(nextDayString2.trim(), "yyyy-MM-dd");
                final Long dateMax = nextDay2.getTime() - 1L;
                dateCriteria.append(dateMax);
            }
        }
        return dateCriteria.toString();
    }
    
    private String dateRangeString(final Long fromDate, final Long toDate, final String tableName, final String columnName) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.dateRangeString");
        final StringBuilder dateCriteria = new StringBuilder();
        if (!tableName.equalsIgnoreCase("")) {
            dateCriteria.append(tableName).append(".");
        }
        dateCriteria.append(columnName);
        dateCriteria.append(" ");
        dateCriteria.append(">=");
        dateCriteria.append(" ");
        dateCriteria.append(fromDate);
        dateCriteria.append(" ");
        dateCriteria.append("AND");
        dateCriteria.append(" ");
        if (!tableName.equalsIgnoreCase("")) {
            dateCriteria.append(tableName).append(".");
        }
        dateCriteria.append(columnName);
        dateCriteria.append(" ");
        dateCriteria.append("<=");
        dateCriteria.append(" ");
        dateCriteria.append(toDate);
        return dateCriteria.toString();
    }
    
    public String getOperatorToken(String value, final String dataType) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getOperatorToken");
        value = value.trim();
        if (value.equalsIgnoreCase("equal")) {
            if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("I18N") || dataType.equalsIgnoreCase("EQUALONLY")) {
                return "in";
            }
            return "=";
        }
        else {
            if (value.equalsIgnoreCase("empty")) {
                return "=";
            }
            if (value.equalsIgnoreCase("not equal")) {
                if (dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("I18N") || dataType.equalsIgnoreCase("EQUALONLY")) {
                    return "not in";
                }
                return "!=";
            }
            else {
                if (value.equalsIgnoreCase("not empty")) {
                    return "!=";
                }
                if (value.equalsIgnoreCase("greater than")) {
                    return ">";
                }
                if (value.equalsIgnoreCase("greater or equal")) {
                    return ">=";
                }
                if (value.equalsIgnoreCase("less than")) {
                    return "<";
                }
                if (value.equalsIgnoreCase("less or equal")) {
                    return "<=";
                }
                if (value.equalsIgnoreCase("like")) {
                    return "like";
                }
                if (value.equalsIgnoreCase("contains")) {
                    return "like";
                }
                if (value.equalsIgnoreCase("not like")) {
                    return "not like";
                }
                if (value.equalsIgnoreCase("starts with")) {
                    return "like";
                }
                if (value.equalsIgnoreCase("ends with")) {
                    return "like";
                }
                if (value.equalsIgnoreCase("INNER_JOIN")) {
                    return "INNER JOIN";
                }
                if (value.equalsIgnoreCase("LEFT_JOIN")) {
                    return "LEFT JOIN";
                }
                if (value.equalsIgnoreCase("Before")) {
                    return "<";
                }
                if (value.equalsIgnoreCase("After")) {
                    return ">";
                }
                return "";
            }
        }
    }
    
    public Criteria getReportCriteria(final String scheduleID) {
        return this.getReportCriteria(scheduleID, null, null);
    }
    
    public Criteria getReportCriteria(final String scheduleID, final String viewId, final Integer criteriaFor) {
        ReportCriteriaUtil.logger.log(Level.INFO, "Entered ReportCriteriaUtil.getReportCriteria for schedule id: {0}", scheduleID);
        Criteria criteria = null;
        Criteria multipleCriteria = null;
        try {
            final DataObject dataObject = this.getCriteriaDetailsFromDb(scheduleID, viewId, criteriaFor);
            final Iterator iter = dataObject.getRows("CriteriaColumnDetails");
            while (iter.hasNext()) {
                final Row tableRows = iter.next();
                final String columnID = String.valueOf(tableRows.get("COLUMN_ID"));
                final Column idCol = Column.getColumn("CRColumns", "COLUMN_ID");
                final Criteria columnCriteria = new Criteria(idCol, (Object)columnID, 0);
                final Row value = dataObject.getRow("CRColumns", columnCriteria);
                final String tableName = String.valueOf(value.get("TABLE_NAME_ALIAS"));
                final String columnName = String.valueOf(value.get("COLUMN_NAME_ALIAS"));
                final String dataType = String.valueOf(value.get("DATA_TYPE"));
                final String criteriaType = String.valueOf(tableRows.get("COMPARATOR"));
                final String searchValue = String.valueOf(tableRows.get("SEARCH_VALUE"));
                String searchString = CRConstantValues.getSearchString(searchValue, criteriaType).toString();
                searchString = (String)DCViewFilterUtil.getInstance().alterSearchvalue(searchString, criteriaType);
                final int dispOrder = (int)tableRows.get("CRITERIA_ORDER");
                final Column viewColumn = new Column(tableName, columnName);
                Criteria viewCriteria;
                if (dataType.equalsIgnoreCase("DATE")) {
                    viewCriteria = this.getDateRangeCriteria(viewColumn, searchValue, criteriaType);
                    ReportCriteriaUtil.logger.log(Level.FINE, "getScheduleReportCriteria: Date Range Criteria: {0}", viewCriteria);
                }
                else if ((dataType.equalsIgnoreCase("CHAR") || dataType.equalsIgnoreCase("I18N") || dataType.equalsIgnoreCase("EQUALONLY")) && (criteriaType.equalsIgnoreCase("equal") || criteriaType.equalsIgnoreCase("not equal"))) {
                    viewCriteria = this.getCriteriaForMultipleSearchValues(viewColumn, searchString, criteriaType);
                }
                else {
                    viewCriteria = new Criteria(viewColumn, (Object)searchString, CRConstantValues.getOperatorValue(criteriaType), (boolean)Boolean.FALSE);
                }
                if (dispOrder == 1) {
                    multipleCriteria = viewCriteria;
                }
                else {
                    if (dispOrder <= 1) {
                        continue;
                    }
                    final int logicalOperand = (int)tableRows.get("LOGICAL_OPERATOR");
                    if (logicalOperand == 1) {
                        if (multipleCriteria != null) {
                            multipleCriteria = multipleCriteria.and(viewCriteria);
                        }
                        else {
                            multipleCriteria = viewCriteria;
                        }
                    }
                    else {
                        if (logicalOperand != 2) {
                            continue;
                        }
                        if (multipleCriteria != null) {
                            multipleCriteria = multipleCriteria.or(viewCriteria);
                        }
                        else {
                            multipleCriteria = viewCriteria;
                        }
                    }
                }
            }
            if (criteria == null) {
                criteria = multipleCriteria;
            }
            else {
                criteria = criteria.and(multipleCriteria);
            }
            ReportCriteriaUtil.logger.log(Level.INFO, "Criteria set by user for schedule report {0} is {1}", new String[] { scheduleID, String.valueOf(criteria) });
            return criteria;
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Crtieria: Exception while generating report Criteria object", ex);
            return null;
        }
    }
    
    private Criteria getCriteriaForMultipleSearchValues(final Column viewColumn, final String searchString, final String criteriaType) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getCriteriaForMultipleSearchValues");
        Criteria criteria = null;
        int operatorType = 0;
        if (criteriaType.equalsIgnoreCase("equal")) {
            operatorType = 8;
        }
        else if (criteriaType.equalsIgnoreCase("not equal")) {
            operatorType = 9;
        }
        final Object[] searchValues = searchString.split(Pattern.quote("$@$"));
        ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil.getCriteriaForMultipleSearchValues(): multiple search values: {0}", searchValues);
        final String newSearchString = this.getNoNullSearchString(searchValues);
        int noNullSearchLength = 0;
        Object[] noNullSearchValues = null;
        if (!newSearchString.equalsIgnoreCase("")) {
            noNullSearchValues = newSearchString.split(Pattern.quote("$@$"));
            noNullSearchLength = noNullSearchValues.length;
        }
        if (searchValues.length == noNullSearchLength) {
            criteria = new Criteria(viewColumn, (Object)searchValues, operatorType, (boolean)Boolean.FALSE);
        }
        else {
            criteria = new Criteria(viewColumn, (Object)null, 0);
            if (noNullSearchLength > 0) {
                if (operatorType == 8) {
                    criteria = criteria.or(new Criteria(viewColumn, (Object)noNullSearchValues, operatorType, (boolean)Boolean.FALSE));
                }
                else if (operatorType == 9) {
                    criteria = criteria.and(new Criteria(viewColumn, (Object)noNullSearchValues, operatorType, (boolean)Boolean.FALSE));
                }
            }
        }
        return criteria;
    }
    
    private String getNoNullSearchString(final Object[] searchValues) {
        String newSearchString = "";
        try {
            for (int i = 0; i < searchValues.length; ++i) {
                final String searchValue = (String)searchValues[i];
                if (!searchValue.equalsIgnoreCase("null")) {
                    if (newSearchString.equals("")) {
                        newSearchString = (String)searchValues[i];
                    }
                    else {
                        newSearchString = newSearchString + "$@$" + (String)searchValues[i];
                    }
                }
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return newSearchString;
    }
    
    public ArrayList getCriteriaColumnList(final String viewId) {
        ReportCriteriaUtil.logger.log(Level.INFO, "ReportCriteriaUtil.getCriteriaColumnList");
        final ArrayList<String> criteriaCols = new ArrayList<String>();
        try {
            ReportCriteriaUtil.logger.log(Level.INFO, "ReportCriteria :Entered into ReportCriteriaUtil.getCriteriaColumnList()for reportId {0}", viewId);
            final Column viewCol = Column.getColumn("ViewToCRColumnsRel", "VIEW_ID");
            final Criteria cri = new Criteria(viewCol, (Object)viewId, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewToCRColumnsRel"));
            final Join join = new Join("ViewToCRColumnsRel", "CRColumns", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.addJoin(join);
            query.setCriteria(cri);
            ReportCriteriaUtil.logger.log(Level.FINE, "Query: getCriteriaColumnList " + RelationalAPI.getInstance().getSelectSQL((Query)query));
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Iterator iter = dataObject.getRows("CRColumns");
                while (iter.hasNext()) {
                    final Row tableRows = iter.next();
                    final String colname = (String)tableRows.get("DISPLAY_NAME");
                    final String dataType = (String)tableRows.get("DATA_TYPE");
                    final String colId = String.valueOf(tableRows.get("COLUMN_ID"));
                    final String displayName = I18N.getMsg(colname, new Object[0]);
                    final String value = displayName + "::" + dataType + "::" + colId;
                    criteriaCols.add(value);
                }
                ReportCriteriaUtil.logger.log(Level.FINE, "All criteria columns from getCriteriaColumnList " + criteriaCols.toString());
                return criteriaCols;
            }
            return null;
        }
        catch (final Exception e) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "Schedule Report :Exception in fetching criteria status", e);
            return null;
        }
    }
    
    public HashMap<Long, ArrayList<HashMap<String, String>>> convertCriteriaJsonToHash(final JSONArray criteriaString) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.convertCriteriaJsonToHash");
        try {
            ReportCriteriaUtil.logger.log(Level.FINE, "Schedule Report.criteriaString :{0}", criteriaString.toString());
            final HashMap<Long, ArrayList<HashMap<String, String>>> criteriaColumnMap = new HashMap<Long, ArrayList<HashMap<String, String>>>();
            for (int i = 0; i < criteriaString.length(); ++i) {
                final JSONObject criteria = criteriaString.getJSONObject(i);
                final String keyVal = String.valueOf(criteria.get("VIEW_ID"));
                final Long viewId = Long.parseLong(keyVal);
                final JSONArray criteriaCols = criteria.getJSONArray("VALUES");
                final ArrayList<HashMap<String, String>> critArray = new ArrayList<HashMap<String, String>>();
                for (int j = 0; j < criteriaCols.length(); ++j) {
                    final HashMap<String, String> critVal = new HashMap<String, String>();
                    final JSONObject colValues = criteriaCols.getJSONObject(j);
                    final Iterator keys = colValues.keys();
                    while (keys.hasNext()) {
                        final String val = keys.next().toString();
                        critVal.put(val, colValues.get(val).toString());
                    }
                    critArray.add(critVal);
                }
                criteriaColumnMap.put(viewId, critArray);
                ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil.criteriaColumnMap :{0}", criteriaColumnMap.toString());
            }
            return criteriaColumnMap;
        }
        catch (final Exception e) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "ReportCriteriaUtil.getReportwiseCriteria :Exception in processing criteria status{0}", e);
            return null;
        }
    }
    
    public JSONArray getColumnBrowseValues(final String columnId) throws IOException {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getColumnBrowseValues");
        final JSONArray searchValues = new JSONArray();
        JSONObject searchData = null;
        final Object colId = columnId;
        final Long column = Long.valueOf(columnId);
        final Column idColumn = new Column("CRColumns", "COLUMN_ID");
        final Column tableNameColumn = new Column("CRColumns", "TABLE_NAME");
        final Column columnNameColumn = new Column("CRColumns", "COLUMN_NAME");
        final Criteria columnIdCriteria = new Criteria(idColumn, colId, 0);
        final SelectQuery columnDetailsQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRColumns"));
        columnDetailsQuery.addSelectColumn(idColumn);
        columnDetailsQuery.addSelectColumn(tableNameColumn);
        columnDetailsQuery.addSelectColumn(columnNameColumn);
        columnDetailsQuery.setCriteria(columnIdCriteria);
        Connection conn = null;
        DataSet ds = null;
        try {
            final DataObject dataObject = SyMUtil.getPersistence().get(columnDetailsQuery);
            ReportCriteriaUtil.logger.log(Level.INFO, "ReportCriteriaUtil.getColumnBrowseValues: crcolumn query string: {0}", RelationalAPI.getInstance().getSelectSQL((Query)columnDetailsQuery));
            if (!dataObject.isEmpty()) {
                final Iterator iterator = dataObject.getRows("CRColumns");
                Row columnDetails = null;
                if (iterator.hasNext()) {
                    columnDetails = iterator.next();
                    final String tableName = String.valueOf(columnDetails.get("TABLE_NAME"));
                    final String columnName = String.valueOf(columnDetails.get("COLUMN_NAME"));
                    final SortColumn sortColumn = new SortColumn(Column.getColumn(tableName, columnName), true);
                    final SelectQuery searchValueQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
                    searchValueQuery.addSelectColumn(Column.getColumn(tableName, columnName));
                    searchValueQuery.setDistinct(true);
                    final RelationalAPI relApi = RelationalAPI.getInstance();
                    conn = relApi.getConnection();
                    ds = relApi.executeQuery((Query)searchValueQuery, conn);
                    ReportCriteriaUtil.logger.log(Level.INFO, "getSearchValues: search values query string: {0}", RelationalAPI.getInstance().getSelectSQL((Query)searchValueQuery));
                    ReportCriteriaUtil.logger.log(Level.INFO, "getSearchValues: Browse value's table,column :" + tableName + columnName);
                    while (ds.next()) {
                        final Object value = ds.getValue(1);
                        searchData = new JSONObject();
                        searchData.put("searchValue", value);
                        searchData.put("displayValue", (Object)I18N.getMsg(value.toString(), new Object[0]));
                        searchValues.put((Object)searchData);
                        ReportCriteriaUtil.logger.log(Level.FINE, "getSearchValues: search values : {0}", searchValues.toString());
                    }
                    ds.close();
                }
            }
        }
        catch (final Exception e) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "Schedule Report :Exception in fetching criteria search values", e);
            try {
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final Exception ex) {
                ReportCriteriaUtil.logger.log(Level.SEVERE, "Exception while retrieving search values ", ex);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex) {
                ReportCriteriaUtil.logger.log(Level.SEVERE, "Exception while retrieving search values ", ex);
            }
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final Exception ex2) {
                ReportCriteriaUtil.logger.log(Level.SEVERE, "Exception while retrieving search values ", ex2);
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex2) {
                ReportCriteriaUtil.logger.log(Level.SEVERE, "Exception while retrieving search values ", ex2);
            }
        }
        return searchValues;
    }
    
    public static Object getSearchString(Object searchString, String operatorValue, final String dataType) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getSearchString");
        operatorValue = operatorValue.trim();
        if (operatorValue.equalsIgnoreCase("like")) {
            searchString = "'%" + searchString + "%'";
        }
        else if (operatorValue.equalsIgnoreCase("not like")) {
            searchString = "'%" + searchString + "%'";
        }
        else if (operatorValue.equalsIgnoreCase("starts with")) {
            searchString = "'" + searchString + "%'";
        }
        else if (operatorValue.equalsIgnoreCase("ends with")) {
            searchString = "'%" + searchString + "'";
        }
        else if (operatorValue.equalsIgnoreCase("contains")) {
            searchString = "'%" + searchString + "%'";
        }
        else if (operatorValue.equalsIgnoreCase("in") || operatorValue.equalsIgnoreCase("not in") || operatorValue.equalsIgnoreCase("equal") || operatorValue.equalsIgnoreCase("not equal")) {
            final String[] searchValues = searchString.toString().split(Pattern.quote("$@$"));
            final StringBuilder multipleSearchValues = new StringBuilder();
            multipleSearchValues.append("(");
            for (int i = 0; i < searchValues.length; ++i) {
                multipleSearchValues.append("'");
                multipleSearchValues.append(searchValues[i].trim());
                multipleSearchValues.append("'");
                if (i != searchValues.length - 1) {
                    multipleSearchValues.append(",");
                }
            }
            multipleSearchValues.append(")");
            searchString = multipleSearchValues;
        }
        else if (operatorValue.equalsIgnoreCase("empty") || operatorValue.equalsIgnoreCase("not empty")) {
            searchString = "''";
        }
        return searchString;
    }
    
    public boolean isPredefinedDateRange(final String value) {
        final ArrayList predefDateRange = new ArrayList();
        predefDateRange.add("today");
        predefDateRange.add("yesterday");
        predefDateRange.add("current_week");
        predefDateRange.add("this_week");
        predefDateRange.add("last_week");
        predefDateRange.add("current_month");
        predefDateRange.add("this_month");
        predefDateRange.add("last_month");
        predefDateRange.add("current_quarter");
        predefDateRange.add("last_quarter");
        return predefDateRange.contains(value);
    }
    
    public String predefinedDateRangeText(final String value) {
        final HashMap<String, String> predefDateRange = new HashMap<String, String>();
        predefDateRange.put("today", "Today");
        predefDateRange.put("yesterday", "Yesterday");
        predefDateRange.put("current_week", "Current Week");
        predefDateRange.put("this_week", "This Week");
        predefDateRange.put("last_week", "Last Week");
        predefDateRange.put("current_month", "Current Month");
        predefDateRange.put("this_month", "This Month");
        predefDateRange.put("last_month", "Last Month");
        predefDateRange.put("current_quarter", "Current Quarter");
        predefDateRange.put("last_quarter", "Last Quarter");
        return predefDateRange.get(value);
    }
    
    public ArrayList getPlaceHolderCriteriaStringWithComparator(final String isScheduleReport, final String scheduleId, final String viewId, final String placeHolderName) throws DataAccessException {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getPlaceHolderCriteriaString");
        String comparatorModified = "";
        final ArrayList criteriaWithComparator = new ArrayList();
        try {
            if (isScheduleReport.equalsIgnoreCase("true") && !ReportCriteriaUtil.reportCriteriaUtilBase.isCustomScheduleReport(scheduleId)) {
                ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getScheduleReportCriteriaString: Adding criteria string for Reports");
                final StringBuilder criteriaString = new StringBuilder();
                final DataObject dataObject = this.getCriteriaDetailsFromDb(scheduleId, viewId, 2, placeHolderName);
                final Iterator iter = dataObject.getRows("CriteriaColumnDetails");
                final Row tableRows = iter.next();
                final String columnID = String.valueOf(tableRows.get("COLUMN_ID"));
                final Column idCol = Column.getColumn("CRColumns", "COLUMN_ID");
                final Criteria columnCriteria = new Criteria(idCol, (Object)columnID, 0);
                final Row value = dataObject.getRow("CRColumns", columnCriteria);
                final Column placeHolderCol = Column.getColumn("PlaceHolderToCRColumnRel", "COLUMN_ID");
                final Criteria placeHolderColCrit = new Criteria(placeHolderCol, (Object)columnID, 0);
                final Row placeHolderVal = dataObject.getRow("PlaceHolderToCRColumnRel", placeHolderColCrit);
                final String columnAlias = String.valueOf(placeHolderVal.get("COLUMN_ALIAS"));
                final String dataType = String.valueOf(value.get("DATA_TYPE"));
                final String comparator = String.valueOf(tableRows.get("COMPARATOR"));
                if (comparator.equalsIgnoreCase("not equal")) {
                    comparatorModified = "equal";
                }
                else if (comparator.equalsIgnoreCase("not like")) {
                    comparatorModified = "like";
                }
                else {
                    comparatorModified = comparator;
                }
                final String searchValue = String.valueOf(tableRows.get("SEARCH_VALUE"));
                final StringBuilder criteria = new StringBuilder();
                criteria.append(" ");
                criteria.append("(");
                criteria.append(this.buildCriteria("", columnAlias, dataType, comparatorModified, searchValue));
                criteria.append(")");
                criteriaString.append(criteria.toString());
                criteriaWithComparator.add(criteriaString);
                if (comparator.equalsIgnoreCase("not equal") || comparator.equalsIgnoreCase("not like")) {
                    criteriaWithComparator.add("NOT IN");
                }
                else {
                    criteriaWithComparator.add("IN");
                }
                ReportCriteriaUtil.logger.log(Level.INFO, "Criteria set by user for schedule report {0} is {1}", new String[] { scheduleId, criteriaString.toString() });
                return criteriaWithComparator;
            }
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria: Exception while generating criteia string for schedule report: " + scheduleId, ex);
        }
        criteriaWithComparator.add("");
        return criteriaWithComparator;
    }
    
    public String getPlaceHolderCriteriaString(final String isScheduleReport, final String scheduleId, final String viewId, final String placeHolderName) {
        return this.getPlaceHolderCriteriaString(isScheduleReport, scheduleId, viewId, placeHolderName, Boolean.TRUE);
    }
    
    public String getPlaceHolderCriteriaString(final String isScheduleReport, final String scheduleId, final String viewId, final String placeHolderName, final Boolean isNativeQuery) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getPlaceHolderCriteriaString");
        try {
            if (isScheduleReport.equalsIgnoreCase("true") && !ReportCriteriaUtil.reportCriteriaUtilBase.isCustomScheduleReport(scheduleId)) {
                ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getScheduleReportCriteriaString: Adding criteria string for Reports");
                final StringBuilder criteriaString = new StringBuilder();
                try {
                    DataObject dataObject;
                    if (isNativeQuery) {
                        dataObject = this.getCriteriaDetailsFromDb(scheduleId, viewId, 2, placeHolderName);
                    }
                    else {
                        dataObject = this.getCriteriaDetailsFromDb(scheduleId, viewId, 1, placeHolderName);
                    }
                    final Iterator iter = dataObject.getRows("CriteriaColumnDetails");
                    int dispOrder = new Integer(1);
                    while (iter.hasNext()) {
                        final Row tableRows = iter.next();
                        final String columnID = String.valueOf(tableRows.get("COLUMN_ID"));
                        final Column idCol = Column.getColumn("CRColumns", "COLUMN_ID");
                        final Criteria columnCriteria = new Criteria(idCol, (Object)columnID, 0);
                        final Row value = dataObject.getRow("CRColumns", columnCriteria);
                        final Column placeHolderCol = Column.getColumn("PlaceHolderToCRColumnRel", "COLUMN_ID");
                        final Criteria placeHolderColCrit = new Criteria(placeHolderCol, (Object)columnID, 0);
                        final Row placeHolderVal = dataObject.getRow("PlaceHolderToCRColumnRel", placeHolderColCrit);
                        final String columnAlias = String.valueOf(placeHolderVal.get("COLUMN_ALIAS"));
                        final String dataType = String.valueOf(value.get("DATA_TYPE"));
                        final String comparator = String.valueOf(tableRows.get("COMPARATOR"));
                        final String searchValue = String.valueOf(tableRows.get("SEARCH_VALUE"));
                        final StringBuilder criteria = new StringBuilder();
                        criteria.append(" ");
                        criteria.append("(");
                        criteria.append(this.buildCriteria("", columnAlias, dataType, comparator, searchValue));
                        criteria.append(")");
                        if (dispOrder == 1) {
                            criteriaString.append(criteria.toString());
                        }
                        else if (dispOrder > 1) {
                            final int logicalOperand = (int)tableRows.get("LOGICAL_OPERATOR");
                            if (logicalOperand == 1) {
                                criteriaString.append(" ");
                                criteriaString.append("AND");
                            }
                            else if (logicalOperand == 2) {
                                criteriaString.append(" ");
                                criteriaString.append("OR");
                            }
                            criteriaString.append((CharSequence)criteria);
                        }
                        ++dispOrder;
                    }
                    ReportCriteriaUtil.logger.log(Level.INFO, "Criteria set by user for schedule report {0} is {1}", new String[] { scheduleId, criteriaString.toString() });
                    return criteriaString.toString();
                }
                catch (final Exception ex) {
                    ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria: Exception while generating criteia string for schedule report: " + scheduleId, ex);
                    return "";
                }
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return "";
    }
    
    public String appendCriteriaToNativeQuery(final String isScheduleReport, final String scheduleId, final String query, final String viewId) throws Exception {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.appendCriteriaToNativeQuery");
        final StringBuilder sqlBuilder = new StringBuilder(query);
        try {
            if (isScheduleReport.equalsIgnoreCase("true") && !ReportCriteriaUtil.reportCriteriaUtilBase.isCustomScheduleReport(scheduleId)) {
                ReportCriteriaUtil.logger.log(Level.INFO, "ReportCriteriaUtil.appendCriteriaToNativeQuery: {0} schedule has schedule criteria set by user", scheduleId);
                ReportCriteriaUtil.logger.log(Level.INFO, "ReportCriteriaUtil.appendCriteriaToNativeQuery: Query before setting user criteria: {0} ", sqlBuilder.toString());
                final String criteriaString = this.getScheduleReportCriteriaString(scheduleId, viewId);
                if (!criteriaString.equalsIgnoreCase("")) {
                    if (query.contains("where") || query.contains("WHERE") || query.contains("Where")) {
                        sqlBuilder.append(" ");
                        sqlBuilder.append("AND");
                        sqlBuilder.append(" ");
                        sqlBuilder.append(criteriaString);
                    }
                    else {
                        final int whereIndex = sqlBuilder.toString().length() + 1;
                        sqlBuilder.append(" ");
                        sqlBuilder.append("WHERE");
                        sqlBuilder.append(criteriaString);
                    }
                }
                ReportCriteriaUtil.logger.log(Level.INFO, "Query after setting user criteria is : {0}", sqlBuilder.toString());
            }
        }
        catch (final Exception e) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "ReportCriteriaUtil.appendCriteriaToNativeQuery: Exception : {0}", e.getMessage());
        }
        return sqlBuilder.toString();
    }
    
    public String getVariableValueForScheduledView(final String isScheduleReport, final String scheduleId, final String viewId, final String placeHolderName) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getVariableValueForScheduledView");
        String criteria = null;
        try {
            if (isScheduleReport.equalsIgnoreCase("true") && !ReportCriteriaUtil.reportCriteriaUtilBase.isCustomScheduleReport(scheduleId) && this.isVaraibleHandlingRequired(viewId, placeHolderName)) {
                criteria = this.getPlaceHolderCriteriaString(isScheduleReport, scheduleId, viewId, placeHolderName);
            }
        }
        catch (final Exception ee) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "ReportCriteriaUtil.getVariableValueForScheduledView: Exception : {0}", ee.getMessage());
        }
        return null;
    }
    
    public SelectQuery appendCriteriaToSelectQuery(final String isScheduledReport, final String scheduleId, final SelectQuery selectQuery) {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.appendCriteriaToSelectQuery");
        if (isScheduledReport.equalsIgnoreCase("true") && !this.isCustomScheduleReport(scheduleId) && this.hasCustomScheduleCriteria(scheduleId)) {
            try {
                Criteria parentCriteria = selectQuery.getCriteria();
                ReportCriteriaUtil.logger.log(Level.INFO, "ReportCriteriaUtil.appendCriteriaToSelectQuery: Query before setting criteria : {0}", RelationalAPI.getInstance().getSelectSQL((Query)selectQuery));
                ReportCriteriaUtil.logger.log(Level.INFO, "ReportCriteriaUtil.appendCriteriaToSelectQuery: parentCriteria : {0}", parentCriteria);
                ReportCriteriaUtil.logger.log(Level.INFO, "ReportCriteriaUtil.appendCriteriaToSelectQuery: scheduleId : {0}", scheduleId);
                final Criteria scheduleCriteria = this.getReportCriteria(scheduleId);
                ReportCriteriaUtil.logger.log(Level.INFO, "ReportCriteriaUtil.appendCriteriaToSelectQuery: scheduleCriteria : {0}", scheduleCriteria);
                if (scheduleCriteria != null) {
                    if (parentCriteria == null) {
                        selectQuery.setCriteria(scheduleCriteria);
                    }
                    else {
                        parentCriteria = parentCriteria.and(scheduleCriteria);
                        selectQuery.setCriteria(parentCriteria);
                    }
                    ReportCriteriaUtil.logger.log(Level.INFO, "SelectQuery after adding criteria : {0}", RelationalAPI.getInstance().getSelectSQL((Query)selectQuery));
                }
            }
            catch (final Exception ex) {
                ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria: Exception while appending criteria string to report", ex);
            }
        }
        return selectQuery;
    }
    
    public Criteria getDateRangeCriteria(final Column viewColumn, final String searchValue, final String comparator) throws Exception {
        ReportCriteriaUtil.logger.log(Level.FINEST, "ReportCriteriaUtil.getDateRangeCriteria");
        Criteria dateRangeCriteria = null;
        if (this.isPredefinedDateRange(searchValue)) {
            final Hashtable<String, Long> dateRange = DateTimeUtil.determine_From_To_Times(searchValue);
            final Criteria fromDate = new Criteria(viewColumn, (Object)dateRange.get("date1"), 4);
            final Criteria toDate = new Criteria(viewColumn, (Object)dateRange.get("date2"), 6);
            dateRangeCriteria = fromDate.and(toDate);
        }
        else {
            dateRangeCriteria = this.customDateCriteria(viewColumn, searchValue, comparator);
        }
        return dateRangeCriteria;
    }
    
    private Criteria customDateCriteria(final Column viewColumn, final String searchValue, final String comparator) {
        Criteria dateCriteria = null;
        Date date = new Date();
        if (!comparator.trim().equalsIgnoreCase("Last n Days") || !comparator.trim().equalsIgnoreCase("Before n Days") || !comparator.trim().equalsIgnoreCase("Next N Days")) {
            new DateTimeUtil();
            date = DateTimeUtil.getDateFromString(searchValue.trim(), "yyyy-MM-dd");
        }
        if (comparator.trim().equalsIgnoreCase("is")) {
            final Long fromDate = date.getTime();
            new DateTimeUtil();
            final String nextDayString = DateTimeUtil.increment_Decrement_Dates(searchValue, 1L, "yyyy-MM-dd");
            new DateTimeUtil();
            final Date nextDay = DateTimeUtil.getDateFromString(nextDayString.trim(), "yyyy-MM-dd");
            final Long toDate = nextDay.getTime() - 1L;
            final Criteria fromDateCriteria = new Criteria(viewColumn, (Object)fromDate, 4);
            final Criteria toDateCriteria = new Criteria(viewColumn, (Object)toDate, 6);
            dateCriteria = fromDateCriteria.and(toDateCriteria);
        }
        else if (comparator.trim().equalsIgnoreCase("Before") || comparator.trim().equalsIgnoreCase("After")) {
            dateCriteria = new Criteria(viewColumn, (Object)date.getTime(), CRConstantValues.getOperatorValue(comparator));
        }
        else if (comparator.trim().equalsIgnoreCase("Last n Days")) {
            if (searchValue != "") {
                final int noOfDays = Integer.valueOf(searchValue);
                final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, true);
                final Long startTime = dateRange.get("fromDate");
                final Long endTime = dateRange.get("toDate");
                dateCriteria = new Criteria(viewColumn, (Object)startTime, 4);
                dateCriteria = dateCriteria.and(new Criteria(viewColumn, (Object)endTime, 6));
            }
        }
        else if (comparator.trim().equalsIgnoreCase("Before n Days")) {
            if (searchValue != null && searchValue.trim() != "") {
                final int noOfDays = Integer.valueOf(searchValue);
                final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, true);
                final Long fromDate2 = dateRange.get("fromDate");
                dateCriteria = new Criteria(viewColumn, (Object)fromDate2, 7);
            }
        }
        else if (comparator.trim().equalsIgnoreCase("Next N Days") && searchValue != null && searchValue.trim() != "") {
            final int noOfDays = Integer.valueOf(searchValue);
            final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, false);
            final Long startTime = dateRange.get("fromDate");
            final Long endTime = dateRange.get("toDate");
            dateCriteria = new Criteria(viewColumn, (Object)startTime, 4);
            dateCriteria = dateCriteria.and(new Criteria(viewColumn, (Object)endTime, 6));
        }
        ReportCriteriaUtil.logger.log(Level.FINE, "customDateCriteria: Custom date Range Criteria: {0}", dateCriteria);
        return dateCriteria;
    }
    
    public boolean isCriteriaApplicable(final Integer id) {
        try {
            ReportCriteriaUtil.logger.log(Level.FINE, "Schedule Report :Entered into ReportCriteriaUtil.isCriteriaApplicable()for taskid=" + id);
            final Column critCol = Column.getColumn("ViewCriteriaInfo", "VIEW_ID");
            final Criteria critVal = new Criteria(critCol, (Object)id, 0);
            final Criteria cri = new Criteria(critCol, (Object)id, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewCriteriaInfo"));
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.setCriteria(cri);
            ReportCriteriaUtil.logger.log(Level.FINE, "Query: isCriteriaApplicable " + query);
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Row result = dataObject.getRow("ViewCriteriaInfo", critVal);
                if (result != null) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "Schedule Report :Exception in fetching criteria status", e);
        }
        return false;
    }
    
    public boolean isCriteriaAddedToReport(final Object scheduleRepID) {
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("SRToCriteriaRel"));
            final Column schRepIdCol = Column.getColumn("SRToCriteriaRel", "SCHEDULE_REP_ID");
            final Criteria cri = new Criteria(schRepIdCol, scheduleRepID, 0);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.setCriteria(cri);
            ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil: isCriteriaAddedToReport " + query);
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            return !dataObject.isEmpty();
        }
        catch (final DataAccessException ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria: Exception while checking if criteria has been added to report", (Throwable)ex);
            return false;
        }
    }
    
    public int getNativeSqlId(final String viewId) {
        int sqlQueryId = 0;
        try {
            final Column viewIdCol = Column.getColumn("ViewCriteriaInfo", "VIEW_ID");
            final Column sqlId = Column.getColumn("ViewCriteriaInfo", "NATIVE_QUERY_ID");
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewCriteriaInfo"));
            query.addSelectColumn(sqlId);
            query.addSelectColumn(viewIdCol);
            final Criteria viewIdCriteria = new Criteria(viewIdCol, (Object)viewId, 0);
            query.setCriteria(viewIdCriteria);
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Row result = dataObject.getFirstRow("ViewCriteriaInfo");
                sqlQueryId = (int)result.get("NATIVE_QUERY_ID");
            }
        }
        catch (final DataAccessException ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria: Exception while fetching native sql id for views with very long sql queries", (Throwable)ex);
        }
        ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil.getNativeSqlId: sqlQueryId : {0}", sqlQueryId);
        return sqlQueryId;
    }
    
    public String getModuleNameFromViewId(final Object viewId) {
        String moduleName = "";
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewParams"));
            final Join join1 = new Join("ViewParams", "ReportSubCategory", new String[] { "SUB_CATEGORY_ID" }, new String[] { "SUB_CATEGORY_ID" }, 2);
            final Join join2 = new Join("ReportSubCategory", "ReportCategory", new String[] { "CATEGORY_ID" }, new String[] { "CATEGORY_ID" }, 2);
            final Column viewIdColumn = new Column("ViewParams", "VIEW_ID");
            final Criteria viewIdCriteria = new Criteria(viewIdColumn, viewId, 0);
            query.addJoin(join1);
            query.addJoin(join2);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.setCriteria(viewIdCriteria);
            final DataObject moduleNameObject = SyMUtil.getPersistence().get(query);
            ReportCriteriaUtil.logger.log(Level.INFO, "ReportCriteriaUtil: Query to get Module name for Report is :{0}", RelationalAPI.getInstance().getSelectSQL((Query)query));
            moduleName = (String)moduleNameObject.getFirstRow("ReportCategory").get("CATEGORY_NAME");
            moduleName = I18N.getMsg(moduleName, new Object[0]);
            ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil: Module name for Report is :{0}", RelationalAPI.getInstance().getSelectSQL((Query)query));
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria: Exception while fetching module name for report", ex);
        }
        return moduleName;
    }
    
    public int getModuleCategoryIdFromViewId(final Object viewId) {
        int moduleCategoryId = -1;
        try {
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewParams"));
            final Join join1 = new Join("ViewParams", "ReportSubCategory", new String[] { "SUB_CATEGORY_ID" }, new String[] { "SUB_CATEGORY_ID" }, 2);
            final Join join2 = new Join("ReportSubCategory", "ReportCategory", new String[] { "CATEGORY_ID" }, new String[] { "CATEGORY_ID" }, 2);
            final Column viewIdColumn = new Column("ViewParams", "VIEW_ID");
            final Criteria viewIdCriteria = new Criteria(viewIdColumn, viewId, 0);
            query.addJoin(join1);
            query.addJoin(join2);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.setCriteria(viewIdCriteria);
            final DataObject moduleNameObject = SyMUtil.getPersistence().get(query);
            ReportCriteriaUtil.logger.log(Level.INFO, "ReportCriteriaUtil: Query to get Module category id for Report is :{0}", RelationalAPI.getInstance().getSelectSQL((Query)query));
            if (!moduleNameObject.isEmpty()) {
                moduleCategoryId = (int)moduleNameObject.getFirstRow("ReportCategory").get("CATEGORY_ID");
            }
            ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil: Module category id for Report is :{0}", RelationalAPI.getInstance().getSelectSQL((Query)query));
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria: Exception while fetching module category id for report", ex);
        }
        return moduleCategoryId;
    }
    
    private Criteria dateRangeForNoOfDays(final Column dateColumn, final int noOfDays) {
        Criteria dateRangeCriteria = null;
        final HashMap dateRange = DateTimeUtil.getDateRangeFromNoOfDaysPast(noOfDays, true);
        final Criteria fromDate = new Criteria(dateColumn, dateRange.get("fromDate"), 4);
        final Criteria toDate = new Criteria(dateColumn, dateRange.get("fromDate"), 6);
        dateRangeCriteria = fromDate.and(toDate);
        return dateRangeCriteria;
    }
    
    public String getReportCriteria(final Long taskId) {
        ReportCriteriaUtil.logger.log(Level.INFO, "Entered ReportCriteriaUtil.getReportCriteria for task: {0}", String.valueOf(taskId));
        final StringBuilder returnString = new StringBuilder();
        try {
            final Column taskIdCol = new Column("ScheduleRepToReportRel", "TASK_ID");
            final Column reportIdCol = new Column("ScheduleRepToReportRel", "REPORT_ID");
            final Criteria taskCriteria = new Criteria(taskIdCol, (Object)taskId, 0);
            final Criteria anonymousCrit = new Criteria(Column.getColumn("DCViewFilterDetails", "IS_ANONYMOUS"), (Object)true, 0);
            final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleRepToReportRel"));
            final Column c1 = Column.getColumn("ViewParams", "TITLE");
            final String headerStyle = "style=\"background-color: rgb(238, 246, 253); border-bottom: 1px solid rgb(179, 213, 245); font-weight: bold;width:10%\"";
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            final Join srFilterRel = new Join("ScheduleRepToReportRel", "SRToFilterRel", new String[] { "SCHEDULE_REP_ID" }, new String[] { "SCHEDULE_REP_ID" }, 2);
            final Join filterCritJoin = new Join("SRToFilterRel", "DCViewFilterCriteria", new String[] { "FILTER_ID" }, new String[] { "FILTER_ID" }, 2);
            final Join criteriaColumnJoin = new Join("DCViewFilterCriteria", "CriteriaColumnDetails", new String[] { "CRITERIA_COLUMN_ID" }, new String[] { "CRITERIA_COLUMN_ID" }, 2);
            final Join crColumnJoin = new Join("CriteriaColumnDetails", "CRColumns", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
            final Join filterDetailJoin = new Join("DCViewFilterCriteria", "DCViewFilterDetails", new String[] { "FILTER_ID" }, new String[] { "FILTER_ID" }, 2);
            final SortColumn sortColumn = new SortColumn(Column.getColumn("CriteriaColumnDetails", "CRITERIA_ORDER"), true);
            query.addJoin(srFilterRel);
            query.addJoin(filterCritJoin);
            query.addJoin(criteriaColumnJoin);
            query.addJoin(crColumnJoin);
            query.addJoin(filterDetailJoin);
            query.setCriteria(taskCriteria.and(anonymousCrit));
            query.addSortColumn(sortColumn);
            final DataObject reportCriteria = SyMUtil.getPersistence().get(query);
            ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil: Criteria details query is :{0}", RelationalAPI.getInstance().getSelectSQL((Query)query));
            final Iterator reportIter = reportCriteria.getRows("ScheduleRepToReportRel");
            if (!reportCriteria.isEmpty()) {
                returnString.append("<br/><p style=\"font-size: 12px;\">The report is generated based on the criteria explained below:</p>");
            }
            while (reportIter.hasNext()) {
                final Row reportIdRow = reportIter.next();
                final Object reportId = reportIdRow.get("REPORT_ID");
                final Criteria srIDCrit = new Criteria(new Column("SRToFilterRel", "SCHEDULE_REP_ID"), reportIdRow.get("SCHEDULE_REP_ID"), 0);
                final Iterator filterIter = reportCriteria.getRows("SRToFilterRel", srIDCrit);
                final ArrayList filterIDList = new ArrayList();
                while (filterIter.hasNext()) {
                    final Row idRow = filterIter.next();
                    filterIDList.add(idRow.get("FILTER_ID"));
                }
                final Criteria filterListCrit = new Criteria(Column.getColumn("DCViewFilterCriteria", "FILTER_ID"), (Object)filterIDList.toArray(), 8);
                final Iterator iter = reportCriteria.getRows("DCViewFilterCriteria", filterListCrit);
                final Row viewDetails = SyMUtil.getPersistence().get("ViewParams", new Criteria(new Column("ViewParams", "VIEW_ID"), reportId, 0)).getRow("ViewParams");
                final String viewName = (String)viewDetails.get("TITLE");
                final Object viewId = viewDetails.get("VIEW_ID");
                final String moduleName = this.getModuleNameFromViewId(viewId);
                returnString.append("<p style=\"font-size: 12px;\"><b> " + I18N.getMsg(viewName, new Object[0]) + " </b><span style=\"color: rgb(153,153,153);\"> (" + moduleName + ")</span></p>");
                returnString.append("<table cellspacing=\"0\" cellpadding=\"5\"  style=\"border-width: 1.0px;border-style: solid;border-color: rgb(204,204,204);width: 60.0%;border-bottom: none;\">");
                returnString.append("<tr><td ").append("style=\"background-color: rgb(204,204,204);border-bottom: 1.0px solid rgb(204,204,204);font-weight: bold;font-size: 12.0px;width: 20.0%;text-align: left;color: rgb(51,51,51);\"").append("><b>Column</b></td>");
                returnString.append("<td ").append("style=\"background-color: rgb(204,204,204);border-bottom: 1.0px solid rgb(204,204,204);font-weight: bold;font-size: 12.0px;width: 10.0%;text-align: left;color: rgb(51,51,51);\"").append("><b>Condition</b></td>");
                returnString.append("<td ").append("style=\"background-color: rgb(204,204,204);border-bottom: 1.0px solid rgb(204,204,204);font-weight: bold;font-size: 12.0px;width: 60.0%;text-align: left;color: rgb(51,51,51);\"").append("><b>Value</></td>");
                returnString.append("<td ").append("style=\"background-color: rgb(204,204,204);border-bottom: 1.0px solid rgb(204,204,204);font-weight: bold;font-size: 12.0px;width: 10.0%;text-align: left;color: rgb(51,51,51);\"").append("></td></tr>");
                int oddEven = 1;
                String bgColor = "";
                int colCount = 0;
                while (iter.hasNext()) {
                    final Row tableRows = iter.next();
                    final Criteria singleReportCriteria = new Criteria(new Column("CriteriaColumnDetails", "CRITERIA_COLUMN_ID"), tableRows.get("CRITERIA_COLUMN_ID"), 0);
                    final Iterator iterReportCrit = reportCriteria.getRows("CriteriaColumnDetails", singleReportCriteria);
                    final String logicalOperator = "";
                    while (iterReportCrit.hasNext()) {
                        bgColor = ((oddEven % 2 == 0) ? "rgb(249,249,249);" : "rgb(255,255,255);");
                        ++oddEven;
                        final Row critDetailsTable = iterReportCrit.next();
                        if (colCount > 0) {
                            returnString.append("<td style=\"border-bottom: 1.0px solid rgb(204,204,204);font-family: Lato , Roboto;font-size: 11.0px;text-align: left;vertical-align:top;\">");
                            final Object logicalOp = critDetailsTable.get("LOGICAL_OPERATOR");
                            if (logicalOp != null) {
                                final int opVal = (int)logicalOp;
                                if (opVal == 1) {
                                    returnString.append("<strong>AND</strong>");
                                }
                                else if (opVal == 0) {
                                    returnString.append("<strong>OR</strong>");
                                }
                            }
                            returnString.append("</td>");
                            returnString.append("</tr>");
                        }
                        final String style = "style=\"background-color: " + bgColor + "\"";
                        returnString.append("<tr " + style + ">");
                        final Object colIdValue = critDetailsTable.get("COLUMN_ID");
                        final Criteria joinCrColumn = new Criteria(new Column("CRColumns", "COLUMN_ID"), colIdValue, 0);
                        final Row columnNameRow = reportCriteria.getRow("CRColumns", joinCrColumn);
                        final String dataType = (String)columnNameRow.get("DATA_TYPE");
                        final String colDisplayName = I18N.getMsg((String)columnNameRow.get("DISPLAY_NAME"), new Object[0]);
                        final String comparator = (String)critDetailsTable.get("COMPARATOR");
                        String searchValue = (String)critDetailsTable.get("SEARCH_VALUE");
                        searchValue = ((searchValue == null) ? "" : searchValue);
                        String searchValue2 = "";
                        if (critDetailsTable.get("SEARCH_VALUE_2") != null) {
                            searchValue2 = (String)critDetailsTable.get("SEARCH_VALUE_2");
                        }
                        if (dataType.equalsIgnoreCase("I18N")) {
                            final String[] searchValues = searchValue.split(Pattern.quote("$@$"));
                            final StringBuilder resultSearchString = new StringBuilder();
                            for (int i = 0; i < searchValues.length; ++i) {
                                resultSearchString.append(I18N.getMsg(searchValues[i], new Object[0]));
                                if (i < searchValues.length - 1) {
                                    resultSearchString.append(", ");
                                }
                            }
                            searchValue = resultSearchString.toString();
                        }
                        else if (dataType.equalsIgnoreCase("DATE") || dataType.equalsIgnoreCase("DATE_RESTRICTED")) {
                            final String[] searchValues = searchValue.split(Pattern.quote("$@$"));
                            final StringBuilder resultSearchString = new StringBuilder();
                            for (int i = 0; i < searchValues.length; ++i) {
                                if (this.isPredefinedDateRange(searchValues[i])) {
                                    final String predefinedDateRangeText = this.predefinedDateRangeText(searchValues[i]);
                                    resultSearchString.append(predefinedDateRangeText);
                                }
                                else {
                                    resultSearchString.append(searchValues[i]);
                                }
                                if (i < searchValues.length - 1) {
                                    resultSearchString.append(", ");
                                }
                            }
                            searchValue = resultSearchString.toString();
                        }
                        else if (dataType.equalsIgnoreCase("EQUALONLY")) {
                            final String[] searchValues = searchValue.split(Pattern.quote("$@$"));
                            final StringBuilder resultSearchString = new StringBuilder();
                            final SelectQuery searchValueQuery;
                            if ((searchValueQuery = CriteriaColumnValueUtil.getInstance().customBrowseValuesFetchQuery((Long)colIdValue, new Long(viewId.toString()), null, false, null)) == null) {
                                final HashMap<Object, String> transformValue = CriteriaColumnValueUtil.getInstance().getTranformValueList((Long)colIdValue, null);
                                for (int j = 0; j < searchValues.length; ++j) {
                                    resultSearchString.append(transformValue.get(searchValues[j]));
                                    if (j < searchValues.length - 1) {
                                        resultSearchString.append(", ");
                                    }
                                }
                                searchValue = String.valueOf(resultSearchString);
                            }
                            else {
                                final LinkedHashMap<Object, Object> browseValues = new LinkedHashMap<Object, Object>();
                                browseValues.putAll(CriteriaColumnValueUtil.getInstance().getBrowseValuesFromDB(searchValueQuery));
                                final LinkedHashMap<String, String> browseValuesString = new LinkedHashMap<String, String>();
                                for (final Map.Entry<Object, Object> entry : browseValues.entrySet()) {
                                    try {
                                        browseValuesString.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                                    }
                                    catch (final ClassCastException cce) {
                                        ReportCriteriaUtil.logger.log(Level.SEVERE, "Exception while converting transformation value to string", cce);
                                    }
                                }
                                for (int k = 0; k < searchValues.length; ++k) {
                                    resultSearchString.append(browseValuesString.get(searchValues[k]));
                                    if (k < searchValues.length - 1) {
                                        resultSearchString.append(", ");
                                    }
                                }
                                searchValue = String.valueOf(resultSearchString);
                            }
                        }
                        else if (dataType.equalsIgnoreCase("CHAR")) {
                            final String[] searchValues = searchValue.split(Pattern.quote("$@$"));
                            final StringBuilder resultSearchString = new StringBuilder();
                            for (int i = 0; i < searchValues.length; ++i) {
                                resultSearchString.append(searchValues[i]);
                                if (i < searchValues.length - 1) {
                                    resultSearchString.append(", ");
                                }
                            }
                            searchValue = resultSearchString.toString();
                        }
                        else if (dataType.equalsIgnoreCase("EQUALLIKEONLY")) {
                            final String[] searchValues = searchValue.split(Pattern.quote("$@$"));
                            final StringBuilder resultSearchString = new StringBuilder();
                            for (int i = 0; i < searchValues.length; ++i) {
                                resultSearchString.append(searchValues[i]);
                                if (i < searchValues.length - 1) {
                                    resultSearchString.append(", ");
                                }
                            }
                            searchValue = resultSearchString.toString();
                        }
                        returnString.append("<td style=\"border-bottom: 1.0px solid rgb(204,204,204);font-family: Lato , Roboto;font-size: 11.0px;text-align: left;vertical-align:top;\">").append(colDisplayName).append("</td>");
                        returnString.append("<td style=\"border-bottom: 1.0px solid rgb(204,204,204);font-family: Lato , Roboto;font-size: 11.0px;text-align: left;vertical-align:top;\">").append(comparator).append("</td>");
                        if (comparator.equalsIgnoreCase("empty") || comparator.equalsIgnoreCase("not empty")) {
                            returnString.append("<td style=\"border-bottom: 1.0px solid rgb(204,204,204);font-family: Lato , Roboto;font-size: 11.0px;text-align: left;\">N/A</td>");
                        }
                        else if (comparator.equalsIgnoreCase("between")) {
                            returnString.append("<td style=\"border-bottom: 1.0px solid rgb(204,204,204);font-family: Lato , Roboto;font-size: 11.0px;text-align: left;\">").append(searchValue).append(",").append(searchValue2).append("</td>");
                        }
                        else {
                            returnString.append("<td style=\"border-bottom: 1.0px solid rgb(204,204,204);font-family: Lato , Roboto;font-size: 11.0px;text-align: left;\">").append(searchValue).append("</td>");
                        }
                    }
                    ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil Criteria to be sent by mail is {0}", returnString.toString());
                    ++colCount;
                }
                returnString.append("<td style=\"border-bottom: 1.0px solid rgb(204,204,204);font-family: Lato , Roboto;font-size: 11.0px;text-align: left;\"></td></tr>");
                returnString.append("</tr>");
                returnString.append("</table>");
            }
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Criteria: Exception while generating mail content for schedule report criteria", ex);
        }
        return returnString.toString();
    }
    
    public boolean isVaraibleHandlingRequired(final String viewId, final String placeHolderName) {
        boolean handlingRequired = Boolean.FALSE;
        try {
            Criteria crit = new Criteria(Column.getColumn("PlaceHolderToCRColumnRel", "VIEW_ID"), (Object)viewId, 0);
            crit = crit.and(new Criteria(Column.getColumn("PlaceHolderToCRColumnRel", "PLACE_HOLDER"), (Object)placeHolderName, 0));
            final DataObject placeHolderDO = SyMUtil.getPersistence().get("PlaceHolderToCRColumnRel", crit);
            if (!placeHolderDO.isEmpty()) {
                handlingRequired = Boolean.TRUE;
            }
        }
        catch (final Exception ee) {
            ee.printStackTrace();
        }
        return handlingRequired;
    }
    
    public static Boolean getTrimStatus() {
        try {
            final Column col = Column.getColumn("ViewGlobalSettings", "IS_COLUMNS_TRIMMED");
            final Criteria criteria = null;
            final DataObject trimDO = DataAccess.get("ViewGlobalSettings", criteria);
            if (trimDO.isEmpty()) {
                return null;
            }
            final Row sfrow = trimDO.getFirstRow("ViewGlobalSettings");
            final Boolean paramValue = (Boolean)sfrow.get("IS_COLUMNS_TRIMMED");
            return paramValue;
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "Caught exception while retrieving Trim Value  from DB.", ex);
            return null;
        }
    }
    
    public static void updateViewGlobalSettings(final Boolean paramValue) {
        try {
            final Criteria criteria = null;
            final DataObject symParamDO = SyMUtil.getPersistence().get("ViewGlobalSettings", criteria);
            final Row symParamRow = symParamDO.getFirstRow("ViewGlobalSettings");
            if (symParamRow != null) {
                symParamRow.set("IS_COLUMNS_TRIMMED", (Object)paramValue);
                symParamDO.updateRow(symParamRow);
                DataAccess.update(symParamDO);
            }
            ReportCriteriaUtil.logger.log(Level.FINER, "Parameter updated in DB:-   param value: " + paramValue);
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "Caught exception while updating Parameter: in DB.", ex);
        }
    }
    
    public String getFilterCriteriaJSON(final Object scheduleRepID) {
        String criteriaJSONString = "";
        final SelectQuery scheduleFilterQuery = (SelectQuery)new SelectQueryImpl(new Table("SRToFilterRel"));
        final Column scheduleRepColumn = new Column("SRToFilterRel", "SCHEDULE_REP_ID");
        final Column filterIDColumn = new Column("SRToFilterRel", "FILTER_ID");
        scheduleFilterQuery.addSelectColumn(scheduleRepColumn);
        scheduleFilterQuery.addSelectColumn(filterIDColumn);
        final Criteria scheduleCriteria = new Criteria(new Column("SRToFilterRel", "SCHEDULE_REP_ID"), scheduleRepID, 0);
        scheduleFilterQuery.setCriteria(scheduleCriteria);
        try {
            final DataObject reportFilter = SyMUtil.getPersistence().get(scheduleFilterQuery);
            if (!reportFilter.isEmpty()) {
                final Long filterID = (Long)reportFilter.getFirstRow("SRToFilterRel").get("FILTER_ID");
                if (filterID != null) {
                    final JSONObject criteriaJSON = DCViewFilterUtil.getInstance().getCriteriaJSONForFilter(filterID).dcViewFilterMapper();
                    criteriaJSONString = (criteriaJSON.isNull("criteria") ? null : criteriaJSON.toString());
                }
                return criteriaJSONString;
            }
            ReportCriteriaUtil.logger.log(Level.WARNING, "ReportCriteriaUtil.getFilterCriteriaJSON : Filters not found for the Report !");
            return null;
        }
        catch (final Exception e) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "ReportCriteriaUtil.getFilterCriteriaJSON :Exception in processing Filter status{0}", e);
            return null;
        }
    }
    
    public JSONObject getFilterJSON(final Long taskID) {
        final SelectQuery filterlistQuery = getInstance().getFilterListDOForTaskID(taskID);
        final JSONObject filterReportDetails = new JSONObject();
        try {
            final DataObject reportFilter = SyMUtil.getPersistence().get(filterlistQuery);
            final Iterator scheduleRepIter = reportFilter.getRows("ScheduleRepToReportRel");
            while (scheduleRepIter.hasNext()) {
                final Row reportRow = scheduleRepIter.next();
                final Long reportId = (Long)reportRow.get("REPORT_ID");
                final Long scheduleRepId = (Long)reportRow.get("SCHEDULE_REP_ID");
                final JSONObject filterDetails = new JSONObject();
                final Row filterDetail = reportFilter.getRow("DCViewFilterDetails", new Criteria(new Column("SRToFilterRel", "SCHEDULE_REP_ID"), (Object)scheduleRepId, 0));
                final String filterNam = (String)filterDetail.get("FILTER_NAME");
                final Long filterId = (Long)filterDetail.get("FILTER_ID");
                filterDetails.put("filterId", (Object)String.valueOf(filterId));
                filterDetails.put("filterName", (Object)filterNam);
                filterReportDetails.put(reportId.toString(), (Object)filterDetails);
            }
            return filterReportDetails;
        }
        catch (final Exception e) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "ReportCriteriaUtil.getCriteriaJSON :Exception in fetch Filter details {0}", e);
            return null;
        }
    }
    
    public Long getViewIdForScheduleReport(final String scheduleRepId) {
        try {
            if (!scheduleRepId.equals("null") && scheduleRepId != null) {
                final Column critcol = Column.getColumn("ScheduleRepToReportRel", "SCHEDULE_REP_ID");
                final Criteria cri = new Criteria(critcol, (Object)Long.valueOf(scheduleRepId), 0);
                final Persistence persistence = SyMUtil.getPersistence();
                final DataObject scheduleViewId = persistence.get("ScheduleRepToReportRel", cri);
                if (!scheduleViewId.isEmpty()) {
                    final Row reportIdRow = scheduleViewId.getFirstRow("ScheduleRepToReportRel");
                    final Long viewId = (Long)reportIdRow.get("REPORT_ID");
                    return viewId;
                }
            }
        }
        catch (final Exception e) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "ReportCriteriaUtil.getViewIdForScheduleReport :Exception in getting viewId for scheduleID {0}", e);
        }
        return null;
    }
    
    public String constructCriteriaJSONForScheduleID(final String scheduleRepId) {
        String criteriaJSON = "";
        try {
            if (!scheduleRepId.equals("null")) {
                final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CriteriaColumnDetails"));
                final Join criteriaColumnJoin = new Join("CriteriaColumnDetails", "SRToCriteriaRel", new String[] { "CRITERIA_COLUMN_ID" }, new String[] { "CRITERIA_COLUMN_ID" }, 2);
                selectQuery.addSelectColumn(new Column("SRToCriteriaRel", "SCHEDULE_REP_ID"));
                selectQuery.addSelectColumn(new Column("SRToCriteriaRel", "CRITERIA_COLUMN_ID"));
                selectQuery.addSelectColumn(new Column("CriteriaColumnDetails", "CRITERIA_COLUMN_ID"));
                selectQuery.addSelectColumn(new Column("CriteriaColumnDetails", "COLUMN_ID"));
                selectQuery.addSelectColumn(new Column("CriteriaColumnDetails", "LOGICAL_OPERATOR"));
                selectQuery.addSelectColumn(new Column("CriteriaColumnDetails", "COMPARATOR"));
                selectQuery.addSelectColumn(new Column("CriteriaColumnDetails", "OP_CUSTOM_TYPE"));
                selectQuery.addSelectColumn(new Column("CriteriaColumnDetails", "SEARCH_VALUE"));
                selectQuery.addSelectColumn(new Column("CriteriaColumnDetails", "CRITERIA_ORDER"));
                selectQuery.addSelectColumn(new Column("CriteriaColumnDetails", "SEARCH_VALUE_2"));
                selectQuery.addSelectColumn(new Column("CriteriaColumnDetails", "IS_NEGATED"));
                selectQuery.addJoin(criteriaColumnJoin);
                final SortColumn criteriaIndexSort = new SortColumn("CriteriaColumnDetails", "CRITERIA_ORDER", true);
                selectQuery.addSortColumn(criteriaIndexSort);
                selectQuery.setCriteria(new Criteria(Column.getColumn("SRToCriteriaRel", "SCHEDULE_REP_ID"), (Object)Long.valueOf(scheduleRepId), 0));
                final DataObject dataObject = SyMUtil.getPersistence().get(selectQuery);
                if (!dataObject.isEmpty()) {
                    criteriaJSON = buildCriteriaJSON(dataObject);
                    return criteriaJSON;
                }
            }
        }
        catch (final Exception e) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "Exception in fetching Criteria for ScheduleID: ReportCriteriaUtil.constructCriteriaJSONForScheduleID" + e);
        }
        return null;
    }
    
    private static String buildCriteriaJSON(final DataObject criteriaDO) {
        DCViewFilter dcViewFilter = null;
        try {
            if (!criteriaDO.isEmpty()) {
                dcViewFilter = new DCViewFilter();
                final Iterator iterator = criteriaDO.getRows("CriteriaColumnDetails");
                while (iterator.hasNext()) {
                    final DCViewFilterCriteria dcViewFilterCriteria = new DCViewFilterCriteria();
                    final Row filterCriteriaRow = iterator.next();
                    final Long columnID = (Long)filterCriteriaRow.get("COLUMN_ID");
                    final String comparator = (String)filterCriteriaRow.get("COMPARATOR");
                    final String value = (String)filterCriteriaRow.get("SEARCH_VALUE");
                    final String value2 = (String)filterCriteriaRow.get("SEARCH_VALUE_2");
                    final Integer customType = (Integer)filterCriteriaRow.get("OP_CUSTOM_TYPE");
                    final int logicalOperator = (int)filterCriteriaRow.get("LOGICAL_OPERATOR");
                    final List valueList = (value != null) ? new ArrayList(Arrays.asList((String[])value.split(Pattern.quote("$@$")))) : new ArrayList();
                    if (value2 != null && !value2.isEmpty()) {
                        valueList.add(value2);
                    }
                    final String logicalOperatorString = (logicalOperator == 1) ? "AND" : "OR";
                    dcViewFilterCriteria.setColumnID(columnID);
                    dcViewFilterCriteria.setComparator(comparator);
                    dcViewFilterCriteria.setSearchValue(valueList);
                    dcViewFilterCriteria.setLogicalOperator(logicalOperatorString);
                    if (customType != null) {
                        dcViewFilterCriteria.setCustomComparator(DCViewFilterConstants.getCustomTypeValue(customType));
                    }
                    else {
                        dcViewFilterCriteria.setCustomComparator("--");
                    }
                    dcViewFilter.addDCViewFilterCriteria(dcViewFilterCriteria);
                }
            }
            return dcViewFilter.dcViewFilterMapper().toString();
        }
        catch (final Exception e) {
            ReportCriteriaUtil.logger.log(Level.WARNING, "Exception in building buildCriteriaJSON for taskID: ReportCriteriaUtil.buildCriteriaJSON" + e);
            return "";
        }
    }
    
    public String getReportFilter(final Long taskId) {
        ReportCriteriaUtil.logger.log(Level.INFO, "Entered ReportCriteriaUtil.getReportFilter for task: {0}", String.valueOf(taskId));
        final StringBuilder returnString = new StringBuilder();
        try {
            final SelectQuery query = getInstance().getFilterListDOForTaskID(taskId);
            final DataObject reportFilter = SyMUtil.getPersistence().get(query);
            ReportCriteriaUtil.logger.log(Level.FINE, "ReportCriteriaUtil: Criteria details query is :{0}", RelationalAPI.getInstance().getSelectSQL((Query)query));
            final Iterator reportIter = reportFilter.getRows("ScheduleRepToReportRel");
            while (reportIter.hasNext()) {
                final Row reportIdRow = reportIter.next();
                final Object reportId = reportIdRow.get("REPORT_ID");
                final Long scheduleRepId = (Long)reportIdRow.get("SCHEDULE_REP_ID");
                final Row viewDetails = SyMUtil.getPersistence().get("ViewParams", new Criteria(new Column("ViewParams", "VIEW_ID"), reportId, 0)).getRow("ViewParams");
                final String viewName = (String)viewDetails.get("TITLE");
                final Object viewId = viewDetails.get("VIEW_ID");
                final String moduleName = this.getModuleNameFromViewId(viewId);
                returnString.append("<p style=\"font-size: 12px;\"><b> " + I18N.getMsg(viewName, new Object[0]) + " </b><span style=\"color: rgb(153,153,153);\"> (" + moduleName + ")</span></p>");
                final Criteria filterIDCriteria = new Criteria(new Column("SRToFilterRel", "SCHEDULE_REP_ID"), (Object)scheduleRepId, 0);
                final Row filterIDRow = reportFilter.getRow("SRToFilterRel", filterIDCriteria);
                final Long filterID = (Long)filterIDRow.get("FILTER_ID");
                final Criteria filterNameCriteria = new Criteria(new Column("DCViewFilterDetails", "FILTER_ID"), (Object)filterID, 0);
                final Row filterNameRow = reportFilter.getRow("DCViewFilterDetails", filterNameCriteria);
                final String filterName = (String)filterNameRow.get("FILTER_NAME");
                returnString.append("<table cellspacing=\"0\" cellpadding=\"5\"  style=\"border-width: 1.0px;border-style: solid;border-color: rgb(204,204,204);width: 60.0%;border-bottom: none;\">");
                returnString.append("<tr><td ").append("style=\"background-color: rgb(204,204,204);border-bottom: 1.0px solid rgb(204,204,204);font-weight: bold;font-size: 12.0px;width: 20.0%;text-align: left;color: rgb(51,51,51);\"").append("><b>Applied Filter Name</b></td>");
                returnString.append("<td ").append("style=\"background-color: rgb(204,204,204);border-bottom: 1.0px solid rgb(204,204,204);font-weight: bold;font-size: 12.0px;width: 10.0%;text-align: left;color: rgb(51,51,51);\"").append("></td></tr>");
                returnString.append("<tr style=\"background-color: rgb(249,249,249); \">");
                returnString.append("<td style=\"border-bottom: 1.0px solid rgb(204,204,204);font-family: Lato , Roboto;font-size: 11.0px;text-align: left;vertical-align:top;\">").append(filterName).append("</td>");
                returnString.append("<td style=\"border-bottom: 1.0px solid rgb(204,204,204);font-family: Lato , Roboto;font-size: 11.0px;text-align: left;\"></td></tr>");
                returnString.append("</tr>");
                returnString.append("</table>");
            }
        }
        catch (final Exception ex) {
            ReportCriteriaUtil.logger.log(Level.SEVERE, "Report Filter: Exception while generating mail content for schedule report Filter", ex);
        }
        return returnString.toString();
    }
    
    public SelectQuery getFilterListDOForTaskID(final Long taskId) {
        final SelectQuery query = this.getFilterQueryForTaskID(taskId);
        final Criteria anonymousCriteria = new Criteria(Column.getColumn("DCViewFilterDetails", "IS_ANONYMOUS"), (Object)false, 0);
        query.setCriteria(query.getCriteria().and(anonymousCriteria));
        return query;
    }
    
    public SelectQuery getFilterQueryForTaskID(final Long taskID) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ScheduleRepToReportRel"));
        final Join filterRelJoin = new Join("ScheduleRepToReportRel", "SRToFilterRel", new String[] { "SCHEDULE_REP_ID" }, new String[] { "SCHEDULE_REP_ID" }, 2);
        final Join filterDetailsJoin = new Join("SRToFilterRel", "DCViewFilterDetails", new String[] { "FILTER_ID" }, new String[] { "FILTER_ID" }, 2);
        final Criteria taskCriteria = new Criteria(Column.getColumn("ScheduleRepToReportRel", "TASK_ID"), (Object)taskID, 0);
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        query.addJoin(filterRelJoin);
        query.addJoin(filterDetailsJoin);
        query.setCriteria(taskCriteria);
        return query;
    }
    
    static {
        ReportCriteriaUtil.reportCriteriaUtilBase = null;
        ReportCriteriaUtil.logger = Logger.getLogger("ScheduleReportLogger");
        ReportCriteriaUtil.defaultClass = "com.me.devicemanagement.framework.webclient.reportcriteria.CriteriaColumnValueImpl";
        ReportCriteriaUtil.crDefaultClass = "com.me.dc.reports.core.CustomReportColValueFetchImpl";
    }
}
