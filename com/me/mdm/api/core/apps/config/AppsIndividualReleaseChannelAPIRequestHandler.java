package com.me.mdm.api.core.apps.config;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppsIndividualReleaseChannelAPIRequestHandler extends ApiRequestHandler
{
    private AppFacade app;
    
    public AppsIndividualReleaseChannelAPIRequestHandler() {
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 204);
            this.app.updateChannel(apiRequest.toJSONObject());
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in PUT method of AppsIndividualReleaseChannelAPIRequestHandler", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.app.getChannelDetails(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in Get method of AppsIndividualReleaseChannelAPIRequestHandler", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
