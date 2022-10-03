package com.me.mdm.onpremise.api.settings;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.onpremise.server.user.ChangePasswordFacade;
import com.me.mdm.api.ApiRequestHandler;

public class ChangePasswordAPIRequestHandler extends ApiRequestHandler
{
    private ChangePasswordFacade changePasswordFacade;
    
    public ChangePasswordAPIRequestHandler() {
        this.changePasswordFacade = new ChangePasswordFacade();
    }
    
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.changePasswordFacade.updatePassword(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
