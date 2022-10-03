package org.apache.commons.digester.plugins;

public class PluginAssertionFailure extends RuntimeException
{
    private Throwable cause;
    
    public PluginAssertionFailure(final Throwable cause) {
        this(cause.getMessage());
        this.cause = cause;
    }
    
    public PluginAssertionFailure(final String msg) {
        super(msg);
        this.cause = null;
    }
    
    public PluginAssertionFailure(final String msg, final Throwable cause) {
        this(msg);
        this.cause = cause;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
}
