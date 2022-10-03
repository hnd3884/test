package com.me.mdm.api.devices.details;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONException;
import com.me.mdm.http.HttpException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.device.DeviceFacade;
import com.me.mdm.api.ApiRequestHandler;

public class DevicePollingTimeAPIRequestHandler extends ApiRequestHandler
{
    private DeviceFacade deviceFacade;
    
    public DevicePollingTimeAPIRequestHandler() {
        this.deviceFacade = new DeviceFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.deviceFacade.getNextPollTime(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, " -- doGet()    >   Exception   ", (Throwable)e);
            throw new HttpException(400, null);
        }
    }
}
