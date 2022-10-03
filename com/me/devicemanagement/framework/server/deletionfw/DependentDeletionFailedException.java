package com.me.devicemanagement.framework.server.deletionfw;

public class DependentDeletionFailedException extends Exception
{
    public DependentDeletionFailedException(final String message) {
        super(message);
    }
    
    public DependentDeletionFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
