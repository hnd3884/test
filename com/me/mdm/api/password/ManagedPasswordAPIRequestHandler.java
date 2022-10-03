package com.me.mdm.api.password;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ManagedPasswordAPIRequestHandler extends ApiRequestHandler
{
    private static final Logger LOGGER;
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)new ManagedPasswordIDFacade().createMDMManagedPasswordID(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            ManagedPasswordAPIRequestHandler.LOGGER.log(Level.SEVERE, "Exception in ManagedPasswordAPIRequestHandler", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMConfigLogger");
    }
}
