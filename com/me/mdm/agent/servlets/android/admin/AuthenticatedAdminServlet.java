package com.me.mdm.agent.servlets.android.admin;

import java.util.logging.Level;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import com.me.mdm.core.enrollment.AdminDeviceHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONObject;
import javax.servlet.http.HttpServletRequest;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.UserAuthenticatedRequestServlet;

public abstract class AuthenticatedAdminServlet extends UserAuthenticatedRequestServlet
{
    protected static final Logger LOGGER;
    
    protected JSONObject authenticateRequest(final DeviceRequest deviceRequest, final HttpServletRequest request) throws JSONException, DataAccessException, SecurityException, Exception {
        String authToken = request.getParameter("AuthToken");
        if (MDMStringUtils.isEmpty(authToken)) {
            authToken = request.getParameter("zapikey");
        }
        final String strData = (String)deviceRequest.deviceRequestData;
        final JSONObject requestJSON = new JSONObject(strData);
        final String udid = String.valueOf(requestJSON.get("UDID"));
        final Long loginId = new AdminDeviceHandler().getLoggedInUserId(udid);
        if (this.authenticateDevice(udid, loginId)) {
            requestJSON.put("LoginId", (Object)loginId);
            requestJSON.put("AuthToken", (Object)authToken);
            new AdminDeviceHandler().updateAdminDeviceLastContactedTime(udid);
            return requestJSON;
        }
        throw new SecurityException("Device not authenticated to perform request!");
    }
    
    private boolean authenticateDevice(final String udid, final Long loginId) throws Exception {
        final AdminDeviceHandler handler = new AdminDeviceHandler();
        return handler.isDeviceRegisteredForAdmin(udid, loginId);
    }
    
    protected JSONObject constructAndroidMessage(final DeviceMessage deviceMsg) {
        final JSONObject response = new JSONObject();
        try {
            response.put("MessageType", (Object)deviceMsg.messageType);
            response.put("Status", (Object)deviceMsg.status);
            response.put("MessageResponse", (Object)deviceMsg.messageResponse);
        }
        catch (final Exception e) {
            AuthenticatedAdminServlet.LOGGER.log(Level.SEVERE, "Exception while creating the response message", e);
        }
        return response;
    }
    
    protected DeviceMessage getErrorResponse(final String msgType, final int errorCode, final String errorMsg) throws JSONException {
        final DeviceMessage resDeviceMessage = new DeviceMessage();
        resDeviceMessage.setMessageStatus("Error");
        resDeviceMessage.setMessageType(msgType);
        final JSONObject msgResponse = new JSONObject();
        msgResponse.put("ErrorMsg", (Object)errorMsg);
        msgResponse.put("ErrorCode", errorCode);
        resDeviceMessage.setMessageResponseJSON(msgResponse);
        return resDeviceMessage;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMEnrollment");
    }
}
