package com.me.mdm.onpremise.api.checkforupdate;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ProductUpdatesSettingsAPIRequestHandler extends ApiRequestHandler
{
    ProductUpdatesSettingsFacade productUpdatesSettingsFacade;
    public Logger logger;
    
    public ProductUpdatesSettingsAPIRequestHandler() {
        this.productUpdatesSettingsFacade = new ProductUpdatesSettingsFacade();
        this.logger = Logger.getLogger(ProductUpdatesSettingsAPIRequestHandler.class.getName());
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.productUpdatesSettingsFacade.saveUpdatesNotificationsettings(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Error In getting ProductUpdatesSettingsAPIRequestHandler.saveUpdatesNotificationsettings() ", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.productUpdatesSettingsFacade.getUpdatesNotificationsettings());
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Error In getting ProductUpdatesSettingsAPIRequestHandler.getUpdatesNotificationsettings() ", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
