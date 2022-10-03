package org.apache.commons.httpclient;

public class InvalidRedirectLocationException extends RedirectException
{
    private final String location;
    
    public InvalidRedirectLocationException(final String message, final String location) {
        super(message);
        this.location = location;
    }
    
    public InvalidRedirectLocationException(final String message, final String location, final Throwable cause) {
        super(message, cause);
        this.location = location;
    }
    
    public String getLocation() {
        return this.location;
    }
}
