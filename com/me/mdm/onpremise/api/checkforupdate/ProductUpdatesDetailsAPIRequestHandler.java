package com.me.mdm.onpremise.api.checkforupdate;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class ProductUpdatesDetailsAPIRequestHandler extends ApiRequestHandler
{
    ProductUpdatesSettingsFacade productSettingsFacade;
    public Logger logger;
    
    public ProductUpdatesDetailsAPIRequestHandler() {
        this.productSettingsFacade = new ProductUpdatesSettingsFacade();
        this.logger = Logger.getLogger(ProductUpdatesDetailsAPIRequestHandler.class.getName());
    }
    
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.productSettingsFacade.getBuildVersionDetails());
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Error In getting ProductUpdatesDetailsAPIRequestHandler.BuildVersionDetails() ", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.productSettingsFacade.enableRemindMeLater(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Error In getting ProductUpdatesDetailsAPIRequestHandler.enableRemindMeLater() ", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
