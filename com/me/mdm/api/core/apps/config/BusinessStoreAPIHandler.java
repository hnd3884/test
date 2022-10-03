package com.me.mdm.api.core.apps.config;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.apps.businessstore.StoreFacade;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class BusinessStoreAPIHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("RESPONSE", (Object)new StoreFacade().addStoreDetails(apiRequest.toJSONObject(), 3));
            responseDetails.put("status", 200);
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
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            final Long businessStoreID = null;
            new StoreFacade().getStoreDetails(apiRequest.toJSONObject(), 3, businessStoreID);
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)new StoreFacade().deleteStoreDetails(apiRequest.toJSONObject(), 3, businessStoreID));
            return responseDetails;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final Long businessStoreID = null;
            responseDetails.put("RESPONSE", new StoreFacade().getStoreDetails(apiRequest.toJSONObject(), 3, businessStoreID));
            return responseDetails;
        }
        catch (final Exception ex) {
            if (ex instanceof APIHTTPException) {
                throw (APIHTTPException)ex;
            }
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
