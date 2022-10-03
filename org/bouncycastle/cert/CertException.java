package org.bouncycastle.cert;

public class CertException extends Exception
{
    private Throwable cause;
    
    public CertException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public CertException(final String s) {
        super(s);
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
