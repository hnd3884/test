package io.opencensus.tags.propagation;

public final class TagContextDeserializationException extends Exception
{
    private static final long serialVersionUID = 0L;
    
    public TagContextDeserializationException(final String message) {
        super(message);
    }
    
    public TagContextDeserializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
