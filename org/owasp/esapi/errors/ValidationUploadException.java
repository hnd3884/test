package org.owasp.esapi.errors;

public class ValidationUploadException extends ValidationException
{
    private static final long serialVersionUID = 1L;
    
    protected ValidationUploadException() {
    }
    
    public ValidationUploadException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public ValidationUploadException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
