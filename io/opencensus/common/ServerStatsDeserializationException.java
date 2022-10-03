package io.opencensus.common;

public final class ServerStatsDeserializationException extends Exception
{
    private static final long serialVersionUID = 0L;
    
    public ServerStatsDeserializationException(final String message) {
        super(message);
    }
    
    public ServerStatsDeserializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
