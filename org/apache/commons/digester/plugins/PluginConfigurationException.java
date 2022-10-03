package org.apache.commons.digester.plugins;

public class PluginConfigurationException extends RuntimeException
{
    private Throwable cause;
    
    public PluginConfigurationException(final Throwable cause) {
        this(cause.getMessage());
        this.cause = cause;
    }
    
    public PluginConfigurationException(final String msg) {
        super(msg);
        this.cause = null;
    }
    
    public PluginConfigurationException(final String msg, final Throwable cause) {
        this(msg);
        this.cause = cause;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
}
