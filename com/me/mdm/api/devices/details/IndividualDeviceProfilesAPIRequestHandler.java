package com.me.mdm.api.devices.details;

import org.json.JSONException;
import com.me.mdm.http.HttpException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualDeviceProfilesAPIRequestHandler extends ApiRequestHandler
{
    private ProfileFacade profile;
    
    public IndividualDeviceProfilesAPIRequestHandler() {
        this.profile = null;
        this.profile = new ProfileFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws HttpException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.profile.getDeviceProfileDetails(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new HttpException(500, ex.getMessage());
        }
    }
}
