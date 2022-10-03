package org.apache.xml.security.exceptions;

public class Base64DecodingException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public Base64DecodingException() {
    }
    
    public Base64DecodingException(final String s) {
        super(s);
    }
    
    public Base64DecodingException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public Base64DecodingException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public Base64DecodingException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
