package org.bouncycastle.jce.provider;

import org.bouncycastle.jce.exception.ExtException;

public class AnnotatedException extends Exception implements ExtException
{
    private Throwable _underlyingException;
    
    public AnnotatedException(final String s, final Throwable underlyingException) {
        super(s);
        this._underlyingException = underlyingException;
    }
    
    public AnnotatedException(final String s) {
        this(s, null);
    }
    
    Throwable getUnderlyingException() {
        return this._underlyingException;
    }
    
    @Override
    public Throwable getCause() {
        return this._underlyingException;
    }
}
