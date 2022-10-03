package com.me.mdm.onpremise.server.admin.task;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.me.mdm.onpremise.server.android.agent.AndroidAgentSecretsHandler;
import com.me.mdm.server.util.CloudAPIDataPost;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class MDMOPDistributedDailySchedulerTask implements SchedulerExecutionInterface
{
    private static final Logger MDMLOGGER;
    
    public void executeTask(final Properties taskProps) {
        MDMOPDistributedDailySchedulerTask.MDMLOGGER.log(Level.INFO, "MDMOPDistributedDailySchedulerTask starts");
        MDMOPDistributedDailySchedulerTask.MDMLOGGER.log(Level.INFO, "TASK 1 - To get the code starts");
        final CloudAPIDataPost hashCodeForSecureKeys = new CloudAPIDataPost();
        final Properties prop = hashCodeForSecureKeys.getCodeForSecureKeys();
        MDMOPDistributedDailySchedulerTask.MDMLOGGER.log(Level.INFO, "TASK 1 - To get the code Ends");
        MDMOPDistributedDailySchedulerTask.MDMLOGGER.log(Level.INFO, "TASK 2 - MDM Task to update ELM Keys starts");
        try {
            final String elmTimeStamp = ((Hashtable<K, String>)prop).get("ELM");
            AndroidAgentSecretsHandler.getInstance().checkForELMKeyUpdateInCreator(elmTimeStamp);
        }
        catch (final Exception e) {
            MDMOPDistributedDailySchedulerTask.MDMLOGGER.log(Level.SEVERE, "Exception in checkForELMKeyUpdateInCreator", e);
        }
        MDMOPDistributedDailySchedulerTask.MDMLOGGER.log(Level.INFO, "TASK 2 - MDM Task to update ELM Keys starts");
        MDMOPDistributedDailySchedulerTask.MDMLOGGER.log(Level.INFO, "TASK 3 - MDM Task to update FCM Agent Keys starts");
        try {
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", "IOSFCMCreatorTask");
            taskInfoMap.put("poolName", "mdmPool");
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronousWithDelay("com.me.mdm.onpremise.notification.IOSFCMNotificationCreatorHandler", taskInfoMap, prop);
        }
        catch (final Exception e) {
            MDMOPDistributedDailySchedulerTask.MDMLOGGER.log(Level.SEVERE, "Exception in IOSFCMCreatorTask", e);
        }
        MDMOPDistributedDailySchedulerTask.MDMLOGGER.log(Level.INFO, "TASK 3 - MDM Task to update FCM Agent Keys ends");
    }
    
    static {
        MDMLOGGER = Logger.getLogger("MDMLogger");
    }
}
