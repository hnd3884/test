package org.bouncycastle.tsp;

import java.io.IOException;

public class TSPIOException extends IOException
{
    Throwable underlyingException;
    
    public TSPIOException(final String s) {
        super(s);
    }
    
    public TSPIOException(final String s, final Throwable underlyingException) {
        super(s);
        this.underlyingException = underlyingException;
    }
    
    public Exception getUnderlyingException() {
        return (Exception)this.underlyingException;
    }
    
    @Override
    public Throwable getCause() {
        return this.underlyingException;
    }
}
