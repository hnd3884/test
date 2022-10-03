package com.me.mdm.agent.handlers.android.admin;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.core.enrollment.AdminDeviceHandler;
import org.json.JSONObject;
import com.me.mdm.agent.handlers.BaseAppMessageQueueProcessor;

public class AdminAgentMessageResponseQueueProcessor extends BaseAppMessageQueueProcessor
{
    @Override
    protected void processMessage() throws Exception {
        final String messageType = this.messageRequest.messageType;
        final String udid = this.messageRequest.udid;
        if (messageType.equalsIgnoreCase("AgentUpgrade")) {
            this.processAgentUpgradeAck();
        }
    }
    
    private void processAgentUpgradeAck() {
        try {
            final JSONObject adminDeviceJSON = new JSONObject();
            final JSONObject msgResponseJSON = this.messageRequest.messageRequest;
            adminDeviceJSON.put("UDID", (Object)this.messageRequest.udid);
            adminDeviceJSON.put("LOGIN_ID", (Object)new AdminDeviceHandler().getLoggedInUserId(this.messageRequest.udid));
            adminDeviceJSON.put("AGENT_VERSION", (Object)String.valueOf(msgResponseJSON.get("AgentVersion")));
            adminDeviceJSON.put("AGENT_VERSION_CODE", msgResponseJSON.getInt("AgentVersionCode"));
            new AdminDeviceHandler().addOrUpdateAdminDevice(adminDeviceJSON);
        }
        catch (final Exception ex) {
            Logger.getLogger(AdminAgentCommandResponseQueueProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
