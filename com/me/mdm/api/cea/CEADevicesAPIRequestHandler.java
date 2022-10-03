package com.me.mdm.api.cea;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.easmanagement.CEAApiFacade;
import com.me.mdm.api.ApiRequestHandler;

public class CEADevicesAPIRequestHandler extends ApiRequestHandler
{
    private static CEAApiFacade ceaApiFacade;
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)CEADevicesAPIRequestHandler.ceaApiFacade.getCEADevicesDetails(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, " -- doGet()   >   Exception    ", (Throwable)ex);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            CEADevicesAPIRequestHandler.ceaApiFacade.removeCEADevices(apiRequest.toJSONObject());
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, " -- doDelete()   >   Exception    ", (Throwable)ex);
            throw new APIHTTPException(400, null, new Object[0]);
        }
    }
    
    static {
        CEADevicesAPIRequestHandler.ceaApiFacade = new CEAApiFacade();
    }
}
