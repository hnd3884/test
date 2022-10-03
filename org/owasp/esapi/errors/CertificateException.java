package org.owasp.esapi.errors;

public class CertificateException extends EnterpriseSecurityException
{
    private static final long serialVersionUID = 1L;
    
    protected CertificateException() {
    }
    
    public CertificateException(final String userMessage, final String logMessage) {
        super(userMessage, logMessage);
    }
    
    public CertificateException(final String userMessage, final String logMessage, final Throwable cause) {
        super(userMessage, logMessage, cause);
    }
}
