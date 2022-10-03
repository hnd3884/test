package com.adventnet.sym.server.mdm.enroll;

public class MDMEnrollmentConstants
{
    public static final String AUTHENTICATION_METHOD_STRING_OTP = "OTP";
    public static final String AUTHENTICATION_METHOD_STRING_ACTIVE_DIRECTORY = "ActiveDirectory";
    public static final String AUTHENTICATION_METHOD_STRING_COMBINED = "Combined";
    public static final String AUTHENTICATION_METHOD_STRING_AZURE_AD_TOKEN = "AzureADToken";
    public static final String ENROLLMENT_RESPONSE_HEADER_KEY = "MDMEnrollment";
    public static final String ENROLLMENT_RESPONSE_HEADER_VALUE = "true";
    public static final String ENROLL_URL_QUERYPARAM_ENROLL_TYPE_KEY = "et";
    public static final String ENROLL_URL_QUERYPARAM_ENROLL_TYPE_QR_VALUE = "1";
    public static final String ENROLL_URL_QUERYPARAM_ENROLL_ANDROID_APP_SRC_KEY = "isApp";
    public static final String ENROLL_URL_QUERYPARAM_ENROLL_ANDROID_APP_SRC_VALUE = "true";
    public static final String ENROLL_URL_QUERYPARAM_ENROLL_OTP_KEY = "token";
    public static final String ENROLL_QR_SCAN_SRC = "scanSrc";
    public static final int ENROLL_QR_SCAN_SRC_MAIL = 1;
    public static final int ENROLL_QR_SCAN_SRC_MAIL_URL = 3;
    public static final int ENROLL_QR_SCAN_SRC_SERVER = 2;
    public static final String ANDOID_PLAYSTORE_URL_SHOWN_IN_SERVER = "https://play.google.com/store/apps/details?id=com.manageengine.mdm.android";
    public static final String PLAYTORE_URL_TRACKING_PARAM_SRC_SERVER = "&MDMSrc=1";
    public static final String PLAYTORE_URL_TRACKING_PARAM_SRC_ENROLLMENT_MAIL = "&MDMSrc=2";
    public static final String PLAYTORE_URL_TRACKING_PARAM_SRC_DEVICE_ENROLLMENT_PAGE = "&MDMSrc=3";
    public static final int DFE_ENROLL_SUCCESS = 0;
    public static final int DFE_WAITING_FOR_USER_ASSIGNMENT = 1;
    public static final int DFE_USER_ASSIGNED = 2;
    public static final int DFE_ENROLL_FAILED = 9;
    public static final int DFE_ENROLL_UNKNOWN_ERROR = 10;
    public static final int DFE_WIN10_USER_DETAILS_UPDATED_TO_AGENT = 301;
    public static final int DFE_WIN10_AWAITING_USER_LOGON = 302;
    public static final int DFE_MAC_ALTEADY_ENROLLED_BY_MDM = 600;
    public static final int DFE_MAC_ALTEADY_UNABLE_REMOVE_OLD_MDM = 601;
    public static final String MDM_ENROLLMENT_LOGGER = "MDMEnrollment";
    public static final String ERID = "erid";
    public static final String UDID = "udid";
    public static final String ENCAPI_KEY = "encapiKey";
    public static final String REFETCH_IOS_CONFIG = "refetchiosconfig";
    public static final String REFETCH_LINK = "refetchlink";
    public static final String OWNED_BY_CORPORATE = "1";
    public static final String OWNED_BY_PERSONAL = "2";
    public static final String ADDED_USER_ID = "addedUserID";
    public static final int OTP_FAILURE_LIMIT = 3;
    public static final int CLIENT_CERTIFICATE_VALID_YEARS = 5;
    public static final String CA_CERT_CREATED = "CA_CERT_CREATED";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_IDENTIFIER = "USER_IDENTIFIER";
    public static final String EMAIL_ID = "EMAIL_ID";
    public static final String MANAGED_USER_DETAILS = "MANAGED_USER_DETAILS";
    public static final String ZAPIKEY = "ZAPIKEY";
    public static final String GROUP_NAME = "GroupName";
    public static final String RESOURCE_IDS = "resourceIds";
    public static final String DEVICE_REASSIGN_JSON = "DeviceReassignJson";
    
    public static class DeviceReassignJSON
    {
        public static final String GROUPIDS = "GroupIDs";
        public static final String USER_NAME = "UserName";
        public static final String DOMAIN = "Domain";
        public static final String EMAIL = "Email";
        public static final String PROFILEIDS = "ProfileIDs";
        public static final String PROFILE_PROPS = "ProfileProps";
        public static final String LOST_MODE_DETAILS = "LostModeDetails";
        public static final String TECH_USER_ID = "TechUserID";
        public static final String DOC_LIST = "DocList";
        public static final String TEMPLATE_TYPE = "TemplateType";
        public static final String MANAGED_STATUS = "ManagedStatus";
        public static final String DEVICE_NAME = "DeviceName";
    }
    
    public static final class WindowsEnrollmentConstants
    {
        public static final String ENROLLMENT_TYPE_DEVICE = "Device";
        public static final String ENROLLMENT_TYPE_FULL = "Full";
    }
    
    public static final class AppleManagementStatus
    {
        public static final int APPLE_ENROLLMENT_STATUS_DEP_ENROLLMENT = 1;
        public static final int APPLE_ENROLLMENT_STATUS_USER_APPROVED_ENROLLMENT = 2;
        public static final int APPLE_ENROLLMENT_STATUS_USER__ENROLLMENT = 3;
    }
    
    public static final class UserAssignmentRules
    {
        public static final String RULE_NAME = "rule_name";
        public static final String DEFAULT_RULE_NAME = "User Assignment Rule";
        public static final String USER_SETTINGS = "user_settings";
        public static final String USER_ID = "user_id";
        public static final String CUSTOMER_ID = "customer_id";
        public static final String USER_RULES = "user_rules";
        public static final String GROUP_RULES = "group_rules";
        public static final String DEVICE_MODEL_RULES = "device_model_rules";
        public static final String DEVICE_NAME_RULES = "device_name_rules";
        public static final String AGENT_PARAM_RULES = "agent_param_rules";
        public static final String ADDITIONAL_CONTEXT = "additional_context";
        public static final String DEVICES = "devices";
        public static final String SKIP_USER_AUTOMATION = "SkipUserAssignmentAutomation";
        
        public static final class UserRules
        {
            public static final Integer SAME_USER_TYPE;
            public static final Integer FIRST_LOGGED_IN_USER_TYPE;
            public static final Integer AUTHENTICATED_USER_TYPE;
            public static final Integer WORKGROUP_USER;
            public static final Integer INCLUDE_RULE_CRITERIA;
            public static final Integer EXCLUDE_RULE_CRITERIA;
            
            static {
                SAME_USER_TYPE = 1;
                FIRST_LOGGED_IN_USER_TYPE = 2;
                AUTHENTICATED_USER_TYPE = 3;
                WORKGROUP_USER = 4;
                INCLUDE_RULE_CRITERIA = 1;
                EXCLUDE_RULE_CRITERIA = 0;
            }
        }
        
        public static final class DeviceModelRules
        {
            public static final Integer MOBILE_DEVICE;
            public static final Integer MODERN_DEVICE;
            
            static {
                MOBILE_DEVICE = 1;
                MODERN_DEVICE = 2;
            }
        }
        
        public static final class Integrations
        {
            public static final String MACHINE_TYPE = "MachineType";
            public static final String USER_TYPE = "UserType";
            public static final String EXCLUDE_LIST = "ExcludeList";
            public static final String FORCE = "Force";
            public static final String MDM_ENROLL_RULES = "MDMEnrollmentRules";
            public static final String API_KEY_WIN = "WinApiKey";
            public static final String TEMPLATE_TOKEN_WIN = "WindowsTemplateToken";
            public static final String TEMPLATE_TOKEN_MAC = "MacTemplateToken";
            public static final String API_KEY_MAC = "MacApiKey";
            public static final String NAT_URL = "NATUrl";
            public static final String UPN = "UPN";
            public static final String UEM_OTP_EXPIRY_PARAM = "UEM_OTP_EXPIRY";
            public static final String MDM_IOS_SERVER_URL = "MdmIosServerUrl";
            
            public static final class Types
            {
                public static final String MACHINE_TYPE_DOMAIN = "Domain";
                public static final String MACHINE_TYPE_WORKGROUP = "Workgroup";
                public static final String USER_TYPE_DOMAIN_USER = "FirstLoggedInDomainUser";
                public static final String USER_TYPE_WORKGROUP_USER = "FirstLoggedInUser";
            }
        }
    }
    
    public static final class AssignUserConstants
    {
        public static final String IMEI = "IMEI";
        public static final String SERIAL_NUMBER = "SerialNumber";
        public static final String UDID = "UDID";
        public static final String EASID = "EASID";
        public static final String GENERIC_ID = "GENERIC_ID";
        public static final String CUSTOMER_ID = "CustomerId";
        public static final String DEVICE_NAME = "DeviceName";
        public static final String USER_NAME = "UserName";
        public static final String DOMAIN_NAME = "DomainName";
        public static final String GROUP_IDS = "GroupId";
        public static final String EMAIL = "EmailAddr";
        public static final String FAILED_LIST = "FailedList";
        public static final String ERROR_MSG = "ErrorMsg";
        public static final String DEVICE_MODEL = "device_model";
        public static final String SKIP_USER_VALIDATION = "skip_user_validation";
        public static final String GROUP_NAME = "group_name";
        public static final String TECHNICIAN_ID = "technician_id";
    }
    
    public static class Deprovision
    {
        public static final String FAILURE_LIST = "FailureList";
        public static final String USER_ID = "userId";
        public static final String WIPE_TYPE = "wipeType";
        public static final String WIPE_REASON = "wipeReason";
        public static final String OTHER_REASON = "otherReason";
        public static final String BULK_DEPROVISION_CSV = "BULK_DEPROVISION_CSV";
        public static final String RESOURCE_ID_TO_PK = "resourceIdToPrimaryKey";
        public static final String REQ_ID = "reqId";
        public static final String MANAGED_DEVICE_ID = "managedDeviceId";
        public static final String ERROR_MSG = "ErrorMsg";
        public static final String SUCCESS = "success";
        public static final String SUCCESS_LIST = "SuccessList";
        public static final String REMARKS = "REMARKS";
        public static final String MANAGED_STATUS = "MANAGED_STATUS";
        public static final String WIPE_PENDING = "WIPE_PENDING";
        public static final String COMMENT = "comment";
        public static final String ADDED_BY = "ADDED_BY";
        public static final String CUSTOMER_ID = "customerID";
        public static final String DEVICE_NAME = "device_name";
    }
}
