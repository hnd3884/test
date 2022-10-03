package com.adventnet.sym.server.mdm.inv.android;

import java.util.logging.Level;
import java.util.Map;
import com.adventnet.sym.server.mdm.inv.MDMInvDataPopulator;
import org.json.JSONObject;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.inv.MDMInvdetails;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.inv.MDMInventory;

public class DeviceRestrictionMDMInventoryImpl implements MDMInventory
{
    private Logger logger;
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
    private static final String ALLOW_VPN = "allowVPN";
    private static final String ALLOW_GOOGLE_CRASH_REPORT = "allowGoogleCrashReport";
    private static final String ALLOW_OTA_UPGRADE = "allowOTAUpgrade";
    private static final String ALLOW_POWER_OFF = "allowPowerOff";
    private static final String ALLOW_SD_CARD_WRITE = "allowSDCardWrite";
    private static final String ALLOW_STATUSBAR_EXPANSION = "allowStatusBarExpansion";
    private static final String ALLOW_WALLPAPER_CHANGE = "allowWallpaperChange";
    private static final String ALLOW_CONTACTS_OUTSIDE = "allowContactsOutside";
    private static final String ALLOW_OTHER_KEYPADS = "allowOtherKeypads";
    private static final String ALLOW_KNOX_APP_STORE = "allowKnoxAppStore";
    private static final String ALLOW_SHARE_VIA_LIST = "allowShareViaList";
    private static final String ALLOW_WHITELIST_WIFI_ONLY = "activateWifiWhitelist";
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
    private static final String ALLOW_AUDIO_RECORD = "allowAudioRecord";
    private static final String ALLOW_VIDEO_RECORD = "allowVideoRecord";
    private static final String ALLOW_USB_HOST_STORAGE = "allowUsbHostStorage";
    private static final String ALLOW_USE_NETWORK_TIME = "setAutomaticTime";
    private static final String ALLOW_WIFI_STATE_CHANGE = "setWifiStateChangeAllowed";
    private static final String DEVICE_ADMIN_ENABLE = "deviceAdministratorEnabled";
    private static final String DISABLE_CELLULAR_DATA = "disableCellularData";
    private static final String DISABLE_GPS = "disableGPS";
    private static final String ALLOW_USER_CREATION = "allowUserCreation";
    private static final String ALLOW_USER_ADD_ACCOUNTS = "allowUserAddAccounts";
    private static final String ALLOW_AIR_COMMAND = "allowAirCommand";
    private static final String ALLOW_AIR_VIEW = "allowAirView";
    private static final String DISABLE_GPS_STATE_CHANGE = "setGPSStateChangeAllowed";
    private static final String PLAY_PROTECT_MONITORING = "playProtect";
    private static final String ALLOW_CAMERA_IN_KEYGUARD = "allowKeyguardCamera";
    private static final String ALLOW_KEYGUARD_TRUST_AGENTS = "allowTrustAgents";
    private static final String ALLOW_KEYGUARD_NOTIFICATION = "allowKeyguardNotifications";
    private static final String ALLOW_BACKUP_RESTORE = "allowBackupAndRestore";
    private static final String ALLOW_NON_MARKET_APPS_IN_PERSONAL = "allowNonMarketAppsinPersonal";
    
    public DeviceRestrictionMDMInventoryImpl() {
        this.logger = null;
    }
    
    @Override
    public boolean populateInventoryData(final MDMInvdetails inventoryObject) {
        final HashMap<String, String> restrictionInfo = new HashMap<String, String>();
        boolean isDataPopulationSuccess = false;
        try {
            final JSONObject inventoryData = new JSONObject(inventoryObject.strData);
            restrictionInfo.put("ALLOW_CLIPBOARD", inventoryData.optString("allowClipboard", "-1"));
            restrictionInfo.put("ALLOW_GOOGLE_CRASH_REPORT", inventoryData.optString("allowGoogleCrashReport", "-1"));
            restrictionInfo.put("ALLOW_CELLULAR_DATA", inventoryData.optString("allowCellularData", "-1"));
            restrictionInfo.put("ALLOW_DISABLING_CELLULAR_DATA", inventoryData.optString("disableCellularData", "-1"));
            final int gpsStateChange = Integer.valueOf(inventoryData.optString("setGPSStateChangeAllowed", "-1"));
            final int disableGPS = Integer.valueOf(inventoryData.optString("disableGPS", "-1"));
            if (disableGPS < gpsStateChange) {
                restrictionInfo.put("ALLOW_DISABLING_GPS", inventoryData.optString("setGPSStateChangeAllowed", "-1"));
            }
            else {
                restrictionInfo.put("ALLOW_DISABLING_GPS", inventoryData.optString("disableGPS", "-1"));
            }
            restrictionInfo.put("ALLOW_TETHERING", inventoryData.optString("allowTethering", "-1"));
            restrictionInfo.put("ALLOW_BLUETOOTH_TETHERING", inventoryData.optString("allowBluetoothTethering", "-1"));
            restrictionInfo.put("ALLOW_MOCK_LOCATION", inventoryData.optString("allowMockLocation", "-1"));
            restrictionInfo.put("ALLOW_MICROPHONE", inventoryData.optString("allowMicroPhone", "-1"));
            final int wifiStateChange = Integer.valueOf(inventoryData.optString("setWifiStateChangeAllowed", "-1"));
            final int wifi = Integer.valueOf(inventoryData.optString("allowWiFi", "-1"));
            if (wifi < wifiStateChange) {
                restrictionInfo.put("ALLOW_WIFI", inventoryData.optString("setWifiStateChangeAllowed", "-1"));
            }
            else {
                restrictionInfo.put("ALLOW_WIFI", inventoryData.optString("allowWiFi", "-1"));
            }
            restrictionInfo.put("ALLOW_SD_CARD_WRITE", inventoryData.optString("allowSDCardWrite", "-1"));
            restrictionInfo.put("ALLOW_BLUETOOTH", inventoryData.optString("allowBluetooth", "-1"));
            restrictionInfo.put("ALLOW_BACKGROUND_DATA", inventoryData.optString("disableBackgroundData", "-1"));
            restrictionInfo.put("ALLOW_POWER_OFF", inventoryData.optString("allowPowerOff", "-1"));
            restrictionInfo.put("ALLOW_FACTORY_RESET", inventoryData.optString("allowFactoryReset", "-1"));
            restrictionInfo.put("ALLOW_SD_CARD", inventoryData.optString("allowSDCard", "-1"));
            final int nfcStateChange = Integer.valueOf(inventoryData.optString("setNFCStateChangeAllowed", "-1"));
            final int nfc = Integer.valueOf(inventoryData.optString("allowNFC", "-1"));
            if (nfc < nfcStateChange) {
                restrictionInfo.put("ALLOW_NFC", inventoryData.optString("setNFCStateChangeAllowed", "-1"));
            }
            else {
                restrictionInfo.put("ALLOW_NFC", inventoryData.optString("allowNFC", "-1"));
            }
            restrictionInfo.put("ALLOW_SETTINGS", inventoryData.optString("allowSettings", "-1"));
            restrictionInfo.put("ALLOW_TETHERING", inventoryData.optString("allowTethering", "-1"));
            restrictionInfo.put("ALLOW_WALLPAPER_CHANGE", inventoryData.optString("allowWallpaperChange", "-1"));
            restrictionInfo.put("ALLOW_USB", inventoryData.optString("allowUSB", "-1"));
            restrictionInfo.put("ALLOW_USB_TETHERING", inventoryData.optString("allowUSBTethering", "-1"));
            restrictionInfo.put("ALLOW_USB_DEBUG", inventoryData.optString("allowUSBDebug", "-1"));
            restrictionInfo.put("ALLOW_GOOGLE_BACKUP", inventoryData.optString("allowGoogleBackup", "-1"));
            restrictionInfo.put("ALLOW_WIFI_TETHERING", inventoryData.optString("allowWiFiTethering", "-1"));
            restrictionInfo.put("ALLOW_USB_MEDIA_PLAYER", inventoryData.optString("allowUSBMediaPlayer", "-1"));
            restrictionInfo.put("ALLOW_STATUSBAR_EXPANSION", inventoryData.optString("allowStatusBarExpansion", "-1"));
            restrictionInfo.put("ALLOW_OTA_UPGRADE", inventoryData.optString("allowOTAUpgrade", "-1"));
            restrictionInfo.put("ALLOW_VPN", inventoryData.optString("allowVPN", "-1"));
            restrictionInfo.put("ALLOW_SCREEN_CAPTURE", inventoryData.optString("allowScreenCapture", "-1"));
            restrictionInfo.put("ALLOW_CAMERA", inventoryData.optString("allowCamera", "-1"));
            restrictionInfo.put("DEVICE_ADMIN_ENABLED", inventoryData.optString("deviceAdministratorEnabled", "-1"));
            restrictionInfo.put("ALLOW_ACTIVATION_LOCK", inventoryData.optString("allowActivationLock", "-1"));
            restrictionInfo.put("ALLOW_AIRPLANE_MODE", inventoryData.optString("allowAirplaneMode", "-1"));
            restrictionInfo.put("ALLOW_ANDROID_BEAM", inventoryData.optString("allowAndroidBeam", "-1"));
            restrictionInfo.put("ALLOW_BACKGROUND_PROCESS_LIMIT", inventoryData.optString("allowBackgroundProcessLimit", "-1"));
            restrictionInfo.put("ALLOW_S_BEAM", inventoryData.optString("allowSBeam", "-1"));
            restrictionInfo.put("ALLOW_WIFI_DIRECT", inventoryData.optString("allowWifiDirect", "-1"));
            restrictionInfo.put("ALLOW_SMART_CLIP_MODE", inventoryData.optString("allowSmartClipMode", "-1"));
            restrictionInfo.put("ALLOW_CLIPBOARD_SHARE", inventoryData.optString("allowClipboardShare", "-1"));
            restrictionInfo.put("ALLOW_FIRMWARE_RECOVERY", inventoryData.optString("allowFirmwareRecovery", "-1"));
            restrictionInfo.put("ALLOW_SDCARD_MOVE", inventoryData.optString("allowSDCardMove", "-1"));
            restrictionInfo.put("ALLOW_SAFE_MODE", inventoryData.optString("allowSafeMode", "-1"));
            restrictionInfo.put("ALLOW_HOME_KEY", inventoryData.optString("setHomeKeyState", "-1"));
            restrictionInfo.put("ALLOW_LOCK_SCREEN_VIEW", inventoryData.optString("setLockScreenState", "-1"));
            restrictionInfo.put("ALLOW_DEVELOPER_MODE", inventoryData.optString("allowDeveloperMode", "-1"));
            restrictionInfo.put("ALLOW_KILL_ACTIVITY_ON_LEAVE", inventoryData.optString("allowKillingActivitiesOnLeave", "-1"));
            restrictionInfo.put("ALLOW_USER_MOBILE_DATA_LIMIT", inventoryData.optString("allowUserMobileDataLimit", "-1"));
            restrictionInfo.put("ALLOW_DATE_TIME_CHANGE", inventoryData.optString("setDateTimeChangeEnabled", "-1"));
            restrictionInfo.put("ALLOW_GOOGLE_ACCOUNT_AUTO_SYNC", inventoryData.optString("allowGoogleAccountsAutoSync", "-1"));
            restrictionInfo.put("ALLOW_USER_PROFILE", inventoryData.optString("setAllowUserProfiles", "-1"));
            restrictionInfo.put("ALLOW_AUDIO_RECORD", inventoryData.optString("allowAudioRecord", "-1"));
            restrictionInfo.put("ALLOW_VIDEO_RECORD", inventoryData.optString("allowVideoRecord", "-1"));
            restrictionInfo.put("ALLOW_USB_HOST_STORAGE", inventoryData.optString("allowUsbHostStorage", "-1"));
            restrictionInfo.put("ALLOW_USE_NETWORK_TIME", inventoryData.optString("setAutomaticTime", "-1"));
            restrictionInfo.put("ALLOW_CONTACTS_OUTSIDE", inventoryData.optString("allowContactsOutside", "-1"));
            restrictionInfo.put("ALLOW_SHARELIST", inventoryData.optString("allowShareViaList", "-1"));
            restrictionInfo.put("ALLOW_OTHER_KEYPAD", inventoryData.optString("allowOtherKeypads", "-1"));
            restrictionInfo.put("ALLOW_USER_ADD_ACCOUNTS", inventoryData.optString("allowUserAddAccounts", "-1"));
            restrictionInfo.put("ALLOW_USER_CREATION", inventoryData.optString("allowUserCreation", "-1"));
            restrictionInfo.put("ALLOW_AIR_COMMAND", inventoryData.optString("allowAirCommand", "-1"));
            restrictionInfo.put("ALLOW_AIR_VIEW", inventoryData.optString("allowAirView", "-1"));
            restrictionInfo.put("ALLOW_KNOX_APP_STORE", inventoryData.optString("allowKnoxAppStore", "-1"));
            restrictionInfo.put("ALLOW_WHITELIST_WIFI_ONLY", inventoryData.optString("activateWifiWhitelist", "0"));
            restrictionInfo.put("ALLOW_PLAY_PROTECT_MONITORING", inventoryData.optString("playProtect", "2"));
            restrictionInfo.put("ALLOW_CAMERA_IN_KEYGUARD", inventoryData.has("allowKeyguardCamera") ? (inventoryData.optBoolean("allowKeyguardCamera") ? "1" : "2") : "-1");
            restrictionInfo.put("ALLOW_KEYGUARD_NOTIFICATIONS", inventoryData.optString("allowKeyguardNotifications", "2"));
            restrictionInfo.put("ALLOW_KEYGUARD_TRUST_AGENTS", inventoryData.has("allowTrustAgents") ? (inventoryData.optBoolean("allowTrustAgents") ? "1" : "2") : "-1");
            restrictionInfo.put("ALLOW_BACKUP_RESTORE", inventoryData.optString("allowBackupAndRestore", "2"));
            restrictionInfo.put("ALLOW_PERSONAL_SPACE_NON_MARKET_APPS", inventoryData.optString("allowNonMarketAppsinPersonal", "2"));
            final MDMInvDataPopulator invDataPopulator = MDMInvDataPopulator.getInstance();
            invDataPopulator.addOrUpdateAndroidSamsungRestriction(inventoryObject.resourceId, restrictionInfo, inventoryObject.scope);
            isDataPopulationSuccess = true;
        }
        catch (final Exception exp) {
            this.logger.log(Level.INFO, "Exception occurred on populating security details form response data..{0}", exp);
            isDataPopulationSuccess = false;
        }
        return isDataPopulationSuccess;
    }
}
