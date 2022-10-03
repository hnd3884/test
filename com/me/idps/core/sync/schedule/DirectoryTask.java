package com.me.idps.core.sync.schedule;

import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.util.DirectoryUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DirectoryTask implements SchedulerExecutionInterface
{
    public void executeTask(final Properties properties) {
        DirectoryUtil.getInstance().clearActiveTransactionsIfAnyWithoutException();
        IDPSlogger.SYNC.log(Level.INFO, "Directory scheduler invoked.. passing on task to async queue");
        try {
            DirectoryUtil.getInstance().addTaskToQueue("adAsync-task", null, DirectoryUtil.getInstance().getQdataFromSchedulerProps(properties));
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
    }
}
