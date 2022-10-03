package org.apache.commons.lang;

import org.apache.commons.lang.exception.NestableRuntimeException;

public class UnhandledException extends NestableRuntimeException
{
    public UnhandledException(final Throwable cause) {
        super(cause);
    }
    
    public UnhandledException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
