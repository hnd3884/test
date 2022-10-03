package org.apache.commons.httpclient.cookie;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.util.ParameterFormatter;

public class RFC2109Spec extends CookieSpecBase
{
    private final ParameterFormatter formatter;
    public static final String SET_COOKIE_KEY = "set-cookie";
    
    public RFC2109Spec() {
        (this.formatter = new ParameterFormatter()).setAlwaysUseQuotes(true);
    }
    
    public void parseAttribute(final NameValuePair attribute, final Cookie cookie) throws MalformedCookieException {
        if (attribute == null) {
            throw new IllegalArgumentException("Attribute may not be null.");
        }
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null.");
        }
        final String paramName = attribute.getName().toLowerCase();
        final String paramValue = attribute.getValue();
        if (paramName.equals("path")) {
            if (paramValue == null) {
                throw new MalformedCookieException("Missing value for path attribute");
            }
            if (paramValue.trim().equals("")) {
                throw new MalformedCookieException("Blank value for path attribute");
            }
            cookie.setPath(paramValue);
            cookie.setPathAttributeSpecified(true);
        }
        else {
            if (paramName.equals("version")) {
                if (paramValue == null) {
                    throw new MalformedCookieException("Missing value for version attribute");
                }
                try {
                    cookie.setVersion(Integer.parseInt(paramValue));
                    return;
                }
                catch (final NumberFormatException e) {
                    throw new MalformedCookieException("Invalid version: " + e.getMessage());
                }
            }
            super.parseAttribute(attribute, cookie);
        }
    }
    
    public void validate(String host, final int port, final String path, final boolean secure, final Cookie cookie) throws MalformedCookieException {
        RFC2109Spec.LOG.trace((Object)"enter RFC2109Spec.validate(String, int, String, boolean, Cookie)");
        super.validate(host, port, path, secure, cookie);
        if (cookie.getName().indexOf(32) != -1) {
            throw new MalformedCookieException("Cookie name may not contain blanks");
        }
        if (cookie.getName().startsWith("$")) {
            throw new MalformedCookieException("Cookie name may not start with $");
        }
        if (cookie.isDomainAttributeSpecified() && !cookie.getDomain().equals(host)) {
            if (!cookie.getDomain().startsWith(".")) {
                throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2109: domain must start with a dot");
            }
            final int dotIndex = cookie.getDomain().indexOf(46, 1);
            if (dotIndex < 0 || dotIndex == cookie.getDomain().length() - 1) {
                throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2109: domain must contain an embedded dot");
            }
            host = host.toLowerCase();
            if (!host.endsWith(cookie.getDomain())) {
                throw new MalformedCookieException("Illegal domain attribute \"" + cookie.getDomain() + "\". Domain of origin: \"" + host + "\"");
            }
            final String hostWithoutDomain = host.substring(0, host.length() - cookie.getDomain().length());
            if (hostWithoutDomain.indexOf(46) != -1) {
                throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2109: host minus domain may not contain any dots");
            }
        }
    }
    
    public boolean domainMatch(final String host, final String domain) {
        final boolean match = host.equals(domain) || (domain.startsWith(".") && host.endsWith(domain));
        return match;
    }
    
    private void formatParam(final StringBuffer buffer, final NameValuePair param, final int version) {
        if (version < 1) {
            buffer.append(param.getName());
            buffer.append("=");
            if (param.getValue() != null) {
                buffer.append(param.getValue());
            }
        }
        else {
            this.formatter.format(buffer, param);
        }
    }
    
    private void formatCookieAsVer(final StringBuffer buffer, final Cookie cookie, final int version) {
        String value = cookie.getValue();
        if (value == null) {
            value = "";
        }
        this.formatParam(buffer, new NameValuePair(cookie.getName(), value), version);
        if (cookie.getPath() != null && cookie.isPathAttributeSpecified()) {
            buffer.append("; ");
            this.formatParam(buffer, new NameValuePair("$Path", cookie.getPath()), version);
        }
        if (cookie.getDomain() != null && cookie.isDomainAttributeSpecified()) {
            buffer.append("; ");
            this.formatParam(buffer, new NameValuePair("$Domain", cookie.getDomain()), version);
        }
    }
    
    public String formatCookie(final Cookie cookie) {
        RFC2109Spec.LOG.trace((Object)"enter RFC2109Spec.formatCookie(Cookie)");
        if (cookie == null) {
            throw new IllegalArgumentException("Cookie may not be null");
        }
        final int version = cookie.getVersion();
        final StringBuffer buffer = new StringBuffer();
        this.formatParam(buffer, new NameValuePair("$Version", Integer.toString(version)), version);
        buffer.append("; ");
        this.formatCookieAsVer(buffer, cookie, version);
        return buffer.toString();
    }
    
    public String formatCookies(final Cookie[] cookies) {
        RFC2109Spec.LOG.trace((Object)"enter RFC2109Spec.formatCookieHeader(Cookie[])");
        int version = Integer.MAX_VALUE;
        for (int i = 0; i < cookies.length; ++i) {
            final Cookie cookie = cookies[i];
            if (cookie.getVersion() < version) {
                version = cookie.getVersion();
            }
        }
        final StringBuffer buffer = new StringBuffer();
        this.formatParam(buffer, new NameValuePair("$Version", Integer.toString(version)), version);
        for (int j = 0; j < cookies.length; ++j) {
            buffer.append("; ");
            this.formatCookieAsVer(buffer, cookies[j], version);
        }
        return buffer.toString();
    }
}
