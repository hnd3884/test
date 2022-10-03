package com.me.mdm.api.core.profiles.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class PreProfileValidationAPIRequestHandler extends ApiRequestHandler
{
    ProfileFacade profileFacade;
    
    public PreProfileValidationAPIRequestHandler() {
        this.profileFacade = new ProfileFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.profileFacade.validateKioskForWindowsPhone(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in POST /profiles/validatedist", ex);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
}
