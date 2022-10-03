package com.me.mdm.server.task;

import java.util.logging.Level;
import com.me.mdm.onpremise.server.android.agent.AndroidAgentSecretsHandler;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class ELMKeysUpdateHandlerTask implements SchedulerExecutionInterface
{
    private static final Logger LOGGER;
    
    public void executeTask(final Properties props) {
        try {
            final AndroidAgentSecretsHandler androidAgentSecretsHandler = new AndroidAgentSecretsHandler();
            androidAgentSecretsHandler.updateElmKeysInDB();
        }
        catch (final Exception e) {
            ELMKeysUpdateHandlerTask.LOGGER.log(Level.SEVERE, "Exception in running ELMKeyUpdateHandlerTask ", e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
