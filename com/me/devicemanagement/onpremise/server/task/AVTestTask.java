package com.me.devicemanagement.onpremise.server.task;

import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class AVTestTask implements SchedulerExecutionInterface
{
    Logger logger;
    
    public AVTestTask() {
        this.logger = Logger.getLogger(AVTestTask.class.getName());
    }
    
    public void executeTask(final Properties props) {
    }
}
