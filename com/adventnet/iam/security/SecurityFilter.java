package com.adventnet.iam.security;

import java.util.Date;
import java.util.regex.Pattern;
import java.util.Properties;
import com.adventnet.iam.xss.XSSUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.io.InputStream;
import com.zoho.security.rule.ExceptionRule;
import com.zoho.security.eventfw.pojos.log.ZSEC_EXCEPTION_ANOMALY;
import java.io.IOException;
import java.util.Iterator;
import com.zoho.security.SFCorePlugin;
import com.zoho.security.wafad.WAFAttackDiscoveryMetricRecorder;
import com.zoho.security.eventfwimpl.ZSecSinglePointLoggerImplProvider;
import java.util.Map;
import com.zoho.security.eventfw.pojos.log.ZSEC_REQUEST_INFO;
import java.util.Set;
import com.zoho.security.eventfw.pojos.event.ZSEC_SECRET_PARAM_IN_QUERYSTRING;
import com.zoho.security.eventfw.pojos.log.ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE;
import java.util.List;
import com.zoho.security.eventfw.pojos.log.ZSEC_PERFORMANCE_ANOMALY;
import java.io.File;
import javax.servlet.DispatcherType;
import com.zoho.security.appfirewall.AppFirewallException;
import com.zoho.security.appfirewall.FirewallStage;
import com.zoho.security.util.SASConfigProviderUtil;
import javax.servlet.http.HttpServletResponse;
import com.zoho.security.eventfw.ExecutionTimer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.zoho.security.agent.AppSenseAgent;
import com.zoho.security.appfirewall.AppFirewallPolicyLoader;
import javax.servlet.ServletException;
import java.util.logging.Level;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class SecurityFilter implements Filter
{
    public static final Logger logger;
    private SecurityFilterProperties securityFilterConfig;
    private Authenticator authImpl;
    private String contextPath;
    private static final ThreadLocal<String> CURRENT_THREAD_NAME;
    
    public SecurityFilter() {
        this.securityFilterConfig = null;
        this.authImpl = null;
        this.contextPath = null;
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        try {
            this.contextPath = filterConfig.getServletContext().getRealPath("");
            this.securityFilterConfig = new SecurityFilterProperties(filterConfig);
            if (this.securityFilterConfig.isAuthenticationProviderConfigured()) {
                this.authImpl = this.securityFilterConfig.getAuthenticationProvider();
                if (this.authImpl != null) {
                    this.authImpl.init(filterConfig);
                    this.authImpl.init(this.securityFilterConfig);
                }
            }
        }
        catch (final Exception e) {
            SecurityFilter.logger.log(Level.WARNING, null, e);
            throw new ServletException((Throwable)e);
        }
        if (!SecurityFilterProperties.containsFilterInstance(this.contextPath)) {
            SecurityFilterProperties.addFilterInstance(this.contextPath, this.securityFilterConfig);
            this.securityFilterConfig.validateProxyRules(true);
            AppFirewallPolicyLoader.isReqFirewallEnabled = this.securityFilterConfig.isAppFirewallEnabled();
            if (SecurityFilterProperties.isUsingIAMImpl() && this.securityFilterConfig.isAppSenseEnabled()) {
                if (SecurityFilterProperties.isROOTContext()) {
                    AppSenseAgent.init();
                    if (AppSenseAgent.isRegisteredInAppSense()) {
                        AppSenseAgent.handleAppConfigurationsOnServerRestart();
                    }
                }
                else {
                    SecurityFilter.logger.log(Level.WARNING, "AppSense not enabled for the context  :: {0}", SecurityFilterProperties.getContextName());
                }
            }
            this.securityFilterConfig.initPiiDetector();
            DoSController.initLiveWindowThrottles(this.securityFilterConfig);
            this.deleteUnClearedOldTempFiles();
            SecurityUtil.deleteUnClearedTempFilesAtFileUploadDir();
            this.securityFilterConfig.getTempFileUploadDirMonitoring().init();
        }
        SecurityFilter.logger.log(Level.INFO, "Security Filter configurations loaded for the context path :: {0}", this.contextPath);
    }
    
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain fc) throws IOException, ServletException {
        SecurityFilter.logger.log(Level.FINE, "Req encoding : {0}", req.getCharacterEncoding());
        boolean enableSecretValueMasking = false;
        try {
            if (req.getCharacterEncoding() == null) {
                req.setCharacterEncoding(this.securityFilterConfig.getRequestEncoding());
            }
        }
        catch (final Exception e) {
            SecurityFilter.logger.log(Level.SEVERE, "{0}. So proceeding with default configuration [UTF-8] for request encoding", e.getMessage());
            try {
                req.setCharacterEncoding("UTF-8");
            }
            catch (final Exception ex) {
                SecurityFilter.logger.log(Level.WARNING, null, ex);
            }
        }
        HttpServletRequest request = (HttpServletRequest)req;
        final ExecutionTimer uvTimer = ExecutionTimer.startInstance();
        request.setAttribute("ZSEC_CONTEXT_PATH", (Object)this.contextPath);
        final Object zLogsVersion = request.getAttribute("ZLOGS_VERSION");
        enableSecretValueMasking = ((zLogsVersion != null && (int)zLogsVersion > 1) || this.securityFilterConfig.isEnabledSecretParamLoggingMask());
        if (enableSecretValueMasking) {
            SecurityUtil.setCurrentLogRequest((HttpServletRequest)new SecurityLogRequestWrapper(request));
        }
        final HttpServletResponse serResponse = (HttpServletResponse)res;
        final SecurityResponseWrapper response = new SecurityResponseWrapper(serResponse);
        if (this.securityFilterConfig.getResponseEncoding() != null) {
            serResponse.setCharacterEncoding(this.securityFilterConfig.getResponseEncoding());
        }
        SecurityRequestWrapper securedRequest = null;
        try {
            request.setAttribute("ZSEC_REQUEST_PATH", (Object)null);
            this.setThreadName(request);
            if (SASConfigProviderUtil.checkForTLSErrorRedirection(this.securityFilterConfig, request, serResponse)) {
                return;
            }
            try {
                if (AppFirewallPolicyLoader.activateRequestFirewall(this.securityFilterConfig, request, FirewallStage.PRE_STAGE)) {
                    SecurityFilter.logger.log(Level.SEVERE, "  {0}  ", AppFirewallException.class.getName() + " : " + "INVALID_REQUEST_COMPONENT");
                    AppFirewallPolicyLoader.doAction(request, response);
                    return;
                }
            }
            catch (final AppFirewallException ex2) {
                SecurityFilter.logger.log(Level.SEVERE, "  Exception occurred while activating request firewall {0}  ", ex2.toString());
            }
            catch (final Exception ex3) {
                SecurityFilter.logger.log(Level.SEVERE, " {0} ", ex3.toString());
            }
            final String uri = request.getRequestURI();
            if (uri.contains(";")) {
                SecurityFilter.logger.log(Level.SEVERE, "******INVALID******* Pathparameter value is present in the request : {0}", uri);
                throw new IAMSecurityException("URL_RULE_NOT_CONFIGURED");
            }
            if (!this.securityFilterConfig.isDisablePathParameterURIDecodingCheck() && (uri.contains("%3b") || uri.contains("%3B"))) {
                SecurityFilter.logger.log(Level.SEVERE, "******INVALID ENCODED URL******* Pathparameter value is present in the request : {0}", uri);
                throw new IAMSecurityException("URL_RULE_NOT_CONFIGURED");
            }
            if (!this.securityFilterConfig.isAllowedDotDotSlashInRequestURI() && (uri.contains("../") || uri.contains("..\\")) && DispatcherType.REQUEST.equals((Object)request.getDispatcherType())) {
                SecurityFilter.logger.log(Level.SEVERE, "Dot Dot Slash is present in the request URI : {0}", uri);
                throw new IAMSecurityException("URL_RULE_NOT_CONFIGURED");
            }
            final String contextPath = SecurityUtil.getContextPath(request);
            if (!SecurityFilterProperties.containsFilterInstance(contextPath)) {
                SecurityFilterProperties.addFilterInstance(contextPath, this.securityFilterConfig);
            }
            final String webappName = contextPath.substring(contextPath.lastIndexOf(File.separator) + 1);
            SecurityUtil.setCurrentWebapp(webappName);
            final String queryString = request.getQueryString();
            final String lookupURI = SecurityUtil.getRequestPath(request);
            SecurityFilter.logger.log(Level.FINE, "The request URI is {0}", uri);
            SecurityFilter.logger.log(Level.FINE, "The request Security Framework lookup URI is {0}", lookupURI);
            SecurityFilter.logger.log(Level.FINE, "The request QS  is {0}", queryString);
            final String serverName = SecurityUtil.getServerName(request);
            if (this.securityFilterConfig.isEnableHSTS() && this.isHSTSAllowedDomains(serverName) && this.enforceHSTS(request, response, queryString, serverName)) {
                return;
            }
            final boolean isReDispatched = SecurityUtil.getCurrentRequest() != null;
            if ("/getzsecservicename".equals(uri)) {
                serResponse.getWriter().println(SecurityFilterProperties.getServiceName());
                serResponse.setStatus(200);
                return;
            }
            if (lookupURI != null && SecurityUtil.isMatches(this.securityFilterConfig.getExcludeURLs(), lookupURI)) {
                SecurityUtil.setCurrentRequest(request);
                fc.doFilter(req, res);
                return;
            }
            final ProxyURL pUrl = this.securityFilterConfig.getProxyURL(request);
            if (pUrl != null) {
                request.setAttribute("ZSEC_PROXY_URL_UNIQUE_PATH", (Object)pUrl.getPath());
                SecurityFilter.logger.log(Level.FINE, "The proxy url is {0}", pUrl);
                ProxyUtil.service(request, serResponse, pUrl);
                return;
            }
            final ActionRule actionRule = this.securityFilterConfig.getActionRuleByQueryString(request, queryString);
            if (actionRule != null) {
                actionRule.preRequestHeadersCheck(request, this.securityFilterConfig);
            }
            final String streamContent = this.getInputStreamFromRequest(request, actionRule);
            boolean createSecuredRequestWrapper = true;
            if (actionRule != null) {
                if (actionRule.isRequestBodyRequiredForVerification() && SecurityUtil.isFormURLEncodedRequest(request)) {
                    createSecuredRequestWrapper = false;
                }
                else if (!actionRule.parseRequestBody() && SecurityUtil.isFormURLEncodedRequest(request)) {
                    request.getInputStream();
                }
            }
            if (createSecuredRequestWrapper) {
                securedRequest = SecurityRequestWrapper.getRequestWrapperInstance(request);
            }
            else {
                securedRequest = SecurityRequestWrapper.getRequestBodyWrapperInstance(request);
            }
            securedRequest.enableSecretValueMasking = enableSecretValueMasking;
            securedRequest.enableURINormalization = this.securityFilterConfig.isEnabledRequestURINormalization();
            SecurityUtil.setCurrentRequest((HttpServletRequest)securedRequest);
            request.setAttribute(SecurityRequestWrapper.class.getName(), (Object)securedRequest);
            securedRequest.setUserAgent();
            if (this.securityFilterConfig.isCaptchURL(request)) {
                final String hipDigest = request.getParameter("digest");
                final String hipCode = AccessInfo.getHipCodeFromCache(hipDigest);
                if (hipCode == null) {
                    throw new ServletException("Invalid hip digest");
                }
                SimpleCaptchaUtil.renderCAPTCHA(request, serResponse, hipCode);
            }
            else {
                ActionRule rule = null;
                try {
                    if (this.securityFilterConfig.isDevelopmentMode() && this.securityFilterConfig.reInitConfiguration()) {
                        RuleSetParser.validateXMLConfiguration(this.securityFilterConfig, this.getAllURLRule());
                    }
                    final ExecutionTimer urvtimer = ExecutionTimer.startInstance();
                    request = (HttpServletRequest)URLRule.validateURLRule(request, securedRequest, (HttpServletResponse)response, streamContent);
                    ZSEC_PERFORMANCE_ANOMALY.pushUrlruleValidation(request.getRequestURI(), urvtimer);
                    rule = (ActionRule)request.getAttribute("urlrule");
                    if (response.isCommitted() || ("OPTIONS".equals(request.getMethod()) && (this.securityFilterConfig.isRequestOptionsEnabled() || securedRequest.isCorsPreFlightRequest())) || (request.getAttribute(AppFirewallException.class.getName()) != null && (rule == null || !rule.isErrorPage()))) {
                        return;
                    }
                }
                catch (final IAMSecurityException ex4) {
                    throw ex4;
                }
                catch (final Exception e2) {
                    SecurityFilter.logger.log(Level.SEVERE, "Exception occured while validating the Input Parameters", e2);
                    throw new ServletException((Throwable)e2);
                }
                if (SecurityUtil.isValidList(rule.getScopedServices())) {
                    rule.checkForSystemAuthentication((HttpServletRequest)securedRequest, "required");
                }
                else if (rule.isDCSystemAuthRequired()) {
                    SecurityUtil.verifyDCSignature(securedRequest);
                }
                if (rule.isErrorPage()) {
                    if (request.getAttribute("javax.servlet.error.request_uri") != null) {
                        fc.doFilter((ServletRequest)request, (ServletResponse)response);
                        return;
                    }
                    if (isReDispatched) {
                        ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.pushErrAttrNotsetInErrRq(request.getRequestURI(), rule.getPrefix(), rule.getPath(), rule.getMethod(), rule.getOperationValue(), request.getDispatcherType().toString(), (ExecutionTimer)null);
                    }
                    response.setStatus(400);
                }
                else if ("/zsecagentnotify".equals(SecurityUtil.getRequestURI(request))) {
                    if (AppSenseAgent.isRegisteredInAppSense() && AppSenseAgent.handleAppSenseNotification(request)) {
                        response.setStatus(200);
                        return;
                    }
                    response.setStatus(400);
                }
                else {
                    if (!rule.isPublicURL() && this.authImpl != null) {
                        response.setCacheControlHeaders(this.securityFilterConfig, securedRequest);
                        final ExecutionTimer authTimer = ExecutionTimer.startInstance();
                        if (this.authImpl.authenticate(request, (HttpServletResponse)response)) {
                            ZSEC_PERFORMANCE_ANOMALY.pushAuthentication(request.getRequestURI(), authTimer);
                            if (AppFirewallPolicyLoader.activateRequestFirewall(this.securityFilterConfig, (HttpServletRequest)securedRequest, FirewallStage.POST_AUTHENTICATION_STAGE)) {
                                SecurityFilter.logger.log(Level.SEVERE, "  {0}:{1} ", new Object[] { AppFirewallException.class.getName(), "INVALID_REQUEST_COMPONENT" });
                                AppFirewallPolicyLoader.doAction((HttpServletRequest)securedRequest, response);
                                return;
                            }
                            response.setUserConfigControlledResponseHeaders();
                            DoSController.doLiveThrottle(request, true);
                            ZSEC_PERFORMANCE_ANOMALY.pushUrlValidation(request.getRequestURI(), uvTimer);
                            fc.doFilter((ServletRequest)request, (ServletResponse)response);
                        }
                        else {
                            response.setUserConfigControlledResponseHeaders();
                            ZSEC_PERFORMANCE_ANOMALY.pushUrlValidation(request.getRequestURI(), uvTimer);
                        }
                        return;
                    }
                    response.setUserConfigControlledResponseHeaders();
                    DoSController.controlDoS(request, (HttpServletResponse)response, rule);
                    DoSController.doLiveThrottle(request, true);
                    this.securityFilterConfig.getSecurityProvider().authorize(request, (HttpServletResponse)response, rule);
                    ZSEC_PERFORMANCE_ANOMALY.pushUrlValidation(request.getRequestURI(), uvTimer);
                    if (SecurityUtil.WafAgentUrls.CLEAN_LIVE_WINDOW_COUNT.getPath().equals(rule.getPath())) {
                        DoSController.cleanLiveWindowCount(securedRequest, response);
                        return;
                    }
                    fc.doFilter((ServletRequest)request, (ServletResponse)response);
                }
            }
        }
        catch (final IAMSecurityException ex5) {
            ZSEC_PERFORMANCE_ANOMALY.pushUrlValidation(request.getRequestURI(), uvTimer);
            this.handleSecurityExceptions(request, securedRequest, response, ex5);
        }
        finally {
            if (securedRequest != null) {
                if (!securedRequest.secretParamsInQs.isEmpty()) {
                    ZSEC_SECRET_PARAM_IN_QUERYSTRING.pushInfo(securedRequest.getRequestURI(), securedRequest.getURLActionRulePrefix(), securedRequest.getURLActionRulePath(), securedRequest.getURLActionRuleMethod(), securedRequest.getURLActionRuleOperation(), securedRequest.getDispatcherType().toString(), securedRequest.getActualRequestMethod(), SecurityUtil.getHttpOverrideMethod((HttpServletRequest)req), SecurityUtil.getSourceDomain((HttpServletRequest)securedRequest), request.getHeader("User-Agent"), (Set)securedRequest.secretParamsInQs, (ExecutionTimer)null);
                }
                ZSEC_REQUEST_INFO.pushInfo((Map)securedRequest.toMap(), (Map)response.toMap(), (ExecutionTimer)null);
                if (securedRequest.isThrottled()) {
                    try {
                        this.securityFilterConfig.getSecurityProvider().setAccessLogCustomFieldsForThrottledRequest(securedRequest);
                    }
                    catch (final Exception ex6) {
                        SecurityFilter.logger.log(Level.WARNING, null, ex6);
                    }
                }
            }
            if (enableSecretValueMasking) {
                MaskUtil.maskSensitiveValues(request, (HttpServletResponse)response, securedRequest, this.securityFilterConfig);
            }
            DoSController.doLiveThrottle(request, false);
            ZSecSinglePointLoggerImplProvider.pushEvents();
            WAFAttackDiscoveryMetricRecorder.logMetrics();
            this.storeRequestTrace(this.securityFilterConfig, request);
            try {
                this.cleanupTempFiles(request);
            }
            catch (final Exception ex6) {
                SecurityFilter.logger.log(Level.WARNING, null, ex6);
            }
            try {
                if (this.authImpl != null) {
                    this.authImpl.cleanupIAMCredentials(request);
                }
            }
            catch (final Exception ex6) {
                SecurityFilter.logger.log(Level.WARNING, null, ex6);
            }
            if (this.securityFilterConfig.getPlugins() != null) {
                for (final SFCorePlugin plugin : this.securityFilterConfig.getPlugins()) {
                    plugin.clean();
                }
            }
            SecurityUtil.cleanUpThreadLocals();
            this.reSetThreadName();
        }
    }
    
    boolean isHSTSAllowedDomains(final String serverName) {
        final String[] whiteListDomains = this.securityFilterConfig.getHSTSAllowedDomains();
        if (whiteListDomains != null) {
            for (final String whiteListDomain : whiteListDomains) {
                if ("trusted".equalsIgnoreCase(whiteListDomain)) {
                    final Authenticator authProviderImpl = this.securityFilterConfig.getAuthenticationProvider();
                    if (authProviderImpl != null && authProviderImpl.isTrustedDomain(serverName)) {
                        return true;
                    }
                }
                if (whiteListDomain.equals(serverName)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    private boolean enforceHSTS(final HttpServletRequest request, final SecurityResponseWrapper response, final String queryString, final String serverName) {
        final String scheme = request.getScheme();
        switch (scheme) {
            case "https": {
                response.setConnectionHeaders(this.securityFilterConfig);
                break;
            }
            case "http": {
                if (this.securityFilterConfig.enableHSTSRedirection()) {
                    final String query = (queryString != null) ? ("?" + queryString) : "";
                    final String url = "https://" + serverName + request.getRequestURI() + query;
                    response.setStatus(301);
                    response.setHeaderToSuper("Location", url);
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    private void handleSecurityExceptions(final HttpServletRequest request, final SecurityRequestWrapper securedRequest, final SecurityResponseWrapper response, final IAMSecurityException ex) throws IOException {
        if (securedRequest != null) {
            securedRequest.setIAMSecurityException(ex);
        }
        response.setExceptionRelatedResponseHeader(this.securityFilterConfig, securedRequest);
        final boolean enableSendError = this.securityFilterConfig.isSendErrorEnabled();
        if (enableSendError && SecurityUtil.isValid(ex.getErrorCode())) {
            request.setAttribute(IAMSecurityException.class.getName(), (Object)ex);
            int statusCode = -1;
            if (this.securityFilterConfig.getBlockedMethods().contains(request.getMethod())) {
                statusCode = 400;
                response.setStatus(statusCode);
                SecurityFilter.logger.log(Level.SEVERE, "Error Page will not be called for {0} method", request.getMethod());
            }
            else {
                final ExceptionRule exceptionRule = this.securityFilterConfig.getExceptionRule(ex.getErrorCode());
                String message;
                if (exceptionRule != null) {
                    statusCode = exceptionRule.getStatusCode();
                    message = exceptionRule.getStatusMessage();
                }
                else {
                    statusCode = 400;
                    message = "Bad Request";
                }
                response.sendError(statusCode, message);
            }
            if (SecurityUtil.getJsonexceptiontracelist().size() > 0) {
                ex.setJsonExceptionTrace(SecurityUtil.getJsonexceptiontracelist());
            }
            SecurityFilter.logger.log(Level.SEVERE, " IAMSecurityException Error Code : {0} ", ex.getErrorCode());
            SecurityFilter.logger.log(Level.FINE, "IAMSecurityException", ex);
            if (IAMSecurityException.anomalous_Error_Codes.contains(ex.getErrorCode()) && this.securityFilterConfig.pushSecurityException()) {
                ZSEC_EXCEPTION_ANOMALY.pushAnomalousException(ex.getErrorCode(), ex.getParameterValue(), request.getRemoteAddr(), statusCode, request.getHeader("User-Agent"), (ExecutionTimer)null);
            }
            return;
        }
        throw ex;
    }
    
    public void storeRequestTrace(final SecurityFilterProperties filterConfig, final HttpServletRequest sRequest) {
        SecurityFilter.logger.log(Level.FINE, "*** END METHOD ****");
    }
    
    private String getInputStreamFromRequest(final HttpServletRequest request, final ActionRule actionRule) throws IOException {
        final ParameterRule inputStreamRule = (actionRule != null && actionRule.hasInputStream()) ? actionRule.getInputStreamRule() : this.securityFilterConfig.getSecurityProvider().getDynamicInputStreamRule(request, actionRule);
        if (inputStreamRule == null || !SecurityUtil.isFormURLEncodedRequest(request, this.securityFilterConfig, actionRule)) {
            return null;
        }
        if (inputStreamRule.isImportFile() || inputStreamRule.isInputStreamTypeBinary()) {
            throw new UnsupportedOperationException("\"application/x-www-form-urlencoded\" content type based validation is not supported for inputstream if inputstream rule has type=\"binary\" or import=file=\"true\" configuration");
        }
        return SecurityUtil.convertInputStreamAsString((InputStream)request.getInputStream(), inputStreamRule.getMaxLength());
    }
    
    public String getCSRFCookieName() {
        return this.securityFilterConfig.getCSRFCookieName();
    }
    
    public String getCSRFParamName() {
        return this.securityFilterConfig.getCSRFParamName();
    }
    
    public static String getCSRFCookieName(final HttpServletRequest request) {
        return SecurityFilterProperties.getInstance(request).getCSRFCookieName();
    }
    
    public static String getCSRFParamName(final HttpServletRequest request) {
        return SecurityFilterProperties.getInstance(request).getCSRFParamName();
    }
    
    static boolean isInDevelopmentMode(final HttpServletRequest request) {
        return SecurityFilterProperties.getInstance(request).isDevelopmentMode();
    }
    
    public static boolean isTrustedIP(final String ipAddress) {
        return SecurityFilterProperties.isTrustedIP(ipAddress);
    }
    
    public void destroy() {
    }
    
    protected Collection<URLRule> getAllURLRule() {
        return this.securityFilterConfig.getAllURLRule();
    }
    
    protected URLRule getURLRule(final String url) {
        return this.securityFilterConfig.getURLRule(url);
    }
    
    private void cleanupTempFiles(final HttpServletRequest request) {
        try {
            this.cleanupFiles(request, "MULTIPART_FORM_REQUEST");
            this.cleanupFiles(request, "IMPORTED_DATA_AS_FILE");
            this.cleanupFile(request, "STREAM_CONTENT_AS_FILE");
        }
        catch (final Exception e) {
            SecurityFilter.logger.log(Level.INFO, "Exception while deleting the temp files", e);
        }
    }
    
    private void cleanupFiles(final HttpServletRequest request, final String requestAttribute) {
        if (request.getAttribute(requestAttribute) != null) {
            final ArrayList<UploadedFileItem> list = (ArrayList<UploadedFileItem>)request.getAttribute(requestAttribute);
            for (final UploadedFileItem p : list) {
                final File f = p.getUploadedFileForValidation();
                if (f != null && f.exists()) {
                    f.delete();
                }
            }
        }
    }
    
    private void cleanupFile(final HttpServletRequest request, final String requestAttribute) {
        if (request.getAttribute(requestAttribute) != null) {
            final UploadedFileItem fileItem = (UploadedFileItem)request.getAttribute(requestAttribute);
            final File f = fileItem.getUploadedFileForValidation();
            if (f != null && f.exists()) {
                f.delete();
            }
        }
    }
    
    public void addXSSUtil(final String xssPatternName, final XSSUtil xssUtil) {
        this.securityFilterConfig.addXSSUtil(xssPatternName, xssUtil);
    }
    
    public XSSUtil getXSSUtil(final String xssPatternName) {
        return this.securityFilterConfig.getXSSUtil(xssPatternName);
    }
    
    public static XSSUtil getXSSUtil(final SecurityRequestWrapper request, final String xssPatternName) {
        return SecurityFilterProperties.getInstance((HttpServletRequest)request).getXSSUtil(xssPatternName);
    }
    
    public void addRegularExpressions(final Properties prop) {
        this.securityFilterConfig.addRegularExpressions(prop);
    }
    
    public void addRegularExpressions(final String name, final String pattern) {
        this.securityFilterConfig.addRegularExpressions(name, pattern);
    }
    
    public void addProperties(final Properties prop) {
        this.securityFilterConfig.addProperties(prop);
    }
    
    public void addContentTypes(final Properties prop) {
        this.securityFilterConfig.addContentTypes(prop);
    }
    
    public Pattern getContentTypes(final String name) {
        return this.securityFilterConfig.getContentTypes(name);
    }
    
    public static Pattern getContentTypes(final SecurityRequestWrapper request, final String name) {
        return SecurityFilterProperties.getInstance((HttpServletRequest)request).getContentTypes(name);
    }
    
    public static void addRegularExpressions(final SecurityRequestWrapper request, final Properties prop) {
        SecurityFilterProperties.getInstance((HttpServletRequest)request).addRegularExpressions(prop);
    }
    
    public static void addRegularExpressions(final SecurityRequestWrapper request, final String name, final String pattern) {
        SecurityFilterProperties.getInstance((HttpServletRequest)request).addRegularExpressions(name, pattern);
    }
    
    public static Pattern getRegexPattern(final SecurityRequestWrapper request, final String regexName) {
        return SecurityFilterProperties.getInstance((HttpServletRequest)request).getRegexPattern(regexName);
    }
    
    public Pattern getRegexPattern(final String regexName) {
        return this.securityFilterConfig.getRegexPattern(regexName);
    }
    
    public static SecurityFilterProperties getInitParameters(final HttpServletRequest request) {
        return SecurityFilterProperties.getInstance(request);
    }
    
    public SecurityFilterProperties getInitParameters() {
        return this.securityFilterConfig;
    }
    
    private void setThreadName(final HttpServletRequest request) {
        SecurityFilter.CURRENT_THREAD_NAME.set(Thread.currentThread().getName());
        final String threadStr = SecurityUtil.getRequestPath(request) + "-" + System.currentTimeMillis() + "_###_" + Thread.currentThread().getName();
        Thread.currentThread().setName(threadStr);
    }
    
    private void reSetThreadName() {
        Thread.currentThread().setName(SecurityFilter.CURRENT_THREAD_NAME.get());
        SecurityFilter.CURRENT_THREAD_NAME.set("");
    }
    
    private void deleteUnClearedOldTempFiles() {
        final File tmpDirectory = SecurityUtil.getTemporaryDir();
        long clearedFilesSize = 0L;
        if (tmpDirectory.exists()) {
            final List<String> clearedFiles = new ArrayList<String>();
            final List<String> unClearedFiles = new ArrayList<String>();
            final List<String> unClearedDirs = new ArrayList<String>();
            for (final File tempFile : tmpDirectory.listFiles()) {
                final StringBuilder strBuilder = new StringBuilder("Name: ");
                strBuilder.append(tempFile.getName());
                strBuilder.append(", lastModificationTime: ");
                strBuilder.append(new Date(tempFile.lastModified()));
                strBuilder.append(", size (bytes): ");
                strBuilder.append(tempFile.length());
                if (tempFile.isFile()) {
                    if (tempFile.getName().startsWith("upload_")) {
                        clearedFiles.add(strBuilder.toString());
                        clearedFilesSize += tempFile.length();
                        tempFile.delete();
                    }
                    else {
                        unClearedFiles.add(strBuilder.toString());
                    }
                }
                else {
                    unClearedDirs.add(strBuilder.toString());
                }
            }
            SecurityFilter.logger.log(Level.INFO, "Cleared files in temp directory during server startup: {0}", new Object[] { clearedFiles });
            SecurityFilter.logger.log(Level.INFO, "Uncleared files in temp directory during server startup: {0}", new Object[] { unClearedFiles });
            SecurityFilter.logger.log(Level.INFO, "Uncleared directories in temp directory during server start up: {0}", new Object[] { unClearedDirs });
        }
        SecurityFilter.logger.log(Level.INFO, "Temp directory: {0}, IsExists: {1}, ClearedTempFilesSize: {2} bytes", new Object[] { tmpDirectory, tmpDirectory.exists(), clearedFilesSize });
    }
    
    static {
        logger = Logger.getLogger(SecurityFilter.class.getName());
        CURRENT_THREAD_NAME = new ThreadLocal<String>();
    }
}
