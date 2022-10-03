package com.me.mdm.api.message;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.webclient.remote.RemoteUtil;
import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class RemotePageMessageAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject responseJSON = new JSONObject();
        try {
            final JSONObject response = new JSONObject();
            final Long customerId = APIUtil.getCustomerID(apiRequest.toJSONObject());
            response.put("android_unsupported_message", (Object)this.getRemoteDeviceNotSupportedMessageStatus(customerId));
            response.put("ios_app_message", this.getIosAppMessageStatus(customerId));
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)response);
            return responseJSON;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "Exception in RemotePageMessageAPIRequestHandler ", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    private JSONObject getRemoteDeviceNotSupportedMessageStatus(final Long customerId) throws JSONException {
        final RemoteUtil remoteUtil = new RemoteUtil();
        final JSONObject status = new JSONObject();
        final int noOfUnsupportedAndroidDevices = remoteUtil.remoteNonEligibleDevicesWithCustId(customerId);
        status.put("no_of_android_unsupported_devices", noOfUnsupportedAndroidDevices);
        try {
            final String androidInfoBox = MDMUtil.getUserParameter(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), "androidInfoBox");
            if (noOfUnsupportedAndroidDevices > 0 && (androidInfoBox == null || androidInfoBox.isEmpty())) {
                status.put("display", true);
                return status;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in RemoteDeviceNotSupportedMessageHandler", e);
        }
        status.put("display", false);
        return status;
    }
    
    private boolean getIosAppMessageStatus(final Long customerId) throws JSONException {
        final RemoteUtil remoteUtil = new RemoteUtil();
        final JSONObject status = new JSONObject();
        final int noOfIOSEligibleDevices = remoteUtil.iOSEligibleDevicesWithCustId(customerId);
        try {
            final String iOSInfoBox = MDMUtil.getUserParameter(ApiFactoryProvider.getAuthUtilAccessAPI().getUserID(), "iOSInfoBox");
            if (noOfIOSEligibleDevices != 0 && (iOSInfoBox == null || iOSInfoBox.isEmpty())) {
                return true;
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in getIosAppMessageStatus", e);
        }
        return false;
    }
}
