package org.glassfish.jersey.server.internal.process;

import javax.ws.rs.ProcessingException;

public class MappableException extends ProcessingException
{
    private static final long serialVersionUID = -7326005523956892754L;
    
    public MappableException(final Throwable cause) {
        super(unwrap(cause));
    }
    
    public MappableException(final String message, final Throwable cause) {
        super(message, unwrap(cause));
    }
    
    private static Throwable unwrap(Throwable cause) {
        if (cause instanceof MappableException) {
            do {
                final MappableException mce = (MappableException)cause;
                cause = mce.getCause();
            } while (cause instanceof MappableException);
        }
        return cause;
    }
}
