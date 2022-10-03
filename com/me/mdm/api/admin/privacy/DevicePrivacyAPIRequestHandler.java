package com.me.mdm.api.admin.privacy;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import org.json.JSONObject;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DevicePrivacyAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject apiRequestJson = apiRequest.toJSONObject();
        final Long customerId = APIUtil.getCustomerID(apiRequestJson);
        final Long userId = APIUtil.getUserID(apiRequestJson);
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject requestJSON = apiRequestJson.getJSONObject("msg_body");
            requestJSON.put("customer_id", (Object)customerId);
            requestJSON.put("user_id", (Object)userId);
            new PrivacySettingsHandler().savePrivacySettingsDetails(requestJSON);
            responseJSON.put("status", 202);
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final Long customerId = APIUtil.getCustomerID(apiRequest.toJSONObject());
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)new PrivacySettingsHandler().getPrivacyDetails(customerId));
            return responseJSON;
        }
        catch (final JSONException ex) {
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
