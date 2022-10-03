package com.me.devicemanagement.framework.server.certificate.verifier;

public class CertificateVerificationException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public CertificateVerificationException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public CertificateVerificationException(final String message) {
        super(message);
    }
}
