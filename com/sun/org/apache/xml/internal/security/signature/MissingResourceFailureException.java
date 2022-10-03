package com.sun.org.apache.xml.internal.security.signature;

public class MissingResourceFailureException extends XMLSignatureException
{
    private static final long serialVersionUID = 1L;
    private Reference uninitializedReference;
    
    public MissingResourceFailureException(final Reference uninitializedReference, final String s) {
        super(s);
        this.uninitializedReference = uninitializedReference;
    }
    
    @Deprecated
    public MissingResourceFailureException(final String s, final Reference reference) {
        this(reference, s);
    }
    
    public MissingResourceFailureException(final Reference uninitializedReference, final String s, final Object[] array) {
        super(s, array);
        this.uninitializedReference = uninitializedReference;
    }
    
    @Deprecated
    public MissingResourceFailureException(final String s, final Object[] array, final Reference reference) {
        this(reference, s, array);
    }
    
    public MissingResourceFailureException(final Exception ex, final Reference uninitializedReference, final String s) {
        super(ex, s);
        this.uninitializedReference = uninitializedReference;
    }
    
    @Deprecated
    public MissingResourceFailureException(final String s, final Exception ex, final Reference reference) {
        this(ex, reference, s);
    }
    
    public MissingResourceFailureException(final Exception ex, final Reference uninitializedReference, final String s, final Object[] array) {
        super(ex, s, array);
        this.uninitializedReference = uninitializedReference;
    }
    
    @Deprecated
    public MissingResourceFailureException(final String s, final Object[] array, final Exception ex, final Reference reference) {
        this(ex, reference, s, array);
    }
    
    public void setReference(final Reference uninitializedReference) {
        this.uninitializedReference = uninitializedReference;
    }
    
    public Reference getReference() {
        return this.uninitializedReference;
    }
}
