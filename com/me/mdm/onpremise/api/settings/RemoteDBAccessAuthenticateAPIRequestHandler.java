package com.me.mdm.onpremise.api.settings;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class RemoteDBAccessAuthenticateAPIRequestHandler extends ApiRequestHandler
{
    RemoteDBAccessFacade remoteDBAcessFacade;
    
    public RemoteDBAccessAuthenticateAPIRequestHandler() {
        this.remoteDBAcessFacade = new RemoteDBAccessFacade();
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.remoteDBAcessFacade.isValidLogin(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
