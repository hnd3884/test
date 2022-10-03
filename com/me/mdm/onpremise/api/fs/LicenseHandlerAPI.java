package com.me.mdm.onpremise.api.fs;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class LicenseHandlerAPI extends ApiRequestHandler
{
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject IsValid = new JSONObject();
            IsValid.put("isValid", true);
            final JSONObject response = new JSONObject();
            final JSONObject License = new JSONObject();
            License.put("license", (Object)IsValid);
            response.put("message_type", (Object)"license");
            response.put("message_response", (Object)License);
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
