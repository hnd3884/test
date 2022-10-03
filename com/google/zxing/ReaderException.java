package com.google.zxing;

public abstract class ReaderException extends Exception
{
    ReaderException() {
    }
    
    @Override
    public final Throwable fillInStackTrace() {
        return null;
    }
}
