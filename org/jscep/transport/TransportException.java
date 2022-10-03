package org.jscep.transport;

import net.jcip.annotations.Immutable;

@Immutable
public class TransportException extends Exception
{
    private static final long serialVersionUID = 7384278241045962726L;
    
    public TransportException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public TransportException(final Throwable cause) {
        super(cause);
    }
    
    public TransportException(final String message) {
        super(message);
    }
}
