package org.apache.xml.security.signature;

public class InvalidSignatureValueException extends XMLSignatureException
{
    private static final long serialVersionUID = 1L;
    
    public InvalidSignatureValueException() {
    }
    
    public InvalidSignatureValueException(final String s) {
        super(s);
    }
    
    public InvalidSignatureValueException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public InvalidSignatureValueException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public InvalidSignatureValueException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
