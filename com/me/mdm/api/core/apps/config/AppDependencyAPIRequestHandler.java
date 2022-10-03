package com.me.mdm.api.core.apps.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppDependencyAPIRequestHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public AppDependencyAPIRequestHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.appFacade.addAppDependencies(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in POST /apps/dependency", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
