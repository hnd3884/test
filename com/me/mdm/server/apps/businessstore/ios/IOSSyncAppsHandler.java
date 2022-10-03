package com.me.mdm.server.apps.businessstore.ios;

import org.json.JSONArray;
import com.adventnet.sym.server.mdm.apps.AppLicenseMgmtHandler;
import com.me.mdm.server.apps.AppUpdateSync;
import com.me.mdm.server.apps.ios.vpp.VPPSyncStatusHandler;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAppMgmtHandler;
import com.me.mdm.server.tracker.mics.MICSStoreConfigurationFeatureController;
import com.me.mdm.server.apps.ios.vpp.VPPLicenseSyncHandler;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAssetsHandler;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import java.util.Properties;
import com.me.mdm.server.apps.ios.vpp.IOSVPPEnterpriseBusinessStore;
import java.util.Iterator;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.Hashtable;
import com.adventnet.sym.server.mdm.apps.vpp.VPPManagedUserHandler;
import com.me.mdm.server.apps.ios.vpp.VPPTokenDataHandler;
import com.me.mdm.server.apps.ios.vpp.VPPClientConfigHandler;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import org.json.JSONObject;
import java.util.HashMap;
import com.me.mdm.server.apps.businessstore.BaseSyncAppsHandler;

public class IOSSyncAppsHandler extends BaseSyncAppsHandler
{
    int noOfAppsSyncFailed;
    HashMap syncedAppDetailsMap;
    Boolean isDailySync;
    
    public IOSSyncAppsHandler(final Long businessStoreID, final Long customerID) {
        super(businessStoreID, customerID);
        this.syncedAppDetailsMap = new HashMap();
    }
    
    @Override
    public Boolean checkIfAppAvailableAsPortalPurchased(final JSONObject jsonObject) {
        Boolean check = false;
        try {
            final String bundleIdentifier = (String)jsonObject.get("BUNDLE_IDENTIFIER");
            final String version = jsonObject.optString("APP_VERSION", "--").trim();
            final Boolean isCaseSensitive = AppsUtil.getInstance().getIsBundleIdCaseSenstive(1);
            final Criteria identifierCriteria = new Criteria(Column.getColumn("MdAppDetails", "IDENTIFIER"), (Object)bundleIdentifier, 0, (boolean)isCaseSensitive);
            final Criteria versionCriteria = new Criteria(Column.getColumn("MdAppDetails", "APP_VERSION"), (Object)version, 0, (boolean)isCaseSensitive);
            Criteria criteria = identifierCriteria.and(versionCriteria);
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table("MdAppDetails"));
            final Join packageToAppDataJoin = new Join("MdAppDetails", "MdPackageToAppData", new String[] { "APP_ID" }, new String[] { "APP_ID" }, 2);
            final Join packageToAppGroupJoin = new Join("MdPackageToAppData", "MdPackageToAppGroup", new String[] { "PACKAGE_ID" }, new String[] { "PACKAGE_ID" }, 2);
            selectQuery.addJoin(packageToAppDataJoin);
            selectQuery.addJoin(packageToAppGroupJoin);
            selectQuery.addJoin(new Join("MdPackageToAppGroup", "MdStoreAssetToAppGroupRel", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            selectQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            selectQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            selectQuery.addJoin(new Join("MdBusinessStoreToVppRel", "Resource", new String[] { "BUSINESSSTORE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            final Criteria storeCustCriteria = new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)this.customerID, 0);
            criteria = criteria.and(storeCustCriteria);
            final Criteria customerCriteria = new Criteria(Column.getColumn("MdAppDetails", "CUSTOMER_ID"), (Object)this.customerID, 0);
            final Criteria platformCriteria = new Criteria(Column.getColumn("MdAppDetails", "PLATFORM_TYPE"), (Object)1, 0);
            selectQuery.setCriteria(customerCriteria.and(platformCriteria).and(criteria));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "PACKAGE_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "APP_GROUP_ID"));
            selectQuery.addSelectColumn(Column.getColumn("MdPackageToAppData", "STORE_ID"));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                final JSONObject packageGroupJSON = jsonObject.getJSONObject("MdPackageToAppDataFrom");
                final Row row = dataObject.getFirstRow("MdPackageToAppData");
                final Long appGroupId = (Long)row.get("APP_GROUP_ID");
                final String appStoreID = row.get("STORE_ID").toString();
                packageGroupJSON.put("STORE_ID", (Object)appStoreID);
                jsonObject.put("APP_GROUP_ID", (Object)appGroupId);
                check = true;
            }
            else {
                check = false;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in checkIfAppAlreadyPresentAsPurchasedFromPortal", e);
        }
        return check;
    }
    
    public void handlePaidApps(final JSONObject curApp, final Long appGroupID, final JSONObject appJSON) {
    }
    
    @Override
    protected void setApprovedDAppDetailsIncurrAppJSON(final JSONObject curApp, final Long appGroupId, final HashMap portalAndNonPortalAppDetails) {
    }
    
    protected void setPostSyncParams(final List syncedApps, final Boolean includeNonPortalApps, final HashMap postSyncParams, final HashMap portalAndNonPortalAppDetails) {
    }
    
    @Override
    protected void setIfNewlySyncedApp(final JSONObject curApp, final Long appGroupId, final HashMap portalAndNonPortalAppDetails) {
    }
    
    @Override
    protected HashMap getPortalAndNonPortalAppDetails(final Boolean includeNonPortalApps, final int platformType) throws Exception {
        return null;
    }
    
    private boolean isOkayToSyncWithVpp() {
        boolean isOkayToSyncWithVpp = true;
        try {
            final Integer status = MDBusinessStoreUtil.getStoreSyncStatus(this.businessStoreID);
            if (status != null && (status == 2 || status == 1)) {
                isOkayToSyncWithVpp = false;
            }
            if (!isOkayToSyncWithVpp) {
                final Long lastSyncProgress = MDBusinessStoreUtil.getCurrentSyncLastProgress(this.businessStoreID);
                if (lastSyncProgress != null) {
                    final Long currTime = System.currentTimeMillis();
                    if (currTime - lastSyncProgress > 21600000L) {
                        isOkayToSyncWithVpp = true;
                        this.logger.log(Level.INFO, " Already a sync is i nprogress. But no update for more that 5 minutes. So setting isOkayToSyncWithVpp to true.");
                    }
                }
                else if (lastSyncProgress == null) {
                    isOkayToSyncWithVpp = true;
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while checking isOkayToSyncWithVpp() {0}", ex);
        }
        this.logger.log(Level.INFO, "In IOSSyncAppsHandler: isOkayToSyncWithVpp {0}", isOkayToSyncWithVpp);
        return isOkayToSyncWithVpp;
    }
    
    private void deleteSyncErrorDetails() {
        try {
            final DeleteQuery deleteQuery = (DeleteQuery)new DeleteQueryImpl("MdBusinessStoreSyncStatus");
            deleteQuery.setCriteria(new Criteria(Column.getColumn("MdBusinessStoreSyncStatus", "BUSINESSSTORE_ID"), (Object)this.businessStoreID, 0));
            MDMUtil.getPersistence().delete(deleteQuery);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in deleteSyncErrorDetails");
        }
    }
    
    private void setInitialSyncDetails(final Long businessStoreID, final Long customerID) {
        try {
            MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(businessStoreID, -1, null, null, 1);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in setInitialSyncStatusDetails");
        }
    }
    
    @Override
    public void syncApps(final JSONObject params) throws Exception {
        try {
            params.put("PlatformType", 1);
            final Boolean toSetNewClientContext = params.optBoolean("toSetNewClientContext", false);
            if (toSetNewClientContext != null && toSetNewClientContext) {
                VPPClientConfigHandler.getInstance().generateAndAddVppClientConfig(this.businessStoreID, this.customerID);
                params.put("isFirstSync", (Object)Boolean.TRUE);
            }
            else {
                params.put("isFirstSync", (Object)Boolean.FALSE);
            }
            this.isDailySync = params.optBoolean("isDailySync", false);
            if (this.isOkayToSyncWithVpp()) {
                this.setInitialSyncDetails(this.businessStoreID, this.customerID);
                if (this.isDailySync) {
                    MDBusinessStoreUtil.updateStoreNextSyncTime(this.businessStoreID, System.currentTimeMillis() + 86400000L);
                }
                super.syncApps(params);
                if (this.isDailySync) {
                    VPPTokenDataHandler.getInstance().validateAndSendVPPTokenExpiryMail(this.businessStoreID);
                }
                this.checkAndSetMsgBoxesForVppTokenExpiry(this.customerID);
                final String sToken = VPPTokenDataHandler.getInstance().getVppToken(this.businessStoreID);
                VPPManagedUserHandler.getInstance().updateVPPUsersStatus(this.businessStoreID, sToken, this.customerID, null);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in syncApps", e);
            throw e;
        }
        finally {
            MDBusinessStoreUtil.updateStoreSyncStatus(this.businessStoreID, 3);
        }
    }
    
    private void checkAndSetMsgBoxesForVppTokenExpiry(final Long customerID) {
        Boolean isAnyVPPExpiredForCustomer = false;
        Boolean isAnyVPPAboutToExpireForCustomer = false;
        try {
            final SelectQuery tokenQuery = VPPTokenDataHandler.getInstance().getVppTokenDetailsQuery();
            tokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "TOKEN_ID"));
            tokenQuery.addSelectColumn(Column.getColumn("MdVPPTokenDetails", "EXPIRY_DATE"));
            tokenQuery.setCriteria(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0));
            final DataObject tokenDO = MDMUtil.getPersistence().get(tokenQuery);
            final Iterator iter = tokenDO.getRows("MdVPPTokenDetails");
            while (iter.hasNext()) {
                final Row tokenRow = iter.next();
                final Long expiryDate = (Long)tokenRow.get("EXPIRY_DATE");
                Hashtable ht = new Hashtable();
                ht = DateTimeUtil.determine_From_To_Times("today");
                final Long today = ht.get("date1");
                final Long diff = expiryDate - today;
                final int remainingDay = (int)(diff / 86400000L);
                if (remainingDay < 15 && !isAnyVPPAboutToExpireForCustomer) {
                    isAnyVPPAboutToExpireForCustomer = true;
                }
                if (remainingDay <= 0 && !isAnyVPPExpiredForCustomer) {
                    isAnyVPPExpiredForCustomer = true;
                }
                if (isAnyVPPAboutToExpireForCustomer && isAnyVPPAboutToExpireForCustomer) {
                    break;
                }
            }
            if (isAnyVPPExpiredForCustomer || isAnyVPPAboutToExpireForCustomer) {
                MessageProvider.getInstance().unhideMessage("VPP_EXPIRED_OR_ABOUT_TO_EXPIRE", customerID);
            }
            else {
                MessageProvider.getInstance().hideMessage("VPP_EXPIRED_OR_ABOUT_TO_EXPIRE", customerID);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in checkAndOpenMsgBoxesForVppTokenExpiry", e);
        }
    }
    
    @Override
    public void handlePostAddOperationsForApp(final JSONObject jsonObject) throws DataAccessException, JSONException {
        try {
            if (jsonObject.getBoolean("success")) {
                final JSONObject packageGroupJSON = jsonObject.getJSONObject("MdPackageToAppDataFrom");
                final Long appGroupID = jsonObject.getLong("APP_GROUP_ID");
                final String appStoreID = packageGroupJSON.getString("STORE_ID");
                final HashMap vppAssetMap = ((IOSVPPEnterpriseBusinessStore)this.enterpriseBusinessStore).getVppAssetsAPIHandler().getVppAssetMap();
                final Properties properties = vppAssetMap.get(appStoreID);
                final Long assetID = ((Hashtable<K, Long>)properties).get("VPP_ASSET_ID");
                final Properties storeDetails = new Properties();
                ((Hashtable<String, String>)storeDetails).put("STORE_ID", appStoreID);
                ((Hashtable<String, Long>)storeDetails).put("APP_GROUP_ID", appGroupID);
                ((Hashtable<String, Object>)storeDetails).put("ASSIGNED_LICENSE_COUNT", ((Hashtable<K, Object>)properties).get("ASSIGNED_LICENSE_COUNT"));
                ((Hashtable<String, Boolean>)storeDetails).put("isFreeToVppMigrated", jsonObject.optBoolean("isFreeToVppMigrated", false));
                this.syncedAppDetailsMap.put(assetID, storeDetails);
            }
            else {
                MDBusinessStoreUtil.incrementBusinessStoreAppsFailedCount(this.businessStoreID, new Integer(1));
                MDBusinessStoreUtil.incrementBusinessStoreAppsCompletedCount(this.businessStoreID, new Integer(1));
            }
            this.updateSyncProgressTime();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Unable to update sync progress time for businessStoreID" + this.businessStoreID);
        }
    }
    
    protected void updateSyncProgressTime() throws Exception {
        MDBusinessStoreUtil.updateCurrentSyncLastProgress(this.businessStoreID);
    }
    
    @Override
    public void handlePostSyncOperations(final HashMap params) throws DataAccessException {
        try {
            if (this.isAuthorized) {
                if (!this.syncedAppDetailsMap.isEmpty()) {
                    final Long userID = params.get("userID");
                    final Long[] assetIDArray = (Long[])this.syncedAppDetailsMap.keySet().toArray(new Long[this.syncedAppDetailsMap.size()]);
                    VPPAssetsHandler.getInstance().addOrUpdateAssetToAppGroupRel(this.syncedAppDetailsMap, assetIDArray);
                    for (int i = 0; i < assetIDArray.length; ++i) {
                        final Long assetID = assetIDArray[i];
                        final Properties properties = this.syncedAppDetailsMap.get(assetID);
                        final String appStoreID = ((Hashtable<K, Object>)properties).get("STORE_ID").toString();
                        final Long appGroupID = ((Hashtable<K, Long>)properties).get("APP_GROUP_ID");
                        final boolean isFreeToVppMigrated = ((Hashtable<K, Boolean>)properties).getOrDefault("isFreeToVppMigrated", Boolean.FALSE);
                        final int assignedCount = ((Hashtable<K, Integer>)properties).getOrDefault("ASSIGNED_LICENSE_COUNT", 0);
                        final VPPLicenseSyncHandler syncHandler = new VPPLicenseSyncHandler(this.customerID, userID, appGroupID, appStoreID, this.businessStoreID, isFreeToVppMigrated, Boolean.TRUE);
                        final int syncStatus = syncHandler.getSyncStatus();
                        if ((isFreeToVppMigrated || this.isDailySync || this.firstSync || syncHandler.isValidToSyncVppLicenses()) && (syncStatus == 0 || syncStatus == 3)) {
                            syncHandler.syncVppLicenses();
                        }
                        else {
                            VPPAssetsHandler.getInstance().updateAppStatusForBusinessStore(this.businessStoreID, appStoreID, 0, MDMUtil.getCurrentTimeInMillis());
                        }
                        MDBusinessStoreUtil.incrementBusinessStoreAppsCompletedCount(this.businessStoreID, new Integer(1));
                    }
                    final int freeToVPPAppsCount = ((IOSVPPEnterpriseBusinessStore)this.enterpriseBusinessStore).getVppAssetsAPIHandler().getToBeMigratedAdamIDs().size();
                    MDBusinessStoreUtil.addOrUpdateBusinessStoreParam("storeToVppMigrationCount", String.valueOf(freeToVPPAppsCount), this.businessStoreID);
                    super.handlePostSyncOperations(params);
                }
                else {
                    this.logger.log(Level.INFO, "No apps available in VPP token for Syncing Licenses");
                }
                final String isFirstSyncPending = MDBusinessStoreUtil.getBusinessStoreParamValue(IOSStoreHandler.VPP_STORE_SYNC_PARAM, this.businessStoreID);
                if (isFirstSyncPending != null && Boolean.valueOf(isFirstSyncPending)) {
                    MICSStoreConfigurationFeatureController.addTrackingData(1, MICSStoreConfigurationFeatureController.StoreConfigurationOperation.COMPLETE);
                }
                MDBusinessStoreUtil.addOrUpdateBusinessStoreParam(IOSStoreHandler.VPP_STORE_SYNC_PARAM, "false", this.businessStoreID);
                MDBusinessStoreUtil.updateLastSuccessfulSyncTime(this.businessStoreID);
                VPPAppMgmtHandler.getInstance().updateIsPurchasedFromPortalForNonVppApps(this.customerID);
            }
            else {
                this.logger.log(Level.SEVERE, "Issue in VPP token Sync");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in handlePostSyncOperations", ex);
            try {
                VPPSyncStatusHandler.getInstance().updateSyncStatus(this.businessStoreID, 3);
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception in updating sync status");
            }
        }
        finally {
            try {
                VPPSyncStatusHandler.getInstance().updateSyncStatus(this.businessStoreID, 3);
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception in updating sync status");
            }
        }
    }
    
    private void updateAppStoreAppsAndAddDetailsInUpdatedList(final HashMap params) {
        final AppUpdateSync upgradeSync = new AppUpdateSync(this.customerID, 1);
        upgradeSync.syncNonPortalApps();
        upgradeSync.syncStoreIds(new AppLicenseMgmtHandler().getIOSRedemptionCodeAppsStoreIDs());
        final JSONArray updatedAppStoreAppGroupList = upgradeSync.getUpdateAppGroupList();
        JSONArray updatedAppGroupList = params.get("updateAppGroupList");
        if (updatedAppGroupList == null) {
            updatedAppGroupList = new JSONArray();
        }
        if (updatedAppStoreAppGroupList != null && updatedAppStoreAppGroupList.length() > 0) {
            updatedAppGroupList.put((Object)updatedAppStoreAppGroupList);
        }
        if (updatedAppGroupList != null && updatedAppGroupList.length() > 0) {
            params.put("updateAppGroupList", updatedAppGroupList);
        }
    }
    
    @Override
    public void handlePreSyncOperations(final HashMap params) throws Exception {
        MDBusinessStoreUtil.updateStoreSyncStatus(this.businessStoreID, 2);
    }
    
    protected void setPostSyncParams(final List syncedApps, final Boolean includeNonPortalApps, final HashMap postSyncParams, final HashMap portalAndNonPortalAppDetails, final int platformType) {
    }
}
