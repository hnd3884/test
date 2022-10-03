package com.adventnet.sym.server.mdm.samsung;

import com.me.mdm.server.android.knox.inventory.SamsungKnoxInventoryHandler;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.inv.AppDataHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import org.json.JSONArray;
import java.util.logging.Level;
import java.util.Map;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import java.util.ArrayList;
import com.me.mdm.server.privacy.PrivacySettingsHandler;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import java.util.logging.Logger;

public class SamsungInventory
{
    private static SamsungInventory samsungInventory;
    private Logger logger;
    private static final String SECURITY_DETAILS = "SecurityDetails";
    private static final String DEVICE_DETAILS = "DeviceDetails";
    private static final String NETWORK_DETAILS = "NetworkDetails";
    private static final String NETWORK_USAGE_DETAILS = "NetworkUsageDetails";
    private static final String SOFTWARE_DETAILS = "SoftwareDetails";
    private static final String CERTIFICATE_DETAILS = "CertificateDetails";
    private static final String RESTRICTION_DETAILS = "Restriction";
    private static final String DEVICE_RESTRICTION = "DeviceRestriction";
    private static final String ROAMING_RESTRICTION = "RoamingRestriction";
    private static final String BROWSER_RESTRICTION = "BrowserRestriction";
    private static final String APPLICATION_RESTRICTION = "ApplicationRestriction";
    private static final String PHONE_RESTRICTION = "PhoneRestriction";
    private static final String BLUETOOTH_RESTRICTION = "BluetoothRestriction";
    private static final String UDID = "UDID";
    private static final String AVAILABLE_EXTERNAL_CAPACITY = "AvailableExternalCapacity";
    private static final String MODEL = "Model";
    private static final String AVAILABLE_DEVICE_CAPACITY = "AvailableDeviceCapacity";
    private static final String DEVICE_PROCESSOR_TYPE = "DeviceProcessorType";
    private static final String DEVICE_CAPACITY = "DeviceCapacity";
    private static final String OSVERSION = "OSVersion";
    private static final String EXTERNAL_CAPACITY = "ExternalCapacity";
    private static final String MODEL_TYPE = "ModelType";
    private static final String TOTAL_RAM_MEMORY = "TotalRAMMemory";
    private static final String BUILD_VERSION = "BuildVersion";
    private static final String OSNAME = "OSName";
    private static final String AVAILABLE_RAM_MEMORY = "AvailableRAMMemory";
    private static final String USED_DEVICE_SAPCE = "UsedDeviceSpace";
    private static final String SERIAL_NUMBER = "SerialNumber";
    private static final String MODEM_FIRMWARE = "Modem_FirmWare";
    private static final String IMEI = "IMEI";
    private static final String MODEL_NAME = "ModelName";
    private static final String CELLULAR_TECHNOLOGY = "CellularTechnology";
    private static final String PRODUCT_NAME = "ProductName";
    private static final String DEVICE_PROCESSOR_SPEED = "DeviceProcessorSpeed";
    private static final String USED_EXTERNAL_SPACE = "UsedExternalSpace";
    private static final String BATTERY_LEVEL = "Battery_Level";
    private static final String DEVICE_ADMIN_ENABLE = "deviceAdministratorEnabled";
    private static final String DISABLE_CELLULAR_DATA = "disableCellularData";
    private static final String DISABLE_GPS = "disableGPS";
    private static final String DISABLE_GPS_STATE_CHANGE = "setGPSStateChangeAllowed";
    private static final String IS_DEVICEOWNER = "IsDeviceOwner";
    private static final String IS_PROFILEOWNER = "IsProfileOwner";
    private static final String SUBSCRIBER_MNC = "SubscriberMNC";
    private static final String CURRENT_CARRIER_NETWORK = "CurrentCarrierNetwork";
    private static final String SUBSCRIBER_CARRIER_NETWORK = "SubscriberCarrierNetwork";
    private static final String WIFI_MAC = "WiFiMAC";
    private static final String CURRENT_MCC = "CurrentMCC";
    private static final String PHONE_NUMBER = "PhoneNumber";
    private static final String CURRENT_MNC = "CurrentMNC";
    private static final String BLUETOOTH_MAC = "BluetoothMAC";
    private static final String ICCID = "ICCID";
    private static final String SUBSCRIBER_MCC = "SubscriberMCC";
    private static final String VOICE_ROAMING_ENABLED = "VoiceRoamingEnabled";
    private static final String DATA_ROAMING_ENABLED = "DataRoamingEnabled";
    private static final String IS_ROAMING = "IsRoaming";
    private static final String INCOMING_NETWORK_USAGE = "IncomingNetworkUsage";
    private static final String OUT_GOING_NETWORK_USAGE = "OutGoingNetworkUsage";
    private static final String INCOMING_WIFI_USAGE = "IncomingWiFiUsage";
    private static final String OUT_GOING_WIFI_USAGE = "OutGoingWiFiUsage";
    private static final String STORAGE_ENCRYPTION = "StorageEncryption";
    private static final String EXTERNAL_STORAGE_ENCRYPTION = "ExternalStorageEncryption";
    private static final String PASSCODE_COMPLIANT_WITH_PROFILE = "PasscodeCompliantWithProfiles";
    private static final String DEVICE_ROOTED = "DeviceRooted";
    private static final String PASSCODE_ENABLED = "PasscodePresent";
    private static final String CERTIFICATE_NAME = "CommonName";
    private static final String CERTIFICATE_TYPE = "CertificateType";
    private static final String CERTIFICATE_VERSION = "CertificateVersion";
    private static final String CERTIFICATE_SERIAL_NUMBER = "CertificateSerialNumber";
    private static final String CERTIFICATE_SIGNATURE_ALGORITHM_OID = "SingnatureAlgorithmOID";
    private static final String CERTIFICATE_SIGNATURE_ALGORITHM_NAME = "SignatureAlgorithmName";
    private static final String CERTIFICATE_SIGNATURE = "CertificateSignature";
    private static final String CERTIFICATE_EXPIRE = "CertificateExpire";
    private static final String CERTIFICATE_ISSUER_DN = "CertificateIssuerDN";
    private static final String CERTIFICATE_SUBJECT_DN = "CertificateSubjectDN";
    private static final String CERTIFICATE_IS_IDENTITY = "IsIdentity";
    private static final String CERTIFICATE_CONTENT = "CertificateContent";
    private static final String ALLOW_ANDROID_MARKET = "allowAndroidMarket";
    private static final String ALLOW_NON_MARKET_APPS = "allowNonMarketApps";
    private static final String ALLOW_ROAMING_DATA = "allowRoamingData";
    private static final String ALLOW_ROAMING_PUSH = "allowRoamingPush";
    private static final String ALLOW_ROAMING_SYNC = "allowRoamingSync";
    private static final String ALLOW_ANDROID_BROWSER = "allowAndroidBrowser";
    private static final String ALLOW_VOICE_DIALER = "allowVoiceDialer";
    private static final String ALLOW_YOU_TUBE = "allowYouTube";
    private static final String ALLOW_INSTALL_APP = "allowInstallApp";
    private static final String ALLOW_BLUETOOTH = "allowBluetooth";
    private static final String ALLOW_BLUETOOTH_TETHERING = "allowBluetoothTethering";
    private static final String ALLOW_TETHERING = "allowTethering";
    private static final String ALLOW_FACTORY_RESET = "allowFactoryReset";
    private static final String ALLOW_SETTINGS = "allowSettings";
    private static final String ALLOW_WIFI = "allowWiFi";
    private static final String ALLOW_WIFI_TETHERING = "allowWiFiTethering";
    private static final String ALLOW_BACKGROUND_DATA = "disableBackgroundData";
    private static final String ALLOW_GOOGLE_BACKUP = "allowGoogleBackup";
    private static final String ALLOW_CAMERA = "allowCamera";
    private static final String ALLOW_CELLULAR_DATA = "allowCellularData";
    private static final String ALLOW_CLIPBOARD = "allowClipboard";
    private static final String ALLOW_NFC = "allowNFC";
    private static final String ALLOW_NFC_STATE_CHANGE = "setNFCStateChangeAllowed";
    private static final String ALLOW_MICROPHONE = "allowMicroPhone";
    private static final String ALLOW_MOCK_LOCATION = "allowMockLocation";
    private static final String ALLOW_SCREEN_CAPTURE = "allowScreenCapture";
    private static final String ALLOW_SD_CARD = "allowSDCard";
    private static final String ALLOW_USB_DEBUG = "allowUSBDebug";
    private static final String ALLOW_USB = "allowUSB";
    private static final String ALLOW_USB_TETHERING = "allowUSBTethering";
    private static final String ALLOW_USB_MEDIA_PLAYER = "allowUSBMediaPlayer";
    private static final String BROWSER_ALLOW_AUTOFILL = "browserAllowAutoFill";
    private static final String BROWSER_ALLOW_COOKIES = "browserAllowCookies";
    private static final String BROWSER_ALLOW_JAVASCRIPT = "browserAllowJavaScript";
    private static final String BROWSER_ALLOW_POPUPS = "browserAllowPopups";
    private static final String BROWSER_ALLOW_FRAUD_WARNING = "browserAllowFraudWarning";
    private static final String ALLOW_UNINSTALL_APP = "allowUnInstallApp";
    private static final String ALLOW_VPN = "allowVPN";
    private static final String ALLOW_GOOGLE_CRASH_REPORT = "allowGoogleCrashReport";
    private static final String ALLOW_OTA_UPGRADE = "allowOTAUpgrade";
    private static final String ALLOW_POWER_OFF = "allowPowerOff";
    private static final String ALLOW_SD_CARD_WRITE = "allowSDCardWrite";
    private static final String ALLOW_STATUSBAR_EXPANSION = "allowStatusBarExpansion";
    private static final String ALLOW_WALLPAPER_CHANGE = "allowWallpaperChange";
    private static final String ALLOW_ROAMING_VOICE_CALLS = "allowRoamingVoiceCall";
    private static final String ALLOW_CONTACTS_OUTSIDE = "allowContactsOutside";
    private static final String ALLOW_OTHER_KEYPADS = "allowOtherKeypads";
    private static final String ALLOW_KNOX_APP_STORE = "allowKnoxAppStore";
    private static final String ALLOW_SHARE_VIA_LIST = "allowShareViaList";
    private static final String ALLOW_S_VOICE = "allowSVoice";
    private static final String ALLOW_STOP_SYSTEM_APP = "allowStopSystemApp";
    private static final String ALLOW_ACTIVATION_LOCK = "allowActivationLock";
    private static final String ALLOW_AIRPLANE_MODE = "allowAirplaneMode";
    private static final String ALLOW_ANDROID_BEAM = "allowAndroidBeam";
    private static final String ALLOW_BACKGROUND_PROCESS_LIMIT = "allowBackgroundProcessLimit";
    private static final String ALLOW_S_BEAM = "allowSBeam";
    private static final String ALLOW_WIFI_DIRECT = "allowWifiDirect";
    private static final String ALLOW_SMART_CLIP_MODE = "allowSmartClipMode";
    private static final String ALLOW_CLIPBOARD_SHARE = "allowClipboardShare";
    private static final String ALLOW_FIRMWARE_RECOVERY = "allowFirmwareRecovery";
    private static final String ALLOW_SDCARD_MOVE = "allowSDCardMove";
    private static final String ALLOW_SAFE_MODE = "allowSafeMode";
    private static final String ALLOW_HOME_KEY = "setHomeKeyState";
    private static final String ALLOW_DEVELOPER_MODE = "allowDeveloperMode";
    private static final String ALLOW_KILL_ACTIVITY_ON_LEAVE = "allowKillingActivitiesOnLeave";
    private static final String ALLOW_LOCK_SCREEN_VIEW = "setLockScreenState";
    private static final String ALLOW_USER_MOBILE_DATA_LIMIT = "allowUserMobileDataLimit";
    private static final String ALLOW_DATE_TIME_CHANGE = "setDateTimeChangeEnabled";
    private static final String ALLOW_GOOGLE_ACCOUNT_AUTO_SYNC = "allowGoogleAccountsAutoSync";
    private static final String ALLOW_USER_PROFILE = "setAllowUserProfiles";
    private static final String ALLOW_APP_NOTIFICATION_MODE = "setApplicationNotificationMode";
    private static final String ALLOW_AUDIO_RECORD = "allowAudioRecord";
    private static final String ALLOW_VIDEO_RECORD = "allowVideoRecord";
    private static final String ALLOW_USB_HOST_STORAGE = "allowUsbHostStorage";
    private static final String ALLOW_USE_NETWORK_TIME = "setAutomaticTime";
    private static final String ALLOW_GMAIL = "allowGoogleMail";
    private static final String ALLOW_GOOGLE_MAPS = "allowGoogleMaps";
    private static final String ALLOW_BT_DISCOVERABLE = "setDiscoverableState";
    private static final String ALLOW_BT_PAIRING = "setPairingState";
    private static final String ALLOW_BT_OUTGOING_CALLS = "allowOutgoingCallsViaBluetooth";
    private static final String ALLOW_BT_PC_CONNECTION = "setDesktopConnectivityState";
    private static final String ALLOW_BT_DATA_TRANSFER = "setAllowBluetoothDataTransfer";
    private static final String ALLOW_INCOMING_MMS = "allowIncomingMms";
    private static final String ALLOW_OUTGOING_MMS = "allowOutgoingMms";
    private static final String ALLOW_INCOMING_SMS = "allowIncomingSms";
    private static final String ALLOW_OUTGOING_SMS = "allowOutgoingSms";
    private static final String ALLOW_INCOMING_CALL = "allowIncomingCall";
    private static final String ALLOW_OUTGOING_CALL = "allowOutgoingCall";
    private static final String ALLOW_WIFI_STATE_CHANGE = "setWifiStateChangeAllowed";
    private static final String ALLOW_USER_CREATION = "allowUserCreation";
    private static final String ALLOW_USER_ADD_ACCOUNTS = "allowUserAddAccounts";
    private static final String ALLOW_AIR_COMMAND = "allowAirCommand";
    private static final String ALLOW_AIR_VIEW = "allowAirView";
    private static final String ALLOW_S_FINDER = "allowSFinder";
    protected boolean isDataPopulationSuccess;
    private static final String POLICY_INFO = "PolicyInfo";
    private static final String SECURITY_INFO = "SecurityInfo";
    private JSONUtil jsonUtil;
    
    protected SamsungInventory() {
        this.logger = null;
        this.isDataPopulationSuccess = true;
        this.jsonUtil = JSONUtil.getInstance();
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void parseInventoryData(final Long resourceID, final String data) {
        try {
            this.isDataPopulationSuccess = true;
            final Map<String, String> parsedData = JSONUtil.getInstance().ConvertJSONObjectToHash(new JSONObject(data));
            final String respondData = parsedData.get("ResponseData");
            this.jsonUtil = JSONUtil.getInstance();
            final JSONObject inventoryData = new JSONObject(respondData);
            final HashMap<String, String> deviceInfo = this.isolateDeviceInfo(inventoryData);
            final JSONObject privacyJson = new PrivacySettingsHandler().getPrivacySettingsJSON(resourceID);
            final boolean fetchMac = privacyJson.getInt("fetch_mac_address") != 2;
            final HashMap<String, String> networkInfo = this.isolateNetworkInfo(inventoryData, fetchMac);
            final HashMap<String, String> simInfo = this.isolateSimInfo(inventoryData);
            final ArrayList simArrayList = new ArrayList();
            simArrayList.add(simInfo);
            final HashMap<String, String> networkUsageInfo = this.isolateNetworkUsageInfo(resourceID, inventoryData);
            final HashMap<String, String> securityInfo = this.isolateSecurityInfo(inventoryData);
            final HashMap<String, String> restrictionInfo = this.isolateRestrictionInfo(inventoryData);
            final MDMInvDataPopulator invDataPopulator = MDMInvDataPopulator.getInstance();
            invDataPopulator.addOrUpdateDeviceInfo(resourceID, deviceInfo);
            invDataPopulator.addOrUpdateNetworkInfo(resourceID, networkInfo);
            invDataPopulator.addOrUpdateSimInfo(resourceID, simArrayList);
            invDataPopulator.addOrUpdateNetworkUsageInfo(resourceID, networkUsageInfo);
            invDataPopulator.addOrUpdateIOSSecurityInfo(resourceID, securityInfo);
            invDataPopulator.addOrUpdateSamsungRestriction(resourceID, restrictionInfo);
            this.processCertificates(resourceID, inventoryData);
            this.processApps(resourceID, inventoryData);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, ex, () -> "Exception raised on populating SAFE inventory data Resource ID " + n);
            this.isDataPopulationSuccess = false;
        }
    }
    
    protected HashMap<String, String> isolateDeviceInfo(final JSONObject inventoryData) {
        final HashMap<String, String> deviceInfo = new HashMap<String, String>();
        try {
            final JSONObject deviceInfoDetails = new JSONObject(String.valueOf(inventoryData.get("DeviceDetails")));
            deviceInfo.put("OS_VERSION", deviceInfoDetails.optString("OSVersion", (String)null));
            deviceInfo.put("BUILD_VERSION", deviceInfoDetails.optString("BuildVersion", "--"));
            deviceInfo.put("SERIAL_NUMBER", deviceInfoDetails.optString("SerialNumber", (String)null));
            deviceInfo.put("MODEL_ID", deviceInfoDetails.optString("UDID", "-1"));
            deviceInfo.put("BATTERY_LEVEL", deviceInfoDetails.optString("Battery_Level", "-1"));
            deviceInfo.put("CELLULAR_TECHNOLOGY", deviceInfoDetails.optString("CellularTechnology", "0"));
            deviceInfo.put("IMEI", deviceInfoDetails.optString("IMEI", (String)null));
            deviceInfo.put("MEID", deviceInfoDetails.optString("Model", (String)null));
            deviceInfo.put("MODEM_FIRMWARE_VERSION", deviceInfoDetails.optString("Modem_FirmWare", (String)null));
            deviceInfo.put("DEVICE_CAPACITY", deviceInfoDetails.optString("DeviceCapacity", "0.0"));
            deviceInfo.put("AVAILABLE_DEVICE_CAPACITY", deviceInfoDetails.optString("AvailableDeviceCapacity", "0.0"));
            deviceInfo.put("USED_DEVICE_SPACE", deviceInfoDetails.optString("UsedDeviceSpace", "0.0"));
            deviceInfo.put("PROCESSOR_NAME", inventoryData.optString("ChipSet", (String)null));
            deviceInfo.put("PROCESSOR_CORE_COUNT", inventoryData.optString("ProcessorCount", "0"));
            deviceInfo.put("EXTERNAL_CAPACITY", deviceInfoDetails.optString("ExternalCapacity", "0.0"));
            deviceInfo.put("USED_EXTERNAL_SPACE", deviceInfoDetails.optString("UsedExternalSpace", "0.0"));
            deviceInfo.put("AVAILABLE_EXTERNAL_CAPACITY", deviceInfoDetails.optString("AvailableExternalCapacity", "0.0"));
            deviceInfo.put("OS_NAME", deviceInfoDetails.optString("OSName", (String)null));
            deviceInfo.put("PROCESSOR_SPEED", deviceInfoDetails.optString("DeviceProcessorSpeed", (String)null));
            deviceInfo.put("PROCESSOR_TYPE", deviceInfoDetails.optString("DeviceProcessorType", (String)null));
            deviceInfo.put("AVAILABLE_RAM_MEMORY", deviceInfoDetails.optString("AvailableRAMMemory", "0.0"));
            deviceInfo.put("TOTAL_RAM_MEMORY", deviceInfoDetails.optString("TotalRAMMemory", "0.0"));
            deviceInfo.put("IS_SUPERVISED", deviceInfoDetails.optString("IsDeviceOwner", "0"));
            deviceInfo.put("IS_PROFILEOWNER", deviceInfoDetails.optString("IsProfileOwner", "0"));
            deviceInfo.put("MODEL_NAME", deviceInfoDetails.optString("ModelName", (String)null));
            deviceInfo.put("PRODUCT_NAME", deviceInfoDetails.optString("ProductName", "--"));
            deviceInfo.put("MODEL", deviceInfoDetails.optString("Model", (String)null));
            deviceInfo.put("MODEL_TYPE", deviceInfoDetails.optString("ModelType", "0"));
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception occurred on isolating device inforamation form response data.", ex);
            this.isDataPopulationSuccess = false;
        }
        return deviceInfo;
    }
    
    protected HashMap<String, String> isolateNetworkInfo(final JSONObject inventoryData, final boolean fetchMac) {
        final HashMap<String, String> networkInfo = new HashMap<String, String>();
        try {
            final JSONObject networkDetails = new JSONObject(String.valueOf(inventoryData.get("NetworkDetails")));
            if (fetchMac) {
                networkInfo.put("BLUETOOTH_MAC", networkDetails.optString("BluetoothMAC", (String)null));
                networkInfo.put("WIFI_MAC", networkDetails.optString("WiFiMAC", (String)null));
            }
            networkInfo.put("VOICE_ROAMING_ENABLED", networkDetails.optString("VoiceRoamingEnabled", "true"));
            networkInfo.put("DATA_ROAMING_ENABLED", networkDetails.optString("DataRoamingEnabled", "true"));
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception Occurred on isolating network information form response data.", ex);
            this.isDataPopulationSuccess = false;
        }
        return networkInfo;
    }
    
    private HashMap<String, String> isolateSimInfo(final JSONObject inventoryData) {
        final HashMap<String, String> simInfo = new HashMap<String, String>();
        try {
            final JSONObject simDetails = new JSONObject(String.valueOf(inventoryData.get("NetworkDetails")));
            final JSONObject deviceInfoDetails = new JSONObject(String.valueOf(inventoryData.get("DeviceDetails")));
            simInfo.put("IMEI", deviceInfoDetails.optString("IMEI", (String)null));
            simInfo.put("ICCID", simDetails.optString("ICCID", (String)null));
            simInfo.put("CURRENT_CARRIER_NETWORK", simDetails.optString("CurrentCarrierNetwork", (String)null));
            simInfo.put("SUBSCRIBER_CARRIER_NETWORK", simDetails.optString("SubscriberCarrierNetwork", (String)null));
            simInfo.put("PHONE_NUMBER", simDetails.optString("PhoneNumber", (String)null));
            simInfo.put("IS_ROAMING", simDetails.optString("IsRoaming", "true"));
            simInfo.put("SUBSCRIBER_MCC", simDetails.optString("SubscriberMCC", (String)null));
            simInfo.put("SUBSCRIBER_MNC", simDetails.optString("SubscriberMNC", (String)null));
            simInfo.put("CURRENT_MCC", simDetails.optString("CurrentMCC", (String)null));
            simInfo.put("CURRENT_MNC", simDetails.optString("CurrentMNC", (String)null));
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception Occurred on isolating sim information form response data.", ex);
            this.isDataPopulationSuccess = false;
        }
        return simInfo;
    }
    
    protected HashMap<String, String> isolateNetworkUsageInfo(final Long resourceID, final JSONObject inventoryData) {
        final HashMap<String, String> networkUsageInfo = new HashMap<String, String>();
        try {
            final JSONObject networkUsageDetails = new JSONObject(String.valueOf(inventoryData.get("NetworkUsageDetails")));
            networkUsageInfo.put("RESOURCE_ID", resourceID + "");
            networkUsageInfo.put("INCOMING_NETWORK_USAGE", networkUsageDetails.optString("IncomingNetworkUsage", "0.0"));
            networkUsageInfo.put("OUTGOING_NETWORK_USAGE", networkUsageDetails.optString("OutGoingNetworkUsage", "0.0"));
            networkUsageInfo.put("INCOMING_WIFI_USAGE", networkUsageDetails.optString("IncomingWiFiUsage", "0.0"));
            networkUsageInfo.put("OUTGOING_WIFI_USAGE", networkUsageDetails.optString("OutGoingWiFiUsage", "0.0"));
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception Occurred on isolating network usage information from response data.", ex);
            this.isDataPopulationSuccess = false;
        }
        return networkUsageInfo;
    }
    
    protected HashMap<String, String> isolateSecurityInfo(final JSONObject inventoryData) {
        final HashMap<String, String> securityInfo = new HashMap<String, String>();
        try {
            final JSONObject securityDetails = new JSONObject(String.valueOf(inventoryData.get("SecurityDetails")));
            securityInfo.put("StorageEncryption", securityDetails.optString("StorageEncryption", "false"));
            securityInfo.put("ExternalStorageEncryption", securityDetails.optString("ExternalStorageEncryption", "-1"));
            securityInfo.put("PasscodeCompliantWithProfiles", securityDetails.optString("PasscodeCompliantWithProfiles", "false"));
            securityInfo.put("DeviceRooted", securityDetails.optString("DeviceRooted", "false"));
            securityInfo.put("PasscodePresent", securityDetails.optString("PasscodePresent", "false"));
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception Occurred on isolating security information from response data", ex);
            this.isDataPopulationSuccess = false;
        }
        return securityInfo;
    }
    
    protected HashMap<String, String> isolateRestrictionInfo(final JSONObject inventoryData) {
        final HashMap<String, String> restrictionInfo = new HashMap<String, String>();
        try {
            final JSONObject restrictionDetails = new JSONObject(String.valueOf(inventoryData.get("Restriction")));
            final JSONObject deviceRestriction = new JSONObject(restrictionDetails.optString("DeviceRestriction"));
            final JSONObject roamingRestriction = new JSONObject(restrictionDetails.optString("RoamingRestriction", "{}"));
            final JSONObject browserRestriciton = new JSONObject(restrictionDetails.optString("BrowserRestriction", "{}"));
            final JSONObject applictionRestriction = new JSONObject(restrictionDetails.optString("ApplicationRestriction", "{}"));
            final JSONObject phoneRestriction = new JSONObject(restrictionDetails.optString("PhoneRestriction", "{}"));
            final JSONObject bluetoothRestriction = new JSONObject(restrictionDetails.optString("BluetoothRestriction", "{}"));
            this.logger.info("ALLOW_CLIPBOARD");
            this.logger.info(deviceRestriction.toString());
            this.logger.info("allowClipboard");
            this.logger.info(deviceRestriction.optString("allowClipboard", "-1"));
            restrictionInfo.put("ALLOW_CLIPBOARD", deviceRestriction.optString("allowClipboard", "-1"));
            restrictionInfo.put("ALLOW_GOOGLE_CRASH_REPORT", deviceRestriction.optString("allowGoogleCrashReport", "-1"));
            restrictionInfo.put("ALLOW_CELLULAR_DATA", deviceRestriction.optString("allowCellularData", "-1"));
            restrictionInfo.put("ALLOW_DISABLING_CELLULAR_DATA", deviceRestriction.optString("disableCellularData", "-1"));
            final int gpsStateChange = Integer.valueOf(deviceRestriction.optString("setGPSStateChangeAllowed", "-1"));
            final int disableGPS = Integer.valueOf(deviceRestriction.optString("disableGPS", "-1"));
            if (disableGPS < gpsStateChange) {
                restrictionInfo.put("ALLOW_DISABLING_GPS", deviceRestriction.optString("setGPSStateChangeAllowed", "-1"));
            }
            else {
                restrictionInfo.put("ALLOW_DISABLING_GPS", deviceRestriction.optString("disableGPS", "-1"));
            }
            restrictionInfo.put("ALLOW_BLUETOOTH_TETHERING", deviceRestriction.optString("allowBluetoothTethering", "-1"));
            restrictionInfo.put("ALLOW_MOCK_LOCATION", deviceRestriction.optString("allowMockLocation", "-1"));
            restrictionInfo.put("ALLOW_MICROPHONE", deviceRestriction.optString("allowMicroPhone", "-1"));
            final int wifiStateChange = Integer.valueOf(deviceRestriction.optString("setWifiStateChangeAllowed", "-1"));
            final int wifi = Integer.valueOf(deviceRestriction.optString("allowWiFi", "-1"));
            if (wifi < wifiStateChange) {
                restrictionInfo.put("ALLOW_WIFI", deviceRestriction.optString("setWifiStateChangeAllowed", "-1"));
            }
            else {
                restrictionInfo.put("ALLOW_WIFI", deviceRestriction.optString("allowWiFi", "-1"));
            }
            restrictionInfo.put("ALLOW_SD_CARD_WRITE", deviceRestriction.optString("allowSDCardWrite", "-1"));
            restrictionInfo.put("ALLOW_BLUETOOTH", deviceRestriction.optString("allowBluetooth", "-1"));
            restrictionInfo.put("ALLOW_BACKGROUND_DATA", deviceRestriction.optString("disableBackgroundData", "-1"));
            restrictionInfo.put("ALLOW_POWER_OFF", deviceRestriction.optString("allowPowerOff", "-1"));
            restrictionInfo.put("ALLOW_FACTORY_RESET", deviceRestriction.optString("allowFactoryReset", "-1"));
            restrictionInfo.put("ALLOW_SD_CARD", deviceRestriction.optString("allowSDCard", "-1"));
            final int nfcStateChange = Integer.valueOf(deviceRestriction.optString("setNFCStateChangeAllowed", "-1"));
            final int nfc = Integer.valueOf(deviceRestriction.optString("allowNFC", "-1"));
            if (nfc < nfcStateChange) {
                restrictionInfo.put("ALLOW_NFC", deviceRestriction.optString("setNFCStateChangeAllowed", "-1"));
            }
            else {
                restrictionInfo.put("ALLOW_NFC", deviceRestriction.optString("allowNFC", "-1"));
            }
            restrictionInfo.put("ALLOW_SETTINGS", deviceRestriction.optString("allowSettings", "-1"));
            restrictionInfo.put("ALLOW_TETHERING", deviceRestriction.optString("allowTethering", "-1"));
            restrictionInfo.put("ALLOW_WALLPAPER_CHANGE", deviceRestriction.optString("allowWallpaperChange", "-1"));
            restrictionInfo.put("ALLOW_USB", deviceRestriction.optString("allowUSB", "-1"));
            restrictionInfo.put("ALLOW_USB_TETHERING", deviceRestriction.optString("allowUSBTethering", "-1"));
            restrictionInfo.put("ALLOW_USB_DEBUG", deviceRestriction.optString("allowUSBDebug", "-1"));
            restrictionInfo.put("ALLOW_GOOGLE_BACKUP", deviceRestriction.optString("allowGoogleBackup", "-1"));
            restrictionInfo.put("ALLOW_WIFI_TETHERING", deviceRestriction.optString("allowWiFiTethering", "-1"));
            restrictionInfo.put("ALLOW_USB_MEDIA_PLAYER", deviceRestriction.optString("allowUSBMediaPlayer", "-1"));
            restrictionInfo.put("ALLOW_STATUSBAR_EXPANSION", deviceRestriction.optString("allowStatusBarExpansion", "-1"));
            restrictionInfo.put("ALLOW_OTA_UPGRADE", deviceRestriction.optString("allowOTAUpgrade", "-1"));
            restrictionInfo.put("ALLOW_VPN", deviceRestriction.optString("allowVPN", "-1"));
            restrictionInfo.put("ALLOW_SCREEN_CAPTURE", deviceRestriction.optString("allowScreenCapture", "-1"));
            restrictionInfo.put("ALLOW_CAMERA", deviceRestriction.optString("allowCamera", "-1"));
            restrictionInfo.put("DEVICE_ADMIN_ENABLED", deviceRestriction.optString("deviceAdministratorEnabled", "-1"));
            restrictionInfo.put("ALLOW_ACTIVATION_LOCK", deviceRestriction.optString("allowActivationLock", "-1"));
            restrictionInfo.put("ALLOW_AIRPLANE_MODE", deviceRestriction.optString("allowAirplaneMode", "-1"));
            restrictionInfo.put("ALLOW_ANDROID_BEAM", deviceRestriction.optString("allowAndroidBeam", "-1"));
            restrictionInfo.put("ALLOW_BACKGROUND_PROCESS_LIMIT", deviceRestriction.optString("allowBackgroundProcessLimit", "-1"));
            restrictionInfo.put("ALLOW_S_BEAM", deviceRestriction.optString("allowSBeam", "-1"));
            restrictionInfo.put("ALLOW_WIFI_DIRECT", deviceRestriction.optString("allowWifiDirect", "-1"));
            restrictionInfo.put("ALLOW_SMART_CLIP_MODE", deviceRestriction.optString("allowSmartClipMode", "-1"));
            restrictionInfo.put("ALLOW_CLIPBOARD_SHARE", deviceRestriction.optString("allowClipboardShare", "-1"));
            restrictionInfo.put("ALLOW_FIRMWARE_RECOVERY", deviceRestriction.optString("allowFirmwareRecovery", "-1"));
            restrictionInfo.put("ALLOW_SDCARD_MOVE", deviceRestriction.optString("allowSDCardMove", "-1"));
            restrictionInfo.put("ALLOW_SAFE_MODE", deviceRestriction.optString("allowSafeMode", "-1"));
            restrictionInfo.put("ALLOW_HOME_KEY", deviceRestriction.optString("setHomeKeyState", "-1"));
            restrictionInfo.put("ALLOW_LOCK_SCREEN_VIEW", deviceRestriction.optString("setLockScreenState", "-1"));
            restrictionInfo.put("ALLOW_DEVELOPER_MODE", deviceRestriction.optString("allowDeveloperMode", "-1"));
            restrictionInfo.put("ALLOW_KILL_ACTIVITY_ON_LEAVE", deviceRestriction.optString("allowKillingActivitiesOnLeave", "-1"));
            restrictionInfo.put("ALLOW_USER_MOBILE_DATA_LIMIT", deviceRestriction.optString("allowUserMobileDataLimit", "-1"));
            restrictionInfo.put("ALLOW_DATE_TIME_CHANGE", deviceRestriction.optString("setDateTimeChangeEnabled", "-1"));
            restrictionInfo.put("ALLOW_GOOGLE_ACCOUNT_AUTO_SYNC", deviceRestriction.optString("allowGoogleAccountsAutoSync", "-1"));
            restrictionInfo.put("ALLOW_USER_PROFILE", deviceRestriction.optString("setAllowUserProfiles", "-1"));
            restrictionInfo.put("ALLOW_AUDIO_RECORD", deviceRestriction.optString("allowAudioRecord", "-1"));
            restrictionInfo.put("ALLOW_VIDEO_RECORD", deviceRestriction.optString("allowVideoRecord", "-1"));
            restrictionInfo.put("ALLOW_USB_HOST_STORAGE", deviceRestriction.optString("allowUsbHostStorage", "-1"));
            restrictionInfo.put("ALLOW_USE_NETWORK_TIME", deviceRestriction.optString("setAutomaticTime", "-1"));
            restrictionInfo.put("ALLOW_USER_ADD_ACCOUNTS", deviceRestriction.optString("allowUserAddAccounts", "-1"));
            restrictionInfo.put("ALLOW_AIR_COMMAND", deviceRestriction.optString("allowAirCommand", "-1"));
            restrictionInfo.put("ALLOW_AIR_VIEW", deviceRestriction.optString("allowAirView", "-1"));
            restrictionInfo.put("ALLOW_USER_CREATION", deviceRestriction.optString("allowUserCreation", "-1"));
            restrictionInfo.put("ALLOW_ROAMING_SYNC", roamingRestriction.optString("allowRoamingSync", "-1"));
            restrictionInfo.put("ALLOW_ROAMING_VOICE_CALLS", roamingRestriction.optString("allowRoamingVoiceCall", "-1"));
            restrictionInfo.put("ALLOW_ROAMING_PUSH", roamingRestriction.optString("allowRoamingPush", "-1"));
            restrictionInfo.put("ALLOW_ROAMING_DATA", roamingRestriction.optString("allowRoamingData", "-1"));
            restrictionInfo.put("BROWSER_ALLOW_FRAUD_WARNING", browserRestriciton.optString("browserAllowFraudWarning", "-1"));
            restrictionInfo.put("BROWSER_ALLOW_POPUPS", browserRestriciton.optString("browserAllowPopups", "-1"));
            restrictionInfo.put("BROWSER_ALLOW_JAVASCRIPT", browserRestriciton.optString("browserAllowJavaScript", "-1"));
            restrictionInfo.put("BROWSER_ALLOW_AUTOFILL", browserRestriciton.optString("browserAllowAutoFill", "-1"));
            restrictionInfo.put("BROWSER_ALLOW_COOKIES", browserRestriciton.optString("browserAllowCookies", "-1"));
            restrictionInfo.put("ALLOW_ANDROID_BROWSER", browserRestriciton.optString("allowAndroidBrowser", "-1"));
            restrictionInfo.put("ALLOW_ANDROID_MARKET", applictionRestriction.optString("allowAndroidMarket", "-1"));
            restrictionInfo.put("ALLOW_NON_MARKET_APPS", applictionRestriction.optString("allowNonMarketApps", "-1"));
            restrictionInfo.put("ALLOW_YOU_TUBE", applictionRestriction.optString("allowYouTube", "-1"));
            restrictionInfo.put("ALLOW_VOICE_DIALER", applictionRestriction.optString("allowVoiceDialer", "-1"));
            restrictionInfo.put("ALLOW_INSTALL_APP", applictionRestriction.optString("allowInstallApp", "-1"));
            restrictionInfo.put("ALLOW_UNINSTALL_APP", applictionRestriction.optString("allowUnInstallApp", "-1"));
            restrictionInfo.put("ALLOW_USER_CREATION", applictionRestriction.optString("allowUserCreation", "-1"));
            restrictionInfo.put("ALLOW_S_VOICE", applictionRestriction.optString("allowSVoice", "-1"));
            restrictionInfo.put("ALLOW_APP_NOTIFICATION_MODE", applictionRestriction.optString("setApplicationNotificationMode", "-1"));
            restrictionInfo.put("ALLOW_STOP_SYSTEM_APP", applictionRestriction.optString("allowStopSystemApp", "-1"));
            restrictionInfo.put("ALLOW_GOOGLE_MAPS", applictionRestriction.optString("allowGoogleMaps", "-1"));
            restrictionInfo.put("ALLOW_GMAIL", applictionRestriction.optString("allowGoogleMail", "-1"));
            restrictionInfo.put("ALLOW_S_FINDER", applictionRestriction.optString("allowSFinder", "-1"));
            restrictionInfo.put("ALLOW_CONTACTS_OUTSIDE", deviceRestriction.optString("allowContactsOutside", "-1"));
            restrictionInfo.put("ALLOW_SHARELIST", deviceRestriction.optString("allowShareViaList", "-1"));
            restrictionInfo.put("ALLOW_OTHER_KEYPAD", deviceRestriction.optString("allowOtherKeypads", "-1"));
            restrictionInfo.put("ALLOW_KNOX_APP_STORE", deviceRestriction.optString("allowKnoxAppStore", "-1"));
            restrictionInfo.put("ALLOW_INCOMING_MMS", phoneRestriction.optString("allowIncomingMms", "-1"));
            restrictionInfo.put("ALLOW_INCOMING_SMS", phoneRestriction.optString("allowIncomingSms", "-1"));
            restrictionInfo.put("ALLOW_OUTGOING_MMS", phoneRestriction.optString("allowOutgoingMms", "-1"));
            restrictionInfo.put("ALLOW_OUTGOING_SMS", phoneRestriction.optString("allowOutgoingSms", "-1"));
            restrictionInfo.put("ALLOW_OUTGOING_CALL", phoneRestriction.optString("allowOutgoingCall", "-1"));
            restrictionInfo.put("ALLOW_INCOMING_CALL", phoneRestriction.optString("allowIncomingCall", "-1"));
            restrictionInfo.put("ALLOW_BT_DATA_TRANSFER", bluetoothRestriction.optString("setAllowBluetoothDataTransfer", "-1"));
            restrictionInfo.put("ALLOW_BT_DISCOVERABLE", bluetoothRestriction.optString("setDiscoverableState", "-1"));
            restrictionInfo.put("ALLOW_BT_OUTGOING_CALLS", bluetoothRestriction.optString("allowOutgoingCallsViaBluetooth", "-1"));
            restrictionInfo.put("ALLOW_BT_PAIRING", bluetoothRestriction.optString("setPairingState", "-1"));
            restrictionInfo.put("ALLOW_BT_PC_CONNECTION", bluetoothRestriction.optString("setDesktopConnectivityState", "-1"));
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception Occurred on isolating restriciton information from response data{0}", ex.getStackTrace().toString());
            this.isDataPopulationSuccess = false;
        }
        return restrictionInfo;
    }
    
    private JSONObject isolateCertificateInfo(final JSONObject certificateDetails) {
        final JSONObject certificateInfo = new JSONObject();
        try {
            certificateInfo.put("CommonName", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CommonName", null));
            certificateInfo.put("IsIdentity", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "IsIdentity", Boolean.FALSE.toString()));
            certificateInfo.put("CERTIFICATE_TYPE", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateType", null));
            certificateInfo.put("CERTIFICATE_VERSION", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateVersion", null));
            certificateInfo.put("CERTIFICATE_SERIAL_NUMBER", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateSerialNumber", null));
            certificateInfo.put("SIGNATURE_ALGORITHM_OID", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "SingnatureAlgorithmOID", null));
            certificateInfo.put("SIGNATURE_ALGORITHM_NAME", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "SignatureAlgorithmName", null));
            certificateInfo.put("CERTIFICATE_SIGNATURE", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateSignature", null));
            certificateInfo.put("CERTIFICATE_EXPIRE", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateExpire", null));
            certificateInfo.put("CERTIFICATE_ISSUER_DN", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateIssuerDN", null));
            certificateInfo.put("CERTIFICATE_SUBJECT_DN", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateSubjectDN", null));
            certificateInfo.put("CERTIFICATE_CONTENT", (Object)this.jsonUtil.checkAndUpdateTheValue(certificateDetails, "CertificateContent", null));
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception Occurred on isolating certificate information from response data.", ex);
            this.isDataPopulationSuccess = false;
        }
        return certificateInfo;
    }
    
    protected void processCertificates(final Long resourceID, final JSONObject inventoryData) {
        this.logger.log(Level.INFO, "Inside processCertificates() ->  resourceID {0}", resourceID);
        try {
            final JSONArray certificateArray = new JSONArray(String.valueOf(inventoryData.get("CertificateDetails")));
            final DataObject dataObject = MDMUtil.getPersistence().constructDataObject();
            MDMInvDataPopulator.getInstance().deleteCertToResourceRelDetails(resourceID);
            if (certificateArray != null) {
                for (int i = 0; i < certificateArray.length(); ++i) {
                    final JSONObject certificateInfoDetails = (JSONObject)certificateArray.get(i);
                    final JSONObject certificateInfo = this.isolateCertificateInfo(certificateInfoDetails);
                    MDMInvDataPopulator.getInstance().addOrUpdateCertificatesInfo(resourceID, certificateInfo, dataObject);
                }
                if (!dataObject.isEmpty()) {
                    MDMUtil.getPersistence().add(dataObject);
                }
            }
            else {
                this.logger.log(Level.INFO, "Cerificate Array Empty.");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception occurred in process certificates.", ex);
        }
    }
    
    protected void processApps(final Long resourceID, final JSONObject inventoryData) {
        this.processApps(resourceID, inventoryData, 0);
    }
    
    protected void processApps(final Long resourceID, final JSONObject inventoryData, final int scope) {
        this.logger.log(Level.INFO, "Inside processApps() ->  resourceID {0}", resourceID);
        try {
            final JSONArray appListArray = new JSONArray(String.valueOf(inventoryData.get("SoftwareDetails")));
            final AppDataHandler appHandler = new AppDataHandler();
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceID);
            appHandler.processAndroidSoftwares(resourceID, customerId, appListArray, scope, 1);
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception occurred in process SAFE Device Apps.", ex);
            this.isDataPopulationSuccess = false;
        }
    }
    
    public boolean isDataProcessSuccess() {
        return this.isDataPopulationSuccess;
    }
    
    public void processSamsungCompliance(final Long resourceID, final JSONObject data) {
        try {
            final JSONObject joCompliance = data.getJSONObject("PolicyInfo");
            final JSONObject securityData = joCompliance.getJSONObject("SecurityInfo");
            final HashMap securityInfo = new HashMap();
            securityInfo.put("StorageEncryption", String.valueOf(securityData.get("StorageEncryption")));
            securityInfo.put("PasscodePresent", String.valueOf(securityData.get("PasscodePresent")));
            securityInfo.put("PasscodeCompliant", "true");
            securityInfo.put("PasscodeCompliantWithProfiles", String.valueOf(securityData.get("PasscodeCompliantWithProfiles")));
            securityInfo.put("DeviceRooted", String.valueOf(securityData.get("DeviceRooted")));
            MDMInvDataPopulator.getInstance().addOrUpdateIOSSecurityInfo(resourceID, securityInfo);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, ex, () -> "Exception occurred while processing Samsing complience resID:" + n + " Data : " + jsonObject);
        }
    }
    
    public void handleSystemApps(final Long resourceId, final String data, final int scope, final int appType) {
        try {
            final JSONObject systemAppCmdResp = new JSONObject(data);
            final String systemAppResp = String.valueOf(systemAppCmdResp.get("ResponseData"));
            final JSONObject systemApps = new JSONObject(systemAppResp);
            final JSONObject systemAppsInf = new JSONObject(String.valueOf(systemApps.get("PreloadedApps")));
            final JSONArray appListArray = new JSONArray(String.valueOf(systemAppsInf.get("AppList")));
            final AppDataHandler appHandler = new AppDataHandler();
            final Long customerId = CustomerInfoUtil.getInstance().getCustomerIDForResID(resourceId);
            appHandler.processAndroidSoftwares(resourceId, customerId, appListArray, scope, appType);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exceptoin occurred while handleSystemApps", ex);
        }
    }
    
    public static SamsungInventory getSamsungInventoryInstance(final String scope) {
        if (scope != null && scope.equalsIgnoreCase("container")) {
            return SamsungKnoxInventoryHandler.getInstance();
        }
        return (SamsungInventory.samsungInventory == null) ? (SamsungInventory.samsungInventory = new SamsungInventory()) : SamsungInventory.samsungInventory;
    }
    
    static {
        SamsungInventory.samsungInventory = null;
    }
}
