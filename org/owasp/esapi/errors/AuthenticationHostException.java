package org.owasp.esapi.errors;

public class AuthenticationHostException extends AuthenticationException
{
    private static final long serialVersionUID = 1L;
    
    protected AuthenticationHostException() {
    }
    
    public AuthenticationHostException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public AuthenticationHostException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
