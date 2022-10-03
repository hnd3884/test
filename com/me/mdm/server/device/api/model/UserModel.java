package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserModel
{
    @JsonAlias({ "managed_user_id", "resource_id" })
    @JsonProperty("user_id")
    public Long userId;
    @JsonAlias({ "email_address" })
    @JsonProperty("user_email")
    public String userEmail;
    @JsonAlias({ "name" })
    @JsonProperty("user_name")
    public String userName;
    @JsonAlias({ "domain_netbios_name" })
    @JsonProperty("domain_name")
    public String domainName;
    @JsonProperty("phone_number")
    public String phoneNumber;
    
    public Long getUserId() {
        return this.userId;
    }
    
    public void setUserId(final Long userId) {
        this.userId = userId;
    }
    
    public String getUserEmail() {
        return this.userEmail;
    }
    
    public void setUserEmail(final String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getDomainName() {
        return this.domainName;
    }
    
    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }
    
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
