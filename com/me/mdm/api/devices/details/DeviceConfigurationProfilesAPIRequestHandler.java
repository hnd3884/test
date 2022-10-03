package com.me.mdm.api.devices.details;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.http.HttpException;
import com.me.mdm.server.device.DeviceFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DeviceConfigurationProfilesAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)new DeviceFacade().getDeviceInstalledConfigurationProfilesResponse(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final SyMException ex) {
            throw new HttpException(ex.getErrorCode(), ex.getMessage());
        }
        catch (final JSONException ex2) {
            throw new HttpException(500, ex2.getMessage());
        }
    }
}
