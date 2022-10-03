package com.adventnet.sym.server.mdm.queue;

import com.me.devicemanagement.framework.server.logger.DMSecurityLogger;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;

public class QueueControllerHelper
{
    private Logger mdmLogger;
    Logger queueLogger;
    Logger logger;
    private static QueueControllerHelper controllerHelper;
    
    public QueueControllerHelper() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
        this.queueLogger = Logger.getLogger("MDMQueueBriefLogger");
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public static QueueControllerHelper getInstance() {
        return QueueControllerHelper.controllerHelper;
    }
    
    public String getQueueNameForQueueDatatype(final int queueDataType, final String queueData) {
        QueueName queueName = null;
        switch (queueDataType) {
            case 101:
            case 102: {
                queueName = AndroidQueueController.getInstance().getAndroidCommandQueueName(queueData);
                break;
            }
            case 121:
            case 122: {
                queueName = AndroidQueueController.getInstance().getAndroidMessageQueueName(queueData);
                break;
            }
            case 124: {
                queueName = AndroidQueueController.getInstance().getAndroidNativeQueueName(queueData);
                break;
            }
            case 100: {
                queueName = IOSQueueController.getInstance().getIOSCommandQueueName(queueData);
                break;
            }
            case 108: {
                queueName = IOSQueueController.getInstance().getAppleMessageQueueName(queueData);
                break;
            }
            case 107: {
                queueName = IOSQueueController.getInstance().getIOSCommandQueueName(queueData);
                break;
            }
            case 140: {
                queueName = IOSQueueController.getInstance().getIOSAppCommandQueueType(queueData);
                break;
            }
            case 120: {
                queueName = IOSQueueController.getInstance().getIOSAppMessageQueueType(queueData);
                break;
            }
            case 103: {
                queueName = WindowsQueueController.getInstance().getWindowsPhoneCommandQueueName(queueData);
                break;
            }
            case 143: {
                queueName = WindowsQueueController.getInstance().getWindowsAppCommandQueueName(queueData);
                break;
            }
            case 123: {
                queueName = WindowsQueueController.getInstance().getWindowsAppMessageQueueName(queueData);
                break;
            }
            case 106: {
                queueName = ChromeQueueController.getInstance().getQueueNameForCommand(queueData);
                break;
            }
            case 126: {
                queueName = ChromeQueueController.getInstance().getQueueNameForMessage(queueData);
                break;
            }
            case 105: {
                queueName = AndroidQueueController.getInstance().getQueueNameForAdminCommands(queueData);
                break;
            }
            case 125: {
                queueName = AndroidQueueController.getInstance().getQueueNameForAdminMessage(queueData);
                break;
            }
            case 48: {
                queueName = QueueName.DISCOVERED_APP_DATA;
                break;
            }
            default: {
                queueName = QueueName.OTHERS;
                break;
            }
        }
        return queueName.getQueueName();
    }
    
    public String getQueueName(final int qDataType, final String qData) {
        String queuename = "mdm-data";
        final boolean isQueueSplitGloballyEnabled = MDMUtil.getInstance().isMDMQueueSplitAvailableGlobally();
        final boolean isQueueSplitFeatureEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MdmDataQueueSplit");
        if (isQueueSplitFeatureEnabled && isQueueSplitGloballyEnabled) {
            queuename = this.getQueueNameForQueueDatatype(qDataType, qData);
        }
        if (queuename.equalsIgnoreCase(QueueName.OTHERS.getQueueName())) {
            this.printExcessInfo(qData);
        }
        return queuename;
    }
    
    private void printExcessInfo(final String qData) {
        DMSecurityLogger.info(this.logger, QueueControllerHelper.class.getSimpleName(), "OtherDataQueue", "Rogue data spotted, take necessary action -- {0}", (Object)new Object[] { qData });
    }
    
    static {
        QueueControllerHelper.controllerHelper = new QueueControllerHelper();
    }
}
