package org.apache.xml.security.keys;

import org.apache.xml.security.exceptions.XMLSecurityException;

public class ContentHandlerAlreadyRegisteredException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public ContentHandlerAlreadyRegisteredException() {
    }
    
    public ContentHandlerAlreadyRegisteredException(final String s) {
        super(s);
    }
    
    public ContentHandlerAlreadyRegisteredException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public ContentHandlerAlreadyRegisteredException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public ContentHandlerAlreadyRegisteredException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
