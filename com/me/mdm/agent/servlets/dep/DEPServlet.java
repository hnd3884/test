package com.me.mdm.agent.servlets.dep;

import java.io.IOException;
import javax.servlet.ServletException;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.adventnet.iam.security.SecurityUtil;
import com.me.mdm.server.dep.AdminEnrollmentHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.UserAuthenticatedRequestServlet;

public class DEPServlet extends UserAuthenticatedRequestServlet
{
    public static final Logger LOGGER;
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            final String strData = (String)deviceRequest.deviceRequestData;
            DEPServlet.LOGGER.log(Level.INFO, "DEPServlet Received Data: {0}", strData);
            final JSONObject requestJSON = new JSONObject(strData);
            final AdminEnrollmentHandler dep = new AdminEnrollmentHandler();
            final String requestURI = SecurityUtil.getRequestPath(request);
            final JSONObject responseJSON = dep.processMessage(requestJSON);
            response.setContentType("application/json");
            DMSecurityLogger.info(DEPServlet.LOGGER, "DEPServlet", "doPost", "DEPServlet Response Data: {0}", (Object)responseJSON.toString());
            response.getWriter().println(responseJSON.toString());
        }
        catch (final Exception e) {
            DEPServlet.LOGGER.log(Level.WARNING, "DeviceRegistrationServlet : Exception occured while handling messages - ", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMEnrollment");
    }
}
