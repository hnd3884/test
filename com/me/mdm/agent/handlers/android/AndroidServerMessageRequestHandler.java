package com.me.mdm.agent.handlers.android;

import java.util.Hashtable;
import org.json.JSONException;
import java.util.Iterator;
import com.adventnet.sym.server.mdm.command.DeviceCommand;
import com.me.mdm.server.util.MDMSecurityLogger;
import com.me.mdm.core.auth.APIKey;
import com.me.devicemanagement.framework.server.api.EvaluatorAPI;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.me.mdm.server.announcement.handler.AnnouncementSyncHandler;
import com.me.mdm.agent.handlers.DeviceMessageRequest;
import com.adventnet.sym.server.mdm.security.safetynet.SafetyNetHandler;
import com.adventnet.sym.server.mdm.util.ServerCertificateFetchingUtil;
import com.me.mdm.server.doc.DocMgmtDataHandler;
import com.me.mdm.server.doc.DocMgmt;
import com.me.mdm.agent.servlets.doc.DocServlet;
import com.adventnet.sym.server.mdm.terms.MDMTermsHandler;
import java.util.Map;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import com.me.mdm.server.apps.android.afw.AFWAccountRegistrationHandler;
import com.me.mdm.server.command.kiosk.KioskPauseResumeManager;
import com.me.mdm.server.remotesession.RemoteSessionManager;
import java.util.HashMap;
import com.me.mdm.server.onelinelogger.MDMOneLineLogger;
import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMEventLogHandler;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.inv.InventoryUtil;
import java.util.Properties;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayloadHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.android.AndroidEnrollment;
import com.me.mdm.api.command.schedule.ScheduledActionsMessageHandler;
import com.me.mdm.server.privacy.PrivacyDeviceMessageHandler;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.drp.MDMRegistrationHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.agent.handlers.DeviceRequest;
import java.util.logging.Logger;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;

public class AndroidServerMessageRequestHandler extends BaseProcessDeviceRequestHandler
{
    private Logger logger;
    private Logger messagesAccessLogger;
    public Logger checkinLogger;
    private String separator;
    
    public AndroidServerMessageRequestHandler() {
        this.logger = Logger.getLogger("MDMLogger");
        this.messagesAccessLogger = Logger.getLogger("MDMMessagesLogger");
        this.checkinLogger = Logger.getLogger("MDMCheckinLogger");
        this.separator = "\t";
    }
    
    @Override
    public String processRequest(final DeviceRequest request) throws Exception {
        String responseData = null;
        final JSONObject requestJSON = (JSONObject)request.deviceRequestData;
        final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(requestJSON);
        hmap.put("PlatformType", String.valueOf(2));
        hmap.put("CMDRepType", "" + request.repositoryType);
        requestJSON.put("CMDRepType", request.repositoryType);
        String messageType = null;
        if (hmap.containsKey("MsgRequestType")) {
            messageType = hmap.get("MsgRequestType");
        }
        else if (hmap.containsKey("MessageType")) {
            messageType = hmap.get("MessageType");
        }
        this.logger.log(Level.INFO, "The message type received is {0}", messageType);
        if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AndroidMsgReqReinit")) {
            request.initDeviceRequest(requestJSON.optString("UDID"));
        }
        DeviceMessage deviceMsg = null;
        final EvaluatorAPI evaluatorApi = ApiFactoryProvider.getEvaluatorAPI();
        if (messageType != null && messageType.equalsIgnoreCase("AuthMode")) {
            if (evaluatorApi != null) {
                evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Android_App_Auth_Page_Count");
            }
            return this.formatDRPJSONToAgentJSON(MDMRegistrationHandler.getInstance("Android").processMessage(this.formatAgentJSONToDRPJSON(requestJSON)));
        }
        if (messageType != null && messageType.equalsIgnoreCase("Authenticate")) {
            return this.formatDRPJSONToAgentJSON(MDMRegistrationHandler.getInstance("Android").processMessage(this.formatAgentJSONToDRPJSON(requestJSON)));
        }
        if (messageType != null && messageType.equalsIgnoreCase("RegistrationStatusUpdate")) {
            return MDMRegistrationHandler.getInstance("Android").processMessage(this.formatAgentJSONToDRPJSON(requestJSON)).toString();
        }
        if (messageType != null && messageType.equalsIgnoreCase("DeRegistrationStatusUpdate")) {
            return MDMRegistrationHandler.getInstance("Android").processMessage(requestJSON).toString();
        }
        if (messageType != null && messageType.equalsIgnoreCase("DeviceProvisioningSettings")) {
            if (evaluatorApi != null) {
                evaluatorApi.addOrIncrementClickCountForTrialUsers("Enrollment_Module", "Android_App_Enrollment_Settings_Fetched_Count");
            }
            requestJSON.put("CustomerId", (Object)request.customerID);
            return this.formatDRPJSONToAgentJSON(MDMRegistrationHandler.getInstance("Android").processMessage(this.formatAgentJSONToDRPJSON(requestJSON)));
        }
        if (messageType != null && messageType.equalsIgnoreCase("WakeUpPolicy")) {
            return MDMEnrollmentUtil.getInstance().getAndroidPushConfigMessageResponse(request.customerID).toString();
        }
        if (messageType != null && messageType.equalsIgnoreCase("PrivacySettings")) {
            request.resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(requestJSON.optString("UDID", "null"));
            deviceMsg = PrivacyDeviceMessageHandler.getInstance().processPrivacySettingsRequest(request);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("ScheduleActionUpdate")) {
            request.resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(requestJSON.optString("UDID", "null"));
            final String message = hmap.get("Message");
            final JSONObject jsonObject = new JSONObject(message);
            ScheduledActionsMessageHandler.getInstance().processAndroidMessage(request.resourceID, jsonObject.toMap());
        }
        else if (messageType != null && messageType.equalsIgnoreCase("ELMActivation")) {
            deviceMsg = AndroidEnrollment.getInstance().ProcessELMActivation(hmap);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("SyncAppCatalog")) {
            request.resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(requestJSON.optString("UDID", "null"));
            final String accessMessage = "DATA-IN: " + messageType + this.separator + request.resourceID + this.separator + requestJSON.optString("UDID", "null") + this.separator + requestJSON.optString("Status", "null") + this.separator + MDMUtil.getCurrentTimeInMillis();
            this.messagesAccessLogger.log(Level.INFO, accessMessage);
            deviceMsg = AndroidPayloadHandler.getInstance().createSyncAppCatalogMessage(hmap);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("RemoveDevice")) {
            this.checkinLogger.log(Level.INFO, "Android MessageType:{0} Udid:{1}", new Object[] { messageType, hmap.get("UDID") });
            request.resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(requestJSON.optString("UDID", "null"));
            final String accessMessage = "DATA-IN: " + messageType + this.separator + request.resourceID + this.separator + requestJSON.optString("UDID", "null") + this.separator + requestJSON.optString("Status", "null") + this.separator + MDMUtil.getCurrentTimeInMillis();
            this.messagesAccessLogger.log(Level.INFO, accessMessage);
            String sRemarks = "dc.mdm.profile.remarks.removed_from_device";
            if (hmap.containsKey("Remarks")) {
                sRemarks = hmap.get("Remarks");
            }
            final Boolean isMigrated = Boolean.valueOf(hmap.getOrDefault("IsMigrated", Boolean.FALSE.toString()));
            final String strUDID = hmap.get("UDID");
            final Properties properties = new Properties();
            ((Hashtable<String, String>)properties).put("UDID", strUDID);
            Integer managedstatus = new Integer(4);
            final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
            final int ownedby = ManagedDeviceHandler.getInstance().getDeviceOwnership(resourceID);
            if (InventoryUtil.getInstance().isWipedFromServer(strUDID)) {
                if (ownedby == 1) {
                    sRemarks = "mdm.deprovision.old_remark";
                    managedstatus = 10;
                }
                else {
                    sRemarks = "mdm.deprovision.retire_remark";
                    managedstatus = 11;
                }
                ((Hashtable<String, Boolean>)properties).put("WipeCmdFromServer", true);
            }
            final JSONObject json = ManagedDeviceHandler.getInstance().getDeprovisiondetails(resourceID);
            int managedStatus = -1;
            String deprovisionRemarks = "";
            if (json != null) {
                managedStatus = json.optInt("MANAGED_STATUS", -1);
                deprovisionRemarks = json.optString("REMARKS", "");
            }
            if (json != null && managedStatus != -1 && deprovisionRemarks != null && deprovisionRemarks != "") {
                ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", managedStatus);
                ((Hashtable<String, String>)properties).put("REMARKS", deprovisionRemarks);
                ((Hashtable<String, Boolean>)properties).put("WipeCmdFromServer", true);
            }
            else {
                ((Hashtable<String, Integer>)properties).put("MANAGED_STATUS", managedstatus);
                ((Hashtable<String, String>)properties).put("REMARKS", sRemarks);
            }
            ((Hashtable<String, Integer>)properties).put("PLATFORM_TYPE", 2);
            ((Hashtable<String, Boolean>)properties).put("IsMigrated", isMigrated);
            final Long resourceId = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID);
            if (ManagedDeviceHandler.getInstance().isDeviceRemoved(resourceId)) {
                DeviceCommandRepository.getInstance().clearCommandFromDevice(strUDID, request.resourceID, "RemoveDevice", 1);
                if (!DeviceCommandRepository.getInstance().hasDeviceCommandInCacheOrRepo(strUDID)) {
                    ManagedDeviceHandler.getInstance().removeDeviceInTrash(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(strUDID));
                }
            }
            else {
                ManagedDeviceHandler.getInstance().updateManagedDeviceDetails(properties);
                final JSONObject deprovisionJson = new JSONObject();
                deprovisionJson.put("RESOURCE_ID", (Object)resourceID);
                deprovisionJson.put("WIPE_PENDING", (Object)Boolean.FALSE);
                ManagedDeviceHandler.getInstance().updatedeprovisionhistory(deprovisionJson);
                final List remarksList = new ArrayList();
                remarksList.add(ManagedDeviceHandler.getInstance().getDeviceName(resourceID));
                MDMEventLogHandler.getInstance().addEvent(2001, null, "mdm.unmanage.user_revoke_management", remarksList, request.customerID, System.currentTimeMillis());
                final org.json.simple.JSONObject logJSON = new org.json.simple.JSONObject();
                logJSON.put((Object)"REMARKS", (Object)"deprovision-success");
                logJSON.put((Object)"RESOURCE_ID", (Object)resourceID);
                logJSON.put((Object)"NAME", (Object)remarksList);
                MDMOneLineLogger.log(Level.INFO, "DEVICE_UNMANAGED", logJSON);
            }
        }
        else if (messageType != null && messageType.equalsIgnoreCase("EnrollmentFailure")) {
            AndroidEnrollment.getInstance().updateEnrollmentfailed(hmap);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("ElmActivationFailure")) {
            final JSONObject data = new JSONObject();
            data.put("ENROLLMENT_REQUEST_ID", (Object)hmap.get("EnrollmentReqID"));
            data.put("ELM_STATUS", 0);
            AndroidEnrollment.getInstance().addOrUpdateELMStatus(data);
            AndroidEnrollment.getInstance().updateELMFailedRemarks(hmap);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("RemoteSessionUpdate")) {
            final String message = hmap.get("Message");
            final JSONObject messagejson = new JSONObject(message);
            new RemoteSessionManager().handleSessionUpdateFromAgent(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(requestJSON.optString("UDID")), messagejson);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("KioskStatusMessage")) {
            final String message = hmap.get("Message");
            final JSONObject messagejson = new JSONObject(message);
            new KioskPauseResumeManager().handleKioskStatusUpdateFromAgent(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(requestJSON.optString("UDID")), messagejson);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("GenerateAFWToken")) {
            final String token = new AFWAccountRegistrationHandler().generateAFWAccountToken(hmap.get("UDID"), request.customerID);
            deviceMsg = new DeviceMessage();
            deviceMsg.status = "Acknowledged";
            final JSONObject response = new JSONObject();
            response.put("Token", (Object)token);
            deviceMsg.setMessageResponseJSON(response);
            deviceMsg.messageType = "GenerateAFWToken";
        }
        else if (messageType != null && messageType.equalsIgnoreCase("TermsOfUse")) {
            final String deviceMsgRequest = hmap.get("Message");
            this.logger.log(Level.INFO, "AndroidServerMessageRequestHandler(Terms of use) =>  Received request from agent {0}", new Object[] { hmap.toString() });
            final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().getAPIKeyFromMap(request.requestMap);
            deviceMsg = MDMTermsHandler.getInstance().getDiffAndUpdateStatus(deviceMsgRequest, key);
            this.logger.log(Level.INFO, "AndroidServerMessageRequestHandler(Terms of use) : Response data to the agent is {0}", deviceMsg.toString());
        }
        else {
            if (messageType != null && messageType.equalsIgnoreCase("SyncDocuments")) {
                final String deviceMsgRequest = hmap.get("MsgRequest");
                final JSONObject msgRequestJS = new JSONObject(deviceMsgRequest);
                final String udid = hmap.get("UDID");
                final Long lastSyncTime = new DocServlet().extractLastSyncTime(msgRequestJS);
                final boolean ackSupported = msgRequestJS.optBoolean("ACK_SUPPORTED", false);
                DocMgmt.logger.log(Level.INFO, "AndroidServerMessageRequestHandler (sync documents)=>  Received request from agent {0} : {1}", new Object[] { udid, hmap.toString() });
                final Long resourceID2 = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid);
                final JSONObject docDiffData = DocMgmtDataHandler.getInstance().getDiffAndUpdateStatus(resourceID2, lastSyncTime, ackSupported);
                DocMgmt.logger.log(Level.INFO, "AndroidServerMessageRequestHandler (sync documents): Response data to the agent is {0}", docDiffData.toString());
                try {
                    DeviceCommandRepository.getInstance().deleteResourceCommand("SyncDocuments", resourceID2);
                }
                catch (final Exception ex) {
                    DocMgmt.logger.log(Level.INFO, ex, () -> "exception occured deleting the notification command for " + n);
                }
                return docDiffData.toString();
            }
            if (messageType != null && messageType.equalsIgnoreCase("AckDocuments")) {
                final String deviceMsgRequest = hmap.get("MsgRequest");
                final JSONObject msgRequestJS = new JSONObject(deviceMsgRequest);
                final DocServlet docServlet = new DocServlet();
                final String udid2 = hmap.get("UDID");
                DocMgmt.logger.log(Level.INFO, "AndroidServerMessageRequestHandler (ack documents)=>  Received request from agent {0} : {1}", new Object[] { udid2, hmap.toString() });
                final Long[] docMDids = docServlet.extractDocMDIds(msgRequestJS);
                final Long lastSyncTime2 = docServlet.extractLastSyncTime(msgRequestJS);
                final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid2);
                final boolean acknowledged = DocMgmtDataHandler.getInstance().processAck(resourceID, lastSyncTime2, docMDids);
                final JSONObject serverAck = new JSONObject();
                if (acknowledged) {
                    serverAck.put("LastSyncTime", (Object)lastSyncTime2);
                    serverAck.put("MsgResponseType", (Object)"AckDocuments");
                }
                DocMgmt.logger.log(Level.INFO, "AndroidServerMessageRequestHandler (ack documents): Response data to the agent is {0}", serverAck.toString());
                return serverAck.toString();
            }
            if (messageType.equalsIgnoreCase("CertificateRequest")) {
                deviceMsg = new DeviceMessage();
                deviceMsg.messageType = "CertificateRequest";
                deviceMsg.status = "Acknowledged";
                JSONObject responseObject = new JSONObject();
                responseObject = ServerCertificateFetchingUtil.getInstance().fetchCertificateJSON();
                deviceMsg.setMessageResponseJSON(responseObject);
                this.logger.log(Level.INFO, "Received certificate fetch command");
            }
            else if (messageType.equalsIgnoreCase("SafetyNetCredentials")) {
                deviceMsg = new DeviceMessage();
                deviceMsg.messageType = "SafetyNetCredentials";
                deviceMsg.status = "Acknowledged";
                this.logger.log(Level.INFO, " Device with UDID {0} is requesting for safety net credentials", requestJSON.get("UDID"));
                if (!new SafetyNetHandler().isError((JSONObject)request.deviceRequestData)) {
                    deviceMsg.setMessageResponseJSON(new SafetyNetHandler().getSafetyNetCredentials(String.valueOf(requestJSON.get("UDID"))));
                }
                else if (new SafetyNetHandler().isAlreadyAttestedDevice(String.valueOf(requestJSON.get("UDID")))) {
                    this.logger.log(Level.INFO, "The device has already been attested once, no need to handle this one time error message as it will be solved eventually");
                }
                else {
                    final JSONObject deviceData = (JSONObject)request.deviceRequestData;
                    new SafetyNetHandler().storeErrorStates(deviceData.optInt("ErrorCode"), deviceData.optString("ErrorMsg"), hmap.get("UDID"));
                }
            }
            else if (messageType.equalsIgnoreCase("SafetyNetResponse")) {
                deviceMsg = new DeviceMessage();
                deviceMsg.messageType = "SafetyNetResponse";
                deviceMsg.status = "Acknowledged";
                this.addResponseToQueue(request, requestJSON.toString(), 121);
            }
            else if (messageType != null && messageType.equalsIgnoreCase("DeviceAlerts")) {
                final String udid3 = String.valueOf(requestJSON.get("UDID"));
                request.resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid3);
                final JSONObject messageJSON = requestJSON.getJSONObject("MsgRequest");
                messageJSON.put("ResourceId", (Object)request.resourceID);
                messageJSON.put("CustomerID", (Object)request.customerID);
                messageJSON.put("PlatformType", request.devicePlatform);
                messageJSON.put("DevicePlatform", (Object)String.valueOf(requestJSON.get("DevicePlatform")));
                messageJSON.put("UDID", (Object)udid3);
                messageJSON.put("MessageType", (Object)"DeviceAlerts");
                this.addResponseToQueue(request, messageJSON.toString(), 121);
            }
            else if (messageType != null && messageType.equalsIgnoreCase("DeviceEvents")) {
                this.logger.log(Level.INFO, "Device event received, due to huge influx dropping the data");
                this.logger.log(Level.INFO, " The device data dropped is {0}", requestJSON.toString());
            }
            else if (messageType.equalsIgnoreCase("BatteryStatusUpdate")) {
                deviceMsg = new DeviceMessage();
                deviceMsg.messageType = "BatteryStatusUpdate";
                deviceMsg.status = "Acknowledged";
                this.addResponseToQueue(request, requestJSON.toString(), 121);
            }
            else if (messageType != null && messageType.equalsIgnoreCase("SyncAnnouncementMeta")) {
                final String udid3 = String.valueOf(requestJSON.get("UDID"));
                request.resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid3);
                final DeviceMessageRequest messageRequest = new DeviceMessageRequest(requestJSON);
                deviceMsg = new AnnouncementSyncHandler().processSyncAnnouncementMetaData(request.resourceID, messageRequest);
            }
            else if (messageType != null && messageType.equalsIgnoreCase("SyncAnnouncementAck")) {
                this.addResponseToQueue(request, requestJSON.toString(), 121);
                final DeviceMessageRequest messageRequest2 = new DeviceMessageRequest(requestJSON);
                deviceMsg = new AnnouncementSyncHandler().getSyncAnnouncementAckResponse(messageRequest2);
            }
            else if (messageType != null && messageType.equalsIgnoreCase("SyncAnnouncement")) {
                final String udid3 = String.valueOf(requestJSON.get("UDID"));
                request.resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid3);
                final DeviceMessageRequest messageRequest = new DeviceMessageRequest(requestJSON);
                deviceMsg = new AnnouncementSyncHandler().getAnnouncementForResource(request.resourceID, messageRequest, udid3, request.customerID);
            }
            else if (messageType != null && (messageType.equalsIgnoreCase("AnnouncementRead") || messageType.equalsIgnoreCase("AnnouncementAck"))) {
                this.addResponseToQueue(request, requestJSON.toString(), 121);
                deviceMsg = this.getDefaultDeviceMessage(messageType);
            }
            else if (messageType != null && messageType.equalsIgnoreCase("CapabilitiesInfo")) {
                final String udid3 = String.valueOf(requestJSON.get("UDID"));
                request.resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(udid3);
                MDMInvDataPopulator.getInstance().addOrUpdateCapabilitiesInfo(request.resourceID, requestJSON.getJSONObject("CapabilitiesInfo"));
                deviceMsg = new DeviceMessage();
                deviceMsg.messageType = "CapabilitiesInfo";
                deviceMsg.status = "Acknowledged";
            }
            else {
                final JSONObject jsonObject2 = (JSONObject)request.deviceRequestData;
                if (jsonObject2.has("MessageType") && String.valueOf(jsonObject2.get("MessageType")).equalsIgnoreCase("Enrollment")) {
                    this.checkinLogger.log(Level.INFO, "Android MessageType:{0} Erid:{1} Udid:{2}", new Object[] { messageType, hmap.get("EnrollmentReqID"), hmap.get("UDID") });
                }
                int dataQueueType = 121;
                final int agentType = jsonObject2.optInt("AGENT_TYPE");
                if (agentType == 2) {
                    dataQueueType = 121;
                }
                else if (agentType == 3) {
                    dataQueueType = 122;
                }
                this.addResponseToQueue(request, requestJSON.toString(), dataQueueType);
            }
        }
        if (deviceMsg != null) {
            final JSONObject responseMsgJSON = this.constructAndroidMessage(deviceMsg);
            final String accessMessage2 = "DATA-OUT: " + responseMsgJSON.optString("MessageType") + this.separator + request.resourceID + this.separator + request.deviceUDID + this.separator + responseMsgJSON.optString("Status") + this.separator + MDMUtil.getCurrentTimeInMillis();
            responseData = responseMsgJSON.toString();
            this.messagesAccessLogger.log(Level.INFO, accessMessage2);
        }
        return responseData;
    }
    
    private DeviceMessage getDefaultDeviceMessage(final String messageType) {
        final DeviceMessage deviceMessage = new DeviceMessage();
        deviceMessage.messageType = messageType;
        deviceMessage.status = "Acknowledged";
        return deviceMessage;
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
        MDMSecurityLogger.info(this.logger, "AndroidServerMessageRequestHandler", "constructAndroidMessage", "constructAndroidMessage - Sending message to device {0}", response);
        return response;
    }
    
    @Override
    protected String getNextDeviceCommandQuery(final DeviceCommand nextCommand, final DeviceRequest request) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private JSONObject formatAgentJSONToDRPJSON(final JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("AuthMode")) {
            jsonObject.put("AuthMode", (Object)this.getAuthModeAsString(jsonObject.getInt("AuthMode")));
        }
        if (jsonObject.has("OwnedBy")) {
            jsonObject.put("OwnedBy", (Object)this.getOwnedByAsString(jsonObject.getInt("OwnedBy")));
        }
        final JSONObject requestJSON = new JSONObject();
        final String msgType = String.valueOf(jsonObject.get("MessageType"));
        if (msgType.equalsIgnoreCase("AuthMode")) {
            requestJSON.put("MsgRequestType", (Object)"Discover");
        }
        else if (msgType.equalsIgnoreCase("Authenticate")) {
            requestJSON.put("MsgRequestType", (Object)"Authenticate");
        }
        else if (msgType.equalsIgnoreCase("RegistrationStatusUpdate")) {
            requestJSON.put("MsgRequestType", (Object)"RegistrationStatusUpdate");
        }
        else if (msgType.equalsIgnoreCase("DeRegistrationStatusUpdate")) {
            requestJSON.put("MsgRequestType", (Object)"DeRegistrationStatusUpdate");
        }
        else if (msgType.equalsIgnoreCase("DeviceProvisioningSettings")) {
            requestJSON.put("MsgRequestType", (Object)"DeviceProvisioningSettings");
        }
        final JSONObject messageRequestJSON = new JSONObject();
        final Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            if (!key.equals("MessageType")) {
                messageRequestJSON.put(key, jsonObject.get(key));
            }
        }
        requestJSON.put("MsgRequest", (Object)messageRequestJSON);
        requestJSON.put("DevicePlatform", (Object)"android");
        return requestJSON;
    }
    
    private String formatDRPJSONToAgentJSON(final JSONObject processMessage) {
        try {
            final JSONObject messageResponse = processMessage.getJSONObject("MsgResponse");
            if (String.valueOf(processMessage.get("Status")).equals("Acknowledged")) {
                if (messageResponse.has("EnrollmentRequestID")) {
                    messageResponse.put("EnrollmentReqID", messageResponse.get("EnrollmentRequestID"));
                    messageResponse.remove("EnrollmentRequestID");
                }
                if (messageResponse.has("PlatformType")) {
                    messageResponse.put("PlatformType", this.getPlatformAsInt(String.valueOf(messageResponse.get("PlatformType"))));
                }
                if (messageResponse.has("AuthMode")) {
                    messageResponse.put("AuthMode", this.getAuthModeAsInt(String.valueOf(messageResponse.get("AuthMode"))));
                }
                if (messageResponse.has("OwnedBy")) {
                    messageResponse.put("OwnedBy", this.getOwnedByAsInt(String.valueOf(messageResponse.get("OwnedBy"))));
                }
            }
            processMessage.put("MessageResponse", (Object)messageResponse);
            processMessage.remove("MsgResponse");
            final String responseType = String.valueOf(processMessage.get("MsgResponseType"));
            if (responseType.equalsIgnoreCase("DiscoverResponse")) {
                processMessage.put("MessageType", (Object)"AuthMode");
            }
            else if (responseType.equalsIgnoreCase("AuthenticateResponse")) {
                processMessage.put("MessageType", (Object)"Authenticate");
            }
            else if (responseType.equalsIgnoreCase("DeviceProvisioningSettingsResponse")) {
                processMessage.put("MessageType", (Object)"DeviceProvisioningSettings");
            }
            processMessage.remove("MsgResponseType");
            return processMessage.toString();
        }
        catch (final JSONException ex) {
            this.logger.log(Level.SEVERE, null, (Throwable)ex);
            return processMessage.toString();
        }
    }
    
    private int getPlatformAsInt(final String platformValue) {
        int platform = -1;
        if (platformValue != null) {
            if (platformValue.equalsIgnoreCase("ios")) {
                platform = 1;
            }
            else if (platformValue.equalsIgnoreCase("android")) {
                platform = 2;
            }
            else if (platformValue.equalsIgnoreCase("windowsphone")) {
                platform = 3;
            }
        }
        return platform;
    }
    
    private String getAuthModeAsString(final int authMode) {
        switch (authMode) {
            case 2: {
                return "ActiveDirectory";
            }
            case 1: {
                return "OTP";
            }
            case 3: {
                return "Combined";
            }
            default: {
                return null;
            }
        }
    }
    
    private int getAuthModeAsInt(final String authMode) {
        if (authMode.equalsIgnoreCase("OTP")) {
            return 1;
        }
        if (authMode.equalsIgnoreCase("ActiveDirectory")) {
            return 2;
        }
        if (authMode.equalsIgnoreCase("Combined")) {
            return 3;
        }
        return -1;
    }
    
    private int getOwnedByAsInt(final String ownedBy) {
        if (ownedBy.equalsIgnoreCase("Corporate")) {
            return 1;
        }
        if (ownedBy.equalsIgnoreCase("Personal")) {
            return 2;
        }
        return -1;
    }
    
    private String getOwnedByAsString(final int ownedBy) {
        switch (ownedBy) {
            case 1: {
                return "corporate";
            }
            case 2: {
                return "personal";
            }
            default: {
                return null;
            }
        }
    }
}
