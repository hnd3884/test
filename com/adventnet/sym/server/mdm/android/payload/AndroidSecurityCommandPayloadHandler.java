package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONObject;
import com.me.mdm.server.inv.actions.ClearAppDataHandler;
import org.json.JSONException;
import java.util.logging.Logger;

public class AndroidSecurityCommandPayloadHandler
{
    public Logger mdmLogger;
    private static AndroidSecurityCommandPayloadHandler androidSecurityCommandPayloadHandler;
    
    public AndroidSecurityCommandPayloadHandler() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    public static AndroidSecurityCommandPayloadHandler getInstance() {
        if (AndroidSecurityCommandPayloadHandler.androidSecurityCommandPayloadHandler == null) {
            AndroidSecurityCommandPayloadHandler.androidSecurityCommandPayloadHandler = new AndroidSecurityCommandPayloadHandler();
        }
        return AndroidSecurityCommandPayloadHandler.androidSecurityCommandPayloadHandler;
    }
    
    public AndroidCommandPayload createCommandPayload(final String requestType) throws JSONException {
        final AndroidCommandPayload commandPayload = new AndroidCommandPayload();
        commandPayload.setRequestType(requestType);
        commandPayload.setCommandUUID(requestType);
        return commandPayload;
    }
    
    public AndroidCommandPayload createClearAppDataCommandPayload(final Long resourceId) throws Exception {
        final AndroidCommandPayload commandPayload = this.createCommandPayload("ClearAppData");
        final JSONObject clearAppDataInfo = new ClearAppDataHandler().getClearAppCommandRequestData(resourceId);
        commandPayload.setRequestData(clearAppDataInfo);
        return commandPayload;
    }
    
    static {
        AndroidSecurityCommandPayloadHandler.androidSecurityCommandPayloadHandler = null;
    }
}
