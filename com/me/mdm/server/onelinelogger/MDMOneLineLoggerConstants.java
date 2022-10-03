package com.me.mdm.server.onelinelogger;

public interface MDMOneLineLoggerConstants
{
    public static final String REMARKS = "REMARKS";
    
    public interface RemarkConstants
    {
        public static final String CREATE_SUCCESS = "create-success";
        public static final String UPDATE_SUCCESS = "update-success";
        public static final String DELETE_SUCCESS = "delete-success";
        public static final String CREATE_FAILED = "create-failed";
        public static final String UPDATE_FAILED = "update-failed";
        public static final String DELETE_FAILED = "delete-failed";
        public static final String ASSOCIATE_SUCCESS = "associate-success";
        public static final String ASSOCIATE_FAILED = "associate-failed";
        public static final String DISSOCIATE_SUCCESS = "dissociate-success";
        public static final String DISSOCIATE_FAILED = "dissociate-failed";
        public static final String ENROLL_SUCCESS = "enroll-success";
        public static final String ENROLL_FAILED = "enroll-failed";
        public static final String DEPROVISION_SUCCESS = "deprovision-success";
        public static final String DEPROVISION_FAILED = "deprovision-failed";
        public static final String ASSIGN_SUCCESS = "assign-success";
        public static final String ASSIGN_FAILED = "assign-failed";
        public static final String ACCOUNT_CONFIG_SUCCESS = "account-config-success";
        public static final String ACCOUNT_CONFIG_FAILED = "account-config-failed";
        public static final String MAIL_DELIVERY_SUCCESS = "mail-delivery-success";
        public static final String MAIL_DELIVERY_FAILURE = "mail-delivery-failure";
        public static final String COMMAND_INITIATED = "command-initiated";
        public static final String COMMAND_SUCCESS = "command-success";
        public static final String COMMAND_FAILED = "command-failed";
        public static final String UPGRADE_SUCCESS = "upgrade-success";
        public static final String UPGRADE_FAILED = "upgrade-failed";
        public static final String SMS_DELIVERY_SUCCESS = "sms-delivery-success";
        public static final String SMS_DELIVERY_FAILURE = "sms-delivery-failure";
        public static final String ADD_SUCCESS = "add-success";
        public static final String ADD_FAILED = "add-failed";
        public static final String GET_SUCCESS = "get-success";
        public static final String GET_FAILED = "get-failed";
    }
    
    public interface Operations
    {
        public static final String ADD_USER = "ADD_USER";
        public static final String MODIFY_USER = "MODIFY_USER";
        public static final String DELETE_USER = "DELETE_USER";
        public static final String ADD_ROLE = "Role_Addition";
        public static final String MODIFY_ROLE = "Role_Modification";
        public static final String DELETE_ROLE = "Role_Deletion";
        public static final String UPDATE_TFA = "TFA_Update";
        public static final String UPDATE_PASSWORD_POLICY = "PasswordPolicy_Update";
        public static final String CHANGE_PASSWORD = "Password_Updation";
        public static final String DELETE_SESSION = "Session_Deletion";
        public static final String SAVE_NAT = "SAVE_NAT";
        public static final String SAVE_MAIL_SERVER = "SAVE_MAIL_SERVER";
        public static final String ADD_ANNOUNCEMENT = "ADD_ANNOUNCEMENT";
        public static final String MODIFY_ANNOUNCEMENT = "MODIFY_ANNOUNCEMENT";
        public static final String DISTRIBUTE_ANNOUNCEMENT = "DISTRIBUTE_ANNOUNCEMENT";
        public static final String INIT_REMOTE_SESSION = "INIT_REMOTE_SESSION";
        public static final String ACCEPT_REMOTE_SESSION = "ACCEPT_REMOTE_SESSION";
        public static final String INTEGRATE_ASSIST = "INTEGRATE_ASSIST";
        public static final String DISINTEGRATE_ASSIST = "DISINTEGRATE_ASSIST";
        public static final String ADD_PAYLOAD = "ADD_PAYLOAD";
        public static final String MODIFY_PAYLOAD = "MODIFY_PAYLOAD";
        public static final String DELETE_PAYLOAD = "DELETE_PAYLOAD";
        public static final String APP_UPDATE_POLICY_CREATED = "APP_UPDATE_POLICY_CREATED";
        public static final String APP_UPDATE_POLICY_MODIFIED = "APP_UPDATE_POLICY_MODIFIED";
        public static final String APP_UPDATE_POLICY_DELETED = "APP_UPDATE_POLICY_DELETED";
        public static final String DELETE_PAYLOAD_ITEM = "DELETE_PAYLOAD_ITEM";
        public static final String CREATE_PROFILE = "CREATE_PROFILE";
        public static final String TRASH_PROFILE = "TRASH_PROFILE";
        public static final String PUBLISH_PROFILE = "PUBLISH_PROFILE";
        public static final String ASSOCIATE_PROFILE = "ASSOCIATE_PROFILE";
        public static final String DISSOCIATE_PROFILE = "DISSOCIATE_PROFILE";
        public static final String VIEW_FIRMWARE_PASSWORD = "VIEW_FIRMWARE_PASSWORD";
        public static final String VIEW_FILEVAULT_PERSONAL_KEY = "VIEW_FILEVAULT_PERSONAL_KEY";
        public static final String VIEW_FILEVAULT_INSTITUTIONAL_KEY = "VIEW_FILEVAULT_INSTITUTIONAL_KEY";
        public static final String ACCOUNT_CONFIG = "ACCOUNT_CONFIG";
        public static final String UNLOCK_USER_ACCOUNT = "UNLOCK_USER_ACCOUNT";
        public static final String ADD_APP = "ADD_APP";
        public static final String UPDATE_APP = "UPDATE_APP";
        public static final String ASSOCIATE_APP = "ASSOCIATE_APP";
        public static final String DISSOCIATE_APP = "DISSOCIATE_APP";
        public static final String BLACKLIST_APP = "BLACKLIST_APP";
        public static final String WHITELIST_APP = "WHITELIST_APP";
        public static final String ADD_GEOFENCE = "ADD_GEOFENCE";
        public static final String MODIFY_GEOFENCE = "MODIFY_GEOFENCE";
        public static final String ADD_COMPLIANCE = "ADD_COMPLIANCE";
        public static final String MODIFY_COMPLIANCE = "MODIFY_COMPLIANCE";
        public static final String DELETE_COMPLIANCE = "DELETE_COMPLIANCE";
        public static final String ASSOCIATE_COMPLIANCE = "ASSOCIATE_COMPLIANCE";
        public static final String DISSOCIATE_COMPLIANCE = "DISSOCIATE_COMPLIANCE";
        public static final String UPDATE_GEO_TRACKING = "UPDATE_GEO_TRACKING";
        public static final String DEVICE_ENROLLED = "DEVICE_ENROLLED";
        public static final String DEVICE_USER_ASSIGNED = "DEVICE_USER_ASSIGNED";
        public static final String DEVICE_REMOVED = "DEVICE_REMOVED";
        public static final String DEVICE_DEPROVISIONED = "DEVICE_DEPROVISIONED";
        public static final String DEVICE_UNMANAGED = "DEVICE_UNMANAGED";
        public static final String CREATE_ENROLLMENT_REQUEST = "CREATE_ENROLLMENT_REQUEST";
        public static final String SENT_ENROLLMENT_REQUEST = "SENT_ENROLLMENT_REQUEST";
        public static final String AGENT_UPGRADE = "AGENT_UPGRADE";
        public static final String SEND_SMS_REQUEST = "SEND_SMS_REQUEST";
        public static final String ADD_LOCAL_USER = "ADD_LOCAL_USER";
        public static final String MODIFY_LOCAL_USER = "MODIFY_LOCAL_USER";
        public static final String DELETE_LOCAL_USER = "DELETE_LOCAL_USER";
        public static final String DEVICE_PRIVACY_MODIFIED = "DEVICE_PRIVACY_MODIFIED";
        public static final String ADD_DOC = "ADD_DOC";
        public static final String MODIFY_DOC = "MODIFY_DOC";
        public static final String DELETE_DOC = "DELETE_DOC";
        public static final String ASSOCIATE_DOC = "ASSOCIATE_DOC";
        public static final String DISSOCIATE_DOC = "DISSOCIATE_DOC";
        public static final String ADD_DOC_POLICY = "ADD_DOC_POLICY";
        public static final String UPDATE_DOC_POLICY = "UPDATE_DOC_POLICY";
        public static final String DELETE_DOC_POLICY = "DELETE_DOC_POLICY";
        public static final String COMPLETE_WIPE = "COMPLETE_WIPE";
        public static final String CORPORATE_WIPE = "CORPORATE_WIPE";
        public static final String PAUSE_KIOSK = "PAUSE_KIOSK";
        public static final String RESUME_KIOSK = "RESUME_KIOSK";
        public static final String ADD_GROUP = "ADD_GROUP";
        public static final String MODIFY_GROUP = "MODIFY_GROUP";
        public static final String DELETE_GROUP = "DELETE_GROUP";
        public static final String ADD_GROUP_MEMBER = "ADD_GROUP_MEMBER";
        public static final String DELETE_GROUP_MEMBER = "DELETE_GROUP_MEMBER";
        public static final String MOVE_GROUP_MEMBER = "MOVE_GROUP_MEMBER";
    }
    
    public interface Modules
    {
        public static final String USER_MGMT = "User_Management";
        public static final String MDM_CORE = "MDM";
        public static final String SETTINGS = "SETTINGS";
    }
}
