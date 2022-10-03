package com.me.mdm.onpremise.server.api.time;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import java.util.Properties;
import com.me.mdm.onpremise.server.time.ServerTimeValidationTask;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ServerTimeValidationAPIRequestHandler extends ApiRequestHandler
{
    private static final Logger LOGGER;
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            new ServerTimeValidationTask().executeTask(new Properties());
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)new JSONObject());
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            ServerTimeValidationAPIRequestHandler.LOGGER.severe("Error while validating server time : " + ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ServerTimeValidationAPIRequestHandler.class.getName());
    }
}
