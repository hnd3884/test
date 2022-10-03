package com.me.mdm.server.apps.android.afw.appmgmt;

import java.util.Hashtable;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import java.util.Map;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.apps.config.AppConfigPolicyDBHandler;
import com.me.mdm.server.apps.multiversion.AppVersionDBUtil;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreAssetUtil;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import com.me.mdm.server.apps.businessstore.BusinessStoreSyncConstants;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.apps.android.afw.layoutmgmt.StoreLayoutManager;
import java.util.List;
import org.json.JSONArray;
import com.me.mdm.server.apps.android.afw.GooglePlayEnterpriseBusinessStore;
import java.util.logging.Level;
import java.util.Properties;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import java.util.logging.Logger;

public class GooglePlayBusinessAppHandler
{
    public Logger logger;
    public Logger bslogger;
    public static final int SYNC_ONLY_APPS = 1;
    public static final int SYNC_SKIP_DEVICES = 2;
    public static final int SYNC_ALL = 3;
    public static final int SYNC_SOURCE_UI = 1;
    public static final int SYNC_SOURCE_SCHEDULER = 2;
    public static final int SYNC_SOURCE_OTHERS = 3;
    
    public GooglePlayBusinessAppHandler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.bslogger = Logger.getLogger("MDMBStoreLogger");
    }
    
    public static Long getBSStoreAssociatedUser(final Long customerID) throws Exception {
        final JSONObject playStoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerID, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        return playStoreDetails.optLong("BUSINESSSTORE_ADDED_BY", -1L);
    }
    
    public void syncGooglePlay(final Long customerId, final int syncType, final int source) throws Exception {
        if (GoogleForWorkSettings.isAFWSettingsConfigured(customerId)) {
            final JSONObject jsonParams = new JSONObject();
            jsonParams.put("PlatformType", 2);
            jsonParams.put("CustomerID", (Object)customerId);
            final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            if (userId != null) {
                jsonParams.put("userID", (Object)userId);
            }
            else {
                jsonParams.put("userID", (Object)getBSStoreAssociatedUser(customerId));
            }
            jsonParams.put("syncType", syncType);
            jsonParams.put("source", source);
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "SyncAFWAppsTask");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            taskInfoMap.put("poolName", "mdmPool");
            final Properties taskProps = new Properties();
            ((Hashtable<String, String>)taskProps).put("jsonParams", jsonParams.toString());
            ((Hashtable<String, Long>)taskProps).put("customerID", customerId);
            this.logger.log(Level.INFO, "Starting Task to Sync Android Business Store details for customer : {0} with sync type {1}", new Object[] { customerId, syncType });
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.apps.businessstore.SyncAppsTask", taskInfoMap, taskProps);
        }
        else {
            this.logger.log(Level.INFO, "AFW not configured for customer : {0}. Ignoring call for sync", customerId);
        }
    }
    
    public void preFillEarlierAssignedLicense(final JSONObject appJSON, final Long customerId) {
        try {
            final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW));
            final String bundleId = String.valueOf(appJSON.get("IDENTIFIER"));
            final JSONArray userIdArr = ebs.getLicenseAssignedUserList(bundleId);
            final Long bsId = appJSON.getLong("BUSINESSSTORE_ID");
            final Long appGroupId = appJSON.getLong("APP_GROUP_ID");
            new StoreAppsLicenseHandler().addOrUpdateStoreAppsLicenseToBSUsers(bsId, appGroupId, userIdArr);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception in preFillEarlierAssignedLicense", e);
        }
    }
    
    @Deprecated
    public void handleNewlyApprovedAppsForLayout(final JSONObject playStoreDetails, final List newlyDetectedApps, final Long userId, final Long customerId) throws Exception {
        this.logger.log(Level.INFO, "Adding new apps to default cluster");
        final Long bsId = playStoreDetails.getLong("BUSINESSSTORE_ID");
        final StoreLayoutManager manager = new StoreLayoutManager();
        final JSONObject storeLayoutJSON = manager.getStoreLayoutForBusinessStore(bsId, customerId);
        JSONArray pageArr = storeLayoutJSON.optJSONArray("StoreLayoutPage");
        if (pageArr == null) {
            pageArr = new JSONArray();
        }
        JSONObject homePage = pageArr.optJSONObject(0);
        if (homePage == null) {
            homePage = new JSONObject();
            homePage.put("PAGE_NAME", (Object)"Home Page");
        }
        JSONArray clusters = homePage.optJSONArray("StoreLayoutClusters");
        if (clusters == null) {
            clusters = new JSONArray();
        }
        JSONObject defaultCluster = clusters.optJSONObject(0);
        if (defaultCluster == null) {
            defaultCluster = new JSONObject();
        }
        else if (!defaultCluster.optString("CLUSTER_NAME", "").equals("Newly Approved Apps")) {
            defaultCluster = new JSONObject();
        }
        defaultCluster.put("CLUSTER_NAME", (Object)"Newly Approved Apps");
        defaultCluster.put("CLUSTER_ORDER_NUMBER", 1);
        JSONArray clusterApps = defaultCluster.optJSONArray("StoreLayoutClusterApps");
        if (clusterApps == null) {
            clusterApps = new JSONArray();
        }
        if (!newlyDetectedApps.isEmpty()) {
            for (int i = 0; i < newlyDetectedApps.size(); ++i) {
                clusterApps.put(newlyDetectedApps.get(i));
            }
        }
        defaultCluster.put("StoreLayoutClusterApps", (Object)clusterApps);
        clusters = JSONUtil.getInstance().insertElementInJSONArray(clusters, defaultCluster, 0);
        homePage.put("StoreLayoutClusters", (Object)clusters);
        pageArr.put(0, (Object)homePage);
        storeLayoutJSON.put("StoreLayoutPage", (Object)pageArr);
        storeLayoutJSON.put("LAST_MODIFIED_BY", (Object)userId);
        manager.publishStoreLayout(playStoreDetails.getLong("CUSTOMER_ID"), storeLayoutJSON);
    }
    
    public void handleUnApprovedApps(final List unapprovedApps, final Long customerId) throws DataAccessException {
        this.logger.log(Level.INFO, "Unapproved apps list {0}", unapprovedApps);
        final UpdateQuery uQuery = (UpdateQuery)new UpdateQueryImpl("MdPackageToAppGroup");
        uQuery.setCriteria(new Criteria(Column.getColumn("MdPackageToAppGroup", "APP_GROUP_ID"), (Object)unapprovedApps.toArray(), 8));
        uQuery.setUpdateColumn("IS_PURCHASED_FROM_PORTAL", (Object)Boolean.FALSE);
        uQuery.setUpdateColumn("PRIVATE_APP_TYPE", (Object)0);
        DataAccess.update(uQuery);
        final Criteria appGroupIdCriteria = new Criteria(Column.getColumn("StoreLayoutClusterApps", "APP_GROUP_ID"), (Object)unapprovedApps.toArray(), 8);
        DataAccess.delete("StoreLayoutClusterApps", appGroupIdCriteria);
        final Criteria appGroupVersionDeleteCriteria = new Criteria(Column.getColumn("BusinessStoreAppVersion", "APP_GROUP_ID"), (Object)unapprovedApps.toArray(), 8);
        DataAccess.delete("BusinessStoreAppVersion", appGroupVersionDeleteCriteria);
        final Long businessStoreID = MDBusinessStoreUtil.getBusinessStoreID(customerId, BusinessStoreSyncConstants.BS_SERVICE_AFW);
        MDBusinessStoreAssetUtil.removeStoreAssetsFromMdBusinessStoreToAssetRel(businessStoreID, unapprovedApps);
        final Map<Long, List<Long>> appGroupToNonProdLabel = AppVersionDBUtil.getInstance().getNonProdLabelForAppGroup(unapprovedApps, customerId);
        try {
            for (final Long appGroupId : appGroupToNonProdLabel.keySet()) {
                final Long approvedAppId = AppConfigPolicyDBHandler.getInstance().getProductionAppIDFromAppGroupID(appGroupId, customerId);
                AppsUtil.getInstance().setApprovedAppIdForResource(appGroupToNonProdLabel.get(appGroupId), appGroupId, approvedAppId);
                final Criteria appGroupCriteria = new Criteria(new Column("AppGroupToCollection", "APP_GROUP_ID"), (Object)appGroupId, 0);
                final Criteria nonProdReleaseLabelCriteria = new Criteria(new Column("AppGroupToCollection", "RELEASE_LABEL_ID"), (Object)appGroupToNonProdLabel.get(appGroupId).toArray(), 8);
                DataAccess.delete("AppGroupToCollection", appGroupCriteria.and(nonProdReleaseLabelCriteria));
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Cannot set assign prod label for unapproved app {0}", e);
        }
    }
    
    public void unApproveAllPortalApps(final Long customerId) throws Exception {
        this.handleUnApprovedApps(AppsUtil.getInstance().getPortalApprovedApps(customerId, 2, null), customerId);
        DataAccess.delete("StoreLayout", (Criteria)null);
        DataAccess.delete("StoreLayoutToBusinessStore", (Criteria)null);
    }
}
