package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.server.windows.profile.payload.transform.DO2WindowsRestrictionsPayload;

public class WinDesktopRestrictionsPayload extends WinMobileRestrictionsPayload
{
    @Override
    public void requireDeviceEncryption(final String deviceEncryptionReqState) {
    }
    
    @Override
    public void allowScreenCapture(final String screenCaptureState) {
    }
    
    @Override
    public void allowStore(final String storeAllowedState) {
    }
    
    @Override
    public void allowUSBConnection(final String usbState) {
    }
    
    @Override
    public void allowEditDeviceName(final String allowEditDevicName) {
    }
    
    @Override
    public void allowCellularData(final String cellularData) {
    }
    
    @Override
    public void allowCopyPaste(final String copyPasteState) {
    }
    
    @Override
    public void allowManualRootCertificateInstallation(final String rootCertInstallationState) {
    }
    
    @Override
    public void allowActionCenterNotifications(final String actionCenterNotifState) {
    }
    
    @Override
    public void allowAntiTheftMode(final String allowAntiTheftMode) {
    }
    
    @Override
    public void allowVoiceRecording(final String voiceRecordingState) {
    }
    
    @Override
    public void setSafeSearchPermissions(final String safeSearchState) {
    }
    
    @Override
    public void allowBrowser(final String browserAllowedState) {
    }
    
    @Override
    public void allowNFC(final String nfcState) {
    }
    
    @Override
    public void requirePrivateStoreOnly(final String requirePrivateStoreOnly) {
    }
    
    @Override
    public void allowExtensions(final String browserExtensionState) {
        this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowExtensions", browserExtensionState, "int");
    }
    
    @Override
    public void clearBrowsingDataOnExit(final String clearBrowsingDataOnExit) {
        if (!clearBrowsingDataOnExit.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Browser/ClearBrowsingDataOnExit", clearBrowsingDataOnExit, "int");
        }
    }
    
    @Override
    public void preventAboutFlagsAccess(final String browserAboutFlagsState) {
        this.addRequestItemToPayload(this.baseLocURI + "Browser/PreventAccessToAboutFlagsInMicrosoftEdge", browserAboutFlagsState, "int");
    }
    
    @Override
    public void runFlashAutomatically(final String browserRunFlashAutomaticallyState) {
        this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowFlashClickToRun", browserRunFlashAutomaticallyState, "int");
    }
    
    @Override
    public void allowDeveloperTools(final String browserDeveloperToolsState) {
        this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowDeveloperTools", browserDeveloperToolsState, "int");
    }
    
    @Override
    public void allowFlash(final String browserFlashState) {
        if (!browserFlashState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowFlash", browserFlashState, "int");
        }
    }
    
    @Override
    public void allowPopups(final String browserPopupsState) {
        if (!browserPopupsState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowPopups", browserPopupsState, "int");
        }
    }
    
    @Override
    public void allowAutofill(final String browserAutofillState) {
        if (!browserAutofillState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowAutofill", browserAutofillState, "int");
        }
    }
    
    @Override
    public void allowAddressBarDropdown(final String browserAddressBarDropdownState) {
        if (!browserAddressBarDropdownState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            this.addRequestItemToPayload(this.baseLocURI + "Browser/AllowAddressBarDropdown", browserAddressBarDropdownState, "int");
        }
    }
    
    @Override
    public void deleteAllowPopups() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Browser/AllowPopups");
    }
    
    @Override
    public void deleteAllowAutofill() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Browser/AllowAutofill");
    }
    
    @Override
    public void deleteAllowFlash() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Browser/AllowFlash");
    }
    
    @Override
    public void deleteClearBrowsingDataonExit() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Browser/ClearBrowsingDataOnExit");
    }
    
    @Override
    public void addBrowserHomePage(final String browserHomePage) {
        if (browserHomePage != null) {
            this.addRequestItemToPayload(this.baseLocURI + "Browser/HomePages", browserHomePage, "chr");
        }
    }
    
    @Override
    public void deleteBrowserHomePage() {
        this.addTargetItemToNonAtomicDeletePayload(this.baseLocURI + "Browser/HomePages");
    }
}
