package org.apache.axiom.core;

public class NodeFactoryException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    public NodeFactoryException(final String message) {
        super(message);
    }
    
    public NodeFactoryException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
