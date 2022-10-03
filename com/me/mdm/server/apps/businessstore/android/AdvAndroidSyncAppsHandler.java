package com.me.mdm.server.apps.businessstore.android;

import java.util.Iterator;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import com.me.mdm.server.apps.autoupdate.AutoAppUpdateHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.apps.android.afw.usermgmt.GoogleManagedAccountHandler;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.mdm.server.apps.android.afw.usermgmt.GooglePlayDevicesSyncRequestHandler;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreAssetUtil;
import com.google.api.client.http.HttpResponseException;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.me.mdm.server.apps.android.afw.GoogleAPIErrorHandler;
import com.me.mdm.server.apps.tracks.AppTrackHandler;
import com.me.mdm.server.apps.tracks.AppTrackEvent;
import com.me.mdm.server.apps.constants.AppMgmtConstants;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.util.Collection;
import com.me.mdm.server.apps.tracks.AppTrackUtil;
import java.util.logging.Level;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.HashMap;
import org.json.JSONArray;
import com.me.mdm.server.apps.businessstore.EnterpriseBusinessStoreDBUtils;
import com.me.mdm.server.apps.businessstore.BaseEnterpriseBusinessStore;
import org.json.JSONObject;

public class AdvAndroidSyncAppsHandler extends AndroidSyncAppsHandler
{
    public AdvAndroidSyncAppsHandler(final Long businessStoreID, final Long customerID) {
        super(businessStoreID, customerID);
    }
    
    @Override
    public void syncSpecificApps(final JSONObject params) throws Exception {
        try {
            final int platformType = 2;
            final Long userID = params.getLong("userID");
            this.firstSync = params.optBoolean("isFirstSync", false);
            this.enterpriseBusinessStore = BaseEnterpriseBusinessStore.getInstance(platformType, this.businessStoreID, this.customerID);
            final JSONObject BstoreDetails = EnterpriseBusinessStoreDBUtils.getInstance().getBusinessStoreDetails(platformType, this.businessStoreID, this.customerID, userID);
            final JSONObject credentials = this.enterpriseBusinessStore.getCredential(BstoreDetails);
            this.isAuthorized = credentials.optBoolean("Success", false);
            credentials.put("isFirstSync", (Object)this.firstSync);
            final JSONObject specificParams = params.optJSONObject("specificParams");
            final Boolean includeNonPortalApps = params.optBoolean("includeNonPortalApps", false);
            final HashMap portalAndNonPortalAppDetails = this.getPortalAndNonPortalAppDetails(includeNonPortalApps, platformType);
            final JSONArray newlyApprovedAppDetails = new JSONArray();
            this.enterpriseBusinessStore.isAccountActive();
            final JSONObject listOfApps = this.enterpriseBusinessStore.getAppDetails(params);
            this.handleUnpublishedApps(listOfApps.optJSONArray("UnpublishedApps"), this.customerID);
            final HashMap hashMap = new HashMap();
            hashMap.put("CustomerID", this.customerID);
            hashMap.put("BUSINESSSTORE_ID", this.businessStoreID);
            hashMap.put("syncSelectedApps", params.optBoolean("syncSelectedApps"));
            hashMap.put("newApps", params.optInt("newApps"));
            hashMap.put("newIdentifiers", JSONUtil.getInstance().convertJSONArrayTOList(params.optJSONArray("newIdentifiers")));
            final List syncedApps = new ArrayList();
            final List namesList = new ArrayList();
            final JSONArray updateAppGroupList = new JSONArray();
            if (listOfApps.has("Apps")) {
                final JSONArray apps = listOfApps.getJSONArray("Apps");
                hashMap.put("TotalAppsToSync", apps.length());
                this.handlePreSyncOperations(hashMap);
                this.logger.log(Level.INFO, "No of Apps to be synced : {0}", apps.length());
                for (int i = 0; i < apps.length(); ++i) {
                    final JSONObject appJSON = apps.getJSONObject(i);
                    appJSON.put("USER_ID", (Object)userID);
                    appJSON.put("BUSINESSSTORE_ID", (Object)this.businessStoreID);
                    appJSON.put("CustomerID", (Object)this.customerID);
                    JSONObject curApp = new JSONObject();
                    try {
                        final List deprecatedTracks = new ArrayList();
                        boolean isNewVersionAppDetected = false;
                        final DataObject existingAppVersionDetails = new AppTrackUtil().getLatestVersionsForApp(this.customerID, (String)appJSON.get("BUNDLE_IDENTIFIER"));
                        deprecatedTracks.addAll(this.getExistingTracks(existingAppVersionDetails));
                        if (appJSON.has("trackDetails") && appJSON.has("testingVersions")) {
                            final JSONArray trackDetails = appJSON.getJSONArray("trackDetails");
                            for (int index = 0; index < trackDetails.length(); ++index) {
                                final JSONObject trackDetailsObject = trackDetails.getJSONObject(index);
                                final String trackId = (String)trackDetailsObject.get("TRACK_ID");
                                final String trackName = (String)trackDetailsObject.get("TRACK_NAME");
                                final JSONArray applicableVersions = this.getVersionsOnTrack(trackId, appJSON.getJSONArray("testingVersions"));
                                if (applicableVersions.length() > 0) {
                                    final JSONObject topVersion = (JSONObject)applicableVersions.get(0);
                                    curApp = this.enterpriseBusinessStore.processAppData(appJSON);
                                    final boolean success = curApp.getBoolean("success");
                                    if (!success) {
                                        this.handlePostAddOperationsForApp(curApp);
                                        continue;
                                    }
                                    final String appVersion = (String)topVersion.get("APP_VERSION");
                                    final String appVersionCode = String.valueOf(topVersion.get("APP_NAME_SHORT_VERSION"));
                                    curApp.put("APP_VERSION", (Object)appVersion.concat("[[" + trackName + "]]"));
                                    curApp.put("APP_NAME_SHORT_VERSION", (Object)appVersionCode);
                                    curApp.put("RELEASE_LABEL_DISPLAY_NAME", (Object)trackName);
                                    curApp.put("TRACK_ID", (Object)trackId);
                                    curApp.put("TRACK_NAME", (Object)trackName);
                                    curApp.put("isProduction", false);
                                    curApp.put("applicable_versions", (Object)applicableVersions);
                                    final boolean isNewVersionFoundOnTrack = this.isNewVersionDetected(existingAppVersionDetails, trackId, String.valueOf(topVersion.get("APP_NAME_SHORT_VERSION")));
                                    isNewVersionAppDetected = (isNewVersionAppDetected || isNewVersionFoundOnTrack);
                                    if (isNewVersionFoundOnTrack && curApp.optBoolean("success")) {
                                        curApp.put("isNewVersionFoundOnTrack", true);
                                        MDMAppMgmtHandler.getInstance().addOrUpdatePackageInRepository(curApp);
                                    }
                                }
                                deprecatedTracks.remove(trackId);
                            }
                        }
                        if (appJSON.has("productionVersions")) {
                            final JSONObject appVersion2 = (JSONObject)appJSON.getJSONArray("productionVersions").get(0);
                            curApp = this.enterpriseBusinessStore.processAppData(appJSON);
                            final boolean success2 = curApp.getBoolean("success");
                            if (!success2) {
                                this.handlePostAddOperationsForApp(curApp);
                                continue;
                            }
                            curApp.put("APP_VERSION", appVersion2.get("APP_VERSION"));
                            curApp.put("APP_NAME_SHORT_VERSION", (Object)String.valueOf(appVersion2.get("APP_NAME_SHORT_VERSION")));
                            curApp.put("APP_VERSION_STATUS", (Object)AppMgmtConstants.APP_VERSION_APPROVED);
                            curApp.put("applicable_versions", (Object)appJSON.getJSONArray("productionVersions"));
                            curApp.put("isProduction", true);
                            final boolean isNewVersionFoundOnTrack2 = this.isNewVersionDetected(existingAppVersionDetails, null, String.valueOf(appVersion2.get("APP_NAME_SHORT_VERSION")));
                            isNewVersionAppDetected = (isNewVersionAppDetected || isNewVersionFoundOnTrack2);
                            if (isNewVersionFoundOnTrack2) {
                                curApp.put("isNewVersionFoundOnTrack", true);
                                MDMAppMgmtHandler.getInstance().addOrUpdatePackageInRepository(curApp);
                            }
                            final Long appGroupId = curApp.getLong("APP_GROUP_ID");
                            if (includeNonPortalApps) {
                                curApp.put("newlyApprovedAppDetails", (Object)newlyApprovedAppDetails);
                                this.setApprovedDAppDetailsIncurrAppJSON(curApp, appGroupId, portalAndNonPortalAppDetails);
                            }
                            syncedApps.add(curApp.getLong("APP_GROUP_ID"));
                        }
                        if (!deprecatedTracks.isEmpty()) {
                            final AppTrackEvent appTrackEvent = new AppTrackEvent(this.customerID, curApp.getLong("APP_GROUP_ID"));
                            appTrackEvent.trackIds = new ArrayList<String>(deprecatedTracks);
                            appTrackEvent.appName = curApp.optString("PROFILE_NAME");
                            new AppTrackHandler().invokeOperation(appTrackEvent, 2);
                        }
                        if (isNewVersionAppDetected) {
                            updateAppGroupList.put(curApp.getLong("APP_GROUP_ID"));
                        }
                        this.handlePostAddOperationsForApp(curApp);
                    }
                    catch (final Exception e) {
                        this.logger.log(Level.SEVERE, "Exception when adding synced app ", e);
                        curApp.put("success", false);
                        curApp.put("ERROR_CODE", 2003);
                        this.handlePostAddOperationsForApp(curApp);
                    }
                }
            }
            final HashMap postSyncParams = new HashMap();
            postSyncParams.put("CustomerID", this.customerID);
            postSyncParams.put("BUSINESSSTORE_ID", this.businessStoreID);
            postSyncParams.put("userID", userID);
            postSyncParams.put("BUSINESSSTORE_ID", this.businessStoreID);
            postSyncParams.put("updateAppGroupList", updateAppGroupList);
            if (specificParams != null) {
                postSyncParams.put("specificParams", specificParams);
            }
            this.setPostSyncParams(syncedApps, includeNonPortalApps, postSyncParams, portalAndNonPortalAppDetails, platformType, newlyApprovedAppDetails);
            postSyncParams.put("newIdentifiers", params.optJSONArray("newIdentifiers"));
            this.handlePostSyncOperations(postSyncParams);
            this.logger.log(Level.INFO, "Names List of apps  {0}", namesList);
        }
        catch (final TokenResponseException | GoogleJsonResponseException ex) {
            this.logger.log(Level.WARNING, "Error while syncing Google Bstore", (Throwable)ex);
            final String errRemarks = GoogleAPIErrorHandler.getResponseErrorKey((Exception)ex);
            MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(this.businessStoreID, -1, errRemarks, null, 4);
            if (errRemarks.equals("mdm.appmgmt.afw.syncfailed.invalid_time_date")) {
                MDMApiFactoryProvider.getGoogleApiProductBasedHandler().handleServerTimeMismatch((Exception)ex, this.businessStoreID);
            }
            throw ex;
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Error while syncing Google Bstore", e2);
            final String errRemarks = GoogleAPIErrorHandler.getResponseErrorKey(e2);
            int errorCode = -1;
            if (errRemarks.equalsIgnoreCase("mdm.appmgmt.afw.syncfailed.proxy_cert_error")) {
                errorCode = 1000;
            }
            MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(this.businessStoreID, errorCode, errRemarks, null, 4);
            throw e2;
        }
        this.logger.log(Level.INFO, "Syncing Playstore apps completed for businessStoreId {0}", this.businessStoreID);
    }
    
    @Override
    public void handlePreSyncOperations(final HashMap params) throws Exception {
        if (params.getOrDefault("syncSelectedApps", false)) {
            final JSONObject syncDetails = MDBusinessStoreUtil.getBusinessStoreSyncDetails(this.businessStoreID);
            int completedCount = syncDetails.optInt("COMPLETED_APP_COUNT");
            int failedCount = syncDetails.optInt("FAILED_APP_COUNT");
            int totalAppCount = syncDetails.optInt("TOTAL_APP_COUNT");
            final List newIdentifiers = params.get("newIdentifiers");
            completedCount -= params.getOrDefault("TotalAppsToSync", 0);
            completedCount += params.getOrDefault("newApps", 0);
            totalAppCount += params.getOrDefault("newApps", 0);
            MDBusinessStoreUtil.updateCompletedAppsCount(this.businessStoreID, completedCount);
            MDBusinessStoreUtil.updateTotalAppsCount(this.businessStoreID, totalAppCount);
            MDBusinessStoreAssetUtil.deleteStoreAssetIds(this.businessStoreID, newIdentifiers);
            if (failedCount != 0) {
                failedCount -= newIdentifiers.size();
                MDBusinessStoreUtil.updateFailedAppsCount(this.businessStoreID, failedCount);
            }
        }
        MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(this.businessStoreID, -1, null, null, 2);
    }
    
    @Override
    public void handlePostAddOperationsForApp(final JSONObject jsonObject) throws DataAccessException, JSONException {
        final Long storeAssetId = jsonObject.optLong("STORE_ASSET_ID");
        final Long appReleaseLabelId = jsonObject.optLong("RELEASE_LABEL_ID");
        if (jsonObject.getBoolean("success")) {
            final Long appGroupId = jsonObject.optLong("APP_GROUP_ID");
            MDBusinessStoreAssetUtil.addMDStoreAssetToAppGroupRel(storeAssetId, appGroupId);
            MDBusinessStoreUtil.incrementBusinessStoreAppsCompletedCount(this.businessStoreID, 1);
        }
        else {
            MDBusinessStoreUtil.incrementBusinessStoreAppsFailedCount(this.businessStoreID, 1);
            final int errorCode = jsonObject.optInt("ERROR_CODE");
            MDBusinessStoreAssetUtil.addOrUpdateMdStoreAssetErrorDetails(storeAssetId, errorCode, null, null);
        }
        try {
            MDBusinessStoreUtil.updateCurrentSyncLastProgress(this.businessStoreID);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Unable to update sync progress time for " + this.businessStoreID, ex);
        }
    }
    
    private JSONArray getVersionsOnTrack(final String trackId, final JSONArray testingVersions) {
        final JSONArray versionArray = new JSONArray();
        for (int index = 0; index < testingVersions.length(); ++index) {
            final JSONObject version = (JSONObject)testingVersions.get(index);
            if (((String)version.get("TRACK_ID")).equalsIgnoreCase(trackId)) {
                versionArray.put((Object)version);
            }
        }
        return versionArray;
    }
    
    private boolean isNewVersionDetected(final DataObject dataObject, final String trackId, final String versionCode) throws DataAccessException {
        boolean isNewVersion = false;
        if (trackId == null) {
            final Long releaseID = AppVersionDBUtil.getInstance().getProductionAppReleaseLabelIDForCustomer(this.customerID);
            final Criteria releaseLabelCriteria = new Criteria(new Column("BusinessStoreAppVersion", "RELEASE_LABEL_ID"), (Object)releaseID, 0);
            final Criteria versionCodeCriteria = new Criteria(new Column("BusinessStoreAppVersion", "APP_NAME_SHORT_VERSION"), (Object)versionCode, 0);
            final Row row = dataObject.getRow("BusinessStoreAppVersion", releaseLabelCriteria.and(versionCodeCriteria));
            isNewVersion = (row == null);
        }
        else {
            final Criteria releaseLabelCriteria2 = new Criteria(new Column("ReleaseLabelToAppTrack", "TRACK_ID"), (Object)trackId, 0);
            final Row releaseLabelRow = dataObject.getRow("ReleaseLabelToAppTrack", releaseLabelCriteria2);
            if (releaseLabelRow != null) {
                final Long releaseLabelID = (Long)releaseLabelRow.get("RELEASE_LABEL_ID");
                final Criteria criteria = new Criteria(new Column("BusinessStoreAppVersion", "RELEASE_LABEL_ID"), (Object)releaseLabelID, 0);
                final Criteria versionCodeCriteria2 = new Criteria(new Column("BusinessStoreAppVersion", "APP_NAME_SHORT_VERSION"), (Object)versionCode, 0);
                final Row row2 = dataObject.getRow("BusinessStoreAppVersion", versionCodeCriteria2.and(criteria));
                isNewVersion = (row2 == null);
            }
            else {
                isNewVersion = true;
            }
        }
        return isNewVersion;
    }
    
    @Override
    public void handlePostSyncOperations(final HashMap params) throws Exception {
        try {
            final Long customerId = params.get("CustomerID");
            final Long businessStoreID = params.get("BUSINESSSTORE_ID");
            MDBusinessStoreUtil.updateStoreSyncStatus(businessStoreID, 6);
            final JSONObject specificParams = params.getOrDefault("specificParams", new JSONObject());
            final int syncType = specificParams.optInt("syncType", 1);
            final int syncSource = specificParams.optInt("source", 1);
            final GooglePlayDevicesSyncRequestHandler playHandler = new GooglePlayDevicesSyncRequestHandler(customerId);
            final JSONObject googleForWorkSettings = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
            final JSONArray newlyApprovedApps = params.getOrDefault("newIdentifiers", null);
            if (newlyApprovedApps != null && newlyApprovedApps.length() > 0) {
                this.handleNewlyApprovedApps(params);
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
            final int totalApps = AppsUtil.getInstance().getPortalApprovedApps(customerId, 2, businessStoreID).size();
            MDBusinessStoreUtil.updateTotalAppsCount(businessStoreID, totalApps);
            final JSONArray updatedAppGroupList = params.get("updateAppGroupList");
            if (updatedAppGroupList != null && updatedAppGroupList.length() > 0) {
                final List<Long> appGroupList = new JSONUtil().convertLongJSONArrayTOList(updatedAppGroupList);
                AutoAppUpdateHandler.getInstance().handleAutoAppUpdate(customerId, appGroupList);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error when handling post sync operations", ex);
        }
    }
    
    private void handleNewlyApprovedApps(final HashMap params) throws Exception {
        final Long customerId = params.get("CustomerID");
        final JSONObject queueObject = new JSONObject();
        queueObject.put("newIdentifiers", params.get("newIdentifiers"));
        final CommonQueueData afwQueueData = new CommonQueueData();
        afwQueueData.setCustomerId(customerId);
        afwQueueData.setTaskName("AFWNewlyApprovedAppsRedistributionTask");
        afwQueueData.setClassName("com.me.mdm.server.apps.android.afw.AFWNewlyApprovedAppsRedistributionTask");
        afwQueueData.setJsonQueueData(queueObject);
        CommonQueueUtil.getInstance().addToQueue(afwQueueData, CommonQueues.MDM_APP_MGMT);
    }
    
    @Override
    public void syncApps(JSONObject params) throws Exception {
        if (this.checkIfSyncAllowed(this.businessStoreID)) {
            this.resetStoreAndAssetDetails(this.businessStoreID, this.customerID);
            final JSONObject specificParams = params.getJSONObject("specificParams");
            params = this.setEffectiveSyncType(params, this.businessStoreID);
            final int source = specificParams.optInt("source", 1);
            if (source == 2) {
                this.updateNextSyncTime(this.businessStoreID);
            }
            params.put("includeNonPortalApps", true);
            params.put("PlatformType", 2);
            this.logger.log(Level.INFO, "Syncing Playstore apps for customerID {0} having businessStoreID {1} ", new Object[] { this.customerID, this.businessStoreID });
            try {
                this.syncSpecificApps(params);
            }
            catch (final TokenResponseException | GoogleJsonResponseException ex) {
                this.logger.log(Level.WARNING, "Error while syncing Google Bstore", (Throwable)ex);
                final String errRemarks = GoogleAPIErrorHandler.getResponseErrorKey((Exception)ex);
                MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(this.businessStoreID, -1, errRemarks, null, 4);
                if (errRemarks.equals("mdm.appmgmt.afw.syncfailed.invalid_time_date")) {
                    MDMApiFactoryProvider.getGoogleApiProductBasedHandler().handleServerTimeMismatch((Exception)ex, this.businessStoreID);
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
                MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(this.businessStoreID, errorCode, errRemarks, null, 4);
                throw e;
            }
            this.logger.log(Level.INFO, "Syncing Playstore apps completed for businessStoreId {0}", this.businessStoreID);
        }
        else {
            this.logger.log(Level.WARNING, "Syncing Playstore apps skipped for customerID {0} having businessStoreID {1} ", new Object[] { this.customerID, this.businessStoreID });
        }
    }
    
    private List<String> getExistingTracks(final DataObject appTracksObject) {
        List<String> trackIds = new ArrayList<String>();
        try {
            if (appTracksObject != null && !appTracksObject.isEmpty()) {
                final Iterator<Row> iterator = appTracksObject.getRows("ReleaseLabelToAppTrack");
                trackIds = new ArrayList<String>();
                trackIds.addAll(MDMDBUtil.getColumnValuesAsSet(iterator, "TRACK_ID"));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Cannot fetch track details");
        }
        return trackIds;
    }
    
    @Override
    protected void resetStoreAndAssetDetails(final Long businessStoreID, final Long customerId) {
        MDBusinessStoreUtil.resetAppSyncStatus(businessStoreID);
        MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(businessStoreID, -1, null, null, 1);
        MDBusinessStoreAssetUtil.resetMdStoreAssetErrorDetails(businessStoreID);
    }
}
