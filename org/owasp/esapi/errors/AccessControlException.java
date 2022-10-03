package org.owasp.esapi.errors;

public class AccessControlException extends EnterpriseSecurityException
{
    private static final long serialVersionUID = 1L;
    
    protected AccessControlException() {
    }
    
    public AccessControlException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public AccessControlException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
