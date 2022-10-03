package com.me.mdm.api.core.profiles.config;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.profiles.ProfileFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ContentRatingAPIRequestHandler extends ApiRequestHandler
{
    ProfileFacade facade;
    
    public ContentRatingAPIRequestHandler() {
        this.facade = new ProfileFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.facade.getContentRating(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception e2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
