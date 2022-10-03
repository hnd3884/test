package org.apache.http;

import java.io.IOException;

public class ConnectionClosedException extends IOException
{
    private static final long serialVersionUID = 617550366255636674L;
    
    public ConnectionClosedException() {
        super("Connection is closed");
    }
    
    public ConnectionClosedException(final String message) {
        super(HttpException.clean(message));
    }
    
    public ConnectionClosedException(final String format, final Object... args) {
        super(HttpException.clean(String.format(format, args)));
    }
}
