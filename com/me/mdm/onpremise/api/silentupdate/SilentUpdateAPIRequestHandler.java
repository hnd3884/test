package com.me.mdm.onpremise.api.silentupdate;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class SilentUpdateAPIRequestHandler extends ApiRequestHandler
{
    SilentUpdateFacade silentUpdateFacade;
    public Logger logger;
    
    public SilentUpdateAPIRequestHandler() {
        this.silentUpdateFacade = new SilentUpdateFacade();
        this.logger = Logger.getLogger("SilentUpdate");
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.silentUpdateFacade.setSilentUpdateSettings(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Error while posting silentupdatesettings", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.silentUpdateFacade.getSilentUpdateAlertMsgDetails());
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Error while gettings silentupdatesettings  details", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
