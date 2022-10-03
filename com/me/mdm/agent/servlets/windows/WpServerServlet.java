package com.me.mdm.agent.servlets.windows;

import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.WpRequestHandler;
import java.net.URLDecoder;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.util.MDMSecurityLogger;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.DeviceAuthenticatedRequestServlet;

public class WpServerServlet extends DeviceAuthenticatedRequestServlet
{
    public Logger logger;
    public Logger mdmdevicedatalogger;
    
    public WpServerServlet() {
        this.logger = Logger.getLogger("MDMConfigLogger");
        this.mdmdevicedatalogger = Logger.getLogger("MDMDeviceDataLogger");
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            if (deviceRequest == null) {
                this.logger.log(Level.WARNING, "Device Request null in {0}", WpServerServlet.class.getName());
                deviceRequest = this.prepareDeviceRequest(request, this.logger);
            }
            final String strData = new String((String)deviceRequest.deviceRequestData);
            MDMSecurityLogger.info(this.mdmdevicedatalogger, "WpServerServlet", "doPost", "WPServerServlet (POST) Received Data: {0}", strData);
            deviceRequest = new DeviceRequest();
            deviceRequest.devicePlatform = 3;
            deviceRequest.deviceRequestData = strData;
            deviceRequest.deviceRequestType = "wpserver";
            deviceRequest.requestMap = this.getParameterValueMap(request);
            if (deviceRequest.requestMap.containsKey("SerialNumber") && !MDMUtil.isStringEmpty(String.valueOf(deviceRequest.requestMap.get("SerialNumber")))) {
                deviceRequest.requestMap.put("SerialNumber", URLDecoder.decode(deviceRequest.requestMap.get("SerialNumber").toString(), "UTF-8"));
            }
            final WpRequestHandler handler = new WpRequestHandler();
            String responseData = null;
            responseData = handler.processRequest(deviceRequest);
            MDMSecurityLogger.info(this.mdmdevicedatalogger, "WpServerServlet", "doPost", "WPServerServlet (POST) Response Data: {0}", responseData);
            response.setBufferSize(responseData.length());
            response.getWriter().write(responseData);
            response.setContentType("application/vnd.syncml.dm+xml");
            response.setContentLength(responseData.length());
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "WPServerServlet => (POST) Exception occured : {0}", ex);
        }
    }
}
