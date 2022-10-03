package org.apache.xml.security.signature;

public class ReferenceNotInitializedException extends XMLSignatureException
{
    private static final long serialVersionUID = 1L;
    
    public ReferenceNotInitializedException() {
    }
    
    public ReferenceNotInitializedException(final String s) {
        super(s);
    }
    
    public ReferenceNotInitializedException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public ReferenceNotInitializedException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public ReferenceNotInitializedException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
