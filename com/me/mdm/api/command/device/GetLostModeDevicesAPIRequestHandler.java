package com.me.mdm.api.command.device;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.location.lostmode.LostModeDataHandler;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class GetLostModeDevicesAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public GetLostModeDevicesAPIRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("RESPONSE", (Object)new LostModeDataHandler().getLostModeDeviceList(apiRequest.toJSONObject()));
            response.put("status", 200);
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in GetLostModeDevicesAPIRequestHandler.doGet()", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
