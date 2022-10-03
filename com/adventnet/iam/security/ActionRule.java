package com.adventnet.iam.security;

import java.util.Arrays;
import java.util.Set;
import com.zoho.security.eventfw.pojos.log.ZSEC_PERFORMANCE_ANOMALY;
import java.util.Enumeration;
import java.util.Collections;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.ProxyInfo;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Node;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;

public class ActionRule
{
    private static final Logger logger;
    private String path;
    private String prefix;
    private String requestMethod;
    private String operationParam;
    private String operationValue;
    private String uniquePath;
    private boolean isCSRFProtected;
    private boolean disableGetApiCsrf;
    private boolean setCSRFCookie;
    private boolean internal;
    private boolean trusted;
    private boolean isAPI;
    private boolean isISC;
    private String iscScope;
    private String apiScope;
    private List<String> scopedServices;
    private String runAsGroupIdParam;
    private String runAsGroupTypeParam;
    private boolean ipbl;
    private String[] roles;
    private String[] appAccountMemberRoles;
    private boolean dynamicParams;
    private String authentication;
    private Object accountsAttributes;
    private boolean throwAllErrors;
    private boolean urlXSSValidation;
    private boolean doscookie;
    private String actionForward;
    private String operationType;
    private long maxFileSize;
    private Map<String, ParameterRule> paramRuleMap;
    private Map<String, ParameterRule> paramNameRegexRuleMap;
    private Map<String, ParameterRule> allParamRules;
    private Map<String, UploadFileRule> allFileRuleMap;
    private Map<String, UploadFileRule> fileRuleMap;
    private Map<String, UploadFileRule> fileRegexRuleMap;
    private HashMap<String, String> customAttributes;
    private boolean containsImportContent;
    private long maxRequestSizeInKB;
    private long fileUploadMaxSize;
    private boolean loginThrowError;
    private SecurityFilterProperties filterProps;
    private List<String> secretParamNames;
    private List<ParameterRule> secretParamNameRegexRules;
    private List<String> secretRequestHeaders;
    private Map<String, ParameterRule> partialMaskingParamRuleMap;
    private Map<String, ParameterRule> partialMaskingReqHeaderRuleMap;
    private String jsonImpurity;
    private List<OrCriteriaRule> orCriteriaRules;
    private List<String> paramGroupList;
    public boolean hasJSONSecretParam;
    private boolean internalOrTrusted;
    private String urlType;
    private boolean isPatternURL;
    private ChildActionRule appFirewallActionRule;
    private ParameterRule extraParameterRule;
    private boolean isDisableExtraparam;
    private List<String> oAuthScopeList;
    private List<String> orgOAuthScopeList;
    private List<String> authTypeList;
    private Map<String, ResponseHeaderRule> responseHeaderMap;
    private Map<String, HeaderRule> requestHeaderMapWithStrictName;
    private List<String> mandatoryRequestHeaders;
    private Map<String, HeaderRule> requestHeaderMapWithRegexName;
    private CookieRequestHeaderRule cookieRule;
    private UserAgentRequestHeaderRule userAgentRule;
    private List<String> disableSafeHeaders;
    private List<String> disableHeaders;
    private List<String> disableCacheHeaders;
    private String xframeType;
    static final String REQUESTTAG = "request";
    static final String HEADERSTAG = "headers";
    static final String HEADERTAG = "header";
    static final String NAMEATTRIBUTE = "name";
    private ResponseRule responseRule;
    private String systemAuth;
    private List<String> allowedServicesViaProxy;
    private boolean blockProxyRequest;
    private boolean isURLRestrictedViaImport;
    private Boolean ignoreExtraParam;
    protected Map<ThrottlesRule.Windows, List<ThrottlesRule>> throttlesRuleMap;
    private boolean dynamicThrottles;
    protected boolean skipHipDigestParamFromExtraParamValidation;
    private boolean captchaVerification;
    boolean hasOldThrottleConfiguration;
    boolean dbCacheForServiceScopeThrottles;
    private static final int OLD_SINGLE_THROTTLES_RULE_INDEX = 0;
    private List<String> webhookAccessMethods;
    private boolean isWebhookSupported;
    private static final List<String> WEBHOOK_SUPPORTED_METHODS;
    private boolean dcSystemAuth;
    private boolean isErrorPage;
    private String module;
    private String sub_module;
    private String operation;
    private boolean parseRequestBody;
    private CORSConfigType corsConfigType;
    private boolean isPartsConfiguredForMultipart;
    private boolean isRequestBodyRequiredForVerification;
    private Pattern requestDispatchURIsPattern;
    private boolean skipROCheck;
    private String tempFileNamePathAttribute;
    private List<BlacklistRule> blacklistedUrlRules;
    private String allowAccessFor;
    
    public ActionRule() {
        this.path = null;
        this.prefix = null;
        this.requestMethod = null;
        this.operationParam = null;
        this.operationValue = null;
        this.uniquePath = null;
        this.isCSRFProtected = false;
        this.disableGetApiCsrf = false;
        this.setCSRFCookie = true;
        this.internal = false;
        this.trusted = false;
        this.isAPI = false;
        this.isISC = false;
        this.iscScope = null;
        this.apiScope = null;
        this.scopedServices = null;
        this.runAsGroupIdParam = null;
        this.runAsGroupTypeParam = null;
        this.ipbl = false;
        this.roles = null;
        this.appAccountMemberRoles = null;
        this.dynamicParams = false;
        this.authentication = null;
        this.accountsAttributes = null;
        this.throwAllErrors = false;
        this.urlXSSValidation = true;
        this.doscookie = false;
        this.actionForward = null;
        this.operationType = null;
        this.maxFileSize = -1L;
        this.paramRuleMap = new LinkedHashMap<String, ParameterRule>();
        this.paramNameRegexRuleMap = new LinkedHashMap<String, ParameterRule>();
        this.allParamRules = null;
        this.allFileRuleMap = new LinkedHashMap<String, UploadFileRule>();
        this.fileRuleMap = new LinkedHashMap<String, UploadFileRule>();
        this.fileRegexRuleMap = new LinkedHashMap<String, UploadFileRule>();
        this.customAttributes = null;
        this.containsImportContent = false;
        this.maxRequestSizeInKB = -1L;
        this.fileUploadMaxSize = -1L;
        this.loginThrowError = false;
        this.filterProps = null;
        this.secretParamNames = new ArrayList<String>();
        this.secretParamNameRegexRules = new ArrayList<ParameterRule>();
        this.secretRequestHeaders = new ArrayList<String>();
        this.jsonImpurity = null;
        this.paramGroupList = new ArrayList<String>();
        this.hasJSONSecretParam = false;
        this.internalOrTrusted = false;
        this.urlType = null;
        this.isPatternURL = false;
        this.appFirewallActionRule = null;
        this.extraParameterRule = null;
        this.isDisableExtraparam = false;
        this.oAuthScopeList = null;
        this.orgOAuthScopeList = null;
        this.authTypeList = null;
        this.responseHeaderMap = new HashMap<String, ResponseHeaderRule>();
        this.requestHeaderMapWithStrictName = new HashMap<String, HeaderRule>();
        this.mandatoryRequestHeaders = new ArrayList<String>();
        this.requestHeaderMapWithRegexName = new HashMap<String, HeaderRule>();
        this.disableSafeHeaders = new ArrayList<String>();
        this.disableHeaders = new ArrayList<String>();
        this.disableCacheHeaders = null;
        this.xframeType = null;
        this.responseRule = null;
        this.systemAuth = "required";
        this.blockProxyRequest = false;
        this.isURLRestrictedViaImport = false;
        this.ignoreExtraParam = null;
        this.throttlesRuleMap = null;
        this.dynamicThrottles = false;
        this.skipHipDigestParamFromExtraParamValidation = false;
        this.webhookAccessMethods = null;
        this.isWebhookSupported = false;
        this.dcSystemAuth = false;
        this.isErrorPage = false;
        this.module = null;
        this.sub_module = null;
        this.operation = null;
        this.parseRequestBody = true;
        this.corsConfigType = CORSConfigType.NONE;
        this.isPartsConfiguredForMultipart = false;
        this.isRequestBodyRequiredForVerification = false;
        this.skipROCheck = false;
        this.allowAccessFor = null;
    }
    
    public ActionRule(final SecurityFilterProperties filterProperties, final Element element) {
        this.path = null;
        this.prefix = null;
        this.requestMethod = null;
        this.operationParam = null;
        this.operationValue = null;
        this.uniquePath = null;
        this.isCSRFProtected = false;
        this.disableGetApiCsrf = false;
        this.setCSRFCookie = true;
        this.internal = false;
        this.trusted = false;
        this.isAPI = false;
        this.isISC = false;
        this.iscScope = null;
        this.apiScope = null;
        this.scopedServices = null;
        this.runAsGroupIdParam = null;
        this.runAsGroupTypeParam = null;
        this.ipbl = false;
        this.roles = null;
        this.appAccountMemberRoles = null;
        this.dynamicParams = false;
        this.authentication = null;
        this.accountsAttributes = null;
        this.throwAllErrors = false;
        this.urlXSSValidation = true;
        this.doscookie = false;
        this.actionForward = null;
        this.operationType = null;
        this.maxFileSize = -1L;
        this.paramRuleMap = new LinkedHashMap<String, ParameterRule>();
        this.paramNameRegexRuleMap = new LinkedHashMap<String, ParameterRule>();
        this.allParamRules = null;
        this.allFileRuleMap = new LinkedHashMap<String, UploadFileRule>();
        this.fileRuleMap = new LinkedHashMap<String, UploadFileRule>();
        this.fileRegexRuleMap = new LinkedHashMap<String, UploadFileRule>();
        this.customAttributes = null;
        this.containsImportContent = false;
        this.maxRequestSizeInKB = -1L;
        this.fileUploadMaxSize = -1L;
        this.loginThrowError = false;
        this.filterProps = null;
        this.secretParamNames = new ArrayList<String>();
        this.secretParamNameRegexRules = new ArrayList<ParameterRule>();
        this.secretRequestHeaders = new ArrayList<String>();
        this.jsonImpurity = null;
        this.paramGroupList = new ArrayList<String>();
        this.hasJSONSecretParam = false;
        this.internalOrTrusted = false;
        this.urlType = null;
        this.isPatternURL = false;
        this.appFirewallActionRule = null;
        this.extraParameterRule = null;
        this.isDisableExtraparam = false;
        this.oAuthScopeList = null;
        this.orgOAuthScopeList = null;
        this.authTypeList = null;
        this.responseHeaderMap = new HashMap<String, ResponseHeaderRule>();
        this.requestHeaderMapWithStrictName = new HashMap<String, HeaderRule>();
        this.mandatoryRequestHeaders = new ArrayList<String>();
        this.requestHeaderMapWithRegexName = new HashMap<String, HeaderRule>();
        this.disableSafeHeaders = new ArrayList<String>();
        this.disableHeaders = new ArrayList<String>();
        this.disableCacheHeaders = null;
        this.xframeType = null;
        this.responseRule = null;
        this.systemAuth = "required";
        this.blockProxyRequest = false;
        this.isURLRestrictedViaImport = false;
        this.ignoreExtraParam = null;
        this.throttlesRuleMap = null;
        this.dynamicThrottles = false;
        this.skipHipDigestParamFromExtraParamValidation = false;
        this.webhookAccessMethods = null;
        this.isWebhookSupported = false;
        this.dcSystemAuth = false;
        this.isErrorPage = false;
        this.module = null;
        this.sub_module = null;
        this.operation = null;
        this.parseRequestBody = true;
        this.corsConfigType = CORSConfigType.NONE;
        this.isPartsConfiguredForMultipart = false;
        this.isRequestBodyRequiredForVerification = false;
        this.skipROCheck = false;
        this.allowAccessFor = null;
        this.filterProps = filterProperties;
        this.customAttributes = SecurityUtil.convertToMap(element);
        this.setPath(getURLAttribute(element, "path"));
        this.setMethod(getURLAttribute(element, "method"));
        this.setOperationParam(getURLAttribute(element, "operation-param"));
        this.setOperationValue(getURLAttribute(element, "operation-value"));
        this.setCSRFProtected("true".equalsIgnoreCase(getURLAttribute(element, "csrf")));
        this.disableGetApiCsrf = "true".equalsIgnoreCase(element.getAttribute("disable-get-api-csrf"));
        this.setCSRFCookie = !"false".equalsIgnoreCase(getURLAttribute(element, "set-csrf-cookie"));
        this.internal = "true".equalsIgnoreCase(getURLAttribute(element, "internal"));
        this.trusted = "true".equalsIgnoreCase(getURLAttribute(element, "trusted"));
        this.throwAllErrors = "true".equalsIgnoreCase(getURLAttribute(element, "throwallerrors"));
        this.apiScope = getURLAttribute(element, "apiscope");
        final String oAuthScopeStr = getURLAttribute(element, "oauthscope");
        if (SecurityUtil.isValid(oAuthScopeStr)) {
            this.setOAuthScopeList(oAuthScopeStr);
        }
        final String orgOAuthScopeStr = getURLAttribute(element, "org-oauthscope");
        if (SecurityUtil.isValid(orgOAuthScopeStr)) {
            this.setOrgOAuthScopeList(orgOAuthScopeStr);
        }
        final String authType = getURLAttribute(element, "auth-type");
        if (SecurityUtil.isValid(authType)) {
            this.setAuthTypeList(authType);
        }
        this.isAPI = (SecurityUtil.isValid(this.apiScope) || SecurityUtil.isValid(oAuthScopeStr) || SecurityUtil.isValid(orgOAuthScopeStr) || "true".equalsIgnoreCase(getURLAttribute(element, "api")));
        this.iscScope = getURLAttribute(element, "iscscope");
        this.isISC = (SecurityUtil.isValid(this.iscScope) || "true".equalsIgnoreCase(getURLAttribute(element, "isc")));
        this.runAsGroupIdParam = getURLAttribute(element, "runas-groupid-param");
        this.runAsGroupTypeParam = getURLAttribute(element, "runas-grouptype-param");
        this.urlXSSValidation = !"false".equalsIgnoreCase(getURLAttribute(element, "xss"));
        this.ipbl = "true".equalsIgnoreCase(getURLAttribute(element, "ipbl"));
        this.loginThrowError = "true".equalsIgnoreCase(getURLAttribute(element, "login-throwerror"));
        this.doscookie = "true".equalsIgnoreCase(getURLAttribute(element, "dos-cookie"));
        this.actionForward = getURLAttribute(element, "action-forward");
        this.urlType = getURLAttribute(element, "url-type");
        if (!(this.isPatternURL = "true".equals(getURLAttribute(element, "path-regex")))) {
            this.isPatternURL = ("dynamic".equals(this.urlType) || "multiple".equals(this.urlType) || this.path.indexOf("*") > -1 || this.path.indexOf("[") > -1 || this.path.indexOf("(") > -1);
        }
        final String maxRequestSizeStr = getURLAttribute(element, "max-request-size");
        if (SecurityUtil.isValid(maxRequestSizeStr)) {
            this.maxRequestSizeInKB = Long.parseLong(maxRequestSizeStr);
        }
        final String fileUploadMaxSizeStr = getURLAttribute(element, "file-upload-max-size");
        if (SecurityUtil.isValid(fileUploadMaxSizeStr)) {
            try {
                this.setFileUploadMaxSize(Long.parseLong(fileUploadMaxSizeStr));
            }
            catch (final IAMSecurityException e) {
                ActionRule.logger.log(Level.SEVERE, "The ''file-upload-max-size'' value is must be greater than -1. Path: \"{0}\"", new Object[] { this.path });
                throw e;
            }
        }
        this.operationType = getURLAttribute(element, "operation-type");
        this.authentication = getURLAttribute(element, "authentication");
        final String rolesStr = getURLAttribute(element, "roles");
        if (SecurityUtil.isValid(rolesStr)) {
            this.setRoles(rolesStr.split(","));
        }
        final String appAccountMemberRolesStr = getURLAttribute(element, "app-account-member-roles");
        if (SecurityUtil.isValid(appAccountMemberRolesStr)) {
            this.setAppAccountMemberRoles(appAccountMemberRolesStr.split(","));
        }
        this.dynamicParams = "true".equalsIgnoreCase(getURLAttribute(element, "dynamic-params"));
        final String scopedServicesStr = getURLAttribute(element, "scoped-services");
        if (SecurityUtil.isValid(scopedServicesStr)) {
            this.scopedServices = SecurityUtil.getStringAsList(scopedServicesStr, ",");
        }
        this.jsonImpurity = getURLAttribute(element, "response-json-impurity");
        this.internalOrTrusted = "true".equalsIgnoreCase(getURLAttribute(element, "internalortrusted"));
        this.xframeType = getURLAttribute(element, "xframe-type");
        final String webhookMethods = getURLAttribute(element, "webhook-access-method");
        if (SecurityUtil.isValid(webhookMethods)) {
            this.webhookAccessMethods = new ArrayList<String>();
            for (final String wmethod : webhookMethods.toUpperCase().split(",")) {
                if (!ActionRule.WEBHOOK_SUPPORTED_METHODS.contains(wmethod)) {
                    throw new RuntimeException("\"" + wmethod + "\" not supported in the webhook-access-method attribute in the url " + this.getPath() + "'\n");
                }
                this.webhookAccessMethods.add(wmethod);
            }
            this.isWebhookSupported = true;
        }
        final String sAuth = getURLAttribute(element, "system-auth");
        if (SecurityUtil.isValid(sAuth)) {
            if ("optional".equalsIgnoreCase(sAuth) && !this.isAuthenticationRequired()) {
                throw new RuntimeException("system-auth='optional' not allowed for the public/optional URL '" + this.getPath() + "'\n");
            }
            this.systemAuth = sAuth.toLowerCase();
        }
        this.blockProxyRequest = (this.isInternal() || (SecurityUtil.isValidList(this.scopedServices) && !this.systemAuth.equals("optional")));
        this.isURLRestrictedViaImport = (this.internal || this.internalOrTrusted);
        this.isDisableExtraparam = "true".equalsIgnoreCase(getURLAttribute(element, "disable-extraparam"));
        final String ignoreExtraParam = getURLAttribute(element, "ignore-extraparam");
        if (SecurityUtil.isValid(ignoreExtraParam)) {
            this.setIgnoreExtraParam("true".equalsIgnoreCase(ignoreExtraParam));
        }
        if (this.isDisableExtraparam && "true".equalsIgnoreCase(ignoreExtraParam)) {
            throw new RuntimeException("Invalid extraparam configuration :: Either disable-extraparam or ignore-extraparam attribute should be used for the URL '" + this.getPath() + "'\n");
        }
        this.captchaVerification = "true".equals(getURLAttribute(element, "captcha-verification"));
        this.dcSystemAuth = "true".equalsIgnoreCase(getURLAttribute(element, "dc-system-auth"));
        if (this.dcSystemAuth && (this.internal || this.internalOrTrusted || SecurityUtil.isValid(this.scopedServices))) {
            throw new RuntimeException("dc-system-auth=\"true\" not allowed for internal/scoped-services configured URL'" + this.getPath() + "'\n");
        }
        this.isErrorPage = "true".equalsIgnoreCase(getURLAttribute(element, "error-page"));
        this.module = getURLAttribute(element, "zs-module");
        this.sub_module = getURLAttribute(element, "zs-sub-module");
        this.operation = getURLAttribute(element, "zs-operation");
        this.parseRequestBody = !"false".equalsIgnoreCase(getURLAttribute(element, "parse-request-body"));
        this.isRequestBodyRequiredForVerification = "true".equalsIgnoreCase(getURLAttribute(element, "requestbody-required-for-verification"));
        this.skipROCheck = "true".equalsIgnoreCase(getURLAttribute(element, "skip-ro-check"));
        this.initURLBlacklistRule(element);
        this.initParameterRuleMap(element);
        this.initInputStreamRuleMap(element);
        if (this.isRequestBodyRequiredForVerification && this.paramRuleMap.containsKey("zoho-inputstream")) {
            throw new RuntimeException("Invalid configuration :: Either <inputstream ../> tag or 'requestbody-required-for-verification' attribute at <url> level should be specified for the URL '" + this.getPath() + "'\n");
        }
        this.initParamOrStreamRule(element);
        this.initUploadFileRuleMap(element);
        this.initExtraParamRule(element);
        if (this.extraParameterRule != null && (this.isDisableExtraparam || "true".equalsIgnoreCase(ignoreExtraParam))) {
            throw new RuntimeException("Invalid extraparam configuration :: Either extraparameter rule or disable/ignore-extraparam should be specified for the URL '" + this.getPath() + "'\n");
        }
        this.initResponseHeaderRule(element);
        this.initRequestHeaderRule(element);
        this.initResponseRule(element);
        this.initThrottlesRule(element);
        this.initProxyPolicies(element);
        for (final UploadFileRule rule : this.getUploadFileRuleList()) {
            final long size = rule.getMaxSizeInKB() * 1028L;
            if (this.maxFileSize < size) {
                this.maxFileSize = size;
            }
            if (rule.isImportURL()) {
                this.containsImportContent = true;
            }
        }
        if (this.fileUploadMaxSize != -1L) {
            this.maxFileSize = this.fileUploadMaxSize;
        }
        this.isPartsConfiguredForMultipart = "true".equalsIgnoreCase(getURLAttribute(element, "parts-configured-for-mutlipart"));
        this.setRequestDispatchURIs(getURLAttribute(element, "request-dispatch-uri-pattern"));
        if (SecurityUtil.isValid(getURLAttribute(element, "multi-part"))) {
            ActionRule.logger.log(Level.SEVERE, "URL attribute \"multi-part\" will be deprecated soon, please remove it from the url \"{0}\"", this.getPath());
        }
        this.allowAccessFor = getURLAttribute(element, "allow-access-for");
    }
    
    public boolean skipROCheck() {
        return this.skipROCheck;
    }
    
    public boolean isRequestBodyRequiredForVerification() {
        return this.isRequestBodyRequiredForVerification;
    }
    
    void setRequestDispatchURIs(final String urlAttribute) {
        if (SecurityUtil.isValid(urlAttribute)) {
            this.requestDispatchURIsPattern = Pattern.compile(urlAttribute);
        }
    }
    
    public Pattern getRequestDispatchURIsPattern() {
        return this.requestDispatchURIsPattern;
    }
    
    private void setAuthTypeList(final String authTypeStr) {
        this.authTypeList = SecurityUtil.getStringAsList(authTypeStr, ",");
    }
    
    public boolean parseRequestBody() {
        return this.parseRequestBody;
    }
    
    public boolean isDCSystemAuthRequired() {
        return this.dcSystemAuth;
    }
    
    public boolean isIgnoreExtraParam() {
        return (this.ignoreExtraParam == null) ? this.filterProps.isIgnoreExtraParam() : this.ignoreExtraParam;
    }
    
    private void setIgnoreExtraParam(final boolean ignoreExtraParam) {
        this.ignoreExtraParam = ignoreExtraParam;
    }
    
    static String getURLAttribute(final Element element, final String attributeName) {
        String attributeValue = element.getAttribute(attributeName);
        if (SecurityUtil.isValid(attributeValue) || "urls".equals(element.getNodeName())) {
            return attributeValue;
        }
        attributeValue = ((Element)element.getParentNode()).getAttribute(attributeName);
        return SecurityUtil.isValid(attributeValue) ? attributeValue : null;
    }
    
    public boolean hasInputStream() {
        return this.paramRuleMap.containsKey("zoho-inputstream");
    }
    
    public ParameterRule getInputStreamRule() {
        return this.paramRuleMap.get("zoho-inputstream");
    }
    
    public ParameterRule getParamOrStreamRule() {
        return this.paramRuleMap.get("zoho-paramorstream");
    }
    
    public boolean isDosCookieRequired() {
        return this.doscookie;
    }
    
    public List<String> getSecretParameters() {
        return this.secretParamNames;
    }
    
    public List<ParameterRule> getSecretParamNameRegexRules() {
        return this.secretParamNameRegexRules;
    }
    
    public Map<String, ParameterRule> getPartialMaskingParamRules() {
        return this.partialMaskingParamRuleMap;
    }
    
    public ParameterRule getPartialMaskingParamRule(final String paramName) {
        return (this.partialMaskingParamRuleMap != null) ? this.partialMaskingParamRuleMap.get(paramName) : null;
    }
    
    public ParameterRule getPartialMaskingRequestHeaderRule(final String headerName) {
        return (this.partialMaskingReqHeaderRuleMap != null) ? this.partialMaskingReqHeaderRuleMap.get(headerName) : null;
    }
    
    public Map<String, ParameterRule> getPartialMaskingRequestHeaderRules() {
        return this.partialMaskingReqHeaderRuleMap;
    }
    
    public List<String> getSecretRequestHeaders() {
        return this.secretRequestHeaders;
    }
    
    String getJSONImpurity() {
        return this.jsonImpurity;
    }
    
    public long getMaxFileSize() {
        return this.maxFileSize;
    }
    
    public boolean loginThrowError() {
        return this.loginThrowError;
    }
    
    public void setLoginThrowError(final boolean value) {
        this.loginThrowError = value;
    }
    
    public boolean throwAllErrors() {
        return this.throwAllErrors;
    }
    
    public void setThrowAllErrors(final boolean throwAllErrors) {
        this.throwAllErrors = throwAllErrors;
    }
    
    public boolean getURLXSSValidation() {
        return this.urlXSSValidation;
    }
    
    public void setURLXSSValidation(final boolean urlXSSValidation) {
        this.urlXSSValidation = urlXSSValidation;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setPath(final String path) {
        this.path = path;
    }
    
    public void setMethod(final String method) {
        this.requestMethod = method;
    }
    
    public String getMethod() {
        return this.requestMethod;
    }
    
    public String getOperationParam() {
        return this.operationParam;
    }
    
    public void setOperationParam(final String operationParam) {
        this.operationParam = operationParam;
    }
    
    public String getOperationValue() {
        return this.operationValue.equals("ZSEC_DEFAULT_OPERATION_VALUE") ? null : this.operationValue;
    }
    
    public String getAuthentication() {
        return this.authentication;
    }
    
    public boolean isPublicURL() {
        return this.authentication == null || "".equals(this.authentication) || "public".equalsIgnoreCase(this.authentication);
    }
    
    public boolean isOptionalURL() {
        return "optional".equalsIgnoreCase(this.authentication);
    }
    
    public boolean isAuthenticationRequired() {
        return "required".equals(this.authentication);
    }
    
    public boolean isInternal() {
        return this.internal;
    }
    
    public String getSystemAuth() {
        return this.systemAuth;
    }
    
    public boolean isSystemAuth() {
        return "optional".equals(this.systemAuth) && SecurityUtil.isValidList(this.scopedServices);
    }
    
    public boolean isTrusted() {
        return this.trusted;
    }
    
    public void setScopedServices(final List<String> scopedServices) {
        this.scopedServices = scopedServices;
    }
    
    public List<String> getScopedServices() {
        return this.scopedServices;
    }
    
    public Object getAccountsAttribute() {
        return this.accountsAttributes;
    }
    
    public ActionRule setAccountsAttribute(final Object acctAttributes) {
        this.accountsAttributes = acctAttributes;
        return this;
    }
    
    public void setOperationValue(final String operationValue) {
        this.operationValue = operationValue;
    }
    
    public boolean isPutURL() {
        return "put".equalsIgnoreCase(this.requestMethod);
    }
    
    public boolean isPatchURL() {
        return "patch".equalsIgnoreCase(this.requestMethod);
    }
    
    public boolean isCSRFProtected() {
        return this.isCSRFProtected;
    }
    
    public void setCSRFProtected(final boolean isCSRFProtected) {
        this.isCSRFProtected = isCSRFProtected;
    }
    
    public void setCSRFCookie(final boolean set) {
        this.setCSRFCookie = set;
    }
    
    public boolean isSetCSRFCookie() {
        return this.setCSRFCookie;
    }
    
    public boolean isCSRFCheckDisabledForGetApi() {
        return this.disableGetApiCsrf;
    }
    
    public boolean isErrorPage() {
        return this.isErrorPage;
    }
    
    public String[] getRoles() {
        return this.roles;
    }
    
    public void setRoles(final String[] roles) {
        this.roles = roles;
    }
    
    public String[] getAppAccountMemberRoles() {
        return this.appAccountMemberRoles;
    }
    
    public void setAppAccountMemberRoles(final String[] roles) {
        this.appAccountMemberRoles = roles;
    }
    
    public boolean isAPI() {
        return this.isAPI;
    }
    
    public void setAPI(final boolean api) {
        this.isAPI = api;
    }
    
    public boolean isISC() {
        return this.isISC;
    }
    
    public void setISC(final boolean isc) {
        this.isISC = isc;
    }
    
    public String getISCScope() {
        return this.iscScope;
    }
    
    public String getAPIScope() {
        return this.apiScope;
    }
    
    public String getRunAsGroupIdParam() {
        return this.runAsGroupIdParam;
    }
    
    public String getRunAsGroupTypeParam() {
        return this.runAsGroupTypeParam;
    }
    
    public boolean isAuthorized(final String _userRoles) {
        if (this.roles == null || this.roles.length == 0) {
            return true;
        }
        if (_userRoles == null || "".equals(_userRoles)) {
            return false;
        }
        final String[] userRoles = _userRoles.split(";");
        if (userRoles.length == 0) {
            return false;
        }
        for (final String role : this.roles) {
            for (final String urole : userRoles) {
                if (role.equals(urole)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void setOAuthScopeList(final String oAuthScopeStr) {
        this.oAuthScopeList = SecurityUtil.getStringAsList(oAuthScopeStr, ",");
    }
    
    private void setOrgOAuthScopeList(final String orgOAuthScopeStr) {
        this.orgOAuthScopeList = SecurityUtil.getStringAsList(orgOAuthScopeStr, ",");
    }
    
    public List<String> getOAuthScopeList() {
        return this.oAuthScopeList;
    }
    
    public List<String> getOrgOAuthScopeList() {
        return this.orgOAuthScopeList;
    }
    
    public String getAuthType() {
        return (this.authTypeList != null) ? this.authTypeList.get(0) : null;
    }
    
    public List<String> getAuthTypeList() {
        return this.authTypeList;
    }
    
    public void enableCaptchaVerification() {
        this.captchaVerification = true;
    }
    
    public void disableCaptchaVerification() {
        this.captchaVerification = false;
    }
    
    public boolean isCaptchaVerificationEnabled() {
        return this.captchaVerification;
    }
    
    public String getUniquePath() {
        if (this.uniquePath == null) {
            this.uniquePath = this.appendUniqueComponents((this.prefix == null) ? this.path : (this.prefix + this.path));
        }
        return this.uniquePath;
    }
    
    private String appendUniqueComponents(String uniquePath) {
        if (SecurityUtil.isValid(this.requestMethod)) {
            uniquePath = this.requestMethod + " : " + uniquePath;
        }
        if (SecurityUtil.isValid(this.operationParam) && SecurityUtil.isValid(this.operationValue)) {
            uniquePath = uniquePath + "?" + this.operationParam + "=" + this.operationValue;
        }
        return uniquePath;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
    
    public ParameterRule getParameterRule(final String paramName) {
        final ParameterRule rule = this.paramRuleMap.get(paramName);
        if (rule != null) {
            return rule;
        }
        for (final ParameterRule pr : this.paramNameRegexRuleMap.values()) {
            if (SecurityUtil.matchPattern(paramName, pr.getParamName(), this.filterProps)) {
                return pr;
            }
        }
        return null;
    }
    
    public Map<String, ParameterRule> getParameterRules() {
        if (this.allParamRules == null) {
            (this.allParamRules = new LinkedHashMap<String, ParameterRule>(this.paramRuleMap)).putAll(this.paramNameRegexRuleMap);
        }
        return this.allParamRules;
    }
    
    public Map<String, ParameterRule> getParamRuleMap() {
        return this.paramRuleMap;
    }
    
    public Map<String, ParameterRule> getParamNameRegexRuleMap() {
        return this.paramNameRegexRuleMap;
    }
    
    public List<OrCriteriaRule> getOrCriteriaRules() {
        return this.orCriteriaRules;
    }
    
    void addParameterRule(final ParameterRule rule, final boolean throwErrorIfExist) {
        final Map<String, ParameterRule> parameterRuleMap = rule.isParamNameInRegex() ? this.paramNameRegexRuleMap : this.paramRuleMap;
        final String paramName = rule.paramOrStreamConfig ? "zoho-paramorstream" : rule.getParamName();
        if (!parameterRuleMap.containsKey(paramName)) {
            parameterRuleMap.put(paramName, rule);
            this.addSecretParamRule(rule);
        }
        else if (throwErrorIfExist) {
            throw new RuntimeException("Parameter rule '" + rule.getParamName() + "' for the URL '" + this.getPath() + "' already defined ");
        }
    }
    
    private void addSecretParamRule(final ParameterRule paramRule) {
        if (paramRule.isSecret()) {
            if (paramRule.isParamNameInRegex()) {
                this.secretParamNameRegexRules.add(paramRule);
            }
            else {
                this.secretParamNames.add(paramRule.getParamName());
            }
        }
        else if (paramRule.isMaskingRequiredPartially()) {
            (this.partialMaskingParamRuleMap = ((this.partialMaskingParamRuleMap == null) ? new HashMap<String, ParameterRule>() : this.partialMaskingParamRuleMap)).put(paramRule.getParamName(), paramRule);
        }
    }
    
    boolean containsImportContent() {
        return this.containsImportContent;
    }
    
    public long getMaxRequestSize() {
        return this.maxRequestSizeInKB;
    }
    
    public void setMaxRequestSize(final long maxRequestSizeInKB) {
        this.maxRequestSizeInKB = maxRequestSizeInKB;
    }
    
    public void setFileUploadMaxSize(final long fileUploadMaxSizeInKB) {
        if (fileUploadMaxSizeInKB < 0L) {
            throw new IAMSecurityException("INVALID_CONFIGURATION");
        }
        this.fileUploadMaxSize = fileUploadMaxSizeInKB;
    }
    
    public boolean isFileUploadMaxSizeExceeded(final long uploadedFilesSizeInKB) {
        return this.fileUploadMaxSize > -1L && uploadedFilesSizeInKB > this.fileUploadMaxSize;
    }
    
    public long getFileUploadMaxSizeInKB() {
        return this.fileUploadMaxSize;
    }
    
    private void addUploadFileRule(final UploadFileRule uploadFileRule) throws RuntimeException {
        final Map<String, UploadFileRule> ruleMap = uploadFileRule.isNameRegex() ? this.fileRegexRuleMap : this.fileRuleMap;
        if (ruleMap.containsKey(uploadFileRule.getFieldName())) {
            throw new RuntimeException("File rule '" + uploadFileRule.getFieldName() + "' for the URL '" + this.getPath() + "' is already defined ");
        }
        ruleMap.put(uploadFileRule.getFieldName(), uploadFileRule);
        this.allFileRuleMap.put(uploadFileRule.getFieldName(), uploadFileRule);
    }
    
    public UploadFileRule getUploadFileRule(final String fieldName) {
        UploadFileRule fileRule = this.fileRuleMap.get(fieldName);
        if (fileRule == null) {
            for (final UploadFileRule rule : this.fileRegexRuleMap.values()) {
                if (SecurityUtil.matchPattern(fieldName, rule.getFieldName(), this.filterProps)) {
                    fileRule = rule;
                    break;
                }
            }
        }
        return fileRule;
    }
    
    public Collection<UploadFileRule> getUploadFileRuleList() {
        return this.allFileRuleMap.values();
    }
    
    public void setCustomAttribute(final String name, final String value) {
        if (this.customAttributes == null) {
            this.customAttributes = new HashMap<String, String>();
        }
        this.customAttributes.put(name, value);
    }
    
    public String getCustomAttribute(final String name) {
        if (this.customAttributes != null) {
            return this.customAttributes.get(name);
        }
        return null;
    }
    
    public String getActionForward() {
        return this.actionForward;
    }
    
    public void setActionForward(final String actionForward) {
        this.actionForward = actionForward;
    }
    
    public List<String> getParamGroupList() {
        return this.paramGroupList;
    }
    
    public void setExtraParameterRule(final ParameterRule rule) {
        this.extraParameterRule = rule;
    }
    
    public ParameterRule getExtraParameterRule() {
        return this.extraParameterRule;
    }
    
    public boolean isCORSUrl() {
        return this.corsConfigType != CORSConfigType.NONE || this.filterProps.corsConfigType != CORSConfigType.NONE;
    }
    
    public boolean isDisableExtraparam() {
        return this.isDisableExtraparam;
    }
    
    public void setDisableExtraparam(final boolean isDisableExtraparam) {
        this.isDisableExtraparam = isDisableExtraparam;
    }
    
    public boolean isPatternURL() {
        return this.isPatternURL;
    }
    
    private void initThrottlesRule(final Element urlEle) {
        this.dynamicThrottles = "true".equals(getURLAttribute(urlEle, "dynamic-throttles"));
        final List<Element> throttlesElements = RuleSetParser.getChildNodesByTagName(urlEle, "throttles");
        final boolean hasOldThrottleConfiguration = this.hasOldThrottleConfiguration(urlEle);
        this.hasOldThrottleConfiguration = hasOldThrottleConfiguration;
        if (!hasOldThrottleConfiguration) {
            final boolean isDisabledServiceURLThrottles = this.filterProps != null && this.filterProps.isDisabledServiceURLThrottles();
            ThrottlesRule throttlesRule = null;
            for (final Element throttlesEle : throttlesElements) {
                try {
                    if (isDisabledServiceURLThrottles && ThrottlesRule.Scopes.SERVICE.name().equals(throttlesEle.getAttribute("scope"))) {
                        if (!this.filterProps.isConvertServiceUrlRollingThrottlesIntoAppserver() || !ThrottlesRule.Windows.ROLLING.name().equals(throttlesEle.getAttribute("window"))) {
                            continue;
                        }
                        throttlesEle.setAttribute("scope", ThrottlesRule.Scopes.APPSERVER.name());
                    }
                    throttlesRule = new ThrottlesRule(throttlesEle);
                }
                catch (final RuntimeException e) {
                    ActionRule.logger.log(Level.SEVERE, "ErrorMsg: \"{0}\", URL path: \"{1}\", method: \"{2}\", operation-param: \"{3}\", operation-value: \"{4}\".", new Object[] { e.getMessage(), this.path, this.requestMethod, this.operationParam, this.operationValue });
                    throw e;
                }
                if (throttlesRule.getWindow() == ThrottlesRule.Windows.ROLLING && !this.skipHipDigestParamFromExtraParamValidation && isSkipHipDigestParamFromExtraParamValidation(throttlesRule)) {
                    this.skipHipDigestParamFromExtraParamValidation = true;
                }
                if (this.filterProps != null) {
                    this.filterProps.addRequiredCachePoolNames(throttlesRule);
                }
                this.addThrottlesRule(throttlesRule);
            }
            return;
        }
        if (this.filterProps.isDisabledOldThrottleConfig()) {
            final String errorMsg = "INVALID_THROTTLES_CONFIGURATION:: Error msg: \"Old throttle configuration is disabled. URL path: \"" + this.path + "\", method: \"" + this.requestMethod + "\", operation-param: \"" + this.operationParam + "\", operation-value: \"" + this.operationValue + "\", Reference: \"https://intranet.wiki.zoho.com/security/URL-Throttle.html#How_to_change_old_throttle_config_to_new_throttle_configuration\".";
            throw new RuntimeException(errorMsg);
        }
        this.initOldThrottleConfiguration(urlEle);
        if (isSkipHipDigestParamFromExtraParamValidation(this.throttlesRuleMap.get(ThrottlesRule.Windows.ROLLING).get(0))) {
            this.skipHipDigestParamFromExtraParamValidation = true;
        }
        if (SecurityUtil.isValidList(throttlesElements)) {
            final String errorMsg = "INVALID_THROTTLES_CONFIGURATION:: Error msg: \"Same URL doesn't have both new and old throttle configuration. Team should migrate to the new throttles configuration\",  URL path: \"" + this.path + "\", method: \"" + this.requestMethod + "\", operation-param: \"" + this.operationParam + "\", operation-value: \"" + this.operationValue + "\", Reference: \"https://intranet.wiki.zoho.com/security/URL-Throttle.html#How_to_change_old_throttle_config_to_new_throttle_configuration\".";
            throw new RuntimeException(errorMsg);
        }
    }
    
    private boolean hasOldThrottleConfiguration(final Element urlEle) {
        return SecurityUtil.isValid(getURLAttribute(urlEle, "duration")) || RuleSetParser.getChildNodesByTagName(urlEle, "throttle").size() > 0;
    }
    
    private void initOldThrottleConfiguration(final Element urlEle) {
        final ThrottlesRule throttlesRule = new ThrottlesRule(ThrottlesRule.Windows.ROLLING, ThrottlesRule.Scopes.APPSERVER, "true".equals(getURLAttribute(urlEle, "user")) ? "url.path+user.zuid" : "url.path+user.remoteip");
        ThrottleRule throttle = null;
        try {
            if (SecurityUtil.isValid(getURLAttribute(urlEle, "duration"))) {
                throttle = new RollingWindowThrottleRule(getURLAttribute(urlEle, "duration"), getURLAttribute(urlEle, "threshold"), getURLAttribute(urlEle, "lock-period"), "true".equals(getURLAttribute(urlEle, "hip")), getURLAttribute(urlEle, "watch-time"), getURLAttribute(urlEle, "violation-limit"), getURLAttribute(urlEle, "lock-factor"));
                throttlesRule.addThrottle(throttle);
            }
            final List<Element> throttleElements = RuleSetParser.getChildNodesByTagName(urlEle, "throttle");
            for (final Element throttleEle : throttleElements) {
                throttle = ThrottleRule.createThrottleRule(throttleEle, ThrottlesRule.Windows.ROLLING);
                throttlesRule.addThrottle(throttle);
            }
            this.addThrottlesRule(throttlesRule);
        }
        catch (final RuntimeException e) {
            ActionRule.logger.log(Level.SEVERE, "ErrorMsg: \"{0}\", URL path: \"{1}\", method: \"{2}\", operation-param: \"{3}\", operation-value: \"{4}\".", new Object[] { e.getMessage(), this.path, this.requestMethod, this.operationParam, this.operationValue });
            throw e;
        }
    }
    
    static boolean isSkipHipDigestParamFromExtraParamValidation(final ThrottlesRule rolllingThrottlesRule) {
        if (rolllingThrottlesRule != null) {
            for (final ThrottleRule throttle : rolllingThrottlesRule.getThrottleRuleMap().values()) {
                if (((RollingWindowThrottleRule)throttle).getLockType() == ThrottleRule.LockType.HIP) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void addThrottlesRule(final ThrottlesRule throttlesRule) {
        if (throttlesRule == null) {
            return;
        }
        if (this.throttlesRuleMap == null) {
            this.throttlesRuleMap = new HashMap<ThrottlesRule.Windows, List<ThrottlesRule>>();
        }
        if (!this.dbCacheForServiceScopeThrottles && throttlesRule.getScope() == ThrottlesRule.Scopes.SERVICE) {
            this.dbCacheForServiceScopeThrottles = true;
        }
        List<ThrottlesRule> throttlesList = this.throttlesRuleMap.get(throttlesRule.getWindow());
        if (throttlesList == null) {
            throttlesList = new ArrayList<ThrottlesRule>();
            this.throttlesRuleMap.put(throttlesRule.getWindow(), throttlesList);
        }
        throttlesList.add(throttlesRule);
    }
    
    public Map<ThrottlesRule.Windows, List<ThrottlesRule>> getThrottlesRuleMap() {
        return this.throttlesRuleMap;
    }
    
    public List<ThrottlesRule> getThrottlesRuleByWindow(final ThrottlesRule.Windows window) {
        if (this.throttlesRuleMap != null) {
            return this.throttlesRuleMap.get(window);
        }
        return null;
    }
    
    public boolean isThrottlingEnabled() {
        return this.throttlesRuleMap != null || this.dynamicThrottles;
    }
    
    public void enableDynamicThrottles() {
        this.dynamicThrottles = true;
    }
    
    public void disableDynamicThrottles() {
        this.dynamicThrottles = false;
    }
    
    public boolean isDynamicThrottlesEnabled() {
        return this.dynamicThrottles;
    }
    
    public String getModule() {
        return this.module;
    }
    
    public String getSubModule() {
        return this.sub_module;
    }
    
    public String getOperation() {
        return this.operation;
    }
    
    private void initURLBlacklistRule(final Element element) {
        final Element urlBlacklistElem = RuleSetParser.getFirstChildNodeByTagName(element, RuleSetParser.TagName.URL_BLACKLIST.getValue());
        if (urlBlacklistElem != null) {
            this.blacklistedUrlRules = new ArrayList<BlacklistRule>();
            final List<Element> blPaths = RuleSetParser.getChildNodesByTagName(urlBlacklistElem, "path");
            for (final Element blPath : blPaths) {
                final BlacklistRule UrlBlacklistRule = new BlacklistRule(blPath.getAttribute("value"), blPath.getAttribute("operator"));
                this.blacklistedUrlRules.add(UrlBlacklistRule);
            }
        }
    }
    
    public List<BlacklistRule> getUrlBlacklistRules() {
        return this.blacklistedUrlRules;
    }
    
    private void initParameterRuleMap(final Element element) {
        final List<Element> paramElemList = RuleSetParser.getChildNodesByTagName(element, RuleSetParser.TagName.PARAM.getValue());
        if (paramElemList.size() > 0) {
            final List<ParameterRule> deltaParamRules = (paramElemList.size() > 0) ? new ArrayList<ParameterRule>() : null;
            for (final Element paramEle : paramElemList) {
                final String paramName = paramEle.getAttribute("name");
                if (!SecurityUtil.isValid(paramName)) {
                    throw new RuntimeException("Parameter rule with empty param name not allowed for the URL '" + this.getPath() + "'\n");
                }
                final ParameterRule paramRule = new ParameterRule(paramEle);
                this.addParamRule(paramRule, deltaParamRules);
            }
            this.addDeltaParamRule(deltaParamRules);
        }
        final List<Element> paramsElemList = RuleSetParser.getChildNodesByTagName(element, "params");
        for (final Element paramsElem : paramsElemList) {
            final List<Element> orCriteriaElemList = RuleSetParser.getChildNodesByTagName(paramsElem, "or-criteria");
            if (orCriteriaElemList.size() > 0) {
                this.orCriteriaRules = new ArrayList<OrCriteriaRule>(orCriteriaElemList.size());
                for (final Element orCriteriaElem : orCriteriaElemList) {
                    OrCriteriaRule orCriteriaRule = null;
                    try {
                        orCriteriaRule = new OrCriteriaRule(orCriteriaElem, RuleSetParser.TagName.PARAM.getValue());
                    }
                    catch (final RuntimeException e) {
                        ActionRule.logger.log(Level.SEVERE, "Error Msg : {0}, URL path : {1}, Method : {2}, Operation-param : {3}, Operation-value : {4}", new Object[] { e.getMessage(), this.path, this.requestMethod, this.operationParam, this.operationValue });
                        throw new IAMSecurityException("INVALID_CONFIGURATION");
                    }
                    this.orCriteriaRules.add(orCriteriaRule);
                    for (final ParameterRule paramRule2 : orCriteriaRule.getParameterRules()) {
                        this.addParameterRule(paramRule2, true);
                    }
                }
            }
            final List<Element> paramElems = RuleSetParser.getChildNodesByTagName(paramsElem, RuleSetParser.TagName.PARAM.getValue());
            for (final Element paramElem : paramElems) {
                this.addParameterRule(new ParameterRule(paramElem), true);
            }
        }
        final List<Element> nodeList = RuleSetParser.getChildNodesByTagName(element, RuleSetParser.TagName.PARAM_GROUP.getValue());
        if (nodeList.size() > 0) {
            for (int i = 0; i < nodeList.size(); ++i) {
                final Node paramGroupNode = nodeList.get(i);
                if (paramGroupNode.getNodeType() == 1) {
                    final Element paramGroupElement = (Element)paramGroupNode;
                    final String paramGroupName = paramGroupElement.getAttribute("name");
                    if (!SecurityUtil.isValid(paramGroupName)) {
                        throw new RuntimeException("Empty param-group name not allowed for the URL '" + this.getPath() + "'\n");
                    }
                    if (this.paramGroupList.contains(paramGroupName)) {
                        throw new RuntimeException("Param group name '" + paramGroupName + "' for the URL '" + this.getPath() + "' is already defined");
                    }
                    this.paramGroupList.add(paramGroupName);
                }
            }
        }
    }
    
    public void addParamRule(final ParameterRule paramRule, final List<ParameterRule> deltaParamRules) {
        if (paramRule.isDeltaContent()) {
            deltaParamRules.add(paramRule);
        }
        else {
            this.addParameterRule(paramRule, true);
        }
    }
    
    public void addDeltaParamRule(final List<ParameterRule> deltaParamRules) {
        if (deltaParamRules != null && deltaParamRules.size() > 0) {
            for (final ParameterRule deltaParamRule : deltaParamRules) {
                this.addParameterRule(deltaParamRule, true);
            }
        }
    }
    
    private void initInputStreamRuleMap(final Element element) {
        final List<Element> list = RuleSetParser.getChildNodesByTagName(element, "inputstream");
        if (list.size() == 0) {
            return;
        }
        if (list.size() > 1) {
            throw new RuntimeException("More than one inputstream configuration is not allowed for the URL : " + this.path);
        }
        if (list.size() == 1 && list.get(0).getNodeType() == 1) {
            final Element paramEle = list.get(0);
            paramEle.setAttribute("name", "zoho-inputstream");
            final ParameterRule paramRule = new ParameterRule(paramEle);
            final String validationMode = paramEle.getAttribute("validation-mode");
            if (SecurityFilterProperties.InputStreamValidationMode.LOG.getMode().equals(validationMode)) {
                paramRule.streamContentValidationMode = SecurityFilterProperties.InputStreamValidationMode.LOG;
            }
            this.addParameterRule(paramRule, true);
        }
    }
    
    private void initParamOrStreamRule(final Element element) {
        final List<Element> list = RuleSetParser.getChildNodesByTagName(element, RuleSetParser.TagName.PARAM_OR_STREAM.getValue());
        if (list.size() == 0) {
            return;
        }
        if (list.size() > 1) {
            throw new RuntimeException("More than one <paramorstream> configuration is not allowed for the URL : " + this.path);
        }
        if (this.hasInputStream()) {
            throw new RuntimeException("Invalid inputstream configuration :: Either <inputstream> or <paramorstream> configuration should be used for the URL : " + this.path);
        }
        final Element ele = list.get(0);
        final ParameterRule paramRule = new ParameterRule(ele);
        this.addParameterRule(paramRule, paramRule.paramOrStreamConfig = true);
    }
    
    private void initProxyPolicies(final Element element) {
        final Element proxyPolicyEle = RuleSetParser.getFirstChildNodeByTagName(element, RuleSetParser.TagName.PROXY_POLICY.getValue());
        if (proxyPolicyEle != null) {
            final String allowedServices = proxyPolicyEle.getAttribute("allowed-services");
            if (SecurityUtil.isValid(allowedServices)) {
                this.allowedServicesViaProxy = SecurityUtil.getStringAsList(allowedServices, ",");
            }
        }
    }
    
    private void initExtraParamRule(final Element element) {
        final List<Element> nodeList = RuleSetParser.getChildNodesByTagName(element, "extraparam");
        if (nodeList.size() == 0) {
            return;
        }
        final int nodeListLength = nodeList.size();
        if (nodeListLength == 1) {
            final Node node = nodeList.get(0);
            if (node.getNodeType() == 1) {
                final Element extraParamElement = (Element)node;
                this.extraParameterRule = new ParameterRule(extraParamElement);
            }
        }
        else if (nodeListLength > 1) {
            throw new RuntimeException("More than one extra param configuration found");
        }
    }
    
    private void initUploadFileRuleMap(final Element element) {
        final List<Element> list = RuleSetParser.getChildNodesByTagName(element, "file");
        if (list.size() == 0) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).getNodeType() == 1) {
                final Element paramEle = list.get(i);
                if (!SecurityUtil.isValid(paramEle.getAttribute("name"))) {
                    throw new RuntimeException("File rule with empty file name not allowed for the URL '" + this.getPath() + "'\n");
                }
                final UploadFileRule rule = new UploadFileRule(paramEle);
                this.addUploadFileRule(rule);
            }
        }
    }
    
    private void initResponseRule(final Element element) {
        final List<Element> list = RuleSetParser.getChildNodesByTagName(element, "response");
        if (list != null) {
            if (list.size() == 1) {
                final Element responseElement = list.get(0);
                this.responseRule = new ResponseRule(responseElement);
            }
            else if (list.size() != 0) {
                throw new RuntimeException("More than one response rule is not allowed");
            }
        }
    }
    
    private void initResponseHeaderRule(final Element element) {
        final List<Element> responseNodeList = RuleSetParser.getChildNodesByTagName(element, "response");
        if (responseNodeList.size() > 0 && responseNodeList.get(0).getNodeType() == 1) {
            final Element responseElement = responseNodeList.get(0);
            if ("true".equalsIgnoreCase(responseElement.getAttribute("dynamic-cors-headers"))) {
                this.corsConfigType = CORSConfigType.DYNAMIC;
            }
            this.disableSafeHeaders = this.initExcludeHeaders("disable-safe-headers", responseElement);
            this.disableHeaders = this.initExcludeHeaders("disable-headers", responseElement);
            this.disableCacheHeaders = this.initExcludeHeaders("disable-cache-headers", responseElement);
            if (this.disableCacheHeaders.size() > 0 && this.isAuthenticationRequired()) {
                ActionRule.logger.log(Level.SEVERE, "Disabling Cache control related headers not allowed for authentication required url : {0}", this.path);
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            final Element headersElement = RuleSetParser.getFirstChildNodeByTagName(responseElement, "headers");
            if (headersElement != null) {
                final List<Element> headerList = RuleSetParser.getChildNodesByTagName(headersElement, "header");
                if (headerList.size() > 0) {
                    for (int i = 0; i < headerList.size(); ++i) {
                        if (headerList.get(i).getNodeType() == 1) {
                            final Element headerElement = headerList.get(i);
                            if (!SecurityUtil.isValid(headerElement.getAttribute("name"))) {
                                throw new RuntimeException("ResponeHeaderRule with empty responseheader name not allowed for the URL '" + this.getPath() + "'\n");
                            }
                            final String headerName = headerElement.getAttribute("name").trim();
                            final String headerValue = headerElement.getAttribute("value").trim();
                            if (headerName.equals("Access-Control-Allow-Origin")) {
                                if (headerValue.equals("*") && !this.isPublicURL()) {
                                    ActionRule.logger.log(Level.SEVERE, "authenticated URLs are not allowed to set \"Access-Control-Allow-Origin\"=\"*\" , try to opt other options to validate the Origin ");
                                    throw new IAMSecurityException("INVALID_CONFIGURATION");
                                }
                                this.corsConfigType = CORSConfigType.STATIC;
                            }
                            final ResponseHeaderRule headerRule = new ResponseHeaderRule(headerElement);
                            this.addResponseHeaderRule(headerRule);
                        }
                    }
                }
            }
        }
    }
    
    private void initRequestHeaderRule(final Element element) {
        final Element requestElement = RuleSetParser.getFirstChildNodeByTagName(element, "request");
        if (requestElement != null) {
            this.initHeadersRule(requestElement);
        }
    }
    
    void initHeadersRule(final Element element) {
        final Element headersElement = RuleSetParser.getFirstChildNodeByTagName(element, "headers");
        if (headersElement != null) {
            final List<Element> headerList = RuleSetParser.getChildNodesByTagName(headersElement, "header");
            for (final Element headerElement : headerList) {
                final String headerName = headerElement.getAttribute("name").trim();
                if (!SecurityUtil.isValid(headerName)) {
                    throw new RuntimeException("REQUEST-HEADER : <header> with empty(\" \") or \"null\"  as a requestHeader name not allowed \n");
                }
                if ("cookie".equalsIgnoreCase(headerName) || "user-agent".equalsIgnoreCase(headerName)) {
                    throw new RuntimeException("REQUEST-HEADER : \"cookie\" or \"user-agent\" headers shouldn't be defined under <header> section , they must be defined under specific tags of <cookies> or <user-agent> ");
                }
                final HeaderRule headerRule = new GeneralRequestHeaderRule(headerElement);
                if (headerRule.getHeaderRule().isParamNameInRegex()) {
                    this.requestHeaderMapWithRegexName.put(headerName, headerRule);
                }
                else {
                    this.requestHeaderMapWithStrictName.put(headerName, headerRule);
                    if (headerRule.getHeaderRule().getMinOccurrences() == 1) {
                        this.mandatoryRequestHeaders.add(headerName);
                    }
                }
                this.addSecretHeaderRule(headerRule);
            }
            final Element cookiesElement = RuleSetParser.getFirstChildNodeByTagName(headersElement, "cookies");
            if (cookiesElement != null) {
                this.setCookieRule(new CookieRequestHeaderRule(cookiesElement));
            }
            final Element userAgentsElement = RuleSetParser.getFirstChildNodeByTagName(headersElement, "user-agent");
            if (userAgentsElement != null) {
                this.setUserAgentRule(new UserAgentRequestHeaderRule(userAgentsElement));
            }
        }
    }
    
    private List<String> initExcludeHeaders(final String expression, final Element element) {
        final List<String> headers = new ArrayList<String>();
        final String disableheaders = element.getAttribute(expression);
        if (SecurityUtil.isValid(disableheaders)) {
            final String[] disableHeadersArray = disableheaders.split(",");
            if (disableheaders.contains("all") && disableHeadersArray.length > 1) {
                throw new RuntimeException("disable-safe-headers=\"all\" or disable-headers=\"all\" or disable-cache-headers=\"all\" with comma separated values not allowed in response header rule  \n");
            }
            for (final String header : disableHeadersArray) {
                headers.add(header.trim());
            }
        }
        return headers;
    }
    
    void preRequestHeadersCheck(final HttpServletRequest request, final SecurityFilterProperties securityFilterConfig) {
        if (request.getContentLength() != -1) {
            final boolean isLearningMode = SecurityFilterProperties.RequestHeaderValidationMode.LEARNING == securityFilterConfig.getReqHeaderValidationMode();
            final boolean isEnforcementMode = SecurityFilterProperties.RequestHeaderValidationMode.ENFORCEMENT == securityFilterConfig.getReqHeaderValidationMode();
            if (isEnforcementMode || isLearningMode) {
                HeaderRule contentLengthHeaderRule = this.getRequestHeaderRuleWithStrictName("content-length");
                if (contentLengthHeaderRule == null) {
                    contentLengthHeaderRule = securityFilterConfig.getDefaultRequestHeaderRuleWithStrictName("content-length");
                }
                if (contentLengthHeaderRule != null) {
                    final ParameterRule paramRule = contentLengthHeaderRule.getHeaderRule();
                    if (paramRule != null) {
                        try {
                            paramRule.checkForRange(request, "content-length", Integer.toString(request.getContentLength()));
                        }
                        catch (final IAMSecurityException ex) {
                            ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER VALIDATION : validation failed for RequestHeader Name: {0}  :::: for URI {1} REASON: {2} PARAMRULE: {3} Value : {4} ", new Object[] { "content-length", request.getRequestURI(), ex.getMessage(), ex.getParameterRule(), request.getContentLength() });
                            throw new IAMSecurityException("REQUEST_SIZE_MORE_THAN_ALLOWED_SIZE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), "content-length");
                        }
                    }
                }
            }
        }
    }
    
    public boolean hasDynamicParams() {
        return this.dynamicParams;
    }
    
    public boolean getIPBlockCheck() {
        return this.ipbl;
    }
    
    public void setIPBlockCheck(final boolean ipbl) {
        this.ipbl = ipbl;
    }
    
    public final boolean validateCSRFToken(final SecurityRequestWrapper request, final HttpServletResponse response) {
        if (!this.isCSRFProtected()) {
            this.checkForCSRFQueryParam(request, this.filterProps.isAllowedCSRFParamInQS());
            if (this.isAPI() && this.isCSRFCheckDisabledForGetApi() && "get".equalsIgnoreCase(request.getMethod())) {
                return true;
            }
        }
        if (this.isValidCSRFToken(request, response)) {
            SecurityUtil.checkCSRFSamesiteStrictTmpCookie(request, response);
            return true;
        }
        return false;
    }
    
    private void checkForCSRFQueryParam(final SecurityRequestWrapper request, final boolean isAllowedCSRFParamInQS) {
        final String queryString = request.getQueryStringForValidation();
        if (queryString != null) {
            final String queryParam = this.filterProps.getCSRFParamName() + "=" + request.getParameterForValidation(this.filterProps.getCSRFParamName());
            if (queryString.indexOf(queryParam) != -1 && !isAllowedCSRFParamInQS) {
                ActionRule.logger.log(Level.SEVERE, "CSRF Parameter \"{0}\" should not be present in the URL querystring, it should be passed either as post parameter or request header", this.filterProps.getCSRFParamName());
                throw new IAMSecurityException("SENSITIVE_PARAM_NOT_ALLOWED_IN_QUERYSTRING", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), this.filterProps.getCSRFParamName(), SecurityUtil.SENSITIVE_PARAM_TYPE.CSRF, null);
            }
        }
    }
    
    private boolean isValidCSRFToken(final SecurityRequestWrapper request, final HttpServletResponse response) {
        final String cookieName = SecurityUtil.isValid(SecurityUtil.getIAMStatelessCookie((HttpServletRequest)request)) ? SecurityUtil.getStatelessAuthCSRFCookieName((HttpServletRequest)request) : SecurityUtil.getCSRFCookieName((HttpServletRequest)request);
        final String cookieValue = SecurityUtil.getCookie((HttpServletRequest)request, cookieName);
        final String paramValue = SecurityFilterProperties.getInstance((HttpServletRequest)request).getSecurityProvider().getCSRFParameter((HttpServletRequest)request, this);
        String oldCookieName = null;
        String oldCookieValue = null;
        if (cookieValue == null || cookieValue.trim().isEmpty()) {
            if (SecurityUtil.isBrowserCookiesDisabled()) {
                throw new IAMSecurityException("BROWSER_COOKIES_DISABLED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
            }
            ActionRule.logger.log(Level.SEVERE, "CSRF Hacking attempt. Cookie Value is absent. IP {0}, CSRF COOKIE {1}, PARAM {2}, URL {3}", new Object[] { request.getRemoteAddr(), cookieValue, MaskUtil.getCSRFMaskedValue(paramValue), request.getRequestURI() });
            return false;
        }
        else {
            if (paramValue == null || paramValue.trim().isEmpty()) {
                ActionRule.logger.log(Level.SEVERE, "CSRF Hacking attempt. Param Value is absent. IP {0}, CSRF COOKIE {1}, PARAMS {2}, URL {3}", new Object[] { request.getRemoteAddr(), MaskUtil.getCSRFMaskedValue(cookieValue), PRINT(request.getParameterMap().keySet()), request.getRequestURI() });
                return false;
            }
            if (SecurityFilterProperties.getInstance((HttpServletRequest)request).isCSRFMigrationEnabled()) {
                oldCookieName = SecurityUtil.getOldCSRFCookieName((HttpServletRequest)request);
                oldCookieValue = SecurityUtil.getCookie((HttpServletRequest)request, oldCookieName);
            }
            if (cookieValue.equals(paramValue)) {
                if (SecurityFilterProperties.getInstance((HttpServletRequest)request).isAuthCSRFDisabled()) {
                    return true;
                }
                final String ticket = SecurityUtil.getIAMAuthenticatedCookie((HttpServletRequest)request);
                if (!SecurityUtil.isValid(ticket)) {
                    return true;
                }
                if (SecurityUtil.isValidCurrentCSRFCookie((HttpServletRequest)request, response, paramValue, ticket)) {
                    if (SecurityFilterProperties.getInstance((HttpServletRequest)request).isCSRFMigrationEnabled()) {
                        if (paramValue.length() != 128) {
                            SecurityUtil.setCookie((HttpServletRequest)request, response, oldCookieName, cookieValue, "https".equalsIgnoreCase(request.getScheme()));
                            SecurityUtil.setAuthCSRFCookieValue((HttpServletRequest)request, response);
                        }
                        else if (paramValue.length() == 128 && SecurityUtil.isValid(oldCookieValue)) {
                            final Cookie ca = new Cookie(oldCookieName, "");
                            ca.setPath("/");
                            ca.setMaxAge(0);
                            response.addCookie(ca);
                        }
                    }
                    return true;
                }
                ActionRule.logger.log(Level.SEVERE, "CSRF Hacking attempt. INVALID USER TICKET");
                return false;
            }
            else {
                if (SecurityFilterProperties.getInstance((HttpServletRequest)request).isCSRFMigrationEnabled() && SecurityUtil.isValid(oldCookieValue) && oldCookieValue.equals(paramValue)) {
                    return true;
                }
                if (SecurityFilterProperties.getInstance((HttpServletRequest)request).isCSRFMigrationEnabled()) {
                    final String ticket = SecurityUtil.getIAMAuthenticatedCookie((HttpServletRequest)request);
                    if (ticket != null && paramValue.length() == 36 && cookieValue.length() == 128 && SecurityUtil.isValidCurrentCSRFCookie((HttpServletRequest)request, response, cookieValue, ticket)) {
                        ActionRule.logger.log(Level.SEVERE, "Auth CSRF Migation issue ::: csrf param : {0} , cookie : {1} ", new Object[] { MaskUtil.getCSRFMaskedValue(paramValue), MaskUtil.getCSRFMaskedValue(cookieValue) });
                        throw new IAMSecurityException("MIGRATION_CSRF_COOKIE_PARAM_MISMATCH", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
                    }
                }
                ActionRule.logger.log(Level.SEVERE, "CSRF Hacking attempt. Cookie is not same as Param Value. IP {0}, PARAM {1}, CSRF COOKIE {2}, URL {3}", new Object[] { request.getRemoteAddr(), MaskUtil.getCSRFMaskedValue(paramValue), MaskUtil.getCSRFMaskedValue(cookieValue), request.getRequestURI() });
                return false;
            }
        }
    }
    
    public void validate(final SecurityRequestWrapper securedRequest, final HttpServletResponse response) {
        if (this.filterProps.isReadOnlyMode() && !this.skipROCheck && this.isWriteOperation()) {
            throw new IAMSecurityException("WRITE_OPERATION_NOT_ALLOWED", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
        }
        if (this.urlXSSValidation && SecurityUtil.detectXSS(securedRequest.getRequestURI())) {
            ActionRule.logger.log(Level.SEVERE, "XSS found in URL \"{0}\"s", securedRequest.getRequestURI());
            throw new IAMSecurityException("XSS_DETECTED", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
        }
        if (this.ipbl && IPUtil.IS_BLOCKED(securedRequest.getRemoteAddr(), this.filterProps.getHttpIPBlackListDNS(), this.filterProps.getHttpIPWhiteListDNS())) {
            ActionRule.logger.log(Level.SEVERE, "Invoked from block listed IP  \"{0}\"", securedRequest.getRequestURI());
            throw new IAMSecurityException("BLOCK_LISTED_IP", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
        }
        this.authorizeProxyURL(securedRequest);
        if (this.isURLRestrictedViaImport && SecurityUtil.checkForSSRF((HttpServletRequest)securedRequest)) {
            ActionRule.logger.log(Level.SEVERE, "User tries to import internally protected url \"{0}\" from IP \"{1}\", Actual User IP :\"{2}\"", new Object[] { securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("X-Forwarded-For") });
            throw new IAMSecurityException("LAN_ACCESS_DENIED", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
        }
        if (this.isInternal() && !this.isPrivateIP(securedRequest)) {
            ActionRule.logger.log(Level.SEVERE, "Internal URL \"{0}\" accessed by external IP {1}", new Object[] { securedRequest.getRequestURI(), securedRequest.getRemoteAddr() });
            throw new IAMSecurityException("INTERNAL_IP_ACCESS_ONLY", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
        }
        if ((this.isTrusted() || (this.isInternalOrTrusted() && !this.isPrivateIP(securedRequest))) && !SecurityUtil.isTrustedIP((HttpServletRequest)securedRequest)) {
            ActionRule.logger.log(Level.SEVERE, "Trusted URL \"{0}\" accessed by external IP {1}", new Object[] { securedRequest.getRequestURI(), securedRequest.getRemoteAddr() });
            throw new IAMSecurityException("TRUSTED_IP_ACCESS_ONLY", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
        }
        if (this.filterProps.isAuthenticationProviderConfigured()) {
            this.filterProps.getAuthenticationProvider().handleClientPortalRequest((HttpServletRequest)securedRequest, response);
        }
        if (this.isCSRFProtected()) {
            this.checkForCSRFQueryParam(securedRequest, false);
            if (!this.validateCSRFToken(securedRequest, response)) {
                throw new IAMSecurityException("INVALID_CSRF_TOKEN", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
            }
        }
        else if (this.filterProps.isSetCSRFCookie() && this.isSetCSRFCookie()) {
            String ticket = null;
            if (!SecurityFilterProperties.getInstance((HttpServletRequest)securedRequest).isAuthCSRFDisabled() && SecurityUtil.isValid(ticket = SecurityUtil.getIAMAuthenticatedCookie((HttpServletRequest)securedRequest))) {
                this.validateAndSetAuthCSRFCookie(securedRequest, response, ticket);
            }
            else {
                final String cookie = SecurityUtil.getCSRFCookie((HttpServletRequest)securedRequest);
                if (!SecurityUtil.isValid(cookie)) {
                    SecurityUtil.setCSRFCookie((HttpServletRequest)securedRequest, response, null);
                }
            }
        }
    }
    
    private void authorizeProxyURL(final SecurityRequestWrapper securedRequest) {
        final String serverName = securedRequest.getHeader("ZSEC_PROXY_SERVER_NAME");
        final String hostName = securedRequest.getHeader("host");
        if ("true".equalsIgnoreCase(securedRequest.getHeader("ZSEC_PROXY_REQUEST")) || (SecurityUtil.isValid(serverName) && SecurityUtil.isValid(hostName))) {
            this.validateProxyRequestConfiguration(securedRequest);
            Label_0310: {
                if (SecurityUtil.isValidList(this.allowedServicesViaProxy)) {
                    try {
                        final SecurityFilterProperties filterProps = SecurityFilterProperties.getInstance((HttpServletRequest)securedRequest);
                        if (!filterProps.disableISCSignature() || !filterProps.isDevelopmentMode()) {
                            SecurityUtil.verifyISCSignature(securedRequest.getHeader("ZSEC_PROXY_SERVER_SIGNATURE"), this.allowedServicesViaProxy, securedRequest);
                        }
                        securedRequest.setProxyRequest(true);
                        break Label_0310;
                    }
                    catch (final IAMSecurityException ex) {
                        ActionRule.logger.log(Level.SEVERE, "ISC_SIGNATURE_NOT_PRESENT".equals(ex.getErrorCode()) ? ("Security Proxy hacking attempt : Proxy RequestHeader \"ZSEC_PROXY_REQUEST\" sent in the actual request - URL :  \"" + securedRequest.getRequestURI() + "\" from IP : \"" + securedRequest.getRemoteAddr() + "\"") : ("\"" + securedRequest.getHeader("ZSEC_PROXY_SERVER_NAME") + "\" - server not allowed to communicate with this service via Proxy"));
                        throw new IAMSecurityException("UNAUTHORIZED_PROXY_REQUEST", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
                    }
                }
                if (!securedRequest.getValidatedRequestPath().equals(this.filterProps.getLoginPage()) || !this.isPrivateIP(securedRequest)) {
                    ActionRule.logger.log(Level.SEVERE, "\"allowed-services\" not configured to allow url via security proxy for the request uri   \"{0}\"", securedRequest.getRequestURI());
                    throw new IAMSecurityException("PROXYPOLICY_NOT_CONFIGURED", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
                }
                securedRequest.setProxyRequest(true);
            }
            securedRequest.setProxyInfo(new ProxyInfo(securedRequest.getRemoteUserIPAddr(), securedRequest.getRemoteAddr(), SecurityUtil.getServiceNameFromISCSignature((HttpServletRequest)securedRequest)));
        }
    }
    
    private void validateProxyRequestConfiguration(final SecurityRequestWrapper securedRequest) {
        if (this.blockProxyRequest) {
            ActionRule.logger.log(Level.SEVERE, "\" internal / scoped-services\"  url cannot be invoked via security proxy ::  the request uri   \"{0}\"", securedRequest.getRequestURI());
            throw new IAMSecurityException("PROXY_ACCESS_DENIED_INCOMPATIBLE_CONFIGURATION", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
        }
    }
    
    private boolean isPrivateIP(final SecurityRequestWrapper securedRequest) {
        if (IPUtil.isPrivateIP(securedRequest.getRemoteAddr())) {
            securedRequest.setInternalURL(true);
            return true;
        }
        return false;
    }
    
    private void validateAndSetAuthCSRFCookie(final SecurityRequestWrapper request, final HttpServletResponse response, final String ticket) {
        final String csrfCookieName = request.isAuthenticatedViaStatelessCookie ? SecurityUtil.getStatelessAuthCSRFCookieName((HttpServletRequest)request) : SecurityUtil.getCSRFCookieName((HttpServletRequest)request);
        final String csrfCookieValue = SecurityUtil.getCookie((HttpServletRequest)request, csrfCookieName);
        if (SecurityUtil.isValid(csrfCookieValue)) {
            if (csrfCookieValue.length() != 128) {
                if (SecurityFilterProperties.getInstance((HttpServletRequest)request).isCSRFMigrationEnabled()) {
                    SecurityUtil.setCookie((HttpServletRequest)request, response, SecurityUtil.getOldCSRFCookieName((HttpServletRequest)request), csrfCookieValue, "https".equalsIgnoreCase(request.getScheme()));
                }
                SecurityUtil.setAuthCSRFCookieValue((HttpServletRequest)request, response);
            }
            else {
                final String ticketInMap = SecurityUtil.getTicketFromCSRFTokenMap(csrfCookieValue, request.getServerName());
                if (ticketInMap != null) {
                    if (!ticket.equals(ticketInMap)) {
                        SecurityUtil.setAuthCSRFCookieValue((HttpServletRequest)request, response);
                    }
                }
                else {
                    final String hash = SecurityUtil.generateAuthCSRFToken((HttpServletRequest)request, response);
                    if (SecurityUtil.isValid(hash)) {
                        if (!hash.equals(csrfCookieValue)) {
                            SecurityUtil.setAuthCSRFTokenToCookie((HttpServletRequest)request, response, hash);
                        }
                        SecurityUtil.addAuthCSRFTokenToMap(hash, ticket, request.getServerName());
                    }
                }
            }
        }
        else {
            SecurityUtil.setUnauthCSRFIfAuthCSRFCookieFails((HttpServletRequest)request, response);
        }
    }
    
    String validateConfiguration(final SecurityFilterProperties sfp) {
        if (this.isCSRFProtected()) {
            if (!this.isCSRFSupportedMethod() && !this.isAPI()) {
                return "CSRF parameter can't be passed otherthan POST/PUT/PATCH/DELETE methods in url : \n " + this.toString();
            }
            if (sfp.getCSRFCookieName() == null || "".equals(sfp.getCSRFCookieName())) {
                return "CSRF Cookie name is not configured :\n " + this.toString();
            }
            if (sfp.getCSRFParamName() == null || "".equals(sfp.getCSRFParamName())) {
                return "CSRF Parameter name is not configured :\n " + this.toString();
            }
        }
        if (this.getIPBlockCheck() && (this.filterProps.getHttpIPBlackListDNS() == null || this.filterProps.getHttpIPWhiteListDNS() == null)) {
            return "Http IP Black/White list dns server [http.ip.black/white.list.dns] property not configured";
        }
        if (this.isISC && !SecurityUtil.isValid(this.getISCScope())) {
            return "Invalid/empty iscscope configured";
        }
        if (this.internalOrTrusted && (this.scopedServices == null || this.scopedServices.size() == 0)) {
            return "Scoped services not configured for internal or trusted url :\n " + this.toString();
        }
        if (this.getRunAsGroupIdParam() != null && !SecurityUtil.isValid(this.getRunAsGroupTypeParam())) {
            return "Invalid/Empty runas-grouptype-param configured.";
        }
        final List<String> errorMessages = new ArrayList<String>();
        final Map<String, ParameterRule> paramRules = this.getParameterRules();
        final Collection<ParameterRule> c = paramRules.values();
        for (final ParameterRule pr : c) {
            final String result = pr.validateConfiguration(sfp);
            if (result != null) {
                errorMessages.add(result);
            }
        }
        final Collection<UploadFileRule> rules = this.getUploadFileRuleList();
        if (rules != null) {
            for (final UploadFileRule ufr : rules) {
                final String result2 = ufr.validateConfiguration(sfp);
                if (result2 != null) {
                    errorMessages.add(result2);
                }
            }
        }
        if (errorMessages.size() > 0) {
            return this.convertToString(errorMessages);
        }
        return null;
    }
    
    private boolean isCSRFSupportedMethod() {
        final String method = this.requestMethod.toLowerCase();
        return method.equals("post") || method.equals("put") || method.equals("patch") || method.equals("delete");
    }
    
    String convertToString(final List<String> messages) {
        final StringBuilder builder = new StringBuilder(this.toString());
        builder.append('\n');
        for (final String message : messages) {
            builder.append('\n');
            builder.append(message);
        }
        builder.append('\n');
        return builder.toString();
    }
    
    public void validateRequestHeaders(final SecurityRequestWrapper securedRequest, final SecurityFilterProperties securityFilterConfig) {
        int validatedHeaders = 0;
        int totalHeaders = 0;
        int validatedCookies = 0;
        int totalCookies = 0;
        final ExecutionTimer reqtimer = ExecutionTimer.startInstance();
        try {
            final Cookie[] cookies = securedRequest.getCookies();
            if (cookies != null) {
                totalCookies = cookies.length;
                validatedCookies = this.validateCookies(cookies, securityFilterConfig, securedRequest);
            }
            if (securedRequest.getUserAgent() != null && SecurityFilterProperties.RequestHeaderValidationMode.DISABLE != securityFilterConfig.getReqHeaderValidationMode()) {
                final UserAgentRequestHeaderRule defaultUserAgentRule = securityFilterConfig.getDefaultUserAgentRule();
                final Enumeration<String> headerValues = securedRequest.getHeaders("user-agent");
                if ((this.userAgentRule != null || defaultUserAgentRule != null) && headerValues.hasMoreElements()) {
                    this.validateUserAgent(securedRequest, this.userAgentRule, defaultUserAgentRule, headerValues.nextElement(), securityFilterConfig);
                    ++validatedHeaders;
                }
            }
            final Enumeration<String> headerNames = securedRequest.getHeaderNames();
            final List<String> validatedHeaderNames = new ArrayList<String>();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    boolean internalheader = false;
                    final String headerName = headerNames.nextElement();
                    HeaderRule headerRule = null;
                    ++totalHeaders;
                    try {
                        if (!"cookie".equals(headerName) && !"user-agent".equals(headerName)) {
                            if ((headerRule = securityFilterConfig.getInternalRequestHeaders().get(headerName)) != null) {
                                internalheader = true;
                            }
                            if (securityFilterConfig.getReqHeaderValidationMode() != SecurityFilterProperties.RequestHeaderValidationMode.DISABLE) {
                                if (headerRule == null) {
                                    headerRule = this.getRequestHeaderRuleWithStrictName(headerName);
                                }
                                if (headerRule == null) {
                                    headerRule = securityFilterConfig.getDefaultRequestHeaderRuleWithStrictName(headerName);
                                }
                                if (headerRule == null) {
                                    headerRule = this.getMatchedHeaderRule(this.getRequestHeaderMapWithRegexName().values(), headerName);
                                }
                                if (headerRule == null) {
                                    headerRule = this.getMatchedHeaderRule(securityFilterConfig.getDefaultRequestHeadersRuleWithRegexName().values(), headerName);
                                }
                                if (headerRule == null && !securityFilterConfig.isLearningMode()) {
                                    ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER VALIDATION : No Configuration found for HeaderName :::: {0} for URI {1}", new Object[] { headerName, securedRequest.getRequestURI() });
                                    if (securityFilterConfig.isEnforcementMode()) {
                                        throw new IAMSecurityException("REQUEST_HEADER_NOT_CONFIGURED");
                                    }
                                }
                            }
                            if (headerRule != null) {
                                this.validateRequestHeader(securedRequest, headerRule, headerName);
                                validatedHeaderNames.add(headerName);
                                ++validatedHeaders;
                            }
                        }
                        securedRequest.storeHeader(headerName, securedRequest.getHeader(headerName), headerRule);
                    }
                    catch (final IAMSecurityException e) {
                        ++validatedHeaders;
                        validatedHeaderNames.add(headerName);
                        if (logHeaderValue(headerName, e.getErrorCode(), securityFilterConfig)) {
                            final List<String> headerValues2 = (List<String>)Collections.list((Enumeration<Object>)securedRequest.getHeaders(headerName));
                            ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER VALIDATION : validation failed for RequestHeader Name: {0}, Value(s) : {1} :::: for URI {2} REASON: {3} PARAMRULE: {4} ", new Object[] { headerName, headerValues2, securedRequest.getRequestURI(), e.getMessage(), e.getParameterRule() });
                        }
                        else {
                            ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER VALIDATION : validation failed for RequestHeader Name: {0} :::: for URI {1} REASON: {2} PARAMRULE: {3} ", new Object[] { headerName, securedRequest.getRequestURI(), e.getMessage(), e.getParameterRule() });
                        }
                        if ((internalheader && securityFilterConfig.isThrowExForInternalValidation) || securityFilterConfig.isEnforcementMode() || securityFilterConfig.isLearningMode()) {
                            throw e;
                        }
                        continue;
                    }
                }
                if (SecurityFilterProperties.RequestHeaderValidationMode.DISABLE != securityFilterConfig.getReqHeaderValidationMode()) {
                    for (final String mandatoryHeader : this.mandatoryRequestHeaders) {
                        if (!validatedHeaderNames.contains(mandatoryHeader)) {
                            ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER VALIDATION : HEADER - \" {0} \" NOT FOUND for URI {1}", new Object[] { mandatoryHeader, securedRequest.getRequestURI() });
                            if (securityFilterConfig.isEnforcementMode() || securityFilterConfig.isLearningMode()) {
                                throw new IAMSecurityException("REQUEST_HEADER_NOT_FOUND");
                            }
                            continue;
                        }
                    }
                }
            }
        }
        finally {
            ZSEC_PERFORMANCE_ANOMALY.pushReqHeaderValidation(securedRequest.getRequestURI(), validatedHeaders + "/" + totalHeaders, validatedCookies + "/" + totalCookies, reqtimer);
        }
    }
    
    private static boolean logHeaderValue(final String headerName, final String errorCode, final SecurityFilterProperties filterConfig) {
        return "PATTERN_NOT_MATCHED".equals(errorCode) && ("x-zcsrf-token".equalsIgnoreCase(headerName) || filterConfig.getCSRFCookieName().equals(headerName));
    }
    
    private int validateCookies(final Cookie[] cookies, final SecurityFilterProperties securityFilterConfig, final SecurityRequestWrapper securedRequest) {
        int validatedCookies = 0;
        final List<String> valiateCookieNames = new ArrayList<String>();
        if (securityFilterConfig.getInternalCookieRule() != null || securityFilterConfig.getDefaultCookieRule() != null || this.cookieRule != null) {
            for (final Cookie cookie : cookies) {
                if (this.validateCookie(securedRequest, cookie, securityFilterConfig)) {
                    ++validatedCookies;
                    valiateCookieNames.add(cookie.getName());
                }
            }
            if (this.cookieRule != null) {
                for (final String cookieName : this.cookieRule.getMandatoryCookieNames()) {
                    if (!valiateCookieNames.contains(cookieName)) {
                        ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER : COOKIE VALIDATION : COOKIE - \" {0} \" NOT FOUND for URI {1}", new Object[] { cookieName, securedRequest.getRequestURI() });
                        throw new IAMSecurityException("COOKIE_NOT_FOUND");
                    }
                }
            }
        }
        return validatedCookies;
    }
    
    void validateRequestHeader(final SecurityRequestWrapper securedRequest, final HeaderRule headerRule, final String headerName) {
        final Enumeration<String> headerValues = securedRequest.getHeaders(headerName);
        while (headerValues.hasMoreElements()) {
            final String headerValue = headerValues.nextElement();
            headerRule.validate(securedRequest, headerValue);
        }
    }
    
    public boolean validateCookie(final SecurityRequestWrapper securedRequest, final Cookie cookie, final SecurityFilterProperties securityFilterConfig) {
        final CookieRequestHeaderRule defaultCookieRule = securityFilterConfig.getDefaultCookieRule();
        final CookieRequestHeaderRule internalCookieRule = securityFilterConfig.getInternalCookieRule();
        final String cookieName = cookie.getName();
        final String cookieValue = cookie.getValue();
        boolean internalCookie = false;
        ParameterRule cookieParamRule = null;
        try {
            if (internalCookieRule != null) {
                cookieParamRule = internalCookieRule.getCookieMapWithStrictName().get(cookieName);
                if (cookieParamRule != null || (cookieParamRule == null && (cookieParamRule = this.getMatchedRule(internalCookieRule.getCookieMapWithRegexName().values(), cookieName)) != null)) {
                    internalCookie = true;
                }
            }
            if (securityFilterConfig.getReqHeaderValidationMode() != SecurityFilterProperties.RequestHeaderValidationMode.DISABLE) {
                if (cookieParamRule == null && this.cookieRule != null) {
                    cookieParamRule = this.cookieRule.getCookieMapWithStrictName().get(cookieName);
                }
                if (cookieParamRule == null && defaultCookieRule != null) {
                    cookieParamRule = defaultCookieRule.getCookieMapWithStrictName().get(cookieName);
                }
                if (cookieParamRule == null && this.cookieRule != null) {
                    cookieParamRule = this.getMatchedRule(this.cookieRule.getCookieMapWithRegexName().values(), cookieName);
                }
                if (cookieParamRule == null && defaultCookieRule != null) {
                    cookieParamRule = this.getMatchedRule(defaultCookieRule.getCookieMapWithRegexName().values(), cookieName);
                }
                if (cookieParamRule == null && !securityFilterConfig.isLearningMode()) {
                    ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER : COOKIE VALIDATION : No Configuration found :::: {0} for URI {1}", new Object[] { cookieName, securedRequest.getRequestURI() });
                    if (securityFilterConfig.isEnforcementMode()) {
                        throw new IAMSecurityException("REQUEST_HEADER_NOT_CONFIGURED");
                    }
                }
            }
            if (cookieParamRule != null) {
                if (cookieParamRule.isEmptyValueAllowed() && cookieValue.trim().length() == 0) {
                    return true;
                }
                cookieParamRule.validateParamValue(securedRequest, cookieName, cookieValue, null);
                return true;
            }
        }
        catch (final IAMSecurityException e) {
            if (logHeaderValue(cookieName, e.getErrorCode(), securityFilterConfig)) {
                ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER :COOKIE VALIDATION : validation failed for Name: {0}, Value: {1}  :::: for URI {2}  Reason: {3} PARAMRULE:{4} ", new Object[] { cookieName, cookieValue, securedRequest.getRequestURI(), e.getMessage(), e.getParameterRule() });
            }
            else {
                ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER :COOKIE VALIDATION : validation failed for Name: {0}  :::: for URI {1}  Reason: {2} PARAMRULE:{3} ", new Object[] { cookieName, securedRequest.getRequestURI(), e.getMessage(), e.getParameterRule() });
            }
            if ((internalCookie && securityFilterConfig.isThrowExForInternalValidation) || securityFilterConfig.isEnforcementMode() || securityFilterConfig.isLearningMode()) {
                throw e;
            }
            return true;
        }
        return false;
    }
    
    private ParameterRule getMatchedRule(final Collection<ParameterRule> paramRules, final String cookieName) {
        for (final ParameterRule cookieWithRegexRule : paramRules) {
            if (SecurityUtil.matchPattern(cookieName, cookieWithRegexRule.getParamName(), this.filterProps)) {
                return cookieWithRegexRule;
            }
        }
        return null;
    }
    
    private HeaderRule getMatchedHeaderRule(final Collection<HeaderRule> headerRules, final String headerName) {
        for (final HeaderRule headerRule : headerRules) {
            if (SecurityUtil.matchPattern(headerName, headerRule.getHeaderName(), this.filterProps)) {
                return headerRule;
            }
        }
        return null;
    }
    
    public void validateUserAgent(final SecurityRequestWrapper securedRequest, final UserAgentRequestHeaderRule strictUserAgentRule, final UserAgentRequestHeaderRule defaultUserAgentRule, final String userAgentValue, final SecurityFilterProperties securityFilterConfig) {
        final UserAgent agent = securedRequest.getUserAgent();
        for (final UserAgentRequestHeaderRule.USERAGENTTAG tag : UserAgentRequestHeaderRule.USERAGENTTAG.values()) {
            UserAgentRequestHeaderRule userAgentRule = null;
            try {
                String tagValue = null;
                double tagMinorVersion = 0.0;
                double tagMajorVersion = 0.0;
                switch (tag) {
                    case BROWSER: {
                        tagValue = agent.getBrowserName();
                        tagMajorVersion = agent.getBrowserMajorVersion();
                        tagMinorVersion = agent.getBrowserMinorVersion();
                        break;
                    }
                    case OS: {
                        tagValue = agent.getOsName();
                        tagMajorVersion = agent.getOsMajorVersion();
                        tagMinorVersion = agent.getOsMinorVersion();
                        break;
                    }
                    case DEVICE: {
                        tagValue = agent.getDeviceName();
                        break;
                    }
                }
                if ("Other".equals(tagValue)) {
                    ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER VALIDATION : \"{0}\" :::: Name could not be detected \nOriginal user-agent \"{1}\" ", new Object[] { tag, userAgentValue });
                }
                else {
                    if (strictUserAgentRule != null && strictUserAgentRule.contains(tag.getName())) {
                        userAgentRule = strictUserAgentRule;
                    }
                    else if (defaultUserAgentRule != null && defaultUserAgentRule.contains(tag.getName())) {
                        userAgentRule = defaultUserAgentRule;
                    }
                    if (userAgentRule != null) {
                        if (tagMajorVersion > -1.0 && tagMinorVersion > -1.0) {
                            String deviceType = null;
                            if (UserAgentRequestHeaderRule.USERAGENTTAG.DEVICE != tag) {
                                userAgentRule.validateVersions(tag.getName(), tagValue, tagMajorVersion, tagMinorVersion);
                            }
                            else if (!"Other".equals(deviceType = agent.getDeviceType())) {
                                userAgentRule.validateDeviceType(securedRequest, deviceType);
                            }
                            else {
                                ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER VALIDATION : \"device\" :::: Device-family could not be detected \nOriginal user-agent \"{0}\" ", new Object[] { userAgentValue });
                            }
                        }
                        else {
                            ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER VALIDATION : \"{0}\" :::: Major/Minor versions could not be detected \nOriginal user-agent \"{1}\" ", new Object[] { tag, userAgentValue });
                        }
                        userAgentRule.validate(securedRequest, tag.getName(), tagValue);
                    }
                    else {
                        ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER VALIDATION : No Configuration found for UserAgent component :::: {0} for URI {1}", new Object[] { tag, securedRequest.getRequestURI() });
                        if (securityFilterConfig.isEnforcementMode()) {
                            throw new IAMSecurityException("REQUEST_HEADER_NOT_CONFIGURED");
                        }
                    }
                }
            }
            catch (final IAMSecurityException e) {
                ActionRule.logger.log(Level.SEVERE, " REQUEST HEADER VALIDATION : validation failed for UserAgent component: {0}  :::: for URI {1} \nOriginalUserAgentValue : {2} \nReason: {3} \nPARAMRULE:{4} ", new Object[] { tag, securedRequest.getRequestURI(), userAgentValue, e.getMessage(), e.getParameterRule() });
                if (securityFilterConfig.isEnforcementMode() || securityFilterConfig.isLearningMode()) {
                    throw e;
                }
            }
        }
    }
    
    @Override
    public String toString() {
        String str = "ActionRule ::  Path : \"" + this.getPath() + "\"";
        if (SecurityUtil.isValid(this.requestMethod)) {
            str = str + " method :\"" + this.requestMethod + "\"";
        }
        else if (this.operationParam != null && this.operationParam.length() > 0) {
            str = str + " and action : \"" + this.operationParam + "=" + this.operationValue + "\"";
        }
        if (this.operationType != null) {
            str = str + " operation-type : \"" + this.operationType + "\"";
        }
        return str + "\" isCSRFProtected : \"" + this.isCSRFProtected + "\" internal : \"" + this.internal + "\" trusted : \"" + this.trusted + "\" roles : \"" + PRINT(this.roles) + "\" dynamicParams : \"" + this.dynamicParams + "\" api : \"" + this.isAPI + "\" isc : \"" + this.isISC + "\" authentication : \"" + this.authentication + "\" throwAllErrors : \"" + this.throwAllErrors + "\" urlXSSValidation : \"" + this.urlXSSValidation + "\" ipBlockCheck : \"" + this.ipbl + "\" loginThrowError : \"" + this.loginThrowError + " \"\" iscScope : \"" + this.iscScope + "\" runAsGroupIdParam  : \"" + this.runAsGroupIdParam + "\" runAsGroupTypeParam : \"" + this.runAsGroupTypeParam + " \"isThrottlesConfigured : \"" + this.isThrottlingEnabled() + " \"dynamic-throttles : \"" + this.dynamicThrottles;
    }
    
    static String PRINT(final Map map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        String vv = "";
        for (final Map.Entry e : map.entrySet()) {
            vv = vv + "[" + e.getKey();
            if (e.getValue() instanceof String[]) {
                vv = vv + " = " + PRINT(e.getValue());
            }
            else {
                vv = vv + " = " + e.getValue();
            }
            vv += "]";
        }
        return " {" + vv + "} ";
    }
    
    static String PRINT(final String[] values) {
        String vv = "";
        if (values != null && values.length > 0) {
            for (final String v : values) {
                vv = vv + "," + v;
            }
            return vv.substring(1);
        }
        return vv;
    }
    
    static String PRINT(final Set<String> keySet) {
        String printStr = "";
        if (keySet.size() > 0) {
            for (final String key : keySet) {
                printStr = printStr + "," + key;
            }
            return "{" + printStr.substring(1) + "}";
        }
        return printStr;
    }
    
    public String getOperationType() {
        return this.operationType;
    }
    
    public boolean isWriteOperation() {
        return "write".equalsIgnoreCase(this.operationType) || "create".equalsIgnoreCase(this.operationType) || "update".equalsIgnoreCase(this.operationType) || "delete".equalsIgnoreCase(this.operationType);
    }
    
    public void addChildActionRule(final Element elem) {
        if (this.appFirewallActionRule == null) {
            this.appFirewallActionRule = this.filterProps.getRACProvider().getChildActionRule(elem, this);
        }
    }
    
    public ChildActionRule getChildActionRule() {
        return this.appFirewallActionRule;
    }
    
    public boolean isInternalOrTrusted() {
        return this.internalOrTrusted;
    }
    
    String getUrlUniquePath(String requestUri) {
        if ("dynamic".equals(this.urlType)) {
            requestUri = ((this.prefix == null) ? this.path : (this.prefix + this.path));
        }
        if (SecurityUtil.isValid(this.requestMethod) && SecurityUtil.isValid(this.operationParam) && SecurityUtil.isValid(this.operationValue)) {
            requestUri = requestUri + "?" + this.requestMethod + "&" + this.operationParam + "=" + this.operationValue;
        }
        else if (SecurityUtil.isValid(this.requestMethod)) {
            requestUri = requestUri + "?" + this.requestMethod;
        }
        return requestUri;
    }
    
    public Map<String, ResponseHeaderRule> getResponseHeaderRules() {
        return this.responseHeaderMap;
    }
    
    public ResponseHeaderRule getResponseHeaderRule(final String headerName) {
        return this.responseHeaderMap.get(headerName);
    }
    
    private void addResponseHeaderRule(final ResponseHeaderRule headerRule) {
        if (!this.responseHeaderMap.containsKey(headerRule.getHeaderName())) {
            this.responseHeaderMap.put(headerRule.getHeaderName(), headerRule);
            return;
        }
        throw new RuntimeException("Response Header rule '" + headerRule.getHeaderName() + "' for the URL '" + this.getPath() + "' already defined ");
    }
    
    public ResponseRule getResponseRule() {
        return this.responseRule;
    }
    
    Map<String, HeaderRule> getRequestHeaderMapWithRegexName() {
        return this.requestHeaderMapWithRegexName;
    }
    
    Map<String, HeaderRule> getRequestHeaderMapWithStrictName() {
        return this.requestHeaderMapWithStrictName;
    }
    
    public Map<String, HeaderRule> getRequestHeadersMapWithRegexNameForAnalysis() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends HeaderRule>)this.requestHeaderMapWithRegexName);
    }
    
    public Map<String, HeaderRule> getRequestHeadersMapWithStrictNameForAnalysis() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends HeaderRule>)this.requestHeaderMapWithStrictName);
    }
    
    HeaderRule getRequestHeaderRuleWithStrictName(final String headerName) {
        return this.requestHeaderMapWithStrictName.get(headerName);
    }
    
    HeaderRule getRequestHeaderRuleWithRegexName(final String headerName) {
        return this.requestHeaderMapWithRegexName.get(headerName);
    }
    
    public List<String> getDisabledCacheHeaders() {
        return this.disableCacheHeaders;
    }
    
    public List<String> getDisabledSafeHeaders() {
        return this.disableSafeHeaders;
    }
    
    public List<String> getDisabledHeaders() {
        return this.disableHeaders;
    }
    
    void initializeParentVariables(final ActionRule urlsActionRule) {
        if (urlsActionRule.prefix != null) {
            this.prefix = urlsActionRule.prefix;
        }
        for (final ParameterRule urlsParamRule : urlsActionRule.getParamRuleMap().values()) {
            this.addParameterRule(urlsParamRule, false);
        }
        for (final ParameterRule urlsParamNameRegexRule : urlsActionRule.getParamNameRegexRuleMap().values()) {
            this.addParameterRule(urlsParamNameRegexRule, false);
        }
        if (urlsActionRule.getOrCriteriaRules() != null) {
            if (this.orCriteriaRules == null) {
                this.orCriteriaRules = new ArrayList<OrCriteriaRule>();
            }
            this.orCriteriaRules.addAll(urlsActionRule.getOrCriteriaRules());
        }
        final Map<String, String> urlsCustomAttributes = urlsActionRule.customAttributes;
        if (urlsCustomAttributes != null) {
            if (this.customAttributes == null) {
                this.customAttributes = new HashMap<String, String>();
            }
            for (final Map.Entry<String, String> customAttr : urlsCustomAttributes.entrySet()) {
                final String customAttributeName = customAttr.getKey();
                if (!this.customAttributes.containsKey(customAttributeName)) {
                    this.customAttributes.put(customAttributeName, customAttr.getValue());
                }
            }
        }
        for (final UploadFileRule urlsUploadFileRule : urlsActionRule.getUploadFileRuleList()) {
            final Map<String, UploadFileRule> urlFileRuleMap = urlsUploadFileRule.isNameRegex() ? this.fileRegexRuleMap : this.fileRuleMap;
            if (urlFileRuleMap.containsKey(urlsUploadFileRule.getFieldName())) {
                continue;
            }
            final long size = urlsUploadFileRule.getMaxSizeInKB() * 1028L;
            if (this.maxFileSize < size) {
                this.maxFileSize = size;
            }
            if (urlsUploadFileRule.isImportURL()) {
                this.containsImportContent = true;
            }
            this.addUploadFileRule(urlsUploadFileRule);
        }
        if (this.fileUploadMaxSize != -1L) {
            this.maxFileSize = this.fileUploadMaxSize;
        }
        if (!this.parseRequestBody && (this.fileRuleMap.size() > 0 || this.hasInputStream())) {
            throw new RuntimeException("Invalid URL configuration :: Attribute 'parse-request-body' should not be used for URL's having <file> or <inputstream> rule configuration, URL path : '" + this.getPath() + "'\n");
        }
        if (urlsActionRule.skipHipDigestParamFromExtraParamValidation) {
            this.skipHipDigestParamFromExtraParamValidation = true;
        }
        if (SecurityUtil.isValidMap(urlsActionRule.getThrottlesRuleMap())) {
            if (this.hasOldThrottleConfiguration) {
                final ThrottlesRule urlsThrottles = urlsActionRule.getThrottlesRuleMap().get(ThrottlesRule.Windows.ROLLING).get(0);
                final ThrottlesRule urlThrottles = this.throttlesRuleMap.get(ThrottlesRule.Windows.ROLLING).get(0);
                for (final Long urlsDuration : urlsThrottles.getThrottleRuleMap().keySet()) {
                    if (!urlThrottles.getThrottleRuleMap().containsKey(urlsDuration)) {
                        urlThrottles.addThrottle(urlsThrottles.getThrottleRuleMap().get(urlsDuration));
                    }
                }
            }
            else {
                this.addCommonThrottlesRules(urlsActionRule.getThrottlesRuleMap());
            }
        }
        final CookieRequestHeaderRule urls_crhr = urlsActionRule.getCookieRule();
        final CookieRequestHeaderRule url_crhr = this.getCookieRule();
        if (urls_crhr != null) {
            if (url_crhr != null) {
                for (final String cookieRuleStrictName : urls_crhr.getCookieMapWithStrictName().keySet()) {
                    if (!url_crhr.getCookieMapWithStrictName().containsKey(cookieRuleStrictName)) {
                        url_crhr.getCookieMapWithStrictName().put(cookieRuleStrictName, urls_crhr.getCookieMapWithStrictName().get(cookieRuleStrictName));
                    }
                }
                for (final String cookieRuleRegexName : urls_crhr.getCookieMapWithRegexName().keySet()) {
                    if (!url_crhr.getCookieMapWithRegexName().containsKey(cookieRuleRegexName)) {
                        url_crhr.getCookieMapWithRegexName().put(cookieRuleRegexName, urls_crhr.getCookieMapWithRegexName().get(cookieRuleRegexName));
                    }
                }
            }
            else {
                this.setCookieRule(urls_crhr);
            }
        }
        final UserAgentRequestHeaderRule urls_uarhr = urlsActionRule.getUserAgentRule();
        final UserAgentRequestHeaderRule url_uarhr = this.getUserAgentRule();
        if (urls_uarhr != null) {
            if (url_uarhr != null) {
                for (final String userAgentElement : urls_uarhr.getUserAgentRulesMap().keySet()) {
                    if (!url_uarhr.getUserAgentRulesMap().containsKey(userAgentElement)) {
                        url_uarhr.getUserAgentRulesMap().put(userAgentElement, urls_uarhr.getUserAgentRulesMap().get(userAgentElement));
                    }
                }
            }
            else {
                this.setUserAgentRule(urls_uarhr);
            }
        }
        for (final HeaderRule requestHeaderRule : urlsActionRule.getRequestHeaderMapWithStrictName().values()) {
            if (!this.requestHeaderMapWithStrictName.containsKey(requestHeaderRule.getHeaderName())) {
                this.requestHeaderMapWithStrictName.put(requestHeaderRule.getHeaderName(), requestHeaderRule);
                if (requestHeaderRule.getHeaderRule().getMinOccurrences() == 1) {
                    this.mandatoryRequestHeaders.add(requestHeaderRule.getHeaderName());
                }
                this.addSecretHeaderRule(requestHeaderRule);
            }
        }
        for (final HeaderRule requestHeaderRule : urlsActionRule.getRequestHeaderMapWithRegexName().values()) {
            if (!this.requestHeaderMapWithRegexName.containsKey(requestHeaderRule.getHeaderName())) {
                this.requestHeaderMapWithRegexName.put(requestHeaderRule.getHeaderName(), requestHeaderRule);
            }
        }
        if (this.disableCacheHeaders == null && urlsActionRule.getDisabledCacheHeaders() != null) {
            (this.disableCacheHeaders = new ArrayList<String>()).addAll(urlsActionRule.getDisabledCacheHeaders());
        }
        if (this.disableSafeHeaders.size() == 0) {
            this.disableSafeHeaders.addAll(urlsActionRule.getDisabledSafeHeaders());
        }
        if (this.disableSafeHeaders.contains("all")) {
            this.filterProps.disableAllSafeHeaders(this.disableSafeHeaders);
        }
        if (!this.disableHeaders.contains("all")) {
            for (final ResponseHeaderRule headerRule : urlsActionRule.getResponseHeaderRules().values()) {
                final String headerName = headerRule.getHeaderName().trim();
                final String headerValue = headerRule.getHeaderValue().trim();
                if (!this.responseHeaderMap.containsKey(headerName) && !this.disableSafeHeaders.contains(headerName) && !this.disableHeaders.contains(headerName)) {
                    if (headerName.equals("Access-Control-Allow-Origin") && this.corsConfigType == CORSConfigType.NONE) {
                        if (headerValue.equals("*") && !this.isPublicURL()) {
                            ActionRule.logger.log(Level.SEVERE, "Urls level Response header must not set \"Access-Control-Allow-Origin\"=\"*\" , try to opt other options to validate the Origin");
                            throw new IAMSecurityException("INVALID_CONFIGURATION");
                        }
                        this.corsConfigType = CORSConfigType.STATIC;
                    }
                    this.responseHeaderMap.put(headerName, headerRule);
                }
            }
            if (this.corsConfigType == CORSConfigType.NONE && urlsActionRule.corsConfigType == CORSConfigType.DYNAMIC) {
                this.corsConfigType = CORSConfigType.DYNAMIC;
            }
            if (urlsActionRule.getResponseRule() != null) {
                if (this.responseRule == null) {
                    this.responseRule = urlsActionRule.getResponseRule();
                }
                else if (this.responseRule.getResponseLogRule() == null) {
                    this.responseRule.setResponseLogRule(urlsActionRule.getResponseRule().getResponseLogRule());
                }
            }
        }
        if (this.disableHeaders.size() == 0) {
            this.disableHeaders.addAll(urlsActionRule.getDisabledHeaders());
        }
        if (this.allowedServicesViaProxy == null) {
            this.allowedServicesViaProxy = urlsActionRule.allowedServicesViaProxy;
        }
    }
    
    protected void addCommonThrottlesRules(final Map<ThrottlesRule.Windows, List<ThrottlesRule>> commonThrottlesRuleMap) {
        if (this.throttlesRuleMap == null) {
            this.throttlesRuleMap = commonThrottlesRuleMap;
        }
        else {
            for (final Map.Entry<ThrottlesRule.Windows, List<ThrottlesRule>> mapEntry : commonThrottlesRuleMap.entrySet()) {
                final ThrottlesRule.Windows window = mapEntry.getKey();
                final List<ThrottlesRule> commonThrottlesList = mapEntry.getValue();
                if (!this.throttlesRuleMap.containsKey(window)) {
                    this.throttlesRuleMap.put(window, commonThrottlesList);
                }
                else {
                    final List<ThrottlesRule> urlThrottlesList = this.throttlesRuleMap.get(window);
                    for (final ThrottlesRule commonThrottles : commonThrottlesList) {
                        if (!this.isCommonThrottlesOverRiddenByUrlThrottles(commonThrottles, urlThrottlesList)) {
                            urlThrottlesList.add(commonThrottles);
                        }
                    }
                }
            }
        }
    }
    
    private boolean isCommonThrottlesOverRiddenByUrlThrottles(final ThrottlesRule commonThrottles, final List<ThrottlesRule> urlThrottlesList) {
        for (final ThrottlesRule urlThrottles : urlThrottlesList) {
            if (commonThrottles.getScope() == urlThrottles.getScope() && commonThrottles.getKey().equals(urlThrottles.getKey())) {
                return true;
            }
        }
        return false;
    }
    
    private void addSecretHeaderRule(final HeaderRule headerRule) {
        final ParameterRule hrRule = headerRule.getHeaderRule();
        if (hrRule.isSecret()) {
            this.secretRequestHeaders.add(headerRule.getHeaderName());
        }
        else if (hrRule.isMaskingRequiredPartially()) {
            this.partialMaskingReqHeaderRuleMap.put(hrRule.getParamName(), hrRule);
        }
    }
    
    void initializeParamGroups(final ActionRule urlsActionRule) {
        final List<ParameterRule> deltaParamRules = new ArrayList<ParameterRule>();
        if (this.paramGroupList.isEmpty()) {
            this.paramGroupList = urlsActionRule.getParamGroupList();
        }
        else if (!urlsActionRule.getParamGroupList().isEmpty()) {
            for (final String paramGroupName : urlsActionRule.getParamGroupList()) {
                if (!this.paramGroupList.contains(paramGroupName)) {
                    this.paramGroupList.add(paramGroupName);
                }
            }
        }
        for (final String paramGroupName : this.paramGroupList) {
            final List<ParameterRule> paramRules = this.filterProps.getParamGroupRules(paramGroupName);
            if (paramRules == null) {
                throw new RuntimeException("Param Group rule for the name '" + paramGroupName + "' is not defined, URL Path : " + this.path);
            }
            for (final ParameterRule paramRule : paramRules) {
                this.addParameterRule(paramRule, false);
            }
            final List<OrCriteriaRule> criteriaRules = this.filterProps.getParamGroupCriteriaRules(paramGroupName);
            if (criteriaRules == null) {
                continue;
            }
            (this.orCriteriaRules = ((this.orCriteriaRules == null) ? new ArrayList<OrCriteriaRule>() : this.orCriteriaRules)).addAll(criteriaRules);
        }
        this.addDeltaParamRule(deltaParamRules);
    }
    
    public String getXframeType() {
        return this.xframeType;
    }
    
    public boolean isWebhookAccessAllowed() {
        return this.isWebhookSupported;
    }
    
    public boolean isGetAccessForWebhookAllowed() {
        return this.isWebhookSupported;
    }
    
    public List<String> getAccessMethodsForWebhook() {
        return this.webhookAccessMethods;
    }
    
    public CookieRequestHeaderRule getCookieRule() {
        return this.cookieRule;
    }
    
    void setCookieRule(final CookieRequestHeaderRule cookieRule) {
        this.cookieRule = cookieRule;
    }
    
    public UserAgentRequestHeaderRule getUserAgentRule() {
        return this.userAgentRule;
    }
    
    void setUserAgentRule(final UserAgentRequestHeaderRule userAgentRule) {
        this.userAgentRule = userAgentRule;
    }
    
    public List<String> getAllowedServicesViaProxy() {
        return this.allowedServicesViaProxy;
    }
    
    public void setAllowedServicesViaProxy(final List<String> allowedServices) {
        this.allowedServicesViaProxy = allowedServices;
    }
    
    boolean checkForSystemAuthentication(final HttpServletRequest request, final String system_auth) {
        if (!SecurityFilterProperties.getInstance(request).disableISCSignature() && this.getSystemAuth().equals(system_auth)) {
            try {
                return SecurityUtil.verifyISCSignature(SecurityUtil.getISCSignature(request), this.scopedServices, (SecurityRequestWrapper)request);
            }
            catch (final IAMSecurityException e) {
                if ("required".equals(this.getSystemAuth())) {
                    throw e;
                }
                ActionRule.logger.log(Level.SEVERE, "Enabled system-auth=\"optional\" in the URL : \"{0}\" , Exception occurred  :  {1} , return false", new Object[] { request.getRequestURI(), e.getMessage() });
            }
        }
        return false;
    }
    
    void setJSONSecretParam(final JSONTemplateRule templateRule) {
        if (!this.hasJSONSecretParam && templateRule != null && templateRule.hasSecretParam()) {
            this.hasJSONSecretParam = true;
        }
    }
    
    public boolean isEnabledDynamicCorsHeaders() {
        return this.corsConfigType != CORSConfigType.STATIC && (this.corsConfigType == CORSConfigType.DYNAMIC || this.filterProps.corsConfigType == CORSConfigType.DYNAMIC);
    }
    
    boolean isPartsConfiguredForMultipart() {
        return this.isPartsConfiguredForMultipart;
    }
    
    public String getUrlType() {
        return this.urlType;
    }
    
    public String getTempFileNamePathAttribute() {
        if (this.tempFileNamePathAttribute == null) {
            this.tempFileNamePathAttribute = TempFileName.trimPath(this.getPath());
        }
        return this.tempFileNamePathAttribute;
    }
    
    public String getAllowAccessFor() {
        return this.allowAccessFor;
    }
    
    static {
        logger = Logger.getLogger(ActionRule.class.getName());
        WEBHOOK_SUPPORTED_METHODS = Arrays.asList("GET", "HEAD");
    }
    
    enum CORSConfigType
    {
        STATIC, 
        DYNAMIC, 
        NONE;
    }
}
