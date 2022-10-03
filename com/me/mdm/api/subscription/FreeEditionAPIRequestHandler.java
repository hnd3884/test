package com.me.mdm.api.subscription;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class FreeEditionAPIRequestHandler extends ApiRequestHandler
{
    Logger logger;
    
    public FreeEditionAPIRequestHandler() {
        this.logger = Logger.getLogger(FreeEditionAPIRequestHandler.class.getName());
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject temp = apiRequest.toJSONObject().getJSONObject("msg_body");
            new MDMLicenseFacade().moveToFreeEdition(temp);
            final JSONObject response = new JSONObject();
            response.put("status", 202);
            return response;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "JSONException in freeEdition APIRequestHandler", (Throwable)ex);
            throw new APIHTTPException("COM0005", new Object[0]);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in freeEdition APIRequestHandler", e);
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
