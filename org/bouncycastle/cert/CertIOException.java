package org.bouncycastle.cert;

import java.io.IOException;

public class CertIOException extends IOException
{
    private Throwable cause;
    
    public CertIOException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public CertIOException(final String s) {
        super(s);
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
