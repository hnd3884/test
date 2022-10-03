package org.bouncycastle.cert.crmf;

public class CRMFException extends Exception
{
    private Throwable cause;
    
    public CRMFException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
