package com.me.mdm.server.device.api.model;

import com.me.mdm.api.paging.annotations.AllCustomerSearchParam;
import com.me.mdm.api.paging.annotations.SearchParams;
import com.me.mdm.api.paging.annotations.SearchParam;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.me.mdm.api.paging.model.Pagination;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchDevice extends Pagination
{
    private Long deviceId;
    @JsonProperty("search")
    @SearchParam(value = "devicename", tableName = "ManagedDeviceExtn", columnName = "NAME")
    private String search;
    @JsonProperty("email")
    @SearchParam(value = "email", tableName = "ManagedUser", columnName = "EMAIL_ADDRESS", comparator = 0)
    private String email;
    @JsonProperty("platform")
    private String platform;
    @JsonProperty("group_id")
    @SearchParam(value = "groupId", tableName = "CustomGroupMemberRel", columnName = "GROUP_RESOURCE_ID", comparator = 0)
    private Long groupId;
    @JsonProperty("exclude_removed")
    private boolean excludeRemoved;
    @SearchParam(value = "ownedBy", tableName = "DeviceEnrollmentRequest", columnName = "OWNED_BY", comparator = 0)
    @JsonProperty("owned_by")
    private Integer ownedBy;
    @JsonProperty("device_type")
    private String deviceType;
    @SearchParams({ @SearchParam(value = "primaryimei", tableName = "PRIMARYMDSIMINFO", columnName = "IMEI"), @SearchParam(value = "secondaryimei", tableName = "SECONDRYMDSIMINFO", columnName = "IMEI") })
    @JsonProperty("imei")
    private String imei;
    @SearchParam(value = "serialNumber", tableName = "MdDeviceInfo", columnName = "SERIAL_NUMBER")
    @JsonProperty("serial_number")
    private String serialNumber;
    @JsonProperty("device_group_unassigned")
    private boolean deviceGroupUnassigned;
    @JsonProperty("summary")
    private boolean deviceSummary;
    @JsonProperty("is_lost")
    private boolean isLost;
    @JsonProperty("include")
    private String include;
    @JsonProperty("is_tree_source")
    private boolean isTreeSource;
    @AllCustomerSearchParam(tableName = "Resource", columnName = "CUSTOMER_ID")
    @JsonProperty("customer_ids")
    private String customerIds;
    
    public Long getDeviceId() {
        return this.deviceId;
    }
    
    public void setDeviceId(final Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getSearch() {
        return this.search;
    }
    
    public void setSearch(final String search) {
        this.search = search;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(final String email) {
        this.email = email;
    }
    
    public String getPlatform() {
        return this.platform;
    }
    
    public void setPlatform(final String platform) {
        this.platform = platform;
    }
    
    public Long getGroupId() {
        return this.groupId;
    }
    
    public void setGroupId(final Long groupId) {
        this.groupId = groupId;
    }
    
    public boolean isExcludeRemoved() {
        return this.excludeRemoved;
    }
    
    public void setExcludeRemoved(final boolean excludeRemoved) {
        this.excludeRemoved = excludeRemoved;
    }
    
    public Integer getOwnedBy() {
        return this.ownedBy;
    }
    
    public void setOwnedBy(final Integer ownedBy) {
        this.ownedBy = ownedBy;
    }
    
    public String getDeviceType() {
        return this.deviceType;
    }
    
    public void setDeviceType(final String deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getImei() {
        return this.imei;
    }
    
    public void setImei(final String imei) {
        this.imei = imei;
    }
    
    public String getSerialNumber() {
        return this.serialNumber;
    }
    
    public void setSerialNumber(final String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public boolean isDeviceGroupUnassigned() {
        return this.deviceGroupUnassigned;
    }
    
    public void setDeviceGroupUnassigned(final boolean deviceGroupUnassigned) {
        this.deviceGroupUnassigned = deviceGroupUnassigned;
    }
    
    public boolean isDeviceSummary() {
        return this.deviceSummary;
    }
    
    public void setDeviceSummary(final boolean deviceSummary) {
        this.deviceSummary = deviceSummary;
    }
    
    public boolean isLost() {
        return this.isLost;
    }
    
    public void setLost(final boolean lost) {
        this.isLost = lost;
    }
    
    public String getInclude() {
        return this.include;
    }
    
    public void setInclude(final String include) {
        this.include = include;
    }
    
    public boolean isTreeSource() {
        return this.isTreeSource;
    }
    
    public void setTreeSource(final boolean treeSource) {
        this.isTreeSource = treeSource;
    }
    
    public String getCustomerIds() {
        return this.customerIds;
    }
    
    public void setCustomerIds(final String customerIds) {
        this.customerIds = customerIds;
    }
}
