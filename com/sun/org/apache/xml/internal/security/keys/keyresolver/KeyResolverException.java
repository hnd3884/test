package com.sun.org.apache.xml.internal.security.keys.keyresolver;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class KeyResolverException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public KeyResolverException() {
    }
    
    public KeyResolverException(final Exception ex) {
        super(ex);
    }
    
    public KeyResolverException(final String s) {
        super(s);
    }
    
    public KeyResolverException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public KeyResolverException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public KeyResolverException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public KeyResolverException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public KeyResolverException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
