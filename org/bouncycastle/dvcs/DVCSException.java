package org.bouncycastle.dvcs;

public class DVCSException extends Exception
{
    private static final long serialVersionUID = 389345256020131488L;
    private Throwable cause;
    
    public DVCSException(final String s) {
        super(s);
    }
    
    public DVCSException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
