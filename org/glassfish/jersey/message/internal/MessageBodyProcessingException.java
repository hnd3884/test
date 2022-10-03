package org.glassfish.jersey.message.internal;

import javax.ws.rs.ProcessingException;

public class MessageBodyProcessingException extends ProcessingException
{
    private static final long serialVersionUID = 2093175681702118380L;
    
    public MessageBodyProcessingException(final Throwable cause) {
        super(cause);
    }
    
    public MessageBodyProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public MessageBodyProcessingException(final String message) {
        super(message);
    }
}
