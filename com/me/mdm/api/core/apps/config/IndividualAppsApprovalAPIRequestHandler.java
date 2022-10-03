package com.me.mdm.api.core.apps.config;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualAppsApprovalAPIRequestHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public IndividualAppsApprovalAPIRequestHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public JSONObject doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            this.appFacade.approveApp(apiRequest.toJSONObject());
            return response;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in IndividualAppsApprovedAPIRequestHandler", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
