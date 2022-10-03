package com.me.mdm.api.devices.details;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.device.DeviceFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class SystemActivityAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)new DeviceFacade().getSystemActivity(apiRequest.toJSONObject()));
            return response;
        }
        catch (final Exception e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
