package org.apache.tika.exception;

public class TikaException extends Exception
{
    public TikaException(final String msg) {
        super(msg);
    }
    
    public TikaException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
