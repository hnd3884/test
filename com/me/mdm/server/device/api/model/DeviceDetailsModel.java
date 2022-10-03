package com.me.mdm.server.device.api.model;

import java.util.ArrayList;
import org.json.JSONArray;
import java.util.List;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceDetailsModel
{
    @JsonAlias({ "name" })
    @JsonProperty("device_name")
    private String deviceName;
    @JsonProperty("added_time")
    private Long addedTime;
    @JsonProperty("agent_type")
    private Integer agentType;
    @JsonProperty("agent_version")
    private String agentVersion;
    @JsonProperty("agent_version_code")
    private String agentVersionCode;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("available_device_capacity")
    private Double availableDeviceCapacity;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("available_external_capacity")
    private Double availableExternalCapacity;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("available_ram_memory")
    private Double availableRamMemory;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("battery_level")
    private Float batteryLevel;
    @JsonProperty("device_last_sync_time")
    private String deviceLastSyncTime;
    @JsonProperty("build_version")
    private String buildVersion;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("cellular_technology")
    private Integer cellularTechnology;
    @JsonProperty("device_capacity")
    @JsonSerialize(using = ToStringSerializer.class)
    private Double deviceCapacity;
    @JsonProperty("eas_device_identifier")
    private String easDeviceIdentifier;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("external_capacity")
    private Double externalCapacity;
    @JsonProperty("firmware_version")
    private String firmwareVersion;
    @JsonProperty("google_play_service_id")
    private String googlePlayServiceId;
    @JsonProperty("imei")
    private String imei;
    @JsonProperty("is_activation_lock_enabled")
    private boolean activationLockEnabled;
    @JsonProperty("is_cloud_backup_enabled")
    private boolean cloudBackupEnabled;
    @JsonProperty("is_device_locator_enabled")
    private boolean deviceLocatorEnabled;
    @JsonProperty("is_dnd_in_effect")
    private boolean dndInEffect;
    @JsonProperty("is_itunes_account_active")
    private boolean itunesAccountActive;
    @JsonProperty("is_profileowner")
    private boolean profileowner;
    @JsonProperty("is_supervised")
    private boolean supervised;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("last_cloud_backup_date")
    private Long lastCloudBackupDate;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("managed_status")
    private Integer managedStatus;
    @JsonProperty("meid")
    private String meid;
    @JsonProperty("is_ios_native_app_registered")
    private boolean iOSNativeAppRegistered;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("model_id")
    private Long modelId;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("model_type")
    private Integer modelType;
    @JsonProperty("model_name")
    private String model_name;
    @JsonProperty("model")
    private String model;
    @JsonProperty("modem_firmware_version")
    private String modemFirmwareVersion;
    @JsonProperty("notified_agent_version")
    private String notifiedAgentVersion;
    @JsonProperty("os_name")
    private String osName;
    @JsonProperty("os_version")
    private String osVersion;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("owned_by")
    private Integer ownedBy;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("platform_type")
    private Integer platformType;
    @JsonProperty("processor_architecture")
    private String processorArchitecture;
    @JsonProperty("processor_speed")
    private String processorSpeed;
    @JsonProperty("processor_type")
    private String processorType;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("registered_time")
    private Long registeredTime;
    @JsonProperty("remarks")
    private String remarks;
    @JsonAlias({ "resource_id" })
    @JsonProperty("device_id")
    private Long resourceId;
    @JsonProperty("serial_number")
    private String serialNumber;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("total_ram_memory")
    private Float totalRamMemory;
    @JsonProperty("udid")
    private String udid;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("unregistered_time")
    private Long unregisteredTime;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("used_device_space")
    private Double usedDeviceSpace;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("used_external_space")
    private Double usedExternalSpace;
    @JsonProperty("description")
    private String description;
    @JsonProperty("asset_tag")
    private String assetTag;
    @JsonProperty("asset_owner")
    private String assetOwner;
    @JsonProperty("office")
    private String office;
    @JsonProperty("branch")
    private String branch;
    @JsonProperty("location")
    private String location;
    @JsonProperty("area_manager")
    private String areaManager;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("purchase_date")
    private Long purchaseDate;
    @JsonProperty("purchase_order_number")
    private String purchaseOrderNumber;
    @JsonProperty("purchase_price")
    private String purchasePrice;
    @JsonProperty("purchase_type")
    private String purchaseType;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("warranty_expiration_date")
    private Long warrantyExpirationDate;
    @JsonProperty("warranty_number")
    private String warrantyNumber;
    @JsonProperty("warranty_type")
    private String warrantyType;
    @JsonAlias({ "apn_user_name" })
    @JsonProperty("apn_username")
    private String apnUsername;
    @JsonProperty("apn_password")
    private String apnpassword;
    @JsonAlias({ "network" })
    private NetworkModel network;
    @JsonAlias({ "network_usage" })
    @JsonProperty("network_usage")
    private NetworkUsageModel networkUsage;
    @JsonAlias({ "os" })
    private OsModel os;
    @JsonAlias({ "workdatasecuritydetails" })
    private WorkDataSecurityModel workDataSecurityModelDetails;
    @JsonAlias({ "security" })
    private SecurityModel security;
    @JsonAlias({ "sims" })
    private List<SimModel> sims;
    @JsonAlias({ "knox_details" })
    @JsonProperty("knox_details")
    private KnoxDetailsModel knoxDetails;
    @JsonProperty("product_name")
    private String productName;
    @JsonAlias({ "user" })
    private UserModel user;
    @JsonProperty("manufacturer")
    private String manufacturer;
    @JsonProperty("is_knox_enabled")
    private boolean knoxEnabled;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("notification_service_type")
    private Integer notificationServiceType;
    @JsonProperty("is_lost_mode_enabled")
    private boolean lostModeEnabled;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("lost_mode_status")
    private Integer lostModeStatus;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("last_scan_time")
    private Long lastScanTime;
    @JsonProperty("remote_settings_enabled")
    private boolean remoteSettingsEnabled;
    @JsonProperty("is_mail_server_enabled")
    private boolean mailServerEnabled;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("last_contact_time")
    private Long lastContactTime;
    @JsonProperty("summary")
    private DeviceSummaryModel summary;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("enrollment_request_time")
    private Long enrollmentRequestTime;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("enrollment_type")
    private Integer enrollmentType;
    @JsonProperty("privacy_settings")
    private PrivacySettingModel privacySetting;
    @JsonProperty("location_settings")
    private LocationSettingModel locationSettings;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("groups")
    private JSONArray groups;
    @JsonProperty("is_modified")
    private boolean modified;
    @JsonProperty("has_filevault_recovery_key")
    private Boolean hasFilevaultRecoveryKey;
    @JsonProperty("is_multiuser")
    private boolean isMultiUser;
    @JsonAlias({ "shared_device_details" })
    @JsonProperty("shared_device_details")
    private SharedDeviceDetailModel sharedDeviceInfo;
    @JsonProperty("ios_accessibility_settings")
    private IOSAccessibilitySettingsModel iOSAccessibilitySettingsInfo;
    @JsonProperty("processor_name")
    private String processor_name;
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonProperty("processor_core_count")
    private Integer coreCount;
    
    public DeviceDetailsModel() {
        this.imei = "--";
        this.meid = "--";
        this.model_name = "--";
        this.description = "--";
        this.assetTag = "--";
        this.assetOwner = "--";
        this.office = "--";
        this.branch = "--";
        this.location = "--";
        this.areaManager = "--";
        this.purchaseDate = 0L;
        this.purchaseOrderNumber = "--";
        this.purchasePrice = "0.0";
        this.purchaseType = "--";
        this.warrantyExpirationDate = 0L;
        this.warrantyNumber = "--";
        this.warrantyType = "--";
        this.apnUsername = "--";
        this.apnpassword = "--";
        this.sims = new ArrayList<SimModel>();
    }
    
    public Boolean isHasFilevaultRecoveryKey() {
        return this.hasFilevaultRecoveryKey;
    }
    
    public void setHasFilevaultRecoveryKey(final boolean hasFilevaultRecoveryKey) {
        this.hasFilevaultRecoveryKey = hasFilevaultRecoveryKey;
    }
    
    public SharedDeviceDetailModel getSharedDeviceInfo() {
        return this.sharedDeviceInfo;
    }
    
    public void setSharedDeviceInfo(final SharedDeviceDetailModel sharedDeviceInfo) {
        this.sharedDeviceInfo = sharedDeviceInfo;
    }
    
    public IOSAccessibilitySettingsModel getiOSAccessibilitySettingsInfo() {
        return this.iOSAccessibilitySettingsInfo;
    }
    
    public void setiOSAccessibilitySettingsInfo(final IOSAccessibilitySettingsModel iOSAccessibilitySettingsInfo) {
        this.iOSAccessibilitySettingsInfo = iOSAccessibilitySettingsInfo;
    }
    
    public boolean isMultiUser() {
        return this.isMultiUser;
    }
    
    public void setMultiUser(final boolean multiUser) {
        this.isMultiUser = multiUser;
    }
    
    public String getDeviceName() {
        return this.deviceName;
    }
    
    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }
    
    public Long getAddedTime() {
        return this.addedTime;
    }
    
    public void setAddedTime(final Long addedTime) {
        this.addedTime = addedTime;
    }
    
    public Integer getAgentType() {
        return this.agentType;
    }
    
    public void setAgentType(final Integer agentType) {
        this.agentType = agentType;
    }
    
    public String getAgentVersion() {
        return this.agentVersion;
    }
    
    public void setAgentVersion(final String agentVersion) {
        this.agentVersion = agentVersion;
    }
    
    public String getAgentVersionCode() {
        return this.agentVersionCode;
    }
    
    public void setAgentVersionCode(final String agentVersionCode) {
        this.agentVersionCode = agentVersionCode;
    }
    
    public Double getAvailableDeviceCapacity() {
        return this.availableDeviceCapacity;
    }
    
    public void setAvailableDeviceCapacity(final Double availableDeviceCapacity) {
        this.availableDeviceCapacity = availableDeviceCapacity;
    }
    
    public Double getAvailableExternalCapacity() {
        return this.availableExternalCapacity;
    }
    
    public void setAvailableExternalCapacity(final Double availableExternalCapacity) {
        this.availableExternalCapacity = availableExternalCapacity;
    }
    
    public Double getAvailableRamMemory() {
        return this.availableRamMemory;
    }
    
    public void setAvailableRamMemory(final Double availableRamMemory) {
        this.availableRamMemory = availableRamMemory;
    }
    
    public Float getBatteryLevel() {
        return this.batteryLevel;
    }
    
    public void setBatteryLevel(final Float batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
    
    public String getDeviceLastSyncTime() {
        return this.deviceLastSyncTime;
    }
    
    public void setDeviceLastSyncTime(final String deviceLastSyncTime) {
        this.deviceLastSyncTime = deviceLastSyncTime;
    }
    
    public String getBuildVersion() {
        return this.buildVersion;
    }
    
    public void setBuildVersion(final String buildVersion) {
        this.buildVersion = buildVersion;
    }
    
    public Integer getCellularTechnology() {
        return this.cellularTechnology;
    }
    
    public void setCellularTechnology(final Integer cellularTechnology) {
        this.cellularTechnology = cellularTechnology;
    }
    
    public Double getDeviceCapacity() {
        return this.deviceCapacity;
    }
    
    public void setDeviceCapacity(final Double deviceCapacity) {
        this.deviceCapacity = deviceCapacity;
    }
    
    public String getEasDeviceIdentifier() {
        return this.easDeviceIdentifier;
    }
    
    public void setEasDeviceIdentifier(final String easDeviceIdentifier) {
        this.easDeviceIdentifier = easDeviceIdentifier;
    }
    
    public Double getExternalCapacity() {
        return this.externalCapacity;
    }
    
    public void setExternalCapacity(final Double externalCapacity) {
        this.externalCapacity = externalCapacity;
    }
    
    public String getFirmwareVersion() {
        return this.firmwareVersion;
    }
    
    public void setFirmwareVersion(final String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
    
    public String getGooglePlayServiceId() {
        return this.googlePlayServiceId;
    }
    
    public void setGooglePlayServiceId(final String googlePlayServiceId) {
        this.googlePlayServiceId = googlePlayServiceId;
    }
    
    public String getImei() {
        return this.imei;
    }
    
    public void setImei(final String imei) {
        this.imei = imei;
    }
    
    public boolean isActivationLockEnabled() {
        return this.activationLockEnabled;
    }
    
    public void setActivationLockEnabled(final boolean activationLockEnabled) {
        this.activationLockEnabled = activationLockEnabled;
    }
    
    public boolean isCloudBackupEnabled() {
        return this.cloudBackupEnabled;
    }
    
    public void setCloudBackupEnabled(final boolean cloudBackupEnabled) {
        this.cloudBackupEnabled = cloudBackupEnabled;
    }
    
    public boolean isDeviceLocatorEnabled() {
        return this.deviceLocatorEnabled;
    }
    
    public void setDeviceLocatorEnabled(final boolean deviceLocatorEnabled) {
        this.deviceLocatorEnabled = deviceLocatorEnabled;
    }
    
    public boolean isDndInEffect() {
        return this.dndInEffect;
    }
    
    public void setDndInEffect(final boolean dndInEffect) {
        this.dndInEffect = dndInEffect;
    }
    
    public boolean isItunesAccountActive() {
        return this.itunesAccountActive;
    }
    
    public void setItunesAccountActive(final boolean itunesAccountActive) {
        this.itunesAccountActive = itunesAccountActive;
    }
    
    public boolean isProfileowner() {
        return this.profileowner;
    }
    
    public void setProfileowner(final boolean profileowner) {
        this.profileowner = profileowner;
    }
    
    public boolean isSupervised() {
        return this.supervised;
    }
    
    public void setSupervised(final boolean supervised) {
        this.supervised = supervised;
    }
    
    public Long getLastCloudBackupDate() {
        return this.lastCloudBackupDate;
    }
    
    public void setLastCloudBackupDate(final Long lastCloudBackupDate) {
        this.lastCloudBackupDate = lastCloudBackupDate;
    }
    
    public Integer getManagedStatus() {
        return this.managedStatus;
    }
    
    public void setManagedStatus(final Integer managedStatus) {
        this.managedStatus = managedStatus;
    }
    
    public String getMeid() {
        return this.meid;
    }
    
    public void setMeid(final String meid) {
        this.meid = meid;
    }
    
    public boolean isiOSNativeAppRegistered() {
        return this.iOSNativeAppRegistered;
    }
    
    public void setiOSNativeAppRegistered(final boolean iOSNativeAppRegistered) {
        this.iOSNativeAppRegistered = iOSNativeAppRegistered;
    }
    
    public Long getModelId() {
        return this.modelId;
    }
    
    public void setModelId(final Long modelId) {
        this.modelId = modelId;
    }
    
    public Integer getModelType() {
        return this.modelType;
    }
    
    public void setModelType(final Integer modelType) {
        this.modelType = modelType;
    }
    
    public String getModel_name() {
        return this.model_name;
    }
    
    public void setModel_name(final String model_name) {
        this.model_name = model_name;
    }
    
    public String getModel() {
        return this.model;
    }
    
    public void setModel(final String model) {
        this.model = model;
    }
    
    public String getModemFirmwareVersion() {
        return this.modemFirmwareVersion;
    }
    
    public void setModemFirmwareVersion(final String modemFirmwareVersion) {
        this.modemFirmwareVersion = modemFirmwareVersion;
    }
    
    public String getNotifiedAgentVersion() {
        return this.notifiedAgentVersion;
    }
    
    public void setNotifiedAgentVersion(final String notifiedAgentVersion) {
        this.notifiedAgentVersion = notifiedAgentVersion;
    }
    
    public String getOsName() {
        return this.osName;
    }
    
    public void setOsName(final String osName) {
        this.osName = osName;
    }
    
    public String getOsVersion() {
        return this.osVersion;
    }
    
    public void setOsVersion(final String osVersion) {
        this.osVersion = osVersion;
    }
    
    public Integer getOwnedBy() {
        return this.ownedBy;
    }
    
    public void setOwnedBy(final Integer ownedBy) {
        this.ownedBy = ownedBy;
    }
    
    public Integer getPlatformType() {
        return this.platformType;
    }
    
    public void setPlatformType(final Integer platformType) {
        this.platformType = platformType;
    }
    
    public String getProcessorArchitecture() {
        return this.processorArchitecture;
    }
    
    public void setProcessorArchitecture(final String processorArchitecture) {
        this.processorArchitecture = processorArchitecture;
    }
    
    public String getProcessorSpeed() {
        return this.processorSpeed;
    }
    
    public void setProcessorSpeed(final String processorSpeed) {
        this.processorSpeed = processorSpeed;
    }
    
    public String getProcessorType() {
        return this.processorType;
    }
    
    public void setProcessorType(final String processorType) {
        this.processorType = processorType;
    }
    
    public Long getRegisteredTime() {
        return this.registeredTime;
    }
    
    public void setRegisteredTime(final Long registeredTime) {
        this.registeredTime = registeredTime;
    }
    
    public String getRemarks() {
        return this.remarks;
    }
    
    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }
    
    public Long getResourceId() {
        return this.resourceId;
    }
    
    public void setResourceId(final Long resourceId) {
        this.resourceId = resourceId;
    }
    
    public String getSerialNumber() {
        return this.serialNumber;
    }
    
    public void setSerialNumber(final String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public Float getTotalRamMemory() {
        return this.totalRamMemory;
    }
    
    public void setTotalRamMemory(final Float totalRamMemory) {
        this.totalRamMemory = totalRamMemory;
    }
    
    public String getUdid() {
        return this.udid;
    }
    
    public void setUdid(final String udid) {
        this.udid = udid;
    }
    
    public Long getUnregisteredTime() {
        return this.unregisteredTime;
    }
    
    public void setUnregisteredTime(final Long unregisteredTime) {
        this.unregisteredTime = unregisteredTime;
    }
    
    public Double getUsedDeviceSpace() {
        return this.usedDeviceSpace;
    }
    
    public void setUsedDeviceSpace(final Double usedDeviceSpace) {
        this.usedDeviceSpace = usedDeviceSpace;
    }
    
    public Double getUsedExternalSpace() {
        return this.usedExternalSpace;
    }
    
    public void setUsedExternalSpace(final Double usedExternalSpace) {
        this.usedExternalSpace = usedExternalSpace;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String getAssetTag() {
        return this.assetTag;
    }
    
    public void setAssetTag(final String assetTag) {
        this.assetTag = assetTag;
    }
    
    public String getAssetOwner() {
        return this.assetOwner;
    }
    
    public void setAssetOwner(final String assetOwner) {
        this.assetOwner = assetOwner;
    }
    
    public String getOffice() {
        return this.office;
    }
    
    public void setOffice(final String office) {
        this.office = office;
    }
    
    public String getBranch() {
        return this.branch;
    }
    
    public void setBranch(final String branch) {
        this.branch = branch;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public void setLocation(final String location) {
        this.location = location;
    }
    
    public String getAreaManager() {
        return this.areaManager;
    }
    
    public void setAreaManager(final String areaManager) {
        this.areaManager = areaManager;
    }
    
    public Long getPurchaseDate() {
        return this.purchaseDate;
    }
    
    public void setPurchaseDate(final Long purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public String getPurchaseOrderNumber() {
        return this.purchaseOrderNumber;
    }
    
    public void setPurchaseOrderNumber(final String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }
    
    public String getPurchasePrice() {
        return this.purchasePrice;
    }
    
    public void setPurchasePrice(final String purchasePrice) {
        if (!purchasePrice.equalsIgnoreCase("--")) {
            this.purchasePrice = purchasePrice;
        }
    }
    
    public String getPurchaseType() {
        return this.purchaseType;
    }
    
    public void setPurchaseType(final String purchaseType) {
        this.purchaseType = purchaseType;
    }
    
    public Long getWarrantyExpirationDate() {
        return this.warrantyExpirationDate;
    }
    
    public void setWarrantyExpirationDate(final Long warrantyExpirationDate) {
        this.warrantyExpirationDate = warrantyExpirationDate;
    }
    
    public String getWarrantyNumber() {
        return this.warrantyNumber;
    }
    
    public void setWarrantyNumber(final String warrantyNumber) {
        this.warrantyNumber = warrantyNumber;
    }
    
    public String getWarrantyType() {
        return this.warrantyType;
    }
    
    public void setWarrantyType(final String warrantyType) {
        this.warrantyType = warrantyType;
    }
    
    public String getApnUsername() {
        return this.apnUsername;
    }
    
    public void setApnUsername(final String apnUsername) {
        this.apnUsername = apnUsername;
    }
    
    public String getApnpassword() {
        return this.apnpassword;
    }
    
    public void setApnpassword(final String apnpassword) {
        this.apnpassword = apnpassword;
    }
    
    public NetworkModel getNetwork() {
        return this.network;
    }
    
    public void setNetwork(final NetworkModel network) {
        this.network = network;
    }
    
    public NetworkUsageModel getNetworkUsage() {
        return this.networkUsage;
    }
    
    public void setNetworkUsage(final NetworkUsageModel networkUsage) {
        this.networkUsage = networkUsage;
    }
    
    public OsModel getOs() {
        return this.os;
    }
    
    public void setOs(final OsModel os) {
        this.os = os;
    }
    
    public WorkDataSecurityModel getWorkDataSecurity() {
        return this.workDataSecurityModelDetails;
    }
    
    public void setWorkDataSecurity(final WorkDataSecurityModel workDataSecurityModelDetails) {
        this.workDataSecurityModelDetails = workDataSecurityModelDetails;
    }
    
    public SecurityModel getSecurity() {
        return this.security;
    }
    
    public void setSecurity(final SecurityModel security) {
        this.security = security;
    }
    
    public List<SimModel> getSims() {
        return this.sims;
    }
    
    public void setSims(final List<SimModel> sims) {
        this.sims = sims;
    }
    
    public KnoxDetailsModel getKnoxDetails() {
        return this.knoxDetails;
    }
    
    public void setKnoxDetails(final KnoxDetailsModel knoxDetails) {
        this.knoxDetails = knoxDetails;
    }
    
    public String getProductName() {
        return this.productName;
    }
    
    public void setProductName(final String productName) {
        this.productName = productName;
    }
    
    public UserModel getUser() {
        return this.user;
    }
    
    public void setUser(final UserModel user) {
        this.user = user;
    }
    
    public String getManufacturer() {
        return this.manufacturer;
    }
    
    public void setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public boolean isKnoxEnabled() {
        return this.knoxEnabled;
    }
    
    public void setKnoxEnabled(final boolean knoxEnabled) {
        this.knoxEnabled = knoxEnabled;
    }
    
    public Integer getNotificationServiceType() {
        return this.notificationServiceType;
    }
    
    public void setNotificationServiceType(final Integer notificationServiceType) {
        this.notificationServiceType = notificationServiceType;
    }
    
    public boolean isLostModeEnabled() {
        return this.lostModeEnabled;
    }
    
    public void setLostModeEnabled(final boolean lostModeEnabled) {
        this.lostModeEnabled = lostModeEnabled;
    }
    
    public Integer getLostModeStatus() {
        return this.lostModeStatus;
    }
    
    public void setLostModeStatus(final Integer lostModeStatus) {
        this.lostModeStatus = lostModeStatus;
    }
    
    public Long getLastScanTime() {
        return this.lastScanTime;
    }
    
    public void setLastScanTime(final Long lastScanTime) {
        this.lastScanTime = lastScanTime;
    }
    
    public boolean isRemoteSettingsEnabled() {
        return this.remoteSettingsEnabled;
    }
    
    public void setRemoteSettingsEnabled(final boolean remoteSettingsEnabled) {
        this.remoteSettingsEnabled = remoteSettingsEnabled;
    }
    
    public boolean isMailServerEnabled() {
        return this.mailServerEnabled;
    }
    
    public void setMailServerEnabled(final boolean mailServerEnabled) {
        this.mailServerEnabled = mailServerEnabled;
    }
    
    public Long getLastContactTime() {
        return this.lastContactTime;
    }
    
    public void setLastContactTime(final Long lastContactTime) {
        this.lastContactTime = lastContactTime;
    }
    
    public DeviceSummaryModel getSummary() {
        return this.summary;
    }
    
    public void setSummary(final DeviceSummaryModel summary) {
        this.summary = summary;
    }
    
    public Long getEnrollmentRequestTime() {
        return this.enrollmentRequestTime;
    }
    
    public void setEnrollmentRequestTime(final Long enrollmentRequestTime) {
        this.enrollmentRequestTime = enrollmentRequestTime;
    }
    
    public Integer getEnrollmentType() {
        return this.enrollmentType;
    }
    
    public void setEnrollmentType(final Integer enrollmentType) {
        this.enrollmentType = enrollmentType;
    }
    
    public PrivacySettingModel getPrivacySetting() {
        return this.privacySetting;
    }
    
    public void setPrivacySetting(final PrivacySettingModel privacySetting) {
        this.privacySetting = privacySetting;
    }
    
    public LocationSettingModel getLocationSettings() {
        return this.locationSettings;
    }
    
    public void setLocationSettings(final LocationSettingModel locationSettings) {
        this.locationSettings = locationSettings;
    }
    
    public Long getUserId() {
        return this.userId;
    }
    
    public void setUserId(final Long userId) {
        this.userId = userId;
    }
    
    public boolean isModified() {
        return this.modified;
    }
    
    public void setModified(final boolean modified) {
        this.modified = modified;
    }
    
    public JSONArray getGroups() {
        return this.groups;
    }
    
    public void setGroups(final JSONArray groups) {
        this.groups = groups;
    }
    
    public Integer getCoreCount() {
        return this.coreCount;
    }
    
    public void setCoreCount(final Integer coreCount) {
        this.coreCount = coreCount;
    }
    
    public String getProcessor_name() {
        return this.processor_name;
    }
    
    public void setProcessor_name(final String processor_name) {
        this.processor_name = processor_name;
    }
}
