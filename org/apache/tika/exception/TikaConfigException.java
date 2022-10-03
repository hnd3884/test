package org.apache.tika.exception;

public class TikaConfigException extends TikaException
{
    public TikaConfigException(final String msg) {
        super(msg);
    }
    
    public TikaConfigException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
