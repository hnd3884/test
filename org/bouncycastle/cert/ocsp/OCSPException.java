package org.bouncycastle.cert.ocsp;

public class OCSPException extends Exception
{
    private Throwable cause;
    
    public OCSPException(final String s) {
        super(s);
    }
    
    public OCSPException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
