package org.bouncycastle.util.io.pem;

import java.io.IOException;

public class PemGenerationException extends IOException
{
    private Throwable cause;
    
    public PemGenerationException(final String s, final Throwable cause) {
        super(s);
        this.cause = cause;
    }
    
    public PemGenerationException(final String s) {
        super(s);
    }
    
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
