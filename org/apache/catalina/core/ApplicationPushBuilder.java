package org.apache.catalina.core;

import java.util.HashSet;
import org.apache.tomcat.util.http.CookieProcessor;
import org.apache.tomcat.util.buf.HexUtils;
import java.nio.charset.Charset;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.coyote.ActionCode;
import java.util.Collections;
import org.apache.tomcat.util.http.parser.HttpParser;
import java.util.Locale;
import java.util.Iterator;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Context;
import java.util.Enumeration;
import org.apache.catalina.authenticator.AuthenticatorBase;
import java.util.Collection;
import java.util.Arrays;
import javax.servlet.SessionTrackingMode;
import org.apache.catalina.util.SessionConfig;
import java.util.ArrayList;
import org.apache.tomcat.util.collections.CaseInsensitiveKeyMap;
import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Map;
import org.apache.catalina.connector.Request;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;

public class ApplicationPushBuilder
{
    private static final StringManager sm;
    private static final Set<String> DISALLOWED_METHODS;
    private final HttpServletRequest baseRequest;
    private final Request catalinaRequest;
    private final org.apache.coyote.Request coyoteRequest;
    private final String sessionCookieName;
    private final String sessionPathParameterName;
    private final boolean addSessionCookie;
    private final boolean addSessionPathParameter;
    private final Map<String, List<String>> headers;
    private final List<Cookie> cookies;
    private String method;
    private String path;
    private String queryString;
    private String sessionId;
    private String userName;
    
    public ApplicationPushBuilder(final Request catalinaRequest, final HttpServletRequest request) {
        this.headers = (Map<String, List<String>>)new CaseInsensitiveKeyMap();
        this.cookies = new ArrayList<Cookie>();
        this.method = "GET";
        this.baseRequest = request;
        this.catalinaRequest = catalinaRequest;
        this.coyoteRequest = catalinaRequest.getCoyoteRequest();
        final Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            final List<String> values = new ArrayList<String>();
            this.headers.put(headerName, values);
            final Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                values.add(headerValues.nextElement());
            }
        }
        this.headers.remove("if-match");
        this.headers.remove("if-none-match");
        this.headers.remove("if-modified-since");
        this.headers.remove("if-unmodified-since");
        this.headers.remove("if-range");
        this.headers.remove("range");
        this.headers.remove("expect");
        this.headers.remove("authorization");
        this.headers.remove("referer");
        this.headers.remove("cookie");
        final StringBuffer referer = request.getRequestURL();
        if (request.getQueryString() != null) {
            referer.append('?');
            referer.append(request.getQueryString());
        }
        this.addHeader("referer", referer.toString());
        final Context context = catalinaRequest.getContext();
        this.sessionCookieName = SessionConfig.getSessionCookieName(context);
        this.sessionPathParameterName = SessionConfig.getSessionUriParamName(context);
        final HttpSession session = request.getSession(false);
        if (session != null) {
            this.sessionId = session.getId();
        }
        if (this.sessionId == null) {
            this.sessionId = request.getRequestedSessionId();
        }
        if (!request.isRequestedSessionIdFromCookie() && !request.isRequestedSessionIdFromURL() && this.sessionId != null) {
            final Set<SessionTrackingMode> sessionTrackingModes = request.getServletContext().getEffectiveSessionTrackingModes();
            this.addSessionCookie = sessionTrackingModes.contains(SessionTrackingMode.COOKIE);
            this.addSessionPathParameter = sessionTrackingModes.contains(SessionTrackingMode.URL);
        }
        else {
            this.addSessionCookie = request.isRequestedSessionIdFromCookie();
            this.addSessionPathParameter = request.isRequestedSessionIdFromURL();
        }
        if (request.getCookies() != null) {
            this.cookies.addAll(Arrays.asList(request.getCookies()));
        }
        for (final Cookie responseCookie : catalinaRequest.getResponse().getCookies()) {
            if (responseCookie.getMaxAge() < 0) {
                final Iterator<Cookie> cookieIterator = this.cookies.iterator();
                while (cookieIterator.hasNext()) {
                    final Cookie cookie = cookieIterator.next();
                    if (cookie.getName().equals(responseCookie.getName())) {
                        cookieIterator.remove();
                    }
                }
            }
            else {
                this.cookies.add(new Cookie(responseCookie.getName(), responseCookie.getValue()));
            }
        }
        final List<String> cookieValues = new ArrayList<String>(1);
        cookieValues.add(generateCookieHeader(this.cookies, catalinaRequest.getContext().getCookieProcessor()));
        this.headers.put("cookie", cookieValues);
        if (catalinaRequest.getPrincipal() != null) {
            if (session == null || catalinaRequest.getSessionInternal(false).getPrincipal() == null || !(context.getAuthenticator() instanceof AuthenticatorBase) || !((AuthenticatorBase)context.getAuthenticator()).getCache()) {
                this.userName = catalinaRequest.getPrincipal().getName();
            }
            this.setHeader("authorization", "x-push");
        }
    }
    
    public ApplicationPushBuilder path(final String path) {
        if (path.startsWith("/")) {
            this.path = path;
        }
        else {
            final String contextPath = this.baseRequest.getContextPath();
            final int len = contextPath.length() + path.length() + 1;
            final StringBuilder sb = new StringBuilder(len);
            sb.append(contextPath);
            sb.append('/');
            sb.append(path);
            this.path = sb.toString();
        }
        return this;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public ApplicationPushBuilder method(final String method) {
        final String upperMethod = method.trim().toUpperCase(Locale.ENGLISH);
        if (ApplicationPushBuilder.DISALLOWED_METHODS.contains(upperMethod) || upperMethod.length() == 0) {
            throw new IllegalArgumentException(ApplicationPushBuilder.sm.getString("applicationPushBuilder.methodInvalid", new Object[] { upperMethod }));
        }
        for (final char c : upperMethod.toCharArray()) {
            if (!HttpParser.isToken((int)c)) {
                throw new IllegalArgumentException(ApplicationPushBuilder.sm.getString("applicationPushBuilder.methodNotToken", new Object[] { upperMethod }));
            }
        }
        this.method = method;
        return this;
    }
    
    public String getMethod() {
        return this.method;
    }
    
    public ApplicationPushBuilder queryString(final String queryString) {
        this.queryString = queryString;
        return this;
    }
    
    public String getQueryString() {
        return this.queryString;
    }
    
    public ApplicationPushBuilder sessionId(final String sessionId) {
        this.sessionId = sessionId;
        return this;
    }
    
    public String getSessionId() {
        return this.sessionId;
    }
    
    public ApplicationPushBuilder addHeader(final String name, final String value) {
        List<String> values = this.headers.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            this.headers.put(name, values);
        }
        values.add(value);
        return this;
    }
    
    public ApplicationPushBuilder setHeader(final String name, final String value) {
        List<String> values = this.headers.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            this.headers.put(name, values);
        }
        else {
            values.clear();
        }
        values.add(value);
        return this;
    }
    
    public ApplicationPushBuilder removeHeader(final String name) {
        this.headers.remove(name);
        return this;
    }
    
    public Set<String> getHeaderNames() {
        return Collections.unmodifiableSet((Set<? extends String>)this.headers.keySet());
    }
    
    public String getHeader(final String name) {
        final List<String> values = this.headers.get(name);
        if (values == null) {
            return null;
        }
        return values.get(0);
    }
    
    public void push() {
        if (this.path == null) {
            throw new IllegalStateException(ApplicationPushBuilder.sm.getString("pushBuilder.noPath"));
        }
        final org.apache.coyote.Request pushTarget = new org.apache.coyote.Request();
        pushTarget.method().setString(this.method);
        pushTarget.serverName().setString(this.baseRequest.getServerName());
        pushTarget.setServerPort(this.baseRequest.getServerPort());
        pushTarget.scheme().setString(this.baseRequest.getScheme());
        for (final Map.Entry<String, List<String>> header : this.headers.entrySet()) {
            for (final String value : header.getValue()) {
                pushTarget.getMimeHeaders().addValue((String)header.getKey()).setString(value);
            }
        }
        final int queryIndex = this.path.indexOf(63);
        String pushQueryString = null;
        String pushPath;
        if (queryIndex > -1) {
            pushPath = this.path.substring(0, queryIndex);
            if (queryIndex + 1 < this.path.length()) {
                pushQueryString = this.path.substring(queryIndex + 1);
            }
        }
        else {
            pushPath = this.path;
        }
        if (this.sessionId != null) {
            if (this.addSessionPathParameter) {
                pushPath = pushPath + ";" + this.sessionPathParameterName + "=" + this.sessionId;
                pushTarget.addPathParameter(this.sessionPathParameterName, this.sessionId);
            }
            if (this.addSessionCookie) {
                final String sessionCookieHeader = this.sessionCookieName + "=" + this.sessionId;
                MessageBytes mb = pushTarget.getMimeHeaders().getValue("cookie");
                if (mb == null) {
                    mb = pushTarget.getMimeHeaders().addValue("cookie");
                    mb.setString(sessionCookieHeader);
                }
                else {
                    mb.setString(mb.getString() + ";" + sessionCookieHeader);
                }
            }
        }
        pushTarget.requestURI().setString(pushPath);
        pushTarget.decodedURI().setString(decode(pushPath, this.catalinaRequest.getConnector().getURICharset()));
        if (pushQueryString == null && this.queryString != null) {
            pushTarget.queryString().setString(this.queryString);
        }
        else if (pushQueryString != null && this.queryString == null) {
            pushTarget.queryString().setString(pushQueryString);
        }
        else if (pushQueryString != null && this.queryString != null) {
            pushTarget.queryString().setString(pushQueryString + "&" + this.queryString);
        }
        if (this.userName != null) {
            pushTarget.getRemoteUser().setString(this.userName);
            pushTarget.setRemoteUserNeedsAuthorization(true);
        }
        this.coyoteRequest.action(ActionCode.PUSH_REQUEST, (Object)pushTarget);
        this.path = null;
        this.headers.remove("if-none-match");
        this.headers.remove("if-modified-since");
    }
    
    static String decode(final String input, final Charset charset) {
        int start = input.indexOf(37);
        int end = 0;
        if (start == -1) {
            return input;
        }
        final StringBuilder result = new StringBuilder(input.length());
        while (start != -1) {
            result.append(input.substring(end, start));
            for (end = start + 3; end < input.length() && input.charAt(end) == '%'; end += 3) {}
            result.append(decodePercentSequence(input.substring(start, end), charset));
            start = input.indexOf(37, end);
        }
        result.append(input.substring(end));
        return result.toString();
    }
    
    private static String decodePercentSequence(final String sequence, final Charset charset) {
        final byte[] bytes = new byte[sequence.length() / 3];
        for (int i = 0; i < bytes.length; i += 3) {
            bytes[i] = (byte)((HexUtils.getDec((int)sequence.charAt(1 + 3 * i)) << 4) + HexUtils.getDec((int)sequence.charAt(2 + 3 * i)));
        }
        return new String(bytes, charset);
    }
    
    private static String generateCookieHeader(final List<Cookie> cookies, final CookieProcessor cookieProcessor) {
        final StringBuilder result = new StringBuilder();
        boolean first = true;
        for (final Cookie cookie : cookies) {
            if (first) {
                first = false;
            }
            else {
                result.append(';');
            }
            result.append(cookieProcessor.generateHeader(cookie, (HttpServletRequest)null));
        }
        return result.toString();
    }
    
    static {
        sm = StringManager.getManager((Class)ApplicationPushBuilder.class);
        (DISALLOWED_METHODS = new HashSet<String>()).add("POST");
        ApplicationPushBuilder.DISALLOWED_METHODS.add("PUT");
        ApplicationPushBuilder.DISALLOWED_METHODS.add("DELETE");
        ApplicationPushBuilder.DISALLOWED_METHODS.add("CONNECT");
        ApplicationPushBuilder.DISALLOWED_METHODS.add("OPTIONS");
        ApplicationPushBuilder.DISALLOWED_METHODS.add("TRACE");
    }
}
