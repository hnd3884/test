package com.me.mdm.onpremise.api.fs;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AuthenticationAPI extends ApiRequestHandler
{
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseObject = new JSONObject();
            responseObject.put("status", 200);
            final UserAuthenticationFacade userAuth = new UserAuthenticationFacade();
            responseObject.put("RESPONSE", (Object)userAuth.authenticate(apiRequest));
            return responseObject;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
