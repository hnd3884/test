package com.sun.xml.internal.ws.policy.privateutil;

public class ServiceConfigurationError extends Error
{
    public ServiceConfigurationError(final String message) {
        super(message);
    }
    
    public ServiceConfigurationError(final Throwable throwable) {
        super(throwable);
    }
}
