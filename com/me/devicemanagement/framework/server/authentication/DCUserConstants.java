package com.me.devicemanagement.framework.server.authentication;

public class DCUserConstants
{
    public static final String ADMIN_ROLE = "Common_Write";
    public static final String ALL_MANAGED_COMPUTER_ROLE = "All_Managed_Computer";
    public static final String ALL_MANAGED_NETWORKDEVICES = "All_Managed_NetworkDevices";
    public static final String PATCH_EDITION_ROLE = "Patch_Edition_Role";
    public static final int VISIBLE_ROLE;
    public static final int HIDDEN_ROLE;
    public static final int MSP_ROLE;
    public static final String DUMMY_USER = "dummy";
    public static final String PATCHMGMT_ADMIN = "PatchMgmt_Admin";
    public static final String PATCHMGMT_READ = "PatchMgmt_Read";
    public static final String VULNERABILITYMGMT_ADMIN = "VulnerabilityMgmt_Admin";
    public static final String VULNERABILITYMGMT_WRITE = "VulnerabilityMgmt_Write";
    public static final String VULNERABILITYMGMT_READ = "VulnerabilityMgmt_Read";
    public static final String SOM_ADMIN = "SOM_Write";
    public static final String SOM_READ = "SOM_Read";
    public static final String CUSTOMER_ADMIN_ROLE = "CA_Write";
    public static final String RESTRICT_USER_TASKS_ROLE = "RESTRICT_USER_TASKS";
    public static final String ENABLE_TASK_SHARING = "ENABLE_TASK_SHARING";
    public static final String SWDEPLOY_WRITE = "SWDeploy_Write";
    public static final String SWDEPLOY_READ = "SWDeploy_Read";
    public static final String HELPDESK_ADMIN = "Integ_Full";
    public static final String RA_FULL = "RA_Full";
    public static final String RA_LIMITED = "RA_Limited";
    public static final String USERNAME = "UserName";
    public static final String PASSWORD = "Password";
    public static final String DOMAINNAME = "DomainName";
    public static final String AUTHMODE = "AuthMode";
    public static final String HTTPREQUEST = "HTTPRequest";
    public static final String LOGIN_ID = "loginID";
    public static final int AUTHMODE_LOCAL = 1;
    public static final int AUTHMODE_AD = 2;
    public static final String ALL_MANAGED_MOBILE_DEVICES_ROLE = "All_Managed_Mobile_Devices";
    public static final String PROFILE_PICTURE_USED = "Profile Picture";
    public static final String IS_VALID_USER = "isValidUser";
    public static final String IS_SPICE_WORKS_ENABLED = "isSpiceworksEnabled";
    public static final String ENABLED = "enabled";
    public static final String ADMIN = "admin";
    public static final String MIN_LENGTH = "MIN_LENGTH";
    public static final String BAD_ATTEMPT = "BAD_ATTEMPT";
    public static final String LOCK_PERIOD = "LOCK_PERIOD";
    public static final String CUSTOM_POLICY = "CUSTOM_POLICY";
    public static final String DEFAULT_POLICY = "Normal";
    public static final String CUSTOM_PROFILE = "CUSTOM_PROFILE";
    public static final String DEFAULT_PROFILE = "Profile 2";
    public static final String PREVENT_REUSE_FOR = "PREVENT_REUSE_FOR";
    public static final String IS_COMPLEX_PASSWORD = "IS_COMPLEX_PASSWORD";
    public static final String ENABLE_LOGIN_RESTRICTION = "ENABLE_LOGIN_RESTRICTION";
    public static final String IS_LOGIN_NAME_USAGE_RESTRICTED = "IS_LOGIN_NAME_USAGE_RESTRICTED";
    public static final String ALLOWED_INVALID_ATTEMPTS = "AllowedInvalidAttempts";
    public static final String MAX_LOCK_OUT_TIME = "MaxLockOutTime";
    public static final String DEFAULT_LOGIN_DOMAIN_VALUE = "-";
    public static final String DEMO_MODE_MASKED_REMOTE_HOST = "xxx.xxx.xxx.xxx";
    public static final String KILL_ALL_SESSION = "Kill All Session";
    public static final String KILL_SPECIFIED_SESSION = "Kill Specified Session";
    public static final Long ALL_MGD_USER;
    public static final Long CG_MAPPED_USER;
    public static final Long RO_MAPPED_USER;
    public static final String IS_DEFAULT_PASS_CHANGE_REQUIRED = "isDefaultPasswordChangeRequired";
    public static final String LOCAL_USERS_MAP = "localUsersMap";
    public static final String DEFAULT_PASSWORD = "admin";
    public static final String ALL_MANAGED_PROBE_ROLE = "All_Managed_Probes";
    public static final int USER_STATUS_ACTIVE = 0;
    public static final int USER_ACTIVED_SUCCESSFULLY = 1;
    public static final int NETWORK_DEVICE_SCOPE_ALL = 0;
    public static final int NETWORK_DEVICE_SCOPE_GROUP = 1;
    
    static {
        VISIBLE_ROLE = new Integer(1);
        HIDDEN_ROLE = new Integer(0);
        MSP_ROLE = new Integer(5);
        ALL_MGD_USER = 0L;
        CG_MAPPED_USER = 1L;
        RO_MAPPED_USER = 2L;
    }
}
