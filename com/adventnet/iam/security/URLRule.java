package com.adventnet.iam.security;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;
import java.util.Arrays;
import com.zoho.security.appfirewall.AppFirewallException;
import com.zoho.security.appfirewall.AppFirewallPolicyLoader;
import com.zoho.security.appfirewall.FirewallStage;
import com.zoho.security.eventfw.pojos.event.ZSEC_GHOSTCAT_VULNERABILITY;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_WAF_CONFIG_LOOKUP_PROBLEM;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class URLRule
{
    static final Logger logger;
    private String url;
    public static final String ZSEC_DEFAULT_OPERATION_VALUE = "ZSEC_DEFAULT_OPERATION_VALUE";
    public static final String REQUEST_URL_RULE = "RequestURLRule";
    private String actionParamName;
    private String redirectURL;
    private Map<String, Map<String, ActionRule>> actionRuleLookupMap;
    private List<ActionRule> allActionRules;
    private boolean urlInRegex;
    private List<String> webhookAccessMethods;
    private boolean isWebhookAccessAllowed;
    
    public URLRule(final String url, final String actionParamName, final boolean inRegex) {
        this(url, actionParamName, inRegex, null);
    }
    
    public URLRule(final String url, final String actionParamName, final boolean inRegex, final String redirectURL) {
        this.url = null;
        this.actionParamName = null;
        this.redirectURL = null;
        this.actionRuleLookupMap = new LinkedHashMap<String, Map<String, ActionRule>>();
        this.allActionRules = new LinkedList<ActionRule>();
        this.urlInRegex = false;
        this.webhookAccessMethods = null;
        this.isWebhookAccessAllowed = false;
        this.urlInRegex = inRegex;
        this.setUrl(url);
        this.actionParamName = actionParamName;
        this.redirectURL = redirectURL;
    }
    
    public URLRule(final String url, final String actionParamName) {
        this.url = null;
        this.actionParamName = null;
        this.redirectURL = null;
        this.actionRuleLookupMap = new LinkedHashMap<String, Map<String, ActionRule>>();
        this.allActionRules = new LinkedList<ActionRule>();
        this.urlInRegex = false;
        this.webhookAccessMethods = null;
        this.isWebhookAccessAllowed = false;
        this.setUrl(url);
        this.actionParamName = actionParamName;
    }
    
    public void setAccessMethodForWebhook(final boolean isAccessAllowed, final List<String> accessMethodsForWebhook) {
        this.webhookAccessMethods = accessMethodsForWebhook;
        this.isWebhookAccessAllowed = isAccessAllowed;
    }
    
    protected boolean isWebhookAccessllowed() {
        return this.isWebhookAccessAllowed;
    }
    
    public List<String> getAllowedMethodsInWebhook() {
        return this.webhookAccessMethods;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
    
    public String getActionParamName() {
        return this.actionParamName;
    }
    
    public void setActionParamName(final String actionParamName) {
        this.actionParamName = actionParamName;
    }
    
    public void setURLInRegex(final boolean regex) {
        this.urlInRegex = regex;
    }
    
    public boolean isURLInRegex() {
        return this.urlInRegex;
    }
    
    public ActionRule getActionRule(String method, String opParamValue) {
        method = SecurityUtil.getValidValue(method, "GET");
        opParamValue = SecurityUtil.getValidValue(opParamValue, "ZSEC_DEFAULT_OPERATION_VALUE");
        if (this.actionRuleLookupMap.containsKey(method)) {
            final Map<String, ActionRule> actionrule_map = this.actionRuleLookupMap.get(method);
            if (actionrule_map.containsKey(opParamValue)) {
                return actionrule_map.get(opParamValue);
            }
        }
        return null;
    }
    
    public Map<String, Map<String, ActionRule>> getActionRuleLookupMap() {
        return this.actionRuleLookupMap;
    }
    
    public List<ActionRule> getAllActionRule() {
        if (this.allActionRules.isEmpty()) {
            final Collection<Map<String, ActionRule>> actionruleLookupInnerMap = this.actionRuleLookupMap.values();
            for (final Map<String, ActionRule> actionruleLookupMap : actionruleLookupInnerMap) {
                final Collection<ActionRule> actionRules = actionruleLookupMap.values();
                this.allActionRules.addAll(actionRules);
            }
        }
        return this.allActionRules;
    }
    
    protected void addActionRule(final String method, final String operationParamValue, final ActionRule actionRule) {
        if (!this.actionRuleLookupMap.containsKey(method)) {
            final Map<String, ActionRule> actionrule_map = new HashMap<String, ActionRule>();
            actionrule_map.put(operationParamValue, actionRule);
            this.actionRuleLookupMap.put(method, actionrule_map);
        }
        else {
            final Map<String, ActionRule> actionrule_map = this.actionRuleLookupMap.get(method);
            if (!actionrule_map.containsKey(operationParamValue)) {
                actionrule_map.put(operationParamValue, actionRule);
            }
            else {
                if (operationParamValue.equalsIgnoreCase("ZSEC_DEFAULT_OPERATION_VALUE")) {
                    throw new RuntimeException(" '" + method + "' is already defined for the Same URL '" + this.getUrl() + "' NOTE : default method is \"GET\" if method is not configured ");
                }
                throw new RuntimeException("Operation paramater value '" + operationParamValue + "' is already defined for the URL '" + this.getUrl() + "'");
            }
        }
    }
    
    public void setRedirectURL(final String redirectURL) {
        this.redirectURL = redirectURL;
    }
    
    public String getRedirectURL() {
        return this.redirectURL;
    }
    
    String validateConfiguration(final SecurityFilterProperties sfp) {
        final List<String> errorMessages = new ArrayList<String>();
        final Collection<ActionRule> actionRules = this.getAllActionRule();
        for (final ActionRule arule : actionRules) {
            final String result = arule.validateConfiguration(sfp);
            if (result != null) {
                errorMessages.add(result);
            }
        }
        if (errorMessages.size() > 0) {
            return this.convertToString(errorMessages);
        }
        return null;
    }
    
    String convertToString(final List<String> messages) {
        String str = this.toString();
        for (final String message : messages) {
            str = str + "\n" + message;
        }
        return str + "\n";
    }
    
    public static SecurityRequestWrapper validateURLRule(final HttpServletRequest request, final SecurityRequestWrapper securedRequest, final HttpServletResponse serResponse, final String inputStreamContent) throws IOException {
        final SecurityFilterProperties securityFilterConfig = SecurityFilterProperties.getInstance(request);
        final SecurityResponseWrapper response = (SecurityResponseWrapper)serResponse;
        final UserAgent ua = securedRequest.getUserAgent();
        if (!securedRequest.skipHeaderValidationAPIMode && ua != null && !securityFilterConfig.isAllowedUserAgent(ua.getBrowserName(), ua.getBrowserMajorVersion())) {
            URLRule.logger.log(Level.SEVERE, "UNSUPPORTED_BROWSER_VERSION. VERSION {0}, BROWSER NAME {1}, URL {2}", new Object[] { ua.getBrowserMajorVersion(), ua.getBrowserName(), securedRequest.getRequestURI() });
            throw new IAMSecurityException("UNSUPPORTED_BROWSER_VERSION", securedRequest.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
        }
        request.setAttribute(SecurityResponseWrapper.class.getName(), (Object)response);
        securedRequest.setOriginalInputStreamContent(inputStreamContent);
        final String uri = SecurityUtil.getRequestURIForURLRuleLookup((HttpServletRequest)securedRequest, securityFilterConfig);
        final URLRule urlRule = securityFilterConfig.getURLRule(uri);
        final String standardRequestPath = SecurityUtil.ignoreURIPrefixAndTrailingSlash(SecurityUtil.getStandardRequestPathForAnalysis(request), securityFilterConfig);
        final URLRule urlRule_standardPath = securityFilterConfig.getURLRule(standardRequestPath);
        if (urlRule_standardPath == null && urlRule != null && !"/".equals(uri)) {
            ZSEC_WAF_CONFIG_LOOKUP_PROBLEM.pushSsWcNotFound(uri, urlRule.getUrl(), standardRequestPath, request.getServletPath(), request.getPathInfo(), (ExecutionTimer)null);
        }
        else if (securityFilterConfig.followServletStdForUrlPath()) {
            if (urlRule_standardPath != null && urlRule != null) {
                if (!urlRule_standardPath.getUrl().equals(urlRule.getUrl())) {
                    ZSEC_WAF_CONFIG_LOOKUP_PROBLEM.pushRqAndSsWcMismatch(uri, urlRule.getUrl(), standardRequestPath, urlRule_standardPath.getUrl(), request.getServletPath(), request.getPathInfo(), (ExecutionTimer)null);
                }
            }
            else if (urlRule_standardPath == null && urlRule != null && !"/".equals(uri)) {
                URLRule.logger.log(Level.WARNING, "uri - {0} , Standard path -{1}  url configuration not found . so validated with configured path {2} ", new Object[] { uri, standardRequestPath, urlRule.getUrl() });
                ZSEC_WAF_CONFIG_LOOKUP_PROBLEM.pushSsWcNotFound(uri, urlRule.getUrl(), standardRequestPath, request.getServletPath(), request.getPathInfo(), (ExecutionTimer)null);
            }
            else if (urlRule_standardPath != null && urlRule == null) {
                ZSEC_WAF_CONFIG_LOOKUP_PROBLEM.pushRqWcNotFound(uri, standardRequestPath, urlRule_standardPath.getUrl(), request.getServletPath(), request.getPathInfo(), (ExecutionTimer)null);
            }
        }
        if (urlRule == null) {
            throw new IAMSecurityException("URL_RULE_NOT_CONFIGURED", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
        }
        if (urlRule.getRedirectURL() != null && !"".equals(urlRule.getRedirectURL())) {
            String redirectURLHttps = urlRule.getRedirectURL();
            final String queryString = request.getQueryString();
            if (queryString != null) {
                if (redirectURLHttps.indexOf(63) != -1) {
                    redirectURLHttps = redirectURLHttps + "&" + queryString;
                }
                else {
                    redirectURLHttps = redirectURLHttps + "?" + queryString;
                }
            }
            response.sendRedirect(request.getContextPath() + redirectURLHttps);
            return securedRequest;
        }
        final ActionRule actionRule = securityFilterConfig.getURLActionRule(securedRequest, urlRule);
        if (actionRule != null) {
            if (actionRule.isErrorPage()) {
                securedRequest.setAttribute("RequestURLRule", request.getAttribute("urlrule"));
            }
            securedRequest.setAttribute("urlrule", actionRule);
            if (actionRule.getUrlBlacklistRules() != null) {
                for (final BlacklistRule urlBlacklistRule : actionRule.getUrlBlacklistRules()) {
                    if (SecurityUtil.isBlacklisted(uri, urlBlacklistRule)) {
                        URLRule.logger.log(Level.SEVERE, "Blacklisted URL found, Request URI: \"{0}\", Blacklisted Config: \"{1}\", URL Path: \"{2}\"", new Object[] { uri, urlBlacklistRule.getValue(), actionRule.getPath() });
                        throw new IAMSecurityException("BLACKLISTED_URL", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
                    }
                }
            }
            final String includeSPath = (String)request.getAttribute("javax.servlet.include.servlet_path");
            if (includeSPath != null) {
                final Map<String, String> includeAttributes = new HashMap<String, String>(2);
                includeAttributes.put("javax.servlet.include.servlet_path", includeSPath);
                final String pathInfo = (String)request.getAttribute("javax.servlet.include.path_info");
                if (pathInfo != null) {
                    includeAttributes.put("javax.servlet.include.path_info", pathInfo);
                }
                ZSEC_GHOSTCAT_VULNERABILITY.pushInfo(request.getRequestURI(), actionRule.getPrefix(), actionRule.getPath(), actionRule.getMethod(), actionRule.getOperationValue(), request.getServletPath(), request.getPathInfo(), (Map)includeAttributes, (ExecutionTimer)null);
            }
            if (!securedRequest.skipHeaderValidationAPIMode && (SecurityFilterProperties.RequestHeaderValidationMode.DISABLE != securityFilterConfig.getInternalReqHeaderValidationMode() || SecurityFilterProperties.RequestHeaderValidationMode.DISABLE != securityFilterConfig.getReqHeaderValidationMode())) {
                actionRule.validateRequestHeaders(securedRequest, securityFilterConfig);
            }
            if (actionRule.isCORSUrl() && securedRequest.isCorsRequest()) {
                if (securedRequest.isCorsPreFlightRequest()) {
                    response.setAllResponseHeaders();
                    return securedRequest;
                }
                request.setAttribute("CORS_REQUEST_TYPE", (Object)SecurityFilterProperties.CORS_REQUEST_TYPE.APPLICATION);
            }
            else if (securedRequest.isCorsPreFlightRequest()) {
                URLRule.logger.log(Level.SEVERE, " CORS response headers are not configured for url \"{0}\" , kindly configure the CORS response headers in order to handle the CORS Requests  ", new Object[] { request.getRequestURI() });
                throw new IAMSecurityException("CORS_NOT_CONFIGURED");
            }
        }
        if (securityFilterConfig.isRequestOptionsEnabled() && securedRequest.isOptionsURL()) {
            String methodStr = "";
            int i = 0;
            final Set<String> methodSet = urlRule.actionRuleLookupMap.keySet();
            for (final String method : methodSet) {
                ++i;
                methodStr += ((i <= methodSet.size() - 1) ? (method + ",") : method);
            }
            if (!methodSet.contains("OPTIONS")) {
                methodStr += ",OPTIONS";
            }
            response.addHeader("Allow", methodStr);
            response.getWriter().println();
            response.setStatus(200);
            return securedRequest;
        }
        if (actionRule == null) {
            throw new IAMSecurityException("INVALID_METHOD", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"), SecurityUtil.getRequestMethodForActionRuleLookup((HttpServletRequest)securedRequest));
        }
        securityFilterConfig.getRACProvider().setThreadLocalActionRule(actionRule.getChildActionRule());
        securedRequest.setURLActionRule(actionRule);
        response.setResponseHeaders();
        if (actionRule.parseRequestBody()) {
            if (securedRequest.isMultipartRequest()) {
                securedRequest.initMultipartParams();
            }
            else if ((!securedRequest.isAPIRequestValidation || (request instanceof APIRequestWrapper && ((APIRequestWrapper)request).hasInputStream())) && !SecurityUtil.isFormURLEncodedRequest((HttpServletRequest)securedRequest)) {
                final ParameterRule inputStreamRule = SecurityUtil.getInputStreamRule(request, actionRule);
                if (inputStreamRule != null && !inputStreamRule.isImportFile()) {
                    final boolean isBinaryType = inputStreamRule.isInputStreamTypeBinary();
                    final long maxSize = isBinaryType ? inputStreamRule.getInputStreamMaxSizeInKB() : inputStreamRule.getMaxLength();
                    securedRequest.cacheInputStream(maxSize, isBinaryType);
                }
            }
        }
        if (actionRule.isCaptchaVerificationEnabled() && !SimpleCaptchaUtil.verifyCAPTCHA((HttpServletRequest)securedRequest, securedRequest.getParameterForValidation("captcha-digest"), securedRequest.getParameterForValidation("captcha"))) {
            throw new IAMSecurityException("CAPTCHA_VERIFICATION_FAILED", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
        }
        if (AppFirewallPolicyLoader.activateRequestFirewall(securityFilterConfig, (HttpServletRequest)securedRequest, FirewallStage.POST_STAGE)) {
            URLRule.logger.log(Level.SEVERE, "  {0}  ", AppFirewallException.class.getName() + " : " + "INVALID_REQUEST_COMPONENT");
            AppFirewallPolicyLoader.doAction((HttpServletRequest)securedRequest, response);
            return securedRequest;
        }
        securedRequest.validateDefaultParameters();
        if (actionRule.containsImportContent()) {
            securedRequest.importFiles();
        }
        final String csrfParamValue = securedRequest.getParameterForValidation(securityFilterConfig.getCSRFParamName());
        if (csrfParamValue != null) {
            securedRequest.addValidatedParameter(securityFilterConfig.getCSRFParamName());
            if (securedRequest.enableSecretValueMasking) {
                final String maskedCsrfParamValue = MaskUtil.getCSRFMaskedValue(csrfParamValue);
                ((SecurityLogRequestWrapper)SecurityUtil.getCurrentLogRequest()).addPartiallyMaskedParameter(securityFilterConfig.getCSRFParamName(), Arrays.asList(maskedCsrfParamValue));
            }
        }
        securedRequest.setAttribute("ZSEC_URL_UNIQUE_PATH", actionRule.getUniquePath());
        actionRule.validate(securedRequest, (HttpServletResponse)response);
        if (actionRule.isRequestBodyRequiredForVerification() && securedRequest instanceof SecurityRequestBodyWrapper) {
            securityFilterConfig.getSecurityProvider().verifyRequest((HttpServletRequest)securedRequest, actionRule);
        }
        if (securityFilterConfig.isDisableParameterValidationForTestingOutputEncoding() && securityFilterConfig.isDevelopmentMode() && SecurityUtil.isTrustedIP((HttpServletRequest)securedRequest)) {
            securedRequest.disableParamInputValidationForTestingOE = true;
        }
        if (actionRule.getOrCriteriaRules() != null) {
            for (final OrCriteriaRule criteriaRule : actionRule.getOrCriteriaRules()) {
                int noOfOccurrences = 0;
                final List<String> configuredParams = new ArrayList<String>();
                final List<String> requestParams = new ArrayList<String>();
                for (final ParameterRule paramRule : criteriaRule.getParameterRules()) {
                    configuredParams.add(paramRule.getParamName());
                    final int individualParamOccurrence = paramRule.getNumberOfOccurances(securedRequest.getParameterValuesForValidation(paramRule.getParamName()));
                    if (individualParamOccurrence > 0) {
                        if (paramRule.isMaxOccurrenceConfigured) {
                            paramRule.checkForMaxOccurrence(securedRequest, individualParamOccurrence, paramRule.getParamName());
                        }
                        noOfOccurrences += individualParamOccurrence;
                        requestParams.add(paramRule.getParamName());
                        if (ParameterRule.isParamOccuranceExceedsMaxLimit(noOfOccurrences, criteriaRule.getMaxOccurrences())) {
                            URLRule.logger.log(Level.SEVERE, "The occurrences of the parameters \"{0}\" for the URL \"{1}\" is more than the maximum occurances configured in the criteria rule :\n{2}", new Object[] { requestParams.toString(), securedRequest.getRequestURI(), criteriaRule.toString() });
                            throw new IAMSecurityException("MORE_THAN_MAX_OCCURANCE", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"), requestParams.toString(), noOfOccurrences);
                        }
                        continue;
                    }
                }
                if (ParameterRule.isParamOccuranceLessThanMinLimit(noOfOccurrences, criteriaRule.getMinOccurrences(), securedRequest)) {
                    if (noOfOccurrences == 0) {
                        URLRule.logger.log(Level.SEVERE, "Expecting one of the parameters \"{0}\" for the URL : \"{1}\", Criteria Rule is : \n{2}", new Object[] { configuredParams.toString(), securedRequest.getRequestURI(), criteriaRule.toString() });
                    }
                    else {
                        URLRule.logger.log(Level.SEVERE, "The occurrences of the parameters \"{0}\" for the URL \"{1}\" is less than the minimum occurances configured in the criteria rule :\n{2}", new Object[] { requestParams.toString(), securedRequest.getRequestURI(), criteriaRule.toString() });
                    }
                    throw new IAMSecurityException("LESS_THAN_MIN_OCCURANCE", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"), (noOfOccurrences == 0) ? configuredParams.toString() : requestParams.toString(), noOfOccurrences);
                }
            }
        }
        IAMSecurityException exception = null;
        for (final ParameterRule paramRule2 : actionRule.getParameterRules().values()) {
            try {
                paramRule2.validate(securedRequest);
            }
            catch (final IAMSecurityException e) {
                if (paramRule2.getDefaultValue() != null) {
                    if (paramRule2.isParamNameInRegex()) {
                        final Enumeration<String> params = securedRequest.getParameterNames();
                        final RegexRule regexRule = securityFilterConfig.getRegexRule(paramRule2.getParamName());
                        while (params.hasMoreElements()) {
                            final String paramName = params.nextElement();
                            if (!securedRequest.isValidated(paramName) && SecurityUtil.matchPattern(paramName, regexRule)) {
                                securedRequest.addValidatedParameterValue(paramName, paramRule2.getDefaultValue(), paramRule2);
                                securedRequest.addValidatedParameter(paramName);
                            }
                        }
                    }
                    else {
                        securedRequest.addValidatedParameterValue(paramRule2.getParamName(), paramRule2.getDefaultValue(), paramRule2);
                        securedRequest.addValidatedParameter(paramRule2.getParamName());
                    }
                }
                else if (paramRule2.isInputStream(paramRule2.getParamName()) && paramRule2.streamContentValidationMode == SecurityFilterProperties.InputStreamValidationMode.LOG) {
                    securedRequest.addValidatedParameterValue(paramRule2.getParamName(), securedRequest.getOriginalInputStreamContent(), paramRule2);
                    securedRequest.addValidatedParameter("zoho-inputstream");
                }
                else {
                    if (!actionRule.throwAllErrors()) {
                        throw e;
                    }
                    if (exception == null) {
                        exception = new IAMSecurityException();
                    }
                    exception.addIAMSecurityException(e);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
        if (securedRequest.getMultipartFiles() != null) {
            final List<UploadedFileItem> uploadedList = securedRequest.getMultipartFiles();
            for (final UploadedFileItem fileItem : uploadedList) {
                UploadFileRule rule = actionRule.getUploadFileRule(fileItem.getFieldName());
                if (rule == null) {
                    rule = securityFilterConfig.getSecurityProvider().getDynamicFileRule((HttpServletRequest)securedRequest, actionRule, fileItem);
                }
                if (rule == null) {
                    throw new RuntimeException("File rule not configured for the URL " + request.getRequestURI());
                }
                try {
                    rule.validate(securedRequest, fileItem);
                }
                catch (final IAMSecurityException e2) {
                    if (!actionRule.throwAllErrors()) {
                        throw e2;
                    }
                    if (exception == null) {
                        exception = new IAMSecurityException();
                    }
                    exception.addIAMSecurityException(e2);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
        if (actionRule.hasDynamicParams()) {
            final List<ParameterRule> paramRules = securityFilterConfig.getSecurityProvider().getDynamicParameterRules((HttpServletRequest)securedRequest);
            if (paramRules != null) {
                if (securityFilterConfig.isDevelopmentMode()) {
                    final List<String> errorMessages = new ArrayList<String>();
                    for (final ParameterRule paramRule3 : paramRules) {
                        final String result = paramRule3.validateConfiguration(securityFilterConfig);
                        if (result != null) {
                            errorMessages.add(result);
                        }
                    }
                    if (errorMessages.size() > 0) {
                        URLRule.logger.log(Level.SEVERE, RuleSetParser.convertToString(errorMessages));
                        throw new RuntimeException("Invalid dynamic parameter rules specified.");
                    }
                }
                for (final ParameterRule paramRule4 : paramRules) {
                    if (paramRule4.isParamNameInRegex() && !paramRule4.isMaxOccurrenceConfigured && !securityFilterConfig.isEnabledIndividualOccurrenceCheckForDynamicParams()) {
                        paramRule4.setMaxOccurrences(securityFilterConfig.getDynamicParamsMaxOccurrenceLimit());
                    }
                    paramRule4.ruleName = "param";
                    try {
                        paramRule4.validate(securedRequest);
                    }
                    catch (final IAMSecurityException e3) {
                        if (!actionRule.throwAllErrors()) {
                            throw e3;
                        }
                        if (exception == null) {
                            exception = new IAMSecurityException();
                        }
                        exception.addIAMSecurityException(e3);
                    }
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
        final ParameterRule dynamicInputStreamRule = securityFilterConfig.getSecurityProvider().getDynamicInputStreamRule((HttpServletRequest)securedRequest, actionRule);
        if (dynamicInputStreamRule != null && actionRule.getInputStreamRule() == null && actionRule.getParamOrStreamRule() == null) {
            if (securityFilterConfig.isDevelopmentMode()) {
                final String result2 = dynamicInputStreamRule.validateConfiguration(securityFilterConfig);
                if (result2 != null) {
                    URLRule.logger.log(Level.SEVERE, "Invalid dynamic inputstream rule specified for the url : {0}, Error Message : {1}", new Object[] { actionRule.getUniquePath(), result2 });
                    throw new IAMSecurityException("INVALID_CONFIGURATION");
                }
            }
            dynamicInputStreamRule.validate(securedRequest);
        }
        final Enumeration<String> params2 = securedRequest.getParameterNames();
        final List<String> extraParamsList = new ArrayList<String>();
        final ParameterRule extraParamRule = SecurityUtil.getExtraParameterRule((HttpServletRequest)securedRequest, actionRule);
        final boolean isExtraParamRuleExist = extraParamRule != null && !actionRule.isErrorPage();
        final boolean extraParamMaskingEnabled = securityFilterConfig.isExtraParamMaskingEnabled() && securedRequest.enableSecretValueMasking;
        while (params2.hasMoreElements()) {
            final String paramName2 = params2.nextElement();
            if (SecurityUtil.isCaptchaParam(paramName2, actionRule)) {
                continue;
            }
            if (SecurityUtil.isOperationParamOrHipDigestParam(paramName2, actionRule) || securedRequest.isValidated(paramName2)) {
                continue;
            }
            if (isExtraParamRuleExist) {
                extraParamsList.add(paramName2);
                if (extraParamMaskingEnabled) {
                    ((SecurityLogRequestWrapper)SecurityUtil.getCurrentLogRequest()).addExtraParameter(paramName2);
                }
                if (extraParamsList.size() > extraParamRule.getLimit()) {
                    URLRule.logger.log(Level.SEVERE, "Extra parameter limit({0}) exceeded : {1} for the URI : {2}", new Object[] { extraParamRule.getLimit(), extraParamsList, actionRule.getUniquePath() });
                    throw new IAMSecurityException("EXTRA_PARAM_LIMIT_EXCEEDED", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"), extraParamsList.get(0));
                }
                extraParamRule.validateParam(securedRequest, paramName2);
            }
            else {
                if (!actionRule.isIgnoreExtraParam() && !actionRule.isErrorPage()) {
                    if (extraParamMaskingEnabled) {
                        ((SecurityLogRequestWrapper)SecurityUtil.getCurrentLogRequest()).addExtraParameter(paramName2);
                    }
                    URLRule.logger.log(Level.SEVERE, "Extra parameter found : the parameter name : {0} for the URI : {1}", new Object[] { paramName2, actionRule.getUniquePath() });
                    throw new IAMSecurityException("EXTRA_PARAM_FOUND", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"), paramName2);
                }
                securedRequest.addUnvalidatedExtraParam(paramName2);
            }
        }
        if (!extraParamsList.isEmpty()) {
            URLRule.logger.log(Level.WARNING, "Extra parameter found: the parameter List : {0} for the URI : {1}", new Object[] { extraParamsList, actionRule.getUniquePath() });
        }
        else {
            final List<String> unValidatedExtraParams = securedRequest.getUnvalidatedExtraParams();
            if (unValidatedExtraParams != null && !unValidatedExtraParams.isEmpty()) {
                URLRule.logger.log(Level.WARNING, "Ignored Extra parameter List : {0} for the URI : {1}", new Object[] { unValidatedExtraParams, actionRule.getUniquePath() });
                if (securityFilterConfig.isIgnoreExtraParamMaskingEnabled() && securedRequest.enableSecretValueMasking) {
                    ((SecurityLogRequestWrapper)SecurityUtil.getCurrentLogRequest()).addIgnoredExtraParameters(unValidatedExtraParams);
                }
            }
        }
        securedRequest.paramValidationCompleted = true;
        securedRequest.unLock();
        securedRequest.validateQueryString();
        return securedRequest;
    }
    
    @Override
    public String toString() {
        String str = "URLRule ::  path = \"" + this.url + "\"";
        if (this.actionParamName != null && this.actionParamName.length() > 0) {
            str = str + " actionParamName = \"" + this.actionParamName + "\"";
        }
        str = str + "  urlInRegex  = \"" + this.urlInRegex + "\"";
        return str;
    }
    
    static {
        logger = Logger.getLogger(URLRule.class.getName());
    }
}
