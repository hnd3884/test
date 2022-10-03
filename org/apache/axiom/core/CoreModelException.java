package org.apache.axiom.core;

public abstract class CoreModelException extends Exception
{
    private static final long serialVersionUID = 1204321445792058777L;
    
    public CoreModelException() {
    }
    
    public CoreModelException(final String message) {
        super(message);
    }
    
    public CoreModelException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public CoreModelException(final Throwable cause) {
        super(cause);
    }
}
