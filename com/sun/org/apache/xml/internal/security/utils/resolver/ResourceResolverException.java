package com.sun.org.apache.xml.internal.security.utils.resolver;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class ResourceResolverException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    private String uri;
    private String baseURI;
    
    public ResourceResolverException(final String s, final String uri, final String baseURI) {
        super(s);
        this.uri = uri;
        this.baseURI = baseURI;
    }
    
    public ResourceResolverException(final String s, final Object[] array, final String uri, final String baseURI) {
        super(s, array);
        this.uri = uri;
        this.baseURI = baseURI;
    }
    
    public ResourceResolverException(final Exception ex, final String uri, final String baseURI, final String s) {
        super(ex, s);
        this.uri = uri;
        this.baseURI = baseURI;
    }
    
    @Deprecated
    public ResourceResolverException(final String s, final Exception ex, final String s2, final String s3) {
        this(ex, s2, s3, s);
    }
    
    public ResourceResolverException(final Exception ex, final String uri, final String baseURI, final String s, final Object[] array) {
        super(ex, s, array);
        this.uri = uri;
        this.baseURI = baseURI;
    }
    
    @Deprecated
    public ResourceResolverException(final String s, final Object[] array, final Exception ex, final String s2, final String s3) {
        this(ex, s2, s3, s, array);
    }
    
    public void setURI(final String uri) {
        this.uri = uri;
    }
    
    public String getURI() {
        return this.uri;
    }
    
    public void setbaseURI(final String baseURI) {
        this.baseURI = baseURI;
    }
    
    public String getbaseURI() {
        return this.baseURI;
    }
}
