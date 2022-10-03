package com.me.mdm.api.core.apps.config;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import com.me.mdm.server.apps.android.afw.AFWAccountStatusHandler;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class AFWAccountsAPIHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseDetails = new JSONObject();
            responseDetails.put("status", 200);
            final JSONObject requestJSON = apiRequest.toJSONObject();
            responseDetails.put("RESPONSE", (Object)new AFWAccountStatusHandler().getAFWAccountDetails(requestJSON, APIUtil.getCustomerID(requestJSON)));
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
