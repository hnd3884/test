package com.me.mdm.agent.handlers.android.admin;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.enrollment.MDMAgentUpdateHandler;
import com.me.mdm.core.enrollment.AdminDeviceHandler;
import org.json.JSONObject;
import org.json.JSONException;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.mdm.agent.handlers.BaseAppCommandQueueProcessor;

public class AdminAgentCommandResponseQueueProcessor extends BaseAppCommandQueueProcessor
{
    @Override
    protected void processCommand() throws JSONException {
        Boolean shouldDeleteCommand = Boolean.FALSE;
        if (this.commandResponse.responseType.equalsIgnoreCase("AgentUpgrade")) {
            shouldDeleteCommand = this.processAgentUpgradeAck();
        }
        if (shouldDeleteCommand) {
            DeviceCommandRepository.getInstance().deleteResourceCommand(this.commandResponse.commandUUID, this.commandResponse.udid);
        }
        else {
            this.updateResourceCommandStatus(12);
        }
    }
    
    private Boolean processAgentUpgradeAck() {
        if (!this.commandResponse.status.equalsIgnoreCase("Error")) {
            try {
                final JSONObject adminDeviceJSON = new JSONObject();
                adminDeviceJSON.put("UDID", (Object)this.commandResponse.udid);
                adminDeviceJSON.put("LOGIN_ID", (Object)new AdminDeviceHandler().getLoggedInUserId(this.commandResponse.udid));
                adminDeviceJSON.put("NOTIFIED_AGENT_VERSION", (Object)MDMAgentUpdateHandler.getInstance().getAgentNotifiedVersion(3));
                new AdminDeviceHandler().addOrUpdateAdminDevice(adminDeviceJSON);
                return true;
            }
            catch (final JSONException ex) {
                Logger.getLogger(AdminAgentCommandResponseQueueProcessor.class.getName()).log(Level.SEVERE, null, (Throwable)ex);
            }
            catch (final Exception ex2) {
                Logger.getLogger(AdminAgentCommandResponseQueueProcessor.class.getName()).log(Level.SEVERE, null, ex2);
            }
        }
        return false;
    }
    
    private void updateResourceCommandStatus(final int commandStatusToUpdate) {
        final Long commandID = DeviceCommandRepository.getInstance().getCommandID(this.commandResponse.commandUUID);
        DeviceCommandRepository.getInstance().updateResourceCommandStatus(commandID, this.commandResponse.udid, 2, commandStatusToUpdate);
    }
}
