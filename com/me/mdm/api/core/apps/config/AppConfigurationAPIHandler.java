package com.me.mdm.api.core.apps.config;

import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppConfigurationAPIHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public AppConfigurationAPIHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject temp = new JSONObject();
            temp.put("status", 200);
            temp.put("RESPONSE", (Object)this.appFacade.getAppConfiguration(apiRequest.toJSONObject()));
            return temp;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in GET /apps/:id/configuration", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.appFacade.addAppConfiguration(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in POST /apps/:id/configuration", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.appFacade.deleteAppConfiguration(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 202);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in DELETE /apps/:id/configuration", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
