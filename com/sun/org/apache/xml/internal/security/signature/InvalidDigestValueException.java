package com.sun.org.apache.xml.internal.security.signature;

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
    
    public InvalidDigestValueException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public InvalidDigestValueException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public InvalidDigestValueException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public InvalidDigestValueException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
