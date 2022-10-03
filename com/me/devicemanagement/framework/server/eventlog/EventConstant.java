package com.me.devicemanagement.framework.server.eventlog;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class EventConstant
{
    public static final int GENERAL_RUNTIME_INFO = 121;
    public static final int GENERAL_RUNTIME_ERROR = 122;
    public static final int ROLE_ADDED_SUCCESS = 707;
    public static final int ROLE_ADDED_FAILURE = 708;
    public static final int ROLE_MODIFY_SUCCESS = 709;
    public static final int ROLE_MODIFY_FAILURE = 710;
    public static final int ROLE_DELETE_SUCCESS = 711;
    public static final int ROLE_DELETE_FAILURE = 712;
    public static final int USER_ADDED_SUCCESS = 701;
    public static final int USER_ADDED_FAILURE = 702;
    public static final int USER_MODIFY_SUCCESS = 703;
    public static final int USER_MODIFY_FAILURE = 704;
    public static final int USER_DELETE_SUCCESS = 705;
    public static final int USER_DELETE_FAILURE = 706;
    public static final int TECH_LOGIN_SUCCESS = 718;
    public static final int TECH_LOGOUT_SUCCESS = 719;
    public static final int USER_LOGIN_THRESHOLD_REACHED = 720;
    public static final int CUSTOMER_ADDED_SUCCESS = 801;
    public static final int CUSTOMER_ADDED_FAILURE = 802;
    public static final int CUSTOMER_DELETE_SUCCESS = 803;
    public static final int CUSTOMER_DELETE_FAILURE = 804;
    public static final int MDM_GROUP_MODIFIED = 2072;
    public static final int CUSTOM_GROUP_ADDED = 1201;
    public static final int CUSTOM_GROUP_DELETED = 1203;
    public static final int CUSTOM_GROUP_MODIFIED = 1202;
    public static final int CUSTOM_GROUP_COMP_SHARE_SETTINGS_ENABLED = 1204;
    public static final int CUSTOM_GROUP_COMP_SHARE_SETTINGS_DISABLED = 1205;
    public static final int CUSTOM_GROUP_COPIED = 1206;
    public static final int CUSTOM_GROUP_USER_SHARE_SETTINGS_ENABLED = 1207;
    public static final int CUSTOM_GROUP_USER_SHARE_SETTINGS_DISABLED = 1208;
    public static final int MDM_GROUP_ADDED = 2071;
    public static final int MDM_GROUP_DELETED = 2073;
    public static final int PROXY_SETTINGS = 121;
    public static final int ADMIN_SETTINGS_INFO = 4001;
    public static final int MAIL_SENDING_FAILED = 5000;
    public static final String DC_SYSTEM_USER;
    public static final int MOBILE_APP_LOGIN_EVENT = 716;
    public static final int TWO_FACTOR_AUTH_MODIFIED = 715;
    public static final int DC_VIEWFILTER_SAVE_SUCCESS = 6000;
    public static final int DC_VIEWFILTER_SAVE_FAILURE = 6001;
    public static final int DC_VIEWFILTER_DELETE_SUCCESS = 6002;
    public static final int DC_VIEWFILTER_DELETE_FAILURE = 6003;
    public static final int DC_VIEWFILTER_RENAME_SUCCESS = 6004;
    public static final int DC_VIEWFILTER_RENAME_FAILURE = 6005;
    public static final int PASSWORD_CHANGE_SUCCESS = 713;
    public static final int PASSWORD_CHANGE_FAILURE = 714;
    public static final int USER_EXIST = 717;
    public static final int USER_EMAIL_EXIST = 720;
    public static final int REMOVE_DOMAIN_SUCCESS = 208;
    public static final int REMOVE_DOMAIN_FAILURE = 209;
    public static final int FOS_SETTINGS_SAVED = 7001;
    public static final int FOS_TRIAL_ENABLED = 7003;
    public static final int FWS_INSTALLED = 10501;
    public static final int FWS_UNINSTALLED = 10502;
    public static final int SCHEDULE_REPORT_CREATE = 786;
    public static final int SCHEDULE_REPORT_MODIFY = 787;
    public static final int SCHEDULE_REPORT_DELETE = 788;
    public static final int SCHEDULE_REPORT_EXECUTE = 799;
    public static final int ADD_CUSTOM_COLUMN = 2201;
    public static final int MODIFY_CUSTOM_COLUMN = 2202;
    public static final int DELETE_CUSTOM_COLUMN = 2203;
    public static final int ADD_CUSTOM_DATATYPE = 2204;
    public static final int MODIFY_CUSTOM_COL_VALUE = 2205;
    public static final int BULK_UPDATE = 2206;
    public static final int PASSWORD_POLICY_UPDATE = 8000;
    public static final int NETWORKDEVCE_CUSTOM_GROUP_ADDED = 9000;
    public static final int NETWORKDEVCE_CUSTOM_GROUP_DELETED = 9001;
    public static final int NETWORKDEVCE_CUSTOM_GROUP_MODIFIED = 9002;
    public static final int NETWORKDEVCE_CUSTOM_GROUP_COPIED = 9003;
    
    static {
        DC_SYSTEM_USER = ApiFactoryProvider.getAuthUtilAccessAPI().getSystemUserName();
    }
}
