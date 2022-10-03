package org.apache.xml.security.signature;

import org.apache.xml.security.exceptions.XMLSecurityException;

public class XMLSignatureException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public XMLSignatureException() {
    }
    
    public XMLSignatureException(final String s) {
        super(s);
    }
    
    public XMLSignatureException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public XMLSignatureException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public XMLSignatureException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
