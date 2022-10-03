package com.me.mdm.api.core.profiles.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ProfileRestoreRequestHandler extends ApiRequestHandler
{
    private ProfileFacade profileFacade;
    
    public ProfileRestoreRequestHandler() {
        this.profileFacade = new ProfileFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.profileFacade.restoreProfileFromTrash(apiRequest.toJSONObject());
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception while restore profile from trash", ex2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
