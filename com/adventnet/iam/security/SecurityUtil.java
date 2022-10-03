package com.adventnet.iam.security;

import java.util.Hashtable;
import java.net.PasswordAuthentication;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.net.MalformedURLException;
import com.zoho.security.validator.url.URLValidatorRule;
import com.zoho.security.validator.url.ZSecURL;
import com.zoho.logs.logclient.LogClientThreadLocal;
import java.io.ByteArrayOutputStream;
import com.zoho.security.eventfw.pojos.log.ZSEC_EXCEPTION_IN_ENABLING_SAMESITE_FOR_CSRF_COOKIE;
import com.zoho.security.util.CommonUtil;
import com.zoho.security.eventfw.pojos.log.ZSEC_GETTING_URLENCODED_PARAMS_AS_INPUTSTREAM;
import com.zoho.lpzc.antispam.url.result.UrlValidationResult;
import com.zoho.lpzc.antispam.url.validator.SafeUrlValidator;
import java.util.regex.Matcher;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.util.Date;
import ua_parser.Parser;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import com.zoho.security.wafad.WAFAttackDiscoveryUtil;
import com.zoho.security.eventfw.EventDataProcessor;
import com.adventnet.iam.security.antivirus.AVScanResult;
import com.zoho.security.util.TikaUtil;
import java.io.BufferedInputStream;
import eu.medsea.mimeutil.MimeUtil;
import com.zoho.security.eventfwimpl.ZSecSinglePointLoggerImplProvider;
import com.zoho.security.threadlocal.ZSecThreadLocalRegistry;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.apache.xerces.impl.Constants;
import org.w3c.dom.ls.LSResourceResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.Transformer;
import org.xml.sax.InputSource;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.EntityResolver;
import org.apache.xerces.util.SecurityManager;
import javax.xml.parsers.DocumentBuilderFactory;
import com.zoho.security.api.wrapper.PatternMatcherWrapper;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.Arrays;
import java.security.PrivateKey;
import java.security.Signature;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.Properties;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.disk.DiskFileItem;
import com.zoho.security.eventfw.pojos.log.ZSEC_PERFORMANCE_ANOMALY;
import com.zoho.security.eventfw.ExecutionTimer;
import java.security.MessageDigest;
import com.zoho.security.agent.AppSenseAgent;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import java.net.HttpURLConnection;
import com.zoho.security.api.wrapper.URLWrapper;
import java.net.URL;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.File;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONTokener;
import com.adventnet.iam.xss.IAMJSONTokener;
import com.adventnet.iam.xss.XSSUtil;
import java.util.logging.Level;
import java.security.KeyFactory;
import java.util.BitSet;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import java.util.List;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.XMLReader;
import javax.xml.transform.TransformerFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.servlet.http.HttpServletRequest;
import com.zoho.security.threadlocal.ZSecThreadLocal;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class SecurityUtil
{
    public static final String ZSEC_PROXY_SERVER_NAME = "ZSEC_PROXY_SERVER_NAME";
    public static final String ZSEC_PROXY_SERVER_SIGNATURE = "ZSEC_PROXY_SERVER_SIGNATURE";
    public static final String ZSEC_PROXY_REQUEST = "ZSEC_PROXY_REQUEST";
    public static final String ZSEC_XFRAME_TRUSTED_SERVICE = "ZSEC_XFRAME_TRUSTED_SERVICE";
    public static final String IMPORTED_DATA_AS_FILE = "IMPORTED_DATA_AS_FILE";
    public static final String MULTIPART_FORM_REQUEST = "MULTIPART_FORM_REQUEST";
    public static final String MULTIPART_FORM_REQUEST_FILE_NAME = "MULTIPART_FORM_REQUEST_FILE_NAME";
    public static final String MULTIPART_FORM_REQUEST_FILE_SIZE = "MULTIPART_FORM_REQUEST_FILE_SIZE";
    public static final String MULTIPART_FORM_REQUEST_FILE_PATH = "MULTIPART_FORM_REQUEST_FILE_PATH";
    public static final String MULTIPART_FORM_REQUEST_FILE_CONTENT = "MULTIPART_FORM_REQUEST_FILE_CONTENT";
    public static final String ZSEC_LIVE_WINDOW_REQ_THROTTLES_KEY = "ZSEC_LIVE_WINDOW_REQ_THROTTLES_KEY";
    private static final Logger LOGGER;
    public static final int BUFFER_SIZE = 1024;
    public static final String CHARSET = "UTF-8";
    public static final char[] HEX;
    private static Pattern inverseClearTextPattern;
    private static MatcherUtil matcherUtil;
    public static final ZSecThreadLocal<HttpServletRequest> CURRENT_REQUEST;
    public static final String ZOHO_INPUTSTREAM = "zoho-inputstream";
    public static final String ZOHO_PARAMORSTREAM = "zoho-paramorstream";
    private static final ZSecThreadLocal<DocumentBuilder> DOCUMENT_BUILDER;
    private static final ZSecThreadLocal<TransformerFactory> TRANSFORMER_FACTORY;
    private static final ZSecThreadLocal<XMLReader> XML_READER;
    private static final ZSecThreadLocal<SchemaFactory> SCHEMAFACTORY;
    private static final ZSecThreadLocal<List<JSONObject>> JSONEXCEPTIONTRACELIST;
    private static final ZSecThreadLocal<List<Object>> ZSECEVENTS;
    private static boolean isMimeDetectorRegistered;
    protected static final String TIKA = "tika";
    private static final String MIMEUTILWITHTIKA = "mimeutilwithtika";
    private static final String TEMPDIR;
    private static final String TEMP_FILE_UPLOAD_DIR = "waf_fileupload";
    public static final String ZSEC_USER_IMPORT_URL = "ZSEC_USER_IMPORT_URL";
    public static final String API_CALL = "API_CALL";
    private static Hex hexCodec;
    public static final String ZSEC_SERVICE_NAME_URL = "/getzsecservicename";
    static final String STATELESS_AUTH_CSRF_COOKIE_NAME_SUFFIX = "_slauth";
    public static final String STREAM_CONTENT_AS_FILE = "STREAM_CONTENT_AS_FILE";
    private static boolean isLogClientThreadLocalClassLoaded;
    private static final char WINDOWS_SEPARATOR = '\\';
    private static final String WAF_SCHEDULER_THREAD_NAME = "waf_scheduler";
    private static final ScheduledExecutorService WAF_SCHEDULER;
    public static final Pattern SINGLE_TIME_PATTERN;
    static final String APPLICATION_URLENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static Map<String, String> xframeTrustedDomainServiceMapping;
    private static final ThreadLocal<HttpServletRequest> CURRENT_LOGREQUEST;
    static final String TMP_CSRF_SAMESITE_STRICT_COOKIENAME = "_zcsr_tmp";
    private static final List<String> HTTP_OVERRIDE_METHODS;
    private static final List<String> HTTP_MULTIPART_METHODS;
    public static final String Z_SIGNED_REMOTE_USER_IP = "Z-SIGNED_REMOTE_USER_IP";
    static ThreadLocal<Integer> numberOfRedirectCounts;
    private static final BitSet CHARACTERS_TO_ESCAPE;
    private static final char[] CHAR_ARRAY;
    private static KeyFactory keyFactory;
    private static ZSecThreadLocal<String> currentWebAppName;
    static final LRUCacheMap<String, String> CSRFTOKENTOTICKETMAP;
    private static String confDirPath;
    
    public static MatcherUtil getMatcherUtil() {
        return SecurityUtil.matcherUtil;
    }
    
    public static void setMatcherUtil(final MatcherUtil matcherUtilObj) {
        SecurityUtil.matcherUtil = matcherUtilObj;
    }
    
    public static String filterXSS(final SecurityRequestWrapper request, final String parameterValue, final String xssPatternName) {
        return filterXSS(request.getRequestWebContext(), parameterValue, xssPatternName, request.getServerName());
    }
    
    public static String filterXSS(final String webContext, final String parameterValue, final String xssPatternName, final String serverName) {
        final XSSUtil xssUtil = SecurityFilterProperties.getInstance(webContext).getXSSUtil(xssPatternName);
        if (xssUtil == null) {
            SecurityUtil.LOGGER.log(Level.SEVERE, "Invalid XSS filter configuation - Filter XSS method :: XSSPatternName : {0} ", xssPatternName);
            throw new IAMSecurityException("INVALID_XSSFILTER_CONFIGURATION");
        }
        if (SecurityFilterProperties.getInstance(webContext).isTrustedScriptEnabled()) {
            return xssUtil.filterXSS(serverName, parameterValue);
        }
        return xssUtil.filterXSS(parameterValue);
    }
    
    public static Pattern getRegexPattern(final SecurityRequestWrapper request, final String regexPattern) {
        return SecurityFilterProperties.getInstance((HttpServletRequest)request).getRegexPattern(regexPattern);
    }
    
    public static RegexRule getRegexRule(final SecurityRequestWrapper request, final String regexPattern) {
        return SecurityFilterProperties.getInstance((HttpServletRequest)request).getRegexRule(regexPattern);
    }
    
    public static boolean isValidPattern(final String string, final Pattern pattern) {
        return matchPattern(string, pattern);
    }
    
    public static boolean isValidPattern(final String string, final String pattern) {
        return matchPattern(string, pattern);
    }
    
    public static boolean detectXSS(final String string, final boolean isEnableXSSTimeoutMatcher) {
        return XSSUtil.detectXSS(string, isEnableXSSTimeoutMatcher);
    }
    
    public static boolean detectXSS(final String string) {
        return XSSUtil.detectXSS(string);
    }
    
    public static String escapeHTMLTags(final String string) {
        return htmlEscape(string);
    }
    
    public static String getCSRFCookieName(final HttpServletRequest request) {
        return SecurityFilterProperties.getInstance(request).getCSRFCookieName();
    }
    
    public static String getStatelessAuthCSRFCookieName(final HttpServletRequest request) {
        return SecurityFilterProperties.getInstance(request).getCSRFCookieName() + "_slauth";
    }
    
    private static String getAuthCSRFCookieName(final HttpServletRequest request) {
        if (((SecurityRequestWrapper)request).isAuthenticatedViaStatelessCookie) {
            return getStatelessAuthCSRFCookieName(request);
        }
        return getCSRFCookieName(request);
    }
    
    public static String getCSRFParamName(final HttpServletRequest request) {
        return SecurityFilterProperties.getInstance(request).getCSRFParamName();
    }
    
    public static Object applyJSONXSSPattern(final SecurityRequestWrapper request, final String jsonType, final String xssPatternName, final String paramName, final String parameterValue) throws JSONException {
        Object value = null;
        if ("escape".equals(xssPatternName)) {
            final IAMJSONTokener tokener = new IAMJSONTokener(parameterValue, true);
            if (jsonType.equals("JSONObject")) {
                value = new JSONObject((JSONTokener)tokener);
            }
            else if (jsonType.equals("JSONArray")) {
                value = new JSONArray((JSONTokener)tokener);
            }
        }
        else {
            final boolean detectXSS = detectXSS(parameterValue, SecurityFilterProperties.getInstance((HttpServletRequest)request).isEnableXSSTimeoutMatcher());
            if (detectXSS && ("throw".equals(xssPatternName) || "throwerror".equalsIgnoreCase(xssPatternName))) {
                throw new IAMSecurityException("XSS_DETECTED");
            }
            if (!detectXSS) {
                if (jsonType.equals("JSONObject")) {
                    value = new JSONObject(parameterValue);
                }
                else if (jsonType.equals("JSONArray")) {
                    value = new JSONArray(parameterValue);
                }
            }
            else {
                final XSSUtil xssUtil = SecurityFilterProperties.getInstance((HttpServletRequest)request).getXSSUtil(xssPatternName);
                if (xssUtil == null) {
                    SecurityUtil.LOGGER.log(Level.SEVERE, "Invalid XSS filter configuation - JSON validation :: Param Name : {0} , XSSPatternName : {1} , IPAddress : {2}", new Object[] { paramName, xssPatternName, request.getRemoteAddr() });
                    throw new IAMSecurityException("INVALID_XSSFILTER_CONFIGURATION");
                }
                final IAMJSONTokener tokener2 = new IAMJSONTokener(parameterValue, xssUtil);
                if (jsonType.equals("JSONObject")) {
                    value = new JSONObject((JSONTokener)tokener2);
                }
                else if (jsonType.equals("JSONArray")) {
                    value = new JSONArray((JSONTokener)tokener2);
                }
            }
        }
        return value;
    }
    
    public static String applyXSSPattern(final SecurityRequestWrapper request, final String xssPatternName, final String parameterName, final String parameterValue) {
        final SecurityFilterProperties secFilterProps = SecurityFilterProperties.getInstance((HttpServletRequest)request);
        if ("escape".equalsIgnoreCase(xssPatternName)) {
            return escapeHTMLTags(parameterValue);
        }
        if (secFilterProps.isXSSPatternDetectEnabled() && ("throwerror".equalsIgnoreCase(xssPatternName) || "throw".equals(xssPatternName))) {
            if (detectXSS(parameterValue, secFilterProps.isEnableXSSTimeoutMatcher())) {
                SecurityUtil.LOGGER.log(Level.FINE, "XSS DETECTED  PARAM_NAME : {0} IPADDRESS : {1}", new Object[] { parameterName, request.getRemoteAddr() });
                throw new IAMSecurityException("XSS_DETECTED");
            }
            return parameterValue;
        }
        else {
            if (isClearText(request, "cleartext:check", parameterName, parameterValue)) {
                SecurityUtil.LOGGER.log(Level.FINE, "XSS DETECT/FILTERING NOT NEEDED AS CLEARTEXT  PARAM : {0} ", parameterName);
                return parameterValue;
            }
            final XSSUtil xssUtil = SecurityFilterProperties.getInstance((HttpServletRequest)request).getXSSUtil(xssPatternName);
            if (xssUtil == null) {
                SecurityUtil.LOGGER.log(Level.SEVERE, "Invalid XSS filter configuation - Parameter validation :: Param Name : {0} , XSSPatternName : {1} , IPAddress : {2}", new Object[] { parameterName, xssPatternName, request.getRemoteAddr() });
                throw new IAMSecurityException("INVALID_XSSFILTER_CONFIGURATION");
            }
            if (!secFilterProps.isXSSPatternDetectEnabled() || xssUtil.balanceHtmlTags() || secFilterProps.isXSSPatternDetectForFilterDisabled() || detectXSS(parameterValue, secFilterProps.isEnableXSSTimeoutMatcher())) {
                final String origStr = parameterValue;
                final String resultStr = filterXSS(request, parameterValue, xssPatternName);
                if (!origStr.equals(resultStr)) {
                    SecurityUtil.LOGGER.log(Level.WARNING, "XSS FILTERED  PARAM_NAME : {0} IPADDRESS : {1}", new Object[] { parameterName, request.getRemoteAddr() });
                    final List<String> remElements = xssUtil.getRemovedElements();
                    if (remElements != null) {
                        request.setRemovedXSSElements(parameterName, xssUtil.getRemovedElements());
                    }
                    if (secFilterProps != null && secFilterProps.isXSSFilterLogEnabled()) {
                        SecurityUtil.LOGGER.log(Level.WARNING, "XSS_FILTER_LOG :: PARAM_NAME : {0}  IPADDRESS : {1} \nORIGINAL_STRING : {2} \nXSS_FILTERED_STRING : {3} ", new Object[] { parameterName, request.getRemoteAddr(), XSSUtil.getLogString(origStr), XSSUtil.getLogString(resultStr) });
                    }
                }
                return resultStr;
            }
            return parameterValue;
        }
    }
    
    public static String applyClearTextPattern(final SecurityRequestWrapper request, final String dataType, final String parameterName, final String parameterValue) {
        if (isClearText(request, dataType, parameterName, parameterValue)) {
            return parameterValue;
        }
        if ("cleartext:removehtmlentities".equals(dataType)) {
            return removeHtmlEntities(parameterValue);
        }
        String xssPatternName = "throwerror";
        if ("cleartext:filter".equals(dataType)) {
            xssPatternName = "htmlfilter";
        }
        else {
            final int parIdx = dataType.indexOf("cleartext:filter:");
            if (parIdx != -1) {
                xssPatternName = dataType.substring("cleartext:filter:".length(), dataType.length());
            }
        }
        return applyXSSPattern(request, xssPatternName, parameterName, parameterValue);
    }
    
    public static boolean isClearText(final SecurityRequestWrapper request, final String dataType, final String parameterName, final String parameterValue) {
        final String allowedValueRegex = "cleartextpattern";
        final SecurityFilterProperties filterConfig = SecurityFilterProperties.getInstance((HttpServletRequest)request);
        final RegexRule regexRule = filterConfig.getRegexRule(allowedValueRegex);
        if (regexRule == null) {
            SecurityUtil.LOGGER.log(Level.WARNING, "Regular Expression : cleartextpattern not found in security-common.xml . Check if you are using older version of security-common.xml");
            SecurityUtil.LOGGER.log(Level.WARNING, "Type: {0} defined for parameter name : {1}", new Object[] { dataType, parameterName });
            throw new IAMSecurityException("PATTERN_NOT_DEFINED");
        }
        return matchPattern(parameterValue, regexRule);
    }
    
    private static String generateCSRFCookieValue() {
        return UUID.randomUUID().toString();
    }
    
    public static void setCookie(final HttpServletRequest request, final HttpServletResponse response, final String name, final String value) {
        setCookie(request, response, name, value, false);
    }
    
    public static void setCookie(final HttpServletRequest request, final HttpServletResponse response, final String name, final String value, final boolean secure) {
        final Cookie ca = new Cookie(name, value);
        ca.setPath("/");
        ca.setSecure(secure);
        response.addCookie(ca);
    }
    
    public static void removeCSRFCookie(final HttpServletRequest request, final HttpServletResponse response) {
        setCSRFCookie(request, response, getCSRFCookieName(request), "", "/", 0);
    }
    
    public static String setCSRFCookie(final HttpServletRequest request, final HttpServletResponse response) {
        final String ticket = getIAMAuthenticatedCookie(request);
        String csrfCookieValue = null;
        if (isValid(ticket) && !SecurityFilterProperties.getInstance(request).isAuthCSRFDisabled()) {
            csrfCookieValue = generateAuthCSRFToken(request, response);
        }
        return setCSRFCookie(request, response, csrfCookieValue);
    }
    
    private static boolean isSecureOrProxyRequest(final HttpServletRequest request) {
        return "https".equalsIgnoreCase(request.getScheme()) || ((SecurityRequestWrapper)request).isProxyRequest();
    }
    
    static String setCSRFCookie(final HttpServletRequest request, final HttpServletResponse response, String cookieValue) {
        if (cookieValue == null) {
            cookieValue = generateCSRFCookieValue();
        }
        final String cookieName = (cookieValue != null && cookieValue.length() == 128) ? getAuthCSRFCookieName(request) : getCSRFCookieName(request);
        request.setAttribute("_IAM_COOKIE_" + cookieName, (Object)cookieValue);
        setCSRFCookie(request, response, cookieName, cookieValue, "/", -1);
        return cookieValue;
    }
    
    private static void setCSRFCookie(final HttpServletRequest request, final HttpServletResponse response, final String cookieName, final String cookieValue, final String path, final int maxAge) {
        final HttpCookie cookie = new HttpCookie(cookieName, cookieValue);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setPriority(HttpCookie.PRIORITY.HIGH);
        final boolean isSecure = isSecureOrProxyRequest(request);
        cookie.setSecure(isSecure);
        final SecurityFilterProperties filterProps = SecurityFilterProperties.getInstance(request);
        final HttpCookie.SAMESITE sameSiteMode = filterProps.getCSRFCookieSamesiteMode();
        if (sameSiteMode != HttpCookie.SAMESITE.NONE) {
            cookie.setSameSite(sameSiteMode.getValue());
        }
        else if (isSecure) {
            final UserAgent ua = ((SecurityRequestWrapper)request).getUserAgent();
            if (ua != null && !ua.isSameSiteNoneIncompatibleClient()) {
                cookie.setSameSite(sameSiteMode.getValue());
            }
        }
        response.addHeader("Set-Cookie", cookie.generateCookie());
        if (sameSiteMode != HttpCookie.SAMESITE.STRICT && filterProps.isEnabledCSRFSamesiteStrictTmpCookie()) {
            HttpCookie tmpCookie;
            try {
                tmpCookie = (HttpCookie)cookie.clone();
            }
            catch (final CloneNotSupportedException ex) {
                SecurityUtil.LOGGER.log(Level.WARNING, "Unable to set CSRF cookie with samesite=strict. RequestURI : {0}, Ex Message : {1}", new Object[] { request.getRequestURI(), ex.getMessage() });
                return;
            }
            tmpCookie.name = "_zcsr_tmp";
            tmpCookie.setSameSite(HttpCookie.SAMESITE.STRICT.getValue());
            response.addHeader("Set-Cookie", tmpCookie.generateCookie());
        }
    }
    
    public static String getCSRFCookie(final HttpServletRequest request) {
        return getCSRFCookie(request, true);
    }
    
    public static String getCSRFCookie(final HttpServletRequest request, final boolean validateCSRFValue) {
        final String attrName = "_IAM_COOKIE_" + getCSRFCookieName(request);
        if (request.getAttribute(attrName) != null) {
            return (String)request.getAttribute(attrName);
        }
        final String cookieValue = getCookie(request, getCSRFCookieName(request));
        if (validateCSRFValue && !SecurityFilterProperties.getInstance(request).isAuthCSRFDisabled()) {
            final String ticket = getIAMCookie(request);
            if (ticket != null && cookieValue != null) {
                if (isValidCurrentCSRFCookie(request, null, cookieValue, ticket)) {
                    return cookieValue;
                }
                return null;
            }
        }
        return cookieValue;
    }
    
    public static String getStatelessAuthCSRFCookie(final HttpServletRequest request) {
        final String csrfCookieName = getStatelessAuthCSRFCookieName(request);
        final String attrName = "_IAM_COOKIE_" + csrfCookieName;
        if (request.getAttribute(attrName) != null) {
            return (String)request.getAttribute(attrName);
        }
        return getCookie(request, csrfCookieName);
    }
    
    public static boolean isInDevelopmentMode(final HttpServletRequest request) {
        return SecurityFilterProperties.getInstance(request).isDevelopmentMode();
    }
    
    public static String getFileAsString(final File file) throws FileNotFoundException, IOException {
        final StringWriter content = new StringWriter();
        int readCount = 0;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            final char[] buf = new char[8192];
            while ((readCount = reader.read(buf)) > 0) {
                content.write(buf, 0, readCount);
            }
        }
        finally {
            if (reader != null) {
                reader.close();
            }
            if (content != null) {
                content.close();
            }
        }
        return content.toString();
    }
    
    public static void writeToFile(final File file, final String content) throws Exception {
        ByteArrayInputStream in = null;
        OutputStream out = null;
        try {
            in = new ByteArrayInputStream(content.getBytes("UTF8"));
            out = new FileOutputStream(file);
            final byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
        finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
    
    public static int getRedirectCount() {
        if (SecurityUtil.numberOfRedirectCounts.get() == null) {
            SecurityUtil.numberOfRedirectCounts.set(0);
        }
        return SecurityUtil.numberOfRedirectCounts.get();
    }
    
    public static void resetRedirectCount() {
        SecurityUtil.numberOfRedirectCounts.set(0);
    }
    
    public static void incrementRedirectCount() {
        int i = getRedirectCount();
        SecurityUtil.numberOfRedirectCounts.set(++i);
    }
    
    public static String convertInputStreamAsString(final InputStream in, final long maxSize) {
        int read = 0;
        final StringBuilder content = new StringBuilder();
        long streamSize = 0L;
        final char[] buffer = new char[1024];
        Reader reader = null;
        try {
            reader = new InputStreamReader(in, "UTF-8");
            do {
                read = reader.read(buffer, 0, 1024);
                if (read > 0) {
                    content.append(buffer, 0, read);
                }
                streamSize += read;
                if (maxSize != -1L && streamSize > maxSize) {
                    SecurityUtil.LOGGER.log(Level.WARNING, "Inputstream exceed the maximum allowable length");
                    throw new IAMSecurityException("MORE_THAN_MAX_LENGTH");
                }
            } while (read >= 0);
        }
        catch (final UnsupportedEncodingException ex) {
            SecurityUtil.LOGGER.log(Level.WARNING, "Unable to read input stream (Encoding Not Supported) {0}", ex.getMessage());
            throw new IAMSecurityException("UNABLE_TO_ENCODE_INPUTSTREAM");
        }
        catch (final IOException io) {
            SecurityUtil.LOGGER.log(Level.WARNING, "Exception occured while reading input stream reason {0}", io.getMessage());
            throw new IAMSecurityException("UNABLE_TO_READ_INPUTSTREAM");
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final IOException ex2) {
                SecurityUtil.LOGGER.log(Level.INFO, "Unable to close inputstream reader");
            }
        }
        return content.toString().trim();
    }
    
    static UploadedFileItem importFromURL(URL url, final String uploadFieldName, final UploadFileRule rule, final SecurityRequestWrapper request) {
        if (url == null) {
            return null;
        }
        String xForwardedFor = request.getRemoteAddr();
        final String userAgent = request.getHeader("User-Agent");
        if (request.getHeader("X-Forwarded-For") != null) {
            xForwardedFor = request.getHeader("X-Forwarded-For") + ", " + request.getRemoteAddr();
        }
        final String fieldName = isValid(uploadFieldName) ? uploadFieldName : rule.getFieldName();
        try {
            String params = url.getQuery();
            params = ((params == null || "".equals(params)) ? "" : ("?" + params));
            url = new URL(url.getProtocol() + "://" + url.getHost() + ((url.getPort() == -1 || url.getPort() == 80 || url.getPort() == 443) ? "" : (":" + url.getPort())) + escapeURLPath(url.getPath()) + params);
            HttpURLConnection urlConn;
            try {
                final URLWrapper wrappedURL = new URLWrapper(url.toString(), true);
                if (userAgent != null) {
                    wrappedURL.setRequestProperty("User-Agent", userAgent);
                }
                if (xForwardedFor != null) {
                    wrappedURL.setRequestProperty("X-Forwarded-For", xForwardedFor);
                }
                final SecurityFilterProperties secFilterProps = SecurityFilterProperties.getInstance((HttpServletRequest)request);
                wrappedURL.setConnectTimeout(secFilterProps.getUploadFileRuleConnectionTimeOut());
                wrappedURL.setReadTimeout(secFilterProps.getUploadFileRuleReadTimeOut());
                wrappedURL.setInstanceFollowRedirects(secFilterProps.isAllowImportURLRedirect());
                wrappedURL.isImportURL(true);
                urlConn = wrappedURL.openURLConnection(SecurityFilterProperties.getProxy());
            }
            catch (final IAMSecurityException ex) {
                throw new IAMSecurityException(ex.getErrorCode(), request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
            }
            return createUploadedFileItem(fieldName, url.toString(), urlConn.getContentType(), rule, urlConn.getInputStream(), urlConn.getHeaderFields(), request);
        }
        catch (final IAMSecurityException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            SecurityUtil.LOGGER.log(Level.WARNING, "Import from the URL \"{0}\" failed", url);
            SecurityUtil.LOGGER.log(Level.WARNING, "", ex3);
            throw new IAMSecurityException("UNABLE_TO_IMPORT", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
        }
    }
    
    static UploadedFileItem createUploadedFileItem(final String fieldName, final String fileName, final String contentType, final UploadFileRule fileRule, final InputStream inputStream, final Map<String, List<String>> headerFields, final SecurityRequestWrapper request) throws Exception {
        DiskFileItemFactory factory = null;
        DiskFileItem item = null;
        try {
            final File fileUploadDir = getTempFileUploadDir();
            factory = new DiskFileItemFactory(0, fileUploadDir);
            item = SecurityDiskFileItem.createDiskFileItem(new TempFileName(request.getURLActionRule()), factory, fieldName, null, false, fileName);
            long fileMaxSizeInBytes = fileRule.getMaxSizeInKB();
            if (fileMaxSizeInBytes > 0L) {
                fileMaxSizeInBytes = fileRule.getMaxSizeInKB() * 1024L;
            }
            MessageDigest md = null;
            if (AppSenseAgent.isEnableReqInfoFileHash()) {
                md = MessageDigest.getInstance(AppSenseAgent.getFileHashAlgorithm());
            }
            copy(inputStream, new byte[8192], fileMaxSizeInBytes, fileRule, item, (HttpServletRequest)request, md);
            if (item.getSize() == 0L && !fileRule.isAllowedEmptyFile()) {
                SecurityUtil.LOGGER.log(Level.SEVERE, "Empty file is not allowed for the file rule {0} for the request URI {1} and the import url is {2}", new Object[] { item.getFieldName(), request.getRequestURI(), fileName });
                throw new IAMSecurityException("EMPTY_FILE_NOT_ALLOWED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), item.getFieldName());
            }
            final File uploadedFile = item.getStoreLocation();
            if (item.getSize() > 0L && uploadedFile != null) {
                final ExecutionTimer cdTimer = ExecutionTimer.startInstance();
                final String detectedContentType = getMimeType(request, uploadedFile, null);
                ZSEC_PERFORMANCE_ANOMALY.pushMimeDetection(request.getRequestURI(), fileName, detectedContentType, cdTimer);
                final UploadedFileItem uploadedFileItem = new UploadedFileItem(fileName, item.getSize(), uploadedFile, fieldName, (contentType == null) ? detectedContentType : contentType, detectedContentType, item);
                if (md != null) {
                    uploadedFileItem.setFileHash(getDigestString(md));
                }
                if (headerFields != null) {
                    uploadedFileItem.setHeaderFields(headerFields);
                }
                return uploadedFileItem;
            }
        }
        catch (final Exception ex) {
            if (item != null) {
                item.delete();
            }
            throw ex;
        }
        return null;
    }
    
    static long copy(final InputStream pIn, final byte[] pBuffer, final long maxSizeInBytes, final UploadFileRule uploadRule, final DiskFileItem fileItem, final HttpServletRequest request, final MessageDigest md) throws IOException {
        OutputStream out = fileItem.getOutputStream();
        InputStream in = pIn;
        try {
            long total = 0L;
            while (true) {
                final int res = in.read(pBuffer);
                if (res == -1) {
                    if (out != null) {
                        out.close();
                        out.flush();
                        out = null;
                    }
                    in.close();
                    in = null;
                    return total;
                }
                if (res <= 0) {
                    continue;
                }
                total += res;
                if (maxSizeInBytes > -1L && maxSizeInBytes < total) {
                    SecurityUtil.LOGGER.log(Level.SEVERE, "Size of uploaded file is more than the allowed-file-size limit of \"{0}\" kb ", new Object[] { maxSizeInBytes / 1024L });
                    throw new IAMSecurityException("FILE_SIZE_MORE_THAN_ALLOWED_SIZE", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), null, fileItem.getName(), total, uploadRule.getFieldName(), uploadRule);
                }
                if (out == null) {
                    continue;
                }
                out.write(pBuffer, 0, res);
                if (md == null) {
                    continue;
                }
                md.update(pBuffer, 0, res);
            }
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (final Throwable t) {
                    SecurityUtil.LOGGER.log(Level.FINEST, t.getMessage());
                }
            }
            if (out != null) {
                try {
                    out.close();
                }
                catch (final Throwable t) {
                    SecurityUtil.LOGGER.log(Level.FINEST, t.getMessage());
                }
            }
        }
    }
    
    static InputStream decodeInputStream(final String dataURIDatapart, final String dataURIEncoding, final SecurityRequestWrapper request) {
        if ("base64".equalsIgnoreCase(dataURIEncoding)) {
            return new ByteArrayInputStream(Base64.decodeBase64(dataURIDatapart));
        }
        SecurityUtil.LOGGER.log(Level.WARNING, "\n  Unsupported Encoding format: \"{0}\" ", new Object[] { dataURIEncoding });
        throw new IAMSecurityException("UNSUPPORTED_ENCODING_IN_DATAURI", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
    }
    
    public static HashMap<String, String> convertToMap(final Element element) {
        final NamedNodeMap attributes = element.getAttributes();
        if (attributes.getLength() > 0) {
            final HashMap<String, String> toReturn = new HashMap<String, String>();
            for (int i = 0; i < attributes.getLength(); ++i) {
                final Node n = attributes.item(i);
                toReturn.put(n.getNodeName(), n.getNodeValue());
            }
            return toReturn;
        }
        return null;
    }
    
    public static String escapeURLPath(final String urlPath) {
        if (urlPath == null || "".equalsIgnoreCase(urlPath)) {
            return urlPath;
        }
        final char[] res = new char[3 * urlPath.length()];
        int rc = 0;
        for (int i = 0; i < urlPath.length(); ++i) {
            final char ch = urlPath.charAt(i);
            if (ch >= '\u0080' || SecurityUtil.CHARACTERS_TO_ESCAPE.get(ch)) {
                res[rc++] = '%';
                res[rc++] = SecurityUtil.CHAR_ARRAY[(ch & '\u00f0') >>> 4];
                res[rc++] = SecurityUtil.CHAR_ARRAY[ch & '\u000f'];
            }
            else {
                res[rc++] = ch;
            }
        }
        if (rc > urlPath.length()) {
            return new String(res, 0, rc);
        }
        return urlPath;
    }
    
    public static XSSUtil createXSSUtil(final String xssPattern, final String[] filePaths) throws Exception {
        final SecurityFilterProperties secFilterProps = new SecurityFilterProperties();
        for (final String filepath : filePaths) {
            RuleSetParser.initSecurityRules(secFilterProps, new File(filepath));
        }
        secFilterProps.initXSSUtils();
        return secFilterProps.getXSSUtil(xssPattern);
    }
    
    public static String getContextPath(final HttpServletRequest request) {
        Object context = request.getAttribute("ZSEC_CONTEXT_PATH");
        if (context == null) {
            context = request.getAttribute("ZSEC_API_CONTEXT_PATH");
        }
        if (context != null) {
            return context.toString();
        }
        throw new NullPointerException("Request Context is null");
    }
    
    public static boolean isMatches(final String patterns, final String input) {
        if (!isValid(patterns)) {
            return false;
        }
        final String[] split;
        final String[] urls = split = patterns.split(",");
        for (final String url : split) {
            if (!url.isEmpty() && (url.equals("*") || input.equals(url) || matchPattern(input, url))) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isValid(final Object value) {
        return value != null && !value.equals("null") && !value.equals("");
    }
    
    public static boolean isNullObject(final Object obj) {
        return obj == null;
    }
    
    public static boolean isEmpty(final Object value) {
        return "".equals(value);
    }
    
    public static String htmlEscape(final String s) {
        if (s == null) {
            return null;
        }
        final StringBuilder html = new StringBuilder(s.length() + 50);
        for (final char c : s.toCharArray()) {
            switch (c) {
                case '<': {
                    html.append("&lt;");
                    break;
                }
                case '>': {
                    html.append("&gt;");
                    break;
                }
                case '&': {
                    html.append("&amp;");
                    break;
                }
                case '\"': {
                    html.append("&quot;");
                    break;
                }
                case '\'': {
                    html.append("&#39;");
                    break;
                }
                default: {
                    html.append(c);
                    break;
                }
            }
        }
        return html.toString();
    }
    
    public static boolean isTrustedIP(final HttpServletRequest request) {
        if (SecurityFilterProperties.getInstance(request).isAuthenticationProviderConfigured()) {
            return SecurityFilterProperties.getInstance(request).getAuthenticationProvider().isTrustedIP(request);
        }
        return SecurityFilterProperties.isTrustedIP(request.getRemoteAddr());
    }
    
    public static HttpServletRequest getCurrentRequest() {
        return SecurityUtil.CURRENT_REQUEST.get();
    }
    
    public static List<JSONObject> getJsonexceptiontracelist() {
        List<JSONObject> jsonExcepTraceList = SecurityUtil.JSONEXCEPTIONTRACELIST.get();
        if (jsonExcepTraceList == null) {
            jsonExcepTraceList = new ArrayList<JSONObject>();
            SecurityUtil.JSONEXCEPTIONTRACELIST.set(jsonExcepTraceList);
        }
        return jsonExcepTraceList;
    }
    
    public static List<Object> getEventObjectList() {
        List<Object> events = SecurityUtil.ZSECEVENTS.get();
        if (events == null) {
            events = new ArrayList<Object>();
            SecurityUtil.ZSECEVENTS.set(events);
        }
        return events;
    }
    
    public static void setCurrentRequest(final HttpServletRequest request) {
        SecurityUtil.CURRENT_REQUEST.set(request);
        try {
            if (request != null) {
                final String contextPath = getContextPath(request);
                if (contextPath != null) {
                    final SecurityFilterProperties filterProps = SecurityFilterProperties.getInstance(contextPath);
                    if (isValid(filterProps)) {
                        filterProps.getRACProvider().setCurrentRequest(request);
                    }
                }
            }
        }
        catch (final Exception n) {
            SecurityUtil.LOGGER.log(Level.INFO, "AppFirewall Exception:Context Path not found for the request.Unable to setCurrentRequest");
        }
    }
    
    static HttpServletRequest getCurrentLogRequest() {
        return SecurityUtil.CURRENT_LOGREQUEST.get();
    }
    
    static void setCurrentLogRequest(final HttpServletRequest request) {
        SecurityUtil.CURRENT_LOGREQUEST.set(request);
    }
    
    public static int getInt(final String str) {
        if (isValid(str)) {
            try {
                return Integer.parseInt(str);
            }
            catch (final NumberFormatException ex) {
                SecurityUtil.LOGGER.log(Level.WARNING, ex.getMessage());
            }
        }
        return -1;
    }
    
    public static long getLong(final String str) {
        if (isValid(str)) {
            try {
                return Long.parseLong(str);
            }
            catch (final NumberFormatException ex) {
                SecurityUtil.LOGGER.log(Level.WARNING, ex.getMessage());
            }
        }
        return -1L;
    }
    
    public static String getCookie(final HttpServletRequest request, final String name) {
        final Cookie[] cookies = request.getCookies();
        String cookieValue = null;
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookieValue = cookie.getValue();
                    break;
                }
            }
        }
        return cookieValue;
    }
    
    public static Properties getElementProperties(final Element elem) {
        final Properties props = new Properties();
        try {
            final NamedNodeMap nmap = elem.getAttributes();
            for (int i = 0; i < nmap.getLength(); ++i) {
                final String key = nmap.item(i).getNodeName();
                final String value = nmap.item(i).getNodeValue();
                props.setProperty(key, value);
            }
        }
        catch (final Exception e) {
            SecurityUtil.LOGGER.log(Level.WARNING, e.getMessage(), e.getMessage());
        }
        return props;
    }
    
    public static String getDomain(final String domain) {
        return getDomainWithPort(domain, false);
    }
    
    public static String getDomainWithPort(final String domain) {
        return getDomainWithPort(domain, true);
    }
    
    private static String getDomainWithPort(final String completeURL, final boolean isAppendPort) {
        if (!isValid(completeURL)) {
            return null;
        }
        try {
            final URI uriObj = new URI(completeURL);
            String domain = isValidScheme(uriObj.getScheme()) ? uriObj.getHost() : null;
            if (isAppendPort && domain != null && uriObj.getPort() != -1) {
                domain = domain + ":" + Integer.toString(uriObj.getPort());
            }
            return domain;
        }
        catch (final URISyntaxException e) {
            SecurityUtil.LOGGER.log(Level.WARNING, e.getMessage());
            return null;
        }
    }
    
    static boolean isValidScheme(final String scheme) {
        return "https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme);
    }
    
    public static byte[] BASE64_DECODE(final String base64String) {
        return Base64.decodeBase64(base64String);
    }
    
    public static String BASE16_ENCODE(final byte[] input) {
        final char[] b16 = new char[input.length * 2];
        int i = 0;
        for (final byte c : input) {
            final int low = c & 0xF;
            final int high = (c & 0xF0) >> 4;
            b16[i++] = SecurityUtil.HEX[high];
            b16[i++] = SecurityUtil.HEX[low];
        }
        return new String(b16);
    }
    
    public static byte[] BASE16_DECODE(final String b16str) {
        final int len = b16str.length();
        final byte[] out = new byte[len / 2];
        int j = 0;
        for (int i = 0; i < len; i += 2) {
            final int c1 = INT(b16str.charAt(i));
            final int c2 = INT(b16str.charAt(i + 1));
            final int bt = c1 << 4 | c2;
            out[j++] = (byte)bt;
        }
        return out;
    }
    
    public static int INT(final char c) {
        return Integer.decode("0x" + c);
    }
    
    public static String sign(final HttpServletRequest request) throws Exception {
        return sign();
    }
    
    public static String sign(final String webappName) throws Exception {
        return sign();
    }
    
    public static String sign() throws Exception {
        return sign("", SystemAuthType.ISC);
    }
    
    private static String sign(final String data, final SystemAuthType systemAuthType) throws Exception {
        final boolean isc = SystemAuthType.ISC == systemAuthType;
        final PrivateKey privateKey = isc ? SecurityFilterProperties.getPrivateKey() : SecurityFilterProperties.getInterDCPrivateKey();
        if (privateKey == null) {
            SecurityUtil.LOGGER.log(Level.WARNING, "Private key not cofigured for {0} communication: ", systemAuthType.value);
            throw new IAMSecurityException("ISC_PRIVATE_KEY_NOT_CONFIGURED");
        }
        final String currentTimeStr = String.valueOf(System.currentTimeMillis());
        final String signStr = isValid(data) ? (data + currentTimeStr) : currentTimeStr;
        final Signature rsa = Signature.getInstance("MD5withRSA");
        rsa.initSign(privateKey);
        rsa.update(signStr.getBytes());
        final byte[] realSig = rsa.sign();
        final String sStr = BASE16_ENCODE(realSig);
        final String serviceName = SecurityFilterProperties.getServiceName();
        if (!isValid(serviceName)) {
            SecurityUtil.LOGGER.log(Level.SEVERE, " {0} signature is not supported for this service due to Invalid Service name . Service Name ::: {1} ", new Object[] { systemAuthType.value, serviceName });
            throw new IAMSecurityException("ISC_SIGNATURE_INVALID_SERVICE_NAME");
        }
        if (isc) {
            return getISCSignatureHash(serviceName, data, currentTimeStr, sStr);
        }
        return SecurityFilterProperties.currentDCLocation + "." + serviceName + "-" + currentTimeStr + "-" + sStr;
    }
    
    private static String getISCSignatureHash(final String serviceName, final String data, final String currentTimeStr, final String sStr) {
        if (isValid(data)) {
            return serviceName + "-" + data + "-" + currentTimeStr + "-" + sStr;
        }
        return serviceName + "-" + currentTimeStr + "-" + sStr;
    }
    
    public static String getDCSignature() throws Exception {
        return sign("", SystemAuthType.INTERDC);
    }
    
    public static KeyFactory getKeyFactory() throws Exception {
        if (SecurityUtil.keyFactory == null) {
            SecurityUtil.keyFactory = KeyFactory.getInstance("RSA");
        }
        return SecurityUtil.keyFactory;
    }
    
    static String removeHtmlEntities(final String inputParamValue) {
        return SecurityUtil.inverseClearTextPattern.matcher(inputParamValue).replaceAll(" ").trim();
    }
    
    public static Map<Object, String> getResponseStatus(final String secException) {
        final Map<Object, String> statusMap = new HashMap<Object, String>();
        if (secException.equals("URL_RULE_NOT_CONFIGURED") || secException.equals("UPLOAD_RULE_NOT_CONFIGURED")) {
            statusMap.put(404, "Not Found");
        }
        else if (secException.equals("NOT_AUTHENTICATED") || secException.equals("AUTHENTICATION_FAILED") || secException.equals("INVALID_OAUTHTOKEN") || secException.equals("INVALID_OAUTHSCOPE")) {
            statusMap.put(401, "Unauthorized");
        }
        else if (secException.equals("UNAUTHORISED")) {
            statusMap.put(403, "Forbidden");
        }
        else {
            statusMap.put(400, "Bad Request");
        }
        return statusMap;
    }
    
    public static List<String> getStringAsList(final String roleStr, String delim) {
        if (roleStr == null) {
            return null;
        }
        delim = ((delim == null) ? " " : delim);
        final String[] rs = roleStr.split(delim);
        return new ArrayList<String>(Arrays.asList(rs));
    }
    
    public static void setCurrentWebapp(final String currentWebApp) {
        SecurityUtil.currentWebAppName.set(currentWebApp);
    }
    
    public static String getCurrentWebApp() {
        return SecurityUtil.currentWebAppName.get();
    }
    
    public static ArrayList<String> getSecretParamList(final SecurityRequestWrapper request) {
        final Set<String> requestParams = request.getParameterMap().keySet();
        if (!requestParams.isEmpty()) {
            final ArrayList<String> secretRequestParamsList = new ArrayList<String>();
            final ActionRule actionRule = request.getURLActionRule();
            final boolean isActionRuleExist = actionRule != null;
            final boolean partialMaskingEnabled = isActionRuleExist && actionRule.getPartialMaskingParamRules() != null;
            Map<String, String[]> partiallyMaskedParamMap = null;
            if (partialMaskingEnabled) {
                partiallyMaskedParamMap = new HashMap<String, String[]>();
            }
            final SecurityFilterProperties filterConfig = SecurityFilterProperties.getInstance((HttpServletRequest)request);
            for (final String requestParam : requestParams) {
                ParameterRule partialMaskingParamRule = null;
                if (partialMaskingEnabled && (partialMaskingParamRule = actionRule.getPartialMaskingParamRule(requestParam)) != null) {
                    final String[] paramValues = request.getParameterValuesForLogging(requestParam);
                    if (paramValues == null) {
                        continue;
                    }
                    final int length = paramValues.length;
                    final String[] partiallyMaskedValues = new String[length];
                    for (int i = 0; i < length; ++i) {
                        partiallyMaskedValues[i] = MaskUtil.getPartiallyMaskedValue(paramValues[i], partialMaskingParamRule);
                    }
                    partiallyMaskedParamMap.put(requestParam, partiallyMaskedValues);
                }
                else {
                    if (!SecurityRequestWrapper.getDefaultSecretParameters().contains(requestParam) && (!isActionRuleExist || !isURLSecretParam(requestParam, actionRule, filterConfig))) {
                        continue;
                    }
                    secretRequestParamsList.add(requestParam);
                }
            }
            if (partiallyMaskedParamMap != null && partiallyMaskedParamMap.size() > 0) {
                request.setAttribute("ZSEC_ACCESS_LOG_PARTIALLY_MASKED_PARAM_MAP", partiallyMaskedParamMap);
            }
            return secretRequestParamsList.isEmpty() ? null : secretRequestParamsList;
        }
        return null;
    }
    
    private static boolean isURLSecretParam(final String paramName, final ActionRule actionRule, final SecurityFilterProperties filterConfig) {
        if (!isOperationParamOrHipDigestParam(paramName, actionRule)) {
            if (actionRule.getSecretParameters().contains(paramName)) {
                return true;
            }
            if (!actionRule.getSecretParamNameRegexRules().isEmpty() && !SecurityRequestWrapper.getDefaultParameters().containsKey(paramName) && !actionRule.getParamRuleMap().containsKey(paramName)) {
                for (final ParameterRule paramRule : actionRule.getSecretParamNameRegexRules()) {
                    if (matchPattern(paramName, paramRule.getParamName(), filterConfig)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    static boolean isOperationParamOrHipDigestParam(final String paramName, final ActionRule actionRule) {
        return paramName.equals(actionRule.getOperationParam()) || (actionRule.skipHipDigestParamFromExtraParamValidation && paramName.length() == 128);
    }
    
    public static String getOldCSRFCookieName(final HttpServletRequest request) {
        return SecurityFilter.getCSRFCookieName(request) + "ocrmai";
    }
    
    static String getIAMAuthenticatedCookie(final HttpServletRequest request) {
        final String statelessCookie = getIAMStatelessCookie(request);
        if (isValid(statelessCookie)) {
            ((SecurityRequestWrapper)request).isAuthenticatedViaStatelessCookie = true;
            return statelessCookie;
        }
        return getIAMCookie(request);
    }
    
    public static String getIAMCookie(final HttpServletRequest request) {
        if (SecurityFilterProperties.getInstance(request).isAuthenticationProviderConfigured()) {
            return SecurityFilterProperties.getInstance(request).getAuthenticationProvider().GET_IAM_COOKIE(request);
        }
        return null;
    }
    
    static String getIAMStatelessCookie(final HttpServletRequest request) {
        if (SecurityFilterProperties.getInstance(request).isAuthenticationProviderConfigured()) {
            return SecurityFilterProperties.getInstance(request).getAuthenticationProvider().GET_IAM_STATELESS_COOKIE(request);
        }
        return null;
    }
    
    public static String encrypt(final HttpServletRequest request, final String password, final String salt, final String algorithm) {
        if (SecurityFilterProperties.getInstance(request).isAuthenticationProviderConfigured()) {
            return SecurityFilterProperties.getInstance(request).getAuthenticationProvider().encrypt(password, salt, algorithm);
        }
        return null;
    }
    
    public static String generateAuthCSRFToken(final HttpServletRequest request, final HttpServletResponse response) {
        final String ticket = getIAMAuthenticatedCookie(request);
        if (isValid(ticket) && SecurityFilterProperties.getInstance(request).isAuthenticationProviderConfigured()) {
            long userRegisteredTime = -1L;
            try {
                userRegisteredTime = SecurityFilterProperties.getInstance(request).getAuthenticationProvider().getUserRegisteredTime(ticket, request, response);
            }
            catch (final IAMSecurityException ex) {
                return null;
            }
            if (userRegisteredTime != -1L) {
                return getCSRFHashValue(request, ticket, userRegisteredTime);
            }
        }
        return null;
    }
    
    static void setUnauthCSRFIfAuthCSRFCookieFails(final HttpServletRequest request, final HttpServletResponse response) {
        final String ticket = getIAMAuthenticatedCookie(request);
        if (isValid(ticket) && SecurityFilterProperties.getInstance(request).isAuthenticationProviderConfigured()) {
            long userRegisteredTime = -1L;
            try {
                userRegisteredTime = SecurityFilterProperties.getInstance(request).getAuthenticationProvider().getUserRegisteredTime(ticket, request, response);
            }
            catch (final IAMSecurityException ex) {
                if ("INVALID_TICKET".equals(ex.getErrorCode())) {
                    SecurityUtil.LOGGER.log(Level.SEVERE, "Invalid user ticket - Ticket expired case so setting unauth csrf cookie");
                    setCSRFCookie(request, response, null);
                    return;
                }
                SecurityUtil.LOGGER.log(Level.SEVERE, "Error while fetching ticket user, Error : {0}", ex.getMessage());
            }
            if (userRegisteredTime != -1L) {
                final String authCSRFCookieValue = getCSRFHashValue(request, ticket, userRegisteredTime);
                setAuthCSRFCookieValue(request, response, authCSRFCookieValue);
            }
        }
    }
    
    public static void setAuthCSRFCookieValue(final HttpServletRequest request, final HttpServletResponse response) {
        setAuthCSRFCookieValue(request, response, generateAuthCSRFToken(request, response));
    }
    
    private static void setAuthCSRFCookieValue(final HttpServletRequest request, final HttpServletResponse response, final String csrfCookieValue) {
        if (csrfCookieValue != null) {
            setAuthCSRFTokenToCookie(request, response, csrfCookieValue);
            addAuthCSRFTokenToMap(csrfCookieValue, getIAMAuthenticatedCookie(request), request.getServerName());
        }
        else {
            SecurityUtil.LOGGER.log(Level.SEVERE, "INVALID TICKET");
        }
    }
    
    public static void setAuthCSRFTokenToCookie(final HttpServletRequest request, final HttpServletResponse response, final String csrfCookieValue) {
        if (isValid(csrfCookieValue)) {
            setCSRFCookie(request, response, csrfCookieValue);
        }
    }
    
    public static String getCSRFHashValue(final HttpServletRequest request, final String ticket, final long userRegisteredTime) {
        final String serviceDomain = request.getServerName();
        return encrypt(request, ticket + ":" + serviceDomain, String.valueOf(userRegisteredTime), "SHA512");
    }
    
    static boolean isValidCurrentCSRFCookie(final HttpServletRequest request, final HttpServletResponse response, final String paramValue, final String ticket) {
        if (SecurityFilterProperties.getInstance(request).isCSRFMigrationEnabled() && paramValue.length() != 128) {
            return true;
        }
        if (ticket == null && paramValue.length() != 128) {
            SecurityUtil.LOGGER.log(Level.WARNING, "CSRF Hacking attempt. PARAM LESS THAN 128. Param Value : {0}", MaskUtil.getCSRFMaskedValue(paramValue));
            return false;
        }
        final String ticketInMap = getTicketFromCSRFTokenMap(paramValue, request.getServerName());
        if (ticketInMap != null) {
            if (ticket.equals(ticketInMap)) {
                return true;
            }
            SecurityUtil.LOGGER.log(Level.WARNING, "CSRF Hacking attempt. MAP TICKET MISMATCH . Param Value : {0}", MaskUtil.getCSRFMaskedValue(paramValue));
            final String hash = generateAuthCSRFToken(request, response);
            resetAuthCSRFCookie(request, response, paramValue, hash, ticket);
            return false;
        }
        else {
            final String hash = generateAuthCSRFToken(request, response);
            if (paramValue.equals(hash)) {
                addAuthCSRFTokenToMap(hash, ticket, request.getServerName());
                return true;
            }
            resetAuthCSRFCookie(request, response, paramValue, hash, ticket);
            SecurityUtil.LOGGER.log(Level.WARNING, "CSRF Hacking attempt. HASH PARAM MISMATCH Hash : {0} , Param Value : {1}", new Object[] { MaskUtil.getCSRFMaskedValue(hash), MaskUtil.getCSRFMaskedValue(paramValue) });
            return false;
        }
    }
    
    private static void resetAuthCSRFCookie(final HttpServletRequest request, final HttpServletResponse response, final String paramValue, final String hash, final String ticket) {
        final String serverName = request.getServerName();
        final String ticketInMap = getTicketFromCSRFTokenMap(paramValue, serverName);
        if (ticketInMap != null) {
            removeAuthCSRFTokenFromMap(paramValue, serverName);
        }
        if (response != null) {
            if (hash != null) {
                setAuthCSRFTokenToCookie(request, response, hash);
                addAuthCSRFTokenToMap(hash, ticket, serverName);
            }
            else {
                SecurityUtil.LOGGER.log(Level.SEVERE, "INVALID_USER_TICKET - Expired ticket or User does not exist case");
            }
        }
    }
    
    static void addAuthCSRFTokenToMap(final String csrfToken, final String ticket, final String serverName) {
        SecurityUtil.CSRFTOKENTOTICKETMAP.put(serverName + "-" + csrfToken, ticket);
    }
    
    static void removeAuthCSRFTokenFromMap(final String csrfToken, final String serverName) {
        SecurityUtil.CSRFTOKENTOTICKETMAP.remove(serverName + "-" + csrfToken);
    }
    
    static String getTicketFromCSRFTokenMap(final String csrfParamValue, final String serverName) {
        return SecurityUtil.CSRFTOKENTOTICKETMAP.get(serverName + "-" + csrfParamValue);
    }
    
    public static boolean matchPattern(final String value, final String regexName, final SecurityRequestWrapper request) {
        return matchPattern(value, regexName, SecurityFilterProperties.getInstance((HttpServletRequest)request));
    }
    
    public static boolean matchPattern(final String value, final String regexName, final String webContext) {
        return matchPattern(value, regexName, SecurityFilterProperties.getInstance(webContext));
    }
    
    static boolean matchPattern(final String value, final String regexName, final SecurityFilterProperties filterConfig) {
        final RegexRule regexRule = filterConfig.getRegexRule(regexName);
        if (regexRule == null) {
            SecurityUtil.LOGGER.log(Level.SEVERE, "Pattern is not defined for the regex : {0} ", new Object[] { regexName });
            throw new IAMSecurityException("PATTERN_NOT_DEFINED");
        }
        return matchPattern(value, regexRule);
    }
    
    static boolean matchPattern(final String value, final RegexRule regexRule) {
        return matchPattern(value, regexRule.getPattern(), regexRule.getTimeOut(), regexRule.getIterationCount());
    }
    
    public static boolean matchPattern(final String value, final Pattern regexPattern) {
        return matchPattern(value, regexPattern, -1, -1);
    }
    
    public static boolean matchPattern(final String value, final String regexPattern) {
        return matchPattern(value, Pattern.compile(regexPattern));
    }
    
    public static boolean matchPattern(final String value, final Pattern regexPattern, final int timeoutInMillis, final int maxIterationCount) {
        return matchPattern(value, regexPattern, timeoutInMillis, maxIterationCount, false);
    }
    
    public static boolean matchPattern(final String value, final Pattern regexPattern, final int timeoutInMillis, final int maxIterationCount, final boolean isSensitive) {
        final PatternMatcherWrapper wrappedMatcher = new PatternMatcherWrapper(regexPattern, timeoutInMillis, maxIterationCount);
        wrappedMatcher.setMode(PatternMatcherWrapper.PatternMatcherMode.LOGGING);
        wrappedMatcher.sensitiveValue(isSensitive);
        return wrappedMatcher.matches(value);
    }
    
    @Deprecated
    public static DocumentBuilder getDocumentBuilderFromThreadLocal() {
        return getDocumentBuilder(false);
    }
    
    public static DocumentBuilder getDocumentBuilder() {
        return getDocumentBuilder(false);
    }
    
    public static DocumentBuilder getDocumentBuilder(final boolean allowInlineEntityExpansion) {
        return getDocumentBuilder(allowInlineEntityExpansion, false);
    }
    
    public static DocumentBuilder getDocumentBuilder(final boolean allowInlineEntityExpansion, final boolean allowExternalEntity) {
        Properties dbfFeatures = null;
        if (!allowExternalEntity) {
            dbfFeatures = new Properties();
            ((Hashtable<String, Boolean>)dbfFeatures).put("http://xml.org/sax/features/external-general-entities", false);
            ((Hashtable<String, Boolean>)dbfFeatures).put("http://xml.org/sax/features/external-parameter-entities", false);
        }
        return getDocumentBuilder(allowInlineEntityExpansion, allowExternalEntity, dbfFeatures);
    }
    
    public static DocumentBuilder getDocumentBuilder(final boolean allowInlineEntityExpansion, final boolean allowExternalEntity, final Properties dbfFeatures) {
        try {
            if (SecurityUtil.DOCUMENT_BUILDER.get() == null) {
                final DocumentBuilder builder = createDocumentBuilder(allowInlineEntityExpansion, allowExternalEntity, dbfFeatures);
                SecurityUtil.DOCUMENT_BUILDER.set(builder);
            }
        }
        catch (final Exception e) {
            SecurityUtil.LOGGER.log(Level.WARNING, null, e);
        }
        return SecurityUtil.DOCUMENT_BUILDER.get();
    }
    
    public static DocumentBuilder createDocumentBuilder(final boolean allowInlineEntityExpansion, final boolean allowExternalEntity, final Properties dbfFeatures) {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            int inlineEntityExpansionCount = 0;
            if (allowInlineEntityExpansion) {
                inlineEntityExpansionCount = 1000;
            }
            final SecurityManager manager = new SecurityManager();
            manager.setEntityExpansionLimit(inlineEntityExpansionCount);
            factory.setAttribute("http://apache.org/xml/properties/security-manager", manager);
            if (dbfFeatures != null) {
                for (final Object featureName : ((Hashtable<Object, V>)dbfFeatures).keySet()) {
                    final String name = featureName.toString();
                    final String val = ((Hashtable<K, Object>)dbfFeatures).get(name).toString();
                    final boolean value = Boolean.parseBoolean(val);
                    factory.setFeature(name, value);
                }
            }
            final DocumentBuilder builder = factory.newDocumentBuilder();
            if (!allowExternalEntity) {
                builder.setEntityResolver(new DummyEntityResolver());
            }
            return builder;
        }
        catch (final Exception e) {
            SecurityUtil.LOGGER.log(Level.WARNING, null, e);
            return null;
        }
    }
    
    public static XMLReader getSAXXMLReader() {
        return getSAXXMLReader(false, false);
    }
    
    public static XMLReader getSAXXMLReader(final boolean allowInlineEntityExpansion, final boolean allowExternalEntity) {
        Properties features = null;
        if (!allowExternalEntity) {
            features = new Properties();
            ((Hashtable<String, Boolean>)features).put("http://xml.org/sax/features/external-general-entities", false);
            ((Hashtable<String, Boolean>)features).put("http://xml.org/sax/features/external-parameter-entities", false);
        }
        return getSAXXMLReader(allowInlineEntityExpansion, allowExternalEntity, features);
    }
    
    public static XMLReader getSAXXMLReader(final boolean allowInlineEntityExpansion, final boolean allowExternalEntity, final Properties xmlFeatures) {
        try {
            if (SecurityUtil.XML_READER.get() == null) {
                final XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
                int inlineEntityExpansionCount = 0;
                if (allowInlineEntityExpansion) {
                    inlineEntityExpansionCount = 1000;
                }
                final SecurityManager manager = new SecurityManager();
                manager.setEntityExpansionLimit(inlineEntityExpansionCount);
                reader.setProperty("http://apache.org/xml/properties/security-manager", manager);
                if (xmlFeatures != null) {
                    for (final Object featureName : ((Hashtable<Object, V>)xmlFeatures).keySet()) {
                        final String name = featureName.toString();
                        final String val = ((Hashtable<K, Object>)xmlFeatures).get(name).toString();
                        final boolean value = Boolean.parseBoolean(val);
                        reader.setFeature(name, value);
                    }
                }
                if (!allowExternalEntity) {
                    reader.setEntityResolver(new DummyEntityResolver());
                }
                SecurityUtil.XML_READER.set(reader);
            }
        }
        catch (final Exception e) {
            SecurityUtil.LOGGER.log(Level.WARNING, null, e);
        }
        return SecurityUtil.XML_READER.get();
    }
    
    public static TransformerFactory getTransformerFactoryFromThreadLocal() {
        if (SecurityUtil.TRANSFORMER_FACTORY.get() == null) {
            SecurityUtil.TRANSFORMER_FACTORY.set(createTransformerFactory(false));
        }
        return SecurityUtil.TRANSFORMER_FACTORY.get();
    }
    
    public static TransformerFactory createTransformerFactory(final boolean allowExternalStyleSheet) {
        final TransformerFactory tFactory = TransformerFactory.newInstance();
        if (!allowExternalStyleSheet) {
            tFactory.setURIResolver(new URIResolver() {
                @Override
                public Source resolve(final String href, final String base) throws TransformerException {
                    return new StreamSource(new StringReader("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"/>"));
                }
            });
        }
        return tFactory;
    }
    
    public static Transformer getXMLTransformer(final InputSource xslInputSource) {
        return getXMLTransformer(false, false, false, xslInputSource);
    }
    
    public static Transformer getXMLTransformer(final boolean allowInlineEntityExpansion, final boolean allowExternalEntity, final boolean allowExternalStyleSheet, final InputSource xslInputSource) {
        try {
            final XMLReader xmlReader = getSAXXMLReader(allowInlineEntityExpansion, allowExternalEntity);
            return createTransformerFactory(allowExternalStyleSheet).newTransformer(new SAXSource(xmlReader, xslInputSource));
        }
        catch (final Exception ex) {
            SecurityUtil.LOGGER.log(Level.WARNING, null, ex);
            return null;
        }
    }
    
    public static SchemaFactory getXMLSchemaFactoryFromThreadLocal() {
        if (SecurityUtil.SCHEMAFACTORY.get() == null) {
            final SchemaFactory schemaFactory = createXMLSchemaFactory(false, false);
            SecurityUtil.SCHEMAFACTORY.set(schemaFactory);
        }
        return SecurityUtil.SCHEMAFACTORY.get();
    }
    
    public static SchemaFactory createXMLSchemaFactory(final boolean allowInlineEntityExpansion, final boolean allowExternalEntity) {
        try {
            final SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema", "org.apache.xerces.jaxp.validation.XMLSchemaFactory", Thread.currentThread().getContextClassLoader());
            disableEntityInSchemaFactory(schemaFactory, allowInlineEntityExpansion, allowExternalEntity);
            return schemaFactory;
        }
        catch (final Exception e) {
            SecurityUtil.LOGGER.log(Level.WARNING, null, e);
            return null;
        }
    }
    
    private static void disableEntityInSchemaFactory(final SchemaFactory schemaFactory, final boolean allowInlineEntityExpansion, final boolean allowExternalEntity) throws Exception {
        int inlineEntityExpansionCount = 0;
        if (allowInlineEntityExpansion) {
            inlineEntityExpansionCount = 1000;
        }
        final SecurityManager manager = new SecurityManager();
        manager.setEntityExpansionLimit(inlineEntityExpansionCount);
        schemaFactory.setProperty("http://apache.org/xml/properties/security-manager", manager);
        if (!allowExternalEntity) {
            schemaFactory.setResourceResolver(new DummyResourceResolver());
        }
    }
    
    static SchemaFactory createXMLSchemaFactory() {
        return createXMLSchemaFactory(false, true);
    }
    
    static SchemaFactory createXMLSchema11Factory(final boolean ctaFullXpathChecking) {
        try {
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(Constants.W3C_XML_SCHEMA11_NS_URI, "org.apache.xerces.jaxp.validation.XMLSchema11Factory", Thread.currentThread().getContextClassLoader());
            if (ctaFullXpathChecking) {
                schemaFactory.setFeature("http://apache.org/xml/features/validation/cta-full-xpath-checking", true);
            }
            disableEntityInSchemaFactory(schemaFactory, true, false);
            return schemaFactory;
        }
        catch (final Exception ex) {
            SecurityUtil.LOGGER.log(Level.WARNING, null, ex);
            return null;
        }
    }
    
    public static Document parseXMLContent(final String xmlContent) throws SAXException, IOException {
        Document document = null;
        final InputStream stream = new ByteArrayInputStream(xmlContent.getBytes("UTF-8"));
        document = getDocumentBuilder().parse(stream);
        return document;
    }
    
    public static void cleanUpThreadLocals() {
        cleanUpCommonThreadLocals();
        if (SecurityUtil.CURRENT_LOGREQUEST.get() != null) {
            SecurityUtil.CURRENT_LOGREQUEST.remove();
        }
    }
    
    private static void cleanUpCommonThreadLocals() {
        ZSecThreadLocalRegistry.resetThreadLocals();
        if (DoSController.URL_ACCESSINFO_LIST.get() != null) {
            DoSController.URL_ACCESSINFO_LIST.remove();
        }
        if (DoSController.DYNAMIC_THROTTLES_RULE_MAP.get() != null) {
            DoSController.DYNAMIC_THROTTLES_RULE_MAP.remove();
        }
        if (XSSUtil.currentXSSVars.get() != null) {
            XSSUtil.currentXSSVars.remove();
        }
    }
    
    public static boolean isBrowserCookiesDisabled() {
        final HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            final Cookie[] cookies = request.getCookies();
            if (cookies == null || cookies.length == 0) {
                SecurityUtil.LOGGER.log(Level.WARNING, "Browser cookies are disabled. Cookies : {0}", cookies);
                return true;
            }
        }
        else {
            SecurityUtil.LOGGER.log(Level.WARNING, "*** Request is not passed via Security Filter ***", request);
        }
        return false;
    }
    
    static File getTemporaryDir() {
        final File tempFile = new File(SecurityUtil.TEMPDIR);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        return tempFile;
    }
    
    static String getTempDirLocation() {
        final File tempFile = new File(SecurityUtil.TEMPDIR);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        return SecurityUtil.TEMPDIR;
    }
    
    static File getTempFileUploadDir() {
        final File tempFileUploadDir = new File(SecurityUtil.TEMPDIR, "waf_fileupload");
        if (!tempFileUploadDir.exists()) {
            tempFileUploadDir.mkdirs();
        }
        return tempFileUploadDir;
    }
    
    public static String getSecurityConfigurationDir() {
        if (SecurityUtil.confDirPath == null) {
            final String path = ".." + File.separator + "conf" + File.separator;
            final File confDir = new File(path);
            if (!confDir.isDirectory()) {
                updateConfDirPathFromClassLoaderPath();
            }
            else {
                try {
                    final File commonFile = new File(confDir, "security-common.xml");
                    if (commonFile.exists()) {
                        SecurityUtil.confDirPath = confDir.getCanonicalPath();
                    }
                    else {
                        updateConfDirPathFromClassLoaderPath();
                    }
                }
                catch (final IOException e) {
                    SecurityUtil.LOGGER.log(Level.WARNING, "IO Exception occurred while getting security configuration dir : {0} & Exception : {1}", new Object[] { confDir.getPath(), e.getMessage() });
                    throw new IAMSecurityException("Failure in getting conf directory");
                }
            }
        }
        return SecurityUtil.confDirPath;
    }
    
    private static void updateConfDirPathFromClassLoaderPath() {
        final String resourceName = SecurityUtil.class.getName().replaceAll("\\.", "/") + ".class";
        String classLoaderPath = Thread.currentThread().getContextClassLoader().getResource(resourceName).getPath();
        classLoaderPath = classLoaderPath.substring(5, classLoaderPath.indexOf("!"));
        String libPath = classLoaderPath.substring(0, classLoaderPath.lastIndexOf("/"));
        libPath = parseUtilDecoder(libPath, "UTF-8");
        final File libDir = new File(libPath);
        final String serverHomePath = libDir.getParent();
        if (serverHomePath.endsWith(File.separator)) {
            SecurityUtil.confDirPath = serverHomePath + "conf";
        }
        else {
            SecurityUtil.confDirPath = serverHomePath + File.separator + "conf";
        }
    }
    
    public static void validateRequest(final HttpServletRequest request) throws IOException {
        validateRequest(request, null);
    }
    
    public static void validateRequest(final HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!(request instanceof APIRequestWrapper)) {
            SecurityUtil.LOGGER.log(Level.WARNING, "The Request object should be APIRequestWrapper for Input Validation via API :{0}", request.getClass());
        }
        try {
            SecurityRequestWrapper secureRequest = SecurityRequestWrapper.getRequestWrapperInstance(request);
            secureRequest.isAPIRequestValidation = true;
            setCurrentRequest((HttpServletRequest)secureRequest);
            request.setAttribute(SecurityRequestWrapper.class.getName(), (Object)secureRequest);
            if (response == null) {
                response = (HttpServletResponse)new APIResponseWrapper();
                secureRequest.skipHeaderValidationAPIMode = true;
            }
            final SecurityResponseWrapper secureResponse = new SecurityResponseWrapper(response);
            final String value = request.getParameter("zoho-inputstream");
            if (request instanceof APIRequestWrapper) {
                final APIRequestWrapper apiRequest = (APIRequestWrapper)request;
                if (apiRequest.hasInputStream() && isFormURLEncodedRequest((HttpServletRequest)apiRequest)) {
                    throw new UnsupportedOperationException("\"application/x-www-form-urlencoded\" content type based validation is not supported in APIRequest validation");
                }
            }
            secureRequest = URLRule.validateURLRule(request, secureRequest, (HttpServletResponse)secureResponse, value);
            final ActionRule rule = secureRequest.getURLActionRule();
            if (rule != null) {
                if (isValidList(rule.getScopedServices())) {
                    rule.checkForSystemAuthentication((HttpServletRequest)secureRequest, "required");
                }
                else if (rule.isDCSystemAuthRequired()) {
                    verifyDCSignature(secureRequest);
                }
                secureResponse.setUserConfigControlledResponseHeaders();
                DoSController.controlDoS((HttpServletRequest)secureRequest, null, rule);
            }
        }
        finally {
            ZSecSinglePointLoggerImplProvider.pushEvents();
            cleanUpCommonThreadLocals();
        }
    }
    
    public static String getRequestURI(final HttpServletRequest request) {
        String uri = request.getRequestURI();
        final String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty()) {
            uri = uri.substring(contextPath.length());
        }
        return uri;
    }
    
    static String getRequestMethodForActionRuleLookup(final HttpServletRequest request) {
        final String overrideMethod = getHttpOverrideMethod(request);
        if (overrideMethod != null) {
            return overrideMethod;
        }
        final String requestMethod = request.getMethod().toUpperCase();
        String requestMethodInHeader = null;
        if ("OPTIONS".equals(requestMethod) && (requestMethodInHeader = request.getHeader("access-control-request-method")) != null) {
            request.setAttribute("CORS_REQUEST_TYPE", (Object)SecurityFilterProperties.CORS_REQUEST_TYPE.PREFLIGHT);
            return requestMethodInHeader.toUpperCase();
        }
        return requestMethod;
    }
    
    public static String getRequestMethod(final HttpServletRequest request) {
        final String overrideMethod = getHttpOverrideMethod(request);
        if (overrideMethod != null) {
            return overrideMethod;
        }
        return request.getMethod().toUpperCase();
    }
    
    static String getHttpOverrideMethod(final HttpServletRequest request) {
        final SecurityFilterProperties filterConfig = SecurityFilterProperties.getInstance(request);
        final String requestMethod = request.getMethod();
        String requestMethodInHeader = null;
        if (filterConfig.isEnableXHTTPMethodOverrideOption() && "post".equalsIgnoreCase(requestMethod) && (requestMethodInHeader = request.getHeader("x-http-method-override")) != null) {
            requestMethodInHeader = requestMethodInHeader.toUpperCase();
            if (SecurityUtil.HTTP_OVERRIDE_METHODS.contains(requestMethodInHeader)) {
                return requestMethodInHeader;
            }
        }
        return null;
    }
    
    static void addToLabelMap(final HashMap<String, String> labelMap, final String labelStr, final ParameterRule parameterRule) {
        final String[] labelKeyValuePair = labelStr.split(",");
        for (int i = 0; i < labelKeyValuePair.length; ++i) {
            final String[] errorMessage = labelKeyValuePair[i].split(":");
            if (errorMessage.length > 1) {
                if (parameterRule != null) {
                    errorMessage[1] = parameterRule.substituteAttributeInLabelMsg(errorMessage[1]);
                }
                labelMap.put(errorMessage[0].trim(), errorMessage[1]);
            }
            else {
                labelMap.put("ZSEC_DEFAULT_LABEL", errorMessage[0]);
            }
        }
    }
    
    public static String getMimeType(final SecurityRequestWrapper request, final File file, final String fileName) throws IOException {
        String contentType = null;
        final SecurityFilterProperties securityFilterProperties = SecurityFilterProperties.getInstance((HttpServletRequest)request);
        final String contentTypeDetection = securityFilterProperties.getContentTypeDetectOption();
        final String fileNameUsedForMimeTypeDetection = securityFilterProperties.isTikaFileContentAndNameBasedDetectionEnabled() ? fileName : null;
        if ("tika".equalsIgnoreCase(contentTypeDetection)) {
            contentType = getMimeTypeUsingTika(file, fileNameUsedForMimeTypeDetection);
        }
        else {
            registerMimeDetector();
            final Collection collection = MimeUtil.getMimeTypes(file);
            final Iterator iter = collection.iterator();
            if (iter.hasNext()) {
                contentType = iter.next().toString();
            }
            if ("mimeutilwithtika".equalsIgnoreCase(contentTypeDetection) && "application/x-unknown-mime-type".equals(contentType)) {
                contentType = getMimeTypeUsingTika(file, fileNameUsedForMimeTypeDetection);
            }
        }
        return contentType;
    }
    
    private static synchronized void registerMimeDetector() {
        if (!SecurityUtil.isMimeDetectorRegistered) {
            MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
            SecurityUtil.isMimeDetectorRegistered = true;
        }
    }
    
    public static String getMimeTypeUsingTika(final File file, final String fileName) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            return getMimeTypeUsingTika(inputStream, fileName);
        }
        finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
    
    public static String getMimeTypeUsingTika(final InputStream bufIS, final String fileName) throws IOException {
        return TikaUtil.getTikaInstance().detect(bufIS, fileName);
    }
    
    public static void validateFileViaApi(final UploadFileRule uploadFileRule, final File fileViaApi, final String fileName, final String contextPath) throws IOException {
        validateFileViaApiAndReturnResult(uploadFileRule, fileViaApi, fileName, contextPath);
    }
    
    public static List<AVScanResult<File>> validateFileViaApiAndReturnResult(final UploadFileRule uploadFileRule, final File fileViaApi, final String fileName, final String contextPath) throws IOException {
        final List<AVScanResult<File>> result = new ArrayList<AVScanResult<File>>();
        validateFile(uploadFileRule, fileViaApi, fileName, contextPath, result);
        return result;
    }
    
    private static void validateFile(final UploadFileRule uploadFileRule, final File fileViaApi, final String fileName, final String contextPath, final List<AVScanResult<File>> result) throws IOException {
        if (uploadFileRule == null || fileViaApi == null || !isValid(contextPath)) {
            throw new IAMSecurityException("INVALID_ARGUMENT(S)_PASSED");
        }
        if (!fileViaApi.exists()) {
            SecurityUtil.LOGGER.log(Level.SEVERE, "File \"{0}\" Not found at \"{1}\" ", new Object[] { fileViaApi.getName(), fileViaApi.getAbsolutePath() });
            throw new IAMSecurityException("FILE_NOT_FOUND");
        }
        final long fileSize = fileViaApi.length();
        if (uploadFileRule.getMaxSizeInKB() > -1L && fileSize / 1024L > uploadFileRule.getMaxSizeInKB()) {
            SecurityUtil.LOGGER.log(Level.SEVERE, "File Size {0} Kb is more that the allowed size limit of {1} Kb", new Object[] { fileSize / 1024L, uploadFileRule.getMaxSizeInKB() });
            throw new IAMSecurityException("FILE_SIZE_MORE_THAN_ALLOWED_SIZE");
        }
        uploadFileRule.validateFile(SecurityFilterProperties.FILTER_INSTANCES.get(contextPath), "API_CALL", null, fileViaApi, getMimeTypeUsingTika(fileViaApi, fileName), fileSize, fileViaApi.getName(), result);
    }
    
    public static void initSecurityConfiguration(final String[] securityFiles, final String contextName) throws Exception {
        final SecurityFilterProperties filterConfig = new SecurityFilterProperties();
        for (final String file : securityFiles) {
            try {
                final File securityXMLFile = new File(file);
                if ("security-development.xml".equals(securityXMLFile.getName())) {
                    throw new RuntimeException("The file 'security-development.xml' shouldn't exist in '/WEB-INF' directory. It will be loaded automatically from the 'conf' directory when the 'development.mode' is true. ");
                }
                if ("zsec-events.xml".equals(securityXMLFile.getName())) {
                    if (securityXMLFile.exists()) {
                        try {
                            SecurityUtil.LOGGER.log(Level.INFO, "EventFramework -  custom configuration file \"{0}\" loaded from conf directory :: {1}", new Object[] { "zsec-events.xml", securityXMLFile.getPath() });
                            EventDataProcessor.init(securityXMLFile);
                        }
                        catch (final Exception e) {
                            throw new RuntimeException("Exception occured while loading eventFramework custom configuration " + e.getMessage());
                        }
                    }
                }
                else {
                    RuleSetParser.initSecurityRules(filterConfig, securityXMLFile);
                }
            }
            catch (final Exception e2) {
                SecurityUtil.LOGGER.log(Level.SEVERE, "Invalid Security configuation - : {0} ", e2);
                throw new IAMSecurityException("INVALID_CONFIGURATION");
            }
        }
        filterConfig.loadAuthProviderClass();
        filterConfig.initAccountsandInlineSecurityRules();
        filterConfig.initPiiDetector();
        filterConfig.getTempFileUploadDirMonitoring().init();
        if (filterConfig.isAuthenticationProviderConfigured()) {
            final Authenticator authImpl = filterConfig.getAuthenticationProvider();
            if (authImpl != null) {
                authImpl.init(filterConfig);
            }
        }
        EventDataProcessor.initDefaultConfig();
        WAFAttackDiscoveryUtil.initEventXML();
        SecurityFilterProperties.FILTER_INSTANCES.put(contextName, filterConfig);
    }
    
    public static void initSecurityConfiguration(final String[] securityFiles) throws Exception {
        initSecurityConfiguration(securityFiles, "ZSEC_API_DEFAULT");
    }
    
    static String getPrefixIgnoredURI(String uri, final List<String> ignoreURIPrefixList) {
        if (ignoreURIPrefixList != null) {
            for (final String ignoreURIPrefix : ignoreURIPrefixList) {
                if (uri.startsWith(ignoreURIPrefix)) {
                    uri = uri.substring(ignoreURIPrefix.length(), uri.length());
                    break;
                }
            }
            if (!uri.startsWith("/")) {
                uri = "/" + uri;
            }
        }
        return uri;
    }
    
    static String getRequestURIForURLRuleLookup(final HttpServletRequest request, final SecurityFilterProperties filterProps) {
        String uri = getRequestPath(request);
        handleNullURI(uri, request);
        uri = ignoreURIPrefixAndTrailingSlash(uri, filterProps);
        return uri;
    }
    
    static String ignoreURIPrefixAndTrailingSlash(String uri, final SecurityFilterProperties filterProps) {
        if (filterProps.ignoreTrailingSlash()) {
            uri = ((!"/".equals(uri) && uri.endsWith("/")) ? uri.substring(0, uri.length() - 1) : uri);
        }
        uri = getPrefixIgnoredURI(uri, filterProps.getURIPrefixToRemove());
        return uri;
    }
    
    public static String encode(final String input) {
        return encode(input, "UTF-8");
    }
    
    public static String getValidValue(final String value, final String defaultValue) {
        return isValid(value) ? value : defaultValue;
    }
    
    public static String encode(final String input, final String characterEncoding) {
        try {
            return (input == null) ? "null" : URLEncoder.encode(input, characterEncoding);
        }
        catch (final Exception e) {
            SecurityUtil.LOGGER.log(Level.WARNING, "Error in character encoding : {0}", e.getMessage());
            return null;
        }
    }
    
    static String decode(final String input, final String characterEncoding) {
        try {
            return (input == null) ? "null" : URLDecoder.decode(input, characterEncoding);
        }
        catch (final Exception e) {
            SecurityUtil.LOGGER.log(Level.WARNING, "Error in character decoding : {0}", e.getMessage());
            return null;
        }
    }
    
    static boolean verifyDCSignature(final SecurityRequestWrapper securedRequest) {
        final String dcSignature = getSignatureValueFromHeader((HttpServletRequest)securedRequest);
        return verifyISCSignature(dcSignature, null, securedRequest, SystemAuthType.INTERDC);
    }
    
    static String getSignatureValueFromHeader(final HttpServletRequest request) {
        String authorizationValue = request.getHeader(RequestConstants.HEADER_NAME.SYSTEM_AUTHORIZATION.getValue());
        if (authorizationValue == null) {
            authorizationValue = request.getHeader(RequestConstants.HEADER_NAME.AUTHORIZATION.getValue());
        }
        if (isValid(authorizationValue)) {
            final AuthHeader authHeader = new AuthHeader(authorizationValue);
            if ("SystemAuth".equals(authHeader.getScheme())) {
                return authHeader.getCredential();
            }
        }
        return null;
    }
    
    public static boolean verifyISCSignature(final String iscsignature, final List<String> scopedServices, final SecurityRequestWrapper securedRequest) {
        return verifyISCSignature(iscsignature, scopedServices, securedRequest, SystemAuthType.ISC);
    }
    
    private static boolean verifyISCSignature(final String iscsignature, final List<String> scopedServices, final SecurityRequestWrapper securedRequest, final SystemAuthType systemAuthType) {
        boolean signatureVerified = false;
        isValidSignature(iscsignature, systemAuthType, securedRequest);
        final String[] sigparams = iscsignature.split("-");
        if (sigparams.length == 3) {
            final String reqServiceName = sigparams[0].trim();
            final String reqTimeStr = sigparams[1].trim();
            final String sign = sigparams[2].trim();
            signatureVerified = verify(iscsignature, reqServiceName, "", reqTimeStr, sign, scopedServices, securedRequest, systemAuthType);
        }
        if (signatureVerified) {
            return true;
        }
        SecurityUtil.LOGGER.log(Level.SEVERE, "Invalid {0} signature : \"{1}\" passed through the URL : \"{2}\" from IP : \"{3}\"", new Object[] { systemAuthType.value, iscsignature, securedRequest.getRequestURI(), securedRequest.getRemoteAddr() });
        throw new IAMSecurityException("ISC_INVALID_SIGNATURE", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
    }
    
    private static boolean verify(final String iscsignature, String reqServiceName, final String signedData, final String reqTimeStr, final String encodedSignature, final List<String> scopedServices, final SecurityRequestWrapper securedRequest, final SystemAuthType systemAuthType) {
        final boolean isc = SystemAuthType.ISC == systemAuthType;
        final SecurityFilterProperties sfProperties = SecurityFilterProperties.getInstance((HttpServletRequest)securedRequest);
        String dcLocation = null;
        if (!isc) {
            final String[] dcSignParams = reqServiceName.split("\\.");
            if (dcSignParams.length != 2) {
                SecurityUtil.LOGGER.log(Level.SEVERE, "Invalid InterDC signature : \"{0}\" passed through the URL : \"{1}\" from IP : \"{2}\"", new Object[] { iscsignature, securedRequest.getRequestURI(), securedRequest.getRemoteAddr() });
                throw new IAMSecurityException("ISC_INVALID_SIGNATURE", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
            }
            dcLocation = dcSignParams[0].trim();
            reqServiceName = dcSignParams[1].trim();
        }
        final String currentServiceName = SecurityFilterProperties.getServiceName();
        final boolean isAllowedService = isc ? (isValid(reqServiceName) && (scopedServices.contains(reqServiceName) || scopedServices.contains("all"))) : reqServiceName.equals(currentServiceName);
        if (!isAllowedService) {
            if (isc) {
                SecurityUtil.LOGGER.log(Level.SEVERE, "\"{0}\" service not allowed to communicate with this service \"{1}\" for ISC communication", new Object[] { reqServiceName, currentServiceName });
            }
            else {
                SecurityUtil.LOGGER.log(Level.SEVERE, "InterDC communication is allowed only within the same service, so cannot allow this \"{0}\" service", new Object[] { reqServiceName });
            }
            throw new IAMSecurityException("ISC_UNSCOPED_SERVICE", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
        }
        if (isValid(reqTimeStr)) {
            long signGeneratedTime = -1L;
            try {
                signGeneratedTime = Long.parseLong(reqTimeStr);
            }
            catch (final Exception ex) {
                SecurityUtil.LOGGER.log(Level.SEVERE, "Unable to parse the generated time : \"{0}\" in the {1} signature . Exception Message : {2}", new Object[] { reqTimeStr, systemAuthType.value, ex.getMessage() });
                throw new IAMSecurityException("ISC_INVALID_SIGNATURE", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
            }
            final long diffTime = Math.abs(System.currentTimeMillis() - signGeneratedTime);
            final long signatureExpiryTime = isc ? sfProperties.getISCSignatureExpiryTime() : 600000L;
            if (diffTime > signatureExpiryTime) {
                if (isc) {
                    SecurityUtil.LOGGER.log(Level.SEVERE, "ISC signature for this service \"{0}\" is expired.Please check if system time of consumer service \"{1}\" is in sync with this service \"{2}\". Generated Time : \"{3}\" Expiry Time : \"{4}\" Diff Time : \"{5}\"", new Object[] { reqServiceName, reqServiceName, currentServiceName, signGeneratedTime, signatureExpiryTime, diffTime });
                }
                else {
                    SecurityUtil.LOGGER.log(Level.SEVERE, "InterDC signature for this service \"{0}\" is expired.Please check the system time of consumer DC \"{1}\" with this DC. Generated Time : \"{2}\" Expiry Time : \"{3}\" Diff Time : \"{4}\"", new Object[] { reqServiceName, dcLocation, signGeneratedTime, signatureExpiryTime, diffTime });
                }
                throw new IAMSecurityException("ISC_SIGNATURE_EXPIRED", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
            }
            if (!sfProperties.isAuthenticationProviderConfigured()) {
                SecurityUtil.LOGGER.log(Level.SEVERE, "Authentication provider impl can not be null for {0} communication. Service Name : \"{1}\" service URL : \"{2}\" and IP : \"{3}\"", new Object[] { systemAuthType.value, reqServiceName, securedRequest.getRequestURI(), securedRequest.getRemoteAddr() });
                throw new IAMSecurityException("ISC_SIGNATURE_NOT_SUPPORTED");
            }
            final Authenticator authProviderImpl = sfProperties.getAuthenticationProvider();
            final String pubKeyStr = isc ? authProviderImpl.getServicePublicKey(reqServiceName) : authProviderImpl.getServicePublicKey(reqServiceName, dcLocation);
            if (pubKeyStr == null) {
                if (isc) {
                    SecurityUtil.LOGGER.log(Level.SEVERE, "Public key not configured for this \"{0}\" service for ISC communication, service URL : \"{1}\" and IP : \"{2}\"", new Object[] { reqServiceName, securedRequest.getRequestURI(), securedRequest.getRemoteAddr() });
                }
                else {
                    SecurityUtil.LOGGER.log(Level.SEVERE, "Public key not configured for this \"{0}\" service DC \"{1}\" for INTERDC communication", new Object[] { reqServiceName, dcLocation });
                }
                throw new IAMSecurityException("ISC_PUBLIC_KEY_NOT_CONFIGURED");
            }
            try {
                final byte[] pubKeyBytes = (byte[])SecurityUtil.hexCodec.decode((Object)pubKeyStr);
                final X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(pubKeyBytes);
                final PublicKey publicKey = getKeyFactory().generatePublic(pubKeySpec);
                final Signature sig = Signature.getInstance("MD5withRSA");
                sig.initVerify(publicKey);
                final String signedStr = isValid(signedData) ? (signedData + reqTimeStr) : reqTimeStr;
                sig.update(signedStr.getBytes());
                final byte[] sigToVerify = BASE16_DECODE(encodedSignature);
                if (sig.verify(sigToVerify)) {
                    securedRequest.setIntegrationRequest(true);
                    return true;
                }
            }
            catch (final Exception ex2) {
                SecurityUtil.LOGGER.log(Level.SEVERE, "{0} signature ( {1} ) verification failed due to : \"{2}\" for this \"{3}\" service URL : \"{4}\" and IP : \"{5}\"", new Object[] { systemAuthType.value, iscsignature, ex2.getMessage(), reqServiceName, securedRequest.getRequestURI(), securedRequest.getRemoteAddr() });
                throw new IAMSecurityException("ISC_SIGNATURE_VERIFICATION_FAILED", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
            }
        }
        return false;
    }
    
    public static Parser getUserAgentParser() {
        return UserAgent.userAgentParser;
    }
    
    public static boolean isValidList(final List<?> list) {
        return list != null && list.size() > 0;
    }
    
    public static boolean isValidMap(final Map<?, ?> map) {
        return map != null && map.size() > 0;
    }
    
    public static String getRequestPath(final HttpServletRequest request) {
        if (request.getAttribute("ZSEC_REQUEST_PATH") != null) {
            return request.getAttribute("ZSEC_REQUEST_PATH").toString();
        }
        final String uri = getNormalizedURI(getRequestURI(request));
        request.setAttribute("ZSEC_REQUEST_PATH", (Object)uri);
        return uri;
    }
    
    public static String getNormalizedURI(final String path) {
        if (path == null) {
            return null;
        }
        String normalized = path;
        if (normalized.indexOf(92) >= 0) {
            normalized = normalized.replace('\\', '/');
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        boolean addedTrailingSlash = false;
        if (normalized.endsWith("/.") || normalized.endsWith("/..")) {
            normalized += "/";
            addedTrailingSlash = true;
        }
        while (true) {
            final int index = normalized.indexOf("/./");
            if (index < 0) {
                break;
            }
            normalized = normalized.substring(0, index) + normalized.substring(index + 2);
        }
        while (true) {
            final int index = normalized.indexOf("/../");
            if (index < 0) {
                if (normalized.length() > 1 && addedTrailingSlash) {
                    normalized = normalized.substring(0, normalized.length() - 1);
                }
                return normalized;
            }
            if (index == 0) {
                return null;
            }
            final int index2 = normalized.lastIndexOf(47, index - 1);
            normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
        }
    }
    
    static String getStandardRequestPathForAnalysis(final HttpServletRequest request) {
        final String servletPath = request.getServletPath();
        String uri = (servletPath != null) ? servletPath : "";
        final String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            uri += pathInfo;
        }
        return uri;
    }
    
    public static void handleNullURI(final String uri, final HttpServletRequest request) {
        if (uri == null) {
            SecurityUtil.LOGGER.log(Level.SEVERE, "ZSEC_URI_NULL");
            throw new IAMSecurityException("URL_RULE_NOT_CONFIGURED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
        }
    }
    
    public static boolean isMultipartRequest(final HttpServletRequest httpServletRequest) {
        final String method = httpServletRequest.getMethod().toUpperCase();
        if (!SecurityUtil.HTTP_MULTIPART_METHODS.contains(method)) {
            return false;
        }
        final String contentType = httpServletRequest.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith("multipart/");
    }
    
    public static String getContextName(final String contextPath) {
        if (isValid(contextPath) && contextPath.contains(File.separator)) {
            final String[] contextPathArray = contextPath.split("\\" + File.separator);
            return contextPathArray[contextPathArray.length - 1];
        }
        return null;
    }
    
    static String parseUtilDecoder(final String inputString, final String characterEncoding) {
        int flag = 0;
        final int length = inputString.length();
        final StringBuffer localStringBuffer = new StringBuffer((length > 20) ? (length / 2) : length);
        int charIndex = 0;
        if (characterEncoding.length() == 0) {
            SecurityUtil.LOGGER.log(Level.SEVERE, "Empty character encoding passed as argument");
            throw new IAMSecurityException("INVALID_CHARACTER_ENCODING");
        }
        byte[] byteArray = null;
        while (charIndex < length) {
            char inputChar = inputString.charAt(charIndex);
            switch (inputChar) {
                case '%': {
                    if (byteArray == null) {
                        byteArray = new byte[(length - charIndex) / 3];
                    }
                    int byteArrayIndex = 0;
                    while (charIndex + 2 < length && inputChar == '%') {
                        final int decimalValue = Integer.parseInt(inputString.substring(charIndex + 1, charIndex + 3), 16);
                        if (decimalValue < 0) {
                            SecurityUtil.LOGGER.log(Level.SEVERE, "Illegal hex characters in escape (%) pattern - negative value");
                            throw new IAMSecurityException("INVALID_DECIMAL_VALUE");
                        }
                        byteArray[byteArrayIndex++] = (byte)decimalValue;
                        charIndex += 3;
                        if (charIndex >= length) {
                            continue;
                        }
                        inputChar = inputString.charAt(charIndex);
                    }
                    if (charIndex < length && inputChar == '%') {
                        SecurityUtil.LOGGER.log(Level.SEVERE, "Incomplete trailing escape (%) pattern");
                        throw new IAMSecurityException("INCOMPLETE_ESCAPE_PATTERN");
                    }
                    try {
                        localStringBuffer.append(new String(byteArray, 0, byteArrayIndex, characterEncoding));
                    }
                    catch (final UnsupportedEncodingException e) {
                        SecurityUtil.LOGGER.log(Level.SEVERE, "Unable to parse byte to string due to unsupported character encoding");
                        throw new IAMSecurityException("INVALID_CHARACTER_ENCODING");
                    }
                    flag = 1;
                    continue;
                }
                default: {
                    localStringBuffer.append(inputChar);
                    ++charIndex;
                    continue;
                }
            }
        }
        return (flag != 0) ? localStringBuffer.toString() : inputString;
    }
    
    public static Date getStartOfDay(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }
    
    public static Date getEndOfDay(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        calendar.set(14, 999);
        return calendar.getTime();
    }
    
    public static long getTimeInMilliSecond(final String timeStr) {
        if (!isValid(timeStr)) {
            return -1L;
        }
        long timeInMilliSeconds = 0L;
        final String[] times = timeStr.split(":");
        for (int i = 0; i < times.length; ++i) {
            final long time = getTimeInMilliSec(times[i]);
            if (time == -1L) {
                return -1L;
            }
            timeInMilliSeconds += time;
        }
        return timeInMilliSeconds;
    }
    
    private static long getTimeInMilliSec(final String timeStr) {
        TIME_UNIT unit = TIME_UNIT.M;
        int val = 0;
        try {
            final Matcher matcher = SecurityUtil.SINGLE_TIME_PATTERN.matcher(timeStr);
            if (matcher.matches()) {
                val = Integer.parseInt(matcher.group(1));
                final String timeUnit = matcher.group(2);
                if (timeUnit != null) {
                    unit = TIME_UNIT.valueOf(timeUnit.toUpperCase());
                }
                final TimeUnit tu = TimeUnit.MILLISECONDS;
                switch (unit) {
                    case MS: {
                        return val;
                    }
                    case S: {
                        return tu.convert(val, TimeUnit.SECONDS);
                    }
                    case M: {
                        return tu.convert(val, TimeUnit.MINUTES);
                    }
                    case H: {
                        return tu.convert(val, TimeUnit.HOURS);
                    }
                    case D: {
                        return tu.convert(val, TimeUnit.DAYS);
                    }
                }
            }
            return -1L;
        }
        catch (final IllegalArgumentException e) {
            return -1L;
        }
    }
    
    public static boolean isSpamUrl(final String url) throws Exception {
        try {
            final UrlValidationResult validationResult = SafeUrlValidator.getUrlValidationResult(url);
            return !validationResult.isSafeUrl();
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    public static boolean isSpamUrl(final String scheme, final String domainAuthority, final String pathInfo) throws Exception {
        try {
            final UrlValidationResult validationResult = SafeUrlValidator.getUrlValidationResult(scheme, domainAuthority, pathInfo);
            return !validationResult.isSafeUrl();
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    static Matcher getTimeLimitedPatternMatcher(final Pattern pattern, final String value) {
        final PatternMatcherWrapper wrappedMatcher = new PatternMatcherWrapper(pattern);
        wrappedMatcher.setMode(PatternMatcherWrapper.PatternMatcherMode.LOGGING);
        return wrappedMatcher.getMatcher(value);
    }
    
    static boolean isFormURLEncodedRequest(final HttpServletRequest request, final SecurityFilterProperties securityFilterConfig, final ActionRule actionRule) {
        if (!isFormURLEncodedRequest(request)) {
            return false;
        }
        ZSEC_GETTING_URLENCODED_PARAMS_AS_INPUTSTREAM.pushInfo(request.getRequestURI(), actionRule.getPrefix(), actionRule.getPath(), actionRule.getMethod(), actionRule.getOperationValue(), (ExecutionTimer)null);
        if (securityFilterConfig.isInputStreamValidationErrorMode()) {
            SecurityUtil.LOGGER.log(Level.SEVERE, "Invalid Inputstream configuration :: Request payload passed with content-type ''application/x-www-form-urlencoded'' cannot be consumed as inputstream, use <param> configuration instead. Request URI :{0}, ActionRule UniquePath : {1}", new Object[] { request.getRequestURI(), actionRule.getUniquePath() });
            throw new IAMSecurityException("INVALID_CONFIGURATION", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"));
        }
        return true;
    }
    
    static boolean isFormURLEncodedRequest(final HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        final int semicolon = contentType.indexOf(59);
        contentType = ((semicolon >= 0) ? contentType.substring(0, semicolon).trim() : contentType.trim());
        return "application/x-www-form-urlencoded".equals(contentType);
    }
    
    public static String getResponseAsString(final InputStream inputStream) {
        final StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String str = null;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        }
        catch (final IOException ex) {
            SecurityUtil.LOGGER.log(Level.WARNING, "Error occurred while reading the inputstream : {0}", ex.getMessage());
            return null;
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (final IOException ex2) {
                    SecurityUtil.LOGGER.log(Level.WARNING, "Unable to close the inputstream : {0}", ex2.getMessage());
                }
            }
        }
    }
    
    static String getRequestDomain() {
        final HttpServletRequest request = getCurrentRequest();
        String serverName = request.getServerName();
        final int port = request.getServerPort();
        if (port != -1) {
            serverName = serverName + ":" + port;
        }
        return serverName;
    }
    
    static String getXframeTrustedService(final String domain) {
        return (SecurityUtil.xframeTrustedDomainServiceMapping != null) ? SecurityUtil.xframeTrustedDomainServiceMapping.get(domain) : null;
    }
    
    static void addXframeTrustedServiceForDomain(final String domainName, final String serviceName) {
        if (SecurityUtil.xframeTrustedDomainServiceMapping == null) {
            SecurityUtil.xframeTrustedDomainServiceMapping = new HashMap<String, String>();
        }
        SecurityUtil.xframeTrustedDomainServiceMapping.put(domainName, serviceName);
    }
    
    static void addValueToList(final String commaSepValues, final List<String> list) {
        if (commaSepValues != null) {
            final String[] split;
            final String[] values = split = commaSepValues.split(",");
            for (String value : split) {
                value = value.trim().toLowerCase();
                if (!list.contains(value)) {
                    list.add(value);
                }
            }
        }
    }
    
    static String getValuefromList(final List<String> secretRequestParamNames) {
        if (secretRequestParamNames != null && secretRequestParamNames.size() > 0) {
            final StringBuilder sb = new StringBuilder();
            for (final String param : secretRequestParamNames) {
                sb.append(param).append(",");
            }
            return sb.substring(0, sb.length() - 1);
        }
        return null;
    }
    
    static File createTempFile(final TempFileName fileNameContext) {
        final String tempFileName = String.format("upload_%s_%s.tmp", fileNameContext.getTempFileName(), CommonUtil.getSecureRandomNumber());
        return new File(getTempFileUploadDir(), tempFileName);
    }
    
    static void checkCSRFSamesiteStrictTmpCookie(final SecurityRequestWrapper request, final HttpServletResponse response) {
        final SecurityFilterProperties filterProps = SecurityFilterProperties.getInstance((HttpServletRequest)request);
        if (!filterProps.isCSRFSamesiteStrictModeEnabledByDefault() && filterProps.isEnabledCSRFSamesiteStrictTmpCookie() && getCookie((HttpServletRequest)request, "_zcsr_tmp") == null) {
            String referer = request.getHeader("Referer");
            if (referer != null) {
                final int qsIndex = referer.indexOf("?");
                referer = ((qsIndex != -1) ? referer.substring(0, qsIndex) : referer);
            }
            ZSEC_EXCEPTION_IN_ENABLING_SAMESITE_FOR_CSRF_COOKIE.pushInfo(request.getRequestURI(), request.getURLActionRulePrefix(), request.getURLActionRulePath(), request.getURLActionRuleMethod(), request.getURLActionRuleOperation(), request.getActualRequestMethod(), referer, request.getHeader("User-Agent"), (ExecutionTimer)null);
        }
    }
    
    static boolean isCaptchaParam(final String paramName, final ActionRule actionRule) {
        return actionRule.isCaptchaVerificationEnabled() && ("captcha".equals(paramName) || "captcha-digest".equals(paramName));
    }
    
    public static String getServiceNameFromISCSignature(final HttpServletRequest request) {
        final ActionRule actionRule = ((SecurityRequestWrapper)request).getURLActionRule();
        if (actionRule != null && isValidList(actionRule.getScopedServices())) {
            final String iscSignature = getISCSignature(request);
            if (iscSignature != null) {
                final String[] signParams = iscSignature.split("-");
                if (signParams.length == 3) {
                    return signParams[0].trim();
                }
            }
        }
        return null;
    }
    
    static String getISCSignature(final HttpServletRequest request) {
        String iscsignature = getSignatureValueFromHeader(request);
        if (iscsignature == null) {
            iscsignature = request.getParameter(RequestConstants.PARAM_NAME.ISC_SIGNATURE_PARAM_NAME.getValue());
        }
        return iscsignature;
    }
    
    static ParameterRule getInputStreamRule(final HttpServletRequest request, final ActionRule actionRule) {
        ParameterRule inputStreamRule = null;
        if (actionRule != null) {
            inputStreamRule = actionRule.getInputStreamRule();
            inputStreamRule = ((inputStreamRule == null) ? actionRule.getParamOrStreamRule() : inputStreamRule);
        }
        if (inputStreamRule != null) {
            return inputStreamRule;
        }
        return SecurityFilterProperties.getInstance(request).getSecurityProvider().getDynamicInputStreamRule(request, actionRule);
    }
    
    public static String getDigestString(final MessageDigest md) {
        final StringBuilder result = new StringBuilder();
        for (final byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    static byte[] convertInputStreamAsByteArray(final InputStream inputStream, final long maxSizeInBytes) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        long total = 0L;
        final byte[] buf = new byte[8194];
        try {
            int res;
            while ((res = inputStream.read(buf)) != -1) {
                total += res;
                if (maxSizeInBytes > -1L && maxSizeInBytes < total) {
                    throw new IAMSecurityException("FILE_SIZE_MORE_THAN_ALLOWED_SIZE");
                }
                output.write(buf, 0, res);
            }
        }
        catch (final IOException io) {
            SecurityUtil.LOGGER.log(Level.WARNING, "Exception occured while reading input stream, reason : {0}", io.getMessage());
            throw new IAMSecurityException("UNABLE_TO_READ_INPUTSTREAM");
        }
        return output.toByteArray();
    }
    
    public static String generateSignature(final String data) throws Exception {
        if (!isValid(data)) {
            SecurityUtil.LOGGER.log(Level.SEVERE, "INVALID ARGUMENTS PASSED : Null/Empty value is passed while generating signature");
            throw new IAMSecurityException("INVALID_ARGUMENT(S)_PASSED");
        }
        return sign(data, SystemAuthType.ISC);
    }
    
    public static boolean verifySignature(final String signature, final List<String> scopedServices, final SecurityRequestWrapper securedRequest) {
        return verifySignatureAndGetData(signature, scopedServices, securedRequest) != null;
    }
    
    public static String verifySignatureAndGetData(final String signature, final SecurityRequestWrapper securedRequest) {
        return verifySignatureAndGetData(signature, Arrays.asList("all"), securedRequest);
    }
    
    public static String verifySignatureAndGetData(final String signature, final List<String> scopedServices, final SecurityRequestWrapper securedRequest) {
        try {
            isValidSignature(signature, SystemAuthType.ISC, securedRequest);
            boolean signatureVerified = false;
            String signedData = null;
            final String[] sigparams = signature.split("-");
            if (sigparams.length == 4) {
                final String reqServiceName = sigparams[0].trim();
                signedData = sigparams[1].trim();
                final String reqTimeStr = sigparams[2].trim();
                final String encodedSignature = sigparams[3].trim();
                signatureVerified = verify(signature, reqServiceName, signedData, reqTimeStr, encodedSignature, scopedServices, securedRequest, SystemAuthType.ISC);
            }
            if (signatureVerified) {
                return signedData;
            }
            SecurityUtil.LOGGER.log(Level.SEVERE, "Invalid {0} signature : \"{1}\" passed through the URL : \"{2}\" from IP : \"{3}\"", new Object[] { SystemAuthType.ISC.value, signature, securedRequest.getRequestURI(), securedRequest.getRemoteAddr() });
            return null;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    private static void isValidSignature(final String signature, final SystemAuthType systemAuthType, final SecurityRequestWrapper securedRequest) {
        if (!isValid(signature)) {
            SecurityUtil.LOGGER.log(Level.SEVERE, "{0} signature not present in the URL : \"{1}\" from IP : \"{2}\"", new Object[] { systemAuthType.value, securedRequest.getRequestURI(), securedRequest.getRemoteAddr() });
            throw new IAMSecurityException("ISC_SIGNATURE_NOT_PRESENT", securedRequest.getRequestURI(), securedRequest.getRemoteAddr(), securedRequest.getHeader("Referer"));
        }
    }
    
    public static String getCurrentRequestID() {
        return SecurityUtil.isLogClientThreadLocalClassLoaded ? LogClientThreadLocal.getRequestID() : null;
    }
    
    public static long sizeOf(final File file) {
        if (!file.exists()) {
            return 0L;
        }
        if (file.isDirectory()) {
            return sizeOfDirectory(file);
        }
        return file.length();
    }
    
    public static long sizeOfDirectory(final File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            return 0L;
        }
        final File[] files = directory.listFiles();
        if (files == null) {
            return 0L;
        }
        long size = 0L;
        for (final File file : files) {
            try {
                if (!isSymlink(file)) {
                    size += sizeOf(file);
                    if (size < 0L) {
                        break;
                    }
                }
            }
            catch (final IOException ex) {}
        }
        return size;
    }
    
    public static boolean isSymlink(final File file) throws IOException {
        if (file == null) {
            return false;
        }
        if (isSystemWindows()) {
            return false;
        }
        File fileInCanonicalDir = null;
        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        }
        else {
            final File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }
        return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile());
    }
    
    public static boolean isSystemWindows() {
        return '\\' == File.separatorChar;
    }
    
    static ScheduledExecutorService getWafScheduler() {
        return SecurityUtil.WAF_SCHEDULER;
    }
    
    public static void deleteUnClearedTempFilesAtFileUploadDir() {
        final File fileUploadDir = getTempFileUploadDir();
        long clearedFilesSize = 0L;
        if (fileUploadDir.exists()) {
            final List<String> clearedFiles = new ArrayList<String>();
            final List<String> unClearedDirs = new ArrayList<String>();
            for (final File tempFile : fileUploadDir.listFiles()) {
                final StringBuilder strBuilder = new StringBuilder("Name: ");
                strBuilder.append(tempFile.getName());
                strBuilder.append(", lastModificationTime: ");
                strBuilder.append(new Date(tempFile.lastModified()));
                strBuilder.append(", size (bytes): ");
                strBuilder.append(tempFile.length());
                if (tempFile.isFile()) {
                    clearedFiles.add(strBuilder.toString());
                    clearedFilesSize += tempFile.length();
                    tempFile.delete();
                }
                else {
                    unClearedDirs.add(strBuilder.toString());
                }
            }
            SecurityUtil.LOGGER.log(Level.INFO, "Cleared files in temp directory during server startup: {0}", new Object[] { clearedFiles });
            SecurityUtil.LOGGER.log(Level.INFO, "Uncleared directories in temp directory during server start up: {0}", new Object[] { unClearedDirs });
        }
        SecurityUtil.LOGGER.log(Level.INFO, "File upload directory: {0}, IsExists: {1}, ClearedTempFilesSize: {2} bytes", new Object[] { fileUploadDir, fileUploadDir.exists(), clearedFilesSize });
    }
    
    public static boolean isZip(final String contentType) {
        return "application/zip".equalsIgnoreCase(contentType) || "application/x-7z-compressed".equalsIgnoreCase(contentType);
    }
    
    public static boolean is7Zip(final String contentType) {
        return "application/x-7z-compressed".equalsIgnoreCase(contentType);
    }
    
    static boolean isBlacklisted(final String value, final BlacklistRule blacklistRule) {
        final BlacklistRule.Operator operator = blacklistRule.getOperator();
        final String blacklistedValue = blacklistRule.getValue();
        boolean matchFound = false;
        switch (operator) {
            case STRINGEQUALS: {
                matchFound = value.equalsIgnoreCase(blacklistedValue);
                break;
            }
            case STRINGCONTAINS: {
                matchFound = value.contains(blacklistedValue);
                break;
            }
            case REGEXMATCHES: {
                matchFound = blacklistRule.getValuePattern().matcher(value).matches();
                break;
            }
            case REGEXFIND: {
                matchFound = blacklistRule.getValuePattern().matcher(value).find();
                break;
            }
            case STARTSWITH: {
                matchFound = value.startsWith(blacklistedValue);
                break;
            }
            case ENDSWITH: {
                matchFound = value.endsWith(blacklistedValue);
                break;
            }
        }
        return matchFound;
    }
    
    public static String getBrowserSessionID(final HttpServletRequest request) {
        final String csrfCookieValue = getCSRFCookie(request, false);
        if (csrfCookieValue == null) {
            return null;
        }
        if (csrfCookieValue.length() <= 36) {
            return csrfCookieValue;
        }
        return csrfCookieValue.substring(csrfCookieValue.length() - 32);
    }
    
    static String getForwardedHost(final HttpServletRequest request) {
        final SecurityFilterProperties filterProps = SecurityFilterProperties.getInstance(request);
        if (filterProps.isEnabledHostOverride()) {
            final String forwardedHostHeaderName = filterProps.getForwardedHostHeaderName();
            final String forwardedHost = request.getHeader(forwardedHostHeaderName);
            if (forwardedHost != null) {
                if (filterProps.getAllowedForwardedHosts().contains("trusted")) {
                    final Authenticator authProviderImpl = filterProps.getAuthenticationProvider();
                    if (authProviderImpl != null && authProviderImpl.isTrustedDomain(forwardedHost)) {
                        return forwardedHost;
                    }
                }
                else {
                    for (String allowedHost : filterProps.getAllowedForwardedHosts()) {
                        allowedHost = allowedHost.trim();
                        if (allowedHost.equals(forwardedHost)) {
                            return forwardedHost;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    public static String getServerName(final HttpServletRequest request) {
        final String forwardedHost = getForwardedHost(request);
        return (forwardedHost != null) ? forwardedHost : request.getServerName();
    }
    
    public static ZSecURL validateURLParameter(final String parameterName, final String parameterValue, final ParameterRule parameterRule) {
        final HttpServletRequest request = getCurrentRequest();
        final URLValidatorRule uvRule = SecurityFilterProperties.getInstance(request).getURLValidatorRule(parameterRule.getTemplateName());
        if (uvRule != null) {
            try {
                final ZSecURL validatedURL = uvRule.getUrlvalidator().getValidatedURLObject(parameterValue);
                try {
                    if (parameterRule.isSpamCheckEnabled() && URLValidatorRule.COMMON_PROTOCOL.contains(validatedURL.getScheme()) && isSpamUrl(validatedURL.getScheme(), validatedURL.getDomainAuthority(), validatedURL.getPathInfo())) {
                        SecurityUtil.LOGGER.log(Level.WARNING, "\n url : {0} , detected as spam ", new Object[] { validatedURL.getSafeURL() });
                        throw new IAMSecurityException("SPAM_DETECTED");
                    }
                }
                catch (final IAMSecurityException e) {
                    throw e;
                }
                catch (final Exception e2) {
                    SecurityUtil.LOGGER.log(Level.SEVERE, "\n Exception occurred while antispam detection , url {0} , exception {1}  ", new Object[] { validatedURL.getSafeURL(), e2.getMessage() });
                    throw new IAMSecurityException("SPAM_DETECTION_FAILED");
                }
                if (parameterRule.getFileRuleForImportedData() != null && validatedURL.getScheme() != null) {
                    final UploadFileRule fileRuleForImport = parameterRule.getFileRuleForImportedData();
                    UploadedFileItem fileItem = null;
                    if (URLValidatorRule.COMMON_PROTOCOL.contains(validatedURL.getScheme())) {
                        fileItem = importFromURL(new URL(parameterValue), parameterName, fileRuleForImport, (SecurityRequestWrapper)request);
                    }
                    else if ("data".equals(validatedURL.getScheme())) {
                        fileItem = convertDataURIAsFile(validatedURL, fileRuleForImport, (SecurityRequestWrapper)request);
                    }
                    if (fileItem != null) {
                        fileRuleForImport.validate(fileItem);
                        validatedURL.setImportedDataAsFile((Object)fileItem);
                        ((SecurityRequestWrapper)request).setImportedDataAsFile_RequestAttribute("IMPORTED_DATA_AS_FILE", fileItem);
                    }
                }
                return validatedURL;
            }
            catch (final MalformedURLException ex) {
                SecurityUtil.LOGGER.log(Level.WARNING, "\n Unable to parse URL : \"{0}\" & Exception Message : {1}", new Object[] { parameterValue, ex.getMessage() });
                throw new IAMSecurityException(ex.getMessage(), request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), parameterRule.getParamName(), parameterRule);
            }
        }
        SecurityUtil.LOGGER.log(Level.WARNING, "\n  <url-validator> rule not defined for the name : \"{0}\" ", new Object[] { parameterRule.getTemplateName() });
        throw new IAMSecurityException("URL_VALIDATOR_RULE_NOT_DEFINED", request.getRequestURI(), request.getRemoteAddr(), request.getHeader("Referer"), parameterRule.getParamName());
    }
    
    private static UploadedFileItem convertDataURIAsFile(final ZSecURL validatedURL, final UploadFileRule fileRule, final SecurityRequestWrapper request) {
        final InputStream inputStream = decodeInputStream(validatedURL.getDataURIDatapart(), validatedURL.getDataURIEncoding(), request);
        final String contentType = validatedURL.getDataURIMimeType();
        final String extension = contentType.substring(contentType.indexOf("/") + 1);
        final String fieldName = fileRule.getFieldName();
        try {
            return createUploadedFileItem(fieldName, fieldName + "." + extension, contentType, fileRule, inputStream, null, request);
        }
        catch (final Exception ex) {
            if (ex instanceof IAMSecurityException) {
                throw (IAMSecurityException)ex;
            }
            SecurityUtil.LOGGER.log(Level.WARNING, " Unable to convert DataURI as File for the field \"{0}\" ", fieldName);
            SecurityUtil.LOGGER.log(Level.WARNING, "", ex);
            throw new IAMSecurityException("UNABLE_TO_IMPORT");
        }
    }
    
    public static boolean checkForSSRF(final HttpServletRequest request) {
        return IPUtil.isPrivateIP(request.getRemoteAddr()) && "true".equals(request.getHeader("ZSEC_USER_IMPORT_URL"));
    }
    
    static String getSourceDomain(final HttpServletRequest request) {
        String sourceOrigin = getDomainWithPort(request.getHeader("Origin"));
        if (!isValid(sourceOrigin)) {
            sourceOrigin = getDomainWithPort(request.getHeader("Referer"));
            if (!isValid(sourceOrigin)) {
                return null;
            }
        }
        return sourceOrigin;
    }
    
    static ParameterRule getExtraParameterRule(final HttpServletRequest request, final ActionRule actionRule) {
        ParameterRule extraParamRule = actionRule.getExtraParameterRule();
        if (extraParamRule == null && !actionRule.isDisableExtraparam() && !actionRule.isIgnoreExtraParam()) {
            extraParamRule = SecurityFilterProperties.getInstance(request).getExtraParamRule();
        }
        return extraParamRule;
    }
    
    static {
        LOGGER = Logger.getLogger(SecurityUtil.class.getName());
        HEX = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        SecurityUtil.inverseClearTextPattern = Pattern.compile("((?![0-9a-zA-Z_\\-\\.\\$@\\?\\,\\:\\'\\/\\!\\P{InBasicLatin}\\s]).)+");
        SecurityUtil.matcherUtil = null;
        CURRENT_REQUEST = new ZSecThreadLocal<HttpServletRequest>();
        DOCUMENT_BUILDER = new ZSecThreadLocal<DocumentBuilder>();
        TRANSFORMER_FACTORY = new ZSecThreadLocal<TransformerFactory>();
        XML_READER = new ZSecThreadLocal<XMLReader>();
        SCHEMAFACTORY = new ZSecThreadLocal<SchemaFactory>();
        JSONEXCEPTIONTRACELIST = new ZSecThreadLocal<List<JSONObject>>();
        ZSECEVENTS = new ZSecThreadLocal<List<Object>>();
        SecurityUtil.isMimeDetectorRegistered = false;
        TEMPDIR = System.getProperty("java.io.tmpdir");
        SecurityUtil.hexCodec = new Hex();
        SINGLE_TIME_PATTERN = Pattern.compile("([0-9]+)(MS|S|M|H|D)?", 2);
        CURRENT_LOGREQUEST = new ThreadLocal<HttpServletRequest>();
        HTTP_OVERRIDE_METHODS = Arrays.asList("PUT", "DELETE", "PATCH");
        HTTP_MULTIPART_METHODS = Arrays.asList("POST", "PUT", "PATCH");
        if (SecurityUtil.TEMPDIR == null) {
            throw new RuntimeException("System property \"java.io.tmpdir\" is null");
        }
        final File tmpDir = new File(SecurityUtil.TEMPDIR);
        if (!tmpDir.mkdirs() && !tmpDir.isDirectory()) {
            throw new RuntimeException("Failed to create temp directory :" + SecurityUtil.TEMPDIR);
        }
        final File tempFileUploadDir = new File(SecurityUtil.TEMPDIR, "waf_fileupload");
        if (!tempFileUploadDir.mkdir() && !tempFileUploadDir.isDirectory()) {
            throw new RuntimeException("Failed to create file upload directory \"waf_fileupload\" under temporary directory \"" + SecurityUtil.TEMPDIR + "\"");
        }
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final String ceClassName = "com.zoho.logs.logclient.LogClientThreadLocal";
        try {
            SecurityUtil.isLogClientThreadLocalClassLoaded = (cl.loadClass(ceClassName) != null);
            SecurityUtil.LOGGER.log(Level.WARNING, "LogClientThreadLocalClass Loaded Successfully. Class Name : {0} ", new Object[] { ceClassName });
        }
        catch (final ClassNotFoundException e) {
            SecurityUtil.LOGGER.log(Level.WARNING, "Unable to Load LogClientThreadLocalClass . Class Name : {0}, Exception {1}", new Object[] { ceClassName, e });
        }
        WAF_SCHEDULER = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable r) {
                final Thread thread = new Thread(r, "waf_scheduler");
                thread.setDaemon(true);
                return thread;
            }
        });
        SecurityUtil.numberOfRedirectCounts = new ThreadLocal<Integer>();
        (CHARACTERS_TO_ESCAPE = new BitSet(128)).set(32);
        SecurityUtil.CHARACTERS_TO_ESCAPE.set(34);
        SecurityUtil.CHARACTERS_TO_ESCAPE.set(96);
        SecurityUtil.CHARACTERS_TO_ESCAPE.set(60);
        SecurityUtil.CHARACTERS_TO_ESCAPE.set(62);
        SecurityUtil.CHARACTERS_TO_ESCAPE.set(123);
        SecurityUtil.CHARACTERS_TO_ESCAPE.set(125);
        SecurityUtil.CHARACTERS_TO_ESCAPE.set(124);
        SecurityUtil.CHARACTERS_TO_ESCAPE.set(92);
        SecurityUtil.CHARACTERS_TO_ESCAPE.set(94);
        SecurityUtil.CHARACTERS_TO_ESCAPE.set(126);
        SecurityUtil.CHARACTERS_TO_ESCAPE.set(91);
        SecurityUtil.CHARACTERS_TO_ESCAPE.set(93);
        SecurityUtil.CHARACTERS_TO_ESCAPE.set(127);
        for (int c = 0; c < 32; ++c) {
            SecurityUtil.CHARACTERS_TO_ESCAPE.set(c);
        }
        CHAR_ARRAY = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        SecurityUtil.keyFactory = null;
        SecurityUtil.currentWebAppName = new ZSecThreadLocal<String>();
        CSRFTOKENTOTICKETMAP = new LRUCacheMap<String, String>(1000, 1000, 1, TimeUnit.HOURS);
        SecurityUtil.confDirPath = null;
    }
    
    public enum WafAgentUrls
    {
        CLEAN_LIVE_WINDOW_COUNT("/waf-agent/clean-live-window-count");
        
        private String path;
        
        private WafAgentUrls(final String path) {
            this.path = path;
        }
        
        public String getPath() {
            return this.path;
        }
    }
    
    enum TIME_UNIT
    {
        D, 
        H, 
        M, 
        S, 
        MS;
    }
    
    static class MyAuthenticator extends Authenticator
    {
        private String usna;
        private String password;
        
        public MyAuthenticator(final String u, final String pass) {
            this.usna = u;
            this.password = pass;
        }
        
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(this.usna, this.password.toCharArray());
        }
    }
    
    enum SystemAuthType
    {
        ISC("ISC"), 
        INTERDC("INTERDC");
        
        private String value;
        
        private SystemAuthType(final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    public enum ZSEC_CUSTOM_REQUEST_ATTRIBUTES
    {
        LIVE_WINDOW_THROTTLES_KEY_MAP;
    }
    
    public enum SENSITIVE_PARAM_TYPE
    {
        CSRF, 
        SECRET;
    }
}
