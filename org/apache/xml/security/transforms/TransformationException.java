package org.apache.xml.security.transforms;

import org.apache.xml.security.exceptions.XMLSecurityException;

public class TransformationException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public TransformationException() {
    }
    
    public TransformationException(final String s) {
        super(s);
    }
    
    public TransformationException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public TransformationException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public TransformationException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
