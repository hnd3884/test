package com.me.devicemanagement.framework.server.eventlog;

public class EventLogThreadLocal
{
    static ThreadLocal<String> sourceIpAddress;
    static ThreadLocal<String> sourceHostName;
    
    public static String getSourceIpAddress() {
        return EventLogThreadLocal.sourceIpAddress.get();
    }
    
    public static void setSourceIpAddress(final String sourceIp) {
        EventLogThreadLocal.sourceIpAddress.set(sourceIp);
    }
    
    public static String getSourceHostName() {
        return EventLogThreadLocal.sourceHostName.get();
    }
    
    public static void setSourceHostName(final String sourceHost) {
        EventLogThreadLocal.sourceHostName.set(sourceHost);
    }
    
    public static void clearEventThreadLocalDetails() {
        EventLogThreadLocal.sourceIpAddress.remove();
        EventLogThreadLocal.sourceHostName.remove();
    }
    
    static {
        EventLogThreadLocal.sourceIpAddress = new ThreadLocal<String>();
        EventLogThreadLocal.sourceHostName = new ThreadLocal<String>();
    }
}
