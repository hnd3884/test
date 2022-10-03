package com.me.mdm.api.apps.config.policy;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.config.AppConfigFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualGroupToAppConfigProfileAPIRequestHandler extends ApiRequestHandler
{
    AppConfigFacade appConfigFacade;
    
    public IndividualGroupToAppConfigProfileAPIRequestHandler() {
        this.appConfigFacade = AppConfigFacade.getInstance();
    }
    
    @Override
    public JSONObject doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            this.appConfigFacade.associateProfilesToGroups(apiRequest.toJSONObject());
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception associating app configuration profile to group", ex);
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
            this.appConfigFacade.disassociateProfilesToGroups(apiRequest.toJSONObject());
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception associating app configuration profile to group", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
