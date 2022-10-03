package com.me.mdm.server.android;

public class GoogleAPIConstants
{
    public static final String WHITELIST = "whitelist";
    public static final String DEVICE_REPORT_ENABLED = "deviceReportEnabled";
    
    public class AutoInstallMode
    {
        public static final String DO_NOT_AUTOINSTALL = "doNotAutoInstall";
        public static final String FORCE_AUTOINSTALL = "forceAutoInstall";
        public static final String AUTOINSTALL_ONCE = "autoInstallOnce";
    }
    
    public class AutoInstallConstraint
    {
        public static final String CHARGING_NOT_REQUIRED = "chargingNotRequired";
        public static final String CHARGING_REQUIRED = "chargingRequired";
        public static final String DEVICE_IDLE_NOT_REQUIRED = "deviceIdleNotRequired";
        public static final String DEVICE_IDLE_REQUIRED = "deviceIdleRequired";
        public static final String ANY_NETWORK = "anyNetwork";
        public static final String UNMETERED_NETWORK = "unmeteredNetwork";
    }
    
    public class AutoUpdateMode
    {
        public static final String DEFAULT = "autoUpdateDefault";
        public static final String HIGH_PRIORITY = "autoUpdateHighPriority";
        public static final String POSTPONED = "autoUpdatePostponed";
    }
}
