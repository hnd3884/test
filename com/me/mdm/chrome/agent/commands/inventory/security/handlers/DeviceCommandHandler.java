package com.me.mdm.chrome.agent.commands.inventory.security.handlers;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.GoogleChromeAPIWrapper;
import com.me.mdm.chrome.agent.Context;
import com.me.mdm.chrome.agent.core.Response;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.core.Request;
import java.util.logging.Logger;

public class DeviceCommandHandler
{
    public Logger logger;
    private static final String RESTART_DEVICE_COMMAND = "REBOOT";
    private static final String TAKE_SCREENSHOT_COMMAND = "TAKE_A_SCREENSHOT";
    
    public DeviceCommandHandler() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    public String getCommandExpiryTime(final Request request) throws Exception {
        String commandExpiryTime = null;
        final JSONObject requestData = (JSONObject)request.requestData;
        final int expiryTime = requestData.optInt("CommandExpiryTime", 600);
        if (expiryTime > 0) {
            commandExpiryTime = String.valueOf(expiryTime).concat("s");
        }
        return commandExpiryTime;
    }
    
    private String getCommandType(final String requestType) {
        String commandType = null;
        switch (requestType) {
            case "RestartDevice": {
                commandType = "REBOOT";
                break;
            }
            case "TakeScreenshot": {
                commandType = "TAKE_A_SCREENSHOT";
                break;
            }
        }
        return commandType;
    }
    
    public Response issueDeviceCommand(final Request request, final Response response, final Context context) {
        try {
            final String commandExpiryTime = this.getCommandExpiryTime(request);
            final String commandType = this.getCommandType(request.requestType);
            if (commandType != null) {
                GoogleChromeAPIWrapper.issueDeviceCommand(commandType, commandExpiryTime, context);
            }
        }
        catch (final GoogleJsonResponseException ex) {
            if (!ex.getDetails().getMessage().contains("there are commands still pending")) {
                this.logger.log(Level.SEVERE, "Error issuing device command", (Throwable)ex);
                response.setErrorCode(GoogleChromeApiErrorHandler.getErrorCode((Exception)ex));
                response.setErrorMessage(ex.getDetails().getMessage());
            }
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Error issuing device command", ex2);
            response.setErrorCode(12132);
            response.setErrorMessage(ex2.getMessage());
        }
        return response;
    }
}
