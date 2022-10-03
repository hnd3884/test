package com.me.mdm.server.msp.sync;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.queue.DCQueueDataProcessor;

public class SyncConfigurationProcessor extends DCQueueDataProcessor
{
    private Logger logger;
    
    public SyncConfigurationProcessor() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    public void processData(final DCQueueData qData) {
        try {
            this.logger.log(Level.INFO, "SyncConfigurationProcessor initiated with props {0} and data {1}", new Object[] { qData.toString(), qData.queueData.toString() });
            ConfigurationsSyncEngineHandler.getInstance().getActionHandler(qData).sync();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in SyncConfigurationProcessor", ex);
        }
    }
}
