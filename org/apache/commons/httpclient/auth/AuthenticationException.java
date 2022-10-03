package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.ProtocolException;

public class AuthenticationException extends ProtocolException
{
    public AuthenticationException() {
    }
    
    public AuthenticationException(final String message) {
        super(message);
    }
    
    public AuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
