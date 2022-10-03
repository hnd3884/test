package org.bouncycastle.tsp;

public class TSPException extends Exception
{
    Throwable underlyingException;
    
    public TSPException(final String s) {
        super(s);
    }
    
    public TSPException(final String s, final Throwable underlyingException) {
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
