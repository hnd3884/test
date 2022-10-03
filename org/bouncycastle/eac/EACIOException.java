package org.bouncycastle.eac;

import java.io.IOException;

public class EACIOException extends IOException
{
    private Throwable cause;
    
    public EACIOException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public EACIOException(final String s) {
        super(s);
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
