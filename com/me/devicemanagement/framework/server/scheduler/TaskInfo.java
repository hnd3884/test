package com.me.devicemanagement.framework.server.scheduler;

import java.util.Hashtable;
import java.util.Properties;

public class TaskInfo
{
    public Long taskId;
    public String taskName;
    public String className;
    public Long scheduleTime;
    public Integer transactionTime;
    public Properties userProps;
    public DCTaskListener listener;
    public String poolName;
    public String taskCompletionHandler;
    private Hashtable userHash;
    
    public TaskInfo() {
        this.taskId = null;
        this.taskName = null;
        this.className = null;
        this.scheduleTime = null;
        this.transactionTime = null;
        this.userProps = null;
        this.listener = null;
        this.poolName = null;
        this.taskCompletionHandler = null;
        this.userHash = null;
    }
    
    public void put(final Object key, final Object value) {
        if (this.userHash == null) {
            this.userHash = new Hashtable();
        }
        this.userHash.put(key, value);
    }
    
    public Object get(final Object key) {
        Object value = null;
        if (this.userHash != null) {
            value = this.userHash.get(key);
        }
        return value;
    }
}
