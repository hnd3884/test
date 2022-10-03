package com.me.ems.onpremise.security.securegatewayserver.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailInformation
{
    private Boolean isMailServerConfigured;
    private Boolean enabled;
    private String emailAddress;
    
    @JsonProperty("isMailServerConfigured")
    public Boolean getIsMailServerConfigured() {
        return this.isMailServerConfigured;
    }
    
    public void setIsMailServerConfigured(final Boolean isMailServerConfigured) {
        this.isMailServerConfigured = isMailServerConfigured;
    }
    
    @JsonProperty("isMailNotificationEnabled")
    public Boolean getEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getEmailAddress() {
        return this.emailAddress;
    }
    
    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    @Override
    public String toString() {
        return "EmailInformation{mail_not_configured=" + this.isMailServerConfigured + ", enabled=" + this.enabled + ", EmailAddress='" + this.emailAddress + '\'' + '}';
    }
}
