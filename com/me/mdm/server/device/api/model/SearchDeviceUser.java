package com.me.mdm.server.device.api.model;

import com.me.mdm.api.paging.annotations.SearchParam;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.me.mdm.api.paging.model.Pagination;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchDeviceUser extends Pagination
{
    private Long deviceId;
    @JsonProperty("isLoggedIn")
    @SearchParam(value = "loggedin", tableName = "MDDeviceUserAccounts", columnName = "IS_LOGGED_IN")
    private String isLoggedIn;
    @JsonProperty("isMobileAccount")
    @SearchParam(value = "mobileaccount", tableName = "MDDeviceUserAccounts", columnName = "IS_MOBILE_ACCOUNT")
    private String isMobileAccount;
    
    public Long getDeviceId() {
        return this.deviceId;
    }
    
    public void setDeviceId(final Long deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getIsLoggedIn() {
        return this.isLoggedIn;
    }
    
    public void setIsLoggedIn(final String isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }
    
    public String getIsMobileAccount() {
        return this.isMobileAccount;
    }
    
    public void setIsMobileAccount(final String isMobileAccount) {
        this.isMobileAccount = isMobileAccount;
    }
}
