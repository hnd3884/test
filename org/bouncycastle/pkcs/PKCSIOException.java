package org.bouncycastle.pkcs;

import java.io.IOException;

public class PKCSIOException extends IOException
{
    private Throwable cause;
    
    public PKCSIOException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public PKCSIOException(final String s) {
        super(s);
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
