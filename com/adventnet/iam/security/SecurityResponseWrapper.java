package com.adventnet.iam.security;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Arrays;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_INVALID_CLICKJACKING_PREVENTION;
import java.util.ArrayList;
import com.zoho.security.agent.AppSenseAgent;
import java.util.Collection;
import java.util.Iterator;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.net.URI;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.List;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponseWrapper;

public class SecurityResponseWrapper extends HttpServletResponseWrapper
{
    private static final Logger LOGGER;
    private static final String XFRAME_HEADER_NAME = "X-Frame-Options";
    private static final String CSP_HEADER_NAME = "Content-Security-Policy";
    private static final String CSP_REPORT_ONLY_HEADER_NAME = "Content-Security-Policy-Report-Only";
    public static final String API_RATE_LIMIT_HEADER_NAME = "X-Rate-Limit";
    private static final String XFRAME_EXCLUDE_COOKIE_NAME = "_zxor";
    private static final Pattern REDIRECT_EXCLUDE_PATTERN;
    private static final List<String> SPECIALHEADERS;
    private static final List<String> CACHE_HEADERS_LIST;
    private static final List<String> CSP_AND_COOKIE_HEADERS;
    private static final int DEFAULT_SIZE = 5;
    private boolean isAPIAuthenticatedViaCookie;
    private boolean userConfigControlledResponseHeadersSet;
    private SecurityPrintWriter printWriter;
    private SecurityServletOutPutStream servletOutputStream;
    private Map<String, ResponseHeaderRule> configuredHeaders;
    private Map<String, Object> responseInfo;
    private static final String ALLOW_FULL_SERVICE_INTEGRATION = "allowfullintegration";
    
    public SecurityResponseWrapper(final HttpServletResponse response) {
        super(response);
        this.isAPIAuthenticatedViaCookie = false;
        this.printWriter = null;
        this.servletOutputStream = null;
        this.configuredHeaders = null;
        this.responseInfo = null;
    }
    
    public void addDateHeader(final String name, final long date) {
        if (this.isConfiguredHeader(name)) {
            return;
        }
        super.addDateHeader(name, date);
    }
    
    public void addIntHeader(final String name, final int value) {
        if (this.isConfiguredHeader(name)) {
            return;
        }
        super.addIntHeader(name, value);
    }
    
    public void addHeader(final String name, final String value) {
        if (this.isConfiguredHeader(name)) {
            return;
        }
        super.addHeader(name, value);
    }
    
    public void setDateHeader(final String name, final long date) {
        if (this.isConfiguredHeader(name)) {
            return;
        }
        super.setDateHeader(name, date);
    }
    
    public void setIntHeader(final String name, final int value) {
        if (this.isConfiguredHeader(name)) {
            return;
        }
        super.setIntHeader(name, value);
    }
    
    public void setHeader(final String name, final String value) {
        if (!SecurityResponseWrapper.CACHE_HEADERS_LIST.contains(name) && this.isConfiguredHeader(name)) {
            return;
        }
        super.setHeader(name, value);
    }
    
    void setHeaderToSuper(final String name, final String value) {
        super.setHeader(name, value);
    }
    
    void addHeaderToSuper(final String name, final String value) {
        super.addHeader(name, value);
    }
    
    public void sendRedirect(String location) throws IOException {
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        if (SecurityUtil.isValid(location) && request != null && SecurityFilterProperties.getInstance(request).addURLParamFrameoriginToRedirectURL()) {
            final String frameoriginParamValue = request.getParameter("frameorigin");
            if (frameoriginParamValue != null) {
                location = this.appendFrameoriginParamToRedirectUrl(location, frameoriginParamValue);
            }
        }
        if (this.isConfiguredHeader(ResponseHeader.NAME.LOCATION.getName())) {
            return;
        }
        super.sendRedirect(location);
    }
    
    private String appendFrameoriginParamToRedirectUrl(String redirectUrl, final String frameoriginParamValue) {
        URI uri = null;
        try {
            uri = new URI(redirectUrl);
        }
        catch (final URISyntaxException e) {
            SecurityResponseWrapper.LOGGER.log(Level.WARNING, "Invalid Redirect URL");
        }
        if (uri != null) {
            boolean appendFrameoriginParam = false;
            if (SecurityUtil.isValidScheme(uri.getScheme()) || redirectUrl.startsWith("//")) {
                String redirectUrlDomain = uri.getHost();
                if (redirectUrlDomain != null && uri.getPort() != -1) {
                    redirectUrlDomain = redirectUrlDomain + ":" + Integer.toString(uri.getPort());
                }
                if (SecurityUtil.getRequestDomain().equals(redirectUrlDomain)) {
                    appendFrameoriginParam = true;
                }
            }
            else if (uri.getScheme() == null && uri.getHost() == null) {
                appendFrameoriginParam = true;
            }
            if (appendFrameoriginParam) {
                final String queryString = uri.getQuery();
                if (queryString == null || !queryString.contains("frameorigin=")) {
                    String separator = "";
                    if (queryString == null) {
                        separator = "?";
                    }
                    else if (queryString.length() > 0) {
                        separator = "&";
                    }
                    final String foParamValue = separator + "frameorigin=" + frameoriginParamValue;
                    final String fragment = uri.getFragment();
                    if (fragment == null) {
                        redirectUrl += foParamValue;
                    }
                    else {
                        redirectUrl = redirectUrl.substring(0, redirectUrl.indexOf("#")) + foParamValue + "#" + fragment;
                    }
                }
            }
        }
        return redirectUrl;
    }
    
    public void setContentType(final String type) {
        if (this.isConfiguredHeader(ResponseHeader.NAME.CONTENT_TYPE.getName())) {
            return;
        }
        super.setContentType(type);
    }
    
    public void setContentLength(final int len) {
        if (this.isConfiguredHeader(ResponseHeader.NAME.CONTENT_LENGTH.getName())) {
            return;
        }
        super.setContentLength(len);
    }
    
    public void setContentLengthLong(final long length) {
        if (this.isConfiguredHeader(ResponseHeader.NAME.CONTENT_LENGTH.getName())) {
            return;
        }
        super.setContentLengthLong(length);
    }
    
    public void reset() {
        super.reset();
        this.setAllResponseHeaders();
    }
    
    public ServletOutputStream getOutputStream() throws IOException {
        if (this.servletOutputStream == null) {
            this.servletOutputStream = new SecurityServletOutPutStream(super.getOutputStream());
            final SecurityRequestWrapper securedRequest = (SecurityRequestWrapper)SecurityUtil.getCurrentRequest();
            if (securedRequest != null) {
                final String jsonImpurity = this.getJsonImpurity(securedRequest);
                if (SecurityUtil.isValid(jsonImpurity)) {
                    this.servletOutputStream.write(jsonImpurity.getBytes());
                }
            }
        }
        return this.servletOutputStream;
    }
    
    public PrintWriter getWriter() throws IOException {
        if (this.printWriter == null) {
            this.printWriter = new SecurityPrintWriter(super.getWriter());
            final SecurityRequestWrapper securedRequest = (SecurityRequestWrapper)SecurityUtil.getCurrentRequest();
            if (securedRequest != null) {
                final ResponseLogRule logRule = this.getResponseLogRule(securedRequest);
                this.printWriter.logRule = logRule;
                final String jsonImpurity = this.getJsonImpurity(securedRequest);
                if (SecurityUtil.isValid(jsonImpurity)) {
                    this.printWriter.append(jsonImpurity);
                }
            }
        }
        return this.printWriter;
    }
    
    public SecurityPrintWriter getPrintWriter() {
        return this.printWriter;
    }
    
    public SecurityServletOutPutStream getServletOutputStream() {
        return this.servletOutputStream;
    }
    
    void setCacheControlHeaders(final SecurityFilterProperties securityFilterConfig, final SecurityRequestWrapper securedRequest) {
        List<String> disabledCacheHeaders = null;
        if (securedRequest != null) {
            final ActionRule actionRule = (ActionRule)securedRequest.getAttribute("urlrule");
            if (actionRule != null && actionRule.getDisabledCacheHeaders() != null) {
                disabledCacheHeaders = actionRule.getDisabledCacheHeaders();
            }
        }
        if (disabledCacheHeaders != null && disabledCacheHeaders.contains("all")) {
            return;
        }
        final List<ResponseHeaderRule> cacheControlHeaders = securityFilterConfig.getSafeResponseHeaderRules("cache-headers");
        for (final ResponseHeaderRule cacheHeaderRule : cacheControlHeaders) {
            if (disabledCacheHeaders != null && disabledCacheHeaders.contains(cacheHeaderRule.getHeaderName())) {
                continue;
            }
            this.setHeaderToSuper(cacheHeaderRule.getHeaderName(), cacheHeaderRule.getHeaderValue());
        }
    }
    
    void setConnectionHeaders(final SecurityFilterProperties securityFilterConfig) {
        final List<ResponseHeaderRule> connectionHeaders = securityFilterConfig.getSafeResponseHeaderRules("connection-headers");
        for (final ResponseHeaderRule connectionHeaderRule : connectionHeaders) {
            this.setHeaderToSuper(connectionHeaderRule.getHeaderName(), connectionHeaderRule.getHeaderValue());
        }
    }
    
    public Map<String, ResponseHeaderRule> getConfiguredHeaders() {
        if (this.configuredHeaders == null || this.configuredHeaders.isEmpty()) {
            final HttpServletRequest request = SecurityUtil.getCurrentRequest();
            if (request != null) {
                final SecurityRequestWrapper securedRequest = (SecurityRequestWrapper)request;
                final ActionRule actionRule = (ActionRule)securedRequest.getAttribute("urlrule");
                if (actionRule != null) {
                    this.configuredHeaders = actionRule.getResponseHeaderRules();
                }
                else {
                    SecurityResponseWrapper.LOGGER.log(Level.SEVERE, "Action Rule is empty for the request URI : {0}", securedRequest.getRequestURI());
                }
            }
        }
        return this.configuredHeaders;
    }
    
    private boolean isConfiguredHeader(final String name) {
        if (this.getConfiguredHeaders() != null && this.getConfiguredHeaders().containsKey(name)) {
            return true;
        }
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        if (request != null) {
            final SecurityRequestWrapper securedRequest = (SecurityRequestWrapper)request;
            final SecurityFilterProperties securityFilterProperties = SecurityFilterProperties.getInstance((HttpServletRequest)securedRequest);
            final ActionRule actionRule = securedRequest.getURLActionRule();
            if (actionRule != null && !actionRule.isPublicURL() && securityFilterProperties.getSafeResponseHeaderRules("cache-headers") != null && SecurityResponseWrapper.CACHE_HEADERS_LIST.contains(name)) {
                return true;
            }
        }
        return false;
    }
    
    private ResponseLogRule getResponseLogRule(final SecurityRequestWrapper securedRequest) {
        final SecurityFilterProperties filterProps = SecurityFilterProperties.getInstance((HttpServletRequest)securedRequest);
        final ActionRule rule = securedRequest.getURLActionRule();
        if (filterProps.isLogResponseEnabled() && (filterProps.getExcludeURLsInLogResponse() == null || !filterProps.getExcludeURLsInLogResponse().matcher(securedRequest.getValidatedRequestPath()).matches()) && rule != null) {
            final ResponseRule responseRule = rule.getResponseRule();
            if (responseRule != null) {
                final ResponseLogRule logRule = responseRule.getResponseLogRule();
                if (logRule != null) {
                    String contentType = this.getContentType();
                    if (contentType != null) {
                        contentType = contentType.split(";")[0];
                    }
                    if (logRule.getAllowedContentTypes() != null && logRule.getAllowedContentTypes().contains(contentType)) {
                        return logRule;
                    }
                }
            }
        }
        return null;
    }
    
    private boolean isJSONResponse() {
        final String contentType = this.getContentType();
        return SecurityUtil.isValid(contentType) && contentType.toLowerCase().contains("json");
    }
    
    private String getJsonImpurity(final SecurityRequestWrapper securedRequest) {
        final ActionRule rule = securedRequest.getURLActionRule();
        if (rule != null) {
            final String jsonImpurity = rule.getJSONImpurity();
            if (this.isJSONResponse() && (!rule.isAPI() || this.isAPIAuthenticatedViaCookie)) {
                return jsonImpurity;
            }
        }
        return null;
    }
    
    public void setApiAuthenticatedViaCookie() {
        this.isAPIAuthenticatedViaCookie = true;
    }
    
    void setAllResponseHeaders() {
        this.setResponseHeaders();
        this.setUserConfigControlledResponseHeaders();
    }
    
    void setResponseHeaders() {
        if (this.getConfiguredHeaders() != null) {
            final SecurityRequestWrapper securedRequest = (SecurityRequestWrapper)SecurityUtil.getCurrentRequest();
            final SecurityFilterProperties securityFilterConfig = SecurityFilterProperties.getInstance((HttpServletRequest)securedRequest);
            this.setResponseHeaders(this.getConfiguredHeaders().values(), securityFilterConfig, securedRequest.skipHeaderValidationAPIMode, securedRequest.getCSPNonce());
        }
    }
    
    void setResponseHeaders(final Collection<ResponseHeaderRule> responseHeaderRules, final SecurityFilterProperties securityFilterConfig, final boolean skipHeaderValidationAPIMode, final String cspNonce) {
        if (responseHeaderRules != null && !skipHeaderValidationAPIMode) {
            for (final ResponseHeaderRule responseHeaderRule : responseHeaderRules) {
                final String responseHeaderName = responseHeaderRule.getHeaderName();
                String responseHeaderValue = responseHeaderRule.getHeaderValue();
                if ("Content-Type".equals(responseHeaderName)) {
                    super.setContentType(responseHeaderValue);
                }
                else if (!SecurityResponseWrapper.SPECIALHEADERS.contains(responseHeaderName)) {
                    this.setHeaderToSuper(responseHeaderName, responseHeaderValue);
                }
                else {
                    if (!"Content-Security-Policy-Report-Only".equalsIgnoreCase(responseHeaderName) && !"Content-Security-Policy".equalsIgnoreCase(responseHeaderName)) {
                        continue;
                    }
                    if (responseHeaderValue.contains("${report_uri}")) {
                        if (!AppSenseAgent.isEnableCSPReport()) {
                            continue;
                        }
                        if (!securityFilterConfig.isDevelopmentMode() && AppSenseAgent.getCSPReportURI() != null) {
                            responseHeaderValue = responseHeaderValue.replace("${report_uri}", AppSenseAgent.getCSPReportURI());
                        }
                    }
                    if (responseHeaderValue.contains("${csp_nonce}")) {
                        responseHeaderValue = responseHeaderValue.replace("${csp_nonce}", cspNonce);
                    }
                    this.setHeaderToSuper(responseHeaderName, responseHeaderValue);
                }
            }
        }
    }
    
    void setUserConfigControlledResponseHeaders() {
        final SecurityRequestWrapper securedRequest = (SecurityRequestWrapper)SecurityUtil.getCurrentRequest();
        final SecurityFilterProperties securityFilterConfig = SecurityFilterProperties.getInstance((HttpServletRequest)securedRequest);
        if (!securedRequest.skipHeaderValidationAPIMode) {
            this.checkAndSetXFrameOption(securityFilterConfig, (HttpServletRequest)securedRequest);
            if (securedRequest.getAttribute("CORS_REQUEST_TYPE") instanceof SecurityFilterProperties.CORS_REQUEST_TYPE) {
                this.setCORSHeaders(securityFilterConfig, securedRequest);
            }
            this.userConfigControlledResponseHeadersSet = true;
        }
    }
    
    private void setCORSHeaders(final SecurityFilterProperties securityFilterConfig, final SecurityRequestWrapper securedRequest) {
        final ActionRule actionRule = (ActionRule)securedRequest.getAttribute("urlrule");
        if (actionRule != null && actionRule.isEnabledDynamicCorsHeaders()) {
            securityFilterConfig.getSecurityProvider().setCORSResponseHeaders((HttpServletRequest)securedRequest, (HttpServletResponse)this, actionRule);
        }
        else {
            this.checkAndSetCORSHeaders(securityFilterConfig, (HttpServletRequest)securedRequest);
        }
    }
    
    void checkAndSetCORSHeaders(final SecurityFilterProperties securityFilterConfig, final HttpServletRequest request) {
        if (this.getConfiguredHeaders() != null) {
            final ResponseHeaderRule responseHeaderRule = this.getConfiguredHeaders().get("Access-Control-Allow-Origin");
            if (responseHeaderRule != null && responseHeaderRule.getHeaderValue() != null) {
                String origin = request.getHeader("Origin");
                final String responseHeaderValue = responseHeaderRule.getHeaderValue();
                final Authenticator authenticator = securityFilterConfig.getAuthenticationProvider();
                boolean isValidCORSOrigin = true;
                final boolean isCorsPreflightReq = ((SecurityRequestWrapper)request).isCorsPreFlightRequest();
                if ("trusted".equals(responseHeaderValue) || "oauthtrusted".equals(responseHeaderValue) || "trusted|oauthtrusted".equals(responseHeaderValue)) {
                    if (isCorsPreflightReq) {
                        SecurityResponseWrapper.LOGGER.log(Level.FINE, " Origin : \"{0}\" ; preflight request is allowed for all trusted clients", origin);
                    }
                    else if ("trusted".equals(responseHeaderValue)) {
                        isValidCORSOrigin = this.isTrustedCORSOrigin(securityFilterConfig, origin, request);
                    }
                    else if ("oauthtrusted".equals(responseHeaderValue) && authenticator != null) {
                        isValidCORSOrigin = this.isOAuthTrustedCORSOrigin(securityFilterConfig, origin, request);
                    }
                    else if ("trusted|oauthtrusted".equals(responseHeaderValue) && authenticator != null && !this.isTrustedCORSOrigin(securityFilterConfig, origin, request)) {
                        isValidCORSOrigin = this.isOAuthTrustedCORSOrigin(securityFilterConfig, origin, request);
                    }
                }
                else if ("*".equals(responseHeaderValue)) {
                    SecurityResponseWrapper.LOGGER.log(Level.FINE, " All Origins are allowed ");
                    origin = "*";
                }
                else {
                    isValidCORSOrigin = responseHeaderValue.equalsIgnoreCase(origin);
                    if (!isValidCORSOrigin) {
                        final Pattern pt = SecurityUtil.getRegexPattern((SecurityRequestWrapper)request, responseHeaderValue);
                        isValidCORSOrigin = (pt != null && pt.matcher(origin).matches());
                    }
                }
                if (isValidCORSOrigin) {
                    this.setResponseCoHeaders(responseHeaderRule, origin, request);
                }
                else if (!isCorsPreflightReq) {
                    SecurityResponseWrapper.LOGGER.log(Level.SEVERE, "CORS request \"{0}\" from origin : \"{1}\" is not allowed", new Object[] { request.getRequestURI(), origin });
                    throw new IAMSecurityException("UNAUTHORIZED_CORS_REQUEST", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
                }
            }
        }
    }
    
    private boolean isOAuthTrustedCORSOrigin(final SecurityFilterProperties securityFilterConfig, final String origin, final HttpServletRequest request) {
        final Authenticator authenticator = securityFilterConfig.getAuthenticationProvider();
        final List<String> clientOAuthTrustedDomainList = authenticator.getClientOAuthTrustedDomains();
        if (clientOAuthTrustedDomainList != null && clientOAuthTrustedDomainList.contains(origin)) {
            SecurityResponseWrapper.LOGGER.log(Level.FINE, " Origin : \"{0}\" is matched with OAuth Trusted Domain List provided by Client ", origin);
            return true;
        }
        SecurityResponseWrapper.LOGGER.log(Level.SEVERE, " Origin : \"{0}\" Did not get matched with the Client provided OAuth Domain List ", origin);
        return false;
    }
    
    private boolean isTrustedCORSOrigin(final SecurityFilterProperties securityFilterConfig, final String origin, final HttpServletRequest request) {
        final SecurityProvider securityProvider = securityFilterConfig.getSecurityProvider();
        final Authenticator authenticator = securityFilterConfig.getAuthenticationProvider();
        final String originDomain = SecurityUtil.getDomainWithPort(origin);
        if (authenticator != null && originDomain != null && authenticator.isTrustedDomain(originDomain)) {
            SecurityResponseWrapper.LOGGER.log(Level.FINE, " Origin : \"{0}\" is matched with Account WhiteList ", origin);
        }
        else {
            if (!securityProvider.isTrusted(request, originDomain, ZSecConstants.TRUSTED_FEATURES.CORS.value)) {
                SecurityResponseWrapper.LOGGER.log(Level.SEVERE, " Origin : \"{0}\" Did not get matched with WhiteLists ", origin);
                return false;
            }
            SecurityResponseWrapper.LOGGER.log(Level.FINE, " Origin : \"{0}\" is matched with Service WhiteList ", origin);
        }
        return true;
    }
    
    void setResponseCoHeaders(final ResponseHeaderRule responseHeaderRule, final String origin, final HttpServletRequest request) {
        this.setHeaderToSuper(responseHeaderRule.getHeaderName(), origin);
        this.setHeaderToSuper("Vary", "Origin");
        final List<ResponseHeaderRule> innerResponseHeaderRuleList = responseHeaderRule.getInnerResponseHeaderList();
        for (final ResponseHeaderRule innerResponseHeader : innerResponseHeaderRuleList) {
            this.setHeaderToSuper(innerResponseHeader.getHeaderName(), innerResponseHeader.getHeaderValue());
        }
    }
    
    void checkAndSetXFrameOption(final SecurityFilterProperties securityFilterConfig, final HttpServletRequest serRequest) {
        if (this.getConfiguredHeaders() != null && serRequest instanceof SecurityRequestWrapper) {
            final SecurityRequestWrapper request = (SecurityRequestWrapper)serRequest;
            final ResponseHeaderRule xframeHeaderRule = this.getConfiguredHeaders().get("X-Frame-Options");
            if (xframeHeaderRule != null && xframeHeaderRule.getHeaderValue() != null) {
                String xframeType = xframeHeaderRule.getHeaderValue().toLowerCase();
                final UserAgent ua = request.getUserAgent();
                if (ResponseHeader.XFRAME_OPTIONS_ZSTD_VALUES.contains(xframeType)) {
                    final String s = xframeType;
                    switch (s) {
                        case "enableontrusted": {
                            if (this.checkAndSetEnableOnTrusted(securityFilterConfig, request, ua, xframeHeaderRule)) {
                                return;
                            }
                            break;
                        }
                        case "enableontrustedlist": {
                            if (ua == null || !this.checkAndSetEnableOnServiceTrustedList(securityFilterConfig, request, ua, xframeHeaderRule)) {
                                this.setHeaderToSuper("X-Frame-Options", "DENY");
                            }
                            return;
                        }
                        case "trusted-list": {
                            if (this.checkAndSetServiceTrustedList(securityFilterConfig, request, ua)) {
                                return;
                            }
                            break;
                        }
                        case "trusted": {
                            if (securityFilterConfig.isAuthenticationProviderConfigured() && this.checkAndSetTrustedDomains(securityFilterConfig, request, ua, xframeHeaderRule)) {
                                return;
                            }
                            break;
                        }
                        case "trusted|trusted-list": {
                            String allowFromURL = request.getParameter("frameorigin");
                            allowFromURL = ((allowFromURL == null) ? SecurityUtil.getCookie((HttpServletRequest)request, "_zxor") : allowFromURL);
                            if (!SecurityUtil.isValid(allowFromURL)) {
                                if (this.checkAndSetServiceTrustedList(securityFilterConfig, request, ua)) {
                                    return;
                                }
                                break;
                            }
                            else {
                                if (securityFilterConfig.isAuthenticationProviderConfigured() && this.checkAndSetTrustedDomains(securityFilterConfig, request, ua, xframeHeaderRule)) {
                                    return;
                                }
                                break;
                            }
                            break;
                        }
                    }
                    final String xframeOptionsDefaultValue = xframeHeaderRule.getDefaultValue().toLowerCase();
                    xframeType = ("sameorigin".equals(xframeOptionsDefaultValue) ? xframeOptionsDefaultValue : "deny");
                }
                this.setHeaderToSuper("X-Frame-Options", xframeType.toUpperCase());
            }
        }
    }
    
    private boolean checkAndSetTrustedDomains(final SecurityFilterProperties securityFilterConfig, final SecurityRequestWrapper request, final UserAgent ua, final ResponseHeaderRule xframeHeaderRule) {
        boolean isCookie = false;
        String allowFromURL = request.getParameter("frameorigin");
        if (allowFromURL == null) {
            allowFromURL = SecurityUtil.getCookie((HttpServletRequest)request, "_zxor");
            isCookie = true;
        }
        if (ua != null && SecurityUtil.isValid(allowFromURL)) {
            final String urlWhiteList = allowFromURL.replace(",", "|");
            final String[] frameOriginURLs = urlWhiteList.split("\\|");
            allowFromURL = frameOriginURLs[0].trim();
            final String allowFromDomain = SecurityUtil.getDomainWithPort(allowFromURL);
            if (allowFromDomain != null) {
                final boolean isCSP = this.isCspFrameAncestorsSupported(ua);
                if (isCSP || this.isXFrameOptionsAllowFromSupported(ua)) {
                    if (this.setFrameOptionsHeaderAndCookie(urlWhiteList, frameOriginURLs, isCookie, xframeHeaderRule, request, securityFilterConfig, isCSP)) {
                        return true;
                    }
                }
                else {
                    final String refferer = request.getHeader("Referer");
                    final String referrerDomain = SecurityUtil.getDomainWithPort(refferer);
                    final Authenticator authProviderImpl = securityFilterConfig.getAuthenticationProvider();
                    final SecurityProvider secProvider = securityFilterConfig.getSecurityProvider();
                    boolean trusted = false;
                    final List<String> trustedServicesList = new ArrayList<String>(1);
                    if (referrerDomain != null) {
                        if (authProviderImpl.isTrustedDomain(SecurityUtil.getDomain(refferer)) && authProviderImpl.isTrustedDomain(SecurityUtil.getDomain(allowFromURL))) {
                            trusted = isAllowedService(allowFromURL, xframeHeaderRule, securityFilterConfig, trustedServicesList);
                        }
                        else if (secProvider.isTrusted((HttpServletRequest)request, referrerDomain, ZSecConstants.TRUSTED_FEATURES.IFRAME.value) && secProvider.isTrusted((HttpServletRequest)request, allowFromDomain, ZSecConstants.TRUSTED_FEATURES.IFRAME.value)) {
                            trusted = true;
                        }
                    }
                    if (trusted) {
                        if (allowFromDomain.equals(referrerDomain)) {
                            if (!isCookie) {
                                setTrustedServicesList(request, trustedServicesList);
                                this.addXFrameOptionFrameOriginCookie(allowFromURL, xframeHeaderRule, (HttpServletRequest)request);
                            }
                            ZSEC_INVALID_CLICKJACKING_PREVENTION.pushCrossDomainAccess(request.getRequestURI(), request.getURLActionRulePrefix(), request.getURLActionRulePath(), request.getURLActionRuleMethod(), request.getURLActionRuleOperation(), allowFromDomain, referrerDomain, ua.toString(), (ExecutionTimer)null);
                            return true;
                        }
                        if (referrerDomain.equals(request.getServerName()) || SecurityResponseWrapper.REDIRECT_EXCLUDE_PATTERN.matcher(refferer).find()) {
                            ZSEC_INVALID_CLICKJACKING_PREVENTION.pushSameDomainAccess(request.getRequestURI(), request.getURLActionRulePrefix(), request.getURLActionRulePath(), request.getURLActionRuleMethod(), request.getURLActionRuleOperation(), request.getServerName(), referrerDomain, ua.toString(), (ExecutionTimer)null);
                            return true;
                        }
                    }
                    if (isCookie) {
                        this.addXFrameOptionFrameOriginCookie("", xframeHeaderRule, (HttpServletRequest)request);
                    }
                }
            }
        }
        return false;
    }
    
    private static void setTrustedServicesList(final SecurityRequestWrapper request, final List<String> trustedServicesList) {
        if (trustedServicesList.size() > 0) {
            request.setAttribute("ZSEC_XFRAME_TRUSTED_SERVICE", trustedServicesList);
        }
    }
    
    private boolean checkAndSetServiceTrustedList(final SecurityFilterProperties securityFilterConfig, final SecurityRequestWrapper request, final UserAgent ua) {
        final List<String> trustedDomains = securityFilterConfig.getSecurityProvider().getTrustedDomainList(request.getHeader("host"));
        return trustedDomains != null && this.isValidTrustedDomains(trustedDomains) && this.setTrustedDomains(this.isCspFrameAncestorsSupported(ua), trustedDomains);
    }
    
    private boolean setTrustedDomains(final boolean isCSP, final List<String> trustedDomains) {
        if (isCSP) {
            this.addHeaderToSuper("Content-Security-Policy", "frame-ancestors 'self' " + this.getDomainListAsString(trustedDomains));
            return true;
        }
        this.setHeaderToSuper("X-Frame-Options", "ALLOW-FROM " + trustedDomains.get(0).trim());
        return true;
    }
    
    private boolean checkAndSetEnableOnTrusted(final SecurityFilterProperties securityFilterConfig, final SecurityRequestWrapper request, final UserAgent ua, final ResponseHeaderRule xframeHeaderRule) {
        final boolean enabledTrustedCheck = securityFilterConfig.getSecurityProvider().enableXFrameTrustedCheck((HttpServletRequest)request);
        if (enabledTrustedCheck) {
            return securityFilterConfig.isAuthenticationProviderConfigured() && this.checkAndSetTrustedDomains(securityFilterConfig, request, ua, xframeHeaderRule);
        }
        SecurityResponseWrapper.LOGGER.log(Level.FINE, "Trusted check is disabled for the request URI : {0}, Referer : {1}, frameorigin : {2}", new Object[] { request.getRequestURI(), request.getHeader("Referer"), request.getParameter("frameorigin") });
        return true;
    }
    
    private boolean checkAndSetEnableOnServiceTrustedList(final SecurityFilterProperties securityFilterConfig, final SecurityRequestWrapper request, final UserAgent ua, final ResponseHeaderRule xframeHeaderRule) {
        boolean isCookie = false;
        final List<String> trustedDomains = securityFilterConfig.getSecurityProvider().getTrustedDomainList(request.getHeader("host"));
        if (trustedDomains == null) {
            return true;
        }
        String allowFromURL = request.getParameter("frameorigin");
        if (allowFromURL == null) {
            allowFromURL = SecurityUtil.getCookie((HttpServletRequest)request, "_zxor");
            if (allowFromURL != null) {
                isCookie = true;
            }
        }
        if (!SecurityUtil.isValid(allowFromURL) || allowFromURL.contains(",") || allowFromURL.contains("|")) {
            SecurityResponseWrapper.LOGGER.log(Level.SEVERE, "Frame origin validation failed");
        }
        else if (this.isValidTrustedDomains(trustedDomains) && trustedDomains.contains(allowFromURL)) {
            if (!isCookie) {
                this.addXFrameOptionFrameOriginCookie(allowFromURL, xframeHeaderRule, (HttpServletRequest)request);
            }
            return this.setTrustedDomains(this.isCspFrameAncestorsSupported(ua), Arrays.asList(allowFromURL));
        }
        if (isCookie) {
            this.addXFrameOptionFrameOriginCookie("", xframeHeaderRule, (HttpServletRequest)request);
        }
        return false;
    }
    
    private boolean isValidTrustedDomains(final List<String> trustedDomains) {
        if (trustedDomains.size() > 0 && trustedDomains.size() <= 5) {
            for (final String domain : trustedDomains) {
                if (domain.contains("*")) {
                    SecurityResponseWrapper.LOGGER.log(Level.SEVERE, " \"* \" not allowed in the X-Frame-Options : trusted-list ");
                    return false;
                }
            }
            return true;
        }
        SecurityResponseWrapper.LOGGER.log(Level.SEVERE, " \"SIZE Limit Exceeded \" for  X-Frame-Options : trusted-list - input-size : " + trustedDomains.size() + " , Maximum size : " + 5);
        return false;
    }
    
    private String getDomainListAsString(final List<String> trustedDomains) {
        final StringBuilder domainBuilder = new StringBuilder();
        for (final String domain : trustedDomains) {
            domainBuilder.append(domain).append(" ");
        }
        return domainBuilder.toString();
    }
    
    private boolean isCspFrameAncestorsSupported(final UserAgent ua) {
        return ua.isAllowedBrowserAndVersion("Firefox", 24) || ua.isAllowedBrowserAndVersion("Chrome", 39) || ua.isAllowedBrowserAndVersion("Opera", 26) || ua.isAllowedBrowserAndVersion("Safari", 10) || ua.isAllowedBrowserAndVersion("Edge", 15) || ua.isAllowedBrowserAndVersion("Electron", 0) || ua.isAllowedBrowserAndVersion("Ulaa", 0);
    }
    
    private boolean isXFrameOptionsAllowFromSupported(final UserAgent ua) {
        return ua.isAllowedBrowserAndVersion("Firefox", 18) || ua.isAllowedBrowserAndVersion("IE", 8) || ua.isAllowedBrowserAndVersion("Edge", 14);
    }
    
    private boolean setFrameOptionsHeaderAndCookie(final String urlWhiteList, final String[] frameOriginURLs, final boolean isCookie, final ResponseHeaderRule xframeHeaderRule, final SecurityRequestWrapper request, final SecurityFilterProperties securityFilterConfig, final boolean isCSP) {
        final SecurityProvider securityProvider = securityFilterConfig.getSecurityProvider();
        final Authenticator authProviderImpl = securityFilterConfig.getAuthenticationProvider();
        final List<String> trustedDomains = new ArrayList<String>();
        final List<String> trustedServicesList = new ArrayList<String>(frameOriginURLs.length);
        for (final String allowFromURL : frameOriginURLs) {
            boolean trusted = false;
            if (authProviderImpl.isTrustedDomain(SecurityUtil.getDomain(allowFromURL))) {
                trusted = isAllowedService(allowFromURL, xframeHeaderRule, securityFilterConfig, trustedServicesList);
            }
            else if (securityProvider.isTrusted((HttpServletRequest)request, SecurityUtil.getDomainWithPort(allowFromURL), ZSecConstants.TRUSTED_FEATURES.IFRAME.value)) {
                trusted = true;
            }
            if (!trusted) {
                if (isCookie) {
                    this.addXFrameOptionFrameOriginCookie("", xframeHeaderRule, (HttpServletRequest)request);
                }
                return false;
            }
            if (!isCSP) {
                trustedDomains.add(allowFromURL);
                break;
            }
            trustedDomains.add(allowFromURL);
        }
        if (!isCookie) {
            setTrustedServicesList(request, trustedServicesList);
            this.addXFrameOptionFrameOriginCookie(urlWhiteList, xframeHeaderRule, (HttpServletRequest)request);
        }
        this.setTrustedDomains(isCSP, trustedDomains);
        return true;
    }
    
    private static boolean isAllowedService(String allowFromURL, final ResponseHeaderRule xframeHeaderRule, final SecurityFilterProperties securityFilterConfig, final List<String> trustedServicesList) {
        if (xframeHeaderRule.getAllowedServices() == null) {
            return true;
        }
        final String allowFromDomain = SecurityUtil.getDomainWithPort(allowFromURL);
        String requestedService = SecurityUtil.getXframeTrustedService(allowFromDomain);
        if (requestedService == null) {
            if (allowFromDomain.equals(SecurityUtil.getRequestDomain())) {
                requestedService = SecurityFilterProperties.getServiceName();
            }
            else {
                allowFromURL = (allowFromURL.startsWith("https") ? ("https://" + allowFromDomain) : ("http://" + allowFromDomain));
                requestedService = securityFilterConfig.getAuthenticationProvider().getServiceNameForDomain(allowFromURL);
            }
            SecurityUtil.addXframeTrustedServiceForDomain(allowFromDomain, requestedService);
        }
        final boolean trustedService = requestedService != null && xframeHeaderRule.getAllowedServices().contains(requestedService);
        if (trustedService) {
            trustedServicesList.add(requestedService);
        }
        return trustedService;
    }
    
    private void addXFrameOptionFrameOriginCookie(final String param, final ResponseHeaderRule xframeHeaderRule, final HttpServletRequest request) {
        final HttpCookie cookie = new HttpCookie("_zxor", param);
        cookie.encloseValueWithQuotes = true;
        if ("allowfullintegration".equals(xframeHeaderRule.getScope())) {
            cookie.setPath("/");
        }
        if ("https".equalsIgnoreCase(request.getScheme())) {
            cookie.setSecure(true);
            final UserAgent ua = ((SecurityRequestWrapper)request).getUserAgent();
            if (ua != null && !ua.isSameSiteNoneIncompatibleClient()) {
                cookie.setSameSite(HttpCookie.SAMESITE.NONE.getValue());
            }
        }
        this.addHeader("Set-Cookie", cookie.generateCookie());
    }
    
    public void setExceptionRelatedResponseHeader(final SecurityFilterProperties securityFilterConfig, final SecurityRequestWrapper securedRequest) {
        if (!this.userConfigControlledResponseHeadersSet) {
            if (securedRequest != null && securedRequest.getAttribute("urlrule") != null) {
                this.checkAndSetXFrameOption(securityFilterConfig, (HttpServletRequest)securedRequest);
                if (securedRequest.getAttribute("CORS_REQUEST_TYPE") instanceof SecurityFilterProperties.CORS_REQUEST_TYPE) {
                    this.setCORSHeaders(securityFilterConfig, securedRequest);
                }
            }
            else {
                final List<String> disabledSafeHeaders = securityFilterConfig.getDefaultDisableSafeHeaders();
                for (final ResponseHeaderRule safeHeaderRule : securityFilterConfig.getSafeResponseHeaderRules("secure-headers")) {
                    if (disabledSafeHeaders.isEmpty() || !disabledSafeHeaders.contains(safeHeaderRule.getHeaderName())) {
                        this.setHeaderToSuper(safeHeaderRule.getHeaderName(), safeHeaderRule.getHeaderValue());
                    }
                }
                this.setResponseHeaders(securityFilterConfig.getDefaultResponseHeaderRules(), securityFilterConfig, false, "");
                for (final ResponseHeaderRule errorHandleHeaderRule : securityFilterConfig.getSafeResponseHeaderRules("error-page-headers")) {
                    this.setHeaderToSuper(errorHandleHeaderRule.getHeaderName(), errorHandleHeaderRule.getHeaderValue());
                }
            }
        }
    }
    
    public Map<String, Object> toMap() {
        if (this.responseInfo != null) {
            return this.responseInfo;
        }
        (this.responseInfo = new HashMap<String, Object>()).put("RES_CONTENT_TYPE", super.getContentType());
        final Collection<String> headerNames = super.getHeaderNames();
        this.responseInfo.put("RES_HEADER", headerNames);
        this.responseInfo.put(InfoFields.ACCESSLOGFIELDS.RES_HEADER_CNT.getValue(), headerNames.size());
        this.logResponseHeaderSize();
        return this.responseInfo;
    }
    
    private void logResponseHeaderSize() {
        int totalSize = 0;
        final JSONArray headerArray = new JSONArray();
        final List<String> headers = new ArrayList<String>();
        for (final String headerName : super.getHeaderNames()) {
            if (headers.contains(headerName)) {
                continue;
            }
            headers.add(headerName);
            final int headerNameLength = headerName.length();
            int headerLength = 0;
            for (final String headerValue : super.getHeaders(headerName)) {
                headerLength += headerNameLength + headerValue.length();
            }
            if (SecurityResponseWrapper.CSP_AND_COOKIE_HEADERS.contains(headerName)) {
                final JSONObject innerObj = new JSONObject();
                innerObj.put("name", (Object)headerName);
                innerObj.put("size", headerLength);
                headerArray.put((Object)innerObj);
            }
            totalSize += headerLength;
        }
        this.responseInfo.put(InfoFields.ACCESSLOGFIELDS.RES_HEADER_SIZE_IN_BYTES.getValue(), totalSize);
        if (headerArray.length() > 0) {
            this.responseInfo.put(InfoFields.ACCESSLOGFIELDS.INDIVIDUAL_RES_HEADER_SIZE_IN_BYTES.getValue(), headerArray);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(SecurityResponseWrapper.class.getName());
        REDIRECT_EXCLUDE_PATTERN = Pattern.compile("https://accounts.(localzoho|zoho).com/login|https://gadgets.(localzoho|zoho).com/googledocs");
        SPECIALHEADERS = Arrays.asList("X-Frame-Options", "Access-Control-Allow-Origin", "Content-Security-Policy-Report-Only", "Content-Security-Policy");
        CACHE_HEADERS_LIST = Arrays.asList("Cache-Control", "Pragma", "Expires");
        CSP_AND_COOKIE_HEADERS = Arrays.asList("Content-Security-Policy", "Content-Security-Policy-Report-Only", "Set-Cookie", "Set-Cookie2");
    }
}
