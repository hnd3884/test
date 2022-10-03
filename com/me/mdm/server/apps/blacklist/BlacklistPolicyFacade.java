package com.me.mdm.server.apps.blacklist;

import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.mdm.server.device.DeviceFacade;
import com.adventnet.sym.server.mdm.apps.AppSettingsDataHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Map;
import com.me.mdm.api.paging.PagingUtil;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupingHandler;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import com.me.mdm.server.tracker.mics.MICSBlacklistFeatureController;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.Collection;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import java.util.logging.Logger;

public class BlacklistPolicyFacade
{
    private static final String APP_GROUP_ID = "appGroupID";
    private static final String APP_GROUP_ID_LIST = "app_group_ids";
    private static final String RESOURCE_LIST = "resource_ids";
    private static final String APP_NAME = "appName";
    private static final String IDENTIFIER = "identifier";
    private static final String PLATFORM = "platform";
    private static final String APPS = "apps";
    private static final String RESOURCE_ID = "resourceId";
    private static final String STATUS = "status";
    private Logger logger;
    
    public BlacklistPolicyFacade() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public JSONObject getAppGroupsForBlacklisting(final JSONObject jsonObject) throws Exception {
        String appName = null;
        Integer platform = null;
        String bundleID = null;
        final JSONObject msgHeader = jsonObject.optJSONObject("msg_header");
        if (msgHeader != null) {
            final JSONObject filters = msgHeader.optJSONObject("filters");
            if (filters != null) {
                if (filters.has("query")) {
                    appName = filters.optString("query");
                }
                if (filters.has("platform")) {
                    platform = filters.optInt("platform");
                }
                if (filters.has("identifier")) {
                    bundleID = filters.optString("identifier");
                }
            }
        }
        final Long customerId = APIUtil.getCustomerID(jsonObject);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "MacAppProperties", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 1));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"));
        selectQuery.addSelectColumn(new Column("MacAppProperties", "*"));
        Criteria criteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        if (appName != null) {
            criteria = criteria.and(new Criteria(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"), (Object)appName, 2));
        }
        if (bundleID != null) {
            if (criteria != null) {
                criteria = criteria.or(new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)bundleID, 2));
            }
            else {
                criteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)bundleID, 2);
            }
        }
        if (platform != null) {
            criteria = criteria.and(new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)platform, 0));
        }
        selectQuery.setCriteria(criteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        final JSONArray appsForBlacklist = new JSONArray();
        final Iterator iterator = dataObject.getRows("MdAppGroupDetails");
        while (iterator.hasNext()) {
            final JSONObject app = new JSONObject();
            final Row row = iterator.next();
            final Long appGroupID = (Long)row.get("APP_GROUP_ID");
            final String name = (String)row.get("GROUP_DISPLAY_NAME");
            final String identifier = (String)row.get("IDENTIFIER");
            final Integer plat = (Integer)row.get("PLATFORM_TYPE");
            if (dataObject.containsTable("MacAppProperties")) {
                final Row macAppRow = dataObject.getRow("MacAppProperties", new Criteria(new Column("MacAppProperties", "APP_GROUP_ID"), (Object)appGroupID, 0));
                if (macAppRow != null) {
                    final String codeRequirement = (String)macAppRow.get("CODE_REQUIREMENT");
                    final String signingIdentifier = (String)macAppRow.get("SIGNING_IDENTIFIER");
                    final String installationPath = (String)macAppRow.get("INSTALLATION_PATH");
                    app.put("code_requirement", (Object)((codeRequirement == null) ? "--" : codeRequirement));
                    app.put("signing_identifier", (Object)((signingIdentifier == null) ? "--" : signingIdentifier));
                    app.put("installation_path", (Object)((installationPath == null) ? "--" : installationPath));
                }
            }
            app.put("appGroupID", (Object)appGroupID);
            app.put("platform", (Object)plat);
            app.put("identifier", (Object)identifier);
            app.put("appName", (Object)name);
            appsForBlacklist.put((Object)app);
        }
        final JSONObject returnValue = new JSONObject();
        returnValue.put("apps", (Object)appsForBlacklist);
        return returnValue;
    }
    
    public JSONObject addAppsGroupsForBlacklistinginRepository(final JSONObject params) throws Exception {
        final JSONObject requestJSON = params.getJSONObject("msg_body");
        final JSONObject responseJSON = new JSONObject();
        try {
            final JSONArray apps = requestJSON.getJSONArray("apps");
            final Long customerID = APIUtil.getCustomerID(params);
            final boolean validate = APIUtil.getBooleanFilter(params, "validate", Boolean.FALSE);
            final List appList = new BlacklistAppHandler().addAndGetAppsInRepository(this.getPlatformMapFromAppJSON(apps), customerID, validate);
            final JSONArray jsonArray = new JSONArray((Collection)appList);
            responseJSON.put("apps", (Object)jsonArray);
        }
        catch (final JSONException e) {
            this.logger.log(Level.WARNING, "Parameter : {0} Expected. Parameter must contain array of json objects with app details.", "apps");
            throw new APIHTTPException("COM0014", new Object[] { "Parameter : apps Expected. Parameter must contain array of json objects with app details." });
        }
        return responseJSON;
    }
    
    public HashMap getPlatformMapFromAppJSON(final JSONArray apps) throws Exception {
        final HashMap hashMap = new HashMap();
        for (int i = 0; i < apps.length(); ++i) {
            final JSONObject jsonObject = apps.getJSONObject(i);
            final Integer platform = jsonObject.getInt("platform");
            List platformList = hashMap.get(platform);
            if (platformList == null) {
                platformList = new ArrayList();
            }
            platformList.add(jsonObject);
            hashMap.put(platform, platformList);
        }
        return hashMap;
    }
    
    public JSONObject performBlacklistAction(final JSONObject params, final int restype, final int associationType) throws Exception {
        final org.json.simple.JSONObject secLog = new org.json.simple.JSONObject();
        String remarks = "failed";
        try {
            final JSONObject response = new JSONObject();
            final JSONObject requestJSON = params.getJSONObject("msg_body");
            final JSONArray apps = requestJSON.getJSONArray("app_group_ids");
            final Long customerID = APIUtil.getCustomerID(params);
            final List appList = new ArrayList();
            for (int i = 0; i < apps.length(); ++i) {
                appList.add(Long.parseLong(apps.get(i).toString()));
            }
            secLog.put((Object)"APP_IDs", (Object)appList);
            AppsUtil.getInstance().validateAppGroupsIfFound(appList, customerID);
            if (associationType == 1) {
                AppsUtil.getInstance().checkIfMDMApp(appList);
            }
            final List resList = new ArrayList();
            if (restype != 5) {
                final JSONArray resourceList = requestJSON.getJSONArray("resource_ids");
                for (int j = 0; j < resourceList.length(); ++j) {
                    resList.add(Long.parseLong(resourceList.get(j).toString()));
                }
                response.put("resource_ids", (Object)resourceList);
            }
            secLog.put((Object)"RESOURCE_IDs", (Object)resList);
            final Long userID = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
            final HashMap hashMap = new HashMap();
            hashMap.put("appIDs", appList);
            hashMap.put("resourceIDs", resList);
            hashMap.put("userID", userID);
            hashMap.put("resourceType", restype);
            hashMap.put("Operation", associationType);
            MICSBlacklistFeatureController.addTrackingData(associationType);
            new BlacklistAppHandler().blackListAppsWithAppGroupIds(hashMap, customerID);
            remarks = "success";
            return response;
        }
        finally {
            String resourceType = "";
            switch (restype) {
                case 1: {
                    resourceType = "GROUP_IDs";
                    break;
                }
                case 2: {
                    resourceType = "DEVICE_IDs";
                    break;
                }
                case 3: {
                    resourceType = "USER_GROUP_IDs";
                    break;
                }
                case 4: {
                    resourceType = "USER_IDs";
                    break;
                }
                case 5: {
                    resourceType = "NETWORK";
                    break;
                }
            }
            secLog.put((Object)"RESOURCE_TYPE", (Object)resourceType);
            secLog.put((Object)"REMARKS", (Object)remarks);
            final String operationType = (associationType == 1) ? "BLACKLIST_APP" : "WHITELIST_APP";
            MDMOneLineLogger.log(Level.INFO, operationType, secLog);
        }
    }
    
    public JSONObject getBlacklistingStatus(final JSONObject jsonObject) throws Exception {
        Long resID = null;
        Long appGrpID = null;
        final JSONObject filters = jsonObject.optJSONObject("filters");
        final JSONObject returnValue = new JSONObject();
        final Long customerId = APIUtil.getCustomerID(jsonObject);
        if (filters != null) {
            resID = (Long)filters.opt("resourceId");
            appGrpID = (Long)filters.opt("appGroupID");
        }
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppGroupDetails"));
        selectQuery.addJoin(new Join("MdAppGroupDetails", "BlacklistAppToCollection", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
        selectQuery.addJoin(new Join("BlacklistAppToCollection", "BlacklistAppCollectionStatus", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppGroupDetails", "GROUP_DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"));
        Criteria criteria = new Criteria(new Column("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        if (resID != null) {
            criteria = criteria.and(new Criteria(Column.getColumn("BlacklistAppCollectionStatus", "RESOURCE_ID"), (Object)resID, 0));
        }
        if (appGrpID != null) {
            criteria = criteria.and(new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)appGrpID, 0));
        }
        selectQuery.setCriteria(criteria);
        final RelationalAPI relationalAPI = RelationalAPI.getInstance();
        final Connection connection = relationalAPI.getConnection();
        final DataSet dataSet = relationalAPI.executeQuery((Query)selectQuery, connection);
        try {
            final JSONArray appsForBlacklist = new JSONArray();
            while (dataSet.next()) {
                final JSONObject app = new JSONObject();
                final String name = (String)dataSet.getValue("GROUP_DISPLAY_NAME");
                final String identifier = (String)dataSet.getValue("IDENTIFIER");
                final Integer status = (Integer)dataSet.getValue("STATUS");
                final Long resourceID = (Long)dataSet.getValue("RESOURCE_ID");
                app.put("status", (Object)status);
                app.put("resourceId", (Object)resourceID);
                app.put("identifier", (Object)identifier);
                app.put("appName", (Object)name);
                appsForBlacklist.put((Object)app);
            }
            returnValue.put("blacklistStatus", (Object)appsForBlacklist);
        }
        finally {
            if (dataSet != null) {
                dataSet.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        return returnValue;
    }
    
    public JSONObject getGroupTreeDataForAppGroups(final JSONObject params, final int type) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            final JSONObject msgBody = params.optJSONObject("msg_body");
            if (msgBody == null || msgBody.length() < 1) {
                throw new APIHTTPException("COM0009", new Object[0]);
            }
            final JSONArray apps = msgBody.optJSONArray("app_group_ids");
            if (apps == null || apps.length() < 1) {
                throw new APIHTTPException("COM0009", new Object[0]);
            }
            final Long customerID = APIUtil.getCustomerID(params);
            final String search = APIUtil.getURLDecodedStringFilter(params, "search");
            final boolean allFlag = APIUtil.getBooleanFilter(params, "all");
            final HashMap hashMap = new HashMap();
            hashMap.put("cid", customerID);
            if (search != null) {
                hashMap.put("search", search);
            }
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < apps.length(); ++i) {
                sb.append(apps.get(i).toString()).append(",");
            }
            hashMap.put("appGroupIds", sb.deleteCharAt(sb.length() - 1).toString());
            hashMap.put("type", type);
            final String groupType = APIUtil.getStringFilter(params, "grouptype");
            hashMap.put("groups", groupType);
            final SelectQuery groupViewQuery = BlacklistQueryUtils.getInstance().getGroupTreeForAppGroups(hashMap);
            if (!allFlag) {
                final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(params);
                hashMap.put("count", Boolean.TRUE);
                final SelectQuery groupViewCountQuery = BlacklistQueryUtils.getInstance().getGroupTreeForAppGroups(hashMap);
                final int count = MDMDBUtil.getRecordCount(groupViewCountQuery);
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null) {
                    response.put("paging", (Object)pagingJSON);
                }
                groupViewQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                groupViewQuery.addSortColumn(new SortColumn("Resource", "NAME", false));
            }
            final List customGroupsList = CustomGroupingHandler.getCustomGroupDetailsList(groupViewQuery);
            final JSONArray groupArray = new JSONArray();
            final Iterator groupItr = customGroupsList.iterator();
            String resourceName = "";
            while (groupItr.hasNext()) {
                final Hashtable groupDetails = groupItr.next();
                final JSONObject childNode = new JSONObject();
                final Long groupID = groupDetails.get("CUSTOM_GP_ID");
                childNode.put("group_id", (Object)groupID);
                resourceName = groupDetails.get("CUSTOM_GP_NAME");
                final int grpType = groupDetails.get("CUSTOM_GP_TYPE");
                final int grpMemCnt = groupDetails.get("CUSTOM_GP_MEMBER_COUNT");
                childNode.put("group_type", grpType);
                childNode.put("member_count", grpMemCnt);
                childNode.put("name", (Object)resourceName);
                groupArray.put((Object)childNode);
            }
            response.put("groups", (Object)groupArray);
            return response;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "exception occurred in getGroupTreeDataForAppGroups", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getBlacklistDashboardData(final JSONObject params) throws Exception {
        final JSONObject filters = params.optJSONObject("filters");
        final Boolean hideOtherApps = filters.optBoolean("hideOtherApps");
        final Long customerID = APIUtil.getCustomerID(params);
        final HashMap hashMap = new BlacklistAppHandler().appSummaryDetails(customerID, hideOtherApps);
        return new JSONObject((Map)hashMap);
    }
    
    public JSONObject getDeviceTreeDataForAppGroups(final JSONObject params, final int type) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            final JSONObject msgBody = params.optJSONObject("msg_body");
            if (msgBody == null || msgBody.length() < 1) {
                throw new APIHTTPException("COM0009", new Object[0]);
            }
            final JSONArray apps = msgBody.optJSONArray("app_group_ids");
            if (apps == null || apps.length() < 1) {
                throw new APIHTTPException("COM0009", new Object[0]);
            }
            final Long customerID = APIUtil.getCustomerID(params);
            final String search = APIUtil.getURLDecodedStringFilter(params, "search");
            final boolean allFlag = APIUtil.getBooleanFilter(params, "all");
            final HashMap hashMap = new HashMap();
            hashMap.put("cid", customerID);
            if (search != null) {
                hashMap.put("search", search);
            }
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < apps.length(); ++i) {
                sb.append(apps.get(i).toString()).append(",");
            }
            hashMap.put("appGroupIds", sb.deleteCharAt(sb.length() - 1).toString());
            hashMap.put("type", type);
            final String platform = APIUtil.getStringFilter(params, "platform");
            if (!MDMStringUtils.isEmpty(platform)) {
                final String[] platformTypes = platform.split(",");
                final ArrayList<Integer> values = new ArrayList<Integer>();
                for (int j = 0; j < platformTypes.length; ++j) {
                    final int temp = Integer.parseInt(platformTypes[j]);
                    if (temp == 2 || temp == 1 || temp == 3 || temp == 4) {
                        values.add(temp);
                    }
                }
                hashMap.put("platform", values);
            }
            final SelectQuery deviceViewQuery = BlacklistQueryUtils.getInstance().getResourceTreeForAppGroups(hashMap);
            final JSONArray deviceArray = new JSONArray();
            if (!allFlag) {
                final PagingUtil pagingUtil = APIUtil.getNewInstance().getPagingParams(params);
                hashMap.put("count", Boolean.TRUE);
                final SelectQuery deviceViewCountQuery = BlacklistQueryUtils.getInstance().getResourceTreeForAppGroups(hashMap);
                final int count = DBUtil.getRecordCount(deviceViewCountQuery);
                final JSONObject pagingJSON = pagingUtil.getPagingJSON(count);
                if (pagingJSON != null) {
                    response.put("paging", (Object)pagingJSON);
                }
                deviceViewQuery.setRange(new Range(pagingUtil.getStartIndex(), pagingUtil.getLimit()));
                deviceViewQuery.addSortColumn(new SortColumn("ManagedDeviceExtn", "NAME", false));
            }
            final ArrayList deviceDetailsList = ManagedDeviceHandler.getInstance().getManagedDeviceDetailslist(deviceViewQuery);
            final Iterator deviceItr = deviceDetailsList.iterator();
            String resourceName2 = "";
            String userName = "";
            while (deviceItr.hasNext()) {
                final HashMap managedDeviceDetails = deviceItr.next();
                final JSONObject childNode = new JSONObject();
                childNode.put("resource_id", (Object)managedDeviceDetails.get("RESOURCE_ID").toString());
                resourceName2 = managedDeviceDetails.get("NAME");
                userName = managedDeviceDetails.get("USER_RESOURCE_NAME");
                childNode.put("resource_name", (Object)resourceName2);
                final int platformType = managedDeviceDetails.get("PLATFORM_TYPE");
                childNode.put("platform_type", platformType);
                childNode.put("user_name", (Object)userName);
                deviceArray.put((Object)childNode);
            }
            response.put("devices", (Object)deviceArray);
            return response;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "exception occurred in getDeviceTreeDataForAppGroups", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public JSONObject getBlacklistedAppDetails(final JSONObject apiRequest) throws APIHTTPException {
        final BlacklistAppHandler blacklistHandler = new BlacklistAppHandler();
        final Long customerId = APIUtil.getCustomerID(apiRequest);
        final Long appGroupId = APIUtil.getResourceID(apiRequest, "appgroup_id");
        if (appGroupId == -1L) {
            throw new APIHTTPException("COM0009", new Object[] { "App group id" });
        }
        final JSONObject appDetails = blacklistHandler.getBlacklistedAppDetails(customerId, appGroupId);
        return appDetails;
    }
    
    public JSONObject getAppSummary(final JSONObject apiRequest) throws APIHTTPException {
        final BlacklistAppHandler blacklistHandler = new BlacklistAppHandler();
        final BlacklistQueryUtils blacklistQuery = new BlacklistQueryUtils();
        final JSONObject appDetails = new JSONObject();
        final Long customerId = APIUtil.getCustomerID(apiRequest);
        final boolean isInstalledOnly = APIUtil.getBooleanFilter(apiRequest, "installed", true);
        final JSONObject appViewData = AppSettingsDataHandler.getInstance().getAppViewSettings(customerId);
        final boolean needDiscoveredApp = APIUtil.getBooleanFilter(apiRequest, "discovered_count", true);
        final boolean managedApp = APIUtil.getBooleanFilter(apiRequest, "managed_count", true);
        final boolean blacklistedApp = APIUtil.getBooleanFilter(apiRequest, "blacklisted_count", true);
        final boolean blacklistDevices = APIUtil.getBooleanFilter(apiRequest, "device_blacklist_apps", true);
        if (needDiscoveredApp) {
            final long discoveredAppCount = blacklistHandler.getDiscoveredAppCount(customerId, isInstalledOnly, appViewData);
            appDetails.put("discovered_count", discoveredAppCount);
        }
        if (managedApp) {
            final int managedAppCount = blacklistHandler.getInstalledManagedAppCount(customerId, appViewData);
            appDetails.put("managed_count", managedAppCount);
        }
        if (blacklistedApp) {
            final long blacklistedApps = blacklistQuery.getBlacklistAppCount(customerId);
            appDetails.put("blacklisted_count", blacklistedApps);
        }
        if (blacklistDevices) {
            final long blacklistedDevices = blacklistHandler.getDeviceWithBlacklist(customerId);
            appDetails.put("device_blacklist_apps", blacklistedDevices);
        }
        return appDetails;
    }
    
    public JSONObject getBlacklistedAppsOnDevice(final JSONObject apiRequest) throws APIHTTPException {
        final BlacklistAppHandler blacklistHandler = new BlacklistAppHandler();
        final Long customerId = APIUtil.getCustomerID(apiRequest);
        Long deviceId = APIUtil.getResourceID(apiRequest, "device_id");
        if (deviceId == -1L) {
            final String udid = APIUtil.getResourceIDString(apiRequest, "udid");
            deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        }
        new DeviceFacade().validateIfDeviceExists(deviceId, APIUtil.getCustomerID(apiRequest));
        return blacklistHandler.getBlacklistedAppsOnDevice(customerId, deviceId);
    }
    
    public JSONObject getInvDeviceAppDetails(final JSONObject apiRequest) throws APIHTTPException {
        final BlacklistAppHandler blacklistHandler = new BlacklistAppHandler();
        final Long customerId = APIUtil.getCustomerID(apiRequest);
        Long deviceId = APIUtil.getResourceID(apiRequest, "device_id");
        if (deviceId == -1L) {
            final String udid = APIUtil.getResourceIDString(apiRequest, "udid");
            deviceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
        }
        new DeviceFacade().validateIfDeviceExists(deviceId, APIUtil.getCustomerID(apiRequest));
        final JSONObject appDetails = blacklistHandler.getDeviceToAppDetails(customerId, deviceId);
        return appDetails;
    }
    
    public void validateIfUserInRoleToMakeGlobalAppWhitelistBlockListOperation(final Long loginId, final int blockListOperation) throws Exception {
        if (!DMUserHandler.isUserInRole(loginId, "All_Managed_Mobile_Devices")) {
            String errorArgs;
            if (blockListOperation == 1) {
                errorArgs = I18N.getMsg("dc.mdm.inv.app.blacklist", new Object[0]);
            }
            else {
                errorArgs = I18N.getMsg("mdm.appmgmt.allowlist", new Object[0]);
            }
            throw new APIHTTPException("APP0035", new Object[] { errorArgs });
        }
    }
}
