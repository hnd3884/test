package com.adventnet.sym.server.mdm.util;

import java.util.Arrays;
import java.util.List;

public class MDMCommonConstants
{
    public static final int NEUTRAL_PLATFORM = 0;
    public static final int IOS_PLATFORM = 1;
    public static final int ANDROID_PLATFORM = 2;
    public static final int WINDOWS_PHONE_PLATFORM = 3;
    public static final int CHROME_OS_PLATFORM = 4;
    public static final int MACOS_PLATFORM = 6;
    public static final int UNIDENTIFIED_PLATEFORM = 5;
    public static final int TVOS_PLATFORM = 7;
    public static final int CONNECTOR_PLATFORM = 101;
    public static final List<Integer> APPLE_PLATFORM;
    public static final int CMD_REP_PROFILE_TYPE = 1;
    public static final int CMD_REP_NATIVE_TYPE = 2;
    public static final int CMD_REP_ADMIN_TYPE = 3;
    public static final int CMD_REP_USER_CHANNEL = 4;
    public static final int CMD_REP_DEVICE_COMMANDS_ON_USER_LOGIN = 5;
    public static final int CONNECTOR_REP_TYPE = 101;
    public static final String DEVICE_REQUEST = "DeviceRequest";
    public static final int IOS_PROFILE_AGENT = 1;
    public static final int ANDROID_CORE_AGENT = 2;
    public static final int SAFE_AGENT = 3;
    public static final int WINDOWS_AGENT = 4;
    public static final int ENROLL_STATUS_BY_ADMIN = 5;
    public static final int ANDROID_ADMIN_AGENT = 6;
    public static final int CHROME_INSETVER_AGENT = 7;
    public static final int MDM_CONNECTOR = 101;
    public static final int MAC_AGENT = 8;
    public static final int USER_TYPE_MDM_MANAGED = 1;
    public static final int USER_TYPE_DIRECTORY_MANAGED = 2;
    public static final int ENROLL_FAILED = 0;
    public static final int ENROLLMENT_FAILED = 1;
    public static final int YET_TO_ENROLL = 1;
    public static final int INVITED = 1;
    public static final int ENROLL_SUCCESS = 2;
    public static final int WAITING_FOR_APPROVAL = 3;
    public static final int UN_MANGED = 4;
    public static final int WAITING_FOR_USER_ASSIGNMENT = 5;
    public static final int WAITING_FOR_LICENSE = 6;
    public static final int REMOVED_DEVICE = 7;
    public static final int REPAIR = 9;
    public static final int OLD = 10;
    public static final int RETIRE = 11;
    public static final int SECURITY_FAILED = 12;
    public static final int OWNED_BY_DEFAULT = 0;
    public static final int CORPORATE_DEVICE = 1;
    public static final int PERSONAL_DEVICE = 2;
    public static final int ALL_DEVICES = 3;
    public static final int ENROLLMENT_BY_INVITATION = 1;
    public static final int ENROLLMENT_BY_SELF = 2;
    public static final int ENROLLMENT_BY_ADMIN = 3;
    public static final int ENROLLMENT_BY_INTEGRATION = 4;
    public static final int SUP_DEV_SMART_PHONE = 2;
    public static final int SUP_DEV_TABLET = 3;
    public static final int SUP_DEV_BOTH = 1;
    public static final int SUP_DEV_OTHER = 0;
    public static final int DEVICETYPE_IPOD = 0;
    public static final int DEVICETYPE_IPAD = 3;
    public static final int DEVICETYPE_IPHONE = 2;
    public static final int MODEL_TYPE_OTHER = 0;
    public static final int MODEL_TYPE_SMART_PHONE = 1;
    public static final int MODEL_TYPE_TABLET = 2;
    public static final int MODEL_TYPE_LAPTOP = 3;
    public static final int MODEL_TYPE_DESKTOP = 4;
    public static final int MODEL_TYPE_TV = 5;
    public static final String IPHONE = "iPhone";
    public static final String IPAD = "iPad";
    public static final String IPOD = "ipod";
    public static final String SMART_PHONE = "SmartPhone";
    public static final String TABLET = "Tablet";
    public static final String OTHER = "Other";
    public static final String SMART_PHONE_IN_SDP = "Smart Phone";
    public static final String TABLET_IN_SDP = "Tablet";
    public static final String WORKSTATION_IN_SDP = "Workstation";
    public static final int MDM_PROFILE_TYPE = 1;
    public static final int MDM_APP_PROFILE_TYPE = 2;
    public static final int MDM_OS_UPDATE_POLICY_PROFILE_TYPE = 3;
    public static final int MDM_APP_BLACKLIST_PROFILE = 4;
    public static final int MDM_COMPLIANCE_POLICY_TYPE = 5;
    public static final int MDM_PRE_ACTIVATION_PROFILE_TYPE = 6;
    public static final int MDM_DATA_USAGE_PROFILE_TYPE = 8;
    public static final int MDM_ANNOUNCEMENT_PROFILE_TYPE = 9;
    public static final int MDM_SCHEDULED_ACTION_PROFILE_TYPE = 11;
    public static final int MDM_HIDDEN_APP_TYPE = 7;
    public static final int MDM_HIDDEN_CONFIG_TYPE = 9;
    public static final int MDM_APP_CONFIG_TYPE = 10;
    public static final int MDM_APP_UPDATE_POLICY = 12;
    public static final int YET_TO_REQUEST_PROCESS = 1;
    public static final int REQUEST_PROCESS_IN_PROGRESS = 2;
    public static final int REQUEST_PROCESS_COMPLETED = 3;
    public static final int REQUEST_PROCESS_EXPIRED = 4;
    public static final int HTTP_MODE = 1;
    public static final int HTTPS_MODE = 2;
    public static final int STORE = 3;
    public static final int MULTI_DOWNLOAD_MODE = 4;
    public static final int RELATIVE_URL = 5;
    public static final int RELATIVE_URL_FOR_NFC = 6;
    public static final int STATIC_SERVER_URL = 7;
    public static final int AUTH_PASSCODE = 1;
    public static final int AUTH_AD = 2;
    public static final int AUTH_BOTH = 3;
    public static final int AUTH_ADMIN_ENROLLMENT = 4;
    public static final String DEFAULT_WORKGROUP = "MDM";
    public static final int APP_STORE_FILTER_APP_TYPE = 1;
    public static final int IOS_ENTERPRISE_FILTER_APP_TYPE = 2;
    public static final int PLAY_STORE_FILTER_APP_TYPE = 3;
    public static final int ANDROID_ENTERPRISE_FILTER_APP_TYPE = 4;
    public static final int WINDOWS_BUSINESS_STORE_FILTER_APP_TYPE = 5;
    public static final int WINDOWS_ENTERPRISE_FILTER_APP_TYPE = 6;
    public static final int ANDROID_GOOGLE_HOSTED_ENTERPRISE_APP_TYPE = 10;
    public static final int ANDROID_MEMDM_ENTERPRISE_APP_TYPE = 11;
    public static final int ALL_STORE_APP_TYPE = 2000;
    public static final int ALL_ENTERPRISE_APP_TYPE = 2001;
    public static final int WINDOWS_ENTERPRISE_MSI_FILTER_APP_TYPE = 7;
    public static final int CHROME_STORE_FILTER_APP_TYPE = 8;
    public static final int CHROME_CUSTOM_FILTER_APP_TYPE = 9;
    public static final int DEFAULT_IOS_CORPORATE_GROUP = 1;
    public static final int DEFAULT_IOS_PERSONAL_GROUP = 2;
    public static final int DEFAULT_ANDROID_CORPORATE_GROUP = 3;
    public static final int DEFAULT_ANDROID_PERSONAL_GROUP = 4;
    public static final int DEFAULT_WINDOWS_CORPORATE_GROUP = 5;
    public static final int DEFAULT_WINDOWS_PERSONAL_GROUP = 6;
    public static final int DEFAULT_NEUTRAL_CORPORATE_GROUP = 7;
    public static final int DEFAULT_NEUTRAL_PERSONAL_GROUP = 8;
    public static final String ISAPPLECONFIG = "isAppleConfig";
    public static final String APPLE_CONFIG_ID = "appleConfigId";
    public static final String IOS_CHECKIN_URL = "iOSCheckInURL";
    public static final String IOS_SERVER_URL = "iOSServerURL";
    public static final int MACOS_DC_AGENT_TYPE = 1;
    public static final int MACOS_MDM_AGENT_TYPE = 2;
    public static final String XML_CONTENT = "xmlcontent";
    public static final String PLATFORM_STRING = "platform";
    public static final String CUSTOMER_NAME = "customername";
    public static final String PLATFORM_IOS_STRING = "iOS";
    public static final String PLATFORM_ANDROID_STRING = "android";
    public static final String PLATFORM_WP_STRING = "windows";
    public static final String PLATFORM_MAC_STRING = "macOS";
    public static final String PLATFORM_CHROME_STRING = "ChromeOS";
    public static final String PLATFORM_NEUTRAL_STRING = "neutral";
    public static final String PLATFORM_TV_OS_STRING = "tvOS";
    public static final String AGENT_TYPE = "AgentType";
    public static final int AGENT_YET_TO_INSTALL_STATUS = 0;
    public static final int AGENT_INSTALLED_STATUS = 1;
    public static final int AGENT_CONFIGURATION_PENDING = 2;
    public static final String MACOS_DC_AGENT_CATEGORY_NAME = "Business";
    public static final String MACOS_DC_AGENT_BUNDLE_IDENTIFIER = "com.manageengine.ems";
    public static final String DC_AGENT_PRIMARY_IDENTIFIER = "dcagentservice";
    public static final String MACOS_MDM_AGENT_BUNDLE_IDENTIFIER = "com.manageengine.mdm.mac";
    public static final String MACOS_PACKAGE_FILE_NAME = "MDMMacAgent.pkg";
    public static final String IOS_NATIVE_AGENT_BUNDLE_IDENTIFIER = "com.manageengine.mdm.iosagent";
    public static final String IOS_NATIVE_AGENT_APP_NAME = "ME MDM";
    public static final String IOS_NATIVE_AGENT_APP_STORE_ID = "720111835";
    public static final String IOS_NATIVE_AGENT_APP_STORE_URL = "https://itunes.apple.com/us/app/manageengine-mdm/id720111835?mt=8";
    public static final String IOS_NATIVE_AGENT_APP_IMAGE_URL = "http://is5.mzstatic.com/image/thumb/Purple69/v4/b0/da/ad/b0daadcc-dbff-dd45-4428-83b7bfebbaf2/source/60x60bb.jpg";
    public static final String IOS_NATIVE_AGENT_CATEGORY_NAME = "Business";
    public static final String IOS_NATIVE_AGENT_APP_VERSION = "1.0";
    public static final String IOS_NATIVE_AGENT_COUNTRY_CODE = "US";
    public static final String COUNTRY_CODE_FOR_US = "US";
    public static final String WP_NATIVE_AGENT_BUNDLE_IDENTIFIER = "d73a6956-c81b-4bcb-ba8d-fe8718735ad7";
    public static final String WP_NATIVE_AGENT_PFN = "ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2";
    public static final String WP_NATIVE_AGENT_IDENTIFIER = "ZohoCorp.ManageEngineMDM";
    public static final String WP_NATIVE_AGENT_APP_NAME = "ME MDM for Windows";
    public static final String WP_NATIVE_AGENT_CATEGORY_NAME = "Business";
    public static final String WP_NATIVE_AGENT_COUNTRY_CODE = "US";
    public static final int WP_DISTRIBUTE_VIA_MAIL = 0;
    public static final int WP_DISTRIBUTE_SILENT_INSTALL = 1;
    public static final String ERROR_CODE = "ErrorCode";
    public static final long INVALID_COMMAND_ERROR_CODE = 12100L;
    public static final String NOTIFICATION_SUCCEEDED = "SUCCEEDED";
    public static final String NOTIFICATION_FAILED = "FAILED";
    public static final String NOTIFICATION_TYPE = "NotificationType";
    public static final int IOS_MDM_CLIENT_PUSH_NOTIFICATION = 1;
    public static final int IOS_MDM_APP_PUSH_NOTIFICATION = 101;
    public static final int ANDROID_MDM_CLIENT_PUSH_NOTIFICATION = 2;
    public static final int ANDROID_MDM_APP_PUSH_NOTIFICATION = 201;
    public static final int WINDOWS_MDM_CLIENT_PUSH_NOTIFICATION = 3;
    public static final int WINDOWS_MDM_APP_PUSH_NOTIFICATION = 303;
    public static final int CHROME_MDM_INSERVER_PUSH_NOTIFICATION = 4;
    public static final int CHROME_MDM_USER_AGENT_INSERVER_PUSH_NOTIFICATION = 401;
    public static final int WINDOWS_MDM_CLIENT_TYPE = 1;
    public static final int WINDOWS_APP_CLIENT_TYPE = 2;
    public static final String IS_LANGUAGE_PACK_ENABLED = "IsLanguagePackEnabled";
    public static final String IOS_DISCOVERY_SERVLETS_ADDED = "DiscoveryServletsAdded";
    public static final String ANDROID_FEATURE_ENABLED = "ANDROID_FEATURE_ENABLED";
    public static final int RESTRICITON_ALLOWED = 1;
    public static final int RESTRICITON_RESTRICTED = 2;
    public static final int RESTRICITON_NOT_APPLICABLE = 0;
    public static final String DEFAULT_MDM_APP_NAME = "ME MDM App";
    public static final int AGENT_ALREADY_UP_TO_DATE = 12050;
    public static final int AGENT_UPGRADE_UNKNOWN_ERROR = 12060;
    public static final int AGENT_DOWNLOAD_FAILED = 12180;
    public static final int AGENT_UPGRADE_FAILED = 12051;
    public static final int LOCATION_PERMISSION_NOT_GIVEN = 2;
    public static final int LOCATION_PERMISSION_WHEN_APP_OPEN = 3;
    public static final String KNOX_AGENT = "market://details?id=com.manageengine.mdm.samsung";
    public static final int USER_CONTROLLED = 4;
    public static final int ALWAYS_ON = 5;
    public static final int ALWAYS_OFF = 6;
    public static final String SHOW_MDM_LICENSE_MSG = "SHOW_MDM_LICENSE_MSG";
    public static final String IS_APP_BASED_ENROLLMENT_FOR_WINDOWS_PHONE = "IsAppBasedEnrollmentForWindowsPhone";
    public static final String WP_STORE_APP_PAGE_URL = "http://www.windowsphone.com/s?appid=551ab9a7-413b-4b79-8142-74550af0c72e";
    public static final String WP_STORE_APP_IDENTIFIER = "551ab9a7-413b-4b79-8142-74550af0c72e";
    public static final String WP_STORE_APP_PFN = "ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2";
    public static final String WP_LOCATION_TRACKING_AGENT_MAJOR_VERSION = "9.2.";
    public static final String WP_LOCATION_TRACKING_AGENT_VERSION = "9.2.1.1040";
    public static final String MANAGE_TAB = "Manage";
    public static final String MANAGE_TAB_KEY = "dc.mdm.manage";
    public static final String MANAGE_TAB_URL = "#/uems/mdm/manage/groups-and-devices";
    public static final String HOME_TAB = "Home";
    public static final String HOME_TAB_KEY = "dc.tab.home";
    public static final String HOME_TAB_URL = "#/uems/mdm/home";
    public static final String ASSET_TAB = "Asset";
    public static final String ASSET_TAB_KEY = "dc.common.INVENTORY";
    public static final String ASSET_TAB_URL = "#/uems/mdm/inventory/devicesList";
    public static final String REPORT_TAB = "Reports";
    public static final String REPORT_TAB_KEY = "dc.common.REPORTS";
    public static final String REPORT_TAB_URL = "#/uems/mdm/reports/predefined";
    public static final String SUPPORT_TAB = "Support";
    public static final String SUPPORT_TAB_KEY = "dc.common.SUPPORT";
    public static final String SUPPORT_TAB_URL = "#/uems/mdm/support";
    public static final String MDM_TAB = "MDM";
    public static final String MDM_TAB_KEY = "dc.mdm.MDM";
    public static final String MDM_TAB_URL = "#/uems/mdm/home";
    public static final String ENROLL_TAB = "Enroll";
    public static final String ENROLL_TAB_KEY = "dc.mdm.general.enrollment";
    public static final String ENROLL_TAB_URL = "#/uems/mdm/enrollment/devices";
    public static final String ADMIN_TAB = "Admin";
    public static final String ADMIN_TAB_KEY = "dc.admin.common.ADMIN";
    public static final String ADMIN_TAB_URL = "#/uems/mdm/admin";
    public static final int NATIVE_SERVICE = 1;
    public static final int POLLING_SERVICE = 2;
    public static final int NS_SERVICE = 3;
    public static final String WINDOWS_NOTIFICATION = "WINDOWS_PHONE_NOTIFICATION_SERVICE";
    public static final String NS_PORT = "NsPort";
    public static final String NS_SYNC_INTERVAL_SECONDS = "NsSyncIntervalSeconds";
    public static final int INACTIVE_DEVICE_PERIOD_IN_DAYS = 30;
    public static final String APPLE_CONFIG_2_CONFIGURED = "APPLE_CONFIG_2_CONFIGURED";
    public static final String IS_ABM_PROFILE_UPDATE_REQ = "IS_ABM_PROFILE_UPDATE_REQ";
    public static final String ADMIN_APP_DIRECT_DOWNLOAD_APK_URL = "/agent/mdm/admin/MDMAndroidAdmin.apk";
    public static final String ADMIN_APP_PLAY_STORE_URL = "market://details?id=com.manageengine.mdm.admin";
    public static final String ANDROID_AGENT_APK = "MDMAndroidAgent.apk";
    public static final int ZOHO_MAP_TYPE = 0;
    public static final int GOOGLE_MAP_TYPE = 1;
    public static final String REGENERATE_MOBILE_CONFIG = "REGENERATE_MOBILE_CONFIG";
    public static final String SERVLET_PATH = "ServletPath";
    public static final String PPKG_DOWNLOADED_ALREADY = "PPKG_DOWNLOADED_ALREADY";
    public static final String LAPTOP_TOOL_DOWNLOADED_ALREADY = "LAPTOP_TOOL_DOWNLOADED_ALREADY";
    public static final int SECURITY_COMMAND_PRIORITY = 100;
    public static final int SEQUENTIAL_COMMAND_PRIORITY = 40;
    public static final int COLLECTION_COMMAND_PRIORITY = 40;
    public static final int DEFAULT_COMMAND_PRIORITY = 100;
    public static int SELF_SIGNED_CERT;
    public static int THIRD_PARTY_SSL_CERT;
    public static int SELF_SIGNED_CA_CERT;
    public static final int DB_KEY_USAGE_NONE = 0;
    public static final int DB_KEY_USAGE_SIGNATURE = 1;
    public static final int DB_KEY_USAGE_ENCIPHERMENT = 4;
    public static final int DB_KEY_USAGE_BOTH = 5;
    public static final int DB_CHALLENGE_TYPE_NONE = 0;
    public static final int DB_CHALLENGE_TYPE_STATIC = 1;
    public static final int DB_CHALLENGE_TYPE_DYNAMIC = 2;
    public static final int DB_KEY_SIZE_1024 = 0;
    public static final int DB_KEY_SIZE_2048 = 1;
    public static final int DB_KEY_SIZE_4096 = 2;
    public static final int DB_SAN_TYPE_NONE = 0;
    public static final int DB_SAN_TYPE_RFC822NAME = 1;
    public static final int DB_SAN_TYPE_DNSNAME = 2;
    public static final int DB_SAN_TYPE_URI = 3;
    public static final int DB_SAN_TYPE_UPN = 4;
    public static final String DEFAULT_IOS_CORPORATE_GROUP_NAME = "Default_iOS_Corporate";
    public static final String DEFAULT_IOS_PERSONAL_GROUP_NAME = "Default_iOS_Personal";
    public static final String DEFAULT_ANDROID_CORPORATE_GROUP_NAME = "Default_Android_Corporate";
    public static final String DEFAULT_ANDROID_PERSONAL_GROUP_NAME = "Default_Android_Personal";
    public static final String DEFAULT_WINDOWS_CORPORATE_GROUP_NAME = "Default_Windows_Corporate";
    public static final String DEFAULT_WINDOWS_PERSONAL_GROUP_NAME = "Default_Windows_Personal";
    public static final int GROUP_VIEW_MEMBER_TYPE = 0;
    public static final int INVITE_REG_STATUS_DISCOVERED = 1;
    public static final int INVITE_REG_STATUS_AUTHETICATED = 2;
    public static final int INVITE_REG_STATUS_SUCCESS = 3;
    public static final int INVITE_REG_STATUS_UNDISCOVERED = 0;
    public static final String DEFAULT_PAYLOAD_ORG_NAME = "ZohoCorp";
    public static final String DEFAULT_ENROLLMENT_ORG_NAME = "ManageEngine";
    public static final String TO_BE_ASSOCIATED_APP_SOURCE = "toBeAssociatedAppSource";
    public static final Integer ASSOCIATED_APP_SOURCE_UNKNOWN;
    public static final Integer ASSOCIATED_APP_SOURCE_BY_GROUP_POLICY;
    public static final Integer ASSOCIATED_APP_SOURCE_BY_USER;
    public static final Integer UNASSIGNED_APP_UPDATE;
    public static final Integer ASSOCIATED_APP_SOURCE_BY_AUTO_UPDATE;
    public static final String LICENSE_EXPIRY_DAYS = "licenseExpiryDays";
    public static final int IOS_NATIVE_AGENT_REMOTE_VIEW_VERSION = 1430;
    public static final Long NEUTRAL_ARCHITECTURE;
    public static final Long ARM_ARCHITECTURE;
    public static final Long X86_ARCHITECTURE;
    public static final Long X64_ARCHITECTURE;
    public static final Long ALL_ARCHITECTURE;
    public static final int INVITATION_ENROLLED_SUCCESS = 3;
    public static final int ANDROID_SINGLE_APP_KIOSK_MODE = 0;
    public static final int ANDROID_MULTI_APP_KIOSK_MODE = 1;
    public static final int ANDROID_PAUSE_KIOSK_MODE = 2;
    public static final int ANDROID_SINGLE_WEB_APP_KIOSK = 3;
    public static final int ANDROID_TERMS_AGENT_VERSION = 2300314;
    public static final int DOC_UPLOAD = 1;
    public static final int IMPORT_FROM_URL = 2;
    public static final Long WIN_10_REDSTONE_BUILD_NUMBER;
    public static final Long WIN_10_REDSTONE_2_BUILD_NUMBER;
    public static final Long WIN_10_REDSTONE_4_BUILD_NUMBER;
    public static final Long WIN_10_REDSTONE_5_BUILD_NUMBER;
    public static final Long WIN_10_20H2_BUILD_NUMBER;
    public static final Long WIN_11_BUILD_NUMBER;
    public static final String AGENT_DOWNLOAD_URL = "AgentDownloadUrl";
    public static final String ABSOLUTE_AGENT_DOWNLOAD_URL = "AgentDownloadURL";
    public static final String AGENT_VERSION = "AgentVersion";
    public static final String AGENT_UNIQUE_IDENTIFIER = "AgentUniqueIdentifier";
    public static final String SHA256_FILE_HASH = "SHA256FileHash";
    public static final String CMD_LINE_PARAMS = "CommandLineParams";
    public static final String SERVER_CERTIFICATE = "ServerCertificate";
    public static final String ROOT_CERTIFICATE = "RootCertificate";
    public static final String INTERMEDIATE_CERTIFICATE = "IntermediateCertificate";
    public static final String VPP_FILTER_APP_TYPE = "10";
    public static final String NONVPP_FILTER_APP_TYPE = "11";
    public static final String AFW_FILTER_APP_TYPE = "12";
    public static final String NONAFW_FILTER_APP_TYPE = "13";
    public static final Long SDP_ASSET_CONSENT_ID;
    public static final Long AE_ASSET_CONSENT_ID;
    public static final Long SDP_ALERTS_CONSENT_ID;
    public static final String SHOW_GDPR_WIDGET = "showGdprWidget";
    public static final String SECURE_PERCENT = "securePercent";
    public static final String SECURE_MESSAGE = "secureMessage";
    public static final String INTEGRATION_SETTINGS_STATUS = "integration_settings_status";
    public static final String INTEGRATION_SETTINGS_DATA = "integration_settings_data";
    public static final String SECURITY_STATUS = "security_status";
    public static final String APP_NAME = "app_name";
    public static final String GDPR_WIDGET_ENABLED = "gdpr_widget_enabled";
    public static final String ENABLE_HTTPS_LOGIN = "enable_https_login";
    public static final String CHANGED_SETTINGS = "changedSettings";
    public static final String STATUS_CODE = "STATUS_CODE";
    public static final String CERTIFICATE_DETAILS = "certificateDetails";
    public static final String TERMS_EXISTS_FOR_OWNED_BY = "terms_already_exist_for_ownedby";
    public static final String ALREADY_EXISTING_GROUP = "already_existing_group";
    public static final String SELF_ENROLL_DEVICE_LIMIT = "selfEnrollDeviceLimit";
    public static final String SHOW_SELF_ENROLL_OWNED_BY_CRITERIA = "showOwnedByFilter";
    public static final String WIPE_REASON = "wipe_reason";
    public static final String WIPE_TYPE = "wipe_type";
    public static final String COMMENT = "other_reason";
    public static final String MANAGED_DEVICE_ID = "managedDeviceId";
    public static final String DEVICE_STATE = "device_state";
    public static final String REPAIR_STR = "In Repair";
    public static final String RETIRE_STR = "Retired";
    public static final String STOCK_STR = "In Stock";
    public static final int DEVICE_NOT_ENROLLED_ERROR = 13001;
    public static final int PESONAL_SPACE_ERROR = 1001;
    public static final int INCORRECT_OPTION_ERROR = 13003;
    public static final String COMPLIANCE_TAB = "Compliance";
    public static final String COMPLIANCE_TAB_KEY = "mdm.common.Compliance";
    public static final String COMPLIANCE_TAB_URL = "#/uems/mdm/manage/compliance/listCompliance";
    public static final int SUP_IPHONE = 2;
    public static final int SUP_IPAD = 1;
    public static final int SUP_IPOD = 4;
    public static final int SUP_TVOS = 8;
    public static final int SUP_IOS_ALL_SMART = 15;
    public static final int SUP_IPHONE_IPAD = 3;
    public static final int SUP_IPHONE_IPOD = 6;
    public static final int SUP_IPHONE_TVOS = 10;
    public static final int SUP_IPAD_IPOD = 5;
    public static final int SUP_IPAD_TVOS = 9;
    public static final int SUP_IPOD_TVOS = 12;
    public static final int SUP_IPHONE_IPAD_IPOD = 7;
    public static final int SUP_IPHONE_IPAD_TVOS = 11;
    public static final int SUP_IPHONE_IPOD_TVOS = 14;
    public static final int SUP_IPAD_IPOD_TVOS = 13;
    public static final int SUP_MAC = 16;
    public static final int PERMISSION_USER_CONTROL = 2;
    public static final int PERMISSION_ALLOW = 1;
    public static final int PERMISSION_DENY = 3;
    public static final int PERMISSION_ALLOW_STANDARD_USER_TO_CONFIGURE = 4;
    public static final String STR_VPP = "ABM/ASM";
    public static final String STR_PFW = "Managed Google Play";
    public static final String STR_BSTORE = "Windows Business Store";
    public static final int EXPORT_TYPE_DEVICE_LOCATION_HISTORY = 1;
    public static final int EXPORT_STATUS_PROCESSING_SCHEDULED = 101;
    public static final int EXPORT_STATUS_PROCESSING_STARTED = 102;
    public static final int EXPORT_STATUS_PROCESSING_INPROGRESS = 103;
    public static final int EXPORT_STATUS_PROCESSING_SUCCESS = 104;
    public static final int EXPORT_STATUS_PROCESSING_FAILED = 115;
    public static final int LOC_EXPORT_STATUS_ZMAPS_RGEOCODE_API_DATA_SUBMIT_RETRY = 501;
    public static final int LOC_EXPORT_STATUS_ZMAPS_RGEOCODE_API_DATA_SUBMIT_SUCCESS = 502;
    public static final int LOC_EXPORT_STATUS_ZMAPS_RGEOCODE_API_DATA_SUBMIT_FAILED = 503;
    public static final int LOC_EXPORT_STATUS_ZMAPS_RGEOCODE_RESULT_API_DATA_FETCH_RETRY = 504;
    public static final int LOC_EXPORT_STATUS_ZMAPS_RGEOCODE_RESULT_API_DATA_FETCH_SUCCESS = 505;
    public static final int LOC_EXPORT_STATUS_ZMAPS_RGEOCODE_RESULT_API_DATA_FETCH_FAILED = 506;
    public static final int LOC_EXPORT_STATUS_FAILED_DUE_TO_SERVER_RESTART = 511;
    public static final String EMAIL_ADDRESS_LIST = "EMAIL_ADDRESS_LIST";
    public static final String ANDROID_NOTOFICATION_SETTINGS = "#/uems/mdm/manage/compliance/listCompliance";
    public static final String REBRANDING_SETTINGS = "REBRANDING_SETTINGS";
    public static final String MANAGE_APP_SETTINGS = "MANAGE_APP_SETTINGS";
    public static final String[] ME_MDM_APP_SETTINGS_REMOVE_LIST;
    public static final int DEFAULT_GRACE_TIME = 5;
    public static final int DEFAULT_USER_REM_TIME = 30;
    public static final int DEFAULT_USER_REM_COUNT = 5;
    public static final String CATEGORY_BUSINESS = "Business";
    public static final String CATEGORY_PRODUCTIVITY = "Productivity";
    public static final int SELF_ENROLLMENT_AD_GROUPS_SELECT_TREE_ID = 2002;
    public static final int SELF_ENROLLMENT_GET_SELECTED_AD_GROUPS_TREE_ID = 2001;
    public static final int MOVE_TO_TRASH = 1;
    public static final int DELETE_PERMANENTLY = 2;
    public static final int RESTORE_APPS = 3;
    public static final int IOS_NO_KIOSK_MODE = 0;
    public static final int IOS_MULTI_APP_KIOSK_MODE = 2;
    public static final int IOS_SINGLE_APP_KIOSK_MODE = 1;
    public static final int IOS_SINGLE_WEB_APP_KIOSK_MODE = 3;
    public static final String SCHEDULED_SCAN_USER_ID = "SCHEDULED_SCAN_USER_ID";
    public static final String PUBLISH_PROFILE = "PUBLISH_PROFILE";
    public static final int AD_AUTHENTICATION_ENABLE = 4;
    public static final int AD_AUTHENTICATION_DISABLE = 0;
    public static final String TRANSFORMER_PRE_DATA = "TRANSFORMER_PRE_DATA";
    public static final String SCAN_TIME_OUT_THRESHOLD = "scanTimeOutThreshold";
    public static final String LAST_SCAN_ALL_INITIATED_TIME = "lastScanAllInitiatedTime";
    public static final String PARAM_NAME = "param_name";
    public static final String PARAM_VALUE = "param_value";
    public static final String WINDOWS_DC_AGENT_MSI_PRODUCT_CODE = "6AD2231F-FF48-4D59-AC26-405AFAE23DB7";
    public static final int FIRST_SLOT = 1;
    public static final int SECOND_SLOT = 2;
    public static final int GEO_STATUS_NOT_APPLICABLE = 1;
    public static final int GEO_STATUS_LIMITED = 2;
    public static final int GEO_STATUS_PERMISSION_DENIED = 3;
    public static final int GEO_STATUS_LOCATION_DISABLED = 4;
    public static final int GEO_STATUS_MISSING_APP = 5;
    public static final int GEO_STATUS_APP_NOT_REISTERED = 6;
    public static final int GEO_STATUS_ACTIVE = 7;
    public static final int GEO_STATUS_INACTIVE = 8;
    public static final int GEO_STATUS_FAILED = 9;
    public static final String HASH_PLIST = "hashPlist";
    public static final String REQUEST_TIME = "reqTime";
    public static final int NOT_SUPPORTED = -1;
    public static final int ALLOWED = 1;
    public static final int DIS_ALLOWED = 2;
    public static final int NOT_AVAILABLE = 3;
    public static final String SEND_INVITE = "send_invite";
    public static final String MDM_IDENTITY = "MDM-IDENTITY";
    public static final String MDM_ROOT_CA_CERT_FILE_NAME = "MdmRootCA.crt";
    public static final String MDM_ROOT_CA_KEY_FILE_NAME = "MdmRootCA.key";
    public static final String MDM_IOS_SCEP_ENROLLMENT_PATH = "/mdm/enrollment/identitycertificate/scep";
    public static final String MDM_IOS_SCEP_ENROLLMENT_REDIRECT_PATH = "/mdm/enrollment/identitycertificate/scepredirect/";
    public static final int TRACK_ONLY_WHEN_LOST = 0;
    public static final int TRACK_ALWAYS = 1;
    public static final String POST_URL_TO_GET_SECURE_KEYS = "https://mdm.manageengine.com/api/v1/mdm";
    public static final String HASH_URL_TO_GET_SECURE_KEYS = "https://mdmdatabase.manageengine.com/MISC/op_secure_key/op-code.txt";
    public static final String POPULATE_DUMMY_LOC_SYS_PARAM = "PopulateDummyLocation";
    public static final String IOSMDM_PROFILE_SIGINING = "IOSMDM_PROFILE_SIGINING";
    public static final String INVENTORY_WIPE_DEPROVISION_COMMENT = "default";
    
    static {
        APPLE_PLATFORM = Arrays.asList(1, 6, 7);
        MDMCommonConstants.SELF_SIGNED_CERT = 1;
        MDMCommonConstants.THIRD_PARTY_SSL_CERT = 2;
        MDMCommonConstants.SELF_SIGNED_CA_CERT = 3;
        ASSOCIATED_APP_SOURCE_UNKNOWN = 0;
        ASSOCIATED_APP_SOURCE_BY_GROUP_POLICY = 1;
        ASSOCIATED_APP_SOURCE_BY_USER = 2;
        UNASSIGNED_APP_UPDATE = 3;
        ASSOCIATED_APP_SOURCE_BY_AUTO_UPDATE = 4;
        NEUTRAL_ARCHITECTURE = 1L;
        ARM_ARCHITECTURE = 2L;
        X86_ARCHITECTURE = 4L;
        X64_ARCHITECTURE = 8L;
        ALL_ARCHITECTURE = 14L;
        WIN_10_REDSTONE_BUILD_NUMBER = 14393L;
        WIN_10_REDSTONE_2_BUILD_NUMBER = 15063L;
        WIN_10_REDSTONE_4_BUILD_NUMBER = 17115L;
        WIN_10_REDSTONE_5_BUILD_NUMBER = 17763L;
        WIN_10_20H2_BUILD_NUMBER = 19041L;
        WIN_11_BUILD_NUMBER = 22000L;
        SDP_ASSET_CONSENT_ID = 3L;
        AE_ASSET_CONSENT_ID = 4L;
        SDP_ALERTS_CONSENT_ID = 5L;
        ME_MDM_APP_SETTINGS_REMOVE_LIST = new String[] { "GRACE_TIME", "USER_REM_TIME", "USER_REM_COUNT" };
    }
    
    public class DeviceAttestationProperties
    {
        public static final int NOT_APPLICABLE = 0;
        public static final int DEVICE_ATTESTATION_SUCCESS = 1;
        public static final int DEVICE_ATTESTATION_FAILED = 2;
        public static final int DEVICE_ATTESTATION_PROPERTY_MISMATCH = 3;
        public static final String SERIAL_NUMBER_OID = "1.2.840.113635.100.8.9.1";
        public static final String UDID_OID = "1.2.840.113635.100.8.9.2";
        public static final String OS_VERSION_OID = "1.2.840.113635.100.8.10.2";
        public static final String NONCE_OID = "1.2.840.113635.100.8.11.1";
    }
}
