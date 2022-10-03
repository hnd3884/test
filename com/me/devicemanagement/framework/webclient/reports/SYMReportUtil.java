package com.me.devicemanagement.framework.webclient.reports;

import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.framework.server.util.DBConstants;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONException;
import com.me.devicemanagement.framework.utils.JsonUtils;
import java.util.HashSet;
import java.util.Map;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import java.util.Collection;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.LinkedHashMap;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.i18n.I18N;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Hashtable;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import java.util.ArrayList;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.CommonUtils;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.schedulereport.ScheduleReportUtil;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class SYMReportUtil
{
    protected static String className;
    protected static Logger out;
    public static final String REPORT_LIST = "report_list";
    
    public static int createScheduleReport(final Long scheduledTaskId, final JSONObject scheduleReportJObj) {
        try {
            final Persistence dbPersistence = SyMUtil.getPersistence();
            final DataObject dbDO = dbPersistence.constructDataObject();
            final Row scheduleRepTask = new Row("ScheduleRepTask");
            scheduleRepTask.set("TASK_ID", (Object)scheduledTaskId);
            scheduleRepTask.set("CUSTOMER_ID", scheduleReportJObj.get("CUSTOMER_ID"));
            scheduleRepTask.set("REPORT_FORMAT", scheduleReportJObj.get("REPORT_FORMAT"));
            scheduleRepTask.set("DELIVERY_FORMAT", scheduleReportJObj.get("DELIVERY_FORMAT"));
            scheduleRepTask.set("EMAIL_ADDRESS", scheduleReportJObj.has("EMAIL_ADDRESS") ? scheduleReportJObj.get("EMAIL_ADDRESS") : null);
            scheduleRepTask.set("SUBJECT", scheduleReportJObj.has("SUBJECT") ? scheduleReportJObj.get("SUBJECT") : null);
            scheduleRepTask.set("CONTENT", scheduleReportJObj.has("CONTENT") ? scheduleReportJObj.get("CONTENT") : null);
            scheduleRepTask.set("DESCRIPTION", scheduleReportJObj.has("DESCRIPTION") ? scheduleReportJObj.get("DESCRIPTION") : null);
            scheduleRepTask.set("ATTACHMENT_LIMIT", scheduleReportJObj.has("ATTACHMENT_LIMIT") ? scheduleReportJObj.get("ATTACHMENT_LIMIT") : null);
            final Boolean attachLimitFlag = scheduleReportJObj.has("DELIVERY_FLAG") && (boolean)scheduleReportJObj.get("DELIVERY_FLAG");
            scheduleRepTask.set("DELIVERY_FLAG", (Object)(boolean)attachLimitFlag);
            final String reportListAsString = scheduleReportJObj.has("report_list") ? scheduleReportJObj.get("report_list").toString() : null;
            dbDO.addRow(scheduleRepTask);
            dbPersistence.update(dbDO);
            final ScheduleReportUtil scheduleReportUtil = new ScheduleReportUtil();
            SYMReportUtil.out.log(Level.FINE, "Data object scheduleRepTask " + dbDO);
            if (reportListAsString != null) {
                final JSONObject reportList = CommonUtils.createJsonObject(reportListAsString);
                final Iterator iterator = reportList.keys();
                while (iterator.hasNext()) {
                    final Row scheduleRep = new Row("ScheduleRepToReportRel");
                    final Long view_id = new Long(iterator.next().toString());
                    final Integer reportType = new Integer(reportList.get(view_id.toString()).toString());
                    scheduleRep.set("REPORT_ID", (Object)view_id);
                    scheduleRep.set("TASK_ID", (Object)scheduledTaskId);
                    scheduleRep.set("REPORT_TYPE", (Object)reportType);
                    dbDO.addRow(scheduleRep);
                    final HashMap<Long, ArrayList<HashMap<String, String>>> criteriaColumnMap = (HashMap<Long, ArrayList<HashMap<String, String>>>)(scheduleReportJObj.has("criteriaColumnMap") ? scheduleReportJObj.get("criteriaColumnMap") : new HashMap<Long, ArrayList<HashMap<String, String>>>());
                    if (!criteriaColumnMap.isEmpty()) {
                        final ArrayList<HashMap<String, String>> criteriaList = criteriaColumnMap.get(view_id);
                        if (criteriaList != null) {
                            for (int j = 0; j < criteriaList.size(); ++j) {
                                final HashMap<String, String> critValues = criteriaList.get(j);
                                final Row criteriaDetails = new Row("CriteriaColumnDetails");
                                final Row scheduleCriteria = new Row("SRToCriteriaRel");
                                scheduleCriteria.set("SCHEDULE_REP_ID", scheduleRep.get("SCHEDULE_REP_ID"));
                                scheduleCriteria.set("CRITERIA_COLUMN_ID", criteriaDetails.get("CRITERIA_COLUMN_ID"));
                                criteriaDetails.set("COLUMN_ID", (Object)critValues.get("COLUMN_ID"));
                                criteriaDetails.set("SEARCH_VALUE", (Object)critValues.get("SEARCH_VALUE"));
                                criteriaDetails.set("COMPARATOR", (Object)critValues.get("COMPARATOR"));
                                criteriaDetails.set("IS_NEGATED", (Object)(critValues.containsKey("IS_NEGATED") ? critValues.get("IS_NEGATED") : "false"));
                                criteriaDetails.set("CRITERIA_ORDER", (Object)critValues.get("CRITERIA_ORDER"));
                                if (critValues.get("LOGICAL_OPERATOR") != null) {
                                    criteriaDetails.set("LOGICAL_OPERATOR", (Object)critValues.get("LOGICAL_OPERATOR"));
                                }
                                dbDO.addRow(criteriaDetails);
                                dbDO.addRow(scheduleCriteria);
                            }
                        }
                    }
                    dbPersistence.update(dbDO);
                }
            }
        }
        catch (final Exception e) {
            SYMReportUtil.out.log(Level.INFO, "Error while creating schedule report ");
            e.printStackTrace();
            return 1001;
        }
        return 1000;
    }
    
    public static Hashtable getViewParams(final Integer viewID) throws SyMException {
        Criteria crit = null;
        final Hashtable hash = new Hashtable();
        try {
            if (viewID != null) {
                crit = new Criteria(Column.getColumn("ViewParams", "VIEW_ID"), (Object)viewID, 0);
            }
            final SelectQuery query = getViewQuery(crit);
            final DataObject dataObject = SyMUtil.getPersistence().get(query);
            if (!dataObject.isEmpty()) {
                final Integer catID = (Integer)dataObject.getFirstValue("ReportCategory", "CATEGORY_ID");
                String catName = (String)dataObject.getFirstValue("ReportCategory", "CATEGORY_NAME");
                catName = I18N.getMsg(catName, new Object[0]);
                final Iterator it = dataObject.getRows("ViewParams");
                while (it.hasNext()) {
                    final Row row = it.next();
                    if (row != null) {
                        final List columns = row.getColumns();
                        for (int i = 0; i < columns.size(); ++i) {
                            final String columnName = columns.get(i);
                            Object columnValue = row.get(columnName);
                            if (columnName.equals("DESCRIPTION")) {
                                columnValue = I18N.getMsg((String)columnValue, new Object[0]);
                            }
                            if (columnName.equals("TITLE")) {
                                columnValue = I18N.getMsg((String)columnValue, new Object[0]);
                            }
                            hash.put(columnName, columnValue);
                        }
                        hash.put("REPORT_CATEGORY", catID);
                        hash.put("REPORT_CATEGORY_NAME", catName);
                        final Criteria countCrit = new Criteria(Column.getColumn("ViewSummary", "VIEW_ID"), hash.get("VIEW_ID"), 0);
                        final Integer recordCount = (Integer)dataObject.getValue("ViewSummary", "RECORD_COUNT", countCrit);
                        if (recordCount == null || recordCount < 0) {
                            continue;
                        }
                        hash.put("RECORD_COUNT", recordCount);
                    }
                }
            }
        }
        catch (final DataAccessException ex) {
            SYMReportUtil.out.log(Level.INFO, "DataAccessException while getting the view params :", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            SYMReportUtil.out.log(Level.INFO, "Exception while getting the view params :", ex2);
            throw new SyMException(1001, ex2);
        }
        return hash;
    }
    
    public static SelectQuery getViewQuery(final Criteria criteria) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewParams"));
        query.addJoin(new Join("ViewParams", "ReportSubCategory", new String[] { "SUB_CATEGORY_ID" }, new String[] { "SUB_CATEGORY_ID" }, 2));
        query.addJoin(new Join("ReportSubCategory", "ReportCategory", new String[] { "CATEGORY_ID" }, new String[] { "CATEGORY_ID" }, 2));
        query.addJoin(new Join("ViewParams", "ViewSummary", new String[] { "VIEW_ID" }, new String[] { "VIEW_ID" }, 1));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        final List sortColumns = new ArrayList();
        sortColumns.add(new SortColumn(Column.getColumn("ViewParams", "ORDER_OF_DISPLAY"), true));
        sortColumns.add(new SortColumn(Column.getColumn("ReportSubCategory", "ORDER_OF_DISPLAY"), true));
        query.addSortColumns(sortColumns);
        if (criteria != null) {
            query.setCriteria(criteria);
        }
        return query;
    }
    
    public static String viewListWithSubCategoryId() {
        return "SUB_CATEGORY_ID";
    }
    
    public static String viewListWithSubCategoryName() {
        return "SUB_CATEGORY_NAME";
    }
    
    public static LinkedHashMap getViewList(final Integer reportCategory, final String listName, final String reportSubCategoryColumnNameForListName, final SelectQuery query) throws SyMException {
        return getViewList(reportCategory, listName, reportSubCategoryColumnNameForListName, query, null);
    }
    
    public static LinkedHashMap getViewList(final Integer reportCategory, final String listName, final String reportSubCategoryColumnNameForListName, final DataObject dataObject) throws SyMException {
        return getViewList(reportCategory, listName, reportSubCategoryColumnNameForListName, null, dataObject);
    }
    
    public static LinkedHashMap getViewList(final Integer reportCategory, final String listName, final String reportSubCategoryColumnNameForListName, final SelectQuery query, DataObject dataObject) throws SyMException {
        final LinkedHashMap categoryHash = new LinkedHashMap();
        final Map<Integer, List<Long>> roleMap = getRolesForViews();
        try {
            final List<Long> userRoleList = DMUserHandler.getRoleIdsFromRoleName(ApiFactoryProvider.getAuthUtilAccessAPI().getRoles());
            if (dataObject != null || query != null) {
                if (dataObject == null) {
                    dataObject = SyMUtil.getPersistence().get(query);
                }
                Criteria reportCategoryCriteria = null;
                if (reportCategory != null) {
                    reportCategoryCriteria = new Criteria(Column.getColumn("ReportSubCategory", "CATEGORY_ID"), (Object)reportCategory, 0);
                }
                final Iterator subCategoryItr = dataObject.getRows("ReportSubCategory", reportCategoryCriteria);
                while (subCategoryItr.hasNext()) {
                    final Row subCategoryRow = subCategoryItr.next();
                    final Integer catID = (Integer)subCategoryRow.get("CATEGORY_ID");
                    final Integer subCatId = (Integer)subCategoryRow.get("SUB_CATEGORY_ID");
                    final Criteria viewParamsCriteria = new Criteria(Column.getColumn("ViewParams", "SUB_CATEGORY_ID"), (Object)subCatId, 0);
                    final Iterator viewParamsItr = dataObject.getRows("ViewParams", viewParamsCriteria);
                    final ArrayList list = new ArrayList();
                    while (viewParamsItr.hasNext()) {
                        final Row row = viewParamsItr.next();
                        if (row != null) {
                            final Hashtable subCategoryHash = new Hashtable();
                            final List columns = row.getColumns();
                            for (int noOfColumns = columns.size(), i = 0; i < noOfColumns; ++i) {
                                final String columnName = columns.get(i);
                                Object columnValue = row.get(columnName);
                                if (columnName.equals("TITLE")) {
                                    columnValue = I18N.getMsg((String)columnValue, new Object[0]);
                                    final String title = (String)row.get(columnName);
                                    if (title.length() > 27) {
                                        subCategoryHash.put("TITLE_TO_DISPLAY", "true");
                                    }
                                }
                                subCategoryHash.put(columnName, columnValue);
                            }
                            subCategoryHash.put("REPORT_CATEGORY", catID);
                            SYMReportUtil.out.log(Level.FINE, "COUNT CRIT :" + subCategoryHash.get("VIEW_ID"));
                            final Criteria countCrit = new Criteria(Column.getColumn("ViewSummary", "VIEW_ID"), subCategoryHash.get("VIEW_ID"), 0);
                            final Integer recordCount = (Integer)dataObject.getValue("ViewSummary", "RECORD_COUNT", countCrit);
                            if (recordCount != null && recordCount >= 0) {
                                subCategoryHash.put("RECORD_COUNT", recordCount);
                            }
                            final Integer viewID = subCategoryHash.get("VIEW_ID");
                            String showReport = "true";
                            final List reportRoles = roleMap.getOrDefault(viewID, null);
                            if (reportRoles != null && !reportRoles.isEmpty()) {
                                final List<Long> tempList = new ArrayList<Long>(userRoleList);
                                tempList.retainAll(reportRoles);
                                if (tempList.isEmpty()) {
                                    showReport = "false";
                                }
                            }
                            subCategoryHash.put("SHOW_REPORT", showReport);
                            try {
                                final String[] classNames = ProductClassLoader.getMultiImplProductClass("DM_REPORT_INVOKER_CLASS");
                                boolean isValid = true;
                                for (final String className : classNames) {
                                    if (!"".equals(className.trim())) {
                                        final ReportsProductInvoker reportsProductInvoker = (ReportsProductInvoker)Class.forName(className).newInstance();
                                        if (!reportsProductInvoker.isValidViewForThisEdition(viewID)) {
                                            isValid = false;
                                            break;
                                        }
                                    }
                                }
                                if (!isValid) {
                                    continue;
                                }
                                list.add(subCategoryHash);
                            }
                            catch (final Exception e) {
                                SYMReportUtil.out.log(Level.WARNING, "No such implementation DM_REPORT_INVOKER_CLASS", e);
                            }
                        }
                    }
                    if (list.size() > 0) {
                        if (listName != null) {
                            categoryHash.put(subCategoryRow.get(listName), list);
                        }
                        else {
                            if (reportSubCategoryColumnNameForListName == null || "".equals(reportSubCategoryColumnNameForListName)) {
                                continue;
                            }
                            categoryHash.put(I18N.getMsg(subCategoryRow.get(reportSubCategoryColumnNameForListName).toString(), new Object[0]), list);
                        }
                    }
                }
            }
        }
        catch (final DataAccessException ex) {
            SYMReportUtil.out.log(Level.INFO, "DataAccessException while getting the view list :", (Throwable)ex);
            throw new SyMException(1001, (Throwable)ex);
        }
        catch (final Exception ex2) {
            SYMReportUtil.out.log(Level.INFO, "Exception while getting the view list :", ex2);
            throw new SyMException(1001, ex2);
        }
        return categoryHash;
    }
    
    public static Map getViewListByReportCategoryList(final SelectQuery query, final Hashtable reportIDList, final String listName, final String reportSubCategoryColumnNameForListName, final HashSet<Integer> reportCategoryList) throws SyMException {
        try {
            final JSONObject jsonObject = JsonUtils.createJson(reportIDList);
            return getViewListByReportCategoryList(query, jsonObject, listName, reportSubCategoryColumnNameForListName, reportCategoryList);
        }
        catch (final JSONException jsonExcep) {
            SYMReportUtil.out.log(Level.INFO, "Exception while creating json in getViewListByReportCategoryList" + jsonExcep);
            jsonExcep.printStackTrace();
            return null;
        }
    }
    
    public static Map getViewListByReportCategoryList(final SelectQuery query, final JSONObject reportIDList, final String listName, final String reportSubCategoryColumnNameForListName, final HashSet<Integer> reportCategoryList) throws SyMException {
        final Map<String, LinkedHashMap> viewList = new LinkedHashMap<String, LinkedHashMap>();
        try {
            Criteria criteria = null;
            if (reportCategoryList != null) {
                criteria = new Criteria(Column.getColumn("ReportCategory", "CATEGORY_ID"), (Object)reportCategoryList.toArray(), 8);
            }
            if (query != null) {
                query.setCriteria(criteria);
                final DataObject dataObject = SyMUtil.getPersistence().get(query);
                for (final Integer reportCategory : reportCategoryList) {
                    final LinkedHashMap categoryHash = getViewList(reportCategory, listName, reportSubCategoryColumnNameForListName, dataObject);
                    String reportName = reportCategory.toString();
                    try {
                        reportName = (String)reportIDList.get(reportName);
                    }
                    catch (final JSONException jsonExp) {
                        SYMReportUtil.out.log(Level.INFO, "Report catogory hash value not found for key : " + reportName);
                    }
                    viewList.put(reportName, categoryHash);
                    SYMReportUtil.out.log(Level.FINE, "Report category hash :\t" + reportName + "\tcategoryHash :" + categoryHash);
                }
            }
        }
        catch (final DataAccessException e) {
            SYMReportUtil.out.log(Level.INFO, "Exception while getting the view list :");
            e.printStackTrace();
            throw new SyMException(1001, (Throwable)e);
        }
        return viewList;
    }
    
    public static Map getViewList(final Hashtable reportID, final String listName, final String reportSubCategoryColumnNameForListName) throws SyMException {
        try {
            final JSONObject jsonObject = JsonUtils.createJson(reportID);
            return getViewList(jsonObject, listName, reportSubCategoryColumnNameForListName);
        }
        catch (final JSONException jsonExcep) {
            SYMReportUtil.out.log(Level.INFO, "Exception while creating json in getViewList" + jsonExcep);
            jsonExcep.printStackTrace();
            return null;
        }
    }
    
    public static Map getViewList(final JSONObject reportID, final String listName, final String reportSubCategoryColumnNameForListName) throws SyMException {
        try {
            SYMReportUtil.out.log(Level.INFO, "***Inside the set view List***");
            if (reportID != null && reportID.length() > 0) {
                final HashSet<Integer> categoryList = getReportCategoryList(reportID);
                Criteria criteria = null;
                if (categoryList != null) {
                    criteria = new Criteria(Column.getColumn("ReportCategory", "CATEGORY_ID"), (Object)categoryList.toArray(), 8);
                }
                final SelectQuery query = getViewQuery(criteria);
                if (categoryList != null && !categoryList.isEmpty()) {
                    return getViewListByReportCategoryList(query, reportID, listName, reportSubCategoryColumnNameForListName, categoryList);
                }
            }
        }
        catch (final Exception e) {
            SYMReportUtil.out.log(Level.WARNING, "Exception while getViewList for Report...", e);
        }
        return null;
    }
    
    @Deprecated
    public static SelectQuery getCRViewListQuery() {
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        return getCRViewQuery(customerId);
    }
    
    public static SelectQuery getCRViewQuery(final Long customerID) {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("CRViewParams"));
        query.addJoin(new Join("CRViewParams", "CRSaveViewDetails", new String[] { "CRSAVEVIEW_ID" }, new String[] { "CRSAVEVIEW_ID" }, 2));
        query.addJoin(new Join("CRViewParams", "CRToCustomerRel", new String[] { "CRSAVEVIEW_ID" }, new String[] { "CR_VIEW_ID" }, 2));
        query.addJoin(new Join("CRSaveViewDetails", "AaaUser", new String[] { "USER_ID" }, new String[] { "USER_ID" }, 2));
        query.addJoin(new Join("CRSaveViewDetails", "CRSubModule", new String[] { "SUB_MODULE_ID" }, new String[] { "SUB_MODULE_ID" }, 2));
        query.addSelectColumn(Column.getColumn((String)null, "*"));
        final SortColumn sortColumn = new SortColumn(Column.getColumn("CRSaveViewDetails", "LAST_MODIFIED_TIME"), false);
        query.addSortColumn(sortColumn);
        Criteria customerCrit = null;
        if (customerID != null) {
            customerCrit = new Criteria(Column.getColumn("CRToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        }
        query.setCriteria(customerCrit);
        return query;
    }
    
    @Deprecated
    public static SelectQuery getCRViewListQuerySpecificToUserId(final Long user_id) {
        final SelectQuery selectQuery = getCRViewListQuery();
        Criteria CRCriteria = selectQuery.getCriteria();
        if (CRCriteria != null) {
            CRCriteria = CRCriteria.and(new Criteria(Column.getColumn("CRSaveViewDetails", "USER_ID"), (Object)user_id, 0));
        }
        else {
            CRCriteria = new Criteria(Column.getColumn("CRSaveViewDetails", "USER_ID"), (Object)user_id, 0);
        }
        selectQuery.setCriteria(CRCriteria);
        return selectQuery;
    }
    
    public static SelectQuery getCRViewQuery(final Long userID, final Long customerID) {
        final SelectQuery selectQuery = getCRViewQuery(customerID);
        final Criteria existingCritera = selectQuery.getCriteria();
        final Criteria userCrit = new Criteria(Column.getColumn("CRSaveViewDetails", "USER_ID"), (Object)userID, 0);
        final Criteria criteria = (existingCritera != null) ? existingCritera.and(userCrit) : userCrit;
        selectQuery.setCriteria(criteria);
        return selectQuery;
    }
    
    public static LinkedHashMap getCRViewList() throws SyMException {
        return getCustomCRViewList(getCRViewListQuery());
    }
    
    public static LinkedHashMap getCRViewList(final Long user_id) throws SyMException {
        return getCustomCRViewList(getCRViewListQuerySpecificToUserId(user_id));
    }
    
    public static LinkedHashMap getCustomCRViewList(final SelectQuery query) throws SyMException {
        LinkedHashMap categoryHash = new LinkedHashMap();
        try {
            final String dbName = DBUtil.getActiveDBName();
            categoryHash = new LinkedHashMap(10);
            final DataObject resultDO = SyMUtil.getPersistence().get(query);
            final Iterator reportList = resultDO.getRows("CRViewParams");
            final ArrayList list = new ArrayList();
            while (reportList.hasNext()) {
                final Row row = reportList.next();
                if (row != null) {
                    String queryFromTable = "";
                    Long view_id = 0L;
                    final Hashtable hash = new Hashtable();
                    final Criteria criteria = new Criteria(Column.getColumn("CRSaveViewDetails", "CRSAVEVIEW_ID"), row.get("CRSAVEVIEW_ID"), 0);
                    final String displayViewName = (String)resultDO.getValue("CRSaveViewDetails", "DISPLAY_CRVIEWNAME", criteria);
                    final String viewName = (String)resultDO.getValue("CRSaveViewDetails", "CRVIEWNAME", criteria);
                    final Long cr_view_id = (Long)row.get("CRSAVEVIEW_ID");
                    final Object objview_id = resultDO.getValue("CRSaveViewDetails", "VIEWID", criteria);
                    final int dbType = (int)resultDO.getValue("CRSaveViewDetails", "DB_TYPE", criteria);
                    final String dbTypeStr = DBConstants.getDBNameByDBType(dbType);
                    if (objview_id != null) {
                        view_id = (Long)objview_id;
                    }
                    if (objview_id == null && !dbTypeStr.equals(dbName)) {
                        continue;
                    }
                    final String action_url = (String)row.get("ACTION_URL");
                    final Object queryObject = resultDO.getValue("CRSaveViewDetails", "QR_QUERY", criteria);
                    if (queryObject != null) {
                        queryFromTable = (String)queryObject;
                    }
                    hash.put("ACTIONURL", action_url);
                    hash.put("CRVIEW_ID", cr_view_id);
                    hash.put("VIEW_ID", view_id);
                    hash.put("QUERY", queryFromTable);
                    hash.put("DISPLAYVIEWNAME", displayViewName);
                    hash.put("VIEWNAME", viewName);
                    list.add(hash);
                }
            }
            if (list.size() > 0) {
                categoryHash.put("CustomReport", list);
            }
        }
        catch (final Exception ex) {
            SYMReportUtil.out.log(Level.WARNING, "Exception while getting CustomReport details...", ex);
            throw new SyMException(1002, "Exception while getting CustomReport details...", ex);
        }
        return categoryHash;
    }
    
    public static HashSet<Integer> getReportCategoryList(final Hashtable reportID) {
        try {
            final JSONObject jsonObject = JsonUtils.createJson(reportID);
            return getReportCategoryList(jsonObject);
        }
        catch (final JSONException jsonExcep) {
            SYMReportUtil.out.log(Level.INFO, "Exception while creating json in getReportCategoryList" + jsonExcep);
            jsonExcep.printStackTrace();
            return null;
        }
    }
    
    public static HashSet<Integer> getReportCategoryList(final JSONObject reportID) {
        HashSet<Integer> categoryList = null;
        try {
            SYMReportUtil.out.log(Level.INFO, "***Inside the get category List***");
            final ArrayList reportIDList = new ArrayList();
            final List roleList = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
            SYMReportUtil.out.log(Level.FINE, "ROLE LIST:" + roleList.toString());
            if (reportID != null && reportID.length() > 0) {
                final Iterator reportIdIterator = reportID.keys();
                while (reportIdIterator.hasNext()) {
                    reportIDList.add(reportIdIterator.next().toString());
                }
                SYMReportUtil.out.log(Level.FINE, "Report ID List to be displayed:" + reportIDList.toString());
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("ReportModuleRelation"));
                query.addSelectColumn(Column.getColumn("ReportModuleRelation", "CATEGORY_ID"));
                query.addSelectColumn(Column.getColumn("ReportModuleRelation", "MODULE_ID"));
                query.addJoin(new Join("ReportModuleRelation", "UMModule", new String[] { "MODULE_ID" }, new String[] { "DC_MODULE_ID" }, 2));
                query.addJoin(new Join("UMModule", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
                Criteria roleCriteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)roleList.toArray(), 8);
                final Criteria categoryCriteria = new Criteria(Column.getColumn("ReportModuleRelation", "CATEGORY_ID"), (Object)reportIDList.toArray(), 8);
                roleCriteria = roleCriteria.and(categoryCriteria);
                query.setCriteria(roleCriteria);
                final DataObject reportCategoryDO = SyMUtil.getPersistence().get(query);
                final String printQuery = RelationalAPI.getInstance().getSelectSQL((Query)query);
                SYMReportUtil.out.log(Level.FINE, "Query to fetch Report List:" + printQuery);
                if (reportCategoryDO != null && !reportCategoryDO.isEmpty()) {
                    categoryList = new HashSet<Integer>();
                    final Iterator itr = reportCategoryDO.get("ReportModuleRelation", "CATEGORY_ID");
                    while (itr.hasNext()) {
                        categoryList.add(itr.next());
                    }
                    SYMReportUtil.out.log(Level.FINE, "CATEGORY LIST :" + categoryList.toString());
                }
            }
        }
        catch (final Exception e) {
            SYMReportUtil.out.log(Level.WARNING, "Exception while getViewList for Report...", e);
        }
        return categoryList;
    }
    
    public static String getReportParamter(final String key) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ReportParams", "PARAM_NAME"), (Object)key, 0);
            final DataObject reportParams = SyMUtil.getPersistence().get("ReportParams", criteria);
            final Row reportParamsRow = reportParams.getRow("ReportParams");
            if (reportParamsRow == null) {
                return null;
            }
            final String paramValue = (String)reportParamsRow.get("PARAM_VALUE");
            return paramValue;
        }
        catch (final Exception ex) {
            SYMReportUtil.out.log(Level.WARNING, "Caught exception while retrieving Report Parameter:" + key + " from DB.", ex);
            return null;
        }
    }
    
    public static void updateReportParameter(final String paramName, final String paramValue) {
        try {
            final Criteria criteria = new Criteria(Column.getColumn("ReportParams", "PARAM_NAME"), (Object)paramName, 0);
            final DataObject reportParams = SyMUtil.getPersistence().get("ReportParams", criteria);
            Row reportParamsRow = reportParams.getRow("ReportParams");
            if (reportParamsRow == null) {
                reportParamsRow = new Row("ReportParams");
                reportParamsRow.set("PARAM_NAME", (Object)paramName);
                reportParamsRow.set("PARAM_VALUE", (Object)paramValue);
                reportParams.addRow(reportParamsRow);
                SYMReportUtil.out.log(Level.INFO, "Report Parameter added in DB  param name: " + paramName + "  param value: " + paramValue);
            }
            else {
                reportParamsRow.set("PARAM_VALUE", (Object)paramValue);
                reportParams.updateRow(reportParamsRow);
                SYMReportUtil.out.log(Level.INFO, "Report Parameter updated in DB:- param name: " + paramName + "  param value: " + paramValue);
            }
            SyMUtil.getPersistence().update(reportParams);
        }
        catch (final Exception ex) {
            SYMReportUtil.out.log(Level.SEVERE, "Caught exception while updating the Report Parameter:" + paramName + " in DB.", ex);
        }
    }
    
    public static void increementParameterValue(final String parameter) {
        int count = 1;
        final String usageCount = getReportParamter(parameter);
        if (usageCount != null) {
            count = Integer.parseInt(usageCount);
            ++count;
        }
        updateReportParameter(parameter, String.valueOf(count));
    }
    
    public static SelectQuery getCRSaveDetailsQuery(final Long userID, final Long customerID, final boolean isAdmin, final boolean isQueryReport) {
        SelectQuery viewQuery;
        if (isAdmin) {
            viewQuery = getCRViewQuery(customerID);
        }
        else {
            viewQuery = getCRViewQuery(userID, customerID);
        }
        final Criteria queryCriteria = isQueryReport ? new Criteria(Column.getColumn("CRSaveViewDetails", "QR_QUERY"), (Object)null, 1) : new Criteria(Column.getColumn("CRSaveViewDetails", "QR_QUERY"), (Object)null, 0);
        final Criteria existingCriteria = viewQuery.getCriteria();
        final Criteria criteria = (existingCriteria == null) ? queryCriteria : existingCriteria.and(queryCriteria);
        viewQuery.setCriteria(criteria);
        return viewQuery;
    }
    
    public static DataObject getCRSaveDetailsDO(final List crSaveViewIDs, final Long userID, final Long customerID, final boolean isAdmin, final boolean isQueryReport) throws DataAccessException {
        final SelectQuery viewQuery = getCRSaveDetailsQuery(userID, customerID, isAdmin, isQueryReport);
        final Criteria existingCriteria = viewQuery.getCriteria();
        final Criteria viewIDCrit = new Criteria(Column.getColumn("CRSaveViewDetails", "CRSAVEVIEW_ID"), (Object)crSaveViewIDs.toArray(), 8);
        viewQuery.setCriteria(existingCriteria.and(viewIDCrit));
        DataObject crDetailsDO = null;
        try {
            crDetailsDO = SyMUtil.getPersistence().get(viewQuery);
        }
        catch (final DataAccessException ex) {
            SYMReportUtil.out.log(Level.WARNING, "Exception in SYMReportUtil.getCRSaveDetailsDO(),crSaveViewIDs: " + crSaveViewIDs.toString() + ",userID: " + userID + ",customerID: " + customerID, (Throwable)ex);
            throw ex;
        }
        return crDetailsDO;
    }
    
    public static DataObject getCustomReportCriteriaDO(final List crSaveViewIDs) throws DataAccessException {
        final SelectQuery criteriaQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRToCriteriaRel"));
        final SortColumn sortColumn = new SortColumn(Column.getColumn("CriteriaColumnDetails", "CRITERIA_ORDER"), true);
        criteriaQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        criteriaQuery.addJoin(new Join("CRToCriteriaRel", "CriteriaColumnDetails", new String[] { "CRITERIA_COLUMN_ID" }, new String[] { "CRITERIA_COLUMN_ID" }, 2));
        criteriaQuery.setCriteria(new Criteria(Column.getColumn("CRToCriteriaRel", "CRSAVEVIEW_ID"), (Object)crSaveViewIDs.toArray(), 8));
        criteriaQuery.addSortColumn(sortColumn);
        DataObject criteriaDO;
        try {
            criteriaDO = SyMUtil.getPersistence().get(criteriaQuery);
        }
        catch (final DataAccessException ex) {
            SYMReportUtil.out.log(Level.WARNING, "Exception in SYMReportUtil.getCustomReportCriteriaDO(),crSaveViewIDs: " + crSaveViewIDs.toString(), (Throwable)ex);
            throw ex;
        }
        return criteriaDO;
    }
    
    public static boolean isCustomReportNameExists(final String reportName, final Long userID, final Long customerID, final boolean isAdmin, final boolean isQueryReport) {
        boolean status = false;
        final SelectQuery nameQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRSaveViewDetails"));
        final Criteria nameCriteria = new Criteria(Column.getColumn("CRSaveViewDetails", "DISPLAY_CRVIEWNAME"), (Object)reportName, 0, false);
        Criteria queryCriteria;
        if (!isQueryReport) {
            queryCriteria = new Criteria(Column.getColumn("CRSaveViewDetails", "QR_QUERY"), (Object)null, 0);
        }
        else {
            queryCriteria = new Criteria(Column.getColumn("CRSaveViewDetails", "QR_QUERY"), (Object)null, 1);
        }
        nameQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        nameQuery.setCriteria(queryCriteria.and(nameCriteria));
        DataObject nameDO = null;
        try {
            nameDO = SyMUtil.getPersistence().get(nameQuery);
        }
        catch (final DataAccessException ex) {
            SYMReportUtil.out.log(Level.WARNING, "Exception in SYMReportUtil.isCustomReportNameExists(),reportName: " + reportName + ",userID: " + userID + ",customerID: " + customerID + ",isAdmin: " + isAdmin + ",isQueryReport: " + isQueryReport, (Throwable)ex);
        }
        if (nameDO == null || !nameDO.isEmpty()) {
            status = true;
        }
        return status;
    }
    
    private static Map<Integer, List<Long>> getRolesForViews() {
        final Map<Integer, List<Long>> roleMap = new HashMap<Integer, List<Long>>();
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RoleToViewParamsMapping"));
            selectQuery.addSelectColumn(new Column("RoleToViewParamsMapping", "ROLE_ID"));
            selectQuery.addSelectColumn(new Column("RoleToViewParamsMapping", "VIEW_ID"));
            final DataObject roleObject = SyMUtil.getPersistence().get(selectQuery);
            if (!roleObject.isEmpty()) {
                final Iterator<Row> rolesToView = roleObject.getRows("RoleToViewParamsMapping");
                while (rolesToView.hasNext()) {
                    final Row roleRow = rolesToView.next();
                    final int viewID = (int)roleRow.get("VIEW_ID");
                    if (!roleMap.containsKey(viewID)) {
                        roleMap.put(viewID, new ArrayList<Long>());
                    }
                    final Long roleId = (Long)roleRow.get("ROLE_ID");
                    final List roleList = roleMap.get(viewID);
                    roleList.add(roleId);
                    roleMap.put(viewID, roleList);
                }
            }
        }
        catch (final Exception e) {
            SYMReportUtil.out.log(Level.WARNING, "Cannot fetch report roles ", e);
        }
        return roleMap;
    }
    
    static {
        SYMReportUtil.className = SYMReportUtil.class.getName();
        SYMReportUtil.out = Logger.getLogger(SYMReportUtil.className);
    }
}
