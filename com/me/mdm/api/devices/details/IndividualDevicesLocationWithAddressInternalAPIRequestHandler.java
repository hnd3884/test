package com.me.mdm.api.devices.details;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.server.device.DeviceFacade;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualDevicesLocationWithAddressInternalAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseJSON = new DeviceFacade().requestDeviceLocationsWithAddressInternal(apiRequest.toJSONObject());
        return responseJSON;
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseJSON = new DeviceFacade().getDeviceLocationsWithAddressInternal(apiRequest.toJSONObject());
        return responseJSON;
    }
}
