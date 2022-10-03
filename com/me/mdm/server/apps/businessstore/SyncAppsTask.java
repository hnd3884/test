package com.me.mdm.server.apps.businessstore;

import java.util.Hashtable;
import org.json.JSONException;
import java.util.Properties;
import com.me.mdm.server.tracker.mics.MICSStoreConfigurationFeatureController;
import com.me.mdm.server.apps.businessstore.ios.IOSSyncAppsHandler;
import com.me.mdm.server.apps.businessstore.android.AndroidSyncAppsHandler;
import com.me.mdm.server.apps.businessstore.android.AdvAndroidSyncAppsHandler;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.apps.businessstore.windows.WindowsSyncAppsHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;

public class SyncAppsTask implements CommonQueueProcessorInterface, SchedulerExecutionInterface
{
    public static Logger logger;
    
    @Override
    public void processData(final CommonQueueData data) {
        SyncAppsTask.logger.log(Level.INFO, "SyncAppsTask : Started task");
        final JSONObject jsonQueueData = data.getJsonQueueData();
        try {
            final Integer platformType = jsonQueueData.getInt("PlatformType");
            final Long customerID = data.getCustomerId();
            final Long userID = jsonQueueData.getLong("userID");
            Long businessStoreID = jsonQueueData.optLong("BUSINESSSTORE_ID");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("CustomerID", (Object)customerID);
            jsonObject.put("userID", (Object)userID);
            boolean isFirstSync = false;
            if (platformType == 3) {
                final String firstSync = CustomerParamsHandler.getInstance().getParameterValue("bstoreFirstSyncPending", (long)customerID);
                if (!MDMStringUtils.isEmpty(firstSync)) {
                    isFirstSync = Boolean.parseBoolean(firstSync);
                    jsonObject.put("isFirstSync", isFirstSync);
                }
                new WindowsSyncAppsHandler(businessStoreID, customerID).syncApps(jsonObject);
            }
            else if (platformType == 2) {
                final int syncType = jsonQueueData.optInt("syncType", 1);
                final int source = jsonQueueData.optInt("source", 3);
                final JSONObject specificParams = new JSONObject();
                specificParams.put("syncType", syncType);
                specificParams.put("source", source);
                jsonObject.put("specificParams", (Object)specificParams);
                final String firstSync2 = MDBusinessStoreUtil.getBusinessStoreParamValue("afwFirstSyncPending", businessStoreID);
                if (!MDMStringUtils.isEmpty(firstSync2) && Boolean.parseBoolean(firstSync2)) {
                    isFirstSync = true;
                }
                businessStoreID = MDBusinessStoreUtil.getBusinessStoreID(customerID, BusinessStoreSyncConstants.BS_SERVICE_AFW);
                jsonObject.put("BUSINESSSTORE_ID", (Object)businessStoreID);
                if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableAndroidDeviceAppPolicy")) {
                    new AdvAndroidSyncAppsHandler(businessStoreID, customerID).syncApps(jsonObject);
                }
                else {
                    new AndroidSyncAppsHandler(businessStoreID, customerID).syncApps(jsonObject);
                }
            }
            else if (platformType == 1) {
                final Boolean toSetNewClientContext = jsonQueueData.optBoolean("toSetNewClientContext", false);
                jsonObject.put("toSetNewClientContext", (Object)toSetNewClientContext);
                jsonObject.put("BUSINESSSTORE_ID", (Object)businessStoreID);
                jsonObject.put("PlatformType", 1);
                new IOSSyncAppsHandler(businessStoreID, customerID).syncApps(jsonObject);
            }
            if (isFirstSync && platformType != 1) {
                MICSStoreConfigurationFeatureController.addTrackingData(platformType, MICSStoreConfigurationFeatureController.StoreConfigurationOperation.COMPLETE);
            }
        }
        catch (final Exception e) {
            SyncAppsTask.logger.log(Level.WARNING, "error while Syncing apps", e);
        }
    }
    
    public void executeTask(final Properties props) {
        final CommonQueueData tempData = new CommonQueueData();
        try {
            tempData.setCustomerId(((Hashtable<K, Long>)props).get("customerID"));
            tempData.setJsonQueueData(new JSONObject((String)((Hashtable<K, String>)props).get("jsonParams")));
            tempData.setTaskName(((Hashtable<K, String>)props).get("taskName"));
            this.processData(tempData);
        }
        catch (final JSONException exp) {
            SyncAppsTask.logger.log(Level.SEVERE, "Cannot form JSON from the props file ", (Throwable)exp);
        }
    }
    
    static {
        SyncAppsTask.logger = Logger.getLogger("MDMConfigLogger");
    }
}
