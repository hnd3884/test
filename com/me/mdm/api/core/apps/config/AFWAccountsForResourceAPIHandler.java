package com.me.mdm.api.core.apps.config;

import com.me.mdm.api.APIUtil;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.apps.android.afw.AFWAccountRetryHandler;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AFWAccountsForResourceAPIHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            responseDetails.put("RESPONSE", (Object)new AFWAccountRetryHandler().handleRetryRequest(apiRequest.toJSONObject()));
            return responseDetails;
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final APIHTTPException e2) {
            throw e2;
        }
        catch (final Exception e3) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final JSONObject requestJSON = apiRequest.toJSONObject();
            responseDetails.put("RESPONSE", (Object)new AFWAccountRetryHandler().getAccountStatusForDevice(APIUtil.getResourceID(requestJSON, "device_id"), APIUtil.getCustomerID(requestJSON)));
            return responseDetails;
        }
        catch (final JSONException e) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        catch (final APIHTTPException e2) {
            throw e2;
        }
        catch (final Exception e3) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
