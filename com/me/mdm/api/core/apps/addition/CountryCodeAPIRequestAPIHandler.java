package com.me.mdm.api.core.apps.addition;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class CountryCodeAPIRequestAPIHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public CountryCodeAPIRequestAPIHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("RESPONSE", this.appFacade.getCountry(apiRequest.toJSONObject()));
            responseDetails.put("status", 200);
            return responseDetails;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
