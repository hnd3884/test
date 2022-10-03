package org.antlr.v4.runtime.misc;

import java.util.concurrent.CancellationException;

public class ParseCancellationException extends CancellationException
{
    public ParseCancellationException() {
    }
    
    public ParseCancellationException(final String message) {
        super(message);
    }
    
    public ParseCancellationException(final Throwable cause) {
        this.initCause(cause);
    }
    
    public ParseCancellationException(final String message, final Throwable cause) {
        super(message);
        this.initCause(cause);
    }
}
