package org.bouncycastle.cert.path;

public class CertPathValidationException extends Exception
{
    private final Exception cause;
    
    public CertPathValidationException(final String s) {
        this(s, null);
    }
    
    public CertPathValidationException(final String s, final Exception cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
