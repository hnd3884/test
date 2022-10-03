package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.DateTime;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class MobileDevice extends GenericJson
{
    @Key
    private Boolean adbStatus;
    @Key
    private List<Applications> applications;
    @Key
    private String basebandVersion;
    @Key
    private String bootloaderVersion;
    @Key
    private String brand;
    @Key
    private String buildNumber;
    @Key
    private String defaultLanguage;
    @Key
    private Boolean developerOptionsStatus;
    @Key
    private String deviceCompromisedStatus;
    @Key
    private String deviceId;
    @Key
    private String devicePasswordStatus;
    @Key
    private List<String> email;
    @Key
    private String encryptionStatus;
    @Key
    private String etag;
    @Key
    private DateTime firstSync;
    @Key
    private String hardware;
    @Key
    private String hardwareId;
    @Key
    private String imei;
    @Key
    private String kernelVersion;
    @Key
    private String kind;
    @Key
    private DateTime lastSync;
    @Key
    private Boolean managedAccountIsOnOwnerProfile;
    @Key
    private String manufacturer;
    @Key
    private String meid;
    @Key
    private String model;
    @Key
    private List<String> name;
    @Key
    private String networkOperator;
    @Key
    private String os;
    @Key
    private List<String> otherAccountsInfo;
    @Key
    private String privilege;
    @Key
    private String releaseVersion;
    @Key
    private String resourceId;
    @Key
    @JsonString
    private Long securityPatchLevel;
    @Key
    private String serialNumber;
    @Key
    private String status;
    @Key
    private Boolean supportsWorkProfile;
    @Key
    private String type;
    @Key
    private Boolean unknownSourcesStatus;
    @Key
    private String userAgent;
    @Key
    private String wifiMacAddress;
    
    public Boolean getAdbStatus() {
        return this.adbStatus;
    }
    
    public MobileDevice setAdbStatus(final Boolean adbStatus) {
        this.adbStatus = adbStatus;
        return this;
    }
    
    public List<Applications> getApplications() {
        return this.applications;
    }
    
    public MobileDevice setApplications(final List<Applications> applications) {
        this.applications = applications;
        return this;
    }
    
    public String getBasebandVersion() {
        return this.basebandVersion;
    }
    
    public MobileDevice setBasebandVersion(final String basebandVersion) {
        this.basebandVersion = basebandVersion;
        return this;
    }
    
    public String getBootloaderVersion() {
        return this.bootloaderVersion;
    }
    
    public MobileDevice setBootloaderVersion(final String bootloaderVersion) {
        this.bootloaderVersion = bootloaderVersion;
        return this;
    }
    
    public String getBrand() {
        return this.brand;
    }
    
    public MobileDevice setBrand(final String brand) {
        this.brand = brand;
        return this;
    }
    
    public String getBuildNumber() {
        return this.buildNumber;
    }
    
    public MobileDevice setBuildNumber(final String buildNumber) {
        this.buildNumber = buildNumber;
        return this;
    }
    
    public String getDefaultLanguage() {
        return this.defaultLanguage;
    }
    
    public MobileDevice setDefaultLanguage(final String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
        return this;
    }
    
    public Boolean getDeveloperOptionsStatus() {
        return this.developerOptionsStatus;
    }
    
    public MobileDevice setDeveloperOptionsStatus(final Boolean developerOptionsStatus) {
        this.developerOptionsStatus = developerOptionsStatus;
        return this;
    }
    
    public String getDeviceCompromisedStatus() {
        return this.deviceCompromisedStatus;
    }
    
    public MobileDevice setDeviceCompromisedStatus(final String deviceCompromisedStatus) {
        this.deviceCompromisedStatus = deviceCompromisedStatus;
        return this;
    }
    
    public String getDeviceId() {
        return this.deviceId;
    }
    
    public MobileDevice setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
    
    public String getDevicePasswordStatus() {
        return this.devicePasswordStatus;
    }
    
    public MobileDevice setDevicePasswordStatus(final String devicePasswordStatus) {
        this.devicePasswordStatus = devicePasswordStatus;
        return this;
    }
    
    public List<String> getEmail() {
        return this.email;
    }
    
    public MobileDevice setEmail(final List<String> email) {
        this.email = email;
        return this;
    }
    
    public String getEncryptionStatus() {
        return this.encryptionStatus;
    }
    
    public MobileDevice setEncryptionStatus(final String encryptionStatus) {
        this.encryptionStatus = encryptionStatus;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public MobileDevice setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public DateTime getFirstSync() {
        return this.firstSync;
    }
    
    public MobileDevice setFirstSync(final DateTime firstSync) {
        this.firstSync = firstSync;
        return this;
    }
    
    public String getHardware() {
        return this.hardware;
    }
    
    public MobileDevice setHardware(final String hardware) {
        this.hardware = hardware;
        return this;
    }
    
    public String getHardwareId() {
        return this.hardwareId;
    }
    
    public MobileDevice setHardwareId(final String hardwareId) {
        this.hardwareId = hardwareId;
        return this;
    }
    
    public String getImei() {
        return this.imei;
    }
    
    public MobileDevice setImei(final String imei) {
        this.imei = imei;
        return this;
    }
    
    public String getKernelVersion() {
        return this.kernelVersion;
    }
    
    public MobileDevice setKernelVersion(final String kernelVersion) {
        this.kernelVersion = kernelVersion;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public MobileDevice setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public DateTime getLastSync() {
        return this.lastSync;
    }
    
    public MobileDevice setLastSync(final DateTime lastSync) {
        this.lastSync = lastSync;
        return this;
    }
    
    public Boolean getManagedAccountIsOnOwnerProfile() {
        return this.managedAccountIsOnOwnerProfile;
    }
    
    public MobileDevice setManagedAccountIsOnOwnerProfile(final Boolean managedAccountIsOnOwnerProfile) {
        this.managedAccountIsOnOwnerProfile = managedAccountIsOnOwnerProfile;
        return this;
    }
    
    public String getManufacturer() {
        return this.manufacturer;
    }
    
    public MobileDevice setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }
    
    public String getMeid() {
        return this.meid;
    }
    
    public MobileDevice setMeid(final String meid) {
        this.meid = meid;
        return this;
    }
    
    public String getModel() {
        return this.model;
    }
    
    public MobileDevice setModel(final String model) {
        this.model = model;
        return this;
    }
    
    public List<String> getName() {
        return this.name;
    }
    
    public MobileDevice setName(final List<String> name) {
        this.name = name;
        return this;
    }
    
    public String getNetworkOperator() {
        return this.networkOperator;
    }
    
    public MobileDevice setNetworkOperator(final String networkOperator) {
        this.networkOperator = networkOperator;
        return this;
    }
    
    public String getOs() {
        return this.os;
    }
    
    public MobileDevice setOs(final String os) {
        this.os = os;
        return this;
    }
    
    public List<String> getOtherAccountsInfo() {
        return this.otherAccountsInfo;
    }
    
    public MobileDevice setOtherAccountsInfo(final List<String> otherAccountsInfo) {
        this.otherAccountsInfo = otherAccountsInfo;
        return this;
    }
    
    public String getPrivilege() {
        return this.privilege;
    }
    
    public MobileDevice setPrivilege(final String privilege) {
        this.privilege = privilege;
        return this;
    }
    
    public String getReleaseVersion() {
        return this.releaseVersion;
    }
    
    public MobileDevice setReleaseVersion(final String releaseVersion) {
        this.releaseVersion = releaseVersion;
        return this;
    }
    
    public String getResourceId() {
        return this.resourceId;
    }
    
    public MobileDevice setResourceId(final String resourceId) {
        this.resourceId = resourceId;
        return this;
    }
    
    public Long getSecurityPatchLevel() {
        return this.securityPatchLevel;
    }
    
    public MobileDevice setSecurityPatchLevel(final Long securityPatchLevel) {
        this.securityPatchLevel = securityPatchLevel;
        return this;
    }
    
    public String getSerialNumber() {
        return this.serialNumber;
    }
    
    public MobileDevice setSerialNumber(final String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public MobileDevice setStatus(final String status) {
        this.status = status;
        return this;
    }
    
    public Boolean getSupportsWorkProfile() {
        return this.supportsWorkProfile;
    }
    
    public MobileDevice setSupportsWorkProfile(final Boolean supportsWorkProfile) {
        this.supportsWorkProfile = supportsWorkProfile;
        return this;
    }
    
    public String getType() {
        return this.type;
    }
    
    public MobileDevice setType(final String type) {
        this.type = type;
        return this;
    }
    
    public Boolean getUnknownSourcesStatus() {
        return this.unknownSourcesStatus;
    }
    
    public MobileDevice setUnknownSourcesStatus(final Boolean unknownSourcesStatus) {
        this.unknownSourcesStatus = unknownSourcesStatus;
        return this;
    }
    
    public String getUserAgent() {
        return this.userAgent;
    }
    
    public MobileDevice setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
        return this;
    }
    
    public String getWifiMacAddress() {
        return this.wifiMacAddress;
    }
    
    public MobileDevice setWifiMacAddress(final String wifiMacAddress) {
        this.wifiMacAddress = wifiMacAddress;
        return this;
    }
    
    public MobileDevice set(final String fieldName, final Object value) {
        return (MobileDevice)super.set(fieldName, value);
    }
    
    public MobileDevice clone() {
        return (MobileDevice)super.clone();
    }
    
    static {
        Data.nullOf((Class)Applications.class);
    }
    
    public static final class Applications extends GenericJson
    {
        @Key
        private String displayName;
        @Key
        private String packageName;
        @Key
        private List<String> permission;
        @Key
        private Integer versionCode;
        @Key
        private String versionName;
        
        public String getDisplayName() {
            return this.displayName;
        }
        
        public Applications setDisplayName(final String displayName) {
            this.displayName = displayName;
            return this;
        }
        
        public String getPackageName() {
            return this.packageName;
        }
        
        public Applications setPackageName(final String packageName) {
            this.packageName = packageName;
            return this;
        }
        
        public List<String> getPermission() {
            return this.permission;
        }
        
        public Applications setPermission(final List<String> permission) {
            this.permission = permission;
            return this;
        }
        
        public Integer getVersionCode() {
            return this.versionCode;
        }
        
        public Applications setVersionCode(final Integer versionCode) {
            this.versionCode = versionCode;
            return this;
        }
        
        public String getVersionName() {
            return this.versionName;
        }
        
        public Applications setVersionName(final String versionName) {
            this.versionName = versionName;
            return this;
        }
        
        public Applications set(final String fieldName, final Object value) {
            return (Applications)super.set(fieldName, value);
        }
        
        public Applications clone() {
            return (Applications)super.clone();
        }
    }
}
