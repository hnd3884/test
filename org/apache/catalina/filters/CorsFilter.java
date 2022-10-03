package org.apache.catalina.filters;

import java.util.Collections;
import java.util.Arrays;
import java.util.Set;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.http.ResponseUtil;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Locale;
import javax.servlet.FilterConfig;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.HashSet;
import org.apache.juli.logging.LogFactory;
import java.util.Collection;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import javax.servlet.Filter;

public class CorsFilter implements Filter
{
    private final Log log;
    private static final StringManager sm;
    private final Collection<String> allowedOrigins;
    private boolean anyOriginAllowed;
    private final Collection<String> allowedHttpMethods;
    private final Collection<String> allowedHttpHeaders;
    private final Collection<String> exposedHeaders;
    private boolean supportsCredentials;
    private long preflightMaxAge;
    private boolean decorateRequest;
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String RESPONSE_HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    @Deprecated
    public static final String REQUEST_HEADER_VARY = "Vary";
    public static final String REQUEST_HEADER_ORIGIN = "Origin";
    public static final String REQUEST_HEADER_ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    public static final String REQUEST_HEADER_ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
    public static final String HTTP_REQUEST_ATTRIBUTE_PREFIX = "cors.";
    public static final String HTTP_REQUEST_ATTRIBUTE_ORIGIN = "cors.request.origin";
    public static final String HTTP_REQUEST_ATTRIBUTE_IS_CORS_REQUEST = "cors.isCorsRequest";
    public static final String HTTP_REQUEST_ATTRIBUTE_REQUEST_TYPE = "cors.request.type";
    public static final String HTTP_REQUEST_ATTRIBUTE_REQUEST_HEADERS = "cors.request.headers";
    public static final Collection<String> SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES;
    public static final String DEFAULT_ALLOWED_ORIGINS = "";
    public static final String DEFAULT_ALLOWED_HTTP_METHODS = "GET,POST,HEAD,OPTIONS";
    public static final String DEFAULT_PREFLIGHT_MAXAGE = "1800";
    public static final String DEFAULT_SUPPORTS_CREDENTIALS = "false";
    public static final String DEFAULT_ALLOWED_HTTP_HEADERS = "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers";
    public static final String DEFAULT_EXPOSED_HEADERS = "";
    public static final String DEFAULT_DECORATE_REQUEST = "true";
    public static final String PARAM_CORS_ALLOWED_ORIGINS = "cors.allowed.origins";
    public static final String PARAM_CORS_SUPPORT_CREDENTIALS = "cors.support.credentials";
    public static final String PARAM_CORS_EXPOSED_HEADERS = "cors.exposed.headers";
    public static final String PARAM_CORS_ALLOWED_HEADERS = "cors.allowed.headers";
    public static final String PARAM_CORS_ALLOWED_METHODS = "cors.allowed.methods";
    public static final String PARAM_CORS_PREFLIGHT_MAXAGE = "cors.preflight.maxage";
    public static final String PARAM_CORS_REQUEST_DECORATE = "cors.request.decorate";
    
    public CorsFilter() {
        this.log = LogFactory.getLog((Class)CorsFilter.class);
        this.allowedOrigins = new HashSet<String>();
        this.allowedHttpMethods = new HashSet<String>();
        this.allowedHttpHeaders = new HashSet<String>();
        this.exposedHeaders = new HashSet<String>();
    }
    
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        if (!(servletRequest instanceof HttpServletRequest) || !(servletResponse instanceof HttpServletResponse)) {
            throw new ServletException(CorsFilter.sm.getString("corsFilter.onlyHttp"));
        }
        final HttpServletRequest request = (HttpServletRequest)servletRequest;
        final HttpServletResponse response = (HttpServletResponse)servletResponse;
        final CORSRequestType requestType = this.checkRequestType(request);
        if (this.isDecorateRequest()) {
            decorateCORSProperties(request, requestType);
        }
        switch (requestType) {
            case SIMPLE:
            case ACTUAL: {
                this.handleSimpleCORS(request, response, filterChain);
                break;
            }
            case PRE_FLIGHT: {
                this.handlePreflightCORS(request, response, filterChain);
                break;
            }
            case NOT_CORS: {
                this.handleNonCORS(request, response, filterChain);
                break;
            }
            default: {
                this.handleInvalidCORS(request, response, filterChain);
                break;
            }
        }
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.parseAndStore(this.getInitParameter(filterConfig, "cors.allowed.origins", ""), this.getInitParameter(filterConfig, "cors.allowed.methods", "GET,POST,HEAD,OPTIONS"), this.getInitParameter(filterConfig, "cors.allowed.headers", "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers"), this.getInitParameter(filterConfig, "cors.exposed.headers", ""), this.getInitParameter(filterConfig, "cors.support.credentials", "false"), this.getInitParameter(filterConfig, "cors.preflight.maxage", "1800"), this.getInitParameter(filterConfig, "cors.request.decorate", "true"));
    }
    
    private String getInitParameter(final FilterConfig filterConfig, final String name, final String defaultValue) {
        if (filterConfig == null) {
            return defaultValue;
        }
        final String value = filterConfig.getInitParameter(name);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }
    
    protected void handleSimpleCORS(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        final CORSRequestType requestType = this.checkRequestType(request);
        if (requestType != CORSRequestType.SIMPLE && requestType != CORSRequestType.ACTUAL) {
            throw new IllegalArgumentException(CorsFilter.sm.getString("corsFilter.wrongType2", new Object[] { CORSRequestType.SIMPLE, CORSRequestType.ACTUAL }));
        }
        final String origin = request.getHeader("Origin");
        final String method = request.getMethod();
        if (!this.isOriginAllowed(origin)) {
            this.handleInvalidCORS(request, response, filterChain);
            return;
        }
        if (!this.getAllowedHttpMethods().contains(method)) {
            this.handleInvalidCORS(request, response, filterChain);
            return;
        }
        this.addStandardHeaders(request, response);
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
    
    protected void handlePreflightCORS(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        final CORSRequestType requestType = this.checkRequestType(request);
        if (requestType != CORSRequestType.PRE_FLIGHT) {
            throw new IllegalArgumentException(CorsFilter.sm.getString("corsFilter.wrongType1", new Object[] { CORSRequestType.PRE_FLIGHT.name().toLowerCase(Locale.ENGLISH) }));
        }
        final String origin = request.getHeader("Origin");
        if (!this.isOriginAllowed(origin)) {
            this.handleInvalidCORS(request, response, filterChain);
            return;
        }
        String accessControlRequestMethod = request.getHeader("Access-Control-Request-Method");
        if (accessControlRequestMethod == null) {
            this.handleInvalidCORS(request, response, filterChain);
            return;
        }
        accessControlRequestMethod = accessControlRequestMethod.trim();
        final String accessControlRequestHeadersHeader = request.getHeader("Access-Control-Request-Headers");
        final List<String> accessControlRequestHeaders = new LinkedList<String>();
        if (accessControlRequestHeadersHeader != null && !accessControlRequestHeadersHeader.trim().isEmpty()) {
            final String[] arr$;
            final String[] headers = arr$ = accessControlRequestHeadersHeader.trim().split(",");
            for (final String header : arr$) {
                accessControlRequestHeaders.add(header.trim().toLowerCase(Locale.ENGLISH));
            }
        }
        if (!this.getAllowedHttpMethods().contains(accessControlRequestMethod)) {
            this.handleInvalidCORS(request, response, filterChain);
            return;
        }
        if (!accessControlRequestHeaders.isEmpty()) {
            for (final String header2 : accessControlRequestHeaders) {
                if (!this.getAllowedHttpHeaders().contains(header2)) {
                    this.handleInvalidCORS(request, response, filterChain);
                    return;
                }
            }
        }
        this.addStandardHeaders(request, response);
    }
    
    private void handleNonCORS(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        this.addStandardHeaders(request, response);
        filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
    }
    
    private void handleInvalidCORS(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) {
        final String origin = request.getHeader("Origin");
        final String method = request.getMethod();
        final String accessControlRequestHeaders = request.getHeader("Access-Control-Request-Headers");
        response.setContentType("text/plain");
        response.setStatus(403);
        response.resetBuffer();
        if (this.log.isDebugEnabled()) {
            final StringBuilder message = new StringBuilder("Invalid CORS request; Origin=");
            message.append(origin);
            message.append(";Method=");
            message.append(method);
            if (accessControlRequestHeaders != null) {
                message.append(";Access-Control-Request-Headers=");
                message.append(accessControlRequestHeaders);
            }
            this.log.debug((Object)message.toString());
        }
    }
    
    private void addStandardHeaders(final HttpServletRequest request, final HttpServletResponse response) {
        final String method = request.getMethod();
        final String origin = request.getHeader("Origin");
        final boolean anyOriginAllowed = this.isAnyOriginAllowed();
        if (!anyOriginAllowed) {
            ResponseUtil.addVaryFieldName(response, "Origin");
        }
        if (anyOriginAllowed) {
            response.addHeader("Access-Control-Allow-Origin", "*");
        }
        else {
            response.addHeader("Access-Control-Allow-Origin", origin);
        }
        if (this.isSupportsCredentials()) {
            response.addHeader("Access-Control-Allow-Credentials", "true");
        }
        final Collection<String> exposedHeaders = this.getExposedHeaders();
        if (exposedHeaders != null && exposedHeaders.size() > 0) {
            final String exposedHeadersString = join(exposedHeaders, ",");
            response.addHeader("Access-Control-Expose-Headers", exposedHeadersString);
        }
        if ("OPTIONS".equals(method)) {
            ResponseUtil.addVaryFieldName(response, "Access-Control-Request-Method");
            ResponseUtil.addVaryFieldName(response, "Access-Control-Request-Headers");
            final long preflightMaxAge = this.getPreflightMaxAge();
            if (preflightMaxAge > 0L) {
                response.addHeader("Access-Control-Max-Age", String.valueOf(preflightMaxAge));
            }
            final Collection<String> allowedHttpMethods = this.getAllowedHttpMethods();
            if (allowedHttpMethods != null && !allowedHttpMethods.isEmpty()) {
                response.addHeader("Access-Control-Allow-Methods", join(allowedHttpMethods, ","));
            }
            final Collection<String> allowedHttpHeaders = this.getAllowedHttpHeaders();
            if (allowedHttpHeaders != null && !allowedHttpHeaders.isEmpty()) {
                response.addHeader("Access-Control-Allow-Headers", join(allowedHttpHeaders, ","));
            }
        }
    }
    
    public void destroy() {
    }
    
    protected static void decorateCORSProperties(final HttpServletRequest request, final CORSRequestType corsRequestType) {
        if (request == null) {
            throw new IllegalArgumentException(CorsFilter.sm.getString("corsFilter.nullRequest"));
        }
        if (corsRequestType == null) {
            throw new IllegalArgumentException(CorsFilter.sm.getString("corsFilter.nullRequestType"));
        }
        switch (corsRequestType) {
            case SIMPLE:
            case ACTUAL: {
                request.setAttribute("cors.isCorsRequest", (Object)Boolean.TRUE);
                request.setAttribute("cors.request.origin", (Object)request.getHeader("Origin"));
                request.setAttribute("cors.request.type", (Object)corsRequestType.name().toLowerCase(Locale.ENGLISH));
                break;
            }
            case PRE_FLIGHT: {
                request.setAttribute("cors.isCorsRequest", (Object)Boolean.TRUE);
                request.setAttribute("cors.request.origin", (Object)request.getHeader("Origin"));
                request.setAttribute("cors.request.type", (Object)corsRequestType.name().toLowerCase(Locale.ENGLISH));
                String headers = request.getHeader("Access-Control-Request-Headers");
                if (headers == null) {
                    headers = "";
                }
                request.setAttribute("cors.request.headers", (Object)headers);
                break;
            }
            case NOT_CORS: {
                request.setAttribute("cors.isCorsRequest", (Object)Boolean.FALSE);
                break;
            }
        }
    }
    
    protected static String join(final Collection<String> elements, final String joinSeparator) {
        String separator = ",";
        if (elements == null) {
            return null;
        }
        if (joinSeparator != null) {
            separator = joinSeparator;
        }
        final StringBuilder buffer = new StringBuilder();
        boolean isFirst = true;
        for (final String element : elements) {
            if (!isFirst) {
                buffer.append(separator);
            }
            else {
                isFirst = false;
            }
            if (element != null) {
                buffer.append(element);
            }
        }
        return buffer.toString();
    }
    
    protected CORSRequestType checkRequestType(final HttpServletRequest request) {
        CORSRequestType requestType = CORSRequestType.INVALID_CORS;
        if (request == null) {
            throw new IllegalArgumentException(CorsFilter.sm.getString("corsFilter.nullRequest"));
        }
        final String originHeader = request.getHeader("Origin");
        if (originHeader != null) {
            if (originHeader.isEmpty()) {
                requestType = CORSRequestType.INVALID_CORS;
            }
            else if (!RequestUtil.isValidOrigin(originHeader)) {
                requestType = CORSRequestType.INVALID_CORS;
            }
            else {
                if (RequestUtil.isSameOrigin(request, originHeader)) {
                    return CORSRequestType.NOT_CORS;
                }
                final String method = request.getMethod();
                if (method != null) {
                    if ("OPTIONS".equals(method)) {
                        final String accessControlRequestMethodHeader = request.getHeader("Access-Control-Request-Method");
                        if (accessControlRequestMethodHeader != null && !accessControlRequestMethodHeader.isEmpty()) {
                            requestType = CORSRequestType.PRE_FLIGHT;
                        }
                        else if (accessControlRequestMethodHeader != null && accessControlRequestMethodHeader.isEmpty()) {
                            requestType = CORSRequestType.INVALID_CORS;
                        }
                        else {
                            requestType = CORSRequestType.ACTUAL;
                        }
                    }
                    else if ("GET".equals(method) || "HEAD".equals(method)) {
                        requestType = CORSRequestType.SIMPLE;
                    }
                    else if ("POST".equals(method)) {
                        final String mediaType = this.getMediaType(request.getContentType());
                        if (mediaType != null) {
                            if (CorsFilter.SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES.contains(mediaType)) {
                                requestType = CORSRequestType.SIMPLE;
                            }
                            else {
                                requestType = CORSRequestType.ACTUAL;
                            }
                        }
                    }
                    else {
                        requestType = CORSRequestType.ACTUAL;
                    }
                }
            }
        }
        else {
            requestType = CORSRequestType.NOT_CORS;
        }
        return requestType;
    }
    
    private String getMediaType(final String contentType) {
        if (contentType == null) {
            return null;
        }
        String result = contentType.toLowerCase(Locale.ENGLISH);
        final int firstSemiColonIndex = result.indexOf(59);
        if (firstSemiColonIndex > -1) {
            result = result.substring(0, firstSemiColonIndex);
        }
        result = result.trim();
        return result;
    }
    
    private boolean isOriginAllowed(final String origin) {
        return this.isAnyOriginAllowed() || this.getAllowedOrigins().contains(origin);
    }
    
    private void parseAndStore(final String allowedOrigins, final String allowedHttpMethods, final String allowedHttpHeaders, final String exposedHeaders, final String supportsCredentials, final String preflightMaxAge, final String decorateRequest) throws ServletException {
        if (allowedOrigins.trim().equals("*")) {
            this.anyOriginAllowed = true;
        }
        else {
            this.anyOriginAllowed = false;
            final Set<String> setAllowedOrigins = this.parseStringToSet(allowedOrigins);
            this.allowedOrigins.clear();
            this.allowedOrigins.addAll(setAllowedOrigins);
        }
        final Set<String> setAllowedHttpMethods = this.parseStringToSet(allowedHttpMethods);
        this.allowedHttpMethods.clear();
        this.allowedHttpMethods.addAll(setAllowedHttpMethods);
        final Set<String> setAllowedHttpHeaders = this.parseStringToSet(allowedHttpHeaders);
        final Set<String> lowerCaseHeaders = new HashSet<String>();
        for (final String header : setAllowedHttpHeaders) {
            final String lowerCase = header.toLowerCase(Locale.ENGLISH);
            lowerCaseHeaders.add(lowerCase);
        }
        this.allowedHttpHeaders.clear();
        this.allowedHttpHeaders.addAll(lowerCaseHeaders);
        final Set<String> setExposedHeaders = this.parseStringToSet(exposedHeaders);
        this.exposedHeaders.clear();
        this.exposedHeaders.addAll(setExposedHeaders);
        this.supportsCredentials = Boolean.parseBoolean(supportsCredentials);
        if (this.supportsCredentials && this.anyOriginAllowed) {
            throw new ServletException(CorsFilter.sm.getString("corsFilter.invalidSupportsCredentials"));
        }
        try {
            if (!preflightMaxAge.isEmpty()) {
                this.preflightMaxAge = Long.parseLong(preflightMaxAge);
            }
            else {
                this.preflightMaxAge = 0L;
            }
        }
        catch (final NumberFormatException e) {
            throw new ServletException(CorsFilter.sm.getString("corsFilter.invalidPreflightMaxAge"), (Throwable)e);
        }
        this.decorateRequest = Boolean.parseBoolean(decorateRequest);
    }
    
    private Set<String> parseStringToSet(final String data) {
        String[] splits;
        if (data != null && data.length() > 0) {
            splits = data.split(",");
        }
        else {
            splits = new String[0];
        }
        final Set<String> set = new HashSet<String>();
        if (splits.length > 0) {
            for (final String split : splits) {
                set.add(split.trim());
            }
        }
        return set;
    }
    
    @Deprecated
    protected static boolean isValidOrigin(final String origin) {
        return RequestUtil.isValidOrigin(origin);
    }
    
    public boolean isAnyOriginAllowed() {
        return this.anyOriginAllowed;
    }
    
    public Collection<String> getExposedHeaders() {
        return this.exposedHeaders;
    }
    
    public boolean isSupportsCredentials() {
        return this.supportsCredentials;
    }
    
    public long getPreflightMaxAge() {
        return this.preflightMaxAge;
    }
    
    public Collection<String> getAllowedOrigins() {
        return this.allowedOrigins;
    }
    
    public Collection<String> getAllowedHttpMethods() {
        return this.allowedHttpMethods;
    }
    
    public Collection<String> getAllowedHttpHeaders() {
        return this.allowedHttpHeaders;
    }
    
    public boolean isDecorateRequest() {
        return this.decorateRequest;
    }
    
    static {
        sm = StringManager.getManager((Class)CorsFilter.class);
        SIMPLE_HTTP_REQUEST_CONTENT_TYPE_VALUES = Collections.unmodifiableSet((Set<?>)new HashSet<Object>(Arrays.asList("application/x-www-form-urlencoded", "multipart/form-data", "text/plain")));
    }
    
    protected enum CORSRequestType
    {
        SIMPLE, 
        ACTUAL, 
        PRE_FLIGHT, 
        NOT_CORS, 
        INVALID_CORS;
    }
}
