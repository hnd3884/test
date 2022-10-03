package com.me.mdm.server.apps.api.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.model.BaseAPIModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppAssociateDetailsModel extends BaseAPIModel
{
    @JsonProperty("device_id")
    private Long deviceId;
    @JsonProperty("device_ids")
    private List<Long> deviceIds;
    @JsonProperty("app_ids")
    private List<Long> appIds;
    @JsonProperty("app_id")
    private Long appId;
    @JsonProperty("businessstore_id")
    private Long businessStoreID;
    @JsonProperty("label_id")
    private Long labelId;
    @JsonProperty("app_details")
    private List<AppDetailModel> appDetails;
    @JsonProperty("silent_install")
    private Boolean silentInstall;
    @JsonProperty("notify_user_via_email")
    private Boolean notifyUserViaEmail;
    @JsonProperty("invite_user")
    private Boolean inviteUser;
    @JsonProperty("status")
    private Integer status;
    
    public AppAssociateDetailsModel() {
        this.appDetails = null;
    }
    
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(final Integer status) {
        this.status = status;
    }
    
    public List<AppDetailModel> getAppDetails() {
        return this.appDetails;
    }
    
    public void setAppDetails(final List<AppDetailModel> appDetails) {
        this.appDetails = appDetails;
    }
    
    public Boolean getSilentInstall() {
        return this.silentInstall;
    }
    
    public void setSilentInstall(final Boolean silentInstall) {
        this.silentInstall = silentInstall;
    }
    
    public Boolean getNotifyUserViaEmail() {
        return this.notifyUserViaEmail;
    }
    
    public void setNotifyUserViaEmail(final Boolean notifyUserViaEmail) {
        this.notifyUserViaEmail = notifyUserViaEmail;
    }
    
    public Boolean getInviteUser() {
        return this.inviteUser;
    }
    
    public void setInviteUser(final Boolean inviteUser) {
        this.inviteUser = inviteUser;
    }
    
    public List<Long> getDeviceIds() {
        return this.deviceIds;
    }
    
    public void setDeviceIds(final List<Long> deviceIds) {
        this.deviceIds = deviceIds;
    }
    
    public Long getDeviceId() {
        return this.deviceId;
    }
    
    public void setDeviceId(final Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public Long getAppId() {
        return this.appId;
    }
    
    public void setAppId(final Long appId) {
        this.appId = appId;
    }
    
    public Long getLabelId() {
        return this.labelId;
    }
    
    public void setLabelId(final Long labelId) {
        this.labelId = labelId;
    }
    
    public List<Long> getAppIds() {
        return this.appIds;
    }
    
    public void setAppIds(final List<Long> appIds) {
        this.appIds = appIds;
    }
    
    public Long getBusinessStoreID() {
        return this.businessStoreID;
    }
}
