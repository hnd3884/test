package org.apache.xml.security.signature;

public class InvalidDigestValueException extends XMLSignatureException
{
    private static final long serialVersionUID = 1L;
    
    public InvalidDigestValueException() {
    }
    
    public InvalidDigestValueException(final String s) {
        super(s);
    }
    
    public InvalidDigestValueException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public InvalidDigestValueException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public InvalidDigestValueException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
