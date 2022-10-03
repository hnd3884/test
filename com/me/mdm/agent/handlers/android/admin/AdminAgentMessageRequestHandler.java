package com.me.mdm.agent.handlers.android.admin;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Iterator;
import java.util.List;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.common.MDMEventConstant;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.core.enrollment.AdminDeviceHandler;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.logging.Logger;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;

public class AdminAgentMessageRequestHandler extends BaseProcessDeviceRequestHandler
{
    public static final Logger LOGGER;
    
    @Override
    public String processRequest(final DeviceRequest request) throws Exception {
        String responseData = null;
        final JSONObject requestJSON = (JSONObject)request.deviceRequestData;
        final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(requestJSON);
        hmap.put("PlatformType", String.valueOf(2));
        final String messageType = hmap.get("MessageType");
        DeviceMessage deviceMsg = null;
        if (messageType.equalsIgnoreCase("AdminLogout")) {
            deviceMsg = this.processAdminLogoutMessage(requestJSON);
        }
        else if (messageType.equalsIgnoreCase("SyncData")) {
            deviceMsg = this.processSyncMessage(requestJSON);
        }
        else if (messageType.equalsIgnoreCase("DownloadDetailsAcquisition")) {
            deviceMsg = this.processDownloadDetailsMessage(requestJSON);
        }
        else if (messageType.equalsIgnoreCase("AdminAppRegistration")) {
            deviceMsg = this.processRegistrationMessage(requestJSON);
        }
        else {
            final int dataQueueType = 125;
            this.addResponseToQueue(request, requestJSON.toString(), dataQueueType);
        }
        if (deviceMsg != null) {
            final JSONObject responseMsgJSON = this.constructAndroidMessage(deviceMsg);
            responseData = responseMsgJSON.toString();
        }
        return responseData;
    }
    
    public DeviceMessage processRegistrationMessage(final JSONObject requestJSON) throws JSONException, DataAccessException {
        final String messageType = "AdminAppRegistration";
        final DeviceMessage responseMsg = new DeviceMessage();
        responseMsg.setMessageType(messageType);
        final JSONObject msgResponseJSON = new JSONObject();
        try {
            final JSONObject msgRequestJSON = requestJSON.getJSONObject("Message");
            final JSONObject deviceDetailsJSON = msgRequestJSON.getJSONObject("DeviceDetails");
            final JSONObject agentDetailsJSON = msgRequestJSON.getJSONObject("AgentDetails");
            final String udid = String.valueOf(requestJSON.get("UDID"));
            final String imei = String.valueOf(deviceDetailsJSON.get("IMEI"));
            final String serialNo = String.valueOf(deviceDetailsJSON.get("SerialNumber"));
            final String modelName = String.valueOf(deviceDetailsJSON.get("ModelName"));
            final String version = String.valueOf(agentDetailsJSON.get("AgentVersion"));
            final Integer versionCode = agentDetailsJSON.getInt("AgentVersionCode");
            final String authToken = String.valueOf(requestJSON.get("AuthToken"));
            final Long loginId = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().authenticateUser(authToken);
            final JSONObject deviceInfo = new JSONObject();
            deviceInfo.put("UDID", (Object)udid);
            deviceInfo.put("LOGIN_ID", (Object)loginId);
            deviceInfo.put("IMEI", (Object)imei);
            deviceInfo.put("SERIAL_NUMBER", (Object)serialNo);
            deviceInfo.put("MODEL_NAME", (Object)modelName);
            deviceInfo.put("AGENT_VERSION", (Object)version);
            deviceInfo.put("AGENT_VERSION_CODE", (Object)versionCode);
            new AdminDeviceHandler().addOrUpdateAdminDevice(deviceInfo);
            responseMsg.setMessageStatus("Acknowledged");
        }
        catch (final SecurityException se) {
            responseMsg.setMessageStatus("Error");
            msgResponseJSON.put("ErrorMsg", (Object)se.getMessage());
            msgResponseJSON.put("ErrorCode", 210001);
        }
        catch (final Exception e) {
            AdminAgentMessageRequestHandler.LOGGER.log(Level.WARNING, "Unexpected Failure", e);
            responseMsg.setMessageStatus("Error");
            msgResponseJSON.put("ErrorMsg", (Object)"Internal Server Error");
            msgResponseJSON.put("ErrorCode", 210002);
        }
        responseMsg.setMessageResponseJSON(msgResponseJSON);
        return responseMsg;
    }
    
    JSONObject constructAndroidMessage(final DeviceMessage deviceMsg) {
        final JSONObject response = new JSONObject();
        try {
            response.put("MessageType", (Object)deviceMsg.messageType);
            response.put("Status", (Object)deviceMsg.status);
            response.put("MessageResponse", (Object)deviceMsg.messageResponse);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while creating the response message", e);
        }
        return response;
    }
    
    @Override
    protected String getNextDeviceCommandQuery(final DeviceCommand nextCommand, final DeviceRequest request) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    DeviceMessage processAdminLogoutMessage(final JSONObject requestJSON) throws Exception {
        final String udid = String.valueOf(requestJSON.get("UDID"));
        final Long loginId = requestJSON.getLong("LoginId");
        final AdminDeviceHandler handler = new AdminDeviceHandler();
        final String modelName = handler.getAdminDeviceModelName(udid);
        handler.removeAdminDevice(udid);
        final DeviceMessage responseMsg = new DeviceMessage();
        responseMsg.setMessageType("AdminLogout");
        responseMsg.setMessageStatus("Acknowledged");
        final Object remarksArgs = DMUserHandler.getDCUser(loginId) + "@@@" + DMUserHandler.getRoleForUser(loginId) + "@@@" + modelName;
        final String i18n = "dc.mdm.adminagent.host_disconnected_msg";
        MDMEventLogHandler.getInstance().MDMEventLogEntry(121, null, MDMEventConstant.DC_SYSTEM_USER, i18n, remarksArgs, CustomerInfoUtil.getInstance().getDefaultCustomer());
        return responseMsg;
    }
    
    DeviceMessage processSyncMessage(final JSONObject requestJSON) throws Exception {
        final String lastSyncTimeKEY = "LastSyncTime";
        final String udid = String.valueOf(requestJSON.get("UDID"));
        final Long loginId = requestJSON.getLong("LoginId");
        final JSONObject requestMSG = requestJSON.getJSONObject("Message");
        final Long lastSyncTimeFromAgent = requestMSG.getLong(lastSyncTimeKEY);
        final AdminDeviceHandler adminDeviceHandler = new AdminDeviceHandler();
        final Long lastSyncTimeFromServer = adminDeviceHandler.getLastSyncTime(udid);
        final DeviceMessage responseMsg = new DeviceMessage();
        responseMsg.setMessageType("SyncData");
        responseMsg.setMessageStatus("Acknowledged");
        final JSONObject responseJSON = new JSONObject();
        if (!lastSyncTimeFromServer.equals(-1L) && !lastSyncTimeFromAgent.equals(lastSyncTimeFromServer)) {
            final SelectQuery sQuery = (SelectQuery)new SelectQueryImpl(new Table("AndroidAdminEnrollmentTemplate"));
            final Join enrollmentTemplateJoin = new Join("AndroidAdminEnrollmentTemplate", "EnrollmentTemplate", new String[] { "TEMPLATE_ID" }, new String[] { "TEMPLATE_ID" }, 2);
            sQuery.addJoin(enrollmentTemplateJoin);
            sQuery.setCriteria(new Criteria(new Column("AndroidAdminEnrollmentTemplate", "LOGIN_ID"), (Object)loginId, 0));
            sQuery.addSelectColumn(new Column((String)null, "*"));
            final DataObject dO = MDMUtil.getPersistence().get(sQuery);
            if (!dO.isEmpty()) {
                final List<HashMap> customerList = CustomerInfoUtil.getInstance().getCustomerDetailsForUser(DMUserHandler.getDCUserID(loginId));
                final JSONArray responseArray = new JSONArray();
                for (int i = 0; i < customerList.size(); ++i) {
                    final Long customerId = customerList.get(i).get("CUSTOMER_ID");
                    final JSONObject customerJSON = new JSONObject();
                    customerJSON.put("CustomerID", (Object)customerId);
                    customerJSON.put("CustomerName", (Object)customerList.get(i).get("CUSTOMER_NAME"));
                    final JSONArray templateArray = new JSONArray();
                    final Iterator it = dO.getRows("EnrollmentTemplate", new Criteria(new Column("EnrollmentTemplate", "CUSTOMER_ID"), (Object)customerId, 0));
                    while (it.hasNext()) {
                        final Row enrollRow = it.next();
                        final JSONObject templateJSON = new JSONObject();
                        templateJSON.put("TemplateID", (Object)enrollRow.get("TEMPLATE_ID"));
                        templateJSON.put("TemplateName", (Object)enrollRow.get("TEMPLATE_NAME"));
                        templateJSON.put("TemplateToken", (Object)enrollRow.get("TEMPLATE_TOKEN"));
                        templateArray.put((Object)templateJSON);
                    }
                    customerJSON.put("TemplateDetails", (Object)templateArray);
                    responseArray.put((Object)customerJSON);
                }
                responseJSON.put("CustomerDetails", (Object)responseArray);
            }
            else {
                responseJSON.put("ErrorMsg", (Object)"No Customer/No Template Token Found");
                responseJSON.put("ErrorCode", -4001);
                responseMsg.setMessageStatus("Error");
            }
            responseJSON.put(lastSyncTimeKEY, (Object)lastSyncTimeFromServer);
            responseMsg.setMessageResponseJSON(responseJSON);
        }
        return responseMsg;
    }
    
    DeviceMessage processDownloadDetailsMessage(final JSONObject requestJSON) throws Exception {
        final DeviceMessage responseMsg = new DeviceMessage();
        responseMsg.setMessageType("DownloadDetailsAcquisition");
        final JSONObject msgResponseJSON = new JSONObject();
        msgResponseJSON.put("KNOXAgentDownloadURL", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getAgentDownloadUrl(2, 6));
        msgResponseJSON.put("AndroidAgentDownloadURL", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getAgentDownloadUrl(2, 6));
        msgResponseJSON.put("ModeOfDownload", (Object)"HTTPS");
        msgResponseJSON.put("AgentDownloadURL", (Object)MDMApiFactoryProvider.getMDMUtilAPI().getAgentDownloadUrl(2, 2));
        final JSONObject serverDetails = new JSONObject();
        serverDetails.put("NATAddress", (Object)((Hashtable<K, String>)ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties()).get("NAT_ADDRESS"));
        serverDetails.put("ServerPort", (Object)((Hashtable<K, Integer>)ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties()).get("NAT_HTTPS_PORT"));
        msgResponseJSON.put("ServerDetails", (Object)serverDetails);
        responseMsg.setMessageStatus("Acknowledged");
        responseMsg.setMessageResponseJSON(msgResponseJSON);
        return responseMsg;
    }
    
    static {
        LOGGER = Logger.getLogger("MDMEnrollment");
    }
}
