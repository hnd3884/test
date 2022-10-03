package com.me.mdm.server.reports;

import java.util.HashMap;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.me.mdm.server.customer.MDMCustomerInfoUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.api.APIUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MDMReportsFacade
{
    protected static Logger logger;
    
    public JSONArray getReportParamValue(final JSONObject apiRequest) throws Exception {
        final Long customerId = APIUtil.getCustomerID(apiRequest);
        final long reportId = APIUtil.getResourceID(apiRequest, "report_id");
        final String paramName = APIUtil.getStringFilter(apiRequest, "name");
        JSONArray jsonArray = new JSONArray();
        final JSONObject optionalParams = APIUtil.getFilters(apiRequest);
        if (MDMStringUtils.isEmpty(paramName)) {
            MDMReportsFacade.logger.log(Level.WARNING, "Parameter name should be provided");
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        final List<JSONObject> paramValueList = MDMReportsMgmtHandler.getInstance().getReportParamValue((int)reportId, paramName, customerId, optionalParams);
        if (paramValueList != null && !paramValueList.isEmpty()) {
            jsonArray = JSONUtil.getInstance().convertListToJSONArray(paramValueList);
        }
        return jsonArray;
    }
    
    public JSONObject getMDMPreDefinedReports(final JSONObject request) {
        try {
            final Hashtable reportID = MDMReportUtil.getReportNameList();
            final Map<String, LinkedHashMap> viewList = MDMReportUtil.getViewList(reportID, (String)null, MDMReportUtil.viewListWithSubCategoryName());
            final LinkedHashMap mdmList = viewList.getOrDefault("MDM_LIST", null);
            final JSONObject resultJSON = new JSONObject();
            if (mdmList != null) {
                final JSONArray responseArray = new JSONArray();
                for (final String reportsCategory : mdmList.keySet()) {
                    final JSONArray resultArray = new JSONArray();
                    final JSONArray listArray = new JSONArray((Collection)mdmList.get(reportsCategory));
                    final JSONObject parentObject = new JSONObject();
                    parentObject.put("title", (Object)reportsCategory);
                    for (int i = 0; i < listArray.length(); ++i) {
                        final JSONObject viewData = listArray.getJSONObject(i);
                        final Integer viewId = Integer.valueOf(viewData.get("VIEW_ID").toString());
                        if (Boolean.valueOf(viewData.optString("SHOW_REPORT", "true"))) {
                            if (viewId != 40106) {
                                if (viewId != 40503) {
                                    if ((viewId != 40501 && viewId != 40502) || MDMUtil.getInstance().isGeoTrackingEnabled()) {
                                        if (viewId == 40501) {
                                            viewData.remove("TITLE");
                                            viewData.put("TITLE", (Object)I18N.getMsg("mdm.reports.location_details_and_hist", new Object[0]));
                                        }
                                        final String description = I18N.getMsg((String)viewData.get("DESCRIPTION"), new Object[0]);
                                        final JSONObject reportData = new JSONObject();
                                        reportData.put("view_id", viewData.get("VIEW_ID"));
                                        reportData.put("description", (Object)description);
                                        reportData.put("view_name", viewData.get("VIEW_NAME"));
                                        reportData.put("order_of_display", viewData.get("ORDER_OF_DISPLAY"));
                                        reportData.put("title", viewData.get("TITLE"));
                                        reportData.put("action_url", (Object)MDMRestAPIFactoryProvider.getAPIUtil().getPredefinedURL(String.valueOf(viewData.get("ACTION_URL")), APIUtil.getStringFilter(request, "tab")));
                                        resultArray.put((Object)reportData);
                                    }
                                }
                            }
                        }
                    }
                    parentObject.put("children", (Object)resultArray);
                    if (resultArray.length() > 0) {
                        responseArray.put((Object)parentObject);
                    }
                }
                resultJSON.put("mdm_list", (Object)responseArray);
            }
            return resultJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            MDMReportsFacade.logger.log(Level.SEVERE, "error in getting MDM Pre-defined reports...", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getScheduledReportsList() {
        try {
            final Hashtable reportID = MDMReportUtil.getReportNameList();
            final Map<String, LinkedHashMap> viewList = MDMReportUtil.getViewList(reportID, (String)null, MDMReportUtil.viewListWithSubCategoryName());
            final LinkedHashMap mdmList = viewList.getOrDefault("MDM_LIST", null);
            final JSONObject resultJSON = new JSONObject();
            if (mdmList != null) {
                final JSONArray responseArray = new JSONArray();
                for (final String reportsCategory : mdmList.keySet()) {
                    final JSONArray listArray = new JSONArray((Collection)mdmList.get(reportsCategory));
                    for (int i = 0; i < listArray.length(); ++i) {
                        final JSONObject viewData = listArray.getJSONObject(i);
                        if (Boolean.valueOf(viewData.optString("SHOW_REPORT", "true"))) {
                            final JSONObject reportData = new JSONObject();
                            reportData.put("view_id", viewData.get("VIEW_ID"));
                            reportData.put("view_name", viewData.get("VIEW_NAME"));
                            reportData.put("title", viewData.get("TITLE"));
                            responseArray.put((Object)reportData);
                        }
                    }
                }
                resultJSON.put("mdm_list", (Object)responseArray);
            }
            return resultJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            MDMReportsFacade.logger.log(Level.SEVERE, "error in getting scheduled reports", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getQueryReportList(final JSONObject apiRequest) {
        final Long loginID = APIUtil.getLoginID(apiRequest);
        final Long userID = APIUtil.getUserID(apiRequest);
        final boolean isAdmin = DMUserHandler.isUserInAdminRole(loginID);
        final JSONObject responseJSON = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        try {
            LinkedHashMap reportMap;
            if (isAdmin) {
                reportMap = MDMReportUtil.getCRViewList();
            }
            else {
                reportMap = MDMReportUtil.getCRViewList(userID);
            }
            if (reportMap != null && reportMap.size() > 0) {
                final List queryReportList = reportMap.get("CustomReport");
                final Iterator<Hashtable> reportList = (Iterator<Hashtable>)queryReportList.iterator();
                while (reportList.hasNext()) {
                    final JSONObject queryObject = new JSONObject();
                    final Hashtable table = reportList.next();
                    queryObject.put("display_view_name", table.get("DISPLAYVIEWNAME"));
                    queryObject.put("view_name", table.get("VIEWNAME"));
                    queryObject.put("cr_view_id", table.get("CRVIEW_ID"));
                    queryObject.put("query", table.get("QUERY"));
                    jsonArray.put((Object)queryObject);
                }
            }
            responseJSON.put("mdm_list", (Object)jsonArray);
            return responseJSON;
        }
        catch (final Exception e) {
            MDMReportsFacade.logger.log(Level.WARNING, "Issue on fetching query reports", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public String getReportDownloadURL(final JSONObject apiRequest) throws Exception {
        final String reportId = APIUtil.getResourceIDString(apiRequest, "report_id");
        final Long loginId = APIUtil.getLoginID(apiRequest);
        final Long userId = APIUtil.getUserID(apiRequest);
        String path = null;
        if (MDMStringUtils.isEmpty(reportId)) {
            throw new APIHTTPException("COM0001", new Object[0]);
        }
        if (!DMUserHandler.isUserInAdminRole(loginId)) {
            final List customerIds = (List)MDMCustomerInfoUtil.getInstance().getCustomerDetailsForUser(userId).stream().map(map -> map.get("CUSTOMER_ID")).collect(Collectors.toList());
            final Criteria customerCriteria = new Criteria(new Column("ScheduleBackupStatus", "CUSTOMER_ID"), (Object)customerIds.toArray(), 8);
            path = this.getPublishedReportDetails(reportId, customerCriteria);
        }
        else {
            path = this.getPublishedReportDetails(reportId, null);
        }
        if (MDMStringUtils.isEmpty(path)) {
            MDMReportsFacade.logger.log(Level.WARNING, "File not found under name {0}", reportId);
            throw new APIHTTPException("COM0001", new Object[0]);
        }
        return path;
    }
    
    private String getPublishedReportDetails(final String reportID, final Criteria criteria) throws DataAccessException {
        String path = null;
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("PublishedReportDetails"));
        selectQuery.addJoin(new Join("PublishedReportDetails", "ScheduleBackupStatus", new String[] { "SCHEDULE_BACKUP_STATUS_ID" }, new String[] { "SCHEDULE_BACKUP_STATUS_ID" }, 2));
        final Criteria reportCriteria = new Criteria(new Column("PublishedReportDetails", "REPORT_ID"), (Object)reportID, 0);
        selectQuery.setCriteria(reportCriteria);
        if (criteria != null) {
            selectQuery.setCriteria(criteria.and(reportCriteria));
        }
        selectQuery.addSelectColumn(new Column("PublishedReportDetails", "REPORT_ID"));
        selectQuery.addSelectColumn(new Column("PublishedReportDetails", "REPORT_PATH"));
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            final Row row = dataObject.getRow("PublishedReportDetails");
            path = String.valueOf(row.get("REPORT_PATH"));
        }
        return path;
    }
    
    static {
        MDMReportsFacade.logger = Logger.getLogger("MDMConfigLogger");
    }
}
