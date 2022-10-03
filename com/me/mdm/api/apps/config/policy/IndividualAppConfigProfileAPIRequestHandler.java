package com.me.mdm.api.apps.config.policy;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.core.profiles.ProfilesWrapper;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.config.AppConfigFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualAppConfigProfileAPIRequestHandler extends ApiRequestHandler
{
    AppConfigFacade appConfigFacade;
    
    public IndividualAppConfigProfileAPIRequestHandler() {
        this.appConfigFacade = AppConfigFacade.getInstance();
    }
    
    @Override
    public JSONObject doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            apiRequest.urlStartKey = "profiles";
            response.put("RESPONSE", (Object)this.appConfigFacade.addPayload(ProfilesWrapper.toJSONWithCollectionID(apiRequest)));
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in adding app configuration payload", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public JSONObject doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            apiRequest.urlStartKey = "profiles";
            response.put("RESPONSE", (Object)this.appConfigFacade.getPayloads(ProfilesWrapper.toJSONWithCollectionID(apiRequest)));
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in getting app configuration payloads", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
