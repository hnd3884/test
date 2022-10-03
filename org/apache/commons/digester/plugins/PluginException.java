package org.apache.commons.digester.plugins;

public class PluginException extends Exception
{
    private Throwable cause;
    
    public PluginException(final Throwable cause) {
        this(cause.getMessage());
        this.cause = cause;
    }
    
    public PluginException(final String msg) {
        super(msg);
        this.cause = null;
    }
    
    public PluginException(final String msg, final Throwable cause) {
        this(msg);
        this.cause = cause;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
}
