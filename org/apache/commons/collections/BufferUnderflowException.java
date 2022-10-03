package org.apache.commons.collections;

import java.util.NoSuchElementException;

public class BufferUnderflowException extends NoSuchElementException
{
    private final Throwable throwable;
    
    public BufferUnderflowException() {
        this.throwable = null;
    }
    
    public BufferUnderflowException(final String message) {
        this(message, null);
    }
    
    public BufferUnderflowException(final String message, final Throwable exception) {
        super(message);
        this.throwable = exception;
    }
    
    public final Throwable getCause() {
        return this.throwable;
    }
}
