package com.me.mdm.api.inventory;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.settings.location.GeoLocationFacade;
import com.me.mdm.api.ApiRequestHandler;

public class LocationsAPIRequestHandler extends ApiRequestHandler
{
    GeoLocationFacade geoLocationFacade;
    
    public LocationsAPIRequestHandler() {
        this.geoLocationFacade = new GeoLocationFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.geoLocationFacade.getLocations(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
