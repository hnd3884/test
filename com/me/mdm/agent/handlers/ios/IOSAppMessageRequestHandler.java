package com.me.mdm.agent.handlers.ios;

import com.me.mdm.core.auth.APIKey;
import com.adventnet.sym.server.mdm.command.DeviceMessage;
import com.me.mdm.server.announcement.handler.AnnouncementSyncHandler;
import com.me.mdm.agent.handlers.DeviceMessageRequest;
import com.adventnet.sym.server.mdm.terms.MDMTermsHandler;
import java.util.Map;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.server.privacy.PrivacyDeviceMessageHandler;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.mdm.server.remotesession.RemoteSessionManager;
import com.adventnet.sym.server.mdm.iosnativeapp.payload.IOSNativeAppPayloadHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.me.mdm.agent.handlers.DeviceRequest;
import com.me.mdm.agent.handlers.BaseProcessDeviceRequestHandler;

public class IOSAppMessageRequestHandler extends BaseProcessDeviceRequestHandler
{
    @Override
    public String processRequest(final DeviceRequest request) throws Exception {
        final JSONObject requestData = new JSONObject((String)request.deviceRequestData);
        final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(requestData);
        this.addIsIOS4Property(hmap, request);
        JSONObject response = new JSONObject();
        String responseData = null;
        String messageType = null;
        if (hmap.containsKey("MsgRequestType")) {
            messageType = hmap.get("MsgRequestType");
        }
        else if (hmap.containsKey("MessageType")) {
            messageType = hmap.get("MessageType");
        }
        DeviceMessage deviceMsg = null;
        if (messageType != null && messageType.equalsIgnoreCase("AgentAuthentication")) {
            deviceMsg = MDMEnrollmentUtil.getInstance().processAuthModeModified(hmap);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("AuthenticateEID")) {
            deviceMsg = MDMiOSEntrollmentUtil.getInstance().processEnrollmentId(hmap);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("fcmTokenRegistration")) {
            deviceMsg = MDMiOSEntrollmentUtil.getInstance().updateAppNotificationToken(request.resourceID, hmap);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("Location")) {
            this.addResponseToQueue(request, (String)request.deviceRequestData, 120);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("LocationUpdate")) {
            this.addResponseToQueue(request, (String)request.deviceRequestData, 120);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("RemoteSessionUpdate")) {
            this.addResponseToQueue(request, (String)request.deviceRequestData, 120);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("GetRemoteSessionInfo")) {
            deviceMsg = IOSNativeAppPayloadHandler.getInstance().createGetRemoteSessionInfoMsg(request);
            final JSONObject constructedMsg = new JSONObject();
            final RemoteSessionManager remoteSessionMgr = new RemoteSessionManager();
            constructedMsg.put("StatusCode", remoteSessionMgr.getSessionStartedUserApprovedErrorCode());
            remoteSessionMgr.handleSessionUpdateFromAgent(request.resourceID, constructedMsg);
            final String udid = ManagedDeviceHandler.getInstance().getUDIDFromResourceID(request.resourceID);
            DeviceCommandRepository.getInstance().clearCommandFromDevice(udid, request.resourceID, "RemoteSession", 2);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("PrivacySettings")) {
            deviceMsg = PrivacyDeviceMessageHandler.getInstance().processPrivacySettingsRequest(request);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("TermsOfUse")) {
            final JSONObject requestJSON = new JSONObject((String)request.deviceRequestData);
            final APIKey key = MDMDeviceAPIKeyGenerator.getInstance().getAPIKeyFromMap(request.requestMap);
            deviceMsg = MDMTermsHandler.getInstance().getDiffAndUpdateStatus(requestJSON.optJSONObject("MsgRequest").toString(), key);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("DeviceAlerts")) {
            final JSONObject requestJSON = new JSONObject((String)request.deviceRequestData);
            final JSONObject messageJSON = requestJSON.getJSONObject("MsgRequest");
            messageJSON.put("ResourceId", (Object)request.resourceID);
            messageJSON.put("CustomerID", (Object)request.customerID);
            messageJSON.put("PlatformType", request.devicePlatform);
            messageJSON.put("DevicePlatform", (Object)String.valueOf(requestJSON.get("DevicePlatform")));
            messageJSON.put("UDID", (Object)request.deviceUDID);
            messageJSON.put("CommandUUID", (Object)request.deviceUDID);
            messageJSON.put("CommandResponse", (Object)"DeviceAlerts");
            messageJSON.put("MsgRequestType", (Object)"DeviceAlerts");
            this.addResponseToQueue(request, messageJSON.toString(), 120);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("BatteryStatusUpdate")) {
            this.addResponseToQueue(request, (String)request.deviceRequestData, 120);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("WifiSsidMsg")) {
            this.addResponseToQueue(request, (String)request.deviceRequestData, 120);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("SyncDeviceDetails")) {
            this.addResponseToQueue(request, (String)request.deviceRequestData, 120);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("SyncAnnouncementMeta")) {
            final DeviceMessageRequest messageRequest = new DeviceMessageRequest(requestData);
            deviceMsg = new AnnouncementSyncHandler().processSyncAnnouncementMetaData(request.resourceID, messageRequest);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("SyncAnnouncementAck")) {
            final DeviceMessageRequest messageRequest = new DeviceMessageRequest(requestData);
            this.addResponseToQueue(request, (String)request.deviceRequestData, 120);
            deviceMsg = new AnnouncementSyncHandler().getSyncAnnouncementAckResponse(messageRequest);
        }
        else if (messageType != null && messageType.equalsIgnoreCase("SyncAnnouncement")) {
            final DeviceMessageRequest messageRequest = new DeviceMessageRequest(requestData);
            deviceMsg = new AnnouncementSyncHandler().getAnnouncementForResource(request.resourceID, messageRequest, request.deviceUDID, request.customerID);
        }
        else if (messageType != null && (messageType.equalsIgnoreCase("AnnouncementRead") || messageType.equalsIgnoreCase("AnnouncementAck"))) {
            this.addResponseToQueue(request, (String)request.deviceRequestData, 120);
            deviceMsg = this.getDefaultDeviceMessageResponse(messageType);
        }
        if (deviceMsg != null) {
            response = this.constructMessage(deviceMsg);
        }
        responseData = response.toString();
        return responseData;
    }
    
    private DeviceMessage getDefaultDeviceMessageResponse(final String messageType) {
        final DeviceMessage message = new DeviceMessage();
        message.status = "Acknowledged";
        message.messageType = messageType;
        return message;
    }
    
    private void addIsIOS4Property(final HashMap<String, String> hmap, final DeviceRequest request) {
        final String userAgent = request.headerMap.get("user-agent");
        String isIOS4 = "false";
        if (userAgent != null && userAgent.contains("OS 4_")) {
            isIOS4 = "true";
        }
        hmap.put("isIOS4", isIOS4);
    }
}
