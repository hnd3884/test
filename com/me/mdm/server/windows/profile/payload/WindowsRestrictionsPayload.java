package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.server.windows.profile.payload.transform.DO2WindowsRestrictionsPayload;

public class WindowsRestrictionsPayload extends WindowsPayload
{
    public WindowsRestrictionsPayload() {
    }
    
    public WindowsRestrictionsPayload(final String commandUUID) {
    }
    
    public void setDeviceEncryptionEnabled(final String intDeviceEncryptionEnabled) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/Registry/HKLM/Software/Microsoft/Provisioning/DeviceEncryption/Enabled", intDeviceEncryptionEnabled.toString(), "int"));
    }
    
    public void setDisableSDCard(final String storageCard) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/Storage/Disable", storageCard, "bool"));
    }
    
    public void setRemoveProfilePayload(final String storageCard) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/Storage/Disable", storageCard, "bool"));
    }
    
    public void allowIdleReturnWithoutPassword(final String idleReturnState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/DeviceLock/AllowIdleReturnWithoutPassword", idleReturnState, "int"));
    }
    
    public void allowWiFi(final String wifiState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/WiFi/AllowWiFi", wifiState, "int"));
    }
    
    public void allowInternetSharing(final String internetSharingState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/WiFi/AllowInternetSharing", internetSharingState, "int"));
    }
    
    public void allowAutoConnectToWiFiHotspots(final String autoConnectState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/WiFi/AllowAutoConnectToWiFiSenseHotspots", autoConnectState, "int"));
    }
    
    public void allowManualWiFiConfiguration(final String manualWiFiConfigState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/WiFi/AllowManualWiFiConfiguration", manualWiFiConfigState, "int"));
    }
    
    public void allowNFC(final String nfcState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Connectivity/AllowNFC", nfcState, "int"));
    }
    
    public void allowBluetooth(final String bluetoothState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Connectivity/AllowBluetooth", bluetoothState, "int"));
    }
    
    public void allowVPNRoamingOverCellularNetwork(String vpnRoamingState) {
        if (vpnRoamingState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            vpnRoamingState = "1";
        }
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Connectivity/AllowVPNRoamingOverCellular", vpnRoamingState, "int"));
    }
    
    public void allowVPNOverCellularNetwork(String vpnOverCellularState) {
        if (vpnOverCellularState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            vpnOverCellularState = "1";
        }
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Connectivity/AllowVPNOverCellular", vpnOverCellularState, "int"));
    }
    
    public void allowUSBConnection(final String usbState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Connectivity/AllowUSBConnection", usbState, "int"));
    }
    
    public void allowCellularDataRoaming(final String cellularRoamingState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Connectivity/AllowCellularDataRoaming", cellularRoamingState, "int"));
    }
    
    public void allowStorageCard(final String storageCardState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/System/AllowStorageCard", storageCardState, "int"));
    }
    
    public void allowTelemetry(final String telemetryState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/System/AllowTelemetry", telemetryState, "int"));
    }
    
    public void allowLocation(final String locationState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/System/AllowLocation", locationState, "int"));
    }
    
    public void allowUserToResetPhone(final String userResetState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/System/AllowUserToResetPhone", userResetState, "int"));
    }
    
    public void allowCopyPaste(final String copyPasteState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Experience/AllowCopyPaste", copyPasteState, "int"));
    }
    
    public void allowScreenCapture(final String screenCaptureState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Experience/AllowScreenCapture", screenCaptureState, "int"));
    }
    
    public void allowVoiceRecording(final String voiceRecordingState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Experience/AllowVoiceRecording", voiceRecordingState, "int"));
    }
    
    public void allowSaveAsOfOfficeFiles(final String officeFileSaveAsState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Experience/AllowSaveAsOfOfficeFiles", officeFileSaveAsState, "int"));
    }
    
    public void allowSharingOfOfficeFiles(final String officeFileSharingState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Experience/AllowSharingOfOfficeFiles", officeFileSharingState, "int"));
    }
    
    public void allowCortana(final String cortanaState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Experience/AllowCortana", cortanaState, "int"));
    }
    
    public void allowSyncMySettings(final String syncMySettingsState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Experience/AllowSyncMySettings", syncMySettingsState, "int"));
    }
    
    public void allowMicrosoftAccountConnection(final String microsoftAccountState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Accounts/AllowMicrosoftAccountConnection", microsoftAccountState, "int"));
    }
    
    public void allowAddingNonMSAccountsManually(final String nonMSAccountState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Accounts/AllowAddingNonMicrosoftAccountsManually", nonMSAccountState, "int"));
    }
    
    public void allowManualRootCertificateInstallation(final String rootCertInstallationState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Security/AllowManualRootCertificateInstallation", rootCertInstallationState, "int"));
    }
    
    public void requireDeviceEncryption(final String deviceEncryptionReqState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Security/RequireDeviceEncryption", deviceEncryptionReqState, "int"));
    }
    
    public void allowStore(final String storeAllowedState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/ApplicationManagement/AllowStore", storeAllowedState, "int"));
    }
    
    public void allowDeveloperUnlock(String developerUnlockAllowedState) {
        if (developerUnlockAllowedState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            developerUnlockAllowedState = "1";
        }
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/ApplicationManagement/AllowDeveloperUnlock", developerUnlockAllowedState, "int"));
    }
    
    public void allowBrowser(final String browserAllowedState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Browser/AllowBrowser", browserAllowedState, "int"));
    }
    
    public void allowCamera(final String cameraAllowedState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Camera/AllowCamera", cameraAllowedState, "int"));
    }
    
    public void allowSearchToUseLocation(final String locationAvailableToSearchState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Search/AllowSearchToUseLocation", locationAvailableToSearchState, "int"));
    }
    
    public void setSafeSearchPermissions(final String safeSearchState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Search/SafeSearchPermissions", safeSearchState, "int"));
    }
    
    public void allowStoringImagesFromVisionSearch(final String allowStoringImagesState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/Search/AllowStoringImagesFromVisionSearch", allowStoringImagesState, "int"));
    }
    
    public void allowActionCenterNotifications(final String actionCenterNotifState) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/PolicyManager/My/AboveLock/AllowActionCenterNotifications", actionCenterNotifState, "int"));
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificInstallPayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getReplacePayloadCommand());
        winConfigPayload.setNonAtomicPayloadContent(this.getNonAtomicDeletePayloadCommand());
        return winConfigPayload;
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificRemovePayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getReplacePayloadCommand());
        winConfigPayload.setNonAtomicPayloadContent(this.getNonAtomicDeletePayloadCommand());
        return winConfigPayload;
    }
    
    protected void addRequestItemToPayload(final String locationUri, final String value, final String type) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(locationUri, value, type));
    }
    
    protected void addTargetItemToNonAtomicDeletePayload(final String locationUri) {
        this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(locationUri));
    }
}
