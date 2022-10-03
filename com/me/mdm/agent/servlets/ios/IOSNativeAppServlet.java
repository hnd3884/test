package com.me.mdm.agent.servlets.ios;

import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;
import com.me.mdm.agent.handlers.ios.IOSAppMessageRequestHandler;
import com.me.mdm.agent.handlers.ios.IOSAppCommandRequestHandler;
import com.me.devicemanagement.framework.webclient.common.FileUploadUtil;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.google.json.JsonSanitizer;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.DeviceAuthenticatedRequestServlet;

public class IOSNativeAppServlet extends DeviceAuthenticatedRequestServlet
{
    public static Logger logger;
    public static Logger mdmdevicedatalogger;
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            IOSNativeAppServlet.logger.log(Level.INFO, "IOSNativeAppServlet =>  Received request from IOS agent ");
            String responseData = null;
            if (deviceRequest == null) {
                IOSNativeAppServlet.logger.log(Level.WARNING, "Device Request null in {0}", IOSNativeAppServlet.class.getName());
                deviceRequest = this.prepareDeviceRequest(request, IOSNativeAppServlet.logger);
            }
            final DeviceRequest devicerequest = deviceRequest;
            devicerequest.deviceRequestData = JsonSanitizer.sanitize((String)devicerequest.deviceRequestData);
            final String strData = (String)devicerequest.deviceRequestData;
            DMSecurityLogger.info(IOSNativeAppServlet.mdmdevicedatalogger, IOSNativeAppServlet.class.getName(), "doPost", "IOSNativeAppServlet Received Data: {0}", (Object)strData);
            final String UDID = String.valueOf(new JSONObject(strData).get("UDID"));
            if (UDID != null && FileUploadUtil.hasVulnerabilityInFileName(UDID)) {
                IOSNativeAppServlet.logger.log(Level.WARNING, "IOSNativeAppServlet : Going to refuse request, UDID{0}", UDID);
                response.sendError(403, "Request Refused");
                return;
            }
            devicerequest.initDeviceRequest(String.valueOf(new JSONObject(strData).get("UDID")));
            devicerequest.requestMap = this.getParameterValueMap(request);
            devicerequest.repositoryType = 2;
            final Boolean isCommand = this.checkIfCommandStr(devicerequest.deviceRequestData);
            BaseProcessDeviceRequestHandler requestHandler = null;
            devicerequest.deviceRequestType = "iosnativeapp";
            if (isCommand) {
                requestHandler = new IOSAppCommandRequestHandler();
            }
            else {
                devicerequest.headerMap = this.getHeaderValueMap(request);
                requestHandler = new IOSAppMessageRequestHandler();
            }
            responseData = requestHandler.processRequest(devicerequest);
            if (responseData == null) {
                final JSONObject json = new JSONObject();
                responseData = json.toString();
            }
            response.setHeader("Content-Type", "application/json");
            response.getWriter().write(responseData);
            DMSecurityLogger.info(IOSNativeAppServlet.mdmdevicedatalogger, "IOSNativeAppServlet", "doPost", "IOSNativeAppServlet : Response data to the agent is {0}", (Object)responseData);
        }
        catch (final Exception e) {
            IOSNativeAppServlet.logger.log(Level.WARNING, "IOSNativeAppServlet : Exception occured while handling Native Agent Commands.. ", e);
        }
    }
    
    private Boolean checkIfCommandStr(final Object deviceRequest) {
        Boolean isCommand = false;
        if (deviceRequest.toString().contains("Idle") || deviceRequest.toString().contains("idle") || deviceRequest.toString().contains("CommandVersion") || deviceRequest.toString().contains("CommandUUID")) {
            isCommand = true;
        }
        return isCommand;
    }
    
    static {
        IOSNativeAppServlet.logger = Logger.getLogger("MDMLogger");
        IOSNativeAppServlet.mdmdevicedatalogger = Logger.getLogger("MDMDeviceDataLogger");
    }
}
