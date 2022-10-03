package com.adventnet.iam.security;

import org.apache.commons.codec.binary.Base64;
import java.security.SecureRandom;
import javax.servlet.http.Cookie;
import java.io.StringReader;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import com.zoho.security.eventfw.pojos.log.ZSEC_UNVALIDATED_BODY;
import javax.servlet.ServletInputStream;
import java.io.InputStream;
import com.zoho.security.eventfw.type.EventProcessor;
import com.zoho.security.eventfw.EventCallerInferrer;
import com.zoho.security.eventfw.EventDataProcessor;
import com.zoho.security.eventfw.config.EventFWConstants;
import com.zoho.security.eventfw.pojos.log.ZSEC_INVALID_DISPATCHER_URI;
import javax.servlet.RequestDispatcher;
import com.zoho.security.eventfw.pojos.log.ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE;
import java.util.Properties;
import org.json.JSONArray;
import org.w3c.dom.Element;
import com.zoho.security.validator.url.ZSecURL;
import org.json.JSONObject;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Function;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileItemIterator;
import java.io.File;
import org.apache.commons.fileupload.FileUploadBase;
import com.zoho.security.eventfw.pojos.log.ZSEC_PERFORMANCE_ANOMALY;
import java.io.IOException;
import java.security.MessageDigest;
import com.zoho.security.agent.AppSenseAgent;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import javax.servlet.DispatcherType;
import com.zoho.security.eventfw.pojos.log.ZSEC_ACCESS_DECRYPTPARAM_VIA_GETPARAMETER;
import java.util.logging.Level;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_UNVALIDATED_PARAM;
import java.util.regex.Matcher;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.HashSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.Collection;
import java.util.Set;
import com.zoho.security.ProxyInfo;
import java.security.Principal;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import com.adventnet.iam.security.component.info.HeaderInfo;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequestWrapper;

public class SecurityRequestWrapper extends HttpServletRequestWrapper
{
    private static final Pattern DIGIT_PATTERN;
    private String originalStreamContent;
    private byte[] originalStreamAsByteArray;
    private Map<String, ParamInfo> validatedParamValues;
    public static final String MULTIPART = "multipart/";
    private Map<String, ParamInfo> validatedParamValueObjects;
    private Map<String, List<HeaderInfo>> headerValues;
    private List<UploadedFileItem> multiPartValues;
    private Map<String, List<UploadedFilePart>> paramToPartsMap;
    private HashMap<String, List<String>> multiPartPostParams;
    private List<UploadedFileItem> importedDataAsFileList;
    private ArrayList<String> validatedParams;
    private ArrayList<String> invalidParams;
    private ActionRule actionRule;
    private static Map<String, ParameterRule> defaultParams;
    private static List<String> defaultSecretParamsList;
    private static final Logger logger;
    protected boolean locked;
    Principal principal;
    List roles;
    private String webContext;
    public RequestEntities appFirewallEntities;
    private boolean internalURL;
    private UserAgent userAgent;
    String orgUserId;
    private String queryString;
    protected String characterEncoding;
    private boolean isProxyRequest;
    private boolean isIntegrationRequest;
    private String validatedRequestPath;
    private List<String> unvalidatedExtraParams;
    boolean paramValidationCompleted;
    boolean enableSecretValueMasking;
    boolean disableParamInputValidationForTestingOE;
    boolean skipHeaderValidationAPIMode;
    private boolean isMultipartRequest;
    boolean isAPIRequestValidation;
    private int queryParamCounter;
    private int extraParamCounter;
    private int paramCounter;
    private Map<String, Object> requestInfo;
    private String referer;
    private String cspNonce;
    private String method;
    boolean isAuthenticatedViaStatelessCookie;
    private ProxyInfo proxyInfo;
    boolean enableURINormalization;
    private String forwardedHost;
    private String normalizedRequestURI;
    private IAMSecurityException iamSecurityException;
    Set<String> secretParamsInQs;
    private boolean isThrottled;
    HashMap<String, List<String>> removedXSSElementsMap;
    private String userAgentString;
    private boolean discardRequest;
    Collection<Part> cachedPartsList;
    private UploadedFileItem streamContentAsFile;
    
    protected SecurityRequestWrapper(final HttpServletRequest request) {
        super(request);
        this.originalStreamContent = null;
        this.originalStreamAsByteArray = null;
        this.validatedParamValues = null;
        this.validatedParamValueObjects = null;
        this.headerValues = null;
        this.multiPartValues = null;
        this.multiPartPostParams = null;
        this.importedDataAsFileList = null;
        this.validatedParams = null;
        this.invalidParams = null;
        this.actionRule = null;
        this.locked = true;
        this.principal = null;
        this.roles = null;
        this.webContext = null;
        this.appFirewallEntities = null;
        this.internalURL = false;
        this.userAgent = null;
        this.orgUserId = "";
        this.queryString = null;
        this.characterEncoding = null;
        this.isProxyRequest = false;
        this.isIntegrationRequest = false;
        this.validatedRequestPath = null;
        this.paramValidationCompleted = false;
        this.enableSecretValueMasking = false;
        this.disableParamInputValidationForTestingOE = false;
        this.skipHeaderValidationAPIMode = false;
        this.isAPIRequestValidation = false;
        this.queryParamCounter = 0;
        this.extraParamCounter = 0;
        this.paramCounter = 0;
        this.requestInfo = null;
        this.referer = null;
        this.cspNonce = null;
        this.isAuthenticatedViaStatelessCookie = false;
        this.enableURINormalization = false;
        this.normalizedRequestURI = null;
        this.secretParamsInQs = new HashSet<String>();
        this.removedXSSElementsMap = null;
        this.discardRequest = false;
        this.characterEncoding = request.getCharacterEncoding();
        this.method = SecurityUtil.getRequestMethod(request);
    }
    
    protected void init() {
        this.validatedParams = new ArrayList<String>(super.getParameterMap().size());
        this.validatedParamValues = new HashMap<String, ParamInfo>(super.getParameterMap().size());
        this.validatedParamValueObjects = new HashMap<String, ParamInfo>(super.getParameterMap().size());
        this.headerValues = new HashMap<String, List<HeaderInfo>>();
        this.referer = this.getHeader("referer");
        this.webContext = SecurityUtil.getContextPath((HttpServletRequest)this);
        this.validatedRequestPath = SecurityUtil.getRequestPath((HttpServletRequest)this);
        this.isMultipartRequest = SecurityUtil.isMultipartRequest((HttpServletRequest)this);
        this.forwardedHost = SecurityUtil.getForwardedHost((HttpServletRequest)this);
    }
    
    static SecurityRequestWrapper getRequestWrapperInstance(final HttpServletRequest request) {
        final SecurityRequestWrapper securedRequest = new SecurityRequestWrapper(request);
        securedRequest.init();
        return securedRequest;
    }
    
    static SecurityRequestBodyWrapper getRequestBodyWrapperInstance(final HttpServletRequest request) {
        return new SecurityRequestBodyWrapper(request);
    }
    
    void addValidatedParameterValue(final String paramName, final String value, final ParameterRule parameterRule) {
        if (this.validatedParamValueObjects.containsKey(paramName)) {
            final ParamInfo validateParamObj = this.validatedParamValueObjects.get(paramName);
            validateParamObj.updateValueList(value);
            this.validatedParamValues.put(paramName, validateParamObj);
        }
        else {
            ParamInfo existingParamInfo = this.validatedParamValues.get(paramName);
            if (existingParamInfo == null) {
                existingParamInfo = new ParamInfo(paramName, (T)value, parameterRule, this.getSourceAndIncrementParamCounters(paramName, parameterRule, value));
                this.validatedParamValues.put(paramName, existingParamInfo);
            }
            else {
                existingParamInfo.addParam(value, this.getSourceAndIncrementParamCounters(paramName, parameterRule, value));
            }
            this.debugSensitiveParam(existingParamInfo);
        }
    }
    
    void addValidatedParameterValueObject(final String paramName, final Object value, final ParameterRule parameterRule) {
        ParamInfo existingParamInfo = this.validatedParamValueObjects.get(paramName);
        if (existingParamInfo == null) {
            existingParamInfo = new ParamInfo(paramName, (T)value, parameterRule, this.getSourceAndIncrementParamCounters(paramName, parameterRule, value));
            this.validatedParamValueObjects.put(paramName, existingParamInfo);
        }
        else {
            existingParamInfo.addParam(value, this.getSourceAndIncrementParamCounters(paramName, parameterRule, value));
        }
        this.debugSensitiveParam(existingParamInfo);
    }
    
    private void debugSensitiveParam(final ParamInfo paramInfo) {
        if (!this.isAPIRequestValidation && this.actionRule != null && paramInfo.isSecret() && !this.actionRule.isInternal() && paramInfo.getSource().contains("QS")) {
            this.secretParamsInQs.add(paramInfo.getName());
        }
    }
    
    private String getSourceAndIncrementParamCounters(final String paramName, final ParameterRule parameterRule, final Object value) {
        ++this.paramCounter;
        if (parameterRule.isExtraParamRule()) {
            ++this.extraParamCounter;
        }
        final String queryStr = super.getQueryString();
        if (queryStr != null && queryStr.contains(paramName + "=") && queryStr.contains(value.toString())) {
            ++this.queryParamCounter;
            return "QS";
        }
        return this.isMultipartRequest ? "MR" : "BY";
    }
    
    protected static void addDefaultParameter(final String paramName, final ParameterRule rule) {
        if (rule.isSecret() && !SecurityRequestWrapper.defaultSecretParamsList.contains(paramName)) {
            SecurityRequestWrapper.defaultSecretParamsList.add(paramName);
        }
        SecurityRequestWrapper.defaultParams.put(paramName, rule);
    }
    
    public static Map<String, ParameterRule> getDefaultParameters() {
        return SecurityRequestWrapper.defaultParams;
    }
    
    boolean isDefaultParam(final String paramName) {
        return SecurityRequestWrapper.defaultParams.containsKey(paramName);
    }
    
    static List<String> getDefaultSecretParameters() {
        return SecurityRequestWrapper.defaultSecretParamsList;
    }
    
    public void validateDefaultParameters() {
        for (final Map.Entry<String, ParameterRule> defaultParamRules : SecurityRequestWrapper.defaultParams.entrySet()) {
            final String paramName = defaultParamRules.getKey();
            final String paramValue = this.getParameterForValidation(paramName);
            if (paramValue != null) {
                final ParameterRule rule = defaultParamRules.getValue();
                rule.validate(this);
            }
        }
    }
    
    void validateQueryString() {
        this.getQueryString();
    }
    
    public String getQueryString() {
        if (!this.locked) {
            if (this.queryString == null) {
                this.queryString = this.getValidatedQueryString();
            }
            return this.queryString;
        }
        return null;
    }
    
    String getQueryStringForValidation() {
        return super.getQueryString();
    }
    
    private String getValidatedQueryString() {
        final String originalQS = super.getQueryString();
        if (originalQS == null) {
            return null;
        }
        if (originalQS.length() == 0) {
            return "";
        }
        final Map<String, List<String>> validatedQueryParams = this.parse(originalQS);
        final String validatedQS = this.constructQueryString(validatedQueryParams);
        return originalQS.equalsIgnoreCase(validatedQS) ? originalQS : validatedQS;
    }
    
    private HashMap<String, List<String>> parse(final String query) {
        final LinkedHashMap<String, List<String>> queryParamMap = new LinkedHashMap<String, List<String>>();
        final LinkedHashMap<String, Integer> queryParamValueCounter = new LinkedHashMap<String, Integer>();
        final String[] split;
        final String[] params = split = query.split("&");
        for (final String param : split) {
            final String[] paramNameValuePair = param.split("=", 2);
            final String queryParamName = SecurityUtil.decode(paramNameValuePair[0], this.characterEncoding);
            String queryParamValue = "";
            if (paramNameValuePair.length > 1) {
                queryParamValue = SecurityUtil.decode(paramNameValuePair[1], this.characterEncoding);
            }
            if (queryParamName != null && queryParamValue != null) {
                this.addQueryParamValues(queryParamMap, queryParamValueCounter, queryParamName, queryParamValue);
            }
        }
        return queryParamMap;
    }
    
    private void addQueryParamValues(final Map<String, List<String>> queryParamMap, final Map<String, Integer> queryParamValueCounter, final String queryParamName, final String queryParamValue) {
        if (!queryParamMap.containsKey(queryParamName)) {
            queryParamMap.put(queryParamName, new ArrayList<String>());
            queryParamValueCounter.put(queryParamName, 0);
        }
        final List<String> queryParamValues = queryParamMap.get(queryParamName);
        int queryParamValuesCount = queryParamValueCounter.get(queryParamName);
        if (SecurityUtil.isValid(queryParamValue)) {
            final String[] validatedParamValues = this.getValidatedParameterValues(queryParamName);
            if (validatedParamValues != null) {
                final ParameterRule paramRule = this.getParameterRule(queryParamName);
                if (paramRule != null && paramRule.isSplitDefined()) {
                    final Pattern pattern = paramRule.getSplitPattern();
                    final Matcher matcher = pattern.matcher(queryParamValue);
                    final StringBuilder builder = new StringBuilder();
                    int index = 0;
                    while (matcher.find()) {
                        builder.append(this.getValueByIndex(validatedParamValues, queryParamValuesCount));
                        builder.append(matcher.group());
                        ++queryParamValuesCount;
                        index = matcher.end();
                    }
                    if (queryParamValue.length() > index) {
                        builder.append(this.getValueByIndex(validatedParamValues, queryParamValuesCount));
                        ++queryParamValuesCount;
                    }
                    queryParamValues.add(builder.toString());
                }
                else {
                    queryParamValues.add(this.getValueByIndex(validatedParamValues, queryParamValuesCount));
                    ++queryParamValuesCount;
                }
            }
        }
        else {
            queryParamValues.add(queryParamValue);
            ++queryParamValuesCount;
        }
        queryParamValueCounter.put(queryParamName, queryParamValuesCount);
    }
    
    private String[] getValidatedParameterValues(final String paramName) {
        if (this.isUnvalidatedExtraParam(paramName)) {
            return null;
        }
        if (this.locked && !this.isValidated(paramName)) {
            return null;
        }
        if (this.validatedParamValues.containsKey(paramName)) {
            return this.validatedParamValues.get(paramName).getValueListToArray();
        }
        return super.getParameterValues(paramName);
    }
    
    private String getValueByIndex(final String[] validatedParamValues, final int index) {
        if (index < validatedParamValues.length) {
            return validatedParamValues[index];
        }
        return "";
    }
    
    private String constructQueryString(final Map<String, List<String>> queryParams) {
        final StringBuilder builder = new StringBuilder();
        for (final Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            String paramName = entry.getKey();
            paramName = SecurityUtil.encode(paramName, this.characterEncoding);
            if (paramName != null) {
                for (String paramValue : entry.getValue()) {
                    if (builder.length() > 0) {
                        builder.append("&");
                    }
                    builder.append(paramName);
                    if (!"".equals(paramValue)) {
                        paramValue = SecurityUtil.encode(paramValue, this.characterEncoding);
                        if (paramValue == null) {
                            continue;
                        }
                        builder.append("=");
                        builder.append(paramValue);
                    }
                }
            }
        }
        return builder.toString();
    }
    
    boolean isCorsRequest() {
        final String origin = SecurityUtil.getDomainWithPort(this.getHeader("Origin"));
        if (!SecurityUtil.isValid(origin)) {
            return false;
        }
        final String hostFromRequestURL = SecurityUtil.getDomainWithPort(this.getRequestURL().toString());
        return !origin.equals(hostFromRequestURL);
    }
    
    boolean isCorsPreFlightRequest() {
        return SecurityFilterProperties.CORS_REQUEST_TYPE.PREFLIGHT == this.getAttribute("CORS_REQUEST_TYPE");
    }
    
    public String getParameter(final String paramName) {
        if (this.isUnvalidatedExtraParam(paramName)) {
            if (this.actionRule != null && this.actionRule.isErrorPage()) {
                final ActionRule requestActionRule = (ActionRule)this.getAttribute("RequestURLRule");
                if (requestActionRule != null) {
                    ZSEC_UNVALIDATED_PARAM.pushErrRq((String)this.getAttribute("javax.servlet.error.request_uri"), requestActionRule.getPrefix(), requestActionRule.getPath(), requestActionRule.getMethod(), requestActionRule.getOperationValue(), paramName, this.getRequestURI(), (ExecutionTimer)null);
                }
                else {
                    ZSEC_UNVALIDATED_PARAM.pushErrRq((String)this.getAttribute("javax.servlet.error.request_uri"), (String)null, (String)null, (String)null, (String)null, paramName, this.getRequestURI(), (ExecutionTimer)null);
                }
                if (SecurityFilterProperties.getInstance((HttpServletRequest)this).getErrorPageValidationMode() == SecurityFilterProperties.ErrorPageValidationMode.ENFORCEMENT) {
                    throw new UnsupportedOperationException("Unsupported access for Parameter \"" + paramName + "\" not Configured in error-page marked url");
                }
            }
            return null;
        }
        if (this.locked && !this.isValidated(paramName)) {
            return null;
        }
        if (this.validatedParamValues.containsKey(paramName)) {
            if (this.getURLActionRule() != null) {
                final ParameterRule paramRule = this.getURLActionRule().getParameterRule(paramName);
                if (paramRule != null) {
                    if ("xml".equals(paramRule.getDataType())) {
                        SecurityRequestWrapper.logger.log(Level.WARNING, "Unsupported access for xml content. To access use getXMLParameter().");
                        throw new UnsupportedOperationException("Unsupported access for xml content. To access Use getXMLParameter().");
                    }
                    if ("url".equals(paramRule.getDataType())) {
                        SecurityRequestWrapper.logger.log(Level.WARNING, "Unsupported access for url object. To access use getURLObject(<paramName>) .");
                        throw new UnsupportedOperationException("Unsupported access for url content. To access Use getURLObject(<paramName>.");
                    }
                    if (paramRule.isDecryptionEnabled()) {
                        ZSEC_ACCESS_DECRYPTPARAM_VIA_GETPARAMETER.pushInfo(this.getRequestURI(), this.actionRule.getPrefix(), this.actionRule.getPath(), this.actionRule.getMethod(), this.actionRule.getOperationValue(), paramName, (ExecutionTimer)null);
                    }
                    if ((DispatcherType.FORWARD.equals((Object)this.getDispatcherType()) || DispatcherType.INCLUDE.equals((Object)this.getDispatcherType())) && this.isAllowedInDispatcher(paramRule)) {
                        final String paramvalue = super.getParameter(paramName);
                        if (paramvalue != null) {
                            return paramvalue;
                        }
                    }
                }
            }
            return this.validatedParamValues.get(paramName).getValue();
        }
        final String superParam = super.getParameter(paramName);
        if (superParam != null && this.getURLActionRule() != null && !this.isAllowedParamConsumptionFromSuper(paramName)) {
            ZSEC_UNVALIDATED_PARAM.pushNormalRq(this.getRequestURI(), this.actionRule.getPrefix(), this.actionRule.getPath(), this.actionRule.getMethod(), this.actionRule.getOperationValue(), paramName, (ExecutionTimer)null);
        }
        if (this.actionRule != null && this.actionRule.isErrorPage() && !this.isValidated(paramName)) {
            final ActionRule requestActionRule2 = (ActionRule)this.getAttribute("RequestURLRule");
            if (requestActionRule2 != null) {
                ZSEC_UNVALIDATED_PARAM.pushErrRq((String)this.getAttribute("javax.servlet.error.request_uri"), requestActionRule2.getPrefix(), requestActionRule2.getPath(), requestActionRule2.getMethod(), requestActionRule2.getOperationValue(), paramName, this.getRequestURI(), (ExecutionTimer)null);
            }
            else {
                ZSEC_UNVALIDATED_PARAM.pushErrRq((String)this.getAttribute("javax.servlet.error.request_uri"), (String)null, (String)null, (String)null, (String)null, paramName, this.getRequestURI(), (ExecutionTimer)null);
            }
            if (SecurityFilterProperties.getInstance((HttpServletRequest)this).getErrorPageValidationMode() == SecurityFilterProperties.ErrorPageValidationMode.ENFORCEMENT) {
                throw new UnsupportedOperationException("Unsupported access for Unvalidated  Parameter :\"" + paramName + " \" in error-page marked url");
            }
        }
        return superParam;
    }
    
    public String getDecryptParameter(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValues.containsKey(paramName) && this.getURLActionRule() != null) {
            final ParameterRule paramRule = this.getURLActionRule().getParameterRule(paramName);
            if (paramRule != null && paramRule.isDecryptionEnabled()) {
                return this.validatedParamValues.get(paramName).getValue();
            }
        }
        return null;
    }
    
    private boolean isAllowedParamConsumptionFromSuper(final String paramName) {
        return SecurityUtil.isCaptchaParam(paramName, this.actionRule) || paramName.equals(SecurityFilterProperties.getInstance((HttpServletRequest)this).getCSRFParamName()) || SecurityUtil.isOperationParamOrHipDigestParam(paramName, this.actionRule);
    }
    
    public String getParamOrStreamContent() {
        final ParameterRule paramOrStreamRule = this.getURLActionRule().getParamOrStreamRule();
        if (paramOrStreamRule == null) {
            return null;
        }
        if (this.validatedParamValues.containsKey(paramOrStreamRule.getParamName())) {
            return this.validatedParamValues.get(paramOrStreamRule.getParamName()).getValue();
        }
        return this.getFilteredStreamContent();
    }
    
    private boolean isAllowedInDispatcher(final ParameterRule paramRule) {
        return !paramRule.isEditableOnValidation() && !paramRule.isTrim((HttpServletRequest)this) && (paramRule.isRegExValidation() || paramRule.isPrimitive());
    }
    
    public String getFilteredStreamContent() {
        return this.getParameter("zoho-inputstream");
    }
    
    String getOriginalInputStreamContent() {
        return this.originalStreamContent;
    }
    
    void setOriginalInputStreamContent(final String content) {
        this.originalStreamContent = content;
    }
    
    public String[] getParameterValues(final String paramName) {
        if (this.isUnvalidatedExtraParam(paramName)) {
            if (this.actionRule != null && this.actionRule.isErrorPage()) {
                SecurityRequestWrapper.logger.log(Level.WARNING, "Unsupported access for Parameter : \"{0}\" not Configured in error-page marked url", new Object[] { paramName });
                if (SecurityFilterProperties.getInstance((HttpServletRequest)this).getErrorPageValidationMode() == SecurityFilterProperties.ErrorPageValidationMode.ENFORCEMENT) {
                    throw new UnsupportedOperationException("Unsupported access for Parameter \"" + paramName + "\" not Configured in error-page marked url");
                }
            }
            return null;
        }
        if (this.locked && !this.isValidated(paramName)) {
            return null;
        }
        if (this.validatedParamValues.containsKey(paramName)) {
            if (this.actionRule != null) {
                final ParameterRule paramRule = this.actionRule.getParameterRule(paramName);
                if (paramRule != null && paramRule.isDecryptionEnabled()) {
                    ZSEC_ACCESS_DECRYPTPARAM_VIA_GETPARAMETER.pushInfo(this.getRequestURI(), this.actionRule.getPrefix(), this.actionRule.getPath(), this.actionRule.getMethod(), this.actionRule.getOperationValue(), paramName, (ExecutionTimer)null);
                }
            }
            return this.validatedParamValues.get(paramName).getValueListToArray();
        }
        if (this.actionRule != null && this.actionRule.isErrorPage()) {
            SecurityRequestWrapper.logger.log(Level.WARNING, "Unsupported access for Unvalidated Parameter : \"{0}\" in error-page marked url", new Object[] { paramName });
            if (SecurityFilterProperties.getInstance((HttpServletRequest)this).getErrorPageValidationMode() == SecurityFilterProperties.ErrorPageValidationMode.ENFORCEMENT) {
                throw new UnsupportedOperationException("Unsupported access for Unvalidated  Parameter :\"" + paramName + " \" in error-page marked url");
            }
        }
        return super.getParameterValues(paramName);
    }
    
    protected String[] getParameterValuesForLogging(final String paramName) {
        final String[] paramValues = super.getParameterValues(paramName);
        if (paramValues != null) {
            return paramValues;
        }
        if (this.multiPartPostParams != null && this.multiPartPostParams.containsKey(paramName)) {
            return (String[])this.multiPartPostParams.get(paramName).toArray(new String[0]);
        }
        return null;
    }
    
    protected String[] getOperationParameterValues(final String paramName) {
        return super.getParameterValues(paramName);
    }
    
    public Map getParameterMap() {
        final Enumeration<String> e = this.getParameterNames();
        if (e == null) {
            return new HashMap();
        }
        final HashMap map = new HashMap(super.getParameterMap().size());
        while (e.hasMoreElements()) {
            final String key = e.nextElement();
            final String[] value = this.getParameterValues(key);
            if (value != null) {
                map.put(key, value);
            }
            else {
                map.put(key, new String[] { "" });
            }
        }
        return map;
    }
    
    protected String[] getParameterValuesForValidation(final String paramName) {
        if (this.isUnvalidatedExtraParam(paramName)) {
            return null;
        }
        if (this.validatedParamValues.containsKey(paramName)) {
            return this.validatedParamValues.get(paramName).getValueListToArray();
        }
        final String[] paramValues = super.getParameterValues(paramName);
        if (paramValues != null) {
            return paramValues;
        }
        if (this.multiPartPostParams != null && this.multiPartPostParams.containsKey(paramName)) {
            return (String[])this.multiPartPostParams.get(paramName).toArray(new String[0]);
        }
        return null;
    }
    
    protected String getParameterForValidation(final String paramName) {
        if (this.isUnvalidatedExtraParam(paramName)) {
            return null;
        }
        if (this.validatedParamValues.containsKey(paramName)) {
            return this.validatedParamValues.get(paramName).getValue();
        }
        final String paramValue = super.getParameter(paramName);
        if (paramValue != null) {
            return paramValue;
        }
        if (this.multiPartPostParams != null && this.multiPartPostParams.containsKey(paramName)) {
            return this.multiPartPostParams.get(paramName).get(0);
        }
        return null;
    }
    
    public Enumeration<String> getParameterNames() {
        if (this.isMultipartRequest && this.multiPartPostParams != null) {
            final Enumeration<String> requestParams = super.getParameterNames();
            final ArrayList<String> list = Collections.list(requestParams);
            list.addAll(this.multiPartPostParams.keySet());
            return Collections.enumeration(list);
        }
        return super.getParameterNames();
    }
    
    public boolean isMultipartRequest() {
        return this.isMultipartRequest;
    }
    
    protected ArrayList<String> getValidatedParameters() {
        return this.validatedParams;
    }
    
    void importFiles() {
        try {
            final ActionRule rule = this.getURLActionRule();
            if (rule.containsImportContent()) {
                for (final UploadFileRule fileRule : rule.getUploadFileRuleList()) {
                    if (fileRule.isImportURL()) {
                        final String fieldName = fileRule.getFieldName();
                        final String url = this.getParameterForValidation(fieldName);
                        if (fileRule.getFileNameRule() == null) {
                            final String fileNameRegex = "url";
                            fileRule.setFileNameRule(new ParameterRule(fieldName, fileNameRegex));
                        }
                        this.addValidatedParameter(fieldName);
                        if (url == null) {
                            continue;
                        }
                        if ("".equals(url)) {
                            continue;
                        }
                        this.addValidatedParameterValue(fieldName, url, fileRule.getFileNameRule());
                        final UploadedFileItem ufi = SecurityUtil.importFromURL(new URL(url), fieldName, fileRule, this);
                        if (this.multiPartValues == null) {
                            this.multiPartValues = new ArrayList<UploadedFileItem>();
                        }
                        if (ufi == null) {
                            continue;
                        }
                        this.multiPartValues.add(ufi);
                    }
                }
            }
            this.setAttribute("MULTIPART_FORM_REQUEST", this.multiPartValues);
        }
        catch (final MalformedURLException ex) {
            SecurityRequestWrapper.logger.log(Level.WARNING, null, ex.getMessage());
            throw new IAMSecurityException("INVALID_IMPORT_URL", this.getRequestURI(), this.getRemoteAddr(), this.getHeader("Referer"));
        }
        catch (final IAMSecurityException e) {
            throw new IAMSecurityException(e.getErrorCode(), this.getRequestURI(), this.getRemoteAddr(), this.getHeader("Referer"));
        }
        catch (final Exception e2) {
            SecurityRequestWrapper.logger.log(Level.WARNING, null, e2);
            throw new IAMSecurityException("UNABLE_TO_IMPORT", this.getRequestURI(), this.getRemoteAddr(), this.getHeader("Referer"));
        }
    }
    
    void initMultipartParams() {
        try {
            this.multiPartValues = new ArrayList<UploadedFileItem>();
            this.multiPartPostParams = new HashMap<String, List<String>>();
            final Map<String, FileUploadTracker> fileUploadRecord = new HashMap<String, FileUploadTracker>();
            if (this.actionRule.isPartsConfiguredForMultipart()) {
                try {
                    MultipartPartsUtil.initParts(this, super.getParts(), fileUploadRecord);
                }
                catch (final IllegalStateException e) {
                    final Throwable cause = e.getCause();
                    if (cause != null) {
                        final String className = cause.getClass().getSimpleName();
                        if (className.equals("FileSizeLimitExceededException")) {
                            SecurityRequestWrapper.logger.log(Level.SEVERE, "File Size Limit Exceeded", cause);
                            throw new IAMSecurityException("URL_FILE_UPLOAD_MAX_SIZE_LIMIT_EXCEEDED", this.getRequestURI(), this.getRemoteAddr(), this.getHeader("Referer"));
                        }
                        if (className.equals("SizeLimitExceededException")) {
                            SecurityRequestWrapper.logger.log(Level.SEVERE, "Request Size Limit Exceeded", cause);
                            throw new IAMSecurityException("REQUEST_SIZE_MORE_THAN_ALLOWED_SIZE", this.getRequestURI(), this.getRemoteAddr(), this.getHeader("Referer"));
                        }
                    }
                    throw e;
                }
                finally {
                    if (this.paramToPartsMap != null) {
                        this.multiPartValues.addAll(this.getPartsAsUploadedFilePart());
                    }
                }
            }
            else {
                final File tempFileuploadDir = SecurityUtil.getTempFileUploadDir();
                final DiskFileItemFactory factory = new DiskFileItemFactory(0, tempFileuploadDir);
                final ServletFileUpload upload = new ServletFileUpload((FileItemFactory)factory);
                final long maxRequestSize = SecurityFilterProperties.getInstance((HttpServletRequest)this).getSecurityProvider().getMaxMultiPartRequestSize((HttpServletRequest)this, this.getURLActionRule());
                if (maxRequestSize > 0L) {
                    upload.setSizeMax(maxRequestSize * 1024L);
                }
                final long maxAllowedSize = SecurityFilterProperties.getInstance((HttpServletRequest)this).getSecurityProvider().getMaximumUploadSize((HttpServletRequest)this, this.actionRule);
                final FileItemIterator iter = upload.getItemIterator((HttpServletRequest)this);
                long uploadedFilesSizeInBytes = 0L;
                while (iter.hasNext()) {
                    final FileItemStream item = iter.next();
                    if (item.isFormField()) {
                        List<String> values = this.multiPartPostParams.get(item.getFieldName());
                        if (values == null) {
                            values = new ArrayList<String>();
                            this.multiPartPostParams.put(item.getFieldName(), values);
                        }
                        values.add(Streams.asString(item.openStream(), "UTF-8"));
                    }
                    else {
                        final DiskFileItem fileItem = SecurityDiskFileItem.createDiskFileItem(new TempFileName(this.getURLActionRule()), factory, item.getFieldName(), item.getContentType(), item.isFormField(), item.getName());
                        final String filename = fileItem.getName();
                        final String[] str = filename.split("[\\/\\\\]");
                        final String fileName = str[str.length - 1];
                        final UploadedFileItem uploadFileItem = new UploadedFileItem(fileName, fileItem.getFieldName(), fileItem.getContentType(), fileItem);
                        UploadFileRule uploadRule = this.getURLActionRule().getUploadFileRule(fileItem.getFieldName());
                        if (uploadRule == null) {
                            uploadRule = SecurityFilterProperties.getInstance((HttpServletRequest)this).getSecurityProvider().getDynamicFileRule((HttpServletRequest)this, this.actionRule, uploadFileItem);
                        }
                        if (uploadRule == null) {
                            SecurityRequestWrapper.logger.log(Level.INFO, "Upload rule for the file {0} is not configured for the URL {1}", new Object[] { fileItem.getFieldName(), this.getRequestURI() });
                            throw new IAMSecurityException("UPLOAD_RULE_NOT_CONFIGURED", this.getRequestURI(), this.getRemoteAddr(), this.getHeader("Referer"), fileItem.getFieldName(), uploadRule);
                        }
                        try {
                            long maxAllowedSizeInBytes = (maxAllowedSize != -1L) ? maxAllowedSize : uploadRule.getMaxSizeInKB();
                            if (maxAllowedSizeInBytes > 0L) {
                                maxAllowedSizeInBytes *= 1024L;
                            }
                            this.validateNumberOfUploads(uploadFileItem, uploadRule, fileUploadRecord);
                            MessageDigest md = null;
                            if (AppSenseAgent.isEnableReqInfoFileHash()) {
                                md = MessageDigest.getInstance(AppSenseAgent.getFileHashAlgorithm());
                            }
                            final long size = SecurityUtil.copy(item.openStream(), new byte[8192], maxAllowedSizeInBytes, uploadRule, fileItem, (HttpServletRequest)this, md);
                            if (md != null) {
                                uploadFileItem.setFileHash(SecurityUtil.getDigestString(md));
                            }
                            if (size > -1L) {
                                uploadedFilesSizeInBytes += size;
                                if (this.actionRule.isFileUploadMaxSizeExceeded(uploadedFilesSizeInBytes / 1024L)) {
                                    throw new IAMSecurityException("URL_FILE_UPLOAD_MAX_SIZE_LIMIT_EXCEEDED");
                                }
                            }
                            if (fileItem.getSize() == 0L && filename.length() > 0 && !uploadRule.isAllowedEmptyFile()) {
                                SecurityRequestWrapper.logger.log(Level.SEVERE, "Empty file is not allowed for the field : {0} for the request URI {1}", new Object[] { fileItem.getFieldName(), this.getRequestURI() });
                                throw new IAMSecurityException("EMPTY_FILE_NOT_ALLOWED", this.getRequestURI(), this.getRemoteAddr(), this.getHeader("Referer"), null, fileName, 0L, fileItem.getFieldName(), uploadRule);
                            }
                        }
                        catch (final IAMSecurityException | IOException e2) {
                            fileItem.delete();
                            IAMSecurityException ise = null;
                            if (e2 instanceof IAMSecurityException) {
                                ise = (IAMSecurityException)e2;
                            }
                            else {
                                ise = new IAMSecurityException(e2.getMessage());
                            }
                            if (uploadRule.continueOnError()) {
                                uploadFileItem.addSecurityException(ise);
                                this.multiPartValues.add(uploadFileItem);
                                continue;
                            }
                            throw ise;
                        }
                        if (fileItem.getSize() <= 0L || fileItem.getStoreLocation() == null) {
                            continue;
                        }
                        final ExecutionTimer cdtimer = ExecutionTimer.startInstance();
                        final String contentType = SecurityUtil.getMimeType(this, fileItem.getStoreLocation(), fileName);
                        ZSEC_PERFORMANCE_ANOMALY.pushMimeDetection(this.getRequestURI(), fileItem.getName(), contentType, cdtimer);
                        uploadFileItem.setFileSize(fileItem.getSize());
                        uploadFileItem.setUploadedFile(fileItem.getStoreLocation());
                        uploadFileItem.setContentTypeDetected(contentType);
                        this.multiPartValues.add(uploadFileItem);
                    }
                }
            }
            final List<String> fieldNamesWithMinLimitFailed = new ArrayList<String>();
            for (final UploadFileRule fileRule : this.actionRule.getUploadFileRuleList()) {
                boolean mandatoryFieldPresent = false;
                if (fileRule.isMandatory()) {
                    for (final FileUploadTracker tracker : fileUploadRecord.values()) {
                        if (tracker.fileRule == fileRule) {
                            mandatoryFieldPresent = true;
                            break;
                        }
                    }
                    if (mandatoryFieldPresent || fileRule.continueOnError()) {
                        continue;
                    }
                    fieldNamesWithMinLimitFailed.add(fileRule.getFieldName());
                }
            }
            if (fileUploadRecord.size() > 0) {
                for (final Map.Entry<String, FileUploadTracker> fileUploadRecordEntry : fileUploadRecord.entrySet()) {
                    final String fieldName = fileUploadRecordEntry.getKey();
                    final FileUploadTracker fileUploadTracker = fileUploadRecordEntry.getValue();
                    if (!fileUploadTracker.lowerLimitSatisfied) {
                        if (fileUploadTracker.fileRule.continueOnError()) {
                            for (final UploadedFileItem fileItem2 : fileUploadTracker.fileItemsBelowLowerLimit) {
                                fileItem2.addSecurityException(new IAMSecurityException("LESS_THAN_MIN_OCCURANCE", this.getRequestURI(), this.getRemoteAddr(), this.getHeader("Referer"), fieldName, fileUploadTracker.fileRule));
                            }
                        }
                        else {
                            fieldNamesWithMinLimitFailed.add(fieldName);
                        }
                    }
                }
            }
            if (fieldNamesWithMinLimitFailed.size() > 0) {
                SecurityRequestWrapper.logger.log(Level.SEVERE, "follwing File field(s) : \"{0}\" , does not satisfy the minimum upload limit configured \n ", new Object[] { fieldNamesWithMinLimitFailed });
                throw new IAMSecurityException("LESS_THAN_MIN_OCCURANCE", this.getRequestURI(), this.getRemoteAddr(), this.getHeader("Referer"), fieldNamesWithMinLimitFailed);
            }
            this.setAttribute("MULTIPART_FORM_REQUEST", this.multiPartValues);
        }
        catch (final IAMSecurityException | IOException e3) {
            for (final UploadedFileItem p : this.multiPartValues) {
                final File f = p.getUploadedFileForValidation();
                if (f != null && f.exists()) {
                    f.delete();
                }
            }
            IAMSecurityException ise2 = null;
            if (e3 instanceof IAMSecurityException) {
                ise2 = (IAMSecurityException)e3;
            }
            else {
                ise2 = new IAMSecurityException(e3.getMessage());
            }
            throw ise2;
        }
        catch (final FileUploadBase.SizeLimitExceededException e4) {
            throw new IAMSecurityException("REQUEST_SIZE_MORE_THAN_ALLOWED_SIZE", this.getRequestURI(), this.getRemoteAddr(), this.getHeader("Referer"));
        }
        catch (final Exception e3) {
            SecurityRequestWrapper.logger.log(Level.SEVERE, "Exception while parsing Multipart-form request for {0} {1}", new Object[] { this.getRequestURI(), e3 });
            throw new IAMSecurityException(e3.getMessage());
        }
    }
    
    void setPartsMap(final Map<String, List<UploadedFilePart>> partsMap) {
        this.paramToPartsMap = partsMap;
    }
    
    private Collection<UploadedFilePart> getPartsAsUploadedFilePart() {
        return this.paramToPartsMap.values().stream().flatMap((Function<? super List<UploadedFilePart>, ? extends Stream<?>>)Collection::stream).collect((Collector<? super Object, ?, Collection<UploadedFilePart>>)Collectors.toList());
    }
    
    protected void unLock() {
        this.locked = false;
    }
    
    protected void addValidatedParameter(final String paramName) {
        if (this.validatedParams == null) {
            this.validatedParams = new ArrayList<String>();
        }
        this.validatedParams.add(paramName);
    }
    
    protected void validateNumberOfUploads(final UploadedFileItem fileItem, final UploadFileRule fileRule, final Map<String, FileUploadTracker> fileUploadRecord) {
        final String fileName = fileItem.getFileName();
        final String fieldName = fileItem.getFieldName();
        if (fileName != null) {
            FileUploadTracker fileUploadTracker = fileUploadRecord.get(fieldName);
            if (fileUploadTracker == null) {
                fileUploadTracker = new FileUploadTracker(fileRule);
                fileUploadRecord.put(fieldName, fileUploadTracker);
            }
            final int lowerLimit = fileRule.getUploadLimit().getLowerLimit();
            final int upperLimit = fileRule.getUploadLimit().getUpperLimit();
            final FileUploadTracker fileUploadTracker2 = fileUploadTracker;
            ++fileUploadTracker2.uploadCounter;
            if (!fileUploadTracker.lowerLimitSatisfied) {
                if (fileUploadTracker.uploadCounter >= lowerLimit) {
                    fileUploadTracker.updateLowerLimitAchieved();
                }
                else {
                    fileUploadTracker.addFileItem(fileItem);
                }
            }
            if (fileUploadTracker.uploadCounter > upperLimit) {
                SecurityRequestWrapper.logger.log(Level.SEVERE, "<file name=\"{0}\" ...  is getting more file uploads than its configured/projected  \"limit\":\"{1}\" ", new Object[] { fieldName, upperLimit });
                throw new IAMSecurityException("MORE_THAN_MAX_OCCURANCE", this.getRequestURI(), this.getRemoteAddr(), this.getHeader("Referer"), fieldName, fileRule);
            }
        }
    }
    
    void addInvalidParam(final String invalidParamName) {
        (this.invalidParams = ((this.invalidParams == null) ? new ArrayList<String>() : this.invalidParams)).add(invalidParamName);
    }
    
    boolean isInvalidParam(final String paramName) {
        return this.invalidParams != null && this.invalidParams.contains(paramName);
    }
    
    public boolean isValidated(final String paramName) {
        return SecurityRequestWrapper.defaultParams.containsKey(paramName) || this.validatedParams.contains(paramName);
    }
    
    void addUnvalidatedExtraParam(final String paramName) {
        if (this.unvalidatedExtraParams == null) {
            this.unvalidatedExtraParams = new ArrayList<String>();
        }
        this.unvalidatedExtraParams.add(paramName);
    }
    
    boolean isUnvalidatedExtraParam(final String paramName) {
        return this.unvalidatedExtraParams != null && this.unvalidatedExtraParams.contains(paramName);
    }
    
    List<String> getUnvalidatedExtraParams() {
        return (this.unvalidatedExtraParams != null) ? this.unvalidatedExtraParams : null;
    }
    
    public Long getLongParameter(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return (Long)this.validatedParamValueObjects.get(paramName).getObject();
        }
        return null;
    }
    
    public Long getLongParameter(final String paramName, final Long ifabsent) {
        final Long l = this.getLongParameter(paramName);
        return (l == null) ? ifabsent : l;
    }
    
    public List<Long> getLongParameters(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return this.validatedParamValueObjects.get(paramName).getObjectList();
        }
        return null;
    }
    
    public Double getDoubleParameter(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return (Double)this.validatedParamValueObjects.get(paramName).getObject();
        }
        return null;
    }
    
    public Double getDoubleParameter(final String paramName, final Double ifabsent) {
        final Double d = this.getDoubleParameter(paramName);
        return (d == null) ? ifabsent : d;
    }
    
    public List<Double> getDoubleParameters(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return this.validatedParamValueObjects.get(paramName).getObjectList();
        }
        return null;
    }
    
    public Float getFloatParameter(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return (Float)this.validatedParamValueObjects.get(paramName).getObject();
        }
        return null;
    }
    
    public Float getFloatParameter(final String paramName, final Float ifabsent) {
        final Float f = this.getFloatParameter(paramName);
        return (f == null) ? ifabsent : f;
    }
    
    public List<Float> getFloatParameters(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return this.validatedParamValueObjects.get(paramName).getObjectList();
        }
        return null;
    }
    
    public Integer getIntegerParameter(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return (Integer)this.validatedParamValueObjects.get(paramName).getObject();
        }
        return null;
    }
    
    public Integer getIntegerParameter(final String paramName, final Integer ifabsent) {
        final Integer i = this.getIntegerParameter(paramName);
        return (i == null) ? ifabsent : i;
    }
    
    public List<Integer> getIntegerParameters(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return this.validatedParamValueObjects.get(paramName).getObjectList();
        }
        return null;
    }
    
    public Short getShortParameter(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return (Short)this.validatedParamValueObjects.get(paramName).getObject();
        }
        return null;
    }
    
    public Short getShortParameter(final String paramName, final Short ifabsent) {
        final Short s = this.getShortParameter(paramName);
        return (s == null) ? ifabsent : s;
    }
    
    public List<Short> getShortParameters(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return this.validatedParamValueObjects.get(paramName).getObjectList();
        }
        return null;
    }
    
    public Boolean getBooleanParameter(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return (Boolean)this.validatedParamValueObjects.get(paramName).getObject();
        }
        return null;
    }
    
    public JSONObject getJSONObjectParameter(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return (JSONObject)this.validatedParamValueObjects.get(paramName).getObject();
        }
        return null;
    }
    
    public ZSecURL getURLObject(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return (ZSecURL)this.validatedParamValueObjects.get(paramName).getObject();
        }
        return null;
    }
    
    public UploadedFileItem getImportedDataAsFile(final String paramName) {
        final ZSecURL urlObj = this.getURLObject(paramName);
        if (urlObj != null) {
            return (UploadedFileItem)urlObj.getImportedDataAsFile();
        }
        return null;
    }
    
    public Element getXMLParameter(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return (Element)this.validatedParamValueObjects.get(paramName).getObject();
        }
        return null;
    }
    
    public JSONArray getJSONArrayParameter(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return (JSONArray)this.validatedParamValueObjects.get(paramName).getObject();
        }
        return null;
    }
    
    public Object getFilteredStreamObject() {
        final String paramName = "zoho-inputstream";
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return this.validatedParamValueObjects.get(paramName).getObject();
        }
        return null;
    }
    
    public Properties getPropertiesParameter(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return (Properties)this.validatedParamValueObjects.get(paramName).getObject();
        }
        return null;
    }
    
    public CsvValidator getCSVParameter(final String paramName) {
        if ((!this.locked || this.isValidated(paramName)) && this.validatedParamValueObjects.containsKey(paramName)) {
            return (CsvValidator)this.validatedParamValueObjects.get(paramName).getObject();
        }
        return null;
    }
    
    public UploadedFileItem getMultipartParameter(final String paramName) {
        if (this.multiPartValues != null) {
            for (final UploadedFileItem uf : this.multiPartValues) {
                if (uf.getFieldName().equals(paramName)) {
                    return uf;
                }
            }
        }
        return null;
    }
    
    public List<UploadedFileItem> getMultipartParameters(final String paramName) {
        if (this.multiPartValues == null) {
            return null;
        }
        final List<UploadedFileItem> files = new ArrayList<UploadedFileItem>();
        for (final UploadedFileItem uf : this.multiPartValues) {
            if (uf.getFieldName().equals(paramName)) {
                files.add(uf);
            }
        }
        return files;
    }
    
    public List<UploadedFileItem> getMultipartFiles() {
        return this.multiPartValues;
    }
    
    public static SecurityRequestWrapper getInstance(final HttpServletRequest request) {
        final SecurityRequestWrapper securityRequest = (SecurityRequestWrapper)request.getAttribute(SecurityRequestWrapper.class.getName());
        if (securityRequest != null) {
            return securityRequest;
        }
        throw new IAMSecurityException("This request is not passed via SecurityFilter");
    }
    
    public Principal getUserPrincipal() {
        return (this.principal != null) ? this.principal : super.getUserPrincipal();
    }
    
    public String getRemoteUser() {
        return (this.principal != null) ? this.principal.getName() : super.getRemoteUser();
    }
    
    public boolean isUserInRole(final String role) {
        return (this.roles != null && this.roles.contains(role)) || super.isUserInRole(role);
    }
    
    public void addRemoteUser(final String user) {
        this.principal = new SimplePrincipal(user);
    }
    
    public void addOrgUser(final String user) {
        this.orgUserId = user;
    }
    
    public String getOrgUser() {
        return this.orgUserId;
    }
    
    public void addRoles(final List roles) {
        this.roles = roles;
    }
    
    void setURLActionRule(final ActionRule actionRule) {
        this.actionRule = actionRule;
    }
    
    public ActionRule getURLActionRule() {
        return this.actionRule;
    }
    
    public String getURLActionRulePrefix() {
        return (this.actionRule != null) ? this.actionRule.getPrefix() : null;
    }
    
    public String getURLActionRulePath() {
        return (this.actionRule != null) ? this.actionRule.getPath() : null;
    }
    
    public String getURLActionRuleMethod() {
        return (this.actionRule != null) ? this.actionRule.getMethod() : null;
    }
    
    public String getURLActionRuleOperation() {
        return (this.actionRule != null) ? this.actionRule.getOperationValue() : null;
    }
    
    public ArrayList<String> getDynamicParamNames() {
        final Enumeration<String> allParams = this.getParameterNames();
        final ArrayList<String> list = new ArrayList<String>();
        while (allParams.hasMoreElements()) {
            final String name = allParams.nextElement();
            if (!this.isValidated(name)) {
                list.add(name);
            }
        }
        return list;
    }
    
    public List<String> getRemovedXSSElements(final String paramName) {
        if (this.removedXSSElementsMap != null) {
            return this.removedXSSElementsMap.get(paramName);
        }
        return null;
    }
    
    void setRemovedXSSElements(final String paramName, final List<String> remList) {
        if (this.removedXSSElementsMap == null) {
            this.removedXSSElementsMap = new HashMap<String, List<String>>();
        }
        this.removedXSSElementsMap.put(paramName, remList);
    }
    
    public boolean isRemovedXSSElement(final String paramName, final String elementName) {
        if (this.removedXSSElementsMap != null && paramName != null && elementName != null) {
            final List<String> remElements = this.removedXSSElementsMap.get(paramName);
            if (remElements != null) {
                return remElements.contains(elementName.toLowerCase());
            }
        }
        return false;
    }
    
    public boolean isAnyXSSElementRemoved(final String paramName) {
        if (this.removedXSSElementsMap != null && paramName != null) {
            final List<String> remElements = this.removedXSSElementsMap.get(paramName);
            if (remElements != null && remElements.size() > 0) {
                return true;
            }
        }
        return false;
    }
    
    void setUserAgent() {
        this.userAgentString = this.getHeader("User-Agent");
        if (SecurityUtil.isValid(this.userAgentString)) {
            this.userAgent = new UserAgent(this.userAgentString);
        }
    }
    
    public UserAgent getUserAgent() {
        return this.userAgent;
    }
    
    public String getRemoteAddr() {
        final String remoteIp = super.getRemoteAddr();
        if (SecurityFilterProperties.getInstance((HttpServletRequest)this).isAuthenticationProviderConfigured()) {
            return SecurityFilterProperties.getInstance((HttpServletRequest)this).getAuthenticationProvider().getRemoteAddr((HttpServletRequest)this, remoteIp);
        }
        return remoteIp;
    }
    
    public String getRemoteUserIPAddr() {
        String remoteUserIP = null;
        if (this.isInternalURL() || this.isProxyRequest()) {
            if (this.getHeader("Z-SIGNED_REMOTE_USER_IP") != null) {
                remoteUserIP = SecurityUtil.verifySignatureAndGetData(this.getHeader("Z-SIGNED_REMOTE_USER_IP"), this);
                if (remoteUserIP == null && this.isProxyRequest()) {
                    remoteUserIP = this.getHeader("REMOTE_USER_IP");
                }
            }
            else {
                remoteUserIP = this.getHeader("REMOTE_USER_IP");
            }
        }
        return (remoteUserIP != null) ? remoteUserIP : this.getRemoteAddr();
    }
    
    public ArrayList<String> getSecretParams() {
        return SecurityUtil.getSecretParamList(this);
    }
    
    public String getContextName() {
        return SecurityUtil.getContextName(this.webContext);
    }
    
    public String getRequestWebContext() {
        return this.webContext;
    }
    
    boolean isOptionsURL() {
        return "OPTIONS".equals(SecurityUtil.getRequestMethodForActionRuleLookup((HttpServletRequest)this));
    }
    
    void discardRequest() {
        this.discardRequest = true;
    }
    
    boolean isRequestDiscarded() {
        return this.discardRequest;
    }
    
    public RequestEntities getAppFirewallRequestEntities() {
        return SecurityFilterProperties.getInstance((HttpServletRequest)this).getRACProvider().getRACRequestEntities();
    }
    
    static void clearDefaultParamConfiguration() {
        SecurityRequestWrapper.defaultParams = new HashMap<String, ParameterRule>(10);
        SecurityRequestWrapper.defaultSecretParamsList = new ArrayList<String>();
    }
    
    void setInternalURL(final boolean internal) {
        this.internalURL = internal;
    }
    
    boolean isInternalURL() {
        return this.internalURL;
    }
    
    public Collection<Part> getParts() {
        if (this.paramToPartsMap == null) {
            return Collections.EMPTY_LIST;
        }
        return (this.cachedPartsList == null) ? (this.cachedPartsList = Collections.unmodifiableCollection((Collection<? extends Part>)this.getPartsAsUploadedFilePart())) : this.cachedPartsList;
    }
    
    public Part getPart(final String paramName) {
        if (this.paramToPartsMap == null) {
            return null;
        }
        final List<UploadedFilePart> list = this.paramToPartsMap.get(paramName);
        if (list != null) {
            return (Part)list.get(0);
        }
        return null;
    }
    
    public Collection<Part> getAllPartsOfParam(final String paramName) {
        if (this.paramToPartsMap == null) {
            return Collections.EMPTY_LIST;
        }
        return (Collection)this.paramToPartsMap.get(paramName);
    }
    
    public void setAttribute(final String name, final Object o) {
        if (name != null) {
            if (name.startsWith("javax.servlet.forward")) {
                ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.pushFwdAttrSetInRq(this.getRequestURI(), this.getURLActionRulePrefix(), this.getURLActionRulePath(), this.getURLActionRuleMethod(), this.getURLActionRuleOperation(), this.getServletPath(), this.getPathInfo(), name, (ExecutionTimer)null);
            }
            else if (name.startsWith("javax.servlet.include")) {
                ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.pushIncAttrSetInRq(this.getRequestURI(), this.getURLActionRulePrefix(), this.getURLActionRulePath(), this.getURLActionRuleMethod(), this.getURLActionRuleOperation(), this.getServletPath(), this.getPathInfo(), name, (ExecutionTimer)null);
            }
            else if (name.startsWith("javax.servlet.async")) {
                ZSEC_SERVLET_STD_ATTRIBUTE_MISUSE.pushAsyncAttrSetInRq(this.getRequestURI(), this.getURLActionRulePrefix(), this.getURLActionRulePath(), this.getURLActionRuleMethod(), this.getURLActionRuleOperation(), this.getServletPath(), this.getPathInfo(), name, (ExecutionTimer)null);
            }
        }
        super.setAttribute(name, o);
    }
    
    public RequestDispatcher getRequestDispatcher(final String path) {
        if (path != null) {
            final Pattern requestDispatchURIsPattern = this.actionRule.getRequestDispatchURIsPattern();
            if (requestDispatchURIsPattern == null) {
                if (!SecurityFrameworkUtil.isWAFInstrumentLoaded() && (path.contains("/../") || path.contains("\\..\\"))) {
                    this.pushPathTraversal(this.getRequestURI(), this.actionRule.getPrefix(), this.actionRule.getPath(), this.actionRule.getMethod(), this.actionRule.getOperationValue(), this.getServletPath(), this.getPathInfo(), null, path, "[PATH_PAYLOAD] field data contains [..]");
                }
            }
            else if (!requestDispatchURIsPattern.matcher(path).matches()) {
                if (SecurityFilterProperties.getInstance((HttpServletRequest)this).getReqDispValidationMode() == SecurityFilterProperties.DispatcherValidationMode.ENFORCEMENT) {
                    SecurityRequestWrapper.logger.log(Level.SEVERE, "Dispatch uri \"{0}\" does not match with configured uris pattern \"{1}\"", new Object[] { path, requestDispatchURIsPattern.toString() });
                    throw new IAMSecurityException("INVALID_REQUEST_DISPATCH_PATH_URI");
                }
                ZSEC_INVALID_DISPATCHER_URI.pushInfo(this.getRequestURI(), this.actionRule.getPrefix(), this.actionRule.getPath(), this.actionRule.getMethod(), this.actionRule.getOperationValue(), this.getServletPath(), this.getPathInfo(), path, requestDispatchURIsPattern.toString(), (ExecutionTimer)null);
            }
        }
        return super.getRequestDispatcher(path);
    }
    
    private void pushPathTraversal(final String rqUri, final String wcUriPrefix, final String wcUri, final String wcMethod, final String wcOperation, final String ssServletPath, final String ssPathInfo, final String rqDispatcherType, final String pathPayload, final String monitoringCondition) {
        final EventProcessor pathTraversalEvent = EventDataProcessor.getEventProcessor(EventFWConstants.TYPE.EVENT, "PATH_TRAVERSAL");
        final Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("RQ_URI", rqUri);
        dataMap.put("WC_URI_PREFIX", wcUriPrefix);
        dataMap.put("WC_URI", wcUri);
        dataMap.put("WC_METHOD", wcMethod);
        dataMap.put("WC_OPERATION", wcOperation);
        dataMap.put("SS_SERVLET_PATH", ssServletPath);
        dataMap.put("SS_PATH_INFO", ssPathInfo);
        dataMap.put("RQ_DISPATCHER_TYPE", rqDispatcherType);
        dataMap.put("PATH_PAYLOAD", pathPayload);
        dataMap.put("MONITORING_CONDITION", monitoringCondition);
        EventDataProcessor.pushData(pathTraversalEvent, (Map)dataMap, EventCallerInferrer.inferClass(pathTraversalEvent, "PATH_TRAVERSAL", "pushInfo"), (ExecutionTimer)null);
    }
    
    long getRequestSize() {
        if (SecurityUtil.isValid(this.getOriginalInputStreamContent())) {
            return this.getOriginalInputStreamContent().length();
        }
        if (this.getParameterMap().size() > 0) {
            return this.getRequestParamMapSize(this);
        }
        final int contentLength = this.getContentLength();
        if (contentLength != -1) {
            return contentLength;
        }
        final String contentLenHeader = this.getHeader("content-length");
        return SecurityUtil.getLong(contentLenHeader);
    }
    
    private long getRequestParamMapSize(final SecurityRequestWrapper req) {
        long size = 0L;
        final HashMap<String, String[]> map = (HashMap<String, String[]>)req.getParameterMap();
        for (final Map.Entry<String, String[]> entry : map.entrySet()) {
            final String paramName = entry.getKey();
            final String[] paramValues = entry.getValue();
            for (int i = 0; i < paramValues.length; ++i) {
                size += paramName.length() + 1;
                if (SecurityUtil.isValid(paramValues[i])) {
                    size += paramValues[i].length() + 1;
                }
            }
        }
        if (size > 0L) {
            --size;
        }
        final int queryStringLength = this.getQueryStringLength();
        if (queryStringLength > 0) {
            if (queryStringLength < size) {
                size -= queryStringLength + 1;
            }
            else {
                size -= queryStringLength;
            }
        }
        return size;
    }
    
    private int getQueryStringLength() {
        int queryStringLength = 0;
        final String queryString = this.getQueryString();
        if (SecurityUtil.isValid(queryString)) {
            final String[] split;
            final String[] params = split = queryString.split("&");
            for (final String param : split) {
                final String[] paramNameValuePair = param.split("=");
                final String queryParamName = SecurityUtil.decode(paramNameValuePair[0], this.characterEncoding);
                String queryParamValue = "";
                if (paramNameValuePair.length > 1) {
                    queryParamValue = SecurityUtil.decode(paramNameValuePair[1], this.characterEncoding);
                }
                if (queryParamName != null && queryParamValue != null) {
                    if ("".equals(queryParamValue)) {
                        queryStringLength += queryParamName.length() + 1;
                    }
                    else {
                        String[] paramValues = { queryParamValue };
                        final ParameterRule paramRule = this.getParameterRule(queryParamName);
                        if (paramRule != null && paramRule.isSplitDefined()) {
                            paramValues = paramRule.getSplitPattern().split(queryParamValue);
                        }
                        for (int i = 0; i < paramValues.length; ++i) {
                            queryStringLength += queryParamName.length() + 1;
                            queryStringLength += paramValues[i].length() + 1;
                        }
                    }
                }
            }
            if (queryStringLength > 0) {
                --queryStringLength;
            }
        }
        return queryStringLength;
    }
    
    private ParameterRule getParameterRule(final String paramName) {
        final ParameterRule paramRule = this.actionRule.getParameterRule(paramName);
        if (paramRule != null) {
            return paramRule;
        }
        return SecurityRequestWrapper.defaultParams.get(paramName);
    }
    
    void setImportedDataAsFile_RequestAttribute(final String importedDataAsFile, final UploadedFileItem fileItem) {
        if (this.importedDataAsFileList == null) {
            this.setAttribute("IMPORTED_DATA_AS_FILE", this.importedDataAsFileList = new ArrayList<UploadedFileItem>());
        }
        this.importedDataAsFileList.add(fileItem);
    }
    
    public StringBuffer getRequestURL() {
        if (this.forwardedHost != null) {
            final StringBuffer url = new StringBuffer();
            final String scheme = this.getScheme();
            int port = this.getServerPort();
            if (port < 0) {
                port = 80;
            }
            url.append(scheme);
            url.append("://");
            url.append(this.getServerName());
            if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
                url.append(':');
                url.append(port);
            }
            url.append(this.getRequestURI());
            return url;
        }
        return super.getRequestURL();
    }
    
    public String getHeader(final String headerName) {
        if ("Host".equalsIgnoreCase(headerName) && this.forwardedHost != null) {
            return this.forwardedHost;
        }
        return super.getHeader(headerName);
    }
    
    public String getServerName() {
        if (this.isProxyRequest()) {
            final String serverName = this.getHeader("ZSEC_PROXY_SERVER_NAME");
            if (SecurityUtil.isValid(serverName)) {
                if (!serverName.contains(":")) {
                    return serverName;
                }
                final int firstIndex = serverName.indexOf(":");
                final int lastIndex = serverName.lastIndexOf(":");
                if (firstIndex != lastIndex) {
                    SecurityRequestWrapper.logger.log(Level.WARNING, "More than one colon present in the Host header : {0}", serverName);
                    throw new IAMSecurityException("INVALID_PROXY_HOST_HEADER");
                }
                final String[] serverNameComps = serverName.trim().split(":");
                if (serverNameComps.length == 2) {
                    final String domain = serverNameComps[0];
                    final String port = serverNameComps[1];
                    if (SecurityUtil.isValid(port) && !SecurityRequestWrapper.DIGIT_PATTERN.matcher(port).matches()) {
                        SecurityRequestWrapper.logger.log(Level.WARNING, "Port is not a DIGIT Port : {0}, Host : {1}", new Object[] { port, serverName });
                        throw new IAMSecurityException("INVALID_PROXY_HOST_HEADER");
                    }
                    return domain;
                }
            }
        }
        return (this.forwardedHost != null) ? this.forwardedHost : super.getServerName();
    }
    
    public void setProxyRequest(final boolean isProxy) {
        this.isProxyRequest = isProxy;
    }
    
    public boolean isProxyRequest() {
        return this.isProxyRequest;
    }
    
    public void setIntegrationRequest(final boolean isIntegrationRequest) {
        this.isIntegrationRequest = isIntegrationRequest;
    }
    
    public boolean isIntegrationRequest() {
        return this.isIntegrationRequest;
    }
    
    public String getValidatedRequestPath() {
        return this.validatedRequestPath;
    }
    
    void cacheInputStream(final long maxSize, final boolean isBinaryType) {
        InputStream in;
        try {
            in = (InputStream)super.getInputStream();
        }
        catch (final IOException ioEx) {
            SecurityRequestWrapper.logger.log(Level.WARNING, "IOException while reading inputstream from request URI : {0}, {1}", new Object[] { this.getRequestURI(), ioEx.getMessage() });
            throw new IAMSecurityException("UNABLE_TO_READ_INPUTSTREAM", this.getRequestURI(), this.getRemoteAddr(), this.getHeader("Referer"));
        }
        if (isBinaryType) {
            this.originalStreamAsByteArray = SecurityUtil.convertInputStreamAsByteArray(in, maxSize * 1024L);
        }
        else {
            this.originalStreamContent = SecurityUtil.convertInputStreamAsString(in, maxSize);
        }
    }
    
    public ServletInputStream getInputStream() throws IOException {
        if (this.locked) {
            return super.getInputStream();
        }
        if ((this.actionRule != null && !this.actionRule.parseRequestBody()) || this.isMultipartRequest) {
            return super.getInputStream();
        }
        final ParameterRule inputStreamRule = SecurityUtil.getInputStreamRule((HttpServletRequest)this, this.actionRule);
        if (inputStreamRule != null) {
            if (inputStreamRule.isImportFile()) {
                throw new UnsupportedOperationException("Unsupported access for file import through request body. To access Use getStreamContentAsFile()");
            }
            if (inputStreamRule.isInputStreamTypeBinary()) {
                return new CachedServletInputStream(this.originalStreamAsByteArray);
            }
        }
        final String streamContent = this.getValidatedStreamContent(inputStreamRule);
        if (streamContent == null) {
            ZSEC_UNVALIDATED_BODY.pushInfo(this.getRequestURI(), this.actionRule.getPrefix(), this.actionRule.getPath(), this.actionRule.getMethod(), this.actionRule.getOperationValue(), (ExecutionTimer)null);
            return super.getInputStream();
        }
        return new CachedServletInputStream(streamContent, this.getCharacterEncoding());
    }
    
    public BufferedReader getReader() throws IOException {
        if (this.locked) {
            return super.getReader();
        }
        if ((this.actionRule != null && !this.actionRule.parseRequestBody()) || this.isMultipartRequest) {
            return new BufferedReader(new InputStreamReader((InputStream)super.getInputStream(), "UTF-8"));
        }
        final ParameterRule inputStreamRule = SecurityUtil.getInputStreamRule((HttpServletRequest)this, this.actionRule);
        if (inputStreamRule != null) {
            if (inputStreamRule.isImportFile()) {
                throw new UnsupportedOperationException("Unsupported access for file import via request body. To access Use getStreamContentAsFile()");
            }
            if (inputStreamRule.isInputStreamTypeBinary()) {
                return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.originalStreamAsByteArray)));
            }
        }
        final String streamContent = this.getValidatedStreamContent(inputStreamRule);
        if (streamContent == null) {
            ZSEC_UNVALIDATED_BODY.pushInfo(this.getRequestURI(), this.actionRule.getPrefix(), this.actionRule.getPath(), this.actionRule.getMethod(), this.actionRule.getOperationValue(), (ExecutionTimer)null);
            return super.getReader();
        }
        return new BufferedReader(new StringReader(streamContent));
    }
    
    private String getValidatedStreamContent(final ParameterRule inputStreamRule) {
        if (inputStreamRule != null) {
            final String streamContent = this.getFilteredStreamContent();
            return (streamContent != null) ? streamContent : "";
        }
        if (SecurityFilterProperties.getInstance((HttpServletRequest)this).isInputStreamValidationErrorMode()) {
            SecurityRequestWrapper.logger.log(Level.SEVERE, "INPUTSTREAM VALIDATION LOG :: INPUTSTREAM RULE NOT CONFIGURED for the URL : {0}", this.getRequestURI());
            return "";
        }
        return null;
    }
    
    void storeHeader(final String headerName, final String headerValue, final HeaderRule headerRule) {
        List<HeaderInfo> values = this.headerValues.get(headerName);
        if (values == null) {
            values = new ArrayList<HeaderInfo>();
            this.headerValues.put(headerName, values);
        }
        values.add(new HeaderInfo(headerName, headerValue, headerRule));
    }
    
    public Map<String, Object> toMap() {
        if (this.requestInfo != null) {
            return this.requestInfo;
        }
        (this.requestInfo = new HashMap<String, Object>()).put("URI", this.getRequestURI());
        this.requestInfo.put("REMOTE_IP", this.getRemoteAddr());
        this.requestInfo.put("METHOD", super.getMethod());
        this.requestInfo.put("REQ_CONTENT_TYPE", this.getContentType());
        this.requestInfo.put("USER_AGENT", this.userAgentString);
        if (this.referer != null) {
            this.requestInfo.put("REFERER", this.referer);
        }
        if (this.actionRule != null) {
            this.requestInfo.put("ACTIONRULE", this.actionRule.getPath());
        }
        int size = this.validatedParamValues.size();
        if (size > 0 && this.validatedParamValues.containsKey("zoho-inputstream")) {
            --size;
        }
        if (size > 0) {
            final List<HashMap<String, Object>> values = new ArrayList<HashMap<String, Object>>();
            for (final ParamInfo paramInfo : this.validatedParamValues.values()) {
                final HashMap<String, Object> map = (HashMap<String, Object>)paramInfo.toMap();
                if (map != null) {
                    values.add(map);
                }
            }
            this.requestInfo.put(InfoFields.ACCESSLOGFIELDS.PARAM.getValue(), values);
            this.requestInfo.put(InfoFields.ACCESSLOGFIELDS.PARAM_CNT.getValue(), this.paramCounter);
        }
        if (this.multiPartValues != null && this.multiPartValues.size() > 0) {
            final List<HashMap<String, Object>> multipartList = new ArrayList<HashMap<String, Object>>();
            for (final UploadedFileItem fileitem : this.multiPartValues) {
                final HashMap<String, Object> map = (HashMap)fileitem.toMap();
                if (map != null) {
                    multipartList.add(map);
                }
            }
            this.requestInfo.put(InfoFields.ACCESSLOGFIELDS.FILE.getValue(), multipartList);
            this.requestInfo.put(InfoFields.ACCESSLOGFIELDS.FILE_CNT.getValue(), this.multiPartValues.size());
            this.requestInfo.put(InfoFields.ACCESSLOGFIELDS.DETECTOR.getValue(), SecurityFilterProperties.getInstance((HttpServletRequest)this).getContentTypeDetectOption());
        }
        if (SecurityFilterProperties.getInstance((HttpServletRequest)this).isDevelopmentMode() && this.validatedParamValues.containsKey("zoho-inputstream")) {
            final ParamInfo info = this.validatedParamValues.get("zoho-inputstream");
            if (!info.isSecret()) {
                this.requestInfo.put("INPUTSTREAM", info.getValue());
            }
        }
        this.requestInfo.put("REQ_HEADER", this.headerValues.keySet());
        this.requestInfo.put(InfoFields.ACCESSLOGFIELDS.REQ_HEADER_CNT.getValue(), this.headerValues.size());
        final Cookie[] cookie = this.getCookies();
        if (cookie != null && cookie.length > 0) {
            this.requestInfo.put(InfoFields.ACCESSLOGFIELDS.COOKIE_CNT.getValue(), cookie.length);
        }
        if (this.queryParamCounter > 0) {
            this.requestInfo.put(InfoFields.ACCESSLOGFIELDS.QP_CNT.getValue(), this.queryParamCounter);
        }
        if (this.extraParamCounter > 0) {
            this.requestInfo.put(InfoFields.ACCESSLOGFIELDS.EP_CNT.getValue(), this.extraParamCounter);
        }
        final List<String> errorCodes = new ArrayList<String>();
        if (this.iamSecurityException != null) {
            errorCodes.addAll(this.iamSecurityException.getErrorCodes());
        }
        if (this.multiPartValues != null) {
            for (final UploadedFileItem fileItem : this.multiPartValues) {
                if (fileItem.getException() != null) {
                    errorCodes.addAll(fileItem.getException().getErrorCodes());
                }
            }
        }
        if (!errorCodes.isEmpty()) {
            this.requestInfo.put(InfoFields.ACCESSLOGFIELDS.ERROR_CODE.getValue(), errorCodes);
        }
        return this.requestInfo;
    }
    
    public String getCSPNonce() {
        if (this.cspNonce == null) {
            final SecureRandom random = new SecureRandom();
            final byte[] bytes = new byte[16];
            random.nextBytes(bytes);
            this.cspNonce = Base64.encodeBase64String(bytes);
        }
        return this.cspNonce;
    }
    
    public String getMethod() {
        return this.method;
    }
    
    String getActualRequestMethod() {
        return super.getMethod();
    }
    
    public String getRequestURI() {
        if (!this.enableURINormalization) {
            return super.getRequestURI();
        }
        if (DispatcherType.REQUEST.equals((Object)this.getDispatcherType())) {
            if (this.normalizedRequestURI == null) {
                this.normalizedRequestURI = SecurityUtil.getNormalizedURI(super.getRequestURI());
            }
            return this.normalizedRequestURI;
        }
        return SecurityUtil.getNormalizedURI(super.getRequestURI());
    }
    
    public String getOriginalRequestURI() {
        return super.getRequestURI();
    }
    
    void setStreamContentAsFile(final UploadedFileItem fileItem) {
        this.setAttribute("STREAM_CONTENT_AS_FILE", fileItem);
        this.streamContentAsFile = fileItem;
    }
    
    public UploadedFileItem getStreamContentAsFile() {
        return this.streamContentAsFile;
    }
    
    public void setProxyInfo(final ProxyInfo proxyInfo) {
        this.proxyInfo = proxyInfo;
    }
    
    public ProxyInfo getProxyInfo() {
        return this.proxyInfo;
    }
    
    public void setThrottledFlagAsTrue() {
        this.isThrottled = true;
    }
    
    public boolean isThrottled() {
        return this.isThrottled;
    }
    
    void setIAMSecurityException(final IAMSecurityException ex) {
        this.iamSecurityException = ex;
    }
    
    static {
        DIGIT_PATTERN = Pattern.compile("[0-9]{0,5}");
        SecurityRequestWrapper.defaultParams = new HashMap<String, ParameterRule>(10);
        SecurityRequestWrapper.defaultSecretParamsList = new ArrayList<String>();
        logger = Logger.getLogger(SecurityRequestWrapper.class.getName());
    }
    
    public enum ZsecAttributes
    {
        urlrule, 
        live_window_throttles_key_map;
    }
    
    class FileUploadTracker
    {
        UploadFileRule fileRule;
        boolean lowerLimitSatisfied;
        List<UploadedFileItem> fileItemsBelowLowerLimit;
        int uploadCounter;
        
        FileUploadTracker(final UploadFileRule fileRule) {
            this.fileRule = null;
            this.lowerLimitSatisfied = false;
            this.fileItemsBelowLowerLimit = null;
            this.uploadCounter = 0;
            this.fileRule = fileRule;
            this.fileItemsBelowLowerLimit = new ArrayList<UploadedFileItem>();
        }
        
        void updateLowerLimitAchieved() {
            this.lowerLimitSatisfied = true;
            this.fileItemsBelowLowerLimit = null;
        }
        
        void addFileItem(final UploadedFileItem fileItem) {
            if (this.fileItemsBelowLowerLimit != null) {
                this.fileItemsBelowLowerLimit.add(fileItem);
            }
        }
    }
    
    static class SimplePrincipal implements Principal
    {
        String name;
        
        public SimplePrincipal(final String name) {
            this.name = "";
            this.name = name;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
    }
}
