package com.adventnet.iam.security;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;

public interface Authenticator
{
    public static final String ZSEC_AUTH_TYPE = "ZSEC_AUTH_TYPE";
    
    void init(final FilterConfig p0) throws ServletException;
    
    void init(final SecurityFilterProperties p0) throws ServletException;
    
    boolean authenticate(final HttpServletRequest p0, final HttpServletResponse p1) throws ServletException, IOException;
    
    void cleanupIAMCredentials(final HttpServletRequest p0);
    
    String getRemoteAddr(final HttpServletRequest p0, final String p1);
    
    boolean isTrustedIP(final HttpServletRequest p0);
    
    String GET_IAM_COOKIE(final HttpServletRequest p0);
    
    String GET_IAM_STATELESS_COOKIE(final HttpServletRequest p0);
    
    String getIAMCookieDomain();
    
    String getServicePublicKey(final String p0);
    
    String getServicePublicKey(final String p0, final String p1);
    
    Map<String, String> getRequestComponentBlackListPatterns();
    
    boolean isTrustedDomain(final String p0);
    
    List<String> getClientOAuthTrustedDomains();
    
    long getUserRegisteredTime(final String p0);
    
    long getUserRegisteredTime(final String p0, final HttpServletRequest p1, final HttpServletResponse p2);
    
    String encrypt(final String p0, final String p1, final String p2);
    
    String decrypt(final String p0, final String p1) throws Exception;
    
    String sha512Hex(final String p0);
    
    boolean isSecurityCacheEnabled();
    
    Object getFromSecurityCache(final String p0);
    
    boolean putInSecurityCache(final String p0, final String p1);
    
    boolean clearFromSecurityCache(final String p0);
    
    List<String> getAuthCommonConfigurationFiles();
    
    String getIAMAppSystemConfigurationValue(final String p0);
    
    boolean isValidWebhookRequest(final HttpServletRequest p0);
    
    boolean authenticateViaSecuritySystemSignature(final HttpServletRequest p0);
    
    List<String> getDataSpacesForCurrentUser(final HttpServletRequest p0);
    
    String getAuthenticatedUserId(final SecurityRequestWrapper p0, final ThrottlesRule.UserKeysName p1);
    
    void validateCSRFTokenForWebApiURL(final HttpServletRequest p0, final HttpServletResponse p1, final ActionRule p2);
    
    String getCurrentUserId(final SecurityRequestWrapper p0);
    
    boolean isUserInRole(final SecurityRequestWrapper p0, final String p1);
    
    boolean isUserInGroup(final SecurityRequestWrapper p0, final String p1);
    
    String getServiceNameForDomain(final String p0);
    
    void handleClientPortalRequest(final HttpServletRequest p0, final HttpServletResponse p1);
    
    Object initAccountsAttributes(final ActionRule p0);
    
    public enum AUTH_TYPE
    {
        PASSWORD("password"), 
        TOKEN("token");
        
        private String value;
        
        private AUTH_TYPE(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
}
