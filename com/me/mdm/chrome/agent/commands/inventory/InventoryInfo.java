package com.me.mdm.chrome.agent.commands.inventory;

import java.util.Collection;
import com.me.mdm.chrome.agent.ChromeDeviceManager;
import org.json.JSONArray;
import java.util.List;
import java.util.logging.Level;
import com.google.chromedevicemanagement.v1.model.NetworkState;
import java.util.Iterator;
import java.math.BigDecimal;
import com.google.chromedevicemanagement.v1.model.VolumeInfo;
import com.google.chromedevicemanagement.v1.model.CpuInfo;
import com.me.mdm.chrome.agent.utils.ChromeAgentUtil;
import org.json.JSONObject;
import java.io.IOException;
import com.me.mdm.chrome.agent.DevicerContext;
import com.google.chromedevicemanagement.v1.model.Device;
import com.google.api.services.directory.model.ChromeOsDevice;
import com.me.mdm.chrome.agent.Context;
import java.util.logging.Logger;

public abstract class InventoryInfo
{
    Logger logger;
    Context context;
    private final ChromeOsDevice chromeDeviceDirectoryModel;
    private final Device cromeDeviceCMPAModel;
    public static final String CHROMEBOOK = "Chromebook";
    public static final String KEY_DEVICE_TYPE = "DeviceType";
    public static final String KEY_SERIAL_NUMBER = "SerialNumber";
    public static final Integer DEVICE_TYPE_LAPTOP;
    public static final String NETWORK_DETAILS = "NetworkDetails";
    public static final String SYSTEM_ACTIVITY_DETAILS = "SystemActivityDetails";
    public static final String BLUETOOTH_MAC = "BluetoothMAC";
    public static final String CELLULAR_TECHNOLOGY = "CellularTechnology";
    public static final String CURRENT_CARRIER_NETWORK = "CurrentCarrierNetwork";
    public static final String ICCID = "ICCID";
    public static final String IMEI = "IMEI";
    public static final String MEID = "MEID";
    public static final String IMSI = "IMSI";
    public static final String PHONE_NUMBER = "PhoneNumber";
    public static final String NETWORK_TYPE = "NetworkType";
    public static final String DATA_ROAMING_ENABLED = "DataRoamingEnabled";
    public static final String VOICE_ROAMING_ENABLED = "VoiceRoamingEnabled";
    public static final String SUBSCRIBER_CARRRIER_NETWORK = "SubscriberCarrierNetwork";
    public static final String IS_ROAMING = "IsRoaming";
    public static final String CURRENT_MCC = "CurrentMCC";
    public static final String SUBSCRIBER_MCC = "SubscriberMCC";
    public static final String CURRENT_MNC = "CurrentMNC";
    public static final String SUBSCRIBER_MNC = "SubscriberMNC";
    public static final String WIFI_INFO = "wifiInfo";
    public static final String SSID = "SSID";
    public static final String WLAN_IP_ADDRESS = "WLANIPAddr";
    public static final String LAN_IP_ADDRESS = "LANIPAddr";
    public static final String GATEWAY_IP_ADDRESS = "GatewayIPAddr";
    public static final String WIFI_MAC = "WiFiMAC";
    public static final String ETHERNET_MAC = "EthernetMAC";
    public static final String BLUETOOTH = "BluetoothMAC";
    public static final String NETWORK_TYPE_MOBILE = "Mobile";
    public static final String NETWORK_TYPE_WIFI = "WiFi";
    public static final String NETWORK_TYPE_UNKNOWN = "Unknown";
    public static final String SOFTWARE_DETAILS = "SoftwareDetails";
    public static final String LABEL = "Label";
    public static final String APP_NAME = "appname";
    public static final String PACKAGE_NAME = "packageName";
    public static final String DATA_DIR = "DataDir";
    public static final String PACKAGE_PATH = "PackagePath";
    public static final String VERSION_CODE = "VersionCode";
    public static final String VERSION_NAME = "VersionName";
    public static final String APP_LIST = "AppList";
    public static final String NETWORK_USAGE_DETAILS = "NetworkUsageDetails";
    public static final String SECURITY_DETAILS = "SecurityDetails";
    public static final String STORAGE_ENCRYPTION = "StorageEncryption";
    public static final String PASSCODE_COMPLIANT_WITH_PROFILES = "PasscodeCompliantWithProfiles";
    public static final String PASSCODE_PRESENT = "PasscodePresent";
    public static final String DEVICE_ROOTED = "DeviceRooted";
    public static final String DEVICE_DETAILS = "DeviceDetails";
    public static final String CUSTOM_FIELDS_DETAILS = "CustomFieldsDetails";
    public static final String ANNOTATED_ASSET_USER = "AnnotatedAssetUser";
    public static final String ANNOTATED_ASSET_LOCATION = "AnnotatedAssetLocation";
    public static final String ANNOTATED_ASSET_ID = "AnnotatedAssetId";
    public static final String ANNOTATED_ASSET_NOTES = "AnnotatedAssetNotes";
    public static final String OS_VERSION = "OSVersion";
    public static final String BUILD_VERSION = "BuildVersion";
    public static final String FIRMWARE_VERSION = "FirmwareVersion";
    public static final String OS_NAME = "OSName";
    public static final String SERIAL_NUMBER = "SerialNumber";
    public static final String MODEL = "Model";
    public static final String MODEL_NAME = "ModelName";
    public static final String MODEL_TYPE = "ModelType";
    public static final String PRODUCT_NAME = "ProductName";
    public static final String MANUFACTURE = "Manufacture";
    public static final String UDID = "UDID";
    public static final String IS_DEVICE_OWNER = "IsDeviceOwner";
    public static final String EAS_DEVICE_IDENTIFIER = "EASDeviceIdentifier";
    public static final String IS_PROFILE_OWNER = "IsProfileOwner";
    public static final String BATTERY_LEVEL = "Battery_Level";
    public static final String BATTERY_TECHNOLOGY = "BatteryTechnology";
    public static final String BATTERY_STATUS = "BatteryStatus";
    public static final String BATTERY_HEALTH = "BatteryHealth";
    public static final String CHIPSET = "ChipSet";
    public static final String LOCATION_DETAILS = "Location";
    public static final String CERTIFICATE_DETAILS = "CertificateDetails";
    public static final String CERTIFICATE_LIST = "CertificateList";
    public static final String RESTRICTION_DETAILS = "Restriction";
    public static final String BLUETOOTH_RESTRICTION = "BluetoothRestriction";
    public static final String ROAMING_RESTRICTION = "RoamingRestriction";
    public static final String DEVICE_RESTRICTION = "DeviceRestriction";
    public static final String PHONE_RESTRICTION = "PhoneRestriction";
    public static final String APPLICATION_RESTRICTION = "ApplicationRestriction";
    public static final String BROWSER_RESTRICTION = "BrowserRestriction";
    public static final String PRELOADED_APPS = "PreloadedApps";
    private static final String USED_INTERNAL_STORAGE = "UsedDeviceSpace";
    private static final String USED_EXTERNAL_STORAGE = "UsedExternalSpace";
    private static final String TOT_INTERNAL_STORAGE = "DeviceCapacity";
    private static final String TOT_EXTERNAL_STORAGE = "ExternalCapacity";
    private static final String AVAILABLE_DEVICE_CAPACITY = "AvailableDeviceCapacity";
    private static final String AVAILABLE_EXTERNAL_CAPACITY = "AvailableExternalCapacity";
    private static final String EXTERNAL_ENV = "SECONDARY_STORAGE";
    private static final String TOTAL_RAM_MEMORY = "TotalRAMMemory";
    private static final String BOOT_MODE = "BootMode";
    private static final String CPU_PERFORMANCE = "CpuPerformance";
    private static final String RECENT_USERS = "RecentUsers";
    private static final String LAST_SYNC_TIME = "LastSyncTime";
    private static final String ACTIVE_TIME_RANGES = "ActiveTimeRanges";
    
    public InventoryInfo(final Context context) throws IOException {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
        this.context = context;
        this.chromeDeviceDirectoryModel = ((DevicerContext)context).getChromeOsDeviceFromDirectory();
        this.cromeDeviceCMPAModel = ((DevicerContext)context).getChromeOsDeviceFromCMPA();
    }
    
    public abstract JSONObject fetchInfo(final JSONObject p0) throws Throwable;
    
    protected JSONObject getDeviceInfo() throws Exception {
        final JSONObject joDeviceInfo = new JSONObject();
        joDeviceInfo.put("IMEI", (Object)"--");
        joDeviceInfo.put("MEID", (Object)ChromeAgentUtil.optString(this.cromeDeviceCMPAModel.getMeid(), "--"));
        joDeviceInfo.put("OSVersion", (Object)ChromeAgentUtil.optString(this.chromeDeviceDirectoryModel.getOsVersion(), "--"));
        joDeviceInfo.put("BuildVersion", (Object)ChromeAgentUtil.optString(this.cromeDeviceCMPAModel.getPlatformVersion(), "--"));
        joDeviceInfo.put("FirmwareVersion", (Object)ChromeAgentUtil.optString(this.cromeDeviceCMPAModel.getFirmwareVersion(), "--"));
        joDeviceInfo.put("OSName", (Object)ChromeAgentUtil.optString(this.chromeDeviceDirectoryModel.getOsVersion(), "--"));
        joDeviceInfo.put("SerialNumber", (Object)this.chromeDeviceDirectoryModel.getSerialNumber());
        joDeviceInfo.put("Model", (Object)ChromeAgentUtil.optString(this.cromeDeviceCMPAModel.getHardwareModel(), "--"));
        joDeviceInfo.put("ModelName", (Object)ChromeAgentUtil.optString(this.chromeDeviceDirectoryModel.getModel(), "--"));
        joDeviceInfo.put("ModelType", (Object)InventoryInfo.DEVICE_TYPE_LAPTOP);
        joDeviceInfo.put("ProductName", (Object)ChromeAgentUtil.optString(this.chromeDeviceDirectoryModel.getModel(), "Chromebook"));
        joDeviceInfo.put("Manufacture", (Object)"--");
        joDeviceInfo.put("UDID", (Object)this.context.getUdid());
        joDeviceInfo.put("IsDeviceOwner", false);
        joDeviceInfo.put("EASDeviceIdentifier", (Object)this.context.getUdid());
        joDeviceInfo.put("IsProfileOwner", false);
        joDeviceInfo.put("ChipSet", (Object)this.cromeDeviceCMPAModel.getCpuInfo().get(0).getModelName());
        return joDeviceInfo;
    }
    
    protected JSONObject getCustomerFieldsInfo() throws Exception {
        final JSONObject joDeviceInfo = new JSONObject();
        joDeviceInfo.put("AnnotatedAssetId", (Object)ChromeAgentUtil.optString(this.chromeDeviceDirectoryModel.getAnnotatedAssetId(), "--"));
        joDeviceInfo.put("AnnotatedAssetLocation", (Object)ChromeAgentUtil.optString(this.chromeDeviceDirectoryModel.getAnnotatedLocation(), "--"));
        joDeviceInfo.put("AnnotatedAssetUser", (Object)ChromeAgentUtil.optString(this.chromeDeviceDirectoryModel.getAnnotatedUser(), "--"));
        joDeviceInfo.put("AnnotatedAssetNotes", (Object)ChromeAgentUtil.optString(this.chromeDeviceDirectoryModel.getNotes(), "--"));
        return joDeviceInfo;
    }
    
    protected JSONObject fetchDiskInfo(final JSONObject joDiskUsage) throws Exception {
        long freeInternalSpace = 0L;
        long totalInternalSpace = 0L;
        long freeExternalSpace = 0L;
        long totalExternalSpace = 0L;
        if (this.cromeDeviceCMPAModel.getVolumeInfos() != null && this.cromeDeviceCMPAModel.getVolumeInfos().size() > 0) {
            for (final VolumeInfo info : this.cromeDeviceCMPAModel.getVolumeInfos()) {
                if (info.getVolumeId() != null && info.getVolumeId().contains("removable")) {
                    freeExternalSpace += ChromeAgentUtil.optLong(info.getStorageFreeBytes(), 0L);
                    totalExternalSpace += ChromeAgentUtil.optLong(info.getStorageTotalBytes(), 0L);
                }
                else {
                    freeInternalSpace += ChromeAgentUtil.optLong(info.getStorageFreeBytes(), 0L);
                    totalInternalSpace += ChromeAgentUtil.optLong(info.getStorageTotalBytes(), 0L);
                }
            }
            final long usedInternalSpace = totalInternalSpace - freeInternalSpace;
            final long usedExternalSpace = totalExternalSpace - freeExternalSpace;
            joDiskUsage.put("DeviceCapacity", BigDecimal.valueOf(totalInternalSpace / Math.pow(1024.0, 3.0)).setScale(2, 4).doubleValue());
            joDiskUsage.put("AvailableDeviceCapacity", BigDecimal.valueOf(freeInternalSpace / Math.pow(1024.0, 3.0)).setScale(2, 4).doubleValue());
            joDiskUsage.put("UsedDeviceSpace", BigDecimal.valueOf(usedInternalSpace / Math.pow(1024.0, 3.0)).setScale(2, 4).doubleValue());
            joDiskUsage.put("ExternalCapacity", BigDecimal.valueOf(totalExternalSpace / Math.pow(1024.0, 3.0)).setScale(2, 4).doubleValue());
            joDiskUsage.put("AvailableExternalCapacity", BigDecimal.valueOf(freeExternalSpace / Math.pow(1024.0, 3.0)).setScale(2, 4).doubleValue());
            joDiskUsage.put("UsedExternalSpace", BigDecimal.valueOf(usedExternalSpace / Math.pow(1024.0, 3.0)).setScale(2, 4).doubleValue());
        }
        joDiskUsage.put("TotalRAMMemory", (Object)ChromeAgentUtil.optLong(this.cromeDeviceCMPAModel.getSystemRamTotal(), 0L));
        return joDiskUsage;
    }
    
    public JSONObject fetchNetworkInfo(final Context context, final JSONObject inventoryInfo) {
        try {
            inventoryInfo.put("WiFiMAC", (Object)ChromeAgentUtil.optString(this.cromeDeviceCMPAModel.getMacAddress(), "--"));
            inventoryInfo.put("EthernetMAC", (Object)ChromeAgentUtil.optString(this.cromeDeviceCMPAModel.getEthernetMacAddress(), "--"));
            inventoryInfo.put("BluetoothMAC", (Object)"--");
            final List<NetworkState> networkstates = this.cromeDeviceCMPAModel.getNetworkStates();
            if (networkstates != null && networkstates.size() > 0) {
                inventoryInfo.put("WLANIPAddr", (Object)networkstates.get(0).getWanIpAddress());
                inventoryInfo.put("LANIPAddr", (Object)networkstates.get(0).getLanIpAddress());
                inventoryInfo.put("GatewayIPAddr", (Object)networkstates.get(0).getGatewayIpAddress());
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while fetching Network Info", ex);
        }
        return inventoryInfo;
    }
    
    public JSONObject fetchSecurityInfo(final Context context, final JSONObject inventoryInfo) {
        try {
            if (this.cromeDeviceCMPAModel.getBootMode() != null) {
                inventoryInfo.put("DeviceRooted", !this.cromeDeviceCMPAModel.getBootMode().equalsIgnoreCase("Verified"));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while fetching Security Info", ex);
        }
        return inventoryInfo;
    }
    
    public JSONArray fetchAppsInfo(final Context context) {
        try {
            this.cromeDeviceCMPAModel.getActiveAppStatus();
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while fetching Apps Info", ex);
        }
        return new JSONArray();
    }
    
    public JSONObject fetchSystemActivityInfo(final Context context, final JSONObject inventoryInfo) {
        try {
            if (ChromeDeviceManager.getInstance().getPrivacyManager().isDeviceReportingEnabled(context)) {
                inventoryInfo.put("ActiveTimeRanges", (Collection)this.chromeDeviceDirectoryModel.getActiveTimeRanges());
            }
            if (this.cromeDeviceCMPAModel.getCpuStatus() != null) {
                inventoryInfo.put("CpuPerformance", (Object)this.cromeDeviceCMPAModel.getCpuStatus().getCpuUtilizationPct());
            }
            if (ChromeDeviceManager.getInstance().getPrivacyManager().isRecentUserReportingEnabled(context)) {
                inventoryInfo.put("RecentUsers", (Collection)this.chromeDeviceDirectoryModel.getRecentUsers());
            }
            inventoryInfo.put("LastSyncTime", (Object)this.chromeDeviceDirectoryModel.getLastSync());
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while fetching SystemActivity Info ", ex);
        }
        return inventoryInfo;
    }
    
    static {
        DEVICE_TYPE_LAPTOP = 3;
    }
}
