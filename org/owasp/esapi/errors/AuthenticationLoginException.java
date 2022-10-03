package org.owasp.esapi.errors;

public class AuthenticationLoginException extends AuthenticationException
{
    private static final long serialVersionUID = 1L;
    
    protected AuthenticationLoginException() {
    }
    
    public AuthenticationLoginException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public AuthenticationLoginException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
