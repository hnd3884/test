package com.me.mdm.api.inventory;

public class FeatureSettingConstants
{
    public static class Api
    {
        public static class Key
        {
            public static String feature_type;
            public static String apply_to_all;
            public static String is_enabled;
            public static String groups;
            public static String group_id;
            public static String groups_info;
            public static String name;
            public static String feature_response;
            public static String groups_to_be_notified;
            public static String is_previously_enabled_for_all;
            
            static {
                Key.feature_type = "feature_type";
                Key.apply_to_all = "apply_to_all";
                Key.is_enabled = "is_enabled";
                Key.groups = "groups";
                Key.group_id = "group_id";
                Key.groups_info = "groups_info";
                Key.name = "name";
                Key.feature_response = "feature_response";
                Key.groups_to_be_notified = "groups_to_be_notified";
                Key.is_previously_enabled_for_all = "is_previously_enabled_for_all";
            }
        }
    }
    
    public static class Battery
    {
        public static String battery_tracking_interval;
        public static String history_deletion_interval;
        
        static {
            Battery.battery_tracking_interval = "battery_tracking_interval";
            Battery.history_deletion_interval = "history_deletion_interval";
        }
    }
    
    public static class FeatureType
    {
        public static final int BATTERY_SETTINGS = 1;
    }
    
    public static class General
    {
        public static final String SETTINGS_ID = "SETTINGS_ID";
        public static final int INCLUDE_GROUP = 1;
        public static final int EXCLUDE_GROUP = 2;
    }
}
