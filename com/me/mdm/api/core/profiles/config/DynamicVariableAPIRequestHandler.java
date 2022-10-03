package com.me.mdm.api.core.profiles.config;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.command.DynamicVariableHandler;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DynamicVariableAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)new DynamicVariableHandler().getDynamicVariable());
            return responseDetails;
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
