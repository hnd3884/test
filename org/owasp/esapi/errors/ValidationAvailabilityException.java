package org.owasp.esapi.errors;

public class ValidationAvailabilityException extends ValidationException
{
    private static final long serialVersionUID = 1L;
    
    protected ValidationAvailabilityException() {
    }
    
    public ValidationAvailabilityException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public ValidationAvailabilityException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
