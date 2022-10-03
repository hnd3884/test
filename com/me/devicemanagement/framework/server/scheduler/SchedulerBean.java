package com.me.devicemanagement.framework.server.scheduler;

public class SchedulerBean
{
    String scheduleJsonObj;
    String workflowName;
    String scheduleName;
    String className;
    
    public String getClassName() {
        return this.className;
    }
    
    public void setClassName(final String className) {
        this.className = className;
    }
    
    public String getWorkflowName() {
        return this.workflowName;
    }
    
    public void setWorkflowName(final String workflowName) {
        this.workflowName = workflowName;
    }
    
    public String getScheduleName() {
        return this.scheduleName;
    }
    
    public void setScheduleName(final String scheduleName) {
        this.scheduleName = scheduleName;
    }
    
    public String getScheduleJsonObj() {
        return this.scheduleJsonObj;
    }
    
    public void setScheduleJsonObj(final String scheduleJsonObj) {
        this.scheduleJsonObj = scheduleJsonObj;
    }
}
