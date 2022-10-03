package com.me.mdm.api.apps.config.policy;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.config.AppConfigFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppConfigIndividualProfile extends ApiRequestHandler
{
    AppConfigFacade appConfigFacade;
    
    public AppConfigIndividualProfile() {
        this.appConfigFacade = AppConfigFacade.getInstance();
    }
    
    @Override
    public JSONObject doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            apiRequest.urlStartKey = "profiles";
            response.put("RESPONSE", (Object)this.appConfigFacade.getProfile(apiRequest.toJSONObject()));
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception getting app config profile details", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public JSONObject doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            apiRequest.urlStartKey = "profiles";
            this.appConfigFacade.modifyProfile(apiRequest.toJSONObject());
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception updating app config profile details", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public JSONObject doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            apiRequest.urlStartKey = "profiles";
            final JSONObject message = apiRequest.toJSONObject();
            message.put("permanent_delete", true);
            this.appConfigFacade.deleteOrTrashProfile(message);
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception deleting app config profile details", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
