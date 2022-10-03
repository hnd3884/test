package com.adventnet.sym.server.mdm.queue.commonqueue;

import java.util.Hashtable;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.Map;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.queue.DCQueueData;
import java.util.logging.Logger;

public class CommonQueueUtil
{
    private static CommonQueueUtil util;
    Logger mdmCommonQueueLog;
    String separator;
    
    public CommonQueueUtil() {
        this.mdmCommonQueueLog = Logger.getLogger("MDMCommonQueueLogger");
        this.separator = "\t";
    }
    
    public static CommonQueueUtil getInstance() {
        return CommonQueueUtil.util;
    }
    
    public void addToQueue(final CommonQueueData data, final CommonQueues queueName) throws Exception {
        if (!this.validateDataBeforeAddingToQueue(data)) {
            throw new Exception("All the fields are mandatory and must be present before adding to queue");
        }
        if (this.isCommonQueueFeatureOn()) {
            final long postTime = System.currentTimeMillis();
            final String fileName = data.getTaskName() + "-" + postTime + ".txt";
            final DCQueueData qData = new DCQueueData();
            qData.fileName = fileName;
            qData.queueData = data.getJsonQueueData().toString();
            qData.customerID = data.getCustomerId();
            qData.postTime = postTime;
            final Map extnTableDetails = new HashMap();
            extnTableDetails.put("CLASS_NAME", data.getClassName());
            extnTableDetails.put("CUSTOMER_ID", data.getCustomerId());
            extnTableDetails.put("COMMAND_NAME", data.getTaskName());
            qData.queueExtnTableData = extnTableDetails;
            final DCQueue commonQueue = DCQueueHandler.getQueue(queueName.getQueueName());
            commonQueue.addToQueue(qData);
            this.mdmCommonQueueLog.log(Level.INFO, "AddedToQueue {0}{1}{2}PostTime : {3}{4}{5}", new Object[] { this.separator, qData.fileName, this.separator, String.valueOf(qData.postTime), this.separator, queueName.getQueueName() });
        }
        else {
            final HashMap taskInfoMap = new HashMap();
            taskInfoMap.put("taskName", data.getTaskName());
            taskInfoMap.put("schedulerTime", System.currentTimeMillis());
            taskInfoMap.put("poolName", "asynchThreadPool");
            final Properties taskProps = new Properties();
            ((Hashtable<String, String>)taskProps).put("jsonParams", data.getJsonQueueData().toString());
            ((Hashtable<String, Long>)taskProps).put("customerId", data.getCustomerId());
            ((Hashtable<String, String>)taskProps).put("taskName", data.getTaskName());
            ApiFactoryProvider.getSchedulerAPI().executeAsynchronously(data.getClassName(), taskInfoMap, taskProps);
        }
    }
    
    private boolean validateDataBeforeAddingToQueue(final CommonQueueData data) {
        if (data.getClassName() == null || data.getCustomerId() == null || data.getJsonQueueData() == null) {
            return false;
        }
        if (data.getTaskName() != null) {
            return true;
        }
        data.setTaskName(data.getClassName());
        return true;
    }
    
    public boolean isCommonQueueFeatureOn() {
        return MDMFeatureParamsHandler.getInstance().isFeatureAvailableGlobally("CommonQueueSwitch", true);
    }
    
    static {
        CommonQueueUtil.util = new CommonQueueUtil();
    }
}
