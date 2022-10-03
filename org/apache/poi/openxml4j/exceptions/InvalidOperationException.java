package org.apache.poi.openxml4j.exceptions;

public class InvalidOperationException extends OpenXML4JRuntimeException
{
    public InvalidOperationException(final String message) {
        super(message);
    }
    
    public InvalidOperationException(final String message, final Throwable reason) {
        super(message, reason);
    }
}
