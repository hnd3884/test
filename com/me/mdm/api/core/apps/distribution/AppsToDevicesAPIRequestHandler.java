package com.me.mdm.api.core.apps.distribution;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppsToDevicesAPIRequestHandler extends ApiRequestHandler
{
    private AppFacade app;
    
    public AppsToDevicesAPIRequestHandler() {
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.app.getDevicesForApp(apiRequest.toJSONObject()));
            return JSONUtil.toJSON("status", 200);
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in GET /apps/:id/devices", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.app.associateAppsToDevices(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 202);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in POST /apps/:id/devices", ex2);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.app.disassociateAppsToDevices(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 202);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in DELETE /apps/:id/devices", ex2);
            throw new APIHTTPException(500, null, new Object[0]);
        }
    }
}
