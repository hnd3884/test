package com.google.zxing;

public final class WriterException extends Exception
{
    public WriterException() {
    }
    
    public WriterException(final String message) {
        super(message);
    }
    
    public WriterException(final Throwable cause) {
        super(cause);
    }
}
