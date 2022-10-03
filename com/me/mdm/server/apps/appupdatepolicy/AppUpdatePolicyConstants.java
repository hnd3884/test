package com.me.mdm.server.apps.appupdatepolicy;

public class AppUpdatePolicyConstants
{
    public static class SchedulerConstants
    {
        public static String schedulerClass;
        
        static {
            SchedulerConstants.schedulerClass = "com.me.mdm.server.apps.scheduledapptask.ScheduledAppUpdateTask";
        }
    }
    
    public static class ApprovedVersionStatusConstants
    {
        public static final int YET_TO_DISTRIBUTE_UPDATE = 1;
        public static final int APP_UPDATE_SCHEDULED = 2;
        public static final int UPDATE_DISTRIBUTED = 3;
    }
    
    public static class APIConstants
    {
        public static final String POLICY_PATH_PARAM = "app_update_policy_id";
        public static final String PACKAGE_PATH_PARAM = "package_ids";
        public static final String GROUP_PATH_PARAM = "group_ids";
        public static final String POLICY_ID_AUTHORIZER = "com.me.mdm.server.apps.appupdatepolicy.AppUpdatePolicyAuthorizer";
        public static final String POLICY_NAME = "policy_name";
        public static final String DISTRIBUTION_TYPE = "distribution_type";
        public static final String POLICY_TYPE = "policy_type";
        public static final String DESCRIPTION = "description";
        public static final String INCLUSION_FLAG = "inclusion_flag";
        public static final String ALL_APPS = "all_apps";
        public static final String PACKAGE_LIST = "package_list";
        public static final String TIME_ZONE = "time_zone";
        public static final String WINDOW_START_TIME = "window_start_time";
        public static final String WINDOW_END_TIME = "window_end_time";
        public static final String SCHEDULE_PARAMS = "schedule_params";
        public static final String IS_SILENT_INSTALL = "is_silent_install";
        public static final String IS_NOTIFY_USER = "is_notify_user";
        public static final String APP_UPDATE_POLICY_ID = "app_update_policy_id";
        public static final String CREATION_TIME = "creation_time";
        public static final String MODIFIED_TIME = "modified_time";
        public static final String CREATED_USER = "created_user";
        public static final String MODIFIED_USER = "modified_user";
        public static final String APP_DETAILS = "app_details";
        public static final String ASSOCIATED_GROUP_COUNT = "associated_group_count";
        public static final String IS_STORE_APP_POLICY = "is_store_app_policy";
    }
    
    public static class StoreAppPolicy
    {
        public static final String POLICY_NAME = "Store Apps - update policy";
    }
    
    public static class DistributionType
    {
        public static final Integer MANUAL_DISTRIBUTION;
        public static final Integer AUTOMATIC_DISTRIBUTION;
        
        static {
            MANUAL_DISTRIBUTION = 1;
            AUTOMATIC_DISTRIBUTION = 2;
        }
    }
    
    public static class PolicyType
    {
        public static final Integer ANYTIME;
        public static final Integer SCHEDULED;
        
        static {
            ANYTIME = 1;
            SCHEDULED = 2;
        }
    }
    
    public static class Properties
    {
        public static final String IS_SCHEDULE = "isSchedule";
        public static final String FORCE_UPDATE = "forceUpdate";
    }
}
