package com.adventnet.sym.server.mdm.config.task;

import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class AssignUserCommandTask implements SchedulerExecutionInterface
{
    public static Logger logger;
    
    public void executeTask(final Properties taskProps) {
        AssignCommandTaskProcessor.getTaskProcessor().assignUserCommandTask(taskProps);
    }
    
    static {
        AssignUserCommandTask.logger = Logger.getLogger("MDMConfigLogger");
    }
}
