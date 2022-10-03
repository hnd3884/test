package org.apache.catalina.authenticator;

import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.tomcat.util.buf.ByteChunk;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.Cookie;
import java.util.List;

public final class SavedRequest
{
    private final List<Cookie> cookies;
    private final Map<String, List<String>> headers;
    private final List<Locale> locales;
    private String method;
    private String queryString;
    private String requestURI;
    private String decodedRequestURI;
    private ByteChunk body;
    private String contentType;
    
    public SavedRequest() {
        this.cookies = new ArrayList<Cookie>();
        this.headers = new HashMap<String, List<String>>();
        this.locales = new ArrayList<Locale>();
        this.method = null;
        this.queryString = null;
        this.requestURI = null;
        this.decodedRequestURI = null;
        this.body = null;
        this.contentType = null;
    }
    
    public void addCookie(final Cookie cookie) {
        this.cookies.add(cookie);
    }
    
    public Iterator<Cookie> getCookies() {
        return this.cookies.iterator();
    }
    
    public void addHeader(final String name, final String value) {
        List<String> values = this.headers.get(name);
        if (values == null) {
            values = new ArrayList<String>();
            this.headers.put(name, values);
        }
        values.add(value);
    }
    
    public Iterator<String> getHeaderNames() {
        return this.headers.keySet().iterator();
    }
    
    public Iterator<String> getHeaderValues(final String name) {
        final List<String> values = this.headers.get(name);
        if (values == null) {
            return Collections.emptyIterator();
        }
        return values.iterator();
    }
    
    public void addLocale(final Locale locale) {
        this.locales.add(locale);
    }
    
    public Iterator<Locale> getLocales() {
        return this.locales.iterator();
    }
    
    public String getMethod() {
        return this.method;
    }
    
    public void setMethod(final String method) {
        this.method = method;
    }
    
    public String getQueryString() {
        return this.queryString;
    }
    
    public void setQueryString(final String queryString) {
        this.queryString = queryString;
    }
    
    public String getRequestURI() {
        return this.requestURI;
    }
    
    public void setRequestURI(final String requestURI) {
        this.requestURI = requestURI;
    }
    
    public String getDecodedRequestURI() {
        return this.decodedRequestURI;
    }
    
    public void setDecodedRequestURI(final String decodedRequestURI) {
        this.decodedRequestURI = decodedRequestURI;
    }
    
    public ByteChunk getBody() {
        return this.body;
    }
    
    public void setBody(final ByteChunk body) {
        this.body = body;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }
}
