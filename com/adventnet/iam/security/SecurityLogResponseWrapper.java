package com.adventnet.iam.security;

import java.util.logging.Level;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponseWrapper;

public class SecurityLogResponseWrapper extends HttpServletResponseWrapper
{
    private static final Logger LOGGER;
    private static final List<String> SET_COOKIE_HEADERS;
    private static final String MASKED_VALUE = "*****";
    List<Map<String, Object>> cookiesList;
    private List<String> secretHeaderNames;
    
    public SecurityLogResponseWrapper(final HttpServletResponse response) {
        super(response);
        this.parseSetCookieHeader();
    }
    
    public String getHeader(final String name) {
        return this.isSecretHeader(name) ? "*****" : super.getHeader(name);
    }
    
    public Collection<String> getHeaders(final String name) {
        return this.isSecretHeader(name) ? Arrays.asList("*****") : super.getHeaders(name);
    }
    
    private boolean isSecretHeader(final String name) {
        return SecurityLogResponseWrapper.SET_COOKIE_HEADERS.contains(name) || (this.secretHeaderNames != null && this.secretHeaderNames.contains(name));
    }
    
    void addSecretHeader(final String name) {
        if (this.secretHeaderNames == null) {
            this.secretHeaderNames = new ArrayList<String>();
        }
        this.secretHeaderNames.add(name);
    }
    
    private void parseSetCookieHeader() {
        for (final String cookieHeaderName : SecurityLogResponseWrapper.SET_COOKIE_HEADERS) {
            final Collection<String> cookies = super.getHeaders(cookieHeaderName);
            if (cookies.size() > 0) {
                this.cookiesList = ((this.cookiesList == null) ? new ArrayList<Map<String, Object>>() : this.cookiesList);
                for (final String cookie : cookies) {
                    this.cookiesList.add(parse(cookie));
                }
            }
        }
    }
    
    private static Map<String, Object> parse(final String cookie) {
        final Map<String, Object> attributesMap = new HashMap<String, Object>();
        final String[] values = cookie.split(";");
        final String cookieNameValuePair = values[0];
        final int index = cookieNameValuePair.indexOf("=");
        if (index == -1) {
            SecurityLogResponseWrapper.LOGGER.log(Level.SEVERE, "Invalid cookie name-value pair, so ignore logging of cookie name");
        }
        else {
            final String cookieName = cookieNameValuePair.substring(0, index).trim();
            if ("".equals(cookieName)) {
                SecurityLogResponseWrapper.LOGGER.log(Level.SEVERE, "Cookie name is empty, so ignore logging of cookie name");
            }
            else {
                attributesMap.put("name", cookieName);
            }
        }
        for (int i = 1; i < values.length; ++i) {
            final String attrNameValuePair = values[i];
            final int attrIndex = attrNameValuePair.indexOf("=");
            String attrValue = null;
            String attrName;
            if (attrIndex != -1) {
                attrName = attrNameValuePair.substring(0, attrIndex).trim().toLowerCase();
                attrValue = attrNameValuePair.substring(attrIndex + 1).trim();
            }
            else {
                attrName = attrNameValuePair.trim().toLowerCase();
            }
            if ("path".equals(attrName)) {
                attrValue = ((attrValue != null && attrValue.length() > 5) ? (attrValue.substring(0, 5) + "***") : attrValue);
                attributesMap.put(attrName, attrValue);
            }
            else if ("expires".equals(attrName) || "domain".equals(attrName) || "max-age".equals(attrName) || "priority".equals(attrName) || "version".equals(attrName) || "samesite".equals(attrName)) {
                attributesMap.put(attrName, attrValue);
            }
            else if ("secure".equals(attrName) || "httponly".equals(attrName)) {
                attributesMap.put(attrName, true);
            }
        }
        return attributesMap;
    }
    
    static {
        LOGGER = Logger.getLogger(SecurityLogResponseWrapper.class.getName());
        SET_COOKIE_HEADERS = new ArrayList<String>(Arrays.asList("Set-Cookie", "Set-Cookie2"));
    }
}
