package com.me.mdm.onpremise.api.fs;

import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.onpremise.server.admin.SecureGatewayServerAPI;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class SyncCeritificateAPI extends ApiRequestHandler
{
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final SecureGatewayServerAPI syncCert = new SecureGatewayServerAPI();
            responseJSON.put("status", 200);
            final JSONObject response = new JSONObject();
            final JSONObject SyncData = new JSONObject();
            SyncData.put("syncdata", (Object)syncCert.syncData((com.me.devicemanagement.framework.webclient.api.util.APIRequest)null));
            response.put("message_response", (Object)SyncData);
            response.put("message_type", (Object)"syncdata");
            response.put("status", (Object)"success");
            response.put("message_version", (Object)"1.0");
            responseJSON.put("RESPONSE", (Object)response);
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
