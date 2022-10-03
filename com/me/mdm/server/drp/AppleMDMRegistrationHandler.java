package com.me.mdm.server.drp;

import java.util.Hashtable;
import java.util.Properties;
import org.json.JSONException;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.logging.Level;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONObject;

public class AppleMDMRegistrationHandler extends MDMRegistrationHandler
{
    @Override
    protected void processPostAppRegistration(final JSONObject requestJSON) throws JSONException {
        super.processPostAppRegistration(requestJSON);
        final JSONObject msgRequestJSON = requestJSON.getJSONObject("MsgRequest");
        final Boolean isAgentupgrade = msgRequestJSON.optBoolean("IsAppUpgraded", false);
        final String deviceUDID = String.valueOf(msgRequestJSON.get("UDID"));
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
        final List resourceList = new ArrayList();
        resourceList.add(resourceID);
        if (isAgentupgrade) {
            this.logger.log(Level.INFO, "Adding sync agent command for app update");
            DeviceCommandRepository.getInstance().addSyncAgentSettingsCommand(resourceList, 2);
        }
        else {
            final Long agentVersionCode = msgRequestJSON.optLong("AgentVersionCode", -1L);
            final String agentVersion = msgRequestJSON.optString("AgentVersion", (String)null);
            this.updateAgentVersion(resourceID, agentVersionCode, agentVersion, deviceUDID);
        }
    }
    
    private void updateAgentVersion(final Long resourceId, final Long versionCode, final String agentVersion, final String deviceUDID) {
        try {
            final Properties properties = new Properties();
            ((Hashtable<String, Long>)properties).put("RESOURCE_ID", resourceId);
            ((Hashtable<String, String>)properties).put("UDID", deviceUDID);
            ((Hashtable<String, Long>)properties).put("AGENT_VERSION_CODE", versionCode);
            if (agentVersion != null) {
                ((Hashtable<String, String>)properties).put("AGENT_VERSION", agentVersion);
            }
            this.logger.log(Level.INFO, "Going to update the Managed Device details for resource:{0}", new Object[] { resourceId });
            ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(properties);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in processPostAppRegistration-updateAgentversion", e);
        }
    }
}
