package org.msgpack.unpacker;

import java.io.IOException;

public class SizeLimitException extends IOException
{
    public SizeLimitException() {
    }
    
    public SizeLimitException(final String message) {
        super(message);
    }
    
    public SizeLimitException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public SizeLimitException(final Throwable cause) {
        super(cause);
    }
}
