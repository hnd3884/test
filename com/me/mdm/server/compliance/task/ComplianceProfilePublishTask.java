package com.me.mdm.server.compliance.task;

import java.util.Hashtable;
import org.json.JSONArray;
import com.me.mdm.server.compliance.ComplianceDistributionHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ComplianceProfilePublishTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public ComplianceProfilePublishTask() {
        this.logger = Logger.getLogger("MDMDeviceComplianceLogger");
    }
    
    public void executeTask(final Properties properties) {
        this.logger.log(Level.INFO, "Compliance profile publish task started");
        try {
            final Long profileId = ((Hashtable<K, Long>)properties).get("compliance_id");
            final Long userId = ((Hashtable<K, Long>)properties).get("user_id");
            final Long customerId = ((Hashtable<K, Long>)properties).get("customer_id");
            final String complianceState = ((Hashtable<K, String>)properties).get("compliance_state");
            final Long collectionId = ((Hashtable<K, Long>)properties).get("collection_id");
            final JSONObject requestJSON = new JSONObject();
            requestJSON.put("compliance_id", (Object)profileId);
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_id", (Object)userId);
            requestJSON.put("compliance_state", (Object)complianceState);
            requestJSON.put("collection_id", (Object)collectionId);
            final JSONObject groupJSON = ComplianceDistributionHandler.getInstance().getManagedGroupsForComplianceProfile(requestJSON);
            final JSONObject deviceJSON = ComplianceDistributionHandler.getInstance().getManagedDevicesForComplianceProfile(requestJSON);
            final JSONObject userJSON = ComplianceDistributionHandler.getInstance().getManagedUsersForComplianceProfile(requestJSON);
            final JSONArray groupList = groupJSON.getJSONArray("group_list");
            final JSONArray deviceList = deviceJSON.getJSONArray("resource_list");
            final JSONArray userList = userJSON.getJSONArray("user_list");
            if (groupList.length() != 0 || deviceList.length() != 0 || userList.length() != 0) {
                requestJSON.put("group_list", (Object)groupList);
                requestJSON.put("resource_list", (Object)deviceList);
                requestJSON.put("user_list", (Object)userList);
                this.logger.log(Level.INFO, "Beginning to distribute compliance profile to {0}     {1} {2}", new Object[] { groupJSON.toString(), deviceJSON.toString(), userJSON.toString() });
                ComplianceDistributionHandler.getInstance().removePendingCommands(requestJSON);
                ComplianceDistributionHandler.getInstance().distributeComplianceProfile(requestJSON);
            }
            this.logger.log(Level.INFO, "Successfully distributed compliance profile");
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, " -- executeTask() >   ComplianceProfilePublishTask    >   Error   ", e);
        }
    }
}
