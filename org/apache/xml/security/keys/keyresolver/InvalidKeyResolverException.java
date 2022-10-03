package org.apache.xml.security.keys.keyresolver;

import org.apache.xml.security.exceptions.XMLSecurityException;

public class InvalidKeyResolverException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public InvalidKeyResolverException() {
    }
    
    public InvalidKeyResolverException(final String s) {
        super(s);
    }
    
    public InvalidKeyResolverException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public InvalidKeyResolverException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public InvalidKeyResolverException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
