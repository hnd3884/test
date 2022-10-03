package com.me.mdm.api.reports;

import java.util.List;
import com.adventnet.sym.server.mdm.graphs.MDMGraphDataProducerImpl;
import java.util.ArrayList;
import java.util.HashMap;
import com.me.mdm.server.profiles.ProfileFacade;
import java.util.Collection;
import java.util.Arrays;
import com.me.mdm.server.customgroup.GroupFacade;
import java.util.Map;
import com.me.mdm.server.apps.blacklist.BlacklistAppHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ReportsApiHandler extends ApiRequestHandler
{
    private Logger logger;
    public static final String DEVICE_TYPE = "device_type";
    public static final String DEVICE_OWNER_TYPE = "device_owner_type";
    public static final String DEVICE_PLATFORM_TYPE = "device_platform_type";
    public static final String DEVICE_SCAN_SUMMARY = "device_scan_summary";
    public static final String MDM_APP_SUMMARY = "mdm_app_summary";
    public static final String PROFILE_EXECUTION_SUMMARY = "profile_execution_summary";
    public static final String GROUP_PROFILE_EXECUTION_STATUS = "group_profile_execution_status";
    public static final String INACTIVE_DEVICES = "inactive_devices";
    public static final String MDM_HOME_APP_SUMMARY = "mdm_home_app_summary";
    public static final String ANNOUNCEMENT_EXECUTION_SUMMARY = "announcement_execution_summary";
    public static final String MDM_GROUP_ACTION_EXECUTION_SUMMARY = "mdmGroupActionSummary";
    
    public ReportsApiHandler() {
        this.logger = Logger.getLogger("InventoryLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject response = this.getReport(apiRequest);
        final JSONObject result = new JSONObject();
        try {
            result.put("status", 200);
            result.put("RESPONSE", (Object)response);
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Error occurred in ReportsApiHandler.doGet", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return result;
    }
    
    private String getReportName(final APIRequest apiRequest) throws APIHTTPException {
        try {
            return apiRequest.pathInfo.split("/")[2];
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "error occurred in getReportName()", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getReport(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final String reportName = this.getReportName(apiRequest);
            final JSONObject requestJson = apiRequest.toJSONObject();
            final Long customerID = APIUtil.getCustomerID(requestJson);
            String localReportName = null;
            HashMap parameterMap = null;
            Long profileId = null;
            final String s = reportName;
            switch (s) {
                case "device_type": {
                    localReportName = "mdmdevices";
                    break;
                }
                case "device_owner_type": {
                    localReportName = "mdmBYODSummary";
                    break;
                }
                case "device_platform_type": {
                    localReportName = "mdmplatform";
                    break;
                }
                case "device_scan_summary": {
                    localReportName = "mdmdevicescan";
                    break;
                }
                case "mdm_app_summary": {
                    final HashMap appSummary = new BlacklistAppHandler().appSummaryDetails(customerID);
                    final Integer discoveredCount = appSummary.getOrDefault("discoveredCount", 0);
                    final Integer blacklistCount = appSummary.getOrDefault("blacklistCount", 0);
                    appSummary.put("discovered_count", discoveredCount);
                    appSummary.remove("discoveredCount");
                    appSummary.put("managed_count", appSummary.get("managedCount"));
                    appSummary.remove("managedCount");
                    appSummary.put("devices_with_blacklisted_apps", appSummary.get("devicesWithBlacklistedApps"));
                    appSummary.remove("devicesWithBlacklistedApps");
                    appSummary.put("blacklist_count", blacklistCount);
                    appSummary.remove("blacklistCount");
                    appSummary.put("non_blacklist_count", discoveredCount - blacklistCount);
                    return new JSONObject((Map)appSummary);
                }
                case "group_profile_execution_status": {
                    localReportName = "mdmprofilestatus";
                    final Long groupId = APIUtil.getLongFilter(requestJson, "group_id");
                    new GroupFacade().validateIfGroupsExists(Arrays.asList(groupId), APIUtil.getCustomerID(requestJson));
                    profileId = APIUtil.getLongFilter(requestJson, "profile_id");
                    new ProfileFacade().validateIfProfileExists(profileId, APIUtil.getCustomerID(requestJson));
                    parameterMap = new HashMap();
                    parameterMap.put("groupId", groupId);
                    parameterMap.put("profileId", profileId);
                    parameterMap.put("isAPI", Boolean.TRUE);
                    break;
                }
                case "profile_execution_summary": {
                    localReportName = "mdmprofileexecstatus";
                    profileId = APIUtil.getLongFilter(requestJson, "profile_id");
                    new ProfileFacade().validateIfProfileExists(profileId, APIUtil.getCustomerID(requestJson));
                    parameterMap = new HashMap();
                    parameterMap.put("profileId", profileId);
                    parameterMap.put("isAPI", Boolean.TRUE);
                    break;
                }
                case "inactive_devices": {
                    localReportName = "mdmLastSeenBreakdownCount";
                    final Long startRange = APIUtil.getLongFilter(requestJson, "start_range");
                    final Long endRange = APIUtil.getLongFilter(requestJson, "end_range");
                    parameterMap = new HashMap();
                    parameterMap.put("startRange", startRange);
                    parameterMap.put("endRange", endRange);
                    parameterMap.put("customerID", customerID);
                    break;
                }
                case "mdm_home_app_summary": {
                    final List requiredData = new ArrayList();
                    requiredData.add("discoveredCount");
                    requiredData.add("blacklistCount");
                    final HashMap appSummary = new BlacklistAppHandler().appSummaryDetails(customerID, requiredData);
                    final Integer discoveredCount = appSummary.getOrDefault("discoveredCount", 0);
                    final Integer blacklistCount = appSummary.getOrDefault("blacklistCount", 0);
                    appSummary.put("discovered_count", discoveredCount);
                    appSummary.remove("discoveredCount");
                    appSummary.put("blacklist_count", blacklistCount);
                    appSummary.remove("blacklistCount");
                    appSummary.put("non_blacklist_count", discoveredCount - blacklistCount);
                    return new JSONObject((Map)appSummary);
                }
                case "announcement_execution_summary": {
                    localReportName = "announcement_execution_summary";
                    final Long announcementId = APIUtil.getLongFilter(requestJson, "announcement_id");
                    parameterMap = new HashMap();
                    parameterMap.put("announcementId", announcementId);
                    parameterMap.put("isAPI", Boolean.TRUE);
                    break;
                }
                case "mdmGroupActionSummary": {
                    localReportName = "mdmGroupActionSummary";
                    final Long actionGroupId = APIUtil.getLongFilter(requestJson, "group_id");
                    final Long groupActionId = APIUtil.getLongFilter(requestJson, "group_action_id");
                    final Integer actionType = APIUtil.getIntegerFilter(requestJson, "action_type");
                    parameterMap = new HashMap();
                    parameterMap.put("groupId", actionGroupId);
                    parameterMap.put("groupActionId", groupActionId);
                    parameterMap.put("actionType", actionType);
                    parameterMap.put("isAPI", Boolean.TRUE);
                    break;
                }
                default: {
                    throw new APIHTTPException("COM0008", new Object[] { reportName });
                }
            }
            final HashMap map = new MDMGraphDataProducerImpl().getGraphValues(localReportName, parameterMap);
            return new JSONObject((Map)map);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "exception occurred in getReport", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
