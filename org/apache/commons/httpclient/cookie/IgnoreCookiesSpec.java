package org.apache.commons.httpclient.cookie;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.Header;
import java.util.Collection;
import org.apache.commons.httpclient.Cookie;

public class IgnoreCookiesSpec implements CookieSpec
{
    public Cookie[] parse(final String host, final int port, final String path, final boolean secure, final String header) throws MalformedCookieException {
        return new Cookie[0];
    }
    
    public Collection getValidDateFormats() {
        return null;
    }
    
    public void setValidDateFormats(final Collection datepatterns) {
    }
    
    public String formatCookie(final Cookie cookie) {
        return null;
    }
    
    public Header formatCookieHeader(final Cookie cookie) throws IllegalArgumentException {
        return null;
    }
    
    public Header formatCookieHeader(final Cookie[] cookies) throws IllegalArgumentException {
        return null;
    }
    
    public String formatCookies(final Cookie[] cookies) throws IllegalArgumentException {
        return null;
    }
    
    public boolean match(final String host, final int port, final String path, final boolean secure, final Cookie cookie) {
        return false;
    }
    
    public Cookie[] match(final String host, final int port, final String path, final boolean secure, final Cookie[] cookies) {
        return new Cookie[0];
    }
    
    public Cookie[] parse(final String host, final int port, final String path, final boolean secure, final Header header) throws MalformedCookieException, IllegalArgumentException {
        return new Cookie[0];
    }
    
    public void parseAttribute(final NameValuePair attribute, final Cookie cookie) throws MalformedCookieException, IllegalArgumentException {
    }
    
    public void validate(final String host, final int port, final String path, final boolean secure, final Cookie cookie) throws MalformedCookieException, IllegalArgumentException {
    }
    
    public boolean domainMatch(final String host, final String domain) {
        return false;
    }
    
    public boolean pathMatch(final String path, final String topmostPath) {
        return false;
    }
}
