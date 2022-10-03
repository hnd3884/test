package com.adventnet.sym.server.mdm.android.payload;

import org.json.JSONException;

public class AndroidRestrictionsPayload extends AndroidPayload
{
    public AndroidRestrictionsPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Restrictions", payloadIdentifier, payloadDisplayName);
    }
    
    public void setAllowBackgroudData(final boolean value) throws JSONException {
        this.getPayloadJSON().put("disableBackgroundData", value);
    }
    
    public void setAllowBluetooth(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowBluetooth", value);
    }
    
    public void setAllowBTStateChange(final int value) throws JSONException {
        if (value == 1) {
            this.getPayloadJSON().put("setBTStateChangeAllowed", 5);
            this.getPayloadJSON().put("allowBluetooth", true);
        }
        else if (value == 2) {
            this.getPayloadJSON().put("setBTStateChangeAllowed", 6);
            this.getPayloadJSON().put("allowBluetooth", false);
        }
        else {
            this.getPayloadJSON().put("setBTStateChangeAllowed", 4);
            this.getPayloadJSON().put("allowBluetooth", true);
        }
    }
    
    public void setAllowUSB(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUSB", value);
    }
    
    public void setAllowCamera(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowCamera", value);
    }
    
    public void setAllowStorageEncryption(final boolean value) throws JSONException {
        this.getPayloadJSON().put("disableStorageEncryption", value);
    }
    
    public void setExternalStorageEncryption(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowExternalStorageEncryption", value);
    }
    
    public void setDisablingCellularData(final boolean value) throws JSONException {
        this.getPayloadJSON().put("disableCellularData", value);
    }
    
    public void setDisablingGPS(final int value) throws JSONException {
        this.getPayloadJSON().put("disableGPS", value != 5);
        this.getPayloadJSON().put("setGPSStateChangeAllowed", value);
    }
    
    public void setAllowNFC(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowNFC", value);
    }
    
    public void setAllowNFCStateChange(final int value) throws JSONException {
        if (value == 1) {
            this.getPayloadJSON().put("setNFCStateChangeAllowed", 5);
            this.getPayloadJSON().put("allowNFC", true);
        }
        else if (value == 2) {
            this.getPayloadJSON().put("setNFCStateChangeAllowed", 6);
            this.getPayloadJSON().put("allowNFC", false);
        }
        else {
            this.getPayloadJSON().put("setNFCStateChangeAllowed", 4);
            this.getPayloadJSON().put("allowNFC", true);
        }
    }
    
    public void setAllowBluetoothTethering(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowBluetoothTethering", value);
    }
    
    public void setAllowTethering(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowTethering", value);
    }
    
    public void setAllowFactoryReset(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowFactoryReset", value);
    }
    
    public void setAllowSettings(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowSettings", value);
    }
    
    public void setAllowWiFi(final int value) throws JSONException {
        this.getPayloadJSON().put("allowWiFi", value != 6);
        this.getPayloadJSON().put("setWifiStateChangeAllowed", value);
    }
    
    public void setAllowWiFiTethering(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowWiFiTethering", value);
    }
    
    public void setAllowGoogleBackup(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowGoogleBackup", value);
    }
    
    public void setAllowBackupAndRestore(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowBackupAndRestore", value);
    }
    
    public void setAllowCellularData(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowCellularData", value);
    }
    
    public void setAllowClipboard(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowClipboard", value);
    }
    
    public void setAllowMicrophone(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowMicroPhone", value);
    }
    
    public void setAllowMockLocation(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowMockLocation", value);
    }
    
    public void setAllowScreenCapture(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowScreenCapture", value);
    }
    
    public void setAllowSDCard(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowSDCard", value);
    }
    
    public void setAllowUSBDebug(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUSBDebug", value);
    }
    
    public void setAllowUSBTethering(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUSBTethering", value);
    }
    
    public void setAllowUSBMediaPlayer(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUSBMediaPlayer", value);
    }
    
    public void setAllowVPN(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowVPN", value);
    }
    
    public void setAllowGoogleCrashReport(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowGoogleCrashReport", value);
    }
    
    public void setAllowOTAUpgrade(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowOTAUpgrade", value);
    }
    
    public void setAllowPowerOff(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowPowerOff", value);
    }
    
    public void setAllowSDCardWrite(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowSDCardWrite", value);
    }
    
    public void setAllowStatusBarExpansion(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowStatusBarExpansion", value);
    }
    
    public void setAllowWallpaperChange(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowWallpaperChange", value);
    }
    
    public void setAllowAndroidMarket(final int value) throws JSONException {
        switch (value) {
            case 0: {
                this.getPayloadJSON().put("allowAndroidMarket", false);
                break;
            }
            case 1: {
                this.getPayloadJSON().put("allowAndroidMarket", true);
                break;
            }
        }
    }
    
    public void setAllowNonMarketApps(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowNonMarketApps", value);
    }
    
    public void setAllowNonMarketAppsInPersonalSpace(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowNonMarketAppsinPersonal", value);
    }
    
    public void setAllowVoiceDialer(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowVoiceDialer", value);
    }
    
    public void setAllowYouTube(final int value) throws JSONException {
        switch (value) {
            case 0: {
                this.getPayloadJSON().put("allowYouTube", false);
                break;
            }
            case 1: {
                this.getPayloadJSON().put("allowYouTube", true);
                break;
            }
        }
    }
    
    public void setAllowInstallApp(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowInstallApp", value);
    }
    
    public void setAllowUnInstallApp(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUnInstallApp", value);
    }
    
    public void setAllowAndroidBrowser(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowAndroidBrowser", value);
    }
    
    public void setAllowBrowserAutoFill(final boolean value) throws JSONException {
        this.getPayloadJSON().put("browserAllowAutoFill", value);
    }
    
    public void setAllowBrowserCookies(final boolean value) throws JSONException {
        this.getPayloadJSON().put("browserAllowCookies", value);
    }
    
    public void setAllowBrowserJavaScript(final boolean value) throws JSONException {
        this.getPayloadJSON().put("browserAllowJavaScript", value);
    }
    
    public void setAllowBrowserPopUps(final boolean value) throws JSONException {
        this.getPayloadJSON().put("browserAllowPopups", value);
    }
    
    public void setAllowBrowserAllowFraudWarning(final boolean value) throws JSONException {
        this.getPayloadJSON().put("browserAllowFraudWarning", value);
    }
    
    public void setSetRoamingAlwaysOn(final boolean value) throws JSONException {
        this.getPayloadJSON().put("setRoamingAlwaysOn", value);
    }
    
    public void setAllowRoamingData(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowRoamingData", value);
    }
    
    public void setAllowRoamingPush(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowRoamingPush", value);
    }
    
    public void setAllowRoamingSync(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowRoamingSync", value);
    }
    
    public void setAllowRoamingVoiceCall(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowRoamingVoiceCall", value);
    }
    
    public void setAllowInternalStorageEncryption(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowInternalStorageEncryption", value);
    }
    
    public void setAllowExternalStorageEncryption(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowExternalStorageEncryption", value);
    }
    
    public void setAllowNonSecureKeypad(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowOtherKeypads", value);
    }
    
    public void setAllowKnoxAppStore(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowKnoxAppStore", value);
    }
    
    public void setAllowContactsOutside(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowContactsOutside", value);
    }
    
    public void setAllowShareList(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowShareViaList", value);
    }
    
    public void setAllowPlatProtectMonitoring(final boolean value) throws JSONException {
        this.getPayloadJSON().put("enablePlayProtect", value);
    }
    
    public void setAllowUserCreation(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUserCreation", value);
    }
    
    public void setAllowAirView(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowAirView", value);
    }
    
    public void setTimeZone(final String value) throws JSONException {
        if (value.equals("")) {
            this.getPayloadJSON().put("setTimeZone", (Object)"None");
        }
        else {
            this.getPayloadJSON().put("setTimeZone", (Object)value);
        }
    }
    
    public void setAllowAirCommand(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowAirCommand", value);
    }
    
    public void setAllowSFinder(final int value) throws JSONException {
        switch (value) {
            case 0: {
                this.getPayloadJSON().put("allowSFinder", false);
                break;
            }
            case 1: {
                this.getPayloadJSON().put("allowSFinder", true);
                break;
            }
        }
    }
    
    public void setGlobalPermissionPolicyState(final int value) throws JSONException {
        this.getPayloadJSON().put("setGlobalPermissionPolicyState", value);
    }
    
    public void setAllowUserAddAccount(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUserAddAccounts", value);
    }
    
    public void setAllowSVoice(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowSVoice", value);
    }
    
    public void setAllowStopSystemApp(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowStopSystemApp", value);
    }
    
    public void setAllowActivationLock(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowActivationLock", value);
    }
    
    public void setAllowAirplaneMode(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowAirplaneMode", value);
    }
    
    public void setAllowBackgroundProcessLimit(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowBackgroundProcessLimit", value);
    }
    
    public void setAllowSBeam(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowSBeam", value);
    }
    
    public void setAllowAndroidBeam(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowAndroidBeam", value);
    }
    
    public void setAllowWifiDirect(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowWifiDirect", value);
    }
    
    public void setAllowedWhitelistedWifi(final boolean value) throws JSONException {
        this.getPayloadJSON().put("activateWifiWhitelist", value);
    }
    
    public void setHeadphoneState(final boolean value) throws JSONException {
        this.getPayloadJSON().put("setHeadphoneState", value);
    }
    
    public void setAllowSmartClipMode(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowSmartClipMode", value);
    }
    
    public void setAllowClipboardShare(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowClipboardShare", value);
    }
    
    public void setAllowFirmwareRecovery(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowFirmwareRecovery", value);
    }
    
    public void setAllowSDCardMove(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowSDCardMove", value);
    }
    
    public void setAllowSafeMode(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowSafeMode", value);
    }
    
    public void setHomeKeyState(final boolean value) throws JSONException {
        this.getPayloadJSON().put("setHomeKeyState", value);
    }
    
    public void setLockScreenState(final boolean value) throws JSONException {
        this.getPayloadJSON().put("setLockScreenState", value);
    }
    
    public void setAllowDeveloperMode(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowDeveloperMode", value);
    }
    
    public void setAllowKillingActivitiesOnLeave(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowKillingActivitiesOnLeave", value);
    }
    
    public void setAllowLockScreenView(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowLockScreenView", value);
    }
    
    public void setAllowUserMobileDataLimit(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUserMobileDataLimit", value);
    }
    
    public void setDateTimeChangeEnabled(final boolean value) throws JSONException {
        this.getPayloadJSON().put("setDateTimeChangeEnabled", value);
    }
    
    public void setAllowGoogleAccountsAutoSync(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowGoogleAccountsAutoSync", value);
    }
    
    public void setApplicationNotificationMode(final boolean value) throws JSONException {
        this.getPayloadJSON().put("setApplicationNotificationMode", value);
    }
    
    public void setAllowAudioRecord(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowAudioRecord", value);
    }
    
    public void setAllowVideoRecord(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowVideoRecord", value);
    }
    
    public void setAllowUsbHostStorage(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUsbHostStorage", value);
    }
    
    public void setAllowNetworkTime(final boolean value) throws JSONException {
        this.getPayloadJSON().put("setAutomaticTime", value);
    }
    
    public void setAllowEmail(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowEmail", value);
    }
    
    public void setAllowGmail(final int value) throws JSONException {
        switch (value) {
            case 0: {
                this.getPayloadJSON().put("allowGoogleMail", false);
                break;
            }
            case 1: {
                this.getPayloadJSON().put("allowGoogleMail", true);
                break;
            }
        }
    }
    
    public void setAllowGoogleMaps(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowGoogleMaps", value);
    }
    
    public void setAllowBtDiscoverable(final boolean value) throws JSONException {
        this.getPayloadJSON().put("setDiscoverableState", value);
    }
    
    public void setAllowBtPairing(final boolean value) throws JSONException {
        this.getPayloadJSON().put("setPairingState", value);
    }
    
    public void setAllowBtOutgoingCalls(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowOutgoingCallsViaBluetooth", value);
    }
    
    public void setAllowBtPcConnection(final boolean value) throws JSONException {
        this.getPayloadJSON().put("setDesktopConnectivityState", value);
    }
    
    public void setAllowBtDataTransfer(final boolean value) throws JSONException {
        this.getPayloadJSON().put("setAllowBluetoothDataTransfer", value);
    }
    
    public void setAllowEmergencyCalls(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowEmergencyCalls", value);
    }
    
    public void setAllowSMS(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowSms", value);
    }
    
    public void setAllowIncommingSMS(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowIncomingSms", value);
    }
    
    public void setAllowOutgoingSMS(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowOutgoingSms", value);
    }
    
    public void setAllowMMS(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowMms", value);
    }
    
    public void setAllowIncommingMMS(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowIncomingMms", value);
    }
    
    public void setAllowOutgoingMMS(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowOutgoingMms", value);
    }
    
    public void setAllowCall(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowCall", value);
    }
    
    public void setAllowIncommingCall(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowIncomingCall", value);
    }
    
    public void setAllowOutgoingCall(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowOutgoingCall", value);
    }
    
    public void setAmbientDisplay(final Boolean ambientDisplay) throws JSONException {
        this.getPayloadJSON().put("allowAmbientDisplay", (Object)ambientDisplay);
    }
    
    public void setBrightnessConfig(final Boolean brightnessConfig) throws JSONException {
        this.getPayloadJSON().put("allowConfigBrightness", (Object)brightnessConfig);
    }
    
    public void setAdaptiveBrightness(final Boolean adaptiveBrightness) throws JSONException {
        this.getPayloadJSON().put("allowAdaptiveBrightness", (Object)adaptiveBrightness);
    }
    
    public void setBrightness(final int brightnessValue) throws JSONException {
        this.getPayloadJSON().put("brightnessValue", brightnessValue);
    }
    
    public void setAllowScreenTimeoutConfig(final Boolean allowScreenTimeoutConfig) throws JSONException {
        this.getPayloadJSON().put("allowConfigScreenTimeout", (Object)allowScreenTimeoutConfig);
    }
    
    public void setScreenTimeout(final int screenTimeout) throws JSONException {
        this.getPayloadJSON().put("screenTimeoutValue", screenTimeout);
    }
    
    public void setAllowTimeDateSettings(final Boolean allowTimeDateSettings) throws JSONException {
        this.getPayloadJSON().put("allowConfigDateTime", (Object)allowTimeDateSettings);
    }
    
    public void setAllowPrinting(final Boolean allowPrinting) throws JSONException {
        this.getPayloadJSON().put("allowPrinting", (Object)allowPrinting);
    }
    
    public void setAllowKeyguardCamera(final Boolean allowKeyguardCamera) throws JSONException {
        this.getPayloadJSON().put("allowKeyguardCamera", (Object)allowKeyguardCamera);
    }
    
    public void setAllowKeyguardNotifications(final int allowKeyguardNotifications) throws JSONException {
        this.getPayloadJSON().put("allowKeyguardNotifications", allowKeyguardNotifications);
    }
    
    public void setAllowInstallOrModifyCertificates(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowInstallCertificate", value);
    }
    
    public void setAllowAutomateCertificateBasedAuthenticationForManagedApps(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowAutomateCertBasedAuth", value);
    }
    
    public void setAllowDataSaver(final Boolean allowDataSaver) throws JSONException {
        this.getPayloadJSON().put("allowDataSaver", (Object)allowDataSaver);
    }
    
    public void setAllowSecuredWifi(final int allowSecuredWifi) throws JSONException {
        this.getPayloadJSON().put("allowSecuredWifi", allowSecuredWifi);
    }
    
    public void setAllowAutoFillService(final Boolean allowAutoFillService) throws JSONException {
        this.getPayloadJSON().put("autoFillService", (Object)allowAutoFillService);
    }
}
