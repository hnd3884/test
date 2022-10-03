package com.me.mdm.core.windows.commands;

import java.util.HashMap;
import com.me.mdm.framework.syncml.core.data.Location;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.List;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class WpDeviceInformationCommand
{
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        try {
            final GetRequestCommand devInfoGet = new GetRequestCommand();
            devInfoGet.setRequestCmdId("DeviceInformation");
            final ArrayList items = this.getWpInventoryQuery(jsonObject);
            devInfoGet.setRequestItems(items);
            responseSyncML.getSyncBody().addRequestCmd(devInfoGet);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    protected ArrayList getWpInventoryQuery(final JSONObject jsonObject) throws Exception {
        final ArrayList items = new ArrayList();
        final Boolean fetchDeviceName = jsonObject.optBoolean("fetchDeviceName", true);
        final Boolean fetchMacAddr = jsonObject.optBoolean("fetchMacAddr", true);
        if (fetchDeviceName) {
            items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/DNSComputerName"));
        }
        items.add(this.createTargetItemTagElement("./DevDetail/DevTyp"));
        items.add(this.createTargetItemTagElement("./DevDetail/OEM"));
        items.add(this.createTargetItemTagElement("./DevDetail/FwV"));
        items.add(this.createTargetItemTagElement("./DevDetail/SwV"));
        items.add(this.createTargetItemTagElement("./DevDetail/HwV"));
        items.add(this.createTargetItemTagElement("./DevDetail/LrgObj"));
        items.add(this.createTargetItemTagElement("./DevDetail/URI/MaxDepth"));
        items.add(this.createTargetItemTagElement("./DevDetail/URI/MaxTotLen"));
        items.add(this.createTargetItemTagElement("./DevDetail/URI/MaxSegLen"));
        items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/MobileID"));
        items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/LocalTime"));
        items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/OSPlatform"));
        items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/ProcessorType"));
        items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/ProcessorArchitecture"));
        items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/RadioSwV"));
        items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/Resolution"));
        items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/CommercializationOperator"));
        items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/SMBIOSSerialNumber"));
        if (fetchMacAddr) {
            items.add(this.createTargetItemTagElement("./DevDetail/Ext/WLANMACAddress"));
        }
        items.add(this.createTargetItemTagElement("./DevDetail/Ext/WlanIPv4Address"));
        items.add(this.createTargetItemTagElement("./DevDetail/Ext/WlanIPv6Address"));
        items.add(this.createTargetItemTagElement("./Vendor/MSFT/BitLocker/Status/DeviceEncryptionStatus"));
        items.add(this.createTargetItemTagElement("./DevInfo/Man"));
        items.add(this.createTargetItemTagElement("./DevInfo/Mod"));
        items.add(this.createTargetItemTagElement("./DevInfo/DmV"));
        items.add(this.createTargetItemTagElement("./DevInfo/Lang"));
        final Boolean fetchPhonenum = jsonObject.optBoolean("fetchPhonenum", true);
        if (fetchPhonenum) {
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceInstanceService/Identity/Identity1/PhoneNumber"));
        }
        items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceInstanceService/Identity/Identity1/IMSI"));
        items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceInstanceService/Identity/Identity1/IMEI"));
        items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceInstanceService/Identity/Identity1/Roaming"));
        if (fetchPhonenum) {
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceInstanceService/Identity/Identity2/PhoneNumber"));
        }
        items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceInstanceService/Identity/Identity2/IMSI"));
        items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceInstanceService/Identity/Identity2/IMEI"));
        items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceInstanceService/Identity/Identity2/Roaming"));
        if (jsonObject.optBoolean("isWindows10RedstoneOrAbove", (boolean)Boolean.FALSE)) {
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceStatus/Battery/Status"));
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceStatus/Battery/EstimatedChargeRemaining"));
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceStatus/Battery/EstimatedRuntime"));
        }
        if (jsonObject.optBoolean("isWindows10OrAbove")) {
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/Policy/Result/DeviceLock/DevicePasswordEnabled"));
            items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/TotalStorage"));
            items.add(this.createTargetItemTagElement("./DevDetail/Ext/Microsoft/TotalRAM"));
        }
        else if (jsonObject.optBoolean("isWindows81OrAbove")) {
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/PolicyManager/Device/DeviceLock/DevicePasswordEnabled"));
        }
        else {
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceLock/DeviceValue/DevicePasswordEnabled"));
        }
        items.add(this.createTargetItemTagElement("./Vendor/MSFT/CertificateStore/Root/System?list=StructData"));
        items.add(this.createTargetItemTagElement("./Vendor/MSFT/CertificateStore/CA/System?list=StructData"));
        items.add(this.createTargetItemTagElement("./Vendor/MSFT/CertificateStore/My/User?list=StructData"));
        items.add(this.createTargetItemTagElement("./Vendor/MSFT/CertificateStore/My/System?list=StructData"));
        items.addAll(this.getDeviceRestrictionQuery(jsonObject));
        items.add(this.createTargetItemTagElement("./Vendor/MSFT/DMClient/Provider/MEMDM/ExchangeID"));
        return items;
    }
    
    private ArrayList getDeviceRestrictionQuery(final JSONObject jsonObject) throws Exception {
        final ArrayList items = new ArrayList();
        final Set<String> keySet = this.getKeyToDBColumnNameMap().keySet();
        final Boolean isWindows81OrAbove = jsonObject.optBoolean("isWindows81OrAbove", (boolean)Boolean.FALSE);
        final Boolean isWindows10RedstoneOrAbove = jsonObject.optBoolean("isWindows10RedstoneOrAbove", (boolean)Boolean.FALSE);
        if (isWindows10RedstoneOrAbove) {
            for (final String key : keySet) {
                if (key.contains("Policy/")) {
                    items.add(this.createTargetItemTagElement(key));
                }
            }
        }
        else if (isWindows81OrAbove) {
            for (final String key : keySet) {
                if (key.contains("PolicyManager")) {
                    items.add(this.createTargetItemTagElement(key));
                }
            }
        }
        else {
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/Storage/Disable"));
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/BitLocker/Status/DeviceEncryptionStatus"));
        }
        return items;
    }
    
    Item createTargetItemTagElement(final String locationUri) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        return item;
    }
    
    public HashMap<String, String> getKeyToDBColumnNameMap() {
        final HashMap<String, String> keyToDBColumnName = new HashMap<String, String>();
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/System/AllowStorageCard", "SD_CARD");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Security/RequireDeviceEncryption", "DEV_ENCRYPT");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/WiFi/AllowWiFi", "WIFI");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/WiFi/AllowInternetSharing", "NET_SHARING");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/WiFi/AllowWiFiHotSpotReporting", "WIFI_HOTSPOT_REPORTING");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/WiFi/AllowManualWiFiConfiguration", "MANUAL_WIFI_CONFIG");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/WiFi/AllowAutoConnectToWiFiSenseHotspots", "AUTO_CONNECT_TO_WIFI_HOTSPOT");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Connectivity/AllowNFC", "NFC");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Connectivity/AllowBluetooth", "BLUETOOTH");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Connectivity/AllowVPNOverCellular", "VPN");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Connectivity/AllowVPNRoamingOverCellular", "VPN_ROAMING");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Connectivity/AllowUSBConnection", "USB");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Connectivity/AllowCellularDataRoaming", "DATA_ROAMING");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/System/AllowLocation", "LOCATION");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/System/AllowTelemetry", "TELEMETRY");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/System/AllowUserToResetPhone", "USER_RESET_PHONE");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Experience/AllowCopyPaste", "COPY_PASTE");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Experience/AllowCortana", "CORTANA");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Experience/AllowScreenCapture", "SCREEN_CAPTURE");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Experience/AllowSyncMySettings", "SYNC_MY_SETTINGS");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Experience/AllowVoiceRecording", "VOICE_RECORDING");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Experience/AllowSaveAsOfOfficeFiles", "SAVE_AS_OF_OFFICE_FILES");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Experience/AllowSharingOfOfficeFiles", "SHARE_OFFICE_FILES");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Experience/AllowManualMDMUnenrollment", "MANUAL_MDM_UNENROLLMENT");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/AboveLock/AllowActionCenterNotifications", "ACTION_CENTER_NOTIFICATIONS");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Accounts/AllowMicrosoftAccountConnection", "MS_ACC_CONNECTION");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Accounts/AllowAddingNonMicrosoftAccountsManually", "NON_MS_ACC");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Security/AllowManualRootCertificateInstallation", "MANUAL_ROOT_CERT_INSTALL");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/ApplicationManagement/AllowStore", "STORE");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/ApplicationManagement/AllowDeveloperUnlock", "DEVELOPER_UNLOCK");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Browser/AllowBrowser", "BROWSER");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Camera/AllowCamera", "CAMERA");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Search/SafeSearchPermissions", "SAFE_SEARCH_PERMISSIONS");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Search/AllowSearchToUseLocation", "SEARCH_USE_LOCATION");
        keyToDBColumnName.put("./Vendor/MSFT/PolicyManager/Device/Search/AllowStoringImagesFromVisionSearch", "STORE_IMG_FROM_VISION_SEARCH");
        keyToDBColumnName.put("./Vendor/MSFT/Storage/Disable", "SD_CARD");
        keyToDBColumnName.put("./Vendor/MSFT/BitLocker/Status/DeviceEncryptionStatus", "DEV_ENCRYPT");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/WiFi/AllowWiFi", "WIFI");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/WiFi/AllowInternetSharing", "NET_SHARING");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/WiFi/AllowAutoConnectToWiFiSenseHotspots", "AUTO_CONNECT_TO_WIFI_HOTSPOT");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/WiFi/AllowManualWiFiConfiguration", "MANUAL_WIFI_CONFIG");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Connectivity/AllowNFC", "NFC");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Connectivity/AllowBluetooth", "BLUETOOTH");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Connectivity/AllowVPNRoamingOverCellular", "VPN_ROAMING");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Connectivity/AllowVPNOverCellular", "VPN");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Connectivity/AllowUSBConnection", "USB");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Connectivity/AllowCellularDataRoaming", "DATA_ROAMING");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/System/AllowStorageCard", "SD_CARD");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/System/AllowTelemetry", "TELEMETRY");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/System/AllowLocation", "LOCATION");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/System/AllowUserToResetPhone", "USER_RESET_PHONE");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Experience/AllowCopyPaste", "COPY_PASTE");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Experience/AllowScreenCapture", "SCREEN_CAPTURE");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Experience/AllowVoiceRecording", "VOICE_RECORDING");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Experience/AllowCortana", "CORTANA");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Experience/AllowSyncMySettings", "SYNC_MY_SETTINGS");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Accounts/AllowMicrosoftAccountConnection", "MS_ACC_CONNECTION");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Accounts/AllowAddingNonMicrosoftAccountsManually", "NON_MS_ACC");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Security/AllowManualRootCertificateInstallation", "MANUAL_ROOT_CERT_INSTALL");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Security/RequireDeviceEncryption", "DEV_ENCRYPT");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/ApplicationManagement/AllowStore", "STORE");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/ApplicationManagement/AllowDeveloperUnlock", "DEVELOPER_UNLOCK");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowBrowser", "BROWSER");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Camera/AllowCamera", "CAMERA");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Search/AllowSearchToUseLocation", "SEARCH_USE_LOCATION");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Search/SafeSearchPermissions", "SAFE_SEARCH_PERMISSIONS");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/AboveLock/AllowActionCenterNotifications", "ACTION_CENTER_NOTIFICATIONS");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/AboveLock/AllowToasts", "TOAST");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/ApplicationManagement/RestrictAppDataToSystemVolume", "LIMIT_APPDATA_TO_SYS_VOL");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/ApplicationManagement/RestrictAppToSystemVolume", "LIMIT_APPINSTALL_TO_SYS_VOL");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/ApplicationManagement/AllowAllTrustedApps", "TRUSTED_APPS_INSTALL");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/ApplicationManagement/AllowAppStoreAutoUpdate", "APP_STORE_AUTO_UPDATE");
        keyToDBColumnName.put("./User/Vendor/MSFT/Policy/Result/ApplicationManagement/RequirePrivateStoreOnly", "PRIVATE_STORE_ONLY");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Bluetooth/AllowAdvertising", "BLUETOOTH_ADVERTISING");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Bluetooth/AllowDiscoverableMode", "BLUETOOTH_DISCOVERABLE");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Bluetooth/AllowPrepairing", "BLUETOOTH_PREPAIRING");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowCookies", "ALLOW_COOKIES");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowDoNotTrack", "ALLOW_DONOT_TRACK");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowInPrivate", "ALLOW_INPRIVATE");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowPasswordManager", "ALLOW_PASSMGR");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowSearchSuggestionsinAddressBar", "ALLOW_SEARCHSUGGEST");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowSmartScreen", "ALLOW_SMARTSCREEN");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/PreventSmartScreenPromptOverride", "SMARTSCREEN_PROMPT");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/PreventSmartScreenPromptOverrideForFiles", "SMARTSCREEN_FILES");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowExtensions", "ALLOW_EXTENSIONS");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/PreventAccessToAboutFlagsInMicrosoftEdge", "ABOUT_FLAGS_ACCESS");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowFlashClickToRun", "RUN_FLASH_AUTOMATICALLY");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowDeveloperTools", "ALLOW_DEVELOPER_TOOLS");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowFlash", "ALLOW_FLASH");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowPopups", "ALLOW_POPUPS");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowAutofill", "ALLOW_AUTOFILL");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/AllowAddressBarDropdown", "ALLOW_ADDRESS_BAR_DROPDOWN");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Browser/ClearBrowsingDataOnExit", "CLEAR_BROWSING_DATA_EXIT");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Connectivity/AllowCellularData", "CELLULAR_DATA");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Settings/AllowVPN", "VPN_SETTING");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Experience/DoNotShowFeedbackNotifications", "MS_FEEDBACK_NOTIF");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Security/AllowAddProvisioningPackage", "ADD_PROV_PACKAGE");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Security/AllowRemoveProvisioningPackage", "REMOVE_PROV_PACKAGE");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Security/AntiTheftMode", "ANTI_THEFT_MODE");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Cryptography/AllowFipsAlgorithmPolicy", "FIPS_POLICY");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Settings/AllowDateTime", "DATE_TIME");
        keyToDBColumnName.put("./Vendor/MSFT/Policy/Result/Settings/AllowEditDeviceName", "EDIT_DEVICE_NAME");
        return keyToDBColumnName;
    }
    
    public WpDeviceInformationCommand getInstance(final HashMap hashMap) {
        WpDeviceInformationCommand wpDeviceInformationCommand = new WpDeviceInformationCommand();
        if (hashMap != null) {
            final Integer model = hashMap.get("MODEL_TYPE");
            if (model != null && !model.equals(1)) {
                wpDeviceInformationCommand = new WindowsDesktopDevInfoCommand();
            }
        }
        return wpDeviceInformationCommand;
    }
}
