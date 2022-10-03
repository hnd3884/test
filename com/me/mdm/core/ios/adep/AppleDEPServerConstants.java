package com.me.mdm.core.ios.adep;

public class AppleDEPServerConstants
{
    public static final Long DEP_SERVER_PROTOCOL_VERSION;
    public static final String DEP_SERVER_PROTOCOL_VERSION_HEADER = "X-Server-Protocol-Version";
    public static final String DEP_SERVER_USER_AGENT = "MEMDMServer";
    public static final String DEP_SERVER_AUTH_SESSION_HEADER = "X-ADM-Auth-Session";
    public static final String DEP_SERVICE_DEFINE_PROFILE = "DefineProfile";
    public static final String DEP_SERVICE_FETCH_PROFILE_DETAILS = "FetchProfile";
    public static final String DEP_SERVICE_ASSIGN_PROFILE = "AssignProfile";
    public static final String DEP_SERVICE_REMOVE_PROFILE = "RemoveProfile";
    public static final String DEP_SERVICE_FETCH_DEVICES = "FetchDevices";
    public static final String DEP_SERVICE_FETCH_DEVICE_DETAILS = "FetchDevices";
    public static final String DEP_SERVICE_SYNC_DEVICES = "SyncDevices";
    public static final String DEP_SERVICE_ACCOUNT_DETAILS = "Account";
    public static final String DEP_SERVICE_DEVICE_DETAILS = "Devices";
    public static final String DEP_SERVER_SESSION_TOKEN_URL = "https://mdmenrollment.apple.com/session";
    public static final String DEP_DEFINE_PROFILE_URL = "https://mdmenrollment.apple.com/profile";
    public static final String DEP_FETCH_PROFILE_DETAILS_URL = "https://mdmenrollment.apple.com/profile";
    public static final String DEP_ASSIGN_PROFILE_URL = "https://mdmenrollment.apple.com/profile/devices";
    public static final String DEP_REMOVE_PROFILE_URL = "https://mdmenrollment.apple.com/profile/devices";
    public static final String DEP_FETCH_DEVICES_URL = "https://mdmenrollment.apple.com/server/devices";
    public static final String DEP_FETCH_DEVICE_DETAILS_URL = "https://mdmenrollment.apple.com/devices";
    public static final String DEP_SYNC_DEVICES_URL = "https://mdmenrollment.apple.com/devices/sync";
    public static final String DEP_ACCOUNT_DETAILS_URL = "https://mdmenrollment.apple.com/account";
    public static final String DEP_SERVER_SERVICE_REQUEST_NAME = "DEPServiceRequestName";
    public static final String DEP_SERVER_SERVICE_REQUEST_DATA = "DEPServiceRequestData";
    public static final String DEP_SERVER_SERVICE_REQUEST_PARAMS = "DEPServiceRequestParams";
    public static final String DEP_SERVER_SERVICE_RESPONSE_NAME = "DEPServiceResponseName";
    public static final String DEP_SERVER_SERVICE_RESPONSE_DATA = "DEPServiceResponseData";
    public static final String DEP_SERVER_SERVICE_RESPONSE_STATUS = "DEPServiceStatus";
    public static final String DEP_SERVER_SERVICE_RESPONSE_ERROR = "DEPServiceError";
    public static final String CUSTOMER_ID = "CustomerId";
    public static final Integer DEP_ORG_VERSION_APPLE_DEPLOYMENT_PROGRAMME;
    public static final Integer DEP_ORG_VERSION_APPLE_SCHOOL_MANAGER;
    public static final Integer DEP_ORG_TYPE_ENTERPRISE_ORGANISATION;
    public static final Integer DEP_ORG_TYPE_EDUCATIONAL_INSTITUTION;
    
    static {
        DEP_SERVER_PROTOCOL_VERSION = 5L;
        DEP_ORG_VERSION_APPLE_DEPLOYMENT_PROGRAMME = 1;
        DEP_ORG_VERSION_APPLE_SCHOOL_MANAGER = 2;
        DEP_ORG_TYPE_ENTERPRISE_ORGANISATION = 1;
        DEP_ORG_TYPE_EDUCATIONAL_INSTITUTION = 2;
    }
}
