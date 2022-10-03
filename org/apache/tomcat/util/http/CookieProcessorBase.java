package org.apache.tomcat.util.http;

import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import java.text.DateFormat;

public abstract class CookieProcessorBase implements CookieProcessor
{
    private static final String COOKIE_DATE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";
    protected static final ThreadLocal<DateFormat> COOKIE_DATE_FORMAT;
    protected static final String ANCIENT_DATE;
    private SameSiteCookies sameSiteCookies;
    
    public CookieProcessorBase() {
        this.sameSiteCookies = SameSiteCookies.UNSET;
    }
    
    public SameSiteCookies getSameSiteCookies() {
        return this.sameSiteCookies;
    }
    
    public void setSameSiteCookies(final String sameSiteCookies) {
        this.sameSiteCookies = SameSiteCookies.fromString(sameSiteCookies);
    }
    
    @Deprecated
    @Override
    public String generateHeader(final Cookie cookie, final HttpServletRequest request) {
        return this.generateHeader(cookie);
    }
    
    static {
        COOKIE_DATE_FORMAT = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                final DateFormat df = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.US);
                df.setTimeZone(TimeZone.getTimeZone("GMT"));
                return df;
            }
        };
        ANCIENT_DATE = CookieProcessorBase.COOKIE_DATE_FORMAT.get().format(new Date(10000L));
    }
}
