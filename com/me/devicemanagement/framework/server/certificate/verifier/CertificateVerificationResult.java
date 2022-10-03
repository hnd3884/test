package com.me.devicemanagement.framework.server.certificate.verifier;

import java.security.cert.PKIXCertPathBuilderResult;

public class CertificateVerificationResult
{
    private boolean valid;
    private PKIXCertPathBuilderResult result;
    private Throwable exception;
    
    public CertificateVerificationResult(final PKIXCertPathBuilderResult result) {
        this.valid = true;
        this.result = result;
    }
    
    public CertificateVerificationResult(final Throwable exception) {
        this.valid = false;
        this.exception = exception;
    }
    
    public boolean isValid() {
        return this.valid;
    }
    
    public PKIXCertPathBuilderResult getResult() {
        return this.result;
    }
    
    public Throwable getException() {
        return this.exception;
    }
}
