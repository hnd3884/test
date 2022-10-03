package org.apache.axiom.om;

public class OMException extends RuntimeException
{
    private static final long serialVersionUID = -730218408325095333L;
    
    public OMException() {
    }
    
    public OMException(final String message) {
        super(message);
    }
    
    public OMException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public OMException(final Throwable cause) {
        super(cause);
    }
}
