package org.apache.poi.openxml4j.exceptions;

public class OpenXML4JRuntimeException extends RuntimeException
{
    public OpenXML4JRuntimeException(final String msg) {
        super(msg);
    }
    
    public OpenXML4JRuntimeException(final String msg, final Throwable reason) {
        super(msg, reason);
    }
}
