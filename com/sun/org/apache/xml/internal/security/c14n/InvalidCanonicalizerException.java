package com.sun.org.apache.xml.internal.security.c14n;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class InvalidCanonicalizerException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public InvalidCanonicalizerException() {
    }
    
    public InvalidCanonicalizerException(final String s) {
        super(s);
    }
    
    public InvalidCanonicalizerException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public InvalidCanonicalizerException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public InvalidCanonicalizerException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public InvalidCanonicalizerException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public InvalidCanonicalizerException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
