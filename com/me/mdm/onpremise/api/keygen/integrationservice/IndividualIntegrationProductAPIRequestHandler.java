package com.me.mdm.onpremise.api.keygen.integrationservice;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualIntegrationProductAPIRequestHandler extends ApiRequestHandler
{
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)new IntegrationServiceFacade().createServiceForProduct(requestJSON));
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in IntegrationServicesAPIRequestHandler", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
