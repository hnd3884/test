package org.glassfish.jersey.internal.guava;

public class ExecutionError extends Error
{
    private static final long serialVersionUID = 0L;
    
    public ExecutionError(final Error cause) {
        super(cause);
    }
}
