package org.owasp.esapi.errors;

public class AuthenticationCredentialsException extends AuthenticationException
{
    private static final long serialVersionUID = 1L;
    
    protected AuthenticationCredentialsException() {
    }
    
    public AuthenticationCredentialsException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public AuthenticationCredentialsException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
