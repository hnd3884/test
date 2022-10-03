package com.adventnet.sym.server.mdm.queue;

import java.util.List;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AndroidQueueController extends BaseQueueController
{
    private static AndroidQueueController androidQueueController;
    private static Logger mdmLogger;
    
    public static AndroidQueueController getInstance() {
        return AndroidQueueController.androidQueueController;
    }
    
    public QueueName getAndroidMessageQueueName(final String queueData) {
        final HashMap hmap = this.convertStringToHash(queueData);
        String msgType = null;
        if (hmap.containsKey("MsgRequestType")) {
            msgType = hmap.get("MsgRequestType");
        }
        else if (hmap.containsKey("MessageType")) {
            msgType = hmap.get("MessageType");
        }
        QueueName name;
        if (msgType.equalsIgnoreCase("Enrollment") || msgType.equalsIgnoreCase("TokenUpdate")) {
            name = QueueName.ENROLLMENT_DATA;
        }
        else if (msgType.equalsIgnoreCase("ContainerStatus") || msgType.equalsIgnoreCase("KnoxAvailability") || msgType.equalsIgnoreCase("PolicyInfo") || msgType.equalsIgnoreCase("GoogleAccountChanged") || msgType.equalsIgnoreCase("GooglePlayActivationReq") || msgType.equalsIgnoreCase("AFWAccountStatusUpdate") || msgType.equalsIgnoreCase("DeviceAlerts") || msgType.equalsIgnoreCase("UpdateToken") || msgType.equalsIgnoreCase("DetectUserGSuiteAccount")) {
            name = QueueName.ACTIVE_EVENTS;
        }
        else if (msgType.equalsIgnoreCase("ManagedAppStatus")) {
            name = QueueName.APP_COLLECTION_STATUS;
        }
        else if (msgType.equalsIgnoreCase("Location") || msgType.equalsIgnoreCase("DeviceEvents") || msgType.equalsIgnoreCase("LocationUpdate")) {
            name = QueueName.PASSIVE_EVENTS;
        }
        else if (msgType.equalsIgnoreCase("BatteryStatusUpdate")) {
            name = QueueName.DEVICE_TRACKING_UPDATES;
        }
        else if (msgType.equalsIgnoreCase("AgentUpgrade") || msgType.equalsIgnoreCase("AgentMigrationStatus")) {
            name = QueueName.AGENT_UPGRADE;
        }
        else if (msgType.equalsIgnoreCase("OSUpgraded") || msgType.equalsIgnoreCase("PendingOSUpdates") || msgType.equalsIgnoreCase("SecurityPatchLevelUpdated")) {
            name = QueueName.OSUPDATE_COLLECTION_STATUS;
        }
        else if (msgType.equalsIgnoreCase("LostModeDisabled") || msgType.equalsIgnoreCase("SafetyNetResponse")) {
            name = QueueName.SECURITY_DATA;
        }
        else {
            name = QueueName.OTHERS;
        }
        AndroidQueueController.mdmLogger.log(Level.INFO, " Queue for Message - {0} is {1}", new Object[] { msgType, name.getQueueName() });
        return name;
    }
    
    public QueueName getAndroidCommandQueueName(final String queueData) {
        final HashMap hashMap = this.convertStringToHash(queueData);
        final String strCommandUuid = hashMap.get("CommandUUID");
        final List valueList = MDMUtil.getInstance().getStringList(strCommandUuid, ";");
        final String deviceCommand = valueList.get(0);
        final QueueName name = this.getQueueName(deviceCommand, strCommandUuid);
        return name;
    }
    
    public QueueName getAndroidNativeQueueName(final String queueData) {
        return QueueName.AGENT_UPGRADE;
    }
    
    public QueueName getQueueNameForAdminCommands(final String queueData) {
        final String cmdType = this.getDeviceCommandFromQueueData(queueData).responseType;
        if (cmdType.equalsIgnoreCase("AgentUpgrade")) {
            return QueueName.AGENT_UPGRADE;
        }
        return QueueName.OTHERS;
    }
    
    public QueueName getQueueNameForAdminMessage(final String queueData) {
        final String msgType = this.getDeviceMessageFromQueueData(queueData).messageType;
        if (msgType.equalsIgnoreCase("AgentUpgrade")) {
            return QueueName.AGENT_UPGRADE;
        }
        return QueueName.OTHERS;
    }
    
    static {
        AndroidQueueController.androidQueueController = new AndroidQueueController();
        AndroidQueueController.mdmLogger = Logger.getLogger("MDMLogger");
    }
}
