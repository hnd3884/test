package com.me.idps.core.sync.asynch;

import com.me.idps.core.util.DirectoryResetHandler;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.idps.core.util.DirectoryUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.simple.JSONObject;

class DirectorySchedulerImpl
{
    private static DirectorySchedulerImpl directorySchedulerImpl;
    
    static DirectorySchedulerImpl getInstance() {
        if (DirectorySchedulerImpl.directorySchedulerImpl == null) {
            DirectorySchedulerImpl.directorySchedulerImpl = new DirectorySchedulerImpl();
        }
        return DirectorySchedulerImpl.directorySchedulerImpl;
    }
    
    void executeTask(final JSONObject taskDetails) {
        try {
            String taskType = null;
            boolean isCollationTask = false;
            taskDetails.remove((Object)"TASK_TYPE");
            if (taskDetails.containsKey((Object)"SCHEDULER_TASK")) {
                taskType = (String)taskDetails.get((Object)"SCHEDULER_TASK");
                if (!SyMUtil.isStringEmpty(taskType) && taskType.equalsIgnoreCase("collate")) {
                    isCollationTask = true;
                }
            }
            if (isCollationTask) {
                final JSONObject qData = new JSONObject();
                qData.put((Object)"TASK_TYPE", (Object)taskType);
                qData.put((Object)"COLLATE_REQUEST_ID", taskDetails.get((Object)"COLLATE_REQUEST_ID"));
                final Properties dmDomainProps = (Properties)DirectoryUtil.getInstance().convertObj(taskDetails, new Properties());
                DirectoryUtil.getInstance().addTaskToQueue("adProc-task", dmDomainProps, qData);
            }
            else {
                IDPSlogger.AUDIT.log(Level.INFO, "Directory Sync Scheduler");
                DirectoryResetHandler.getInstance().reset(true);
            }
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.SEVERE, null, ex);
        }
    }
    
    static {
        DirectorySchedulerImpl.directorySchedulerImpl = null;
    }
}
