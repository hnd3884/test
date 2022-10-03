package com.adventnet.authentication.internal;

import java.util.Locale;
import java.util.TimeZone;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.io.Serializable;

public class WritableCredential implements Serializable
{
    String domainName;
    private Long userId;
    private Long loginId;
    private Long accountId;
    private Long sessionId;
    private String loginName;
    private String serviceName;
    private String hostName;
    private List roles;
    private String timeZone;
    private String countryCode;
    private String langCode;
    private Long localeId;
    private transient Hashtable hashTable;
    private String authRuleName;
    
    public WritableCredential() {
        this.domainName = null;
        this.userId = new Long(-1L);
        this.loginId = new Long(-1L);
        this.accountId = new Long(-1L);
        this.sessionId = new Long(-1L);
        this.loginName = null;
        this.serviceName = null;
        this.hostName = null;
        this.roles = new ArrayList();
        this.timeZone = null;
        this.countryCode = null;
        this.langCode = null;
        this.localeId = null;
        this.hashTable = new Hashtable();
        this.authRuleName = null;
    }
    
    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }
    
    public String getDomainName() {
        return this.domainName;
    }
    
    public void setUserId(final Long userId) {
        this.userId = userId;
    }
    
    public Long getUserId() {
        return this.userId;
    }
    
    public void setLoginId(final Long loginId) {
        this.loginId = loginId;
    }
    
    public Long getLoginId() {
        return this.loginId;
    }
    
    public void setAccountId(final Long accountId) {
        this.accountId = accountId;
    }
    
    public Long getAccountId() {
        return this.accountId;
    }
    
    public void setSessionId(final Long sessionid) {
        this.sessionId = sessionid;
    }
    
    public Long getSessionId() {
        return this.sessionId;
    }
    
    public void setLoginName(final String loginName) {
        this.loginName = loginName;
    }
    
    public String getLoginName() {
        return (this.loginName == null) ? "unknown" : this.loginName;
    }
    
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getServiceName() {
        return this.serviceName;
    }
    
    public void setHostName(final String hostName) {
        this.hostName = hostName;
    }
    
    public String getHostName() {
        return (this.hostName == null) ? "unknown" : this.hostName;
    }
    
    public void addRole(final String roleName) {
        if (!this.roles.contains(roleName)) {
            this.roles.add(roleName);
        }
    }
    
    public void addRoles(final List roleList) {
        if (roleList != null) {
            this.roles.addAll(roleList);
        }
    }
    
    public void removeRole(final String roleName) {
        this.roles.remove(roleName);
    }
    
    public void removeRoles(final List roleList) {
        if (roleList != null) {
            this.roles.removeAll(roleList);
        }
    }
    
    public List getRoles() {
        return this.roles;
    }
    
    public void setTimeZone(final String timezone) {
        this.timeZone = timezone;
    }
    
    public TimeZone getTimeZone() {
        if (this.timeZone != null) {
            return TimeZone.getTimeZone(this.timeZone);
        }
        return TimeZone.getDefault();
    }
    
    public void setCountryCode(final String code) {
        this.countryCode = code;
    }
    
    public void setLangCode(final String code) {
        this.langCode = code;
    }
    
    public Locale getLocale() {
        if (this.countryCode != null && this.langCode != null) {
            return new Locale(this.langCode, this.countryCode);
        }
        return Locale.getDefault();
    }
    
    public void setLocaleId(final Long localeID) {
        this.localeId = localeID;
    }
    
    public Long getLocaleId() {
        return (this.localeId == null) ? -1L : this.localeId;
    }
    
    public void addToHashTable(final Object key, final Object value) {
        this.hashTable.put(key, value);
    }
    
    public Object getFromHashTable(final Object key) {
        return this.hashTable.get(key);
    }
    
    public String getAuthRuleName() {
        return this.authRuleName;
    }
    
    public void setAuthRuleName(final String authRuleName) {
        this.authRuleName = authRuleName;
    }
    
    @Override
    public String toString() {
        final StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("WritableCredential : [");
        strBuffer.append("\n\tUserId      : " + this.userId);
        strBuffer.append("\n\tLoginId     : " + this.loginId);
        strBuffer.append("\n\tAccountId   : " + this.accountId);
        strBuffer.append("\n\tSessionId   : " + this.sessionId);
        strBuffer.append("\n\tLocaleId    : " + this.localeId);
        strBuffer.append("\n\tServiceName : " + this.serviceName);
        strBuffer.append("\n\tTimeZone    : " + this.timeZone);
        strBuffer.append("\n\tLocale      : " + this.langCode + "/" + this.countryCode);
        strBuffer.append("\n\tHostName    : " + this.hostName);
        strBuffer.append("\n\tUserRoles   : " + this.getRoles());
        strBuffer.append("\n\tHashtable  : " + this.hashTable + " ]");
        return strBuffer.toString();
    }
}
