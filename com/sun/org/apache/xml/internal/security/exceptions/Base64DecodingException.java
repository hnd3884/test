package com.sun.org.apache.xml.internal.security.exceptions;

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
    
    public Base64DecodingException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public Base64DecodingException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public Base64DecodingException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public Base64DecodingException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
