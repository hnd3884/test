package com.me.mdm.api.apps.config.policy;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.apps.config.AppConfigFacade;
import com.me.mdm.api.ApiRequestHandler;

public class OEMConfiguredVendorsAPIRequestHandler extends ApiRequestHandler
{
    AppConfigFacade appConfigFacade;
    
    public OEMConfiguredVendorsAPIRequestHandler() {
        this.appConfigFacade = AppConfigFacade.getInstance();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("RESPONSE", (Object)this.appConfigFacade.getConfiguredVendors(apiRequest.toJSONObject()));
            return response;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception getting configured vendors", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
