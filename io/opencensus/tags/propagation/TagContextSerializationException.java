package io.opencensus.tags.propagation;

public final class TagContextSerializationException extends Exception
{
    private static final long serialVersionUID = 0L;
    
    public TagContextSerializationException(final String message) {
        super(message);
    }
    
    public TagContextSerializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
