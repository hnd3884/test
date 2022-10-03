package com.sun.org.apache.xml.internal.security.keys.storage;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class StorageResolverException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public StorageResolverException() {
    }
    
    public StorageResolverException(final Exception ex) {
        super(ex);
    }
    
    public StorageResolverException(final String s) {
        super(s);
    }
    
    public StorageResolverException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public StorageResolverException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public StorageResolverException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public StorageResolverException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public StorageResolverException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
