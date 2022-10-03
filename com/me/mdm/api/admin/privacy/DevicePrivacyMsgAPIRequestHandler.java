package com.me.mdm.api.admin.privacy;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.privacy.PrivacyCustomMessageHandler;
import org.json.JSONObject;
import com.me.mdm.api.APIUtil;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DevicePrivacyMsgAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final Long customerId = APIUtil.getCustomerID(apiRequest.toJSONObject());
        try {
            final JSONObject responseJSON = new JSONObject();
            final JSONObject requestJSON = apiRequest.toJSONObject().getJSONObject("msg_body");
            new PrivacyCustomMessageHandler().addOrUpdateCustomMessage((String)requestJSON.get("message_name"), (String)requestJSON.get("message"), customerId);
            responseJSON.put("status", 202);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in DevicePrivacyMsgAPIRequestHandler ", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final Long customerId = APIUtil.getCustomerID(apiRequest.toJSONObject());
        try {
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", 200);
            final String messageName = String.valueOf(apiRequest.toJSONObject().getJSONObject("msg_header").getJSONObject("filters").get("operation"));
            final String message = new PrivacyCustomMessageHandler().getCustomMessage(messageName, customerId);
            final JSONObject responseBody = new JSONObject();
            responseBody.put("message", (Object)message);
            responseBody.put("message_name", (Object)messageName);
            responseJSON.put("RESPONSE", (Object)responseBody);
            return responseJSON;
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, "Exception in DevicePrivacyMsgAPIRequestHandler ", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
