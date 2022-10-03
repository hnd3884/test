package com.me.mdm.onpremise.api.fs;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ShowSettingsHandler extends ApiRequestHandler
{
    public Logger logger;
    
    public ShowSettingsHandler() {
        this.logger = Logger.getLogger(ShowSettingsHandler.class.getName());
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final SecureGatewayServerFacade securegatewayserver = new SecureGatewayServerFacade();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)securegatewayserver.showSettings());
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in ShowSettingsHandler ", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
