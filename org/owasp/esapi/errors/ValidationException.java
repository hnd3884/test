package org.owasp.esapi.errors;

public class ValidationException extends EnterpriseSecurityException
{
    protected static final long serialVersionUID = 1L;
    private String context;
    
    protected ValidationException() {
    }
    
    public ValidationException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public ValidationException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
    
    public ValidationException(final String userMessage, final String logMessage, final String context) {
        super(userMessage, logMessage);
        this.setContext(context);
    }
    
    public ValidationException(final String userMessage, final String logMessage, final Throwable cause, final String context) {
        super(userMessage, logMessage, cause);
        this.setContext(context);
    }
    
    public String getContext() {
        return this.context;
    }
    
    public void setContext(final String context) {
        this.context = context;
    }
}
