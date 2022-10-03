package org.jscep.message;

import net.jcip.annotations.Immutable;

@Immutable
public class MessageEncodingException extends Exception
{
    private static final long serialVersionUID = -6111956271602335933L;
    
    public MessageEncodingException(final Throwable cause) {
        super(cause);
    }
    
    public MessageEncodingException(final String message) {
        super(message);
    }
}
