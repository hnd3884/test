package org.glassfish.jersey.client.authentication;

import javax.ws.rs.core.Response;
import javax.ws.rs.client.ResponseProcessingException;

public class ResponseAuthenticationException extends ResponseProcessingException
{
    public ResponseAuthenticationException(final Response response, final Throwable cause) {
        super(response, cause);
    }
    
    public ResponseAuthenticationException(final Response response, final String message) {
        super(response, message);
    }
    
    public ResponseAuthenticationException(final Response response, final String message, final Throwable cause) {
        super(response, message, cause);
    }
}
