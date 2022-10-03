package com.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class InvalidTransformException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public InvalidTransformException() {
    }
    
    public InvalidTransformException(final String s) {
        super(s);
    }
    
    public InvalidTransformException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public InvalidTransformException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public InvalidTransformException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public InvalidTransformException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public InvalidTransformException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
