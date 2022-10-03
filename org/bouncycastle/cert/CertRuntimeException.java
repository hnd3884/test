package org.bouncycastle.cert;

public class CertRuntimeException extends RuntimeException
{
    private Throwable cause;
    
    public CertRuntimeException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
