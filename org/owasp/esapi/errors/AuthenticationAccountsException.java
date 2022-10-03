package org.owasp.esapi.errors;

public class AuthenticationAccountsException extends AuthenticationException
{
    private static final long serialVersionUID = 1L;
    
    protected AuthenticationAccountsException() {
    }
    
    public AuthenticationAccountsException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public AuthenticationAccountsException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
