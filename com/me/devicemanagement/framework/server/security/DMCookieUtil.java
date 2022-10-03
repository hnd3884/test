package com.me.devicemanagement.framework.server.security;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.List;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Properties;
import java.util.logging.Logger;

public class DMCookieUtil
{
    private static Logger logger;
    private static Properties securityProps;
    
    public static Cookie generateDMCookies(final HttpServletRequest request, final String cookieName, final String cookieValue) {
        final Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setSecure(request.isSecure());
        return cookie;
    }
    
    public static void setCookieAttributes(final HttpServletRequest request, final HttpServletResponse response) {
        final Cookie[] cookies = request.getCookies();
        List<String> httpCookieList = null;
        List<String> secureCookieList = null;
        if (DMCookieUtil.securityProps != null && DMCookieUtil.securityProps.containsKey("httponly_cookies")) {
            final String httpOnlyCookies = DMCookieUtil.securityProps.getProperty("httponly_cookies");
            httpCookieList = Arrays.asList(httpOnlyCookies.split(","));
        }
        if (DMCookieUtil.securityProps != null && DMCookieUtil.securityProps.containsKey("secure_cookies")) {
            final String secureCookies = DMCookieUtil.securityProps.getProperty("secure_cookies");
            secureCookieList = Arrays.asList(secureCookies.split(","));
        }
        if (cookies != null) {
            for (int index = 0; index < cookies.length; ++index) {
                final Cookie currentCookie = cookies[index];
                Boolean isCookieUpdated = false;
                if (secureCookieList != null && request.isSecure() && !currentCookie.getSecure() && secureCookieList.contains(currentCookie.getName())) {
                    currentCookie.setSecure(true);
                    isCookieUpdated = true;
                }
                if (httpCookieList != null && httpCookieList.contains(currentCookie.getName()) && !currentCookie.isHttpOnly()) {
                    currentCookie.setHttpOnly(true);
                    isCookieUpdated = true;
                }
                if (isCookieUpdated) {
                    if (currentCookie.getPath() == null || SyMUtil.isStringEmpty(currentCookie.getPath())) {
                        currentCookie.setPath("/");
                    }
                    response.addCookie(currentCookie);
                }
            }
        }
    }
    
    static {
        DMCookieUtil.logger = Logger.getLogger(DMCookieUtil.class.getName());
        DMCookieUtil.securityProps = null;
        final String securityPropfile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "security_properties.conf";
        try {
            DMCookieUtil.securityProps = FileAccessUtil.readProperties(securityPropfile);
        }
        catch (final Exception ex) {
            DMCookieUtil.logger.log(Level.SEVERE, "Exception while loading security_properties {0}", ex);
        }
    }
}
