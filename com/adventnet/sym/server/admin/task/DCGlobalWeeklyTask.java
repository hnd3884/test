package com.adventnet.sym.server.admin.task;

import com.adventnet.taskengine.TaskExecutionException;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DCGlobalWeeklyTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public DCGlobalWeeklyTask() {
        this.logger = Logger.getLogger(DCGlobalWeeklyTask.class.getName());
    }
    
    public void executeTask(final Properties taskProps) {
    }
    
    public void stopTask() throws TaskExecutionException {
    }
}
