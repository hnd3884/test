package com.me.mdm.server.apps.businessstore.service;

import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueues;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueUtil;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.List;
import java.util.logging.Level;
import com.me.mdm.server.apps.businessstore.MDBusinessStoreUtil;
import java.util.ArrayList;
import java.util.Collection;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.server.apps.api.model.AppListModel;
import java.util.logging.Logger;

public class AndroidBusinessStoreService extends BusinessStoreService
{
    public Logger logger;
    
    public AndroidBusinessStoreService() {
        this.logger = Logger.getLogger("MDMBStoreLogger");
    }
    
    public void addOrUpdateBusinessStoreApp(final Long userId, final Long businessStoreID, final Long customerID, final AppListModel appList) {
        try {
            final List identifiers = appList.getIdentifierList();
            final JSONObject params = new JSONObject();
            params.put("userID", (Object)userId);
            params.put("identifiers", (Object)JSONUtil.getInstance().convertListToJSONArray(identifiers));
            params.put("BUSINESSSTORE_ID", (Object)businessStoreID);
            final List<String> existingAppList = AppsUtil.getInstance().getPortalApprovedAppIdentifiers(customerID, 2, businessStoreID);
            existingAppList.retainAll(identifiers);
            final List<String> newIdentifiers = new ArrayList<String>(identifiers);
            newIdentifiers.removeAll(existingAppList);
            final int newAppCount = Math.abs(existingAppList.size() - identifiers.size());
            params.put("newApps", newAppCount);
            params.put("newIdentifiers", (Collection)newIdentifiers);
            MDBusinessStoreUtil.addOrUpdateBusinessStoreSyncStatus(businessStoreID, -1, null, null, 2);
            this.postDataToQueue(customerID, userId, params);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Cannot add apps due to {0}", e);
        }
    }
    
    private void postDataToQueue(final Long customerID, final Long userId, final JSONObject params) throws Exception {
        final JSONObject queueData = new JSONObject();
        queueData.put("PlatformType", 2);
        queueData.put("CustomerID", (Object)customerID);
        queueData.put("syncSelectedApps", true);
        queueData.put("userID", (Object)userId);
        queueData.put("BUSINESSSTORE_ID", params.getLong("BUSINESSSTORE_ID"));
        queueData.put("identifiers", (Object)params.optJSONArray("identifiers"));
        queueData.put("newApps", params.optInt("newApps"));
        queueData.put("newIdentifiers", (Object)params.optJSONArray("newIdentifiers"));
        final CommonQueueData syncAppsData = new CommonQueueData();
        syncAppsData.setCustomerId(customerID);
        syncAppsData.setTaskName("SpecificAppSyncTask");
        syncAppsData.setClassName("com.me.mdm.server.apps.businessstore.SpecificAppSyncTask");
        syncAppsData.setJsonQueueData(queueData);
        this.logger.log(Level.INFO, "Starting Task to Sync ANdroid Business Store Apps for customer : " + customerID);
        CommonQueueUtil.getInstance().addToQueue(syncAppsData, CommonQueues.MDM_APP_MGMT);
    }
}
