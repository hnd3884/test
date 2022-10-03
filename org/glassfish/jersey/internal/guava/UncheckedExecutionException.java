package org.glassfish.jersey.internal.guava;

public class UncheckedExecutionException extends RuntimeException
{
    private static final long serialVersionUID = 0L;
    
    public UncheckedExecutionException(final Throwable cause) {
        super(cause);
    }
}
