package org.apache.lucene.store;

import java.io.IOException;

public class LockObtainFailedException extends IOException
{
    public LockObtainFailedException(final String message) {
        super(message);
    }
    
    public LockObtainFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
