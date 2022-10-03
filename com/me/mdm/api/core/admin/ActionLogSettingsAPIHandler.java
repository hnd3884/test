package com.me.mdm.api.core.admin;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.audit.AuditDataHandler;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class ActionLogSettingsAPIHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)new AuditDataHandler().getAuditSettings());
            return responseDetails;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)new AuditDataHandler().addOrUpdateAuditSettings(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
    }
}
