package org.apache.poi.openxml4j.exceptions;

public final class InvalidFormatException extends OpenXML4JException
{
    public InvalidFormatException(final String message) {
        super(message);
    }
    
    public InvalidFormatException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
