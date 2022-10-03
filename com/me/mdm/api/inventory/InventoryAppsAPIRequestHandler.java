package com.me.mdm.api.inventory;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class InventoryAppsAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    private AppFacade app;
    
    public InventoryAppsAPIRequestHandler() {
        this.logger = Logger.getLogger("InventoryLog");
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject response = new JSONObject();
            JSONObject result = this.app.getAppList(apiRequest.toJSONObject());
            if (result != null) {
                response.put("RESPONSE", (Object)result);
                response.put("status", 200);
            }
            else {
                result = new JSONObject();
                result.put("apps", (Object)new JSONArray());
                response.put("RESPONSE", (Object)result);
                response.put("status", 200);
            }
            return response;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception occurred in InventoryAppsAPIRequestHandler", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final APIHTTPException e2) {
            this.logger.log(Level.SEVERE, "Exception occurred in InventoryAppsAPIRequestHandler", e2);
            throw e2;
        }
    }
}
