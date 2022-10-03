package com.me.mdm.api.core.blacklist;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.blacklist.BlacklistPolicyFacade;
import com.me.mdm.api.ApiRequestHandler;

public class BlacklistDeviceAPIRequestHandler extends ApiRequestHandler
{
    BlacklistPolicyFacade blacklistPolicyFacade;
    
    public BlacklistDeviceAPIRequestHandler() {
        this.blacklistPolicyFacade = new BlacklistPolicyFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.blacklistPolicyFacade.performBlacklistAction(apiRequest.toJSONObject(), 2, 1));
            return responseDetails;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception in device blacklist", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.blacklistPolicyFacade.performBlacklistAction(apiRequest.toJSONObject(), 2, 2));
            return responseDetails;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception in Deletedevice blacklist", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final BlacklistPolicyFacade blacklistPolicyFacade = new BlacklistPolicyFacade();
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)blacklistPolicyFacade.getBlacklistedAppsOnDevice(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "exception occurred in getInvDeviceAppDetails", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
