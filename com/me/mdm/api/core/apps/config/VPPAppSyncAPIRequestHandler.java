package com.me.mdm.api.core.apps.config;

import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.apps.businessstore.StoreFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class VPPAppSyncAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 202);
            final JSONObject jsonObject = apiRequest.toJSONObject();
            new StoreFacade().syncAllStores(jsonObject, 1);
            return responseDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in POST /apps/account/vpp/sync", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final JSONObject jsonObject = apiRequest.toJSONObject();
            responseDetails.put("RESPONSE", (Object)new StoreFacade().getAllStoreSyncStatus(jsonObject, 1));
            return responseDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            this.logger.log(Level.SEVERE, "Exception in GET /apps/account/vpp/sync", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
