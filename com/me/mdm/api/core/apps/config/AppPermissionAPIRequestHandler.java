package com.me.mdm.api.core.apps.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppPermissionAPIRequestHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public AppPermissionAPIRequestHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject temp = new JSONObject();
            temp.put("status", 200);
            temp.put("RESPONSE", (Object)this.appFacade.getPermissionsAssociatedWithApp(apiRequest.toJSONObject()));
            return temp;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in GET /apps/:id/permission/config", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("RESPONSE", (Object)this.appFacade.modifyAppPermissions(apiRequest.toJSONObject()));
            responseDetails.put("status", 202);
            return responseDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in PUT /apps/:id/permission/config", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
