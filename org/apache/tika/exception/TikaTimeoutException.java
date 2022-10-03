package org.apache.tika.exception;

public class TikaTimeoutException extends RuntimeException
{
    public TikaTimeoutException(final String message) {
        super(message);
    }
}
