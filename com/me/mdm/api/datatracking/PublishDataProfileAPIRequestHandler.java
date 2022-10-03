package com.me.mdm.api.datatracking;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.datausage.DataUsagePolicyFacade;
import com.me.mdm.api.ApiRequestHandler;

public class PublishDataProfileAPIRequestHandler extends ApiRequestHandler
{
    DataUsagePolicyFacade profile;
    
    public PublishDataProfileAPIRequestHandler() {
        this.profile = new DataUsagePolicyFacade();
    }
    
    @Override
    public JSONObject doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            apiRequest.urlStartKey = "profiles";
            this.profile.publishProfile(apiRequest.toJSONObject());
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
}
