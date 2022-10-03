package org.owasp.esapi.errors;

public class AvailabilityException extends EnterpriseSecurityException
{
    private static final long serialVersionUID = 1L;
    
    protected AvailabilityException() {
    }
    
    public AvailabilityException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public AvailabilityException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
