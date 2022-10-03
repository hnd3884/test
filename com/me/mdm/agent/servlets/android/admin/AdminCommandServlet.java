package com.me.mdm.agent.servlets.android.admin;

import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import com.me.mdm.agent.handlers.android.admin.AdminAgentCommandRequestHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class AdminCommandServlet extends AuthenticatedAdminServlet
{
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            final DeviceRequest deviceRequest = this.validateRequest(request, response);
            final String strData = (String)deviceRequest.deviceRequestData;
            AdminCommandServlet.LOGGER.log(Level.INFO, "AdminCommandServlet Received Data: {0}", strData);
            final JSONObject requestJSON = new JSONObject(strData);
            final String messageType = requestJSON.optString("RequestType");
            JSONObject responseJSON;
            try {
                deviceRequest.deviceRequestData = requestJSON;
                String responseStr = new AdminAgentCommandRequestHandler().processRequest(deviceRequest);
                if (responseStr == null) {
                    responseStr = new JSONObject().toString();
                }
                responseJSON = new JSONObject(responseStr);
            }
            catch (final SecurityException se) {
                responseJSON = this.constructAndroidMessage(this.getErrorResponse(messageType, 210001, se.getMessage()));
            }
            catch (final Exception e) {
                AdminCommandServlet.LOGGER.log(Level.WARNING, "Unexpected Failure", e);
                responseJSON = this.constructAndroidMessage(this.getErrorResponse(messageType, 210002, "Internal Server Error"));
            }
            response.setContentType("application/json");
            AdminCommandServlet.LOGGER.log(Level.INFO, "AdminCommandServlet Response Data: {0}", responseJSON.toString());
            response.getWriter().println(responseJSON.toString());
        }
        catch (final Exception e2) {
            AdminCommandServlet.LOGGER.log(Level.WARNING, "AdminCommandServlet : Exception occured while handling messages - ", e2);
        }
    }
}
