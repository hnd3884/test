package org.bouncycastle.cert.dane;

public class DANEException extends Exception
{
    private Throwable cause;
    
    public DANEException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public DANEException(final String s) {
        super(s);
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
