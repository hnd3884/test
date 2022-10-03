package com.adventnet.sym.server.mdm.ios;

public class AppleCommandsRequestTypeConstants extends MDMRequestTypeConstants
{
    public static class Settings
    {
        public static final String REQUEST_TYPE = "Settings";
        
        public static class SettingsProperties
        {
            public static final String ITEM = "SharedDeviceConfiguration";
            
            public static class SharedDeviceConfiguration
            {
                public static final String QUOTA_SIZE = "QuotaSize";
                public static final String RESIDENT_USERS = "ResidentUsers";
            }
        }
    }
}
