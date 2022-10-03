package com.me.mdm.api.core.apps.config;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppToUsersAPIRequestHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public AppToUsersAPIRequestHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject temp = new JSONObject();
            temp.put("status", 200);
            temp.put("RESPONSE", this.appFacade.getUsersForApp(apiRequest.toJSONObject()));
            return temp;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in GET AppToUsersAPIRequestHandler", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject temp = new JSONObject();
            temp.put("status", 202);
            this.appFacade.associateAppsToManagedUsers(apiRequest.toJSONObject());
            return temp;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in POST AppToUsersAPIRequestHandler", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject temp = new JSONObject();
            temp.put("status", 202);
            this.appFacade.disassociateAppsToManagedUsers(apiRequest.toJSONObject());
            return temp;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in DELETE AppToUsersAPIRequestHandler", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
