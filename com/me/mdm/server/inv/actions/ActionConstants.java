package com.me.mdm.server.inv.actions;

public class ActionConstants
{
    public static final String DAYS = "days";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String SCAN = "scan";
    public static final String LOCK = "lock";
    public static final String REMOTE_CONTROL = "remote_control";
    public static final String REMOTE_VIEW = "remote_view";
    public static final String REMOTE_ALARM = "remote_alarm";
    public static final String COMPLETE_WIPE = "complete_wipe";
    public static final String CORPORATE_WIPE = "corporate_wipe";
    public static final String CLEAR_PASSCODE = "clear_passcode";
    public static final String RESET_PASSCODE = "reset_passcode";
    public static final String FETCH_LOCATION = "fetch_location";
    public static final String SHUTDOWN = "shutdown";
    public static final String SCHEDULED_SHUTDOWN = "scheduled_shutdown";
    public static final String RESTART = "restart";
    public static final String SCHEDULED_RESTART = "scheduled_restart";
    public static final String MAC_UNLOCK_USER_ACCOUNT = "unlock_user_account";
    public static final String MAC_FILEVAULT_PERSONAL_KEY_ROTATE = "rotate_filevault_personal_key";
    public static final String ENABLE_LOST_MODE = "enable_lost_mode";
    public static final String DISABLE_LOST_MODE = "disable_lost_mode";
    public static final String PAUSE_KIOSK = "pause_kiosk";
    public static final String RE_APPLY_KIOSK = "re_apply_kiosk";
    public static final String CLEAR_APP_DATA = "clear_app_data";
    public static final String KNOX_COMMAND_CREATE_CONTAINER = "create_container";
    public static final String KNOX_COMMAND_REMOVE_CONTAINER = "remove_container";
    public static final String KNOX_COMMAND_LOCK_CONTAINER = "lock_container";
    public static final String KNOX_COMMAND_UNLOCK_CONTAINER = "unlock_container";
    public static final String REMOTE_DEBUG = "remote_debug";
    public static final String KNOX_COMMAND_CLEAR_CONTAINER_PASSWORD = "clear_container_password";
    public static final String LOGOUT_USER = "logout_user";
    public static final String SUCCESS_LIST = "success_list";
    public static final String NA_LIST = "na_list";
    public static final String DEVICE_ID = "device_id";
    public static final String SCHEDULED_ACTION = "scheduled";
    public static final String STATUS = "status";
    public static final String REMARKS = "remarks";
    public static final String TOTAL_COUNT = "total_count";
    public static final String SUCCESS_COUNT = "success_count";
    public static final String NOT_APPLICABLE_COUNT = "not_applicable_count";
    public static final String[] ACTIONS_LIST;
    public static final String[] KNOX_ACTIONS_LIST;
    public static final String ACTION_ID = "action_id";
    public static final String ACTION = "action";
    public static final String ACTIONS = "actions";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String PLATFORM_TYPE = "PLATFORM_TYPE";
    public static final String VALID_DEVICES = "valid_devices";
    public static final String REASON_MESSAGE = "reason_message";
    public static final String APP_GROUP_IDS = "app_group_ids";
    public static final String APP_IDS = "app_ids";
    public static final String IS_GROUP_ACTION = "is_group_action";
    public static final String INCLUSION = "inclusion";
    public static final String CLEAR_DATA_FOR_ALL_APPS = "clear_data_for_all_apps";
    public static final String TOTAL_DEVICES = "total_devices";
    public static final String APPLICABLE_DEVICES = "applicable_devices";
    public static final int SHUTDOWN_ACTION = 0;
    public static final int RESTART_ACTION = 1;
    public static final String[] BULK_ACTION_LIST;
    
    static {
        ACTIONS_LIST = new String[] { "scan", "lock", "remote_control", "remote_view", "remote_alarm", "complete_wipe", "corporate_wipe", "clear_passcode", "reset_passcode", "fetch_location", "enable_lost_mode", "disable_lost_mode", "restart", "shutdown", "pause_kiosk", "re_apply_kiosk", "remote_debug", "unlock_user_account", "clear_app_data", "rotate_filevault_personal_key", "logout_user" };
        KNOX_ACTIONS_LIST = new String[] { "create_container", "remove_container", "lock_container", "unlock_container", "clear_container_password" };
        BULK_ACTION_LIST = new String[] { "shutdown", "restart", "clear_app_data", "enable_lost_mode", "disable_lost_mode" };
    }
}
