package org.bouncycastle.pkcs;

public class PKCSException extends Exception
{
    private Throwable cause;
    
    public PKCSException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public PKCSException(final String s) {
        super(s);
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
