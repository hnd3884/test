package com.sun.org.apache.xml.internal.security.signature;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class XMLSignatureException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public XMLSignatureException() {
    }
    
    public XMLSignatureException(final Exception ex) {
        super(ex);
    }
    
    public XMLSignatureException(final String s) {
        super(s);
    }
    
    public XMLSignatureException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public XMLSignatureException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public XMLSignatureException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public XMLSignatureException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public XMLSignatureException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
