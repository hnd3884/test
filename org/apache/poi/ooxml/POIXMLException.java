package org.apache.poi.ooxml;

public final class POIXMLException extends RuntimeException
{
    public POIXMLException() {
    }
    
    public POIXMLException(final String msg) {
        super(msg);
    }
    
    public POIXMLException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public POIXMLException(final Throwable cause) {
        super(cause);
    }
}
