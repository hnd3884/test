package org.apache.xml.security.c14n;

import org.apache.xml.security.exceptions.XMLSecurityException;

public class CanonicalizationException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public CanonicalizationException() {
    }
    
    public CanonicalizationException(final String s) {
        super(s);
    }
    
    public CanonicalizationException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public CanonicalizationException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public CanonicalizationException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
