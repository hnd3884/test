package com.sun.org.apache.xml.internal.security.exceptions;

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
    
    public AlgorithmAlreadyRegisteredException(final Exception ex, final String s) {
        super(ex, s);
    }
    
    @Deprecated
    public AlgorithmAlreadyRegisteredException(final String s, final Exception ex) {
        this(ex, s);
    }
    
    public AlgorithmAlreadyRegisteredException(final Exception ex, final String s, final Object[] array) {
        super(ex, s, array);
    }
    
    @Deprecated
    public AlgorithmAlreadyRegisteredException(final String s, final Object[] array, final Exception ex) {
        this(ex, s, array);
    }
}
