package com.me.mdm.agent.handlers.ios;

import java.util.HashMap;
import com.me.mdm.server.profiles.ios.response.IOSSingleWebAppFeedbackResponseProcessor;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.me.uem.announcement.AnnouncementHandler;
import com.me.mdm.server.devicealerts.DeviceAlertsMessageProcessor;
import com.me.mdm.server.remotesession.RemoteSessionManager;
import java.util.Map;
import com.me.mdm.server.location.LocationDataHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONObject;
import com.me.mdm.agent.handlers.BaseAppMessageQueueProcessor;

public class IOSAppMessageRequestQueueProcessor extends BaseAppMessageQueueProcessor
{
    @Override
    protected void processMessage() throws Exception {
        final String strData = (String)this.queueDataObject.queueData;
        final JSONObject iosMessageData = new JSONObject(strData);
        final String msgType = this.messageRequest.messageType;
        final String sUDID = this.messageRequest.udid;
        iosMessageData.put("CUSTOMER_ID", (Object)(this.queueDataObject.customerID + ""));
        final Long resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(sUDID);
        if (msgType.equalsIgnoreCase("Location")) {
            MDMInvDataPopulator.getInstance().processIosLocationMessage(iosMessageData, sUDID);
        }
        else if (msgType.equalsIgnoreCase("LocationUpdate")) {
            final HashMap<String, String> hmap = JSONUtil.getInstance().ConvertJSONObjectToHash(iosMessageData);
            new LocationDataHandler().deviceLocationUpdates(resourceID, hmap);
        }
        else if (msgType.equalsIgnoreCase("RemoteSessionUpdate")) {
            final JSONObject messagejson = iosMessageData.optJSONObject("Message");
            new RemoteSessionManager().handleSessionUpdateFromAgent(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(sUDID), messagejson);
        }
        else if (msgType.equalsIgnoreCase("DeviceAlerts")) {
            DeviceAlertsMessageProcessor.getInstance().processDeviceAlertsMessage(iosMessageData);
        }
        else if (msgType.equalsIgnoreCase("AnnouncementAck")) {
            final JSONObject anJSON = this.messageRequest.messageRequest;
            AnnouncementHandler.newInstance().updateAcknowledgment(anJSON, this.messageRequest.resourceID);
        }
        else if (msgType.equalsIgnoreCase("AnnouncementRead")) {
            final JSONObject anJSON = this.messageRequest.messageRequest;
            AnnouncementHandler.newInstance().updateAnnouncementRead(anJSON, this.messageRequest.resourceID);
        }
        else if (msgType.equals("SyncDeviceDetails")) {
            DeviceCommandRepository.getInstance().addiOSDeviceSyncCommandsAndNotify(resourceID);
        }
        else if (msgType.equalsIgnoreCase("SyncAnnouncementAck")) {
            final JSONObject requestJSON = this.messageRequest.messageRequest;
            AnnouncementHandler.newInstance().processAnnouncementStatusForResource(resourceID, requestJSON);
        }
        else if (msgType.equals("KioskAgentStatus")) {
            new IOSSingleWebAppFeedbackResponseProcessor().handleResponseFromAgent(iosMessageData, resourceID);
        }
    }
}
