package com.zoho.security.util;

import java.util.ArrayList;
import java.net.HttpURLConnection;
import com.adventnet.iam.security.SecurityFrameworkUtil;
import java.util.Set;
import com.zoho.conf.ConfigurationListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.logging.Level;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_TLS_ERROPAGE_REDIRECTION;
import com.adventnet.iam.security.SecurityUtil;
import com.zoho.conf.Configuration;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.iam.security.SecurityFilterProperties;
import java.util.logging.Logger;
import java.util.List;

public class SASConfigProviderUtil
{
    private static final String SSL_VERSION_HEADER = "lb_ssl_version";
    private static final String UTF_8 = "UTF-8";
    private static final List<String> TLS_VERSION_WHITELIST;
    private static final Logger LOGGER;
    private static final String ENABLE_PROPERTY = "zsec.tls.error.redirect.enable";
    public static final String EXCLUDE_DOMAINS_PROPERTY = "zsec.tls.error.excludedomains";
    public static final String RESPONSE_PROPERTY = "zsec.tls.error.response";
    public static List<String> excludeDomains;
    public static List<String> updatedExcludeDomains;
    public static String errorResponse;
    public static String updatedErrorResponse;
    private static String defaultMessage;
    
    public static boolean checkForTLSErrorRedirection(final SecurityFilterProperties securityFilterConfig, final HttpServletRequest request, final HttpServletResponse serResponse) {
        try {
            if (securityFilterConfig.getSASConfigProvider() != null && Configuration.getBoolean("zsec.tls.error.redirect.enable", "false")) {
                final String sslHeader = request.getHeader("lb_ssl_version");
                if (SecurityUtil.isValid(sslHeader) && !SASConfigProviderUtil.TLS_VERSION_WHITELIST.contains(sslHeader)) {
                    if (SASConfigProviderUtil.excludeDomains == null) {
                        getAndRegisterConfigurationProperties();
                    }
                    final List<String> excludeDomainList = getExcludedDomains();
                    final String serverName = SecurityUtil.getServerName(request);
                    if (!excludeDomainList.contains(serverName)) {
                        if (securityFilterConfig.getResponseEncoding() == null) {
                            serResponse.setCharacterEncoding("UTF-8");
                        }
                        final String errorMessage = getErrorResponse();
                        if (errorMessage != null) {
                            serResponse.getWriter().println(errorMessage);
                        }
                        else {
                            serResponse.getWriter().println(SASConfigProviderUtil.defaultMessage);
                        }
                        serResponse.setStatus(403);
                        ZSEC_TLS_ERROPAGE_REDIRECTION.pushRedirectionSuccess(serverName, sslHeader, (ExecutionTimer)null);
                        return true;
                    }
                }
            }
        }
        catch (final Exception e) {
            SASConfigProviderUtil.LOGGER.log(Level.WARNING, " Exception Occurred while redirecting to tls error page , Exception {0}", new Object[] { e });
        }
        return false;
    }
    
    private static String getErrorResponse() {
        if (SASConfigProviderUtil.updatedErrorResponse != null) {
            SASConfigProviderUtil.errorResponse = SASConfigProviderUtil.updatedErrorResponse;
            SASConfigProviderUtil.updatedErrorResponse = null;
        }
        return SASConfigProviderUtil.errorResponse;
    }
    
    private static List<String> getExcludedDomains() {
        if (SASConfigProviderUtil.updatedExcludeDomains != null) {
            SASConfigProviderUtil.excludeDomains = SASConfigProviderUtil.updatedExcludeDomains;
            SASConfigProviderUtil.updatedExcludeDomains = null;
        }
        return SASConfigProviderUtil.excludeDomains;
    }
    
    private static synchronized void getAndRegisterConfigurationProperties() {
        if (SASConfigProviderUtil.excludeDomains == null) {
            SASConfigProviderUtil.excludeDomains = getExcludedDomainsAsList(Configuration.getString("zsec.tls.error.excludedomains"));
            SASConfigProviderUtil.errorResponse = getErrorResponseFromServer(Configuration.getString("zsec.tls.error.response"));
            try {
                Configuration.registerKeys((Set)new HashSet(Arrays.asList("zsec.tls.error.excludedomains", "zsec.tls.error.response")), (ConfigurationListener)Thread.currentThread().getContextClassLoader().loadClass("com.zoho.security.listener.TLSErrorRedirectListener").newInstance());
            }
            catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                SASConfigProviderUtil.LOGGER.log(Level.WARNING, " Exception Occurred while registering keys in Configuration Listener , Exception {0}", new Object[] { e });
            }
        }
    }
    
    public static String getErrorResponseFromServer(final String url) {
        try {
            final HttpURLConnection conn = SecurityFrameworkUtil.getURLConnection(url, null, "GET");
            final int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                return SecurityUtil.convertInputStreamAsString(conn.getInputStream(), -1L);
            }
            SASConfigProviderUtil.LOGGER.log(Level.WARNING, "Unable to get errorpage from remote url {0} , status code : {1} ", new Object[] { url, responseCode });
        }
        catch (final Exception e) {
            SASConfigProviderUtil.LOGGER.log(Level.WARNING, "Unable to get errorpage from remote url {0} , ex : {1} ", new Object[] { url, e });
        }
        return null;
    }
    
    public static List<String> getExcludedDomainsAsList(final String domainStr) {
        List<String> domainList = new ArrayList<String>();
        if (SecurityUtil.isValid(domainStr)) {
            domainList = Arrays.asList(domainStr.split(","));
        }
        return domainList;
    }
    
    static {
        TLS_VERSION_WHITELIST = Arrays.asList("TLSv1.2", "TLSv1.3");
        LOGGER = Logger.getLogger(SASConfigProviderUtil.class.getName());
        SASConfigProviderUtil.excludeDomains = null;
        SASConfigProviderUtil.updatedExcludeDomains = null;
        SASConfigProviderUtil.errorResponse = null;
        SASConfigProviderUtil.updatedErrorResponse = null;
        SASConfigProviderUtil.defaultMessage = null;
        SASConfigProviderUtil.defaultMessage = "<html><body><p>Your browser is outdated since it does not support TLS 1.2, and accessing Zoho Services through it is not supported.<a href=\"https://www.zoho.com/general/blog/end-of-support-for-older-tls-versions-in-zoho.html\">Read more</a></p></body></html>";
    }
}
