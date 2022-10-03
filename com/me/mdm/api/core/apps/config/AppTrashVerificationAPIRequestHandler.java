package com.me.mdm.api.core.apps.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppTrashVerificationAPIRequestHandler extends ApiRequestHandler
{
    private AppFacade app;
    
    public AppTrashVerificationAPIRequestHandler() {
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseObject = new JSONObject();
        try {
            responseObject.put("RESPONSE", (Object)this.app.verifyTrashActivity(apiRequest.toJSONObject()));
            responseObject.put("status", 200);
            return responseObject;
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception in POST /apps/trash/verify", e);
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
    }
}
