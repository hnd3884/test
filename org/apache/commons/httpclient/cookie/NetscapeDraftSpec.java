package org.apache.commons.httpclient.cookie;

import java.util.StringTokenizer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.commons.httpclient.NameValuePair;
import java.util.Date;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.Cookie;

public class NetscapeDraftSpec extends CookieSpecBase
{
    public Cookie[] parse(String host, final int port, String path, final boolean secure, final String header) throws MalformedCookieException {
        NetscapeDraftSpec.LOG.trace((Object)"enter NetscapeDraftSpec.parse(String, port, path, boolean, Header)");
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
        host = host.toLowerCase();
        String defaultPath = path;
        int lastSlashIndex = defaultPath.lastIndexOf("/");
        if (lastSlashIndex >= 0) {
            if (lastSlashIndex == 0) {
                lastSlashIndex = 1;
            }
            defaultPath = defaultPath.substring(0, lastSlashIndex);
        }
        final HeaderElement headerelement = new HeaderElement(header.toCharArray());
        final Cookie cookie = new Cookie(host, headerelement.getName(), headerelement.getValue(), defaultPath, null, false);
        final NameValuePair[] parameters = headerelement.getParameters();
        if (parameters != null) {
            for (int j = 0; j < parameters.length; ++j) {
                this.parseAttribute(parameters[j], cookie);
            }
        }
        return new Cookie[] { cookie };
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
        if (paramName.equals("expires")) {
            if (paramValue == null) {
                throw new MalformedCookieException("Missing value for expires attribute");
            }
            try {
                final DateFormat expiryFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.US);
                final Date date = expiryFormat.parse(paramValue);
                cookie.setExpiryDate(date);
                return;
            }
            catch (final ParseException e) {
                throw new MalformedCookieException("Invalid expires attribute: " + e.getMessage());
            }
        }
        super.parseAttribute(attribute, cookie);
    }
    
    public boolean domainMatch(final String host, final String domain) {
        return host.endsWith(domain);
    }
    
    public void validate(final String host, final int port, final String path, final boolean secure, final Cookie cookie) throws MalformedCookieException {
        NetscapeDraftSpec.LOG.trace((Object)"enterNetscapeDraftCookieProcessor RCF2109CookieProcessor.validate(Cookie)");
        super.validate(host, port, path, secure, cookie);
        if (host.indexOf(".") >= 0) {
            final int domainParts = new StringTokenizer(cookie.getDomain(), ".").countTokens();
            if (isSpecialDomain(cookie.getDomain())) {
                if (domainParts < 2) {
                    throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates the Netscape cookie specification for " + "special domains");
                }
            }
            else if (domainParts < 3) {
                throw new MalformedCookieException("Domain attribute \"" + cookie.getDomain() + "\" violates the Netscape cookie specification");
            }
        }
    }
    
    private static boolean isSpecialDomain(final String domain) {
        final String ucDomain = domain.toUpperCase();
        return ucDomain.endsWith(".COM") || ucDomain.endsWith(".EDU") || ucDomain.endsWith(".NET") || ucDomain.endsWith(".GOV") || ucDomain.endsWith(".MIL") || ucDomain.endsWith(".ORG") || ucDomain.endsWith(".INT");
    }
}
