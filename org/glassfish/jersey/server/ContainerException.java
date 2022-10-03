package org.glassfish.jersey.server;

import javax.ws.rs.ProcessingException;

public class ContainerException extends ProcessingException
{
    private static final long serialVersionUID = -1721209891860592440L;
    
    public ContainerException(final Throwable cause) {
        super(cause);
    }
    
    public ContainerException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public ContainerException(final String message) {
        super(message);
    }
}
