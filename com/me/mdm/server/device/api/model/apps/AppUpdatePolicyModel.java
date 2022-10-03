package com.me.mdm.server.device.api.model.apps;

import com.me.mdm.server.apps.appupdatepolicy.AppUpdatePolicyConstants;
import com.me.mdm.server.device.api.model.AppDetailsModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.me.mdm.server.device.api.model.schedule.SchedulerModel;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.model.BaseAPIModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppUpdatePolicyModel extends BaseAPIModel
{
    @JsonProperty("policy_name")
    private String policyName;
    @JsonProperty("distribution_type")
    private Integer distributionType;
    @JsonProperty("policy_type")
    private Integer policyType;
    @JsonProperty("description")
    private String description;
    @JsonProperty("inclusion_flag")
    private Boolean inclusionFlag;
    @JsonProperty("all_apps")
    private Boolean isAllApps;
    @JsonProperty(value = "package_list", access = JsonProperty.Access.WRITE_ONLY)
    private List<Long> packageList;
    @JsonProperty("time_zone")
    private String timeZone;
    @JsonProperty("window_start_time")
    private String windowStartTime;
    @JsonProperty("window_end_time")
    private String windowEndTime;
    @JsonProperty("schedule_params")
    private SchedulerModel schedulerModel;
    @JsonProperty("is_silent_install")
    private Boolean isSilentInstall;
    @JsonProperty("is_notify_user")
    private Boolean isNotifyUser;
    @JsonIgnore
    private Long scheduleId;
    @JsonProperty("app_update_policy_id")
    private Long profileId;
    @JsonIgnore
    private Long collectionId;
    @JsonIgnore
    private Object appUpdateConfigId;
    @JsonIgnore
    private Object deploymentTemplateId;
    @JsonProperty("creation_time")
    private Long creationTime;
    @JsonProperty("modified_time")
    private Long modifiedTime;
    @JsonProperty("created_user")
    private String createdUser;
    @JsonProperty("modified_user")
    private String modifiedUser;
    @JsonProperty(value = "app_details", access = JsonProperty.Access.READ_ONLY)
    private List<AppDetailsModel> appDetailsModels;
    @JsonProperty(value = "associated_group_count", access = JsonProperty.Access.READ_ONLY)
    private Integer policyAssociatedGroupCount;
    @JsonProperty(value = "is_store_app_policy", access = JsonProperty.Access.READ_ONLY)
    private Boolean isStoreAppPolicy;
    
    public AppUpdatePolicyModel() {
        this.distributionType = AppUpdatePolicyConstants.DistributionType.AUTOMATIC_DISTRIBUTION;
        this.policyType = AppUpdatePolicyConstants.PolicyType.SCHEDULED;
        this.description = "--";
        this.inclusionFlag = Boolean.TRUE;
        this.isAllApps = Boolean.FALSE;
        this.isSilentInstall = Boolean.TRUE;
        this.isNotifyUser = Boolean.FALSE;
        this.profileId = null;
        this.policyAssociatedGroupCount = 0;
        this.isStoreAppPolicy = Boolean.FALSE;
    }
    
    public Boolean getIsStoreAppPolicy() {
        return this.isStoreAppPolicy;
    }
    
    public void setIsStoreAppPolicy() {
        this.isStoreAppPolicy = (this.policyName != null && this.policyName.equalsIgnoreCase("Store Apps - update policy"));
    }
    
    public void setPolicyAssociatedGroupCount(final int policyAssociatedGroupCount) {
        this.policyAssociatedGroupCount = policyAssociatedGroupCount;
    }
    
    public int getPolicyAssociatedGroupCount() {
        return this.policyAssociatedGroupCount;
    }
    
    public void setAppDetailsModels(final List appDetailsModels) {
        this.appDetailsModels = appDetailsModels;
    }
    
    public List<AppDetailsModel> getAppDetailsModels() {
        return this.appDetailsModels;
    }
    
    public void setModifiedUser(final String modifiedUser) {
        this.modifiedUser = modifiedUser;
    }
    
    public String getModifiedUser() {
        return this.modifiedUser;
    }
    
    public void setCreatedUser(final String createdUser) {
        this.createdUser = createdUser;
    }
    
    public String getCreatedUser() {
        return this.createdUser;
    }
    
    public void setModifiedTime(final Long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
    
    public Long getModifiedTime() {
        return this.modifiedTime;
    }
    
    public void setCreationTime(final Long creationTime) {
        this.creationTime = creationTime;
    }
    
    public Long getCreationTime() {
        return this.creationTime;
    }
    
    public void setScheduleId(final Long scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public Long getScheduleId() {
        return this.scheduleId;
    }
    
    public void setDeploymentTemplateId(final Object deploymentTemplateId) {
        this.deploymentTemplateId = deploymentTemplateId;
    }
    
    public Object getDeploymentTemplateId() {
        return this.deploymentTemplateId;
    }
    
    public void setIsSilentInstall(final Boolean isSilentInstall) {
        this.isSilentInstall = isSilentInstall;
    }
    
    public Boolean getIsSilentInstall() {
        return this.isSilentInstall;
    }
    
    public void setIsNotifyUser(final Boolean isNotifyUser) {
        this.isNotifyUser = isNotifyUser;
    }
    
    public Boolean getIsNotifyUser() {
        return this.isNotifyUser;
    }
    
    public void setAppUpdateConfigId(final Object appUpdateConfigId) {
        this.appUpdateConfigId = appUpdateConfigId;
    }
    
    public Object getAppUpdateConfigId() {
        return this.appUpdateConfigId;
    }
    
    public void setCollectionId(final Long collectionId) {
        this.collectionId = collectionId;
    }
    
    public Long getCollectionId() {
        return this.collectionId;
    }
    
    public void setProfileId(final Long profileId) {
        this.profileId = profileId;
    }
    
    public Long getProfileId() {
        return this.profileId;
    }
    
    public void setSchedulerModel(final SchedulerModel schedulerModel) {
        this.schedulerModel = schedulerModel;
    }
    
    public SchedulerModel getSchedulerModel() {
        return this.schedulerModel;
    }
    
    public void setWindowEndTime(final String windowEndTime) {
        this.windowEndTime = windowEndTime;
    }
    
    public String getWindowEndTime() {
        return this.windowEndTime;
    }
    
    public void setWindowStartTime(final String windowStartTime) {
        this.windowStartTime = windowStartTime;
    }
    
    public String getWindowStartTime() {
        return this.windowStartTime;
    }
    
    public void setTimeZone(final String timeZone) {
        this.timeZone = timeZone;
    }
    
    public String getTimeZone() {
        return this.timeZone;
    }
    
    public void setPackageList(final List<Long> packageList) {
        this.packageList = packageList;
    }
    
    public List<Long> getPackageList() {
        return this.packageList;
    }
    
    public void setIsAllApps(final Boolean allApps) {
        this.isAllApps = allApps;
    }
    
    public Boolean getIsAllApps() {
        return this.isAllApps;
    }
    
    public void setInclusionFlag(final Boolean inclusionFlag) {
        this.inclusionFlag = inclusionFlag;
    }
    
    public Boolean getInclusionFlag() {
        return this.inclusionFlag;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setPolicyType(final Integer policyType) {
        this.policyType = policyType;
    }
    
    public Integer getPolicyType() {
        return this.policyType;
    }
    
    public void setDistributionType(final Integer distributionType) {
        this.distributionType = distributionType;
    }
    
    public Integer getDistributionType() {
        return this.distributionType;
    }
    
    public void setPolicyName(final String policyName) {
        this.policyName = policyName.trim();
    }
    
    public String getPolicyName() {
        return this.policyName;
    }
    
    @Override
    public String toString() {
        return "Profile Id:" + this.getProfileId() + " Collection Id:" + this.getCollectionId();
    }
}
