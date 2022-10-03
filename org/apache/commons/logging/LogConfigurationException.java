package org.apache.commons.logging;

public class LogConfigurationException extends RuntimeException
{
    protected Throwable cause;
    
    public LogConfigurationException() {
        this.cause = null;
    }
    
    public LogConfigurationException(final String message) {
        super(message);
        this.cause = null;
    }
    
    public LogConfigurationException(final String message, final Throwable cause) {
        super(String.valueOf(message) + " (Caused by " + cause + ")");
        this.cause = null;
        this.cause = cause;
    }
    
    public LogConfigurationException(final Throwable cause) {
        this((cause == null) ? null : cause.toString(), cause);
    }
    
    public Throwable getCause() {
        return this.cause;
    }
}
