package com.me.mdm.agent.servlets.windows;

import java.util.Arrays;
import java.util.HashMap;
import com.me.mdm.agent.handlers.WpRequestHandler;
import com.me.mdm.server.enrollment.MDMEnrollmentRequestHandler;
import com.adventnet.iam.security.SecurityUtil;
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

public class WpDiscoverServlet extends UserAuthenticatedRequestServlet
{
    public Logger logger;
    public static List<Integer> PURPOSE;
    
    public WpDiscoverServlet() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            final String strData = (String)deviceRequest.deviceRequestData;
            this.logger.log(Level.INFO, "============================================================================");
            this.logger.log(Level.INFO, "WpDiscoverServlet (GET) Received Data: {0}", strData);
            this.logger.log(Level.INFO, "============================================================================");
            final String responseData = "";
            response.setStatus(200);
            response.getWriter().write(responseData);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "WpDiscoverServlet.java => (GET) Exception occured : {0}", ex);
        }
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            response.setContentType("application/soap+xml");
            String strData = null;
            strData = (String)deviceRequest.deviceRequestData;
            final HashMap requestMap = deviceRequest.requestMap;
            if (strData.contains("zapikey")) {
                final String passwordText = StringUtils.substringBetween(strData, "<a:To s:mustUnderstand=\"1\">", "</a:To>");
                strData = strData.replace(passwordText, "##########");
            }
            this.logger.log(Level.INFO, "============================================================================");
            this.logger.log(Level.INFO, "WpDiscoverServlet (POST) Received Data: {0}", strData);
            this.logger.log(Level.INFO, "============================================================================");
            final DeviceRequest devicerequest = new DeviceRequest();
            devicerequest.devicePlatform = 3;
            devicerequest.deviceRequestData = strData;
            devicerequest.requestMap = requestMap;
            devicerequest.deviceRequestType = "wpdiscover";
            devicerequest.requestMap = this.getParameterValueMap(request);
            final String requestURL = SecurityUtil.getRequestPath(request);
            final String erid = devicerequest.requestMap.get("erid");
            if (erid != null && !erid.equals("null")) {
                devicerequest.requestMap.put("ServletPath", devicerequest.requestMap.get("ServletPath") + "/" + MDMEnrollmentRequestHandler.getInstance().getCustomerIDForEnrollmentRequest(Long.valueOf(erid)));
            }
            devicerequest.requestMap.put("pathPrefix", request.getServletPath().contains("admin") ? "/admin" : "");
            devicerequest.requestMap.put("requestURL", requestURL + ((request.getQueryString() != null) ? ("?" + request.getQueryString().trim()) : ""));
            final WpRequestHandler handler = new WpRequestHandler();
            String responseData = "";
            responseData = handler.processRequest(devicerequest);
            this.logger.log(Level.INFO, "============================================================================");
            if (responseData.contains("zapikey")) {
                final String passwordText2 = StringUtils.substringBetween(responseData, "<EnrollmentPolicyServiceUrl>", "</EnrollmentPolicyServiceUrl>");
                final String loggerResponse = responseData.replace(passwordText2, "##########");
                this.logger.log(Level.INFO, "WpDiscoverServlet (POST) Response Data: {0}", loggerResponse);
            }
            else {
                this.logger.log(Level.INFO, "WpDiscoverServlet (POST) Response Data: {0}", responseData);
            }
            this.logger.log(Level.INFO, "============================================================================");
            response.setContentLength(responseData.length());
            response.getWriter().write(responseData);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "WpDiscoverServlet.java => (POST) Exception occured : {0}", ex);
        }
    }
    
    static {
        WpDiscoverServlet.PURPOSE = Arrays.asList(300, 51, 301);
    }
}
