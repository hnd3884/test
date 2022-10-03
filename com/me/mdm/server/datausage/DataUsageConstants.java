package com.me.mdm.server.datausage;

public class DataUsageConstants
{
    public static final String DATA_USAGES = "DataUsages";
    public static final String OLD_DATA_DELETE_TIME = "DataUsageDeletePeriod";
    public static final String OLD_DATA_DELETE_OVERRIDE = "DataUsageDeleteOverride";
    
    public static class DataUsages
    {
        public static final String USAGE_FROM_DATE = "UsageFrom";
        public static final String USAGE_TO_DATE = "UsageTo";
        public static final String AGENT_COMPUTED_TIME = "AgentComputedTime";
        public static final String MANAGED_APP_USAGE = "TotalManagedAppUsage";
        public static final String UNMANAGED_APP_USAGE = "TotalUnManagedAppUsage";
        public static final String DEVICE_USAGE = "FullDeviceUsage";
        public static final String PER_APP_USAGES = "PerAppUsages";
        public static final String PACKAGE_NAME = "PackageName";
        
        public class DataUsage
        {
            public static final String TOTAL = "Total";
            public static final String ROAMING = "Roaming";
            public static final String WIFI = "WiFi";
            public static final String MOBILE = "Mobile";
        }
        
        public static class DataEntities
        {
            public static final String DEVICE_USAGE = "data.device.full";
            public static final String DEVICE_MANAGED = "data.device.managed";
            public static final String DEVICE_UNMANAGED = "data.device.unmanaged";
            public static final Integer TOTAL_TYPE;
            public static final Integer ROAMING_TYPE;
            public static final Integer APP_TYPE;
            public static final Integer WIFI_TYPE;
            public static final Integer MOBILE_TYPE;
            public static final Integer NO_BIAS;
            
            static {
                TOTAL_TYPE = 1;
                ROAMING_TYPE = 4;
                APP_TYPE = 16;
                WIFI_TYPE = 8;
                MOBILE_TYPE = 2;
                NO_BIAS = 0;
            }
        }
    }
}
