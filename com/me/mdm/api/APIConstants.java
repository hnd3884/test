package com.me.mdm.api;

public class APIConstants implements com.me.devicemanagement.framework.webclient.api.util.APIConstants
{
    public static final String MAPPER_PACKAGE = "com.me.mdm.api";
    public static final String RESPONSE = "RESPONSE";
    public static final String LOCALIZED_ERROR_DESCRIPTION = "localized_error_description";
    public static final String CUSTOM_ERROR_DATA = "custom_error_data";
    public static final String MSG_HEADER = "msg_header";
    public static final String MSG_BODY = "msg_body";
    public static final String REQUEST_URL = "request_url";
    public static final String FILTERS = "filters";
    public static final String RESOURCE_IDENTIFIER = "resource_identifier";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String API_VERSION_1 = "application/mdm.v1+json";
    public static final String CONTENT_TYPE = "application/json";
    public static final String MM_DEVICES = "devices";
    public static final String MM_DEVICE_NAME = "device_name";
    public static final String MM_FORCE_OTP_REGENERATION = "force_otp_regeneration";
    public static final String MM_IS_DOMAIN_JOINED = "is_domain_joined";
    public static final String MM_DOMAIN_OR_WORKGROUP_NAME = "domain_or_workgroup_name";
    public static final String MM_CUSTOMER_ID = "customer_id";
    public static final String MM_DEVICE_UNIQUE_PROPS = "device_unique_props";
    public static final String MM_DEVICE_TOKENS = "device_tokens";
    public static final String MM_SERIAL_NUMBER = "serial_number";
    public static final String MM_IMEI = "imei";
    public static final String MM_UUID = "uuid";
    public static final String MM_UDID = "udid";
    public static final String MM_OTP = "otp";
    public static final String MM_GENERIC_ID = "generic_id";
    public static final String MM_HARDWARE_HASH = "hardware_hash";
    public static final String MM_ETHERNET_MAC = "ethernet_mac";
    public static final String MM_DEVICE_ADDITIONAL_DETAILS = "device_additional_details";
    public static final String MM_DEVICE_MODEL = "device_model";
    public static final String MM_OS_VERSION = "os_version";
    public static final String MM_TECH_USER_ID = "tech_user_id";
    public static final String MM_USER_DETAILS = "user_details";
    public static final String MM_GROUP_RO_DETAILS = "group_ro_details";
    public static final String LOGGER = "MDMApiLogger";
    public static final String ALLOWED_APPS = "allowed_apps";
    public static final String PRIMARY_DOMAIN = "primary_domain_name";
    public static final String OTHER_DOMAIN = "other_domain_names";
    public static final String PROTECTED_DOMAIN = "protected_domain_names";
    public static final String INTERNAL_PROXY_SERVER = "internal_proxy_server";
    public static final String PROXY_SERVER = "proxy_server";
    public static final String NEUTRAL_RESOURCES = "neutral_resources";
    public static final String IP_RANGE = "enterprise_ip_range";
    public static final String CLOUD_RESOURCES = "enterprise_cloud_resources";
    public static final String ENFORCEMENT_LEVEL = "enforcement_level";
    public static final String ALLOW_USER_DECRYPT = "allow_user_decryption";
    public static final String DATA_RECOVERY_CERT_ID = "data_recovery_cert_id";
    public static final String POLICY_NAME = "policy_name";
    public static final String POLICY_ID = "policy_id";
    public static final String POLICY_DESCRIPTION = "policy_desc";
    public static final String APP_IDENTIFIER = "app_identifier";
    public static final String IS_ALLOWED = "is_allowed";
    public static final String APP_TYPE = "app_type";
    public static final String IS_API = "isApi";
    public static final String APPLE_CORPORATE = "apple_corporate";
    public static final String ANDROID_CORPORATE = "android_corporate";
    public static final String WINDOWS_CORPORATE = "windows_corporate";
    public static final String NEUTRAL_CORPORATE = "neutral_corporate";
    public static final String APPLE_PERSONAL = "apple_personal";
    public static final String ANDROID_PERSONAL = "android_personal";
    public static final String WINDOWS_PERSONAL = "windows_personal";
    public static final String NEUTRAL_PERSONAL = "neutral_personal";
    public static final String DEFAULT_IOS_CORPORATE_GROUP_STR = "1";
    public static final String DEFAULT_IOS_PERSONAL_GROUP_STR = "2";
    public static final String DEFAULT_ANDROID_CORPORATE_GROUP_STR = "3";
    public static final String DEFAULT_ANDROID_PERSONAL_GROUP_STR = "4";
    public static final String DEFAULT_WINDOWS_CORPORATE_GROUP_STR = "5";
    public static final String DEFAULT_WINDOWS_PERSONAL_GROUP_STR = "6";
    public static final String DEFAULT_NEUTRAL_CORPORATE_GROUP_STR = "7";
    public static final String DEFAULT_NEUTRAL_PERSONAL_GROUP_STR = "8";
    public static final String DOMAIN = "domain";
    public static final String USER = "user";
    public static final String ADMIN_EMAIL = "admin_email";
    public static final String SIGNUP_URL = "redirect_url";
    public static final String AUTH_CODE = "auth_code";
    public static final String TEMPLATE_TYPE = "template_id";
    public static final String EMAIL = "email";
    public static final String EMAIL_ID = "user_email";
    public static final String DOMAIN_NETBIOS_NAME = "domain_name";
    public static final String NAME = "name";
    public static final String USER_NAME = "user_name";
    public static final String MANAGED_DOMAINS = "managed_domains";
    public static final String DEVICE_IDS = "device_ids";
    public static final String DEVICE_ID = "device_id";
    public static final String ADDITIONAL_CONTEXT = "additional_context";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String SELECT_ALL = "select_all";
    public static final String GROUP = "group";
    public static final String GROUP_ID = "group_id";
    public static final String GROUP_NAME = "name";
    public static final String PAYLOADS = "payloads";
    public static final String USER_PARAMS = "params";
    public static final String REDIRECT_URL = "redirect_url";
    public static final String USER_DETALS = "user_details";
    public static final String CONFIGURATION = "configuration";
    public static final String FILE_PATH = "file_path";
    public static final String ORDER_BY = "orderby";
    public static final String SORT_ORDER = "sortorder";
    public static final String SEARCH_FIELD = "searchfield";
    public static final String SEARCH_KEY = "searchkey";
    public static final String ASC = "asc";
    public static final String DESC = "desc";
    public static final String IMPORT_DEVICES_TO_GROUP_CSV = "import_devices_to_group_csv";
    public static final String FAILURE = "failure";
    public static final String REMOTE_ADDRESS = "remote_address";
    public static final String ENROLLMENT_URL = "enrollment_url";
    public static final String OTP_PASSWORD = "otp_password";
    public static final String OWNED_BY = "owned_by";
    public static final String SERVER_NAME = "server_name";
    public static final String QR_URL = "qr_url";
    public static final String PLATFORM_TYPE = "platform_type";
    public static final String EMAIL_ADDRESS = "email_address";
    public static final String AUTH_MODE = "auth_mode";
    public static final String PLAY_STORE_URL = "play_store_url";
    public static final String SERVER_PORT = "server_port";
    public static final String DISCOVER_URL = "discover_url";
    public static final String IS_APP_BASED_ENROLLMENT_FOR_WINDOWS_PHONE = "is_app_based_enrollment_for_windows_phone";
    public static final String TINY_URL = "tiny_url";
    public static final String APK_URL = "apk_url";
    public static final String BULK_ENROLLMENT_CSV = "bulk_enrollment_csv";
    public static final String BULK_DEPROVISION_CSV = "bulk_deprovision_csv";
    public static final String USER_IMPORT_CSV = "user_import_csv";
    public static final String EAS_POLICY_CSV = "eas_policy_csv";
    public static final String FILEVAULT_RECOVERY_KEY_IMPORT = "filevault_recovery_key_import";
    public static final String WAITING_FOR_LICENSE_COUNT = "waiting_for_license_count";
    public static final String ENROLLED_COUNT = "enrolled_count";
    public static final String IS_LICENSE_LIMIT_REACHED = "is_license_limit_reached";
    public static final String LICENSE_LIMIT = "license_limit";
    public static final String KNOX_GROUP_ID = "knox_group_id";
    public static final String KNOX_DISTIBUTION_OPTION = "knox_distibution_option";
    public static final String KNOX_DISTIBUTION_TO_GROUP = "knox_distibution_to_selected_group";
    public static final String MANAGED_USER_OPERATION_LABEL = "ManagedUserImport";
    public static final String DEPROVISION_PERMISSION = "deprovision_permission";
    public static final String APP_DISTRIBUTION_PERMISSION = "app_distribution_permission";
    public static final String PORIFILE_ASSOCIATION_PERMISSION = "profile_association_permission";
    public static final String LOCATE_DEVICE_PERMISSION = "locate_device_permission";
    public static final String RESOURCE_LIST = "resource_list";
    public static final String TOTAL_RECORD_COUNT = "total_record_count";
    public static final String METADATA = "metadata";
    public static final String DELTA_TOKEN = "delta-token";
    public static final String PAGING = "paging";
    public static final String SYNCML_COMMANDS = "syncml_commands";
    public static final String IS_BLOB = "is_blob";
    public static final String DATA_FILE_ID = "data_file_id";
    public static final String IS_MODIFIED = "is_modified";
    public static final String REPORTS_URL_FULL_PREFIX = "#/uems/mdm/reports/predefined/";
    public static final String REPORTS_URL_PREFIX = "predefined.";
    public static final String DEVICE_IMPORT_CSV_NAME = "deviceCSVFile";
    
    public class Configuration
    {
        public static final String ASSOCIATED_USER = "associated_user";
        public static final String AUTO_LOGON_APP = "auto_logon_app";
        public static final String CREATE_USER = "create_user";
        public static final String AUTO_DISTRIBUTE_APPS = "auto_distribute_apps";
    }
    
    public class AllowedApps
    {
        public static final String IDENTIFIER = "app_identifier";
    }
    
    public class Certificates
    {
        public static final String RENEWAL_SETTINGS = "renewal_settings";
        public static final String SERVER_URL = "url";
        public static final String SERVER_NAME = "server_name";
        public static final String CA_THUMBPRINT = "ca_finger_print";
        public static final String SERVER_TYPE = "type";
        public static final String SERVER_ID = "server_id";
        public static final String RA_CERTIFICATE_ID = "ra_certificate_id";
        public static final String PROFILE_OID = "profile_oid";
        public static final String TEMPLATE_ID = "template_id";
        public static final String OLD_CERTIFICATE_ID = "old_certificate_id";
        public static final String CERTIFICATE_IDS = "certificate_ids";
        public static final String SCEP_SERVERS = "scep_servers";
        public static final String TEMPLATE_IDS = "template_ids";
        public static final String TEMPLATE_COUNT = "template_count";
        public static final String REDISTRIBUTE_PROFILES = "redistribute_profiles";
        public static final String CA_CERT_DETAILS = "ca_cert_details";
        public static final String CA_CERTIFICATE_ID = "ca_certificate_id";
        public static final String CSR_ID = "csr_id";
        public static final String CHALLENGE = "CHALLENGE";
    }
    
    public static final class UserAssignmentRules
    {
        public static final String USER_ASSIGNMENT_RULE = "user_assignment_rule";
        public static final String TEMPLATE_ID = "template_id";
        public static final String ON_BOARD_RULE_ID = "on_board_rule_id";
    }
    
    public class Cookie
    {
        public static final String NAME = "cookie_name";
        public static final String VALUE = "cookie_value";
        public static final String MAX_AGE = "cookie_max_age";
    }
    
    public class ContentType
    {
        public static final String FILE_TYPE_IMAGE = "image";
        public static final String FILE_TYPE_CERTIFICATE = "certificate";
        public static final String FILE_TYPE_ENTERPRISE_APP = "enterprise_app";
        public static final String FILE_TYPE_CSV = "csv";
        public static final String FILE_TYPE_HTML = "html";
        public static final String FILE_TYPE_CONTENT_MGMT = "content_mgmt";
        public static final String FILE_TYPE_JSON = "json";
        public static final String FILE_TYPE_XML = "xml";
        public static final String FILE_TYPE_PLAIN = "plain";
        public static final String FILE_TYPE_FONT = "mac_font";
        public static final String FILE_TYPE_ZIP = "zip";
        public static final String FILE_TYPE_CUSTOM_PROFILE = "custom_profile";
    }
    
    public class DelegateScope
    {
        public static final String DELEGATION_APP_RESTRICTIONS = "delegation_app_restrictions";
        public static final String DELEGATION_BLOCK_UNINSTALL = "delegation_block_uninstall";
        public static final String DELEGATION_CERT_INSTALL = "delegation_cert_install";
        public static final String DELEGATION_ENABLE_SYSTEM_APP = "delegation_enable_system_app";
        public static final String DELEGATION_PACKAGE_ACCESS = "delegation_package_access";
        public static final String DELEGATION_PERMISSION_GRANT = "delegation_permission_grant";
    }
}
