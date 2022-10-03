package com.me.mdm.onpremise.api.technician;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.onpremise.server.user.TechniciansFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class TechEmailAPIRequestHandler extends ApiRequestHandler
{
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseJSON = new JSONObject();
        responseJSON.put("status", 200);
        final boolean isEmailExists = new TechniciansFacade().isEmailExistForOtherUser(apiRequest.toJSONObject());
        final JSONObject dataJSON = new JSONObject();
        dataJSON.put("is_email_exists", isEmailExists);
        responseJSON.put("RESPONSE", (Object)dataJSON);
        return responseJSON;
    }
}
