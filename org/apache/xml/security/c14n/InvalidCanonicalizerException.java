package org.apache.xml.security.c14n;

import org.apache.xml.security.exceptions.XMLSecurityException;

public class InvalidCanonicalizerException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public InvalidCanonicalizerException() {
    }
    
    public InvalidCanonicalizerException(final String s) {
        super(s);
    }
    
    public InvalidCanonicalizerException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public InvalidCanonicalizerException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public InvalidCanonicalizerException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
