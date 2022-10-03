package org.apache.lucene.store;

public class AlreadyClosedException extends IllegalStateException
{
    public AlreadyClosedException(final String message) {
        super(message);
    }
    
    public AlreadyClosedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
