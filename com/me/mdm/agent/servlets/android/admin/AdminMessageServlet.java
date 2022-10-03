package com.me.mdm.agent.servlets.android.admin;

import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import com.me.mdm.agent.handlers.android.admin.AdminAgentMessageRequestHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class AdminMessageServlet extends AuthenticatedAdminServlet
{
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String responseData = null;
        try {
            final DeviceRequest deviceRequest = this.validateRequest(request, response);
            final String strData = (String)deviceRequest.deviceRequestData;
            AdminMessageServlet.LOGGER.log(Level.INFO, "AdminMessageServlet Received Data: {0}", strData);
            final JSONObject requestJSON = new JSONObject(strData);
            final String messageType = String.valueOf(requestJSON.get("MessageType"));
            JSONObject responseJSON;
            try {
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
                AdminMessageServlet.LOGGER.log(Level.WARNING, "Unexpected Failure", e);
                responseJSON = this.constructAndroidMessage(this.getErrorResponse(messageType, 210002, "Internal Server Error"));
            }
            response.setContentType("application/json");
            AdminMessageServlet.LOGGER.log(Level.INFO, "AdminMessageServlet Response Data: {0}", responseJSON.toString());
            response.getWriter().println(responseJSON.toString());
        }
        catch (final Exception e2) {
            AdminMessageServlet.LOGGER.log(Level.WARNING, "AdminMessageServlet : Exception occured while handling messages - ", e2);
        }
    }
}
