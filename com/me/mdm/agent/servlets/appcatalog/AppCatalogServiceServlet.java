package com.me.mdm.agent.servlets.appcatalog;

import com.me.mdm.core.auth.APIKey;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import java.util.Map;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.acp.MDMAppCatalogHandler;
import com.me.mdm.server.acp.IOSAppCatalogHandler;
import org.json.JSONObject;
import com.google.json.JsonSanitizer;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.DeviceAuthenticatedRequestServlet;

public class AppCatalogServiceServlet extends DeviceAuthenticatedRequestServlet
{
    public Logger logger;
    public Logger accesslogger;
    
    public AppCatalogServiceServlet() {
        this.logger = Logger.getLogger("MDMAppCatalogLogger");
        this.accesslogger = Logger.getLogger("MDMAppCatalogAccess");
    }
    
    public void processRequest(final HttpServletRequest request, final HttpServletResponse response, DeviceRequest deviceRequest) {
        try {
            if (deviceRequest == null) {
                this.logger.log(Level.WARNING, "Device Request null in {0}", AppCatalogServiceServlet.class.getName());
                deviceRequest = this.prepareDeviceRequest(request, this.logger);
            }
            final String rawData = deviceRequest.deviceRequestData.toString();
            final String strData = JsonSanitizer.sanitize(rawData);
            final JSONObject requestJSON = new JSONObject(strData);
            final String devDetailsForLogging = requestJSON.optString("UDID") + "\t" + requestJSON.optString("DevicePlatform");
            String accessMessage = "Message Received: " + requestJSON.optString("MsgRequestType") + "\t" + devDetailsForLogging;
            this.accesslogger.log(Level.INFO, accessMessage);
            this.logger.log(Level.INFO, strData);
            JSONObject responseJSON = new JSONObject();
            MDMAppCatalogHandler appCatalogHandler = null;
            if (requestJSON.optString("DevicePlatform").equalsIgnoreCase("IOS")) {
                appCatalogHandler = new IOSAppCatalogHandler();
            }
            else {
                appCatalogHandler = new MDMAppCatalogHandler();
            }
            responseJSON = appCatalogHandler.processMessage(requestJSON);
            responseJSON = JSONUtil.getInstance().convertLongToString(responseJSON);
            String responseData = null;
            final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().getAPIKeyFromMap(this.getParameterValueMap(request));
            if (key != null) {
                responseData = MDMDeviceAPIKeyGenerator.getInstance().replaceDeviceAPIKeyPlaceHolder(responseJSON.toString(), key, false, requestJSON.get("UDID").toString());
            }
            if (requestJSON.optString("DevicePlatform") != null && requestJSON.optString("DevicePlatform").equalsIgnoreCase("IOS")) {
                responseData = new IOSAppCatalogHandler().addRefetchConfigDetails(responseData, requestJSON.get("UDID").toString());
            }
            accessMessage = "Message Sent: " + responseJSON.optString("MsgResponseType") + "\t" + devDetailsForLogging;
            DMSecurityLogger.info(this.accesslogger, "AppCatalogServiceServlet", "processRequestForDEPToken", "{0}", (Object)accessMessage);
            DMSecurityLogger.info(this.logger, "AppCatalogServiceServlet", "processRequestForDEPToken", "{0}", (Object)accessMessage);
            SYMClientUtil.writeJsonFormattedResponse(response);
            final JSONObject resonseJSON = new JSONObject(responseData);
            response.getWriter().write(resonseJSON.toString());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error in AppCatalogServiceServlet processRequestForDEPToken() ", ex);
        }
    }
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) {
        try {
            this.logger.log(Level.INFO, "AppCatalogServiceServlet => Received data -> doGet()");
            this.processRequest(request, response, deviceRequest);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error in AppCatalogServiceServlet doGet() ", ex);
        }
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) {
        try {
            this.logger.log(Level.INFO, "AppCatalogServiceServlet => Received data -> doGet()");
            this.processRequest(request, response, deviceRequest);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Error in AppCatalogServiceServlet doPost() ", ex);
        }
    }
}
