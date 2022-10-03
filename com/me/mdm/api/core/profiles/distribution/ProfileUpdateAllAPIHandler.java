package com.me.mdm.api.core.profiles.distribution;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ProfileUpdateAllAPIHandler extends ApiRequestHandler
{
    public Logger logger;
    private ProfileFacade profileFacade;
    
    public ProfileUpdateAllAPIHandler() {
        this.logger = Logger.getLogger("MDMAPILogger");
        this.profileFacade = new ProfileFacade();
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject requestJSON = apiRequest.toJSONObject();
            final JSONObject responseJSON = new JSONObject();
            this.profileFacade.updateRecentProfileForAll(requestJSON);
            responseJSON.put("status", 202);
            responseJSON.put("RESPONSE", (Object)new JSONObject());
            return responseJSON;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
    }
}
