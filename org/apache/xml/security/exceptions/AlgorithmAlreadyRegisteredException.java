package org.apache.xml.security.exceptions;

public class AlgorithmAlreadyRegisteredException extends XMLSecurityException
{
    private static final long serialVersionUID = 1L;
    
    public AlgorithmAlreadyRegisteredException() {
    }
    
    public AlgorithmAlreadyRegisteredException(final String s) {
        super(s);
    }
    
    public AlgorithmAlreadyRegisteredException(final String s, final Object[] array) {
        super(s, array);
    }
    
    public AlgorithmAlreadyRegisteredException(final String s, final Exception ex) {
        super(s, ex);
    }
    
    public AlgorithmAlreadyRegisteredException(final String s, final Object[] array, final Exception ex) {
        super(s, array, ex);
    }
}
