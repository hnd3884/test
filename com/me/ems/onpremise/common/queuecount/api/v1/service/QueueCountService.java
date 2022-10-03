package com.me.ems.onpremise.common.queuecount.api.v1.service;

import java.util.Hashtable;
import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.Properties;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import com.me.devicemanagement.framework.server.queue.RedisQueueUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Map;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.onpremise.common.queuecount.core.QueueCountUtil;

public class QueueCountService
{
    public void refreshAllQueue() throws APIException {
        try {
            QueueCountUtil.refreshAllQueue();
        }
        catch (final Exception e) {
            throw new APIException("GENERIC0005");
        }
    }
    
    public void refreshQueue(final Map queueDetails) throws APIException {
        try {
            final String tableName = queueDetails.get("tableName");
            final String queueName = queueDetails.get("queueName");
            final long queueId = Long.parseLong(queueDetails.get("queueId").toString());
            long processTime = 0L;
            long lastDataTime = 0L;
            long count = 0L;
            long memoryCount = 0L;
            final boolean isRedis = Boolean.parseBoolean(SyMUtil.getSyMParameter("enableRedis"));
            if (isRedis) {
                final Properties props = RedisQueueUtil.getRedisQueueDetails(queueName);
                if (props != null) {
                    memoryCount = ((Hashtable<K, Long>)props).get("memoryCount");
                    count = ((Hashtable<K, Long>)props).get("totalCount");
                    processTime = ((Hashtable<K, Long>)props).get("firstTime");
                    lastDataTime = ((Hashtable<K, Long>)props).get("lastTime");
                }
            }
            else {
                count = QueueCountUtil.getQueueCount(tableName);
                processTime = QueueCountUtil.getProcessTime(tableName, 0);
                lastDataTime = QueueCountUtil.getProcessTime(tableName, 1);
                memoryCount = new DCQueueHandler().getMemoryCount(queueName);
            }
            QueueCountUtil.addOrUpdateQueueCountTable(queueId, count, memoryCount, processTime, lastDataTime);
        }
        catch (final Exception e) {
            throw new APIException("GENERIC0005");
        }
    }
    
    public void suspendQueue(final Map queueDetails) throws APIException {
        try {
            final String queueName = queueDetails.get("queueName");
            final DCQueue queue = DCQueueHandler.getQueue(queueName);
            final boolean queueSuspend = queue.isQueueSuspended();
            if (!queueSuspend) {
                queue.suspendQExecution();
            }
        }
        catch (final Exception e) {
            throw new APIException("GENERIC0005");
        }
    }
    
    public void resumeQueue(final Map queueDetails) throws APIException {
        try {
            final String queueName = queueDetails.get("queueName");
            final DCQueue queue = DCQueueHandler.getQueue(queueName);
            final boolean queueSuspend = queue.isQueueSuspended();
            if (queueSuspend) {
                queue.resumeQExecution();
            }
        }
        catch (final Exception e) {
            throw new APIException("GENERIC0005");
        }
    }
}
