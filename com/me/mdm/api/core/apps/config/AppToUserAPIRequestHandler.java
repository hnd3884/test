package com.me.mdm.api.core.apps.config;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppToUserAPIRequestHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public AppToUserAPIRequestHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            this.appFacade.associateAppsToManagedUsers(apiRequest.toJSONObject());
            responseDetails.put("status", 202);
            return responseDetails;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in POST AppToUserAPIRequestHandler", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            this.appFacade.disassociateAppsToManagedUsers(apiRequest.toJSONObject());
            responseDetails.put("status", 202);
            return responseDetails;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in DELETE AppToUserAPIRequestHandler", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
