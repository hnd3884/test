package com.me.mdm.api.core.misc;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.factory.MDMOnPremisesAPIFactoryProvider;
import com.me.mdm.server.util.MDMServerDetailsUtil;
import com.me.mdm.api.ApiRequestHandler;

public class ServerDetailsAPIRequestHandler extends ApiRequestHandler
{
    MDMServerDetailsUtil mdmServerDetailsUtil;
    
    public ServerDetailsAPIRequestHandler() {
        this.mdmServerDetailsUtil = MDMOnPremisesAPIFactoryProvider.getMdmServerDetailsUtil();
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.mdmServerDetailsUtil.getServerDetails());
            return response;
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
