package com.me.mdm.api.core.groups;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualGroupProfileAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    protected ProfileFacade profileFacade;
    
    public IndividualGroupProfileAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.profileFacade = new ProfileFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject request = apiRequest.toJSONObject();
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.profileFacade.getGroupProfileDetail(request));
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "jsonexception", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
