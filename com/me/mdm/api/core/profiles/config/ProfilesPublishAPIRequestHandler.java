package com.me.mdm.api.core.profiles.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ProfilesPublishAPIRequestHandler extends ApiRequestHandler
{
    ProfileFacade profile;
    
    public ProfilesPublishAPIRequestHandler() {
        this.profile = new ProfileFacade();
    }
    
    @Override
    public JSONObject doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.profile.publishProfile(apiRequest.toJSONObject());
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Ex eption while publishing the profile", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
