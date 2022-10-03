package com.me.mdm.api.inventory;

import com.me.mdm.server.security.passcode.AndroidRecoveryPasscodeMangedDeviceListener;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import com.me.mdm.server.security.passcode.AndroidRecoveryPasscodeHandler;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.mdm.server.device.DeviceFacade;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.api.APIUtil;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class AndroidRecoveryPasscodeAPIRequestHandler extends ApiRequestHandler
{
    public static Logger logger;
    
    @Override
    public Object doPost(final APIRequest apiRequest) {
        try {
            final JSONObject response = new JSONObject();
            final JSONObject apiRequestJson = apiRequest.toJSONObject();
            final String reason = apiRequestJson.getJSONObject("msg_body").optString("reason", "");
            final Long resourceId = APIUtil.getResourceID(apiRequest.toJSONObject(), "device_id");
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final Long customerId = APIUtil.getCustomerID(apiRequestJson);
            final String userName = DMUserHandler.getUserName(loginId);
            final String deviceName = ManagedDeviceHandler.getInstance().getDeviceName(resourceId);
            new DeviceFacade().validateIfDeviceExists(resourceId, customerId);
            if (!reason.isEmpty()) {
                final String remarks = "dc.mdm.inventory.android.recovery_passcode";
                final String remarksArgs = deviceName + "@@@" + reason;
                MDMEventLogHandler.getInstance().MDMEventLogEntry(29052, resourceId, userName, remarks, remarksArgs, customerId);
            }
            final AndroidRecoveryPasscodeHandler androidRecoveryPasscodeHandler = new AndroidRecoveryPasscodeHandler();
            final Long totp = androidRecoveryPasscodeHandler.getTotpForResourceId(resourceId);
            final JSONObject result = new JSONObject();
            result.put("password", (Object)totp);
            response.put("RESPONSE", (Object)result);
            response.put("status", 200);
            return response;
        }
        catch (final APIHTTPException e) {
            AndroidRecoveryPasscodeAPIRequestHandler.logger.log(Level.SEVERE, "Exception in Android Recover Passcode Api request  ", e);
            throw e;
        }
        catch (final Exception e2) {
            AndroidRecoveryPasscodeAPIRequestHandler.logger.log(Level.SEVERE, "Exception in Android Recover Passcode Api request  ", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        AndroidRecoveryPasscodeAPIRequestHandler.logger = Logger.getLogger(AndroidRecoveryPasscodeMangedDeviceListener.class.getName());
    }
}
