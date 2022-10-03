package com.me.mdm.agent.handlers.android.admin;

import com.adventnet.sym.server.mdm.android.payload.AndroidCommandPayload;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayloadHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import org.json.JSONObject;
import com.me.mdm.agent.handlers.DeviceRequest;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;

public class AdminAgentCommandRequestHandler extends BaseProcessDeviceRequestHandler
{
    @Override
    public String processRequest(final DeviceRequest request) throws Exception {
        final JSONObject jsonObject = (JSONObject)request.deviceRequestData;
        final String requestStatus = jsonObject.optString("Status", (String)null);
        final String deviceUDID = jsonObject.optString("UDID", (String)null);
        request.initDeviceRequest(deviceUDID);
        request.repositoryType = 3;
        if (requestStatus != null && requestStatus.equals("Idle")) {
            this.initServerRequest(request, 3);
        }
        else {
            this.addResponseToQueue(request, jsonObject.toString(), 105);
        }
        final String responseData = this.getNextDeviceCommandQuery(request);
        return responseData;
    }
    
    @Override
    protected String getNextDeviceCommandQuery(final DeviceCommand deviceCommand, final DeviceRequest request) {
        final String command = deviceCommand.commandType;
        final String commandUUID = deviceCommand.commandUUID;
        final String deviceUDID = request.deviceUDID;
        final Long resourceID = request.resourceID;
        final Long customerID = request.customerID;
        this.logger.log(Level.INFO, "getNextDeviceCommandQuery command: {0} commandUUID: {1} UDID: {2} resourceID: {3}", new Object[] { command, commandUUID, deviceUDID, resourceID });
        String strQuery = null;
        try {
            if (command.equalsIgnoreCase("AgentUpgrade")) {
                final AndroidPayloadHandler handler = AndroidPayloadHandler.getInstance();
                final AndroidCommandPayload createAgentUpgradeCommand = handler.createAgentUpgradeCommand(request, 6);
                createAgentUpgradeCommand.setCommandUUID(commandUUID, false);
                strQuery = createAgentUpgradeCommand.toString();
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while creating android command", e);
        }
        return strQuery;
    }
}
