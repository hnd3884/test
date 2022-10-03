package com.adventnet.authentication;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.io.Serializable;

public class Credential implements Serializable
{
    private Long userId;
    private Long loginId;
    private Long accountId;
    private Long sessionId;
    private Long localeId;
    private String loginName;
    private String serviceName;
    private String hostName;
    private TimeZone timeZone;
    private String domainName;
    private Locale locale;
    private List roles;
    private String authRuleName;
    
    public Credential() {
        this.userId = new Long(-1L);
        this.loginId = new Long(-1L);
        this.accountId = new Long(-1L);
        this.sessionId = new Long(-1L);
        this.localeId = new Long(-1L);
        this.loginName = null;
        this.serviceName = null;
        this.hostName = null;
        this.timeZone = null;
        this.domainName = null;
        this.locale = null;
        this.roles = new ArrayList();
        this.authRuleName = "Authenticator";
    }
    
    public Credential(final Long userid, final Long loginid, final Long accountid, final Long sessionId, final Long localeId, final String loginname, final String servicename, final String hostname, final TimeZone timezone, final Locale locale, final List roleList) {
        this(userid, loginid, accountid, sessionId, localeId, loginname, servicename, hostname, timezone, locale, roleList, null);
    }
    
    public Credential(final Long userid, final Long loginid, final Long accountid, final Long sessionId, final Long localeId, final String loginname, final String servicename, final String hostname, final TimeZone timezone, final Locale locale, final List roleList, final String domainname) {
        this.userId = new Long(-1L);
        this.loginId = new Long(-1L);
        this.accountId = new Long(-1L);
        this.sessionId = new Long(-1L);
        this.localeId = new Long(-1L);
        this.loginName = null;
        this.serviceName = null;
        this.hostName = null;
        this.timeZone = null;
        this.domainName = null;
        this.locale = null;
        this.roles = new ArrayList();
        this.authRuleName = "Authenticator";
        this.userId = userid;
        this.loginId = loginid;
        this.accountId = accountid;
        this.sessionId = sessionId;
        this.localeId = localeId;
        this.loginName = loginname;
        this.serviceName = servicename;
        this.hostName = hostname;
        this.timeZone = timezone;
        this.locale = locale;
        this.roles = roleList;
        this.domainName = domainname;
    }
    
    public String getDomainName() {
        return this.domainName;
    }
    
    public Long getUserId() {
        return this.userId;
    }
    
    public Long getLoginId() {
        return this.loginId;
    }
    
    public Long getAccountId() {
        return this.accountId;
    }
    
    public Long getSessionId() {
        return this.sessionId;
    }
    
    public Long getLocaleId() {
        return this.localeId;
    }
    
    public String getLoginName() {
        return (this.loginName == null) ? "unknown" : this.loginName;
    }
    
    public String getServiceName() {
        return this.serviceName;
    }
    
    public String getHostName() {
        return (this.hostName == null) ? "unknown" : this.hostName;
    }
    
    public List getRoles() {
        final List toReturn = new ArrayList();
        toReturn.addAll(this.roles);
        return toReturn;
    }
    
    public TimeZone getUserTimeZone() {
        return this.timeZone;
    }
    
    public Locale getUserLocale() {
        return this.locale;
    }
    
    public boolean isEmpty() {
        return this.userId == -1L && this.loginId == -1L && this.accountId == -1L;
    }
    
    @Override
    public String toString() {
        final StringBuffer strBuffer = new StringBuffer();
        if (this.isEmpty()) {
            strBuffer.append("Credential : []");
        }
        else {
            strBuffer.append("Credential : [");
            strBuffer.append("\n\tUserId      : " + this.userId);
            strBuffer.append("\n\tLoginId     : " + this.loginId);
            strBuffer.append("\n\tAccountId   : " + this.accountId);
            strBuffer.append("\n\tSessionId   : " + this.sessionId);
            strBuffer.append("\n\tLocaleId    : " + this.localeId);
            strBuffer.append("\n\tServiceName : " + this.serviceName);
            strBuffer.append("\n\tHostName    : " + this.hostName);
            strBuffer.append("\n\tUserRoles   : " + this.roles);
            strBuffer.append("\n\tTimeZone    : " + this.timeZone);
            strBuffer.append("\n\tLocale      : " + this.locale);
            strBuffer.append("]");
        }
        return strBuffer.toString();
    }
    
    public void setAuthRuleName(final String name) {
        this.authRuleName = name;
    }
    
    public String getAuthRuleName() {
        return this.authRuleName;
    }
}
