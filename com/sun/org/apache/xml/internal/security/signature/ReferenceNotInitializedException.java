package com.sun.org.apache.xml.internal.security.signature;

public class ReferenceNotInitializedException extends XMLSignatureException
{
    private static final long serialVersionUID = 1L;
    
    public ReferenceNotInitializedException() {
    }
    
    public ReferenceNotInitializedException(final Exception ex) {
        super(ex);
    }
    
    public ReferenceNotInitializedException(final String s) {
        super(s);
    }
    
    public ReferenceNotInitializedException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public ReferenceNotInitializedException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public ReferenceNotInitializedException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public ReferenceNotInitializedException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public ReferenceNotInitializedException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
