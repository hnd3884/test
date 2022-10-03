package com.adventnet.sym.server.mdm.queue;

import java.util.List;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;

public class IOSQueueController extends BaseQueueController
{
    private static IOSQueueController queueController;
    private Logger mdmLogger;
    
    public IOSQueueController() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    public static IOSQueueController getInstance() {
        return IOSQueueController.queueController;
    }
    
    public QueueName getIOSCommandQueueName(final String queueData) {
        final HashMap hashMap = this.getHashFromPlist(queueData);
        final String strCommandUuid = hashMap.get("CommandUUID");
        final List valueList = MDMUtil.getInstance().getStringList(strCommandUuid, ";");
        final String deviceCommand = valueList.get(0);
        final QueueName name = this.getQueueName(deviceCommand, strCommandUuid);
        return name;
    }
    
    public QueueName getAppleMessageQueueName(final String queueData) {
        final HashMap hashMap = this.getHashFromPlist(queueData);
        final String messageType = hashMap.get("MessageType");
        final QueueName name = this.getQueueName(messageType, "");
        return name;
    }
    
    public QueueName getIOSAppMessageQueueType(final String queueData) {
        final String messageType = this.getDeviceMessageFromQueueData(queueData).messageType;
        QueueName name;
        if (messageType.equalsIgnoreCase("Location") || messageType.equalsIgnoreCase("LocationUpdate")) {
            name = QueueName.PASSIVE_EVENTS;
        }
        else if (messageType.equalsIgnoreCase("RemoteSessionUpdate")) {
            name = QueueName.SECURITY_DATA;
        }
        else if (messageType.equalsIgnoreCase("DeviceAlerts")) {
            name = QueueName.ACTIVE_EVENTS;
        }
        else if (messageType.equalsIgnoreCase("BatteryStatusUpdate") || messageType.equalsIgnoreCase("WifiSsidMsg")) {
            name = QueueName.DEVICE_TRACKING_UPDATES;
        }
        else {
            name = QueueName.OTHERS;
        }
        return name;
    }
    
    public QueueName getIOSAppCommandQueueType(final String queueData) {
        final HashMap<String, String> hmap = this.getHashFromJson(queueData);
        final String responsedData = hmap.get("CommandResponse");
        final String strCommandUuid = hmap.get("CommandUUID");
        final QueueName name = this.getQueueName(responsedData, strCommandUuid);
        return name;
    }
    
    static {
        IOSQueueController.queueController = new IOSQueueController();
    }
}
