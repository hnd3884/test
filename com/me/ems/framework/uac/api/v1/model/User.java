package com.me.ems.framework.uac.api.v1.model;

import java.util.Collection;
import java.util.ArrayList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Locale;
import java.util.TimeZone;
import java.util.List;
import java.util.Map;
import java.security.Principal;

public class User implements Principal
{
    private String name;
    private String domainName;
    private String displayName;
    private Long userID;
    private Long loginID;
    private Map<String, List> roles;
    private Long scope;
    private boolean isUserActive;
    private TimeZone userTimeZone;
    private Locale userLocale;
    private String authToken;
    private String encodedPassword;
    private String authType;
    private String email;
    private String phoneNumber;
    private String timeFormat;
    private String dateFormat;
    
    public String getDateFormat() {
        return this.dateFormat;
    }
    
    public void setDateFormat(final String dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }
    
    public String getDomainName() {
        return this.domainName;
    }
    
    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }
    
    public Long getUserID() {
        return this.userID;
    }
    
    public void setUserID(final Long userID) {
        this.userID = userID;
    }
    
    public Long getLoginID() {
        return this.loginID;
    }
    
    public void setLoginID(final Long loginID) {
        this.loginID = loginID;
    }
    
    public Map<String, List> getRoles() {
        return this.roles;
    }
    
    public void setRoles(final Map<String, List> roles) {
        this.roles = roles;
    }
    
    public Long getScope() {
        return this.scope;
    }
    
    public void setScope(final Long scope) {
        this.scope = scope;
    }
    
    public boolean isUserActive() {
        return this.isUserActive;
    }
    
    public void setUserActive(final boolean userActive) {
        this.isUserActive = userActive;
    }
    
    public TimeZone getUserTimeZone() {
        return this.userTimeZone;
    }
    
    public void setUserTimeZone(final TimeZone userTimeZone) {
        this.userTimeZone = userTimeZone;
    }
    
    public Locale getUserLocale() {
        return this.userLocale;
    }
    
    public void setUserLocale(final Locale userLocale) {
        this.userLocale = userLocale;
    }
    
    public String getAuthToken() {
        return this.authToken;
    }
    
    public void setAuthToken(final String authToken) {
        this.authToken = authToken;
    }
    
    public String getEncodedPassword() {
        return this.encodedPassword;
    }
    
    @JsonIgnore
    public void setEncodedPassword(final String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }
    
    public String getAuthType() {
        return this.authType;
    }
    
    @JsonIgnore
    public void setAuthType(final String authType) {
        this.authType = authType;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(final String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getTimeFormat() {
        return this.timeFormat;
    }
    
    public void setTimeFormat(final String timeFormat) {
        this.timeFormat = timeFormat;
    }
    
    public boolean isUserInRole(final String roleName) {
        if (this.roles == null || this.roles.isEmpty()) {
            return Boolean.FALSE;
        }
        if (roleName.endsWith("_read") || roleName.endsWith("_Read")) {
            return this.roles.get("read").contains(roleName);
        }
        if (roleName.endsWith("_write") || roleName.endsWith("_Write")) {
            return this.roles.get("write").contains(roleName);
        }
        if (roleName.endsWith("_admin") || roleName.endsWith("_Admin")) {
            return this.roles.get("admin").contains(roleName);
        }
        return this.roles.get("general").contains(roleName);
    }
    
    public boolean isAdminUser() {
        return this.isUserInRole("Common_Write");
    }
    
    public List<String> getAllRoles() {
        final List<String> allRoles = new ArrayList<String>();
        this.roles.forEach((key, value) -> list.addAll(value));
        return allRoles;
    }
}
