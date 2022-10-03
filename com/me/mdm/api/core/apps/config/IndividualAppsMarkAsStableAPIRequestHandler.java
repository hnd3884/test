package com.me.mdm.api.core.apps.config;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualAppsMarkAsStableAPIRequestHandler extends ApiRequestHandler
{
    private AppFacade app;
    
    public IndividualAppsMarkAsStableAPIRequestHandler() {
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject apiResponseJson = new JSONObject();
            apiResponseJson.put("status", 200);
            apiResponseJson.put("RESPONSE", (Object)this.app.markAppAsStable(apiRequest.toJSONObject()));
            return apiResponseJson;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in /apps/:id/labels/:label_id/stable", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject apiResponseJson = new JSONObject();
            apiResponseJson.put("status", 200);
            apiResponseJson.put("RESPONSE", (Object)this.app.getChannelsToMerge(apiRequest.toJSONObject()));
            return apiResponseJson;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in Get /apps/:id/labels/:label_id/stable", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
