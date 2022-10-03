package com.me.mdm.api.core.apps.config;

import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.apps.businessstore.StoreFacade;
import com.me.mdm.server.apps.android.afw.GoogleForWorkSettings;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AFWAPIHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            final JSONObject jsonObject = apiRequest.toJSONObject();
            final JSONObject messageBody = jsonObject.getJSONObject("msg_body");
            messageBody.put("type", (Object)GoogleForWorkSettings.ENTERPRISE_TYPE_EMM);
            jsonObject.put("msg_body", (Object)messageBody);
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)new StoreFacade().addStoreDetails(jsonObject, 2));
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
            final JSONObject response = new StoreFacade().deleteStoreDetails(apiRequest.toJSONObject(), 2, businessStoreID);
            responseDetails.put("RESPONSE", (Object)response);
            final Boolean success = (Boolean)response.get("success");
            if (success) {
                responseDetails.put("status", 200);
                return responseDetails;
            }
            final String errorStr = String.valueOf(response.get("Status"));
            if (errorStr.toLowerCase().contains("notfound")) {
                throw new APIHTTPException("COM0014", new Object[0]);
            }
            throw new APIHTTPException("COM0004", new Object[0]);
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
            responseDetails.put("RESPONSE", new StoreFacade().getStoreDetails(apiRequest.toJSONObject(), 2, businessStoreID));
            return responseDetails;
        }
        catch (final Exception ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
