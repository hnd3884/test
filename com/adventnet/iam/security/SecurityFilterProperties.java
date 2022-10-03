package com.adventnet.iam.security;

import java.util.Hashtable;
import com.zoho.security.agent.AppSenseConstants;
import com.zoho.security.appfirewall.AppFirewallPolicyLoader;
import com.zoho.security.zsecpiidetector.PIIDetectorFactory;
import ua_parser.Parser;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import org.w3c.dom.Element;
import javax.servlet.http.HttpServletRequest;
import com.zoho.security.cache.CacheConstants;
import com.zoho.security.cache.RedisCacheAPI;
import java.util.AbstractMap;
import com.zoho.security.wafad.WAFAttackDiscoveryMetricRecorder;
import com.zoho.security.wafad.WAFAttackDiscovery;
import com.zoho.security.wafad.AttackDiscoveryProvider;
import com.zoho.security.eventfwimpl.ZSecZohoLogsImplProvider;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import com.adventnet.iam.security.antivirus.clamav.CLAMAVConfiguration;
import com.zoho.security.wafad.WAFAttackDiscoveryUtil;
import com.zoho.security.eventfw.EventDataProcessor;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import com.zoho.conf.Configuration;
import com.zoho.security.agent.AppSenseAgent;
import com.zoho.security.agent.Components;
import java.util.Iterator;
import java.util.logging.Level;
import javax.servlet.FilterConfig;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;
import com.zoho.security.zsecpiidetector.detector.PIIDetector;
import com.zoho.security.validator.zip.ZipSanitizerRule;
import com.zoho.security.cache.CacheConfiguration;
import com.zoho.security.SFCorePlugin;
import java.net.Proxy;
import com.zoho.security.rule.ExceptionRule;
import com.zoho.security.api.wrapper.PatternMatcherWrapper;
import java.util.HashMap;
import java.util.Collection;
import javax.xml.validation.Schema;
import java.security.PrivateKey;
import org.apache.commons.codec.binary.Hex;
import com.adventnet.iam.security.antivirus.VendorAVProvider;
import com.adventnet.iam.security.antivirus.AntivirusConfiguration;
import java.util.logging.Logger;
import java.util.Properties;
import java.io.File;
import com.zoho.security.validator.url.URLValidatorRule;
import java.util.List;
import com.adventnet.iam.xss.XSSUtil;
import java.util.regex.Pattern;
import java.util.Map;

public final class SecurityFilterProperties
{
    public static final Map<String, SecurityFilterProperties> FILTER_INSTANCES;
    private static final Pattern MAIN_ZOHO_DOMAINS;
    private Map<String, URLRule> urlRuleMap;
    private Map<Pattern, URLRule> urlPatternRuleMap;
    private Map<String, URLRule> allUrlRules;
    private Map<String, XSSUtil> xssUtilMap;
    private Map<String, RegexRule> regexRuleMap;
    private static Map<String, RegexRule> commonRegexRuleMap;
    private Map<String, Pattern> contentTypes;
    private Map<String, ContentTypeRule> contentTypeRuleMap;
    private Map<String, String> contentTypesXSS;
    private Map<String, ProxyURL> proxyURLs;
    private Map<Pattern, ProxyURL> proxyURLPatterns;
    private List<ProxyURL> allProxyURLRules;
    private String csrf_cookie_name;
    private String csrf_cookie_value_regex;
    private Map<String, JSONTemplateRule> jsonTemplateRuleMap;
    private Map<String, TemplateRule> templateRuleMap;
    private Map<String, URLValidatorRule> urlValidatorRuleMap;
    private String csrf_param_name;
    private String csrf_header_name;
    private String csrf_header_value_pattern;
    private String[] filePaths;
    private List<File> securityFiles;
    private static String contextName;
    private boolean developmentMode;
    private boolean testMode;
    private String excludeURLs;
    private long lastUpdatedTime;
    private String showCaptchaURL;
    private Authenticator authProvider;
    private SecurityProvider provider;
    private RACProvider racProvider;
    private static boolean useIAM;
    private boolean enableTimeoutMatcher;
    private boolean enableXSSTimeoutMatcher;
    private static Pattern trustedIPPattern;
    private Properties properties;
    public static final Logger logger;
    private static String proxyHost;
    private static String proxyPort;
    private static String proxyUserName;
    private static String proxyPassword;
    private String httpIPBlackListDNS;
    private String httpIPWhiteListDNS;
    private ParameterRule extraParamRule;
    private ParameterRule extraJSONKeyRule;
    private boolean allowEmptyValue;
    private boolean allowInvalidValue;
    private boolean setCSRFCookie;
    private String responseEncoding;
    private String requestEncoding;
    private boolean ignoreTrailingSlash;
    private List<String> ignoreURIPrefixList;
    private boolean enableXSSFilterLog;
    private int maxLogSize;
    private boolean enableXSSPatternDetect;
    private boolean disableXSSPatternDetectForFilter;
    private AntivirusConfiguration avConfig;
    private VendorAVProvider vendorAvProvider;
    private boolean disableAPIServiceTicket;
    private boolean isTrustedScriptEnabled;
    private boolean disableParameterValidationForTestingOutputEncoding;
    private int uploadFileRuleReadTimeOut;
    private int uploadFileRuleConnectionTimeOut;
    private Hex hexCodec;
    private static PrivateKey iscPrivateKey;
    private long iscSignatureExpiryTime;
    private boolean enableReadOnlyMode;
    private boolean enableResponseSendError;
    private static List<String> requestMethod;
    private boolean disableAuthCSRF;
    private boolean enableCSRFMigration;
    private static String serviceName;
    private boolean enableRequestOptions;
    ActionRule.CORSConfigType corsConfigType;
    public String racMode;
    private boolean maskSecretParamLogging;
    private static int proxyConnectTimeOut;
    private static int proxyReadTimeOut;
    private Map<String, Integer> allowedUserAgentVersions;
    private boolean disableGetApiCSRFCheck;
    private Map<String, List<ParameterRule>> paramGroupRuleMap;
    private Map<String, List<ParameterRule>> jsonKeyGroupRuleMap;
    private Map<String, List<OrCriteriaRule>> paramGroupCriteriaRuleMap;
    private Map<String, List<OrCriteriaRule>> jsonKeyGroupCriteriaRuleMap;
    private Map<String, List<ResponseHeaderRule>> safeResponseHeadersMap;
    private List<ResponseHeaderRule> defaultResponseHeadersList;
    private List<String> defaultDisableSafeHeaders;
    private boolean enableXFrameOptions;
    private String defaultXFrameOption;
    private Map<String, HeaderRule> defaultRequestHeadersRuleWithStrictName;
    private Map<String, HeaderRule> defaultRequestHeadersRuleWithRegexName;
    private List<String> defaultSecretRequestHeaderNames;
    private CookieRequestHeaderRule defaultCookieRule;
    private Map<String, HeaderRule> internalRequestHeadersWithStrictName;
    private CookieRequestHeaderRule internalCookieRule;
    private List<String> internalSecretRequestHeaderNames;
    private List<ParameterRule> partialMaskingInternalReqHeaderRules;
    private RequestHeaderValidationMode reqHeaderValidationMode;
    private RequestHeaderValidationMode internalReqHeaderValidationMode;
    private UserAgentRequestHeaderRule defaultUserAgentRule;
    private String contentTypeDetection;
    private boolean enableTikaFileContentAndNameBasedDetection;
    private boolean isAppFirewallEnabled;
    private boolean isAppFirewallFileBasedLoadingEnabled;
    private Map<String, Schema> xmlSchemaMap;
    private Map<String, Collection<XSDElementRule>> xmlSchemaFilterElementsMap;
    private boolean disablePutMinoccurCheck;
    private Map<String, XMLSchemaRule> xmlSchemaRuleMap;
    private boolean logResponse;
    private Pattern excludedURLsInLogResponse;
    private String labelStr;
    private HashMap<String, String> defaultLabelMap;
    private boolean disablePathParamURIDecodingCheck;
    private boolean enableXHTTPMethodOverrideOption;
    private int[] captchaImageDimensions;
    private boolean pushError;
    private long accessInfoExpiryScheduleInterval;
    private boolean startLiveWindowCleanerScheduler;
    private boolean blockRequestOnDosCacheException;
    private boolean handleErrorPageHip;
    private boolean handleErrorPageJsonResponse;
    private boolean disableParamInputValidationForTestingOutputEncoding;
    private static int patternMatcherTimeOutInMillis;
    private static int patternMatcherMaxIterationCount;
    private static PatternMatcherWrapper.PatternMatcherMode patternMatcherMode;
    private float xmlSchemaVersion;
    private boolean xsd11CtaFullXpathChecking;
    private DispatcherValidationMode reqDispValidationMode;
    private static boolean usingIAMImpl;
    private boolean addURLParamFrameoriginToRedirectURL;
    private boolean followServletStdForUrlPath;
    private final TempFileUploadDirMonitoring tempFileUploadDirMonitoring;
    private Map<String, ExceptionRule> exceptionRuleMap;
    boolean isThrowExForInternalValidation;
    private static Proxy proxy;
    private boolean enablePatchMethodMinOccurCheck;
    private List<String> blockedMethods;
    List<String> allowedServicesViaProxy;
    private boolean proxyURLValidationStatus;
    private String loginPage;
    private boolean enableAppSense;
    private static List<String> defaultConfigFiles;
    private static List<String> permanentConfigFiles;
    private List<SFCorePlugin> plugins;
    private Map<String, List<CacheConfiguration>> cacheConfigurationMap;
    private CacheConfiguration captchaCache;
    private boolean disableServiceURLThrottles;
    private boolean convertServiceUrlRollingThrottlesIntoAppserver;
    private boolean dbCacheForServiceScopeThrottles;
    private boolean isSetAPIRateLimitResponseHeader;
    private boolean disableOldThrottleConfig;
    private boolean allowImportURLRedirect;
    private List<JSON_INVALID_VALUE_TYPE> json_Invalid_Values_List;
    private boolean ignoreExtraParam;
    private boolean enableSecretParamLoggingMask;
    private boolean enableHSTS;
    private boolean enableHSTSRedirection;
    private Object sasConfigProviderClass;
    private boolean allowProxyURLRedirect;
    private int urlRedirectMaxLimit;
    private boolean enablePostMethodRedirect;
    private String[] hstsAlloweddomains;
    private List<String> secretRequestParamNames;
    private static List<String> defaultsecretRequestHeaderNamesList;
    private List<String> secretRequestHeaderNames;
    private List<String> secretResponseHeaderNames;
    private boolean logInputStream;
    private boolean maskExtraParam;
    private boolean maskIgnoreExtraParam;
    private int dynamicParamsMaxOccurrenceLimit;
    private boolean isEnforcementMode;
    private boolean isLearningMode;
    private Map<String, ZipSanitizerRule> zipSanitizerRuleMap;
    private InputStreamValidationMode inputStreamValidationMode;
    protected Map<ThrottlesRule.Windows, List<ThrottlesRule>> commonThrottlesRuleMap;
    private boolean trimEnabled;
    private boolean maskAllParamValuesOnError;
    private boolean enableIndividualOccurrenceCheckForDynamicParams;
    private boolean enablePiiDetector;
    private PIIDetectorRule piiDetectorRule;
    private static PIIDetector piiDetector;
    private static PrivateKey interDCPrivateKey;
    private static final String CURRENT_DC_LOCATION = "current.dc.location";
    static String currentDCLocation;
    private static boolean disableRedisInstrumentation;
    private ErrorPageValidationMode errorPageValidationMode;
    private HttpCookie.SAMESITE csrfCookieSamesiteMode;
    private boolean enableCSRFSamesiteStrictTmpcookie;
    private Set<String> requiredCachePoolNames;
    static final String WAF = "WAF";
    private boolean allowCSRFParamInQS;
    private boolean enableRequestURINormalization;
    private boolean allowDotDotSlashInReqURI;
    private boolean enableHostOverride;
    private String forwardedHostHeaderName;
    private List<String> allowedForwardedHosts;
    private static final String XFRAME_HEADER_NAME = "X-Frame-Options";
    
    public SecurityFilterProperties() {
        this.urlRuleMap = new HashMap<String, URLRule>();
        this.urlPatternRuleMap = new LinkedHashMap<Pattern, URLRule>();
        this.allUrlRules = new HashMap<String, URLRule>();
        this.xssUtilMap = new HashMap<String, XSSUtil>();
        this.regexRuleMap = new HashMap<String, RegexRule>();
        this.contentTypes = new HashMap<String, Pattern>();
        this.contentTypeRuleMap = new HashMap<String, ContentTypeRule>();
        this.contentTypesXSS = new HashMap<String, String>();
        this.proxyURLs = new HashMap<String, ProxyURL>();
        this.proxyURLPatterns = new LinkedHashMap<Pattern, ProxyURL>();
        this.allProxyURLRules = new ArrayList<ProxyURL>();
        this.csrf_cookie_name = null;
        this.csrf_cookie_value_regex = "csrf_cookie_value";
        this.jsonTemplateRuleMap = new HashMap<String, JSONTemplateRule>();
        this.templateRuleMap = new HashMap<String, TemplateRule>();
        this.urlValidatorRuleMap = new HashMap<String, URLValidatorRule>();
        this.csrf_param_name = null;
        this.csrf_header_name = "x-zcsrf-token";
        this.csrf_header_value_pattern = "CSRF_HEADER_VALUE_PATTERN";
        this.filePaths = null;
        this.securityFiles = new ArrayList<File>();
        this.developmentMode = false;
        this.testMode = false;
        this.excludeURLs = null;
        this.lastUpdatedTime = -1L;
        this.showCaptchaURL = "/showcaptcha";
        this.authProvider = null;
        this.provider = null;
        this.racProvider = null;
        this.enableTimeoutMatcher = false;
        this.enableXSSTimeoutMatcher = false;
        this.properties = new Properties();
        this.httpIPBlackListDNS = null;
        this.httpIPWhiteListDNS = null;
        this.extraParamRule = null;
        this.extraJSONKeyRule = null;
        this.allowEmptyValue = true;
        this.allowInvalidValue = true;
        this.setCSRFCookie = true;
        this.responseEncoding = null;
        this.requestEncoding = "UTF-8";
        this.ignoreTrailingSlash = false;
        this.ignoreURIPrefixList = null;
        this.enableXSSFilterLog = false;
        this.maxLogSize = -1;
        this.enableXSSPatternDetect = false;
        this.disableXSSPatternDetectForFilter = false;
        this.avConfig = null;
        this.vendorAvProvider = null;
        this.disableAPIServiceTicket = false;
        this.isTrustedScriptEnabled = false;
        this.disableParameterValidationForTestingOutputEncoding = false;
        this.uploadFileRuleReadTimeOut = 10000;
        this.uploadFileRuleConnectionTimeOut = 10000;
        this.hexCodec = new Hex();
        this.iscSignatureExpiryTime = 600000L;
        this.enableReadOnlyMode = false;
        this.enableResponseSendError = true;
        this.disableAuthCSRF = true;
        this.enableCSRFMigration = false;
        this.enableRequestOptions = false;
        this.corsConfigType = ActionRule.CORSConfigType.NONE;
        this.racMode = "none";
        this.maskSecretParamLogging = true;
        this.allowedUserAgentVersions = null;
        this.disableGetApiCSRFCheck = false;
        this.paramGroupRuleMap = new HashMap<String, List<ParameterRule>>();
        this.jsonKeyGroupRuleMap = new HashMap<String, List<ParameterRule>>();
        this.safeResponseHeadersMap = new HashMap<String, List<ResponseHeaderRule>>();
        this.defaultResponseHeadersList = new ArrayList<ResponseHeaderRule>();
        this.defaultDisableSafeHeaders = new ArrayList<String>();
        this.enableXFrameOptions = true;
        this.defaultXFrameOption = null;
        this.defaultRequestHeadersRuleWithStrictName = new HashMap<String, HeaderRule>();
        this.defaultRequestHeadersRuleWithRegexName = new HashMap<String, HeaderRule>();
        this.defaultSecretRequestHeaderNames = new ArrayList<String>();
        this.internalRequestHeadersWithStrictName = new HashMap<String, HeaderRule>();
        this.internalSecretRequestHeaderNames = new ArrayList<String>();
        this.partialMaskingInternalReqHeaderRules = new ArrayList<ParameterRule>();
        this.reqHeaderValidationMode = RequestHeaderValidationMode.DISABLE;
        this.internalReqHeaderValidationMode = RequestHeaderValidationMode.LOGGING;
        this.defaultUserAgentRule = null;
        this.contentTypeDetection = "mimeutil";
        this.xmlSchemaMap = new HashMap<String, Schema>();
        this.xmlSchemaFilterElementsMap = new HashMap<String, Collection<XSDElementRule>>();
        this.disablePutMinoccurCheck = false;
        this.xmlSchemaRuleMap = null;
        this.logResponse = false;
        this.excludedURLsInLogResponse = null;
        this.labelStr = null;
        this.defaultLabelMap = null;
        this.disablePathParamURIDecodingCheck = false;
        this.enableXHTTPMethodOverrideOption = false;
        this.pushError = false;
        this.accessInfoExpiryScheduleInterval = -1L;
        this.handleErrorPageHip = false;
        this.handleErrorPageJsonResponse = false;
        this.disableParamInputValidationForTestingOutputEncoding = false;
        this.xmlSchemaVersion = 1.0f;
        this.xsd11CtaFullXpathChecking = false;
        this.reqDispValidationMode = DispatcherValidationMode.LEARNING;
        this.addURLParamFrameoriginToRedirectURL = false;
        this.followServletStdForUrlPath = true;
        this.tempFileUploadDirMonitoring = new TempFileUploadDirMonitoring(true, 2147483648L, 900000L);
        this.exceptionRuleMap = new HashMap<String, ExceptionRule>();
        this.isThrowExForInternalValidation = (RequestHeaderValidationMode.ENFORCEMENT == this.internalReqHeaderValidationMode || RequestHeaderValidationMode.LEARNING == this.internalReqHeaderValidationMode);
        this.enablePatchMethodMinOccurCheck = true;
        this.blockedMethods = new ArrayList<String>(Arrays.asList("CONNECT", "TRACE"));
        this.allowedServicesViaProxy = null;
        this.proxyURLValidationStatus = true;
        this.loginPage = null;
        this.enableAppSense = true;
        this.plugins = null;
        this.cacheConfigurationMap = null;
        this.isSetAPIRateLimitResponseHeader = false;
        this.allowImportURLRedirect = false;
        this.json_Invalid_Values_List = Arrays.asList(JSON_INVALID_VALUE_TYPE.EMPTYSTRING, JSON_INVALID_VALUE_TYPE.JSONNULL);
        this.ignoreExtraParam = false;
        this.enableSecretParamLoggingMask = false;
        this.enableHSTS = false;
        this.enableHSTSRedirection = false;
        this.sasConfigProviderClass = null;
        this.allowProxyURLRedirect = true;
        this.urlRedirectMaxLimit = 5;
        this.enablePostMethodRedirect = false;
        this.hstsAlloweddomains = null;
        this.secretRequestParamNames = null;
        this.secretRequestHeaderNames = new CopyOnWriteArrayList<String>(SecurityFilterProperties.defaultsecretRequestHeaderNamesList);
        this.secretResponseHeaderNames = null;
        this.logInputStream = false;
        this.maskExtraParam = false;
        this.maskIgnoreExtraParam = false;
        this.dynamicParamsMaxOccurrenceLimit = 5;
        this.isEnforcementMode = false;
        this.isLearningMode = false;
        this.zipSanitizerRuleMap = new HashMap<String, ZipSanitizerRule>();
        this.commonThrottlesRuleMap = null;
        this.trimEnabled = false;
        this.maskAllParamValuesOnError = false;
        this.enableIndividualOccurrenceCheckForDynamicParams = false;
        this.enablePiiDetector = false;
        this.piiDetectorRule = null;
        this.errorPageValidationMode = ErrorPageValidationMode.LOGGING;
        this.csrfCookieSamesiteMode = HttpCookie.SAMESITE.NONE;
        this.enableCSRFSamesiteStrictTmpcookie = true;
        this.allowCSRFParamInQS = false;
        this.enableRequestURINormalization = false;
        this.allowDotDotSlashInReqURI = false;
        this.enableHostOverride = false;
        this.forwardedHostHeaderName = "LB_FORWARDED_HOST";
    }
    
    public SecurityFilterProperties(final FilterConfig filterConfig) throws Exception {
        this.urlRuleMap = new HashMap<String, URLRule>();
        this.urlPatternRuleMap = new LinkedHashMap<Pattern, URLRule>();
        this.allUrlRules = new HashMap<String, URLRule>();
        this.xssUtilMap = new HashMap<String, XSSUtil>();
        this.regexRuleMap = new HashMap<String, RegexRule>();
        this.contentTypes = new HashMap<String, Pattern>();
        this.contentTypeRuleMap = new HashMap<String, ContentTypeRule>();
        this.contentTypesXSS = new HashMap<String, String>();
        this.proxyURLs = new HashMap<String, ProxyURL>();
        this.proxyURLPatterns = new LinkedHashMap<Pattern, ProxyURL>();
        this.allProxyURLRules = new ArrayList<ProxyURL>();
        this.csrf_cookie_name = null;
        this.csrf_cookie_value_regex = "csrf_cookie_value";
        this.jsonTemplateRuleMap = new HashMap<String, JSONTemplateRule>();
        this.templateRuleMap = new HashMap<String, TemplateRule>();
        this.urlValidatorRuleMap = new HashMap<String, URLValidatorRule>();
        this.csrf_param_name = null;
        this.csrf_header_name = "x-zcsrf-token";
        this.csrf_header_value_pattern = "CSRF_HEADER_VALUE_PATTERN";
        this.filePaths = null;
        this.securityFiles = new ArrayList<File>();
        this.developmentMode = false;
        this.testMode = false;
        this.excludeURLs = null;
        this.lastUpdatedTime = -1L;
        this.showCaptchaURL = "/showcaptcha";
        this.authProvider = null;
        this.provider = null;
        this.racProvider = null;
        this.enableTimeoutMatcher = false;
        this.enableXSSTimeoutMatcher = false;
        this.properties = new Properties();
        this.httpIPBlackListDNS = null;
        this.httpIPWhiteListDNS = null;
        this.extraParamRule = null;
        this.extraJSONKeyRule = null;
        this.allowEmptyValue = true;
        this.allowInvalidValue = true;
        this.setCSRFCookie = true;
        this.responseEncoding = null;
        this.requestEncoding = "UTF-8";
        this.ignoreTrailingSlash = false;
        this.ignoreURIPrefixList = null;
        this.enableXSSFilterLog = false;
        this.maxLogSize = -1;
        this.enableXSSPatternDetect = false;
        this.disableXSSPatternDetectForFilter = false;
        this.avConfig = null;
        this.vendorAvProvider = null;
        this.disableAPIServiceTicket = false;
        this.isTrustedScriptEnabled = false;
        this.disableParameterValidationForTestingOutputEncoding = false;
        this.uploadFileRuleReadTimeOut = 10000;
        this.uploadFileRuleConnectionTimeOut = 10000;
        this.hexCodec = new Hex();
        this.iscSignatureExpiryTime = 600000L;
        this.enableReadOnlyMode = false;
        this.enableResponseSendError = true;
        this.disableAuthCSRF = true;
        this.enableCSRFMigration = false;
        this.enableRequestOptions = false;
        this.corsConfigType = ActionRule.CORSConfigType.NONE;
        this.racMode = "none";
        this.maskSecretParamLogging = true;
        this.allowedUserAgentVersions = null;
        this.disableGetApiCSRFCheck = false;
        this.paramGroupRuleMap = new HashMap<String, List<ParameterRule>>();
        this.jsonKeyGroupRuleMap = new HashMap<String, List<ParameterRule>>();
        this.safeResponseHeadersMap = new HashMap<String, List<ResponseHeaderRule>>();
        this.defaultResponseHeadersList = new ArrayList<ResponseHeaderRule>();
        this.defaultDisableSafeHeaders = new ArrayList<String>();
        this.enableXFrameOptions = true;
        this.defaultXFrameOption = null;
        this.defaultRequestHeadersRuleWithStrictName = new HashMap<String, HeaderRule>();
        this.defaultRequestHeadersRuleWithRegexName = new HashMap<String, HeaderRule>();
        this.defaultSecretRequestHeaderNames = new ArrayList<String>();
        this.internalRequestHeadersWithStrictName = new HashMap<String, HeaderRule>();
        this.internalSecretRequestHeaderNames = new ArrayList<String>();
        this.partialMaskingInternalReqHeaderRules = new ArrayList<ParameterRule>();
        this.reqHeaderValidationMode = RequestHeaderValidationMode.DISABLE;
        this.internalReqHeaderValidationMode = RequestHeaderValidationMode.LOGGING;
        this.defaultUserAgentRule = null;
        this.contentTypeDetection = "mimeutil";
        this.xmlSchemaMap = new HashMap<String, Schema>();
        this.xmlSchemaFilterElementsMap = new HashMap<String, Collection<XSDElementRule>>();
        this.disablePutMinoccurCheck = false;
        this.xmlSchemaRuleMap = null;
        this.logResponse = false;
        this.excludedURLsInLogResponse = null;
        this.labelStr = null;
        this.defaultLabelMap = null;
        this.disablePathParamURIDecodingCheck = false;
        this.enableXHTTPMethodOverrideOption = false;
        this.pushError = false;
        this.accessInfoExpiryScheduleInterval = -1L;
        this.handleErrorPageHip = false;
        this.handleErrorPageJsonResponse = false;
        this.disableParamInputValidationForTestingOutputEncoding = false;
        this.xmlSchemaVersion = 1.0f;
        this.xsd11CtaFullXpathChecking = false;
        this.reqDispValidationMode = DispatcherValidationMode.LEARNING;
        this.addURLParamFrameoriginToRedirectURL = false;
        this.followServletStdForUrlPath = true;
        this.tempFileUploadDirMonitoring = new TempFileUploadDirMonitoring(true, 2147483648L, 900000L);
        this.exceptionRuleMap = new HashMap<String, ExceptionRule>();
        this.isThrowExForInternalValidation = (RequestHeaderValidationMode.ENFORCEMENT == this.internalReqHeaderValidationMode || RequestHeaderValidationMode.LEARNING == this.internalReqHeaderValidationMode);
        this.enablePatchMethodMinOccurCheck = true;
        this.blockedMethods = new ArrayList<String>(Arrays.asList("CONNECT", "TRACE"));
        this.allowedServicesViaProxy = null;
        this.proxyURLValidationStatus = true;
        this.loginPage = null;
        this.enableAppSense = true;
        this.plugins = null;
        this.cacheConfigurationMap = null;
        this.isSetAPIRateLimitResponseHeader = false;
        this.allowImportURLRedirect = false;
        this.json_Invalid_Values_List = Arrays.asList(JSON_INVALID_VALUE_TYPE.EMPTYSTRING, JSON_INVALID_VALUE_TYPE.JSONNULL);
        this.ignoreExtraParam = false;
        this.enableSecretParamLoggingMask = false;
        this.enableHSTS = false;
        this.enableHSTSRedirection = false;
        this.sasConfigProviderClass = null;
        this.allowProxyURLRedirect = true;
        this.urlRedirectMaxLimit = 5;
        this.enablePostMethodRedirect = false;
        this.hstsAlloweddomains = null;
        this.secretRequestParamNames = null;
        this.secretRequestHeaderNames = new CopyOnWriteArrayList<String>(SecurityFilterProperties.defaultsecretRequestHeaderNamesList);
        this.secretResponseHeaderNames = null;
        this.logInputStream = false;
        this.maskExtraParam = false;
        this.maskIgnoreExtraParam = false;
        this.dynamicParamsMaxOccurrenceLimit = 5;
        this.isEnforcementMode = false;
        this.isLearningMode = false;
        this.zipSanitizerRuleMap = new HashMap<String, ZipSanitizerRule>();
        this.commonThrottlesRuleMap = null;
        this.trimEnabled = false;
        this.maskAllParamValuesOnError = false;
        this.enableIndividualOccurrenceCheckForDynamicParams = false;
        this.enablePiiDetector = false;
        this.piiDetectorRule = null;
        this.errorPageValidationMode = ErrorPageValidationMode.LOGGING;
        this.csrfCookieSamesiteMode = HttpCookie.SAMESITE.NONE;
        this.enableCSRFSamesiteStrictTmpcookie = true;
        this.allowCSRFParamInQS = false;
        this.enableRequestURINormalization = false;
        this.allowDotDotSlashInReqURI = false;
        this.enableHostOverride = false;
        this.forwardedHostHeaderName = "LB_FORWARDED_HOST";
        final String fullContextPath = filterConfig.getServletContext().getRealPath("");
        synchronized (fullContextPath) {
            if ("true".equalsIgnoreCase(filterConfig.getInitParameter("development.mode"))) {
                this.developmentMode = true;
            }
            if ("true".equalsIgnoreCase(filterConfig.getInitParameter("test.mode"))) {
                this.testMode = true;
            }
            this.excludeURLs = filterConfig.getInitParameter("exclude");
            final String filePath = filterConfig.getInitParameter("config-file");
            String webInfPath = filterConfig.getServletContext().getRealPath("/WEB-INF");
            if (!webInfPath.endsWith(File.separator)) {
                webInfPath += File.separator;
            }
            this.loadPrivateKeyFromSASConfiguration();
            this.addCommonSecurityConfigurationFiles();
            if (filePath != null) {
                this.filePaths = filePath.split(",");
                for (final String fileStr : this.filePaths) {
                    final File file = new File(webInfPath + fileStr.trim());
                    if (file.isDirectory()) {
                        for (final File childfile : file.listFiles()) {
                            if (childfile.isFile() && "security-development.xml".equals(childfile.getName())) {
                                throw new RuntimeException("The file 'security-development.xml' shouldn't exist in '/WEB-INF' directory. It will be loaded automatically from the 'conf' directory when the 'development.mode' is true. ");
                            }
                            if (childfile.isFile() && !this.securityFiles.contains(childfile) && childfile.getName().startsWith("security") && childfile.getName().endsWith(".xml")) {
                                this.securityFiles.add(childfile);
                            }
                        }
                    }
                    else {
                        if ("security-development.xml".equals(file.getName())) {
                            throw new RuntimeException("The file 'security-development.xml' shouldn't exist in '/WEB-INF' directory. It will be loaded automatically from the 'conf' directory when the 'development.mode' is true. ");
                        }
                        if ("security-wafproperties.xml".equals(file.getName())) {
                            throw new RuntimeException("The file 'security-wafproperties.xml' is not allowed to load. It is WAF's default property file");
                        }
                        validateFileName(file.getName(), "security", ".xml");
                        this.securityFiles.add(file);
                    }
                }
                for (final File file2 : this.securityFiles) {
                    SecurityFilterProperties.logger.log(Level.INFO, "Loading security-conf file : {0}", file2.getName());
                    RuleSetParser.initSecurityRules(this, file2);
                }
                if (this.csrf_cookie_name == null || this.csrf_param_name == null) {
                    throw new RuntimeException("Invalid security configuration :: Security property \"csrf.cookie.name\" & \"csrf.param.name\" both should be configured");
                }
                final String schemaPath = filterConfig.getInitParameter("schema-file");
                if (SecurityUtil.isValid(schemaPath)) {
                    final List<File> schemaFiles = new ArrayList<File>();
                    final String[] split;
                    final String[] schemaPaths = split = schemaPath.split(",");
                    for (final String schema : split) {
                        final File schemaFile = new File(webInfPath + schema.trim());
                        if (schemaFile.isDirectory()) {
                            for (final File childSchemaFile : schemaFile.listFiles()) {
                                if (childSchemaFile.isFile() && !schemaFiles.contains(childSchemaFile) && childSchemaFile.getName().startsWith("schema") && childSchemaFile.getName().endsWith(".xsd")) {
                                    schemaFiles.add(childSchemaFile);
                                }
                            }
                        }
                        else {
                            validateFileName(schemaFile.getName(), "schema", ".xsd");
                            schemaFiles.add(schemaFile);
                        }
                    }
                    for (final File schemaFile2 : schemaFiles) {
                        XMLSchemaRule.initialiseXMLSchema(this, schemaFile2);
                    }
                }
                this.loadAuthProviderClass();
            }
            this.initAccountsandInlineSecurityRules();
            this.loadRunTimeCacheConfigurations();
            this.checkRequiredCacheConfigured();
            this.validateCacheConfigurations();
            this.initInlineVariables();
            this.lastUpdatedTime = System.currentTimeMillis();
            this.postProcess();
            SecurityFilterProperties.contextName = SecurityUtil.getContextName(fullContextPath);
            this.initWAFControlConfigurations();
        }
    }
    
    private void initWAFControlConfigurations() throws Exception {
        this.setAppendSupportedProperties(Components.COMPONENT_NAME.SECRET_REQ_PARAM_NAMES.getValue(), this.secretRequestParamNames);
        this.setAppendSupportedProperties(Components.COMPONENT_NAME.SECRET_REQ_HEADER_NAMES.getValue(), this.secretRequestHeaderNames);
        this.setAppendSupportedProperties(Components.COMPONENT_NAME.SECRET_RES_HEADER_NAMES.getValue(), this.secretResponseHeaderNames);
        if (isROOTContext()) {
            for (final String filePath : SecurityFilterProperties.permanentConfigFiles) {
                final File file = new File(filePath);
                if (file.exists()) {
                    SecurityFilterProperties.logger.log(Level.INFO, "Loading WAFControl Configuration file : {0}", file.getName());
                    RuleSetParser.initWAFProperties(this, file);
                    SecurityFilterProperties.logger.log(Level.INFO, "Loaded  WAF Properties  {0} ", new Object[] { AppSenseAgent.wafProperties });
                }
            }
        }
    }
    
    private void setAppendSupportedProperties(final String propertyName, final List<String> valueList) {
        if (this.properties.containsKey(propertyName)) {
            final String values = SecurityUtil.getValuefromList(valueList);
            this.properties.setProperty(propertyName, values);
        }
    }
    
    void loadPrivateKeyFromSASConfiguration() {
        this.loadConfigurationProviderClass();
        if (this.sasConfigProviderClass != null) {
            final String iscPrivatekey = Configuration.getString("internalrequest.privatekey");
            if (SecurityUtil.isValid(iscPrivatekey)) {
                SecurityFilterProperties.logger.log(Level.FINE, "ISC private key loaded from SAS configuration");
                SecurityFilterProperties.iscPrivateKey = this.getPrivateKeyInstance(iscPrivatekey, SecurityUtil.SystemAuthType.ISC);
            }
            final String dcPrivatekey = Configuration.getString("interdc.privatekey");
            if (SecurityUtil.isValid(dcPrivatekey)) {
                SecurityFilterProperties.logger.log(Level.FINE, "Inter DC private key loaded from SAS configuration");
                SecurityFilterProperties.interDCPrivateKey = this.getPrivateKeyInstance(dcPrivatekey, SecurityUtil.SystemAuthType.INTERDC);
                setCurrentDCLocation(Configuration.getString("current.dc.location"));
            }
        }
    }
    
    private PrivateKey getPrivateKeyInstance(final String privateKeyStr, final SecurityUtil.SystemAuthType authType) {
        try {
            if (privateKeyStr != null) {
                final byte[] privateKeyBytes = (byte[])this.hexCodec.decode((Object)privateKeyStr);
                return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            }
        }
        catch (final Exception se) {
            SecurityFilterProperties.logger.log(Level.SEVERE, "Initializing \"{0}\" PriVateKey {1}", new Object[] { authType.getValue(), se });
            throw new RuntimeException("Error Initializing '" + authType.getValue() + "' PriVateKey :" + se.getMessage());
        }
        return null;
    }
    
    private static void setCurrentDCLocation(final String dcLocation) {
        if (!SecurityUtil.isValid(dcLocation)) {
            throw new RuntimeException("Invalid InterDC property configuration :: Current DC Location '" + dcLocation + "' is not valid");
        }
        SecurityFilterProperties.currentDCLocation = dcLocation;
    }
    
    private void postProcess() throws Exception {
        if (this.properties.containsKey("zohosecurity.plugin")) {
            final String[] plugins = this.properties.getProperty("zohosecurity.plugin").split(",");
            final List<String> initialized = new ArrayList<String>();
            for (final String plugin : plugins) {
                if (!initialized.contains(plugin)) {
                    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    final SFCorePlugin pluginImpl = (SFCorePlugin)cl.loadClass(plugin).newInstance();
                    pluginImpl.initRules(this);
                    this.addPlugin(pluginImpl);
                    initialized.add(plugin);
                }
                else {
                    SecurityFilterProperties.logger.log(Level.WARNING, "Ignoring the Implementation class which is already loaded. Class Name : {0}.", plugin);
                }
            }
        }
        this.checkAndSetSecurityXMLHash();
    }
    
    public void checkAndSetSecurityXMLHash() {
        AppSenseAgent.setSecurityFiles((List)this.securityFiles, (List)SecurityFilterProperties.defaultConfigFiles);
        AppSenseAgent.generateXMLHash();
    }
    
    void loadAuthProviderClass() throws Exception {
        if (this.authProvider == null) {
            String authProviderClass = null;
            if (this.properties.containsKey("com.adventnet.iam.authentication.provider")) {
                authProviderClass = this.properties.getProperty("com.adventnet.iam.authentication.provider", null);
            }
            if (authProviderClass == null && SecurityFilterProperties.useIAM) {
                authProviderClass = "com.adventnet.iam.filter.AuthenticationProviderImpl";
            }
            if (authProviderClass != null) {
                final ClassLoader cl = Thread.currentThread().getContextClassLoader();
                this.authProvider = (Authenticator)cl.loadClass(authProviderClass).newInstance();
                this.addAuthCommonConfigurationFiles();
            }
            if (this.authProvider != null && authProviderClass.equals("com.adventnet.iam.filter.AuthenticationProviderImpl")) {
                SecurityFilterProperties.usingIAMImpl = true;
            }
        }
    }
    
    void loadConfigurationProviderClass() {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final String ceClassName = "com.zoho.conf.Configuration";
        try {
            this.sasConfigProviderClass = cl.loadClass(ceClassName);
            SecurityFilterProperties.logger.log(Level.WARNING, "SAS Configuration Provider Class Loaded Successfully. Class Name : {0} ", new Object[] { ceClassName });
        }
        catch (final ClassNotFoundException e) {
            SecurityFilterProperties.logger.log(Level.WARNING, "SAS Configuration Provider Class Not Loaded for the service {0} .isIAM : {1} , Class Name : {2} , Exception {3}", new Object[] { this.properties.getProperty("service.name"), SecurityFilterProperties.useIAM, ceClassName, e });
        }
    }
    
    static void validateFileName(final String fileName, final String prefix, final String suffix) {
        if (!fileName.startsWith(prefix) || !fileName.endsWith(suffix)) {
            SecurityFilterProperties.logger.log(Level.SEVERE, "The name of the {1} file should starts with \"{1}\" and ends with \"{2}\". \"{0}\" is not allowed", new Object[] { fileName, prefix, suffix });
            throw new RuntimeException("The name of the " + prefix + " file should starts with \"" + prefix + "\" and ends with \"" + suffix + "\". \"" + fileName + "\" is not allowed");
        }
    }
    
    private void addCommonSecurityConfigurationFiles() {
        final String confDirPath = SecurityUtil.getSecurityConfigurationDir() + File.separator;
        SecurityFilterProperties.logger.log(Level.INFO, "Security common configuration files loaded from conf directory :: {0}", confDirPath);
        for (final String fileName : SecurityFilterProperties.defaultConfigFiles) {
            final File sfwConfFile = new File(confDirPath + fileName);
            if (("security-safeheaders.xml".equals(fileName) || "security-default-rules.xml".equals(fileName)) && !sfwConfFile.exists()) {
                throw new RuntimeException(" File " + fileName + " doesn't exist in " + confDirPath);
            }
            if (!sfwConfFile.exists() || ("security-privatekey.xml".equals(fileName) && SecurityFilterProperties.iscPrivateKey != null && SecurityFilterProperties.interDCPrivateKey != null)) {
                continue;
            }
            if ("security-development.xml".equals(fileName) && !this.developmentMode) {
                continue;
            }
            this.securityFiles.add(sfwConfFile);
        }
        final File userAgentRegexFile = new File(confDirPath + "regexes.yaml");
        if (!userAgentRegexFile.exists()) {
            throw new RuntimeException("File \"regexes.yaml\" doesnot exist at " + confDirPath + " it must be fetched from the security conf location ");
        }
        EventDataProcessor.initDefaultConfig();
        final File eventConfigCustomFile = new File(confDirPath + "zsec-events.xml");
        if (eventConfigCustomFile.exists()) {
            try {
                SecurityFilterProperties.logger.log(Level.INFO, "EventFramework -  custom configuration file \"{0}\" loaded from conf directory :: {1}", new Object[] { "zsec-events.xml", confDirPath });
                EventDataProcessor.init(eventConfigCustomFile);
            }
            catch (final Exception e) {
                throw new RuntimeException("Exception occured while loading eventFramework custom configuration " + e.getMessage());
            }
        }
        WAFAttackDiscoveryUtil.initEventXML();
    }
    
    String getExcludeURLs() {
        return this.excludeURLs;
    }
    
    protected void addURLRule(final String url, final URLRule urlRule) {
        if (urlRule.isURLInRegex()) {
            this.urlPatternRuleMap.put(Pattern.compile(url), urlRule);
        }
        else {
            this.urlRuleMap.put(url, urlRule);
        }
    }
    
    public String getCSRFCookieName() {
        return this.csrf_cookie_name;
    }
    
    public String getCSRFParamName() {
        return this.csrf_param_name;
    }
    
    public Properties getSecurityProperties() {
        return this.properties;
    }
    
    public SecurityProvider getSecurityProvider() {
        return this.provider;
    }
    
    public void initSecurityProperties() throws Exception {
        final String secProviderClass = this.properties.getProperty("com.adventnet.iam.security.provider");
        if (secProviderClass != null) {
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            (this.provider = (SecurityProvider)cl.loadClass(secProviderClass).newInstance()).init(new DefaultSecurityProvider());
        }
        else if (this.provider == null) {
            this.provider = new DefaultSecurityProvider();
        }
        if (this.properties.containsKey("csrf.cookie.name")) {
            this.csrf_cookie_name = this.properties.getProperty("csrf.cookie.name");
        }
        if (this.properties.containsKey("csrf.param.name")) {
            this.csrf_param_name = this.properties.getProperty("csrf.param.name");
        }
        if (this.properties.containsKey("error.label")) {
            this.labelStr = this.properties.getProperty("error.label");
            if (SecurityUtil.isValid(this.labelStr) && this.defaultLabelMap == null) {
                SecurityUtil.addToLabelMap(this.defaultLabelMap = new HashMap<String, String>(), this.labelStr, null);
            }
        }
        if (this.properties.containsKey("captcha.url")) {
            this.showCaptchaURL = this.properties.getProperty("captcha.url");
        }
        if (this.properties.containsKey("com.adventnet.iam.secretkey")) {
            System.setProperty("com.adventnet.iam.secretkey", this.properties.getProperty("com.adventnet.iam.secretkey"));
        }
        if (this.properties.containsKey("use.iam")) {
            SecurityFilterProperties.useIAM = "true".equals(this.properties.getProperty("use.iam"));
        }
        if (this.properties.containsKey("enable.timeoutmatcher")) {
            this.enableTimeoutMatcher = "true".equals(this.properties.getProperty("enable.timeoutmatcher"));
        }
        if (this.properties.containsKey("xss.enable.timeoutmatcher")) {
            this.enableXSSTimeoutMatcher = "true".equals(this.properties.getProperty("xss.enable.timeoutmatcher"));
        }
        if ((this.enableTimeoutMatcher || this.enableXSSTimeoutMatcher) && SecurityUtil.getMatcherUtil() == null) {
            final int maxThreads = SecurityUtil.getInt(this.properties.getProperty("timeoutmatcher.maxthreads", "30"));
            final long timeoutInMillis = SecurityUtil.getLong(this.properties.getProperty("timeoutmatcher.timeoutinmillis", "30000"));
            SecurityUtil.setMatcherUtil(new MatcherUtil(maxThreads, timeoutInMillis));
            SecurityFilterProperties.logger.log(Level.FINE, "Initializing timeout matcher utility : MatcherUtil");
        }
        if (this.properties.containsKey("trusted.ip.pattern")) {
            SecurityFilterProperties.trustedIPPattern = Pattern.compile(this.properties.getProperty("trusted.ip.pattern"));
        }
        CLAMAVConfiguration clamAvConfig = null;
        if (this.properties.containsKey("antivirus.host")) {
            final String antiVirusHost = this.properties.getProperty("antivirus.host");
            if (!this.properties.containsKey("antivirus.port")) {
                throw new RuntimeException("antivirus.port is not configured for the host :" + antiVirusHost);
            }
            final int antiVirusPort = Integer.parseInt(this.properties.getProperty("antivirus.port"));
            clamAvConfig = new CLAMAVConfiguration(antiVirusHost, antiVirusPort);
        }
        if (clamAvConfig != null) {
            if (this.properties.containsKey("antivirus.numberof.persistant.connections")) {
                clamAvConfig.setPersistantConnections(Integer.parseInt(this.properties.getProperty("antivirus.numberof.persistant.connections")));
            }
            if (this.properties.containsKey("antivirus.readtimeout")) {
                clamAvConfig.setReadTimeOut(Integer.parseInt(this.properties.getProperty("antivirus.readtimeout")));
            }
            if (this.properties.containsKey("is.nonpersistentscan")) {
                clamAvConfig.setNonPersistenScan("true".equalsIgnoreCase(this.properties.getProperty("is.nonpersistentscan")));
            }
            if (this.properties.containsKey("enable.clamav.instrumentation")) {
                clamAvConfig.setClamAVInstrumentation(Boolean.parseBoolean(this.properties.getProperty("enable.clamav.instrumentation")));
            }
        }
        String icapConfig = null;
        if (this.properties.containsKey("icap.av.config")) {
            icapConfig = this.properties.getProperty("icap.av.config");
            SecurityFilterProperties.logger.log(Level.INFO, "ICAP Vendor AV configured");
        }
        String restApiConfig = null;
        if (this.properties.containsKey("restapi.av.config")) {
            restApiConfig = this.properties.getProperty("restapi.av.config");
            SecurityFilterProperties.logger.log(Level.INFO, "REST API Vendor AV configured");
        }
        if (clamAvConfig != null || icapConfig != null || restApiConfig != null) {
            this.avConfig = AntivirusConfiguration.init(clamAvConfig, icapConfig, restApiConfig);
            this.vendorAvProvider = this.avConfig.newVendorAVProvider();
        }
        if (this.properties.containsKey("content.type.detect.option")) {
            this.contentTypeDetection = this.properties.getProperty("content.type.detect.option");
        }
        if (this.properties.containsKey("disable.param.input.validation.for.testing.output.encoding")) {
            this.disableParamInputValidationForTestingOutputEncoding = "true".equalsIgnoreCase(this.properties.getProperty("disable.param.input.validation.for.testing.output.encoding"));
            final String excludeParams = this.properties.getProperty("disable.param.input.validation.for.testing.output.encoding.exclude.params");
            if (SecurityUtil.isValid(excludeParams)) {
                this.addRegularExpressions("disable.param.input.validation.for.testing.output.encoding.exclude.params", excludeParams);
            }
            final String excludeRegexNames = this.properties.getProperty("disable.param.input.validation.for.testing.output.encoding.exclude.regexname");
            if (SecurityUtil.isValid(excludeRegexNames)) {
                this.addRegularExpressions("disable.param.input.validation.for.testing.output.encoding.exclude.regexname", excludeRegexNames);
            }
        }
        if (this.properties.containsKey("http.ip.black.list.dns")) {
            this.httpIPBlackListDNS = this.properties.getProperty("http.ip.black.list.dns");
        }
        if (this.properties.containsKey("http.ip.white.list.dns")) {
            this.httpIPWhiteListDNS = this.properties.getProperty("http.ip.white.list.dns");
        }
        if (this.properties.containsKey("allow.empty.value")) {
            this.allowEmptyValue = "true".equalsIgnoreCase(this.properties.getProperty("allow.empty.value"));
        }
        if (this.properties.containsKey("allow.invalid.value")) {
            this.allowInvalidValue = "true".equalsIgnoreCase(this.properties.getProperty("allow.invalid.value"));
        }
        if (this.properties.containsKey("json.invalid.list")) {
            final String jsonInvalidList = this.properties.getProperty("json.invalid.list");
            if (SecurityUtil.isValid(jsonInvalidList)) {
                this.json_Invalid_Values_List = null;
                this.json_Invalid_Values_List = new ArrayList<JSON_INVALID_VALUE_TYPE>();
                for (final String invalidToken : jsonInvalidList.split(",")) {
                    this.json_Invalid_Values_List.add(JSON_INVALID_VALUE_TYPE.valueOf(invalidToken.toUpperCase()));
                }
            }
        }
        if (this.properties.containsKey("response.encoding")) {
            this.responseEncoding = this.properties.getProperty("response.encoding");
        }
        if (this.properties.containsKey("request.encoding")) {
            final String reqEncoding = this.properties.getProperty("request.encoding");
            this.requestEncoding = (SecurityUtil.isValid(reqEncoding) ? reqEncoding : this.requestEncoding);
        }
        if (this.properties.containsKey("uri.ignore.prefix")) {
            final String ignoreURIPrefix = this.properties.getProperty("uri.ignore.prefix");
            if (SecurityUtil.isValid(ignoreURIPrefix)) {
                this.ignoreURIPrefixList = new ArrayList<String>();
                for (final String prefix : ignoreURIPrefix.split(",")) {
                    this.ignoreURIPrefixList.add(prefix.trim());
                }
            }
        }
        this.ignoreTrailingSlash = "true".equals(this.properties.getProperty("ignore.trailing.slash"));
        this.enableXSSFilterLog = "true".equals(this.properties.getProperty("enable.xssfilter.log"));
        if (this.properties.containsKey("xssfilter.log.maxsize")) {
            this.maxLogSize = Integer.parseInt(this.properties.getProperty("xssfilter.log.maxsize"));
        }
        if (SecurityUtil.isValid(this.properties.getProperty("enable.xsspattern.detect"))) {
            this.enableXSSPatternDetect = "true".equals(this.properties.getProperty("enable.xsspattern.detect"));
        }
        if (SecurityUtil.isValid(this.properties.getProperty("disable.xssfilter.patterndetect"))) {
            this.disableXSSPatternDetectForFilter = "true".equals(this.properties.getProperty("disable.xssfilter.patterndetect"));
        }
        if (XSSUtil.XSSPATTERN == null && SecurityUtil.isValid(this.properties.getProperty("xss.detect.pattern"))) {
            XSSUtil.initXSSDetectPattern(this.properties.getProperty("xss.detect.pattern"), this.properties.getProperty("xss.detect.pattern.ext"), this.properties.getProperty("xss.encodecheck.pattern"), this.properties.getProperty("xss.trimctrlchars.pattern"));
        }
        if (SecurityFilterProperties.proxy == null && this.properties.containsKey("proxy.host")) {
            SecurityFilterProperties.proxyHost = this.properties.getProperty("proxy.host");
            SecurityFilterProperties.proxyPort = this.properties.getProperty("proxy.port");
            try {
                SecurityFilterProperties.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(SecurityFilterProperties.proxyHost, Integer.parseInt(SecurityFilterProperties.proxyPort)));
                if (this.properties.containsKey("proxy.username")) {
                    SecurityFilterProperties.proxyUserName = this.properties.getProperty("proxy.username");
                    SecurityFilterProperties.proxyPassword = this.properties.getProperty("proxy.password");
                    java.net.Authenticator.setDefault(new SecurityUtil.MyAuthenticator(SecurityFilterProperties.proxyUserName, SecurityFilterProperties.proxyPassword));
                }
            }
            catch (final Exception ex) {
                SecurityFilterProperties.logger.log(Level.SEVERE, "Error in iniatialising proxy : {0}", ex);
                throw new RuntimeException("Invalid proxy configuration");
            }
        }
        this.setCSRFCookie = !"false".equals(this.properties.getProperty("set.csrf.cookie"));
        if (this.properties.containsKey("enable.trusted.script")) {
            this.isTrustedScriptEnabled = "true".equals(this.properties.getProperty("enable.trusted.script"));
            if (this.isTrustedScriptEnabled) {
                XSSUtil.enableTrustedDomainScriptTags();
            }
        }
        if (this.properties.containsKey("upload.filerule.connectiontimeout")) {
            this.uploadFileRuleConnectionTimeOut = Integer.parseInt(this.properties.getProperty("upload.filerule.connectiontimeout"));
        }
        if (this.properties.containsKey("disable.put.minoccur.check")) {
            this.disablePutMinoccurCheck = "true".equalsIgnoreCase(this.properties.getProperty("disable.put.minoccur.check"));
        }
        if (this.properties.containsKey("upload.filerule.readtimeout")) {
            this.uploadFileRuleReadTimeOut = Integer.parseInt(this.properties.getProperty("upload.filerule.readtimeout"));
        }
        if (this.properties.containsKey("disable.authcsrf")) {
            this.disableAuthCSRF = "true".equalsIgnoreCase(this.properties.getProperty("disable.authcsrf"));
        }
        if (this.properties.containsKey("csrf.migration")) {
            this.enableCSRFMigration = "true".equalsIgnoreCase(this.properties.getProperty("csrf.migration"));
        }
        if (this.disableAuthCSRF && this.enableCSRFMigration) {
            throw new RuntimeException("Cannot disable csrf migration when Authcsrf is disabled");
        }
        this.enableReadOnlyMode = Boolean.parseBoolean(this.properties.getProperty("readonly.mode"));
        if (this.properties.containsKey("enable.response.send.error")) {
            this.enableResponseSendError = Boolean.parseBoolean(this.properties.getProperty("enable.response.send.error"));
        }
        if (this.properties.containsKey("isc.signature.expiry.time")) {
            this.iscSignatureExpiryTime = Long.parseLong(this.properties.getProperty("isc.signature.expiry.time"));
        }
        if (this.properties.containsKey("enable.request.options")) {
            this.enableRequestOptions = "true".equalsIgnoreCase(this.properties.getProperty("enable.request.options"));
        }
        final AbstractMap.SimpleEntry<Boolean, String> isAppFirewallEnabledConfig = this.getProperty(Components.COMPONENT_NAME.ENABLE_APP_FIREWALL.getValue());
        if (isAppFirewallEnabledConfig.getKey()) {
            this.isAppFirewallEnabled = "true".equalsIgnoreCase(isAppFirewallEnabledConfig.getValue());
        }
        if (this.properties.containsKey("enable.app.firewall.file")) {
            this.isAppFirewallFileBasedLoadingEnabled = "true".equalsIgnoreCase(this.properties.getProperty("enable.app.firewall.file"));
        }
        if (this.properties.containsKey("allowed.user.agents")) {
            final String allowedUserAgents = this.properties.getProperty("allowed.user.agents");
            if (SecurityUtil.isValid(allowedUserAgents) && this.allowedUserAgentVersions == null) {
                this.allowedUserAgentVersions = new HashMap<String, Integer>();
                final String[] split3;
                final String[] userAgents = split3 = allowedUserAgents.split(",");
                for (final String userAgent : split3) {
                    final String[] uaVars = userAgent.split(":");
                    if (uaVars.length != 2) {
                        SecurityFilterProperties.logger.log(Level.SEVERE, "Invalid  allowed.user.agents  {0}", allowedUserAgents);
                        throw new RuntimeException("Error Initializing allowed.user.agents  :" + allowedUserAgents);
                    }
                    this.allowedUserAgentVersions.put(uaVars[0], Integer.parseInt(uaVars[1]));
                }
            }
        }
        if (this.properties.containsKey("mask.secret.param.logging")) {
            this.maskSecretParamLogging = "true".equalsIgnoreCase(this.properties.getProperty("mask.secret.param.logging"));
        }
        if (this.properties.containsKey("log.response")) {
            this.logResponse = "true".equalsIgnoreCase(this.properties.getProperty("log.response"));
        }
        if (this.properties.containsKey("log.push.security.exception")) {
            this.pushError = "true".equalsIgnoreCase(this.properties.getProperty("log.push.security.exception"));
        }
        if (this.properties.containsKey("exclude.urls.log.response")) {
            this.excludedURLsInLogResponse = Pattern.compile(this.properties.getProperty("exclude.urls.log.response"));
        }
        if (SecurityFilterProperties.iscPrivateKey == null && this.properties.containsKey("internalrequest.privatekey")) {
            SecurityFilterProperties.iscPrivateKey = this.getPrivateKeyInstance(this.properties.getProperty("internalrequest.privatekey"), SecurityUtil.SystemAuthType.ISC);
        }
        if (SecurityFilterProperties.interDCPrivateKey == null && this.properties.containsKey("interdc.privatekey")) {
            SecurityFilterProperties.interDCPrivateKey = this.getPrivateKeyInstance(this.properties.getProperty("interdc.privatekey"), SecurityUtil.SystemAuthType.INTERDC);
            setCurrentDCLocation(System.getProperty("current.dc.location"));
        }
        if (this.properties.containsKey("proxy.connection.timeout")) {
            SecurityFilterProperties.proxyConnectTimeOut = Integer.parseInt(this.properties.getProperty("proxy.connection.timeout"));
        }
        if (this.properties.containsKey("proxy.read.timeout")) {
            SecurityFilterProperties.proxyReadTimeOut = Integer.parseInt(this.properties.getProperty("proxy.read.timeout"));
        }
        if (this.properties.containsKey("disable.get.api.csrf.check")) {
            this.disableGetApiCSRFCheck = "true".equals(this.properties.getProperty("disable.get.api.csrf.check"));
        }
        if (this.properties.containsKey("enable.xframe.options")) {
            this.enableXFrameOptions = "true".equalsIgnoreCase(this.properties.getProperty("enable.xframe.options"));
        }
        if (this.enableXFrameOptions) {
            this.defaultXFrameOption = (SecurityUtil.isValid(this.properties.getProperty("xframe.default.option")) ? this.properties.getProperty("xframe.default.option") : "sameorigin");
        }
        final String racProviderClass = this.properties.getProperty("appfirewall.provider");
        if (racProviderClass != null) {
            final ClassLoader cl2 = Thread.currentThread().getContextClassLoader();
            this.racProvider = (RACProvider)cl2.loadClass(racProviderClass).newInstance();
        }
        else if (this.racProvider == null) {
            this.racProvider = new RACProviderImpl();
        }
        if (this.properties.containsKey("enable.x.http.method.override.option")) {
            this.enableXHTTPMethodOverrideOption = "true".equalsIgnoreCase(this.properties.getProperty("enable.x.http.method.override.option"));
        }
        if (this.properties.containsKey("disable.path.parameter.uri.decoding.check")) {
            SecurityFilterProperties.logger.log(Level.WARNING, "*****ENABLED******** : disable.path.parameter.uri.decoding.check flag is enabled ");
            this.disablePathParamURIDecodingCheck = "true".equalsIgnoreCase(this.properties.getProperty("disable.path.parameter.uri.decoding.check"));
        }
        if (this.properties.containsKey("enable.patch.method.min.occurrence.check")) {
            this.enablePatchMethodMinOccurCheck = "true".equalsIgnoreCase(this.properties.getProperty("enable.patch.method.min.occurrence.check"));
        }
        if (this.properties.containsKey("request.header.validation.mode")) {
            try {
                this.reqHeaderValidationMode = RequestHeaderValidationMode.valueOf(this.properties.getProperty("request.header.validation.mode").toUpperCase());
                this.isEnforcementMode = (RequestHeaderValidationMode.ENFORCEMENT == this.reqHeaderValidationMode);
                this.isLearningMode = (RequestHeaderValidationMode.LEARNING == this.reqHeaderValidationMode);
            }
            catch (final IllegalArgumentException e) {
                SecurityFilterProperties.logger.log(Level.SEVERE, "Request header validation mode is invalid , expect one from the following ['learning','enforcement','logging','disable'] ");
                throw new RuntimeException("Error Initializing Request Header validation Mode :" + e.getMessage());
            }
        }
        if (this.properties.containsKey("login.page")) {
            this.loginPage = this.properties.getProperty("login.page");
        }
        if (this.properties.containsKey("custom.request.methods") && SecurityUtil.isValid(this.properties.getProperty("custom.request.methods"))) {
            final List<String> customRequestMethods = Arrays.asList(this.properties.getProperty("custom.request.methods").toUpperCase().split(","));
            for (final String blockedMethod : this.getBlockedMethods()) {
                if (customRequestMethods.contains(blockedMethod)) {
                    throw new RuntimeException("Invalid method '" + blockedMethod + "' in custom.request.methods property");
                }
            }
            SecurityFilterProperties.logger.log(Level.INFO, "custom httpServletRequest method list {0} is being added ", customRequestMethods);
            SecurityFilterProperties.requestMethod.addAll(customRequestMethods);
        }
        if (this.properties.containsKey("enable.appsense")) {
            this.enableAppSense = "true".equalsIgnoreCase(this.properties.getProperty("enable.appsense"));
            if (this.enableAppSense && !this.developmentMode) {
                ZSecZohoLogsImplProvider.setAppSenseEnabledStatus(this.enableAppSense);
            }
        }
        if (this.properties.containsKey("disable.service.url.throttles")) {
            this.disableServiceURLThrottles = "true".equals(this.properties.getProperty("disable.service.url.throttles"));
        }
        if (this.disableServiceURLThrottles && this.properties.containsKey("convert.service.url.rolling.throttles.into.appserver")) {
            this.convertServiceUrlRollingThrottlesIntoAppserver = "true".equals(this.properties.getProperty("convert.service.url.rolling.throttles.into.appserver"));
        }
        this.isSetAPIRateLimitResponseHeader = "true".equals(this.properties.getProperty("set.api.rate.limit.response.header"));
        if (!this.disableOldThrottleConfig) {
            this.disableOldThrottleConfig = "true".equals(this.properties.getProperty("disable.old.throttle.config"));
        }
        if (this.properties.containsKey("dos.accessinfo.expiry.schedule.timeinterval")) {
            this.accessInfoExpiryScheduleInterval = Long.parseLong(this.properties.getProperty("dos.accessinfo.expiry.schedule.timeinterval"));
        }
        if (this.properties.containsKey("allow.import.url.redirect")) {
            this.allowImportURLRedirect = "true".equalsIgnoreCase(this.properties.getProperty("allow.import.url.redirect"));
        }
        if (this.properties.containsKey("ignore.extra.param")) {
            this.ignoreExtraParam = "true".equalsIgnoreCase(this.properties.getProperty("ignore.extra.param"));
        }
        if (this.properties.containsKey("captcha.image.dimensions")) {
            final String[] captchaImageSize = this.properties.getProperty("captcha.image.dimensions").split(",");
            if (captchaImageSize.length != 4) {
                throw new RuntimeException("Invalid 'captcha.image.dimensions' property configuration :: Value must contain width, height, font & wordspacing size delimited by comma, eg: 200,70,40,0");
            }
            this.captchaImageDimensions = new int[4];
            for (int i = 0; i < captchaImageSize.length; ++i) {
                this.captchaImageDimensions[i] = Integer.parseInt(captchaImageSize[i]);
            }
        }
        if (this.properties.containsKey("enable.secret.param.logging.mask")) {
            this.enableSecretParamLoggingMask = "true".equalsIgnoreCase(this.properties.getProperty("enable.secret.param.logging.mask"));
        }
        if (this.properties.containsKey("enable.hsts")) {
            this.enableHSTS = "true".equalsIgnoreCase(this.properties.getProperty("enable.hsts"));
            if (this.properties.containsKey("hsts.allowed.domains")) {
                final String domainStr = this.properties.getProperty("hsts.allowed.domains");
                if (SecurityUtil.isValid(domainStr)) {
                    this.hstsAlloweddomains = domainStr.split(",");
                }
            }
            if (this.properties.containsKey("hsts.forceful.redirection")) {
                this.enableHSTSRedirection = "true".equalsIgnoreCase(this.properties.getProperty("hsts.forceful.redirection"));
            }
        }
        if (this.properties.containsKey("allow.proxy.url.redirect")) {
            this.allowProxyURLRedirect = "true".equalsIgnoreCase(this.properties.getProperty("allow.proxy.url.redirect"));
        }
        if (this.properties.containsKey("url.redirect.maxlimit")) {
            this.urlRedirectMaxLimit = Integer.parseInt(this.properties.getProperty("url.redirect.maxlimit"));
        }
        if (this.properties.containsKey("enable.post.method.redirect")) {
            this.enablePostMethodRedirect = "true".equalsIgnoreCase(this.properties.getProperty("enable.post.method.redirect"));
        }
        final AbstractMap.SimpleEntry<Boolean, String> secretRequestParam = this.getProperty(Components.COMPONENT_NAME.SECRET_REQ_PARAM_NAMES.getValue());
        if (secretRequestParam.getKey()) {
            if (this.secretRequestParamNames == null) {
                this.secretRequestParamNames = new CopyOnWriteArrayList<String>();
            }
            SecurityUtil.addValueToList(secretRequestParam.getValue(), this.secretRequestParamNames);
        }
        final AbstractMap.SimpleEntry<Boolean, String> secretRequestHeader = this.getProperty(Components.COMPONENT_NAME.SECRET_REQ_HEADER_NAMES.getValue());
        if (secretRequestHeader.getKey()) {
            SecurityUtil.addValueToList(secretRequestHeader.getValue(), this.secretRequestHeaderNames);
        }
        final AbstractMap.SimpleEntry<Boolean, String> secretResponseHeader = this.getProperty(Components.COMPONENT_NAME.SECRET_RES_HEADER_NAMES.getValue());
        if (secretResponseHeader.getKey()) {
            if (this.secretResponseHeaderNames == null) {
                this.secretResponseHeaderNames = new CopyOnWriteArrayList<String>();
            }
            SecurityUtil.addValueToList(secretResponseHeader.getValue(), this.secretResponseHeaderNames);
        }
        if (this.properties.containsKey("mask.all.paramvalues.on.error")) {
            this.maskAllParamValuesOnError = "true".equalsIgnoreCase(this.properties.getProperty("mask.all.paramvalues.on.error"));
        }
        if (this.properties.containsKey("handle.errorpage.hip")) {
            this.handleErrorPageHip = "true".equalsIgnoreCase(this.properties.getProperty("handle.errorpage.hip"));
        }
        if (this.properties.containsKey("handle.errorpage.json.response")) {
            this.handleErrorPageJsonResponse = "true".equalsIgnoreCase(this.properties.getProperty("handle.errorpage.json.response"));
        }
        if (this.properties.containsKey("log.inputstream")) {
            this.logInputStream = "true".equalsIgnoreCase(this.properties.getProperty("log.inputstream"));
        }
        if (this.properties.containsKey("mask.extra.param")) {
            this.maskExtraParam = "true".equalsIgnoreCase(this.properties.getProperty("mask.extra.param"));
        }
        if (this.properties.containsKey("mask.ignore.extra.param")) {
            this.maskIgnoreExtraParam = "true".equalsIgnoreCase(this.properties.getProperty("mask.ignore.extra.param"));
        }
        if (this.properties.containsKey("dynamic.params.max.occurrence.limit")) {
            this.dynamicParamsMaxOccurrenceLimit = Integer.parseInt(this.properties.getProperty("dynamic.params.max.occurrence.limit"));
            if (this.dynamicParamsMaxOccurrenceLimit < 0) {
                throw new RuntimeException("Invalid maxoccurrences global configuration :: 'dynamic.params.max.occurrence.limit' property value should be a positive integer value");
            }
        }
        if (this.properties.containsKey("enable.pii.detector")) {
            this.enablePiiDetector = "true".equalsIgnoreCase(this.properties.getProperty("enable.pii.detector"));
        }
        if (this.properties.containsKey("pattern.matcher.timeoutinmillis")) {
            SecurityFilterProperties.patternMatcherTimeOutInMillis = Integer.parseInt(this.properties.getProperty("pattern.matcher.timeoutinmillis"));
            if (SecurityFilterProperties.patternMatcherTimeOutInMillis < 0) {
                throw new RuntimeException("Invalid pattern matcher timeout configuration :: 'pattern.matcher.timeoutinmillis' property value should be a positive integer value");
            }
        }
        if (this.properties.containsKey("pattern.matcher.max.iterationcount")) {
            SecurityFilterProperties.patternMatcherMaxIterationCount = Integer.parseInt(this.properties.getProperty("pattern.matcher.max.iterationcount"));
            if (SecurityFilterProperties.patternMatcherMaxIterationCount < 0) {
                throw new RuntimeException("Invalid pattern matcher iterationcount configuration :: 'pattern.matcher.max.iterationcount' property value should be a positive integer value");
            }
        }
        if (this.properties.containsKey("inputstream.validation.mode")) {
            try {
                this.inputStreamValidationMode = InputStreamValidationMode.valueOf(this.properties.getProperty("inputstream.validation.mode").toUpperCase());
            }
            catch (final IllegalArgumentException e2) {
                SecurityFilterProperties.logger.log(Level.SEVERE, "Inputstream validation mode is invalid,  Allowed values are : ['log','error'] ");
                throw new RuntimeException("Error Initializing inputstream validation mode :" + e2.getMessage());
            }
        }
        if (this.properties.containsKey("enable.individual.occurrence.check.for.dynamic.params")) {
            this.enableIndividualOccurrenceCheckForDynamicParams = "true".equalsIgnoreCase(this.properties.getProperty("enable.individual.occurrence.check.for.dynamic.params"));
        }
        if (this.properties.containsKey("disable.redis.instrumentation")) {
            SecurityFilterProperties.disableRedisInstrumentation = "true".equals(this.properties.getProperty("disable.redis.instrumentation"));
        }
        if (this.properties.containsKey("multi.av.config.provider")) {
            final ClassLoader cl3 = Thread.currentThread().getContextClassLoader();
            if (this.vendorAvProvider != null) {
                SecurityFilterProperties.logger.log(Level.WARNING, "VendorAVProvider is overridden");
            }
            this.vendorAvProvider = (VendorAVProvider)cl3.loadClass(this.properties.getProperty("multi.av.config.provider")).newInstance();
        }
        if (this.properties.containsKey("enable.tika.file.content.and.name.based.detection")) {
            this.enableTikaFileContentAndNameBasedDetection = "true".equals(this.properties.getProperty("enable.tika.file.content.and.name.based.detection"));
        }
        if (this.properties.containsKey("enable.trim")) {
            this.trimEnabled = "true".equalsIgnoreCase(this.properties.getProperty("enable.trim"));
        }
        if (this.properties.containsKey("pattern.matcher.mode")) {
            try {
                SecurityFilterProperties.patternMatcherMode = PatternMatcherWrapper.PatternMatcherMode.valueOf(this.properties.getProperty("pattern.matcher.mode").toUpperCase());
            }
            catch (final IllegalArgumentException e2) {
                throw new RuntimeException("Invalid pattern matcher mode configuration :: Allowed values of property 'pattern.matcher.mode' are ['logging','error','learning','disable']");
            }
        }
        if (this.properties.containsKey("error.page.validation.mode")) {
            try {
                this.errorPageValidationMode = ErrorPageValidationMode.valueOf(this.properties.getProperty("error.page.validation.mode").toUpperCase());
            }
            catch (final IllegalArgumentException e2) {
                SecurityFilterProperties.logger.log(Level.SEVERE, "Error Page validation mode is invalid , expect one from the following ['lo','enforcement'] ");
                throw new RuntimeException("Error Initializing Error Page validation Mode :" + e2.getMessage());
            }
        }
        if (this.properties.containsKey("csrf.cookie.samesite.mode")) {
            try {
                this.csrfCookieSamesiteMode = HttpCookie.SAMESITE.valueOf(this.properties.getProperty("csrf.cookie.samesite.mode").toUpperCase());
            }
            catch (final IllegalArgumentException e2) {
                SecurityFilterProperties.logger.log(Level.SEVERE, "CSRF cookie samesite mode is invalid,  Allowed values are : ['Strict','Lax','None'] ");
                throw new RuntimeException("Error Initializing csrf cookie samesite attribute value :" + e2.getMessage());
            }
        }
        if (this.properties.containsKey("enable.csrf.samesite.strict.tmpcookie")) {
            this.enableCSRFSamesiteStrictTmpcookie = !"false".equalsIgnoreCase(this.properties.getProperty("enable.csrf.samesite.strict.tmpcookie"));
        }
        if (this.properties.containsKey("start.live.window.cleaner.scheduler")) {
            this.startLiveWindowCleanerScheduler = Boolean.parseBoolean(this.properties.getProperty("start.live.window.cleaner.scheduler"));
        }
        if (this.properties.containsKey("block.request.on.dos.cache.exception")) {
            this.blockRequestOnDosCacheException = Boolean.parseBoolean(this.properties.getProperty("block.request.on.dos.cache.exception"));
        }
        if (this.properties.containsKey("xml.schema.version")) {
            final String xsdVersion = this.properties.getProperty("xml.schema.version");
            if (!"1.0".equals(xsdVersion) && !"1.1".equals(xsdVersion)) {
                throw new RuntimeException("Invalid xml schema version '" + xsdVersion + "' is specified");
            }
            this.xmlSchemaVersion = Float.parseFloat(xsdVersion);
        }
        if (this.properties.containsKey("enable.xsd11.cta.full.xpath.checking")) {
            this.xsd11CtaFullXpathChecking = "true".equalsIgnoreCase(this.properties.getProperty("enable.xsd11.cta.full.xpath.checking"));
        }
        if (this.properties.containsKey("request.dispatcher.validation.mode")) {
            this.reqDispValidationMode = DispatcherValidationMode.propertyToEnum(this.properties.getProperty("request.dispatcher.validation.mode"));
        }
        if (this.properties.containsKey("add.urlparam.frameorigin.to.redirecturl")) {
            this.addURLParamFrameoriginToRedirectURL = "true".equalsIgnoreCase(this.properties.getProperty("add.urlparam.frameorigin.to.redirecturl"));
        }
        if (this.properties.containsKey("follow.servlet.std.for.urlpath")) {
            this.followServletStdForUrlPath = "true".equalsIgnoreCase(this.properties.getProperty("follow.servlet.std.for.urlpath"));
        }
        if (this.properties.containsKey("temp.fileupload.dir.monitoring.enable")) {
            this.tempFileUploadDirMonitoring.setEnable(Boolean.parseBoolean(this.properties.getProperty("temp.fileupload.dir.monitoring.enable")));
        }
        if (this.properties.containsKey("temp.fileupload.dir.monitoring.schedule.time")) {
            this.tempFileUploadDirMonitoring.setScheduleTime(Long.parseLong(this.properties.getProperty("temp.fileupload.dir.monitoring.schedule.time")));
        }
        if (this.properties.containsKey("temp.fileupload.dir.monitoring.threshold.size")) {
            this.tempFileUploadDirMonitoring.setThresholdSize(TempFileUploadDirMonitoring.getSizeInBytes(this.properties.getProperty("temp.fileupload.dir.monitoring.threshold.size")));
        }
        if (this.properties.containsKey("allow.csrf.param.in.querystring")) {
            this.allowCSRFParamInQS = "true".equalsIgnoreCase(this.properties.getProperty("allow.csrf.param.in.querystring"));
        }
        if (this.properties.containsKey("enable.request.uri.normalization")) {
            this.enableRequestURINormalization = "true".equalsIgnoreCase(this.properties.getProperty("enable.request.uri.normalization"));
        }
        if (this.properties.containsKey("allow.dot.dot.slash.in.request.uri")) {
            this.allowDotDotSlashInReqURI = "true".equalsIgnoreCase(this.properties.getProperty("allow.dot.dot.slash.in.request.uri"));
        }
        if (this.properties.containsKey("enable.host.override")) {
            this.enableHostOverride = "true".equalsIgnoreCase(this.properties.getProperty("enable.host.override"));
            if (this.enableHostOverride) {
                if (this.properties.containsKey("forwarded.host.header.name")) {
                    this.forwardedHostHeaderName = this.properties.getProperty("forwarded.host.header.name");
                    if (!SecurityUtil.isValid(this.forwardedHostHeaderName)) {
                        throw new RuntimeException("Invalid Property Configuration :: Invalid value configured for security property 'forwarded.host.header.name'");
                    }
                }
                if (this.properties.containsKey("forwarded.host.allowed.domains")) {
                    final String domainStr2 = this.properties.getProperty("forwarded.host.allowed.domains");
                    if (!SecurityUtil.isValid(domainStr2)) {
                        throw new RuntimeException("Invalid Property Configuration :: Property 'forwarded.host.allowed.domains' should be specified if 'enable.host.override' set to true");
                    }
                    this.allowedForwardedHosts = new ArrayList<String>();
                    for (final String domain : domainStr2.split(",")) {
                        this.allowedForwardedHosts.add(domain.trim());
                    }
                    if (this.allowedForwardedHosts.size() > 1 && this.allowedForwardedHosts.contains("trusted")) {
                        throw new RuntimeException("Invalid Property Configuration :: Either 'trusted' or specific domains should be configured in property 'forwarded.host.allowed.domains'");
                    }
                }
            }
        }
        final AbstractMap.SimpleEntry<Boolean, String> cspReportConfig = this.getProperty(Components.COMPONENT_NAME.ENABLE_CSP_REPORT.getValue());
        if (cspReportConfig.getKey()) {
            AppSenseAgent.enableCSPReport = "true".equalsIgnoreCase(cspReportConfig.getValue());
        }
        final AbstractMap.SimpleEntry<Boolean, String> cspreportURIConfig = this.getProperty(Components.COMPONENT_NAME.CSP_REPORT_URI.getValue());
        if (cspreportURIConfig.getKey()) {
            AppSenseAgent.cspReportURI = cspreportURIConfig.getValue();
        }
        final AbstractMap.SimpleEntry<Boolean, String> reqinfoFileHashConfig = this.getProperty(Components.COMPONENT_NAME.ENBALE_REQINFO_FILEHASH.getValue());
        if (reqinfoFileHashConfig.getKey()) {
            AppSenseAgent.enableReqInfoFileHash = "true".equalsIgnoreCase(reqinfoFileHashConfig.getValue());
        }
        final AbstractMap.SimpleEntry<Boolean, String> reqinfoFileHashAlgo = this.getProperty(Components.COMPONENT_NAME.REQINFO_FILEHASH_ALGO.getValue());
        if (reqinfoFileHashAlgo.getKey()) {
            AppSenseAgent.reqInfoFileHashAlgorithm = reqinfoFileHashAlgo.getValue();
        }
        final AbstractMap.SimpleEntry<Boolean, String> securityXMLPushConfig = this.getProperty(Components.COMPONENT_NAME.ENABLE_SECXML_PUSH.getValue());
        if (securityXMLPushConfig.getKey()) {
            AppSenseAgent.enableSecurityXMLPush = "true".equalsIgnoreCase(securityXMLPushConfig.getValue());
        }
        final AbstractMap.SimpleEntry<Boolean, String> cacertPushConfig = this.getProperty(Components.COMPONENT_NAME.ENABLE_CACERT_PUSH.getValue());
        if (cacertPushConfig.getKey()) {
            AppSenseAgent.enableCACertPush = "true".equalsIgnoreCase(cacertPushConfig.getValue());
        }
        final AbstractMap.SimpleEntry<Boolean, String> milestonePushConfig = this.getProperty(Components.COMPONENT_NAME.ENABLE_MILESTONEVERSION_PUSH.getValue());
        if (milestonePushConfig.getKey()) {
            AppSenseAgent.milestoneVersionPush = "true".equalsIgnoreCase(milestonePushConfig.getValue());
        }
        if (this.properties.containsKey("waf.attackdiscovery.provider")) {
            final String attackDiscoveryHandlerProviderClassName = this.properties.getProperty("waf.attackdiscovery.provider");
            final ClassLoader cl4 = Thread.currentThread().getContextClassLoader();
            final AttackDiscoveryProvider attackDiscoveryProvider = (AttackDiscoveryProvider)cl4.loadClass(attackDiscoveryHandlerProviderClassName).newInstance();
            WAFAttackDiscovery.setAttackDiscoveryInfos(attackDiscoveryProvider);
        }
        if (this.properties.containsKey("waf.attackdiscovery.logstats")) {
            if ("true".equalsIgnoreCase(this.properties.getProperty("waf.attackdiscovery.logstats"))) {
                WAFAttackDiscoveryMetricRecorder.enableForWAFAgent();
            }
            else {
                WAFAttackDiscoveryMetricRecorder.disable();
            }
        }
    }
    
    private AbstractMap.SimpleEntry<Boolean, String> getProperty(final String propertyName) {
        if (AppSenseAgent.wafProperties.containsKey(propertyName)) {
            this.reinitAppendOptionSupportedProperties(propertyName);
            final AbstractMap.SimpleEntry<Boolean, String> wafconfig = new AbstractMap.SimpleEntry<Boolean, String>(true, AppSenseAgent.wafProperties.getProperty(propertyName));
            SecurityFilterProperties.logger.log(Level.INFO, "Loading Configuration from WAFfile {0} : {1}", new Object[] { propertyName, wafconfig.getValue() });
            return wafconfig;
        }
        if (this.properties.containsKey(propertyName)) {
            final AbstractMap.SimpleEntry<Boolean, String> config = new AbstractMap.SimpleEntry<Boolean, String>(true, this.properties.getProperty(propertyName));
            SecurityFilterProperties.logger.log(Level.INFO, "Loading Configuration from Security config file {0} : {1}", new Object[] { propertyName, config.getValue() });
            return config;
        }
        return new AbstractMap.SimpleEntry<Boolean, String>(false, null);
    }
    
    private void reinitAppendOptionSupportedProperties(final String propertyName) {
        if (propertyName == Components.COMPONENT_NAME.SECRET_REQ_HEADER_NAMES.getValue()) {
            this.secretRequestHeaderNames.clear();
            this.secretRequestHeaderNames.addAll(SecurityFilterProperties.defaultsecretRequestHeaderNamesList);
        }
        else if (propertyName == Components.COMPONENT_NAME.SECRET_RES_HEADER_NAMES.getValue()) {
            if (this.secretResponseHeaderNames != null) {
                this.secretResponseHeaderNames.clear();
            }
        }
        else if (propertyName == Components.COMPONENT_NAME.SECRET_REQ_PARAM_NAMES.getValue() && this.secretRequestParamNames != null) {
            this.secretRequestParamNames.clear();
        }
    }
    
    public boolean isEnabledRequestURINormalization() {
        return this.enableRequestURINormalization;
    }
    
    public boolean isAllowedDotDotSlashInRequestURI() {
        return this.allowDotDotSlashInReqURI;
    }
    
    public boolean isEnabledHostOverride() {
        return this.enableHostOverride;
    }
    
    public String getForwardedHostHeaderName() {
        return this.forwardedHostHeaderName;
    }
    
    public List<String> getAllowedForwardedHosts() {
        return this.allowedForwardedHosts;
    }
    
    public boolean isAllowedCSRFParamInQS() {
        return this.allowCSRFParamInQS;
    }
    
    public boolean addURLParamFrameoriginToRedirectURL() {
        return this.addURLParamFrameoriginToRedirectURL;
    }
    
    public float getXmlSchemaVersion() {
        return this.xmlSchemaVersion;
    }
    
    boolean isEnabledXMLSchemaVersion11() {
        return this.xmlSchemaVersion == 1.1f;
    }
    
    public boolean isEnabledXSD11CTAFullXpathChecking() {
        return this.xsd11CtaFullXpathChecking;
    }
    
    public boolean isBlockRequestOnDosCacheException() {
        return this.blockRequestOnDosCacheException;
    }
    
    public boolean isStartLiveWindowCleanerScheduler() {
        return this.startLiveWindowCleanerScheduler;
    }
    
    public boolean isEnabledCSRFSamesiteStrictTmpCookie() {
        return this.enableCSRFSamesiteStrictTmpcookie;
    }
    
    public HttpCookie.SAMESITE getCSRFCookieSamesiteMode() {
        return this.csrfCookieSamesiteMode;
    }
    
    public boolean isCSRFSamesiteStrictModeEnabledByDefault() {
        return this.csrfCookieSamesiteMode == HttpCookie.SAMESITE.STRICT;
    }
    
    public boolean isTikaFileContentAndNameBasedDetectionEnabled() {
        return this.enableTikaFileContentAndNameBasedDetection;
    }
    
    public boolean isTrimEnabled() {
        return this.trimEnabled;
    }
    
    public static void disableRedisInstrumentation() {
        SecurityFilterProperties.disableRedisInstrumentation = true;
    }
    
    public static boolean isRedisInstrumentationDisabled() {
        return SecurityFilterProperties.disableRedisInstrumentation;
    }
    
    public boolean isEnabledIndividualOccurrenceCheckForDynamicParams() {
        return this.enableIndividualOccurrenceCheckForDynamicParams;
    }
    
    public static int getPatternMatcherTimeoutInMillis() {
        return SecurityFilterProperties.patternMatcherTimeOutInMillis;
    }
    
    public static int getPatternMatcherMaxIterationCount() {
        return SecurityFilterProperties.patternMatcherMaxIterationCount;
    }
    
    public boolean maskAllParamValuesOnError() {
        return this.maskAllParamValuesOnError;
    }
    
    public boolean isInputStreamValidationLogMode() {
        return this.inputStreamValidationMode == InputStreamValidationMode.LOG;
    }
    
    public boolean isInputStreamValidationErrorMode() {
        return this.inputStreamValidationMode == InputStreamValidationMode.ERROR;
    }
    
    public boolean isInputStreamValidationModeEnabled() {
        return this.inputStreamValidationMode != null;
    }
    
    public int getDynamicParamsMaxOccurrenceLimit() {
        return this.dynamicParamsMaxOccurrenceLimit;
    }
    
    public boolean isInputStreamLogEnabled() {
        return this.logInputStream;
    }
    
    public boolean isExtraParamMaskingEnabled() {
        return this.maskExtraParam;
    }
    
    public boolean isIgnoreExtraParamMaskingEnabled() {
        return this.maskIgnoreExtraParam;
    }
    
    public boolean handleErrorPageHip() {
        return this.handleErrorPageHip;
    }
    
    public boolean handleErrorPageJsonResponse() {
        return this.handleErrorPageJsonResponse;
    }
    
    public List<String> getSecretRequestParamsFromProperty() {
        return this.secretRequestParamNames;
    }
    
    public List<String> getSecretRequestHeadersFromProperty() {
        return this.secretRequestHeaderNames;
    }
    
    public List<String> getSecretResponseHeadersFromProperty() {
        return this.secretResponseHeaderNames;
    }
    
    public boolean allowProxyURLRedirect() {
        return this.allowProxyURLRedirect;
    }
    
    public int getURLRedirectMaxLimit() {
        return this.urlRedirectMaxLimit;
    }
    
    public boolean isSetAPIRateLimitResponseHeader() {
        return this.isSetAPIRateLimitResponseHeader;
    }
    
    public boolean isDisabledOldThrottleConfig() {
        return this.disableOldThrottleConfig;
    }
    
    public boolean isEnabledPostMethodRedirect() {
        return this.enablePostMethodRedirect;
    }
    
    public int[] getCaptchaImageDimensions() {
        return this.captchaImageDimensions;
    }
    
    public boolean isIgnoreExtraParam() {
        return this.ignoreExtraParam;
    }
    
    public boolean isEnableHSTS() {
        return this.enableHSTS;
    }
    
    public boolean enableHSTSRedirection() {
        return this.enableHSTSRedirection;
    }
    
    public String[] getHSTSAllowedDomains() {
        return this.hstsAlloweddomains;
    }
    
    public boolean isAllowImportURLRedirect() {
        return this.allowImportURLRedirect;
    }
    
    public boolean dbCacheForServiceScopeThrottles() {
        return this.dbCacheForServiceScopeThrottles;
    }
    
    private void loadRunTimeCacheConfigurations() {
        final List<CacheConfiguration> configurations = this.provider.getCacheConfigurations();
        if (configurations == null) {
            return;
        }
        for (final CacheConfiguration configuration : configurations) {
            this.addCacheConfiguration(configuration);
        }
    }
    
    private void validateCacheConfigurations() {
        if (this.cacheConfigurationMap == null) {
            return;
        }
        for (final List<CacheConfiguration> cacheConfigurations : this.cacheConfigurationMap.values()) {
            for (final CacheConfiguration cacheConfiguration : cacheConfigurations) {
                try {
                    RedisCacheAPI.ping(cacheConfiguration);
                }
                catch (final Exception e) {
                    SecurityFilterProperties.logger.log(Level.SEVERE, "Invalid {0}, Error Msg: {1}", new Object[] { cacheConfiguration, e.getMessage() });
                }
            }
        }
    }
    
    private void initInlineVariables() {
        if (this.cacheConfigurationMap != null) {
            final List<CacheConfiguration> cacheConfigurations = this.cacheConfigurationMap.get(CacheConstants.PoolNames.HIP.name());
            if (cacheConfigurations != null) {
                DbCacheRollingAccessInfo.setRedisCacheConfigurationOfHip(this.captchaCache = cacheConfigurations.get(0));
            }
        }
    }
    
    public void addCacheConfiguration(final CacheConfiguration cacheConfiguration) {
        if (this.cacheConfigurationMap == null) {
            this.cacheConfigurationMap = new HashMap<String, List<CacheConfiguration>>();
        }
        List<CacheConfiguration> cacheConfigurationList = this.cacheConfigurationMap.get(cacheConfiguration.getPoolName());
        if (cacheConfigurationList == null) {
            cacheConfigurationList = new ArrayList<CacheConfiguration>();
            this.cacheConfigurationMap.put(cacheConfiguration.getPoolName(), cacheConfigurationList);
        }
        cacheConfigurationList.add(cacheConfiguration);
    }
    
    public List<CacheConfiguration> getCacheConfigurationList(final String poolName) {
        return (this.cacheConfigurationMap != null) ? this.cacheConfigurationMap.get(poolName) : null;
    }
    
    public Map<String, List<CacheConfiguration>> getCacheConfigurationMap() {
        return this.cacheConfigurationMap;
    }
    
    protected CacheConfiguration getCaptchaCache() {
        return this.captchaCache;
    }
    
    public void addContentTypeRule(final ContentTypeRule contentTypeRule) {
        this.contentTypeRuleMap.put(contentTypeRule.getName(), contentTypeRule);
    }
    
    public ContentTypeRule getContentTypeRule(final String name) {
        return this.contentTypeRuleMap.get(name);
    }
    
    public boolean isDisabledServiceURLThrottles() {
        return this.disableServiceURLThrottles;
    }
    
    public boolean isConvertServiceUrlRollingThrottlesIntoAppserver() {
        return this.convertServiceUrlRollingThrottlesIntoAppserver;
    }
    
    public boolean isEnabledPatchMethodMinOccurCheck() {
        return this.enablePatchMethodMinOccurCheck;
    }
    
    public RequestHeaderValidationMode getReqHeaderValidationMode() {
        return this.reqHeaderValidationMode;
    }
    
    public boolean isRequestHeaderValidationEnabled() {
        return RequestHeaderValidationMode.DISABLE != this.reqHeaderValidationMode;
    }
    
    public RequestHeaderValidationMode getInternalReqHeaderValidationMode() {
        return this.internalReqHeaderValidationMode;
    }
    
    public ErrorPageValidationMode getErrorPageValidationMode() {
        return this.errorPageValidationMode;
    }
    
    public static Proxy getProxy() {
        return SecurityFilterProperties.proxy;
    }
    
    public boolean isEnableXHTTPMethodOverrideOption() {
        return this.enableXHTTPMethodOverrideOption;
    }
    
    public boolean isDisablePathParameterURIDecodingCheck() {
        return this.disablePathParamURIDecodingCheck;
    }
    
    public Map<String, RegexRule> getRegexes() {
        return this.regexRuleMap;
    }
    
    public String getRequestEncoding() {
        return this.requestEncoding;
    }
    
    public void setRequestEncoding(final String encoding) {
        this.requestEncoding = encoding;
    }
    
    public String getResponseEncoding() {
        return this.responseEncoding;
    }
    
    public void setResponseEncoding(final String encoding) {
        this.responseEncoding = encoding;
    }
    
    public boolean ignoreTrailingSlash() {
        return this.ignoreTrailingSlash;
    }
    
    public void ignoreTrailingSlash(final boolean ignore) {
        this.ignoreTrailingSlash = ignore;
    }
    
    public boolean isXSSFilterLogEnabled() {
        return this.enableXSSFilterLog;
    }
    
    public void enableXSSFilterLog(final boolean status) {
        this.enableXSSFilterLog = status;
    }
    
    public boolean isXSSPatternDetectEnabled() {
        return this.enableXSSPatternDetect;
    }
    
    public void enableXSSPatternDetect(final boolean status) {
        this.enableXSSPatternDetect = status;
    }
    
    public boolean isPutMinoccurCheckDisabled() {
        return this.disablePutMinoccurCheck;
    }
    
    public boolean isXSSPatternDetectForFilterDisabled() {
        return this.disableXSSPatternDetectForFilter;
    }
    
    public static String getProxyHost() {
        return SecurityFilterProperties.proxyHost;
    }
    
    public static String getProxyPort() {
        return SecurityFilterProperties.proxyPort;
    }
    
    public static String getProxyUserName() {
        return SecurityFilterProperties.proxyUserName;
    }
    
    public static String getProxyPassword() {
        return SecurityFilterProperties.proxyPassword;
    }
    
    public String getErrorLabel() {
        return this.labelStr;
    }
    
    public HashMap<String, String> getDefaultLabelMap() {
        return this.defaultLabelMap;
    }
    
    public String getLabelMessage(final String errorCode) {
        if (this.defaultLabelMap != null) {
            return this.defaultLabelMap.get(errorCode);
        }
        return null;
    }
    
    public boolean isCaptchURL(final HttpServletRequest request) {
        final String uri = SecurityUtil.getRequestPath(request);
        return this.showCaptchaURL.equals(uri);
    }
    
    public boolean isDevelopmentMode() {
        return this.developmentMode;
    }
    
    public boolean isTestMode() {
        return this.testMode;
    }
    
    public static boolean isUsingIAMAuth() {
        return SecurityFilterProperties.useIAM;
    }
    
    public static boolean isUsingIAMImpl() {
        return SecurityFilterProperties.usingIAMImpl;
    }
    
    public boolean isEnableTimeoutMatcher() {
        return this.enableTimeoutMatcher;
    }
    
    public boolean isEnableXSSTimeoutMatcher() {
        return this.enableXSSTimeoutMatcher;
    }
    
    void addExtraParamRule(final ParameterRule rule) {
        this.extraParamRule = rule;
    }
    
    public ParameterRule getExtraParamRule() {
        return this.extraParamRule;
    }
    
    protected void addExtraJSONKeyRule(final ParameterRule rule) {
        this.extraJSONKeyRule = rule;
    }
    
    protected ParameterRule getExtraJSONKeyRule() {
        return this.extraJSONKeyRule;
    }
    
    public String[] getConfiguredFilePaths() {
        return this.filePaths;
    }
    
    public List<String> getDefaultConfigurationsFiles() {
        return SecurityFilterProperties.defaultConfigFiles;
    }
    
    public void addProxyURL(final Element element) {
        final ProxyURL pu = new ProxyURL(element, this.properties);
        if (pu.isProxyURLInRegex()) {
            this.proxyURLPatterns.put(pu.getPathPattern(), pu);
        }
        else {
            this.proxyURLs.put(pu.getPath(), pu);
        }
        SecurityFilterProperties.logger.log(Level.FINE, "Added proxy url {0}", pu);
    }
    
    public ProxyURL getProxyURL(final HttpServletRequest request) {
        String uri = SecurityUtil.getRequestURI(request);
        uri = SecurityUtil.ignoreURIPrefixAndTrailingSlash(uri, this);
        ProxyURL resultProxyURL = this.getProxyURL(uri);
        if (resultProxyURL == null) {
            return null;
        }
        if (!this.proxyURLValidationStatus) {
            this.validateProxyRules();
            this.proxyURLValidationStatus = true;
            resultProxyURL = this.getProxyURL(uri);
        }
        return resultProxyURL;
    }
    
    private ProxyURL getProxyURL(final String uri) {
        ProxyURL resultProxyURL = this.proxyURLs.get(uri);
        if (resultProxyURL == null) {
            for (final Map.Entry<Pattern, ProxyURL> entrySet : this.proxyURLPatterns.entrySet()) {
                final Pattern urlPattern = entrySet.getKey();
                if (SecurityUtil.matchPattern(uri, urlPattern, SecurityFilterProperties.patternMatcherTimeOutInMillis, SecurityFilterProperties.patternMatcherMaxIterationCount)) {
                    resultProxyURL = entrySet.getValue();
                    break;
                }
            }
        }
        return resultProxyURL;
    }
    
    synchronized void validateProxyRules() {
        this.validateProxyRules(false);
    }
    
    void validateProxyRules(final boolean isInit) {
        if (this.authProvider != null) {
            this.validateRemoteServer(new LinkedList<ProxyURL>(this.proxyURLs.values()), isInit);
            this.validateRemoteServer(new LinkedList<ProxyURL>(this.proxyURLPatterns.values()), isInit);
        }
    }
    
    private void validateRemoteServer(final Collection<ProxyURL> proxyRules, final boolean isInit) {
        for (final ProxyURL proxyrule : proxyRules) {
            if (proxyrule.allowCookie()) {
                if (!proxyrule.allowHeaders()) {
                    continue;
                }
                final String remoteServer = proxyrule.getRemoteServer();
                try {
                    final String domain = SecurityUtil.getDomainWithPort(remoteServer);
                    if (SecurityFilterProperties.MAIN_ZOHO_DOMAINS.matcher(domain).matches() || this.authProvider.isTrustedDomain(domain)) {
                        continue;
                    }
                    if (isInit) {
                        SecurityFilterProperties.logger.log(Level.SEVERE, " Invalid Proxy Configuration : remote-server [{0} ] configured for the path {1} does not matched with IAM trusted domain list ", new Object[] { remoteServer, proxyrule.getPath() });
                        throw new IAMSecurityException("INVALID_PROXY_CONFIGURATION");
                    }
                    SecurityFilterProperties.logger.log(Level.SEVERE, " Invalid Proxy Configuration : remote-server [{0} ] configured for the path {1} does not matched with IAM trusted domain list and removed from the proxyurl map ", new Object[] { remoteServer, proxyrule.getPath() });
                    if (proxyrule.isProxyURLInRegex()) {
                        this.proxyURLPatterns.remove(proxyrule.getPathPattern());
                    }
                    else {
                        this.proxyURLs.remove(proxyrule.getPath());
                    }
                }
                catch (final IAMSecurityException e) {
                    throw e;
                }
                catch (final Exception e2) {
                    SecurityFilterProperties.logger.log(Level.SEVERE, " ZSEC PROXYURL : Proxy URL Validation failure : Cause IAM is Down");
                    if (isInit) {
                        this.proxyURLValidationStatus = false;
                        break;
                    }
                    throw e2;
                }
            }
        }
    }
    
    boolean allowEmptyValue() {
        return this.allowEmptyValue;
    }
    
    boolean allowInvalidValue() {
        return this.allowInvalidValue;
    }
    
    boolean isInvalidJSONContentBlocked(final JSON_INVALID_VALUE_TYPE invalidToken) {
        return this.json_Invalid_Values_List.contains(invalidToken);
    }
    
    public boolean reInitConfiguration() throws Exception {
        long lastModifiedTime = -1L;
        for (final File f : this.securityFiles) {
            if (lastModifiedTime < f.lastModified()) {
                lastModifiedTime = f.lastModified();
            }
        }
        if (this.lastUpdatedTime < lastModifiedTime) {
            this.reInit(lastModifiedTime);
            return true;
        }
        return false;
    }
    
    public synchronized void reInit(final long lastModifiedTime) throws Exception {
        if (this.lastUpdatedTime >= lastModifiedTime) {
            return;
        }
        this.regexRuleMap = new HashMap<String, RegexRule>();
        this.urlRuleMap = new HashMap<String, URLRule>();
        this.urlPatternRuleMap = new LinkedHashMap<Pattern, URLRule>();
        this.allUrlRules = new HashMap<String, URLRule>();
        this.xssUtilMap = new HashMap<String, XSSUtil>();
        this.jsonTemplateRuleMap = new HashMap<String, JSONTemplateRule>();
        this.templateRuleMap = new HashMap<String, TemplateRule>();
        this.paramGroupRuleMap = new HashMap<String, List<ParameterRule>>();
        this.jsonKeyGroupRuleMap = new HashMap<String, List<ParameterRule>>();
        this.paramGroupCriteriaRuleMap = null;
        this.jsonKeyGroupCriteriaRuleMap = null;
        this.contentTypes = new HashMap<String, Pattern>();
        this.contentTypesXSS = new HashMap<String, String>();
        this.proxyURLs = new HashMap<String, ProxyURL>();
        this.proxyURLPatterns = new LinkedHashMap<Pattern, ProxyURL>();
        this.allProxyURLRules = new ArrayList<ProxyURL>();
        SecurityFilterProperties.commonRegexRuleMap = new HashMap<String, RegexRule>();
        this.safeResponseHeadersMap = new HashMap<String, List<ResponseHeaderRule>>();
        this.defaultDisableSafeHeaders = new ArrayList<String>();
        this.defaultResponseHeadersList = new ArrayList<ResponseHeaderRule>();
        this.urlValidatorRuleMap = new HashMap<String, URLValidatorRule>();
        this.zipSanitizerRuleMap = new HashMap<String, ZipSanitizerRule>();
        this.allowedServicesViaProxy = null;
        this.xmlSchemaRuleMap = null;
        this.defaultRequestHeadersRuleWithStrictName = new HashMap<String, HeaderRule>();
        this.defaultRequestHeadersRuleWithRegexName = new HashMap<String, HeaderRule>();
        this.defaultSecretRequestHeaderNames = new ArrayList<String>();
        this.internalRequestHeadersWithStrictName = new HashMap<String, HeaderRule>();
        this.internalSecretRequestHeaderNames = new ArrayList<String>();
        this.exceptionRuleMap = new HashMap<String, ExceptionRule>();
        this.piiDetectorRule = null;
        SecurityFilterProperties.piiDetector = null;
        this.avConfig = null;
        this.vendorAvProvider = null;
        SecurityRequestWrapper.clearDefaultParamConfiguration();
        for (final File file : this.securityFiles) {
            RuleSetParser.initSecurityRules(this, file);
        }
        this.lastUpdatedTime = System.currentTimeMillis();
        this.initAccountsandInlineSecurityRules();
        this.initPiiDetector();
        this.tempFileUploadDirMonitoring.reinit();
        this.initWAFControlConfigurations();
    }
    
    public static boolean isTrustedIP(final String ipAddress) {
        return SecurityFilterProperties.trustedIPPattern != null && SecurityUtil.matchPattern(ipAddress, SecurityFilterProperties.trustedIPPattern);
    }
    
    public void addXSSUtil(final String xssPatternName, final XSSUtil xssUtil) {
        if (this.getXSSUtil(xssPatternName) == null) {
            this.xssUtilMap.put(xssPatternName, xssUtil);
            return;
        }
        throw new RuntimeException("XSS Element Remover with name '" + xssPatternName + "' is already added");
    }
    
    public XSSUtil getXSSUtil(final String xssPatternName) {
        return this.xssUtilMap.get(xssPatternName);
    }
    
    public void initAccountsandInlineSecurityRules() {
        this.initInlineContentTypeRules();
        final Collection<URLRule> urlRules = this.getAllURLRule();
        for (final URLRule urlRule : urlRules) {
            final Collection<ActionRule> actionRules = urlRule.getAllActionRule();
            for (final ActionRule rule : actionRules) {
                this.initURLParamInlineRegexes(rule);
                this.initAccountsAttributes(rule);
                this.addSafeAndDefaultResponseHeaders(rule);
                this.initURLRequestHeaderInlineRegexes(rule);
                this.attachRegexPatternToRequestHeaderName(rule.getRequestHeaderMapWithRegexName(), rule.getCookieRule());
                this.addAllowedServicesViaProxy(rule);
                if (rule.isCaptchaVerificationEnabled() && !SecurityUtil.isValidList(this.getCacheConfigurationList(CacheConstants.PoolNames.HIP.name()))) {
                    throw new RuntimeException("Should configure the <cache-configuration> rule with the pool-name 'HIP' if the <urls> or <url> rule has 'captcha-verification' as 'true'.");
                }
                if (SecurityUtil.isValidMap(this.commonThrottlesRuleMap)) {
                    rule.addCommonThrottlesRules(this.commonThrottlesRuleMap);
                    final List<ThrottlesRule> rollingThrottleRules = this.commonThrottlesRuleMap.get(ThrottlesRule.Windows.ROLLING);
                    if (rollingThrottleRules != null) {
                        for (final ThrottlesRule rollingThrottleRule : rollingThrottleRules) {
                            if (ActionRule.isSkipHipDigestParamFromExtraParamValidation(rollingThrottleRule)) {
                                rule.skipHipDigestParamFromExtraParamValidation = true;
                                break;
                            }
                        }
                    }
                }
                if (this.isTestMode()) {
                    rule.throttlesRuleMap = null;
                    rule.disableDynamicThrottles();
                }
                try {
                    this.validateFileRuleInlineConfiguration(rule);
                }
                catch (final Exception e) {
                    String logMsg = "Invalid configuration at the <url> rule. Path: {0}, Method: {1}";
                    logMsg = ((rule.getOperationParam() != null) ? (logMsg + ", OperationParam: {2}") : logMsg);
                    logMsg = ((rule.getOperationValue() != null) ? (logMsg + ", OperationValue: {3}") : logMsg);
                    SecurityFilterProperties.logger.log(Level.SEVERE, logMsg, new Object[] { rule.getPath(), rule.getMethod(), rule.getOperationParam(), rule.getOperationValue() });
                    throw e;
                }
            }
        }
        this.initTemplateRuleInlineRegexes();
        this.initDefaultParamInlineRegexes();
        this.initXSSUtils();
        this.attachRegexPatternToRequestHeaderName(this.defaultRequestHeadersRuleWithRegexName, this.defaultCookieRule);
        this.attachRegexPatternToRequestHeaderName(null, this.internalCookieRule);
        if (RequestHeaderValidationMode.DISABLE != this.internalReqHeaderValidationMode) {
            if (this.internalRequestHeadersWithStrictName.containsKey(this.csrf_header_name)) {
                final ParameterRule csrfHeaderRule = this.internalRequestHeadersWithStrictName.get(this.csrf_header_name).getHeaderRule();
                if (csrfHeaderRule.getAllowedValueRegex().contains("${CSRF_PARAM}")) {
                    csrfHeaderRule.setAllowedValueRegex(csrfHeaderRule.getAllowedValueRegex().replace("${CSRF_PARAM}", this.csrf_param_name));
                }
            }
            final ParameterRule cookieRule = new ParameterRule(this.csrf_cookie_name, this.csrf_cookie_value_regex, 1, 200);
            cookieRule.setSecret(true);
            cookieRule.storeParameterValue = false;
            if (this.internalCookieRule != null) {
                this.internalCookieRule.getCookieMapWithStrictName().put(this.csrf_cookie_name, cookieRule);
            }
        }
        this.initRequestHeaderInlineRegexes();
        this.initXMLSchemaRules();
    }
    
    private void initAccountsAttributes(final ActionRule rule) {
        if (this.getAuthenticationProvider() != null) {
            this.getAuthenticationProvider().initAccountsAttributes(rule);
        }
    }
    
    private void initXMLSchemaRules() {
        if (this.xmlSchemaRuleMap != null) {
            for (final XMLSchemaRule schemaRule : this.xmlSchemaRuleMap.values()) {
                schemaRule.initialiseXMLSchema(this);
            }
        }
    }
    
    public void addRequiredCachePoolNames(final ThrottlesRule throttlesRule) {
        if (throttlesRule.getScope() == ThrottlesRule.Scopes.SERVICE) {
            if (this.requiredCachePoolNames == null) {
                this.requiredCachePoolNames = new HashSet<String>();
            }
            this.requiredCachePoolNames.add(throttlesRule.getWindow().name());
            if (throttlesRule.getWindow() == ThrottlesRule.Windows.ROLLING && !this.requiredCachePoolNames.contains(CacheConstants.PoolNames.HIP.name()) && ActionRule.isSkipHipDigestParamFromExtraParamValidation(throttlesRule)) {
                this.requiredCachePoolNames.add(CacheConstants.PoolNames.HIP.name());
            }
        }
    }
    
    private void checkRequiredCacheConfigured() {
        if (this.requiredCachePoolNames != null) {
            final Set<String> unConfiguredPoolNames = new HashSet<String>();
            for (final String requiredCachePoolName : this.requiredCachePoolNames) {
                if (this.getCacheConfigurationList(requiredCachePoolName) == null) {
                    unConfiguredPoolNames.add(requiredCachePoolName);
                }
            }
            if (!unConfiguredPoolNames.isEmpty()) {
                SecurityFilterProperties.logger.log(Level.SEVERE, "Should configure the <cache-configuration> with the pool-names: {0} ", new Object[] { unConfiguredPoolNames });
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
            this.requiredCachePoolNames = null;
        }
    }
    
    private void validateFileRuleInlineConfiguration(final ActionRule actionRule) {
        for (final UploadFileRule uploadFileRule : actionRule.getUploadFileRuleList()) {
            try {
                this.validateAllowedContentTypesConf(uploadFileRule);
                this.validateContentTypeNameConf(uploadFileRule);
                if (actionRule.getFileUploadMaxSizeInKB() == -1L && actionRule.getMaxRequestSize() == -1L && uploadFileRule.getMaxSizeInKB() == -1L) {
                    throw new RuntimeException("Should configure either \"file-upload-max-size\" or \"max-request-size\" in <url> rule or should configure \"max-size\" in <file> rule.");
                }
                continue;
            }
            catch (final Exception e) {
                SecurityFilterProperties.logger.log(Level.SEVERE, "Invalid configuration at the <file> rule. Name: {0}.", new Object[] { uploadFileRule.getFieldName() });
                throw e;
            }
        }
    }
    
    private void validateAllowedContentTypesConf(final UploadFileRule uploadFileRule) {
        if (!uploadFileRule.isAllowedContentTypeRuleConfigured()) {
            return;
        }
        for (final String allowedContentTypeName : uploadFileRule.getAllowedContentTypes()) {
            if (this.getContentTypeRule(allowedContentTypeName) == null) {
                throw new RuntimeException("The 'allowed-content-types' attribute has the content-type rule name '" + allowedContentTypeName + "' is not defined.");
            }
        }
    }
    
    private void validateContentTypeNameConf(final UploadFileRule uploadFileRule) {
        if (uploadFileRule.getAllowedContentTypesName() == null) {
            return;
        }
        for (final String allowedContentTypeName : uploadFileRule.getAllowedContentTypesName()) {
            if (this.getContentTypes(allowedContentTypeName) == null) {
                throw new RuntimeException("The 'content-type-name' attribute has the content-type rule name '" + allowedContentTypeName + "' is not defined.");
            }
        }
    }
    
    private void initInlineContentTypeRules() throws RuntimeException {
        for (final ContentTypeRule rule : this.contentTypeRuleMap.values()) {
            rule.initExtendedContentTypeRulesIfPresent(this.contentTypeRuleMap);
            rule.validateConfigurationAgainstTika();
        }
    }
    
    private void initURLRequestHeaderInlineRegexes(final ActionRule rule) {
        this.addRequestHeaderInlineRegexes(rule.getRequestHeaderMapWithStrictName(), rule.getRequestHeaderMapWithRegexName());
        final CookieRequestHeaderRule cookieRule = rule.getCookieRule();
        if (cookieRule != null) {
            this.addCookieRuleInlineRegexes(cookieRule);
        }
    }
    
    private void initRequestHeaderInlineRegexes() {
        this.addRequestHeaderInlineRegexes(this.defaultRequestHeadersRuleWithStrictName, this.defaultRequestHeadersRuleWithRegexName, this.internalRequestHeadersWithStrictName);
        this.addCookieRuleInlineRegexes(this.internalCookieRule, this.defaultCookieRule);
    }
    
    private void addCookieRuleInlineRegexes(final CookieRequestHeaderRule... cookieRules) {
        for (final CookieRequestHeaderRule cookieRule : cookieRules) {
            if (cookieRule != null) {
                this.addCookieRuleInlineRegexes(cookieRule.getCookieMapWithStrictName().values(), cookieRule.getCookieMapWithRegexName().values());
            }
        }
    }
    
    private void addCookieRuleInlineRegexes(final Collection<ParameterRule>... paramRuleList) {
        for (final Collection<ParameterRule> paramRules : paramRuleList) {
            for (final ParameterRule paramRule : paramRules) {
                this.addParamRegexes(paramRule);
            }
        }
    }
    
    private void addRequestHeaderInlineRegexes(final Map<String, HeaderRule>... requestHeadersRuleMap) {
        for (final Map<String, HeaderRule> headerRuleMap : requestHeadersRuleMap) {
            for (final HeaderRule headerRule : headerRuleMap.values()) {
                this.addParamRegexes(headerRule.getHeaderRule());
            }
        }
    }
    
    private void addAllowedServicesViaProxy(final ActionRule rule) {
        final List<String> allowedServices = rule.getAllowedServicesViaProxy();
        if (allowedServices == null) {
            rule.setAllowedServicesViaProxy(this.allowedServicesViaProxy);
        }
        else if (this.allowedServicesViaProxy != null) {
            for (final String service : this.allowedServicesViaProxy) {
                if (!allowedServices.contains(service)) {
                    allowedServices.add(service);
                }
            }
        }
    }
    
    private void attachRegexPatternToRequestHeaderName(final Map<String, HeaderRule> requestHeaderMapWithRegexName, final CookieRequestHeaderRule cookieRule) {
        if (requestHeaderMapWithRegexName != null && requestHeaderMapWithRegexName.size() > 0) {
            for (final String headerNameRegex : requestHeaderMapWithRegexName.keySet()) {
                Pattern headerRegexValue = null;
                if ((headerRegexValue = this.getRegexPattern(headerNameRegex)) != null) {
                    requestHeaderMapWithRegexName.get(headerNameRegex).getHeaderRule().setParamNameInRegex(headerRegexValue);
                }
            }
        }
        if (cookieRule != null) {
            for (final String cookieNameRegex : cookieRule.getCookieMapWithRegexName().keySet()) {
                Pattern cookieRegexValue = null;
                if ((cookieRegexValue = this.getRegexPattern(cookieNameRegex)) != null) {
                    cookieRule.getCookieMapWithRegexName().get(cookieNameRegex).setParamNameInRegex(cookieRegexValue);
                }
            }
        }
    }
    
    private void initURLParamInlineRegexes(final ActionRule rule) {
        this.addParamRegexes(rule.getParameterRules().values());
        for (final UploadFileRule fileRule : rule.getUploadFileRuleList()) {
            final ParameterRule fileNameRule = fileRule.getFileNameRule();
            if (fileNameRule != null) {
                this.addParamRegexes(fileNameRule);
            }
        }
    }
    
    private void initTemplateRuleInlineRegexes() {
        final Collection<JSONTemplateRule> templateRules = this.jsonTemplateRuleMap.values();
        for (final JSONTemplateRule templateRule : templateRules) {
            this.addParamRegexes(templateRule.getKeyValueRule().values());
            this.addParamRegexes(templateRule.getParamNameRegexKeyValueRule().values());
            this.addParamRegexes(templateRule.getJsonArrayIndexMap().values());
        }
    }
    
    private void initDefaultParamInlineRegexes() {
        this.addParamRegexes(SecurityRequestWrapper.getDefaultParameters().values());
    }
    
    private void addParamRegexes(final Collection<ParameterRule> paramRules) {
        for (final ParameterRule paramRule : paramRules) {
            if (paramRule.isParamNameInRegex()) {
                final String paramNameRegex = paramRule.getParamName();
                Pattern regexPattern = this.getRegexPattern(paramNameRegex);
                if (regexPattern == null) {
                    final RegexRule regexRule = new RegexRule(paramNameRegex, paramNameRegex);
                    this.regexRuleMap.put(paramNameRegex, regexRule);
                    regexPattern = regexRule.getPattern();
                }
                paramRule.setParamNameInRegex(regexPattern);
                if (!this.isEnabledIndividualOccurrenceCheckForDynamicParams() && !paramRule.isMaxOccurrenceConfigured) {
                    paramRule.setMaxOccurrences(this.dynamicParamsMaxOccurrenceLimit);
                }
            }
            this.addParamRegexes(paramRule);
            final String dynamicTempKeyRegex = paramRule.getDynamicTemplateKeyRegex();
            if (SecurityUtil.isValid(dynamicTempKeyRegex) && this.getRegexPattern(dynamicTempKeyRegex) == null) {
                this.addRegularExpressions(dynamicTempKeyRegex, dynamicTempKeyRegex);
            }
        }
    }
    
    private void addParamRegexes(final ParameterRule paramRule) {
        final String regex = paramRule.getAllowedValueRegex();
        if (SecurityUtil.isValid(regex) && this.getRegexPattern(regex) == null) {
            this.addRegularExpressions(regex, regex);
        }
    }
    
    public void initXSSUtils() {
        if (this.xssUtilMap != null) {
            final Collection<XSSUtil> xssUtils = this.xssUtilMap.values();
            for (final XSSUtil xssUtil : xssUtils) {
                final String extendsXssFilter = xssUtil.getExtendsFilter();
                if (extendsXssFilter != null) {
                    final XSSUtil extendsXssUtil = this.xssUtilMap.get(extendsXssFilter);
                    if (extendsXssUtil != null && !extendsXssUtil.isFilterExtended()) {
                        xssUtil.extendFilterProps(extendsXssUtil);
                    }
                }
                if (this.maxLogSize != -1) {
                    xssUtil.setMaxLogSize(this.maxLogSize);
                }
            }
        }
    }
    
    private void addSafeAndDefaultResponseHeaders(final ActionRule actionRule) {
        final Map<String, ResponseHeaderRule> responseHeaderMap = actionRule.getResponseHeaderRules();
        final List<String> excludeSafeHeaders = actionRule.getDisabledSafeHeaders();
        final List<String> excludeCustomHeaders = actionRule.getDisabledHeaders();
        if (excludeSafeHeaders.size() == 0) {
            excludeSafeHeaders.addAll(this.getDefaultDisableSafeHeaders());
            if (excludeSafeHeaders.contains("all")) {
                this.disableAllSafeHeaders(excludeSafeHeaders);
            }
        }
        if (!excludeCustomHeaders.contains("all")) {
            this.mergeHeadersWithURL(this.getDefaultResponseHeaderRules(), excludeSafeHeaders, excludeCustomHeaders, responseHeaderMap);
        }
        if (this.isEnableXFrameOptions()) {
            if (!responseHeaderMap.containsKey("X-Frame-Options") && !excludeSafeHeaders.contains("X-Frame-Options") && !excludeCustomHeaders.contains("X-Frame-Options")) {
                final String xframeType = (actionRule.getXframeType() == null) ? this.getDefaultXFrameOption() : actionRule.getXframeType();
                if ("none".equalsIgnoreCase(xframeType)) {
                    excludeSafeHeaders.add("X-Frame-Options");
                }
                else {
                    responseHeaderMap.put("X-Frame-Options", new ResponseHeaderRule("X-Frame-Options", xframeType));
                }
            }
        }
        else {
            excludeSafeHeaders.add("X-Frame-Options");
        }
        if (!SecurityFilterProperties.useIAM && responseHeaderMap.containsKey("X-Frame-Options") && "trusted".equalsIgnoreCase(responseHeaderMap.get("X-Frame-Options").getHeaderValue())) {
            throw new RuntimeException("X-Frame-Options - trusted is not allowed for NON IAM Services");
        }
        if (responseHeaderMap.containsKey("X-Frame-Options")) {
            final String headerVal = responseHeaderMap.get("X-Frame-Options").getHeaderValue().toLowerCase();
            if (!ResponseHeader.XFRAME_OPTIONS_ZSTD_VALUES.contains(headerVal) && !ResponseHeader.XFRAME_OPTIONS_STD_VALUES.contains(headerVal) && !headerVal.startsWith("allow-from")) {
                throw new RuntimeException("X-Frame-Options - Invalid value is configured  - Allowed values : [trusted, trusted-list, trusted|trusted-list, enableontrusted, enableontrustedlist, sameorigin, deny, allow-from]");
            }
        }
        this.mergeSafeHeadersWithURL("secure-headers", excludeSafeHeaders, excludeCustomHeaders, responseHeaderMap);
        if (responseHeaderMap.containsKey("Content-Type") && responseHeaderMap.get("Content-Type").getHeaderValue().toLowerCase().startsWith("application/json")) {
            this.mergeSafeHeadersWithURL("json-headers", excludeSafeHeaders, excludeCustomHeaders, responseHeaderMap);
        }
        if (responseHeaderMap.containsKey("Content-Disposition") && responseHeaderMap.get("Content-Disposition").getHeaderValue().toLowerCase().startsWith("attachment")) {
            this.mergeSafeHeadersWithURL("download-headers", excludeSafeHeaders, excludeCustomHeaders, responseHeaderMap);
        }
    }
    
    private void mergeSafeHeadersWithURL(final String key, final List<String> excludeSafeHeaders, final List<String> excludeCustomHeaders, final Map<String, ResponseHeaderRule> urlResponseHeaderMap) {
        final List<ResponseHeaderRule> safeResponseHeaderRules = this.safeResponseHeadersMap.get(key);
        if (safeResponseHeaderRules != null) {
            this.mergeHeadersWithURL(safeResponseHeaderRules, excludeSafeHeaders, excludeCustomHeaders, urlResponseHeaderMap);
        }
    }
    
    private void mergeHeadersWithURL(final List<ResponseHeaderRule> headerRules, final List<String> excludeSafeHeaders, final List<String> excludeCustomHeaders, final Map<String, ResponseHeaderRule> urlResponseHeaderMap) {
        for (final ResponseHeaderRule headerRule : headerRules) {
            if (!urlResponseHeaderMap.containsKey(headerRule.getHeaderName()) && !excludeSafeHeaders.contains(headerRule.getHeaderName()) && !excludeCustomHeaders.contains(headerRule.getHeaderName())) {
                urlResponseHeaderMap.put(headerRule.getHeaderName(), headerRule);
            }
        }
    }
    
    void disableAllSafeHeaders(final List<String> excludeSafeHeaders) {
        this.disableSafeHeaders(excludeSafeHeaders, this.getSafeResponseHeaderRules("secure-headers"));
        this.disableSafeHeaders(excludeSafeHeaders, this.getSafeResponseHeaderRules("json-headers"));
        this.disableSafeHeaders(excludeSafeHeaders, this.getSafeResponseHeaderRules("download-headers"));
    }
    
    private void disableSafeHeaders(final List<String> excludeSafeHeaders, final List<ResponseHeaderRule> safeResponseHeaderRules) {
        for (final ResponseHeaderRule headerRule : safeResponseHeaderRules) {
            if (!excludeSafeHeaders.contains(headerRule.getHeaderName())) {
                excludeSafeHeaders.add(headerRule.getHeaderName());
            }
        }
    }
    
    public Collection<XSSUtil> getXSSUtils() {
        return (this.xssUtilMap != null) ? this.xssUtilMap.values() : null;
    }
    
    public void addRegularExpressions(final Properties prop) {
        this.addRegularExpressions(prop, null);
    }
    
    public void addRegularExpressions(final Properties prop, final String fileName) {
        final boolean loadCommonRegexes = SecurityFilterProperties.commonRegexRuleMap.isEmpty() && "security-common.xml".equals(fileName);
        for (final Map.Entry entry : prop.entrySet()) {
            final String name = entry.getKey();
            final String value = entry.getValue();
            final RegexRule rule = new RegexRule(name, value);
            this.regexRuleMap.put(name, rule);
            if (loadCommonRegexes) {
                SecurityFilterProperties.commonRegexRuleMap.put(name, rule);
            }
        }
    }
    
    void addRegularExpressions(final List<RegexRule> regexRules, final String fileName) {
        final boolean loadCommonRegexes = SecurityFilterProperties.commonRegexRuleMap.isEmpty() && "security-common.xml".equals(fileName);
        for (final RegexRule rule : regexRules) {
            this.regexRuleMap.put(rule.getName(), rule);
            if (loadCommonRegexes) {
                SecurityFilterProperties.commonRegexRuleMap.put(rule.getName(), rule);
            }
        }
    }
    
    public void addRegularExpressions(final String name, final String pattern) {
        this.regexRuleMap.put(name, new RegexRule(name, pattern));
    }
    
    public void addProperties(final Properties prop) {
        this.addProperties(prop, null);
    }
    
    void addProperties(final Properties prop, final String category) {
        if (prop != null) {
            for (final String key : ((Hashtable<Object, V>)prop).keySet()) {
                if ("WAF".equals(category)) {
                    ((Hashtable<String, String>)AppSenseAgent.wafProperties).put(key, prop.getProperty(key));
                }
                else {
                    ((Hashtable<String, String>)this.properties).put(key, prop.getProperty(key));
                }
            }
        }
    }
    
    public void addParamGroupRule(final String groupName, final List<ParameterRule> paramRules, final List<OrCriteriaRule> criteriaRules, final String tagName) {
        final boolean isParamConfig = "param".equals(tagName);
        final Map<String, List<ParameterRule>> paramGroupRuleMap = isParamConfig ? this.paramGroupRuleMap : this.jsonKeyGroupRuleMap;
        if (paramRules != null) {
            if (paramGroupRuleMap.containsKey(groupName)) {
                throw new RuntimeException((isParamConfig ? "Param" : "JsonKey") + "-group rule '" + groupName + "' is already defined");
            }
            paramGroupRuleMap.put(groupName, paramRules);
        }
        if (criteriaRules != null) {
            Map<String, List<OrCriteriaRule>> criteriaRuleMap = null;
            if (isParamConfig) {
                if (this.paramGroupCriteriaRuleMap == null) {
                    this.paramGroupCriteriaRuleMap = new HashMap<String, List<OrCriteriaRule>>();
                }
                criteriaRuleMap = this.paramGroupCriteriaRuleMap;
            }
            else {
                if (this.jsonKeyGroupCriteriaRuleMap == null) {
                    this.jsonKeyGroupCriteriaRuleMap = new HashMap<String, List<OrCriteriaRule>>();
                }
                criteriaRuleMap = this.jsonKeyGroupCriteriaRuleMap;
            }
            criteriaRuleMap.put(groupName, criteriaRules);
        }
    }
    
    public List<ParameterRule> getParamGroupRules(final String paramGroupName) {
        return this.paramGroupRuleMap.get(paramGroupName);
    }
    
    public List<ParameterRule> getJSONKeyGroupRules(final String keyGroupName) {
        return this.jsonKeyGroupRuleMap.get(keyGroupName);
    }
    
    public List<OrCriteriaRule> getParamGroupCriteriaRules(final String paramGroupName) {
        if (this.paramGroupCriteriaRuleMap != null && this.paramGroupCriteriaRuleMap.containsKey(paramGroupName)) {
            return this.paramGroupCriteriaRuleMap.get(paramGroupName);
        }
        return null;
    }
    
    public List<OrCriteriaRule> getJSONKeyGroupCriteriaRules(final String keyGroupName) {
        if (this.jsonKeyGroupCriteriaRuleMap != null && this.jsonKeyGroupCriteriaRuleMap.containsKey(keyGroupName)) {
            return this.jsonKeyGroupCriteriaRuleMap.get(keyGroupName);
        }
        return null;
    }
    
    public void addJSONTemplateRule(final Element element, final JSONTemplateRule parentJSONTemplatesRule) {
        final JSONTemplateRule jsonTemplateRule = new JSONTemplateRule(element);
        jsonTemplateRule.initializeParentVariables(parentJSONTemplatesRule);
        jsonTemplateRule.initializeJSONKeyGroup(this.jsonKeyGroupRuleMap, this.jsonKeyGroupCriteriaRuleMap);
        if (this.jsonTemplateRuleMap.containsKey(jsonTemplateRule.getName())) {
            throw new RuntimeException("Invalid Configuration :: JSON Template rule '" + jsonTemplateRule.getName() + "' is already defined");
        }
        this.jsonTemplateRuleMap.put(jsonTemplateRule.getName(), jsonTemplateRule);
    }
    
    public void addURLValidatorRule(final Element urlValidatorElement, final URLValidatorRule parentUrlValidatorsRule) {
        final URLValidatorRule urlValidatorRule = new URLValidatorRule(urlValidatorElement);
        if (this.urlValidatorRuleMap.containsKey(urlValidatorRule.getName())) {
            throw new RuntimeException("Invalid Configuration :: URL-Validator rule '" + urlValidatorRule.getName() + "' is already defined");
        }
        urlValidatorRule.initializeParentVariables(parentUrlValidatorsRule);
        urlValidatorRule.createZsecURLValidatorInstance();
        this.urlValidatorRuleMap.put(urlValidatorRule.getName(), urlValidatorRule);
    }
    
    public URLValidatorRule getURLValidatorRule(final String name) {
        return this.urlValidatorRuleMap.get(name);
    }
    
    public JSONTemplateRule getJSONTemplateRule(final String name) {
        return this.jsonTemplateRuleMap.get(name);
    }
    
    protected void addTemplateRule(final Element templateElement) {
        final TemplateRule templateObject = new TemplateRule(templateElement);
        if (this.templateRuleMap.containsKey(templateObject.getTemplateName())) {
            throw new RuntimeException("Invalid Configuration :: Template rule '" + templateObject.getTemplateName() + "' is already defined");
        }
        this.templateRuleMap.put(templateObject.getTemplateName(), templateObject);
    }
    
    public Map<String, TemplateRule> getTemplateRuleMap() {
        return this.templateRuleMap;
    }
    
    public TemplateRule getTemplateRule(final String templatename) {
        return this.templateRuleMap.get(templatename);
    }
    
    public boolean isAllowedUserAgent(final String browser, final int version) {
        if (this.allowedUserAgentVersions != null && this.allowedUserAgentVersions.containsKey(browser)) {
            final int configuredVersion = this.allowedUserAgentVersions.get(browser);
            if (configuredVersion > version) {
                return false;
            }
        }
        return true;
    }
    
    public void addContentTypes(final Properties prop) {
        for (final String key : ((Hashtable<Object, V>)prop).keySet()) {
            this.contentTypes.put(key, Pattern.compile(prop.getProperty(key)));
        }
    }
    
    public void addContentTypesXSS(final Properties prop) {
        for (final String key : ((Hashtable<Object, V>)prop).keySet()) {
            this.contentTypesXSS.put(key, prop.getProperty(key));
        }
    }
    
    List<String> getDefaultSecretRequestHeaders() {
        return this.defaultSecretRequestHeaderNames;
    }
    
    Map<String, HeaderRule> getDefaultRequestHeadersRuleWithRegexName() {
        return this.defaultRequestHeadersRuleWithRegexName;
    }
    
    Map<String, HeaderRule> getDefaultRequestHeadersRuleWithStrictName() {
        return this.defaultRequestHeadersRuleWithStrictName;
    }
    
    HeaderRule getDefaultRequestHeaderRuleWithStrictName(final String headerName) {
        return this.defaultRequestHeadersRuleWithStrictName.get(headerName);
    }
    
    HeaderRule getDefaultRequestHeaderRuleWithRegexName(final String headerName) {
        return this.defaultRequestHeadersRuleWithRegexName.get(headerName);
    }
    
    Map<String, HeaderRule> getInternalRequestHeaders() {
        return this.internalRequestHeadersWithStrictName;
    }
    
    List<String> getInternalSecretRequestHeaders() {
        return this.internalSecretRequestHeaderNames;
    }
    
    List<ParameterRule> getPartialMaskingInternalReqHeaderRules() {
        return this.partialMaskingInternalReqHeaderRules;
    }
    
    public Map<String, HeaderRule> getDefaultRequestHeadersMapWithRegexNameForAnalysis() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends HeaderRule>)this.defaultRequestHeadersRuleWithRegexName);
    }
    
    public Map<String, HeaderRule> getDefaultRequestHeadersMapWithStrictNameForAnalysis() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends HeaderRule>)this.defaultRequestHeadersRuleWithStrictName);
    }
    
    public Pattern getContentTypes(final String name) {
        return this.contentTypes.get(name);
    }
    
    public String getContentTypesXSS(final String name) {
        return this.contentTypesXSS.get(name);
    }
    
    public RegexRule getRegexRule(final String regexName) {
        return this.regexRuleMap.get(regexName);
    }
    
    public static RegexRule getCommonRegexRule(final String regexName) {
        return SecurityFilterProperties.commonRegexRuleMap.get(regexName);
    }
    
    public Pattern getRegexPattern(final String regexName) {
        final RegexRule rule = this.regexRuleMap.get(regexName);
        return (rule != null) ? rule.getPattern() : null;
    }
    
    public static Pattern getCommonRegexPattern(final String regexName) {
        final RegexRule rule = SecurityFilterProperties.commonRegexRuleMap.get(regexName);
        return (rule != null) ? rule.getPattern() : null;
    }
    
    public Collection<URLRule> getAllURLRule() {
        if (this.allUrlRules.isEmpty()) {
            this.allUrlRules = new HashMap<String, URLRule>(this.urlRuleMap);
            for (final URLRule rule : this.urlPatternRuleMap.values()) {
                this.allUrlRules.put(rule.getUrl(), rule);
            }
        }
        return this.allUrlRules.values();
    }
    
    public Map<String, URLRule> getURLRuleMap() {
        return this.urlRuleMap;
    }
    
    public Map<Pattern, URLRule> getURLPatternRuleMap() {
        return this.urlPatternRuleMap;
    }
    
    public Map<String, ProxyURL> getProxyURLsMap() {
        return this.proxyURLs;
    }
    
    public Map<Pattern, ProxyURL> getProxyURLPatternsMap() {
        return this.proxyURLPatterns;
    }
    
    public List<ProxyURL> getAllProxyURLs() {
        if (this.allProxyURLRules.isEmpty()) {
            this.allProxyURLRules.addAll(this.proxyURLs.values());
            this.allProxyURLRules.addAll(this.proxyURLPatterns.values());
        }
        return this.allProxyURLRules;
    }
    
    public URLRule getURLRuleForInit(final String url) {
        final URLRule uRule = this.urlRuleMap.get(url);
        if (uRule != null) {
            return uRule;
        }
        for (final Map.Entry<Pattern, URLRule> entrySet : this.urlPatternRuleMap.entrySet()) {
            final Pattern urlPattern = entrySet.getKey();
            if (urlPattern.pattern().equals(url)) {
                return entrySet.getValue();
            }
        }
        return null;
    }
    
    public URLRule getURLRule(final String url) {
        final URLRule uRule = this.urlRuleMap.get(url);
        if (uRule != null) {
            return uRule;
        }
        for (final Map.Entry<Pattern, URLRule> entrySet : this.urlPatternRuleMap.entrySet()) {
            final Pattern urlPattern = entrySet.getKey();
            if (SecurityUtil.matchPattern(url, urlPattern, SecurityFilterProperties.patternMatcherTimeOutInMillis, SecurityFilterProperties.patternMatcherMaxIterationCount)) {
                return entrySet.getValue();
            }
        }
        return null;
    }
    
    ActionRule getActionRuleByQueryString(final HttpServletRequest request, final String queryString) {
        ActionRule actionRule = null;
        final String uri = SecurityUtil.getRequestURIForURLRuleLookup(request, this);
        final URLRule urlRule = this.getURLRule(uri);
        if (urlRule != null) {
            String operationParamValue = "ZSEC_DEFAULT_OPERATION_VALUE";
            if (queryString != null && urlRule.getActionParamName() != null) {
                for (final String param : queryString.split("&")) {
                    final String[] paramNameValuePair = param.split("=", 2);
                    if (urlRule.getActionParamName().equals(paramNameValuePair[0])) {
                        operationParamValue = ((paramNameValuePair.length > 1) ? paramNameValuePair[1] : operationParamValue);
                        break;
                    }
                }
            }
            actionRule = this.getURLActionRule(uri, SecurityUtil.getRequestMethodForActionRuleLookup(request), operationParamValue);
        }
        return actionRule;
    }
    
    ActionRule getActionRule(final SecurityRequestWrapper securedRequest) {
        final Object actionRuleObj = securedRequest.getAttribute("urlrule");
        if (actionRuleObj != null) {
            return (ActionRule)actionRuleObj;
        }
        ActionRule actionRule = null;
        final String uri = SecurityUtil.getRequestURIForURLRuleLookup((HttpServletRequest)securedRequest, this);
        final URLRule urlRule = this.getURLRule(uri);
        if (urlRule != null) {
            String method = SecurityUtil.getRequestMethodForActionRuleLookup((HttpServletRequest)securedRequest);
            if (this.isWebhookURL(urlRule, method, securedRequest)) {
                method = "POST";
            }
            final String[] operationParamValues = securedRequest.getOperationParameterValues(urlRule.getActionParamName());
            if (operationParamValues == null) {
                actionRule = this.getURLActionRule(uri, method, "ZSEC_DEFAULT_OPERATION_VALUE");
            }
            else if (operationParamValues.length == 1) {
                actionRule = this.getURLActionRule(uri, method, operationParamValues[0]);
            }
        }
        return actionRule;
    }
    
    protected ActionRule getURLActionRule(final SecurityRequestWrapper securedRequest, final URLRule urlRule) {
        String method = SecurityUtil.getRequestMethodForActionRuleLookup((HttpServletRequest)securedRequest);
        final String operationParamName = urlRule.getActionParamName();
        String operationParamValue = "ZSEC_DEFAULT_OPERATION_VALUE";
        final Map<String, Map<String, ActionRule>> actionRuleLookupMap = urlRule.getActionRuleLookupMap();
        if (this.isWebhookURL(urlRule, method, securedRequest)) {
            method = "POST";
        }
        if (actionRuleLookupMap.containsKey(method)) {
            final Map<String, ActionRule> actionRuleLookupInnerMap = actionRuleLookupMap.get(method);
            if (SecurityUtil.isValid(operationParamName)) {
                final String[] operationParamValues = securedRequest.getOperationParameterValues(operationParamName);
                if (operationParamValues != null && operationParamValues.length > 1) {
                    SecurityFilterProperties.logger.log(Level.SEVERE, "The parameter \"{0}\" for the URL \"{1}\" is more than the maximum occurances configured in the param rule :\n {2}", new Object[] { operationParamName, securedRequest.getRequestURI(), urlRule.toString() });
                    throw new IAMSecurityException("MORE_THAN_MAX_OCCURANCE", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"), operationParamName, operationParamValues.length);
                }
                securedRequest.addValidatedParameter(operationParamName);
                final String opValue = (operationParamValues != null) ? operationParamValues[0] : null;
                operationParamValue = (SecurityUtil.isValid(opValue) ? opValue : operationParamValue);
            }
            if (actionRuleLookupInnerMap.containsKey(operationParamValue)) {
                return actionRuleLookupInnerMap.get(operationParamValue);
            }
            if (SecurityUtil.isValid(operationParamName)) {
                final String opException = operationParamValue.equals("ZSEC_DEFAULT_OPERATION_VALUE") ? "URL_ACTION_PARAM_MISSING" : "URL_RULE_NOT_CONFIGURED";
                throw new IAMSecurityException(opException, securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"), operationParamName, operationParamValue);
            }
        }
        else if (!this.isRequestOptionsEnabled() || !"OPTIONS".equals(method)) {
            throw new IAMSecurityException("INVALID_METHOD", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"), method);
        }
        return null;
    }
    
    private boolean isWebhookURL(final URLRule urlRule, final String method, final SecurityRequestWrapper securedRequest) {
        return urlRule.isWebhookAccessllowed() && urlRule.getAllowedMethodsInWebhook().contains(method) && this.isAuthenticationProviderConfigured() && this.getAuthenticationProvider().isValidWebhookRequest((HttpServletRequest)securedRequest);
    }
    
    protected ActionRule getURLActionRule(final String uri, final String requestMethod, final String operationValue) {
        final URLRule urlRule = this.getURLRule(uri);
        if (urlRule != null) {
            return urlRule.getActionRule(requestMethod, operationValue);
        }
        return null;
    }
    
    private String substituteRegexesInUrl(String url) {
        int endLoc;
        String regexPatternStr;
        for (int startLoc = 0; (startLoc = url.indexOf("${", startLoc)) != -1; url = url.substring(0, startLoc) + regexPatternStr + url.substring(endLoc + 1), startLoc += regexPatternStr.length()) {
            endLoc = url.substring(startLoc).indexOf(125);
            if (endLoc == -1) {
                throw new RuntimeException("Path regex is not valid");
            }
            endLoc += startLoc;
            final String regex = url.substring(startLoc + 2, endLoc);
            final Pattern regexPattern = this.getRegexPattern(regex);
            if (!SecurityUtil.isValid(regexPattern)) {
                throw new RuntimeException("Path regex is not valid");
            }
            regexPatternStr = regexPattern.pattern();
        }
        url = url.replaceAll("\\\\\\$", "\\$");
        url = url.replaceAll("\\\\\\{", "\\{");
        url = url.replaceAll("\\\\\\}", "\\}");
        return url;
    }
    
    protected void addURLRule(final Element element, final ActionRule urlsActionRule) {
        String url = ActionRule.getURLAttribute(element, "path");
        if (url == null || "".equals(url)) {
            throw new RuntimeException("Attribute PATH not specifed in the URL element : " + element);
        }
        if (!urlsActionRule.parseRequestBody()) {
            SecurityFilterProperties.logger.log(Level.SEVERE, "Attribute \"parse-request-body\" is not allowed at <urls> level, it is only allowed at <url.. > level, URL Path: {0}", url);
            throw new IAMSecurityException("INVALID_CONFIGURATION");
        }
        if (SecurityUtil.isValid(element.getAttribute("prefix"))) {
            SecurityFilterProperties.logger.log(Level.SEVERE, " \"prefix\" attribute is not allowed at <url path=\"{0}\" > , \"prefix\" is only allowed with <urls .. >  ", url);
            throw new IAMSecurityException("INVALID_CONFIGURATION");
        }
        final String urlPrefix = urlsActionRule.getPrefix();
        if (SecurityUtil.isValid(urlPrefix)) {
            url = urlPrefix + url;
        }
        url = this.substituteRegexesInUrl(url);
        final String method = SecurityUtil.getValidValue(ActionRule.getURLAttribute(element, "method"), "GET").toUpperCase();
        final String operationParamName = SecurityUtil.getValidValue(ActionRule.getURLAttribute(element, "operation-param"), null);
        final String operationParamValue = SecurityUtil.getValidValue(ActionRule.getURLAttribute(element, "operation-value"), "ZSEC_DEFAULT_OPERATION_VALUE");
        URLRule urlRule = this.getURLRuleForInit(url);
        if (urlRule == null) {
            boolean inRegex = "true".equals(ActionRule.getURLAttribute(element, "path-regex"));
            if (!inRegex) {
                final String urlType = ActionRule.getURLAttribute(element, "url-type");
                inRegex = ("dynamic".equals(urlType) || "multiple".equals(urlType) || url.indexOf("*") > -1 || url.indexOf("[") > -1 || url.indexOf("(") > -1);
            }
            urlRule = new URLRule(url, operationParamName, inRegex, ActionRule.getURLAttribute(element, "redirect"));
            this.addURLRule(url, urlRule);
        }
        else if (this.isURLActionRuleExists(url, method, operationParamValue)) {
            if (urlRule.getActionParamName() == null) {
                throw new RuntimeException("Invalid security configuration - URL conflicts :: URL path '" + url + "' already exists");
            }
            throw new RuntimeException("Invalid security configuration - URL conflicts :: operation-value '" + operationParamValue + "' already exists for the url " + url);
        }
        else if (SecurityUtil.isValid(operationParamName)) {
            if (!SecurityUtil.isValid(urlRule.getActionParamName())) {
                urlRule.setActionParamName(operationParamName);
            }
            else if (!operationParamName.equals(urlRule.getActionParamName())) {
                throw new RuntimeException("Invalid security configuration : Multiple Action param name not allowed for the URL  " + url + "  Existing operation-param : " + urlRule.getActionParamName() + " , New operation-param : " + operationParamName);
            }
        }
        final String[] split;
        final String[] opParamValues = split = operationParamValue.split(",");
        for (String opParamValue : split) {
            opParamValue = opParamValue.trim();
            for (String c_method : method.split(",")) {
                c_method = c_method.trim();
                if (!SecurityFilterProperties.requestMethod.contains(c_method)) {
                    throw new RuntimeException("Invalid method Configured for the url = " + url + " and method name is = " + c_method);
                }
                element.setAttribute("method", c_method);
                element.setAttribute("operation-param", operationParamName);
                element.setAttribute("operation-value", opParamValue);
                final ActionRule actionRule = this.getSecurityProvider().getActionRule(this, element);
                actionRule.addChildActionRule(element);
                actionRule.initializeParentVariables(urlsActionRule);
                if (!this.dbCacheForServiceScopeThrottles && actionRule.dbCacheForServiceScopeThrottles) {
                    this.dbCacheForServiceScopeThrottles = true;
                }
                actionRule.initializeParamGroups(urlsActionRule);
                if (actionRule.isWebhookAccessAllowed()) {
                    boolean isInvalidConfig = false;
                    if (!"POST".equalsIgnoreCase(c_method)) {
                        throw new RuntimeException("Invalid method Configured for the url = " + url + " and method name is = " + c_method);
                    }
                    if (!actionRule.isAuthenticationRequired()) {
                        isInvalidConfig = true;
                    }
                    else if (!"CREATE".equalsIgnoreCase(actionRule.getOperationType())) {
                        isInvalidConfig = true;
                    }
                    else if (!SecurityUtil.isValidList(actionRule.getOAuthScopeList()) && !SecurityUtil.isValidList(actionRule.getOrgOAuthScopeList())) {
                        isInvalidConfig = true;
                    }
                    if (isInvalidConfig) {
                        throw new RuntimeException("Invalid Configuration for the url = " + url);
                    }
                    urlRule.setAccessMethodForWebhook(actionRule.isWebhookAccessAllowed(), actionRule.getAccessMethodsForWebhook());
                }
                urlRule.addActionRule(c_method, opParamValue, actionRule);
            }
        }
    }
    
    private boolean isURLActionRuleExists(final String url, final String method, final String operationParamValue) {
        final URLRule urlRule = this.getURLRuleForInit(url);
        if (urlRule == null) {
            return false;
        }
        ActionRule actionRule = null;
        for (final String opParamValue : operationParamValue.split(",")) {
            for (final String c_method : method.split(",")) {
                actionRule = urlRule.getActionRule(c_method, opParamValue.trim());
                if (actionRule != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static SecurityFilterProperties getInstance(final HttpServletRequest request) {
        final String contextPath = SecurityUtil.getContextPath(request);
        if (SecurityFilterProperties.FILTER_INSTANCES.containsKey(contextPath)) {
            return SecurityFilterProperties.FILTER_INSTANCES.get(contextPath);
        }
        return SecurityFilterProperties.FILTER_INSTANCES.get(contextPath + File.separator);
    }
    
    public static SecurityFilterProperties getInstance(String webContext) {
        if (SecurityFilterProperties.FILTER_INSTANCES.containsKey(webContext)) {
            return SecurityFilterProperties.FILTER_INSTANCES.get(webContext);
        }
        if (SecurityFilterProperties.FILTER_INSTANCES.containsKey(webContext + File.separator)) {
            return SecurityFilterProperties.FILTER_INSTANCES.get(webContext + File.separator);
        }
        webContext = File.separator + webContext;
        for (final Map.Entry<String, SecurityFilterProperties> next : SecurityFilterProperties.FILTER_INSTANCES.entrySet()) {
            final String contextPath = next.getKey();
            if (contextPath.endsWith(webContext) || contextPath.endsWith(webContext + File.separator)) {
                return next.getValue();
            }
        }
        return null;
    }
    
    public String getHttpIPBlackListDNS() {
        return this.httpIPBlackListDNS;
    }
    
    public String getHttpIPWhiteListDNS() {
        return this.httpIPWhiteListDNS;
    }
    
    public void setCSRFCookie(final boolean set) {
        this.setCSRFCookie = set;
    }
    
    public boolean isSetCSRFCookie() {
        return this.setCSRFCookie;
    }
    
    public boolean isDisableParameterValidationForTestingOutputEncoding() {
        return this.disableParamInputValidationForTestingOutputEncoding;
    }
    
    public Pattern getOETestExcludedRegexPattern() {
        return this.getRegexPattern("disable.param.input.validation.for.testing.output.encoding.exclude.regexname");
    }
    
    public Pattern getOETestExcludedParams() {
        return this.getRegexPattern("disable.param.input.validation.for.testing.output.encoding.exclude.params");
    }
    
    public boolean isTrustedScriptEnabled() {
        return this.isTrustedScriptEnabled;
    }
    
    public int getUploadFileRuleReadTimeOut() {
        return this.uploadFileRuleReadTimeOut;
    }
    
    public int getUploadFileRuleConnectionTimeOut() {
        return this.uploadFileRuleConnectionTimeOut;
    }
    
    public static boolean containsFilterInstance(final String contextPath) {
        return SecurityFilterProperties.FILTER_INSTANCES.containsKey(contextPath);
    }
    
    public static void addFilterInstance(final String contextPath, final SecurityFilterProperties sfp) {
        SecurityFilterProperties.FILTER_INSTANCES.put(contextPath, sfp);
    }
    
    public static int getProxyConnectionTimeOut() {
        return SecurityFilterProperties.proxyConnectTimeOut;
    }
    
    public static int getProxyReadTimeOut() {
        return SecurityFilterProperties.proxyReadTimeOut;
    }
    
    public boolean isReadOnlyMode() {
        final String appReadOnlyMode = System.getProperty("app.readonly.mode");
        if (appReadOnlyMode != null) {
            return "true".equalsIgnoreCase(appReadOnlyMode);
        }
        return this.enableReadOnlyMode;
    }
    
    public boolean isSendErrorEnabled() {
        return this.enableResponseSendError;
    }
    
    public long getLastUpdatedTime() {
        return this.lastUpdatedTime;
    }
    
    static PrivateKey getPrivateKey() {
        return SecurityFilterProperties.iscPrivateKey;
    }
    
    static PrivateKey getInterDCPrivateKey() {
        return SecurityFilterProperties.interDCPrivateKey;
    }
    
    long getISCSignatureExpiryTime() {
        return this.iscSignatureExpiryTime;
    }
    
    boolean isAppFirewallEnabled() {
        return this.isAppFirewallEnabled;
    }
    
    boolean isFileBasedLoadingEnabled() {
        return this.isAppFirewallFileBasedLoadingEnabled;
    }
    
    public static String getServiceName() {
        if (SecurityFilterProperties.useIAM) {
            if (SecurityFilterProperties.serviceName == null) {
                for (final String context : SecurityFilterProperties.FILTER_INSTANCES.keySet()) {
                    if (SecurityFilterProperties.FILTER_INSTANCES.get(context).getSecurityProperties().containsKey("service.name")) {
                        SecurityFilterProperties.serviceName = SecurityFilterProperties.FILTER_INSTANCES.get(context).getSecurityProperties().getProperty("service.name");
                        break;
                    }
                }
            }
            return SecurityFilterProperties.serviceName;
        }
        return null;
    }
    
    public boolean isAuthCSRFDisabled() {
        return this.disableAuthCSRF;
    }
    
    public boolean isRequestOptionsEnabled() {
        return this.enableRequestOptions;
    }
    
    public boolean isCSRFMigrationEnabled() {
        return this.enableCSRFMigration;
    }
    
    public boolean isAuthenticationProviderConfigured() {
        return this.authProvider != null;
    }
    
    public Authenticator getAuthenticationProvider() {
        return this.authProvider;
    }
    
    public Object getSASConfigProvider() {
        return this.sasConfigProviderClass;
    }
    
    public String getRACMode() {
        return this.racMode;
    }
    
    public boolean isRACEnabled() {
        return !"none".equals(this.racMode);
    }
    
    public void enableRACLearnMode() {
        this.racMode = "learn";
    }
    
    public void disableRACLearnMode() {
        this.racMode = "none";
    }
    
    public RACProvider getRACProvider() {
        return this.racProvider;
    }
    
    public List<File> getSecurityConfigFiles() {
        return this.securityFiles;
    }
    
    public static Set<String> getServiceWebContexts() {
        return SecurityFilterProperties.FILTER_INSTANCES.keySet();
    }
    
    public boolean isSecretParamLoggingMasked() {
        return this.maskSecretParamLogging;
    }
    
    public boolean isCSRFCheckDisabledForGetApi() {
        return this.disableGetApiCSRFCheck;
    }
    
    public List<ResponseHeaderRule> getSafeResponseHeaderRules(final String headerGroupname) {
        return this.safeResponseHeadersMap.get(headerGroupname);
    }
    
    void addSafeResponseHeaderRule(final String key, final List<ResponseHeaderRule> responseHeaderRules) {
        if (!this.safeResponseHeadersMap.containsKey(key)) {
            this.safeResponseHeadersMap.put(key, responseHeaderRules);
        }
        else {
            SecurityFilterProperties.logger.log(Level.WARNING, "Safe Response Headers - \"{0}\" is already defined ", new Object[] { key });
        }
    }
    
    public void addDefaultResponseHeaderRule(final ResponseHeaderRule responseHeaderRule) {
        this.defaultResponseHeadersList.add(responseHeaderRule);
    }
    
    public List<ResponseHeaderRule> getDefaultResponseHeaderRules() {
        return this.defaultResponseHeadersList;
    }
    
    public List<String> getDefaultDisableSafeHeaders() {
        return this.defaultDisableSafeHeaders;
    }
    
    public String getContentTypeDetectOption() {
        return this.contentTypeDetection;
    }
    
    public boolean isLogResponseEnabled() {
        return this.logResponse;
    }
    
    public Pattern getExcludeURLsInLogResponse() {
        return this.excludedURLsInLogResponse;
    }
    
    public Schema getXMLSchema(final String root) {
        return this.xmlSchemaMap.get(root);
    }
    
    public Map<String, Schema> getXMLSchemas() {
        return this.xmlSchemaMap;
    }
    
    void addXMLSchema(final String root, final Schema schema) {
        if (!this.xmlSchemaMap.containsKey(root)) {
            this.xmlSchemaMap.put(root, schema);
            return;
        }
        throw new RuntimeException("XML Root element name '" + root + "' is already defined in Schema");
    }
    
    void addXMLSchemaFilterElements(final String root, final Collection<XSDElementRule> elementsList) {
        if (elementsList.size() > 0 && !this.xmlSchemaFilterElementsMap.containsKey(root)) {
            this.xmlSchemaFilterElementsMap.put(root, elementsList);
        }
    }
    
    public Collection<XSDElementRule> getXMLSchemaFilterElements(final String root) {
        return this.xmlSchemaFilterElementsMap.get(root);
    }
    
    public boolean isEnableXFrameOptions() {
        return this.enableXFrameOptions;
    }
    
    public String getDefaultXFrameOption() {
        return this.defaultXFrameOption;
    }
    
    void disableDefaultSafeHeaders(final String disableSafeheaders) {
        if (SecurityUtil.isValid(disableSafeheaders)) {
            final String[] excludeHeaders = disableSafeheaders.split(",");
            if (disableSafeheaders.contains("all") && excludeHeaders.length > 1) {
                throw new RuntimeException("disable-safe-headers=\"all\"  with comma separated values not allowed  \n");
            }
            for (final String excludeHeader : excludeHeaders) {
                this.defaultDisableSafeHeaders.add(excludeHeader.trim());
            }
        }
    }
    
    private void addAuthCommonConfigurationFiles() throws Exception {
        final String confDirPath = SecurityUtil.getSecurityConfigurationDir() + File.separator;
        for (final String fileName : this.authProvider.getAuthCommonConfigurationFiles()) {
            if ("security-development.xml".equals(fileName)) {
                throw new RuntimeException("The authentication configuration file with the name 'security-development.xml' is not allowed for security reason. Please change the file name.");
            }
            if (!fileName.startsWith("security")) {
                SecurityFilterProperties.logger.log(Level.SEVERE, "The name of the Security file should only start with \"security\". \"{0}\" is not allowed", fileName);
                throw new IAMSecurityException("The name of the Security file should only start with \"security\"");
            }
            final File file = new File(confDirPath + fileName);
            RuleSetParser.initSecurityRules(this, file);
            this.securityFiles.add(file);
        }
    }
    
    public List<String> getURIPrefixToRemove() {
        return this.ignoreURIPrefixList;
    }
    
    public CookieRequestHeaderRule getDefaultCookieRule() {
        return this.defaultCookieRule;
    }
    
    void setDefaultCookieRule(final CookieRequestHeaderRule defaultCookieRule) {
        if (this.defaultCookieRule != null) {
            this.defaultCookieRule.getCookieMapWithStrictName().putAll(defaultCookieRule.getCookieMapWithStrictName());
            this.defaultCookieRule.getCookieMapWithRegexName().putAll(defaultCookieRule.getCookieMapWithRegexName());
        }
        else {
            this.defaultCookieRule = defaultCookieRule;
        }
    }
    
    public void setInternalCookieRule(final CookieRequestHeaderRule cookieRule) {
        if (this.internalCookieRule != null) {
            this.internalCookieRule.getCookieMapWithStrictName().putAll(cookieRule.getCookieMapWithStrictName());
            this.internalCookieRule.getCookieMapWithRegexName().putAll(cookieRule.getCookieMapWithRegexName());
        }
        else {
            this.internalCookieRule = cookieRule;
        }
    }
    
    public UserAgentRequestHeaderRule getDefaultUserAgentRule() {
        return this.defaultUserAgentRule;
    }
    
    void setDefaultUserAgentRule(final UserAgentRequestHeaderRule defaultUserAgentRule) {
        this.defaultUserAgentRule = defaultUserAgentRule;
    }
    
    public List<String> getBlockedMethods() {
        return this.blockedMethods;
    }
    
    boolean disableISCSignature() {
        final String disableISC = System.getProperty("disable.isc.signature");
        return Boolean.valueOf(disableISC);
    }
    
    void addProxyPolicy(final String allowedServices) {
        if (SecurityUtil.isValid(allowedServices)) {
            this.allowedServicesViaProxy = SecurityUtil.getStringAsList(allowedServices, ",");
        }
    }
    
    public String getLoginPage() {
        return this.loginPage;
    }
    
    @Deprecated
    public Parser getUserAgentParser() {
        return SecurityUtil.getUserAgentParser();
    }
    
    public boolean isAppSenseEnabled() {
        return this.enableAppSense && !this.developmentMode;
    }
    
    void addXMLSchemaRule(final String name, final XMLSchemaRule schemaRule) {
        if (this.xmlSchemaRuleMap == null) {
            this.xmlSchemaRuleMap = new HashMap<String, XMLSchemaRule>();
        }
        if (this.xmlSchemaRuleMap.containsKey(name)) {
            throw new RuntimeException("XML Schema rule already defined for : '" + name + "' and SchemaRule : " + schemaRule.toString());
        }
        this.xmlSchemaRuleMap.put(name, schemaRule);
    }
    
    public XMLSchemaRule getXMLSchemaRule(final String name) {
        return (this.xmlSchemaRuleMap != null) ? this.xmlSchemaRuleMap.get(name) : null;
    }
    
    public void addPlugin(final SFCorePlugin plugin) {
        if (this.plugins == null) {
            this.plugins = new ArrayList<SFCorePlugin>();
        }
        this.plugins.add(plugin);
    }
    
    public List<SFCorePlugin> getPlugins() {
        return this.plugins;
    }
    
    public boolean pushSecurityException() {
        return this.pushError;
    }
    
    public static String getContextName() {
        return SecurityFilterProperties.contextName;
    }
    
    public boolean isEnabledSecretParamLoggingMask() {
        return this.enableSecretParamLoggingMask;
    }
    
    public boolean isEnabledPiiDetector() {
        return this.enablePiiDetector;
    }
    
    public VendorAVProvider getVendorAVProvier() {
        return this.vendorAvProvider;
    }
    
    public CLAMAVConfiguration getClamAvConfig() {
        return (this.avConfig == null) ? null : this.avConfig.getClamAvConfig();
    }
    
    public AntivirusConfiguration getAvConfig() {
        return this.avConfig;
    }
    
    public long getAccessInfoExpiryScheduleInterval() {
        return this.accessInfoExpiryScheduleInterval;
    }
    
    public void addZipSanitizerRule(final ZipSanitizerRule zipRule) {
        if (!this.zipSanitizerRuleMap.containsKey(zipRule.getName())) {
            this.zipSanitizerRuleMap.put(zipRule.getName(), zipRule);
            return;
        }
        throw new RuntimeException(" <zip-extractor>  rule name=\"" + zipRule.getName() + "\" already defined");
    }
    
    public ZipSanitizerRule getZipSanitizerRule(final String zipSanitizerName) {
        if (this.zipSanitizerRuleMap.containsKey(zipSanitizerName)) {
            return this.zipSanitizerRuleMap.get(zipSanitizerName);
        }
        SecurityFilterProperties.logger.log(Level.SEVERE, "zip-sanitizer rule for  name \"{0}\" is not configured ", zipSanitizerName);
        throw new IAMSecurityException("ZIPSANITIZER_RULE_NOT_CONFIGURED");
    }
    
    protected void setPIIDetectorRule(final Element classifier) {
        this.piiDetectorRule = new PIIDetectorRule(classifier);
    }
    
    public void initPiiDetector() {
        try {
            if (this.isEnabledSecretParamLoggingMask() && this.isEnabledPiiDetector()) {
                final PIIDetectorFactory detectorFactory = PIIDetectorFactory.createFactoryInstance();
                SecurityFilterProperties.piiDetector = detectorFactory.getDetector(PIIDetectorFactory.Detector.REGEX_DETECTOR, this.piiDetectorRule.getPiiClassifier(), this.piiDetectorRule.getPiiHandler());
                SecurityFilterProperties.logger.log(Level.INFO, "PII Detector initialized");
            }
        }
        catch (final Exception ex) {
            SecurityFilterProperties.logger.log(Level.SEVERE, "PII Detetor initialization failed");
        }
    }
    
    public static PIIDetector getPiiDetector() {
        return SecurityFilterProperties.piiDetector;
    }
    
    public static void resetPiiDetector() {
        SecurityFilterProperties.piiDetector = null;
    }
    
    public CookieRequestHeaderRule getInternalCookieRule() {
        return this.internalCookieRule;
    }
    
    public boolean isEnforcementMode() {
        return this.isEnforcementMode;
    }
    
    public boolean isLearningMode() {
        return this.isLearningMode;
    }
    
    protected void addCommonThrottlesRule(final ThrottlesRule throttlesRule) {
        if (this.commonThrottlesRuleMap == null) {
            this.commonThrottlesRuleMap = new HashMap<ThrottlesRule.Windows, List<ThrottlesRule>>();
        }
        List<ThrottlesRule> throttlesList = this.commonThrottlesRuleMap.get(throttlesRule.getWindow());
        if (throttlesList == null) {
            this.commonThrottlesRuleMap.put(throttlesRule.getWindow(), throttlesList = new ArrayList<ThrottlesRule>());
        }
        this.addRequiredCachePoolNames(throttlesRule);
        throttlesList.add(throttlesRule);
    }
    
    public List<ThrottlesRule> getCommonThrottlesRuleList(final ThrottlesRule.Windows throttlesWindow) {
        return (this.commonThrottlesRuleMap != null) ? this.commonThrottlesRuleMap.get(throttlesWindow) : null;
    }
    
    public Map<ThrottlesRule.Windows, List<ThrottlesRule>> getCommonThrottlesRuleMap() {
        return this.commonThrottlesRuleMap;
    }
    
    public static PatternMatcherWrapper.PatternMatcherMode getPatternMatcherMode() {
        return SecurityFilterProperties.patternMatcherMode;
    }
    
    public String getCaptchaUrl() {
        return this.showCaptchaURL;
    }
    
    public List<JSON_INVALID_VALUE_TYPE> getJsonInvalidValuesList() {
        return this.json_Invalid_Values_List;
    }
    
    public int getXSSFilterMaxLogSize() {
        return this.maxLogSize;
    }
    
    public DispatcherValidationMode getReqDispValidationMode() {
        return this.reqDispValidationMode;
    }
    
    public TempFileUploadDirMonitoring getTempFileUploadDirMonitoring() {
        return this.tempFileUploadDirMonitoring;
    }
    
    public boolean followServletStdForUrlPath() {
        return this.followServletStdForUrlPath;
    }
    
    public static boolean isROOTContext() {
        return "ROOT".equalsIgnoreCase(getContextName());
    }
    
    public void addExceptionRule(final ExceptionRule rule) {
        this.exceptionRuleMap.put(rule.getExceptionName(), rule);
    }
    
    public ExceptionRule getExceptionRule(final String exceptionName) {
        return this.exceptionRuleMap.get(exceptionName);
    }
    
    public static void setProperty(final Components.COMPONENT_NAME component_name, final String value) {
        final HttpServletRequest request = SecurityUtil.getCurrentRequest();
        final SecurityFilterProperties sfp = getInstance(request);
        if (sfp != null) {
            String val = null;
            switch (component_name) {
                case SECRET_REQ_PARAM_NAMES: {
                    if (sfp.secretRequestParamNames == null) {
                        sfp.secretRequestParamNames = new CopyOnWriteArrayList<String>();
                    }
                    else {
                        sfp.secretRequestParamNames.clear();
                    }
                    val = loadPropertyValue(component_name, value, sfp);
                    SecurityUtil.addValueToList(val, sfp.secretRequestParamNames);
                    break;
                }
                case SECRET_REQ_HEADER_NAMES: {
                    if (sfp.secretRequestHeaderNames == null) {
                        sfp.secretRequestHeaderNames = new CopyOnWriteArrayList<String>();
                    }
                    else {
                        sfp.secretRequestHeaderNames.clear();
                        sfp.secretRequestHeaderNames.addAll(SecurityFilterProperties.defaultsecretRequestHeaderNamesList);
                    }
                    val = loadPropertyValue(component_name, value, sfp);
                    SecurityUtil.addValueToList(val, sfp.secretRequestHeaderNames);
                    break;
                }
                case SECRET_RES_HEADER_NAMES: {
                    if (sfp.secretResponseHeaderNames == null) {
                        sfp.secretResponseHeaderNames = new CopyOnWriteArrayList<String>();
                    }
                    else {
                        sfp.secretResponseHeaderNames.clear();
                    }
                    val = loadPropertyValue(component_name, value, sfp);
                    SecurityUtil.addValueToList(val, sfp.secretResponseHeaderNames);
                    break;
                }
                case ENABLE_APP_FIREWALL: {
                    val = loadPropertyValue(component_name, value, sfp);
                    if (val != null) {
                        AppFirewallPolicyLoader.isReqFirewallEnabled = Boolean.parseBoolean(val);
                        break;
                    }
                    AppFirewallPolicyLoader.isReqFirewallEnabled = false;
                    break;
                }
            }
        }
        else {
            SecurityFilterProperties.logger.log(Level.WARNING, " SecurityFilterProperties instance is NULL ");
        }
    }
    
    private static String loadPropertyValue(final Components.COMPONENT_NAME propertyName, final String value, final SecurityFilterProperties sfp) {
        if ("NULL".equalsIgnoreCase(value)) {
            AppSenseAgent.clearPropertyFromWAFProperties(propertyName.getValue());
            final String val = getPropertiesFromSecurityConfig(propertyName.getValue(), sfp);
            SecurityFilterProperties.logger.log(Level.WARNING, "Removing \"{0}\" from waf configuration and loading from security configuration - value : {1} ", new Object[] { propertyName.getValue(), val });
            return val;
        }
        SecurityFilterProperties.logger.log(Level.WARNING, " \"{0}\" = {1} loaded from waf configuration  ", new Object[] { propertyName.getValue(), value });
        ((Hashtable<String, String>)AppSenseAgent.wafProperties).put(propertyName.getValue(), value);
        return value;
    }
    
    private static String getPropertiesFromSecurityConfig(final String propertyName, final SecurityFilterProperties sfp) {
        if (sfp.getProperties().containsKey(propertyName)) {
            return sfp.getProperties().getProperty(propertyName);
        }
        return null;
    }
    
    private Properties getProperties() {
        return this.properties;
    }
    
    static {
        FILTER_INSTANCES = new HashMap<String, SecurityFilterProperties>();
        MAIN_ZOHO_DOMAINS = Pattern.compile(".*\\.zoho\\.com");
        SecurityFilterProperties.commonRegexRuleMap = new HashMap<String, RegexRule>();
        SecurityFilterProperties.contextName = null;
        SecurityFilterProperties.useIAM = true;
        SecurityFilterProperties.trustedIPPattern = null;
        logger = Logger.getLogger(SecurityFilterProperties.class.getName());
        SecurityFilterProperties.proxyHost = null;
        SecurityFilterProperties.proxyPort = null;
        SecurityFilterProperties.proxyUserName = null;
        SecurityFilterProperties.proxyPassword = null;
        SecurityFilterProperties.iscPrivateKey = null;
        SecurityFilterProperties.requestMethod = new ArrayList<String>(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        SecurityFilterProperties.serviceName = null;
        SecurityFilterProperties.proxyConnectTimeOut = 5000;
        SecurityFilterProperties.proxyReadTimeOut = 60000;
        SecurityFilterProperties.patternMatcherTimeOutInMillis = 5000;
        SecurityFilterProperties.patternMatcherMaxIterationCount = 1000000;
        SecurityFilterProperties.patternMatcherMode = PatternMatcherWrapper.PatternMatcherMode.ERROR;
        SecurityFilterProperties.proxy = null;
        SecurityFilterProperties.defaultConfigFiles = Arrays.asList("security-appfirewallproperties.xml", "security-safeheaders.xml", "security-privatekey.xml", "security-antivirus.xml", "security-development.xml", "security-content-types.xml", "security-default-rules.xml");
        SecurityFilterProperties.permanentConfigFiles = Arrays.asList(AppSenseConstants.PROPERTY_FILE);
        SecurityFilterProperties.defaultsecretRequestHeaderNamesList = new ArrayList<String>(Arrays.asList("cookie", "authorization", "z-authorization", "proxy-authorization"));
        SecurityFilterProperties.piiDetector = null;
        SecurityFilterProperties.interDCPrivateKey = null;
        SecurityFilterProperties.currentDCLocation = null;
    }
    
    public enum RequestHeaderValidationMode
    {
        DISABLE("disable"), 
        LOGGING("logging"), 
        LEARNING("learning"), 
        ENFORCEMENT("enforcement");
        
        private String mode;
        
        private RequestHeaderValidationMode(final String mode) {
            this.mode = null;
            this.mode = mode;
        }
        
        public String getMode() {
            return this.mode;
        }
    }
    
    public enum CORS_REQUEST_TYPE
    {
        PREFLIGHT, 
        APPLICATION;
    }
    
    public enum DispatcherValidationMode
    {
        LEARNING, 
        ENFORCEMENT;
        
        public static DispatcherValidationMode propertyToEnum(final String mode) {
            final String lowerCase = mode.toLowerCase();
            switch (lowerCase) {
                case "learning": {
                    return DispatcherValidationMode.LEARNING;
                }
                case "enforcement": {
                    return DispatcherValidationMode.ENFORCEMENT;
                }
                default: {
                    throw new RuntimeException("Invalid request dispatcher validation mode \"" + mode + "\"");
                }
            }
        }
    }
    
    public enum JSON_INVALID_VALUE_TYPE
    {
        JSONNULL("jsonnull (null) "), 
        EMPTYSTRING("emptystring (\" \") "), 
        EMPTYOBJECT("emptyobject { } "), 
        EMPTYARRAY("emptyarray [ ] ");
        
        private String value;
        
        private JSON_INVALID_VALUE_TYPE(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    public enum ErrorPageValidationMode
    {
        LOGGING("logging"), 
        ENFORCEMENT("enforcement");
        
        private String mode;
        
        private ErrorPageValidationMode(final String mode) {
            this.mode = null;
            this.mode = mode;
        }
        
        public String getMode() {
            return this.mode;
        }
    }
    
    public enum InputStreamValidationMode
    {
        LOG("log"), 
        ERROR("error");
        
        private String validationMode;
        
        private InputStreamValidationMode(final String mode) {
            this.validationMode = null;
            this.validationMode = mode;
        }
        
        public String getMode() {
            return this.validationMode;
        }
    }
}
