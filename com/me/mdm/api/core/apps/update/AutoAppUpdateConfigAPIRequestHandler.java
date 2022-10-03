package com.me.mdm.api.core.apps.update;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import java.util.logging.Logger;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AutoAppUpdateConfigAPIRequestHandler extends ApiRequestHandler
{
    private AppFacade app;
    public Logger logger;
    
    public AutoAppUpdateConfigAPIRequestHandler() {
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
        this.logger = Logger.getLogger("MDMAPILogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            final JSONObject responseJSON = this.app.getAutoAppUpdateConfigList(apiRequest.toJSONObject());
            if (responseJSON != null) {
                responseDetails.put("status", 200);
                responseDetails.put("RESPONSE", (Object)responseJSON);
                return responseDetails;
            }
            return JSONUtil.toJSON("status", 204);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception while deleting auto update configuration", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.app.deleteAutoAppUpdateConfig(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 202);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception while deleting auto update configuration", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)this.app.addAutoAppUpdateConfig(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPut(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.app.updateAutoAppUpdateConfig(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 202);
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
