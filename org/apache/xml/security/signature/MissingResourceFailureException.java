package org.apache.xml.security.signature;

public class MissingResourceFailureException extends XMLSignatureException
{
    private static final long serialVersionUID = 1L;
    Reference uninitializedReference;
    
    public MissingResourceFailureException(final String s, final Reference uninitializedReference) {
        super(s);
        this.uninitializedReference = null;
        this.uninitializedReference = uninitializedReference;
    }
    
    public MissingResourceFailureException(final String s, final Object[] array, final Reference uninitializedReference) {
        super(s, array);
        this.uninitializedReference = null;
        this.uninitializedReference = uninitializedReference;
    }
    
    public MissingResourceFailureException(final String s, final Exception ex, final Reference uninitializedReference) {
        super(s, ex);
        this.uninitializedReference = null;
        this.uninitializedReference = uninitializedReference;
    }
    
    public MissingResourceFailureException(final String s, final Object[] array, final Exception ex, final Reference uninitializedReference) {
        super(s, array, ex);
        this.uninitializedReference = null;
        this.uninitializedReference = uninitializedReference;
    }
    
    public void setReference(final Reference uninitializedReference) {
        this.uninitializedReference = uninitializedReference;
    }
    
    public Reference getReference() {
        return this.uninitializedReference;
    }
}
