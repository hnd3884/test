package org.apache.commons.collections;

public class BufferOverflowException extends RuntimeException
{
    private final Throwable throwable;
    
    public BufferOverflowException() {
        this.throwable = null;
    }
    
    public BufferOverflowException(final String message) {
        this(message, null);
    }
    
    public BufferOverflowException(final String message, final Throwable exception) {
        super(message);
        this.throwable = exception;
    }
    
    public final Throwable getCause() {
        return this.throwable;
    }
}
