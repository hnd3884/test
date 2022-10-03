package com.me.devicemanagement.onpremise.server.common;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.logging.Logger;

public class FlashMessageUpdateTaskUtil
{
    private static FlashMessageUpdateTaskUtil flashMessageUpdateTaskUtil;
    private static Logger logger;
    
    private FlashMessageUpdateTaskUtil() {
    }
    
    public static FlashMessageUpdateTaskUtil getInstance() {
        return FlashMessageUpdateTaskUtil.flashMessageUpdateTaskUtil = ((FlashMessageUpdateTaskUtil.flashMessageUpdateTaskUtil == null) ? new FlashMessageUpdateTaskUtil() : FlashMessageUpdateTaskUtil.flashMessageUpdateTaskUtil);
    }
    
    public HashMap constructDefaultTaskProps() {
        final HashMap schedulerProps = new HashMap();
        schedulerProps.put("workflowName", "FlashMessageUpdateTask1");
        schedulerProps.put("schedulerName", "Flash_Message_Update_Schedule_1");
        schedulerProps.put("taskName", "FlashMessageUpdateTask1");
        schedulerProps.put("className", "com.me.devicemanagement.onpremise.server.common.FlashMessageUpdateTask");
        schedulerProps.put("schType", "Once");
        schedulerProps.put("operationType", "23");
        return schedulerProps;
    }
    
    public void deleteFlashMessageUpdateTask(final String scheduleName) {
        try {
            ApiFactoryProvider.getSchedulerAPI().removeScheduler(scheduleName);
        }
        catch (final Exception ex) {
            FlashMessageUpdateTaskUtil.logger.log(Level.SEVERE, "Exception while deleting Flash Message Update Task");
        }
    }
    
    static {
        FlashMessageUpdateTaskUtil.flashMessageUpdateTaskUtil = null;
        FlashMessageUpdateTaskUtil.logger = Logger.getLogger(FlashMessageUpdateTaskUtil.class.getName());
    }
}
