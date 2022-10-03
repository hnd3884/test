package com.me.mdm.onpremise.api.checkforupdate;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ProductUpdatesAPIRequestHandler extends ApiRequestHandler
{
    ProductUpdatesSettingsFacade productSettingsFacade;
    public Logger logger;
    
    public ProductUpdatesAPIRequestHandler() {
        this.productSettingsFacade = new ProductUpdatesSettingsFacade();
        this.logger = Logger.getLogger(ProductUpdatesAPIRequestHandler.class.getName());
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.productSettingsFacade.isBuildNotificationAvailable());
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Error In getting ProductUpdatesAPIRequestHandler.isBuildNotificationAvailable() ", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.productSettingsFacade.removeUpdatesNotificationsettings(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Error In getting ProductUpdatesAPIRequestHandler.removeUpdatesNotificationsettings() ", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
