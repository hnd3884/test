package com.me.mdm.server.device.api.model;

import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceModel
{
    @JsonProperty("customer_id")
    private Long customerId;
    @JsonProperty("device_id")
    private Long deviceId;
    @JsonProperty("udid")
    private String udid;
    @JsonProperty("serial_number")
    private String serialNumber;
    @JsonProperty("is_lost_mode_enabled")
    private boolean lostModeEnabled;
    @JsonProperty("user_mail")
    private String userMail;
    @JsonProperty("platform_type_id")
    private int platformTypeId;
    @JsonProperty("platform_type")
    private String platformType;
    @JsonProperty("model")
    private String model;
    @JsonProperty("device_type")
    private String deviceType;
    @JsonProperty("owned_by")
    private String ownedBy;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("os_version")
    private String osVersion;
    @JsonProperty("device_capacity")
    private String deviceCapacity;
    @JsonProperty("device_name")
    private String deviceName;
    @JsonProperty("last_contact_time")
    private String lastContactTime;
    @JsonProperty("is_removed")
    private boolean isRemoved;
    @JsonProperty("located_time")
    private String locatedTime;
    @JsonProperty("imei")
    private List<String> imei;
    @JsonProperty("summary")
    private DeviceSummaryModel summary;
    @JsonProperty("user")
    private UserModel user;
    @JsonProperty("managed_status")
    private Integer managedStatus;
    @JsonProperty("is_supervised")
    private Boolean isSupervised;
    
    public DeviceSummaryModel getSummary() {
        return this.summary;
    }
    
    public void setSummary(final DeviceSummaryModel summary) {
        this.summary = summary;
    }
    
    public UserModel getUser() {
        return this.user;
    }
    
    public void setUser(final UserModel user) {
        this.user = user;
    }
    
    public List<String> getImei() {
        return this.imei;
    }
    
    public void setImei(final List<String> imei) {
        this.imei = imei;
    }
    
    public Long getCustomerId() {
        return this.customerId;
    }
    
    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }
    
    public Long getDeviceId() {
        return this.deviceId;
    }
    
    public void setDeviceId(final Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getUdid() {
        return this.udid;
    }
    
    public void setUdid(final String udid) {
        this.udid = udid;
    }
    
    public String getSerialNumber() {
        return this.serialNumber;
    }
    
    public void setSerialNumber(final String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public boolean isLostModeEnabled() {
        return this.lostModeEnabled;
    }
    
    public void setLostModeEnabled(final boolean lostModeEnabled) {
        this.lostModeEnabled = lostModeEnabled;
    }
    
    public String getUserMail() {
        return this.userMail;
    }
    
    public void setUserMail(final String userMail) {
        this.userMail = userMail;
    }
    
    public int getPlatformTypeId() {
        return this.platformTypeId;
    }
    
    public void setPlatformTypeId(final int platformTypeId) {
        this.setPlatformType(this.platformTypeId = platformTypeId);
    }
    
    public String getPlatformType() {
        return this.platformType;
    }
    
    public void setPlatformType(final int platformTypeId) {
        this.platformType = MDMEnrollmentUtil.getPlatformString(platformTypeId);
    }
    
    public String getModel() {
        return this.model;
    }
    
    public void setModel(final String model) {
        this.model = model;
    }
    
    public String getDeviceType() {
        return this.deviceType;
    }
    
    public void setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getOwnedBy() {
        return this.ownedBy;
    }
    
    public void setOwnedBy(final String ownedBy) {
        this.ownedBy = ownedBy;
    }
    
    public String getProductName() {
        return this.productName;
    }
    
    public void setProductName(final String productName) {
        this.productName = productName;
    }
    
    public String getOsVersion() {
        return this.osVersion;
    }
    
    public void setOsVersion(final String osVersion) {
        this.osVersion = osVersion;
    }
    
    public String getDeviceCapacity() {
        return this.deviceCapacity;
    }
    
    public void setDeviceCapacity(final String deviceCapacity) {
        this.deviceCapacity = deviceCapacity;
    }
    
    public String getDeviceName() {
        return this.deviceName;
    }
    
    public void setDeviceName(final String deviceName) {
        this.deviceName = deviceName;
    }
    
    public String getLastContactTime() {
        return this.lastContactTime;
    }
    
    public void setLastContactTime(final String lastContactTime) {
        this.lastContactTime = lastContactTime;
    }
    
    public boolean isRemoved() {
        return this.isRemoved;
    }
    
    public void setRemoved(final boolean removed) {
        this.isRemoved = removed;
    }
    
    public String getLocatedTime() {
        return this.locatedTime;
    }
    
    public void setLocatedTime(final String locatedTime) {
        this.locatedTime = locatedTime;
    }
    
    public Integer getManagedStatus() {
        return this.managedStatus;
    }
    
    public void setManagedStatus(final Integer managedStatus) {
        this.managedStatus = managedStatus;
    }
    
    public Boolean getIsSupervised() {
        return this.isSupervised;
    }
    
    public void setIsSupervised(final Boolean isSupervised) {
        this.isSupervised = isSupervised;
    }
}
