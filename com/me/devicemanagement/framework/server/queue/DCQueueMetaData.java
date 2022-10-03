package com.me.devicemanagement.framework.server.queue;

import java.io.Serializable;

public class DCQueueMetaData implements Serializable
{
    public String queueName;
    public String queueTableName;
    public String queueExtnTableName;
    public String priorityQRefTableName;
    public String queueClassName;
    public String processorClassName;
    public boolean isBulkProcessor;
    public String qHelperClassName;
    public String loggerName;
    public String qErrorLoggerName;
    public long qMaxSize;
    public long qMinSize;
    public int processThreadCount;
    public int processThreadMaxCount;
    public long keepAliveTimeout;
    public long delayBetweenProcessing;
    public boolean timeoutAllThreads;
    public long sleepBeweenQueueSizeCheck;
    public boolean autoStart;
    public boolean retainQDataInMemory;
    
    public DCQueueMetaData() {
        this.queueName = null;
        this.queueTableName = null;
        this.queueExtnTableName = null;
        this.priorityQRefTableName = null;
        this.queueClassName = null;
        this.processorClassName = null;
        this.isBulkProcessor = false;
        this.qHelperClassName = null;
        this.loggerName = null;
        this.qErrorLoggerName = "QProcessingErrorLog";
        this.qMaxSize = 100L;
        this.qMinSize = 50L;
        this.processThreadCount = 1;
        this.processThreadMaxCount = 1;
        this.keepAliveTimeout = 60L;
        this.delayBetweenProcessing = -1L;
        this.timeoutAllThreads = false;
        this.sleepBeweenQueueSizeCheck = 30000L;
        this.autoStart = true;
        this.retainQDataInMemory = false;
    }
    
    @Override
    public String toString() {
        return "queueName=" + this.queueName + "; queueClassName=" + this.queueClassName + "; delayBetweenProcessing=" + this.delayBetweenProcessing + "; processorClassName=" + this.processorClassName + "; loggerName=" + this.loggerName + "; qMaxSize=" + this.qMaxSize + "; qMinSize=" + this.qMinSize + "; queueTableName=" + this.queueTableName + "; queueExtnTableName=" + this.queueExtnTableName + "; priorityQueueTableName=" + this.priorityQRefTableName;
    }
}
