package io.opencensus.trace.propagation;

public final class SpanContextParseException extends Exception
{
    private static final long serialVersionUID = 0L;
    
    public SpanContextParseException(final String message) {
        super(message);
    }
    
    public SpanContextParseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
