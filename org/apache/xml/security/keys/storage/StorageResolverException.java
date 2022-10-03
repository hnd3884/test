package org.apache.xml.security.keys.storage;

import org.apache.xml.security.exceptions.XMLSecurityException;

public class StorageResolverException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public StorageResolverException() {
    }
    
    public StorageResolverException(final String s) {
        super(s);
    }
    
    public StorageResolverException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public StorageResolverException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public StorageResolverException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
