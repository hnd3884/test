package org.apache.xml.security.transforms;

import org.apache.xml.security.exceptions.XMLSecurityException;

public class InvalidTransformException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public InvalidTransformException() {
    }
    
    public InvalidTransformException(final String s) {
        super(s);
    }
    
    public InvalidTransformException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public InvalidTransformException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public InvalidTransformException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
