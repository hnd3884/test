package com.adventnet.sym.server.mdm.queue;

import java.util.List;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;

public class ChromeQueueController extends BaseQueueController
{
    public static ChromeQueueController queueController;
    
    public static ChromeQueueController getInstance() {
        return ChromeQueueController.queueController;
    }
    
    public QueueName getQueueNameForMessage(final String queueData) {
        final String msgType = this.getDeviceMessageFromQueueData(queueData).messageType;
        if (msgType.equalsIgnoreCase("Enrollment") || msgType.equalsIgnoreCase("RemoveDevice")) {
            return QueueName.ENROLLMENT_DATA;
        }
        return QueueName.OTHERS;
    }
    
    public QueueName getQueueNameForCommand(final String queueData) {
        final String cmdType = this.getDeviceCommandFromQueueData(queueData).responseType;
        if (cmdType.equalsIgnoreCase("AssetScan")) {
            return QueueName.ASSET_DATA;
        }
        final HashMap hmap = this.convertStringToHash(queueData);
        final String strCommandUuid = hmap.get("CommandUUID");
        final List valueList = MDMUtil.getInstance().getStringList(strCommandUuid, ";");
        final String deviceCommand = valueList.get(0);
        return this.getQueueName(deviceCommand, strCommandUuid);
    }
    
    static {
        ChromeQueueController.queueController = new ChromeQueueController();
    }
}
