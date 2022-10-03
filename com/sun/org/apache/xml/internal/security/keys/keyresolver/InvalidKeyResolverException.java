package com.sun.org.apache.xml.internal.security.keys.keyresolver;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class InvalidKeyResolverException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public InvalidKeyResolverException() {
    }
    
    public InvalidKeyResolverException(final String s) {
        super(s);
    }
    
    public InvalidKeyResolverException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public InvalidKeyResolverException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public InvalidKeyResolverException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public InvalidKeyResolverException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public InvalidKeyResolverException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
