package com.me.mdm.mdmmigration.mecloud;

import com.me.mdm.server.apps.businessstore.StoreFacade;
import java.util.concurrent.TimeUnit;
import com.me.mdm.mdmmigration.MDMMigrationUtil;
import com.me.mdm.mdmmigration.APIServiceDataHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;
import java.net.URLDecoder;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.Map;
import org.json.JSONObject;
import java.util.logging.Logger;

public class AppMigrationHandler
{
    private static Logger logger;
    private static MECloudAPIRequestHandler meCloudAPIRequestHandler;
    
    public AppMigrationHandler(final MECloudAPIRequestHandler meCloudAPIRequestHandler) {
        AppMigrationHandler.meCloudAPIRequestHandler = meCloudAPIRequestHandler;
    }
    
    public JSONObject constructAppJSON(final JSONObject appDetails, final JSONObject betaAppDetails, final boolean isBetaVersion, final Map requestHeaderMap, final boolean is_src_afw_configured) throws Exception {
        final JSONObject appDataToPost = new JSONObject();
        try {
            final String[] array;
            final String[] appKeys = array = new String[] { "bundle_identifier", "is_paid_app", "supported_devices", "platform_type", "app_name", "description", "app_category_id", "app_version", "prevent_backup", "remove_app_with_profile" };
            for (final String key : array) {
                if (appDetails.has(key)) {
                    appDataToPost.put(key, appDetails.get(key));
                }
            }
            if (is_src_afw_configured) {
                appDataToPost.put("app_type", (appDetails.has("app_type") && appDetails.getInt("app_type") == 2) ? 2 : 3);
            }
            else {
                appDataToPost.put("app_type", (Object)appDetails.getString("app_type"));
            }
            JSONObject versionData;
            if (isBetaVersion) {
                versionData = betaAppDetails.getJSONObject("files");
            }
            else {
                versionData = appDetails;
            }
            if (versionData.has("app_file")) {
                final String appFileId = AppMigrationHandler.meCloudAPIRequestHandler.fileUpload(versionData.getString("app_file"), requestHeaderMap, 2);
                appDataToPost.put("app_file", (Object)appFileId);
            }
            if (versionData.has("icon") && !versionData.getString("icon").startsWith("http")) {
                final String iconFileId = AppMigrationHandler.meCloudAPIRequestHandler.fileUpload(versionData.getString("icon"), requestHeaderMap, 1);
                appDataToPost.put("display_image", (Object)iconFileId);
            }
            if (versionData.has("full_image_loc")) {
                final String full_imageId = AppMigrationHandler.meCloudAPIRequestHandler.fileUpload(versionData.getString("full_image_loc"), requestHeaderMap, 1);
                appDataToPost.put("full_image", (Object)full_imageId);
            }
        }
        catch (final Exception ex) {
            AppMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while constructing apps request json", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return appDataToPost;
    }
    
    public Map fetchDestinationApps(final Map requestHeaderMap, final boolean isBusinessApps) throws Exception {
        final AppFacade appFacade = new AppFacade();
        final Map<String, String> destApps = new HashMap<String, String>();
        final JSONObject reqJSON = this.createRequestJsonForApp(null, requestHeaderMap);
        String baseServerUrl = ApiFactoryProvider.getUtilAccessAPI().getServerURL();
        baseServerUrl += "/api/v1/mdm/apps";
        reqJSON.getJSONObject("msg_header").put("request_url", (Object)baseServerUrl);
        JSONObject responseBodyObject = (JSONObject)appFacade.getRepositoryApps(reqJSON, Boolean.FALSE);
        final JSONArray movedApps = responseBodyObject.getJSONArray("apps");
        for (int i = 0; i < movedApps.length(); ++i) {
            if (isBusinessApps && movedApps.getJSONObject(i).getInt("app_type") == 2) {
                destApps.put(movedApps.getJSONObject(i).getString("bundle_identifier"), movedApps.getJSONObject(i).getString("app_id"));
            }
            else if (!isBusinessApps) {
                destApps.put(movedApps.getJSONObject(i).getString("bundle_identifier"), movedApps.getJSONObject(i).getString("app_id"));
            }
        }
        if (responseBodyObject.has("paging")) {
            while (responseBodyObject.getJSONObject("paging").has("next")) {
                final String request_url = responseBodyObject.getJSONObject("paging").getString("next");
                reqJSON.getJSONObject("msg_header").put("request_url", (Object)request_url);
                final String[] req_url_split = request_url.split("skip-token=");
                final String skip_token = req_url_split[req_url_split.length - 1];
                reqJSON.getJSONObject("msg_header").getJSONObject("filters").put("skip-token", (Object)URLDecoder.decode(skip_token, "UTF-8"));
                responseBodyObject = (JSONObject)appFacade.getRepositoryApps(reqJSON, Boolean.FALSE);
                final JSONArray nextSetApps = responseBodyObject.getJSONArray("apps");
                for (int j = 0; j < nextSetApps.length(); ++j) {
                    destApps.put(nextSetApps.getJSONObject(j).getString("bundle_identifier"), nextSetApps.getJSONObject(j).getString("app_id"));
                }
            }
        }
        return destApps;
    }
    
    public int addOrUpdateAppMigrationSummary(final JSONArray migratedApps, final Long config_id) {
        AppMigrationHandler.logger.log(Level.INFO, "Updating Apps Migration  Summary...{0}", migratedApps.toString());
        int migratedAppsCount = 0;
        try {
            final DataObject migrationStatusDO = MDMUtil.getPersistence().get("AppMigrationSummary", new Criteria(new Column("AppMigrationSummary", "CONFIG_ID"), (Object)config_id, 0));
            for (int i = 0; i < migratedApps.length(); ++i) {
                final JSONObject app = migratedApps.getJSONObject(i);
                final Long server_app_id = Long.valueOf(app.getString("server_app_id"));
                Row summaryRow = migrationStatusDO.getRow("AppMigrationSummary", new Criteria(new Column("AppMigrationSummary", "SERVER_APP_ID"), (Object)server_app_id, 0));
                if (summaryRow != null) {
                    summaryRow.set("IS_MIGRATED", app.get("is_migrated"));
                    summaryRow.set("REMARKS", app.get("remarks"));
                    summaryRow.set("APP_ID", app.opt("app_id"));
                    summaryRow.set("RELEASE_LABEL", (Object)app.optString("release_labels", ""));
                    migrationStatusDO.updateRow(summaryRow);
                }
                else {
                    summaryRow = new Row("AppMigrationSummary");
                    summaryRow.set("CONFIG_ID", (Object)config_id);
                    summaryRow.set("APP_ID", app.opt("app_id"));
                    summaryRow.set("SERVER_APP_ID", (Object)app.getLong("server_app_id"));
                    summaryRow.set("APP_TITLE", app.get("profile_name"));
                    summaryRow.set("BUNDLE_IDENTIFIER", app.get("bundle_identifier"));
                    summaryRow.set("PLATFORM_TYPE", app.get("platform_type"));
                    summaryRow.set("RELEASE_LABEL", (Object)app.optString("release_labels", ""));
                    summaryRow.set("IS_MIGRATED", app.get("is_migrated"));
                    summaryRow.set("REMARKS", app.get("remarks"));
                    migrationStatusDO.addRow(summaryRow);
                }
                if (app.getBoolean("is_migrated")) {
                    ++migratedAppsCount;
                }
            }
            MDMUtil.getPersistence().update(migrationStatusDO);
        }
        catch (final Exception e) {
            AppMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while updating profile migration summary {0}", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return migratedAppsCount;
    }
    
    public List getMigratedApps(final long config_id) {
        final List<String> appList = new ArrayList<String>();
        try {
            final Criteria configCriteria = new Criteria(new Column("AppMigrationSummary", "CONFIG_ID"), (Object)config_id, 0);
            final Criteria migCriteria = new Criteria(new Column("AppMigrationSummary", "IS_MIGRATED"), (Object)Boolean.TRUE, 0);
            final DataObject migratedAppsDO = MDMUtil.getPersistence().get("AppMigrationSummary", configCriteria.and(migCriteria));
            if (!migratedAppsDO.isEmpty()) {
                final Iterator appIterator = migratedAppsDO.getRows("AppMigrationSummary");
                while (appIterator.hasNext()) {
                    final Row appRow = appIterator.next();
                    final Long appID = (Long)appRow.get("SERVER_APP_ID");
                    appList.add(String.valueOf(appID));
                }
            }
        }
        catch (final Exception e) {
            AppMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while fetching successfully migrated apps list {0} - {1}", new Object[] { config_id, e });
        }
        return appList;
    }
    
    public void migrateAppConfigurations(final JSONObject versionData, final Map requestHeaderMap) throws Exception {
        final JSONObject configurationsJSON = versionData.getJSONObject("configuration");
        if (configurationsJSON.has("app_config_data")) {
            final JSONArray configuration = configurationsJSON.getJSONArray("app_config_data");
            if (configuration.length() > 0) {
                final JSONObject postConfigurationJSON = new JSONObject();
                postConfigurationJSON.put("app_configuration", (Object)configuration);
                final JSONObject reqJSON = this.createRequestJsonForApp(postConfigurationJSON, requestHeaderMap);
                try {
                    AppMigrationHandler.logger.log(Level.INFO, "Migrating App Configurations..");
                    new AppFacade().addAppConfiguration(reqJSON);
                }
                catch (final Exception e) {
                    AppMigrationHandler.logger.log(Level.SEVERE, "Exception while adding app configuration {0}", e);
                }
            }
        }
    }
    
    public void migrateAppPermissions(final JSONObject versionData, final Map requestHeaderMap) throws Exception {
        final JSONObject app_permission_data = versionData.getJSONObject("permission").getJSONObject("app_permission_data");
        if (app_permission_data.length() > 0) {
            final JSONArray appPermissions = app_permission_data.getJSONArray("apppermissionconfigdetails");
            if (appPermissions.length() > 0) {
                final JSONObject permissionToPost = new JSONObject();
                final JSONArray configToPost = new JSONArray();
                for (int i = 0; i < appPermissions.length(); ++i) {
                    final JSONObject conf = new JSONObject();
                    conf.put("group_name", (Object)appPermissions.getJSONObject(i).getString("app_permission_group_name"));
                    conf.put("app_permission_grant_state", (Object)Integer.valueOf(appPermissions.getJSONObject(i).getString("app_permission_grant_state")));
                    configToPost.put((Object)conf);
                }
                permissionToPost.put("permissions", (Object)configToPost);
                final JSONObject reqJSON = this.createRequestJsonForApp(permissionToPost, requestHeaderMap);
                try {
                    AppMigrationHandler.logger.log(Level.INFO, "Migrating App Permissions..");
                    new AppFacade().modifyAppPermissions(reqJSON);
                }
                catch (final Exception e) {
                    AppMigrationHandler.logger.log(Level.SEVERE, "Exception while posting app permissions {0} ", e);
                }
            }
        }
    }
    
    public Long getReleaseLabelId(final String labelName, final Long customerID) {
        Long releaseLabelID = 1L;
        try {
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AppReleaseLabel"));
            selectQuery.setCriteria(new Criteria(Column.getColumn("AppReleaseLabel", "CUSTOMER_ID"), (Object)customerID, 0));
            selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID"));
            selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME"));
            final DataObject releaseLabelDO = DataAccess.get(selectQuery);
            final Iterator<Row> releaseLabelRows = releaseLabelDO.getRows("AppReleaseLabel");
            while (releaseLabelRows.hasNext()) {
                final Row releaseLabelRow = releaseLabelRows.next();
                if (I18N.getMsg(String.valueOf(releaseLabelRow.get("RELEASE_LABEL_DISPLAY_NAME")), new Object[0]).equalsIgnoreCase(labelName)) {
                    return (long)releaseLabelRow.get("RELEASE_LABEL_ID");
                }
            }
            releaseLabelID = AppVersionDBUtil.getInstance().addChannel(customerID, labelName);
        }
        catch (final Exception ex) {
            AppMigrationHandler.logger.log(Level.SEVERE, "Exception in getReleaseLabelId method.", ex);
        }
        return releaseLabelID;
    }
    
    public JSONObject createRequestJsonForApp(final JSONObject requestBody, final Map requestHeaderMap) throws Exception {
        final JSONObject payloadCreateJson = new JSONObject();
        final String user_id = requestHeaderMap.get("user_id").toString();
        final Long login_id = DMUserHandler.getLoginIdForUserId(Long.valueOf(user_id));
        final String customer_id = requestHeaderMap.get("customer_id").toString();
        final String user_name = DMUserHandler.getUserNameFromUserID(Long.valueOf(user_id));
        payloadCreateJson.put("msg_body", (Object)requestBody);
        payloadCreateJson.put("msg_header", (Object)new JSONObject().put("filters", (Object)new JSONObject().put("user_id", (Object)user_id).put("customer_id", (Object)customer_id).put("login_id", (Object)login_id).put("user_name", (Object)user_name)));
        if (requestHeaderMap.containsKey("app_id")) {
            payloadCreateJson.getJSONObject("msg_header").put("resource_identifier", (Object)new JSONObject().put("app_id", (Object)requestHeaderMap.get("app_id").toString()));
        }
        if (requestHeaderMap.containsKey("label_id")) {
            payloadCreateJson.getJSONObject("msg_header").getJSONObject("resource_identifier").put("label_id", (Object)requestHeaderMap.get("label_id").toString());
        }
        return payloadCreateJson;
    }
    
    public JSONArray fetchAppsFromSrc(final boolean isBusinessApps) throws Exception {
        final String url = AppMigrationHandler.meCloudAPIRequestHandler.appsUrl;
        JSONObject responseObject = AppMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", url, null);
        AppMigrationHandler.meCloudAPIRequestHandler.validateResponse(responseObject);
        JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
        final JSONArray apps = responseBodyObject.getJSONArray("apps");
        final JSONArray srcApps = new JSONArray();
        for (int i = 0; i < apps.length(); ++i) {
            if (apps.getJSONObject(i).getInt("platform_type") != 3) {
                if (isBusinessApps && apps.getJSONObject(i).getInt("app_type") == 2) {
                    srcApps.put((Object)apps.getJSONObject(i));
                }
                else if (!isBusinessApps) {
                    srcApps.put((Object)apps.getJSONObject(i));
                }
            }
        }
        if (responseBodyObject.has("paging")) {
            while (responseBodyObject.getJSONObject("paging").has("next")) {
                responseObject = AppMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", responseBodyObject.getJSONObject("paging").getString("next"), null);
                responseBodyObject = responseObject.getJSONObject("ResponseJson");
                final JSONArray nextSetOfApps = responseBodyObject.getJSONArray("apps");
                for (int j = 0; j < nextSetOfApps.length(); ++j) {
                    if (nextSetOfApps.getJSONObject(j).getInt("platform_type") != 3) {
                        if (isBusinessApps && nextSetOfApps.getJSONObject(j).getInt("app_type") == 2) {
                            srcApps.put((Object)nextSetOfApps.getJSONObject(j));
                        }
                        else if (!isBusinessApps) {
                            srcApps.put((Object)nextSetOfApps.getJSONObject(j));
                        }
                    }
                }
            }
        }
        return srcApps;
    }
    
    public void associateAppsToGroup(final Map appIdsMap, final Long config_id, final Map requestHeaderMap, final Map releaseLabelInfo) {
        AppMigrationHandler.logger.log(Level.INFO, "Going to associate apps to group..");
        try {
            final APIServiceDataHandler apiServiceDataHandler = new APIServiceDataHandler();
            final DataObject migratedGroupsDO = MDMUtil.getPersistence().get("MigrationGroups", new Criteria(new Column("MigrationGroups", "CONFIG_ID"), (Object)config_id, 0, false));
            if (!migratedGroupsDO.isEmpty()) {
                final Iterator groupIterator = migratedGroupsDO.getRows("MigrationGroups");
                while (groupIterator.hasNext()) {
                    final Row groupRow = groupIterator.next();
                    final String group_name = (String)groupRow.get("GROUP_NAME");
                    final String old_groupId = (String)groupRow.get("MIGRATION_SERVER_GROUP_ID");
                    AppMigrationHandler.logger.log(Level.INFO, "associate apps group: {0}..", group_name);
                    final Long customer_id = Long.valueOf(requestHeaderMap.get("customer_id").toString());
                    final Long new_group_id = apiServiceDataHandler.getGroupIDForGroupName(group_name, customer_id);
                    final JSONObject responseObject = AppMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", AppMigrationHandler.meCloudAPIRequestHandler.groupsUrl + "/" + old_groupId + "/" + "apps", null);
                    AppMigrationHandler.meCloudAPIRequestHandler.validateResponse(responseObject);
                    final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
                    final JSONArray old_apps_array = responseBodyObject.getJSONArray("app_details");
                    final JSONArray associatedAppDetails = new JSONArray();
                    for (int i = 0; i < old_apps_array.length(); ++i) {
                        final JSONObject associatedApp = old_apps_array.getJSONObject(i);
                        final Object new_app_id = appIdsMap.get(associatedApp.getString("app_id"));
                        if (new_app_id != null) {
                            final JSONObject appInfo = new JSONObject();
                            appInfo.put("app_id", new_app_id);
                            appInfo.put("release_label_id", releaseLabelInfo.get(associatedApp.getString("release_label_name")));
                            associatedAppDetails.put((Object)appInfo);
                        }
                    }
                    if (associatedAppDetails.length() > 0 && new_group_id != null) {
                        JSONObject requestJson = new JSONObject();
                        requestJson = this.createRequestJsonForApp(requestJson, requestHeaderMap);
                        requestJson.getJSONObject("msg_header").put("resource_identifier", (Object)new JSONObject().put("group_id", (Object)new_group_id));
                        requestJson.getJSONObject("msg_body").put("app_details", (Object)associatedAppDetails);
                        new AppFacade().associateAppsToGroups(requestJson);
                    }
                }
            }
        }
        catch (final Exception e) {
            AppMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while associating apps to group {0}..", e);
        }
    }
    
    public void associateAppsToDevice(final Map appIdsMap, final Long config_id, final Map releaseLabelInfo) {
        AppMigrationHandler.logger.log(Level.INFO, "Going to associate apps to device..");
        try {
            final DataObject migratedGroupsDO = MDMUtil.getPersistence().get("MigrationDevices", new Criteria(new Column("MigrationDevices", "CONFIG_ID"), (Object)config_id, 0));
            if (!migratedGroupsDO.isEmpty()) {
                final DataObject deviceAssociationDO = MDMUtil.getPersistence().get("MigrationDeviceToApp", (Criteria)null);
                final Iterator deviceIterator = migratedGroupsDO.getRows("MigrationDevices");
                while (deviceIterator.hasNext()) {
                    final Row deviceRow = deviceIterator.next();
                    final String udid = String.valueOf(deviceRow.get("UDID"));
                    final String old_deviceId = String.valueOf(deviceRow.get("MIGRATION_SERVER_DEVICE_ID"));
                    final JSONObject responseObject = AppMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("GET", AppMigrationHandler.meCloudAPIRequestHandler.deviceUrl + "/" + old_deviceId + "/" + "apps", null);
                    AppMigrationHandler.meCloudAPIRequestHandler.validateResponse(responseObject);
                    final JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
                    final JSONArray old_apps_array = responseBodyObject.getJSONArray("apps");
                    for (int i = 0; i < old_apps_array.length(); ++i) {
                        final JSONObject associatedApp = old_apps_array.getJSONObject(i);
                        final Object new_app_id = appIdsMap.get(associatedApp.getString("app_id"));
                        final String release_label_id = releaseLabelInfo.get(associatedApp.getJSONObject("release_label_details").getString("release_label_name")).toString();
                        if (new_app_id != null && udid != null) {
                            final Row appRow = new Row("MigrationDeviceToApp");
                            appRow.set("UDID", (Object)udid);
                            appRow.set("APP_ID", new_app_id);
                            appRow.set("RELEASE_LABEL_ID", (Object)release_label_id);
                            deviceAssociationDO.addRow(appRow);
                        }
                    }
                }
                MDMUtil.getPersistence().update(deviceAssociationDO);
            }
        }
        catch (final Exception e) {
            AppMigrationHandler.logger.log(Level.SEVERE, "Exception occurred while associating apps to device ...{0}", e);
        }
    }
    
    public void afwAccountMigration(final Map requestHeaderMap) {
        try {
            AppMigrationHandler.logger.log(Level.INFO, "Going to start Afw account migration..");
            JSONObject afwRequestTopic = new JSONObject();
            afwRequestTopic.put("topic", (Object)"ManagedGooglePlay");
            JSONObject responseObject = AppMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("POST", AppMigrationHandler.meCloudAPIRequestHandler.apiServerURL + "api/v1/mdm/migration/fetch", null, afwRequestTopic);
            AppMigrationHandler.meCloudAPIRequestHandler.validateResponse(responseObject);
            JSONObject responseBodyObject = responseObject.getJSONObject("ResponseJson");
            responseBodyObject = this.createRequestJsonForApp(responseBodyObject, requestHeaderMap);
            responseBodyObject = new MDMMigrationUtil().updateMigrationDataForRequest(responseBodyObject);
            if (responseBodyObject.getString("status").equals("success")) {
                AppMigrationHandler.logger.log(Level.INFO, "Afw account successfully migrated ..");
            }
            JSONObject response = new JSONObject();
            responseBodyObject = this.createRequestJsonForApp(responseBodyObject, requestHeaderMap);
            for (int minute = 0; !response.has("PlaystoreSyncStatus") && minute < 180; response = (JSONObject)new StoreFacade().getSyncStatus(responseBodyObject, 2, null), ++minute) {
                TimeUnit.MINUTES.sleep(1L);
                AppMigrationHandler.logger.log(Level.INFO, "Checking afw account sync status..{0}", minute);
            }
            if (!response.has("PlaystoreSyncStatus") || response.getString("PlaystoreSyncStatus").equalsIgnoreCase("Failed")) {
                AppMigrationHandler.logger.log(Level.INFO, "Afw account sync failed..");
                throw new APIHTTPException("COM0004", new Object[0]);
            }
            AppMigrationHandler.logger.log(Level.INFO, "Afw account sync success and Going to start EMM Users and Accounts migration..");
            afwRequestTopic = new JSONObject();
            afwRequestTopic.put("topic", (Object)"EMMUsersAndAccounts");
            responseObject = AppMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("POST", AppMigrationHandler.meCloudAPIRequestHandler.apiServerURL + "api/v1/mdm/migration/fetch", null, afwRequestTopic);
            AppMigrationHandler.meCloudAPIRequestHandler.validateResponse(responseObject);
            JSONObject afwSettings = responseObject.getJSONObject("ResponseJson");
            if (afwSettings.getJSONObject("metadata").getInt("count") != 0) {
                JSONArray afwUserAndAccountsData = afwSettings.getJSONArray("data");
                if (afwSettings.has("paging")) {
                    while (afwSettings.getJSONObject("paging").has("next")) {
                        final String nextUrl = afwSettings.getJSONObject("paging").getString("next");
                        responseObject = AppMigrationHandler.meCloudAPIRequestHandler.executeAPIRequest("POST", nextUrl, null, afwRequestTopic);
                        AppMigrationHandler.meCloudAPIRequestHandler.validateResponse(responseObject);
                        afwSettings = responseObject.getJSONObject("ResponseJson");
                        final JSONArray data = afwSettings.getJSONArray("data");
                        if (afwUserAndAccountsData.length() + data.length() <= 500) {
                            for (int i = 0; i < data.length(); ++i) {
                                afwUserAndAccountsData.put((Object)data.getJSONObject(i));
                            }
                        }
                        else {
                            AppMigrationHandler.logger.log(Level.INFO, "Posting afwSettings for EMMUsersAndAccounts");
                            final JSONObject afwEMMUserSettings = new JSONObject();
                            afwEMMUserSettings.put("topic", (Object)"EMMUsersAndAccounts");
                            afwEMMUserSettings.put("data", (Object)afwUserAndAccountsData);
                            responseBodyObject = this.createRequestJsonForApp(afwEMMUserSettings, requestHeaderMap);
                            afwUserAndAccountsData = afwSettings.getJSONArray("data");
                            new MDMMigrationUtil().updateMigrationDataForRequest(responseBodyObject);
                        }
                    }
                }
                final JSONObject afwEMMUserSettings2 = new JSONObject();
                afwEMMUserSettings2.put("topic", (Object)"EMMUsersAndAccounts");
                afwEMMUserSettings2.put("data", (Object)afwUserAndAccountsData);
                responseBodyObject = this.createRequestJsonForApp(afwEMMUserSettings2, requestHeaderMap);
                new MDMMigrationUtil().updateMigrationDataForRequest(responseBodyObject);
                AppMigrationHandler.logger.log(Level.INFO, "EMM Users and Accounts successfully migrated ..");
            }
        }
        catch (final Exception ex) {
            AppMigrationHandler.logger.log(Level.SEVERE, "Exception while migrating afw account ..");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        AppMigrationHandler.logger = Logger.getLogger("MDMMigrationLogger");
        AppMigrationHandler.meCloudAPIRequestHandler = null;
    }
}
