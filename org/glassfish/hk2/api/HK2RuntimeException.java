package org.glassfish.hk2.api;

public class HK2RuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 4421677624470704792L;
    
    public HK2RuntimeException() {
    }
    
    public HK2RuntimeException(final String message) {
        super(message);
    }
    
    public HK2RuntimeException(final Throwable cause) {
        super(cause);
    }
    
    public HK2RuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
