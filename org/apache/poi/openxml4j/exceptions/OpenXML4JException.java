package org.apache.poi.openxml4j.exceptions;

public class OpenXML4JException extends Exception
{
    public OpenXML4JException(final String msg) {
        super(msg);
    }
    
    public OpenXML4JException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
