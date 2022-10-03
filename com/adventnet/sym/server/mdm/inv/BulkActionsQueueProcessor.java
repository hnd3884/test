package com.adventnet.sym.server.mdm.inv;

import java.util.logging.Level;
import com.me.mdm.api.command.CommandFacade;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class BulkActionsQueueProcessor extends DCQueueDataProcessor
{
    private static Logger logger;
    
    public void processData(final DCQueueData qData) {
        try {
            final HashMap queueData = (HashMap)qData.queueData;
            new CommandFacade().invokeBukActions(queueData);
        }
        catch (final Exception e) {
            BulkActionsQueueProcessor.logger.log(Level.SEVERE, "Exception in processing bulk actions queue", e);
        }
    }
    
    static {
        BulkActionsQueueProcessor.logger = Logger.getLogger("MDMQueueLogger");
    }
}
