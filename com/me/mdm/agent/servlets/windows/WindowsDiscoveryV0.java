package com.me.mdm.agent.servlets.windows;

import java.util.HashMap;
import com.adventnet.iam.security.SecurityUtil;
import org.apache.commons.lang.StringUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.agent.servlets.DeviceRequestServlet;

public class WindowsDiscoveryV0 extends DeviceRequestServlet
{
    public Logger logger;
    
    public WindowsDiscoveryV0() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            final DeviceRequest devicerequest = this.prepareDeviceRequest(request, Logger.getLogger("MDMEnrollment"));
            final String strData = (String)devicerequest.deviceRequestData;
            this.logger.log(Level.INFO, "============================================================================");
            this.logger.log(Level.INFO, "WindowsDiscoveryV0 (GET) Received Data: {0}", strData);
            this.logger.log(Level.INFO, "============================================================================");
            final String responseData = "";
            response.setStatus(200);
            response.getWriter().write(responseData);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "WpDiscoverServlet.java => (GET) Exception occured : {0}", ex);
        }
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setContentType("application/soap+xml");
            String strData = null;
            final DeviceRequest deviceRequest = this.prepareDeviceRequest(request, Logger.getLogger("MDMEnrollment"));
            strData = (String)deviceRequest.deviceRequestData;
            final HashMap requestMap = deviceRequest.requestMap;
            if (strData.contains("zapikey")) {
                final String passwordText = StringUtils.substringBetween(strData, "<a:To s:mustUnderstand=\"1\">", "</a:To>");
                strData = strData.replace(passwordText, "##########");
            }
            this.logger.log(Level.INFO, "============================================================================");
            this.logger.log(Level.INFO, "WindowsDiscoveryV0 (POST) Received Data: {0}", strData);
            this.logger.log(Level.INFO, "============================================================================");
            final DeviceRequest devicerequest = new DeviceRequest();
            devicerequest.devicePlatform = 3;
            devicerequest.deviceRequestData = strData;
            devicerequest.requestMap = requestMap;
            devicerequest.deviceRequestType = "wpdiscover";
            devicerequest.requestMap = this.getParameterValueMap(request);
            String requestURL = SecurityUtil.getRequestPath(request);
            if (SecurityUtil.getRequestPath(request).contains("/v0/")) {
                final String encApiKey = request.getParameter("dencapiKey");
                requestURL = requestURL.split("\\?")[0] + "?encapiKey=" + encApiKey;
                requestURL = requestURL.replace("/v0/", "/v2/");
                response.setStatus(307);
                response.addHeader("Location", requestURL);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "WpDiscoverServlet.java => (POST) Exception occured : {0}", ex);
        }
    }
}
