package com.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class TransformationException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public TransformationException() {
    }
    
    public TransformationException(final Exception ex) {
        super(ex);
    }
    
    public TransformationException(final String s) {
        super(s);
    }
    
    public TransformationException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public TransformationException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public TransformationException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public TransformationException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public TransformationException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
