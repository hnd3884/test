package org.apache.lucene.store;

import java.io.IOException;

public class LockReleaseFailedException extends IOException
{
    public LockReleaseFailedException(final String message) {
        super(message);
    }
    
    public LockReleaseFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
