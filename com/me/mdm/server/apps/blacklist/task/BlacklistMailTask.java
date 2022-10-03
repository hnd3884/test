package com.me.mdm.server.apps.blacklist.task;

import java.util.Hashtable;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Properties;
import com.me.mdm.server.apps.blacklist.BlacklistMailUtils;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;

public class BlacklistMailTask implements CommonQueueProcessorInterface, SchedulerExecutionInterface
{
    Logger mdmLogger;
    
    public BlacklistMailTask() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void processData(final CommonQueueData data) {
        this.mdmLogger.log(Level.INFO, "BlacklistMailTask : Started task");
        new BlacklistMailUtils().sendDailyBlacklistMailToResources(data.getCustomerId());
    }
    
    public void executeTask(final Properties props) {
        this.mdmLogger.log(Level.INFO, "BlacklistMailTask : Async thread Started");
        final CommonQueueData tempData = new CommonQueueData();
        try {
            tempData.setJsonQueueData(new JSONObject((String)((Hashtable<K, String>)props).get("jsonParams")));
            tempData.setCustomerId(((Hashtable<K, Long>)props).get("customerId"));
            this.processData(tempData);
        }
        catch (final JSONException exp) {
            this.mdmLogger.log(Level.SEVERE, "Cannot fetch JSON from Props", (Throwable)exp);
        }
    }
}
