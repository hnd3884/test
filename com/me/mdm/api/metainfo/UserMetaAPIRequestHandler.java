package com.me.mdm.api.metainfo;

import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class UserMetaAPIRequestHandler extends ApiRequestHandler
{
    Logger logger;
    
    public UserMetaAPIRequestHandler() {
        this.logger = Logger.getLogger(UserMetaAPIRequestHandler.class.getName());
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.getResponse());
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, " Exception in UserMetaAPIRequestHandler", (Throwable)ex);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
    
    private JSONObject getResponse() {
        JSONObject user_meta = new JSONObject();
        try {
            user_meta = new JSONObject(MDMRestAPIFactoryProvider.getUserMetaAPI().getUserMeta().toString());
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception while getting user meta", (Throwable)e);
        }
        return user_meta;
    }
}
