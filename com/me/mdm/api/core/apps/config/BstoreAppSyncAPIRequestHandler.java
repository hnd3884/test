package com.me.mdm.api.core.apps.config;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.apps.businessstore.StoreFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class BstoreAppSyncAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 202);
            final Long businessStoreID = null;
            responseDetails.put("RESPONSE", new StoreFacade().syncStoreToMDM(apiRequest.toJSONObject(), 3, businessStoreID));
            return responseDetails;
        }
        catch (final Exception ex) {
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
            responseDetails.put("RESPONSE", new StoreFacade().getSyncStatus(apiRequest.toJSONObject(), 3, businessStoreID));
            return responseDetails;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final Long businessStoreID = null;
            new StoreFacade().clearSyncStoreStatus(apiRequest.toJSONObject(), 3, businessStoreID);
            return JSONUtil.toJSON("status", 202);
        }
        catch (final Exception e) {
            if (e instanceof APIHTTPException) {
                throw (APIHTTPException)e;
            }
            this.logger.log(Level.SEVERE, "Exception in DELETE /apps/account/bstore/sync", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
