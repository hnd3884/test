package com.me.mdm.api.core.users;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.user.ManagedUserFacade;
import com.me.mdm.api.ApiRequestHandler;

public class UserAppsAPIRequestHandler extends ApiRequestHandler
{
    ManagedUserFacade managedUserFacade;
    
    public UserAppsAPIRequestHandler() {
        this.managedUserFacade = MDMRestAPIFactoryProvider.getManagedUserFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject temp = new JSONObject();
            temp.put("status", 200);
            temp.put("RESPONSE", this.managedUserFacade.getAppsForUser(apiRequest.toJSONObject()));
            return temp;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
