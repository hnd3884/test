package com.me.mdm.server.apps.businessstore;

import com.me.mdm.server.apps.businessstore.android.AdvAndroidSyncAppsHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;

public class SpecificAppSyncTask implements CommonQueueProcessorInterface
{
    public static Logger logger;
    
    @Override
    public void processData(final CommonQueueData data) {
        SpecificAppSyncTask.logger.log(Level.INFO, "SyncAppsTask : Started task");
        final JSONObject jsonQueueData = data.getJsonQueueData();
        try {
            final Integer platformType = jsonQueueData.getInt("PlatformType");
            final Long customerID = data.getCustomerId();
            final Long userID = jsonQueueData.getLong("userID");
            final Long businessStoreID = jsonQueueData.optLong("BUSINESSSTORE_ID");
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("CustomerID", (Object)customerID);
            jsonObject.put("userID", (Object)userID);
            if (platformType == 2) {
                final int syncType = jsonQueueData.optInt("syncType", 1);
                final int source = jsonQueueData.optInt("source", 1);
                final JSONObject specificParams = new JSONObject();
                specificParams.put("syncType", syncType);
                specificParams.put("source", source);
                jsonObject.put("specificParams", (Object)specificParams);
                if (jsonQueueData.optBoolean("syncSelectedApps")) {
                    jsonObject.put("identifiers", (Object)jsonQueueData.getJSONArray("identifiers"));
                    jsonObject.put("newIdentifiers", (Object)jsonQueueData.optJSONArray("newIdentifiers"));
                    jsonObject.put("syncSelectedApps", true);
                    jsonObject.put("newApps", jsonQueueData.optInt("newApps"));
                }
                new AdvAndroidSyncAppsHandler(businessStoreID, customerID).syncSpecificApps(jsonObject);
            }
        }
        catch (final Exception e) {
            SpecificAppSyncTask.logger.log(Level.WARNING, "Exception while syncing the apps", e);
        }
    }
    
    static {
        SpecificAppSyncTask.logger = Logger.getLogger("MDMConfigLogger");
    }
}
