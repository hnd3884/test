package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import com.google.api.client.json.JsonString;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class Device extends GenericJson
{
    @Key
    private AppStatus activeAppStatus;
    @Key
    private List<ActiveTimeRange> activeTimeRanges;
    @Key
    private String annotatedAssetId;
    @Key
    private String annotatedLocation;
    @Key
    private String annotatedUser;
    @Key
    private String autoUpdateExpiration;
    @Key
    private String bootMode;
    @Key
    private List<CpuInfo> cpuInfo;
    @Key
    private CpuStatusCollection cpuStatus;
    @Key
    private List<CpuStatusReport> cpuStatusReports;
    @Key
    private List<String> deprovisionTimes;
    @Key
    private List<DeviceAlertRecord> deviceAlerts;
    @Key
    private DeviceConnectionState deviceConnectionState;
    @Key
    private String deviceId;
    @Key
    private String enrollmentUserId;
    @Key
    private String enterpriseId;
    @Key
    private String ethernetMacAddress;
    @Key
    private String firmwareVersion;
    @Key
    private GraphicsInfo graphicsInfo;
    @Key
    private String hardwareModel;
    @Key
    private List<HardwareStatusReport> hardwareStatusReport;
    @Key
    private String lastDeviceEnrollerEmail;
    @Key
    private String lastPolicySyncTime;
    @Key
    private String lastStatusReportTime;
    @Key
    private String macAddress;
    @Key
    private String meid;
    @Key
    private String model;
    @Key
    private String name;
    @Key
    private List<NetworkState> networkStates;
    @Key
    private String note;
    @Key
    private String orderNumber;
    @Key
    private String orgUnitPath;
    @Key
    private OsUpdateStatus osUpdateStatus;
    @Key
    private String osVersion;
    @Key
    private String platformVersion;
    @Key
    private List<DeviceUserInfo> recentUsers;
    @Key
    private List<String> registerTimes;
    @Key
    private String serialNumber;
    @Key
    private String state;
    @Key
    private StatefulPartitionInfo statefulPartitionInfo;
    @Key
    private String supportEndDate;
    @Key
    @JsonString
    private Long systemRamFree;
    @Key
    private List<FreeRamStatusReport> systemRamFreeReports;
    @Key
    @JsonString
    private Long systemRamTotal;
    @Key
    private TimezoneInfo timezoneInfo;
    @Key
    private List<VolumeInfo> volumeInfos;
    @Key
    private Boolean willAutoRenew;
    
    public AppStatus getActiveAppStatus() {
        return this.activeAppStatus;
    }
    
    public Device setActiveAppStatus(final AppStatus activeAppStatus) {
        this.activeAppStatus = activeAppStatus;
        return this;
    }
    
    public List<ActiveTimeRange> getActiveTimeRanges() {
        return this.activeTimeRanges;
    }
    
    public Device setActiveTimeRanges(final List<ActiveTimeRange> activeTimeRanges) {
        this.activeTimeRanges = activeTimeRanges;
        return this;
    }
    
    public String getAnnotatedAssetId() {
        return this.annotatedAssetId;
    }
    
    public Device setAnnotatedAssetId(final String annotatedAssetId) {
        this.annotatedAssetId = annotatedAssetId;
        return this;
    }
    
    public String getAnnotatedLocation() {
        return this.annotatedLocation;
    }
    
    public Device setAnnotatedLocation(final String annotatedLocation) {
        this.annotatedLocation = annotatedLocation;
        return this;
    }
    
    public String getAnnotatedUser() {
        return this.annotatedUser;
    }
    
    public Device setAnnotatedUser(final String annotatedUser) {
        this.annotatedUser = annotatedUser;
        return this;
    }
    
    public String getAutoUpdateExpiration() {
        return this.autoUpdateExpiration;
    }
    
    public Device setAutoUpdateExpiration(final String autoUpdateExpiration) {
        this.autoUpdateExpiration = autoUpdateExpiration;
        return this;
    }
    
    public String getBootMode() {
        return this.bootMode;
    }
    
    public Device setBootMode(final String bootMode) {
        this.bootMode = bootMode;
        return this;
    }
    
    public List<CpuInfo> getCpuInfo() {
        return this.cpuInfo;
    }
    
    public Device setCpuInfo(final List<CpuInfo> cpuInfo) {
        this.cpuInfo = cpuInfo;
        return this;
    }
    
    public CpuStatusCollection getCpuStatus() {
        return this.cpuStatus;
    }
    
    public Device setCpuStatus(final CpuStatusCollection cpuStatus) {
        this.cpuStatus = cpuStatus;
        return this;
    }
    
    public List<CpuStatusReport> getCpuStatusReports() {
        return this.cpuStatusReports;
    }
    
    public Device setCpuStatusReports(final List<CpuStatusReport> cpuStatusReports) {
        this.cpuStatusReports = cpuStatusReports;
        return this;
    }
    
    public List<String> getDeprovisionTimes() {
        return this.deprovisionTimes;
    }
    
    public Device setDeprovisionTimes(final List<String> deprovisionTimes) {
        this.deprovisionTimes = deprovisionTimes;
        return this;
    }
    
    public List<DeviceAlertRecord> getDeviceAlerts() {
        return this.deviceAlerts;
    }
    
    public Device setDeviceAlerts(final List<DeviceAlertRecord> deviceAlerts) {
        this.deviceAlerts = deviceAlerts;
        return this;
    }
    
    public DeviceConnectionState getDeviceConnectionState() {
        return this.deviceConnectionState;
    }
    
    public Device setDeviceConnectionState(final DeviceConnectionState deviceConnectionState) {
        this.deviceConnectionState = deviceConnectionState;
        return this;
    }
    
    public String getDeviceId() {
        return this.deviceId;
    }
    
    public Device setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
    
    public String getEnrollmentUserId() {
        return this.enrollmentUserId;
    }
    
    public Device setEnrollmentUserId(final String enrollmentUserId) {
        this.enrollmentUserId = enrollmentUserId;
        return this;
    }
    
    public String getEnterpriseId() {
        return this.enterpriseId;
    }
    
    public Device setEnterpriseId(final String enterpriseId) {
        this.enterpriseId = enterpriseId;
        return this;
    }
    
    public String getEthernetMacAddress() {
        return this.ethernetMacAddress;
    }
    
    public Device setEthernetMacAddress(final String ethernetMacAddress) {
        this.ethernetMacAddress = ethernetMacAddress;
        return this;
    }
    
    public String getFirmwareVersion() {
        return this.firmwareVersion;
    }
    
    public Device setFirmwareVersion(final String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
        return this;
    }
    
    public GraphicsInfo getGraphicsInfo() {
        return this.graphicsInfo;
    }
    
    public Device setGraphicsInfo(final GraphicsInfo graphicsInfo) {
        this.graphicsInfo = graphicsInfo;
        return this;
    }
    
    public String getHardwareModel() {
        return this.hardwareModel;
    }
    
    public Device setHardwareModel(final String hardwareModel) {
        this.hardwareModel = hardwareModel;
        return this;
    }
    
    public List<HardwareStatusReport> getHardwareStatusReport() {
        return this.hardwareStatusReport;
    }
    
    public Device setHardwareStatusReport(final List<HardwareStatusReport> hardwareStatusReport) {
        this.hardwareStatusReport = hardwareStatusReport;
        return this;
    }
    
    public String getLastDeviceEnrollerEmail() {
        return this.lastDeviceEnrollerEmail;
    }
    
    public Device setLastDeviceEnrollerEmail(final String lastDeviceEnrollerEmail) {
        this.lastDeviceEnrollerEmail = lastDeviceEnrollerEmail;
        return this;
    }
    
    public String getLastPolicySyncTime() {
        return this.lastPolicySyncTime;
    }
    
    public Device setLastPolicySyncTime(final String lastPolicySyncTime) {
        this.lastPolicySyncTime = lastPolicySyncTime;
        return this;
    }
    
    public String getLastStatusReportTime() {
        return this.lastStatusReportTime;
    }
    
    public Device setLastStatusReportTime(final String lastStatusReportTime) {
        this.lastStatusReportTime = lastStatusReportTime;
        return this;
    }
    
    public String getMacAddress() {
        return this.macAddress;
    }
    
    public Device setMacAddress(final String macAddress) {
        this.macAddress = macAddress;
        return this;
    }
    
    public String getMeid() {
        return this.meid;
    }
    
    public Device setMeid(final String meid) {
        this.meid = meid;
        return this;
    }
    
    public String getModel() {
        return this.model;
    }
    
    public Device setModel(final String model) {
        this.model = model;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Device setName(final String name) {
        this.name = name;
        return this;
    }
    
    public List<NetworkState> getNetworkStates() {
        return this.networkStates;
    }
    
    public Device setNetworkStates(final List<NetworkState> networkStates) {
        this.networkStates = networkStates;
        return this;
    }
    
    public String getNote() {
        return this.note;
    }
    
    public Device setNote(final String note) {
        this.note = note;
        return this;
    }
    
    public String getOrderNumber() {
        return this.orderNumber;
    }
    
    public Device setOrderNumber(final String orderNumber) {
        this.orderNumber = orderNumber;
        return this;
    }
    
    public String getOrgUnitPath() {
        return this.orgUnitPath;
    }
    
    public Device setOrgUnitPath(final String orgUnitPath) {
        this.orgUnitPath = orgUnitPath;
        return this;
    }
    
    public OsUpdateStatus getOsUpdateStatus() {
        return this.osUpdateStatus;
    }
    
    public Device setOsUpdateStatus(final OsUpdateStatus osUpdateStatus) {
        this.osUpdateStatus = osUpdateStatus;
        return this;
    }
    
    public String getOsVersion() {
        return this.osVersion;
    }
    
    public Device setOsVersion(final String osVersion) {
        this.osVersion = osVersion;
        return this;
    }
    
    public String getPlatformVersion() {
        return this.platformVersion;
    }
    
    public Device setPlatformVersion(final String platformVersion) {
        this.platformVersion = platformVersion;
        return this;
    }
    
    public List<DeviceUserInfo> getRecentUsers() {
        return this.recentUsers;
    }
    
    public Device setRecentUsers(final List<DeviceUserInfo> recentUsers) {
        this.recentUsers = recentUsers;
        return this;
    }
    
    public List<String> getRegisterTimes() {
        return this.registerTimes;
    }
    
    public Device setRegisterTimes(final List<String> registerTimes) {
        this.registerTimes = registerTimes;
        return this;
    }
    
    public String getSerialNumber() {
        return this.serialNumber;
    }
    
    public Device setSerialNumber(final String serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }
    
    public String getState() {
        return this.state;
    }
    
    public Device setState(final String state) {
        this.state = state;
        return this;
    }
    
    public StatefulPartitionInfo getStatefulPartitionInfo() {
        return this.statefulPartitionInfo;
    }
    
    public Device setStatefulPartitionInfo(final StatefulPartitionInfo statefulPartitionInfo) {
        this.statefulPartitionInfo = statefulPartitionInfo;
        return this;
    }
    
    public String getSupportEndDate() {
        return this.supportEndDate;
    }
    
    public Device setSupportEndDate(final String supportEndDate) {
        this.supportEndDate = supportEndDate;
        return this;
    }
    
    public Long getSystemRamFree() {
        return this.systemRamFree;
    }
    
    public Device setSystemRamFree(final Long systemRamFree) {
        this.systemRamFree = systemRamFree;
        return this;
    }
    
    public List<FreeRamStatusReport> getSystemRamFreeReports() {
        return this.systemRamFreeReports;
    }
    
    public Device setSystemRamFreeReports(final List<FreeRamStatusReport> systemRamFreeReports) {
        this.systemRamFreeReports = systemRamFreeReports;
        return this;
    }
    
    public Long getSystemRamTotal() {
        return this.systemRamTotal;
    }
    
    public Device setSystemRamTotal(final Long systemRamTotal) {
        this.systemRamTotal = systemRamTotal;
        return this;
    }
    
    public TimezoneInfo getTimezoneInfo() {
        return this.timezoneInfo;
    }
    
    public Device setTimezoneInfo(final TimezoneInfo timezoneInfo) {
        this.timezoneInfo = timezoneInfo;
        return this;
    }
    
    public List<VolumeInfo> getVolumeInfos() {
        return this.volumeInfos;
    }
    
    public Device setVolumeInfos(final List<VolumeInfo> volumeInfos) {
        this.volumeInfos = volumeInfos;
        return this;
    }
    
    public Boolean getWillAutoRenew() {
        return this.willAutoRenew;
    }
    
    public Device setWillAutoRenew(final Boolean willAutoRenew) {
        this.willAutoRenew = willAutoRenew;
        return this;
    }
    
    public Device set(final String s, final Object o) {
        return (Device)super.set(s, o);
    }
    
    public Device clone() {
        return (Device)super.clone();
    }
    
    static {
        Data.nullOf((Class)ActiveTimeRange.class);
        Data.nullOf((Class)HardwareStatusReport.class);
        Data.nullOf((Class)NetworkState.class);
        Data.nullOf((Class)VolumeInfo.class);
    }
}
