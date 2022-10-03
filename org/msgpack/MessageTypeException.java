package org.msgpack;

public class MessageTypeException extends RuntimeException
{
    public MessageTypeException() {
    }
    
    public MessageTypeException(final String message) {
        super(message);
    }
    
    public MessageTypeException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public MessageTypeException(final Throwable cause) {
        super(cause);
    }
}
