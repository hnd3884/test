package org.apache.tika.mime;

import org.apache.tika.exception.TikaException;

public class MimeTypeException extends TikaException
{
    public MimeTypeException(final String message) {
        super(message);
    }
    
    public MimeTypeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
