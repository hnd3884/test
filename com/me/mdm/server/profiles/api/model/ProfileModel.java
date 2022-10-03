package com.me.mdm.server.profiles.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileModel
{
    @JsonProperty("device_count")
    private Integer deviceCount;
    @JsonProperty("group_count")
    private Integer groupCount;
    @JsonProperty("creation_time")
    private Long creationTime;
    @JsonProperty("last_modified_time")
    private Long lastModifiedTime;
    @JsonProperty("associated_by_user_name")
    private String associatedByUserName;
    @JsonProperty("last_modified_by_user")
    private String lastModifiedByUser;
    @JsonProperty("last_modified_by")
    private Long lastModifiedBy;
    @JsonProperty("applied_time")
    private Long appliedTime;
    @JsonProperty("profile_description")
    private String profileDescription;
    @JsonProperty("created_by")
    private Long createdBy;
    @JsonProperty("associated_by_user_id")
    private Long associatedByUserId;
    @JsonProperty("created_by_user")
    private String createdByUser;
    @JsonProperty("localized_remarks")
    private String localizedRemarks;
    @JsonProperty("profile_name")
    private String profileName;
    @JsonProperty("platform_type")
    private Integer platformType;
    @JsonProperty("latest_version")
    private Integer latestVersion;
    @JsonProperty("executed_version")
    private Integer executedVersion;
    @JsonProperty("profile_id")
    private Long profileId;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("status")
    private Integer status;
    
    public Long getCreationTime() {
        return this.creationTime;
    }
    
    public void setCreationTime(final Long creationTime) {
        this.creationTime = creationTime;
    }
    
    public Integer getDeviceCount() {
        return this.deviceCount;
    }
    
    public void setDeviceCount(final Integer deviceCount) {
        this.deviceCount = deviceCount;
    }
    
    public Integer getGroupCount() {
        return this.groupCount;
    }
    
    public void setGroupCount(final Integer groupCount) {
        this.groupCount = groupCount;
    }
    
    public Long getLastModifiedTime() {
        return this.lastModifiedTime;
    }
    
    public void setLastModifiedTime(final Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }
    
    public String getAssociatedByUserName() {
        return this.associatedByUserName;
    }
    
    public void setAssociatedByUserName(final String associatedByUserName) {
        this.associatedByUserName = associatedByUserName;
    }
    
    public String getLastModifiedByUser() {
        return this.lastModifiedByUser;
    }
    
    public void setLastModifiedByUser(final String lastModifiedByUser) {
        this.lastModifiedByUser = lastModifiedByUser;
    }
    
    public Long getLastModifiedBy() {
        return this.lastModifiedBy;
    }
    
    public void setLastModifiedBy(final Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }
    
    public Long getAppliedTime() {
        return this.appliedTime;
    }
    
    public void setAppliedTime(final Long appliedTime) {
        this.appliedTime = appliedTime;
    }
    
    public String getProfileDescription() {
        return this.profileDescription;
    }
    
    public void setProfileDescription(final String profileDescription) {
        this.profileDescription = profileDescription;
    }
    
    public Long getCreatedBy() {
        return this.createdBy;
    }
    
    public void setCreatedBy(final Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public Long getAssociatedByUserId() {
        return this.associatedByUserId;
    }
    
    public void setAssociatedByUserId(final Long associatedByUserId) {
        this.associatedByUserId = associatedByUserId;
    }
    
    public String getCreatedByUser() {
        return this.createdByUser;
    }
    
    public void setCreatedByUser(final String createdByUser) {
        this.createdByUser = createdByUser;
    }
    
    public String getLocalizedRemarks() {
        return this.localizedRemarks;
    }
    
    public void setLocalizedRemarks(final String localizedRemarks) {
        this.localizedRemarks = localizedRemarks;
    }
    
    public String getProfileName() {
        return this.profileName;
    }
    
    public void setProfileName(final String profileName) {
        this.profileName = profileName;
    }
    
    public Integer getPlatformType() {
        return this.platformType;
    }
    
    public void setPlatformType(final Integer platformType) {
        this.platformType = platformType;
    }
    
    public Integer getLatestVersion() {
        return this.latestVersion;
    }
    
    public void setLatestVersion(final Integer latestVersion) {
        this.latestVersion = latestVersion;
    }
    
    public Integer getExecutedVersion() {
        return this.executedVersion;
    }
    
    public void setExecutedVersion(final Integer executedVersion) {
        this.executedVersion = executedVersion;
    }
    
    public Long getProfileId() {
        return this.profileId;
    }
    
    public void setProfileId(final Long profileId) {
        this.profileId = profileId;
    }
    
    public String getRemarks() {
        return this.remarks;
    }
    
    public void setRemarks(final String remarks) {
        this.remarks = remarks;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(final Integer status) {
        this.status = status;
    }
}
