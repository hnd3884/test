package org.glassfish.jersey.client.authentication;

import javax.ws.rs.ProcessingException;

public class RequestAuthenticationException extends ProcessingException
{
    public RequestAuthenticationException(final Throwable cause) {
        super(cause);
    }
    
    public RequestAuthenticationException(final String message) {
        super(message);
    }
    
    public RequestAuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
