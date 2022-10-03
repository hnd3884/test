package com.me.devicemanagement.framework.server.deletionfw;

public class DeletionQueueFailedException extends Exception
{
    public DeletionQueueFailedException(final String message) {
        super(message);
    }
    
    public DeletionQueueFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
