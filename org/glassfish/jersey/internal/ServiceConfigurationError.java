package org.glassfish.jersey.internal;

public class ServiceConfigurationError extends Error
{
    private static final long serialVersionUID = -8532392338326428074L;
    
    public ServiceConfigurationError(final String msg) {
        super(msg);
    }
    
    public ServiceConfigurationError(final Throwable x) {
        super(x);
    }
}
