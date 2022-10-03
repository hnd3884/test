package com.me.mdm.api.core.customer;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.server.customer.CustomerFacade;
import com.me.mdm.api.ApiRequestHandler;

public class CustomerDeleteSafeAPIHandler extends ApiRequestHandler
{
    CustomerFacade customer;
    
    public CustomerDeleteSafeAPIHandler() {
        this.customer = new CustomerFacade();
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)this.customer.hasDedicatedTechniciansForCustomer(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Issue on getting dedicated technicians for the customer");
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
