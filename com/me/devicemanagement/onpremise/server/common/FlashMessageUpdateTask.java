package com.me.devicemanagement.onpremise.server.common;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class FlashMessageUpdateTask implements SchedulerExecutionInterface
{
    private Logger logger;
    
    public FlashMessageUpdateTask() {
        this.logger = Logger.getLogger(FlashMessageUpdateTask.class.getName());
    }
    
    public void executeTask(final Properties props) {
        this.logger.log(Level.INFO, "%%%%%%%%%%%%%%%%%Executing Flash Message Update Task @" + System.currentTimeMillis() + "%%%%%%%%%%%%%%%%%");
        final String currSchedule = ((Hashtable<K, String>)props).get("scheduleName");
        if (currSchedule.equals("Flash_Message_Update_Schedule_1")) {
            FlashMessageUpdateTaskUtil.getInstance().deleteFlashMessageUpdateTask("Flash_Message_Update_Schedule_2");
        }
        else if (currSchedule.equals("Flash_Message_Update_Schedule_2")) {
            FlashMessageUpdateTaskUtil.getInstance().deleteFlashMessageUpdateTask("Flash_Message_Update_Schedule_1");
        }
        FlashMessage flashMessage = null;
        final String[] flashMessageClasses = ProductClassLoader.getMultiImplProductClass("DM_FLASH_MESSAGE_CLASS");
        if (flashMessageClasses.length > 0) {
            try {
                flashMessage = (FlashMessage)Class.forName(flashMessageClasses[0]).newInstance();
            }
            catch (final Exception e) {
                this.logger.log(Level.SEVERE, "Exception while getting flash message class name", e);
            }
        }
        if (flashMessage != null) {
            flashMessage.checkAndDownloadFlashMessage(false);
        }
        this.logger.log(Level.INFO, "Flash Message Update Task Executed");
    }
}
