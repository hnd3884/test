package org.bouncycastle.cert.cmp;

public class CMPException extends Exception
{
    private Throwable cause;
    
    public CMPException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public CMPException(final String s) {
        super(s);
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
