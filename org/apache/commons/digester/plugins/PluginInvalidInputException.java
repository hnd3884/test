package org.apache.commons.digester.plugins;

public class PluginInvalidInputException extends PluginException
{
    private Throwable cause;
    
    public PluginInvalidInputException(final Throwable cause) {
        this(cause.getMessage());
        this.cause = cause;
    }
    
    public PluginInvalidInputException(final String msg) {
        super(msg);
        this.cause = null;
    }
    
    public PluginInvalidInputException(final String msg, final Throwable cause) {
        this(msg);
        this.cause = cause;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
}
