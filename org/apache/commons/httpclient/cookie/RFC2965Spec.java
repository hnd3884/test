package org.apache.commons.httpclient.cookie;

import java.util.StringTokenizer;
import java.util.Arrays;
import org.apache.commons.httpclient.NameValuePair;
import java.util.Date;
import java.util.LinkedList;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import org.apache.commons.httpclient.util.ParameterFormatter;
import java.util.Comparator;

public class RFC2965Spec extends CookieSpecBase implements CookieVersionSupport
{
    private static final Comparator PATH_COMPOARATOR;
    public static final String SET_COOKIE2_KEY = "set-cookie2";
    private final ParameterFormatter formatter;
    private final List attribHandlerList;
    private final Map attribHandlerMap;
    private final CookieSpec rfc2109;
    
    public RFC2965Spec() {
        (this.formatter = new ParameterFormatter()).setAlwaysUseQuotes(true);
        this.attribHandlerMap = new HashMap(10);
        this.attribHandlerList = new ArrayList(10);
        this.rfc2109 = new RFC2109Spec();
        this.registerAttribHandler("path", new Cookie2PathAttributeHandler());
        this.registerAttribHandler("domain", new Cookie2DomainAttributeHandler());
        this.registerAttribHandler("port", new Cookie2PortAttributeHandler());
        this.registerAttribHandler("max-age", new Cookie2MaxageAttributeHandler());
        this.registerAttribHandler("secure", new CookieSecureAttributeHandler());
        this.registerAttribHandler("comment", new CookieCommentAttributeHandler());
        this.registerAttribHandler("commenturl", new CookieCommentUrlAttributeHandler());
        this.registerAttribHandler("discard", new CookieDiscardAttributeHandler());
        this.registerAttribHandler("version", new Cookie2VersionAttributeHandler());
    }
    
    protected void registerAttribHandler(final String name, final CookieAttributeHandler handler) {
        if (name == null) {
            throw new IllegalArgumentException("Attribute name may not be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Attribute handler may not be null");
        }
        if (!this.attribHandlerList.contains(handler)) {
            this.attribHandlerList.add(handler);
        }
        this.attribHandlerMap.put(name, handler);
    }
    
    protected CookieAttributeHandler findAttribHandler(final String name) {
        return this.attribHandlerMap.get(name);
    }
    
    protected CookieAttributeHandler getAttribHandler(final String name) {
        final CookieAttributeHandler handler = this.findAttribHandler(name);
        if (handler == null) {
            throw new IllegalStateException("Handler not registered for " + name + " attribute.");
        }
        return handler;
    }
    
    protected Iterator getAttribHandlerIterator() {
        return this.attribHandlerList.iterator();
    }
    
    public Cookie[] parse(final String host, final int port, final String path, final boolean secure, final Header header) throws MalformedCookieException {
        RFC2965Spec.LOG.trace((Object)"enter RFC2965.parse(String, int, String, boolean, Header)");
        if (header == null) {
            throw new IllegalArgumentException("Header may not be null.");
        }
        if (header.getName() == null) {
            throw new IllegalArgumentException("Header name may not be null.");
        }
        if (header.getName().equalsIgnoreCase("set-cookie2")) {
            return this.parse(host, port, path, secure, header.getValue());
        }
        if (header.getName().equalsIgnoreCase("set-cookie")) {
            return this.rfc2109.parse(host, port, path, secure, header.getValue());
        }
        throw new MalformedCookieException("Header name is not valid. RFC 2965 supports \"set-cookie\" and \"set-cookie2\" headers.");
    }
    
    public Cookie[] parse(String host, final int port, String path, final boolean secure, final String header) throws MalformedCookieException {
        RFC2965Spec.LOG.trace((Object)"enter RFC2965Spec.parse(String, int, String, boolean, String)");
        if (host == null) {
            throw new IllegalArgumentException("Host of origin may not be null");
        }
        if (host.trim().equals("")) {
            throw new IllegalArgumentException("Host of origin may not be blank");
        }
        if (port < 0) {
            throw new IllegalArgumentException("Invalid port: " + port);
        }
        if (path == null) {
            throw new IllegalArgumentException("Path of origin may not be null.");
        }
        if (header == null) {
            throw new IllegalArgumentException("Header may not be null.");
        }
        if (path.trim().equals("")) {
            path = "/";
        }
        host = getEffectiveHost(host);
        final HeaderElement[] headerElements = HeaderElement.parseElements(header.toCharArray());
        final List cookies = new LinkedList();
        for (int i = 0; i < headerElements.length; ++i) {
            final HeaderElement headerelement = headerElements[i];
            Cookie2 cookie = null;
            try {
                cookie = new Cookie2(host, headerelement.getName(), headerelement.getValue(), path, null, false, new int[] { port });
            }
            catch (final IllegalArgumentException ex) {
                throw new MalformedCookieException(ex.getMessage());
            }
            final NameValuePair[] parameters = headerelement.getParameters();
            if (parameters != null) {
                final Map attribmap = new HashMap(parameters.length);
                for (int j = parameters.length - 1; j >= 0; --j) {
                    final NameValuePair param = parameters[j];
                    attribmap.put(param.getName().toLowerCase(), param);
                }
                final Iterator it = attribmap.entrySet().iterator();
                while (it.hasNext()) {
                    final Map.Entry entry = it.next();
                    this.parseAttribute(entry.getValue(), cookie);
                }
            }
            cookies.add(cookie);
        }
        return cookies.toArray(new Cookie[cookies.size()]);
    }
    
    public void parseAttribute(final NameValuePair attribute, final Cookie cookie) throws MalformedCookieException {
        if (attribute == null) {
            throw new IllegalArgumentException("Attribute may not be null.");
        }
        if (attribute.getName() == null) {
            throw new IllegalArgumentException("Attribute Name may not be null.");
        }
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null.");
        }
        final String paramName = attribute.getName().toLowerCase();
        final String paramValue = attribute.getValue();
        final CookieAttributeHandler handler = this.findAttribHandler(paramName);
        if (handler == null) {
            if (RFC2965Spec.LOG.isDebugEnabled()) {
                RFC2965Spec.LOG.debug((Object)("Unrecognized cookie attribute: " + attribute.toString()));
            }
        }
        else {
            handler.parse(cookie, paramValue);
        }
    }
    
    public void validate(final String host, final int port, final String path, final boolean secure, final Cookie cookie) throws MalformedCookieException {
        RFC2965Spec.LOG.trace((Object)"enter RFC2965Spec.validate(String, int, String, boolean, Cookie)");
        if (cookie instanceof Cookie2) {
            if (cookie.getName().indexOf(32) != -1) {
                throw new MalformedCookieException("Cookie name may not contain blanks");
            }
            if (cookie.getName().startsWith("$")) {
                throw new MalformedCookieException("Cookie name may not start with $");
            }
            final CookieOrigin origin = new CookieOrigin(getEffectiveHost(host), port, path, secure);
            final Iterator i = this.getAttribHandlerIterator();
            while (i.hasNext()) {
                final CookieAttributeHandler handler = i.next();
                handler.validate(cookie, origin);
            }
        }
        else {
            this.rfc2109.validate(host, port, path, secure, cookie);
        }
    }
    
    public boolean match(final String host, final int port, final String path, final boolean secure, final Cookie cookie) {
        RFC2965Spec.LOG.trace((Object)"enter RFC2965.match(String, int, String, boolean, Cookie");
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        if (!(cookie instanceof Cookie2)) {
            return this.rfc2109.match(host, port, path, secure, cookie);
        }
        if (cookie.isPersistent() && cookie.isExpired()) {
            return false;
        }
        final CookieOrigin origin = new CookieOrigin(getEffectiveHost(host), port, path, secure);
        final Iterator i = this.getAttribHandlerIterator();
        while (i.hasNext()) {
            final CookieAttributeHandler handler = i.next();
            if (!handler.match(cookie, origin)) {
                return false;
            }
        }
        return true;
    }
    
    private void doFormatCookie2(final Cookie2 cookie, final StringBuffer buffer) {
        final String name = cookie.getName();
        String value = cookie.getValue();
        if (value == null) {
            value = "";
        }
        this.formatter.format(buffer, new NameValuePair(name, value));
        if (cookie.getDomain() != null && cookie.isDomainAttributeSpecified()) {
            buffer.append("; ");
            this.formatter.format(buffer, new NameValuePair("$Domain", cookie.getDomain()));
        }
        if (cookie.getPath() != null && cookie.isPathAttributeSpecified()) {
            buffer.append("; ");
            this.formatter.format(buffer, new NameValuePair("$Path", cookie.getPath()));
        }
        if (cookie.isPortAttributeSpecified()) {
            String portValue = "";
            if (!cookie.isPortAttributeBlank()) {
                portValue = this.createPortAttribute(cookie.getPorts());
            }
            buffer.append("; ");
            this.formatter.format(buffer, new NameValuePair("$Port", portValue));
        }
    }
    
    public String formatCookie(final Cookie cookie) {
        RFC2965Spec.LOG.trace((Object)"enter RFC2965Spec.formatCookie(Cookie)");
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        if (cookie instanceof Cookie2) {
            final Cookie2 cookie2 = (Cookie2)cookie;
            final int version = cookie2.getVersion();
            final StringBuffer buffer = new StringBuffer();
            this.formatter.format(buffer, new NameValuePair("$Version", Integer.toString(version)));
            buffer.append("; ");
            this.doFormatCookie2(cookie2, buffer);
            return buffer.toString();
        }
        return this.rfc2109.formatCookie(cookie);
    }
    
    public String formatCookies(final Cookie[] cookies) {
        RFC2965Spec.LOG.trace((Object)"enter RFC2965Spec.formatCookieHeader(Cookie[])");
        if (cookies == null) {
            throw new IllegalArgumentException("Cookies may not be null");
        }
        boolean hasOldStyleCookie = false;
        int version = -1;
        for (int i = 0; i < cookies.length; ++i) {
            final Cookie cookie = cookies[i];
            if (!(cookie instanceof Cookie2)) {
                hasOldStyleCookie = true;
                break;
            }
            if (cookie.getVersion() > version) {
                version = cookie.getVersion();
            }
        }
        if (version < 0) {
            version = 0;
        }
        if (hasOldStyleCookie || version < 1) {
            return this.rfc2109.formatCookies(cookies);
        }
        Arrays.sort(cookies, RFC2965Spec.PATH_COMPOARATOR);
        final StringBuffer buffer = new StringBuffer();
        this.formatter.format(buffer, new NameValuePair("$Version", Integer.toString(version)));
        for (int j = 0; j < cookies.length; ++j) {
            buffer.append("; ");
            final Cookie2 cookie2 = (Cookie2)cookies[j];
            this.doFormatCookie2(cookie2, buffer);
        }
        return buffer.toString();
    }
    
    private String createPortAttribute(final int[] ports) {
        final StringBuffer portValue = new StringBuffer();
        for (int i = 0, len = ports.length; i < len; ++i) {
            if (i > 0) {
                portValue.append(",");
            }
            portValue.append(ports[i]);
        }
        return portValue.toString();
    }
    
    private int[] parsePortAttribute(final String portValue) throws MalformedCookieException {
        final StringTokenizer st = new StringTokenizer(portValue, ",");
        final int[] ports = new int[st.countTokens()];
        try {
            int i = 0;
            while (st.hasMoreTokens()) {
                ports[i] = Integer.parseInt(st.nextToken().trim());
                if (ports[i] < 0) {
                    throw new MalformedCookieException("Invalid Port attribute.");
                }
                ++i;
            }
        }
        catch (final NumberFormatException e) {
            throw new MalformedCookieException("Invalid Port attribute: " + e.getMessage());
        }
        return ports;
    }
    
    private static String getEffectiveHost(final String host) {
        String effectiveHost = host.toLowerCase();
        if (host.indexOf(46) < 0) {
            effectiveHost += ".local";
        }
        return effectiveHost;
    }
    
    public boolean domainMatch(final String host, final String domain) {
        final boolean match = host.equals(domain) || (domain.startsWith(".") && host.endsWith(domain));
        return match;
    }
    
    private boolean portMatch(final int port, final int[] ports) {
        boolean portInList = false;
        for (int i = 0, len = ports.length; i < len; ++i) {
            if (port == ports[i]) {
                portInList = true;
                break;
            }
        }
        return portInList;
    }
    
    public int getVersion() {
        return 1;
    }
    
    public Header getVersionHeader() {
        final ParameterFormatter formatter = new ParameterFormatter();
        final StringBuffer buffer = new StringBuffer();
        formatter.format(buffer, new NameValuePair("$Version", Integer.toString(this.getVersion())));
        return new Header("Cookie2", buffer.toString(), true);
    }
    
    static {
        PATH_COMPOARATOR = new CookiePathComparator();
    }
    
    private class Cookie2PathAttributeHandler implements CookieAttributeHandler
    {
        public void parse(final Cookie cookie, final String path) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (path == null) {
                throw new MalformedCookieException("Missing value for path attribute");
            }
            if (path.trim().equals("")) {
                throw new MalformedCookieException("Blank value for path attribute");
            }
            cookie.setPath(path);
            cookie.setPathAttributeSpecified(true);
        }
        
        public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            String path = origin.getPath();
            if (path == null) {
                throw new IllegalArgumentException("Path of origin host may not be null.");
            }
            if (cookie.getPath() == null) {
                throw new MalformedCookieException("Invalid cookie state: path attribute is null.");
            }
            if (path.trim().equals("")) {
                path = "/";
            }
            if (!RFC2965Spec.this.pathMatch(path, cookie.getPath())) {
                throw new MalformedCookieException("Illegal path attribute \"" + cookie.getPath() + "\". Path of origin: \"" + path + "\"");
            }
        }
        
        public boolean match(final Cookie cookie, final CookieOrigin origin) {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            String path = origin.getPath();
            if (cookie.getPath() == null) {
                CookieSpecBase.LOG.warn((Object)"Invalid cookie state: path attribute is null.");
                return false;
            }
            if (path.trim().equals("")) {
                path = "/";
            }
            return RFC2965Spec.this.pathMatch(path, cookie.getPath());
        }
    }
    
    private class Cookie2DomainAttributeHandler implements CookieAttributeHandler
    {
        public void parse(final Cookie cookie, String domain) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (domain == null) {
                throw new MalformedCookieException("Missing value for domain attribute");
            }
            if (domain.trim().equals("")) {
                throw new MalformedCookieException("Blank value for domain attribute");
            }
            domain = domain.toLowerCase();
            if (!domain.startsWith(".")) {
                domain = "." + domain;
            }
            cookie.setDomain(domain);
            cookie.setDomainAttributeSpecified(true);
        }
        
        public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            final String host = origin.getHost().toLowerCase();
            if (cookie.getDomain() == null) {
                throw new MalformedCookieException("Invalid cookie state: domain not specified");
            }
            final String cookieDomain = cookie.getDomain().toLowerCase();
            if (cookie.isDomainAttributeSpecified()) {
                if (!cookieDomain.startsWith(".")) {
                    throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2109: domain must start with a dot");
                }
                final int dotIndex = cookieDomain.indexOf(46, 1);
                if ((dotIndex < 0 || dotIndex == cookieDomain.length() - 1) && !cookieDomain.equals(".local")) {
                    throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2965: the value contains no embedded dots " + "and the value is not .local");
                }
                if (!RFC2965Spec.this.domainMatch(host, cookieDomain)) {
                    throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2965: effective host name does not " + "domain-match domain attribute.");
                }
                final String effectiveHostWithoutDomain = host.substring(0, host.length() - cookieDomain.length());
                if (effectiveHostWithoutDomain.indexOf(46) != -1) {
                    throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2965: " + "effective host minus domain may not contain any dots");
                }
            }
            else if (!cookie.getDomain().equals(host)) {
                throw new MalformedCookieException("Illegal domain attribute: \"" + cookie.getDomain() + "\"." + "Domain of origin: \"" + host + "\"");
            }
        }
        
        public boolean match(final Cookie cookie, final CookieOrigin origin) {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            final String host = origin.getHost().toLowerCase();
            final String cookieDomain = cookie.getDomain();
            if (!RFC2965Spec.this.domainMatch(host, cookieDomain)) {
                return false;
            }
            final String effectiveHostWithoutDomain = host.substring(0, host.length() - cookieDomain.length());
            return effectiveHostWithoutDomain.indexOf(46) == -1;
        }
    }
    
    private class Cookie2PortAttributeHandler implements CookieAttributeHandler
    {
        public void parse(final Cookie cookie, final String portValue) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (cookie instanceof Cookie2) {
                final Cookie2 cookie2 = (Cookie2)cookie;
                if (portValue == null || portValue.trim().equals("")) {
                    cookie2.setPortAttributeBlank(true);
                }
                else {
                    final int[] ports = RFC2965Spec.this.parsePortAttribute(portValue);
                    cookie2.setPorts(ports);
                }
                cookie2.setPortAttributeSpecified(true);
            }
        }
        
        public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            if (cookie instanceof Cookie2) {
                final Cookie2 cookie2 = (Cookie2)cookie;
                final int port = origin.getPort();
                if (cookie2.isPortAttributeSpecified() && !RFC2965Spec.this.portMatch(port, cookie2.getPorts())) {
                    throw new MalformedCookieException("Port attribute violates RFC 2965: Request port not found in cookie's port list.");
                }
            }
        }
        
        public boolean match(final Cookie cookie, final CookieOrigin origin) {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            if (cookie instanceof Cookie2) {
                final Cookie2 cookie2 = (Cookie2)cookie;
                final int port = origin.getPort();
                if (cookie2.isPortAttributeSpecified()) {
                    if (cookie2.getPorts() == null) {
                        CookieSpecBase.LOG.warn((Object)"Invalid cookie state: port not specified");
                        return false;
                    }
                    if (!RFC2965Spec.this.portMatch(port, cookie2.getPorts())) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }
    
    private class Cookie2MaxageAttributeHandler implements CookieAttributeHandler
    {
        public void parse(final Cookie cookie, final String value) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (value == null) {
                throw new MalformedCookieException("Missing value for max-age attribute");
            }
            int age = -1;
            try {
                age = Integer.parseInt(value);
            }
            catch (final NumberFormatException e) {
                age = -1;
            }
            if (age < 0) {
                throw new MalformedCookieException("Invalid max-age attribute.");
            }
            cookie.setExpiryDate(new Date(System.currentTimeMillis() + age * 1000L));
        }
        
        public void validate(final Cookie cookie, final CookieOrigin origin) {
        }
        
        public boolean match(final Cookie cookie, final CookieOrigin origin) {
            return true;
        }
    }
    
    private class CookieSecureAttributeHandler implements CookieAttributeHandler
    {
        public void parse(final Cookie cookie, final String secure) throws MalformedCookieException {
            cookie.setSecure(true);
        }
        
        public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        }
        
        public boolean match(final Cookie cookie, final CookieOrigin origin) {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (origin == null) {
                throw new IllegalArgumentException("Cookie origin may not be null");
            }
            return cookie.getSecure() == origin.isSecure();
        }
    }
    
    private class CookieCommentAttributeHandler implements CookieAttributeHandler
    {
        public void parse(final Cookie cookie, final String comment) throws MalformedCookieException {
            cookie.setComment(comment);
        }
        
        public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        }
        
        public boolean match(final Cookie cookie, final CookieOrigin origin) {
            return true;
        }
    }
    
    private class CookieCommentUrlAttributeHandler implements CookieAttributeHandler
    {
        public void parse(final Cookie cookie, final String commenturl) throws MalformedCookieException {
            if (cookie instanceof Cookie2) {
                final Cookie2 cookie2 = (Cookie2)cookie;
                cookie2.setCommentURL(commenturl);
            }
        }
        
        public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        }
        
        public boolean match(final Cookie cookie, final CookieOrigin origin) {
            return true;
        }
    }
    
    private class CookieDiscardAttributeHandler implements CookieAttributeHandler
    {
        public void parse(final Cookie cookie, final String commenturl) throws MalformedCookieException {
            if (cookie instanceof Cookie2) {
                final Cookie2 cookie2 = (Cookie2)cookie;
                cookie2.setDiscard(true);
            }
        }
        
        public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        }
        
        public boolean match(final Cookie cookie, final CookieOrigin origin) {
            return true;
        }
    }
    
    private class Cookie2VersionAttributeHandler implements CookieAttributeHandler
    {
        public void parse(final Cookie cookie, final String value) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (cookie instanceof Cookie2) {
                final Cookie2 cookie2 = (Cookie2)cookie;
                if (value == null) {
                    throw new MalformedCookieException("Missing value for version attribute");
                }
                int version = -1;
                try {
                    version = Integer.parseInt(value);
                }
                catch (final NumberFormatException e) {
                    version = -1;
                }
                if (version < 0) {
                    throw new MalformedCookieException("Invalid cookie version.");
                }
                cookie2.setVersion(version);
                cookie2.setVersionAttributeSpecified(true);
            }
        }
        
        public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
            if (cookie == null) {
                throw new IllegalArgumentException("Cookie may not be null");
            }
            if (cookie instanceof Cookie2) {
                final Cookie2 cookie2 = (Cookie2)cookie;
                if (!cookie2.isVersionAttributeSpecified()) {
                    throw new MalformedCookieException("Violates RFC 2965. Version attribute is required.");
                }
            }
        }
        
        public boolean match(final Cookie cookie, final CookieOrigin origin) {
            return true;
        }
    }
}
