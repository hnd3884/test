package org.bouncycastle.eac;

public class EACException extends Exception
{
    private Throwable cause;
    
    public EACException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public EACException(final String s) {
        super(s);
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
