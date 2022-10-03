package com.adventnet.sym.server.mdm.apps;

import java.util.Hashtable;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.net.URLEncoder;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.apps.AppVersionChecker;
import java.util.Arrays;
import com.me.mdm.server.apps.config.AppConfigPolicyDBHandler;
import com.adventnet.sym.server.mdm.inv.AppDataHandler;
import java.util.Properties;
import com.adventnet.persistence.DataAccessException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import org.json.JSONException;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.sym.server.mdm.config.MDMCollectionStatusUpdate;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManagedAppStatusHandler
{
    public Logger logger;
    
    public ManagedAppStatusHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void updateAndroidAppCatalogStatus(final String syncAppCatalogData, final Long customerId) {
        try {
            this.logger.log(Level.INFO, "Received App Catalog Status from Agent: {0}", syncAppCatalogData);
            final JSONObject appCatalogJSON = new JSONObject(syncAppCatalogData);
            final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(appCatalogJSON);
            final String message = hmap.get("Message");
            final JSONArray jsonArr = new JSONArray(message);
            final String udid = hmap.get("UDID");
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            for (int i = 0; i < jsonArr.length(); ++i) {
                final JSONObject jsonMsg = jsonArr.getJSONObject(i);
                final String scopeString = jsonMsg.optString("scope", "container");
                final Integer scope = (int)(scopeString.equalsIgnoreCase("container") ? 1 : 0);
                this.handleAndroidAppCatalogStatus(jsonMsg, customerId, resourceID, scope);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in UpdateAndroidAppCatalogStatus", ex);
        }
    }
    
    private void handleAndroidAppCatalogStatus(final JSONObject jsonMsg, final Long customerId, final Long resourceID, final int scope) throws JSONException, SyMException, DataAccessException {
        final Long collectionId = jsonMsg.getLong("CollectionID");
        final String appVersion = jsonMsg.optString("AppVersion", "--");
        final String appVersionCode = jsonMsg.optString("VersionCode", "--");
        final HashMap appMap = this.handleVersionAndFetchDetailsForAndroidApp(jsonMsg, customerId, appVersion, appVersionCode);
        final String bundleIdentifier = String.valueOf(jsonMsg.get("PackageName"));
        final Long installedAppId = appMap.getOrDefault("APP_ID", null);
        final Long appGroupId = appMap.get("APP_GROUP_ID");
        appMap.put("IDENTIFIER", bundleIdentifier);
        appMap.put("APP_VERSION", appVersion);
        appMap.put("APP_NAME_SHORT_VERSION", appVersionCode);
        appMap.put("PLATFORM_TYPE", 2);
        appMap.put("DistributedCollectionID", collectionId);
        final String appStatus = String.valueOf(jsonMsg.get("Status"));
        final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
        final MDMCollectionStatusUpdate collnUpdater = MDMCollectionStatusUpdate.getInstance();
        int inventoryScope = scope;
        if (ManagedDeviceHandler.getInstance().isProfileOwner(resourceID)) {
            inventoryScope = 1;
        }
        if (appStatus.equalsIgnoreCase("Managed")) {
            final JSONObject extraParamsJSON = new JSONObject();
            extraParamsJSON.put("CollectionID", (Object)collectionId);
            final String updateRemarks = handler.updateAppInstallationDetailsFromDevice(resourceID, appGroupId, installedAppId, 2, "dc.db.mdm.collection.Successfully_installed_the_app", scope, 2, extraParamsJSON);
            if (updateRemarks != null) {
                collnUpdater.clearCollnErrorCode(resourceID, collectionId);
                collnUpdater.updateMdmConfigStatus(resourceID, collectionId.toString(), 6, updateRemarks);
            }
        }
        else if (appStatus.equalsIgnoreCase("Failed")) {
            final String errorCodeStr = jsonMsg.has("ErrorCode") ? String.valueOf(jsonMsg.get("ErrorCode")) : "-1";
            final Integer errorCode = Integer.parseInt(errorCodeStr);
            final String errorMsg = jsonMsg.optString("ErrorMsg", "");
            int statusConstant = 7;
            String remarks;
            if (errorCode == 12092) {
                if (!jsonMsg.has("RetryLimit") || jsonMsg.getInt("RetryLimit") == -1) {
                    if (errorMsg.isEmpty()) {
                        remarks = "mdm.agent.download.failed.unknownerror";
                    }
                    else {
                        this.logger.log(Level.INFO, "Failed to download app {0} in device {1} due to {2}", new Object[] { appGroupId, resourceID, errorMsg });
                        remarks = this.getRemarksForAppDownloadFailure(errorMsg, resourceID);
                    }
                }
                else {
                    remarks = "mdm.app.scheduled_for_retry";
                    statusConstant = 13;
                }
            }
            else if (errorCode == 12043) {
                if (errorMsg.isEmpty()) {
                    remarks = "mdm.agent.App_installation_failed_failure@@@<l>$(mdmUrl)/kb/mdm-app-installation-failed.html";
                }
                else {
                    remarks = this.getRemarksForAppInstallFailure(errorMsg);
                }
            }
            else {
                remarks = errorMsg;
            }
            handler.updateAppInstallationDetailsFromDevice(resourceID, appGroupId, installedAppId, 0, remarks, scope);
            collnUpdater.updateMdmConfigStatus(resourceID, collectionId.toString(), statusConstant, remarks);
        }
        else if (appStatus.equalsIgnoreCase("QueuedToInstall")) {
            final String remarks = "mdm.app.Installation_in_agent_queue";
            collnUpdater.updateMdmConfigStatus(resourceID, collectionId.toString(), 3, remarks);
        }
        else if (appStatus.equalsIgnoreCase("Removed") || appStatus.equalsIgnoreCase("UserRejectedUninstallation") || appStatus.equalsIgnoreCase("ManagedButUninstalled")) {
            final boolean isMarkedForDelete = ProfileAssociateHandler.getInstance().isCollectionDeleteSafe(resourceID, collectionId);
            if (isMarkedForDelete && appStatus.equalsIgnoreCase("Removed")) {
                this.handleAppRemovedOnDissociation(resourceID, appGroupId, collectionId);
            }
            else if (!isMarkedForDelete && (appStatus.equalsIgnoreCase("Removed") || appStatus.equalsIgnoreCase("ManagedButUninstalled"))) {
                this.handleManagedAppRemoved(resourceID, appGroupId, collectionId, scope);
            }
            else if (appStatus.equalsIgnoreCase("UserRejectedUninstallation")) {
                handler.updateAppInstallationDetailsFromDevice(resourceID, appGroupId, installedAppId, 2, "dc.db.mdm.collection.Successfully_user_remove_cancell_the_app", scope);
                collnUpdater.updateMdmConfigStatus(resourceID, collectionId.toString(), 7, "dc.db.mdm.collection.Successfully_user_remove_cancell_the_app");
            }
        }
        else {
            this.logger.log(Level.WARNING, "Unknown ManagedAppStatus received {0}", appStatus);
        }
        try {
            this.logger.log(Level.INFO, "Updating inventory data for managed app status");
            this.handleInventoryActionForManagedApp(resourceID, appMap, inventoryScope, appStatus);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "Cannot update inventory details for managed app " + n + " from " + n2);
        }
        if ((appStatus.equalsIgnoreCase("Managed") || appStatus.equalsIgnoreCase("Failed") || appStatus.equalsIgnoreCase("UserRejectedUninstallation")) && installedAppId != null) {
            this.handleIsUpdateAvailable(resourceID, appMap);
        }
        final List resourceList = new ArrayList();
        resourceList.add(resourceID);
        AppsUtil.getInstance().addOrUpdateAppCatalogSync(resourceList);
    }
    
    private HashMap handleVersionAndFetchDetailsForAndroidApp(final JSONObject jsonMsg, final Long customerId, final String appVersion, final String appVersionCode) throws JSONException {
        final String bundleIdentifier = String.valueOf(jsonMsg.get("PackageName"));
        if (appVersion.equals("--")) {
            final HashMap map = new HashMap();
            final Long appGroupId = new AppsUtil().getAppGroupIDFromIdentifier(bundleIdentifier, 2, customerId);
            map.put("APP_GROUP_ID", appGroupId);
            this.logger.log(Level.INFO, "App version not present. So not updating. App group ID {0}", new Object[] { appGroupId });
            return map;
        }
        final Properties prop = new Properties();
        ((Hashtable<String, String>)prop).put("IDENTIFIER", bundleIdentifier);
        ((Hashtable<String, String>)prop).put("APP_VERSION", appVersion);
        ((Hashtable<String, String>)prop).put("APP_NAME_SHORT_VERSION", appVersionCode);
        ((Hashtable<String, Long>)prop).put("CUSTOMER_ID", customerId);
        return new AppDataHandler().processAndroidAppRepositoryData(prop);
    }
    
    private void handleManagedAppRemoved(final Long resourceID, final Long appGroupId, final Long collectionId, final int scope) throws SyMException {
        this.logger.log(Level.INFO, "Managed app removed");
        final MDMCollectionStatusUpdate collnUpdater = MDMCollectionStatusUpdate.getInstance();
        final AppsUtil appsUtil = AppsUtil.getInstance();
        final AppInstallationStatusHandler handler = new AppInstallationStatusHandler();
        handler.updateAppInstallationDetailsFromDevice(resourceID, appGroupId, null, 0, "dc.db.mdm.apps.status.ManagedButUninstalled", scope);
        final int newScope = appsUtil.getScopeForApp(resourceID, appGroupId);
        if (scope != newScope) {
            handler.updateAppInstallationDetailsFromDevice(resourceID, appGroupId, null, 0, "dc.db.mdm.apps.status.ManagedButUninstalled", newScope);
        }
        collnUpdater.updateMdmConfigStatus(resourceID, collectionId.toString(), 12, "dc.db.mdm.apps.status.ManagedButUninstalled");
    }
    
    private void handleAppRemovedOnDissociation(final Long resourceID, final Long appGroupId, final Long collectionId) throws DataAccessException, SyMException {
        this.logger.log(Level.INFO, "Successful removal of app on dissaciation");
        final MDMCollectionStatusUpdate collnUpdater = MDMCollectionStatusUpdate.getInstance();
        final AppsUtil appsUtil = AppsUtil.getInstance();
        appsUtil.deleteAppResourceRel(resourceID, appGroupId);
        ProfileAssociateHandler.getInstance().deleteRecentProfileForResource(resourceID, collectionId);
        final List resourceList = new ArrayList();
        resourceList.add(resourceID);
        ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
        collnUpdater.clearCollnErrorCode(resourceID, collectionId);
        collnUpdater.updateMdmConfigStatus(resourceID, collectionId.toString(), 6, "dc.db.mdm.collection.Successfully_removed_the_app");
        AppConfigPolicyDBHandler.getInstance().deleteAppConfigFeedback(resourceID, Arrays.asList(appGroupId));
    }
    
    private void handleInventoryActionForManagedApp(final Long resourceID, final HashMap appMap, final Integer scope, final String appStatus) throws Exception {
        final Long installedAppId = appMap.getOrDefault("APP_ID", null);
        final Long appGroupId = appMap.get("APP_GROUP_ID");
        final MDDeviceInstalledAppsHandler handler = new MDDeviceInstalledAppsHandler();
        if (appStatus.equalsIgnoreCase("Managed") || appStatus.equalsIgnoreCase("Failed") || appStatus.equalsIgnoreCase("UserRejectedUninstallation")) {
            if (installedAppId != null) {
                this.logger.log(Level.INFO, "Adding app installed data in inventory");
                handler.addOrUpdateInstalledAppResourceRel(resourceID, appMap, scope, 1);
            }
        }
        else if (appStatus.equalsIgnoreCase("Removed") || appStatus.equalsIgnoreCase("ManagedButUninstalled")) {
            this.logger.log(Level.INFO, "Removing uninstalled app from inventory");
            handler.removeInstalledAppResourceRelation(resourceID, appGroupId);
        }
    }
    
    private void handleIsUpdateAvailable(final Long resourceID, final HashMap appMap) {
        try {
            final Integer platform = appMap.get("PLATFORM_TYPE");
            final AppVersionChecker checker = AppVersionChecker.getInstance(platform);
            final Long appGroupId = appMap.get("APP_GROUP_ID");
            final JSONObject installedVersionDetails = new JSONObject();
            installedVersionDetails.put("APP_ID", appMap.get("APP_ID"));
            installedVersionDetails.put("APP_VERSION", (Object)appMap.getOrDefault("APP_VERSION", "--"));
            installedVersionDetails.put("APP_NAME_SHORT_VERSION", (Object)appMap.getOrDefault("APP_NAME_SHORT_VERSION", "--"));
            final Long distributedColln = appMap.get("DistributedCollectionID");
            final Long distributedAppId = MDMUtil.getInstance().getAppIDFromCollection(distributedColln);
            final JSONObject distributedAppDetails = AppsUtil.getInstance().getAppDetailsJson(distributedAppId);
            final Long releaseLabelId = AppVersionDBUtil.getInstance().getReleaseLabelIdForCollectionInAppGroupToCollection(distributedColln);
            final JSONObject latestVersionDetails = new MDMAppMgmtHandler().getLatestAndroidAppDetailsForAppReleaseLabel(appGroupId, releaseLabelId);
            this.logger.log(Level.INFO, "App group : {0} , Distributed app {1} , Installed app {2}, Latest app {3}", new Object[] { appGroupId, distributedAppDetails, installedVersionDetails, latestVersionDetails });
            if (!checker.isAppVersionGreater(distributedAppDetails, installedVersionDetails) && !checker.isAppVersionGreater(latestVersionDetails, installedVersionDetails)) {
                this.logger.log(Level.INFO, "Appgroup {0} is marked for no update for resource {1} through app installation ManagedAppStatus", new Object[] { appGroupId, resourceID });
                new MDMAppUpdateMgmtHandler().setAppUpdateAvailable(resourceID, appGroupId, false);
                if (!checker.isAppVersionGreater(latestVersionDetails, installedVersionDetails)) {
                    this.logger.log(Level.INFO, "Appgroup {0} has an unknown greater version {1} Partial rollout/Split apk/Sync not upto date", new Object[] { appGroupId, installedVersionDetails });
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in updating is update available flag for resource {0}", resourceID);
        }
    }
    
    public String getRemarksForAppInstallFailure(final String errorMsg) {
        String helpLink;
        if (errorMsg.contains("mdm.agent.App_installation_failed_aborted")) {
            helpLink = "$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)#user";
        }
        else if (errorMsg.contains("mdm.agent.App_installation_failed_conflict") || errorMsg.contains("mdm.agent.App_installation_failed_incompatible") || errorMsg.contains("mdm.agent.App_installation_failed_invalid") || errorMsg.contains("mdm.agent.installation_failed_versioncode_mismatch")) {
            helpLink = "$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)#apk";
        }
        else if (errorMsg.contains("mdm.agent.App_installation_failed_storage") || errorMsg.contains("mdm.agent.installation_failed_miui_optimization")) {
            helpLink = "$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)#device";
        }
        else if (errorMsg.contains("mdm.agent.App_installation_blocked_by_playprotect")) {
            final String googleHelpLink = "https://support.google.com/googleplay/android-developer/contact/protectappeals";
            helpLink = googleHelpLink + "@@@<l>" + "$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)#apk";
        }
        else {
            helpLink = "$(mdmUrl)/kb/mdm-app-installation-failed.html?$(traceurl)";
        }
        final String remarks = errorMsg + "@@@<l>" + helpLink;
        return remarks;
    }
    
    private String getRemarksForAppDownloadFailure(final String errorMsg, final Long resourceId) {
        String remarks = errorMsg;
        try {
            final String supportMessage = I18N.getMsg("mdm.agent.download_failed_message", new Object[0]);
            Boolean needDeviceLogs = false;
            String i18RemarksKey;
            if (errorMsg.equals("mdm.agent.download.failed.networkunreachable") || errorMsg.equals("mdm.agent.download.failed.sockettimeout") || errorMsg.equals("mdm.agent.download.failed.unknownhost") || errorMsg.equals("mdm.agent.download.failed.sslhandshake")) {
                needDeviceLogs = true;
                i18RemarksKey = "mdm.agent.download.failed.connectivityissues";
            }
            else if (errorMsg.equals("mdm.agent.download.failed.malformedurl") || errorMsg.equals("mdm.agent.download.failed.filenotfoundexception") || errorMsg.equals("mdm.agent.download.failed.unknownerror")) {
                needDeviceLogs = true;
                i18RemarksKey = "mdm.agent.download.failed.unknownerror";
            }
            else {
                i18RemarksKey = errorMsg;
            }
            remarks = i18RemarksKey + "@@@<l>" + MDMUtil.getInstance().getSupportFileUploadUrl(supportMessage);
            if (needDeviceLogs) {
                String deviceList = "[\"" + resourceId.toString() + "\"]";
                deviceList = URLEncoder.encode(deviceList, "UTF-8");
                remarks = remarks + "&mobileDeviceList=" + deviceList;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred while getting app download failure remarks", ex);
        }
        return remarks;
    }
    
    public String getManagedAppRemarksForAndroid(final Long appGroupId, final Long resourceId, final int packageType, final Long installedAppID, final JSONObject extraParamsJSON) {
        String remarks = "dc.db.mdm.collection.Successfully_installed_the_app";
        try {
            if (packageType == 2) {
                final Long collectionId = extraParamsJSON.optLong("CollectionID");
                final MDMCollectionStatusUpdate collnUpdater = MDMCollectionStatusUpdate.getInstance();
                final DataObject collnToResDO = collnUpdater.getCollnToResourceDO(collectionId, resourceId);
                if (!collnToResDO.isEmpty()) {
                    final Row collnToResRow = collnToResDO.getFirstRow("CollnToResources");
                    if (collnToResRow != null && (int)collnToResRow.get("STATUS") == 7) {
                        remarks = null;
                    }
                }
            }
            else {
                final HashMap<Long, HashMap> appToVersions = AppsUtil.getInstance().getAvailableVersions(resourceId, appGroupId, null);
                if (appToVersions != null && appToVersions.getOrDefault(appGroupId, null) != null && appToVersions.get(appGroupId).size() > 1) {
                    final HashMap availableVersions = appToVersions.get(appGroupId);
                    final JSONObject appVersionJSON = new AppsUtil().getAppVersionJSONFromAppId(installedAppID);
                    final String versionName = (String)appVersionJSON.get("APP_VERSION");
                    final String versionCode = (String)appVersionJSON.get("APP_NAME_SHORT_VERSION");
                    if (availableVersions.containsKey(versionCode) && availableVersions.containsValue(versionName)) {
                        remarks = "mdm.app.installed.applicable_version";
                    }
                    else {
                        remarks = "mdm.app.installed.applicable_version";
                    }
                }
                else {
                    remarks = "mdm.app.installed.applicable_version";
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception occurred while getting ManagedAppRemarks", ex);
        }
        return remarks;
    }
}
