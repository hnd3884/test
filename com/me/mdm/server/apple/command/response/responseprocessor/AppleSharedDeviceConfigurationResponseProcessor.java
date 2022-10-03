package com.me.mdm.server.apple.command.response.responseprocessor;

import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;
import com.me.mdm.server.command.CommandResponseProcessor;

public class AppleSharedDeviceConfigurationResponseProcessor implements CommandResponseProcessor.QueuedResponseProcessor
{
    public Logger mdmLogger;
    
    public AppleSharedDeviceConfigurationResponseProcessor() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public JSONObject processQueuedCommand(final JSONObject params) {
        final String strStatus = params.optString("strStatus");
        final String strData = params.optString("strData");
        final Long resourceID = params.optLong("resourceId");
        this.mdmLogger.log(Level.INFO, "Inside AppleSharedDeviceConfigurationResponseProcessor:{0}", params);
        return new JSONObject();
    }
}
