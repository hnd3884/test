package com.me.mdm.api.apps.config.policy;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.config.AppConfigFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppConfigProfileAPIRequestHandler extends ApiRequestHandler
{
    AppConfigFacade appConfigFacade;
    
    public AppConfigProfileAPIRequestHandler() {
        this.appConfigFacade = AppConfigFacade.getInstance();
    }
    
    @Override
    public JSONObject doGet(final APIRequest apiRequest) {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.appConfigFacade.getProfiles(apiRequest.toJSONObject()));
            return response;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in getting app configuration profiles", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public JSONObject doPost(final APIRequest apiRequest) {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.appConfigFacade.createProfile(apiRequest.toJSONObject()));
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in creating app configuration profile", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public JSONObject doDelete(final APIRequest apiRequest) {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            final JSONObject message = apiRequest.toJSONObject();
            message.put("permanent_delete", true);
            this.appConfigFacade.deleteOrTrashProfile(message);
            return response;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in deleting app configuration profile", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
