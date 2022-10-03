package org.apache.xml.security.encryption;

import org.apache.xml.security.exceptions.XMLSecurityException;

public class XMLEncryptionException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public XMLEncryptionException() {
    }
    
    public XMLEncryptionException(final String s) {
        super(s);
    }
    
    public XMLEncryptionException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public XMLEncryptionException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public XMLEncryptionException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
