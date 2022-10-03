package com.me.mdm.server.updates.osupdates.task;

import java.util.Hashtable;
import org.json.JSONException;
import java.util.Properties;
import java.util.Collection;
import org.json.JSONArray;
import java.util.List;
import com.me.mdm.server.updates.osupdates.OSUpdatePolicyHandler;
import java.util.ArrayList;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;

public class OSUpdatePublishTask implements CommonQueueProcessorInterface, SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    
    @Override
    public void processData(final CommonQueueData data) {
        OSUpdatePublishTask.LOGGER.log(Level.INFO, "OSUpdate Publish Task Started");
        try {
            final JSONObject queueData = data.getJsonQueueData();
            final Long profileId = queueData.getLong("PROFILE_ID");
            final Long userId = queueData.getLong("USER_ID");
            final Long customerId = data.getCustomerId();
            final String loggedOnUserName = (String)queueData.get("LOGGEDONUSERNAME");
            final JSONObject msgHeaderJSON = new JSONObject();
            msgHeaderJSON.put("loggedOnUserName", (Object)loggedOnUserName);
            msgHeaderJSON.put("CUSTOMER_ID", (Object)customerId);
            msgHeaderJSON.put("USER_ID", (Object)userId);
            final List<Long> profileIds = new ArrayList<Long>();
            profileIds.add(profileId);
            final OSUpdatePolicyHandler osUpdatePolicyHandler = new OSUpdatePolicyHandler();
            final List groupIds = osUpdatePolicyHandler.getManagedGroupsAssignedForProfiles(profileIds);
            final List resourceIds = osUpdatePolicyHandler.getManagedDevicesAssignedForProfiles(profileIds);
            if (!groupIds.isEmpty() || !resourceIds.isEmpty()) {
                final JSONObject distributeOSPolicyJSON = new JSONObject();
                distributeOSPolicyJSON.put("PROFILE_ID", (Object)profileId);
                distributeOSPolicyJSON.put("DEVICE_IDS", (Object)new JSONArray((Collection)resourceIds));
                distributeOSPolicyJSON.put("GROUP_IDS", (Object)new JSONArray((Collection)groupIds));
                OSUpdatePublishTask.LOGGER.log(Level.INFO, "Going to distribute from task MsgHeader: {0} - DistributePolicyJSON {1}", new Object[] { msgHeaderJSON, distributeOSPolicyJSON });
                osUpdatePolicyHandler.distributeOSUpdatePolicy(msgHeaderJSON, distributeOSPolicyJSON);
            }
            OSUpdatePublishTask.LOGGER.log(Level.INFO, "Distribute the policy from publish task completed");
        }
        catch (final Exception ex) {
            OSUpdatePublishTask.LOGGER.log(Level.SEVERE, "Exception in OSUpdate publish task", ex);
        }
    }
    
    public void executeTask(final Properties props) {
        try {
            final CommonQueueData tempData = new CommonQueueData();
            tempData.setCustomerId(((Hashtable<K, Long>)props).get("customerId"));
            tempData.setJsonQueueData(new JSONObject((String)((Hashtable<K, String>)props).get("jsonParams")));
            tempData.setTaskName(((Hashtable<K, String>)props).get("taskName"));
            this.processData(tempData);
        }
        catch (final JSONException exp) {
            OSUpdatePublishTask.LOGGER.log(Level.SEVERE, "Cannot form JSON from props file", (Throwable)exp);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
