package com.sun.org.apache.xml.internal.security.c14n;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class CanonicalizationException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public CanonicalizationException() {
    }
    
    public CanonicalizationException(final Exception ex) {
        super(ex);
    }
    
    public CanonicalizationException(final String s) {
        super(s);
    }
    
    public CanonicalizationException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public CanonicalizationException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public CanonicalizationException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public CanonicalizationException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public CanonicalizationException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
