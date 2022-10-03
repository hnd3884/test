package com.me.devicemanagement.framework.server.queue;

import java.util.Map;
import java.io.Serializable;

public class DCQueueData implements Serializable
{
    public Long queueDataId;
    public int queueDataType;
    public long postTime;
    public Object queueData;
    public String fileName;
    public boolean isRedis;
    public boolean overwriteFile;
    public Long customerID;
    public String zaaid;
    public Long remoteOfficeID;
    public boolean priority;
    public boolean isCompressed;
    public String loggingId;
    public Map<String, Object> queueExtnTableData;
    public Map<String, Object> priorityQRefTableData;
    
    public DCQueueData() {
        this.queueDataId = null;
        this.queueDataType = -1;
        this.postTime = -1L;
        this.queueData = null;
        this.fileName = null;
        this.isRedis = false;
        this.overwriteFile = false;
        this.customerID = null;
        this.zaaid = null;
        this.remoteOfficeID = null;
        this.priority = false;
        this.isCompressed = false;
        this.loggingId = null;
        this.queueExtnTableData = null;
        this.priorityQRefTableData = null;
    }
    
    @Override
    public String toString() {
        return "queueId=" + this.queueDataId + "; fileName=" + this.fileName + "; postTime=" + this.postTime + "; queueDataType=" + this.queueDataType + "; queueExtnTableData=" + this.queueExtnTableData;
    }
}
