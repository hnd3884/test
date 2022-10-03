package com.adventnet.sym.server.mdm.config;

import java.util.Properties;
import com.adventnet.sym.server.mdm.config.task.AssignCommandTaskProcessor;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class AssignAppQueueProcessor extends DCQueueDataProcessor
{
    Logger logger;
    
    public AssignAppQueueProcessor() {
        this.logger = Logger.getLogger("MDMProfileDistributionLog");
    }
    
    public void processData(final DCQueueData qData) {
        try {
            this.logger.log(Level.INFO, "AssignAppForDevice queue data processed with props {0} with queue data {1}", new Object[] { qData, qData.queueData.toString() });
            final Properties taskProperties = MDMApiFactoryProvider.getAssociationQueueSerializer().deserializeProperty(qData.queueData);
            if (qData.queueDataType == 2) {
                AssignCommandTaskProcessor.getTaskProcessor().assignUserCommandTask(taskProperties);
            }
            else {
                AssignCommandTaskProcessor.getTaskProcessor().assignDeviceCommandTask(taskProperties);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in AssignAppForDeviceQueueProcessor", e);
        }
    }
}
