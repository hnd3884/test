package com.me.mdm.api.core.blacklist;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.server.apps.blacklist.BlacklistPolicyFacade;
import com.me.mdm.api.ApiRequestHandler;

public class BlacklistAppDeviceRemoveAPIRequestHandler extends ApiRequestHandler
{
    BlacklistPolicyFacade blacklistPolicyFacade;
    private Logger logger;
    
    public BlacklistAppDeviceRemoveAPIRequestHandler() {
        this.blacklistPolicyFacade = new BlacklistPolicyFacade();
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.blacklistPolicyFacade.getDeviceTreeDataForAppGroups(apiRequest.toJSONObject(), 2));
            return responseDetails;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "exception occurred in BlacklistAppDeviceRemoveAPIRequestHandler", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
