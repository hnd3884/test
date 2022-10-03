package com.adventnet.sym.server.mdm.android.payload;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.io.File;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AndroidKioskPolicyPayload extends AndroidPayload
{
    private JSONObject kioskRestriction;
    private JSONObject customSettings;
    private JSONObject advancedSettings;
    private static final String[] SCREEN_ROTATION_KEYS;
    public static final int KIOSK_DEVICE_LAUNCHER = 2;
    public static final int KIOSK_MDM_LAUNCHER = 0;
    
    public AndroidKioskPolicyPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Kiosk", payloadIdentifier, payloadDisplayName);
        this.kioskRestriction = new JSONObject();
        this.customSettings = new JSONObject();
        this.advancedSettings = new JSONObject();
    }
    
    public void setKioskType(final int type) throws JSONException {
        this.getPayloadJSON().put("KioskType", type);
    }
    
    public void setLauncherType(final int type) throws JSONException {
        this.getPayloadJSON().put("LauncherType", type);
    }
    
    public void setKioskApps(final JSONArray apps) throws JSONException {
        this.getPayloadJSON().put("KioskApps", (Object)apps);
    }
    
    public void setKioskWebApps(final JSONArray webApps) throws JSONException {
        this.getPayloadJSON().put("WebApps", (Object)webApps);
    }
    
    public void setBackgroundApps(final JSONArray apps) throws JSONException {
        this.getPayloadJSON().put("BackgroundApps", (Object)apps);
    }
    
    public void setIdleRefreshTimeOut(final Integer integer) throws JSONException {
        this.getPayloadJSON().put("IdleRefreshTimeOut", (Object)integer);
    }
    
    public void setExitKioskPassword(final String kioskPassword) throws JSONException {
        this.getPayloadJSON().put("ExitKioskPassword", (Object)kioskPassword);
    }
    
    public void setKioskWallpaper(String wallpaper) throws JSONException {
        if (wallpaper != null) {
            wallpaper = wallpaper.toString().replace("/", File.separator);
            final HashMap hm = new HashMap();
            hm.put("path", wallpaper);
            hm.put("IS_SERVER", false);
            hm.put("IS_AUTHTOKEN", false);
            wallpaper = ApiFactoryProvider.getFileAccessAPI().constructFileURL(hm);
        }
        this.getPayloadJSON().put("KioskWallpaper", (Object)wallpaper);
    }
    
    public void setMEMDMAppVisiable(final boolean visible) throws JSONException {
        this.getPayloadJSON().put("ShowMEMDM", visible);
    }
    
    public void setKioskRestrictions(final JSONObject restrictions) throws JSONException {
        this.getPayloadJSON().put("KioskRestrictions", (Object)restrictions);
    }
    
    public JSONObject getRestictionObject() {
        return this.kioskRestriction;
    }
    
    public void setAllowTaskManager(final boolean isAllowTaskManager) throws JSONException {
        this.kioskRestriction.put("AllowTaskManager", isAllowTaskManager);
    }
    
    public void setAllowSystemBar(final boolean isAllowSystemBar) throws JSONException {
        this.kioskRestriction.put("AllowSystemBar", isAllowSystemBar);
    }
    
    public void setAllowNavigationBar(final boolean isAllowNavigationBar) throws JSONException {
        this.kioskRestriction.put("AllowNavigationBar", isAllowNavigationBar);
    }
    
    public void setAllowStatusBar(final boolean isAllowStatusBar) throws JSONException {
        this.kioskRestriction.put("AllowStatusBar", isAllowStatusBar);
    }
    
    public void setAllowStatusBarExpansion(final boolean isAllowStatusBarExpansion) throws JSONException {
        this.kioskRestriction.put("AllowStatusBarExpansion", isAllowStatusBarExpansion);
    }
    
    public void setAllowHomeButton(final boolean isAllowHomeButton) throws JSONException {
        this.kioskRestriction.put("AllowHome", isAllowHomeButton);
    }
    
    public void setAllowVolumeButton(final boolean isAllowVolumeButton) throws JSONException {
        this.kioskRestriction.put("AllowVolume", isAllowVolumeButton);
    }
    
    public void setAllowBackButton(final boolean isAllowBackButton) throws JSONException {
        this.kioskRestriction.put("AllowBack", isAllowBackButton);
    }
    
    public void setAllowPowerButton(final boolean isAllowPowerButton) throws JSONException {
        this.kioskRestriction.put("AllowPower", isAllowPowerButton);
    }
    
    public void setAllowShutDown(final boolean isAllowShutDown) throws JSONException {
        this.kioskRestriction.put("AllowShutDown", isAllowShutDown);
    }
    
    public void setAllowKeyGaurd(final boolean isAllowKeyGuard) throws JSONException {
        this.kioskRestriction.put("AllowKeyGuard", !isAllowKeyGuard);
    }
    
    public void setAllowNotificationBar(final boolean isAllowNotificationBar) throws JSONException {
        this.kioskRestriction.put("AllowNotification", isAllowNotificationBar);
    }
    
    public void setAllowCrashDialog(final boolean isAllowCrashDialog) throws JSONException {
        this.kioskRestriction.put("AllowSystemErrorDialog", isAllowCrashDialog);
    }
    
    public void setAllowRecentApps(final boolean isAlloweRecentApps) throws JSONException {
        this.kioskRestriction.put("AllowAppSwitch", isAlloweRecentApps);
    }
    
    public void setIsStayAwakeOnCharging(final boolean isStayAwakeOnCharging) throws JSONException {
        this.kioskRestriction.put("IsStayAwakeOnCharging", isStayAwakeOnCharging);
    }
    
    public void setAllowSimUnlock(final boolean isAllowSimUnlock) throws JSONException {
        this.kioskRestriction.put("AllowSIMUnlock", isAllowSimUnlock);
    }
    
    public void setIsWifiAllowed(final boolean isWifiAllowed) throws JSONException {
        this.customSettings.put("Wifi", isWifiAllowed);
    }
    
    public void setIsBrightnessAllowed(final boolean isBrightnessAllowed) throws JSONException {
        this.customSettings.put("Brightness", isBrightnessAllowed);
    }
    
    public void setIsFlashLightAllowed(final boolean isFlashLightAllowed) throws JSONException {
        this.customSettings.put("FlashLight", isFlashLightAllowed);
    }
    
    public void setScreenOrientation(final int screenOrientation) throws JSONException {
        this.customSettings.put("ScreenOrientation", (Object)AndroidKioskPolicyPayload.SCREEN_ROTATION_KEYS[screenOrientation - 1]);
    }
    
    public void setIsCustomSettings(final boolean isCustomsettingsConfigured) throws JSONException {
        this.kioskRestriction.put("CustomSettingsEnabled", isCustomsettingsConfigured);
        if (isCustomsettingsConfigured) {
            this.getPayloadJSON().put("SettingsConfig", (Object)this.customSettings);
        }
    }
    
    public void setScreenTimeout(final int timeDuration) throws JSONException {
        this.customSettings.put("ScreenTimeOut", timeDuration);
    }
    
    public void setIsMobileNetworkSettingsAllowed(final boolean isMobileNetworkSettingsAllowed) throws JSONException {
        this.customSettings.put("MobileNetwork", isMobileNetworkSettingsAllowed);
    }
    
    public void setIsMobileHotspotSettingsAllowed(final boolean isMobileHotspotSettingsAllowed) throws JSONException {
        this.customSettings.put("MobileHotSpot", isMobileHotspotSettingsAllowed);
    }
    
    public void setHotspotSettingsTimeout(final int timeDuration) throws JSONException {
        this.customSettings.put("SettingsTimeoutHotspot", timeDuration);
    }
    
    public void setIsBlutoothSettingsAllowed(final boolean isBlutoothSettingsAllowed) throws JSONException {
        this.customSettings.put("Bluetooth", isBlutoothSettingsAllowed);
    }
    
    public void setBluetoothSettingsTimeout(final int timeDuration) throws JSONException {
        this.customSettings.put("SettingsTimeoutBluetooth", timeDuration);
    }
    
    public void setIsAPNSettingsAllowed(final boolean isAPNAllowed) throws JSONException {
        this.customSettings.put("Apn", isAPNAllowed);
    }
    
    public void setAPNSettingsTimeout(final int timeDuration) throws JSONException {
        this.customSettings.put("SettingsTimeoutApn", timeDuration);
    }
    
    public void setAllowWifiNetworkConfiguration(final boolean allowWifiNetworkConfiguration) throws JSONException {
        this.customSettings.put("AllowAddWifiNetwork", allowWifiNetworkConfiguration);
    }
    
    public void setAllowBatteryOptimization(final boolean allowBatteryOptimization) throws JSONException {
        this.customSettings.put("BatteryOptimizationEnabled", allowBatteryOptimization);
    }
    
    public void setAllowLocaleSettings(final boolean allowLocaleSettings) throws JSONException {
        this.customSettings.put("Locale", allowLocaleSettings);
    }
    
    public void setBatteryOptimizedApps(final JSONArray apps) throws JSONException {
        this.customSettings.put("BatteryOptimizationApps", (Object)apps);
    }
    
    public void setSettingsTimeout(final int timeDuration) throws JSONException {
        this.customSettings.put("SettingsTimeout", timeDuration);
    }
    
    public void setMobileDataUsage(final boolean mobileDataUsage) throws JSONException {
        this.customSettings.put("MobileDataUsage", mobileDataUsage);
    }
    
    public void setDefaultKeyboardSetting(final boolean defaultKeyboardSetting) throws JSONException {
        this.customSettings.put("KeyboardChange", defaultKeyboardSetting);
    }
    
    public void setVolumeConfiguration(final int mediaVolume, final int ringVolume, final int notificationVolume, final int alarmVolume) throws JSONException {
        final JSONObject volumeConfigurations = new JSONObject();
        volumeConfigurations.put("MediaVolume", mediaVolume);
        volumeConfigurations.put("RingVolume", ringVolume);
        volumeConfigurations.put("NotificationVolume", notificationVolume);
        volumeConfigurations.put("AlarmVolume", alarmVolume);
        volumeConfigurations.put("VoiceCallVolume", -1);
        this.kioskRestriction.put("VolumeConfigurations", (Object)volumeConfigurations);
    }
    
    public void setAdvancedSettings() throws JSONException {
        this.getPayloadJSON().put("AdvancedSettings", (Object)this.advancedSettings);
    }
    
    public void setDefaultAppSettings(final String appPackageName, final int duration, final int mode) throws JSONException {
        final JSONObject defaultAppSettings = new JSONObject();
        defaultAppSettings.put("DefaultKioskAppPackageName", (Object)appPackageName);
        defaultAppSettings.put("LaunchDefaultAppTimeout", duration * 1000);
        defaultAppSettings.put("DefaultAppLaunch", mode);
        this.advancedSettings.put("DefaultAppSettings", (Object)defaultAppSettings);
    }
    
    static {
        SCREEN_ROTATION_KEYS = new String[] { "AUTO", "USER", "PORTRAIT", "LANDSCAPE" };
    }
}
