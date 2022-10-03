package org.apache.tomcat.util.http;

import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;

public interface CookieProcessor
{
    void parseCookieHeader(final MimeHeaders p0, final ServerCookies p1);
    
    @Deprecated
    String generateHeader(final Cookie p0);
    
    String generateHeader(final Cookie p0, final HttpServletRequest p1);
    
    Charset getCharset();
}
