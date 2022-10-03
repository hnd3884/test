package org.apache.commons.httpclient.cookie;

import java.util.Collection;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.Cookie;

public interface CookieSpec
{
    public static final String PATH_DELIM = "/";
    public static final char PATH_DELIM_CHAR = "/".charAt(0);
    
    Cookie[] parse(final String p0, final int p1, final String p2, final boolean p3, final String p4) throws MalformedCookieException, IllegalArgumentException;
    
    Cookie[] parse(final String p0, final int p1, final String p2, final boolean p3, final Header p4) throws MalformedCookieException, IllegalArgumentException;
    
    void parseAttribute(final NameValuePair p0, final Cookie p1) throws MalformedCookieException, IllegalArgumentException;
    
    void validate(final String p0, final int p1, final String p2, final boolean p3, final Cookie p4) throws MalformedCookieException, IllegalArgumentException;
    
    void setValidDateFormats(final Collection p0);
    
    Collection getValidDateFormats();
    
    boolean match(final String p0, final int p1, final String p2, final boolean p3, final Cookie p4);
    
    Cookie[] match(final String p0, final int p1, final String p2, final boolean p3, final Cookie[] p4);
    
    boolean domainMatch(final String p0, final String p1);
    
    boolean pathMatch(final String p0, final String p1);
    
    String formatCookie(final Cookie p0);
    
    String formatCookies(final Cookie[] p0) throws IllegalArgumentException;
    
    Header formatCookieHeader(final Cookie[] p0) throws IllegalArgumentException;
    
    Header formatCookieHeader(final Cookie p0) throws IllegalArgumentException;
}
