package com.adventnet.iam.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.zoho.security.util.HashUtil;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;

public class ZSecAuthenticationProviderImpl extends DefaultAuthenticationProviderImpl
{
    private static final long USER_REGISTERD_TIME = 1523015887877L;
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }
    
    @Override
    public final void init(final SecurityFilterProperties secFilterProps) throws ServletException {
    }
    
    @Override
    public final boolean authenticate(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (this.isAuthenticated(request, response)) {
            final ActionRule rule = (ActionRule)request.getAttribute("urlrule");
            this.validateCSRFTokenForWebApiURL(request, response, rule);
            DoSController.controlDoS(request, response, rule);
            SecurityFilterProperties.getInstance(request).getSecurityProvider().authorize(request, response, rule);
            return true;
        }
        throw new IAMSecurityException("AUTHENTICATION_FAILED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
    }
    
    public boolean isAuthenticated(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        return false;
    }
    
    @Override
    public void cleanupIAMCredentials(final HttpServletRequest request) {
    }
    
    @Override
    public String getRemoteAddr(final HttpServletRequest request, final String remoteIp) {
        return remoteIp;
    }
    
    @Override
    public boolean isTrustedIP(final HttpServletRequest request) {
        return SecurityFilterProperties.isTrustedIP(request.getRemoteAddr());
    }
    
    @Override
    public String GET_IAM_COOKIE(final HttpServletRequest request) {
        return SecurityUtil.getCookie(request, this.getSessionCookieName(request));
    }
    
    public String getSessionCookieName(final HttpServletRequest request) {
        return "JSESSIONID";
    }
    
    @Override
    public String getIAMCookieDomain() {
        return null;
    }
    
    @Override
    public final String decrypt(final String decrypt_label, final String parameterValue) throws Exception {
        return null;
    }
    
    @Override
    public String sha512Hex(final String input) {
        return HashUtil.SHA512(input);
    }
    
    @Override
    public String getServicePublicKey(final String serviceName) {
        return null;
    }
    
    @Override
    public String getServicePublicKey(final String serviceName, final String dcLocation) {
        return null;
    }
    
    @Override
    public final Map<String, String> getRequestComponentBlackListPatterns() {
        return null;
    }
    
    @Override
    public long getUserRegisteredTime(final String iamTicket) {
        return 1523015887877L;
    }
    
    @Override
    public final String encrypt(final String password, final String salt, final String algorithm) {
        return HashUtil.hash(password, salt, algorithm);
    }
    
    @Override
    public boolean isTrustedDomain(final String domain) {
        return false;
    }
    
    @Override
    public boolean isSecurityCacheEnabled() {
        return false;
    }
    
    @Override
    public Object getFromSecurityCache(final String key) {
        return null;
    }
    
    @Override
    public boolean putInSecurityCache(final String key, final String value) {
        return false;
    }
    
    @Override
    public boolean clearFromSecurityCache(final String key) {
        return false;
    }
    
    @Override
    public List<String> getAuthCommonConfigurationFiles() {
        return new ArrayList<String>();
    }
    
    @Override
    public final String getIAMAppSystemConfigurationValue(final String key) {
        return "";
    }
    
    @Override
    public final boolean isValidWebhookRequest(final HttpServletRequest request) {
        return false;
    }
    
    @Override
    public List<String> getClientOAuthTrustedDomains() {
        return null;
    }
    
    @Override
    public final boolean authenticateViaSecuritySystemSignature(final HttpServletRequest request) {
        return false;
    }
    
    @Override
    public List getDataSpacesForCurrentUser(final HttpServletRequest request) {
        return null;
    }
    
    @Override
    public String getAuthenticatedUserId(final SecurityRequestWrapper request, final ThrottlesRule.UserKeysName userkeyName) {
        return null;
    }
    
    @Override
    public final void validateCSRFTokenForWebApiURL(final HttpServletRequest request, final HttpServletResponse response, final ActionRule rule) {
        if (rule.isAPI() && Authenticator.AUTH_TYPE.PASSWORD.getValue().equals(request.getAttribute("ZSEC_AUTH_TYPE"))) {
            if (SecurityFilterProperties.getInstance(request).isCSRFCheckDisabledForGetApi() && "get".equalsIgnoreCase(request.getMethod())) {
                return;
            }
            final String queryString = request.getQueryString();
            final String queryParam = SecurityUtil.getCSRFParamName(request) + "=" + request.getParameter(SecurityUtil.getCSRFParamName(request));
            if (queryString != null && queryString.indexOf(queryParam) != -1) {
                throw new RuntimeException("CSRF Parameter should be sent only via  post request and it should not be present in the Query string.");
            }
            if (!rule.validateCSRFToken((SecurityRequestWrapper)request, response)) {
                throw new IAMSecurityException("INVALID_CSRF_TOKEN", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
            }
        }
    }
    
    @Override
    public String getServiceNameForDomain(final String domain) {
        return "";
    }
}
