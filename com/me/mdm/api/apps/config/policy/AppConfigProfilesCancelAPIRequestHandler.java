package com.me.mdm.api.apps.config.policy;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.config.AppConfigFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppConfigProfilesCancelAPIRequestHandler extends ApiRequestHandler
{
    AppConfigFacade appConfigFacade;
    
    public AppConfigProfilesCancelAPIRequestHandler() {
        this.appConfigFacade = AppConfigFacade.getInstance();
    }
    
    @Override
    public JSONObject doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            apiRequest.urlStartKey = "profiles";
            response.put("status", 204);
            this.appConfigFacade.cancelProfileCreation(apiRequest.toJSONObject());
            return response;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in cancelling app config profile", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
