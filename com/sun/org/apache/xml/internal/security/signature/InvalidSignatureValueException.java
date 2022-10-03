package com.sun.org.apache.xml.internal.security.signature;

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
    
    public InvalidSignatureValueException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public InvalidSignatureValueException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public InvalidSignatureValueException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public InvalidSignatureValueException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
