package com.me.mdm.api.core.apps.config;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualAppReleaseChannelsAPIRequestHandler extends ApiRequestHandler
{
    private AppFacade app;
    
    public IndividualAppReleaseChannelsAPIRequestHandler() {
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJson = new JSONObject();
            responseJson.put("status", 200);
            responseJson.put("RESPONSE", (Object)this.app.getAvailableChannels(apiRequest.toJSONObject()));
            return responseJson;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in Get method of AppReleaseChannelsAPIRequestHandler", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
