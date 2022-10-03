package com.me.mdm.api.core.profiles.config;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.CustomProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualCustomProfileAPIRequestHandler extends ApiRequestHandler
{
    CustomProfileFacade facade;
    
    public IndividualCustomProfileAPIRequestHandler() {
        this.facade = new CustomProfileFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            apiRequest.urlStartKey = "customprofiles";
            responseDetails.put("RESPONSE", (Object)this.facade.getCustomProfileDetails(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", (Object[])null);
        }
    }
}
