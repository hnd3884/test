package com.zoho.security.eventfw;

public class CalleeInfo
{
    private String monitoringClassName;
    private String monitoringMethodName;
    private String calleeClassName;
    private String calleeMethodName;
    
    public CalleeInfo(final String monitoringClassName, final String monitoringMethodName, final String calleeClassName, final String calleeMethodName) {
        this.monitoringClassName = monitoringClassName;
        this.monitoringMethodName = monitoringMethodName;
        this.calleeClassName = calleeClassName;
        this.calleeMethodName = calleeMethodName;
    }
    
    public String getMonitoringClassName() {
        return this.monitoringClassName;
    }
    
    public String getMonitoringMethodName() {
        return this.monitoringMethodName;
    }
    
    public String getCalleeClassName() {
        return this.calleeClassName;
    }
    
    public String getCalleeMethodName() {
        return this.calleeMethodName;
    }
}
