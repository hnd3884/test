package org.apache.xerces.util;

import java.util.Iterator;
import java.io.Reader;
import java.io.InputStream;
import org.apache.xerces.xni.XMLResourceIdentifier;
import java.util.HashMap;
import java.util.Map;
import org.apache.xerces.xni.parser.XMLInputSource;

public final class HTTPInputSource extends XMLInputSource
{
    protected boolean fFollowRedirects;
    protected Map fHTTPRequestProperties;
    
    public HTTPInputSource(final String s, final String s2, final String s3) {
        super(s, s2, s3);
        this.fFollowRedirects = true;
        this.fHTTPRequestProperties = new HashMap();
    }
    
    public HTTPInputSource(final XMLResourceIdentifier xmlResourceIdentifier) {
        super(xmlResourceIdentifier);
        this.fFollowRedirects = true;
        this.fHTTPRequestProperties = new HashMap();
    }
    
    public HTTPInputSource(final String s, final String s2, final String s3, final InputStream inputStream, final String s4) {
        super(s, s2, s3, inputStream, s4);
        this.fFollowRedirects = true;
        this.fHTTPRequestProperties = new HashMap();
    }
    
    public HTTPInputSource(final String s, final String s2, final String s3, final Reader reader, final String s4) {
        super(s, s2, s3, reader, s4);
        this.fFollowRedirects = true;
        this.fHTTPRequestProperties = new HashMap();
    }
    
    public boolean getFollowHTTPRedirects() {
        return this.fFollowRedirects;
    }
    
    public void setFollowHTTPRedirects(final boolean fFollowRedirects) {
        this.fFollowRedirects = fFollowRedirects;
    }
    
    public String getHTTPRequestProperty(final String s) {
        return this.fHTTPRequestProperties.get(s);
    }
    
    public Iterator getHTTPRequestProperties() {
        return this.fHTTPRequestProperties.entrySet().iterator();
    }
    
    public void setHTTPRequestProperty(final String s, final String s2) {
        if (s2 != null) {
            this.fHTTPRequestProperties.put(s, s2);
        }
        else {
            this.fHTTPRequestProperties.remove(s);
        }
    }
}
