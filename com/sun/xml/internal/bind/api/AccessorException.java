package com.sun.xml.internal.bind.api;

public final class AccessorException extends Exception
{
    public AccessorException() {
    }
    
    public AccessorException(final String message) {
        super(message);
    }
    
    public AccessorException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public AccessorException(final Throwable cause) {
        super(cause);
    }
}
