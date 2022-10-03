package org.apache.commons.httpclient;

public class CircularRedirectException extends RedirectException
{
    public CircularRedirectException() {
    }
    
    public CircularRedirectException(final String message) {
        super(message);
    }
    
    public CircularRedirectException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
