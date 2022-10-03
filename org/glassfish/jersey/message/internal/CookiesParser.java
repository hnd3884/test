package org.glassfish.jersey.message.internal;

import java.util.Date;
import java.text.ParseException;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.logging.Level;
import javax.ws.rs.core.NewCookie;
import java.util.LinkedHashMap;
import javax.ws.rs.core.Cookie;
import java.util.Map;
import java.util.logging.Logger;

public class CookiesParser
{
    private static final Logger LOGGER;
    
    public static Map<String, Cookie> parseCookies(final String header) {
        final String[] bites = header.split("[;,]");
        final Map<String, Cookie> cookies = new LinkedHashMap<String, Cookie>();
        int version = 0;
        MutableCookie cookie = null;
        for (final String bite : bites) {
            final String[] crumbs = bite.split("=", 2);
            final String name = (crumbs.length > 0) ? crumbs[0].trim() : "";
            String value = (crumbs.length > 1) ? crumbs[1].trim() : "";
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
                value = value.substring(1, value.length() - 1);
            }
            if (!name.startsWith("$")) {
                if (cookie != null) {
                    cookies.put(cookie.name, cookie.getImmutableCookie());
                }
                cookie = new MutableCookie(name, value);
                cookie.version = version;
            }
            else if (name.startsWith("$Version")) {
                version = Integer.parseInt(value);
            }
            else if (name.startsWith("$Path") && cookie != null) {
                cookie.path = value;
            }
            else if (name.startsWith("$Domain") && cookie != null) {
                cookie.domain = value;
            }
        }
        if (cookie != null) {
            cookies.put(cookie.name, cookie.getImmutableCookie());
        }
        return cookies;
    }
    
    public static Cookie parseCookie(final String header) {
        final Map<String, Cookie> cookies = parseCookies(header);
        return (Cookie)cookies.entrySet().iterator().next().getValue();
    }
    
    public static NewCookie parseNewCookie(final String header) {
        final String[] bites = header.split("[;,]");
        MutableNewCookie cookie = null;
        for (int i = 0; i < bites.length; ++i) {
            final String[] crumbs = bites[i].split("=", 2);
            final String name = (crumbs.length > 0) ? crumbs[0].trim() : "";
            String value = (crumbs.length > 1) ? crumbs[1].trim() : "";
            if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
                value = value.substring(1, value.length() - 1);
            }
            if (cookie == null) {
                cookie = new MutableNewCookie(name, value);
            }
            else {
                final String param = name.toLowerCase();
                if (param.startsWith("comment")) {
                    cookie.comment = value;
                }
                else if (param.startsWith("domain")) {
                    cookie.domain = value;
                }
                else if (param.startsWith("max-age")) {
                    cookie.maxAge = Integer.parseInt(value);
                }
                else if (param.startsWith("path")) {
                    cookie.path = value;
                }
                else if (param.startsWith("secure")) {
                    cookie.secure = true;
                }
                else if (param.startsWith("version")) {
                    cookie.version = Integer.parseInt(value);
                }
                else if (param.startsWith("domain")) {
                    cookie.domain = value;
                }
                else if (param.startsWith("httponly")) {
                    cookie.httpOnly = true;
                }
                else if (param.startsWith("expires")) {
                    try {
                        cookie.expiry = HttpDateFormat.readDate(value + ", " + bites[++i]);
                    }
                    catch (final ParseException e) {
                        CookiesParser.LOGGER.log(Level.FINE, LocalizationMessages.ERROR_NEWCOOKIE_EXPIRES(value), e);
                    }
                }
            }
        }
        return cookie.getImmutableNewCookie();
    }
    
    private CookiesParser() {
    }
    
    static {
        LOGGER = Logger.getLogger(CookiesParser.class.getName());
    }
    
    private static class MutableCookie
    {
        String name;
        String value;
        int version;
        String path;
        String domain;
        
        public MutableCookie(final String name, final String value) {
            this.version = 1;
            this.path = null;
            this.domain = null;
            this.name = name;
            this.value = value;
        }
        
        public Cookie getImmutableCookie() {
            return new Cookie(this.name, this.value, this.path, this.domain, this.version);
        }
    }
    
    private static class MutableNewCookie
    {
        String name;
        String value;
        String path;
        String domain;
        int version;
        String comment;
        int maxAge;
        boolean secure;
        boolean httpOnly;
        Date expiry;
        
        public MutableNewCookie(final String name, final String value) {
            this.name = null;
            this.value = null;
            this.path = null;
            this.domain = null;
            this.version = 1;
            this.comment = null;
            this.maxAge = -1;
            this.secure = false;
            this.httpOnly = false;
            this.expiry = null;
            this.name = name;
            this.value = value;
        }
        
        public NewCookie getImmutableNewCookie() {
            return new NewCookie(this.name, this.value, this.path, this.domain, this.version, this.comment, this.maxAge, this.expiry, this.secure, this.httpOnly);
        }
    }
}
