package com.me.mdm.server.apps.businessstore;

import com.me.mdm.server.apps.autoupdate.AutoAppUpdateHandler;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.server.tracker.mics.MICSAppRepositoryFeatureController;
import java.util.Collection;
import org.json.JSONArray;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.apps.AppLicenseHandler;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import java.util.HashMap;
import org.json.JSONObject;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public abstract class BaseSyncAppsHandler
{
    protected Logger logger;
    protected Long businessStoreID;
    protected Long customerID;
    protected EnterpriseBusinessStore enterpriseBusinessStore;
    protected Boolean firstSync;
    protected Boolean isAuthorized;
    public DataObject pkgToAppDO;
    
    public BaseSyncAppsHandler(final Long businessStoreID, final Long customerID) {
        this.logger = Logger.getLogger("MDMBStoreLogger");
        this.pkgToAppDO = (DataObject)new WritableDataObject();
        this.businessStoreID = businessStoreID;
        this.customerID = customerID;
    }
    
    public BaseSyncAppsHandler() {
        this.logger = Logger.getLogger("MDMBStoreLogger");
        this.pkgToAppDO = (DataObject)new WritableDataObject();
    }
    
    public Boolean checkIfAppAvailableAsPortalPurchased(final JSONObject jsonObject) {
        return false;
    }
    
    protected HashMap getPortalAndNonPortalAppDetails(final Boolean includeNonPortalApps, final int platformType) throws Exception {
        final HashMap portalAndNonPortalAppDetails = new HashMap();
        final int portalOrNonPortalApps = includeNonPortalApps ? 3 : 1;
        final JSONObject portalAndNonPortalAppsForCustomer = MDMAppMgmtHandler.getInstance().getPortalAndNonPortalAppsForCustomer(this.customerID, platformType, portalOrNonPortalApps);
        final List portalAppsBeforeSync = JSONUtil.getInstance().convertLongJSONArrayTOList(portalAndNonPortalAppsForCustomer.optJSONArray("portalApps"));
        List nonPortalAppsBeforeSync = new ArrayList();
        if (includeNonPortalApps) {
            nonPortalAppsBeforeSync = JSONUtil.getInstance().convertLongJSONArrayTOList(portalAndNonPortalAppsForCustomer.optJSONArray("nonPortalApps"));
        }
        portalAndNonPortalAppDetails.put("portalAppsBeforeSync", portalAppsBeforeSync);
        portalAndNonPortalAppDetails.put("nonPortalAppsBeforeSync", nonPortalAppsBeforeSync);
        return portalAndNonPortalAppDetails;
    }
    
    protected void handlePaidApps(final JSONObject curApp, final Long appGroupId, final JSONObject appJSON) throws DataAccessException {
        if (curApp.getJSONObject("MDPackageToAppGroupForm").getBoolean("IS_PAID_APP")) {
            new AppLicenseHandler().processStoreAppsLicense(appGroupId, this.customerID, appJSON);
        }
    }
    
    protected void setApprovedDAppDetailsIncurrAppJSON(final JSONObject curApp, final Long appGroupId, final HashMap portalAndNonPortalAppDetails) {
        final List nonPortalAppsBeforeSync = portalAndNonPortalAppDetails.get("nonPortalAppsBeforeSync");
        if (nonPortalAppsBeforeSync != null) {
            final boolean appPreviouslyAddedAsNonPortalApp = nonPortalAppsBeforeSync.contains(appGroupId);
            curApp.put("appPreviouslyAddedAsNonPortalApp", appPreviouslyAddedAsNonPortalApp);
        }
    }
    
    protected void setPostSyncParams(final List syncedApps, final Boolean includeNonPortalApps, final HashMap postSyncParams, final HashMap portalAndNonPortalAppDetails, final int platformType, final JSONArray newlyApprovedAppDetails) {
        List portalAppsBeforeSync = null;
        if (portalAndNonPortalAppDetails != null) {
            portalAppsBeforeSync = portalAndNonPortalAppDetails.get("portalAppsBeforeSync");
            if (portalAppsBeforeSync != null) {
                final List unManagedApps = new ArrayList(portalAppsBeforeSync);
                unManagedApps.removeAll(syncedApps);
                if (unManagedApps.size() > 0) {
                    postSyncParams.put("UnManagedApps", unManagedApps);
                    MICSAppRepositoryFeatureController.addTrackingData(platformType, MICSAppRepositoryFeatureController.AppOperation.DELETE_APP, false, false);
                }
            }
            if (includeNonPortalApps) {
                if (newlyApprovedAppDetails != null) {
                    postSyncParams.put("newlyApprovedAppDetails", newlyApprovedAppDetails);
                }
                final List nonPortalAppsBeforeSync = portalAndNonPortalAppDetails.get("nonPortalAppsBeforeSync");
                if (nonPortalAppsBeforeSync != null) {
                    postSyncParams.put("nonPortalAppsBeforeSync", nonPortalAppsBeforeSync);
                }
            }
        }
        if (syncedApps != null && !syncedApps.isEmpty()) {
            final List newlyApprovedApps = new ArrayList(syncedApps);
            if (portalAppsBeforeSync != null) {
                newlyApprovedApps.removeAll(portalAppsBeforeSync);
            }
            if (newlyApprovedApps.size() > 0) {
                postSyncParams.put("newlyApprovedApps", newlyApprovedApps);
                MICSAppRepositoryFeatureController.addTrackingData(platformType, MICSAppRepositoryFeatureController.AppOperation.ADD_APP, false, false);
            }
        }
    }
    
    protected void setIfNewlySyncedApp(final JSONObject curApp, final Long appGroupId, final HashMap portalAndNonPortalAppDetails) {
        final List portalAppsBeforeSync = portalAndNonPortalAppDetails.get("portalAppsBeforeSync");
        if (portalAppsBeforeSync != null) {
            final boolean isAppNewlySynced = !portalAppsBeforeSync.contains(appGroupId);
            curApp.put("isAppNewlySynced", isAppNewlySynced);
        }
    }
    
    public void syncApps(final JSONObject params) throws Exception {
        final int platformType = params.getInt("PlatformType");
        final Long userID = params.getLong("userID");
        this.firstSync = params.optBoolean("isFirstSync", false);
        this.enterpriseBusinessStore = BaseEnterpriseBusinessStore.getInstance(platformType, this.businessStoreID, this.customerID);
        final JSONObject BstoreDetails = EnterpriseBusinessStoreDBUtils.getInstance().getBusinessStoreDetails(platformType, this.businessStoreID, this.customerID, userID);
        final JSONObject credentials = this.enterpriseBusinessStore.getCredential(BstoreDetails);
        this.isAuthorized = credentials.optBoolean("Success", false);
        credentials.put("isFirstSync", (Object)this.firstSync);
        final JSONObject listOfApps = this.enterpriseBusinessStore.getAppDetails(credentials);
        final JSONObject specificParams = params.optJSONObject("specificParams");
        final JSONArray updateAppGroupList = new JSONArray();
        final List namesList = new ArrayList();
        final Boolean includeNonPortalApps = params.optBoolean("includeNonPortalApps", false);
        final HashMap portalAndNonPortalAppDetails = this.getPortalAndNonPortalAppDetails(includeNonPortalApps, platformType);
        final JSONArray newlyApprovedAppDetails = new JSONArray();
        final List syncedApps = new ArrayList();
        final HashMap hashMap = new HashMap();
        hashMap.put("CustomerID", this.customerID);
        hashMap.put("BUSINESSSTORE_ID", this.businessStoreID);
        if (listOfApps.optBoolean("Success", true)) {
            final JSONArray apps = listOfApps.optJSONArray("Apps");
            final int appsCount = (apps != null) ? apps.length() : 0;
            hashMap.put("TotalAppsToSync", appsCount);
            this.handlePreSyncOperations(hashMap);
            this.logger.log(Level.INFO, "No of Apps Detected : {0}", appsCount);
            for (int i = 0; i < appsCount; ++i) {
                final JSONObject appJSON = apps.getJSONObject(i);
                appJSON.put("CustomerID", (Object)this.customerID);
                appJSON.put("BUSINESSSTORE_ID", (Object)this.businessStoreID);
                appJSON.put("USER_ID", (Object)userID);
                final JSONObject curApp = this.enterpriseBusinessStore.processAppData(appJSON);
                curApp.put("CUSTOMER_ID", (Object)this.customerID);
                curApp.put("BUSINESSSTORE_ID", (Object)this.businessStoreID);
                try {
                    final boolean success = curApp.getBoolean("success");
                    if (!success || (curApp.optJSONArray("MDAPPS") != null && curApp.getJSONArray("MDAPPS").length() == 0)) {
                        this.handlePostAddOperationsForApp(curApp);
                    }
                    else {
                        namesList.add(("App : " + curApp.get("APP_NAME") + "  No of packages found :  " + curApp.optJSONArray("MDAPPS") != null) ? curApp.getJSONArray("MDAPPS").length() : 1);
                        curApp.put("PACKAGE_ADDED_BY", (Object)userID);
                        curApp.put("doNotRestore", Boolean.TRUE && !this.firstSync);
                        if (curApp.optBoolean("isNewVersion", true) || this.firstSync || curApp.optBoolean("isFreeToVppMigrated", false)) {
                            MDMAppMgmtHandler.getInstance().addOrUpdatePackageInRepository(curApp);
                            if (curApp.has("updatedAppGroupId") && curApp.get("updatedAppGroupId") != null) {
                                updateAppGroupList.put((Object)Long.valueOf(curApp.get("updatedAppGroupId").toString()));
                            }
                            appJSON.put("CUSTOMER_ID", (Object)this.customerID);
                            appJSON.put("BUSINESSSTORE_ID", BstoreDetails.getLong("BUSINESSSTORE_ID"));
                            final Long userId = BstoreDetails.optLong("BUSINESSSTORE_ADDED_BY", -1L);
                            appJSON.put("PACKAGE_ADDED_BY", (Object)userId);
                            appJSON.put("CREATED_BY", (Object)userId);
                            final Long appGroupId = curApp.getLong("APP_GROUP_ID");
                            this.handlePaidApps(curApp, appGroupId, appJSON);
                            if (includeNonPortalApps) {
                                curApp.put("newlyApprovedAppDetails", (Object)newlyApprovedAppDetails);
                                this.setApprovedDAppDetailsIncurrAppJSON(curApp, appGroupId, portalAndNonPortalAppDetails);
                            }
                            this.setIfNewlySyncedApp(curApp, appGroupId, portalAndNonPortalAppDetails);
                            syncedApps.add(appGroupId);
                            this.handlePostAddOperationsForApp(curApp);
                        }
                        else {
                            this.handlePostAddOperationsForApp(curApp);
                        }
                    }
                }
                catch (final Exception e) {
                    this.logger.log(Level.SEVERE, "Exception when adding synced app ", e);
                    curApp.put("success", false);
                    curApp.put("ERROR_CODE", 2003);
                    this.handlePostAddOperationsForApp(curApp);
                }
            }
        }
        else if (listOfApps.has("Error")) {
            throw new Exception("Error while fetching apps");
        }
        final HashMap postSyncParams = new HashMap();
        postSyncParams.put("CustomerID", this.customerID);
        postSyncParams.put("BUSINESSSTORE_ID", this.businessStoreID);
        postSyncParams.put("userID", userID);
        postSyncParams.put("BUSINESSSTORE_ID", this.businessStoreID);
        if (specificParams != null) {
            postSyncParams.put("specificParams", specificParams);
        }
        this.setPostSyncParams(syncedApps, includeNonPortalApps, postSyncParams, portalAndNonPortalAppDetails, platformType, newlyApprovedAppDetails);
        postSyncParams.put("updateAppGroupList", updateAppGroupList);
        this.handlePostSyncOperations(postSyncParams);
        this.logger.log(Level.INFO, "Names List of apps  {0}", namesList);
    }
    
    public abstract void handlePostAddOperationsForApp(final JSONObject p0) throws DataAccessException, JSONException;
    
    public void handlePostSyncOperations(final HashMap params) throws Exception {
        final Long customerId = params.get("CustomerID");
        final JSONArray updatedAppGroupList = params.get("updateAppGroupList");
        if (updatedAppGroupList != null && updatedAppGroupList.length() > 0) {
            final List<Long> appGroupList = new JSONUtil().convertLongJSONArrayTOList(updatedAppGroupList);
            AutoAppUpdateHandler.getInstance().handleAutoAppUpdate(customerId, appGroupList);
        }
    }
    
    public abstract void handlePreSyncOperations(final HashMap p0) throws Exception;
    
    public void syncSpecificApps(final JSONObject params) throws Exception {
        throw new UnsupportedOperationException("Not supported yet");
    }
}
