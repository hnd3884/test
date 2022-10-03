package com.me.mdm.server.device.resource;

import com.me.mdm.server.user.User;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device
{
    @SerializedName(value = "device_name", alternate = { "name" })
    private String deviceName;
    @SerializedName("added_time")
    private Long addedTime;
    @SerializedName("agent_type")
    private Integer agentType;
    @SerializedName("agent_version")
    private String agentVersion;
    @SerializedName("agent_version_code")
    private String agentVersionCode;
    @SerializedName("available_device_capacity")
    private Double availableDeviceCapacity;
    @SerializedName("available_external_capacity")
    private Double availableExternalCapacity;
    @SerializedName("available_ram_memory")
    private Double availableRamMemory;
    @SerializedName("battery_level")
    private Float batteryLevel;
    @SerializedName("device_last_sync_time")
    private String deviceLastSyncTime;
    @SerializedName("build_version")
    private String buildVersion;
    @SerializedName("cellular_technology")
    private Integer cellularTechnology;
    @SerializedName("device_capacity")
    private Double deviceCapacity;
    @SerializedName("eas_device_identifier")
    private String easDeviceIdentifier;
    @SerializedName("external_capacity")
    private Double externalCapacity;
    @SerializedName("firmware_version")
    private String firmwareVersion;
    @SerializedName("google_play_service_id")
    private String googlePlayServiceId;
    @Expose
    private String imei;
    @SerializedName("is_activation_lock_enabled")
    private Boolean isActivationLockEnabled;
    @SerializedName("is_cloud_backup_enabled")
    private Boolean isCloudBackupEnabled;
    @SerializedName("is_device_locator_enabled")
    private Boolean isDeviceLocatorEnabled;
    @SerializedName("is_dnd_in_effect")
    private Boolean isDndInEffect;
    @SerializedName("is_itunes_account_active")
    private Boolean isItunesAccountActive;
    @SerializedName("is_profileowner")
    private Boolean isProfileowner;
    @SerializedName("is_supervised")
    private Boolean isSupervised;
    @SerializedName("last_cloud_backup_date")
    private Long lastCloudBackupDate;
    @SerializedName("managed_status")
    private Integer managedStatus;
    @Expose
    private String meid;
    @SerializedName("is_ios_native_app_registered")
    private Boolean isIOSNativeAppRegistered;
    @SerializedName("model_id")
    private Long modelId;
    @SerializedName("model_type")
    private Integer modelType;
    @SerializedName("model_name")
    private String model_name;
    @SerializedName("model")
    private String model;
    @SerializedName("modem_firmware_version")
    private String modemFirmwareVersion;
    @SerializedName("notified_agent_version")
    private String notifiedAgentVersion;
    @SerializedName("os_name")
    private String osName;
    @SerializedName("os_version")
    private String osVersion;
    @SerializedName("owned_by")
    private Integer ownedBy;
    @SerializedName("platform_type")
    private Integer platformType;
    @SerializedName("processor_architecture")
    private String processorArchitecture;
    @SerializedName("processor_speed")
    private String processorSpeed;
    @SerializedName("processor_type")
    private String processorType;
    @SerializedName("registered_time")
    private Long registeredTime;
    @Expose
    private String remarks;
    @SerializedName(value = "device_id", alternate = { "resource_id" })
    private Long resourceId;
    @SerializedName("serial_number")
    private String serialNumber;
    @SerializedName("total_ram_memory")
    private Float totalRamMemory;
    @Expose
    private String udid;
    @SerializedName("unregistered_time")
    private Long unregisteredTime;
    @SerializedName("used_device_space")
    private Double usedDeviceSpace;
    @SerializedName("used_external_space")
    private Double usedExternalSpace;
    @SerializedName("description")
    private String description;
    @SerializedName("asset_tag")
    private String assetTag;
    @SerializedName("asset_owner")
    private String assetOwner;
    @SerializedName("office")
    private String office;
    @SerializedName("branch")
    private String branch;
    @SerializedName("location")
    private String location;
    @SerializedName("areaManager")
    private String areaManager;
    @SerializedName("purchase_date")
    private Long purchaseDate;
    @SerializedName("purchase_order_number")
    private String purchaseOrderNumber;
    @SerializedName("purchase_price")
    private float purchasePrice;
    @SerializedName("purchase_type")
    private String purchaseType;
    @SerializedName("warranty_expiration_date")
    private Long warrantyExpirationDate;
    @SerializedName("warranty_number")
    private String warrantyNumber;
    @SerializedName("warranty_type")
    private String warrantyType;
    @SerializedName(value = "apn_username", alternate = { "apn_user_name" })
    private String apnUsername;
    @SerializedName("apn_password")
    private String apnpassword;
    @SerializedName("network")
    private Network network;
    @SerializedName("network_usage")
    private NetworkUsage networkUsage;
    @SerializedName("work_data_security")
    private WorkDataSecurityDetails workDataSecurityDetails;
    @SerializedName("os")
    private Os os;
    @SerializedName("security")
    private Security security;
    @SerializedName("shared_device_details")
    private SharedDevice sharedDeviceInfo;
    @SerializedName("sims")
    private List<Sim> sims;
    @SerializedName("knox_details")
    private KnoxDetails knoxDetails;
    @SerializedName("product_name")
    private String productName;
    @SerializedName("user")
    private User user;
    @SerializedName("manufacturer")
    private String manufacturer;
    @SerializedName("is_knox_enabled")
    private Boolean isKnoxEnabled;
    @SerializedName("is_multiuser")
    private Boolean isMultiUser;
    @SerializedName("processor_name")
    private String processor_name;
    @SerializedName("processor_core_count")
    private String coreCount;
    
    public Device() {
        this.meid = "--";
        this.isIOSNativeAppRegistered = false;
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
        this.purchasePrice = 0.0f;
        this.purchaseType = "--";
        this.warrantyExpirationDate = 0L;
        this.warrantyNumber = "--";
        this.warrantyType = "--";
        this.apnUsername = "--";
        this.apnpassword = "--";
    }
    
    public SharedDevice getSharedDeviceInfo() {
        return this.sharedDeviceInfo;
    }
    
    public void setSharedDeviceInfo(final SharedDevice sharedDeviceInfo) {
        this.sharedDeviceInfo = sharedDeviceInfo;
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
    
    public void getDeviceLastSyncTime(final String deviceLastSyncTime) {
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
    
    public Boolean getActivationLockEnabled() {
        return this.isActivationLockEnabled;
    }
    
    public void setActivationLockEnabled(final Boolean activationLockEnabled) {
        this.isActivationLockEnabled = activationLockEnabled;
    }
    
    public Boolean getCloudBackupEnabled() {
        return this.isCloudBackupEnabled;
    }
    
    public void setCloudBackupEnabled(final Boolean cloudBackupEnabled) {
        this.isCloudBackupEnabled = cloudBackupEnabled;
    }
    
    public Boolean getDeviceLocatorEnabled() {
        return this.isDeviceLocatorEnabled;
    }
    
    public void setDeviceLocatorEnabled(final Boolean deviceLocatorEnabled) {
        this.isDeviceLocatorEnabled = deviceLocatorEnabled;
    }
    
    public Boolean getDndInEffect() {
        return this.isDndInEffect;
    }
    
    public void setDndInEffect(final Boolean dndInEffect) {
        this.isDndInEffect = dndInEffect;
    }
    
    public Boolean getItunesAccountActive() {
        return this.isItunesAccountActive;
    }
    
    public void setItunesAccountActive(final Boolean itunesAccountActive) {
        this.isItunesAccountActive = itunesAccountActive;
    }
    
    public Boolean getProfileowner() {
        return this.isProfileowner;
    }
    
    public void setProfileowner(final Boolean profileowner) {
        this.isProfileowner = profileowner;
    }
    
    public Boolean getSupervised() {
        return this.isSupervised;
    }
    
    public void setSupervised(final Boolean supervised) {
        this.isSupervised = supervised;
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
    
    public Long getModelId() {
        return this.modelId;
    }
    
    public void setModelId(final Long modelId) {
        this.modelId = modelId;
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
    
    public Network getNetwork() {
        return this.network;
    }
    
    public void setNetwork(final Network network) {
        this.network = network;
    }
    
    public WorkDataSecurityDetails getWorkDataSecurityDetails() {
        return this.workDataSecurityDetails;
    }
    
    public void setWorkDataSecurityDetails(final WorkDataSecurityDetails workDataSecurityDetails) {
        this.workDataSecurityDetails = workDataSecurityDetails;
    }
    
    public NetworkUsage getNetworkUsage() {
        return this.networkUsage;
    }
    
    public void setNetworkUsage(final NetworkUsage networkUsage) {
        this.networkUsage = networkUsage;
    }
    
    public Os getOs() {
        return this.os;
    }
    
    public void setOs(final Os os) {
        this.os = os;
    }
    
    public Security getSecurity() {
        return this.security;
    }
    
    public void setSecurity(final Security security) {
        this.security = security;
    }
    
    public List<Sim> getSims() {
        return this.sims;
    }
    
    public void setSims(final List<Sim> sims) {
        this.sims = sims;
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
    
    public float getPurchasePrice() {
        return this.purchasePrice;
    }
    
    public void setPurchasePrice(final float purchasePrice) {
        this.purchasePrice = purchasePrice;
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
    
    public KnoxDetails getKnoxDetails() {
        return this.knoxDetails;
    }
    
    public void setKnoxDetails(final KnoxDetails knoxDetails) {
        this.knoxDetails = knoxDetails;
    }
    
    public Integer getModelType() {
        return this.modelType;
    }
    
    public void setModelType(final Integer modelType) {
        this.modelType = modelType;
    }
    
    public String getDeviceName() {
        return this.deviceName;
    }
    
    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }
    
    public String getModel_name() {
        return this.model_name;
    }
    
    public void setModel_name(final String model_name) {
        this.model_name = model_name;
    }
    
    public String getProductName() {
        return this.productName;
    }
    
    public void setProductName(final String productName) {
        this.productName = productName;
    }
    
    public String getModel() {
        return this.model;
    }
    
    public void setModel(final String model) {
        this.model = model;
    }
    
    public Boolean getIOSNativeAppRegistered() {
        return this.isIOSNativeAppRegistered;
    }
    
    public void setIOSNativeAppRegistered(final Boolean IOSNativeAppRegistered) {
        this.isIOSNativeAppRegistered = IOSNativeAppRegistered;
    }
    
    public Boolean getMultiUser() {
        return this.isMultiUser;
    }
    
    public void setMultiUser(final Boolean multiUser) {
        this.isMultiUser = multiUser;
    }
    
    public String getCoreCount() {
        return this.coreCount;
    }
    
    public void setCoreCount(final String coreCount) {
        this.coreCount = coreCount;
    }
    
    public String getProcessor_name() {
        return this.processor_name;
    }
    
    public void setProcessor_name(final String processor_name) {
        this.processor_name = processor_name;
    }
}
