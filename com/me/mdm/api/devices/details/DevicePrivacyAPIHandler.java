package com.me.mdm.api.devices.details;

import org.json.JSONException;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import java.util.Map;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import com.me.mdm.server.device.DeviceFacade;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DevicePrivacyAPIHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject result = new JSONObject();
            final JSONObject request = apiRequest.toJSONObject();
            result.put("status", 200);
            final Long deviceID = APIUtil.getResourceID(request, "device_id");
            new DeviceFacade().validateIfDeviceExists(deviceID, APIUtil.getCustomerID(request));
            result.put("RESPONSE", (Map)new PrivacySettingsHandler().getPrivacySettingsForMdDevices(deviceID));
            return result;
        }
        catch (final JSONException e) {
            this.logger.log(Level.SEVERE, "exception occurred in DevicePrivacyAPIHandler.doGet", (Throwable)e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
