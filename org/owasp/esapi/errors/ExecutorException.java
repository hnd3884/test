package org.owasp.esapi.errors;

public class ExecutorException extends EnterpriseSecurityException
{
    private static final long serialVersionUID = 1L;
    
    protected ExecutorException() {
    }
    
    public ExecutorException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public ExecutorException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
