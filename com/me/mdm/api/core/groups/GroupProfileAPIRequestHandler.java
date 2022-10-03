package com.me.mdm.api.core.groups;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class GroupProfileAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    protected ProfileFacade profileFacade;
    
    public GroupProfileAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.profileFacade = new ProfileFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject request = apiRequest.toJSONObject();
            request.getJSONObject("msg_header").getJSONObject("resource_identifier").remove("group_id");
            this.profileFacade.associateProfilesToGroups(request);
            final JSONObject response = new JSONObject();
            response.put("status", 202);
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "jsonexception", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
