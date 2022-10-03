package com.sun.xml.internal.ws.util;

public class ServiceConfigurationError extends Error
{
    public ServiceConfigurationError(final String msg) {
        super(msg);
    }
    
    public ServiceConfigurationError(final Throwable x) {
        super(x);
    }
}
