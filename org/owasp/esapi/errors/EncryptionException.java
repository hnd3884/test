package org.owasp.esapi.errors;

public class EncryptionException extends EnterpriseSecurityException
{
    private static final long serialVersionUID = 1L;
    
    protected EncryptionException() {
    }
    
    public EncryptionException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public EncryptionException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
