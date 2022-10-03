package org.owasp.esapi.errors;

public class IntegrityException extends EnterpriseSecurityException
{
    private static final long serialVersionUID = 1L;
    
    protected IntegrityException() {
    }
    
    public IntegrityException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public IntegrityException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
