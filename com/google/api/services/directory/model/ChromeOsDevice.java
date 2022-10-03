package com.google.api.services.directory.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.DateTime;
import com.google.api.client.json.JsonString;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class ChromeOsDevice extends GenericJson
{
    @Key
    private List<ActiveTimeRanges> activeTimeRanges;
    @Key
    private String annotatedAssetId;
    @Key
    private String annotatedLocation;
    @Key
    private String annotatedUser;
    @Key
    @JsonString
    private Long autoUpdateExpiration;
    @Key
    private String bootMode;
    @Key
    private List<CpuStatusReports> cpuStatusReports;
    @Key
    private List<DeviceFiles> deviceFiles;
    @Key
    private String deviceId;
    @Key
    private List<DiskVolumeReports> diskVolumeReports;
    @Key
    private String dockMacAddress;
    @Key
    private String etag;
    @Key
    private String ethernetMacAddress;
    @Key
    private String ethernetMacAddress0;
    @Key
    private String firmwareVersion;
    @Key
    private String kind;
    @Key
    private DateTime lastEnrollmentTime;
    @Key
    private List<LastKnownNetwork> lastKnownNetwork;
    @Key
    private DateTime lastSync;
    @Key
    private String macAddress;
    @Key
    private String manufactureDate;
    @Key
    private String meid;
    @Key
    private String model;
    @Key
    private String notes;
    @Key
    private String orderNumber;
    @Key
    private String orgUnitPath;
    @Key
    private String osVersion;
    @Key
    private String platformVersion;
    @Key
    private List<RecentUsers> recentUsers;
    @Key
    private List<ScreenshotFiles> screenshotFiles;
    @Key
    private String serialNumber;
    @Key
    private String status;
    @Key
    private DateTime supportEndDate;
    @Key
    private List<SystemRamFreeReports> systemRamFreeReports;
    @Key
    @JsonString
    private Long systemRamTotal;
    @Key
    private TpmVersionInfo tpmVersionInfo;
    @Key
    private Boolean willAutoRenew;
    
    public List<ActiveTimeRanges> getActiveTimeRanges() {
        return this.activeTimeRanges;
    }
    
    public ChromeOsDevice setActiveTimeRanges(final List<ActiveTimeRanges> activeTimeRanges) {
        this.activeTimeRanges = activeTimeRanges;
        return this;
    }
    
    public String getAnnotatedAssetId() {
        return this.annotatedAssetId;
    }
    
    public ChromeOsDevice setAnnotatedAssetId(final String annotatedAssetId) {
        this.annotatedAssetId = annotatedAssetId;
        return this;
    }
    
    public String getAnnotatedLocation() {
        return this.annotatedLocation;
    }
    
    public ChromeOsDevice setAnnotatedLocation(final String annotatedLocation) {
        this.annotatedLocation = annotatedLocation;
        return this;
    }
    
    public String getAnnotatedUser() {
        return this.annotatedUser;
    }
    
    public ChromeOsDevice setAnnotatedUser(final String annotatedUser) {
        this.annotatedUser = annotatedUser;
        return this;
    }
    
    public Long getAutoUpdateExpiration() {
        return this.autoUpdateExpiration;
    }
    
    public ChromeOsDevice setAutoUpdateExpiration(final Long autoUpdateExpiration) {
        this.autoUpdateExpiration = autoUpdateExpiration;
        return this;
    }
    
    public String getBootMode() {
        return this.bootMode;
    }
    
    public ChromeOsDevice setBootMode(final String bootMode) {
        this.bootMode = bootMode;
        return this;
    }
    
    public List<CpuStatusReports> getCpuStatusReports() {
        return this.cpuStatusReports;
    }
    
    public ChromeOsDevice setCpuStatusReports(final List<CpuStatusReports> cpuStatusReports) {
        this.cpuStatusReports = cpuStatusReports;
        return this;
    }
    
    public List<DeviceFiles> getDeviceFiles() {
        return this.deviceFiles;
    }
    
    public ChromeOsDevice setDeviceFiles(final List<DeviceFiles> deviceFiles) {
        this.deviceFiles = deviceFiles;
        return this;
    }
    
    public String getDeviceId() {
        return this.deviceId;
    }
    
    public ChromeOsDevice setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
    
    public List<DiskVolumeReports> getDiskVolumeReports() {
        return this.diskVolumeReports;
    }
    
    public ChromeOsDevice setDiskVolumeReports(final List<DiskVolumeReports> diskVolumeReports) {
        this.diskVolumeReports = diskVolumeReports;
        return this;
    }
    
    public String getDockMacAddress() {
        return this.dockMacAddress;
    }
    
    public ChromeOsDevice setDockMacAddress(final String dockMacAddress) {
        this.dockMacAddress = dockMacAddress;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public ChromeOsDevice setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public String getEthernetMacAddress() {
        return this.ethernetMacAddress;
    }
    
    public ChromeOsDevice setEthernetMacAddress(final String ethernetMacAddress) {
        this.ethernetMacAddress = ethernetMacAddress;
        return this;
    }
    
    public String getEthernetMacAddress0() {
        return this.ethernetMacAddress0;
    }
    
    public ChromeOsDevice setEthernetMacAddress0(final String ethernetMacAddress0) {
        this.ethernetMacAddress0 = ethernetMacAddress0;
        return this;
    }
    
    public String getFirmwareVersion() {
        return this.firmwareVersion;
    }
    
    public ChromeOsDevice setFirmwareVersion(final String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public ChromeOsDevice setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public DateTime getLastEnrollmentTime() {
        return this.lastEnrollmentTime;
    }
    
    public ChromeOsDevice setLastEnrollmentTime(final DateTime lastEnrollmentTime) {
        this.lastEnrollmentTime = lastEnrollmentTime;
        return this;
    }
    
    public List<LastKnownNetwork> getLastKnownNetwork() {
        return this.lastKnownNetwork;
    }
    
    public ChromeOsDevice setLastKnownNetwork(final List<LastKnownNetwork> lastKnownNetwork) {
        this.lastKnownNetwork = lastKnownNetwork;
        return this;
    }
    
    public DateTime getLastSync() {
        return this.lastSync;
    }
    
    public ChromeOsDevice setLastSync(final DateTime lastSync) {
        this.lastSync = lastSync;
        return this;
    }
    
    public String getMacAddress() {
        return this.macAddress;
    }
    
    public ChromeOsDevice setMacAddress(final String macAddress) {
        this.macAddress = macAddress;
        return this;
    }
    
    public String getManufactureDate() {
        return this.manufactureDate;
    }
    
    public ChromeOsDevice setManufactureDate(final String manufactureDate) {
        this.manufactureDate = manufactureDate;
        return this;
    }
    
    public String getMeid() {
        return this.meid;
    }
    
    public ChromeOsDevice setMeid(final String meid) {
        this.meid = meid;
        return this;
    }
    
    public String getModel() {
        return this.model;
    }
    
    public ChromeOsDevice setModel(final String model) {
        this.model = model;
        return this;
    }
    
    public String getNotes() {
        return this.notes;
    }
    
    public ChromeOsDevice setNotes(final String notes) {
        this.notes = notes;
        return this;
    }
    
    public String getOrderNumber() {
        return this.orderNumber;
    }
    
    public ChromeOsDevice setOrderNumber(final String orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }
    
    public String getOrgUnitPath() {
        return this.orgUnitPath;
    }
    
    public ChromeOsDevice setOrgUnitPath(final String orgUnitPath) {
        this.orgUnitPath = orgUnitPath;
        return this;
    }
    
    public String getOsVersion() {
        return this.osVersion;
    }
    
    public ChromeOsDevice setOsVersion(final String osVersion) {
        this.osVersion = osVersion;
        return this;
    }
    
    public String getPlatformVersion() {
        return this.platformVersion;
    }
    
    public ChromeOsDevice setPlatformVersion(final String platformVersion) {
        this.platformVersion = platformVersion;
        return this;
    }
    
    public List<RecentUsers> getRecentUsers() {
        return this.recentUsers;
    }
    
    public ChromeOsDevice setRecentUsers(final List<RecentUsers> recentUsers) {
        this.recentUsers = recentUsers;
        return this;
    }
    
    public List<ScreenshotFiles> getScreenshotFiles() {
        return this.screenshotFiles;
    }
    
    public ChromeOsDevice setScreenshotFiles(final List<ScreenshotFiles> screenshotFiles) {
        this.screenshotFiles = screenshotFiles;
        return this;
    }
    
    public String getSerialNumber() {
        return this.serialNumber;
    }
    
    public ChromeOsDevice setSerialNumber(final String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public ChromeOsDevice setStatus(final String status) {
        this.status = status;
        return this;
    }
    
    public DateTime getSupportEndDate() {
        return this.supportEndDate;
    }
    
    public ChromeOsDevice setSupportEndDate(final DateTime supportEndDate) {
        this.supportEndDate = supportEndDate;
        return this;
    }
    
    public List<SystemRamFreeReports> getSystemRamFreeReports() {
        return this.systemRamFreeReports;
    }
    
    public ChromeOsDevice setSystemRamFreeReports(final List<SystemRamFreeReports> systemRamFreeReports) {
        this.systemRamFreeReports = systemRamFreeReports;
        return this;
    }
    
    public Long getSystemRamTotal() {
        return this.systemRamTotal;
    }
    
    public ChromeOsDevice setSystemRamTotal(final Long systemRamTotal) {
        this.systemRamTotal = systemRamTotal;
        return this;
    }
    
    public TpmVersionInfo getTpmVersionInfo() {
        return this.tpmVersionInfo;
    }
    
    public ChromeOsDevice setTpmVersionInfo(final TpmVersionInfo tpmVersionInfo) {
        this.tpmVersionInfo = tpmVersionInfo;
        return this;
    }
    
    public Boolean getWillAutoRenew() {
        return this.willAutoRenew;
    }
    
    public ChromeOsDevice setWillAutoRenew(final Boolean willAutoRenew) {
        this.willAutoRenew = willAutoRenew;
        return this;
    }
    
    public ChromeOsDevice set(final String fieldName, final Object value) {
        return (ChromeOsDevice)super.set(fieldName, value);
    }
    
    public ChromeOsDevice clone() {
        return (ChromeOsDevice)super.clone();
    }
    
    static {
        Data.nullOf((Class)ActiveTimeRanges.class);
        Data.nullOf((Class)CpuStatusReports.class);
        Data.nullOf((Class)DeviceFiles.class);
        Data.nullOf((Class)DiskVolumeReports.class);
        Data.nullOf((Class)LastKnownNetwork.class);
        Data.nullOf((Class)RecentUsers.class);
        Data.nullOf((Class)ScreenshotFiles.class);
        Data.nullOf((Class)SystemRamFreeReports.class);
    }
    
    public static final class ActiveTimeRanges extends GenericJson
    {
        @Key
        private Integer activeTime;
        @Key
        private DateTime date;
        
        public Integer getActiveTime() {
            return this.activeTime;
        }
        
        public ActiveTimeRanges setActiveTime(final Integer activeTime) {
            this.activeTime = activeTime;
            return this;
        }
        
        public DateTime getDate() {
            return this.date;
        }
        
        public ActiveTimeRanges setDate(final DateTime date) {
            this.date = date;
            return this;
        }
        
        public ActiveTimeRanges set(final String fieldName, final Object value) {
            return (ActiveTimeRanges)super.set(fieldName, value);
        }
        
        public ActiveTimeRanges clone() {
            return (ActiveTimeRanges)super.clone();
        }
    }
    
    public static final class CpuStatusReports extends GenericJson
    {
        @Key
        private List<CpuTemperatureInfo> cpuTemperatureInfo;
        @Key
        private List<Integer> cpuUtilizationPercentageInfo;
        @Key
        private DateTime reportTime;
        
        public List<CpuTemperatureInfo> getCpuTemperatureInfo() {
            return this.cpuTemperatureInfo;
        }
        
        public CpuStatusReports setCpuTemperatureInfo(final List<CpuTemperatureInfo> cpuTemperatureInfo) {
            this.cpuTemperatureInfo = cpuTemperatureInfo;
            return this;
        }
        
        public List<Integer> getCpuUtilizationPercentageInfo() {
            return this.cpuUtilizationPercentageInfo;
        }
        
        public CpuStatusReports setCpuUtilizationPercentageInfo(final List<Integer> cpuUtilizationPercentageInfo) {
            this.cpuUtilizationPercentageInfo = cpuUtilizationPercentageInfo;
            return this;
        }
        
        public DateTime getReportTime() {
            return this.reportTime;
        }
        
        public CpuStatusReports setReportTime(final DateTime reportTime) {
            this.reportTime = reportTime;
            return this;
        }
        
        public CpuStatusReports set(final String fieldName, final Object value) {
            return (CpuStatusReports)super.set(fieldName, value);
        }
        
        public CpuStatusReports clone() {
            return (CpuStatusReports)super.clone();
        }
        
        static {
            Data.nullOf((Class)CpuTemperatureInfo.class);
        }
        
        public static final class CpuTemperatureInfo extends GenericJson
        {
            @Key
            private String label;
            @Key
            private Integer temperature;
            
            public String getLabel() {
                return this.label;
            }
            
            public CpuTemperatureInfo setLabel(final String label) {
                this.label = label;
                return this;
            }
            
            public Integer getTemperature() {
                return this.temperature;
            }
            
            public CpuTemperatureInfo setTemperature(final Integer temperature) {
                this.temperature = temperature;
                return this;
            }
            
            public CpuTemperatureInfo set(final String fieldName, final Object value) {
                return (CpuTemperatureInfo)super.set(fieldName, value);
            }
            
            public CpuTemperatureInfo clone() {
                return (CpuTemperatureInfo)super.clone();
            }
        }
    }
    
    public static final class DeviceFiles extends GenericJson
    {
        @Key
        private DateTime createTime;
        @Key
        private String downloadUrl;
        @Key
        private String name;
        @Key
        private String type;
        
        public DateTime getCreateTime() {
            return this.createTime;
        }
        
        public DeviceFiles setCreateTime(final DateTime createTime) {
            this.createTime = createTime;
            return this;
        }
        
        public String getDownloadUrl() {
            return this.downloadUrl;
        }
        
        public DeviceFiles setDownloadUrl(final String downloadUrl) {
            this.downloadUrl = downloadUrl;
            return this;
        }
        
        public String getName() {
            return this.name;
        }
        
        public DeviceFiles setName(final String name) {
            this.name = name;
            return this;
        }
        
        public String getType() {
            return this.type;
        }
        
        public DeviceFiles setType(final String type) {
            this.type = type;
            return this;
        }
        
        public DeviceFiles set(final String fieldName, final Object value) {
            return (DeviceFiles)super.set(fieldName, value);
        }
        
        public DeviceFiles clone() {
            return (DeviceFiles)super.clone();
        }
    }
    
    public static final class DiskVolumeReports extends GenericJson
    {
        @Key
        private List<VolumeInfo> volumeInfo;
        
        public List<VolumeInfo> getVolumeInfo() {
            return this.volumeInfo;
        }
        
        public DiskVolumeReports setVolumeInfo(final List<VolumeInfo> volumeInfo) {
            this.volumeInfo = volumeInfo;
            return this;
        }
        
        public DiskVolumeReports set(final String fieldName, final Object value) {
            return (DiskVolumeReports)super.set(fieldName, value);
        }
        
        public DiskVolumeReports clone() {
            return (DiskVolumeReports)super.clone();
        }
        
        static {
            Data.nullOf((Class)VolumeInfo.class);
        }
        
        public static final class VolumeInfo extends GenericJson
        {
            @Key
            @JsonString
            private Long storageFree;
            @Key
            @JsonString
            private Long storageTotal;
            @Key
            private String volumeId;
            
            public Long getStorageFree() {
                return this.storageFree;
            }
            
            public VolumeInfo setStorageFree(final Long storageFree) {
                this.storageFree = storageFree;
                return this;
            }
            
            public Long getStorageTotal() {
                return this.storageTotal;
            }
            
            public VolumeInfo setStorageTotal(final Long storageTotal) {
                this.storageTotal = storageTotal;
                return this;
            }
            
            public String getVolumeId() {
                return this.volumeId;
            }
            
            public VolumeInfo setVolumeId(final String volumeId) {
                this.volumeId = volumeId;
                return this;
            }
            
            public VolumeInfo set(final String fieldName, final Object value) {
                return (VolumeInfo)super.set(fieldName, value);
            }
            
            public VolumeInfo clone() {
                return (VolumeInfo)super.clone();
            }
        }
    }
    
    public static final class LastKnownNetwork extends GenericJson
    {
        @Key
        private String ipAddress;
        @Key
        private String wanIpAddress;
        
        public String getIpAddress() {
            return this.ipAddress;
        }
        
        public LastKnownNetwork setIpAddress(final String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }
        
        public String getWanIpAddress() {
            return this.wanIpAddress;
        }
        
        public LastKnownNetwork setWanIpAddress(final String wanIpAddress) {
            this.wanIpAddress = wanIpAddress;
            return this;
        }
        
        public LastKnownNetwork set(final String fieldName, final Object value) {
            return (LastKnownNetwork)super.set(fieldName, value);
        }
        
        public LastKnownNetwork clone() {
            return (LastKnownNetwork)super.clone();
        }
    }
    
    public static final class RecentUsers extends GenericJson
    {
        @Key
        private String email;
        @Key
        private String type;
        
        public String getEmail() {
            return this.email;
        }
        
        public RecentUsers setEmail(final String email) {
            this.email = email;
            return this;
        }
        
        public String getType() {
            return this.type;
        }
        
        public RecentUsers setType(final String type) {
            this.type = type;
            return this;
        }
        
        public RecentUsers set(final String fieldName, final Object value) {
            return (RecentUsers)super.set(fieldName, value);
        }
        
        public RecentUsers clone() {
            return (RecentUsers)super.clone();
        }
    }
    
    public static final class ScreenshotFiles extends GenericJson
    {
        @Key
        private DateTime createTime;
        @Key
        private String downloadUrl;
        @Key
        private String name;
        @Key
        private String type;
        
        public DateTime getCreateTime() {
            return this.createTime;
        }
        
        public ScreenshotFiles setCreateTime(final DateTime createTime) {
            this.createTime = createTime;
            return this;
        }
        
        public String getDownloadUrl() {
            return this.downloadUrl;
        }
        
        public ScreenshotFiles setDownloadUrl(final String downloadUrl) {
            this.downloadUrl = downloadUrl;
            return this;
        }
        
        public String getName() {
            return this.name;
        }
        
        public ScreenshotFiles setName(final String name) {
            this.name = name;
            return this;
        }
        
        public String getType() {
            return this.type;
        }
        
        public ScreenshotFiles setType(final String type) {
            this.type = type;
            return this;
        }
        
        public ScreenshotFiles set(final String fieldName, final Object value) {
            return (ScreenshotFiles)super.set(fieldName, value);
        }
        
        public ScreenshotFiles clone() {
            return (ScreenshotFiles)super.clone();
        }
    }
    
    public static final class SystemRamFreeReports extends GenericJson
    {
        @Key
        private DateTime reportTime;
        @Key
        @JsonString
        private List<Long> systemRamFreeInfo;
        
        public DateTime getReportTime() {
            return this.reportTime;
        }
        
        public SystemRamFreeReports setReportTime(final DateTime reportTime) {
            this.reportTime = reportTime;
            return this;
        }
        
        public List<Long> getSystemRamFreeInfo() {
            return this.systemRamFreeInfo;
        }
        
        public SystemRamFreeReports setSystemRamFreeInfo(final List<Long> systemRamFreeInfo) {
            this.systemRamFreeInfo = systemRamFreeInfo;
            return this;
        }
        
        public SystemRamFreeReports set(final String fieldName, final Object value) {
            return (SystemRamFreeReports)super.set(fieldName, value);
        }
        
        public SystemRamFreeReports clone() {
            return (SystemRamFreeReports)super.clone();
        }
    }
    
    public static final class TpmVersionInfo extends GenericJson
    {
        @Key
        private String family;
        @Key
        private String firmwareVersion;
        @Key
        private String manufacturer;
        @Key
        private String specLevel;
        @Key
        private String tpmModel;
        @Key
        private String vendorSpecific;
        
        public String getFamily() {
            return this.family;
        }
        
        public TpmVersionInfo setFamily(final String family) {
            this.family = family;
            return this;
        }
        
        public String getFirmwareVersion() {
            return this.firmwareVersion;
        }
        
        public TpmVersionInfo setFirmwareVersion(final String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
            return this;
        }
        
        public String getManufacturer() {
            return this.manufacturer;
        }
        
        public TpmVersionInfo setManufacturer(final String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }
        
        public String getSpecLevel() {
            return this.specLevel;
        }
        
        public TpmVersionInfo setSpecLevel(final String specLevel) {
            this.specLevel = specLevel;
            return this;
        }
        
        public String getTpmModel() {
            return this.tpmModel;
        }
        
        public TpmVersionInfo setTpmModel(final String tpmModel) {
            this.tpmModel = tpmModel;
            return this;
        }
        
        public String getVendorSpecific() {
            return this.vendorSpecific;
        }
        
        public TpmVersionInfo setVendorSpecific(final String vendorSpecific) {
            this.vendorSpecific = vendorSpecific;
            return this;
        }
        
        public TpmVersionInfo set(final String fieldName, final Object value) {
            return (TpmVersionInfo)super.set(fieldName, value);
        }
        
        public TpmVersionInfo clone() {
            return (TpmVersionInfo)super.clone();
        }
    }
}
