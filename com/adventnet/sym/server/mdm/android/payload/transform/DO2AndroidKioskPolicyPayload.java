package com.adventnet.sym.server.mdm.android.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.JSONArray;
import com.adventnet.sym.server.mdm.android.payload.AndroidKioskPolicyPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;
import com.adventnet.persistence.DataObject;

public class DO2AndroidKioskPolicyPayload implements DO2AndroidPayload
{
    @Override
    public AndroidPayload createPayload(final DataObject dataObject) {
        AndroidKioskPolicyPayload kioskPayload = null;
        try {
            final Iterator iterator = dataObject.getRows("AndroidKioskPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                kioskPayload = new AndroidKioskPolicyPayload("1.0", "com.mdm.mobiledevice.kiosk", "Kiosk Policy");
                final boolean allowStatusBar = (boolean)row.get("ALLOW_STATUS_BAR");
                final boolean allowStatusbarExp = (boolean)row.get("ALLOW_STATUS_BAR_EXPANSION");
                final JSONArray apps = new JSONArray();
                final Iterator appIterator = dataObject.getRows("MdAppGroupDetails");
                while (appIterator.hasNext()) {
                    final Row appRow = appIterator.next();
                    apps.put(appRow.get("IDENTIFIER"));
                }
                final Integer kioskType = (Integer)row.get("KIOSK_MODE");
                kioskPayload.setKioskType(kioskType);
                kioskPayload.setKioskApps(apps);
                final JSONArray webApps = new JSONArray();
                final Iterator webAppIterator = dataObject.getRows("WebClipPolicies");
                while (webAppIterator.hasNext()) {
                    final Row webclipRow = webAppIterator.next();
                    webApps.put(webclipRow.get("WEBCLIP_URL"));
                }
                kioskPayload.setKioskWebApps(webApps);
                final JSONArray bgapps = new JSONArray();
                final Iterator bgAppIterator = dataObject.getRows("MDBACKGROUNDAPPGROUPDETAILS");
                while (bgAppIterator.hasNext()) {
                    final Row appRow2 = bgAppIterator.next();
                    bgapps.put(appRow2.get("IDENTIFIER"));
                }
                kioskPayload.setBackgroundApps(bgapps);
                final int launcherType = (int)row.get("LAUNCHER_TYPE");
                kioskPayload.setLauncherType(launcherType);
                if (launcherType != 1) {
                    kioskPayload.setKioskWallpaper((String)row.get("WALLPAPER"));
                    kioskPayload.setMEMDMAppVisiable((boolean)row.get("SHOW_ME_MDM_APP"));
                    kioskPayload.setAllowTaskManager((boolean)row.get("ALLOW_TASK_MANAGER"));
                    kioskPayload.setAllowSystemBar((boolean)row.get("ALLOW_SYSTEM_BAR"));
                    kioskPayload.setAllowNavigationBar((boolean)row.get("ALLOW_NAVIGATION_BAR"));
                    kioskPayload.setAllowStatusBar(allowStatusBar);
                    kioskPayload.setAllowStatusBarExpansion(allowStatusBar ? allowStatusbarExp : allowStatusBar);
                    kioskPayload.setAllowHomeButton((boolean)row.get("ALLOW_HOME_BUTTON"));
                    kioskPayload.setAllowVolumeButton((boolean)row.get("ALLOW_VOLUME_BUTTON"));
                    kioskPayload.setVolumeConfiguration((int)row.get("MEDIA_VOLUME"), (int)row.get("RING_VOLUME"), (int)row.get("NOTIFICATION_VOLUME"), (int)row.get("ALARM_VOLUME"));
                    kioskPayload.setAllowBackButton((boolean)row.get("ALLOW_BACK_BUTTON"));
                    kioskPayload.setAllowPowerButton((boolean)row.get("ALLOW_POWER_BUTTON"));
                    kioskPayload.setAllowShutDown((boolean)row.get("ALLOW_SHUTDOWN"));
                    kioskPayload.setAllowKeyGaurd((boolean)row.get("ALLOW_KEY_GUARD"));
                    kioskPayload.setAllowNotificationBar((boolean)row.get("ALLOW_NOTIFICATION"));
                    kioskPayload.setAllowCrashDialog((boolean)row.get("ALLOW_SYSTEM_ERROR_DIALOG"));
                    kioskPayload.setAllowRecentApps((boolean)row.get("ALLOW_RECENT_APPS"));
                    kioskPayload.setIsStayAwakeOnCharging((boolean)row.get("IS_STAY_AWAKE_ON_CHARGING"));
                    if (row.get("ALLOW_SIM_UNLOCK") != null) {
                        kioskPayload.setAllowSimUnlock((boolean)row.get("ALLOW_SIM_UNLOCK"));
                    }
                    kioskPayload.setKioskRestrictions(kioskPayload.getRestictionObject());
                    final boolean isCustomSettingsConfigured = (boolean)row.get("ALLOW_CUSTOM_SETTINGS");
                    if (isCustomSettingsConfigured) {
                        final Row customSettingsRow = dataObject.getRow("KioskCustomSettings");
                        kioskPayload.setIsWifiAllowed((boolean)customSettingsRow.get("ALLOW_WIFI"));
                        kioskPayload.setIsBrightnessAllowed((boolean)customSettingsRow.get("ALLOW_BRIGHTNESS"));
                        kioskPayload.setIsFlashLightAllowed((boolean)customSettingsRow.get("ALLOW_FLASH_LIGHT"));
                        kioskPayload.setScreenOrientation((int)customSettingsRow.get("SCREEN_ORIENTATION"));
                        kioskPayload.setScreenTimeout((int)customSettingsRow.get("SCREEN_TIMEOUT"));
                        kioskPayload.setIsMobileNetworkSettingsAllowed((boolean)customSettingsRow.get("ALLOW_MOBILE_NETWORK"));
                        kioskPayload.setIsMobileHotspotSettingsAllowed((boolean)customSettingsRow.get("ALLOW_MOBILE_HOTSPOT_SETTINGS"));
                        kioskPayload.setHotspotSettingsTimeout((int)customSettingsRow.get("SETTINGS_TIMEOUT"));
                        kioskPayload.setIsBlutoothSettingsAllowed((boolean)customSettingsRow.get("ALLOW_BLUETOOTH_SETTINGS"));
                        kioskPayload.setBluetoothSettingsTimeout((int)customSettingsRow.get("SETTINGS_TIMEOUT"));
                        kioskPayload.setIsAPNSettingsAllowed((boolean)customSettingsRow.get("ALLOW_APN_SETTINGS"));
                        kioskPayload.setAPNSettingsTimeout((int)customSettingsRow.get("SETTINGS_TIMEOUT"));
                        kioskPayload.setAllowBatteryOptimization((boolean)customSettingsRow.get("ALLOW_BATTERY_OPTIMIZATION"));
                        kioskPayload.setAllowLocaleSettings((boolean)customSettingsRow.get("ALLOW_LOCALE_SETTINGS"));
                        kioskPayload.setSettingsTimeout((int)customSettingsRow.get("SETTINGS_TIMEOUT"));
                        kioskPayload.setMobileDataUsage((boolean)customSettingsRow.get("MOBILE_DATA_USAGE"));
                        kioskPayload.setDefaultKeyboardSetting((boolean)customSettingsRow.get("DEFAULT_KEYBOARD"));
                        final JSONArray batteryOptimizedAppsArray = new JSONArray();
                        final Iterator batteryOptimizedAppsIterator = dataObject.getRows("MDBATTERYOPTIMIZEDAPPS");
                        while (batteryOptimizedAppsIterator.hasNext()) {
                            final Row appRow3 = batteryOptimizedAppsIterator.next();
                            batteryOptimizedAppsArray.put(appRow3.get("IDENTIFIER"));
                        }
                        kioskPayload.setBatteryOptimizedApps(batteryOptimizedAppsArray);
                        final boolean isWifiAllowed = (boolean)customSettingsRow.get("ALLOW_WIFI");
                        if (isWifiAllowed) {
                            kioskPayload.setAllowWifiNetworkConfiguration((boolean)customSettingsRow.get("ALLOW_WIFI_NETWORK_CONFIGURATION"));
                        }
                        else {
                            kioskPayload.setAllowWifiNetworkConfiguration(false);
                        }
                    }
                    kioskPayload.setIsCustomSettings(isCustomSettingsConfigured);
                    final Long appGroupId = (Long)row.get("DEFAULT_KIOSK_APP");
                    if (appGroupId != null) {
                        String packageName = null;
                        final Criteria appIdCriteria = new Criteria(new Column("MdAppGroupDetails", "APP_GROUP_ID"), (Object)appGroupId, 0);
                        final Iterator appRowItr = dataObject.getRows("MdAppGroupDetails", appIdCriteria);
                        while (appRowItr.hasNext()) {
                            final Row appDetailRow = appRowItr.next();
                            packageName = (String)appDetailRow.get("IDENTIFIER");
                        }
                        if (packageName != null) {
                            kioskPayload.setDefaultAppSettings(packageName, (int)row.get("DEFAULT_APP_LAUNCH_TIMEOUT"), (int)row.get("DEFAULT_APP_LAUNCH_MODE"));
                        }
                    }
                    kioskPayload.setAdvancedSettings();
                }
                if (kioskType == 3) {
                    final Integer idleRefreshTimeOut = (Integer)row.get("IDLE_REFRESH_TIMEOUT");
                    kioskPayload.setIdleRefreshTimeOut(idleRefreshTimeOut);
                }
                String exitKioskPassword = "";
                if (row.get("EXIT_KIOSK_PASSWORD_ID") != null) {
                    final Long exitKioskPasswordId = (Long)row.get("EXIT_KIOSK_PASSWORD_ID");
                    exitKioskPassword = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(exitKioskPasswordId.toString());
                }
                if (!MDMStringUtils.isEmpty(exitKioskPassword)) {
                    kioskPayload.setExitKioskPassword(exitKioskPassword);
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(DO2AndroidKioskPolicyPayload.class.getName()).log(Level.SEVERE, "Exception in createPayload", ex);
        }
        return kioskPayload;
    }
}
