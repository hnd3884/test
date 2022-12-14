package org.apache.http;

import java.io.IOException;

public class ContentTooLongException extends IOException
{
    private static final long serialVersionUID = -924287689552495383L;
    
    public ContentTooLongException(final String message) {
        super(message);
    }
    
    public ContentTooLongException(final String format, final Object... args) {
        super(HttpException.clean(String.format(format, args)));
    }
}
