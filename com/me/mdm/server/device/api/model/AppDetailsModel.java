package com.me.mdm.server.device.api.model;

import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.me.mdm.server.apps.api.model.ReleaseLabelDetailModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppDetailsModel
{
    @JsonProperty("identifier")
    private String identifier;
    @JsonProperty("localized_remark")
    private String localizedRemark;
    @JsonProperty("added_time")
    private Long addedTime;
    @JsonProperty("app_version")
    private String appVersion;
    @JsonProperty("package_type")
    private Integer packageType;
    @JsonProperty("app_name")
    private String appName;
    @JsonProperty("modified_time")
    private Long modifiedTime;
    @JsonProperty("release_label_details")
    private ReleaseLabelDetailModel releaseLabelDetails;
    @JsonProperty("platform_type")
    private Integer platformType;
    @JsonProperty("app_version_code")
    private String appVersionCode;
    @JsonProperty("app_id")
    private Long appId;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("icon")
    private String icon;
    
    public String getIdentifier() {
        return this.identifier;
    }
    
    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }
    
    public String getLocalizedRemark() {
        return this.localizedRemark;
    }
    
    public void setLocalizedRemark(final String localizedRemark) {
        this.localizedRemark = localizedRemark;
    }
    
    public Long getAddedTime() {
        return this.addedTime;
    }
    
    public void setAddedTime(final Long addedTime) {
        this.addedTime = addedTime;
    }
    
    public String getAppVersion() {
        return this.appVersion;
    }
    
    public void setAppVersion(final String appVersion) {
        this.appVersion = appVersion;
    }
    
    public Integer getPackageType() {
        return this.packageType;
    }
    
    public void setPackageType(final Integer packageType) {
        this.packageType = packageType;
    }
    
    public String getAppName() {
        return this.appName;
    }
    
    public void setAppName(final String appName) {
        this.appName = appName;
    }
    
    public Long getModifiedTime() {
        return this.modifiedTime;
    }
    
    public void setModifiedTime(final Long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
    
    public ReleaseLabelDetailModel getReleaseLabelDetails() {
        return this.releaseLabelDetails;
    }
    
    public void setReleaseLabelDetails(final ReleaseLabelDetailModel releaseLabelDetails) {
        this.releaseLabelDetails = releaseLabelDetails;
    }
    
    @JsonProperty("platform_type")
    public String getPlatformType() {
        return MDMEnrollmentUtil.getPlatformString(this.platformType);
    }
    
    public void setPlatformType(final Integer platformType) {
        this.platformType = platformType;
    }
    
    public String getAppVersionCode() {
        return this.appVersionCode;
    }
    
    public void setAppVersionCode(final String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }
    
    public Long getAppId() {
        return this.appId;
    }
    
    public void setAppId(final Long appId) {
        this.appId = appId;
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
    
    public String getIcon() {
        return this.icon;
    }
    
    public void setIcon(final String icon) {
        this.icon = icon;
    }
}
