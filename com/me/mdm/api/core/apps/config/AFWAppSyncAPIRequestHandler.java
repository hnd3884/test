package com.me.mdm.api.core.apps.config;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.apps.businessstore.StoreFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AFWAppSyncAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 202);
            final Long businessStoreID = null;
            responseDetails.put("RESPONSE", new StoreFacade().syncStoreToMDM(apiRequest.toJSONObject(), 2, businessStoreID));
            return responseDetails;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in syncing AFW ", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final Long businessStoreID = null;
            responseDetails.put("RESPONSE", new StoreFacade().getSyncStatus(apiRequest.toJSONObject(), 2, businessStoreID));
            return responseDetails;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in syncing AFW ", ex);
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
