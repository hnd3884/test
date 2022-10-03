package com.me.mdm.server.apps.android.afw.layoutmgmt;

import org.json.JSONException;
import java.util.List;
import com.google.api.services.androidenterprise.model.StoreCluster;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.ArrayList;
import com.me.mdm.server.apps.android.afw.GooglePlayEnterpriseBusinessStore;
import org.json.JSONArray;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;

public class StoreLayoutMigrationAsyncHandler implements CommonQueueProcessorInterface
{
    protected static Logger logger;
    
    public void migrateLayoutViaQueue(final Long customerId) throws Exception {
        final JSONObject queueData = new JSONObject();
        queueData.put("CustomerID", (Object)customerId);
        final CommonQueueData layoutMigrationData = new CommonQueueData();
        layoutMigrationData.setCustomerId(customerId);
        layoutMigrationData.setTaskName("layoutMigration" + customerId);
        layoutMigrationData.setClassName("com.me.mdm.server.apps.android.afw.layoutmgmt.StoreLayoutMigrationAsyncHandler");
        layoutMigrationData.setJsonQueueData(queueData);
        StoreLayoutMigrationAsyncHandler.logger.log(Level.INFO, "Adding to queue layout migration for customer : {0}", customerId);
        CommonQueueUtil.getInstance().addToQueue(layoutMigrationData, CommonQueues.MDM_APP_MGMT);
    }
    
    @Override
    public void processData(final CommonQueueData data) {
        try {
            final Long customerID = data.getCustomerId();
            StoreLayoutMigrationAsyncHandler.logger.log(Level.INFO, "StoreLayoutMigrationAsyncHandler Migrate layout for customer {0}", customerID);
            try {
                this.migrateToIFrameCompatibleLayout(customerID);
            }
            catch (final Exception e) {
                StoreLayoutMigrationAsyncHandler.logger.log(Level.SEVERE, "Exception when migrating layout for {0}", customerID);
            }
            CustomerParamsHandler.getInstance().addOrUpdateParameter("storeLayoutMigrationNeeded", Boolean.FALSE.toString(), (long)customerID);
            CustomerParamsHandler.getInstance().addOrUpdateParameter("storeLayoutMigrationNotification", Boolean.TRUE.toString(), (long)customerID);
        }
        catch (final Exception e2) {
            StoreLayoutMigrationAsyncHandler.logger.log(Level.WARNING, "error while Syncing apps", e2);
        }
    }
    
    private void migratePlaystoreLayout(final Long customerId) throws Exception {
        StoreLayoutMigrationAsyncHandler.logger.log(Level.INFO, "Layout migration starts {0}", customerId);
        final JSONObject playstoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        final long bsId = playstoreDetails.getLong("BUSINESSSTORE_ID");
        final StoreLayoutManager storeLayoutManager = new StoreLayoutManager();
        final JSONObject storeLayoutJSON = storeLayoutManager.getStoreLayoutForBusinessStore(bsId, customerId);
        final JSONObject modifiedStoreLayout = new JSONObject();
        modifiedStoreLayout.put("LAST_MODIFIED_TIME", System.currentTimeMillis());
        modifiedStoreLayout.put("LAST_MODIFIED_BY", storeLayoutJSON.get("LAST_MODIFIED_BY"));
        final Long layoutId = storeLayoutJSON.getLong("STORE_LAYOUT_ID");
        modifiedStoreLayout.put("STORE_LAYOUT_ID", (Object)layoutId);
        final JSONObject modifiedHomePage = new JSONObject();
        modifiedHomePage.put("STORE_LAYOUT_ID", (Object)layoutId);
        final JSONArray pages = storeLayoutJSON.getJSONArray("StoreLayoutPage");
        final JSONObject homePage = pages.getJSONObject(0);
        modifiedHomePage.put("STORE_LAYOUT_ID", (Object)layoutId);
        modifiedHomePage.put("STORE_PAGE_ID", homePage.get("STORE_PAGE_ID"));
        modifiedHomePage.put("PAGE_NAME", homePage.get("PAGE_NAME"));
        modifiedHomePage.put("PAGE_ID", homePage.get("PAGE_ID"));
        final JSONArray modifiedHomePageClusters = new JSONArray();
        final JSONArray homePageClusters = homePage.getJSONArray("StoreLayoutClusterApps");
        final int homePageClusterCount = homePageClusters.length();
        for (int i = 0; i < homePageClusterCount; ++i) {
            final JSONObject modifiedHomePageCluster = new JSONObject();
            final JSONObject homePageCluster = homePageClusters.getJSONObject(i);
            modifiedHomePageCluster.put("STORE_CLUSTER_ID", homePageCluster.get("STORE_CLUSTER_ID"));
            modifiedHomePageCluster.put("CLUSTER_NAME", homePageCluster.get("CLUSTER_NAME"));
            modifiedHomePageCluster.put("CLUSTER_ID", homePageCluster.get("CLUSTER_ID"));
            modifiedHomePageCluster.put("CLUSTER_ORDER_NUMBER", homePageCluster.get("CLUSTER_ORDER_NUMBER"));
            modifiedHomePageCluster.put("StoreLayoutClusterApps", homePageCluster.get("StoreLayoutClusterApps"));
            modifiedHomePageClusters.put((Object)modifiedHomePageCluster);
        }
        int migratedClusterOrderNumber = homePageClusterCount + 1;
        for (int pagesCount = pages.length(), j = 1; j < pagesCount; ++j) {
            final JSONObject pageJSON = pages.getJSONObject(j);
            final JSONArray pageClusters = pageJSON.getJSONArray("StoreLayoutClusters");
            for (int k = 0; k < pageClusters.length(); ++k) {
                final JSONObject cluster = pageClusters.getJSONObject(k);
                final JSONObject modifiedCluster = new JSONObject();
                modifiedCluster.put("CLUSTER_NAME", cluster.get("CLUSTER_NAME"));
                modifiedCluster.put("CLUSTER_ID", cluster.get("CLUSTER_ID"));
                modifiedCluster.put("CLUSTER_ORDER_NUMBER", migratedClusterOrderNumber);
                modifiedCluster.put("StoreLayoutClusterApps", cluster.get("StoreLayoutClusterApps"));
                modifiedHomePageClusters.put((Object)modifiedCluster);
                ++migratedClusterOrderNumber;
            }
        }
        modifiedHomePage.put("StoreLayoutClusters", (Object)modifiedHomePageClusters);
        modifiedStoreLayout.put("StoreLayoutPage", (Object)modifiedHomePage);
        final Long storeLayoutId = storeLayoutManager.persistStoreLayout(bsId, storeLayoutJSON);
        storeLayoutManager.updateStatus(storeLayoutId, StoreLayoutManager.STORELAYOUT_STATUS_INPROGRESS, "mdm.afw.layout.migration_in_progress");
        StoreLayoutMigrationAsyncHandler.logger.log(Level.INFO, "Compiled data for layout migration {0}", customerId);
        final CommonQueueData tempData = new CommonQueueData();
        tempData.setCustomerId(customerId);
        tempData.setJsonQueueData(new JSONObject());
        tempData.setTaskName("layoutMigration" + customerId);
        storeLayoutManager.processData(tempData);
        StoreLayoutMigrationAsyncHandler.logger.log(Level.INFO, "Layout migration completed for customer {0}", customerId);
    }
    
    private void migrateToIFrameCompatibleLayout(final Long customerId) throws Exception {
        final JSONObject playStoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        final JSONObject storeLayoutJSON = this.getToBeMigratedLayout(customerId);
        final GooglePlayEnterpriseBusinessStore ebs = new GooglePlayEnterpriseBusinessStore(playStoreDetails);
        final JSONObject layoutJSON = ebs.getStoreLayout();
        final String homepageId = layoutJSON.getString("HomePageId");
        int modifiedClusterOrder = 1;
        if (homepageId != null) {
            final JSONArray pages = storeLayoutJSON.getJSONArray("StoreLayoutPage");
            for (int i = 0; i < pages.length(); ++i) {
                final JSONObject page = pages.getJSONObject(i);
                if (!homepageId.equals(page.get("STORE_PAGE_ID"))) {
                    final JSONArray clusters = page.getJSONArray("StoreLayoutClusters");
                    if (clusters != null) {
                        for (int j = 0; j < clusters.length(); ++j) {
                            final JSONObject cluster = clusters.getJSONObject(j);
                            final String clusterName = cluster.getString("CLUSTER_NAME");
                            final JSONObject clusterJSON = new JSONObject();
                            clusterJSON.put("CLUSTER_NAME", (Object)clusterName);
                            clusterJSON.put("CLUSTER_ORDER_NUMBER", modifiedClusterOrder);
                            final String clusterId = ebs.insertCluster(homepageId, clusterJSON);
                            final StoreCluster storeCluster = ebs.getStoreCluster(clusterId, homepageId);
                            final JSONArray clusterApps = cluster.getJSONArray("StoreLayoutClusterApps");
                            if (clusterApps != null) {
                                final List clusterAppIds = new ArrayList();
                                for (int k = 0; k < clusterApps.length(); ++k) {
                                    clusterAppIds.add("app:" + DBUtil.getValueFromDB("MdAppGroupDetails", "APP_GROUP_ID", (Object)clusterApps.getLong(k), "IDENTIFIER"));
                                }
                                storeCluster.setProductId(clusterAppIds);
                                ebs.updateStoreCluster(storeCluster, homepageId);
                            }
                            ++modifiedClusterOrder;
                        }
                    }
                }
            }
        }
    }
    
    public JSONObject getToBeMigratedLayout(final Long customerId) throws Exception {
        final JSONObject playstoreDetails = GoogleForWorkSettings.getGoogleForWorkSettings(customerId, GoogleForWorkSettings.SERVICE_TYPE_AFW);
        if (playstoreDetails.getBoolean("isConfigured")) {
            StoreLayoutMigrationAsyncHandler.logger.log(Level.INFO, "AFW is configured for {0}. So, checking if migration is needed", customerId);
            final JSONObject storeLayoutJSON = new StoreLayoutManager().getStoreLayoutForBusinessStore(playstoreDetails.getLong("BUSINESSSTORE_ID"), customerId);
            if (this.isLayoutMigrationNeeded(storeLayoutJSON)) {
                return storeLayoutJSON;
            }
        }
        return null;
    }
    
    private boolean isLayoutMigrationNeeded(final JSONObject storeLayoutJSON) throws JSONException {
        final int pagesCount = storeLayoutJSON.getJSONArray("StoreLayoutPage").length();
        return pagesCount > 1;
    }
    
    static {
        StoreLayoutMigrationAsyncHandler.logger = Logger.getLogger("MDMBStoreLogger");
    }
}
