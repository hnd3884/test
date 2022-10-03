package com.me.devicemanagement.framework.server.alerts;

public class AlertConstants
{
    public static final String ALERT_LEVEL_INFO = "INFORMATION";
    public static final Long MONITOR_ADDED;
    public static final Long MOTHERBOARD_REMOVED;
    public static final String CONFIG_ALERT_NAME = "config-alert";
    
    static {
        MONITOR_ADDED = 17L;
        MOTHERBOARD_REMOVED = 64L;
    }
}
