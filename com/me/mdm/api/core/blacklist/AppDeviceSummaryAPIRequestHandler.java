package com.me.mdm.api.core.blacklist;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.server.apps.blacklist.BlacklistPolicyFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AppDeviceSummaryAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final BlacklistPolicyFacade blacklistPolicyFacade = new BlacklistPolicyFacade();
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)blacklistPolicyFacade.getInvDeviceAppDetails(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "exception occurred in getInvDeviceAppDetails", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
