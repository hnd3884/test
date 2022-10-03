package com.me.mdm.server.apps.businessstore.android;

import java.util.Map;
import java.util.Iterator;
import com.adventnet.persistence.DataAccess;
import com.me.mdm.server.apps.config.AppConfigPolicyDBHandler;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.apps.android.afw.GoogleApiRetryHandler;
import com.me.mdm.server.apps.android.afw.layoutmgmt.StoreLayoutManager;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.me.mdm.server.util.MDMCustomerParamsHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleManagedAccountHandler;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.mdm.server.apps.android.afw.usermgmt.GooglePlayDevicesSyncRequestHandler;
import java.util.List;
import java.util.HashMap;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreAssetUtil;
import com.me.mdm.server.apps.android.afw.appmgmt.GooglePlayBusinessAppHandler;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.me.mdm.server.apps.android.afw.GoogleAPIErrorHandler;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.ArrayList;
import com.me.mdm.server.apps.businessstore.BaseSyncAppsHandler;

public class AndroidSyncAppsHandler extends BaseSyncAppsHandler
{
    public static final String TOTAL_APPS = "TotalPlaystoreApps";
    public static final String SUCCESS_APPS = "SuccessPlaystoreApps";
    public static final String FAILURE_APPS = "FailurePlaystoreApps";
    public static final String PLAYSTORE_SYNC_STATUS = "PlaystoreSyncStatus";
    public static final String SYNC_STATUS_FETCH = "Fetch";
    public static final String SYNC_STATUS_SUCCESS = "Success";
    public static final String SYNC_STATUS_FAILED = "Failed";
    public static final String FAILURE_REASON = "PSFailureReason";
    public static final String SYNC_STATUS_LAYOUT = "PlaystoreLayout";
    public static final String SYNC_STATUS_USERS = "Users";
    public static final String SYNC_STATUS_DEVICES = "Devices";
    public static final String SYNC_STATUS_APPS = "Apps";
    public static final String BSTORE_LAST_SYNC = "PLAYSTORE_LAST_SYNC";
    public static final String BSTORE_NEXT_SYNC = "PLAYSTORE_NEXT_SYNC";
    public static final String CURRENT_SYNC_LAST_PROGRESS = "PS_CURRENT_SYNC_LAST_PROGRESS";
    public static final String PS_SPLIT_APK_COUNT = "PS_SPLIT_APK_COUNT";
    public static final String PS_TWO_TRACK_APPS = "PS_TWO_TRACK_APPS";
    public static final String PS_MULTI_TRACK_APPS = "PS_MULTI_TRACK_APPS";
    public static final String MANDATORY_SYNC_TYPE = "MANDATORY_SYNC_TYPE";
    public static final String FORCE_SYNC = "PFW_FORCE_SYNC";
    public static ArrayList syncParams;
    public static final String PS_FAILURE_ERRORCODE = "PSFailureErrorCode";
    public static final int SYNC_APPS_ACTION_LOG_ARG = 1;
    public static final int SYNC_USERS_ACTION_LOG_ARG = 2;
    public static final int SYNC_DEVICES_ACTION_LOG_ARG = 3;
    public static final int UNPUBLISHED_APP_ACTION_LOG_ARG = 4;
    
    public AndroidSyncAppsHandler(final Long businessStoreID, final Long customerID) {
        super(businessStoreID, customerID);
    }
    
    public AndroidSyncAppsHandler() {
    }
    
    @Override
    public void syncApps(JSONObject params) throws Exception {
        final Long customerId = params.getLong("CustomerID");
        final Long businessStoreID = params.optLong("BUSINESSSTORE_ID");
        if (this.checkIfSyncAllowed(businessStoreID)) {
            this.resetStoreAndAssetDetails(businessStoreID, customerId);
            final JSONObject specificParams = (JSONObject)params.get("specificParams");
            params = this.setEffectiveSyncType(params, businessStoreID);
            final int source = specificParams.optInt("source", 1);
            if (source == 2) {
                this.updateNextSyncTime(businessStoreID);
            }
            params.put("includeNonPortalApps", true);
            params.put("PlatformType", 2);
            this.logger.log(Level.INFO, "Syncing Playstore apps for customerID {0} having businessStoreID {1} ", new Object[] { customerId, businessStoreID });
            try {
                super.syncApps(params);
            }
            catch (final TokenResponseException | GoogleJsonResponseException ex) {
                this.logger.log(Level.WARNING, "Error while syncing Google Bstore", (Throwable)ex);
                final String errRemarks = GoogleAPIErrorHandler.getResponseErrorKey((Exception)ex);
                MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(businessStoreID, -1, errRemarks, null, 4);
                if (errRemarks.equals("mdm.appmgmt.afw.syncfailed.invalid_time_date")) {
                    MDMApiFactoryProvider.getGoogleApiProductBasedHandler().handleServerTimeMismatch((Exception)ex, businessStoreID);
                }
                throw ex;
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Error while syncing Google Bstore", e);
                final String errRemarks = GoogleAPIErrorHandler.getResponseErrorKey(e);
                int errorCode = -1;
                if (errRemarks.equalsIgnoreCase("mdm.appmgmt.afw.syncfailed.proxy_cert_error")) {
                    errorCode = 1000;
                }
                MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(businessStoreID, errorCode, errRemarks, null, 4);
                throw e;
            }
            this.logger.log(Level.INFO, "Syncing Playstore apps completed for businessStoreId {0}", businessStoreID);
        }
        else {
            this.logger.log(Level.WARNING, "Syncing Playstore apps skipped for customerID {0} having businessStoreID {1} ", new Object[] { customerId, businessStoreID });
        }
    }
    
    @Override
    public void handlePostAddOperationsForApp(final JSONObject jsonObject) throws DataAccessException, JSONException {
        final Long businessStoreID = jsonObject.optLong("BUSINESSSTORE_ID");
        final Long customerId = jsonObject.getLong("CUSTOMER_ID");
        final Long storeAssetId = jsonObject.optLong("STORE_ASSET_ID");
        if (jsonObject.getBoolean("success")) {
            final boolean isAppNewlySynced = jsonObject.optBoolean("isAppNewlySynced", false);
            if (isAppNewlySynced) {
                if (jsonObject.getJSONObject("MDPackageToAppGroupForm").getBoolean("IS_PAID_APP")) {
                    new GooglePlayBusinessAppHandler().preFillEarlierAssignedLicense(jsonObject, customerId);
                }
                final boolean appPreviouslyAddedAsNonPortalApp = jsonObject.optBoolean("appPreviouslyAddedAsNonPortalApp", false);
                if (appPreviouslyAddedAsNonPortalApp) {
                    final JSONArray newlyApprovedAppDetails = jsonObject.optJSONArray("newlyApprovedAppDetails");
                    final JSONObject newlyApprovedApp = new JSONObject();
                    newlyApprovedApp.put("APP_GROUP_ID", jsonObject.optLong("APP_GROUP_ID"));
                    newlyApprovedApp.put("PROFILE_ID", jsonObject.optLong("PROFILE_ID"));
                    newlyApprovedApp.put("COLLECTION_ID", jsonObject.optLong("COLLECTION_ID"));
                    newlyApprovedAppDetails.put((Object)newlyApprovedApp);
                    jsonObject.put("newlyApprovedAppDetails", (Object)newlyApprovedAppDetails);
                }
            }
            final Long appGroupId = jsonObject.optLong("APP_GROUP_ID");
            MDBusinessStoreAssetUtil.addMDStoreAssetToAppGroupRel(storeAssetId, appGroupId);
            final Long packageId = jsonObject.getLong("PACKAGE_ID");
            if (jsonObject.has("applicable_versions")) {
                final JSONArray applicableVersions = jsonObject.getJSONArray("applicable_versions");
                AppsUtil.getInstance().fillLatestApplicableVersions(packageId, appGroupId, applicableVersions);
            }
            MDBusinessStoreUtil.incrementBusinessStoreAppsCompletedCount(businessStoreID, 1);
        }
        else {
            MDBusinessStoreUtil.incrementBusinessStoreAppsFailedCount(businessStoreID, 1);
            final int errorCode = jsonObject.optInt("ERROR_CODE");
            MDBusinessStoreAssetUtil.addOrUpdateMdStoreAssetErrorDetails(storeAssetId, errorCode, null, null);
        }
        try {
            MDBusinessStoreUtil.updateCurrentSyncLastProgress(businessStoreID);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Unable to update sync progress time for " + n);
        }
    }
    
    @Override
    public void handlePostSyncOperations(final HashMap params) throws Exception {
        try {
            final Long customerId = params.get("CustomerID");
            final Long businessStoreID = params.get("BUSINESSSTORE_ID");
            MDBusinessStoreUtil.updateStoreSyncStatus(businessStoreID, 6);
            final List unManagedApps = params.get("UnManagedApps");
            final JSONObject specificParams = params.getOrDefault("specificParams", new JSONObject());
            final int syncType = specificParams.optInt("syncType", 1);
            final int syncSource = specificParams.optInt("source", 1);
            final GooglePlayDevicesSyncRequestHandler playHandler = new GooglePlayDevicesSyncRequestHandler(customerId);
            final JSONObject googleForWorkSettings = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
            if (unManagedApps != null) {
                new GooglePlayBusinessAppHandler().handleUnApprovedApps(unManagedApps, customerId);
            }
            final List newlyApprovedApps = params.getOrDefault("newlyApprovedApps", null);
            if (newlyApprovedApps != null && !newlyApprovedApps.isEmpty()) {
                this.handleNewlyApprovedApps(googleForWorkSettings, params);
            }
            this.addActionLogEntry(googleForWorkSettings, 1, null);
            MDBusinessStoreUtil.updateCurrentSyncLastProgress(businessStoreID);
            if (googleForWorkSettings.getInt("ENTERPRISE_TYPE") == GoogleForWorkSettings.ENTERPRISE_TYPE_GOOGLE && (syncType == 2 || syncType == 3)) {
                final String afwFirstSyncPending = MDBusinessStoreUtil.getBusinessStoreParamValue("afwFirstSyncPending", businessStoreID);
                MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(businessStoreID, -1, null, null, 5);
                this.logger.log(Level.INFO, "Syncing GSuite users for customer ID {0}", customerId);
                playHandler.syncUsers();
                this.logger.log(Level.INFO, "Syncing GSuite users completed for customer ID {0}", customerId);
                this.addActionLogEntry(googleForWorkSettings, 2, null);
                if (afwFirstSyncPending != null && Boolean.valueOf(afwFirstSyncPending)) {
                    new GoogleManagedAccountHandler().sendGSuiteUserAccountDetectCmd(customerId);
                }
            }
            if (syncType == 3) {
                MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(businessStoreID, -1, null, null, 7);
                playHandler.syncDevices(null);
                this.addActionLogEntry(googleForWorkSettings, 3, null);
            }
            this.deleteDataFromDeprecatedVersionHandling(customerId);
            MDBusinessStoreUtil.updateLastSuccessfulSyncTime(businessStoreID);
            SyMUtil.deleteSyMParameter("MANDATORY_SYNC_TYPE");
            MDBusinessStoreUtil.updateStoreSyncStatus(businessStoreID, 3);
            MDBusinessStoreUtil.addOrUpdateBusinessStoreParam("afwFirstSyncPending", "false", businessStoreID);
            MDBusinessStoreUtil.addOrUpdateBusinessStoreParam("PFW_FORCE_SYNC", "false", businessStoreID);
            super.handlePostSyncOperations(params);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error when handling post sync operations", ex);
        }
    }
    
    protected void resetSyncTrackCount(final Long customerId) {
        try {
            final JSONObject syncTrackParams = new JSONObject();
            syncTrackParams.put("PS_MULTI_TRACK_APPS", 0);
            syncTrackParams.put("PS_TWO_TRACK_APPS", 0);
            new MDMCustomerParamsHandler().addOrUpdateParameters(syncTrackParams, customerId);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in resetting sync tracking counts", e);
        }
    }
    
    protected void deleteDataFromDeprecatedVersionHandling(final Long customerId) {
        try {
            final String forceSyncForCollnFW = CustomerParamsHandler.getInstance().getParameterValue("FORCE_SYNC_COLLECTION_FW", (long)customerId);
            if (forceSyncForCollnFW == null || forceSyncForCollnFW.equalsIgnoreCase("true")) {
                this.deleteAndroidAppGroupLatestVersion(customerId);
                CustomerParamsHandler.getInstance().addOrUpdateParameter("FORCE_SYNC_COLLECTION_FW", "false", (long)customerId);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, e, () -> "Exception deleting old data for deprecated version handling " + n);
        }
    }
    
    private void deleteAndroidAppGroupLatestVersion(final Long customerId) throws Exception {
        this.logger.log(Level.INFO, "Deleting old data for deprecated version handling {0}", customerId);
        final DeleteQueryImpl dQuery = new DeleteQueryImpl("MdAppGroupLatestVersion");
        final Join appGroupJoin = new Join("MdAppGroupLatestVersion", "MdAppGroupDetails", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2);
        dQuery.addJoin(appGroupJoin);
        final Criteria androidC = new Criteria(Column.getColumn("MdAppGroupDetails", "PLATFORM_TYPE"), (Object)2, 0);
        final Criteria customerC = new Criteria(Column.getColumn("MdAppGroupDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        dQuery.setCriteria(androidC.and(customerC));
        MDMUtil.getPersistence().delete((DeleteQuery)dQuery);
    }
    
    public void addActionLogEntry(final JSONObject playStoreDetails, final int type, final String actionLogAdditionalArg) {
        try {
            final Long customerId = playStoreDetails.getLong("CUSTOMER_ID");
            final Long userId = playStoreDetails.optLong("BUSINESSSTORE_ADDED_BY", -1L);
            final String userName = DMUserHandler.getUserNameFromUserID(userId);
            String actionLogArg;
            final String domainName = actionLogArg = (String)playStoreDetails.get("MANAGED_DOMAIN_NAME");
            String actionLogMessage = "dc.mdm.actionlog.afw.sync.apps";
            if (type == 2) {
                actionLogMessage = "dc.mdm.actionlog.afw.sync.users";
            }
            else if (type == 4) {
                actionLogMessage = "mdm.actionlog.afw.app_unpublished";
                actionLogArg = actionLogAdditionalArg + "@@@" + domainName;
            }
            MDMEventLogHandler.getInstance().MDMEventLogEntry(72509, null, userName, actionLogMessage, actionLogArg, customerId);
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Error when handling post sync operations", (Throwable)ex);
        }
        catch (final SyMException ex2) {
            this.logger.log(Level.SEVERE, "Error when handling post sync operations", (Throwable)ex2);
        }
    }
    
    @Override
    public void handlePreSyncOperations(final HashMap params) throws Exception {
        final Integer totalApps = params.getOrDefault("TotalAppsToSync", 0);
        final Long businessStoreID = params.get("BUSINESSSTORE_ID");
        MDBusinessStoreUtil.updateTotalAppsCount(businessStoreID, totalApps);
        MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(businessStoreID, -1, null, null, 2);
    }
    
    public JSONObject getStatus(final Long customerId) throws DataAccessException, JSONException, Exception {
        final Long businessStoreID = MDBusinessStoreUtil.getBusinessStoreID(customerId, BusinessStoreSyncConstants.BS_SERVICE_AFW);
        JSONObject statusJSON = MDBusinessStoreUtil.getBusinessStoreSyncDetails(businessStoreID);
        JSONObject returnJson;
        if (statusJSON.optInt("STORE_SYNC_STATUS") != 0) {
            returnJson = this.buildSyncStatusMsgForBusinessStore(statusJSON);
        }
        else {
            statusJSON = this.getSyncStatus(customerId);
            returnJson = this.buildSyncStatusMessage(statusJSON);
        }
        return returnJson;
    }
    
    protected boolean checkIfSyncAllowed(final Long businessStoreID) throws DataAccessException, JSONException, Exception {
        final JSONObject syncStatus = MDBusinessStoreUtil.getBusinessStoreSyncDetails(businessStoreID);
        final int status = syncStatus.optInt("STORE_SYNC_STATUS");
        if (status != 0 && status != 3 && status != 4) {
            final Long lastSyncProgressTime = syncStatus.optLong("CURRENT_SYNC_LAST_PROGRESS");
            if (lastSyncProgressTime != 0L) {
                final Long currTime = System.currentTimeMillis();
                return currTime - lastSyncProgressTime > 900000L;
            }
        }
        return true;
    }
    
    protected void updateNextSyncTime(final Long businessStoreID) throws Exception {
        MDBusinessStoreUtil.updateStoreNextSyncTime(businessStoreID, System.currentTimeMillis() + 86400000L);
    }
    
    public static ArrayList getSyncParams() {
        if (AndroidSyncAppsHandler.syncParams == null) {
            constructSyncParams();
        }
        return AndroidSyncAppsHandler.syncParams;
    }
    
    private static void constructSyncParams() {
        (AndroidSyncAppsHandler.syncParams = new ArrayList()).add("TotalPlaystoreApps");
        AndroidSyncAppsHandler.syncParams.add("FailurePlaystoreApps");
        AndroidSyncAppsHandler.syncParams.add("PlaystoreSyncStatus");
        AndroidSyncAppsHandler.syncParams.add("SuccessPlaystoreApps");
        AndroidSyncAppsHandler.syncParams.add("PLAYSTORE_LAST_SYNC");
        AndroidSyncAppsHandler.syncParams.add("PLAYSTORE_NEXT_SYNC");
        AndroidSyncAppsHandler.syncParams.add("PS_CURRENT_SYNC_LAST_PROGRESS");
        AndroidSyncAppsHandler.syncParams.add("PSFailureReason");
        AndroidSyncAppsHandler.syncParams.add("PSFailureErrorCode");
    }
    
    private JSONObject buildSyncStatusMessage(final JSONObject statusJSON) throws JSONException {
        final JSONObject syncMessage = new JSONObject();
        final String status = statusJSON.optString("PlaystoreSyncStatus");
        if (status != null && !MDMStringUtils.isEmpty(status)) {
            if (status.equals("Devices")) {
                syncMessage.put("PlaystoreSyncStatus", (Object)"InProgress");
                syncMessage.put("TotalPlaystoreApps", Long.parseLong(statusJSON.optString("TotalPlaystoreApps", "0")));
                syncMessage.put("SuccessPlaystoreApps", Long.parseLong(statusJSON.optString("SuccessPlaystoreApps", "0")));
                syncMessage.put("Status", (Object)"Devices");
            }
            else if (status.equals("Fetch")) {
                syncMessage.put("Status", (Object)"InProgress");
                syncMessage.put("PlaystoreSyncStatus", (Object)"Fetch");
            }
            else if (status.equals("PlaystoreLayout")) {
                syncMessage.put("PlaystoreSyncStatus", (Object)"InProgress");
                syncMessage.put("TotalPlaystoreApps", Long.parseLong(statusJSON.optString("TotalPlaystoreApps", "0")));
                syncMessage.put("SuccessPlaystoreApps", Long.parseLong(statusJSON.optString("SuccessPlaystoreApps", "0")));
                syncMessage.put("Status", (Object)"PlaystoreLayout");
            }
            else if (status.equals("Users")) {
                syncMessage.put("PlaystoreSyncStatus", (Object)"InProgress");
                syncMessage.put("TotalPlaystoreApps", Long.parseLong(statusJSON.optString("TotalPlaystoreApps", "0")));
                syncMessage.put("SuccessPlaystoreApps", Long.parseLong(statusJSON.optString("SuccessPlaystoreApps", "0")));
                syncMessage.put("Status", (Object)"Users");
            }
            else if (status.equals("Apps")) {
                syncMessage.put("PlaystoreSyncStatus", (Object)"InProgress");
                syncMessage.put("TotalPlaystoreApps", Long.parseLong(statusJSON.optString("TotalPlaystoreApps", "0")));
                syncMessage.put("SuccessPlaystoreApps", Long.parseLong(statusJSON.optString("SuccessPlaystoreApps", "0")));
                syncMessage.put("FailurePlaystoreApps", Long.parseLong(statusJSON.optString("FailurePlaystoreApps", "0")));
                syncMessage.put("Status", (Object)"Apps");
            }
            else if (status.equals("Success")) {
                final Long failureApps = Long.parseLong(statusJSON.optString("FailurePlaystoreApps", "0"));
                syncMessage.put("TotalPlaystoreApps", Long.parseLong(statusJSON.optString("TotalPlaystoreApps", "0")));
                syncMessage.put("SuccessPlaystoreApps", Long.parseLong(statusJSON.optString("SuccessPlaystoreApps", "0")));
                syncMessage.put("FailurePlaystoreApps", (Object)failureApps);
                final Long lastSyncTime = Long.parseLong(statusJSON.optString("PLAYSTORE_LAST_SYNC", "--"));
                syncMessage.put("PLAYSTORE_LAST_SYNC", (Object)lastSyncTime);
                syncMessage.put("PLAYSTORE_NEXT_SYNC", (Object)statusJSON.optString("PLAYSTORE_NEXT_SYNC", "--"));
                syncMessage.put("PlaystoreSyncStatus", (Object)"Success");
            }
            else if (status.equals("Failed")) {
                syncMessage.put("TotalPlaystoreApps", Long.parseLong(statusJSON.optString("TotalPlaystoreApps", "0")));
                syncMessage.put("SuccessPlaystoreApps", Long.parseLong(statusJSON.optString("SuccessPlaystoreApps", "0")));
                syncMessage.put("FailurePlaystoreApps", Long.parseLong(statusJSON.optString("FailurePlaystoreApps", "0")));
                syncMessage.put("PSFailureReason", (Object)statusJSON.optString("PSFailureReason", ""));
                syncMessage.put("PLAYSTORE_LAST_SYNC", (Object)statusJSON.optString("PLAYSTORE_LAST_SYNC", "--"));
                syncMessage.put("PLAYSTORE_NEXT_SYNC", (Object)statusJSON.optString("PLAYSTORE_NEXT_SYNC", "--"));
                syncMessage.put("PlaystoreSyncStatus", (Object)"Failure");
                final String psFailureErrorCode = statusJSON.optString("PSFailureErrorCode");
                if (psFailureErrorCode != null && !psFailureErrorCode.isEmpty()) {
                    syncMessage.put("PSFailureErrorCode", (Object)psFailureErrorCode);
                }
            }
        }
        else {
            syncMessage.put("PlaystoreSyncStatus", (Object)"NoSync");
        }
        return syncMessage;
    }
    
    private JSONObject buildSyncStatusMsgForBusinessStore(final JSONObject statusJSON) throws Exception {
        final JSONObject syncMessage = new JSONObject();
        final int status = statusJSON.optInt("STORE_SYNC_STATUS");
        if (status != 0) {
            if (status == 7) {
                syncMessage.put("PlaystoreSyncStatus", (Object)"InProgress");
                syncMessage.put("TotalPlaystoreApps", statusJSON.optInt("TOTAL_APP_COUNT", 0));
                syncMessage.put("SuccessPlaystoreApps", statusJSON.optInt("COMPLETED_APP_COUNT", 0));
                syncMessage.put("Status", (Object)"Devices");
            }
            else if (status == 1) {
                syncMessage.put("Status", (Object)"InProgress");
                syncMessage.put("PlaystoreSyncStatus", (Object)"Fetch");
            }
            else if (status == 6) {
                syncMessage.put("PlaystoreSyncStatus", (Object)"InProgress");
                syncMessage.put("TotalPlaystoreApps", statusJSON.optInt("TOTAL_APP_COUNT", 0));
                syncMessage.put("SuccessPlaystoreApps", statusJSON.optInt("COMPLETED_APP_COUNT", 0));
                syncMessage.put("Status", (Object)"PlaystoreLayout");
            }
            else if (status == 5) {
                syncMessage.put("PlaystoreSyncStatus", (Object)"InProgress");
                syncMessage.put("TotalPlaystoreApps", statusJSON.optInt("TOTAL_APP_COUNT", 0));
                syncMessage.put("SuccessPlaystoreApps", statusJSON.optInt("COMPLETED_APP_COUNT", 0));
                syncMessage.put("Status", (Object)"Users");
            }
            else if (status == 2) {
                syncMessage.put("PlaystoreSyncStatus", (Object)"InProgress");
                syncMessage.put("TotalPlaystoreApps", statusJSON.optInt("TOTAL_APP_COUNT", 0));
                syncMessage.put("SuccessPlaystoreApps", statusJSON.optInt("COMPLETED_APP_COUNT", 0));
                syncMessage.put("FailurePlaystoreApps", statusJSON.optInt("FAILED_APP_COUNT", 0));
                syncMessage.put("Status", (Object)"Apps");
            }
            else if (status == 3) {
                syncMessage.put("TotalPlaystoreApps", statusJSON.optInt("TOTAL_APP_COUNT", 0));
                syncMessage.put("SuccessPlaystoreApps", statusJSON.optInt("COMPLETED_APP_COUNT", 0));
                syncMessage.put("FailurePlaystoreApps", statusJSON.optInt("FAILED_APP_COUNT", 0));
                final Long lastSuccessfulSyncTime = statusJSON.optLong("LAST_SUCCESSFULL_SYNC_TIME", 0L);
                final String lastSyncTimeStr = (lastSuccessfulSyncTime != 0L) ? String.valueOf(lastSuccessfulSyncTime) : "--";
                syncMessage.put("PLAYSTORE_LAST_SYNC", (Object)lastSyncTimeStr);
                final Long nextSyncTime = statusJSON.optLong("STORE_NEXT_SYNC", 0L);
                final String nextSyncTimeStr = (nextSyncTime != 0L) ? String.valueOf(nextSyncTime) : "--";
                syncMessage.put("PLAYSTORE_NEXT_SYNC", (Object)nextSyncTimeStr);
                syncMessage.put("PlaystoreSyncStatus", (Object)"Success");
            }
            else if (status == 4) {
                syncMessage.put("TotalPlaystoreApps", statusJSON.optInt("TOTAL_APP_COUNT", 0));
                syncMessage.put("SuccessPlaystoreApps", statusJSON.optInt("COMPLETED_APP_COUNT", 0));
                syncMessage.put("FailurePlaystoreApps", statusJSON.optInt("FAILED_APP_COUNT", 0));
                String failureReason = statusJSON.optString("REMARKS", "");
                if (failureReason != null && !failureReason.isEmpty()) {
                    failureReason = I18N.getMsg(failureReason, new Object[0]);
                }
                final int errorCode = statusJSON.optInt("ERROR_CODE", -1);
                if (errorCode != -1) {
                    syncMessage.put("PSFailureErrorCode", errorCode);
                }
                syncMessage.put("PSFailureReason", (Object)failureReason);
                final Long lastSuccessfulSyncTime2 = statusJSON.optLong("LAST_SUCCESSFULL_SYNC_TIME", 0L);
                final String lastSyncTimeStr2 = (lastSuccessfulSyncTime2 != 0L) ? String.valueOf(lastSuccessfulSyncTime2) : "--";
                syncMessage.put("PLAYSTORE_LAST_SYNC", (Object)lastSyncTimeStr2);
                final Long nextSyncTime2 = statusJSON.optLong("STORE_NEXT_SYNC", 0L);
                final String nextSyncTimeStr2 = (nextSyncTime2 != 0L) ? String.valueOf(nextSyncTime2) : "--";
                syncMessage.put("PLAYSTORE_NEXT_SYNC", (Object)nextSyncTimeStr2);
                syncMessage.put("PlaystoreSyncStatus", (Object)"Failure");
            }
        }
        else {
            syncMessage.put("PlaystoreSyncStatus", (Object)"NoSync");
        }
        return syncMessage;
    }
    
    public JSONObject getSyncStatus(final Long customerId) throws JSONException, DataAccessException, Exception {
        final JSONObject statusJSON = new JSONObject();
        final Object[] bsSyncParams = getSyncParams().toArray();
        final Criteria syncParamsCriteria = new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)bsSyncParams, 8);
        final Criteria customerCriteria = new Criteria(Column.getColumn("CustomerParams", "CUSTOMER_ID"), (Object)customerId, 0);
        final Criteria finalCriteria = syncParamsCriteria.and(customerCriteria);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("CustomerParams"));
        selectQuery.addSelectColumn(Column.getColumn("CustomerParams", "*"));
        selectQuery.setCriteria(finalCriteria);
        final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
        if (!dataObject.isEmpty()) {
            for (final Object param : bsSyncParams) {
                final String paramName = (String)param;
                final Criteria criteria = new Criteria(Column.getColumn("CustomerParams", "PARAM_NAME"), (Object)paramName, 0);
                final Row row = dataObject.getRow("CustomerParams", criteria);
                if (row != null) {
                    statusJSON.put(paramName, row.get("PARAM_VALUE"));
                }
            }
        }
        if (statusJSON.has("PSFailureReason")) {
            final String failureReasonKey = statusJSON.get("PSFailureReason").toString();
            statusJSON.put("PSFailureReason", (Object)I18N.getMsg(failureReasonKey, new Object[0]));
        }
        return statusJSON;
    }
    
    protected JSONObject setEffectiveSyncType(final JSONObject params, final Long businessStoreID) throws JSONException {
        final JSONObject specificParams = (JSONObject)params.get("specificParams");
        int syncType = specificParams.optInt("syncType", 1);
        this.logger.log(Level.INFO, "Sync type requested {0}", syncType);
        try {
            final String mandatorySyncTypeS = SyMUtil.getSyMParameter("MANDATORY_SYNC_TYPE");
            if (mandatorySyncTypeS != null) {
                final int mandatorySyncType = Integer.parseInt(mandatorySyncTypeS);
                syncType = ((syncType > mandatorySyncType) ? syncType : mandatorySyncType);
                this.logger.log(Level.INFO, "Sync type set to {0}", syncType);
            }
            final String afwFirstSyncPending = MDBusinessStoreUtil.getBusinessStoreParamValue("afwFirstSyncPending", businessStoreID);
            if (afwFirstSyncPending != null && afwFirstSyncPending.equals("true")) {
                syncType = 2;
                this.logger.log(Level.INFO, "AFW First sync is pending, initiating full sync");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in computing effective sync type", e);
        }
        specificParams.put("syncType", syncType);
        params.put("specificParams", (Object)specificParams);
        return params;
    }
    
    protected void resetStoreAndAssetDetails(final Long businessStoreID, final Long customerId) {
        MDBusinessStoreUtil.setInitialSyncDetails(businessStoreID);
        MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(businessStoreID, -1, null, null, 1);
        this.resetSyncTrackCount(customerId);
        MDBusinessStoreAssetUtil.resetMdStoreAssetErrorDetails(businessStoreID);
    }
    
    private void handleNewlyApprovedApps(final JSONObject googleForWorkSettings, final HashMap params) throws Exception {
        final Long customerId = params.get("CustomerID");
        final List newlyApprovedApps = params.get("newlyApprovedApps");
        new StoreLayoutManager().handleStoreLayout(googleForWorkSettings, newlyApprovedApps);
        new GoogleApiRetryHandler().initiateNewlyApprovedAppRedistribution(params);
    }
    
    public void handleUnpublishedApps(final JSONArray unPublishedApps, final Long customerID) {
        if (unPublishedApps != null && unPublishedApps.length() > 0) {
            try {
                final List unPublishedAppsList = new JSONUtil().convertStringJSONArrayTOList(unPublishedApps);
                final SelectQuery selectQuery = AppsUtil.getInstance().getPortalApprovedAppsQuery(customerID, 2, null);
                selectQuery.addSelectColumn(new Column((String)null, "*"));
                final Criteria criteria = selectQuery.getCriteria();
                final Criteria appCriteria = new Criteria(Column.getColumn("MdAppGroupDetails", "IDENTIFIER"), (Object)unPublishedAppsList.toArray(), 8);
                selectQuery.setCriteria(criteria.and(appCriteria));
                final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
                if (dataObject != null && !dataObject.isEmpty()) {
                    final Iterator rowIterator = dataObject.getRows("MdAppGroupDetails");
                    final List<String> identifiers = DBUtil.getColumnValuesAsList(rowIterator, "IDENTIFIER");
                    this.logger.log(Level.INFO, "Marking apps {0} as NOT PURCHASED FROM PORTAL", identifiers);
                    final Iterator updateRowsIterator = dataObject.getRows("MdPackageToAppGroup");
                    while (updateRowsIterator.hasNext()) {
                        final Row row = updateRowsIterator.next();
                        row.set("IS_PURCHASED_FROM_PORTAL", (Object)Boolean.FALSE);
                        row.set("PRIVATE_APP_TYPE", (Object)0);
                        dataObject.updateRow(row);
                    }
                    MDMUtil.getPersistence().update(dataObject);
                    final Long businessStoreID = MDBusinessStoreUtil.getBusinessStoreID(customerID, BusinessStoreSyncConstants.BS_SERVICE_AFW);
                    MDBusinessStoreAssetUtil.deleteStoreAssetIds(businessStoreID, identifiers);
                    final Map<Long, List<Long>> appGroupToNonProdLabel = AppVersionDBUtil.getInstance().getNonProdLabelForAppGroup(unPublishedAppsList, customerID);
                    try {
                        for (final Long appGroupId : appGroupToNonProdLabel.keySet()) {
                            final Long approvedAppId = AppConfigPolicyDBHandler.getInstance().getProductionAppIDFromAppGroupID(appGroupId, customerID);
                            AppsUtil.getInstance().setApprovedAppIdForResource(appGroupToNonProdLabel.get(appGroupId), appGroupId, approvedAppId);
                            final Criteria appGroupCriteria = new Criteria(new Column("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupId, 0);
                            final Criteria nonProdReleaseLabelCriteria = new Criteria(new Column("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)appGroupToNonProdLabel.get(appGroupId).toArray(), 8);
                            DataAccess.delete("AppGroupToCollection", appGroupCriteria.and(nonProdReleaseLabelCriteria));
                        }
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.SEVERE, "Cannot set assign prod label for unapproved app {0}", e);
                    }
                    final JSONObject googleForWorkSettings = GoogleForWorkSettings.getGoogleForWorkSettings(customerID, GoogleForWorkSettings.SERVICE_TYPE_AFW);
                    for (final String identifier : identifiers) {
                        new AndroidSyncAppsHandler().addActionLogEntry(googleForWorkSettings, 4, identifier);
                    }
                }
            }
            catch (final Exception ex) {
                this.logger.log(Level.SEVERE, "Exception in handleUnpublishedApp ", ex);
            }
        }
    }
    
    static {
        AndroidSyncAppsHandler.syncParams = null;
    }
}
