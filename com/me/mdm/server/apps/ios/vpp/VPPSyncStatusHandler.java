package com.me.mdm.server.apps.ios.vpp;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.apps.vpp.VPPAssetsHandler;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import org.json.JSONObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Logger;

public class VPPSyncStatusHandler
{
    public static VPPSyncStatusHandler vppSyncStatusHandler;
    final Logger logger;
    
    public VPPSyncStatusHandler() {
        this.logger = Logger.getLogger("MDMVPPAppsMgmtLogger");
    }
    
    public static VPPSyncStatusHandler getInstance() {
        if (VPPSyncStatusHandler.vppSyncStatusHandler == null) {
            VPPSyncStatusHandler.vppSyncStatusHandler = new VPPSyncStatusHandler();
        }
        return VPPSyncStatusHandler.vppSyncStatusHandler;
    }
    
    public int getVppAppCount(final long businessStoreID) {
        int totalVppAppInRepo = 0;
        try {
            final SelectQuery assetQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdStoreAssetToAppGroupRel"));
            assetQuery.addJoin(new Join("MdStoreAssetToAppGroupRel", "MdVppAsset", new String[] { "STORE_ASSET_ID" }, new String[] { "VPP_ASSET_ID" }, 2));
            assetQuery.addJoin(new Join("MdVppAsset", "MdVPPTokenDetails", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            assetQuery.addJoin(new Join("MdVPPTokenDetails", "MdBusinessStoreToVppRel", new String[] { "TOKEN_ID" }, new String[] { "TOKEN_ID" }, 2));
            assetQuery.addJoin(new Join("MdBusinessStoreToVppRel", "ManagedBusinessStore", new String[] { "BUSINESSSTORE_ID" }, new String[] { "BUSINESSSTORE_ID" }, 2));
            assetQuery.setCriteria(new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0));
            totalVppAppInRepo = DBUtil.getRecordCount(assetQuery, "MdVppAsset", "VPP_ASSET_ID");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getVppAppCount", e);
        }
        return totalVppAppInRepo;
    }
    
    public JSONObject getSyncStatus(final Long businessStoreID) throws Exception {
        final JSONObject syncResponseJSON = new JSONObject();
        final JSONObject syncDetails = MDBusinessStoreUtil.getBusinessStoreSyncDetails(businessStoreID);
        final Integer status = syncDetails.optInt("STORE_SYNC_STATUS");
        if (status != null) {
            int totalCount = 0;
            int completedCount = 0;
            int failureCount = 0;
            int successCount = 0;
            String remarks = "";
            Integer errorCode = 0;
            Long lastSyncTime = null;
            if (syncDetails.length() > 0) {
                totalCount = syncDetails.optInt("TOTAL_APP_COUNT", 0);
                completedCount = syncDetails.optInt("COMPLETED_APP_COUNT", 0);
                failureCount = syncDetails.optInt("FAILED_APP_COUNT", 0);
                successCount = completedCount - failureCount;
                lastSyncTime = (Long)syncDetails.opt("LAST_SUCCESSFULL_SYNC_TIME");
            }
            syncResponseJSON.put("total_apps_count", totalCount);
            syncResponseJSON.put("completed_apps_count", completedCount);
            syncResponseJSON.put("successful_apps_count", successCount);
            syncResponseJSON.put("failed_apps_count", failureCount);
            syncResponseJSON.put("businessstore_id", (Object)businessStoreID);
            if (lastSyncTime != null) {
                syncResponseJSON.put("last_sync_time", (Object)lastSyncTime);
            }
            final int appsWithInsufficientLicense = this.getCountForAppsWithInsufficientLicense(businessStoreID);
            if (appsWithInsufficientLicense > 0) {
                syncResponseJSON.put("if_licenses_insufficient", (Object)Boolean.TRUE);
                syncResponseJSON.put("apps_with_insufficient_licenses", appsWithInsufficientLicense);
            }
            remarks = syncDetails.optString("REMARKS", "");
            syncResponseJSON.put("remarks", (Object)remarks);
            errorCode = syncDetails.optInt("ERROR_CODE", 0);
            if (errorCode == 888801) {
                try {
                    final String remarksParams = syncDetails.optString("REMARKS_PARAMS");
                    final JSONObject clientContextJson = new JSONObject(remarksParams);
                    final String hostname = clientContextJson.optString("hostname", "other MDM");
                    syncResponseJSON.put("other_mdm_hostname", (Object)hostname);
                }
                catch (final Exception e) {
                    this.logger.log(Level.INFO, "ClientContext is not JSONObject String as it is expected. Hence, setting default hostName for businessStore: {0}", new Object[] { businessStoreID });
                    syncResponseJSON.put("other_mdm_hostname", (Object)"other MDM");
                }
            }
            if (status != null) {
                syncResponseJSON.put("status", (Object)status);
            }
            if (errorCode != null && errorCode != 0) {
                syncResponseJSON.put("if_sync_failed", (Object)Boolean.TRUE);
            }
            final String freeToVPPAppsCountInString = MDBusinessStoreUtil.getBusinessStoreParamValue("storeToVppMigrationCount", businessStoreID);
            if (freeToVPPAppsCountInString != null) {
                syncResponseJSON.put("free_to_vpp_apps_count", Integer.parseInt(freeToVPPAppsCountInString));
            }
        }
        return syncResponseJSON;
    }
    
    private int getCountForAppsWithInsufficientLicense(final Long businessStoreID) {
        int count = 0;
        try {
            final SelectQuery selectQuery = VPPAssetsHandler.getInstance().getVppAssetsQuery();
            selectQuery.addJoin(new Join("MdVppAsset", "MdStoreAssetErrorDetails", new String[] { "VPP_ASSET_ID" }, new String[] { "STORE_ASSET_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("MdStoreAssetErrorDetails", "STORE_ASSET_ID"));
            final Criteria businessStoreCriteria = new Criteria(Column.getColumn("ManagedBusinessStore", "BUSINESSSTORE_ID"), (Object)businessStoreID, 0);
            final Criteria insufficientLicenseCriteria = new Criteria(Column.getColumn("MdStoreAssetErrorDetails", "ERROR_CODE"), (Object)888804, 0);
            selectQuery.setCriteria(businessStoreCriteria.and(insufficientLicenseCriteria));
            final DataObject dataObject = MDMUtil.getPersistence().get(selectQuery);
            if (!dataObject.isEmpty()) {
                count = dataObject.size("MdStoreAssetErrorDetails");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getCountForAppsWithInsufficientLicense", e);
        }
        return count;
    }
    
    public String getVPPSyncProgressFromCache(final Long businessStoreID) {
        try {
            final Object syncStatus = ApiFactoryProvider.getCacheAccessAPI().getCache("VppSync_Status", 2);
            if (syncStatus != null) {
                final JSONObject json = new JSONObject(syncStatus.toString());
                final JSONObject dataJson = (JSONObject)json.get(businessStoreID.toString());
                return dataJson.get("VppSync_Status").toString();
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, String.format("Exception in getVPPSyncProgressFromCache for businessStoreID : %s ", businessStoreID.toString()), ex.getCause());
            this.logger.log(Level.INFO, "VPP Sync might be completed");
        }
        return "No Status";
    }
    
    public void updateSyncStatus(final Long businessStoreID, final Integer vppSyncStatus) throws Exception {
        MDBusinessStoreUtil.updateStoreSyncStatus(businessStoreID, vppSyncStatus);
    }
    
    public void putVPPSyncProgressToCache(final String vppSyncStatus, final Long businessStoreID) {
        try {
            final JSONObject json = new JSONObject();
            final JSONObject dataJson = new JSONObject();
            dataJson.put("VppSync_Status", (Object)vppSyncStatus);
            json.put(businessStoreID.toString(), (Object)dataJson);
            ApiFactoryProvider.getCacheAccessAPI().putCache("VppSync_Status", (Object)json.toString(), 2, 86400);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception in putVPPSyncProgressToCache for storeID" + n + "{0}");
        }
    }
    
    static {
        VPPSyncStatusHandler.vppSyncStatusHandler = null;
    }
}
