package org.bouncycastle.cert.cmp;

public class CMPRuntimeException extends RuntimeException
{
    private Throwable cause;
    
    public CMPRuntimeException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
