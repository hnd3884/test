package com.me.mdm.api.core.apps.config;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualAppsAPIRequestHandler extends ApiRequestHandler
{
    private AppFacade app;
    
    public IndividualAppsAPIRequestHandler() {
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.app.updateApp(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "JSONException in PUT /apps/:id", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.app.getApp(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in GET /apps/:id", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.app.deleteApp(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 204);
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in DELETE /apps/:id", (Throwable)ex);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
}
