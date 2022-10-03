package com.me.ems.framework.uac.api.v1.model;

public class AuthUser
{
    String userName;
    String encodedPassword;
    String domainName;
    String authType;
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getEncodedPassword() {
        return this.encodedPassword;
    }
    
    public void setEncodedPassword(final String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }
    
    public String getDomainName() {
        return this.domainName;
    }
    
    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }
    
    public String getAuthType() {
        return this.authType;
    }
    
    public void setAuthType(final String authType) {
        this.authType = authType;
    }
}
