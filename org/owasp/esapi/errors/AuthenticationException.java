package org.owasp.esapi.errors;

public class AuthenticationException extends EnterpriseSecurityException
{
    private static final long serialVersionUID = 1L;
    
    protected AuthenticationException() {
    }
    
    public AuthenticationException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public AuthenticationException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
