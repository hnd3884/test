package com.me.mdm.api.core.apps.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppPermissionListAPIRequestHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public AppPermissionListAPIRequestHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject temp = new JSONObject();
            temp.put("status", 200);
            temp.put("RESPONSE", (Object)this.appFacade.getPermissionsListforApp(apiRequest.toJSONObject()));
            return temp;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in GET /apps/:id/permission", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
