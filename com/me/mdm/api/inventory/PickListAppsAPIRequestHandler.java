package com.me.mdm.api.inventory;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.factory.MDMRestAPIFactoryProvider;
import com.me.mdm.server.apps.AppFacade;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class PickListAppsAPIRequestHandler extends ApiRequestHandler
{
    private Logger logger;
    private AppFacade app;
    
    public PickListAppsAPIRequestHandler() {
        this.logger = Logger.getLogger("InventoryLog");
        this.app = MDMRestAPIFactoryProvider.getAppFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        this.logger.log(Level.INFO, "start get apps list");
        try {
            final JSONObject response = new JSONObject();
            JSONObject result = this.app.getAppKioskPickList(apiRequest.toJSONObject());
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
            this.logger.log(Level.SEVERE, "Exception occurred in PickListAppsAPIRequestHandler", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final APIHTTPException e2) {
            this.logger.log(Level.SEVERE, "Exception occurred in PickListAppsAPIRequestHandler", e2);
            throw e2;
        }
        catch (final Exception e3) {
            this.logger.log(Level.SEVERE, "Exception occurred in PickListAppsAPIRequestHandler", e3);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
