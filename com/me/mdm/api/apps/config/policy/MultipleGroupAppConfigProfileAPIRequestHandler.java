package com.me.mdm.api.apps.config.policy;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.core.profiles.ProfilesWrapper;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.config.AppConfigFacade;
import com.me.mdm.api.ApiRequestHandler;

public class MultipleGroupAppConfigProfileAPIRequestHandler extends ApiRequestHandler
{
    AppConfigFacade appConfigFacade;
    
    public MultipleGroupAppConfigProfileAPIRequestHandler() {
        this.appConfigFacade = AppConfigFacade.getInstance();
    }
    
    @Override
    public JSONObject doPost(final APIRequest apiRequest) {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            final JSONObject request = ProfilesWrapper.toJSONWithCollectionID(apiRequest);
            request.getJSONObject("msg_header").getJSONObject("resource_identifier").remove("group_id");
            this.appConfigFacade.associateProfilesToGroups(apiRequest.toJSONObject());
            return response;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in associating app configuration profile to groups", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
