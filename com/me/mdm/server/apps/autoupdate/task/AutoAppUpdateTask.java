package com.me.mdm.server.apps.autoupdate.task;

import java.util.Hashtable;
import java.util.List;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.apps.MDMAppMgmtHandler;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONException;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueData;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;
import com.adventnet.sym.server.mdm.queue.commonqueue.CommonQueueProcessorInterface;

public class AutoAppUpdateTask implements CommonQueueProcessorInterface, SchedulerExecutionInterface
{
    Logger mdmLogger;
    
    public AutoAppUpdateTask() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    public void executeTask(final Properties properties) {
        final CommonQueueData tempData = new CommonQueueData();
        try {
            tempData.setJsonQueueData(new JSONObject((String)((Hashtable<K, String>)properties).get("jsonParams")));
            tempData.setCustomerId(((Hashtable<K, Long>)properties).get("customerId"));
            this.processData(tempData);
        }
        catch (final JSONException exp) {
            this.mdmLogger.log(Level.SEVERE, "Cannot fetch JSON from Props", (Throwable)exp);
        }
        catch (final Exception e) {
            this.mdmLogger.log(Level.SEVERE, "Couldn't restrict auto update", e);
        }
    }
    
    @Override
    public void processData(final CommonQueueData data) {
        final Long customerId = data.getCustomerId();
        final JSONObject jsonObject = data.getJsonQueueData();
        try {
            final JSONArray jsonArray = jsonObject.getJSONArray("appGroupList");
            final List<Long> appGroupList = new JSONUtil().convertLongJSONArrayTOList(jsonArray);
            this.mdmLogger.log(Level.INFO, "Automated Update of App Group {0}: Started Task", appGroupList);
            MDMAppMgmtHandler.getInstance().performAutoAppUpdate(appGroupList, customerId);
        }
        catch (final JSONException ex) {
            this.mdmLogger.log(Level.SEVERE, "Automated Update process failure  : JSONError", (Throwable)ex);
        }
        catch (final Exception ex2) {
            this.mdmLogger.log(Level.SEVERE, "Automated Update process failure  : ", ex2);
        }
    }
}
