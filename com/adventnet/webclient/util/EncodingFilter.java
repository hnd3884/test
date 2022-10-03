package com.adventnet.webclient.util;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.Filter;

public class EncodingFilter implements Filter
{
    private String className;
    private FilterConfig config;
    private String encoding;
    private String contentType;
    
    public EncodingFilter() {
        this.className = this.getClass().getName();
        this.config = null;
        this.encoding = null;
        this.contentType = null;
        this.log("Within the constructor of EncodingFilter");
        this.init();
    }
    
    public void init(final FilterConfig config) throws ServletException {
        this.log("Entering init() method");
        this.config = config;
        final String _encoding = config.getInitParameter("encoding");
        if (_encoding != null) {
            this.log("Character encoding specified in deployment descriptor is " + _encoding);
            this.encoding = _encoding;
        }
        final String _contentType = config.getInitParameter("contentType");
        if (_contentType != null) {
            this.log("ContentType specified in deployment descriptor is " + _contentType);
            this.contentType = _contentType + ";charset=" + _encoding;
        }
        this.log("Exiting init() method");
    }
    
    public void destroy() {
        this.log("Entering destroy() method");
        this.init();
        this.log("Exiting destroy() method");
    }
    
    public void doFilter(final ServletRequest srequest, final ServletResponse sresponse, final FilterChain chain) throws IOException, ServletException {
        this.log("Entering doFilter() method");
        final Locale locale = this.getLocale(srequest);
        srequest.setCharacterEncoding(this.encoding);
        this.log("Successfully set the character encoding " + this.encoding);
        this.log("Calling the next resource on the filter chain");
        chain.doFilter(srequest, sresponse);
        sresponse.setContentType(this.contentType);
        this.log("Successfully set the content type " + this.contentType);
        sresponse.setLocale(locale);
        this.log("Successfully set the locale " + locale);
    }
    
    protected void init() {
        this.config = null;
        this.encoding = "UTF-8";
        this.contentType = "text/html;charset=" + this.encoding;
    }
    
    protected void log(final String msg) {
        System.out.println(this.className + " : " + msg);
    }
    
    protected Locale getLocale(final ServletRequest srequest) {
        Locale locale = srequest.getLocale();
        if (locale == null) {
            this.log("The locale information from the browser is null, trying to get the System properties user.language and user.country");
            String language = System.getProperty("user.language");
            if (language == null || language.trim().equals("")) {
                language = "en";
            }
            String country = System.getProperty("user.country");
            if (country == null || country.trim().equals("")) {
                country = "US";
            }
            locale = new Locale(language, country);
        }
        else {
            this.log("The locale got from the browser is " + locale);
        }
        return locale;
    }
}
