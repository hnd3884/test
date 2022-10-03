package com.me.mdm.api.devices.details;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class IndividualDeviceAppStatusRefreshAPIRequestHandler extends ApiRequestHandler
{
    AppFacade facade;
    
    public IndividualDeviceAppStatusRefreshAPIRequestHandler() {
        this.facade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseObject = new JSONObject();
            this.facade.refreshAppStatusForDevice(apiRequest.toJSONObject());
            responseObject.put("status", 204);
            return responseObject;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in refresh status", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
