package com.me.mdm.agent.servlets.drp;

import java.io.IOException;
import javax.servlet.ServletException;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.mdm.server.drp.MDMRegistrationHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.DeviceAuthenticatedRequestServlet;

public class DeviceRegistrationServlet extends DeviceAuthenticatedRequestServlet
{
    public Logger logger;
    
    public DeviceRegistrationServlet() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            final String strData = (String)this.prepareDeviceRequest(request, this.logger).deviceRequestData;
            final String userAgent = request.getHeader("user-agent");
            this.logger.log(Level.INFO, "============================================================================");
            this.logger.log(Level.INFO, "DeviceRegistrationServlet (GET) Received Data: {0}", strData);
            this.logger.log(Level.INFO, "DeviceRegistrationServlet (GET) Received User-Agent: {0}", userAgent);
            this.logger.log(Level.INFO, "============================================================================");
            final JSONObject requestJSON = new JSONObject(strData);
            final String devicePlatform = requestJSON.optString("DevicePlatform", "WindowsPhone");
            final JSONObject responseJSON = MDMRegistrationHandler.getInstance(devicePlatform).processMessage(requestJSON);
            response.setContentType("application/json");
            response.getWriter().write(responseJSON.toString());
            SYMClientUtil.writeJsonFormattedResponse(response);
            this.logger.log(Level.INFO, "DeviceRegistrationServlet : Response data to the agent {0}", responseJSON.toString());
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "DeviceRegistrationServlet : Exception occured while handling messages - ", e);
        }
    }
}
