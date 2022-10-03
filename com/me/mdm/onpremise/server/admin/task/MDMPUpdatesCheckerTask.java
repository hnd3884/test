package com.me.mdm.onpremise.server.admin.task;

import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import com.me.mdm.onpremise.server.admin.MDMPFlashMessageConstants;
import com.me.devicemanagement.framework.server.util.UpdatesParamUtil;
import java.util.logging.Level;
import com.me.mdm.onpremise.server.admin.MDMPFlashMessage;
import java.util.Properties;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;
import com.me.devicemanagement.onpremise.server.common.ProductUpdatesCheckerTask;

public class MDMPUpdatesCheckerTask extends ProductUpdatesCheckerTask implements CommonQueueProcessorInterface
{
    private Logger logger;
    
    public MDMPUpdatesCheckerTask() {
        this.logger = Logger.getLogger(MDMPUpdatesCheckerTask.class.getName());
    }
    
    public void executeTask(final Properties taskProps) {
        final MDMPFlashMessage flashMessage = new MDMPFlashMessage();
        flashMessage.downloadAndInitialise();
        this.updateProductSpecificDetails(flashMessage.getOtherProps());
        this.logger.log(Level.INFO, "MDMPUpdatesCheckerTask : executeTask : calls Super UpdatesCheckerTask  ");
        super.executeTask(taskProps);
        final String updates_checker_task = UpdatesParamUtil.getUpdParameter("MDMPUpdatesCheckerTask");
        if (updates_checker_task != null && updates_checker_task.equalsIgnoreCase("started")) {
            UpdatesParamUtil.updateUpdParams("MDMPUpdatesCheckerTask", "success");
            synchronized (MDMPFlashMessageConstants.TASKCHECKER) {
                try {
                    MDMPFlashMessageConstants.TASKCHECKER.notify();
                }
                catch (final Exception e) {
                    this.logger.log(Level.WARNING, "Exception occurred inside synchronized block: ", e);
                }
            }
        }
    }
    
    public void processData(final CommonQueueData data) {
        if (data.getTaskName().equalsIgnoreCase("MDMPUpdatesCheckerTask")) {
            this.executeTask(new Properties());
        }
    }
}
