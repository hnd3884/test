package com.me.mdm.api.core.apps.distribution;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppsUpdateAPIRequestHandler extends ApiRequestHandler
{
    private AppFacade app;
    
    public AppsUpdateAPIRequestHandler() {
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.app.upgradeAppForAllDevices(apiRequest.toJSONObject());
            return JSONUtil.toJSON("status", 202);
        }
        catch (final APIHTTPException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in POST /apps/:id/update", ex2);
            throw new APIHTTPException("COM0004", new Object[] { ex2 });
        }
    }
}
