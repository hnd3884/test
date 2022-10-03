package com.sun.xml.internal.ws.spi.db;

public class DatabindingException extends RuntimeException
{
    public DatabindingException() {
    }
    
    public DatabindingException(final String message) {
        super(message);
    }
    
    public DatabindingException(final Throwable cause) {
        super(cause);
    }
    
    public DatabindingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
