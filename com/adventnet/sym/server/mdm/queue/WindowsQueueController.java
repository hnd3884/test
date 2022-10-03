package com.adventnet.sym.server.mdm.queue;

import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.core.windows.SyncMLMessageParser;
import com.me.mdm.framework.syncml.xml.XML2SyncMLMessageConverter;

public class WindowsQueueController extends BaseQueueController
{
    public static WindowsQueueController queueController;
    
    public static WindowsQueueController getInstance() {
        return WindowsQueueController.queueController;
    }
    
    public QueueName getWindowsPhoneCommandQueueName(final String queueData) {
        try {
            final XML2SyncMLMessageConverter converter = new XML2SyncMLMessageConverter();
            final SyncMLMessage requestSyncML = converter.transform(queueData);
            final SyncMLMessageParser parser = new SyncMLMessageParser();
            final JSONObject commandStatusObject = parser.parseCommandStatusMessage(requestSyncML);
            final String commandUUID = JSONUtil.getString(commandStatusObject, "CommandUUID", null);
            QueueName name = this.getWindowsQueueName(commandUUID);
            if (name == QueueName.ASSET_DATA) {
                name = QueueName.MODERN_MGMT_ASSET_DATA;
            }
            WindowsQueueController.queueLogger.log(Level.INFO, "WindowsQueueController: Queue name: {0}, CommandUUID: {1}", new Object[] { name.getQueueName(), commandUUID });
            return name;
        }
        catch (final Exception e) {
            WindowsQueueController.queueLogger.log(Level.SEVERE, "Cannot parse windows data ", e);
            return QueueName.OTHERS;
        }
    }
    
    public QueueName getWindowsAppCommandQueueName(final String queueData) {
        final String cmdType = this.getDeviceCommandFromQueueData(queueData).responseType;
        if (cmdType.equalsIgnoreCase("AppNotificationCredential")) {
            return QueueName.ACTIVE_EVENTS;
        }
        if (cmdType.equalsIgnoreCase("GetLocation")) {
            return QueueName.PASSIVE_EVENTS;
        }
        if (cmdType.equalsIgnoreCase("SyncAppCatalog") || cmdType.equalsIgnoreCase("AppCatalogSummary")) {
            return QueueName.SECURITY_DATA;
        }
        if (cmdType.equalsIgnoreCase("CorporateWipe")) {
            return QueueName.ENROLLMENT_DATA;
        }
        if (cmdType.equalsIgnoreCase("SyncAgentSettings") || cmdType.equalsIgnoreCase("AgentUpgrade")) {
            return QueueName.AGENT_UPGRADE;
        }
        return QueueName.OTHERS;
    }
    
    public QueueName getWindowsAppMessageQueueName(final String queueData) {
        final String msgType = this.getDeviceMessageFromQueueData(queueData).messageType;
        if (msgType.equalsIgnoreCase("InstallApplication") || msgType.equalsIgnoreCase("UpdateApplication")) {
            return QueueName.APP_COLLECTION_STATUS;
        }
        if (msgType.equalsIgnoreCase("AppNotificationCredential")) {
            return QueueName.ACTIVE_EVENTS;
        }
        return QueueName.OTHERS;
    }
    
    public QueueName getWindowsQueueName(final String commandUUID) {
        if (commandUUID.equalsIgnoreCase("InstalledApplicationList") || commandUUID.equalsIgnoreCase("PreloadedAppsInfo") || commandUUID.equalsIgnoreCase("DeviceInformation") || commandUUID.contains("DeviceClientSettings")) {
            return QueueName.ASSET_DATA;
        }
        if (commandUUID.equalsIgnoreCase("CorporateWipe") || commandUUID.equalsIgnoreCase("EraseDevice") || commandUUID.contains("AppEnrollmentToken") || commandUUID.contains("DeviceCommunicationPush") || commandUUID.contains("GetChannelUri") || commandUUID.contains("WindowsSelectiveWipe")) {
            return QueueName.ENROLLMENT_DATA;
        }
        if (commandUUID.equalsIgnoreCase("DeviceRing") || commandUUID.equalsIgnoreCase("ResetPasscode") || commandUUID.equalsIgnoreCase("UpdateUserInfo") || commandUUID.equalsIgnoreCase("DeviceName") || commandUUID.contains("WmiInstancePropsQuery")) {
            return QueueName.SECURITY_DATA;
        }
        if (commandUUID.equalsIgnoreCase("DeviceLock") || commandUUID.equalsIgnoreCase("RestartDevice") || commandUUID.equalsIgnoreCase("GetLocation")) {
            return QueueName.PASSIVE_EVENTS;
        }
        if (commandUUID.contains("InstallApplication") || commandUUID.contains("UpdateApplication") || commandUUID.contains("RemoveApplication") || commandUUID.contains("WinAppInstallStatusQuery") || commandUUID.contains("ApplicationConfiguration")) {
            return QueueName.PROFILE_COLLECTION_STATUS;
        }
        if (commandUUID.contains("EnableSideloadApps") || commandUUID.contains("DisableSideloadApps") || commandUUID.contains("SideloadNotConfigured") || commandUUID.contains("InstallProfile") || commandUUID.contains("RemoveProfile") || commandUUID.contains("ScepStatusCheck")) {
            return QueueName.PROFILE_COLLECTION_STATUS;
        }
        if (commandUUID.equalsIgnoreCase("ServerURLReplace")) {
            return QueueName.ACTIVE_EVENTS;
        }
        return QueueName.OTHERS;
    }
    
    static {
        WindowsQueueController.queueController = new WindowsQueueController();
    }
}
