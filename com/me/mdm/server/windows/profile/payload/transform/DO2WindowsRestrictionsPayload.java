package com.me.mdm.server.windows.profile.payload.transform;

import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.me.mdm.server.windows.profile.payload.WinDesktopRestrictionsPayload;
import com.me.mdm.server.windows.profile.payload.WinMobileRestrictionsPayload;
import com.me.mdm.server.windows.profile.payload.WindowsRestrictionsPayload;
import com.me.mdm.server.windows.profile.payload.WindowsPayload;
import com.adventnet.persistence.DataObject;

public class DO2WindowsRestrictionsPayload extends DO2WindowsPayload
{
    public static String USER_CONFIG_VALUE;
    private static Boolean nonAtomicDeletePayloadRequired;
    String disableSDCard;
    String deviceEncryptionEnabled;
    String bluetoothState;
    String storageCardState;
    String allowIdleReturnState;
    String copyPasteState;
    String screenCaptureState;
    String locationState;
    String userResetState;
    String internetSharingState;
    String manualWiFiConfigState;
    String usbState;
    String nfcState;
    String voiceRecordingState;
    String storeAllowedState;
    String browserAllowedState;
    String cameraState;
    String officeFileSaveAsState;
    String officeFileSharingState;
    String actionCenterNotifState;
    String locationAvailableToSearchState;
    String safeSearchState;
    String allowStoringImagesState;
    String syncMySettingsState;
    String vpnOverCellularState;
    String vpnRoamingOverCellularState;
    String cortanaState;
    String autoConnectState;
    String hotspotReportingState;
    String cellularRoamingState;
    String nonMSAccountState;
    String deviceEncryptionReqState;
    String wifiState;
    String microsoftAccountState;
    String rootCertInstallationState;
    String developerUnlockState;
    String telemetryState;
    String toastAllowedState;
    String appDataToSystemVolumeState;
    String restrictAppInstallToSystemVolume;
    String trustedAppsState;
    String allowappstoreautoupdate;
    String requirePrivateStoreOnly;
    String blueToothAdvertisingState;
    String blueToothDiscoverableState;
    String bluetoothPairingState;
    String browserCookiesState;
    String doNotTrackRequestsState;
    String browserExtensionState;
    String browserAboutFlagsState;
    String browserFlashState;
    String browserRunFlashAutomaticallyState;
    String browserPopupsState;
    String browserAutofillState;
    String browserDeveloperToolsState;
    String clearBrowsingDataOnExit;
    String browserAddressBarDropdownState;
    String inPrivateBrowsingState;
    String passwordMgrState;
    String searchSuggestionInAddressBar;
    String smartScreenState;
    String smartScreenPromptOverrideForSites;
    String smartScreenPromptOverrideForFiles;
    String cellularDataState;
    String vpnSettingState;
    String fipsAllowedState;
    String msFeedbackNotif;
    String addProvPackageState;
    String removeProvPackageState;
    String antiTheftMode;
    String dateTimeSetting;
    String editDeviceNameState;
    String browserHomePage;
    
    public DO2WindowsRestrictionsPayload() {
        this.disableSDCard = null;
        this.deviceEncryptionEnabled = null;
        this.bluetoothState = null;
        this.storageCardState = null;
        this.allowIdleReturnState = null;
        this.copyPasteState = null;
        this.screenCaptureState = null;
        this.locationState = null;
        this.userResetState = null;
        this.internetSharingState = null;
        this.manualWiFiConfigState = null;
        this.usbState = null;
        this.nfcState = null;
        this.voiceRecordingState = null;
        this.storeAllowedState = null;
        this.browserAllowedState = null;
        this.cameraState = null;
        this.officeFileSaveAsState = null;
        this.officeFileSharingState = null;
        this.actionCenterNotifState = null;
        this.locationAvailableToSearchState = null;
        this.safeSearchState = null;
        this.allowStoringImagesState = null;
        this.syncMySettingsState = null;
        this.vpnOverCellularState = null;
        this.vpnRoamingOverCellularState = null;
        this.cortanaState = null;
        this.autoConnectState = null;
        this.hotspotReportingState = null;
        this.cellularRoamingState = null;
        this.nonMSAccountState = null;
        this.deviceEncryptionReqState = null;
        this.wifiState = null;
        this.microsoftAccountState = null;
        this.rootCertInstallationState = null;
        this.developerUnlockState = null;
        this.telemetryState = null;
        this.toastAllowedState = null;
        this.appDataToSystemVolumeState = null;
        this.restrictAppInstallToSystemVolume = null;
        this.trustedAppsState = null;
        this.allowappstoreautoupdate = null;
        this.requirePrivateStoreOnly = null;
        this.blueToothAdvertisingState = null;
        this.blueToothDiscoverableState = null;
        this.bluetoothPairingState = null;
        this.browserCookiesState = null;
        this.doNotTrackRequestsState = null;
        this.browserExtensionState = null;
        this.browserAboutFlagsState = null;
        this.browserFlashState = null;
        this.browserRunFlashAutomaticallyState = null;
        this.browserPopupsState = null;
        this.browserAutofillState = null;
        this.browserDeveloperToolsState = null;
        this.clearBrowsingDataOnExit = null;
        this.browserAddressBarDropdownState = null;
        this.inPrivateBrowsingState = null;
        this.passwordMgrState = null;
        this.searchSuggestionInAddressBar = null;
        this.smartScreenState = null;
        this.smartScreenPromptOverrideForSites = null;
        this.smartScreenPromptOverrideForFiles = null;
        this.cellularDataState = null;
        this.vpnSettingState = null;
        this.fipsAllowedState = null;
        this.msFeedbackNotif = null;
        this.addProvPackageState = null;
        this.removeProvPackageState = null;
        this.antiTheftMode = null;
        this.dateTimeSetting = null;
        this.editDeviceNameState = null;
        this.browserHomePage = null;
        this.disableSDCard = "false";
        this.wifiState = "1";
        this.internetSharingState = "1";
        this.autoConnectState = "1";
        this.hotspotReportingState = "1";
        this.manualWiFiConfigState = "1";
        this.nfcState = "1";
        this.bluetoothState = "2";
        this.vpnRoamingOverCellularState = "1";
        this.vpnOverCellularState = "1";
        this.storageCardState = "1";
        this.telemetryState = "2";
        this.copyPasteState = "1";
        this.microsoftAccountState = "1";
        this.nonMSAccountState = "1";
        this.rootCertInstallationState = "1";
        this.storeAllowedState = "1";
        this.developerUnlockState = "1";
        this.browserAllowedState = "1";
        this.screenCaptureState = "1";
        this.locationState = "1";
        this.usbState = "1";
        this.cellularRoamingState = "1";
        this.cameraState = "1";
        this.locationAvailableToSearchState = "1";
        this.safeSearchState = "1";
        this.allowStoringImagesState = "1";
        this.voiceRecordingState = "1";
        this.officeFileSaveAsState = "1";
        this.officeFileSharingState = "1";
        this.actionCenterNotifState = "1";
        this.allowIdleReturnState = "1";
        this.cortanaState = "1";
        this.syncMySettingsState = "1";
        this.userResetState = "1";
        this.deviceEncryptionEnabled = "-1";
        this.deviceEncryptionReqState = "-1";
        this.toastAllowedState = "1";
        this.appDataToSystemVolumeState = "0";
        this.restrictAppInstallToSystemVolume = "0";
        this.trustedAppsState = "65535";
        this.allowappstoreautoupdate = "1";
        this.requirePrivateStoreOnly = "0";
        this.blueToothAdvertisingState = "1";
        this.blueToothDiscoverableState = "1";
        this.bluetoothPairingState = "1";
        this.browserCookiesState = "1";
        this.doNotTrackRequestsState = "0";
        this.browserExtensionState = "1";
        this.browserAboutFlagsState = "0";
        this.browserFlashState = "1";
        this.browserRunFlashAutomaticallyState = "1";
        this.browserPopupsState = "0";
        this.browserAutofillState = "1";
        this.browserDeveloperToolsState = "1";
        this.clearBrowsingDataOnExit = "0";
        this.browserAddressBarDropdownState = "1";
        this.inPrivateBrowsingState = "1";
        this.passwordMgrState = "1";
        this.searchSuggestionInAddressBar = "1";
        this.smartScreenState = "1";
        this.smartScreenPromptOverrideForSites = "0";
        this.smartScreenPromptOverrideForFiles = "0";
        this.cellularDataState = "1";
        this.vpnSettingState = "1";
        this.fipsAllowedState = "0";
        this.msFeedbackNotif = "0";
        this.addProvPackageState = "1";
        this.removeProvPackageState = "1";
        this.antiTheftMode = "1";
        this.dateTimeSetting = "1";
        this.editDeviceNameState = "1";
    }
    
    @Override
    public WindowsPayload createPayload(final DataObject dataObject) {
        final WindowsRestrictionsPayload payload = new WindowsRestrictionsPayload();
        final WindowsRestrictionsPayload win8payload = new WindowsRestrictionsPayload();
        final WindowsRestrictionsPayload win81payload = new WindowsRestrictionsPayload();
        WinMobileRestrictionsPayload win10payload = new WinMobileRestrictionsPayload();
        WinDesktopRestrictionsPayload win10DesktopPayload = new WinDesktopRestrictionsPayload();
        try {
            final Iterator iterator = dataObject.getRows("WpRestrictionsPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload.getReplacePayloadCommand().addRequestItem(payload.createTargetItemTagElement("%restriction_payload_xml%"));
                if (Integer.parseInt(row.get("SAFE_SEARCH_PERMISSIONS").toString()) != -1) {
                    win81payload.setSafeSearchPermissions(this.safeSearchState = row.get("SAFE_SEARCH_PERMISSIONS").toString());
                }
                if (row.get("DISABLE_SD_CARD")) {
                    this.disableSDCard = "true";
                    this.storageCardState = "0";
                }
                if (!(boolean)row.get("ALLOW_NFC")) {
                    this.nfcState = "0";
                }
                if (!(boolean)row.get("ALLOW_MICROSOFT_ACCOUNT")) {
                    this.microsoftAccountState = "0";
                }
                if (((String)row.get("ALLOW_DEVELOPER_UNLOCK_NEW")).equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
                    this.developerUnlockState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (!Boolean.valueOf((String)row.get("ALLOW_DEVELOPER_UNLOCK_NEW"))) {
                    this.developerUnlockState = "0";
                }
                if (!(boolean)row.get("ALLOW_WIFI")) {
                    this.wifiState = "0";
                }
                if (!(boolean)row.get("ALLOW_INTERNET_SHARING")) {
                    this.internetSharingState = "0";
                }
                if (!(boolean)row.get("ALLOW_AUTO_WIFI_HOTSPOT")) {
                    this.autoConnectState = "0";
                }
                if (!(boolean)row.get("ALLOW_WIFI_HOTSPOT")) {
                    this.hotspotReportingState = "0";
                }
                if (!(boolean)row.get("ALLOW_MANUAL_WIFI_CONFIG")) {
                    this.manualWiFiConfigState = "0";
                }
                if (!(boolean)row.get("ALLOW_BLUETOOTH")) {
                    this.bluetoothState = "0";
                }
                if (((String)row.get("ALLOW_VPN_OVER_DATA_ROAMING")).equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
                    this.vpnRoamingOverCellularState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (!Boolean.valueOf((String)row.get("ALLOW_VPN_OVER_DATA_ROAMING"))) {
                    this.vpnRoamingOverCellularState = "0";
                }
                if (((String)row.get("ALLOW_VPN_OVER_DATA")).equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
                    this.vpnOverCellularState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (!Boolean.valueOf((String)row.get("ALLOW_VPN_OVER_DATA"))) {
                    this.vpnOverCellularState = "0";
                }
                if (!(boolean)row.get("ALLOW_COPY_PASTE")) {
                    this.copyPasteState = "0";
                }
                if (!(boolean)row.get("ALLOW_ADDING_NON_MICROSOFT")) {
                    this.nonMSAccountState = "0";
                }
                if (!(boolean)row.get("ALLOW_STORE")) {
                    this.storeAllowedState = "0";
                }
                if (!(boolean)row.get("ALLOW_BROWSER")) {
                    this.browserAllowedState = "0";
                }
                if (!(boolean)row.get("ALLOW_SCREEN_CAPTURE")) {
                    this.screenCaptureState = "0";
                }
                if ((int)row.get("LOCATION_SETTING") != 1) {
                    this.locationState = row.get("LOCATION_SETTING").toString();
                }
                if (!(boolean)row.get("ALLOW_USB")) {
                    this.usbState = "0";
                }
                if (!(boolean)row.get("ALLOW_DATA_ROAMING")) {
                    this.cellularRoamingState = "0";
                }
                if (!(boolean)row.get("ALLOW_USE_OF_CAMERA")) {
                    this.cameraState = "0";
                }
                if (!(boolean)row.get("ALLOW_SEARCH_USE_LOCATION")) {
                    this.locationAvailableToSearchState = "0";
                }
                if (!(boolean)row.get("ALLOW_STORING_IMAGE_SEARCH")) {
                    this.allowStoringImagesState = "0";
                }
                if (!(boolean)row.get("ALLOW_VOICE_RECORDING")) {
                    this.voiceRecordingState = "0";
                }
                if (!(boolean)row.get("ALLOW_SAVE_AS_OFFICE_FILES")) {
                    this.officeFileSaveAsState = "0";
                }
                if (!(boolean)row.get("ALLOW_SHARING_OFFICE_FILES")) {
                    this.officeFileSharingState = "0";
                }
                if (!(boolean)row.get("ALLOW_ACTION_NOTIFICATION")) {
                    this.actionCenterNotifState = "0";
                }
                if (!(boolean)row.get("ALLOW_CORTANA")) {
                    this.cortanaState = "0";
                }
                if (!(boolean)row.get("ALLOW_SYNC_MY_SETTINGS")) {
                    this.syncMySettingsState = "0";
                }
                if (!(boolean)row.get("ALLOW_USER_RESET_PHONE")) {
                    this.userResetState = "0";
                }
                if (!(boolean)row.get("ALLOW_ROOT_CERTIFICATE_INSTALL")) {
                    this.rootCertInstallationState = "0";
                }
                if (Integer.parseInt(row.get("ALLOW_TELEMETRY").toString()) != 2) {
                    this.telemetryState = row.get("ALLOW_TELEMETRY").toString();
                }
                if (row.get("ENFORCE_DEVICE_ENCRIPTION")) {
                    this.deviceEncryptionEnabled = "1";
                    this.deviceEncryptionReqState = "1";
                }
                else {
                    this.deviceEncryptionEnabled = "0";
                    this.deviceEncryptionReqState = "0";
                }
                if (!(boolean)row.get("ALLOW_TOAST")) {
                    this.toastAllowedState = "0";
                }
                if (row.get("LIMIT_APPDATA_TO_SYS_VOL")) {
                    this.appDataToSystemVolumeState = "1";
                }
                if (row.get("LIMIT_APPINSTALL_TO_SYS_VOL")) {
                    this.restrictAppInstallToSystemVolume = "1";
                }
                this.trustedAppsState = row.get("ALLOW_ALL_TRUSTED_APPS").toString();
                if (Integer.parseInt(row.get("ALLOW_APPSTORE_AUTO_UPDATE").toString()) == -999) {
                    this.allowappstoreautoupdate = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (Integer.parseInt(row.get("ALLOW_APPSTORE_AUTO_UPDATE").toString()) == 0) {
                    this.allowappstoreautoupdate = "0";
                }
                if (row.get("REQUIRE_PRIVATE_STORE_ONLY")) {
                    this.requirePrivateStoreOnly = "1";
                }
                if (!(boolean)row.get("ALLOW_BLUETOOTH_ADVERTISING")) {
                    this.blueToothAdvertisingState = "0";
                }
                if (!(boolean)row.get("ALLOW_BLUETOOTH_DISCOVERABLE")) {
                    this.blueToothDiscoverableState = "0";
                }
                if (!(boolean)row.get("ALLOW_BLUETOOTH_PREPAIRING")) {
                    this.bluetoothPairingState = "0";
                }
                if (((String)row.get("BROWSER_ALLOW_COOKIES")).equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
                    this.browserCookiesState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else {
                    this.browserCookiesState = (String)row.get("BROWSER_ALLOW_COOKIES");
                }
                if (((String)row.get("BROWSER_ALLOW_DONOT_TRACK")).equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
                    this.doNotTrackRequestsState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (Boolean.valueOf((String)row.get("BROWSER_ALLOW_DONOT_TRACK"))) {
                    this.doNotTrackRequestsState = "1";
                }
                if (!(boolean)row.get("BROWSER_ALLOW_INPRIVATE")) {
                    this.inPrivateBrowsingState = "0";
                }
                if (((String)row.get("BROWSER_ALLOW_PASSMGR")).equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
                    this.passwordMgrState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (!Boolean.valueOf((String)row.get("BROWSER_ALLOW_PASSMGR"))) {
                    this.passwordMgrState = "0";
                }
                if (((String)row.get("BROWSER_ALLOW_SEARCHSUGGEST")).equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
                    this.searchSuggestionInAddressBar = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (!Boolean.valueOf((String)row.get("BROWSER_ALLOW_SEARCHSUGGEST"))) {
                    this.searchSuggestionInAddressBar = "0";
                }
                if (((String)row.get("BROWSER_ALLOW_SMARTSCREEN")).equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
                    this.smartScreenState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (!Boolean.valueOf((String)row.get("BROWSER_ALLOW_SMARTSCREEN"))) {
                    this.smartScreenState = "0";
                }
                if (((String)row.get("BROWSER_SMARTSCREEN_PROMPT")).equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
                    this.smartScreenPromptOverrideForSites = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (!Boolean.valueOf((String)row.get("BROWSER_SMARTSCREEN_PROMPT"))) {
                    this.smartScreenPromptOverrideForSites = "1";
                }
                if (((String)row.get("BROWSER_SMARTSCREEN_FILES")).equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
                    this.smartScreenPromptOverrideForFiles = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (!Boolean.valueOf((String)row.get("BROWSER_SMARTSCREEN_FILES"))) {
                    this.smartScreenPromptOverrideForFiles = "1";
                }
                if (!(boolean)row.get("BROWSER_ALLOW_ADDRESS_BAR_DROPDOWN")) {
                    this.browserAddressBarDropdownState = "0";
                }
                if (!(boolean)row.get("BROWSER_ALLOW_EXTENSIONS")) {
                    this.browserExtensionState = "0";
                }
                if (!(boolean)row.get("BROWSER_ABOUT_FLAGS_ACCESS")) {
                    this.browserAboutFlagsState = "1";
                }
                if (row.get("BROWSER_RUN_FLASH_AUTOMATICALLY")) {
                    this.browserRunFlashAutomaticallyState = "0";
                }
                if (!(boolean)row.get("BROWSER_ALLOW_DEVELOPER_TOOLS")) {
                    this.browserDeveloperToolsState = "0";
                }
                if (Integer.parseInt(row.get("BROWSER_CLEAR_BROWSING_DATA_EXIT").toString()) == -999) {
                    this.clearBrowsingDataOnExit = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (Integer.parseInt(row.get("BROWSER_CLEAR_BROWSING_DATA_EXIT").toString()) == 1) {
                    this.clearBrowsingDataOnExit = "1";
                }
                if (Integer.parseInt(row.get("BROWSER_ALLOW_FLASH").toString()) == -999) {
                    this.browserFlashState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (Integer.parseInt(row.get("BROWSER_ALLOW_FLASH").toString()) == 0) {
                    this.browserFlashState = "0";
                }
                if (Integer.parseInt(row.get("BROWSER_ALLOW_AUTOFILL").toString()) == -999) {
                    this.browserAutofillState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (Integer.parseInt(row.get("BROWSER_ALLOW_AUTOFILL").toString()) == 0) {
                    this.browserAutofillState = "0";
                }
                if (Integer.parseInt(row.get("BROWSER_ALLOW_POPUPS").toString()) == -999) {
                    this.browserPopupsState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                }
                else if (Integer.parseInt(row.get("BROWSER_ALLOW_POPUPS").toString()) == 0) {
                    this.browserPopupsState = "1";
                }
                this.cellularDataState = row.get("ALLOW_CELLULAR_DATA").toString();
                if (!(boolean)row.get("ALLOW_VPN_SETTING")) {
                    this.vpnSettingState = "0";
                }
                if (row.get("ALLOW_FIPS_POLICY")) {
                    this.fipsAllowedState = "1";
                }
                this.msFeedbackNotif = row.get("MS_FEEDBACK_NOTIF").toString();
                if (!(boolean)row.get("ALLOW_ADD_PROV_PACKAGE")) {
                    this.addProvPackageState = "0";
                }
                if (!(boolean)row.get("ALLOW_REMOVE_PROV_PACKAGE")) {
                    this.removeProvPackageState = "0";
                }
                this.antiTheftMode = row.get("ENABLE_ANTI_THEFT_MODE").toString();
                if (!(boolean)row.get("ALLOW_DATE_TIME")) {
                    this.dateTimeSetting = "0";
                }
                if (!(boolean)row.get("ALLOW_EDIT_DEVICE_NAME")) {
                    this.editDeviceNameState = "0";
                }
                final String homePage = (String)row.get("BROWSER_HOME_PAGE");
                if (homePage != null && !homePage.equals("")) {
                    final String[] urls = homePage.split(",");
                    this.browserHomePage = "";
                    for (final String url : urls) {
                        this.browserHomePage = this.browserHomePage + "<" + url + ">";
                    }
                }
                else {
                    this.browserHomePage = null;
                }
                win8payload.setDeviceEncryptionEnabled(this.deviceEncryptionEnabled);
                win81payload.requireDeviceEncryption(this.deviceEncryptionReqState);
                win8payload.setDisableSDCard(this.disableSDCard);
                win81payload.allowStorageCard(this.storageCardState);
                win81payload.allowNFC(this.nfcState);
                win81payload.allowMicrosoftAccountConnection(this.microsoftAccountState);
                win81payload.allowDeveloperUnlock(this.developerUnlockState);
                win81payload.allowWiFi(this.wifiState);
                win81payload.allowInternetSharing(this.internetSharingState);
                win81payload.allowAutoConnectToWiFiHotspots(this.autoConnectState);
                win81payload.allowManualWiFiConfiguration(this.manualWiFiConfigState);
                win81payload.allowBluetooth(this.bluetoothState);
                win81payload.allowVPNRoamingOverCellularNetwork(this.vpnRoamingOverCellularState);
                win81payload.allowVPNOverCellularNetwork(this.vpnOverCellularState);
                win81payload.allowCopyPaste(this.copyPasteState);
                win81payload.allowAddingNonMSAccountsManually(this.nonMSAccountState);
                win81payload.allowStore(this.storeAllowedState);
                win81payload.allowBrowser(this.browserAllowedState);
                win81payload.allowScreenCapture(this.screenCaptureState);
                win81payload.allowLocation(this.locationState);
                win81payload.allowUSBConnection(this.usbState);
                win81payload.allowCellularDataRoaming(this.cellularRoamingState);
                win81payload.allowCamera(this.cameraState);
                win81payload.allowSearchToUseLocation(this.locationAvailableToSearchState);
                win81payload.allowStoringImagesFromVisionSearch(this.allowStoringImagesState);
                win81payload.allowVoiceRecording(this.voiceRecordingState);
                win81payload.allowSaveAsOfOfficeFiles(this.officeFileSaveAsState);
                win81payload.allowSharingOfOfficeFiles(this.officeFileSharingState);
                win81payload.allowActionCenterNotifications(this.actionCenterNotifState);
                win81payload.allowCortana(this.cortanaState);
                win81payload.allowSyncMySettings(this.syncMySettingsState);
                win81payload.allowUserToResetPhone(this.userResetState);
                win81payload.allowManualRootCertificateInstallation(this.rootCertInstallationState);
                win81payload.allowTelemetry(this.telemetryState);
                win10payload = this.setWin10PayloadData(win10payload);
                win10payload = this.setWin10DeletePayloadData(win10payload);
                if (DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired) {
                    payload.getNonAtomicDeletePayloadCommand().addRequestItem(payload.createTargetItemTagElement("%restriction_payload_xml_nonAtomicDelete%"));
                }
                win10DesktopPayload = (WinDesktopRestrictionsPayload)this.setWin10PayloadData(win10DesktopPayload);
                win10DesktopPayload = (WinDesktopRestrictionsPayload)this.setWin10DeletePayloadData(win10DesktopPayload);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while creating windows restriction payload : ", ex);
        }
        this.packOsSpecificPayloadToXML(dataObject, win81payload, "install", "WindowsPhone81");
        this.packOsSpecificPayloadToXML(dataObject, win8payload, "install", "WindowsPhone8");
        this.packOsSpecificPayloadToXML(dataObject, win10payload, "install", "Windows10Mobile");
        this.packOsSpecificPayloadToXML(dataObject, win10DesktopPayload, "install", "Windows10Desktop");
        return payload;
    }
    
    @Override
    public WindowsPayload createRemoveProfilePayload(final DataObject dataObject) {
        final WindowsRestrictionsPayload payload = new WindowsRestrictionsPayload();
        final WindowsRestrictionsPayload win8payload = new WindowsRestrictionsPayload();
        final WindowsRestrictionsPayload win81payload = new WindowsRestrictionsPayload();
        WinMobileRestrictionsPayload win10payload = new WinMobileRestrictionsPayload();
        WinDesktopRestrictionsPayload win10DesktopPayload = new WinDesktopRestrictionsPayload();
        try {
            final Iterator iterator = dataObject.getRows("WpRestrictionsPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload.getReplacePayloadCommand().addRequestItem(payload.createTargetItemTagElement("%restriction_payload_xml%"));
                payload.getNonAtomicDeletePayloadCommand().addRequestItem(payload.createTargetItemTagElement("%restriction_payload_xml_nonAtomicDelete%"));
                if (Integer.parseInt(row.get("SAFE_SEARCH_PERMISSIONS").toString()) == 0) {
                    win81payload.setSafeSearchPermissions(this.safeSearchState = "1");
                }
                if (!(boolean)row.get("ALLOW_WIFI")) {
                    this.wifiState = "1";
                }
                if (!(boolean)row.get("ALLOW_INTERNET_SHARING")) {
                    this.internetSharingState = "1";
                }
                if (!(boolean)row.get("ALLOW_AUTO_WIFI_HOTSPOT")) {
                    this.autoConnectState = "1";
                }
                if (!(boolean)row.get("ALLOW_WIFI_HOTSPOT")) {
                    this.hotspotReportingState = "1";
                }
                if (!(boolean)row.get("ALLOW_MANUAL_WIFI_CONFIG")) {
                    this.manualWiFiConfigState = "1";
                }
                if (!(boolean)row.get("ALLOW_NFC")) {
                    this.nfcState = "1";
                }
                if (!(boolean)row.get("ALLOW_BLUETOOTH")) {
                    this.bluetoothState = "2";
                }
                this.vpnRoamingOverCellularState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                this.vpnOverCellularState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                if (!(boolean)row.get("ALLOW_COPY_PASTE")) {
                    this.copyPasteState = "1";
                }
                if (!(boolean)row.get("ALLOW_MICROSOFT_ACCOUNT")) {
                    this.microsoftAccountState = "1";
                }
                if (!(boolean)row.get("ALLOW_ADDING_NON_MICROSOFT")) {
                    this.nonMSAccountState = "1";
                }
                if (!(boolean)row.get("ALLOW_STORE")) {
                    this.storeAllowedState = "1";
                }
                this.developerUnlockState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                if (!(boolean)row.get("ALLOW_BROWSER")) {
                    this.browserAllowedState = "1";
                }
                if (!(boolean)row.get("ALLOW_SCREEN_CAPTURE")) {
                    this.screenCaptureState = "1";
                }
                if ((int)row.get("LOCATION_SETTING") != 1) {
                    this.locationState = "1";
                }
                if (!(boolean)row.get("ALLOW_USB")) {
                    this.usbState = "1";
                }
                if (!(boolean)row.get("ALLOW_DATA_ROAMING")) {
                    this.cellularRoamingState = "1";
                }
                if (!(boolean)row.get("ALLOW_USE_OF_CAMERA")) {
                    this.cameraState = "1";
                }
                if (!(boolean)row.get("ALLOW_SEARCH_USE_LOCATION")) {
                    this.locationAvailableToSearchState = "1";
                }
                if (!(boolean)row.get("ALLOW_STORING_IMAGE_SEARCH")) {
                    this.allowStoringImagesState = "1";
                }
                if (!(boolean)row.get("ALLOW_VOICE_RECORDING")) {
                    this.voiceRecordingState = "1";
                }
                if (!(boolean)row.get("ALLOW_SAVE_AS_OFFICE_FILES")) {
                    this.officeFileSaveAsState = "1";
                }
                if (!(boolean)row.get("ALLOW_SHARING_OFFICE_FILES")) {
                    this.officeFileSharingState = "1";
                }
                if (!(boolean)row.get("ALLOW_ACTION_NOTIFICATION")) {
                    this.actionCenterNotifState = "1";
                }
                if (!(boolean)row.get("ALLOW_CORTANA")) {
                    this.cortanaState = "1";
                }
                if (!(boolean)row.get("ALLOW_SYNC_MY_SETTINGS")) {
                    this.syncMySettingsState = "1";
                }
                if (!(boolean)row.get("ALLOW_USER_RESET_PHONE")) {
                    this.userResetState = "1";
                }
                if (!(boolean)row.get("ALLOW_ROOT_CERTIFICATE_INSTALL")) {
                    this.rootCertInstallationState = "1";
                }
                if (Integer.parseInt(row.get("ALLOW_TELEMETRY").toString()) != 2) {
                    this.telemetryState = "2";
                }
                if (row.get("DISABLE_SD_CARD")) {
                    this.disableSDCard = "false";
                    this.storageCardState = "1";
                }
                if ((boolean)row.get("ENFORCE_DEVICE_ENCRIPTION") || (this.deviceEncryptionEnabled.equals("-1") && this.deviceEncryptionReqState.equals("-1"))) {
                    this.deviceEncryptionEnabled = "0";
                    this.deviceEncryptionReqState = "0";
                }
                if (!(boolean)row.get("ALLOW_TOAST")) {
                    this.toastAllowedState = "1";
                }
                if (row.get("LIMIT_APPDATA_TO_SYS_VOL")) {
                    this.appDataToSystemVolumeState = "0";
                }
                if (row.get("LIMIT_APPINSTALL_TO_SYS_VOL")) {
                    this.restrictAppInstallToSystemVolume = "0";
                }
                this.trustedAppsState = row.get("ALLOW_ALL_TRUSTED_APPS").toString();
                this.allowappstoreautoupdate = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                if (row.get("REQUIRE_PRIVATE_STORE_ONLY")) {
                    this.requirePrivateStoreOnly = "0";
                }
                if (!(boolean)row.get("ALLOW_BLUETOOTH_ADVERTISING")) {
                    this.blueToothAdvertisingState = "1";
                }
                if (!(boolean)row.get("ALLOW_BLUETOOTH_DISCOVERABLE")) {
                    this.blueToothDiscoverableState = "1";
                }
                if (!(boolean)row.get("ALLOW_BLUETOOTH_PREPAIRING")) {
                    this.bluetoothPairingState = "1";
                }
                this.browserCookiesState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                this.doNotTrackRequestsState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                this.searchSuggestionInAddressBar = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                this.smartScreenState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                this.smartScreenPromptOverrideForFiles = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                this.smartScreenPromptOverrideForSites = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                this.passwordMgrState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                this.browserFlashState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                this.browserPopupsState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                this.browserAutofillState = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                this.clearBrowsingDataOnExit = DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE;
                if (!(boolean)row.get("BROWSER_ALLOW_ADDRESS_BAR_DROPDOWN")) {
                    this.browserAddressBarDropdownState = "1";
                }
                if (!(boolean)row.get("BROWSER_ALLOW_EXTENSIONS")) {
                    this.browserExtensionState = "1";
                }
                if (!(boolean)row.get("BROWSER_ABOUT_FLAGS_ACCESS")) {
                    this.browserAboutFlagsState = "0";
                }
                if (row.get("BROWSER_RUN_FLASH_AUTOMATICALLY")) {
                    this.browserRunFlashAutomaticallyState = "1";
                }
                if (!(boolean)row.get("BROWSER_ALLOW_DEVELOPER_TOOLS")) {
                    this.browserDeveloperToolsState = "1";
                }
                if (!(boolean)row.get("BROWSER_ALLOW_INPRIVATE")) {
                    this.inPrivateBrowsingState = "1";
                }
                this.cellularDataState = "1";
                if (!(boolean)row.get("ALLOW_VPN_SETTING")) {
                    this.vpnSettingState = "1";
                }
                if (row.get("ALLOW_FIPS_POLICY")) {
                    this.fipsAllowedState = "0";
                }
                this.msFeedbackNotif = "0";
                if (!(boolean)row.get("ALLOW_ADD_PROV_PACKAGE")) {
                    this.addProvPackageState = "1";
                }
                if (!(boolean)row.get("ALLOW_REMOVE_PROV_PACKAGE")) {
                    this.removeProvPackageState = "1";
                }
                this.antiTheftMode = "1";
                if (!(boolean)row.get("ALLOW_DATE_TIME")) {
                    this.dateTimeSetting = "1";
                }
                if (!(boolean)row.get("ALLOW_EDIT_DEVICE_NAME")) {
                    this.editDeviceNameState = "1";
                }
                win8payload.setDeviceEncryptionEnabled(this.deviceEncryptionEnabled);
                win81payload.requireDeviceEncryption(this.deviceEncryptionReqState);
                win81payload.allowWiFi(this.wifiState);
                win81payload.allowInternetSharing(this.internetSharingState);
                win81payload.allowAutoConnectToWiFiHotspots(this.autoConnectState);
                win81payload.allowManualWiFiConfiguration(this.manualWiFiConfigState);
                win81payload.allowNFC(this.nfcState);
                win81payload.allowBluetooth(this.bluetoothState);
                win81payload.allowVPNRoamingOverCellularNetwork(this.vpnRoamingOverCellularState);
                win81payload.allowVPNOverCellularNetwork(this.vpnOverCellularState);
                win81payload.allowCopyPaste(this.copyPasteState);
                win81payload.allowMicrosoftAccountConnection(this.microsoftAccountState);
                win81payload.allowAddingNonMSAccountsManually(this.nonMSAccountState);
                win81payload.allowStore(this.storeAllowedState);
                win81payload.allowDeveloperUnlock(this.developerUnlockState);
                win81payload.allowBrowser(this.browserAllowedState);
                win81payload.allowScreenCapture(this.screenCaptureState);
                win81payload.allowLocation(this.locationState);
                win81payload.allowUSBConnection(this.usbState);
                win81payload.allowCellularDataRoaming(this.cellularRoamingState);
                win81payload.allowCamera(this.cameraState);
                win81payload.allowSearchToUseLocation(this.locationAvailableToSearchState);
                win81payload.allowStoringImagesFromVisionSearch(this.allowStoringImagesState);
                win81payload.allowVoiceRecording(this.voiceRecordingState);
                win81payload.allowSaveAsOfOfficeFiles(this.officeFileSaveAsState);
                win81payload.allowSharingOfOfficeFiles(this.officeFileSharingState);
                win81payload.allowActionCenterNotifications(this.actionCenterNotifState);
                win81payload.allowCortana(this.cortanaState);
                win81payload.allowSyncMySettings(this.syncMySettingsState);
                win81payload.allowUserToResetPhone(this.userResetState);
                win81payload.allowManualRootCertificateInstallation(this.rootCertInstallationState);
                win81payload.allowTelemetry(this.telemetryState);
                win8payload.setDisableSDCard(this.disableSDCard);
                win81payload.allowStorageCard(this.storageCardState);
                win10payload = this.setWin10PayloadData(win10payload);
                win10payload = this.setWin10DeletePayloadData(win10payload);
                win10DesktopPayload = (WinDesktopRestrictionsPayload)this.setWin10PayloadData(win10DesktopPayload);
                win10DesktopPayload = (WinDesktopRestrictionsPayload)this.setWin10DeletePayloadData(win10DesktopPayload);
            }
        }
        catch (final DataAccessException ex) {
            this.logger.log(Level.SEVERE, "Exception while creating windows restriction remove payload : ", (Throwable)ex);
        }
        this.packOsSpecificPayloadToXML(dataObject, win81payload, "remove", "WindowsPhone81");
        this.packOsSpecificPayloadToXML(dataObject, win8payload, "remove", "WindowsPhone8");
        this.packOsSpecificPayloadToXML(dataObject, win10payload, "remove", "Windows10Mobile");
        this.packOsSpecificPayloadToXML(dataObject, win10DesktopPayload, "remove", "Windows10Desktop");
        return payload;
    }
    
    private WinMobileRestrictionsPayload setWin10PayloadData(final WinMobileRestrictionsPayload win10payload) {
        win10payload.requireDeviceEncryption(this.deviceEncryptionReqState);
        win10payload.allowStorageCard(this.storageCardState);
        win10payload.allowNFC(this.nfcState);
        win10payload.allowMicrosoftAccountConnection(this.microsoftAccountState);
        win10payload.allowDeveloperUnlock(this.developerUnlockState);
        win10payload.allowWiFi(this.wifiState);
        win10payload.allowInternetSharing(this.internetSharingState);
        win10payload.allowAutoConnectToWiFiHotspots(this.autoConnectState);
        win10payload.allowManualWiFiConfiguration(this.manualWiFiConfigState);
        win10payload.allowBluetooth(this.bluetoothState);
        win10payload.allowVPNRoamingOverCellularNetwork(this.vpnRoamingOverCellularState);
        win10payload.allowVPNOverCellularNetwork(this.vpnOverCellularState);
        win10payload.allowCopyPaste(this.copyPasteState);
        win10payload.allowAddingNonMSAccountsManually(this.nonMSAccountState);
        win10payload.allowStore(this.storeAllowedState);
        win10payload.allowBrowser(this.browserAllowedState);
        win10payload.allowScreenCapture(this.screenCaptureState);
        win10payload.allowLocation(this.locationState);
        win10payload.allowUSBConnection(this.usbState);
        win10payload.allowCellularDataRoaming(this.cellularRoamingState);
        win10payload.allowCamera(this.cameraState);
        win10payload.allowSearchToUseLocation(this.locationAvailableToSearchState);
        win10payload.allowVoiceRecording(this.voiceRecordingState);
        win10payload.allowActionCenterNotifications(this.actionCenterNotifState);
        win10payload.allowCortana(this.cortanaState);
        win10payload.allowSyncMySettings(this.syncMySettingsState);
        win10payload.allowUserToResetPhone(this.userResetState);
        win10payload.allowManualRootCertificateInstallation(this.rootCertInstallationState);
        win10payload.allowTelemetry(this.telemetryState);
        win10payload.allowToast(this.toastAllowedState);
        win10payload.restrictAppDataToSystemVolume(this.appDataToSystemVolumeState);
        win10payload.restrictAppInstallToSystemVolume(this.restrictAppInstallToSystemVolume);
        win10payload.allowTrustedApps(this.trustedAppsState);
        win10payload.allowAppStoreAutoUpdate(this.allowappstoreautoupdate);
        win10payload.requirePrivateStoreOnly(this.requirePrivateStoreOnly);
        win10payload.allowBluetoothAdvertising(this.blueToothAdvertisingState);
        win10payload.allowBluetoothDiscoverable(this.blueToothDiscoverableState);
        win10payload.allowBluetoothPairing(this.bluetoothPairingState);
        win10payload.allowBrowserCookies(this.browserCookiesState);
        win10payload.allowDoNotTrackRequests(this.doNotTrackRequestsState);
        win10payload.allowInPrivateBrowsing(this.inPrivateBrowsingState);
        win10payload.allowPasswordManager(this.passwordMgrState);
        win10payload.allowSearchSuggestionsInAddressBar(this.searchSuggestionInAddressBar);
        win10payload.allowSmartScreen(this.smartScreenState);
        win10payload.preventSmartScreenPromptOverrideForSites(this.smartScreenPromptOverrideForSites);
        win10payload.preventSmartScreenPromptOverrideForFiles(this.smartScreenPromptOverrideForFiles);
        win10payload.allowExtensions(this.browserExtensionState);
        win10payload.preventAboutFlagsAccess(this.browserAboutFlagsState);
        win10payload.allowFlash(this.browserFlashState);
        win10payload.runFlashAutomatically(this.browserRunFlashAutomaticallyState);
        win10payload.allowDeveloperTools(this.browserDeveloperToolsState);
        win10payload.clearBrowsingDataOnExit(this.clearBrowsingDataOnExit);
        win10payload.allowAddressBarDropdown(this.browserAddressBarDropdownState);
        win10payload.allowAutofill(this.browserAutofillState);
        win10payload.allowPopups(this.browserPopupsState);
        win10payload.allowCellularData(this.cellularDataState);
        win10payload.allowVpn(this.vpnSettingState);
        win10payload.allowFIPSAlgorithm(this.fipsAllowedState);
        win10payload.allowMSFeedBackNotifications(this.msFeedbackNotif);
        win10payload.allowAddProvPackage(this.addProvPackageState);
        win10payload.allowRemoveProvPackage(this.removeProvPackageState);
        win10payload.allowAntiTheftMode(this.antiTheftMode);
        win10payload.allowDateTimeSetting(this.dateTimeSetting);
        win10payload.allowEditDeviceName(this.editDeviceNameState);
        win10payload.addBrowserHomePage(this.browserHomePage);
        return win10payload;
    }
    
    private WinMobileRestrictionsPayload setWin10DeletePayloadData(final WinMobileRestrictionsPayload win10Payload) {
        DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.FALSE;
        if (this.browserCookiesState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteCookies();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.doNotTrackRequestsState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteDonotTrackRequest();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.searchSuggestionInAddressBar.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteSearchSuggestionsInAddressBar();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.smartScreenState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteSmartScreen();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.passwordMgrState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deletePasswordManager();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.smartScreenPromptOverrideForSites.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteSmartScreenPromptOverride();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.smartScreenPromptOverrideForFiles.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteSmartScreenPromptOverrideForFiles();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.vpnOverCellularState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteVpnOverCellularData();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.vpnRoamingOverCellularState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteVpnOverRoamingCellularData();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.developerUnlockState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteDeveloperUnlock();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.allowappstoreautoupdate.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteAllowAutoAppStoreAutoUpdate();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.clearBrowsingDataOnExit.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteClearBrowsingDataonExit();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.browserAutofillState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteAllowAutofill();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.browserPopupsState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteAllowPopups();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.browserFlashState.equalsIgnoreCase(DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE)) {
            win10Payload.deleteAllowFlash();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        if (this.browserHomePage == null || (this.browserHomePage != null && this.browserHomePage.equals(""))) {
            win10Payload.deleteBrowserHomePage();
            DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.TRUE;
        }
        return win10Payload;
    }
    
    static {
        DO2WindowsRestrictionsPayload.USER_CONFIG_VALUE = "userConfig";
        DO2WindowsRestrictionsPayload.nonAtomicDeletePayloadRequired = Boolean.FALSE;
    }
}
