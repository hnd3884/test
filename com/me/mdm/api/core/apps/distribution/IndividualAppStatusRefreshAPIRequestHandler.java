package com.me.mdm.api.core.apps.distribution;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualAppStatusRefreshAPIRequestHandler extends ApiRequestHandler
{
    AppFacade app;
    
    public IndividualAppStatusRefreshAPIRequestHandler() {
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            this.app.refreshAppStatusForAppGroup(apiRequest.toJSONObject());
            responseDetails.put("status", 204);
            return responseDetails;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in IndividualAppStatusRefreshAPIRequestHandler", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
