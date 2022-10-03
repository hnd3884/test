package com.me.mdm.onpremise.api.settings;

import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.onpremise.server.user.ActiveSessionsFacade;
import com.me.mdm.api.ApiRequestHandler;

public class LogoutAPIRequestHandler extends ApiRequestHandler
{
    private ActiveSessionsFacade activeSessionsFacade;
    
    public LogoutAPIRequestHandler() {
        this.activeSessionsFacade = new ActiveSessionsFacade();
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("status", 202);
        this.activeSessionsFacade.logoutSession(apiRequest);
        return responseJSON;
    }
}
