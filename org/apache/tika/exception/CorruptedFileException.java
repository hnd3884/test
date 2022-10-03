package org.apache.tika.exception;

public class CorruptedFileException extends TikaException
{
    public CorruptedFileException(final String msg) {
        super(msg);
    }
    
    public CorruptedFileException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
