package com.adventnet.iam.security;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;

public class DefaultAuthenticationProviderImpl implements Authenticator
{
    private static final Logger LOGGER;
    private static final String OPERATION_NOT_ALLOWED = "Operation Not Allowed";
    
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public void init(final SecurityFilterProperties secFilterProps) throws ServletException {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public boolean authenticate(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public void cleanupIAMCredentials(final HttpServletRequest request) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public String getRemoteAddr(final HttpServletRequest request, final String remoteIp) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public boolean isTrustedIP(final HttpServletRequest request) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public String GET_IAM_COOKIE(final HttpServletRequest request) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public String GET_IAM_STATELESS_COOKIE(final HttpServletRequest request) {
        return null;
    }
    
    @Override
    public String getIAMCookieDomain() {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public String decrypt(final String decrypt_label, final String parameterValue) throws Exception {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public String sha512Hex(final String input) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public String getServicePublicKey(final String serviceName) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public String getServicePublicKey(final String serviceName, final String dcLocation) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public Map<String, String> getRequestComponentBlackListPatterns() {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public long getUserRegisteredTime(final String iamTicket) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public long getUserRegisteredTime(final String iamTicket, final HttpServletRequest request, final HttpServletResponse response) {
        return this.getUserRegisteredTime(iamTicket);
    }
    
    @Override
    public String encrypt(final String password, final String salt, final String algorithm) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public boolean isTrustedDomain(final String domain) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public boolean isSecurityCacheEnabled() {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public Object getFromSecurityCache(final String key) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public boolean putInSecurityCache(final String key, final String value) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public boolean clearFromSecurityCache(final String key) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public List<String> getAuthCommonConfigurationFiles() {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public String getIAMAppSystemConfigurationValue(final String key) {
        throw new UnsupportedOperationException("Operation Not Allowed");
    }
    
    @Override
    public boolean isValidWebhookRequest(final HttpServletRequest request) {
        return false;
    }
    
    @Override
    public List<String> getClientOAuthTrustedDomains() {
        return null;
    }
    
    @Override
    public boolean authenticateViaSecuritySystemSignature(final HttpServletRequest request) {
        final ActionRule rule = (ActionRule)request.getAttribute("urlrule");
        if (!((SecurityRequestWrapper)request).isProxyRequest()) {
            return rule.checkForSystemAuthentication(request, "optional");
        }
        DefaultAuthenticationProviderImpl.LOGGER.log(Level.SEVERE, "\" internal / scoped-services\"  url cannot be invoked via security proxy ::  the request uri   \"{0}\"", request.getRequestURI());
        return false;
    }
    
    @Override
    public List getDataSpacesForCurrentUser(final HttpServletRequest request) {
        return null;
    }
    
    @Override
    public String getAuthenticatedUserId(final SecurityRequestWrapper request, final ThrottlesRule.UserKeysName userkeyName) {
        String userId = null;
        switch (userkeyName) {
            case ZUID: {
                if (SecurityUtil.isValid(request.getOrgUser())) {
                    userId = request.getOrgUser();
                    break;
                }
                if (request.getUserPrincipal() != null) {
                    userId = request.getUserPrincipal().getName();
                    break;
                }
                break;
            }
            case AUTHTOKEN: {
                userId = ((request.getHeader("Authorization") != null) ? request.getHeader("Authorization") : request.getHeader("Z-Authorization"));
                if (userId != null) {
                    final int index = userId.trim().indexOf(32);
                    final boolean authScheme = index != -1 && userId.substring(0, index).equalsIgnoreCase("Zoho-authtoken");
                    userId = (authScheme ? userId.substring(index + 1).trim() : null);
                    break;
                }
                userId = ((request.getParameter("authtoken") != null) ? request.getParameter("authtoken") : request.getParameter("AUTHTOKEN"));
                break;
            }
            case OAUTHTOKEN: {
                userId = ((request.getHeader("Authorization") != null) ? request.getHeader("Authorization") : request.getHeader("Z-Authorization"));
                if (userId != null) {
                    final int index = userId.trim().indexOf(32);
                    final boolean authScheme = index != -1 && userId.substring(0, index).equalsIgnoreCase("Zoho-oauthtoken");
                    userId = (authScheme ? userId.substring(index + 1).trim() : null);
                    break;
                }
                break;
            }
        }
        return userId;
    }
    
    @Override
    public void validateCSRFTokenForWebApiURL(final HttpServletRequest request, final HttpServletResponse response, final ActionRule rule) {
    }
    
    @Override
    public String getCurrentUserId(final SecurityRequestWrapper request) {
        return (String)request.getAttribute("IAM_ZUID");
    }
    
    @Override
    public boolean isUserInRole(final SecurityRequestWrapper request, final String roleName) {
        return request.isUserInRole(roleName);
    }
    
    @Override
    public boolean isUserInGroup(final SecurityRequestWrapper request, final String groupName) {
        return false;
    }
    
    @Override
    public String getServiceNameForDomain(final String domain) {
        try {
            final URL urlObj = new URL(domain + "/getzsecservicename");
            final HttpURLConnection conn = (HttpURLConnection)urlObj.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("zsec_iframe_verification_request", "true");
            if (conn instanceof HttpsURLConnection && SecurityFilterProperties.getInstance(SecurityUtil.getCurrentRequest()).isDevelopmentMode()) {
                skipSSLValidation((HttpsURLConnection)conn);
            }
            conn.connect();
            if (conn.getResponseCode() == 200) {
                return SecurityUtil.getResponseAsString(conn.getInputStream());
            }
        }
        catch (final Exception ex) {
            DefaultAuthenticationProviderImpl.LOGGER.log(Level.WARNING, "Error occurred while getting service name for this domain : {0} in XFrame trusted service verification, Exception : {1} ", new Object[] { domain, ex.getMessage() });
        }
        return "";
    }
    
    private static void skipSSLValidation(final HttpsURLConnection conn) throws NoSuchAlgorithmException, KeyManagementException {
        final TrustManager[] trustAllCerts = { new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                
                @Override
                public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
                }
                
                @Override
                public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
                }
            } };
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        conn.setSSLSocketFactory(sc.getSocketFactory());
        conn.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(final String urlHostName, final SSLSession session) {
                return true;
            }
        });
    }
    
    @Override
    public void handleClientPortalRequest(final HttpServletRequest request, final HttpServletResponse response) {
    }
    
    @Override
    public Object initAccountsAttributes(final ActionRule rule) {
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(DefaultAuthenticationProviderImpl.class.getName());
    }
}
