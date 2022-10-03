package com.adventnet.sym.server.mdm.chrome.payload;

import com.me.mdm.server.inv.actions.BaseCommandExpiryTimeHandler;
import com.me.mdm.server.inv.actions.CommandExpiryHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import org.json.JSONException;
import java.util.logging.Logger;

public class ChromeSecurityCommandPayloadHandler
{
    public Logger mdmLogger;
    private static ChromeSecurityCommandPayloadHandler chromeSecurityCommandPayloadHandler;
    
    public ChromeSecurityCommandPayloadHandler() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    public static ChromeSecurityCommandPayloadHandler getInstance() {
        if (ChromeSecurityCommandPayloadHandler.chromeSecurityCommandPayloadHandler == null) {
            ChromeSecurityCommandPayloadHandler.chromeSecurityCommandPayloadHandler = new ChromeSecurityCommandPayloadHandler();
        }
        return ChromeSecurityCommandPayloadHandler.chromeSecurityCommandPayloadHandler;
    }
    
    public ChromeCommandPayload createCommandPayload(final String requestType) throws JSONException {
        final ChromeCommandPayload commandPayload = new ChromeCommandPayload();
        commandPayload.setRequestType(requestType);
        commandPayload.setCommandUUID(requestType);
        return commandPayload;
    }
    
    public ChromeCommandPayload createEnableLostModeCommand(final Long resourceID) throws Exception {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("EnableLostMode");
        final JSONObject enableLostModeData = new LostModeDataHandler().getChromeEnableLostModePayloadData(resourceID);
        commandPayload.setRequestData(enableLostModeData);
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "EnableLostMode", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createDisableLostModeCommand(final Long resourceID) throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("DisableLostMode");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "DisableLostMode", commandPayload.toString() });
        return commandPayload;
    }
    
    public ChromeCommandPayload createRestartDeviceCommand(final Long resourceID) throws JSONException {
        final ChromeCommandPayload commandPayload = this.createCommandPayload("RestartDevice");
        final JSONObject restartInfo = this.getRestartInfo(resourceID);
        commandPayload.setRequestData(restartInfo);
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "RestartDevice", commandPayload.toString() });
        return commandPayload;
    }
    
    private JSONObject getRestartInfo(final Long resourceID) throws JSONException {
        final JSONObject restartInfo = new JSONObject();
        final BaseCommandExpiryTimeHandler handler = CommandExpiryHandler.getHandler(4);
        final int expiryTime = handler.getExpiryTime();
        restartInfo.put("CommandExpiryTime", expiryTime);
        return restartInfo;
    }
    
    static {
        ChromeSecurityCommandPayloadHandler.chromeSecurityCommandPayloadHandler = null;
    }
}
