package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.server.windows.profile.payload.transform.DO2WindowsRestrictionsPayload;

public class WinMobileRestrictionsPayload extends WindowsRestrictionsPayload
{
    protected String baseLocURI;
    protected String baseLocURIUserScope;
    
    public WinMobileRestrictionsPayload() {
        this.baseLocURI = "./Vendor/MSFT/Policy/Config/";
        this.baseLocURIUserScope = "./User/Vendor/MSFT/Policy/Config/";
    }
    
    @Override
    public void allowIdleReturnWithoutPassword(final String idleReturnState) {
        this.addRequestItemToPayload(this.baseLocURI + "DeviceLock/AllowIdleReturnWithoutPassword", idleReturnState, "int");
    }
    
    @Override
    public void allowWiFi(final String wifiState) {
        this.addRequestItemToPayload(this.baseLocURI + "WiFi/AllowWiFi", wifiState, "int");
    }
    
    @Override
    public void allowInternetSharing(final String internetSharingState) {
        this.addRequestItemToPayload(this.baseLocURI + "WiFi/AllowInternetSharing", internetSharingState, "int");
    }
    
    @Override
    public void allowAutoConnectToWiFiHotspots(final String autoConnectState) {
        this.addRequestItemToPayload(this.baseLocURI + "WiFi/AllowAutoConnectToWiFiSenseHotspots", autoConnectState, "int");
    }
    
    @Override
    public void allowManualWiFiConfiguration(final String manualWiFiConfigState) {
        this.addRequestItemToPayload(this.baseLocURI + "WiFi/AllowManualWiFiConfiguration", manualWiFiConfigState, "int");
    }
    
    @Override
    public void allowNFC(final String nfcState) {
        this.addRequestItemToPayload(this.baseLocURI + "Connectivity/AllowNFC", nfcState, "int");
    }
    
    @Override
    public void allowBluetooth(final String bluetoothState) {
        this.addRequestItemToPayload(this.baseLocURI + "Connectivity/AllowBluetooth", bluetoothState, "int");
    }
    
    @Override
    public void allowVPNRoamingOverCellularNetwork(final String vpnRoamingState) {
        if (!vpnRoamingState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Connectivity/AllowVPNRoamingOverCellular", vpnRoamingState, "int");
        }
    }
    
    @Override
    public void allowVPNOverCellularNetwork(final String vpnOverCellularState) {
        if (!vpnOverCellularState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Connectivity/AllowVPNOverCellular", vpnOverCellularState, "int");
        }
    }
    
    @Override
    public void allowUSBConnection(final String usbState) {
        this.addRequestItemToPayload(this.baseLocURI + "Connectivity/AllowUSBConnection", usbState, "int");
    }
    
    @Override
    public void allowCellularDataRoaming(final String cellularRoamingState) {
        this.addRequestItemToPayload(this.baseLocURI + "Connectivity/AllowCellularDataRoaming", cellularRoamingState, "int");
    }
    
    @Override
    public void allowStorageCard(final String storageCardState) {
        this.addRequestItemToPayload(this.baseLocURI + "System/AllowStorageCard", storageCardState, "int");
    }
    
    @Override
    public void allowTelemetry(final String telemetryState) {
        this.addRequestItemToPayload(this.baseLocURI + "System/AllowTelemetry", telemetryState, "int");
    }
    
    @Override
    public void allowLocation(final String locationState) {
        this.addRequestItemToPayload(this.baseLocURI + "System/AllowLocation", locationState, "int");
    }
    
    @Override
    public void allowUserToResetPhone(final String userResetState) {
        this.addRequestItemToPayload(this.baseLocURI + "System/AllowUserToResetPhone", userResetState, "int");
    }
    
    @Override
    public void allowCopyPaste(final String copyPasteState) {
        this.addRequestItemToPayload(this.baseLocURI + "Experience/AllowCopyPaste", copyPasteState, "int");
    }
    
    @Override
    public void allowScreenCapture(final String screenCaptureState) {
        this.addRequestItemToPayload(this.baseLocURI + "Experience/AllowScreenCapture", screenCaptureState, "int");
    }
    
    @Override
    public void allowVoiceRecording(final String voiceRecordingState) {
        this.addRequestItemToPayload(this.baseLocURI + "Experience/AllowVoiceRecording", voiceRecordingState, "int");
    }
    
    @Override
    public void allowCortana(final String cortanaState) {
        this.addRequestItemToPayload(this.baseLocURI + "Experience/AllowCortana", cortanaState, "int");
    }
    
    @Override
    public void allowSyncMySettings(final String syncMySettingsState) {
        this.addRequestItemToPayload(this.baseLocURI + "Experience/AllowSyncMySettings", syncMySettingsState, "int");
    }
    
    @Override
    public void allowMicrosoftAccountConnection(final String microsoftAccountState) {
        this.addRequestItemToPayload(this.baseLocURI + "Accounts/AllowMicrosoftAccountConnection", microsoftAccountState, "int");
    }
    
    @Override
    public void allowAddingNonMSAccountsManually(final String nonMSAccountState) {
        this.addRequestItemToPayload(this.baseLocURI + "Accounts/AllowAddingNonMicrosoftAccountsManually", nonMSAccountState, "int");
    }
    
    @Override
    public void allowManualRootCertificateInstallation(final String rootCertInstallationState) {
        this.addRequestItemToPayload(this.baseLocURI + "Security/AllowManualRootCertificateInstallation", rootCertInstallationState, "int");
    }
    
    @Override
    public void requireDeviceEncryption(final String deviceEncryptionReqState) {
        this.addRequestItemToPayload(this.baseLocURI + "Security/RequireDeviceEncryption", deviceEncryptionReqState, "int");
    }
    
    @Override
    public void allowStore(final String storeAllowedState) {
        this.addRequestItemToPayload(this.baseLocURI + "ApplicationManagement/AllowStore", storeAllowedState, "int");
    }
    
    @Override
    public void allowDeveloperUnlock(final String developerUnlockAllowedState) {
        if (!developerUnlockAllowedState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "ApplicationManagement/AllowDeveloperUnlock", developerUnlockAllowedState, "int");
        }
    }
    
    @Override
    public void allowBrowser(final String browserAllowedState) {
        this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowBrowser", browserAllowedState, "int");
    }
    
    @Override
    public void allowCamera(final String cameraAllowedState) {
        this.addRequestItemToPayload(this.baseLocURI + "Camera/AllowCamera", cameraAllowedState, "int");
    }
    
    @Override
    public void allowSearchToUseLocation(final String locationAvailableToSearchState) {
        this.addRequestItemToPayload(this.baseLocURI + "Search/AllowSearchToUseLocation", locationAvailableToSearchState, "int");
    }
    
    @Override
    public void setSafeSearchPermissions(final String safeSearchState) {
        this.addRequestItemToPayload(this.baseLocURI + "Search/SafeSearchPermissions", safeSearchState, "int");
    }
    
    @Override
    public void allowActionCenterNotifications(final String actionCenterNotifState) {
        this.addRequestItemToPayload(this.baseLocURI + "AboveLock/AllowActionCenterNotifications", actionCenterNotifState, "int");
    }
    
    public void allowToast(final String allowToast) {
        this.addRequestItemToPayload(this.baseLocURI + "AboveLock/AllowToasts", allowToast, "int");
    }
    
    public void restrictAppDataToSystemVolume(final String appDataToSystemVolume) {
        this.addRequestItemToPayload(this.baseLocURI + "ApplicationManagement/RestrictAppDataToSystemVolume", appDataToSystemVolume, "int");
    }
    
    public void restrictAppInstallToSystemVolume(final String appInstallToSystemVolume) {
        this.addRequestItemToPayload(this.baseLocURI + "ApplicationManagement/RestrictAppToSystemVolume", appInstallToSystemVolume, "int");
    }
    
    public void allowTrustedApps(final String trustedAppsSetting) {
        this.addRequestItemToPayload(this.baseLocURI + "ApplicationManagement/AllowAllTrustedApps", trustedAppsSetting, "int");
    }
    
    public void allowAppStoreAutoUpdate(final String allowAppStoreAutoUpdate) {
        if (!allowAppStoreAutoUpdate.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "ApplicationManagement/AllowAppStoreAutoUpdate", allowAppStoreAutoUpdate, "int");
        }
    }
    
    public void requirePrivateStoreOnly(final String requirePrivateStoreOnly) {
        this.addRequestItemToPayload(this.baseLocURIUserScope + "ApplicationManagement/RequirePrivateStoreOnly", requirePrivateStoreOnly, "int");
    }
    
    public void allowBluetoothAdvertising(final String blueToothAdvertising) {
        this.addRequestItemToPayload(this.baseLocURI + "Bluetooth/AllowAdvertising", blueToothAdvertising, "int");
    }
    
    public void allowBluetoothDiscoverable(final String blueToothDiscovery) {
        this.addRequestItemToPayload(this.baseLocURI + "Bluetooth/AllowDiscoverableMode", blueToothDiscovery, "int");
    }
    
    public void allowBluetoothPairing(final String bluetoothPairing) {
        this.addRequestItemToPayload(this.baseLocURI + "Bluetooth/AllowPrepairing", bluetoothPairing, "int");
    }
    
    public void allowBrowserCookies(final String allowCookies) {
        if (!allowCookies.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowCookies", allowCookies, "int");
        }
    }
    
    public void allowDoNotTrackRequests(final String donotTrackRequest) {
        if (!donotTrackRequest.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowDoNotTrack", donotTrackRequest, "int");
        }
    }
    
    public void allowInPrivateBrowsing(final String inPrivateBrowsing) {
        this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowInPrivate", inPrivateBrowsing, "int");
    }
    
    public void allowPasswordManager(final String passwordMgr) {
        if (!passwordMgr.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowPasswordManager", passwordMgr, "int");
        }
    }
    
    public void allowSearchSuggestionsInAddressBar(final String searchSuggestion) {
        if (!searchSuggestion.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowSearchSuggestionsinAddressBar", searchSuggestion, "int");
        }
    }
    
    public void allowSmartScreen(final String smartScreen) {
        if (!smartScreen.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowSmartScreen", smartScreen, "int");
        }
    }
    
    public void preventSmartScreenPromptOverrideForSites(final String preventSmartScreenPromptOverride) {
        if (!preventSmartScreenPromptOverride.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Browser/PreventSmartScreenPromptOverride", preventSmartScreenPromptOverride, "int");
        }
    }
    
    public void preventSmartScreenPromptOverrideForFiles(final String preventSmartScreenPromptOverride) {
        if (!preventSmartScreenPromptOverride.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Browser/PreventSmartScreenPromptOverrideForFiles", preventSmartScreenPromptOverride, "int");
        }
    }
    
    public void allowExtensions(final String browserExtensionState) {
    }
    
    public void preventAboutFlagsAccess(final String browserAboutFlagsState) {
    }
    
    public void runFlashAutomatically(final String browserRunFlashAutomaticallyState) {
    }
    
    public void allowDeveloperTools(final String browserDeveloperToolsState) {
    }
    
    public void allowFlash(final String browserFlashState) {
    }
    
    public void allowPopups(final String browserPopupsState) {
    }
    
    public void allowAutofill(final String browserAutofillState) {
    }
    
    public void allowAddressBarDropdown(final String browserAddressBarDropdownState) {
    }
    
    public void clearBrowsingDataOnExit(final String clearBrowsingDataOnExit) {
    }
    
    public void allowCellularData(final String cellularData) {
        this.addRequestItemToPayload(this.baseLocURI + "Connectivity/AllowCellularData", cellularData, "int");
    }
    
    public void allowVpn(final String allowVpn) {
        this.addRequestItemToPayload(this.baseLocURI + "Settings/AllowVPN", allowVpn, "int");
    }
    
    public void allowFIPSAlgorithm(final String allowFIPS) {
        this.addRequestItemToPayload(this.baseLocURI + "Cryptography/AllowFipsAlgorithmPolicy", allowFIPS, "int");
    }
    
    public void allowMSFeedBackNotifications(final String msFeedbackNotif) {
        this.addRequestItemToPayload(this.baseLocURI + "Experience/DoNotShowFeedbackNotifications", msFeedbackNotif, "int");
    }
    
    public void allowAddProvPackage(final String allowAddProvPackage) {
        this.addRequestItemToPayload(this.baseLocURI + "Security/AllowAddProvisioningPackage", allowAddProvPackage, "int");
    }
    
    public void allowRemoveProvPackage(final String allowRemoveProvPackage) {
        this.addRequestItemToPayload(this.baseLocURI + "Security/AllowRemoveProvisioningPackage", allowRemoveProvPackage, "int");
    }
    
    public void allowAntiTheftMode(final String allowAntiTheftMode) {
        this.addRequestItemToPayload(this.baseLocURI + "Security/AntiTheftMode", allowAntiTheftMode, "int");
    }
    
    public void allowDateTimeSetting(final String allowDateTimeSetting) {
        this.addRequestItemToPayload(this.baseLocURI + "Settings/AllowDateTime", allowDateTimeSetting, "int");
    }
    
    public void allowEditDeviceName(final String allowEditDevicName) {
        this.addRequestItemToPayload(this.baseLocURI + "Settings/AllowEditDeviceName", allowEditDevicName, "int");
    }
    
    public void deletePasswordManager() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Browser/AllowPasswordManager");
    }
    
    public void deleteCookies() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Browser/AllowCookies");
    }
    
    public void deleteDonotTrackRequest() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Browser/AllowDoNotTrack");
    }
    
    public void deleteSearchSuggestionsInAddressBar() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Browser/AllowSearchSuggestionsinAddressBar");
    }
    
    public void deleteSmartScreen() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Browser/AllowSmartScreen");
    }
    
    public void deleteSmartScreenPromptOverride() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Browser/PreventSmartScreenPromptOverride");
    }
    
    public void deleteSmartScreenPromptOverrideForFiles() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Browser/PreventSmartScreenPromptOverrideForFiles");
    }
    
    public void deleteVpnOverCellularData() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Connectivity/AllowVPNOverCellular");
    }
    
    public void deleteVpnOverRoamingCellularData() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Connectivity/AllowVPNRoamingOverCellular");
    }
    
    public void deleteDeveloperUnlock() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "ApplicationManagement/AllowDeveloperUnlock");
    }
    
    public void deleteAllowAutoAppStoreAutoUpdate() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "ApplicationManagement/AllowAppStoreAutoUpdate");
    }
    
    public void deleteClearBrowsingDataonExit() {
    }
    
    public void deleteAllowPopups() {
    }
    
    public void deleteAllowAutofill() {
    }
    
    public void deleteAllowFlash() {
    }
    
    public void addBrowserHomePage(final String browserHomePage) {
    }
    
    public void deleteBrowserHomePage() {
    }
}
