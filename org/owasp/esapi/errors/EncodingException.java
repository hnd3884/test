package org.owasp.esapi.errors;

public class EncodingException extends EnterpriseSecurityException
{
    private static final long serialVersionUID = 1L;
    
    protected EncodingException() {
    }
    
    public EncodingException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public EncodingException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
