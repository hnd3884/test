package com.me.mdm.api.enrollment;

import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.core.enrollment.AndroidQREnrollmentHandler;
import com.me.mdm.api.ApiRequestHandler;

public class AdvQRWifiSettingsAPIRequestHandler extends ApiRequestHandler
{
    AndroidQREnrollmentHandler qrEnrollmentHandler;
    Logger logger;
    
    public AdvQRWifiSettingsAPIRequestHandler() {
        this.qrEnrollmentHandler = new AndroidQREnrollmentHandler();
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        try {
            response.put("status", 200);
            final long userId = APIUtil.getUserID(apiRequest.toJSONObject());
            final Long customerId = APIUtil.getCustomerID(apiRequest.toJSONObject());
            final JSONObject ssidJson = this.qrEnrollmentHandler.getWifiConfiguration(userId, customerId);
            if (ssidJson.length() > 0) {
                final JSONObject wifiConfDetails = new JSONObject();
                wifiConfDetails.put("ssid", (Object)String.valueOf(ssidJson.get("ssid")));
                wifiConfDetails.put("security_type", (Object)String.valueOf(ssidJson.get("security_type")));
                response.put("RESPONSE", (Object)wifiConfDetails);
            }
            else {
                response.put("RESPONSE", (Object)new JSONObject());
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in doGet", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        final JSONObject requestJson = apiRequest.toJSONObject();
        try {
            this.qrEnrollmentHandler.addOrUpdateWifiConfiguration(apiRequest.toJSONObject());
            response.put("status", 200);
            response.put("RESPONSE", (Object)new JSONObject());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in doPost", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        return response;
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        final JSONObject requestJson = apiRequest.toJSONObject();
        try {
            this.qrEnrollmentHandler.deleteWifiConfiguration(requestJson);
            response.put("status", 200);
            response.put("RESPONSE", (Object)new JSONObject());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in doDelete", e);
        }
        return response;
    }
}
