package com.me.mdm.chrome.agent.commands.profiles;

public class PayloadConstants
{
    public static final String PASSCODE = "Passcode";
    public static final String RESTRICTIONS = "Restrictions";
    public static final String BROWSER_POLICIES = "BrowserPolicies";
    public static final String EMAILCONFIG = "Email";
    public static final String EMAIL_EXCHANGE = "ActiveSync";
    public static final String CERTIFICATE = "Certificate";
    public static final String WIFI = "Wifi";
    public static final String ETHERNET = "Ethernet";
    public static final String VPN = "VPN";
    public static final String PRIVACY = "Privacy";
    public static final String KIOSK = "Kiosk";
    public static final String WALLPAPER = "Wallpaper";
    public static final String GLOBAL_PROXY = "GlobalProxy";
    public static final String WEB_CONTENT_FILTER = "WebContentFilter";
    public static final String APN = "APN";
    public static final String OS_UPDATE_POLICY = "OSUpdatePolicy";
    public static final String DEVICE_VERIFIED_ACCESS = "VerifiedAccess";
    public static final String POWER_MANAGEMENT = "PowerIdleManagement";
    public static final String WEBCONTENT_FILTER = "WebContent";
    public static final String USER_RESTRICTION = "UserRestrictions";
    public static final String MANAGED_BOOKMARKS = "ManagedBookMarks";
    public static final String CHROME_BROWSER_RESTRICTION = "ChromeBrowserRestriction";
    public static final String EXTENSION_INSTALL_SOURCES = "ExtensionInstallSources";
    public static final String USER_VERIFIED_ACCESS = "VerifiedAccess";
    public static final String MANAGED_GUEST_SESSION = "ManagedGuestSession";
    public static final String PAYLOAD_TYPE = "PayloadType";
    public static final String PAYLOAD_IDENTIFIER = "PayloadIdentifier";
    public static final String PAYLOAD_CONTENT = "PayloadContent";
    public static final String PAYLOAD_DISPLAY_NAME = "PayloadDisplayName";
    public static final int UNDER_COMPLIANCE = 2;
    public static final int COMPLIANCE = 1;
    public static final int COMPLIANCE_USER_DEFINED = 0;
    public static final int ENABLE = 1;
    public static final int DISABLE = 2;
    public static final int ALLOWED = 1;
    public static final int RESTRICTED = 0;
    public static final int NOT_CONFIGURED = -1;
    public static final int USER_CONFIGRABLE = 3;
    public static final int ALWAYS_ON = 4;
    public static final int ALWAYS_OFF = 5;
    public static final boolean YES = true;
    public static final boolean NO = false;
    public static final String PAYLOAD_UNDER_COMP_LIST = "payloadUnderComp";
    public static final String PAYLOAD_COMP_LIST = "payloadComp";
    public static final String SERVER_KEY_SEPARATOR = "@@@";
    public static final String PWD_QUALITY = "pwdQuality";
    public static final String PWD_LENGTH = "pwdLength";
    public static final String PWD_MAX_FAILED = "pwdMaxFailed";
    public static final String LOCK_MAX_TIMEOUT = "maxLockTimeOut";
    public static final String CERTIFICATE_LIST = "CertificatesList";
    public static final String PROFILE_CONFIG_LIST = "PayloadConfigList";
    public static final String COMPLIANCE_MONITOR_LIST = "ComplianceMonitorList";
    public static final String BLUETOOTH = "allowBluetooth";
    public static final String BACKGROUNDDATA = "disableBackgroundData";
    public static final String USEPACKETDATA = "allowCellularData";
    public static final String CELLULAR_DATA_DISABLE = "disableCellularData";
    public static final String GPS_DISABLE = "disableGPS";
    public static final String CAMERA = "allowCamera";
    public static final String USB = "allowUSB";
    public static final String DEVICE_ADMIN = "DeviceAdmin";
    public static final String PASSWORD = "Password";
    public static final String STORAGEENCRYPTION = "disableStorageEncryption";
    public static final String NFC = "allowNFC";
    public static final String GPS_POLICY = "GPSPolicy";
    public static final String SET_GPS_STATE_CHANGE_ALLOWED = "setGPSStateChangeAllowed";
    public static final String SET_NFC_STATE_CHANGE_ALLOWED = "setNFCStateChangeAllowed";
    public static final String GRACE_TIME_CONFIGURE_POLICY = "GraceTimeToConfigurePolicy";
    public static final String TIME_TO_REMIND_USER = "TimeToRemindUser";
    public static final String NO_OF_TIME_TO_REMIND = "NoOfTimeToRemindUser";
    public static final int ERROR_DEVICE_ADMIN_DISABLED = 12030;
    public static final int ERROR_PROFILE_ALREADY_REMOVED = 12031;
    public static final int ERROR_INVALID_PAYLOAD = 12032;
    public static final int ERROR_UNKNOWN_PAYLOAD_ERROR = 12179;
    public static final int ERROR_PAYLOAD_NOTCOMPATIBLE_FOR_PROFILEOWNER = 12150;
    public static final int ERROR_CLEAR_PASSCODE_FAILED = 12140;
    public static final int ERROR_RESET_PASSCODE_FAILED = 12141;
    public static final int ERROR_CLEAR_PASSCODE_FAILED_DEVICE_ENCRYPTED = 12142;
}
