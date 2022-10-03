package com.me.mdm.api.command.device;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.device.DeviceFacade;
import com.me.mdm.api.ApiRequestHandler;

public class RecentCommandAPIRequestHandler extends ApiRequestHandler
{
    private DeviceFacade deviceFacade;
    
    public RecentCommandAPIRequestHandler() {
        this.deviceFacade = new DeviceFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final JSONObject complianceJSON = this.deviceFacade.getRecentCommandStatusForDevice(apiRequest.toJSONObject());
            responseJSON.put("RESPONSE", (Object)complianceJSON);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, " -- doGet()   >   Exception    ", (Throwable)ex);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
}
