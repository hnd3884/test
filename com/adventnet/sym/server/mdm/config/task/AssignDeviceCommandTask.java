package com.adventnet.sym.server.mdm.config.task;

import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class AssignDeviceCommandTask implements SchedulerExecutionInterface
{
    public static Logger logger;
    
    public void executeTask(final Properties taskProps) {
        AssignCommandTaskProcessor.getTaskProcessor().assignDeviceCommandTask(taskProps);
    }
    
    static {
        AssignDeviceCommandTask.logger = Logger.getLogger("MDMConfigLogger");
    }
}
