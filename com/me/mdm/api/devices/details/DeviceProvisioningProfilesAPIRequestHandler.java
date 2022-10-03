package com.me.mdm.api.devices.details;

import org.json.JSONException;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.server.device.DeviceFacade;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import com.me.mdm.api.ApiRequestHandler;

public class DeviceProvisioningProfilesAPIRequestHandler extends ApiRequestHandler
{
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        try {
            final JSONObject responseJSON = new JSONObject();
            Long resourceID = APIUtil.getResourceID(apiRequest.toJSONObject(), "device_id");
            final Long customerId = APIUtil.getCustomerID(apiRequest.toJSONObject());
            if (resourceID == -1L) {
                final String udid = APIUtil.getResourceIDString(apiRequest.toJSONObject(), "udid");
                resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
            }
            new DeviceFacade().validateIfDeviceExists(resourceID, customerId);
            responseJSON.put("status", 200);
            responseJSON.put("RESPONSE", (Object)new DeviceFacade().getDeviceInstalledProvisioningProfilesResponse(apiRequest.toJSONObject()));
            return responseJSON;
        }
        catch (final SyMException ex) {
            throw new APIHTTPException(ex.getErrorCode(), ex.getMessage(), new Object[0]);
        }
        catch (final JSONException ex2) {
            throw new APIHTTPException(500, ex2.getMessage(), new Object[0]);
        }
    }
}
