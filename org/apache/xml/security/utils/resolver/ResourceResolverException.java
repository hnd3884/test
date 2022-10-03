package org.apache.xml.security.utils.resolver;

import org.w3c.dom.Attr;
import org.apache.xml.security.exceptions.XMLSecurityException;

public class ResourceResolverException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    Attr _uri;
    String _BaseURI;
    
    public ResourceResolverException(final String s, final Attr uri, final String baseURI) {
        super(s);
        this._uri = null;
        this._uri = uri;
        this._BaseURI = baseURI;
    }
    
    public ResourceResolverException(final String s, final Object[] array, final Attr uri, final String baseURI) {
        super(s, array);
        this._uri = null;
        this._uri = uri;
        this._BaseURI = baseURI;
    }
    
    public ResourceResolverException(final String s, final Exception ex, final Attr uri, final String baseURI) {
        super(s, ex);
        this._uri = null;
        this._uri = uri;
        this._BaseURI = baseURI;
    }
    
    public ResourceResolverException(final String s, final Object[] array, final Exception ex, final Attr uri, final String baseURI) {
        super(s, array, ex);
        this._uri = null;
        this._uri = uri;
        this._BaseURI = baseURI;
    }
    
    public void setURI(final Attr uri) {
        this._uri = uri;
    }
    
    public Attr getURI() {
        return this._uri;
    }
    
    public void setBaseURI(final String baseURI) {
        this._BaseURI = baseURI;
    }
    
    public String getBaseURI() {
        return this._BaseURI;
    }
}
