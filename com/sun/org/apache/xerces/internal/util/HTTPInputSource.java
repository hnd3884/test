package com.sun.org.apache.xerces.internal.util;

import java.util.Iterator;
import java.io.Reader;
import java.io.InputStream;
import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;
import java.util.HashMap;
import java.util.Map;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;

public final class HTTPInputSource extends XMLInputSource
{
    protected boolean fFollowRedirects;
    protected Map<String, String> fHTTPRequestProperties;
    
    public HTTPInputSource(final String publicId, final String systemId, final String baseSystemId) {
        super(publicId, systemId, baseSystemId);
        this.fFollowRedirects = true;
        this.fHTTPRequestProperties = new HashMap<String, String>();
    }
    
    public HTTPInputSource(final XMLResourceIdentifier resourceIdentifier) {
        super(resourceIdentifier);
        this.fFollowRedirects = true;
        this.fHTTPRequestProperties = new HashMap<String, String>();
    }
    
    public HTTPInputSource(final String publicId, final String systemId, final String baseSystemId, final InputStream byteStream, final String encoding) {
        super(publicId, systemId, baseSystemId, byteStream, encoding);
        this.fFollowRedirects = true;
        this.fHTTPRequestProperties = new HashMap<String, String>();
    }
    
    public HTTPInputSource(final String publicId, final String systemId, final String baseSystemId, final Reader charStream, final String encoding) {
        super(publicId, systemId, baseSystemId, charStream, encoding);
        this.fFollowRedirects = true;
        this.fHTTPRequestProperties = new HashMap<String, String>();
    }
    
    public boolean getFollowHTTPRedirects() {
        return this.fFollowRedirects;
    }
    
    public void setFollowHTTPRedirects(final boolean followRedirects) {
        this.fFollowRedirects = followRedirects;
    }
    
    public String getHTTPRequestProperty(final String key) {
        return this.fHTTPRequestProperties.get(key);
    }
    
    public Iterator<Map.Entry<String, String>> getHTTPRequestProperties() {
        return this.fHTTPRequestProperties.entrySet().iterator();
    }
    
    public void setHTTPRequestProperty(final String key, final String value) {
        if (value != null) {
            this.fHTTPRequestProperties.put(key, value);
        }
        else {
            this.fHTTPRequestProperties.remove(key);
        }
    }
}
