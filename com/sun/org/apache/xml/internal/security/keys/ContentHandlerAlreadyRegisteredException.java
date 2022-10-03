package com.sun.org.apache.xml.internal.security.keys;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

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
    
    public ContentHandlerAlreadyRegisteredException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public ContentHandlerAlreadyRegisteredException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public ContentHandlerAlreadyRegisteredException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public ContentHandlerAlreadyRegisteredException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
