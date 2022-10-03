package com.adventnet.sym.server.inventory.task;

import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class InvFileScanRulesPopulatorScheduler implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public InvFileScanRulesPopulatorScheduler() {
        this.logger = Logger.getLogger("InvFileScanLog");
    }
    
    public void executeTask(final Properties props) {
    }
}
