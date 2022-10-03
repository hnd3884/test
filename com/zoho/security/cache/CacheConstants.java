package com.zoho.security.cache;

public class CacheConstants
{
    public static final String THROTTLES_LOCK_KEY_SUFFIX = "l";
    public static final String THROTTLE_WINDOW_START_TIME_INTERVAL_KEY_SUFFIX = "tvit";
    public static final String THROTTLE_VIOLATION_TIME_LIST_KEY_SUFFIX = "tvt";
    public static final String LIVE_WINDOW_SERVICE_MAP_ID_PREFIX = "LW_SM";
    public static final String LIVE_WINDOW_SERVICE_APP_MAP_ID_PREFIX = "LW_SAM";
    public static final String LIVE_WINDOW_SERVICE_APP_HOST_SET_ID_PREFIX = "LW_SAHS";
    public static final String LIVE_WINDOW_CLEANER_LAST_START_TIME_INFO_MAP_ID = "LW_CLSTI";
    public static final String LIVE_WINDOW_CLEANER_SCHEDULER_MAP = "LW_CSM";
    
    public enum CacheType
    {
        local, 
        central;
    }
    
    public enum PoolNames
    {
        ROLLING, 
        FIXED, 
        SLIDING, 
        LIVE, 
        HIP;
    }
}
