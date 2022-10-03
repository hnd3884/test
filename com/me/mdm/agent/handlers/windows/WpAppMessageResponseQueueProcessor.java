package com.me.mdm.agent.handlers.windows;

import org.json.JSONException;
import com.me.mdm.server.windows.notification.WpDeviceChannelUri;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONObject;
import com.me.uem.announcement.AnnouncementHandler;
import com.me.mdm.agent.handlers.BaseAppMessageQueueProcessor;

public class WpAppMessageResponseQueueProcessor extends BaseAppMessageQueueProcessor
{
    private static String sCHANNEL_URI;
    
    @Override
    protected void processMessage() throws Exception {
        final String messageType = this.messageRequest.messageType;
        if (messageType.equalsIgnoreCase("InstallApplication") || messageType.equalsIgnoreCase("UpdateApplication")) {
            this.processInstallOrUpdateAppMessage();
        }
        else if (messageType.equalsIgnoreCase("AppNotificationCredential")) {
            this.processAppChannelUriUpdateMessage();
        }
        else if (messageType.equalsIgnoreCase("AnnouncementAck")) {
            final JSONObject anJSON = this.messageRequest.messageRequest;
            AnnouncementHandler.newInstance().updateAcknowledgment(anJSON, this.messageRequest.resourceID);
        }
        else if (messageType.equalsIgnoreCase("AnnouncementRead")) {
            final JSONObject anJSON = this.messageRequest.messageRequest;
            AnnouncementHandler.newInstance().updateAnnouncementRead(anJSON, this.messageRequest.resourceID);
        }
    }
    
    private void processInstallOrUpdateAppMessage() throws Exception {
        final Long collectionId = this.messageRequest.messageRequest.getLong("CollectionID");
        final List resourceIdList = Arrays.asList(ManagedDeviceHandler.getInstance().getResourceIDFromUDID(this.messageRequest.udid));
        final String commandName = this.messageRequest.messageType;
        final List commandIdList = DeviceCommandRepository.getInstance().getCollectionIdsCommandList(Arrays.asList(collectionId), commandName);
        DeviceCommandRepository.getInstance().assignCommandToDevices(commandIdList, resourceIdList);
        NotificationHandler.getInstance().SendNotification(resourceIdList, 3);
    }
    
    private void processAppChannelUriUpdateMessage() throws JSONException {
        final String channelUri = String.valueOf(this.messageRequest.messageRequest.get(WpAppMessageResponseQueueProcessor.sCHANNEL_URI));
        new WpDeviceChannelUri().updateNativeAppChannelUri(this.messageRequest.udid, channelUri);
    }
    
    static {
        WpAppMessageResponseQueueProcessor.sCHANNEL_URI = "ChannelURI";
    }
}
