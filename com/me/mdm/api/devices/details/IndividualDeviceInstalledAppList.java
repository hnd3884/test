package com.me.mdm.api.devices.details;

import com.me.mdm.server.apps.AppFacade;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.device.DeviceFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualDeviceInstalledAppList extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", new DeviceFacade().getInstalledAppList(apiRequest.toJSONObject()));
            return response;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            new AppFacade().associateAppsToDevices(apiRequest.toJSONObject());
            response.put("status", 202);
            return response;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            new AppFacade().disassociateAppsToDevices(apiRequest.toJSONObject());
            response.put("status", 202);
            return response;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
