package com.me.mdm.onpremise.api.fs;

import com.me.mdm.api.error.APIHTTPException;
import com.me.devicemanagement.onpremise.server.admin.SecureGatewayServerAPI;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class CertificateHandlerAPI extends ApiRequestHandler
{
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final SecureGatewayServerAPI getCert = new SecureGatewayServerAPI();
            final JSONObject response = new JSONObject();
            final JSONObject Certificate = new JSONObject();
            Certificate.put("certificate", (Object)getCert.getCertificates((com.me.devicemanagement.framework.webclient.api.util.APIRequest)null));
            response.put("message_response", (Object)Certificate);
            response.put("message_type", (Object)"certificate");
            response.put("message_version", (Object)"1.0");
            response.put("status", (Object)"success");
            responseJSON.put("status", 200);
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
