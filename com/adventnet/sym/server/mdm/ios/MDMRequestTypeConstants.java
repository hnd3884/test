package com.adventnet.sym.server.mdm.ios;

import java.util.ArrayList;
import java.util.List;

public class MDMRequestTypeConstants
{
    public static final String INSTALL_PROFILE = "InstallProfile";
    public static final String REMOVE_PROFILE = "RemoveProfile";
    public static final String PROFILE_LIST = "ProfileList";
    public static final String QUERIES = "Queries";
    public static final String PROVISIONING_PROFILE_LIST = "ProvisioningProfileList";
    public static final String INSTALL_PROVISIONING_PROFILE = "InstallProvisioningProfile";
    public static final String REMOVE_PROVISIONING_PROFILE = "RemoveProvisioningProfile";
    public static final String CERTIFICATE_LIST = "CertificateList";
    public static final String INSTALLED_APPLICATION_LIST = "InstalledApplicationList";
    public static final String INSTALL_ENTERPRISE_APPLICATION = "InstallEnterpriseApplication";
    public static final String DEVICE_CONFIGURED = "DeviceConfigured";
    public static final String ACCOUNT_CONFIGURATION = "AccountConfiguration";
    public static final String MANAGED_APPS_ONLY = "ManagedAppsOnly";
    public static final String INSTALLED_APPLICATION_LIST_IDENTIFIERS = "Identifiers";
    public static final String MANAGED_APPLICATION_LIST = "ManagedApplicationList";
    public static final String DEVICE_INFORMATION = "DeviceInformation";
    public static final String DEVICE_NAME = "DeviceName";
    public static final String IOS_DEVICE_NAME = "IOSDEVICENAME";
    public static final String SECURITY_INFO = "SecurityInfo";
    public static final String DEVICE_LOCK = "DeviceLock";
    public static final String UNLOCK_DEVICE_ACCOUNT = "UnlockUserAccount";
    public static final String CLEAR_PASSCODE = "ClearPasscode";
    public static final String PASSCODE_DISABLE = "DisablePasscode";
    public static final String REMOVE_PASSCODE_DISABLE = "RemoveDisablePasscode";
    public static final String CLEAR_PASSCODE_DISABLE = "ClearPasscodeForPasscodeRestriction";
    public static final String RESTRICT_PASSCODE = "RestrictPasscode";
    public static final String REMOVE_RESTRICT_PASSCODE = "RemoveRestrictedPasscode";
    public static final String CLEAR_PASSCODE_RESTRICTION = "ClearPasscodeRestriction";
    public static final String RESET_PASSCODE = "ResetPasscode";
    public static final String ERASE_DEVICE = "EraseDevice";
    public static final String CORPORATE_WIPE = "CorporateWipe";
    public static final String DEVICE_RING = "DeviceRing";
    public static final String REMOTE_DEBUG = "RemoteDebug";
    public static final String RESTRICTIONS = "Restrictions";
    public static final String INSTALL_APPLICATION = "InstallApplication";
    public static final String REMOVE_APPLICATION = "RemoveApplication";
    public static final String APPLY_REDEMPTION_CODE = "ApplyRedemptionCode";
    public static final String REMOVE_DEVICE = "RemoveDevice";
    public static final String SYNC_NATIVE_AGENT_SETTINGS = "SyncAgentSettings";
    public static final String GET_LOCATION = "GetLocation";
    public static final String INVITE_TO_PROGRAM = "InviteToProgram";
    public static final String ENABLE_LOST_MODE = "EnableLostMode";
    public static final String DISABLE_LOST_MODE = "DisableLostMode";
    public static final String LOST_MODE_DEVICE_LOCATION = "DeviceLocation";
    public static final String PROGRAM_ID = "ProgramID";
    public static final String INVITATION_URL = "InvitationURL";
    public static final String SILENT_INSTALL_APPLICATION = "SilentInstallApplication";
    public static final String UPDATE_APPLICATION = "UpdateApplication";
    public static final String DEVICE_ENROLLMENT = "Enrollment";
    public static final String SYNC_APP_CATALOG = "SyncAppCatalog";
    public static final String APP_ENROLLMENT_TOKEN = "AppEnrollmentToken";
    public static final String DEVICE_CLIENT_SETTINGS = "DeviceClientSettings";
    public static final String SETTINGS = "Settings";
    public static final String APPLICATION_CONFIGURATION = "ApplicationConfiguration";
    public static final String INSTALL_APPLICATION_CONFIGURATION = "InstallApplicationConfiguration";
    public static final String REMOVE_APPLICATION_CONFIGURATION = "RemoveApplicationConfiguration";
    public static final String INSTALL_APP_UPDATE_POLICY = "InstallAppUpdatePolicy";
    public static final String REMOVE_APP_UPDATE_POLICY = "RemoveAppUpdatePolicy";
    public static final String INSTALL_SCHEDULE_CONFIGURATION = "InstallScheduleConfiguration";
    public static final String REMOVE_SCHEDULE_CONFIGURATION = "RemoveScheduleConfiguration";
    public static final String DEVICE_COMMUNICATION_PUSH = "DeviceCommunicationPush";
    public static final String GET_CHANNEL_URI = "GetChannelUri";
    public static final String CHECK_COMMAND_STATUS = "CheckCommandTask";
    public static final String MANAGED_APPLICATION_FEEDBACK = "ManagedApplicationFeedback";
    public static final String RE_REGISTER_NOTIFICATION_TOKEN = "ReregisterNotificationToken";
    public static final String SYSTEM_APP_DEVICE_SCAN = "PreloadedAppsInfo";
    public static final String SYSTEM_APP_CONTAINER_SCAN = "PreloadedContainerAppsInfo";
    public static final String ANDROID_OS_UPGRADE = "OSUpgraded";
    public static final String ANDROID_DEVICE_INFORMATION = "DeviceInfo";
    public static final String SCEP_STATUS_CHECK = "ScepStatusCheck";
    public static final String LOST_MODE_DISABLED = "LostModeDisabled";
    public static final String PLAY_LOST_MODE_SOUND = "PlayLostModeSound";
    public static final String RESTART_DEVICE = "RestartDevice";
    public static final String SHUT_DOWN_DEVICE = "ShutDownDevice";
    public static final String LOCATION_UPDATE = "LocationUpdate";
    public static final String LOCATION_CONFIGURATION = "LocationConfiguration";
    public static final String KIOSK_AGENT_STATUS = "KioskAgentStatus";
    public static final String DEVICE_EVENTS = "DeviceEvents";
    public static final String MDM_DEFAULT_APP_CONFIGURATION = "MDMDefaultApplicationConfiguration";
    public static final String MDM_DEFAULT_APP_CONFIGURATION_MIGRATE = "MDMDefaultApplicationConfigMigrate";
    public static final String INSTALL_MANAGED_SETTINGS = "InstallManagedSettings";
    public static final String SMS_SAVE_PUBLIC_KEY = "SavePublicKey";
    public static final String KIOSK_INSTALL_PROFILE = "KioskInstallProfile";
    public static final String KIOSK_CUSTOM_INSTALL_PROFILE = "KioskDefaultRestriction";
    public static final String KIOSK_CUSTOM_REMOVE_PROFILE = "RemoveKioskDefaultRestriction";
    public static final String KIOSK_UPDATE_PROFILE = "KioskUpdateProfile";
    public static final String DEFAULT_MDM_KIOSK_PROFILE = "DefaultMDMKioskProfile";
    public static final String DEFAULT_MDM_REMOVE_KIOSK_PROFILE = "DefaultMDMRemoveKioskProfile";
    public static final String SINGLE_WEB_APP_KIOSK_CONFIGURATION = "SingleWebAppKioskAppConfiguration";
    public static final String REMOVE_SINGLE_WEB_APP_KIOSK_CONFIGURATION = "RemoveSingleWebAppKioskAppConfiguration";
    public static final String SINGLE_WEB_APP_KIOSK_APP_FEEDBACK = "SingleWebAppKioskFeedback";
    public static final String REMOVE_SINGLE_WEB_APP_KIOSK_FEEDBACK = "RemoveSingleWebAppKioskFeedback";
    public static final String DEFAULT_APP_CATALOG = "DefaultAppCatalogWebClips";
    public static final String DEFAULT_APP_CATALOG_MIGRATE = "DefaultAppCatalogWebClipsMigrate";
    public static final String REMOVE_DEFAULT_APP_CATALOG = "DefaultRemoveAppCatalogWebClips";
    public static final String LOCK_SCREEN_MESSAGES = "LockScreenMessages";
    public static final String REMOVE_USER_INSTALLED_PROFILE = "RemoveUserInstalledProfile";
    public static final String SINGLETON_RESTRICTION = "SingletonRestriction";
    public static final String REMOVE_SINGLETON_RESTRICTION = "RemoveSingletonRestriction";
    public static final String REMOVE_DEVICE_NAME_RESTRICTION = "IOSRemoveDeviceNameRestriction";
    public static final String REMOVE_AFFECTED_SINGLETON_RESTRICTION = "RemoveAffectedSingletonRestriction";
    public static final String REMOVE_IOS_DEVICE_NAME_RESTRICTION = "RemoveIosDeviceNameRestriction";
    public static final String SERVER_URL_REPLACE = "ServerURLReplace";
    public static final String WMI_QUERY = "WmiQuery";
    public static final String WMI_INSTANCE_PROPERTIES_QUERY = "WmiInstancePropsQuery";
    public static final String SCHEDULE_OS_UPDATE = "ScheduleOSUpdate";
    public static final String ATTEMPT_OS_UPDATE = "AttemptOSUpdate";
    public static final String AVAILABLE_OS_UPDATES = "AvailableOSUpdates";
    public static final String OS_UPDATE_STATUS = "OSUpdateStatus";
    public static final String RESTRICT_OS_UPDATES = "RestrictOSUpdates";
    public static final String REMOVE_RESTRICT_OS_UPDATE = "RemoveRestrictOSUpdates";
    public static final String PENDING_OS_UPDATES = "PendingOSUpdates";
    public static final String OS_DOWNLOAD_FAILURE = "OsDownloadFailure";
    public static final String OS_DOWNLOAD_SUCCESS = "OsDownloadSuccess";
    public static final String OS_NO_STORAGE = "StorageUsability";
    public static final String SECURITY_PATCH_LEVEL_UPDATED = "SecurityPatchLevelUpdated";
    public static final String WIN_SIDELOAD_ENABLE = "EnableSideloadApps";
    public static final String WIN_SIDELOAD_DISABLE = "DisableSideloadApps";
    public static final String WIN_APPINSTALL_STATUS = "WinAppInstallStatusQuery";
    public static final String WIN_SIDELOAD_NOTCONFIGURED = "SideloadNotConfigured";
    public static final String WIN_SELECTIVE_WIPE = "WindowsSelectiveWipe";
    public static final String BLACKLIST_APP_IN_DEVICE = "BlacklistAppInDevice";
    public static final String BLACKLIST_APP_IN_CONTAINER = "BlacklistAppInContainer";
    public static final String REMOVE_BLACKLIST_APP_IN_DEVICE = "RemoveBlacklistAppInDevice";
    public static final String REMOVE_BLACKLIST_APP_IN_CONTAINER = "RemoveBlacklistAppInContainer";
    public static final String INSTALL_LEGACY_AGENT = "InstallLegacyAgent";
    public static final String MANAGE_APPLICATION = "ManageApplication";
    public static final String DATA_USAGE_SUMMARY = "DataUsageMessage";
    public static final String DETAILED_DATA_USAGE = "DetailedDataUsageMessage";
    public static final String INSTALL_DATA_PROFILE = "InstallDataProfile";
    public static final String REMOVE_DATA_PROFILE = "RemoveDataProfile";
    public static final String FILEVAULT_USER_LOGIN_SECURITY_INFO_UPDATE = "FileVaultUserLoginSecurityInfo";
    public static final String WINDOWS_NATIVE_APP_CONFIGURATION = "WindowsNativeAppConfig";
    public static final String WINDOWS_ENROLLMENT_TYPE_QUERY = "EnrollmentTypeQuery";
    public static final String WINDOWS_TRIGGER_ROBO = "TriggerROBO";
    public static final String APPLE_DEVICE_ATTESTATION = "AppleDeviceAttestation";
    public static final String REFRESH_TOKEN_UPDATE = "RefreshTokenUpdate";
    private static final List<String> REQUEST_TYPE_LIST;
    
    public static List<String> getSupportedRequestTypes() {
        return MDMRequestTypeConstants.REQUEST_TYPE_LIST;
    }
    
    static {
        REQUEST_TYPE_LIST = new ArrayList<String>() {
            {
                this.add("InstallProfile");
                this.add("RemoveProfile");
                this.add("ProfileList");
                this.add("ProvisioningProfileList");
                this.add("InstallProvisioningProfile");
                this.add("RemoveProvisioningProfile");
                this.add("CertificateList");
                this.add("InstalledApplicationList");
                this.add("DeviceConfigured");
                this.add("AccountConfiguration");
                this.add("ManagedApplicationList");
                this.add("DeviceInformation");
                this.add("SecurityInfo");
                this.add("DeviceLock");
                this.add("UnlockUserAccount");
                this.add("ClearPasscode");
                this.add("EraseDevice");
                this.add("CorporateWipe");
                this.add("DeviceRing");
                this.add("Restrictions");
                this.add("InstallApplication");
                this.add("RemoveApplication");
                this.add("ApplyRedemptionCode");
                this.add("RemoveDevice");
                this.add("InviteToProgram");
                this.add("EnableLostMode");
                this.add("DisableLostMode");
                this.add("DeviceLocation");
                this.add("ApplicationConfiguration");
                this.add("RemoveApplicationConfiguration");
                this.add("ManagedApplicationFeedback");
                this.add("PlayLostModeSound");
                this.add("RestartDevice");
                this.add("ShutDownDevice");
                this.add("ScheduleOSUpdate");
                this.add("AttemptOSUpdate");
                this.add("AvailableOSUpdates");
                this.add("OSUpdateStatus");
                this.add("OSUpdateStatus");
            }
        };
    }
    
    public static final class Mac
    {
        public static final class FirmwarePassword
        {
            public static final String REQUEST_TYPE_SET_FIRMWARE_PASSWORD = "SetFirmwarePassword";
            public static final String REQUEST_TYPE_VERIFY_FIRMWARE_PASSWORD = "VerifyFirmwarePassword";
            public static final String REQUEST_TYPE_KEY_CURRENT_PASSWORD = "CurrentPassword";
            public static final String REQUEST_TYPE_KEY_NEW_PASSWORD = "NewPassword";
            public static final String REQUEST_TYPE_KEY_PASSWORD = "Password";
            public static final String FIMRWARE_PRE_SECURITY_INFO = "MacFirmwarePreSecurityInfo";
            public static final String FIMRWARE_VERIFY_PASSWORD = "MacFirmwareVerifyPassword";
            public static final String FIMRWARE_SET_PASSWORD = "MacFirmwareSetPasscode";
            public static final String FIMRWARE_CLEAR_PASSWORD = "MacFirmwareClearPasscode";
            public static final String FIMRWARE_POST_SECURITY_INFO = "MacFirmwarePostSecurityInfo";
        }
        
        public static final class BootstrapToken
        {
            public static final String BOOTSTRAPTOKEN = "BootstrapToken";
        }
        
        public static final class PPPC
        {
            public static final String SERVICES = "Services";
            public static final String IDENTIFIER = "Identifier";
            public static final String IDENTIFIER_TYPE = "IdentifierType";
            public static final String CODE_REQUIREMENT = "CodeRequirement";
            public static final String STATIC_CODE = "StaticCode";
            public static final String ALLOWED = "Allowed";
            public static final String AUTHORIZATION = "Authorization";
            public static final String AE_RECEIVER_ID = "AEReceiverIdentifier";
            public static final String AE_RECEIVER_ID_TYPE = "AEReceiverIdentifierType";
            public static final String AE_CODE_REQUIREMENT = "AEReceiverCodeRequirement";
        }
        
        public static final class FilevaultRotate
        {
            public static final String REQUEST_TYPE = "RotateFileVaultKey";
            public static final String KEY_TYPE = "KeyType";
            public static final String FILEVAULT_UNLOCK = "FileVaultUnlock";
            
            public static final class FilevaultPersonalKeyRotate
            {
                public static final String COMMAND_NAME = "MacFileVaultPersonalKeyRotate";
                public static final String PASSWORD = "Password";
                public static final String KEY_TYPE_PERSONAL = "personal";
                public static final String REPLY_ENCRYPTION_CERTIFICATE = "ReplyEncryptionCertificate";
            }
        }
    }
    
    public static final class AgentResponseKeys
    {
        public static final class SecurityInfoResponse
        {
            public static final class Mac
            {
                public static final String AUTH_ROOT_VOL_ENABLED = "AuthenticatedRootVolumeEnabled";
                public static final String BOOTSTRAPTOKEN_ALLOWED_FOR_AUTH = "BootstrapTokenAllowedForAuthentication";
                public static final String BOOTSTRAPTOKEN_REQ_FOR_KERNAL_EXT_APPROVAL = "BootstrapTokenRequiredForKernelExtensionApproval";
                public static final String BOOTSTRAPTOKEN_REQ_FOR_SOFTWARE_UPDATE = "BootstrapTokenRequiredForSoftwareUpdate";
                public static final String NOT_SUPPORTED = "Not Supported";
                public static final String ALLOWED = "allowed";
                public static final String DIS_ALLOWED = "disallowed";
            }
            
            public static final class Firmware
            {
                public static final String FIRMWARE_PASSWORD_STATUS_DICT = "FirmwarePasswordStatus";
                public static final String FIRMWARE_PASSWORD_PASSWORD_EXISTS = "PasswordExists";
                public static final String FIRMWARE_PASSWORD_CHANGE_PENDING = "ChangePending";
                public static final String FIRMWARE_MODE = "Mode";
                public static final String FIRMWARE_MODE_COMMAND = "command";
                public static final String FIRMWARE_MODE_FULL = "full";
                public static final String FIRMWARE_PASSWORD_ALLOW_OROMS = "AllowOroms";
            }
            
            public static final class ManagementStatus
            {
                public static final String MANAGEMENT_STATUS = "ManagementStatus";
                public static final String MANAGEMENT_STATUS_ENROLLED_VIA_DEP = "EnrolledViaDEP";
                public static final String MANAGEMENT_STATUS_USER_APPROVED_MDM = "UserApprovedEnrollment";
                public static final String MANAGEMENT_STATUS_USER_ENROLLMENT = "IsUserEnrollment";
            }
        }
        
        public static final class GetBootstrapTokenResponse
        {
            public static final String GET_BOOTSTRAPTOKEN = "GetBootstrapToken";
        }
        
        public static final class SetBootstrapTokenResponse
        {
            public static final String SET_BOOTSTRAPTOKEN = "SetBootstrapToken";
        }
        
        public static final class SetFirmwarePasswordResponse
        {
            public static final String FIRMWARE_PASSWORD_CHANGED = "PasswordChanged";
        }
        
        public static final class VerifyFirmwarePasswordResponse
        {
            public static final String FIRMWARE_PASSWORD_VERIFY_STATUS = "PasswordVerified";
        }
        
        public static final class RotateFileVaultKey
        {
            public static final String ROTATE_RESULT = "RotateResult";
            public static final String ENCRYPTED_NEW_RECOVERY_KEY = "EncryptedNewRecoveryKey";
        }
    }
    
    public static final class MacKernelExtensionKeys
    {
        public static final String ALLOWUSEROVERRIDES = "AllowUserOverrides";
        public static final String ALLOWEDTEAMIDENTIFIERS = "AllowedTeamIdentifiers";
        public static final String ALLOWEDKERNELEXTENSIONS = "AllowedKernelExtensions";
        public static final String ALLOWEDSYSTEMEXTENSIONTYPES = "AllowedSystemExtensionTypes";
        public static final String ALLOWEDSYSTEMEXTENSIONS = "AllowedSystemExtensions";
    }
    
    public static final class MacSystemPreferenceKeys
    {
        public static final String DISABLEDPREFERENCEPANES = "DisabledPreferencePanes";
        public static final String ENABLEDPREFERENCEPANES = "EnabledPreferencePanes";
        public static final String HIDDENPREFERENCEPANES = "HiddenPreferencePanes";
    }
    
    public static final class AppleDeviceInfoQuery
    {
        public static final String DEVICE_NAME = "DeviceName";
        public static final String OS_VERSION = "OSVersion";
        public static final String BUILD_VERSION = "BuildVersion";
        public static final String MODEL_NAME = "ModelName";
        public static final String MODEL = "Model";
        public static final String PRODUCT_NAME = "ProductName";
        public static final String SERIAL_NUMBER = "SerialNumber";
        public static final String DEVICE_CAPACITY = "DeviceCapacity";
        public static final String AVAILABLE_DEVICE_CAPACITY = "AvailableDeviceCapacity";
        public static final String BATTERY_LEVEL = "BatteryLevel";
        public static final String CELLULAR_TECHNOLOGY = "CellularTechnology";
        public static final String IMEI = "IMEI";
        public static final String MEID = "MEID";
        public static final String MODEM_FIRMWARE_VERSION = "ModemFirmwareVersion";
        public static final String ICCID = "ICCID";
        public static final String BLUETOOTH_MAC = "BluetoothMAC";
        public static final String WIFI_MAC = "WiFiMAC";
        public static final String CURRENT_CARRIER_NETWORK = "CurrentCarrierNetwork";
        public static final String SIM_CARRIER_NETWORK = "SIMCarrierNetwork";
        public static final String SUBSCRIBER_CARRIER_NETWORK = "SubscriberCarrier-Network";
        public static final String CARRIER_SETTINGS_VERSION = "CarrierSettingsVersion";
        public static final String PHONE_NUMBER = "PhoneNumber";
        public static final String VOICE_ROAMING_ENABLED = "VoiceRoamingEnabled";
        public static final String DATA_ROAMING_ENABLED = "DataRoamingEnabled";
        public static final String IS_ROAMING = "IsRoaming";
        public static final String SUBSCRIBER_MCC = "SubscriberMCC";
        public static final String SUBSCRIBER_MNC = "SubscriberMNC";
        public static final String CURRENT_MCC = "CurrentMCC";
        public static final String CURRENT_MNC = "CurrentMNC";
        public static final String UDID = "UDID";
        public static final String IS_SUPERVISED = "IsSupervised";
        public static final String IS_MULTIUSER = "IsMultiUser";
        public static final String ACTIVE_MANAGED_USER = "ActiveManagedUsers";
        public static final String AUTO_SETUP_ADMIN_ACCOUNT = "AutoSetupAdminAccounts";
        public static final String ESTIMATED_RESIDENT_USER = "EstimatedResidentUsers";
        public static final String MAXIMUM_RESIDENT_USER = "MaximumResidentUsers";
        public static final String RESIDENT_USERS = "ResidentUsers";
        public static final String QUOTA_SIZE = "QuotaSize";
        public static final String IS_DEVICE_LOCATOR_SERVICE_ENABLED = "IsDeviceLocatorServiceEnabled";
        public static final String IS_ACTIVATION_LOCK_ENABLED = "IsActivationLockEnabled";
        public static final String IS_DO_NOT_DISTURB_IN_EFFECT = "IsDoNotDisturbInEffect";
        public static final String ITUNES_STORE_ACCOUNT_IS_ACTIVE = "iTunesStoreAccountIsActive";
        public static final String EAS_DEVICE_IDENTIFIER = "EASDeviceIdentifier";
        public static final String ETHERNET_MAC = "EthernetMAC";
        public static final String ETHERNET_MACS = "EthernetMACs";
        public static final String PERSONAL_HOTSPOT_ENABLED = "PersonalHotspotEnabled";
        public static final String LAST_CLOUD_BACKUP_DATE = "LastCloudBackupDate";
        public static final String IS_CLOUD_BACKUP_ENABLED = "IsCloudBackupEnabled";
        public static final String IS_MDM_LOST_MODE_ENABLED = "IsMDMLostModeEnabled";
        public static final String SERVICE_SUBSCRIPTIONS = "ServiceSubscriptions";
        public static final String LANGUAGES = "Languages";
        public static final String LOCALES = "Locales";
        public static final String DEVICE_ID = "DeviceID";
        public static final String ORGANIZATION_INFO = "OrganizationInfo";
        public static final String AWAITING_CONFIGURATION = "AwaitingConfiguration";
        public static final String MDM_OPTIONS = "MDMOptions";
        public static final String ITEM = "Item";
        public static final String ITUNES_STORE_ACCOUNT_HASH = "iTunesStoreAccountHash";
        public static final String SIMMCC = "SIMMCC";
        public static final String SIMMNC = "SIMMNC";
        public static final String OS_UPDATE_SETTINGS = "OSUpdateSettings";
        public static final String LOCAL_HOST_NAME = "LocalHostName";
        public static final String HOST_NAME = "HostName";
        public static final String CATALOG_URL = "CatalogURL";
        public static final String IS_DEFAULT_CATALOG = "IsDefaultCatalog";
        public static final String PREVIOUS_SCAN_DATE = "PreviousScanDate";
        public static final String PREVIOUS_SCAN_RESULT = "PreviousScanResult";
        public static final String PERFORM_PERIODIC_CHECK = "PerformPeriodicCheck";
        public static final String AUTOMATIC_CHECK_ENABLED = "AutomaticCheckEnabled";
        public static final String BACKGROUND_DOWNLOAD_ENABLED = "BackgroundDownloadEnabled";
        public static final String AUTOMATIC_APP_INSTALLATION_ENABLED = "AutomaticAppInstallationEnabled";
        public static final String AUTOMATIC_OS_INSTALLATION_ENABLED = "AutomaticOSInstallationEnabled";
        public static final String AUTOMATIC_SECURITY_UPDATES_ENABLED = "AutomaticSecurityUpdatesEnabled";
        public static final String IS_MULTI_USER = "IsMultiUser";
        public static final String MAXIMUM_RESIDENT_USERS = "MaximumResidentUsers";
        public static final String PUSH_TOKEN = "PushToken";
        public static final String DIAGNOSTIC_SUBMISSION_ENABLED = "DiagnosticSubmissionEnabled";
        public static final String APP_ANALYTICS_ENABLED = "AppAnalyticsEnabled";
        public static final String IS_NETWORK_TETHERED = "IsNetworkTethered";
        public static final String IS_APPLE_SILICON = "IsAppleSilicon";
        public static final String SUPPORTS_IOS_APP_INSTALLS = "SupportsiOSAppInstalls";
        public static final String IS_ACTIVATION_LOCK_SUPPORTED = "IsActivationLockSupported";
        public static final String ACCESSIBILITY_SETTINGS = "AccessibilitySettings";
        public static final String BOLD_TEXT_ENABLED = "BoldTextEnabled";
        public static final String INCREASE_CONTRAST_ENABLED = "IncreaseContrastEnabled";
        public static final String REDUCE_MOTION_ENABLED = "ReduceMotionEnabled";
        public static final String REDUCE_TRANSPARENCY_ENABLED = "ReduceTransparencyEnabled";
        public static final String TEXT_SIZE = "TextSize";
        public static final String TOUCH_ACCOMMODATIONS_ENABLED = "TouchAccommodationsEnabled";
        public static final String VOICE_OVER_ENABLED = "VoiceOverEnabled";
        public static final String ZOOM_ENABLED = "ZoomEnabled";
        
        public class DeviceInformation
        {
            public class ServiceSubscriptions
            {
                public static final String IS_DATA_PREFERRED = "IsDataPreferred";
                public static final String IS_VOICE_PREFERRED = "IsVoicePreferred";
                public static final String LABEL = "Label";
                public static final String LABEL_ID = "LabelID";
                public static final String SLOT = "Slot";
            }
            
            public class MdmOptions
            {
                public static final String BOOTSTRAPTOKEN_ALLOWED = "BootstrapTokenAllowed";
                public static final String PROMPT_USER_TO_ALLOW_BOOTSTRAPTOKEN_FOR_AUTHENTICATION = "PromptUserToAllowBootstrapTokenForAuthentication";
            }
            
            public class SharedDevice
            {
                public static final String ESTIMATED_USER = "EstimatedResidentUsers";
                public static final String RESIDENT_USER = "ResidentUsers";
                public static final String QUOTA_SIZE = "QuotaSize";
            }
        }
        
        public class DeviceAttestation
        {
            public static final String DEVICE_PROPERTIES_ATTESTATION = "DevicePropertiesAttestation";
            public static final String DEVICE_ATTESTATION_NONCE = "DeviceAttestationNonce";
        }
    }
    
    public static final class MacEnergySaverPolicy
    {
        public static final String DESTROY_FV_KEY = "DestroyFVKeyOnStandby";
        public static final String SLEEP_DISABLED = "SleepDisabled";
        public static final String PORTABLE_BATTERY_SETTINGS = "com.apple.EnergySaver.portable.BatteryPower";
        public static final String PORTABLE_ACPOWER_SETTINGS = "com.apple.EnergySaver.portable.ACPower";
        public static final String DESKTOP_SETTINGS = "com.apple.EnergySaver.desktop.ACPower";
        public static final String SCHEDULE = "com.apple.EnergySaver.desktop.Schedule";
        public static final String POWER_ON_SCHEDULE = "RepeatingPowerOn";
        public static final String POWER_OFF_SCHEDULE = "RepeatingPowerOff";
        
        public static final class EnergySaverSettings
        {
            public static final String AUTOMATIC_RESTART_ON_POWER_LOSS = "Automatic Restart On Power Loss";
            public static final String DISK_SLEEP_TIMER = "Disk Sleep Timer";
            public static final String DISPLAY_SLEEP_TIMER = "Display Sleep Timer";
            public static final String DYNAMIC_POWER_SETUP = "Dynamic Power Step";
            public static final String REDUCE_PROCESSOR_SPEED = "Reduce Processor Speed";
            public static final String SYSTEM_SLEEP_TIMER = "System Sleep Timer";
            public static final String WAKE_ON_LAN = "Wake On LAN";
            public static final String WAKE_ON_MODEM_RING = "Wake On Modem Ring";
        }
        
        public static final class ScheduleSettings
        {
            public static final String EVENT_TYPE = "eventtype";
            public static final String TIME = "time";
            public static final String WEEKDAYS = "weekdays";
        }
        
        public static final class EventNames
        {
            public static final String RESTART = "restart";
            public static final String POWERON = "poweron";
            public static final String WAKE = "wake";
            public static final String WAKE_POWERON = "wakepoweron";
            public static final String SLEEP = "sleep";
            public static final String SHUTDOWN = "shutdown";
        }
    }
    
    public static final class CertificateListProperties
    {
        public static final String MANAGED_ONLY = "ManagedOnly";
        public static final String REQUEST_REQ_NETWORK_TETHER = "RequestRequiresNetworkTether";
    }
    
    public static final class INSTALLED_APPLICATION_LIST_RES
    {
        public static final String NAME = "Name";
        public static final String IDENTIFIER = "Identifier";
        public static final String VERSION = "Version";
        public static final String SHORT_VERSION = "ShortVersion";
        public static final String DYNAMIC_SIZE = "DynamicSize";
        public static final String BUNDLE_SIZE = "BundleSize";
        public static final String EXTERNAL_VERSION_IDENTIFIER = "ExternalVersionIdentifier";
        public static final String HAS_UPDATE_AVAILABLE = "HasUpdateAvailable";
    }
}
