package com.adventnet.sym.server.mdm.apps;

import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class BlacklistWhitelistCommandTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public BlacklistWhitelistCommandTask() {
        this.logger = Logger.getLogger(BlacklistWhitelistCommandTask.class.getName());
    }
    
    public void executeTask(final Properties taskProps) {
    }
}
