package com.me.mdm.agent.servlets.android.admin;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.android.admin.AdminAgentMessageRequestHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.UserAuthenticatedRequestServlet;

public class UserAuthenticatedAuthServlet extends UserAuthenticatedRequestServlet
{
    protected static final Logger LOGGER;
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            String responseData = null;
            final DeviceRequest deviceRequest = this.prepareDeviceRequest(request, UserAuthenticatedAuthServlet.LOGGER);
            final String strData = (String)deviceRequest.deviceRequestData;
            DMSecurityLogger.info(UserAuthenticatedAuthServlet.LOGGER, "UserAuthenticatedAuthServlet", "doPost", "AdminMessageServlet Received Data: {0}", (Object)strData);
            final JSONObject requestJSON = new JSONObject(strData);
            final String messageType = String.valueOf(requestJSON.get("MessageType"));
            JSONObject responseJSON;
            if (messageType.equals("AdminAppRegistration")) {
                try {
                    String authToken = request.getParameter("AuthToken");
                    if (MDMStringUtils.isEmpty(authToken)) {
                        authToken = request.getParameter("zapikey");
                    }
                    final Long loginId = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().authenticateUser(authToken);
                    requestJSON.put("LoginId", (Object)loginId);
                    requestJSON.put("AuthToken", (Object)authToken);
                    deviceRequest.deviceRequestData = requestJSON;
                    responseData = new AdminAgentMessageRequestHandler().processRequest(deviceRequest);
                    if (responseData == null) {
                        responseJSON = new JSONObject();
                    }
                    else {
                        responseJSON = new JSONObject(responseData);
                    }
                }
                catch (final SecurityException se) {
                    responseJSON = this.constructAndroidMessage(this.getErrorResponse(messageType, 210001, se.getMessage()));
                }
                catch (final Exception e) {
                    UserAuthenticatedAuthServlet.LOGGER.log(Level.WARNING, "Unexpected Failure", e);
                    responseJSON = this.constructAndroidMessage(this.getErrorResponse(messageType, 210002, "Internal Server Error"));
                }
            }
            else {
                responseJSON = this.constructAndroidMessage(this.getErrorResponse(messageType, 210001, "Device not authenticated to perform request!"));
            }
            response.setContentType("application/json");
            UserAuthenticatedAuthServlet.LOGGER.log(Level.INFO, "AdminMessageServlet Response Data: {0}", responseJSON.toString());
            response.getWriter().println(responseJSON.toString());
        }
        catch (final Exception e2) {
            UserAuthenticatedAuthServlet.LOGGER.log(Level.WARNING, "AdminCommandServlet : Exception occured while handling messages - ", e2);
        }
    }
    
    protected JSONObject constructAndroidMessage(final DeviceMessage deviceMsg) {
        final JSONObject response = new JSONObject();
        try {
            response.put("MessageType", (Object)deviceMsg.messageType);
            response.put("Status", (Object)deviceMsg.status);
            response.put("MessageResponse", (Object)deviceMsg.messageResponse);
        }
        catch (final Exception e) {
            UserAuthenticatedAuthServlet.LOGGER.log(Level.SEVERE, "Exception while creating the response message", e);
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
