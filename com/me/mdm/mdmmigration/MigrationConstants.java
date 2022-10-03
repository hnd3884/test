package com.me.mdm.mdmmigration;

import org.json.JSONArray;

public class MigrationConstants
{
    public static final Integer BASIC_DIGEST_AUTH;
    public static final int ME_CLOUD_ID = 1;
    public static final int ME_ONPREMISE_ID = 2;
    public static final int AIRWATCH_ID = 3;
    public static final int IBM_MAAS_ID = 4;
    public static final int MOBILE_IRON_ID = 5;
    public static final int MERAKI_ID = 6;
    public static final int JAMF_ID = 15;
    public static final int ME_DESKTOPCENTRAL_ID = 7;
    public static final int ME_DESKTOPCENTRAL_CLOUD_ID = 8;
    public static final int MIGRATION_YET_TO_START = 1;
    public static final int MIGRATION_IN_PROCESS = 2;
    public static final int MIGRATION_DONE = 3;
    public static final int MIGRATION_FAILED = 4;
    public static final String FETCH_DEVICES = "FETCH_DEVICES";
    public static final String FETCH_ORGANIZATIONS = "FETCH_ORGANIZATIONS";
    public static final String FETCH_USERS = "FETCH_USERS";
    public static final String FETCH_GROUPS = "FETCH_GROUPS";
    public static final String FETCH_PROFILES = "FETCH_PROFILES";
    public static final String FETCH_APPS = "FETCH_APPS";
    public static final String FETCH_ALL = "FETCH_ALL";
    public static final int IMAGE_FILE = 1;
    public static final int APK_FILE = 2;
    public static final int CERTIFICATE_FILE = 3;
    public static final int CUSTOM_PROFILE_FILE = 4;
    public static final String ENTERPRISE_TYPE_GOOGLE = "1";
    public static final String ENTERPRISE_TYPE_EMM = "2";
    public static final int TWENTY_FOUR_HOURS = 1440;
    public static final int FREE_STORE_APP = 1;
    public static final int PAID_STORE_APP = 2;
    public static final int ENTERPRISE_APP = 3;
    public static final int PROFILES = 1;
    public static final int APPS = 2;
    public static final int IOS = 1;
    public static final int ANDROID = 2;
    public static final String CONFIG_ID = "config_id";
    public static final String SERVICE_ID = "service_id";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String USER_ID = "user_id";
    public static final String PROFILE_ID = "profile_id";
    public static final String MIGRATION_TYPE = "type";
    public static final String ROOT_URL = "Server_URL";
    public static final String FETCH_TASK = "MigrationFetchTask";
    public static final String DEVICE_MIGRATION_STATUS = "devices_migrated";
    public static final String USER_MIGRATION_STATUS = "users_migrated";
    public static final String GROUP_MIGRATION_STATUS = "groups_migrated";
    public static final String PROFILE_MIGRATION_STATUS = "profiles_migrated";
    public static final String APPS_MIGRATION_STATUS = "apps_migrated";
    public static final String MIGRATION_DEVICE_ERROR = "migration_device_error";
    public static final String MIGRATION_USER_ERROR = "migration_user_error";
    public static final String MIGRATION_GROUP_ERROR = "migration_group_error";
    public static final String MIGRATION_PROFILE_ERROR = "migration_profile_error";
    public static final String MIGRATION_APPS_ERROR = "migration_apps_error";
    public static final String MIGRATION_ERROR_CODE = "migration_error_code";
    public static final String MIGRATION_ERROR_MSG = "migration_error_msg";
    public static final String MIGRATION_REMARKS = "remarks";
    public static final String REQUEST_HEADER_ACCEPT = "Accept";
    public static final String REQUEST_HEADER_CONTENT_TYPE = "Content-Type";
    public static final String REQUEST_HEADER_APPLICATION_XML = "application/xml";
    public static final String REQUEST_HEADER_AUTHORIZATION = "Authorization";
    public static final String REQUEST_HEADER_APPLICATION_JSON = "application/json";
    public static final String REQUEST_GET = "GET";
    public static final String REQUEST_POST = "POST";
    public static final String REQUEST_DELETE = "DELETE";
    public static final String ADDITIONAL_DETAILS = "AdditionalDetails";
    public static final String TYPES = "Types";
    public static final String DEVICE_ID = "deviceID";
    public static final int FILTER_DEVICE_GROUP_TYPE = 1;
    public static final int FILTER_USER_GROUP_TYPE = 2;
    public static final int FILTER_ALL_GROUP_TYPE = 3;
    public static final String UI_CONFIG_API_KEY = "api_key";
    public static final String UI_CONFIG_BASIC = "basic";
    public static final String UI_CONFIG_DISPLAY_NAME = "display_name";
    public static final String UI_CONFIG_DISPLAY_TYPE = "type";
    public static final String UI_CONFIG_AUTHENTICATION_TYPE = "authentication_type";
    public static final String INVALID_SERVICE_CONFIG_DETAILS = "Invalid API service configuration details";
    public static final String SOURCE_SERVER_ERROR = "Error while processing the request in source server";
    public static final String SERVER_ERROR = "Internal Server Error. Contact support with logs.";
    public static final String MIGRATION_SUCCESS = "Migration completed successfully";
    public static final String FEATURE_PARAMS = "featureparams";
    public static final String PARAM_NAME = "paramname";
    public static final String PARAM_VALUE = "paramvalue";
    public static final String MDM_MIGRATION = "MDMMigration";
    public static final String AGENT_MIGRATION = "AgentMigration";
    public static final String MIGRATION_TARGET = "MigrationTarget";
    public static final String DONOTVERFIYAPPLUSIGNEDCONTENT = "DoNotVerifyAppleSignedContent";
    public static final String KEYPAIR = "KeyPair";
    public static final String PRODUCT_CODE = "productcode";
    public static final String PRODUCT_VERSION = "productversion";
    public static final String MDM_BUILD = "MDM_BUILD";
    public static final String MDM = "MDM";
    public static final String DC = "DC";
    public static final String DC_BUILD = "DC_BUILD";
    public static final String IS_BUILD_LATEST = "is_build_latest";
    public static final String URL_TO_GET_BUILD_NUMBER = "https://mdmdatabase.manageengine.com/MISC/migration_build/build.txt";
    public static final String MIGRATION_TOOL = "MigrationTool";
    public static final String AES_ALGORITHM = "AES";
    public static final String RSA_ALGORITHM = "RSA";
    public static final int MIGRATION_TOOL_KEY_TYPE = 1;
    
    static {
        BASIC_DIGEST_AUTH = 1;
    }
    
    public static class MerakiRequirements
    {
        public static JSONArray productIRequirements;
        
        static {
            MerakiRequirements.productIRequirements = new JSONArray() {
                {
                    this.put((Object)"API Key");
                    this.put((Object)"Network ID");
                    this.put((Object)"Root URL");
                }
            };
        }
    }
    
    public static class MaaS360Requirements
    {
        public static JSONArray requirementArrayList;
        
        static {
            MaaS360Requirements.requirementArrayList = new JSONArray() {
                {
                    this.put((Object)"Billing ID");
                    this.put((Object)"App ID");
                    this.put((Object)"Version");
                    this.put((Object)"Platform ID");
                    this.put((Object)"Access Key");
                    this.put((Object)"Root URL");
                    this.put((Object)"Username");
                    this.put((Object)"Password");
                }
            };
        }
    }
    
    public static class MECloudRequirements
    {
        public static JSONArray requirements;
        
        static {
            MECloudRequirements.requirements = new JSONArray() {
                {
                    this.put((Object)"Client ID");
                    this.put((Object)"Client Secret");
                    this.put((Object)"Redirect URI");
                    this.put((Object)"Grant Type");
                    this.put((Object)"Refresh Token");
                    this.put((Object)"Account URL");
                }
            };
        }
    }
    
    public static class MEOnPremiseRequirements
    {
        public static JSONArray requirements;
        
        static {
            MEOnPremiseRequirements.requirements = new JSONArray() {
                {
                    this.put((Object)"API Key");
                    this.put((Object)"Server URL");
                }
            };
        }
    }
    
    public static class MEDesktopCentralRequirements
    {
        public static JSONArray requirements;
        
        static {
            MEDesktopCentralRequirements.requirements = new JSONArray() {
                {
                    this.put((Object)"API Key");
                    this.put((Object)"Server URL");
                }
            };
        }
    }
    
    public static class AirwatchRequirements
    {
        public static JSONArray requirementList;
        
        static {
            AirwatchRequirements.requirementList = new JSONArray() {
                {
                    this.put((Object)"AirWatch Tenant Code");
                    this.put((Object)"Root URL");
                    this.put((Object)"Username");
                    this.put((Object)"Password");
                }
            };
        }
    }
    
    public static class JamfRequirements
    {
        public static JSONArray requirementList;
        
        static {
            JamfRequirements.requirementList = new JSONArray() {
                {
                    this.put((Object)"username");
                    this.put((Object)"password");
                }
            };
        }
    }
    
    public interface API
    {
        public static final String MANAGED_STATUS = "Managed";
        public static final String UNMANAGED_STATUS = "Unmanaged";
        public static final String STATUS = "Status";
        public static final String NEW_ENROLLMENT_URL = "NewEnrollmentURL";
        public static final String STATE = "state";
        public static final String UDID = "udid";
        
        public interface AUTHENTICATION
        {
            public static final String AUTHORIZATION = "Authorization";
            public static final String BASIC = "Basic";
        }
        
        public interface MECLOUD
        {
            public static final String GRANT_TYPE = "grant_type";
            public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
            public static final String REDIRECT_URI = "redirect_uri";
            public static final String CLIENT_SECRET = "client_secret";
            public static final String CLIENT_ID = "client_id";
            public static final String REFRESH_TOKEN = "refresh_token";
            public static final String ACCOUNT_URL = "account_url";
            public static final String ACCESS_TOKEN = "access_token";
            public static final String TOKEN_TYPE = "token_type";
            public static final String EXPIRES_IN = "expires_in";
        }
        
        public interface ERRORS
        {
            public static final String CODE_UNKNOWN = "100";
        }
        
        public interface COMMON
        {
            public static final String MSG_TYPE = "msgType";
            public static final String MSG_CONTENT = "msgContent";
            public static final String STATUS_CODE = "StatusCode";
            public static final String RESPONSE_JSON = "ResponseJson";
            public static final String RESPONSE_HEADER = "ResponseHeader";
            public static final String RESPONSE_ERROR = "ResponseError";
            public static final String ERROR = "Error";
            public static final String ERROR_MSG = "ErrorMsg";
        }
        
        public interface MSG_TYPES
        {
            public static final String QUERY_MANAGEMENT_STATUS = "QueryManagementStatus";
            public static final String UNMANAGE_DEVICE = "UnmanageDevice";
            public static final String QUERY_ENROLLED_STATUS = "EnrolledStatus";
            public static final String ADD_SERVICE_CONFIG = "AddServiceConfig";
            public static final String MODIFY_SERVICE_CONFIG = "ModifyServiceConfig";
            public static final String ADD_AUTH_INFO = "AddAuthInfo";
            public static final String MODIFY_AUTH_INFO = "ModifyAuthInfo";
            public static final String GET_API_SERVICES = "GetAPIServices";
            public static final String GET_SERVICE_CONFIGURATIONS = "GetServiceConfigurations";
            public static final String GET_SERVICE_CONFIG_DETAILS = "GetServiceConfigurationDetails";
            public static final String DELETE_SERVICE_CONFIG = "DeleteServiceConfig";
        }
    }
}
