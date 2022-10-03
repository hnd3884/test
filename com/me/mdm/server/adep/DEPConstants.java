package com.me.mdm.server.adep;

public class DEPConstants
{
    public static String apple_Business_Manager;
    public static String apple_School_Manager;
    public static final int ABM = 1;
    public static final int ASM = 2;
    
    static {
        DEPConstants.apple_Business_Manager = "Apple Business Manager";
        DEPConstants.apple_School_Manager = "Apple School Manager";
    }
    
    public static final class AccountConfigurationStatus
    {
        public static final int SUCCESS = 1;
        public static final int FAILED = 2;
        public static final int IN_PROGRESS = 3;
    }
    
    public static final class DeviceConfiguredStatus
    {
        public static final int IN_PROGRESS = 1;
        public static final int FAILED = 2;
    }
    
    public static class DFEModel
    {
        public static int IPAD;
        public static int IPHONE;
        public static int IPOD;
        public static int MAC;
        public static int APPLE_TV;
        
        static {
            DFEModel.IPAD = 1;
            DFEModel.IPHONE = 2;
            DFEModel.IPOD = 3;
            DFEModel.MAC = 4;
            DFEModel.APPLE_TV = 5;
        }
    }
    
    public static class DFEProfileStatus
    {
        public static int EMPTY;
        public static int ASSIGNED;
        public static int PUSHED;
        public static int REMOVED;
        
        static {
            DFEProfileStatus.EMPTY = 1;
            DFEProfileStatus.ASSIGNED = 2;
            DFEProfileStatus.PUSHED = 3;
            DFEProfileStatus.REMOVED = 4;
        }
    }
    
    public static class MDMDEPApiConstants
    {
        public static final String SUCCESS = "success";
        public static final String ERROR = "error";
        public static final String DEP_SERVER_ID = "server_id";
        public static final String APPLE_DEP_SERVERS = "appledepservers";
        public static final String APPLEDEPSERVER_ID = "appledepserver_id";
        public static final String IS_NEW_TECHNICIAN_ASSIGNMENT_NEEDED = "is_new_technician_assignment_needed";
        public static final String ABM_SERVER_PENDING_SETUP_COMPLETION = "abmserverpendingsetupcompletion";
        public static final String ERROR_CODE = "error_code";
        public static final String ERROR_REMARKS = "error_remarks";
        public static final String LOGIN_ID = "login_id";
        public static final String ORG_TYPE = "org_type";
        
        public static class ProfileAPI
        {
            public static final String PROFILE = "profile";
            public static final String SKIP = "skip";
            public static final String ACTIVATION_BY = "activation_by";
            public static final String GROUP = "group";
            public static final String GROUP_ID = "id";
            public static final String GROUP_NAME = "name";
            public static final String IS_ACCOUNT_CONFIG_ENABLED = "IS_ACCOUNT_CONFIG_ENABLED";
            public static final String IS_MULTI_USER = "is_multiuser";
            public static final String USER_NAME = "name";
            public static final String USER = "added_user";
            public static final String USER_ID = "id";
            
            public static class AccountConfig
            {
                public static final String ACCOUNT_CONFIG = "account_config";
                public static final String FULL_NAME = "full_name";
                public static final String SHORT_NAME = "short_name";
                public static final String IS_REGULAR_ACCOUNT = "is_regular_account";
                public static final String IS_HIDDEN = "is_hidden";
                public static final String SKIP_ACCOUNT_CREATION = "skip_account_creation";
                public static final String PASSWORD = "password";
            }
            
            public static class SharedDeviceConfiguration
            {
                public static final String SHARED_DEVICE_JSON_KEY = "shared_device_config";
                public static final String SHARED_DEVICE_QUOTA_SIZE = "quota_size";
                public static final String SHARED_DEVICE_RESIDENT_USERS = "resident_users";
            }
        }
        
        public static class SyncAPI
        {
            public static final String ENROLLED = "managed_DEP";
            public static final String STAGED = "staged_DEP";
            public static final String WAITING_USER_ASSIGN = "waiting_user_assign_DEP";
            public static final String ENROLLED_BY_OTHER_METHOD = "without_DEP";
            public static final String DEVICE_COUNT = "device_count";
            public static final String SUCCESS_SYNC_TIME = "success_Sync_Time";
            public static final String SUCCESS_SYNC_TIME_STRING = "success_Sync_Time_string";
            public static final String LAST_SYNC_TIME = "last_Sync_Time";
            public static final String LAST_SYNC_TIME_STRING = "last_Sync_Time_string";
            public static final String SYNC_STATUS = "Status";
            public static final String SYNC_DETAILS = "sync_details";
        }
        
        public static class FetchOrSyncAPI
        {
            public static final String DEVICES = "devices";
            public static final String SERIAL_NUMBER = "serial_number";
            public static final String MODEL = "model";
            public static final String DESCRIPTION = "description";
            public static final String ASSERT_TAG = "assert_tag";
            public static final String DEVICE_ASSIGNED_BY = "device_assigned_by";
            public static final String PROFILE_STATUS = "profile_status";
            public static final String PROFILE_UUID = "profile_uuid";
            public static final String DEVICE_FAMILY = "device_family";
            public static final String DEVICE_ASSIGNED_DATE = "device_assigned_date";
            public static final String OP_DATE = "op_date";
            public static final String MORE_TO_FOLLOW = "more_to_follow";
            public static final String CURSOR = "cursor";
            public static final String OP_TYPE = "op_type";
            public static final String ADDED = "added";
            public static final String MODIFIED = "modified";
            public static final String DELETED = "deleted";
        }
        
        public static class AccountApi
        {
            public static final String SERVER_NAME = "server_name";
            public static final String ADMIN_EMAIL = "admin_email";
            public static final String FACILITATOR_EMAIL = "facilitator_email";
            public static final String SERVER_EXPIRY = "server_expiry";
            public static final String SERVER_UDID = "server_udid";
            public static final String ORG_ID = "id";
            public static final String ORG_EMAIL = "email";
            public static final String ORG_NAME = "name";
            public static final String ORG_HASH = "hash";
            public static final String ORG_PHONE = "phone";
            public static final String ORG_TYPE = "type";
            public static final String ORG_VERSION = "version";
            public static final String ORG_ADDRESS = "address";
            public static final String ORG_DETAILS = "org_details";
        }
        
        public static class DEPTokenAPI
        {
            public static final String FILE_ID = "file_id";
            public static final String EMAIL_ID = "email";
            public static final String CERTIFICATE_FILE_UPLOAD = "CERTIFICATE_FILE_UPLOAD";
            public static final String FILE_PATH = "file_path";
            public static final String FORCE_DELETE = "force_delete";
        }
    }
    
    public static class AppleApiConstants
    {
        public static final String SERVER_UUID = "server_uuid";
        public static final String DEP_ERROR_CODE = "DEPServerErrorCode";
        public static final String DEP_ERROR_MSG = "DEPServerErrorMsg";
    }
    
    public static class DEPTokenStatus
    {
        public static final int DEP_STATUS_CERTIFICATE_NOT_GENERATED = 0;
        public static final int DEP_CERTIFICATE_GENERATED = 1;
        public static final int DEP_TOKEN_UPLOADED = 2;
        public static final int DEP_PROFILE_CREATED = 3;
    }
    
    public static class ErrorConstants
    {
        public static final String ABM_INTERNAL_SERVER_ERROR = "internal_server_error";
        public static final String TERMS_CONDITIONS_NOT_SIGNED = "t_c_not_signed";
        public static final String MESSAGE_FORMAT_ERROR = "message_format_error";
        public static final String TOKEN_REJECTED = "token_rejected";
        public static final String TOKEN_EXPIRED = "token_expired";
        public static final String OAUTH_PROBLEM_ADVICE = "oauth_problem_advice";
        public static final String FORBIDDEN = "forbidden";
    }
    
    public static class ErrorRemarks
    {
        public static final String TC_ERROR = "TCError";
        public static final String TOKEN_REJECTED = "TokenRejected";
        public static final String TOKEN_EXPIRED = "TokenRejected";
        public static final String MESSAGE_FORMAT_ERROR = "FormatError";
        public static final String OAUTH_ERROR = "OauthError";
        public static final String FORBIDDEN = "Forbidden";
        public static final String SERVICE_DOWN = "ServiceDown";
        public static final String OTHER_ERROR = "OtherError";
        public static final String SERVER_TIME_MISMATCH = "ServerTimeMismatch";
    }
}
