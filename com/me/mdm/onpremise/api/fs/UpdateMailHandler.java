package com.me.mdm.onpremise.api.fs;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class UpdateMailHandler extends ApiRequestHandler
{
    public Logger logger;
    
    public UpdateMailHandler() {
        this.logger = Logger.getLogger(UpdateMailHandler.class.getName());
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final SecureGatewayServerFacade securegatewayserver = new SecureGatewayServerFacade();
            final JSONObject responseJSON = new JSONObject();
            securegatewayserver.updateMail(apiRequest.toJSONObject());
            responseJSON.put("status", 204);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in UpdateMailHandler ", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
