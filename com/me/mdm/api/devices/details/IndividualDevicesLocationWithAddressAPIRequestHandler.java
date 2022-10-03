package com.me.mdm.api.devices.details;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.server.device.DeviceFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualDevicesLocationWithAddressAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseJSON = new DeviceFacade().getDeviceLocationsWithAddress(apiRequest.toJSONObject());
        return responseJSON;
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject apiResponse = new DeviceFacade().requestDeviceLocationsWithAddress(apiRequest.toJSONObject());
            responseJSON.put("status", apiResponse.getInt("status"));
            responseJSON.put("RESPONSE", (Object)apiResponse.getJSONObject("RESPONSE"));
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
