package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceUserModel
{
    @JsonProperty("device_user_id")
    private Long deviceUserId;
    @JsonProperty("order")
    private int order;
    @JsonProperty("login_time")
    private Long loginTime;
    @JsonProperty("username")
    private String username;
    @JsonProperty("is_logged_in")
    private boolean isLoggedIn;
    @JsonProperty("data_synced")
    private boolean dataSynced;
    @JsonProperty("data_quota")
    private String dataQuota;
    @JsonProperty("data_used")
    private String dataUsed;
    @JsonProperty("is_mobile_account")
    private boolean isMobileAccount;
    @JsonProperty("user_guid")
    private String userGUID;
    @JsonProperty("has_secure_token")
    private boolean secureToken;
    
    public Long getDeviceUserId() {
        return this.deviceUserId;
    }
    
    public void setDeviceUserId(final Long deviceUserId) {
        this.deviceUserId = deviceUserId;
    }
    
    public int getOrder() {
        return this.order;
    }
    
    public void setOrder(final int order) {
        this.order = order;
    }
    
    public Long getLoginTime() {
        return this.loginTime;
    }
    
    public void setLoginTime(final Long loginTime) {
        this.loginTime = loginTime;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
    
    public boolean isLoggedIn() {
        return this.isLoggedIn;
    }
    
    public void setLoggedIn(final boolean loggedIn) {
        this.isLoggedIn = loggedIn;
    }
    
    public boolean isHasDataToSync() {
        return this.dataSynced;
    }
    
    public void setHasDataToSync(final boolean hasDataToSync) {
        this.dataSynced = hasDataToSync;
    }
    
    public String getDataQuota() {
        return this.dataQuota;
    }
    
    public void setDataQuota(final String dataQuota) {
        this.dataQuota = dataQuota;
    }
    
    public String getDataUsed() {
        return this.dataUsed;
    }
    
    public void setDataUsed(final String dataUsed) {
        this.dataUsed = dataUsed;
    }
    
    public boolean isMobileAccount() {
        return this.isMobileAccount;
    }
    
    public void setMobileAccount(final boolean mobileAccount) {
        this.isMobileAccount = mobileAccount;
    }
    
    public String getUserGUID() {
        return this.userGUID;
    }
    
    public void setUserGUID(final String userGUID) {
        this.userGUID = userGUID;
    }
    
    public boolean isSecureToken() {
        return this.secureToken;
    }
    
    public void setSecureToken(final boolean secureToken) {
        this.secureToken = secureToken;
    }
}
