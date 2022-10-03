package org.glassfish.jersey.server.internal.scanning;

public class ResourceFinderException extends RuntimeException
{
    public ResourceFinderException() {
    }
    
    public ResourceFinderException(final String message) {
        super(message);
    }
    
    public ResourceFinderException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ResourceFinderException(final Throwable cause) {
        super(cause);
    }
}
