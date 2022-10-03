package com.me.mdm.api.devices.details;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.device.DeviceFacade;
import com.me.mdm.api.ApiRequestHandler;

public class DeviceSummaryAPIRequestHandler extends ApiRequestHandler
{
    DeviceFacade deviceFacade;
    
    public DeviceSummaryAPIRequestHandler() {
        this.deviceFacade = new DeviceFacade();
    }
    
    @Override
    public JSONObject doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.deviceFacade.getDeviceDistributionSummary(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
