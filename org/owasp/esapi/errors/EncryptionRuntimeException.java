package org.owasp.esapi.errors;

public class EncryptionRuntimeException extends EnterpriseSecurityRuntimeException
{
    private static final long serialVersionUID = 1L;
    
    protected EncryptionRuntimeException() {
    }
    
    public EncryptionRuntimeException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public EncryptionRuntimeException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
