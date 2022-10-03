package org.apache.xml.security.keys.keyresolver;

import org.apache.xml.security.exceptions.XMLSecurityException;

public class KeyResolverException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public KeyResolverException() {
    }
    
    public KeyResolverException(final String s) {
        super(s);
    }
    
    public KeyResolverException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public KeyResolverException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public KeyResolverException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
