package org.apache.xerces.xni;

public class XNIException extends RuntimeException
{
    static final long serialVersionUID = 9019819772686063775L;
    private Exception fException;
    
    public XNIException(final String s) {
        super(s);
        this.fException = this;
    }
    
    public XNIException(final Exception fException) {
        super(fException.getMessage());
        this.fException = this;
        this.fException = fException;
    }
    
    public XNIException(final String s, final Exception fException) {
        super(s);
        this.fException = this;
        this.fException = fException;
    }
    
    public Exception getException() {
        return (this.fException != this) ? this.fException : null;
    }
    
    public synchronized Throwable initCause(final Throwable t) {
        if (this.fException != this) {
            throw new IllegalStateException();
        }
        if (t == this) {
            throw new IllegalArgumentException();
        }
        this.fException = (Exception)t;
        return this;
    }
    
    public Throwable getCause() {
        return this.getException();
    }
}
