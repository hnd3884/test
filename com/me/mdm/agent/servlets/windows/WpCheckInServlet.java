package com.me.mdm.agent.servlets.windows;

import java.util.Arrays;
import com.me.mdm.agent.handlers.WpRequestHandler;
import java.net.URLDecoder;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.apache.commons.lang.StringUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.UserAuthenticatedRequestServlet;

public class WpCheckInServlet extends UserAuthenticatedRequestServlet
{
    public Logger logger;
    public static List<Integer> PURPOSE;
    
    public WpCheckInServlet() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            final String strData = (String)deviceRequest.deviceRequestData;
            this.logger.log(Level.INFO, "============================================================================");
            this.logger.log(Level.INFO, "WPCheckInServlet (GET) Received Data: {0}", strData);
            this.logger.log(Level.INFO, "============================================================================");
            final String responseData = "";
            response.setStatus(200);
            response.setBufferSize(responseData.length());
            response.getWriter().write(responseData);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "WPCheckInServlet => (GET) Exception occured : {0}", ex);
        }
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            response.setContentType("application/soap+xml");
            String logData;
            final String strData = logData = (String)deviceRequest.deviceRequestData;
            if (strData.contains("PasswordText")) {
                final String passwordText = StringUtils.substringBetween(strData, "<wsse:Password wsse:Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">", "</wsse:Password>");
                logData = strData.replace(passwordText, "##########");
            }
            if (strData.contains("zapikey")) {
                final String passwordText = StringUtils.substringBetween(strData, "<a:To s:mustUnderstand=\"1\">", "</a:To>");
                logData = logData.replace(passwordText, "##########");
            }
            this.logger.log(Level.INFO, "============================================================================");
            this.logger.log(Level.INFO, "WPCheckInServlet (POST) Received Data: {0}", logData);
            this.logger.log(Level.INFO, "============================================================================");
            deviceRequest = new DeviceRequest();
            deviceRequest.devicePlatform = 3;
            deviceRequest.deviceRequestData = strData;
            deviceRequest.deviceRequestType = "wpcheckin";
            (deviceRequest.requestMap = this.getParameterValueMap(request)).put("pathPrefix", request.getServletPath().contains("admin") ? "/admin" : "");
            if (deviceRequest.requestMap.containsKey("SerialNumber") && !MDMUtil.isStringEmpty(String.valueOf(deviceRequest.requestMap.get("SerialNumber")))) {
                deviceRequest.requestMap.put("SerialNumber", URLDecoder.decode(deviceRequest.requestMap.get("SerialNumber").toString(), "UTF-8"));
            }
            final WpRequestHandler handler = new WpRequestHandler();
            String responseData = null;
            responseData = handler.processRequest(deviceRequest);
            this.logger.log(Level.INFO, "============================================================================");
            this.logger.log(Level.INFO, "WPCheckInServlet (POST) Response Data: {0}", responseData);
            this.logger.log(Level.INFO, "============================================================================");
            response.setBufferSize(responseData.length());
            response.setContentLength(responseData.length());
            response.getWriter().write(responseData);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "WPCheckInServlet => (POST) Exception occured : {0}", ex);
        }
    }
    
    static {
        WpCheckInServlet.PURPOSE = Arrays.asList(300, 51, 301);
    }
}
