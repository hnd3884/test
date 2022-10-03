package com.me.mdm.agent.servlets.windows;

import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;
import com.me.mdm.agent.handlers.windows.WpAppMessageRequestHandler;
import com.me.mdm.agent.handlers.windows.WpAppCommandRequestHandler;
import com.me.devicemanagement.framework.webclient.common.FileUploadUtil;
import org.json.JSONObject;
import com.google.json.JsonSanitizer;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.DeviceAuthenticatedRequestServlet;

public class WpNativeAppServlet extends DeviceAuthenticatedRequestServlet
{
    public static Logger logger;
    public static Logger mdmdevicedatalogger;
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, DeviceRequest deviceRequest) throws ServletException, IOException {
        try {
            WpNativeAppServlet.logger.log(Level.INFO, "WindowsNativeAppServlet =>  Received request from WindowsPhone agent ");
            String responseData = null;
            if (deviceRequest == null) {
                WpNativeAppServlet.logger.log(Level.WARNING, "Device Request null in {0}", WpNativeAppServlet.class.getName());
                deviceRequest = this.prepareDeviceRequest(request, WpNativeAppServlet.mdmdevicedatalogger);
            }
            WpNativeAppServlet.mdmdevicedatalogger.log(Level.INFO, "WpNativeAppServlet Received Data: {0}", deviceRequest.deviceRequestData);
            String strData = null;
            if (!deviceRequest.deviceRequestData.equals("")) {
                deviceRequest.deviceRequestData = JsonSanitizer.sanitize((String)deviceRequest.deviceRequestData);
                strData = (String)deviceRequest.deviceRequestData;
            }
            if (strData != null) {
                final String UDID = String.valueOf(new JSONObject(strData).get("UDID"));
                if (UDID != null && FileUploadUtil.hasVulnerabilityInFileName(UDID)) {
                    WpNativeAppServlet.logger.log(Level.WARNING, "WpNativeAppServlet : Going to refuse request, UDID{0}", UDID);
                    response.sendError(403, "Request Refused");
                    return;
                }
                deviceRequest.initDeviceRequest(String.valueOf(new JSONObject(strData).get("UDID")));
                deviceRequest.requestMap = this.getParameterValueMap(request);
                deviceRequest.repositoryType = 2;
                final Boolean isCommand = this.checkIfCommandStr((String)deviceRequest.deviceRequestData);
                BaseProcessDeviceRequestHandler requestHandler = null;
                deviceRequest.deviceRequestType = "wpnativeapp";
                if (isCommand) {
                    requestHandler = new WpAppCommandRequestHandler();
                }
                else {
                    requestHandler = new WpAppMessageRequestHandler();
                }
                responseData = requestHandler.processRequest(deviceRequest);
            }
            if (responseData == null) {
                final JSONObject json = new JSONObject();
                responseData = json.toString();
            }
            response.setHeader("Content-Type", "application/json");
            response.getWriter().write(responseData);
            WpNativeAppServlet.mdmdevicedatalogger.log(Level.INFO, "WpNativeAppServlet : Response data to the agent is {0}", responseData);
        }
        catch (final Exception e) {
            WpNativeAppServlet.logger.log(Level.WARNING, "WpNativeAppServlet : Exception occured while handling Native Agent Commands.. ", e);
        }
    }
    
    private Boolean checkIfCommandStr(final String strData) {
        return strData.contains("Idle") || strData.contains("CommandVersion");
    }
    
    static {
        WpNativeAppServlet.logger = Logger.getLogger("MDMLogger");
        WpNativeAppServlet.mdmdevicedatalogger = Logger.getLogger("MDMDeviceDataLogger");
    }
}
