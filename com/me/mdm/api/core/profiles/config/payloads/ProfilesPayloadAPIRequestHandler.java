package com.me.mdm.api.core.profiles.config.payloads;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.core.profiles.ProfilesWrapper;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ProfilesPayloadAPIRequestHandler extends ApiRequestHandler
{
    ProfileFacade profile;
    
    public ProfilesPayloadAPIRequestHandler() {
        this.profile = new ProfileFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            apiRequest.urlStartKey = "profiles";
            responseJSON.put("RESPONSE", this.profile.getPayloadNames(ProfilesWrapper.toJSONWithCollectionID(apiRequest)));
            return responseJSON;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while getting payload names", ex);
            throw new APIHTTPException("COM0014", new Object[0]);
        }
    }
}
