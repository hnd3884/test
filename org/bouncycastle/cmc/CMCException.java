package org.bouncycastle.cmc;

public class CMCException extends Exception
{
    private final Throwable cause;
    
    public CMCException(final String s) {
        this(s, null);
    }
    
    public CMCException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
