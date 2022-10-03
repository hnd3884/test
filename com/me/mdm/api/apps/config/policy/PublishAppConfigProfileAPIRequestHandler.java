package com.me.mdm.api.apps.config.policy;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.config.AppConfigFacade;
import com.me.mdm.api.ApiRequestHandler;

public class PublishAppConfigProfileAPIRequestHandler extends ApiRequestHandler
{
    AppConfigFacade appConfigFacade;
    
    public PublishAppConfigProfileAPIRequestHandler() {
        this.appConfigFacade = AppConfigFacade.getInstance();
    }
    
    @Override
    public JSONObject doPost(final APIRequest apiRequest) {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            apiRequest.urlStartKey = "profiles";
            this.appConfigFacade.publishProfile(apiRequest.toJSONObject());
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in publishing app configuration profile", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
