package com.me.mdm.api.home;

import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Map;
import java.util.TreeMap;
import com.adventnet.ds.query.SortColumn;
import org.json.JSONArray;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.core.ManagedUserHandler;
import com.me.mdm.server.enrollment.task.InactiveDevicePolicyTask;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Column;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.tracker.mics.MICSFeatureTrackerUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class MDMHomePageAPIRequestHandler extends ApiRequestHandler
{
    private static final String GETTING_STARTED = "show_getting_started";
    private static final String SUMMARY = "summary";
    private static final String SERVER_SECURITY = "server_security";
    private static final String HOME_GETTING_STARTED_CLOSE = "HOME_GETTING_STARTED_CLOSE";
    private static final String SHOW_SECURITY_WIDGET = "show_security_widget";
    private static final String SECURE_PERCENT = "secure_percentage";
    private static final String GRAPH = "graph";
    private static final String GRAPH_ID = "graph_id";
    private static final String DRAG_GRAPH_ID = "drag_graph_id";
    private static final String DROP_GRAPH_ID = "drop_graph_id";
    private static final String GRAPH_NAME = "graph_name";
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final Long customerId = APIUtil.optCustomerID(apiRequest.toJSONObject());
        final Long userId = APIUtil.getUserID(apiRequest.toJSONObject());
        final Long loginId = APIUtil.getLoginID(apiRequest.toJSONObject());
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final JSONObject homeResponse = this.getHomeJSON(customerId, userId, loginId);
            if (CustomerInfoUtil.isSAS) {
                final JSONObject requestJson = new JSONObject();
                requestJson.put("remote_addr", (Object)apiRequest.httpServletRequest.getRemoteAddr());
                requestJson.put("is_admin_user", apiRequest.httpServletRequest.isUserInRole("Common_Write"));
                homeResponse.put("license_details", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getLicenseMessages(requestJson));
            }
            responseJSON.put("RESPONSE", (Object)homeResponse);
            MICSFeatureTrackerUtil.addHomePageAccess();
            responseJSON.put("RESPONSE", (Object)homeResponse);
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in MDMHomePageAPIRequestHandler ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public void addHomePageAccessToMICS() {
        try {
            this.logger.log(Level.INFO, "MICS home page tracking");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("feature", (Object)"HomePage");
            MDMApiFactoryProvider.getMicsTrackingAPI().postDataToMicsForMailer(jsonObject);
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception while tracking MICS home page ", e);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        final Long customerId = APIUtil.getCustomerID(apiRequest.toJSONObject());
        final Long loginId = APIUtil.getLoginID(apiRequest.toJSONObject());
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject().getJSONObject("msg_body");
            final long dragId = requestJSON.optLong("drag_graph_id");
            final long dropId = requestJSON.optLong("drop_graph_id");
            this.updateHomeGraphDisplayOrder(customerId, loginId, dragId, dropId);
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)new JSONObject());
            return responseJSON;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in MDMHomePageAPIRequestHandler ", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private void updateHomeGraphDisplayOrder(final Long customerID, final Long loginID, final Long dragID, final Long dropID) throws DataAccessException {
        final SelectQuery sQuery = this.getHomeGraphQuery(loginID, customerID);
        sQuery.addSelectColumn(new Column("HomePageSummary", "*"));
        sQuery.addSelectColumn(new Column("HomeTabModuleOrder", "*"));
        sQuery.addSelectColumn(new Column("HomePageSummaryDisplayOrder", "*"));
        final DataObject graphDO = MDMUtil.getPersistence().get(sQuery);
        if (!graphDO.isEmpty()) {
            if (!graphDO.containsTable("HomePageSummaryDisplayOrder")) {
                final Iterator summaryRows = graphDO.getRows("HomePageSummary");
                while (summaryRows.hasNext()) {
                    final Row summaryRow = summaryRows.next();
                    final Row summaryDisplayOrderRow = new Row("HomePageSummaryDisplayOrder");
                    summaryDisplayOrderRow.set("LOGIN_ID", (Object)loginID);
                    summaryDisplayOrderRow.set("SUMMARY_ID", summaryRow.get("SUMMARY_ID"));
                    summaryDisplayOrderRow.set("DISPLAY_ORDER", summaryRow.get("DISPLAY_ORDER"));
                    summaryDisplayOrderRow.set("CUSTOMER_ID", (Object)customerID);
                    graphDO.addRow(summaryDisplayOrderRow);
                }
            }
            final Criteria dragCri = new Criteria(new Column("HomePageSummaryDisplayOrder", "SUMMARY_ID"), (Object)dragID, 0);
            final Row dragRow = graphDO.getRow("HomePageSummaryDisplayOrder", dragCri);
            final Long dragOrder = (Long)dragRow.get("DISPLAY_ORDER");
            final Criteria dropCri = new Criteria(new Column("HomePageSummaryDisplayOrder", "SUMMARY_ID"), (Object)dropID, 0);
            final Row dropRow = graphDO.getRow("HomePageSummaryDisplayOrder", dropCri);
            final Long dropOrder = (Long)dropRow.get("DISPLAY_ORDER");
            dragRow.set("DISPLAY_ORDER", (Object)dropOrder);
            dropRow.set("DISPLAY_ORDER", (Object)dragOrder);
            graphDO.updateRow(dragRow);
            graphDO.updateRow(dropRow);
            MDMUtil.getPersistence().update(graphDO);
        }
    }
    
    private JSONObject getHomeJSON(final Long customerId, final Long userId, final Long loginId) throws JSONException {
        final JSONObject responseJson = new JSONObject();
        responseJson.put("show_getting_started", this.isGettingStartedClosed(userId, "HOME_GETTING_STARTED_CLOSE"));
        int enrolledDeviceCount = 0;
        try {
            if (customerId != -1L) {
                enrolledDeviceCount = MDMEnrollmentUtil.getInstance().getEnrolledDeviceCount(customerId);
                final JSONObject summaryDetails = new JSONObject();
                summaryDetails.put("activeDevice", MDMEnrollmentUtil.getInstance().getEnrolledDeviceCount(customerId));
                summaryDetails.put("pendingDevice", MDMEnrollmentUtil.getInstance().getYetToEnrollRequestCount(customerId));
                summaryDetails.put("inactiveDevice", new InactiveDevicePolicyTask().getInactiveDeviceCounts(customerId));
                summaryDetails.put("uniqueUser", ManagedUserHandler.getInstance().getManagedUsersWithDevicesCount(customerId));
                summaryDetails.put("blackListApp", MDMUtil.getInstance().getBlackListAppCount(CustomerInfoUtil.getInstance().getCustomerId()));
                responseJson.put("summary", (Object)summaryDetails);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in setHomePageMessages", e);
        }
        if (customerId != 1L & enrolledDeviceCount != 0) {
            responseJson.put("server_security", (Object)this.getGdprWidgetProps(customerId, enrolledDeviceCount));
        }
        responseJson.put("graph", (Object)this.getHomePageGraphDetails(loginId, customerId));
        return responseJson;
    }
    
    private JSONObject getGdprWidgetProps(final Long customerID, final int enrolledDeviceCount) {
        final JSONObject json = new JSONObject();
        boolean showGdprwidget = false;
        long securePerc = 0L;
        try {
            String showGdprWidgetStr = CustomerParamsHandler.getInstance().getParameterValue("showGdprWidget", (long)customerID);
            showGdprWidgetStr = ((showGdprWidgetStr == null) ? "false" : showGdprWidgetStr);
            if (!showGdprWidgetStr.equalsIgnoreCase("--")) {
                showGdprwidget = Boolean.parseBoolean(showGdprWidgetStr);
            }
            else {
                final String licenseType = LicenseProvider.getInstance().getLicenseType();
                if (!licenseType.equalsIgnoreCase("T") || (licenseType.equalsIgnoreCase("T") && enrolledDeviceCount >= 10)) {
                    showGdprwidget = true;
                    CustomerParamsHandler.getInstance().addOrUpdateParameter("showGdprWidget", "true", (long)customerID);
                }
                else {
                    showGdprwidget = false;
                    CustomerParamsHandler.getInstance().addOrUpdateParameter("showGdprWidget", "false", (long)customerID);
                }
            }
            securePerc = MDMApiFactoryProvider.getMDMGDPRSettingsAPI().getSecureSettings(customerID).get("SECURE_PERCENTAGE");
            json.put("show_security_widget", showGdprwidget);
            json.put("secure_percentage", securePerc);
        }
        catch (final Exception exp) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception while getting security data", exp);
        }
        return json;
    }
    
    private boolean isGettingStartedClosed(final Long userID, final String parameter) {
        boolean close = false;
        try {
            final String closeDB = SyMUtil.getUserParameter(userID, parameter);
            if (closeDB != null) {
                close = Boolean.valueOf(closeDB);
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in  isGettingStartedClosed  : ", exp);
        }
        return close;
    }
    
    private JSONArray getHomePageGraphDetails(final Long loginID, final Long customerId) {
        final JSONArray graphJSONArray = new JSONArray();
        try {
            final SelectQuery query = this.getHomeGraphQuery(loginID, customerId);
            query.addSelectColumn(new Column("HomePageSummary", "SUMMARY_ID"));
            query.addSelectColumn(new Column("HomePageSummary", "VIEW_NAME"));
            query.addSelectColumn(new Column("HomePageSummary", "DISPLAY_ORDER"));
            final Column personalizedColumn = new Column("HomePageSummaryDisplayOrder", "DISPLAY_ORDER");
            personalizedColumn.setColumnAlias("PERSONALIZE_ORDER");
            query.addSelectColumn(personalizedColumn);
            final SortColumn sortColumn1 = new SortColumn(new Column("HomePageSummary", "DISPLAY_ORDER"), true);
            final SortColumn sortColumn2 = new SortColumn(personalizedColumn, true);
            query.addSortColumn(sortColumn2);
            query.addSortColumn(sortColumn1);
            query.setDistinct(true);
            final org.json.simple.JSONArray graphsArray = MDMUtil.executeSelectQuery(query);
            final Map<Long, JSONObject> orderedMap = new TreeMap<Long, JSONObject>();
            for (int i = 0; i < graphsArray.size(); ++i) {
                final JSONObject graphJSON = new JSONObject();
                final org.json.simple.JSONObject jsObject = (org.json.simple.JSONObject)graphsArray.get(i);
                final String viewName = (String)((jsObject.get((Object)"VIEW_NAME") != null) ? jsObject.get((Object)"VIEW_NAME") : "");
                graphJSON.put("graph_id", (Object)jsObject.get((Object)"SUMMARY_ID"));
                graphJSON.put("graph_name", (Object)viewName);
                final long order = (long)((jsObject.get((Object)"PERSONALIZE_ORDER") != null) ? jsObject.get((Object)"PERSONALIZE_ORDER") : ((long)jsObject.get((Object)"DISPLAY_ORDER")));
                if (viewName.startsWith("mdm")) {
                    orderedMap.put(order, graphJSON);
                }
            }
            for (final JSONObject json : orderedMap.values()) {
                graphJSONArray.put((Object)json);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in getHomePageGraphDetails", e);
        }
        return graphJSONArray;
    }
    
    private SelectQuery getHomeGraphQuery(final Long loginID, final Long customerId) {
        final LicenseProvider licenseProvider = LicenseProvider.getInstance();
        final String edition = licenseProvider.getMDMLicenseAPI().getMDMLiceseEditionType();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("UserSummaryMapping"));
        query.addJoin(new Join("UserSummaryMapping", "SummaryGroup", new String[] { "SUMMARYGROUP_ID" }, new String[] { "SUMMARYGROUP_ID" }, 2));
        query.addJoin(new Join("SummaryGroup", "HomePageSummary", new String[] { "SUMMARY_ID" }, new String[] { "SUMMARY_ID" }, 2));
        query.addJoin(new Join("HomePageSummary", "HomeTabModuleOrder", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
        query.addJoin(new Join("HomeTabModuleOrder", "DCUserModuleExtn", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
        query.addJoin(new Join("DCUserModuleExtn", "UMModule", new String[] { "MODULE_ID" }, new String[] { "DC_MODULE_ID" }, 2));
        final Criteria summaryCriteria = new Criteria(Column.getColumn("HomePageSummary", "SUMMARY_ID"), (Object)Column.getColumn("HomePageSummaryDisplayOrder", "SUMMARY_ID"), 0);
        final Criteria customerCriteria = new Criteria(Column.getColumn("HomePageSummaryDisplayOrder", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria logInCriteria = new Criteria(Column.getColumn("HomePageSummaryDisplayOrder", "LOGIN_ID"), (Object)Column.getColumn("UserSummaryMapping", "LOGIN_ID"), 0);
        query.addJoin(new Join("HomePageSummary", "HomePageSummaryDisplayOrder", summaryCriteria.and(customerCriteria).and(logInCriteria), 1));
        Criteria c1 = new Criteria(Column.getColumn("UserSummaryMapping", "LOGIN_ID"), (Object)loginID, 0);
        final String s = edition;
        licenseProvider.getMDMLicenseAPI();
        if (s.equalsIgnoreCase("Standard")) {
            c1 = c1.and(new Criteria(Column.getColumn("UMModule", "LICENSE_TYPE"), (Object)"S", 12));
        }
        query.setCriteria(c1);
        return query;
    }
}
