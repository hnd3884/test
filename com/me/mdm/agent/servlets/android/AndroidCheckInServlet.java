package com.me.mdm.agent.servlets.android;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.android.payload.AndroidCommandPayload;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayloadHandler;
import com.adventnet.sym.server.mdm.DeviceDetails;
import org.json.JSONException;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.core.auth.APIKey;
import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import java.util.Map;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.agent.handlers.android.AndroidServerMessageRequestHandler;
import com.me.mdm.agent.handlers.android.AndroidServerCommandRequestHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.agent.handlers.android.servletmigration.AndroidServletMigrationUtil;
import com.me.devicemanagement.framework.webclient.common.FileUploadUtil;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.DeviceAuthenticatedRequestServlet;

public class AndroidCheckInServlet extends DeviceAuthenticatedRequestServlet
{
    Logger logger;
    Logger mdmdevicedatalogger;
    
    public AndroidCheckInServlet() {
        this.logger = Logger.getLogger("MDMLogger");
        this.mdmdevicedatalogger = Logger.getLogger("MDMDeviceDataLogger");
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response, DeviceRequest deviceRequest) throws ServletException, IOException {
        this.logger.log(Level.INFO, "AndroidCheckInServlet[{0}] => (Post) Received request from Android Devices ", request.getServletPath());
        try {
            String responseData = null;
            if (deviceRequest == null) {
                this.logger.log(Level.WARNING, "Device Request null in {0}", AndroidCheckInServlet.class.getName());
                deviceRequest = this.prepareDeviceRequest(request, this.logger);
            }
            final DeviceRequest devicerequest = deviceRequest;
            this.logIncomingData(devicerequest, request.getServletPath());
            devicerequest.requestMap = this.getParameterValueMap(request);
            devicerequest.devicePlatform = 2;
            if (devicerequest.requestMap.get("customerId") != null) {
                devicerequest.customerID = Long.parseLong(devicerequest.requestMap.get("customerId"));
            }
            final JSONObject requestJSON = new JSONObject((String)devicerequest.deviceRequestData);
            final String deviceUDID = String.valueOf(requestJSON.get("UDID"));
            if (deviceUDID != null && FileUploadUtil.hasVulnerabilityInFileName(deviceUDID)) {
                this.logger.log(Level.WARNING, "AndroidCheckInServlet : Going to refuse request, UDID{0}", deviceUDID);
                response.sendError(403, "Request Refused");
                return;
            }
            final String servletPath = request.getServletPath();
            if (servletPath.contains("androidcheckin")) {
                requestJSON.put("AGENT_TYPE", (Object)"2");
                devicerequest.repositoryType = 1;
            }
            else if (servletPath.contains("safecheckin")) {
                requestJSON.put("AGENT_TYPE", (Object)"3");
                devicerequest.repositoryType = 1;
            }
            else if (servletPath.contains("androidnativeapp")) {
                requestJSON.put("AGENT_TYPE", (Object)"2");
                devicerequest.repositoryType = 2;
            }
            final AndroidServletMigrationUtil migrationUtil = new AndroidServletMigrationUtil();
            try {
                final Long resId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
                if (resId != null) {
                    migrationUtil.checkAndAddMigrationCommand(resId, request.getServletPath());
                }
                else {
                    this.logger.log(Level.INFO, " Could not fetch resource id for the device with UDID{0}", deviceUDID);
                }
            }
            catch (final Exception exp) {
                this.logger.log(Level.SEVERE, " Error while checking and adding migration command", exp);
            }
            devicerequest.deviceRequestData = requestJSON;
            final Boolean isCommand = this.checkIfCommadStr(requestJSON);
            if (isCommand) {
                final AndroidServerCommandRequestHandler requestHandler = new AndroidServerCommandRequestHandler();
                responseData = requestHandler.processRequest(devicerequest);
            }
            else {
                final AndroidServerMessageRequestHandler requestHandler2 = new AndroidServerMessageRequestHandler();
                responseData = requestHandler2.processRequest(devicerequest);
            }
            if (responseData == null) {
                final JSONObject json = new JSONObject();
                responseData = json.toString();
            }
            final APIKey apiKey = MDMDeviceAPIKeyGenerator.getInstance().getAPIKeyFromMap(this.getParameterValueMap(request));
            responseData = MDMDeviceAPIKeyGenerator.getInstance().replaceDeviceAPIKeyPlaceHolder(responseData, apiKey, false, deviceUDID);
            SYMClientUtil.writeJsonFormattedResponse(response);
            final JSONObject responseDataJSON = new JSONObject(responseData);
            response.setContentType("application/json");
            response.getWriter().write(responseDataJSON.toString());
            DMSecurityLogger.info(this.mdmdevicedatalogger, "AndroidCheckInServlet", "doPost", "AndroidCheckInServlet Response data to the agent {0}", (Object)this.wrappedResponse(responseData));
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "AndroidCheckInServlet[{1}] : Exception occured while handling Android Commands.. {0}", new Object[] { e, request.getServletPath() });
            e.printStackTrace();
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
        final String obj = (String)devicerequest.deviceRequestData;
        final JSONObject devRequestData = new JSONObject(obj);
        if (!devRequestData.isNull("ADPassword")) {
            devRequestData.put("ADPassword", (Object)"*****");
        }
        DMSecurityLogger.info(this.mdmdevicedatalogger, "AndroidCheckInServlet", "logIncomingData", "AndroidCheckInServlet Received Data: {0}", (Object)devRequestData.toString());
    }
    
    protected void handleImproperlyAuthenticatedRequest(final HttpServletRequest request, final HttpServletResponse response, DeviceRequest deviceRequest) throws ServletException, IOException {
        this.logger.log(Level.INFO, "AndroidCheckInServlet[{0}] => (Post) Received request from Android Devices with Improper Authentication", request.getServletPath());
        try {
            String responseData = null;
            if (deviceRequest == null) {
                this.logger.log(Level.WARNING, "Device Request null in {0}", AndroidCheckInServlet.class.getName());
                deviceRequest = this.prepareDeviceRequest(request, this.logger);
            }
            final DeviceRequest devicerequest = deviceRequest;
            this.logIncomingData(devicerequest, request.getServletPath());
            devicerequest.requestMap = this.getParameterValueMap(request);
            devicerequest.devicePlatform = 2;
            if (devicerequest.requestMap.get("customerId") != null) {
                devicerequest.customerID = Long.parseLong(devicerequest.requestMap.get("customerId"));
            }
            final JSONObject requestJSON = new JSONObject((String)devicerequest.deviceRequestData);
            final String deviceUDID = String.valueOf(requestJSON.get("UDID"));
            if (deviceUDID != null && FileUploadUtil.hasVulnerabilityInFileName(deviceUDID)) {
                this.logger.log(Level.WARNING, "AndroidCheckInServlet : Going to refuse request, UDID{0}", deviceUDID);
                response.sendError(403, "Request Refused");
                return;
            }
            final String servletPath = request.getServletPath();
            if (servletPath.contains("androidcheckin")) {
                requestJSON.put("AGENT_TYPE", (Object)"2");
                devicerequest.repositoryType = 1;
            }
            else if (servletPath.contains("safecheckin")) {
                requestJSON.put("AGENT_TYPE", (Object)"3");
                devicerequest.repositoryType = 1;
            }
            else if (servletPath.contains("androidnativeapp")) {
                requestJSON.put("AGENT_TYPE", (Object)"2");
                devicerequest.repositoryType = 2;
            }
            devicerequest.deviceRequestData = requestJSON;
            final Boolean isCommand = this.checkIfCommadStr(requestJSON);
            if (isCommand) {
                final String requestStatus = requestJSON.optString("Status", (String)null);
                if (requestStatus == null || !requestStatus.equals("Idle")) {
                    response.sendError(403, "Request Refused");
                    return;
                }
                AndroidCommandPayload createAgentUpgradeCommand = null;
                final DeviceDetails device = new DeviceDetails(deviceUDID);
                createAgentUpgradeCommand = AndroidPayloadHandler.getInstance().createAgentUpgradeCommand(devicerequest, device.agentType);
                createAgentUpgradeCommand.setCommandUUID("AgentUpgrade", false);
                responseData = createAgentUpgradeCommand.toString();
            }
            else {
                final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(requestJSON);
                final String messageType = hmap.get("MessageType");
                if (messageType == null || !messageType.equalsIgnoreCase("GetEncapiKey")) {
                    response.sendError(403, "Request Refused");
                    return;
                }
                this.logger.log(Level.WARNING, "AndroidCheckInServlet : Device with UDID:{0} asked for EncAPIKey :: Data {1}", new Object[] { deviceUDID, requestJSON });
                final JSONObject messageData = new JSONObject((String)hmap.get("Message"));
                final String udid = messageData.optString("UDID", "--");
                final String erId = messageData.optString("EnrollmentRequestId", "-1");
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DeviceEnrollmentRequest"));
                query.addJoin(new Join("DeviceEnrollmentRequest", "EnrollmentRequestToDevice", new String[] { "ENROLLMENT_REQUEST_ID" }, new String[] { "ENROLLMENT_REQUEST_ID" }, 2));
                query.addJoin(new Join("EnrollmentRequestToDevice", "ManagedDevice", new String[] { "MANAGED_DEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                query.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
                query.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
                final Criteria eridCriteria = new Criteria(Column.getColumn("DeviceEnrollmentRequest", "ENROLLMENT_REQUEST_ID"), (Object)Long.parseLong(erId), 0);
                final Criteria udidCriteria = new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)udid, 0);
                query.setCriteria(eridCriteria.and(udidCriteria));
                final DataObject DO = MDMUtil.getPersistence().get(query);
                if (DO.isEmpty()) {
                    this.logger.log(Level.WARNING, "AndroidCheckInServlet : Device with UDID{0} asked for EncAPIKey but Given ERID is wrong, so rejecting the request", deviceUDID);
                    response.sendError(403, "Request Refused");
                    return;
                }
                final DeviceMessage deviceMsg = new DeviceMessage();
                deviceMsg.status = "Acknowledged";
                deviceMsg.messageType = "GetEncapiKey";
                JSONObject responseJsonObject = new JSONObject();
                final JSONObject json = new JSONObject();
                json.put("ENROLLMENT_REQUEST_ID", (Object)erId);
                final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().generateAPIKey(json);
                if (key != null && key.getVersion() == APIKey.VERSION_2_0) {
                    responseJsonObject.put("Services", (Object)key.toClientJSON());
                }
                deviceMsg.setMessageResponseJSON(responseJsonObject);
                responseJsonObject = new JSONObject();
                responseJsonObject.put("MessageType", (Object)deviceMsg.messageType);
                responseJsonObject.put("Status", (Object)deviceMsg.status);
                responseJsonObject.put("MessageResponse", (Object)deviceMsg.messageResponse);
                responseData = responseJsonObject.toString();
            }
            if (responseData == null) {
                final JSONObject json2 = new JSONObject();
                responseData = json2.toString();
            }
            SYMClientUtil.writeJsonFormattedResponse(response);
            response.getWriter().write(responseData);
            DMSecurityLogger.info(this.mdmdevicedatalogger, "AndroidCheckInServlet", "handleImproperlyAuthenticatedRequest", "AndroidCheckInServlet[" + request.getServletPath() + "] : Response data to the agent {0}", (Object)this.wrappedResponse(responseData));
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "AndroidCheckInServlet[{1}] : Exception occured while handling Android Commands with Improper Authentication.. {0}", new Object[] { e, request.getServletPath() });
            e.printStackTrace();
        }
    }
}
