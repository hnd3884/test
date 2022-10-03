package com.me.mdm.agent.servlets.chromeos;

import org.json.JSONException;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.mdm.agent.handlers.chromeos.ChromeServerMessageRequestHandler;
import com.me.mdm.agent.handlers.chromeos.ChromeServerCommandRequestHandler;
import com.me.devicemanagement.framework.webclient.common.FileUploadUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import org.json.JSONObject;
import com.me.mdm.agent.handlers.DeviceRequest;
import com.me.mdm.http.HttpException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.chrome.agent.core.communication.VirtualDeviceRequestServlet;
import com.me.mdm.agent.servlets.DeviceRequestServlet;

public class ChromeOsCheckInServlet extends DeviceRequestServlet implements VirtualDeviceRequestServlet
{
    Logger logger;
    Logger mdmdevicedatalogger;
    
    public ChromeOsCheckInServlet() {
        this.logger = Logger.getLogger("MDMLogger");
        this.mdmdevicedatalogger = Logger.getLogger("MDMDeviceDataLogger");
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            final DeviceRequest deviceRequest = this.prepareDeviceRequest(request, this.logger);
            deviceRequest.requestMap = this.getParameterValueMap(request);
            final JSONObject responseData = this.handleRequest(deviceRequest);
            SYMClientUtil.writeJsonFormattedResponse(response);
            response.getWriter().write(responseData.toString());
        }
        catch (final HttpException he) {
            this.logger.log(Level.WARNING, "ChromeOsCheckInServlet[{1}] : Exception occured while handling Android Commands.. {0}", new Object[] { he, request.getServletPath() });
            response.sendError(403, he.getMessage());
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "ChromeOsCheckInServlet[{1}] : Exception occured while handling Android Commands.. {0}", new Object[] { e, request.getServletPath() });
        }
    }
    
    @Override
    public void doVirtualPost(final VirtualHttpServletRequest request, final VirtualHttpServletResponse response) {
        try {
            final DeviceRequest deviceRequest = new DeviceRequest();
            deviceRequest.deviceRequestData = request.getRequestData();
            (deviceRequest.requestMap = request.getParams()).put("ServletPath", "in_server_request");
            final JSONObject responseData = this.handleRequest(deviceRequest);
            response.writeResponse(responseData.toString());
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "ChromeOsCheckInServlet[] : Exception occured while handling Android Commands.. {0}", new Object[] { ex });
        }
    }
    
    private JSONObject handleRequest(final DeviceRequest deviceRequest) throws HttpException, Exception {
        this.logger.log(Level.INFO, "ChromeOsCheckInServlet[{0}] => (Post) Received request from Android Devices ", deviceRequest.requestMap.get("ServletPath"));
        try {
            String responseData = null;
            this.logIncomingData(deviceRequest, deviceRequest.requestMap.get("ServletPath"));
            deviceRequest.devicePlatform = 4;
            if (deviceRequest.requestMap.get("customerId") != null) {
                deviceRequest.customerID = Long.parseLong(deviceRequest.requestMap.get("customerId"));
            }
            final JSONObject requestJSON = new JSONObject((String)deviceRequest.deviceRequestData);
            final String deviceUDID = String.valueOf(requestJSON.get("UDID"));
            if (deviceUDID != null && FileUploadUtil.hasVulnerabilityInFileName(deviceUDID)) {
                this.logger.log(Level.WARNING, "ChromeOsCheckInServlet : Going to refuse request, UDID{0}", deviceUDID);
                throw new HttpException(403, "Request Refused");
            }
            final String servletPath = deviceRequest.requestMap.get("ServletPath");
            requestJSON.put("AGENT_TYPE", (Object)"7");
            deviceRequest.repositoryType = 1;
            deviceRequest.deviceRequestData = requestJSON;
            final Boolean isCommand = this.checkIfCommadStr(requestJSON);
            BaseProcessDeviceRequestHandler requestHandler;
            if (isCommand) {
                requestHandler = new ChromeServerCommandRequestHandler();
            }
            else {
                requestHandler = new ChromeServerMessageRequestHandler();
            }
            responseData = requestHandler.processRequest(deviceRequest);
            if (responseData == null) {
                final JSONObject json = new JSONObject();
                responseData = json.toString();
            }
            DMSecurityLogger.info(this.logger, "ChromeOsCheckInServlet", "doPost", "ChromeOsCheckInServlet[{1}] : Response data to the agent {0}", (Object)new Object[] { this.wrappedResponse(responseData), servletPath });
            return new JSONObject(responseData);
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private Boolean checkIfCommadStr(final JSONObject requestJSON) {
        return requestJSON.has("CommandVersion");
    }
    
    private String wrappedResponse(final String responseData) {
        JSONObject data = new JSONObject();
        try {
            data = new JSONObject(responseData);
            final JSONObject respData = (JSONObject)data.opt("MessageResponse");
            if (respData != null && !respData.isNull("ELMLicenseKey")) {
                respData.put("ELMLicenseKey", (Object)"*****");
                data.put("MessageResponse", (Object)respData);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while wrapping response content in AndroidCheckInservlet", ex);
        }
        return data.toString();
    }
    
    private void logIncomingData(final DeviceRequest devicerequest, final String servlet) throws JSONException {
        final JSONObject devRequestData = new JSONObject(devicerequest.deviceRequestData.toString());
        if (!devRequestData.isNull("ADPassword")) {
            devRequestData.put("ADPassword", (Object)"*****");
        }
        this.mdmdevicedatalogger.log(Level.INFO, "ChromeOsCheckInServlet[{1}] Received Data: {0}", new Object[] { devRequestData.toString(), servlet });
    }
}
