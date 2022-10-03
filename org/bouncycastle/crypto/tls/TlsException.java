package org.bouncycastle.crypto.tls;

import java.io.IOException;

public class TlsException extends IOException
{
    protected Throwable cause;
    
    public TlsException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
