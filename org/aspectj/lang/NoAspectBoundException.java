package org.aspectj.lang;

public class NoAspectBoundException extends RuntimeException
{
    Throwable cause;
    
    public NoAspectBoundException(final String aspectName, final Throwable inner) {
        super((inner == null) ? aspectName : ("Exception while initializing " + aspectName + ": " + inner));
        this.cause = inner;
    }
    
    public NoAspectBoundException() {
    }
    
    public Throwable getCause() {
        return this.cause;
    }
}
