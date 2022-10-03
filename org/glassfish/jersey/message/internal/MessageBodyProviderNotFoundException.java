package org.glassfish.jersey.message.internal;

import javax.ws.rs.ProcessingException;

public class MessageBodyProviderNotFoundException extends ProcessingException
{
    private static final long serialVersionUID = 2093175681702118380L;
    
    public MessageBodyProviderNotFoundException(final Throwable cause) {
        super(cause);
    }
    
    public MessageBodyProviderNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public MessageBodyProviderNotFoundException(final String message) {
        super(message);
    }
}
