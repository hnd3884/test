package com.me.mdm.uem.queue;

import com.me.devicemanagement.framework.server.queue.DCQueueData;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class ModernMgmtTask extends DCQueueDataProcessor
{
    public void processData(final DCQueueData qData) {
        final ModernMgmtQueueOperation modernMgmtQueueOperation = ModernMgmtQueueOperation.deSerialize((String)qData.queueData);
        modernMgmtQueueOperation.data.processData();
    }
}
