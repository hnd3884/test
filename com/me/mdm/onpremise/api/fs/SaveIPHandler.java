package com.me.mdm.onpremise.api.fs;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class SaveIPHandler extends ApiRequestHandler
{
    public Logger logger;
    
    public SaveIPHandler() {
        this.logger = Logger.getLogger(SaveIPHandler.class.getName());
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final SecureGatewayServerFacade securegatewayserver = new SecureGatewayServerFacade();
            final JSONObject responseJSON = new JSONObject();
            securegatewayserver.saveIP(apiRequest.toJSONObject());
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in SaveIPHandler ", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
