package org.apache.commons.httpclient.cookie;

import org.apache.commons.httpclient.Cookie;

public interface CookieAttributeHandler
{
    void parse(final Cookie p0, final String p1) throws MalformedCookieException;
    
    void validate(final Cookie p0, final CookieOrigin p1) throws MalformedCookieException;
    
    boolean match(final Cookie p0, final CookieOrigin p1);
}
