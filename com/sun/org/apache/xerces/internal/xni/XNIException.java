package com.sun.org.apache.xerces.internal.xni;

public class XNIException extends RuntimeException
{
    static final long serialVersionUID = 9019819772686063775L;
    private Exception fException;
    
    public XNIException(final String message) {
        super(message);
    }
    
    public XNIException(final Exception exception) {
        super(exception.getMessage());
        this.fException = exception;
    }
    
    public XNIException(final String message, final Exception exception) {
        super(message);
        this.fException = exception;
    }
    
    public Exception getException() {
        return this.fException;
    }
    
    @Override
    public Throwable getCause() {
        return this.fException;
    }
}
