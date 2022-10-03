package com.sun.org.apache.xalan.internal.utils;

public final class ConfigurationError extends Error
{
    private Exception exception;
    
    ConfigurationError(final String msg, final Exception x) {
        super(msg);
        this.exception = x;
    }
    
    public Exception getException() {
        return this.exception;
    }
}
