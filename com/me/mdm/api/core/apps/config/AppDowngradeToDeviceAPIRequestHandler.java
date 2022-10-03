package com.me.mdm.api.core.apps.config;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import com.me.mdm.api.ApiRequestHandler;

public class AppDowngradeToDeviceAPIRequestHandler extends ApiRequestHandler
{
    AppFacade appFacade;
    
    public AppDowngradeToDeviceAPIRequestHandler() {
        this.appFacade = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public JSONObject doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 204);
            final JSONObject requestJSON = apiRequest.toJSONObject();
            requestJSON.getJSONObject("msg_header").getJSONObject("resource_identifier").remove("device_id");
            this.appFacade.downgradeAppForDevices(requestJSON);
            return response;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in AppDowngradeToDeviceAPIRequestHandler", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
