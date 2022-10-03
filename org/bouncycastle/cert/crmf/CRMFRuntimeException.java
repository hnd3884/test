package org.bouncycastle.cert.crmf;

public class CRMFRuntimeException extends RuntimeException
{
    private Throwable cause;
    
    public CRMFRuntimeException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
